package chronoMods.ui.hud;

import chronoMods.TogetherManager;
import chronoMods.network.RemotePlayer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;

public class Split {

	public float playtime = 0.0f;
	public String key;
	public int act;
	public Texture boss;
	public Texture bossOutline;
	public boolean activeSplit = false;

	public static float iconSize = 72.0F * Settings.yScale;
	public static float bodyOffset = 235.0F;
	public static float leftTextOffset = 14.0F;
	public static float rightTextOffset = -142.0F;
	public static float fontOffset = 44.0F;
	public static float subfontOffset = 24.0F;
	public static float subfontScale = 0.5F;

    public String[] msg = CardCrawlGame.languagePack.getUIString("Splits").TEXT;

	public Split(String key, int act) {
		this.key = key;
		this.act = act;
	}

	public void activate(String bossKey) {
		this.activeSplit = true;

		if (bossKey != null)
			this.LoadImage(bossKey);
	}

	public void finish(float endTime) {
		this.activeSplit = false;
		playtime = endTime;
	}

	public void render(SpriteBatch sb, int i, Color c) {
		// Set up the variables
		String splitTime = "--:--:---";
		String variance = "--:--:---";
		Color varColor = new Color(Settings.CREAM_COLOR.r, Settings.CREAM_COLOR.g, Settings.CREAM_COLOR.b, c.a);

		if (activeSplit) {
			splitTime = VersusTimer.returnTimeString(VersusTimer.timer);;
		} else {
			splitTime = VersusTimer.returnTimeString(playtime);

			float varTime = getVarianceTime();

			// Check how we're doing
			if (varTime == playtime) {
				// We're the only one done
				variance = "--:--:---";
			} else if (varTime < playtime) {
				// Someone else is ahead of us
				variance = "+" + VersusTimer.returnTimeString(playtime - varTime);
				varColor = new Color(Settings.RED_TEXT_COLOR.r, Settings.RED_TEXT_COLOR.g, Settings.RED_TEXT_COLOR.b, c.a);
			} else if (varTime > playtime) {
				// We're the fastest done so far
				variance = "-" + VersusTimer.returnTimeString(varTime - playtime);
				varColor = new Color(Settings.GREEN_TEXT_COLOR.r, Settings.GREEN_TEXT_COLOR.g, Settings.GREEN_TEXT_COLOR.b, c.a);
			} 
		}

		if (playtime == 0f)
			variance = "--:--:---";

		if (boss != null) {
			// Render the icon
			sb.setColor(new Color(0.0F, 0.0F, 0.0F, c.a));
			sb.draw(bossOutline, SplitTracker.X * Settings.scale - 148.0F, SplitTracker.Y * Settings.scale - (iconSize*i) + bodyOffset * Settings.scale - 6.0F, 160.0F, 52.0F, iconSize+12.0F, iconSize+12.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 512, 512, false, false);

			sb.setColor(c);
			sb.draw(boss, SplitTracker.X * Settings.scale - 148.0F, SplitTracker.Y * Settings.scale - (iconSize*i) + bodyOffset * Settings.scale - 6.0F, 160.0F, 52.0F, iconSize+12.0F, iconSize+12.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 512, 512, false, false);
		} else {
			FontHelper.renderFont(sb, FontHelper.panelNameFont, msg[act], 
									SplitTracker.X * Settings.scale - 148.0F * Settings.xScale, 
									SplitTracker.Y * Settings.scale - (iconSize*i) + bodyOffset * Settings.scale + fontOffset * Settings.scale, 
									c);
		}

		// Render the split
		FontHelper.renderFont(sb, FontHelper.panelNameFont, splitTime, 
								SplitTracker.X * Settings.scale - leftTextOffset, 
								SplitTracker.Y * Settings.scale - (iconSize*i) + bodyOffset * Settings.scale + fontOffset * Settings.scale + 12.0f, 
								c);

		// Render the offset
		BitmapFont.BitmapFontData data = FontHelper.panelNameFont.getData();
		float prevScale = data.scaleX;
		data.setScale(subfontScale);
		FontHelper.renderFontRightAligned(sb, FontHelper.panelNameFont, variance, 
								SplitTracker.X * Settings.scale - rightTextOffset - 2.0F, 
								SplitTracker.Y * Settings.scale - (iconSize*i) + bodyOffset * Settings.scale + fontOffset * Settings.scale - subfontOffset, 
								new Color(Settings.GREEN_TEXT_COLOR.r, Settings.GREEN_TEXT_COLOR.g, Settings.GREEN_TEXT_COLOR.b, c.a));
		data.setScale(prevScale);
	}

	public float getVarianceTime() {
		float shortest = 99999999999.9F;
		for (RemotePlayer playerInfo : TogetherManager.players) {
			if (playerInfo.splits.get(this.key).playtime != 0 && playerInfo.splits.get(this.key).playtime < shortest) {
				shortest = playerInfo.splits.get(this.key).playtime;
			}
		}
		return shortest;
	}

	public void LoadImage(String key) {
		if (key.equals("The Guardian")) {
			this.boss = ImageMaster.loadImage("images/ui/map/boss/guardian.png");
			this.bossOutline = ImageMaster.loadImage("images/ui/map/bossOutline/guardian.png");
		} else if (key.equals("Hexaghost")) {
			this.boss = ImageMaster.loadImage("images/ui/map/boss/hexaghost.png");
			this.bossOutline = ImageMaster.loadImage("images/ui/map/bossOutline/hexaghost.png");
		} else if (key.equals("Slime Boss")) {
			this.boss = ImageMaster.loadImage("images/ui/map/boss/slime.png");
			this.bossOutline = ImageMaster.loadImage("images/ui/map/bossOutline/slime.png");
		} else if (key.equals("Collector")) {
			this.boss = ImageMaster.loadImage("images/ui/map/boss/collector.png");
			this.bossOutline = ImageMaster.loadImage("images/ui/map/bossOutline/collector.png");
		} else if (key.equals("Automaton")) {
			this.boss = ImageMaster.loadImage("images/ui/map/boss/automaton.png");
			this.bossOutline = ImageMaster.loadImage("images/ui/map/bossOutline/automaton.png");
		} else if (key.equals("Champ")) {
			this.boss = ImageMaster.loadImage("images/ui/map/boss/champ.png");
			this.bossOutline = ImageMaster.loadImage("images/ui/map/bossOutline/champ.png");
		} else if (key.equals("Awakened One")) {
			this.boss = ImageMaster.loadImage("images/ui/map/boss/awakened.png");
			this.bossOutline = ImageMaster.loadImage("images/ui/map/bossOutline/awakened.png");
		} else if (key.equals("Time Eater")) {
			this.boss = ImageMaster.loadImage("images/ui/map/boss/timeeater.png");
			this.bossOutline = ImageMaster.loadImage("images/ui/map/bossOutline/timeeater.png");
		} else if (key.equals("Donu and Deca")) {
			this.boss = ImageMaster.loadImage("images/ui/map/boss/donu.png");
			this.bossOutline = ImageMaster.loadImage("images/ui/map/bossOutline/donu.png");
		} else if (key.equals("The Heart")) {
			this.boss = ImageMaster.loadImage("images/ui/map/boss/heart.png");
			this.bossOutline = ImageMaster.loadImage("images/ui/map/bossOutline/heart.png");
		}
	}
}