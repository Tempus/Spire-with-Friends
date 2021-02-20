package chronoMods;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;

import com.megacrit.cardcrawl.core.*;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.integrations.steam.*;
import com.megacrit.cardcrawl.helpers.*;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.map.*;
import com.megacrit.cardcrawl.relics.*;
import com.megacrit.cardcrawl.rewards.*;
import com.codedisaster.steamworks.*;

import java.util.*;
import java.nio.*;

import chronoMods.*;
import chronoMods.steam.*;
import chronoMods.ui.deathScreen.*;
import chronoMods.ui.hud.*;
import chronoMods.ui.lobby.*;
import chronoMods.ui.mainMenu.*;
import chronoMods.utilities.*;

public class RemotePlayer
{
	// Class containing all the necessary information of a remote player

	public int x = 0;
	public int y = 0;

	public SteamID steamUser;

	public String userName = "";
	public Texture portraitImg;
	public int floor = 0;
	public int highestFloor = 0;
	public int gold = 0;
	public int hp = 0;
	public int maxHp = 0;
	// public int relic = 0;
	public float finalTime = 0F;
	public String character = "The Ironclad";

	public boolean emeraldKey, rubyKey, sapphireKey;

	public int ranking = 0;
	public boolean connection = true;
	public boolean ready = false;

	// For iterating over the taken nodes and leaving a trail
	public ArrayList<MapNodeCoords>[] nodesTaken = (ArrayList<MapNodeCoords>[])new ArrayList[5];
	//public ArrayList<MapEdge>[] edgesTaken = (ArrayList<MapEdge>[])new ArrayList[5];
	public int act = 0;

	// Boss Relic display
	public ArrayList<AbstractRelic> displayRelics = new ArrayList();

	// Potion stuff
	public ArrayList<String> potions = new ArrayList();
	public int potionSlots = 3;

	// Transfered Rewards
	public ArrayList<RewardItem> packages = new ArrayList();

	// Player Colour
	public Color colour;

	public static Color[] colourChoices = new Color[] {
	  Color.RED.cpy(),
	  Color.BLUE.cpy(),
	  Color.GREEN.cpy(),
	  Color.YELLOW.cpy(),
	  Color.ORANGE.cpy(),
	  Color.PINK.cpy(),
	  Color.PURPLE.cpy(),
	  Color.BLACK.cpy(),
	  Color.WHITE.cpy(),
	  Color.CYAN.cpy(),
	  Color.TEAL.cpy(),
	  Color.LIME.cpy(),
	  Color.GOLD.cpy(),
	  Color.BROWN.cpy(),
	  Color.MAGENTA.cpy(),
	  Color.MAROON.cpy(), 
	  Color.GRAY.cpy(),
	  Color.NAVY.cpy(),
	  Color.SKY.cpy(),
	  Color.FOREST.cpy(),
	  Color.GOLDENROD.cpy(),
	  Color.TAN.cpy(),
	  Color.FIREBRICK.cpy(),
	  Color.SALMON.cpy(),
	  Color.VIOLET.cpy(),
	  Color.LIGHT_GRAY.cpy(),
	  Color.ROYAL.cpy(),
	  Color.SLATE.cpy(),
	  Color.CHARTREUSE.cpy(),
	  Color.OLIVE.cpy(),
	  Color.SCARLET.cpy(),
	  Color.CORAL.cpy(),
	  Color.DARK_GRAY.cpy(),
	};

	public HashMap<String, Split> splits = new HashMap();

	public RemotePlayer(SteamID steamuser) {
		this.steamUser = steamuser;

		this.userName = NetworkHelper.friends.getFriendPersonaName(this.steamUser);
		int imageID = NetworkHelper.friends.getLargeFriendAvatar(this.steamUser);

		int w = NetworkHelper.utils.getImageWidth(imageID);
		int h = NetworkHelper.utils.getImageHeight(imageID);

		ByteBuffer imageBuffer = ByteBuffer.allocateDirect(w*h*4);
		try {
			boolean success = NetworkHelper.utils.getImageRGBA(imageID, imageBuffer, w*h*4);
			TogetherManager.logger.info("Image downloaded: " + success);
		}
		catch (Exception e) {
			TogetherManager.logger.info(e.getMessage());
		}

		Pixmap pixmap = new Pixmap(w, h, Pixmap.Format.RGBA8888);

		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				pixmap.drawPixel(x, y, imageBuffer.getInt());
			}
		}

		SteamID id = steamuser;

		colour = colourChoices[(TogetherManager.players.size())%(colourChoices.length-1)];

		// Runnable needed to establish GL Context
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				for (RemotePlayer player : TogetherManager.players) {
					if (player.isUser(steamuser)) {
						player.portraitImg = new Texture(pixmap, Pixmap.Format.RGBA8888, false);
						player.portraitImg.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
					}
				}
			}
		});

		// Init the map nodes
		for (int i = 0; i < 5; i++) { 
        	nodesTaken[i] = new ArrayList<MapNodeCoords>();
			// edgesTaken[i] = new ArrayList<MapEdge>();
        } 

		// Set up the default splits
		splits.put("Act 1", new Split("Act 1"));
		splits.put("Act 2", new Split("Act 2"));
		splits.put("Act 3", new Split("Act 3"));
		splits.put("Final", new Split("Final"));
	}

	public boolean isUser(SteamID id) {
		return this.steamUser.getAccountID() == id.getAccountID();
	}

	public class MapNodeCoords {
		public int x, y;

		MapNodeCoords(int x, int y) {
			this.x = x;
			this.y = y;
		}

		public boolean isAt(int x, int y) {
			return (this.x == x && this.y == y);
		}
	}

	public boolean hasNode(int act, MapRoomNode m) {
		for (MapNodeCoords c : nodesTaken[act]) {
			if (c.isAt(m.x, m.y))
				return true;
		}
		return false;
	}

	public void markMapNode() {
		// This is the position of 'special' rooms
		if (y >= 15 && TogetherManager.gameMode != TogetherManager.mode.Coop) { return; }
		if (y >= 16 && TogetherManager.gameMode == TogetherManager.mode.Coop) { return; }
		if (y == -1 || x == -1) { return; }

		nodesTaken[act].add(new MapNodeCoords(x, y));
	}

	// public MapEdge getEdgeConnectedFrom(MapRoomNode higherNode, MapRoomNode lowerNode) {
	// 	for (MapEdge edge : lowerNode.getEdges()) {
	// 	  if (higherNode.x == edge.dstX && higherNode.y == edge.dstY)
	// 		return edge; 
	// 	} 
	// 	return null;
	// }

	// public void checkEdges() {
		// MapRoomNode previousNode = null;
		// for (MapRoomNode currentNode : nodesTaken[act]) {
		// 	if (previousNode != null)
		// 		edgesTaken[act].add(getEdgeConnectedFrom(currentNode, previousNode));
		// 	previousNode = currentNode;
		// }
	// }
}