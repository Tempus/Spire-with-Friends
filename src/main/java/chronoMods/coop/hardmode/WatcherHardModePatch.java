package chronoMods.coop;

import chronoMods.TogetherManager;
import chronoMods.ui.mainMenu.NewMenuButtons;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.purple.Eruption;
import com.megacrit.cardcrawl.cards.purple.JustLucky;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.characters.Watcher;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.util.ArrayList;

public class WatcherHardModePatch {

    @SpirePatch(clz = Watcher.class, method="getStartingDeck")
    public static class WatcherHardMode {
        public static ArrayList<String> Postfix(ArrayList<String> __result, Watcher __instance) {
            if (TogetherManager.gameMode != TogetherManager.mode.Coop) { return __result; }

            if (NewMenuButtons.newGameScreen != null) {
                if (NewMenuButtons.newGameScreen.hardToggle.isTicked()) {
                    __result.remove(__result.indexOf("Eruption"));
                    __result.add("JustLucky");
                    __result.add(0, "Strike_P");
                    __result.add(5, "Defend_P");
                }
            }

            return __result;
        }
    }

    @SpirePatch(clz = AbstractPlayer.class, method="initializeStarterDeck")
    public static class WatcherHardModePoolAdjust {
        public static void Postfix(AbstractPlayer __instance) {
            if (TogetherManager.gameMode != TogetherManager.mode.Coop) { return; }

            if (NewMenuButtons.newGameScreen != null) {
                if (NewMenuButtons.newGameScreen.hardToggle.isTicked() && __instance.chosenClass == AbstractPlayer.PlayerClass.WATCHER) {
                    AbstractDungeon.commonCardPool.removeCard("JustLucky");

                    AbstractCard c = new Eruption();
                    c.rarity = AbstractCard.CardRarity.UNCOMMON; // This doesn't do anything since it isn't propagated in makeCopy()
                    AbstractDungeon.uncommonCardPool.addToBottom(c);
                }
            }
        }
    }

    @SpirePatch(clz = Eruption.class, method="makeCopy")
    public static class EruptionRarityChange {
        public static AbstractCard Postfix(AbstractCard __result, Eruption __instance) {
            if (TogetherManager.gameMode != TogetherManager.mode.Coop) { return __result; }

            if (AbstractDungeon.player != null)
                if (AbstractDungeon.player.hasBlight("StrangeFlame")) 
                    __result.rarity = AbstractCard.CardRarity.UNCOMMON;
            
            return __result;
        }
    }

    @SpirePatch(clz = JustLucky.class, method="makeCopy")
    public static class JustLuckyRarityChange {
        public static AbstractCard Postfix(AbstractCard __result, JustLucky __instance) {
            if (TogetherManager.gameMode != TogetherManager.mode.Coop) { return __result; }

            if (AbstractDungeon.player != null)
                if (AbstractDungeon.player.hasBlight("StrangeFlame")) 
                    __result.rarity = AbstractCard.CardRarity.BASIC;
            
            return __result;
        }
    }

    @SpirePatch(clz = Watcher.class, method="getStartCardForEvent")
    public static class WatcherHardModeMatchKeepAdjust {
        public static AbstractCard Postfix(AbstractCard __result, Watcher __instance) {
            if (TogetherManager.gameMode != TogetherManager.mode.Coop) { return __result; }

            if (AbstractDungeon.player != null)
                if (AbstractDungeon.player.hasBlight("StrangeFlame"))
                    return new JustLucky();
    
            return __result;
        }
    }
}