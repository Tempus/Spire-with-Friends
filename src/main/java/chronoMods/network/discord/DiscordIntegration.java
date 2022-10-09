package chronoMods.network.discord;

import chronoMods.TogetherManager;
import chronoMods.network.Integration;
import chronoMods.network.NetworkHelper;
import chronoMods.network.Packet;
import chronoMods.network.RemotePlayer;
import chronoMods.ui.lobby.NewScreenUpdateRender;
import chronoMods.ui.mainMenu.NewMenuButtons;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import de.jcm.discordgamesdk.*;
import de.jcm.discordgamesdk.lobby.LobbySearchQuery;
import de.jcm.discordgamesdk.lobby.LobbyTransaction;
import de.jcm.discordgamesdk.lobby.LobbyType;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DiscordIntegration implements Integration {
  public boolean initialized = false;
  public Texture logo;
  public CreateParams createParams;
  public Core core;
  public String ourRoute;
  public static List<DiscordIntegration> instances = new ArrayList<>();
  //public final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
  //public ScheduledFuture<?> callbacksExecutor;
  public DiscordEventHandler eventHandler = new DiscordEventHandler();
  public ConcurrentLinkedQueue<Packet> incomingMessages = new ConcurrentLinkedQueue<>();
  boolean needsFlush = false;

  public static ConcurrentLinkedQueue<Runnable> postedRunnables = new ConcurrentLinkedQueue<>();

  @SpirePatch(clz= CardCrawlGame.class, method="update")
  public static class DiscordUpdate
  {
    public static void Prefix(CardCrawlGame __instance) {
      DiscordIntegration.runCallbacks();
    }
    public static void Postfix(CardCrawlGame __instance)
    {
      DiscordIntegration.flushNetwork();
    }
  }

  /// Posted runnables will run once on the main thread, after runCallbacks() but before flushNetwork()
  public static void postRunnable(Runnable r) {
    postedRunnables.add(r);
  }

  public static void runCallbacks() {
    if (instances.size() > 1) {
      TogetherManager.log("Multiple instances of DiscordIntegration!");
    }
    instances.forEach(i -> i.core.runCallbacks());
    while (!postedRunnables.isEmpty()) {
      postedRunnables.poll().run();
    }
  }

  public static void flushNetwork() {
    instances.forEach(i -> i.core.networkManager().flush());
  }

  public File extractDiscordNative() throws IOException, UnsupportedOperationException, RuntimeException {
    // Code modified from https://github.com/JnCrMx/discord-game-sdk4j/blob/d9f40b3e3f2772e1daa680a631f5b2f3ee6186ae/src/main/java/de/jcm/discordgamesdk/Core.java#L46
    // which is licensed under the MIT license:
    /*
    MIT License

    Copyright (c) 2020 JCM

    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included in all
    copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
    SOFTWARE.
     */

    Path tempDir = Files.createTempDirectory("StSTogetherDiscordNative");
    tempDir.toFile().deleteOnExit();
    String name = "discord_game_sdk";
    String osName = System.getProperty("os.name").toLowerCase(Locale.ROOT);
    String arch = System.getProperty("os.arch").toLowerCase(Locale.ROOT);
    String objectName;

    if(osName.contains("windows"))
    {
      osName = "windows";
      objectName = name + ".dll";
    }
    else if(osName.contains("linux"))
    {
      osName = "linux";
      objectName = name + ".so";
    }
    else
    {
      throw new UnsupportedOperationException("OS not supported by Discord library");
    }
    String path = "/discordNative/"+arch+"/"+objectName;
    InputStream in = DiscordIntegration.class.getResourceAsStream(path);
    if(in == null)
      throw new RuntimeException(new FileNotFoundException("cannot find native library at "+path));
    File discordNativeFile = tempDir
        .resolve(objectName)
        .toFile();
    discordNativeFile.deleteOnExit();
    Files.copy(in, discordNativeFile.toPath());
    return discordNativeFile;
  }
  @Override
  public void initialize() {
    //TogetherManager.log("DiscordIntegration.initialize()");
    logo = ImageMaster.loadImage("chrono/images/Discord-Logo-Color-92px.png");
    //if (callbacksExecutor != null && !callbacksExecutor.isCancelled()) callbacksExecutor.cancel(false);
    try {
      Core.init(extractDiscordNative());
      if (createParams != null) {
        createParams.close();
      }
      createParams = new CreateParams();
      createParams.setClientID(850108081882136606L); // App ID for Slay the Spire
      createParams.setFlags(1L); // NoRequireDiscord
      createParams.registerEventHandler(eventHandler);
      eventHandler.removeAllListeners();
      eventHandler.addListener(new DiscordEventAdapter() {
        @Override
        public void onRouteUpdate(String routeData) {
          ourRoute = routeData;
        }

        @Override
        public void onActivityJoin(String secret) {
          //TogetherManager.log("onActivityJoin");
          TogetherManager.clearMultiplayerData();
          core.lobbyManager().connectLobbyWithActivitySecret(secret, (result, lobby) -> {
            if (result != Result.OK) {
              TogetherManager.log(result.name() + " when joining via invite");
              return;
            }
            DiscordLobby createdLobby = new DiscordLobby(lobby, DiscordIntegration.this);
            TogetherManager.currentLobby = createdLobby;
            if (TogetherManager.currentLobby.mode.equals("Versus"))
              TogetherManager.gameMode = TogetherManager.mode.Versus;
            else if (TogetherManager.currentLobby.mode.equals("Bingo"))
              TogetherManager.gameMode = TogetherManager.mode.Bingo;
            else
              TogetherManager.gameMode = TogetherManager.mode.Coop;

            TogetherManager.players = TogetherManager.currentLobby.getLobbyMembers();
            TogetherManager.currentUser = makeCurrentUser();
            createdLobby.setUpNetworking();
            createdLobby.startActivity();

            NewScreenUpdateRender.joinFlag = true;
            NetworkHelper.sendData(NetworkHelper.dataType.Version);
          });
        }
      });
      this.core = new Core(createParams);
      this.core.setLogHook(LogLevel.DEBUG, ((logLevel, s) -> TogetherManager.log(logLevel + ":" + s)));
      /*
      callbacksExecutor = scheduler.scheduleAtFixedRate(
          () -> {
            try {
              core.runCallbacks();

              if (needsFlush) {
                TogetherManager.log("Flushing network");
                core.networkManager().flush();
                needsFlush = false;
              }
            }
            catch (Exception e) {
              TogetherManager.log(e.toString());
              e.printStackTrace();
            }
          },
          0,
          1000 / 15,
          TimeUnit.MILLISECONDS
      );
      */
      instances.add(this);
      //TODO make sure all modules are actually initialized
      initialized = true;
    }
    catch (IOException | UnsatisfiedLinkError e) {
      TogetherManager.log(e.toString());
      e.printStackTrace();
      //TODO
    }
    catch (UnsupportedOperationException e) {
      // OS not supported (yet)
      TogetherManager.log(e.getMessage());
    }
    catch (GameSDKException e) {
      TogetherManager.log("Error initializing Discord: " + e);
    }
  }

  @Override
  public boolean isInitialized() {
    return initialized;
  }

  @Override
  public RemotePlayer makeCurrentUser() {
    // Discord users currently only exist in the context of a lobby
    // And all things that set currentLobby should also add the local player to that lobby
    if (TogetherManager.currentLobby instanceof DiscordLobby) {
      RemotePlayer player = TogetherManager.players.stream()
          .filter(p -> p.isUser(core.userManager().getCurrentUser().getUserId()))
          .findAny()
          .orElseGet(() -> {
            DiscordPlayer p = new DiscordPlayer(
                core.userManager().getCurrentUser(),
                this,
                (DiscordLobby) TogetherManager.currentLobby
            );
            if (TogetherManager.config.has("mark"))
              p.bingoMark = TogetherManager.customMark;

            NetworkHelper.addPlayer(p);
            return p;
          });

      if (TogetherManager.config.has("mark"))
        player.bingoMark = TogetherManager.customMark;

      return player;
    }
    return null;
  }

  @Override
  public void updateLobbyData() {
    // This will probably be handled by NetworkHelper or something interacting directly with a DiscordLobby
  }

  @Override
  public void createLobby(TogetherManager.mode gameMode) {
    LobbyTransaction txn = core.lobbyManager().getLobbyCreateTransaction();
    txn.setType(lobbyPrivate ? LobbyType.PRIVATE : LobbyType.PUBLIC);
    txn.setCapacity(gameMode == TogetherManager.mode.Coop ? 6 : 200);
    core.lobbyManager().createLobby(txn, ((result, lobby) -> {
      if (result != Result.OK) {
        //TODO report error somehow
        return;
      }
      DiscordLobby createdLobby = new DiscordLobby(lobby, this);

      TogetherManager.currentLobby = createdLobby;
      TogetherManager.currentUser = makeCurrentUser();
      NetworkHelper.updateLobbyData();
      NetworkHelper.addPlayer(TogetherManager.currentUser);
      NetworkHelper.sendData(NetworkHelper.dataType.Version);
      createdLobby.setUpNetworking();
      createdLobby.startActivity();
    }));
  }
  public boolean lobbyPrivate = false;
  @Override
  public void setLobbyPrivate(boolean priv) {
    lobbyPrivate = priv;
    if (TogetherManager.currentLobby instanceof DiscordLobby) {
      TogetherManager.currentLobby.setPrivate(priv);
    }
  }

  @Override
  public void getLobbies() {
    //core.runCallbacks();
    LobbySearchQuery query = core.lobbyManager().getSearchQuery();
    query.filter("metadata.mode", LobbySearchQuery.Comparison.EQUAL, LobbySearchQuery.Cast.STRING, TogetherManager.gameMode.toString());
    query.distance(LobbySearchQuery.Distance.GLOBAL);
    core.lobbyManager().search(query, result -> {
      if (result != Result.OK) return; //TODO error handling
      for (de.jcm.discordgamesdk.lobby.Lobby l : core.lobbyManager().getLobbies()) {
        NetworkHelper.lobbies.add(new DiscordLobby(l, this));
      }
      // the Steam version does this inside the loop, but I don't see why
      NewMenuButtons.lobbyScreen.createFreshGameList();
    });
  }

  @Override
  public void getPacket(Packet packet) {
    try {
      //core.runCallbacks();
    }
    catch (Exception e) {
      TogetherManager.log(e.toString());
      e.printStackTrace();
    }
    Packet p = incomingMessages.poll();
    if (p == null) packet.clear();
    else packet.set(p.player(), p.data());
    return;
  }

  @Override
  public void sendPacket(ByteBuffer data) {
    //TogetherManager.log("sendPacket input is " + data.order());
    for (RemotePlayer p : TogetherManager.players) {
      if (p instanceof DiscordPlayer) {
        ((DiscordPlayer) p).sendMessage(data);
      }
    }
    //core.networkManager().flush();
    needsFlush = true;
  }

  @Override
  public void messageUser(RemotePlayer player) {
    // Discord does not provide this functionality
  }

  public Texture getLogo() { return logo; }

  @Override
  public void dispose() {
    TogetherManager.log("Discord integration shutting down");
    //callbacksExecutor.cancel(false);
    instances.remove(this);
    core.close();
    createParams.close();
    createParams = null;
    TogetherManager.log("Discord integration shut down successfully");
    initialized = false;
  }
}
