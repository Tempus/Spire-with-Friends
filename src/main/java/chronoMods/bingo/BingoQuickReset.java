package chronoMods.bingo;

import com.evacipated.cardcrawl.modthespire.lib.*;

import basemod.*;
import basemod.abstracts.*;
import basemod.interfaces.*;

import org.apache.logging.log4j.*;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.helpers.*;
import com.megacrit.cardcrawl.helpers.input.*;
import com.megacrit.cardcrawl.rooms.*;
import com.megacrit.cardcrawl.map.*;
import com.megacrit.cardcrawl.saveAndContinue.*;
import com.megacrit.cardcrawl.rewards.*;
import com.megacrit.cardcrawl.unlock.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.screens.options.*;
import com.megacrit.cardcrawl.screens.stats.*;
import com.megacrit.cardcrawl.screens.*;
import com.megacrit.cardcrawl.shop.*;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.ui.panels.*;

import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import chronoMods.*;
import chronoMods.coop.*;
import chronoMods.network.steam.*;
import chronoMods.network.*;
import chronoMods.ui.deathScreen.*;
import chronoMods.ui.hud.*;
import chronoMods.ui.lobby.*;
import chronoMods.ui.mainMenu.*;
import chronoMods.utilities.*;

import java.util.*;
import java.lang.*;
import java.nio.*;

import com.codedisaster.steamworks.*;
import com.megacrit.cardcrawl.integrations.steam.*;

public class BingoQuickReset extends TopPanelItem {
    private static final Texture IMG = new Texture("chrono/images/bingoQuickReset.png");
    public static final String ID = "BingoQuickReset";

    public BingoQuickReset() {
	   super(IMG, ID);
    }

    @Override
    public void update() {
        if (TogetherManager.gameMode == TogetherManager.mode.Bingo)
            super.update();
    }

    public void lateUpdate() {
        if (this.hitbox.hovered && InputHelper.justClickedLeft) {
            RestartRun();
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        if (TogetherManager.gameMode == TogetherManager.mode.Bingo) {
            super.render(sb);
            if (hitbox.hovered)
                FontHelper.renderFontCentered(sb, FontHelper.topPanelInfoFont, "Reset", x + hitbox.width / 2.0F, y + hitbox.height / 2.0F);
        }
    }

    public float spinSpeed = -0.3f;

    @Override
    public void onHover()
    {
        angle += spinSpeed;
        tint.a = 0.25f;
    }

    @SpirePatch(clz = AbstractDungeon.class, method = "update")
    public static class UpdatePatch {
        public static void Postfix(AbstractDungeon __instance) {
            if (TogetherManager.gameMode == TogetherManager.mode.Bingo)
                TogetherManager.bingoQuickReset.lateUpdate();
        }
    }

    @Override
    public void onClick() {}

    public void RestartRun() {
        EndScreenBase.playtime = VersusTimer.timer;
        CardCrawlGame.music.fadeAll();
        if (Settings.AMBIANCE_ON)
            CardCrawlGame.sound.stop("WIND");

        if(AbstractDungeon.scene != null) {
            AbstractDungeon.scene.fadeOutAmbiance();
        }

        AbstractDungeon.getCurrRoom().clearEvent();
        AbstractDungeon.closeCurrentScreen(); // Might need a safety check? On the death screen?
        
        CardCrawlGame.dungeonTransitionScreen = new DungeonTransitionScreen("Exordium");
        
        AbstractDungeon.reset();
        Settings.isFinalActAvailable = true;
        Settings.isTrial = false;
        Settings.isTestingNeow = true;

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

        // Seed Generation, new map new run
        long sourceTime = System.nanoTime();
        Random rng = new Random(Long.valueOf(sourceTime));
        Settings.seedSourceTimestamp = sourceTime;
        Settings.seed = Long.valueOf(SeedHelper.generateUnoffensiveSeed(rng));
        Settings.seedSet = false;

        AbstractDungeon.generateSeeds();
        
        CardCrawlGame.mode = CardCrawlGame.GameMode.CHAR_SELECT;

        for (RemotePlayerWidget widget : TopPanelPlayerPanels.playerWidgets) {
            widget.xoffset = 0f;
            widget.yoffset = 0f;
        }
    }
}
