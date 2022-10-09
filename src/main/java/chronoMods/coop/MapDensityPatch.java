package chronoMods.coop;

import chronoMods.TogetherManager;
import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.map.MapGenerator;
import com.megacrit.cardcrawl.map.MapRoomNode;

public class MapDensityPatch
{
    @SpirePatch(clz = MapGenerator.class, method="generateDungeon")
    public static class changeDungeonDensity {
        public static void Prefix(int height, int width, @ByRef int[] pathDensity, com.megacrit.cardcrawl.random.Random rng) {
            if (TogetherManager.gameMode == TogetherManager.mode.Coop) {
                pathDensity[0] = 3 + (int)(TogetherManager.players.size() * 2.5f); // 5.5, 8, 10.5, 13, 15.5, 18
            }
        }
    }

    @SpirePatch(clz = MapRoomNode.class, method=SpirePatch.CONSTRUCTOR)
    public static class MapJitterSeed {
        public static void Prefix(MapRoomNode __instance) {
        	__instance.offsetX = (int)AbstractDungeon.miscRng.random(-27.0F * Settings.xScale, 27.0F * Settings.xScale);
        	__instance.offsetY = (int)AbstractDungeon.miscRng.random(-37.0F * Settings.xScale, 37.0F * Settings.xScale);
        }
    }
}