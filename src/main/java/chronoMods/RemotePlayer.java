package chronoMods;

import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;

import com.megacrit.cardcrawl.core.*;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.integrations.steam.*;
import com.megacrit.cardcrawl.helpers.*;
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
    public Texture portraitImg = null;
    public int floor = 0;
    public int gold = 0;
    public int hp = 0;
    // public int relic = 0;

    public int ranking = 0;
    public boolean connection = true;


    public RemotePlayer(SteamID steamuser) {
        this.steamUser = steamuser;

        this.userName = NetworkHelper.friends.getFriendPersonaName(this.steamUser);
        int imageID = NetworkHelper.friends.getSmallFriendAvatar(this.steamUser);

        int w = NetworkHelper.utils.getImageWidth(imageID);
        int h = NetworkHelper.utils.getImageHeight(imageID);

        ByteBuffer imageBuffer = ByteBuffer.allocateDirect(w*h*4);
        try {
            boolean success = NetworkHelper.utils.getImageRGBA(imageID, imageBuffer, w*h*4);
            TogetherManager.logger.info("Image downloaded: " + success);

            byte[] arr = new byte[imageBuffer.remaining()];
            imageBuffer.get(arr);

            Pixmap pixmap = new Pixmap(new Gdx2DPixmap(arr, 0, arr.length, 4));
            this.portraitImg = new Texture(pixmap);
        }
        catch (Exception e) {
            TogetherManager.logger.info(e.getMessage());
        }
    }
}