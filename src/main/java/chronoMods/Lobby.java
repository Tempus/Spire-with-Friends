package chronoMods.network;

import chronoMods.*;
import chronoMods.steam.*;
import chronoMods.ui.hud.*;
import com.codedisaster.steamworks.*;
import com.megacrit.cardcrawl.screens.mainMenu.MenuButton;
import java.util.*;
import java.util.concurrent.*;

public abstract class Lobby {

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


	public abstract String getOwnerName();

	public abstract boolean isOwner();

	public abstract void updateOwner();

	public abstract int getMemberCount();

	public abstract CopyOnWriteArrayList<RemotePlayer> getLobbyMembers();

	public abstract String getMemberNameList();

}