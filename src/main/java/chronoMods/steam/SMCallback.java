package chronoMods.steam;

import com.codedisaster.steamworks.SteamAuth.AuthSessionResponse;
import com.codedisaster.steamworks.*;

import com.megacrit.cardcrawl.integrations.steam.*;
import com.megacrit.cardcrawl.core.*;
import com.evacipated.cardcrawl.modthespire.lib.*;
import basemod.interfaces.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import chronoMods.*;
import chronoMods.steam.*;
import chronoMods.ui.*;
import chronoMods.ui.deathScreen.*;
import chronoMods.ui.hud.*;
import chronoMods.ui.lobby.*;
import chronoMods.ui.mainMenu.*;
import chronoMods.utilities.*;

public class SMCallback
  implements SteamMatchmakingCallback
{
  private static final Logger logger = LogManager.getLogger(SMCallback.class.getName());

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
      TogetherManager.currentLobby = new SteamLobby(lobby);
      TogetherManager.players = TogetherManager.currentLobby.getLobbyMembers();

      NewMenuButtons.joinNewGame();
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
        NetworkHelper.addPlayer(targetPlayer);
        NetworkHelper.sendData(NetworkHelper.dataType.Version);
        NetworkHelper.sendData(NetworkHelper.dataType.Ready);
        if (TogetherManager.gameMode == TogetherManager.mode.Coop)
          NetworkHelper.sendData(NetworkHelper.dataType.Character);
      }
      
      if (event == SteamMatchmaking.ChatMemberStateChange.Left) 
        NetworkHelper.removePlayer(targetPlayer);
      

      if (event == SteamMatchmaking.ChatMemberStateChange.Disconnected) 
        NetworkHelper.removePlayer(targetPlayer);
      

      if (event == SteamMatchmaking.ChatMemberStateChange.Kicked) 
        NetworkHelper.removePlayer(targetPlayer);
      

      if (event == SteamMatchmaking.ChatMemberStateChange.Banned) 
        NetworkHelper.removePlayer(targetPlayer);
      

      NewMenuButtons.newGameScreen.playerList.setPlayers(TogetherManager.players);
      if (TogetherManager.currentLobby.isOwner()) {
        NetworkHelper.matcher.setLobbyData(lobby, "members", TogetherManager.currentLobby.getMemberNameList());
      }
      
      NetworkHelper.sendData(NetworkHelper.dataType.Rules);
      TogetherManager.currentLobby.updateOwner();
  }
  
  // Returns the index of the chat message sent
  public void onLobbyChatMessage(SteamID lobby, SteamID chatter, SteamMatchmaking.ChatEntryType chatType, int chatIndice) {
  	TogetherManager.log("Lobby Chat message");
  }
  
  // Returned after searching for Lobbies
  public void onLobbyMatchList(int lobbiesMatching) {
  	TogetherManager.log("Lobby Match List: " + lobbiesMatching);
    NetworkHelper.steamLobbies.clear();

    SteamLobby l;
    for (int i =0; i < lobbiesMatching; i++ ) {
      NetworkHelper.steamLobbies.add(new SteamLobby(NetworkHelper.matcher.getLobbyByIndex(i)));
      NewMenuButtons.lobbyScreen.createFreshGameList();
    }
  }
  
  // Called after you make a lobby
  public void onLobbyCreated(SteamResult result, SteamID lobby) {
  	TogetherManager.log("Lobby Created: " + result.toString() + " - Steam - " + lobby + " - ID: " + lobby.getAccountID());

    TogetherManager.currentLobby = new SteamLobby(lobby);
    NetworkHelper.updateLobbyData();

    NetworkHelper.addPlayer(NetworkHelper.matcher.getLobbyOwner(lobby));
    NetworkHelper.sendData(NetworkHelper.dataType.Version);
  }
  
  // Special Patch callback for joining via invite
  @SpirePatch(clz = SFCallback.class, method="onGameLobbyJoinRequested")
  public static class getInvitedAndRespond {
      public static void Postfix(SFCallback __instance, SteamID lobby, SteamID steamIDFriend) {
          TogetherManager.log("Entered via invite/join - " + lobby + " - ID: " + lobby.getAccountID());

          TogetherManager.clearMultiplayerData();
          if (TogetherManager.currentLobby.mode.equals("Versus"))
            TogetherManager.gameMode = TogetherManager.mode.Versus;
          else
            TogetherManager.gameMode = TogetherManager.mode.Coop;

          NetworkHelper.matcher.joinLobby(lobby);

          TogetherManager.currentLobby = new SteamLobby(lobby);          
          TogetherManager.players = TogetherManager.currentLobby.getLobbyMembers();

          NewMenuButtons.joinNewGame();
      }
  }

  @SpirePatch(clz = SFCallback.class, method="onAvatarImageLoaded")
  public static class ImageDownloadedCallback {
      public static void Postfix(SFCallback __instance, SteamID steamID, int image, int width, int height) {
          TogetherManager.log("Steam Avatar is downloaded! " + steamID + " - size: " + width);

          for (RemotePlayer player : TogetherManager.players) {
            if (player.isUser(steamID))
              player.updateAvatar(image, width, height);
          }

      }
  }

  // Unused callbacks
  public void onFavoritesListChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, boolean paramBoolean, int paramInt6) {} // For favourites and history of lobby connections
  public void onFavoritesListAccountsUpdated(SteamResult paramSteamResult) {} // For favourites and history of lobby connections
  public void onLobbyGameCreated(SteamID paramSteamID1, SteamID paramSteamID2, int paramInt, short paramShort) {} // For remote server connections, not P2P
  public void onLobbyKicked(SteamID paramSteamID1, SteamID paramSteamID2, boolean paramBoolean) {} // Unused by Steam

}
