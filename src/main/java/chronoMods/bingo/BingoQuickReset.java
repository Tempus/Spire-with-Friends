package chronoMods.bingo;

import basemod.TopPanelItem;
import chronoMods.TogetherManager;
import chronoMods.ui.deathScreen.EndScreenBase;
import chronoMods.ui.hud.RemotePlayerWidget;
import chronoMods.ui.hud.TopPanelPlayerPanels;
import chronoMods.ui.hud.VersusTimer;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardHelper;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.SeedHelper;
import com.megacrit.cardcrawl.helpers.TipTracker;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.screens.DungeonTransitionScreen;
import com.megacrit.cardcrawl.shop.ShopScreen;

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
