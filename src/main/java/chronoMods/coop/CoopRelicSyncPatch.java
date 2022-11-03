package chronoMods.coop;

import chronoMods.TogetherManager;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import java.util.ArrayList;

public class CoopRelicSyncPatch {

    @SpirePatch(clz = AbstractDungeon.class, method="initializeRelicList")
    public static class PadRelicLists {
        @SpireInsertPatch(rloc=1545-1533)
        public static void Insert(AbstractDungeon __instance) {
            
            if (TogetherManager.gameMode != TogetherManager.mode.Coop) { return; }

            CoopRelicSyncPatch.padRelicList(AbstractDungeon.commonRelicPool, AbstractRelic.RelicTier.COMMON);
            CoopRelicSyncPatch.padRelicList(AbstractDungeon.uncommonRelicPool, AbstractRelic.RelicTier.UNCOMMON);
            CoopRelicSyncPatch.padRelicList(AbstractDungeon.rareRelicPool, AbstractRelic.RelicTier.RARE);
            CoopRelicSyncPatch.padRelicList(AbstractDungeon.shopRelicPool, AbstractRelic.RelicTier.SHOP);
            CoopRelicSyncPatch.padRelicList(AbstractDungeon.bossRelicPool, AbstractRelic.RelicTier.BOSS);

        }
    }

    public static void padRelicList(ArrayList<String> relicPool, AbstractRelic.RelicTier tier) {
        ArrayList<String> compareCount = new ArrayList();

        RelicLibrary.populateRelicPool(compareCount, tier, AbstractPlayer.PlayerClass.IRONCLAD);
        TogetherManager.log("Padding " + tier + ": " + (compareCount.size()-relicPool.size()));
        int finalSize = compareCount.size()-relicPool.size();
        for (int i = 0; i <  finalSize; i++) {
            relicPool.add("Circlet");
            AbstractDungeon.relicsToRemoveOnStart.add("Circlet");
        }
        TogetherManager.log("Final Padding " + tier + ": " + (compareCount.size()-relicPool.size()));
        compareCount.clear();
    }
}