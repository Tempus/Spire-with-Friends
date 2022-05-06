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
import com.megacrit.cardcrawl.screens.mainMenu.ScrollBar;
import com.megacrit.cardcrawl.screens.mainMenu.ScrollBarListener;

import basemod.*;
import com.codedisaster.steamworks.*;

import com.evacipated.cardcrawl.modthespire.lib.*;

import chronoMods.*;
import chronoMods.coop.*;
import chronoMods.network.steam.*;
import chronoMods.network.*;
import chronoMods.ui.deathScreen.*;
import chronoMods.ui.hud.*;
import chronoMods.ui.lobby.*;
import chronoMods.ui.mainMenu.*;
import chronoMods.utilities.*;

public class EndScreenBase implements ScrollBarListener {
	public static final Logger logger = LogManager.getLogger(EndScreenBase.class.getName());
	public static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString("DeathScreen");
	public static final String[] TEXT = uiStrings.TEXT;
	public static final String[] msg = CardCrawlGame.languagePack.getUIString("RaceEnd").TEXT;

	public MonsterGroup monsters;
	public String deathText;
	public ArrayList<DeathScreenFloatyEffect> particles = new ArrayList<>();
	public static final float NUM_PARTICLES = 50;
	public float deathAnimWaitTimer = 1f;
	public static final float DEATH_TEXT_TIME = 5f;
	public float deathTextTimer = DEATH_TEXT_TIME;
	public Color defeatTextColor = Color.WHITE.cpy();
	public Color deathTextColor = Settings.BLUE_TEXT_COLOR.cpy();
	public static final float DEATH_TEXT_Y = Settings.HEIGHT - 360f * Settings.scale;
	public EndScreenButton returnButton;
	public EndScreenButton retryButton;

	// Stats
	public static final float STAT_OFFSET_Y = 80f * Settings.scale;
	public static final float STAT_START_Y = Settings.HEIGHT / 2f - 20f * Settings.scale;
	public static final float STATS_TRANSITION_TIME = 0.5f;
	public static final float STAT_ANIM_INTERVAL = 0.1f;
	public float statsTimer = 0f, statAnimateTimer = 0f;

	// Others
	public boolean isVictory;
	public boolean showingStats = false;
	public boolean playedWhir = false;
	public long whirId;
	public static float playtime = 0F;

    // Scrolling
    private ScrollBar scrollBar = null;
    private boolean grabbedScreen = false;
    private float grabStartY = 0.0F;
    private float scrollTargetY = 0.0F;
    private float scrollY = 0.0F;
    private float scrollLowerBound = -Settings.DEFAULT_SCROLL_LIMIT;
    private float scrollUpperBound = Settings.DEFAULT_SCROLL_LIMIT;


    @SpirePatch(clz=AbstractDungeon.class, method="openPreviousScreen")
    public static class Reopen
    {
        public static void Postfix(AbstractDungeon.CurrentScreen s)
        {
            if (s == NewDeathScreenPatches.Enum.RACEEND) {
                NewDeathScreenPatches.EndScreenBase.reopen();
            }
        }
    }

	public EndScreenBase(MonsterGroup m) {
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
		AbstractDungeon.previousScreen = AbstractDungeon.CurrentScreen.NONE;
		AbstractDungeon.overlayMenu.cancelButton.hideInstantly();
		AbstractDungeon.isScreenUp = true;
		monsters = m;

		if (SaveHelper.shouldDeleteSave()) {
			SaveAndContinue.deleteSave(AbstractDungeon.player);
		}

		CardCrawlGame.playerPref.flush();

		// Victory or Retry
		isVictory = (AbstractDungeon.getCurrRoom() instanceof VictoryRoom && !Settings.isFinalActAvailable) || AbstractDungeon.getCurrRoom() instanceof TrueVictoryRoom;

		returnButton = new EndScreenButton();
		retryButton = new EndScreenButton();

		returnButton.hide();
		retryButton.hide();

		// Kill the music
		CardCrawlGame.music.dispose();

		if (AbstractDungeon.getCurrRoom() instanceof RestRoom)
			((RestRoom) AbstractDungeon.getCurrRoom()).cutFireSound();

		defeatTextColor.a = 0f;
		deathTextColor.a = 0f;

        // Setup scrollbar
        if (this.scrollBar == null && TogetherManager.gameMode != TogetherManager.mode.Coop) {
            calculateScrollBounds();
            this.scrollBar = new ScrollBar(this, 1200f * Settings.scale, 475f * Settings.scale, 400.0F * Settings.scale);
        }        
	}

	public void hide() {
		returnButton.hide();
		retryButton.hide();
		AbstractDungeon.dynamicBanner.hide();
	}

	public void reopen() {
		reopen(false);
	}

	public void reopen(boolean fromVictoryUnlock) {
		AbstractDungeon.previousScreen = NewDeathScreenPatches.Enum.RACEEND;
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

		if (returnButton.hb.clicked) {
			// CInputActionSet.topPanel.unpress();
			// if (Settings.isControllerMode) {
			// 	Gdx.input.setCursorPosition(10, Settings.HEIGHT / 2);
			// }
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

        // Scrollbar
        if (TogetherManager.gameMode != TogetherManager.mode.Coop) {
            boolean isDraggingScrollBar = this.scrollBar.update();
            if (!isDraggingScrollBar)
              updateScrolling(); 
        }

        // int i = 0;
        // for (PlayerListWidgetItem p : players) {
        //     p.update(i);
        //     if (TogetherManager.gameMode != TogetherManager.mode.Coop)
        //         // p.scroll(this.scrollY);
        //     i++;
        // }
	}

    //  Begin scroll functions
    private void updateScrolling() {
        int y = InputHelper.mY;
        if (!this.grabbedScreen) {
        if (InputHelper.scrolledDown) {
            this.scrollTargetY += Settings.SCROLL_SPEED;
        } else if (InputHelper.scrolledUp) {
            this.scrollTargetY -= Settings.SCROLL_SPEED;
        } 
        if (InputHelper.justClickedLeft) {
            this.grabbedScreen = true;
            this.grabStartY = y - this.scrollTargetY;
        } 
        } else if (InputHelper.isMouseDown) {
            this.scrollTargetY = y - this.grabStartY;
        } else {
            this.grabbedScreen = false;
        } 
        this.scrollY = MathHelper.scrollSnapLerpSpeed(this.scrollY, this.scrollTargetY);
        resetScrolling();
        updateBarPosition();
    }

    public void scrolledUsingBar(float newPercent) {
        this.scrollY = MathHelper.valueFromPercentBetween(this.scrollLowerBound, this.scrollUpperBound, newPercent);
        this.scrollTargetY = this.scrollY;
        updateBarPosition();
    }

    private void updateBarPosition() {
        float percent = MathHelper.percentFromValueBetween(this.scrollLowerBound, this.scrollUpperBound, this.scrollY);
        this.scrollBar.parentScrolledToPercent(percent);
    }

    private void calculateScrollBounds() {
        if (TopPanelPlayerPanels.playerWidgets.size() > 6)
            this.scrollUpperBound = (75f * (TopPanelPlayerPanels.playerWidgets.size()-6)) * Settings.scale;
        else
            this.scrollUpperBound = 1F * Settings.scale;
        this.scrollLowerBound = 0F * Settings.scale;
    }

    private void resetScrolling() {
        if (this.scrollTargetY < this.scrollLowerBound) {
          this.scrollTargetY = MathHelper.scrollSnapLerpSpeed(this.scrollTargetY, this.scrollLowerBound);
        } else if (this.scrollTargetY > this.scrollUpperBound) {
          this.scrollTargetY = MathHelper.scrollSnapLerpSpeed(this.scrollTargetY, this.scrollUpperBound);
        }
    }
    //  End scroll functions


    public void restartRun() {}

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

		if (returnButton.show)
			returnButton.render(sb);
		if (retryButton.show)
			retryButton.render(sb);

		renderPlayerList(sb);
	}

	private void renderPlayerList(SpriteBatch sb) {
		sb.setColor(new Color(1f, 1f, 1f, 1f));

        if (TogetherManager.gameMode != TogetherManager.mode.Coop)
            this.scrollBar.render(sb);

        if (CoopCutscene.shouldRenderPlayers) {
	        for (RemotePlayerWidget widget : TopPanelPlayerPanels.playerWidgets) {
	        	widget.xoffset = 780f * Settings.scale;
	        	widget.yoffset = -(150f * Settings.scale + scrollY);
	            widget.render(sb);
	        }
    	}
    	
    	CoopCutscene.shouldRenderPlayers = true;
	}
}
