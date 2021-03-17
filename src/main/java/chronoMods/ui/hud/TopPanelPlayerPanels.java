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
import com.megacrit.cardcrawl.map.*;

import java.util.*;
import java.nio.*;
import java.util.concurrent.*;


import chronoMods.*;
import chronoMods.steam.*;
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


    @SpirePatch(clz = AbstractRoom.class, method="render")
    public static class renderPlayerPanels {
        public static void Prefix(AbstractRoom __instance, SpriteBatch sb) {
            for (RemotePlayerWidget widget : TopPanelPlayerPanels.playerWidgets) {
                widget.render(sb);
            }

            if (TogetherManager.gameMode == TogetherManager.mode.Versus && TogetherManager.players.size() > 6)
                FontHelper.renderSmartText(sb, FontHelper.cardDescFont_N, RichPresencePatch.ordinal(TogetherManager.getCurrentUser().ranking+1) + " of " + TogetherManager.players.size(), 16.0F * Settings.scale, TopPanelPlayerPanels.playerWidgets.get(TopPanelPlayerPanels.playerWidgets.size()-1).y + 100.0F * Settings.scale, Color.WHITE);
        }
    }

    @SpirePatch(clz = DungeonMap.class, method="render")
    public static class renderPlayerPanelsOnMap {
        public static void Postfix(DungeonMap __instance, SpriteBatch sb) {
            // Why the fuck is this the correct check to see if the map is up
            if (__instance.targetAlpha > 0.9f) {
                for (RemotePlayerWidget widget : TopPanelPlayerPanels.playerWidgets) {
                    widget.render(sb);
                }

                if (TogetherManager.gameMode == TogetherManager.mode.Versus && TogetherManager.players.size() > 6)
                    FontHelper.renderSmartText(sb, FontHelper.cardDescFont_N, RichPresencePatch.ordinal(TogetherManager.getCurrentUser().ranking+1) + " of " + TogetherManager.players.size(), 16.0F * Settings.scale, TopPanelPlayerPanels.playerWidgets.get(TopPanelPlayerPanels.playerWidgets.size()-1).y + 100.0F * Settings.scale, Color.WHITE);
            }
        }
    }
}
