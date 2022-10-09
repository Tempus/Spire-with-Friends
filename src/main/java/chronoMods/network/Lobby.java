package chronoMods.network;

import chronoMods.TogetherManager;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class Lobby {

	public Integration service;

	public String name = "";
	public String owner = "MegaCrit";
	public String mode = "Versus";
	public String ascension = "0";
	public String character = "IRONCLAD";

	public boolean heart;
	public boolean neow;
	public boolean ironman;

	public int capacity = 6;
	public int members = 0;

	public ArrayList<String> memberNames = new ArrayList();
    public CopyOnWriteArrayList<RemotePlayer> players = new CopyOnWriteArrayList();

    public Lobby(Integration service) {
    	this.service = service;
		TogetherManager.currentUser = service.makeCurrentUser();
    }

	public void fetchAllMetadata() {
 		name = getMetadata("name");
 		owner = getOwnerName();
 		mode = getMetadata("mode");
 		ascension = getMetadata("ascension");
 		character = getMetadata("character");
 		heart = Boolean.parseBoolean(getMetadata("heart"));
 		neow = Boolean.parseBoolean(getMetadata("neow"));
 		ironman = Boolean.parseBoolean(getMetadata("ironman"));
 		capacity = getCapacity();
 		members = getMemberCount();
 	}

 	public abstract String getOwnerName();

 	public abstract long getOwner();

	public abstract boolean isOwner();

	public abstract void newOwner();

	public abstract int getMemberCount();

	public abstract CopyOnWriteArrayList<RemotePlayer> getLobbyMembers();

	public abstract String getMemberNameList();

	public abstract Object getID();

	public abstract void leaveLobby();

	public abstract void setJoinable(boolean toggle);

	public abstract void setPrivate(boolean toggle);

	public abstract void join();

	public abstract int getCapacity();

 	public abstract String getMetadata(String key);

 	public abstract void setMetadata(Map<String, String> pairs);
}