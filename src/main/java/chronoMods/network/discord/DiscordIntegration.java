package chronoMods.network.discord;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.HashMap;

import chronoMods.TogetherManager;
import chronoMods.network.Integration;
import chronoMods.network.Lobby;

import com.evacipated.cardcrawl.modthespire.Loader;

import de.jcm.discordgamesdk.Core;
import de.jcm.discordgamesdk.CreateParams;
import de.jcm.discordgamesdk.Result;
import de.jcm.discordgamesdk.activity.Activity;
import de.jcm.discordgamesdk.lobby.LobbyTransaction;
import de.jcm.discordgamesdk.lobby.LobbyType;
import de.jcm.discordgamesdk.user.DiscordUser;

public class DiscordIntegration implements Integration {
  public boolean initialized = false;
  public Core core;

  public HashMap<Long, DiscordUser> users;
  public HashMap<Long, DiscordLobby> lobbies;
  @Override
  public void initialize() {
    try {
      File discordNativeFile = Files.createTempDirectory("StSTogetherDiscordNative")
          .resolve("discord_game_sdk.dll") // this exact filename is required on Windows
          .toFile();
      Core.init(discordNativeFile);

      try(CreateParams params = new CreateParams()) {
        params.setClientID(406644123832156160L);
        params.setFlags(1L); // NoRequireDiscord
        params.registerEventHandler(new EventAdapter(this));
        try(Core core = new Core(params)) {
          this.core = core;
          initialized = true;
        }
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

  }

  @Override
  public void addPlayer() {

  }

  @Override
  public void removePlayer() {

  }

  @Override
  public ByteBuffer checkForPacket() {
    return null;
  }

  @Override
  public void sendPacket(ByteBuffer data) {

  }
}
