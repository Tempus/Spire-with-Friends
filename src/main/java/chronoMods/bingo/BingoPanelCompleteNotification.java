package chronoMods.bingo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.*;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.vfx.*;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.dungeons.*;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;

import java.util.*;
import java.util.stream.*;

import chronoMods.*;
import chronoMods.network.steam.*;
import chronoMods.network.*;
import chronoMods.coop.*;
import chronoMods.coop.relics.*;
import chronoMods.ui.deathScreen.*;
import chronoMods.ui.hud.*;
import chronoMods.ui.lobby.*;
import chronoMods.ui.mainMenu.*;
import chronoMods.utilities.*;

public class BingoPanelCompleteNotification extends AbstractGameEffect {
	private static final float TEXT_DURATION = 1.8F;
	private static final float DRAW_X = Settings.WIDTH / 2.0F, DRAW_Y = Settings.HEIGHT * 0.6F;	
	private static final float STARTING_OFFSET_Y = 0.0F * Settings.scale;
	private static final float TARGET_OFFSET_Y = 120.0F * Settings.scale;
	private static final float LERP_RATE = 5.0F;

	public static final String[] EasyBingo = CardCrawlGame.languagePack.getUIString("EasyBingo").TEXT;
	public static final String[] MedBingo = CardCrawlGame.languagePack.getUIString("MedBingo").TEXT;
	public static final String[] HardBingo = CardCrawlGame.languagePack.getUIString("HardBingo").TEXT;

	public static List<String> allBingo;

	private float X, Y, offsetX, offsetY;
	public float scaleMod = 1.0f;
	private String msg;

	public RemotePlayer playerMarked;
	public int mark;

	public Color textColour;


	/* Notification animation

		- Panel with text on it slides in from edge of screen
			- Location is the top left corner, above the Player Info panels but below the top bar
		- While it slides, it also fades in

		- When it's arrived, a centered Card Mark Icon fades in over top of it, and scales down from 1.5 or so to 1. The fade ends about halfway through the lerp.
		- When the mark lands, it plays a 'thumb' sound. Possibly the panel shakes a tad very quickly once.

		- The mark shouldn't necessarily be rotated or positioned perfectly. Sticker jitter?

		- Text should slide and fade out of hte right side, saying "<bingo goal> NL COMPLETE!"
		- Maybe some sparkles like the rare start up after the sticker has landed

		- Notification can just fade out entirely.


		Revision: Maybe instead of slide-in/out we use the flash() style animation? It's much more shocking and busier, but the slide-in-out feels kind of mobile/application and this might be more visually consistent. On the other hand, the Confirm banner buttons do slide in-out, so maybe it's fine?
	*/


	public BingoPanelCompleteNotification(int mark, RemotePlayer playerInfo) {
		allBingo = Stream.of(EasyBingo, MedBingo, HardBingo).flatMap(Stream::of).collect(Collectors.toList());
		this.playerMarked = playerInfo;

		this.duration = 5F;
		this.startingDuration = 5F;
		// this.playerDied = playerDied;
		this.color = Color.WHITE.cpy();

		this.offsetX = screenPosX(48f);
		this.offsetY = Settings.HEIGHT-screenPosY(235f);

		this.textColour = Settings.CREAM_COLOR.cpy();
		this.markColour = Color.WHITE.cpy();
		this.markColour.a = 0f;

		this.mark = mark;
	
		X = screenPos(-160f-48f);
		Y = DRAW_Y;

		// CardCrawlGame.sound.playV("MAW_DEATH", 0.5F);
	}
	
	protected float screenPos(float val)  { return val * Settings.scale;  }
	protected float screenPosX(float val) { return val * Settings.xScale; }
	protected float screenPosY(float val) { return val * Settings.yScale; }


	public float slideInDuration = 1f;
	public float stampDuration = 0.25f;
	public float bounceDuration = 0.125f;
	public boolean bounceIn = true;
	public float shineDuration = 2.5f;
	public float shineTimer = 0.1f;
	public Color markColour;
	public boolean sound = false;
	public float scaleModB = 1.0f;

	public void update() {

	    this.duration -= Gdx.graphics.getDeltaTime();

		// Panel Slide in
		if (slideInDuration > 0f) {
			slideInDuration -= Gdx.graphics.getDeltaTime();
			this.X = Interpolation.sineIn.apply(0f, -160f-48f, slideInDuration);
		}

		// Stamp
		if (slideInDuration <= 0f && stampDuration > 0f) {
			stampDuration -= Gdx.graphics.getDeltaTime();
			this.scaleMod = Interpolation.sineOut.apply(1f, 1.5f, stampDuration*4);
			this.markColour.a = Interpolation.sineIn.apply(1f, 0f, stampDuration*4);
		}

		// Stamp Impact Sound
		if (stampDuration <= 0.125f && !sound) {
			CardCrawlGame.sound.playV("BLOCK_ATTACK", 0.25F); // Alt - BLUNT_HEAVY BLOCK_ATTACK RELIC_DROP_FLAT
			CardCrawlGame.sound.playV("EVENT_SHINING", 0.25F); // Alt - EVENT_SHINING
			sound = true;
		}

		// Stamp Impact Bounce
		if (stampDuration <= 0f && bounceDuration > 0f && bounceIn) {
			bounceDuration -= Gdx.graphics.getDeltaTime();
			this.scaleMod = Interpolation.swingOut.apply(.9f, 1f, bounceDuration*8);
			this.scaleModB = Interpolation.swingOut.apply(.9f, 1f, bounceDuration*8);
			if (bounceDuration > 0f) {
				bounceDuration = 0.125f;
				bounceIn = false;
			}
		}

		if (stampDuration <= 0f && bounceDuration > 0f && !bounceIn) {
			bounceDuration -= Gdx.graphics.getDeltaTime();
			this.scaleMod = Interpolation.swingIn.apply(1f, 0.9f, bounceDuration*8);
			this.scaleModB = Interpolation.swingIn.apply(1f, 0.9f, bounceDuration*8);
		}

		// Shiny post-stamp
		if (stampDuration <= 0f && shineDuration > 0f) {
			shineDuration -= Gdx.graphics.getDeltaTime();
			this.shineTimer -= Gdx.graphics.getDeltaTime();
			if (this.shineTimer < 0.0F && !Settings.DISABLE_EFFECTS) {
				this.shineTimer = 0.05F;
				// Alt Effect (purple and large) - Boss Chest Shine Effect
				AbstractDungeon.topLevelEffectsQueue.add(new ShineSparkleEffect( 
					MathUtils.random(offsetX-screenPos(0f), offsetX+screenPosY(160f)), 
					MathUtils.random(offsetY-screenPos(0f), offsetY+screenPosY(160f))));
			} 
		}

	    // Fade Out
	    if (this.duration < 1.0F)
	      this.color.a = this.duration; 
	    if (this.duration < 0.0F) {
	      this.isDone = true;
	      this.color.a = 0.0F;
	    } 
	}
	
	public void render(SpriteBatch sb) {
		if (!this.isDone) {
			sb.setColor(this.color);

			offsetX = 48f + this.X;

			// Panel BG and Text
			// sb.draw(TogetherManager.bingoCompletePanel, offsetX, offsetY, 160f, 160f);
			sb.draw(TogetherManager.bingoCompletePanel, offsetX, offsetY, 
				0, 0, 160f, 160f, Settings.scale * scaleModB, Settings.scale * scaleModB, 0.0F, 0, 0, 160, 160, false, false);
			FontHelper.renderWrappedText(sb, FontHelper.cardTypeFont, allBingo.get(mark), offsetX + screenPos(160f/2f), offsetY + screenPos(160f/2f), 100*Settings.scale, color, 1f*this.scaleModB);

			// Draw Mark 
			if (this.duration > 1.0F)
				sb.setColor(this.markColour);

			Texture customMark = TogetherManager.bingoMark;
			if (playerMarked.bingoMark != null)
				customMark = playerMarked.bingoMark;

			sb.draw(customMark, offsetX + screenPos(40f/2f), offsetY + screenPos(40f/2f), 
				0, 0, 120f, 120f, Settings.scale * scaleMod, Settings.scale * scaleMod, 0.0F, 0, 0, 120, 120, false, false);
			sb.setColor(this.color);

			// Render Portrait
			if (playerMarked.getPortrait() != null) {
				//sb.draw(playerDied.getPortrait(), DRAW_X, DRAW_Y + this.offsetY + 65.0F * Settings.scale * scaleMod, 56f * Settings.scale * scaleMod, 56f * Settings.scale * scaleMod);
				sb.draw(playerMarked.getPortrait(), offsetX + screenPos(163f) + playerMarked.getPortrait().getWidth() / 8f, Y + screenPos(288f) - playerMarked.getPortrait().getHeight() / 8f, 
					0, 0, playerMarked.getPortrait().getWidth(), playerMarked.getPortrait().getHeight(), Settings.scale / 3.3f, Settings.scale / 3.3f, 0.0F, 0, 0, playerMarked.getPortrait().getWidth(), playerMarked.getPortrait().getHeight(), false, false);

				// Render Portrait frame
			    sb.draw(TogetherManager.portraitFrames.get(0), offsetX + screenPos(-24f) + playerMarked.getPortrait().getWidth() / 8f, Y + screenPos(180f) - playerMarked.getPortrait().getHeight() / 8f, 0.0F, 0.0F, 432.0F, 243.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 1920, 1080, false, false);
			
				// Draw the user name
				FontHelper.cardTitleFont.getData().setScale(1f);
				FontHelper.renderFontLeft(sb, FontHelper.cardTypeFont, "Completed by", offsetX + screenPos(240f) + playerMarked.getPortrait().getWidth() / 8f, Y + screenPos(328f) - playerMarked.getPortrait().getHeight() / 8f, this.color); 
				FontHelper.renderFontLeft(sb, FontHelper.cardTitleFont, playerMarked.userName, offsetX + screenPos(240f) + playerMarked.getPortrait().getWidth() / 8f, Y + screenPos(305f) - playerMarked.getPortrait().getHeight() / 8f, this.color); 
				FontHelper.renderFontLeft(sb, FontHelper.cardTypeFont, allBingo.get(mark), offsetX + screenPos(160f) + playerMarked.getPortrait().getWidth() / 8f, Y + screenPos(265f) - playerMarked.getPortrait().getHeight() / 8f, this.color); 
			}

			sb.setColor(Color.WHITE);
		}
	}
	
	public void dispose() {}
}
