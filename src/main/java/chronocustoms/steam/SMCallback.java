package com.megacrit.cardcrawl.integrations.steam;

import com.codedisaster.steamworks.SteamAuth.AuthSessionResponse;
import com.codedisaster.steamworks.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import chronospeed.*;

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
    NetworkHelper.players.add(paramSteamID);
    TopPanelPlayerPanels.playerWidgets.add(new RemotePlayerWidget(paramSteamID));
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
  
  public void onLobbyMatchList(int paramInt) {
  	logger.info("Lobby Match List: " + paramInt);
    if (paramInt > 1) {
      NetworkHelper.matcher.joinLobby(NetworkHelper.matcher.getLobbyByIndex(0));
    }
  }
  
  public void onLobbyKicked(SteamID paramSteamID1, SteamID paramSteamID2, boolean paramBoolean) {
  	logger.info("Lobby Kicked");
  }
  
  public void onLobbyCreated(SteamResult paramSteamResult, SteamID paramSteamID) {
  	logger.info("Lobby Created: " + paramSteamResult.toString());
    NetworkHelper.players.add(NetworkHelper.matcher.getLobbyOwner(paramSteamID));
    TopPanelPlayerPanels.playerWidgets.add(new RemotePlayerWidget(NetworkHelper.matcher.getLobbyOwner(paramSteamID)));
  }
  
  public void onFavoritesListAccountsUpdated(SteamResult paramSteamResult) {
  	logger.info("onFavoritesListAccountsUpdated");
  }
}
