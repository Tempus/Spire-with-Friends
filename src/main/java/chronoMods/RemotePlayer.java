package chronoMods;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.Pixmap.Format;

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

import com.evacipated.cardcrawl.modthespire.*;

import chronoMods.*;
import chronoMods.steam.*;
import chronoMods.ui.deathScreen.*;
import chronoMods.ui.hud.*;
import chronoMods.ui.lobby.*;
import chronoMods.ui.mainMenu.*;
import chronoMods.utilities.*;
import chronoMods.coop.drawable.*;

public class RemotePlayer
{
	// Class containing all the necessary information of a remote player

	public int x = 0;
	public int y = 0;

	public SteamID steamUser;

	public String userName = "";
    public boolean useFallbackFont = false;
	public Texture portraitImg;
	public int floor = 0;
	public int highestFloor = 0;
	public int gold = 0;
	public int hp = 0;
	public int maxHp = 0;
	
	public int relics = 0;
	public int cards = 0;
	public int upgrades = 0;

	public float finalTime = 0F;
	public String character = "The Ironclad";

	public boolean emeraldKey, rubyKey, sapphireKey;

	public int ranking = 0;
	public boolean connection = true;
	public boolean ready = false;

	// Widget Reference
	public RemotePlayerWidget widget;

	// Compatibility checks
	public float version;
	public int modHash;
	public boolean safeMods = true;

	// For iterating over the taken nodes and leaving a trail
	public ArrayList<MapNodeCoords>[] nodesTaken = (ArrayList<MapNodeCoords>[])new ArrayList[5];
	//public ArrayList<MapEdge>[] edgesTaken = (ArrayList<MapEdge>[])new ArrayList[5];
	public int act = 1;

	// Boss Relic display
	public ArrayList<AbstractRelic> displayRelics = new ArrayList();

	// Potion stuff
	public ArrayList<String> potions = new ArrayList();
	public int potionSlots = 3;

	// Transfered Rewards
	public ArrayList<RewardItem> packages = new ArrayList();

	// Player Colour     
	public Color colour;

	// Map Paint!
    public MapCanvas[] drawable = new MapCanvas[4]; 

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

		// Update the Avatar
		int imageID = NetworkHelper.friends.getLargeFriendAvatar(this.steamUser);
		TogetherManager.log("ImageID: " + imageID);
		updateAvatar(imageID, 0, 0);


        // Choose a colour
		setColour(colourChoices[(TogetherManager.players.size())%(colourChoices.length-1)]);

		// Init the map nodes
		for (int i = 0; i < 5; i++) { 
        	nodesTaken[i] = new ArrayList<MapNodeCoords>();
			// edgesTaken[i] = new ArrayList<MapEdge>();
        } 

		// Set up the default splits
		splits.put("Act 1", new Split("Act 1", 1));
		splits.put("Act 2", new Split("Act 2", 2));
		splits.put("Act 3", new Split("Act 3", 3));
		splits.put("Final", new Split("Final", 4));
	}

	public void setColour(Color colour) {
		this.colour = colour;
	}

	public void createMapDrawables() {
        // This goes into Remote Player later, only for Coop
		for (int j = 0; j < 3; j++) 
	        drawable[j] = new MapCanvas(new Pixmap(Settings.WIDTH, Settings.HEIGHT + (int)(2300.0F * Settings.scale), Format.RGBA8888));

        drawable[3] = new MapCanvas(new Pixmap(Settings.WIDTH, Settings.HEIGHT + (int)(300.0F * Settings.scale), Format.RGBA8888));		

		for (int j = 0; j < 4; j++)  {
	        drawable[j].drawColour = this.colour;
	        drawable[j].clear();
		}

		TogetherManager.log(userName + " has set the colour to " + this.colour);
	}

	public void updateAvatar(int imageID, int width, int height) {
		if (width <= 0)
			width = NetworkHelper.utils.getImageWidth(imageID);
		if (height <= 0)
			height = NetworkHelper.utils.getImageHeight(imageID);

		TogetherManager.log("W: " + width + ", H: " + height);

		ByteBuffer imageBuffer = ByteBuffer.allocateDirect(width*height*4);
		try {
			boolean success = NetworkHelper.utils.getImageRGBA(imageID, imageBuffer, width*height*4);
			TogetherManager.log("Image downloaded: " + success);
		}
		catch (Exception e) {
			TogetherManager.log(e.getMessage());
		}

		Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				pixmap.drawPixel(x, y, imageBuffer.getInt());
			}
		}

		SteamID su = this.steamUser;

		// Runnable needed to establish GL Context
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				for (RemotePlayer player : TogetherManager.players) {
					if (player.isUser(su)) {
						player.portraitImg = new Texture(pixmap);
						player.portraitImg.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
					}
				}
			}
		});
		TogetherManager.log("We have completed assigning the Steam image");
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