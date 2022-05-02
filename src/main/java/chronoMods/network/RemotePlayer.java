package chronoMods.network;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.Pixmap.Format;

import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.core.*;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.integrations.steam.*;
import com.megacrit.cardcrawl.helpers.*;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.map.*;
import com.megacrit.cardcrawl.relics.*;
import com.megacrit.cardcrawl.rewards.*;
import com.megacrit.cardcrawl.cards.*;
import com.codedisaster.steamworks.*;

import java.util.*;
import java.nio.*;

import com.evacipated.cardcrawl.modthespire.*;

import chronoMods.*;
import chronoMods.network.*;
import chronoMods.ui.deathScreen.*;
import chronoMods.ui.hud.*;
import chronoMods.ui.lobby.*;
import chronoMods.ui.mainMenu.*;
import chronoMods.utilities.*;
import chronoMods.coop.drawable.*;
import chronoMods.coop.hardmode.*;

public class RemotePlayer
{
	// Class containing all the necessary information of a remote player

	public int x = 0;
	public int y = 0;

	public String userName = "";
	public boolean useFallbackFont = false;
	public Texture portraitImg;
	
	public int floor = 0;
	public int highestFloor = 0;
	public int gold = 0;
	public int hp = 0;
	public int maxHp = 0;
	
	public int lastBoss = 0;

	public int relics = 0;
	public int cards = 0;
	public int upgrades = 0;

	public float finalTime = 0F;
	public AbstractPlayer character = CardCrawlGame.characterManager.getCharacter(AbstractPlayer.PlayerClass.IRONCLAD);
	public String characterCutscene = "";

	public boolean emeraldKey, rubyKey, sapphireKey, act4arrived;

	public int ranking = 0;
	public boolean connection = true;
	public boolean ready = false;
	public boolean victory = false;

	// Widget Reference
	public RemotePlayerWidget widget;

	// Compatibility checks
	public float version;
	public int modHash;
	public boolean safeMods = true;

	// Master Deck Cards
	public CardGroup deck = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);

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

	// Hard Mode Heart Holder
	public HearthOption.Options heartChosen;

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


	// Bingo Properties

	public Texture[][] bingoCard =  new Texture[5][5];
    public int[][] bingoCardIndices = new int[5][5];
	public int team = 0;
	public String teamName = "";
	public Texture bingoMark;


	////////////////////////////////////////////
	// Highly Recommended you reimplement these:

	public RemotePlayer() {
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

		createFallbackAvatar();
	}

	// NB. You will also need to grab the service's player avatar as part of the reimplementations.

	public boolean isUser(Object player) {
		if (player instanceof String)
			return userName.equals(player);
		return false;
	}

	public long getAccountID() { return this.userName.hashCode(); }

	//
	////////////////////////////////////////////
	// Below here, you should not reimplement.

	public Texture getPortrait() {
		return portraitImg;
	}

	public void createFallbackAvatar() {
	}

	public boolean isUser(long accountID) {
		return accountID == getAccountID();
	}

	public boolean isUser(RemotePlayer player) {
		return getAccountID() == player.getAccountID();
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
}
