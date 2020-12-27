package chronoMods.ui.hud;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.*;
import com.badlogic.gdx.math.*;

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

public class RemotePlayerWidget implements Comparable
{
	// Widget displaying the player status of the remote players

	// Ranking
	public int rank = 0;

	// Position
	public float x = -8.0F;
	public float y = 0.0F;

	// For interpolation effects
	public float sx = 0.0F;
	public float sy = 0.0F;

	public float dx = 0.0F;
	public float dy = 0.0F;

	public float duration;
	public float standardDuration = 1.5f;

	private static final float ICON_W = 36f * Settings.scale;

	public RemotePlayer player;

	public RemotePlayerWidget(RemotePlayer player) {
		this.player = player;

		// Set the rank
		rank = TopPanelPlayerPanels.playerWidgets.size();

		// Set the name
		try {
	      TogetherManager.logger.info(NetworkHelper.friends.getFriendPersonaName(player.steamUser));
	    } catch (Exception e) {
	      TogetherManager.logger.info("Widget Init: " + e.getMessage());
	    }
	}

	// Sets the position for lerping.
	public void setPos(float x, float y) {
		this.sx = this.x;
		this.sy = this.y;

		this.dx = x;
		this.dy = y;

		this.duration = standardDuration;
	}

	// Sets the rank in the list, and from that determines the destination position.
	public void setRank(int rank) {
	    TogetherManager.logger.info("Setting rank to " + rank + " for " + player.userName);

		if (this.rank != rank) 
			CardCrawlGame.sound.playV("APPEAR", 0.5F);

		this.rank = rank;

		setPos(-8.0F * Settings.scale, 800.0F * Settings.scale - 80.0F * rank * Settings.scale);
	}

	// Comparators for sorting, returns negative, 0, or positive for lower than, equal to, or higher than respectively
    @Override
    public int compareTo(Object compareToMe) {
        // These should be sorted in ascending order, first by finished time if available, then by floor
    	RemotePlayerWidget c = (RemotePlayerWidget)compareToMe;

    	// We've both completed the run
    	if (player.finalTime > 0.0F && c.player.finalTime > 0.0F) {
    		TogetherManager.logger.info("Compared by final time");
    		return (int)(player.finalTime - c.player.finalTime);
    	}
    	// We're not done but he is
    	else if (player.finalTime == 0.0F && c.player.finalTime > 0.0F) {
    		TogetherManager.logger.info("We're not done");
    		return 1;
    	}
    	// He's done but we're not
    	else if (c.player.finalTime == 0.0F && player.finalTime > 0.0F) {
    		TogetherManager.logger.info("They're not done");
    		return -1;
    	}

    	// Neither of us are done
    	TogetherManager.logger.info("Floor comparison! " + player.floor + " - " + c.player.floor);
	    return player.floor - c.player.floor;
    }

    @Override
    public String toString() {
        return "Remote Player: " + player.userName + " @ Rank " + rank;
    }

    // Render the widgets here
	public void render(SpriteBatch sb) { 
		// These babies don't update, so we'll do the lerping here.
	    if (this.duration > 0.0F) {
		    this.x = Interpolation.exp10Out.apply(this.dx, this.sx, this.duration);
		    this.y = Interpolation.exp10Out.apply(this.dy, this.sy, this.duration);

	    	this.duration -= Gdx.graphics.getDeltaTime(); }
    	else {
    		this.duration = 0.0f;
    	}

		// Render Background
		// sb.draw(this.panelImg, this.x, this.y, 137.5F, 40.0F, 275.0F, 80.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 275, 80, false, false);
		sb.draw(TogetherManager.panelImg, this.x, this.y);

		// Draw the player colour indicator
		sb.setColor(player.colour);
		sb.draw(TogetherManager.colourIndicatorImg, this.x, this.y);
		sb.setColor(Color.WHITE);

		// Render Portrait
		if (player.portraitImg != null) {
			sb.draw(player.portraitImg, this.x+26.0F, this.y+12.0F, 56f, 56f);
		}

		// Render Portrait frame
	    sb.draw(TogetherManager.portraitFrames.get(0), this.x - 160.0F * Settings.scale, this.y - 96.0F * Settings.scale, 0.0F, 0.0F, 432.0F, 243.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 1920, 1080, false, false);

		// Draw the user name
		FontHelper.renderSmartText(sb, FontHelper.topPanelInfoFont, player.userName, this.x + 96.0F, this.y + 64.0F, Settings.CREAM_COLOR);

		// The player hasn't finished the run
		if (player.finalTime == 0.0F) {
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
		// We've finished the run
		else {
			sb.draw(ImageMaster.TIMER_ICON, this.x + 88.0F,  this.y + 6.0F, ICON_W, ICON_W);
			FontHelper.renderSmartText(sb, FontHelper.cardDescFont_N, VersusTimer.returnTimeString(player.finalTime), this.x + 124.0F,  this.y + 32.0F, Settings.CREAM_COLOR);
		}
	}
}