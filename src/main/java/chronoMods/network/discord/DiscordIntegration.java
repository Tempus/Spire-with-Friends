package chronoMods.network.discord;

import de.jcm.discordgamesdk.Core;
import de.jcm.discordgamesdk.CreateParams;
import de.jcm.discordgamesdk.DiscordEventAdapter;
import de.jcm.discordgamesdk.DiscordEventHandler;
import de.jcm.discordgamesdk.Result;
import de.jcm.discordgamesdk.lobby.LobbySearchQuery;
import de.jcm.discordgamesdk.lobby.LobbyTransaction;
import de.jcm.discordgamesdk.lobby.LobbyType;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import chronoMods.TogetherManager;
import chronoMods.network.Integration;
import chronoMods.network.Lobby;
import chronoMods.network.NetworkHelper;
import chronoMods.network.Packet;
import chronoMods.network.RemotePlayer;
import chronoMods.ui.mainMenu.NewMenuButtons;

public class DiscordIntegration implements Integration {
  public boolean initialized = false;
  public Core core;
  public String ourRoute;
  public final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
  public ScheduledFuture<?> callbacksExecutor;
  public DiscordEventHandler eventHandler = new DiscordEventHandler();
  public ConcurrentLinkedQueue<Packet> incomingMessages = new ConcurrentLinkedQueue<>();

  @Override
  public void initialize() {
    eventHandler.removeAllListeners();
    if (callbacksExecutor != null && !callbacksExecutor.isCancelled()) callbacksExecutor.cancel(false);
    try {
      File discordNativeFile = Files.createTempDirectory("StSTogetherDiscordNative")
          .resolve("discord_game_sdk.dll") // this exact filename is required on Windows
          .toFile();
      Core.init(discordNativeFile);

      try(CreateParams params = new CreateParams()) {
        params.setClientID(406644123832156160L); // App ID for Slay the Spire
        params.setFlags(1L); // NoRequireDiscord
        params.registerEventHandler(eventHandler);
        eventHandler.addListener(new DiscordEventAdapter() {
          @Override
          public void onRouteUpdate(String routeData) {
            ourRoute = routeData;
          }
        });
        this.core = new Core(params);
        callbacksExecutor = scheduler.scheduleAtFixedRate(
            () -> core.runCallbacks(),
            0,
            1000 / 15,
            TimeUnit.MILLISECONDS
        );
        initialized = true;
      }
    }
    catch (IOException e) {
      //TODO
    }
    initialized = true;
  }

  @Override
  public boolean isInitialized() {
    return initialized;
  }

  @Override
  public RemotePlayer makeCurrentUser() {
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
      Lobby createdLobby = new DiscordLobby(lobby, this);


      TogetherManager.currentLobby = createdLobby;
      NetworkHelper.updateLobbyData();
      // NetworkHelper.addPlayer(NetworkHelper.matcher.getLobbyOwner(lobby));
      NetworkHelper.sendData(NetworkHelper.dataType.Version);
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
    core.runCallbacks();
    LobbySearchQuery query = core.lobbyManager().getSearchQuery();
    query.filter("mode", LobbySearchQuery.Comparison.EQUAL, LobbySearchQuery.Cast.STRING, TogetherManager.gameMode.toString());
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
  public Packet getPacket() {
    core.runCallbacks();
    return incomingMessages.poll();
  }

  @Override
  public void sendPacket(ByteBuffer data) {
    for (RemotePlayer p : TogetherManager.players) {
      if (p instanceof DiscordPlayer) {
        ((DiscordPlayer) p).sendMessage(data);
      }
    }
    core.networkManager().flush();
  }

  @Override
  public void messageUser(RemotePlayer player) {

  }

  @Override
  public void dispose() {
    TogetherManager.log("Discord integration shutting down");
    callbacksExecutor.cancel(false);
    core.close();
    TogetherManager.log("Discord integration shut down successfully");
    initialized = false;
  }
}
