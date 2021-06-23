package chronoMods.ui.hud;

import com.evacipated.cardcrawl.modthespire.lib.*;

import com.megacrit.cardcrawl.ui.panels.TopPanel;
import com.megacrit.cardcrawl.core.Settings;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.map.*;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.screens.DungeonMapScreen;
import com.megacrit.cardcrawl.vfx.MapDot;

import java.util.*;

import chronoMods.*;
import chronoMods.network.steam.*;
import chronoMods.network.*;
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
            // These are the bottom left coords of the unscaled box
            float xpos = node.x * Settings.scale * 64.0F * 2.0F + 560.0F * Settings.scale - 96.0F + node.offsetX;
            float ypos = node.y * Settings.MAP_DST_Y + 180.0F * Settings.scale + DungeonMapScreen.offsetY - 96.0F + node.offsetY;
            
            // This is the node's scale
            float scale = (float)ReflectionHacks.getPrivate(node, MapRoomNode.class, "scale") + 0.2F;
            int size = (int)(192f * scale);

            // These are the bottom left coords of the scaled box
            int sX = (int)(xpos+96F-(size/2));
            int sY = (int)(ypos+96F-(size/2));

            // This is the interval for each player visited

            // Calculate how many players have been to this node
            int playersVisited = 0;
            for (RemotePlayer player : TogetherManager.players) {
                
                if (player.hasNode(AbstractDungeon.actNum, node))
                    playersVisited++;
            }

            // If no one is here, might as well leave
            if (playersVisited == 0) { return; }

            // Set the Scissor
            sb.flush();
            Gdx.gl.glEnable(GL20.GL_SCISSOR_TEST);

            // Draw the masked node circles
            int playerYOffsetInterval = (int)(size/playersVisited);
            int i = 0;
            for (RemotePlayer player : TogetherManager.players) {
                
                if (player.hasNode(AbstractDungeon.actNum, node)) {

                    Gdx.gl.glScissor(sX, sY+(playerYOffsetInterval*(playersVisited-1-i)), size, playerYOffsetInterval);

                    sb.setColor(player.colour);

                    // Draw the ring
                    sb.draw(ImageMaster.MAP_CIRCLE_5, 
                        xpos, ypos,
                        96.0F, 96.0F, 192.0F, 192.0F, 
                        scale * Settings.scale, scale * Settings.scale, 
                        (float)ReflectionHacks.getPrivate(node, MapRoomNode.class, "angle"), 
                        0, 0, 192, 192, false, false);
                                
                    i++;
                    sb.flush();
               }
            }

            Gdx.gl.glDisable(GL20.GL_SCISSOR_TEST);

            // To avoid scissor clipping the name we draw it separately
            i = 0;
            for (RemotePlayer player : TogetherManager.players) {
                
                if (player.hasNode(AbstractDungeon.actNum, node)) {
                                
                    // Draw the player name
                    if (player.x == node.x && player.y == node.y && player.act == AbstractDungeon.actNum) {
                        // Special Case for starting the floor
                        if (node.x == 0 && node.y == 0) {
                            FontHelper.renderSmartText(sb, FontHelper.topPanelInfoFont, player.userName, 
                                                    node.hb.width + 32.0f*Settings.scale, 
                                                    node.hb.y - (26.0F*i*Settings.scale) + node.hb.height/2, 
                                                    Settings.CREAM_COLOR);

                        } else {
                            FontHelper.renderFontCentered(sb, FontHelper.topPanelInfoFont, player.userName, 
                                                    xpos + 96.0f*Settings.scale, 
                                                    ypos - (26.0F*(i-1)*Settings.scale), 
                                                    Settings.CREAM_COLOR);
                        }
                    }
                    i++;
               }
            }
            sb.setColor(Color.WHITE.cpy());
        }
    }

    @SpirePatch(clz = MapEdge.class, method="render")
    public static class renderPlayerPathsOnMap {
        public static void Prefix(MapEdge edge, SpriteBatch sb) {
            int i = 0;
            for (RemotePlayer player : TogetherManager.players) {
                try {
                    MapRoomNode src = AbstractDungeon.map.get(edge.srcY).get(edge.srcX);
                    MapRoomNode dst = AbstractDungeon.map.get(edge.dstY).get(edge.dstX);

                    if (player.hasNode(AbstractDungeon.actNum, src) &&
                        player.hasNode(AbstractDungeon.actNum, dst)) {

                    // if (player.edgesTaken[AbstractDungeon.actNum].contains(edge)) {
                        sb.setColor(player.colour);

                        ArrayList<MapDot> dots = (ArrayList<MapDot>)ReflectionHacks.getPrivate(edge, MapEdge.class, "dots");

                        for (MapDot d : dots) {
                            float x = (float)ReflectionHacks.getPrivate(d, MapDot.class, "x");
                            ReflectionHacks.setPrivate(d, MapDot.class, "x", x + 6.0f*i*Settings.scale + 3.0f*Settings.scale);
                            d.render(sb);
                            ReflectionHacks.setPrivate(d, MapDot.class, "x", x);
                        }

                        i++;
                    }
                } catch (Exception e) {}
            }
            sb.setColor(Color.WHITE.cpy());
        }
    }
}