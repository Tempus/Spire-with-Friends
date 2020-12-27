package chronoMods.steam;

import chronoMods.*;
import chronoMods.steam.*;
import chronoMods.ui.hud.*;
import com.codedisaster.steamworks.*;
import com.megacrit.cardcrawl.screens.mainMenu.MenuButton;
import java.util.*;

public class SteamLobby {

	public SteamID steamID;

	public String name = "";
	public String owner = "MegaCrit";
	public String mode = "Versus";
	public String ascension = "0";
	public String character = "Ironclad";
	public int capacity = 6;
	public int members = 0;

	public ArrayList<String> memberNames = new ArrayList();

	public SteamID ownerID;

    public static ArrayList<RemotePlayer> players = new ArrayList();

	public SteamLobby (SteamID id) {
		this.steamID = id;
		TogetherManager.logger.info("New Lobby with ID: " + id);

		try {
			name = NetworkHelper.matcher.getLobbyData(steamID, "name");
			TogetherManager.logger.info("Lobby name: " + name);
			
			mode = NetworkHelper.matcher.getLobbyData(steamID, "mode");
			TogetherManager.logger.info("Lobby mode: " + mode);
			
			ascension = NetworkHelper.matcher.getLobbyData(steamID, "ascension");
			TogetherManager.logger.info("Lobby ascension: " + ascension);
			
			character = NetworkHelper.matcher.getLobbyData(steamID, "character");
			TogetherManager.logger.info("Lobby character: " + character);
			
			ownerID = NetworkHelper.matcher.getLobbyOwner(this.steamID);
			TogetherManager.logger.info("Lobby ownerID: " + ownerID);
			
			owner = NetworkHelper.matcher.getLobbyData(steamID, "owner");
			//TogetherManager.logger.info("Lobby owner name: " + getOwnerName());
			
			memberNames = new ArrayList<String>(Arrays.asList(NetworkHelper.matcher.getLobbyData(steamID, "members").split("\t")));
			TogetherManager.logger.info("Lobby members: " + NetworkHelper.matcher.getLobbyData(steamID, "members"));

			TogetherManager.logger.info("Lobby member count: " + getMemberCount());
			
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
		return TogetherManager.currentUser.steamUser == this.ownerID;
	}

	public int getMemberCount() {
		try {
			this.members = NetworkHelper.matcher.getNumLobbyMembers(this.steamID);
		} catch (Exception e) {}

		return this.members;
	}

	public ArrayList<RemotePlayer> getLobbyMembers() {
		members = getMemberCount();
		players.clear();

		try {
			for (int i = 0; i < members; i++) {
				RemotePlayer newPlayer = new RemotePlayer(NetworkHelper.matcher.getLobbyMemberByIndex(steamID, i));
				players.add(newPlayer);
        		TopPanelPlayerPanels.playerWidgets.add(new RemotePlayerWidget(newPlayer));
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

}