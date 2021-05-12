package chronoMods.network.steam;

import chronoMods.*;
import chronoMods.network.NetworkHelper;
import chronoMods.network.steam.*;
import chronoMods.ui.hud.*;
import com.codedisaster.steamworks.*;

import java.util.*;
import java.util.concurrent.*;

public class SteamLobby extends Lobby {

	public SteamID steamID;
	public SteamID ownerID;

	public SteamLobby (SteamID id) {
		this.steamID = id;
		TogetherManager.log("New Lobby with ID: " + id);

		try {
			name = NetworkHelper.matcher.getLobbyData(steamID, "name");
			TogetherManager.log("Lobby name: " + name);
			
			mode = NetworkHelper.matcher.getLobbyData(steamID, "mode");
			TogetherManager.log("Lobby mode: " + mode);
			
			ascension = NetworkHelper.matcher.getLobbyData(steamID, "ascension");
			TogetherManager.log("Lobby ascension: " + ascension);
			
			character = NetworkHelper.matcher.getLobbyData(steamID, "character");
			TogetherManager.log("Lobby character: " + character);

			heart = Boolean.valueOf(NetworkHelper.matcher.getLobbyData(steamID, "heart"));
			TogetherManager.log("Lobby heart: " + heart);

			neow = Boolean.valueOf(NetworkHelper.matcher.getLobbyData(steamID, "neow"));
			TogetherManager.log("Lobby neow: " + neow);

			ironman = Boolean.valueOf(NetworkHelper.matcher.getLobbyData(steamID, "ironman"));
			TogetherManager.log("Lobby ironman: " + ironman);
			
			ownerID = NetworkHelper.matcher.getLobbyOwner(this.steamID);
			TogetherManager.log("Lobby ownerID: " + ownerID);
			
			owner = NetworkHelper.matcher.getLobbyData(steamID, "owner");
			//TogetherManager.log("Lobby owner name: " + getOwnerName());
			
			memberNames = new ArrayList<String>(Arrays.asList(NetworkHelper.matcher.getLobbyData(steamID, "members").split("\t")));
			TogetherManager.log("Lobby members: " + NetworkHelper.matcher.getLobbyData(steamID, "members"));

			TogetherManager.log("Lobby member count: " + getMemberCount());
			
			// capacity = NetworkHelper.matcher.getLobbyMemberLimit(steamID);
		} catch (Exception e) {}
	}

	public String getOwnerName() {
		try {
			this.owner = NetworkHelper.friends.getFriendPersonaName(NetworkHelper.matcher.getLobbyOwner(this.steamID));
		} catch (Exception e) {}

		return this.owner;
	}

	public boolean isOwner() {
		return TogetherManager.currentUser.isUser(this.ownerID);
	}

	public void updateOwner() {
		ownerID = NetworkHelper.matcher.getLobbyOwner(this.steamID);
	}

	public int getMemberCount() {
		return this.memberNames.size();
	}

	public CopyOnWriteArrayList<RemotePlayer> getLobbyMembers() {
		int memberCount = 1;
		try {
			memberCount = NetworkHelper.matcher.getNumLobbyMembers(this.steamID);
			TogetherManager.log("get Members in  lobby: " + memberCount);
		} catch (Exception e) {}
		players.clear();
		TopPanelPlayerPanels.playerWidgets.clear();

		try {
			for (int i = 0; i < memberCount; i++) {
				RemotePlayer newPlayer = new RemotePlayer(NetworkHelper.matcher.getLobbyMemberByIndex(steamID, i));
				players.add(newPlayer);
        		TopPanelPlayerPanels.playerWidgets.add(new RemotePlayerWidget(newPlayer));
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
}