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
    public int gold = 0;
    public int hp = 0;
    // public int relic = 0;
    public float finalTime = 0F;

    public int ranking = 0;
    public boolean connection = true;
    public boolean ready = false;

    // For iterating over the taken nodes and leaving a trail
    public ArrayList<MapRoomNode> nodesTaken = new ArrayList(); 
    public ArrayList<MapEdge> edgesTaken = new ArrayList(); 

    public Color colour;

    private static Color[] colourChoices = new Color[] {
          Color.RED,
          Color.BLUE,
          Color.GREEN,
          Color.YELLOW,
          Color.ORANGE,
          Color.PINK,
          Color.PURPLE,
          Color.BLACK,
          Color.WHITE,
          Color.CYAN,
          Color.TEAL,
          Color.LIME,
          Color.GOLD,
          Color.BROWN,
          Color.MAGENTA,
          Color.MAROON,     
          Color.GRAY,
          Color.NAVY,
          Color.SKY,
          Color.FOREST,
          Color.GOLDENROD,
          Color.TAN,
          Color.FIREBRICK,
          Color.SALMON,
          Color.VIOLET,
          Color.LIGHT_GRAY,
          Color.ROYAL,
          Color.SLATE,
          Color.CHARTREUSE,
          Color.OLIVE,
          Color.SCARLET,
          Color.CORAL,
          Color.DARK_GRAY,
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

        // Choose the player color
        colour = colourChoices[TogetherManager.players.size()];

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

        // Set up the default splits
        splits.put("Act 1", new Split("Act 1"));
        splits.put("Act 2", new Split("Act 2"));
        splits.put("Act 3", new Split("Act 3"));
        splits.put("Final", new Split("Final"));
    }

    public boolean isUser(SteamID id) {
        return this.steamUser.getAccountID() == id.getAccountID();
    }

    public void markMapNode() {
        // This is the position of 'special' rooms
        if (y == 15) { return; }

        // We're on the normal map, so find the edges please
        MapRoomNode currentNode = AbstractDungeon.map.get(y).get(x);

        if (currentNode != null && nodesTaken.size() > 0) {
            edgesTaken.add(getEdgeConnectedFrom(currentNode, nodesTaken.get(nodesTaken.size() - 1)));
            TogetherManager.logger.info("Added edge to player: " + currentNode.getEdgeConnectedTo(nodesTaken.get(nodesTaken.size() - 1)));
        }
        
        nodesTaken.add(currentNode);
    }

    public MapEdge getEdgeConnectedFrom(MapRoomNode higherNode, MapRoomNode lowerNode) {
        for (MapEdge edge : lowerNode.getEdges()) {
          if (higherNode.x == edge.dstX && higherNode.y == edge.dstY)
            return edge; 
        } 
        return null;
    }
}