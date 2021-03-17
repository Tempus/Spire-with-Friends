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
import chronoMods.steam.*;
import chronoMods.ui.deathScreen.*;
import chronoMods.ui.hud.*;
import chronoMods.ui.lobby.*;
import chronoMods.ui.mainMenu.*;
import chronoMods.utilities.*;

public class RaceEndScreen {
	private static final Logger logger = LogManager.getLogger(RaceEndScreen.class.getName());
	private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString("DeathScreen");
	public static final String[] TEXT = uiStrings.TEXT;
	public static final String[] msg = CardCrawlGame.languagePack.getUIString("RaceEnd").TEXT;

	public MonsterGroup monsters;
	private String deathText;
	private ArrayList<DeathScreenFloatyEffect> particles = new ArrayList<>();
	private static final float NUM_PARTICLES = 50;
	private float deathAnimWaitTimer = 1f;
	private static final float DEATH_TEXT_TIME = 5f;
	private float deathTextTimer = DEATH_TEXT_TIME;
	private Color defeatTextColor = Color.WHITE.cpy();
	private Color deathTextColor = Settings.BLUE_TEXT_COLOR.cpy();
	private static final float DEATH_TEXT_Y = Settings.HEIGHT - 360f * Settings.scale;
	public ReturnToMenuButton returnButton;
	public RetryButton retryButton;

	// Stats
	private static final float STAT_OFFSET_Y = 80f * Settings.scale;
	private static final float STAT_START_Y = Settings.HEIGHT / 2f - 20f * Settings.scale;
	public static final float STATS_TRANSITION_TIME = 0.5f;
	public static final float STAT_ANIM_INTERVAL = 0.1f;
	private float statsTimer = 0f, statAnimateTimer = 0f;

	// Others
	public boolean isVictory;
	public boolean showingStats = false;
	private boolean playedWhir = false;
	private long whirId;
	public static float playtime = 0F;

    @SpirePatch(clz=AbstractDungeon.class, method="openPreviousScreen")
    public static class Reopen
    {
        public static void Postfix(AbstractDungeon.CurrentScreen s)
        {
            if (s == NewDeathScreenPatches.Enum.RACEEND) {
                NewDeathScreenPatches.raceEndScreen.reopen();
            }
        }
    }

	public RaceEndScreen(MonsterGroup m) {
		// Remove existing death screens
		AbstractDungeon.deathScreen = null;
		AbstractDungeon.victoryScreen = null;

		// Cleanup
		playtime = (long) VersusTimer.timer;

		if (playtime < 0L) {
			playtime = 0L;
		}

		AbstractDungeon.getCurrRoom().clearEvent();

		AbstractDungeon.is_victory = false;
		for (AbstractCard c : AbstractDungeon.player.hand.group) {
			c.unhover();
		}
		AbstractDungeon.dungeonMapScreen.closeInstantly();
		AbstractDungeon.overlayMenu.showBlackScreen(1f);
		AbstractDungeon.previousScreen = null;
		AbstractDungeon.overlayMenu.cancelButton.hideInstantly();
		AbstractDungeon.isScreenUp = true;
		monsters = m;

		if (SaveHelper.shouldDeleteSave()) {
			SaveAndContinue.deleteSave(AbstractDungeon.player);
		}

		CardCrawlGame.playerPref.flush();

		// Victory or Retry
		isVictory = AbstractDungeon.getCurrRoom() instanceof VictoryRoom || AbstractDungeon.getCurrRoom() instanceof TrueVictoryRoom;
		// if (TogetherManager.gameMode == TogetherManager.mode.Versus) {
		// 	if (!Settings.isFinalActAvailable) {
		// 	} else {
		// 		isVictory = AbstractDungeon.floorNum > 50;
		// 	}
		// }

		returnButton = new ReturnToMenuButton();
		retryButton = new RetryButton();

		if (isVictory || NewDeathScreenPatches.Ironman || TogetherManager.gameMode == TogetherManager.mode.Coop) {
			returnButton.appear(Settings.WIDTH / 2f, Settings.HEIGHT * 0.15f, msg[0]);

			AbstractDungeon.dynamicBanner.appear(msg[1]);

        	NetworkHelper.sendData(NetworkHelper.dataType.Finish);
		} else {
			returnButton.appear(Settings.WIDTH / 2f + (160f * Settings.scale), Settings.HEIGHT * 0.15f, msg[0]);
			retryButton.appear(Settings.WIDTH / 2f - (160f * Settings.scale), Settings.HEIGHT * 0.15f, TEXT[33]);

			AbstractDungeon.dynamicBanner.appear(msg[2]);
		}

		// Kill the music
		CardCrawlGame.music.dispose();

		if (AbstractDungeon.getCurrRoom() instanceof RestRoom) {
			((RestRoom) AbstractDungeon.getCurrRoom()).cutFireSound();
		}

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

		defeatTextColor.a = 0f;
		deathTextColor.a = 0f;
	}

	public void hide() {
		returnButton.hide();
		AbstractDungeon.dynamicBanner.hide();
	}

	public void reopen() {
		reopen(false);
	}

	public void reopen(boolean fromVictoryUnlock) {
		AbstractDungeon.previousScreen = NewDeathScreenPatches.Enum.RACEEND;

		if (isVictory) {
			TogetherManager.log("Victory Banner");
	
			AbstractDungeon.dynamicBanner.appearInstantly(TEXT[1]);
			returnButton.appear(Settings.WIDTH / 2f, Settings.HEIGHT * 0.15f, TEXT[34]);
		} else {
			TogetherManager.log("Failfish banner");

			AbstractDungeon.dynamicBanner.appearInstantly(TEXT[30]);
			retryButton.appear(Settings.WIDTH / 2f, Settings.HEIGHT * 0.15f, TEXT[33]);
		}
		AbstractDungeon.overlayMenu.showBlackScreen(1f);
	}

	public void update() {
		if (monsters != null) {
			monsters.update();
			monsters.updateAnimations();
		}

		if (particles.size() < NUM_PARTICLES) {
			particles.add(new DeathScreenFloatyEffect());
		}

		// Timers, particles, and animations

		if (deathAnimWaitTimer != 0f) {
			deathAnimWaitTimer -= Gdx.graphics.getDeltaTime();
			if (deathAnimWaitTimer < 0f) {
				deathAnimWaitTimer = 0f;
				AbstractDungeon.player.playDeathAnimation();
			}
		} else {
			deathTextTimer -= Gdx.graphics.getDeltaTime();
			if (deathTextTimer < 0f) {
				deathTextTimer = 0f;
			}

			deathTextColor.a = Interpolation.fade.apply(0f, 1f, 1f - deathTextTimer / DEATH_TEXT_TIME);
			defeatTextColor.a = Interpolation.fade.apply(0f, 1f, 1f - deathTextTimer / DEATH_TEXT_TIME);
		}

		// Return Button
		returnButton.update();

		if (returnButton.hb.clicked || (returnButton.show && CInputActionSet.select.isJustPressed())) {
			CInputActionSet.topPanel.unpress();
			if (Settings.isControllerMode) {
				Gdx.input.setCursorPosition(10, Settings.HEIGHT / 2);
			}
			returnButton.hb.clicked = false;

			returnButton.hide();
			retryButton.hide();
			CardCrawlGame.startOver();
		}

		// Retry Button
		retryButton.update();

		if (InputHelper.justClickedLeft && retryButton.hb.hovered || (retryButton.show && CInputActionSet.select.isJustPressed())) {
			retryButton.hb.clicked = false;

			restartRun();
		}

	}

    public static void restartRun()
    {
    	RaceEndScreen.playtime = VersusTimer.timer;
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
		for (Iterator<DeathScreenFloatyEffect> i = particles.iterator(); i.hasNext();) {
			DeathScreenFloatyEffect e = i.next();
			if (e.renderBehind) {
				e.render(sb);
			}
			e.update();
			if (e.isDone) {
				i.remove();
			}
		}

		AbstractDungeon.player.render(sb);
		if (monsters != null) {
			monsters.render(sb);
		}

		sb.setBlendFunction(GL30.GL_SRC_ALPHA, GL30.GL_ONE); // Additive Mode
		for (DeathScreenFloatyEffect e : particles) {
			if (!e.renderBehind) {
				e.render(sb);
			}
		}
		sb.setBlendFunction(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA); // NORMAL

		renderPlayerList(sb);

		if (!showingStats && !isVictory && !NewDeathScreenPatches.Ironman) {
			FontHelper.renderFontCentered(
				sb,
				FontHelper.topPanelInfoFont,
				msg[3],
				Settings.WIDTH / 2f,
				DEATH_TEXT_Y,
				deathTextColor);
		}

		if (TogetherManager.gameMode == TogetherManager.mode.Versus && !NewDeathScreenPatches.Ironman)
			renderRetryBonuses(sb);
	
		returnButton.render(sb);
	}

	private void renderPlayerList(SpriteBatch sb) {
		sb.setColor(new Color(1f, 1f, 1f, 1f));

        for (RemotePlayerWidget widget : TopPanelPlayerPanels.playerWidgets) {
        	widget.xoffset = 780f * Settings.scale;
        	widget.yoffset = -(150f * Settings.scale);
            widget.render(sb);
        }
	}

	private void renderRetryBonuses(SpriteBatch sb) {
		sb.setColor(new Color(1f, 1f, 1f, 1f));

		String msg = this.msg[5];

        int floor = TogetherManager.getCurrentUser().highestFloor;


    	msg += (10 * floor) + this.msg[6] + " NL ";

    	// Then a better potion for each midway chest cleared
    	if (floor > 41) {
	    	msg += PotionHelper.getPotion("EntropicBrew").name + " NL ";
    		msg += RelicLibrary.getRelic("Potion Belt") + " NL ";
    	}
    	else if (floor > 24) {
	    	msg += this.msg[7] + PotionHelper.getPotion("DuplicationPotion").name + " NL ";
    	}
    	else if (floor > 7) {
	    	msg += this.msg[7] + PotionHelper.getPotion("Fire Potion").name + " NL ";
    	}


    	// Then special bonuses for each Act Boss cleared
    	// Cleared Act 3, Get 2 Astrolabes and Flight
    	if (floor > 50) {
	    	msg += this.msg[7] + RelicLibrary.getRelic("Astrolabe").name + " NL ";
	    	msg += "Flight NL ";
    	}

    	// Cleared Act 2, Upgrade Starter Relic and get a Winged Boots
    	else if (floor > 33) {
	    	msg += this.msg[8] + " NL ";
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
