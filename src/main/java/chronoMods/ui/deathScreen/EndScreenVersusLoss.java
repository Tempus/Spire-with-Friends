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

public class EndScreenVersusLoss extends EndScreenBase {
	public EndScreenVersusLoss(MonsterGroup m) {
		super(m);

		if (!NewDeathScreenPatches.Ironman) {
			returnButton.appear(Settings.WIDTH / 2f + (160f * Settings.scale), Settings.HEIGHT * 0.15f, msg[0], true);
			retryButton.appear(Settings.WIDTH / 2f - (160f * Settings.scale), Settings.HEIGHT * 0.15f, TEXT[33], false);
		} else {
			returnButton.appear(Settings.WIDTH / 2f, Settings.HEIGHT * 0.15f, msg[0], true);
		}

		AbstractDungeon.dynamicBanner.appear(msg[2]);

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

	public void reopen() {
		super.reopen();

		AbstractDungeon.dynamicBanner.appearInstantly(TEXT[30]);

		if (!NewDeathScreenPatches.Ironman) {
			returnButton.appear(Settings.WIDTH / 2f + (160f * Settings.scale), Settings.HEIGHT * 0.15f, msg[0], true);
			retryButton.appear(Settings.WIDTH / 2f - (160f * Settings.scale), Settings.HEIGHT * 0.15f, TEXT[33], false);
		} else {
			returnButton.appear(Settings.WIDTH / 2f, Settings.HEIGHT * 0.15f, msg[0], true);
		}
	}

    public void restartRun()
    {
    	EndScreenBase.playtime = VersusTimer.timer;
        CardCrawlGame.music.fadeAll();
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

        if (CardCrawlGame.chosenCharacter == null) {
          CardCrawlGame.chosenCharacter = AbstractDungeon.player.chosenClass;
        }

        AbstractDungeon.generateSeeds();
        
        CardCrawlGame.mode = CardCrawlGame.GameMode.CHAR_SELECT;

        for (RemotePlayerWidget widget : TopPanelPlayerPanels.playerWidgets) {
            widget.xoffset = 0f;
            widget.yoffset = 0f;
        }
    }

    public void render(SpriteBatch sb) {
    	super.render(sb);

		if (!NewDeathScreenPatches.Ironman) {
			renderRetryBonuses(sb);

			FontHelper.renderFontCentered(
				sb,
				FontHelper.topPanelInfoFont,
				msg[3],
				Settings.WIDTH / 2f,
				DEATH_TEXT_Y,
				deathTextColor);
		}
    }

	private void renderRetryBonuses(SpriteBatch sb) {
		sb.setColor(new Color(1f, 1f, 1f, 1f));

		String msg = this.msg[4];

        int floor = TogetherManager.getCurrentUser().highestFloor;


    	msg += (10 * floor) + this.msg[5] + " NL ";

    	// Then a better potion for each midway chest cleared
    	if (floor > 41) {
	    	msg += PotionHelper.getPotion("EntropicBrew").name + " NL ";
    		msg += RelicLibrary.getRelic("Potion Belt") + " NL ";
    	}
    	else if (floor > 24) {
	    	msg += this.msg[6] + PotionHelper.getPotion("DuplicationPotion").name + " NL ";
    	}
    	else if (floor > 7) {
	    	msg += this.msg[6] + PotionHelper.getPotion("Fire Potion").name + " NL ";
    	}


    	// Then special bonuses for each Act Boss cleared
    	// Cleared Act 3, Get 2 Astrolabes and Flight
    	if (floor > 50) {
	    	msg += this.msg[6] + RelicLibrary.getRelic("Astrolabe").name + " NL ";
	    	msg += "Flight NL ";
    	}

    	// Cleared Act 2, Upgrade Starter Relic and get a Winged Boots
    	else if (floor > 33) {
	    	msg += this.msg[7] + " NL ";
	    	msg += RelicLibrary.getRelic("WingedGreaves").name + " NL ";
    	}

    	// Cleared Act 1, get a class specific stat relic
    	else if (floor > 16) {
			if (AbstractDungeon.player.getStartingRelics().get(0).equals("Burning Blood")) 
		    	msg += RelicLibrary.getRelic("Vajra").name + " NL ";
			else if (AbstractDungeon.player.getStartingRelics().get(0).equals("Ring of the Snake")) 
		    	msg += RelicLibrary.getRelic("Oddly Smooth Stone").name + " NL ";
			else if (AbstractDungeon.player.getStartingRelics().get(0).equals("Cracked Core")) 
		    	msg += RelicLibrary.getRelic("Data Disk").name + " NL ";
			else if (AbstractDungeon.player.getStartingRelics().get(0).equals("PureWater")) 
		    	msg += RelicLibrary.getRelic("Lantern").name + " NL ";
			else
		    	msg += RelicLibrary.getRelic("Anchor").name + " NL ";
    	}

		FontHelper.renderSmartText(sb, FontHelper.topPanelInfoFont, msg, Settings.WIDTH / 12f, Settings.HEIGHT * 0.60f, Settings.CREAM_COLOR);
	}
}