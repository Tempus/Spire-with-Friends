package chronoMods.coop;

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

import chronoMods.*;
import chronoMods.steam.*;
import chronoMods.coop.*;
import chronoMods.coop.relics.*;
import chronoMods.ui.deathScreen.*;
import chronoMods.ui.hud.*;
import chronoMods.ui.lobby.*;
import chronoMods.ui.mainMenu.*;
import chronoMods.utilities.*;

public class CoopDeathNotification extends AbstractGameEffect {
	private static final float TEXT_DURATION = 1.8F;
	private static final float DRAW_X = Settings.WIDTH / 2.0F, DRAW_Y = Settings.HEIGHT * 0.6F;	
	private static final float STARTING_OFFSET_Y = 0.0F * Settings.scale;
	private static final float TARGET_OFFSET_Y = 120.0F * Settings.scale;
	private static final float LERP_RATE = 5.0F;

	private float X, Y, offsetY;
	public float scaleMod = 1.0f;
	private String msg;

	public RemotePlayer playerDied;
	
	public CoopDeathNotification(RemotePlayer playerDied) {
		this.duration = 1.8F;
		this.startingDuration = 1.8F;
		this.playerDied = playerDied;
		this.color = Color.WHITE.cpy();
		this.offsetY = STARTING_OFFSET_Y;
	
		X = DRAW_X;
		Y = DRAW_Y;

		CardCrawlGame.sound.playV("MAW_DEATH", 0.5F);
	}
	
	public void update() {
		super.update();
		//this.offsetY = MathUtils.lerp(this.offsetY, TARGET_OFFSET_Y, Gdx.graphics.getDeltaTime() * 5.0F);
		//this.X = Interpolation.exp10In.apply(AbstractDungeon.player.getBlight("StringOfFate").currentX, DRAW_X, this.duration);
		//this.Y = Interpolation.exp10In.apply(AbstractDungeon.player.getBlight("StringOfFate").currentY, DRAW_Y, this.duration);

		this.offsetY = Interpolation.sineIn.apply(TARGET_OFFSET_Y, STARTING_OFFSET_Y, this.duration);
		this.scaleMod = Interpolation.sineIn.apply(0.25f, 1.0f, this.duration);
	}
	
	public void render(SpriteBatch sb) {
		if (!this.isDone) {
			sb.setColor(this.color);
			//sb.draw(ImageMaster.RELIC_POPUP, DRAW_X, DRAW_Y + this.offsetY, TogetherManager.panelImg.getWidth() * Settings.scale * scaleMod, TogetherManager.panelImg.getHeight() * Settings.scale * scaleMod);
			//sb.draw(ImageMaster.RELIC_POPUP, Settings.WIDTH / 2.0F - 960.0F, Settings.HEIGHT / 2.0F - 540.0F + this.offsetY, 960.0F, 540.0F, 1920.0F, 1080.0F, Settings.scale * scaleMod, Settings.scale * scaleMod, 0.0F, 0, 0, 1920, 1080, false, false);
			// Draw the player colour indicator
			// sb.setColor(player.colour);
			// sb.draw(TogetherManager.colourIndicatorImg, xn, yn, TogetherManager.colourIndicatorImg.getWidth() * Settings.scale, TogetherManager.colourIndicatorImg.getHeight() * Settings.scale);
			// sb.setColor(Color.WHITE);

			// Render Portrait
			if (playerDied.portraitImg != null) {
				//sb.draw(playerDied.portraitImg, DRAW_X, DRAW_Y + this.offsetY + 65.0F * Settings.scale * scaleMod, 56f * Settings.scale * scaleMod, 56f * Settings.scale * scaleMod);
				sb.draw(playerDied.portraitImg, X - playerDied.portraitImg.getWidth() / 2f, Y - playerDied.portraitImg.getHeight() / 2f, 
					playerDied.portraitImg.getWidth() / 2f, playerDied.portraitImg.getHeight() / 2f, playerDied.portraitImg.getWidth(), playerDied.portraitImg.getHeight(), Settings.scale * scaleMod, Settings.scale * scaleMod, 0.0F, 0, 0, playerDied.portraitImg.getWidth(), playerDied.portraitImg.getHeight(), false, false);
			}

			// Render Portrait frame
		    //sb.draw(TogetherManager.portraitFrames.get(0), xn - 160.0F * Settings.scale, yn - 96.0F * Settings.scale, 0.0F, 0.0F, 432.0F, 243.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 1920, 1080, false, false);

			// Draw the user name
			FontHelper.renderFontCentered(sb, FontHelper.buttonLabelFont, playerDied.userName + " has died.", X, Y - 160.0F * Settings.scale + this.offsetY, this.color, scaleMod); 
			sb.setColor(Color.WHITE);
		}
	}
	
	public void dispose() {}
}
