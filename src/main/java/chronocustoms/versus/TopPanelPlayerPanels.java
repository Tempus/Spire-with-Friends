package chronospeed;

import com.evacipated.cardcrawl.modthespire.lib.*;

import com.megacrit.cardcrawl.ui.panels.TopPanel;
import com.megacrit.cardcrawl.core.Settings;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.FontHelper;

import java.util.*;

import chronospeed.*;

public class TopPanelPlayerPanels {

    public static ArrayList<RemotePlayerWidget> playerWidgets = new ArrayList();

    public TopPanelPlayerPanels() {}

    @SpirePatch(clz = TopPanel.class, method="render")
    public static class renderPlayerPanels {
        public static void Postfix(TopPanel __instance, SpriteBatch sb) {
            int i = 0;
            for (RemotePlayerWidget widget : TopPanelPlayerPanels.playerWidgets) {
                widget.setPos(-8.0F * Settings.scale, 700.0F * Settings.scale - 80.0F * i * Settings.scale);
                widget.render(sb);
                i++;
            }
        }
    }
}
