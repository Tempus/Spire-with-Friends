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
import com.megacrit.cardcrawl.map.*;
import com.megacrit.cardcrawl.screens.DungeonMapScreen;

import java.util.*;

import chronospeed.*;
/*
public class MapPlayerPatch {

    @SpirePatch(clz = MapRoomNode.class, method="render")
    public static class renderPlayerPositionsOnMap {
        public static void Postfix(MapRoomNode __instance, SpriteBatch sb) {
            int i = 0;
            for (RemotePlayer player : ChronoCustoms.players) {
                if (player.x == __instance.x && player.y == __instance.y) {
                    FontHelper.renderSmartText(sb, FontHelper.topPanelInfoFont, player.userName, 
                                                __instance.hb.x, 
                                                __instance.hb.y - (26.0F*i), 
                                                Settings.CREAM_COLOR);
                    i++;
                }
            }
        }
    }
}

 */
