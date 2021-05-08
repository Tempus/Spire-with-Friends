package chronoMods.coop;

import com.evacipated.cardcrawl.modthespire.lib.*;

import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;

import com.megacrit.cardcrawl.localization.*;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.core.*;
import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.rooms.*;
import com.megacrit.cardcrawl.helpers.*;
import com.megacrit.cardcrawl.events.city.*;
import com.megacrit.cardcrawl.events.shrines.*;
import com.megacrit.cardcrawl.rewards.*;
import com.megacrit.cardcrawl.relics.*;
import com.megacrit.cardcrawl.actions.utility.*;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.unique.*;
import com.megacrit.cardcrawl.vfx.cardManip.*;
import com.megacrit.cardcrawl.vfx.*;

import basemod.*;
import basemod.abstracts.*;
import basemod.interfaces.*;

import java.util.*;

import chronoMods.*;
import chronoMods.network.steam.*;
import chronoMods.network.*;
import chronoMods.coop.*;
import chronoMods.ui.deathScreen.*;
import chronoMods.ui.hud.*;
import chronoMods.ui.lobby.*;
import chronoMods.ui.mainMenu.*;

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