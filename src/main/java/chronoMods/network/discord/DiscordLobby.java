package chronoMods.network.discord;

import de.jcm.discordgamesdk.Core;
import de.jcm.discordgamesdk.lobby.Lobby;
import de.jcm.discordgamesdk.lobby.LobbyTransaction;
import de.jcm.discordgamesdk.user.DiscordUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import chronoMods.RemotePlayer;


public class DiscordLobby extends chronoMods.network.Lobby {
  public DiscordIntegration integration;
  public Lobby lobby;
  public  Map<String, String> metadata;
  public DiscordLobby(DiscordIntegration integration, Lobby lobby) {
    this.integration = integration;
    this.lobby = lobby;
    metadata = integration.core.lobbyManager().getLobbyMetadata(lobby);
    fetchAllMetadata();
  }
  @Override
  public String getOwnerName() {
    return integration.core.lobbyManager().getMemberUser(lobby, lobby.getOwnerId()).getUsername();
  }

  @Override
  public boolean isOwner() {
    return lobby.getOwnerId() == integration.core.userManager().getCurrentUser().getUserId();
  }

  @Override
  public void updateOwner() {
    LobbyTransaction txn = integration.core.lobbyManager().getLobbyUpdateTransaction(lobby);
    txn.setOwner(integration.core.userManager().getCurrentUser().getUserId());
    integration.core.lobbyManager().updateLobby(lobby, txn);
  }

  @Override
  public int getMemberCount() {
    return integration.core.lobbyManager().memberCount(lobby);
  }

  @Override
  public CopyOnWriteArrayList<RemotePlayer> getLobbyMembers() {
    //TODO requires non-Steam-specific RemotePlayer
    return null;
  }

  @Override
  public String getMemberNameList() {
    return integration.core.lobbyManager().getMemberUsers(lobby)
        .stream()
        .map(u -> u.getUsername())
        .collect(Collectors.joining("\t"));
  }

  @Override
  public int getCapacity() {
    return lobby.getCapacity();
  }

  @Override
  public String getMetadata(String key) {
    return metadata.get(key);
  }

  @Override
  public void setMetadata(Map.Entry<String, String>... pairs) {
    LobbyTransaction txn = integration.core.lobbyManager().getLobbyUpdateTransaction(lobby);
    for (Map.Entry<String, String> pair : pairs) {
      txn.setMetadata(pair.getKey(), pair.getValue());
    }
    integration.core.lobbyManager().updateLobby(lobby, txn);
  }
}
