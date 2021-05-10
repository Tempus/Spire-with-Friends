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

public class DiscordIntegration implements Integration {
  public boolean initialized = false;
  public Core core;
  public final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
  public ScheduledFuture<?> callbacksExecutor;
  public DiscordEventHandler eventHandler = new DiscordEventHandler();

  public ConcurrentLinkedQueue<byte[]> incomingMessages;
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
        params.setClientID(406644123832156160L);
        params.setFlags(1L); // NoRequireDiscord
        params.registerEventHandler(eventHandler);
        this.core = new Core(params);
        eventHandler.addListener(new DiscordEventAdapter() {
          @Override
          public void onMessage(long peerId, byte channelId, byte[] data) {
            incomingMessages.add(data);
            //TODO associate packet with the sending RemotePlayer
          }
        });
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
  public void updateLobbyData() {
    // This will probably be handled by NetworkHelper or something interacting directly with a DiscordLobby
  }

  @Override
  public void createLobby(TogetherManager.mode gameMode) {
    LobbyTransaction txn = core.lobbyManager().getLobbyCreateTransaction();
    txn.setType(lobbyPrivate ? LobbyType.PRIVATE : LobbyType.PUBLIC);
    txn.setCapacity(lobbyMaxMembers);
    core.lobbyManager().createLobby(txn, ((result, lobby) -> {
      if (result != Result.OK) {
        //TODO report error somehow
        return;
      }
      Lobby createdLobby = new DiscordLobby(this, lobby);
      //TODO none of this supports Discord lobbies yet:

      // TogetherManager.currentLobby = createdLobby;
      NetworkHelper.updateLobbyData();
      // NetworkHelper.addPlayer(NetworkHelper.matcher.getLobbyOwner(lobby));
      NetworkHelper.sendData(NetworkHelper.dataType.Version);
    }));
  }
  public boolean lobbyPrivate = false;
  @Override
  public void setLobbyPrivate(boolean priv) {
    lobbyPrivate = priv;
  }
  public int lobbyMaxMembers = 6;
  @Override
  public void setLobbyMaxMembers(int maxMembers) {
    lobbyMaxMembers = maxMembers;
  }

  @Override
  public void getLobbies() {
    core.runCallbacks();
    LobbySearchQuery query = core.lobbyManager().getSearchQuery();
    query.filter("mode", LobbySearchQuery.Comparison.EQUAL, LobbySearchQuery.Cast.STRING, TogetherManager.gameMode.toString());
    query.distance(LobbySearchQuery.Distance.GLOBAL);
    core.lobbyManager().search(query);
    //TODO currently no real way to register for a callback when the search finishes
    //     pending https://github.com/JnCrMx/discord-game-sdk4j/issues/27
  }

  @Override
  public void addPlayer() {

  }

  @Override
  public void removePlayer() {

  }

  @Override
  public ByteBuffer checkForPacket() {
    core.runCallbacks();
    byte[] message = incomingMessages.poll();
    if (message != null) return ByteBuffer.wrap(message);
    else return null;
  }

  @Override
  public void sendPacket(ByteBuffer data) {
    //TODO requires a list of players to send data to
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
