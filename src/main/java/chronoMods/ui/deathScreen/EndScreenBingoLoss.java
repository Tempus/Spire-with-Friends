package chronoMods.ui.deathScreen;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.core.*;
import com.megacrit.cardcrawl.daily.TimeHelper;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon.*;
import com.megacrit.cardcrawl.helpers.*;
import com.megacrit.cardcrawl.helpers.controller.*;
import com.megacrit.cardcrawl.helpers.input.*;
import com.megacrit.cardcrawl.integrations.*;
import com.megacrit.cardcrawl.localization.*;
import com.megacrit.cardcrawl.metrics.*;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.relics.SpiritPoop;
import com.megacrit.cardcrawl.rooms.*;
import com.megacrit.cardcrawl.saveAndContinue.SaveAndContinue;
import com.megacrit.cardcrawl.screens.stats.*;
import com.megacrit.cardcrawl.screens.*;
import com.megacrit.cardcrawl.ui.buttons.*;
import com.megacrit.cardcrawl.unlock.*;
import com.megacrit.cardcrawl.vfx.*;
import com.megacrit.cardcrawl.audio.MusicMaster;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.*;
import com.megacrit.cardcrawl.core.CardCrawlGame.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.shop.ShopScreen;
import com.megacrit.cardcrawl.integrations.steam.SteamIntegration;

import basemod.*;
import com.codedisaster.steamworks.*;

import com.evacipated.cardcrawl.modthespire.lib.*;

import chronoMods.*;
import chronoMods.network.steam.*;
import chronoMods.network.*;
import chronoMods.ui.deathScreen.*;
import chronoMods.ui.hud.*;
import chronoMods.ui.lobby.*;
import chronoMods.ui.mainMenu.*;
import chronoMods.utilities.*;

public class EndScreenBingoLoss extends EndScreenBase {

	float retryX = 1550f* Settings.scale;
	float retryY = Settings.HEIGHT * 0.25f;

	float returnX = 160f * Settings.scale;

	public CharacterSelectWidget characterSelectWidget = new CharacterSelectWidget();
	private static final float TOGGLE_X_LEFT = 1560f * Settings.xScale;

	public EndScreenBingoLoss(MonsterGroup m) {
		super(m);

		retryButton.appear(retryX, retryY, msg[13], false);
		// returnButton.appear(Settings.WIDTH / 2f + (160f * Settings.scale), Settings.HEIGHT * 0.15f, msg[0],true);
		// retryButton.appear(Settings.WIDTH / 2f - (160f * Settings.scale), Settings.HEIGHT * 0.15f, TEXT[33], false);

		TogetherManager.log("Construct: " + AbstractDungeon.player.chosenClass);

		characterSelectWidget.move(1400f * Settings.xScale, Settings.HEIGHT * 0.5f); // .65 original
		characterSelectWidget.selectClass(AbstractDungeon.player.chosenClass);

		AbstractDungeon.dynamicBanner.appear(msg[1]);

		// Play death SFX
		CardCrawlGame.sound.play("DEATH_STINGER", true);

		// Play death BGM
		String bgmKey = null;
		switch (MathUtils.random(0, 3)) {
			case 0:
				bgmKey = "STS_DeathStinger_1_v3_MUSIC.ogg";
				break;
			case 1:
				bgmKey = "STS_DeathStinger_2_v3_MUSIC.ogg";
				break;
			case 2:
				bgmKey = "STS_DeathStinger_3_v3_MUSIC.ogg";
				break;
			case 3:
				bgmKey = "STS_DeathStinger_4_v3_MUSIC.ogg";
				break;
			default:
				break;
		}
		CardCrawlGame.music.playTempBgmInstantly(bgmKey, false);
	}

	public void update() {
		super.update();

		characterSelectWidget.update();
	}

	public void reopen() {
		super.reopen();

		TogetherManager.log("Reopen: " + AbstractDungeon.player.chosenClass);
		AbstractDungeon.dynamicBanner.appearInstantly(TEXT[30]);
		characterSelectWidget.selectClass(AbstractDungeon.player.chosenClass);

		retryButton.appear(retryX, retryY, msg[13], false);
		// returnButton.appear(Settings.WIDTH / 2f + (160f * Settings.scale), Settings.HEIGHT * 0.15f, msg[0],true);
		// retryButton.appear(Settings.WIDTH / 2f - (160f * Settings.scale), Settings.HEIGHT * 0.15f, TEXT[8], false);
	}

    public void restartRun()
    {
    	// Fade Music, remove ambient noises
        CardCrawlGame.music.fadeAll();
        if (Settings.AMBIANCE_ON)
            CardCrawlGame.sound.stop("WIND");

        if(AbstractDungeon.scene != null) {
            AbstractDungeon.scene.fadeOutAmbiance();
        }


        AbstractDungeon.getCurrRoom().clearEvent();
        AbstractDungeon.closeCurrentScreen(); 
        
        CardCrawlGame.dungeonTransitionScreen = new DungeonTransitionScreen("Exordium");
        
        AbstractDungeon.reset();
        Settings.hasEmeraldKey = false;
        Settings.hasRubyKey = false;
        Settings.hasSapphireKey = false;
        ShopScreen.resetPurgeCost();
        CardCrawlGame.tips.initialize();
        CardCrawlGame.metricData.clearData();
        CardHelper.clear();
        TipTracker.refresh();
        System.gc();

		CardCrawlGame.chosenCharacter = characterSelectWidget.getChosenClass();

		long sourceTime = System.nanoTime();
		Random rng = new Random(Long.valueOf(sourceTime));
		Settings.seedSourceTimestamp = sourceTime;
		Settings.seed = Long.valueOf(SeedHelper.generateUnoffensiveSeed(rng));
		Settings.seedSet = false;

        AbstractDungeon.generateSeeds();
        
        CardCrawlGame.mode = CardCrawlGame.GameMode.CHAR_SELECT;
		Settings.isFinalActAvailable = true;
		Settings.isTrial = false;
		Settings.isTestingNeow = true;

        for (RemotePlayerWidget widget : TopPanelPlayerPanels.playerWidgets) {
            widget.xoffset = 0f;
            widget.yoffset = 0f;
        }
    }

    public void render(SpriteBatch sb) {
    	super.render(sb);

		characterSelectWidget.render(sb);

		FontHelper.renderFontCentered(sb,
			FontHelper.topPanelInfoFont, msg[9],
			TOGGLE_X_LEFT, 645f, deathTextColor);

		// FontHelper.renderFontCentered(sb,
		// 	FontHelper.topPanelInfoFont, msg[10],
		// 	Settings.WIDTH / 2f, DEATH_TEXT_Y - 18f, deathTextColor);
    }
}