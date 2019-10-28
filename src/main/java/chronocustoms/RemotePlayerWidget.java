package chronospeed;

import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;

import com.megacrit.cardcrawl.core.*;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.integrations.steam.*;
import com.megacrit.cardcrawl.helpers.*;
import com.codedisaster.steamworks.*;

import java.util.*;
import java.nio.*;

import chronospeed.*;

public class RemotePlayerWidget
{
	/* Widget displaying the player status of the remote players

			Contains its own position
			Displays user name, steam portrait, floor, gold, current hp, boss relics

	*/

	public float x = 0.0F;
	public float y = 0.0F;

	private static final float ICON_W = 36f * Settings.scale;

	public SteamID steamUser;

    public String userName = "Test User";
    public Texture portraitImg = null;
    public int floor = 55;
    public int gold = 8000;
    public int hp = 100;
    // public int relic = 0;

    public int placing = 0;
    public boolean connection = true;


	public RemotePlayerWidget(SteamID steamUser) {
		this.steamUser = steamUser;
		this.userName = NetworkHelper.friends.getFriendPersonaName(steamUser);
		int imageID = NetworkHelper.friends.getSmallFriendAvatar(steamUser);

		int w = NetworkHelper.utils.getImageWidth(imageID);
		int h = NetworkHelper.utils.getImageHeight(imageID);

		ByteBuffer imageBuffer = ByteBuffer.allocateDirect(w*h*4);
		try {
			boolean success = NetworkHelper.utils.getImageRGBA(imageID, imageBuffer, w*h*4);
			ChronoCustoms.logger.info("Image downloaded: " + success);

			byte[] arr = new byte[imageBuffer.remaining()];
			imageBuffer.get(arr);

			Pixmap pixmap = new Pixmap(new Gdx2DPixmap(arr, 0, arr.length, 4));
			this.portraitImg = new Texture(pixmap);
		}
		catch (Exception e) {
			ChronoCustoms.logger.info(e.getMessage());
		}
	}

	public void setPos(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public void render(SpriteBatch sb) { 
		// Render Background
		// sb.draw(this.panelImg, this.x, this.y, 137.5F, 40.0F, 275.0F, 80.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 275, 80, false, false);
		sb.draw(ChronoCustoms.panelImg, this.x, this.y);

		// Render Portrait
		if (this.portraitImg != null) {
			sb.draw(this.portraitImg, this.x+24.0F, this.y+10.0F);
		}

		// Render Portrait frame
		// sb.draw(ChronoCustoms.portraitFrames.get(1), this.x, this.y);
	    sb.draw(ChronoCustoms.portraitFrames.get(0), this.x - 160.0F * Settings.scale, this.y - 96.0F * Settings.scale, 0.0F, 0.0F, 432.0F, 243.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 1920, 1080, false, false);

		// Draw the user name
		FontHelper.renderSmartText(sb, FontHelper.topPanelInfoFont, this.userName, this.x + 96.0F, this.y + 64.0F, Settings.CREAM_COLOR);

		// Draw current floor
		sb.draw(ImageMaster.TP_FLOOR, this.x + 88.0F,  this.y + 4.0F, ICON_W, ICON_W);
		FontHelper.renderSmartText(sb, FontHelper.cardDescFont_N, Integer.toString(this.floor), this.x + 124.0F,  this.y + 32.0F, Settings.CREAM_COLOR);

		// Draw HP
		sb.draw(ImageMaster.TP_HP,    this.x + 164.0F, this.y + 4.0F, ICON_W, ICON_W);
		FontHelper.renderSmartText(sb, FontHelper.cardDescFont_N, Integer.toString(this.hp),    this.x + 196.0F,  this.y + 32.0F, Settings.RED_TEXT_COLOR);

		// Draw Gold
		sb.draw(ImageMaster.TP_GOLD,  this.x + 236.0F, this.y + 4.0F, ICON_W, ICON_W);
		FontHelper.renderSmartText(sb, FontHelper.cardDescFont_N, Integer.toString(this.gold),  this.x + 272.0F,  this.y + 32.0F, Settings.GOLD_COLOR);

	}

}