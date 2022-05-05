package chronoMods.network.steam;

import chronoMods.*;
import chronoMods.network.*;
import chronoMods.network.steam.*;
import chronoMods.ui.hud.*;
import com.codedisaster.steamworks.*;

import java.util.*;
import java.util.concurrent.*;

public class SteamLobby extends Lobby {

	public SteamID steamID;
	public SteamID ownerID;
	public SteamIntegration steam;

	public SteamLobby (Integration service, SteamID id) {
		super(service);
		this.steam = (SteamIntegration)service;

		this.steamID = id;

		try {
			ownerID = steam.matcher.getLobbyOwner(this.steamID);
			memberNames = new ArrayList<String>(Arrays.asList(steam.matcher.getLobbyData(steamID, "members").split("\t")));
		} catch (Exception e) {}

		fetchAllMetadata();
	}

	public String getOwnerName() {
		try {
			owner = steam.matcher.getLobbyData(steamID, "owner");
			// this.owner = steam.friends.getFriendPersonaName(steam.matcher.getLobbyOwner(this.steamID));
		} catch (Exception e) {}

		return this.owner;
	}

	public boolean isOwner() {
		if (ownerID == null) {
			try {
				ownerID = steam.matcher.getLobbyOwner(this.steamID);
				memberNames = new ArrayList<String>(Arrays.asList(steam.matcher.getLobbyData(steamID, "members").split("\t")));
			} catch (Exception e) {}
		}

		if (ownerID == null)
			return false;

		return ((SteamPlayer)TogetherManager.getCurrentUser()).isUser(this.ownerID);
	}

	public void newOwner() {
		for (RemotePlayer player : TogetherManager.players) {
			if (!TogetherManager.currentUser.isUser(player) && player instanceof SteamPlayer) {
				NetworkHelper.steam.matcher.setLobbyData(steamID, "owner", player.userName);
				NetworkHelper.steam.matcher.setLobbyOwner(steamID, ((SteamPlayer)player).steamUser);
				ownerID = ((SteamPlayer)player).steamUser;

				return;
			}
		}
	}

	public int getMemberCount() {
		return this.memberNames.size();
	}

	public CopyOnWriteArrayList<RemotePlayer> getLobbyMembers() {
		int memberCount = 1;
		try {
			memberCount = steam.matcher.getNumLobbyMembers(this.steamID);
			TogetherManager.log("get Members in  lobby: " + memberCount);
		} catch (Exception e) {}
		players.clear();
		TopPanelPlayerPanels.playerWidgets.clear();

		try {
			for (int i = 0; i < memberCount; i++) {
				RemotePlayer newPlayer = new SteamPlayer(steam.matcher.getLobbyMemberByIndex(steamID, i));
				players.add(newPlayer);
				// TopPanelPlayerPanels.playerWidgets.add(new RemotePlayerWidget(newPlayer));
				TogetherManager.log("get Members created: " + newPlayer.userName);
			}
		} catch (Exception e) {}

		return players;
	}

	public String getMemberNameList() {
		StringBuilder out = new StringBuilder();
		for (RemotePlayer o : players)
		{
			out.append(o.userName);
			out.append("\t");
		}
		return out.toString().trim();
	}

	public Object getID() {
		return steamID;
	}

	public void leaveLobby() {
		steam.matcher.leaveLobby(steamID);
		TogetherManager.currentLobby = null;
	}

	public void setJoinable(boolean toggle) {
        steam.matcher.setLobbyJoinable(steamID, toggle);
	}

	public void setPrivate(boolean toggle) {
		if (toggle)
			steam.matcher.setLobbyType(steamID, SteamMatchmaking.LobbyType.FriendsOnly);
		else
			steam.matcher.setLobbyType(steamID, SteamMatchmaking.LobbyType.Public);
	}

	public void join() {
		steam.matcher.joinLobby(steamID);
	}

	@Override
	public int getCapacity() {
		if (TogetherManager.gameMode == TogetherManager.mode.Coop)
			return 6;
		if (TogetherManager.gameMode == TogetherManager.mode.Versus)
			return 200;
		if (TogetherManager.gameMode == TogetherManager.mode.Bingo)
			return 200;
		return 0;
	}

	@Override
	public String getMetadata(String key) {
		return steam.matcher.getLobbyData(steamID, key);
	}

	@Override
	public void setMetadata(Map<String, String> pairs) {
		pairs.forEach((k, v) -> {
			steam.matcher.setLobbyData(steamID, k, v);
		});
	}

  	public long getOwner() {
  		return ownerID.getAccountID();
  	}
 }