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

import java.util.*;

import chronospeed.*;

public class MapPlayerPatch {

    public static ArrayList<float[]> playerPositions = new ArrayList();

    // public TopPanelPlayerPanels() {}

    @SpirePatch(clz = MapRoomNode.class, method="render")
    public static class renderPlayerPanels {
        public static void Postfix(TopPanel __instance, SpriteBatch sb) {
            for (float[] position : MapPlayerPatch.playerPositions) {
                // if ()
                // sb.draw(ImageMaster.MAP_CIRCLE_5, this.x * SPACING_X + OFFSET_X - 96.0F + this.offsetX, this.y * Settings.MAP_DST_Y + OFFSET_Y + DungeonMapScreen.offsetY - 96.0F + this.offsetY, 96.0F, 96.0F, 192.0F, 192.0F, (this.scale * 0.95F + 0.2F) * Settings.scale, (this.scale * 0.95F + 0.2F) * Settings.scale, this.angle, 0, 0, 192, 192, false, false);
            }
        }
    }
}

 
