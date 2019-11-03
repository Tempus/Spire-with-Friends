package chronoMods.ui.hud;

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
import com.megacrit.cardcrawl.vfx.MapDot;

import java.util.*;

import chronoMods.*;
import chronoMods.steam.*;
import chronoMods.ui.deathScreen.*;
import chronoMods.ui.hud.*;
import chronoMods.ui.lobby.*;
import chronoMods.ui.mainMenu.*;
import chronoMods.utilities.*;

import basemod.ReflectionHacks;

public class MapPlayerPatch {

    @SpirePatch(clz = MapRoomNode.class, method="render")
    public static class renderPlayerPositionsOnMap {
        public static void Prefix(MapRoomNode node, SpriteBatch sb) {
            int i = 0;
            for (RemotePlayer player : TogetherManager.players) {
                if (player.x == node.x && player.y == node.y) {
                    FontHelper.renderSmartText(sb, FontHelper.topPanelInfoFont, player.userName, 
                                                node.hb.x, 
                                                node.hb.y - (26.0F*i), 
                                                Settings.CREAM_COLOR);
                }

                if (node.taken) {
                    sb.setColor(player.colour);

                    float scale = (float)ReflectionHacks.getPrivate(node, MapRoomNode.class, "scale");
                    sb.draw(ImageMaster.MAP_CIRCLE_5, node.x * Settings.scale * 64.0F * 2.0F + 560.0F * Settings.scale - 96.0F + node.offsetX, node.y * Settings.MAP_DST_Y + 180.0F * Settings.scale + DungeonMapScreen.offsetY - 96.0F + node.offsetY, 96.0F, 96.0F, 192.0F, 192.0F, (scale + 0.2F + 0.05*i) * Settings.scale, (scale + 0.25F + 0.05*i) * Settings.scale, (float)ReflectionHacks.getPrivate(node, MapRoomNode.class, "angle");, 0, 0, 192, 192, false, false);
                
                    i++;
                }

            }
        }
    }

    @SpirePatch(clz = MapEdge.class, method="render")
    public static class renderPlayerPathsOnMap {
        public static void Prefix(MapEdge node, SpriteBatch sb) {
            int i = 0;
            for (RemotePlayer player : TogetherManager.players) {
                if (node.taken) {
                    sb.setColor(player.colour);

                    ArrayList<MapDot> dots = (ArrayList<MapDot>)ReflectionHacks.getPrivate(node, MapEdge.class, "dots");

                    for (MapDot d : dots) {
                        float x = (float)ReflectionHacks.getPrivate(d, MapDot.class, "x");
                        ReflectionHacks.setPrivate(d, MapDot.class, "x", x + 6.0f*i);
                        d.render(sb);
                        ReflectionHacks.setPrivate(d, MapDot.class, "x", x);
                    }

                    i++
                }
            }
        }
    }
}