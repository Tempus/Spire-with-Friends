package chronoMods.ui.hud;

import com.evacipated.cardcrawl.modthespire.lib.*;

import com.megacrit.cardcrawl.ui.panels.TopPanel;
import com.megacrit.cardcrawl.core.Settings;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.*;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.rooms.*;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.shop.*;
import com.megacrit.cardcrawl.screens.*;
import com.megacrit.cardcrawl.screens.select.*;
import com.megacrit.cardcrawl.map.*;

import java.util.*;
import java.nio.*;
import java.util.concurrent.*;


import chronoMods.*;
import chronoMods.network.steam.*;
import chronoMods.network.*;
import chronoMods.ui.deathScreen.*;
import chronoMods.ui.hud.*;
import chronoMods.ui.lobby.*;
import chronoMods.ui.mainMenu.*;
import chronoMods.utilities.*;

public class TopPanelPlayerPanels {

    public static CopyOnWriteArrayList<RemotePlayerWidget> playerWidgets = new CopyOnWriteArrayList();

    public TopPanelPlayerPanels() {}

    // This function resorts the widgets, changing their on-screen positions.
    public static void SortWidgets() {
        TogetherManager.log("Sorting Widgets...");

        // Sorting Widgets
        Collections.sort(TopPanelPlayerPanels.playerWidgets);

        // Setting the new ranks, but sort descending
        int i = TopPanelPlayerPanels.playerWidgets.size()-1;
        for (RemotePlayerWidget widget : TopPanelPlayerPanels.playerWidgets) {
            widget.setRank(i);
            i--;
        }
    }

    @SpirePatch(clz = AbstractDungeon.class, method="update")
    public static class updatePlayerPanels {
        public static void Postfix(AbstractDungeon __instance) {
            for (RemotePlayerWidget widget : TopPanelPlayerPanels.playerWidgets) {
                widget.update();
            }
        }
    }

    @SpirePatch(clz = AbstractRoom.class, method="render")
    public static class renderPlayerPanels {
        public static void Prefix(AbstractRoom __instance, SpriteBatch sb) {
            TopPanelPlayerPanels.renderWidgets(sb);
        }
    }

    @SpirePatch(clz = DungeonMapScreen.class, method="render")
    public static class renderPlayerPanelsOnMap {
        public static void Postfix(DungeonMapScreen __instance, SpriteBatch sb) {
            TopPanelPlayerPanels.renderWidgets(sb);
        }
    }

    @SpirePatch(clz = CardRewardScreen.class, method="render")
    public static class renderPlayerPanelsOnCardRewardScreen {
        public static void Postfix(CardRewardScreen __instance, SpriteBatch sb) {
            TopPanelPlayerPanels.renderWidgets(sb);
        }
    }

    @SpirePatch(clz = GridCardSelectScreen.class, method="render")
    public static class renderPlayerPanelsOnGridCardSelectScreen {
        public static void Postfix(GridCardSelectScreen __instance, SpriteBatch sb) {
            TopPanelPlayerPanels.renderWidgets(sb);
        }
    }

    @SpirePatch(clz = CombatRewardScreen.class, method="render")
    public static class renderPlayerPanelsOnCombatRewardScreen {
        public static void Postfix(CombatRewardScreen __instance, SpriteBatch sb) {
            TopPanelPlayerPanels.renderWidgets(sb);
        }
    }

    @SpirePatch(clz = ShopScreen.class, method="render")
    public static class renderPlayerPanelsOnShopScreen {
        public static void Postfix(ShopScreen __instance, SpriteBatch sb) {
            TopPanelPlayerPanels.renderWidgets(sb);
        }
    }

    @SpirePatch(clz = GameOverScreen.class, method="renderStatsScreen")
    public static class renderPlayerPanelsOnVictoryScreen {
        public static void Prefix(GameOverScreen __instance, SpriteBatch sb) {
            TopPanelPlayerPanels.renderWidgets(sb);
        }
    }


    public static void renderWidgets(SpriteBatch sb) {
        for (RemotePlayerWidget widget : TopPanelPlayerPanels.playerWidgets)
            widget.render(sb);

        if (TogetherManager.gameMode == TogetherManager.mode.Versus && TogetherManager.players.size() > 6)
            FontHelper.renderSmartText(sb, FontHelper.cardDescFont_N, RichPresencePatch.ordinal(TogetherManager.getCurrentUser().ranking+1) + " of " + TogetherManager.players.size(), 16.0F * Settings.scale, TopPanelPlayerPanels.playerWidgets.get(TopPanelPlayerPanels.playerWidgets.size()-1).y + 100.0F * Settings.scale, Color.WHITE);
    }
}
