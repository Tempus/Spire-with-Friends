package chronoMods.ui.hud;

import com.evacipated.cardcrawl.modthespire.lib.*;

import com.megacrit.cardcrawl.ui.panels.TopPanel;
import com.megacrit.cardcrawl.core.Settings;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.*;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.FontHelper;

import java.util.*;
import java.nio.*;

import chronoMods.*;
import chronoMods.steam.*;
import chronoMods.ui.deathScreen.*;
import chronoMods.ui.hud.*;
import chronoMods.ui.lobby.*;
import chronoMods.ui.mainMenu.*;
import chronoMods.utilities.*;

public class TopPanelPlayerPanels {

    public static ArrayList<RemotePlayerWidget> playerWidgets = new ArrayList();

    public TopPanelPlayerPanels() {}

    // This function resorts the widgets, changing their on-screen positions.
    public static void SortWidgets() {
        TogetherManager.logger.info("Sorting Widgets...");

        // Sorting Widgets
        Collections.sort(TopPanelPlayerPanels.playerWidgets);

        // Setting the new ranks
        int i = 0;
        for (RemotePlayerWidget widget : TopPanelPlayerPanels.playerWidgets) {
            widget.setRank(i);
            i++;
        }
    }


    @SpirePatch(clz = TopPanel.class, method="render")
    public static class renderPlayerPanels {
        public static void Postfix(TopPanel __instance, SpriteBatch sb) {
            for (RemotePlayerWidget widget : TopPanelPlayerPanels.playerWidgets) {
                widget.render(sb);
            }
        }
    }
}
