package chronoMods.ui.hud;

import chronoMods.TogetherManager;
import chronoMods.utilities.RichPresencePatch;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.screens.CardRewardScreen;
import com.megacrit.cardcrawl.screens.CombatRewardScreen;
import com.megacrit.cardcrawl.screens.DungeonMapScreen;
import com.megacrit.cardcrawl.screens.GameOverScreen;
import com.megacrit.cardcrawl.screens.select.GridCardSelectScreen;
import com.megacrit.cardcrawl.shop.ShopScreen;

import java.util.Collections;
import java.util.concurrent.CopyOnWriteArrayList;

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
