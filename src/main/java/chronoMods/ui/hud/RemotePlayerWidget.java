package chronoMods.ui.hud;

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

public class RemotePlayerWidget
{
	// Widget displaying the player status of the remote players

	public float x = 0.0F;
	public float y = 0.0F;

	private static final float ICON_W = 36f * Settings.scale;

	public RemotePlayer player;

	public RemotePlayerWidget(RemotePlayer player) {
		this.player = player;
	try {
      TogetherManager.logger.info(NetworkHelper.friends.getFriendPersonaName(player.steamUser));
    } catch (Exception e) {
      TogetherManager.logger.info("Widget Init: " + e.getMessage());
    }

	}

	public void setPos(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public void render(SpriteBatch sb) { 
		// Render Background
		// sb.draw(this.panelImg, this.x, this.y, 137.5F, 40.0F, 275.0F, 80.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 275, 80, false, false);
		sb.draw(TogetherManager.panelImg, this.x, this.y);

		// Render Portrait
		if (player.portraitImg != null) {
			sb.draw(player.portraitImg, this.x+26.0F, this.y+12.0F, 56f, 56f);
		}

		// Render Portrait frame
		// sb.draw(TogetherManager.portraitFrames.get(1), this.x, this.y);
	    sb.draw(TogetherManager.portraitFrames.get(0), this.x - 160.0F * Settings.scale, this.y - 96.0F * Settings.scale, 0.0F, 0.0F, 432.0F, 243.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 1920, 1080, false, false);

		// Draw the user name
		FontHelper.renderSmartText(sb, FontHelper.topPanelInfoFont, player.userName, this.x + 96.0F, this.y + 64.0F, Settings.CREAM_COLOR);

		// Draw current floor
		sb.draw(ImageMaster.TP_FLOOR, this.x + 88.0F,  this.y + 4.0F, ICON_W, ICON_W);
		FontHelper.renderSmartText(sb, FontHelper.cardDescFont_N, Integer.toString(player.floor), this.x + 124.0F,  this.y + 32.0F, Settings.CREAM_COLOR);

		// Draw HP
		sb.draw(ImageMaster.TP_HP,    this.x + 164.0F, this.y + 4.0F, ICON_W, ICON_W);
		FontHelper.renderSmartText(sb, FontHelper.cardDescFont_N, Integer.toString(player.hp),    this.x + 196.0F,  this.y + 32.0F, Settings.RED_TEXT_COLOR);

		// Draw Gold
		sb.draw(ImageMaster.TP_GOLD,  this.x + 236.0F, this.y + 4.0F, ICON_W, ICON_W);
		FontHelper.renderSmartText(sb, FontHelper.cardDescFont_N, Integer.toString(player.gold),  this.x + 272.0F,  this.y + 32.0F, Settings.GOLD_COLOR);

	}

}