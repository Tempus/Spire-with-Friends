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

  public SMCallback() {}


  public void onFavoritesListChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, boolean paramBoolean, int paramInt6) {
  	logger.info("On Favourites List Changed");
  }
  
  public void onLobbyInvite(SteamID paramSteamID1, SteamID paramSteamID2, long paramLong) {
  	logger.info("Lobby Invite");
  }
  
  public void onLobbyEnter(SteamID paramSteamID, int paramInt, boolean paramBoolean, SteamMatchmaking.ChatRoomEnterResponse paramChatRoomEnterResponse) {
  	logger.info("Entered Lobby");

    RemotePlayer newPlayer = new RemotePlayer(paramSteamID);
    
    TogetherManager.players.add(newPlayer);
    TopPanelPlayerPanels.playerWidgets.add(new RemotePlayerWidget(newPlayer));
  }
  
  public void onLobbyDataUpdate(SteamID paramSteamID1, SteamID paramSteamID2, boolean paramBoolean) {
  	logger.info("Lobby Data Updated for some damn reason");
  }
  
  public void onLobbyChatUpdate(SteamID paramSteamID1, SteamID paramSteamID2, SteamID paramSteamID3, SteamMatchmaking.ChatMemberStateChange paramChatMemberStateChange) {
  	logger.info("Lobby chat status change");
  }
  
  public void onLobbyChatMessage(SteamID paramSteamID1, SteamID paramSteamID2, SteamMatchmaking.ChatEntryType paramChatEntryType, int paramInt) {
  	logger.info("Lobby Chat message");
  }
  
  public void onLobbyGameCreated(SteamID paramSteamID1, SteamID paramSteamID2, int paramInt, short paramShort) {
  	logger.info("Lobby Game Created");
  }
  
  public void onLobbyMatchList(int lobbiesMatching) {
  	logger.info("Lobby Match List: " + lobbiesMatching);
    NetworkHelper.steamLobbies.clear();

    for (int i =0; i < lobbiesMatching; i++ ) {
      NetworkHelper.steamLobbies.add(new SteamLobby(NetworkHelper.matcher.getLobbyByIndex(i)));
    }
  }
  
  public void onLobbyKicked(SteamID paramSteamID1, SteamID paramSteamID2, boolean paramBoolean) {
  	logger.info("Lobby Kicked");
  }
  
  public void onLobbyCreated(SteamResult paramSteamResult, SteamID paramSteamID) {
  	logger.info("Lobby Created: " + paramSteamResult.toString() + " - ID: " + paramSteamID.getAccountID());

    RemotePlayer newPlayer = new RemotePlayer(NetworkHelper.matcher.getLobbyOwner(paramSteamID));

    TogetherManager.players.add(newPlayer);
    TopPanelPlayerPanels.playerWidgets.add(new RemotePlayerWidget(newPlayer));
  }
  
  public void onFavoritesListAccountsUpdated(SteamResult paramSteamResult) {
  	logger.info("onFavoritesListAccountsUpdated");
  }
}
