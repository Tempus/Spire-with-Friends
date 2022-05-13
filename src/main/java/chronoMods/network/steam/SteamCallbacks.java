package chronoMods.network.steam;

import com.codedisaster.steamworks.SteamAuth.AuthSessionResponse;
import com.codedisaster.steamworks.*;

import com.megacrit.cardcrawl.integrations.steam.*;
import com.megacrit.cardcrawl.core.*;
import com.evacipated.cardcrawl.modthespire.lib.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import chronoMods.*;
import chronoMods.network.NetworkHelper;
import chronoMods.network.steam.*;
import chronoMods.ui.lobby.*;
import chronoMods.ui.mainMenu.*;

public class SteamCallbacks
  implements SteamMatchmakingCallback, SteamNetworkingCallback, SteamUtilsCallback, SteamFriendsCallback
{
  private static final Logger logger = LogManager.getLogger(SteamCallbacks.class.getName());

  // Steam Matchmaking Callbacks

  // Called when you're invited, Steam Overlay handles this
  public void onLobbyInvite(SteamID user, SteamID lobby, long gameID) {
      TogetherManager.log("Got Invited! :) - " + lobby + " - ID: " + lobby.getAccountID());

      // TogetherManager.currentLobby = new SteamLobby(lobby);
      // TogetherManager.players = TogetherManager.currentLobby.getLobbyMembers();

      // NewMenuButtons.joinNewGame();
  } 

  // Recieved upon attempting to enter a lobby. Lobby metadata is available to use immediately after receiving this
  public void onLobbyEnter(SteamID lobby, int unused, boolean blocked, SteamMatchmaking.ChatRoomEnterResponse successEnum) {
  	TogetherManager.log("Entered Lobby: " + successEnum + " - " + lobby + " - ID: " + lobby.getAccountID());

    if (!blocked && successEnum == SteamMatchmaking.ChatRoomEnterResponse.Success) {
      TogetherManager.currentLobby = new SteamLobby(NetworkHelper.steam, lobby);
      TogetherManager.players = TogetherManager.currentLobby.getLobbyMembers();

      NewScreenUpdateRender.joinFlag = true;
      NetworkHelper.sendData(NetworkHelper.dataType.Version);

    } else {
      TogetherManager.infoPopup.show(CardCrawlGame.languagePack.getUIString("Network").TEXT[5], CardCrawlGame.languagePack.getUIString("Network").TEXT[6]);
    }

    NetworkHelper.sendData(NetworkHelper.dataType.Version);
  }
  
  // Called when the user data of a lobby entry is changed - for us, this should just be coop character choice
  public void onLobbyDataUpdate(SteamID lobby, SteamID playerUpdated, boolean success) {
    if (success) {
    	TogetherManager.log("Lobby Data Updated for some damn reason");
    }
  }

  // Called on joins/parts/disconnects/kicks/bans
  public void onLobbyChatUpdate(SteamID lobby, SteamID targetPlayer, SteamID causePlayer, SteamMatchmaking.ChatMemberStateChange event) {

      if (event == SteamMatchmaking.ChatMemberStateChange.Entered) {
        NetworkHelper.addPlayer(new SteamPlayer(targetPlayer));
        NetworkHelper.sendData(NetworkHelper.dataType.Version);
        NetworkHelper.sendData(NetworkHelper.dataType.Ready);

        if (TogetherManager.gameMode == TogetherManager.mode.Bingo) {
          NetworkHelper.sendData(NetworkHelper.dataType.TeamChange);
          NetworkHelper.sendData(NetworkHelper.dataType.TeamName);
        }

        if (TogetherManager.gameMode != TogetherManager.mode.Versus)
          NetworkHelper.sendData(NetworkHelper.dataType.Character);
      }
      
      SteamPlayer p = SteamIntegration.getPlayer(targetPlayer);

      if (event == SteamMatchmaking.ChatMemberStateChange.Left) 
        NetworkHelper.removePlayer(p);
      

      if (event == SteamMatchmaking.ChatMemberStateChange.Disconnected) 
        NetworkHelper.removePlayer(p);
      

      if (event == SteamMatchmaking.ChatMemberStateChange.Kicked) 
        NetworkHelper.removePlayer(p);
      

      if (event == SteamMatchmaking.ChatMemberStateChange.Banned) 
        NetworkHelper.removePlayer(p);
      

      NewMenuButtons.newGameScreen.playerList.setPlayers(TogetherManager.players);
      if (TogetherManager.currentLobby.isOwner()) {
        NetworkHelper.steam.matcher.setLobbyData(lobby, "members", TogetherManager.currentLobby.getMemberNameList());
      }
      
      NetworkHelper.sendData(NetworkHelper.dataType.Rules);
      if (TogetherManager.gameMode == TogetherManager.mode.Bingo) {
        NetworkHelper.sendData(NetworkHelper.dataType.BingoRules);
      }
      // TogetherManager.currentLobby.updateOwner();
  }
  
  // Returns the index of the chat message sent
  public void onLobbyChatMessage(SteamID lobby, SteamID chatter, SteamMatchmaking.ChatEntryType chatType, int chatIndice) {
  	TogetherManager.log("Lobby Chat message");
  }
  
  // Returned after searching for Lobbies
  public void onLobbyMatchList(int lobbiesMatching) {
  	TogetherManager.log("Lobby Match List: " + lobbiesMatching);

    SteamLobby l;
    for (int i =0; i < lobbiesMatching; i++ ) {
      NetworkHelper.lobbies.add(new SteamLobby(NetworkHelper.steam, NetworkHelper.steam.matcher.getLobbyByIndex(i)));
      NewMenuButtons.lobbyScreen.createFreshGameList();
    }
  }
  
  // Called after you make a lobby
  public void onLobbyCreated(SteamResult result, SteamID lobby) {
  	TogetherManager.log("Lobby Created: " + result.toString() + " - Steam - " + lobby + " - ID: " + lobby.getAccountID());

    TogetherManager.currentLobby = new SteamLobby(NetworkHelper.steam, lobby);
    NetworkHelper.updateLobbyData();

    NetworkHelper.addPlayer(new SteamPlayer(NetworkHelper.steam.matcher.getLobbyOwner(lobby)));
    NetworkHelper.sendData(NetworkHelper.dataType.Version);
  }
  
  // Steam Friends Callbacks  
  public void onGameLobbyJoinRequested(SteamID steamIDLobby, SteamID steamIDFriend) {
  	TogetherManager.log("Entered via invite/join - " + steamIDLobby + " - ID: " + steamIDLobby.getAccountID());

  	TogetherManager.clearMultiplayerData();
  	if (TogetherManager.currentLobby.mode.equals("Versus"))
  		TogetherManager.gameMode = TogetherManager.mode.Versus;
    else if (TogetherManager.currentLobby.mode.equals("Bingo"))
      TogetherManager.gameMode = TogetherManager.mode.Bingo;
  	else
  		TogetherManager.gameMode = TogetherManager.mode.Coop;

  	NetworkHelper.steam.matcher.joinLobby(steamIDLobby);

  	TogetherManager.currentLobby = new SteamLobby(NetworkHelper.steam, steamIDLobby);          
  	TogetherManager.players = TogetherManager.currentLobby.getLobbyMembers();

  	NewScreenUpdateRender.joinFlag = true;
  }
  
  public void onAvatarImageLoaded(SteamID steamID, int image, int width, int height) {
  	TogetherManager.log("Steam Avatar is downloaded! " + steamID + " - size: " + width);

  	// SteamIntegration.getPlayer(steamID).updateAvatar(image, width, height);
    SteamPlayer p = SteamIntegration.getPlayer(steamID);
    if (p != null)
    	p.updateAvatar(image);
  }

  public void onPersonaStateChange(SteamID steamID, SteamFriends.PersonaChange change) {
    if (change == SteamFriends.PersonaChange.Avatar) {
      TogetherManager.log("Steam Avatar is available: " + steamID);

      SteamPlayer p = SteamIntegration.getPlayer(steamID);
      if (p != null)
        p.getAvatar();
    }
  }

  
  // Steam Network Callbacks
  public void onP2PSessionConnectFail(SteamID paramSteamID, SteamNetworking.P2PSessionError paramP2PSessionError) {
    TogetherManager.log("onP2PSessionConnectFail");
  }
  
  public void onP2PSessionRequest(SteamID paramSteamID) {
    TogetherManager.log("onP2PSessionRequest");
    NetworkHelper.steam.net.acceptP2PSessionWithUser(paramSteamID);
  }

  // Steam Utils Callbacks
  public void onSteamShutdown() {};

  // Unused callbacks
  public void onFavoritesListChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, boolean paramBoolean, int paramInt6) {} // For favourites and history of lobby connections
  public void onFavoritesListAccountsUpdated(SteamResult paramSteamResult) {} // For favourites and history of lobby connections
  public void onLobbyGameCreated(SteamID paramSteamID1, SteamID paramSteamID2, int paramInt, short paramShort) {} // For remote server connections, not P2P
  public void onLobbyKicked(SteamID paramSteamID1, SteamID paramSteamID2, boolean paramBoolean) {} // Unused by Steam

  public void onGameServerChangeRequested(String paramString1, String paramString2) {}

  public void onSetPersonaNameResponse(boolean success, boolean localSuccess, SteamResult result) {}
  public void onGameOverlayActivated(boolean active) {}
  public void onFriendRichPresenceUpdate(SteamID steamIDFriend, int appID) {}
  public void onGameRichPresenceJoinRequested(SteamID steamIDFriend, String connect) {}
}
