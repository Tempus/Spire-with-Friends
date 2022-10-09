package chronoMods.coop;

import chronoMods.network.RemotePlayer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

public class CoopDeathNotification extends AbstractGameEffect {
	private static final float TEXT_DURATION = 1.8F;
	private static final float DRAW_X = Settings.WIDTH / 2.0F, DRAW_Y = Settings.HEIGHT * 0.6F;	
	private static final float STARTING_OFFSET_Y = 0.0F * Settings.scale;
	private static final float TARGET_OFFSET_Y = 120.0F * Settings.scale;
	private static final float LERP_RATE = 5.0F;
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString("Death").TEXT;

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
			if (playerDied.getPortrait() != null) {
				//sb.draw(playerDied.getPortrait(), DRAW_X, DRAW_Y + this.offsetY + 65.0F * Settings.scale * scaleMod, 56f * Settings.scale * scaleMod, 56f * Settings.scale * scaleMod);
				sb.draw(playerDied.getPortrait(), X - playerDied.getPortrait().getWidth() / 2f, Y - playerDied.getPortrait().getHeight() / 2f, 
					playerDied.getPortrait().getWidth() / 2f, playerDied.getPortrait().getHeight() / 2f, playerDied.getPortrait().getWidth(), playerDied.getPortrait().getHeight(), Settings.scale * scaleMod, Settings.scale * scaleMod, 0.0F, 0, 0, playerDied.getPortrait().getWidth(), playerDied.getPortrait().getHeight(), false, false);
			}

			// Render Portrait frame
		    //sb.draw(TogetherManager.portraitFrames.get(0), xn - 160.0F * Settings.scale, yn - 96.0F * Settings.scale, 0.0F, 0.0F, 432.0F, 243.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 1920, 1080, false, false);

			// Draw the user name
			FontHelper.renderFontCentered(sb, FontHelper.buttonLabelFont, playerDied.userName + TEXT[0], X, Y - 160.0F * Settings.scale + this.offsetY, this.color, scaleMod); 
			sb.setColor(Color.WHITE);
		}
	}
	
	public void dispose() {}
}
