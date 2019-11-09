package chronoMods.steam;

import com.codedisaster.steamworks.SteamAuth.AuthSessionResponse;
import com.codedisaster.steamworks.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import chronoMods.*;
import chronoMods.steam.*;
import chronoMods.ui.deathScreen.*;
import chronoMods.ui.hud.*;
import chronoMods.ui.lobby.*;
import chronoMods.ui.mainMenu.*;
import chronoMods.utilities.*;

public class SMCallback
  implements SteamMatchmakingCallback
{
  private static final Logger logger = LogManager.getLogger(SMCallback.class.getName());


  
  // Recieved upon attempting to enter a lobby. Lobby metadata is available to use immediately after receiving this
  public void onLobbyEnter(SteamID lobby, int unused, boolean blocked, SteamMatchmaking.ChatRoomEnterResponse successEnum) {
  	logger.info("Entered Lobby");

    if (!blocked) {
      TogetherManager.currentLobby = new SteamLobby(lobby);
      TogetherManager.players.clear();
      TogetherManager.players = TogetherManager.currentLobby.getLobbyMembers();
    }
  }
  
  // Called when the user data of a lobby entry is changed - for us, this should just be coop character choice
  public void onLobbyDataUpdate(SteamID lobby, SteamID playerUpdated, boolean success) {
    if (success) {
    	logger.info("Lobby Data Updated for some damn reason");
    }
  }

  // Called on joins/parts/disconnects/kicks/bans
  public void onLobbyChatUpdate(SteamID lobby, SteamID targetPlayer, SteamID causePlayer, SteamMatchmaking.ChatMemberStateChange event) {

      if (event == SteamMatchmaking.ChatMemberStateChange.Entered) {
        NetworkHelper.addPlayer(targetPlayer);
      }

      if (event == SteamMatchmaking.ChatMemberStateChange.Left) {
        NetworkHelper.removePlayer(targetPlayer);
      }

      if (event == SteamMatchmaking.ChatMemberStateChange.Disconnected) {
        NetworkHelper.removePlayer(targetPlayer);
      }

      if (event == SteamMatchmaking.ChatMemberStateChange.Kicked) {
        NetworkHelper.removePlayer(targetPlayer);
      }

      if (event == SteamMatchmaking.ChatMemberStateChange.Banned) {
        NetworkHelper.removePlayer(targetPlayer);
      }

      NewMenuButtons.newGameScreen.playerList.setPlayers(TogetherManager.players);
  }
  
  // Returns the index of the chat message sent
  public void onLobbyChatMessage(SteamID lobby, SteamID chatter, SteamMatchmaking.ChatEntryType chatType, int chatIndice) {
  	logger.info("Lobby Chat message");
  }
  
  // Returned after searching for Lobbies
  public void onLobbyMatchList(int lobbiesMatching) {
  	logger.info("Lobby Match List: " + lobbiesMatching);
    NetworkHelper.steamLobbies.clear();

    SteamLobby l;
    for (int i =0; i < lobbiesMatching; i++ ) {
      NetworkHelper.steamLobbies.add(new SteamLobby(NetworkHelper.matcher.getLobbyByIndex(i)));
      NewMenuButtons.lobbyScreen.createFreshGameList();
    }
  }
  
  // Called after you make a lobby
  public void onLobbyCreated(SteamResult result, SteamID lobby) {
  	logger.info("Lobby Created: " + result.toString() + " - ID: " + lobby.getAccountID());

    TogetherManager.currentLobby = new SteamLobby(lobby);
    NetworkHelper.matcher.setLobbyData(lobby, "name", "Sample Title");
    NetworkHelper.matcher.setLobbyData(lobby, "mode", TogetherManager.gameMode.toString());
    NetworkHelper.matcher.setLobbyData(lobby, "ascension", "0");
    NetworkHelper.matcher.setLobbyData(lobby, "character", "Ironclad");

    NetworkHelper.addPlayer(NetworkHelper.matcher.getLobbyOwner(lobby));
  }
  

  // Unused callbacks
  public void onFavoritesListChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, boolean paramBoolean, int paramInt6) {} // For favourites and history of lobby connections
  public void onFavoritesListAccountsUpdated(SteamResult paramSteamResult) {} // For favourites and history of lobby connections
  public void onLobbyGameCreated(SteamID paramSteamID1, SteamID paramSteamID2, int paramInt, short paramShort) {} // For remote server connections, not P2P
  public void onLobbyInvite(SteamID paramSteamID1, SteamID paramSteamID2, long paramLong) {} // Called when you're invited, Steam Overlay handles this
  public void onLobbyKicked(SteamID paramSteamID1, SteamID paramSteamID2, boolean paramBoolean) {} // Unused by Steam

}
