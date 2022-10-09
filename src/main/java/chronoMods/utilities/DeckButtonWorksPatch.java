package chronoMods.utilities;

import basemod.ReflectionHacks;
import chronoMods.TogetherManager;
import chronoMods.coop.CoopBossRelicSelectScreen;
import chronoMods.coop.courier.CoopCourierScreen;
import chronoMods.ui.deathScreen.NewDeathScreenPatches;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.ui.panels.TopPanel;

public class DeckButtonWorksPatch
{
    @SpirePatch(clz = TopPanel.class, method="updateDeckViewButtonLogic")
    public static class updateBasedOnCurrentModScreen {
        @SpireInsertPatch(rloc=822-793)
        public static void Insert(TopPanel __instance) {
            if (AbstractDungeon.screen == NewDeathScreenPatches.Enum.RACEEND) {
                ReflectionHacks.setPrivate(__instance, TopPanel.class, "deckButtonDisabled", false);
                __instance.deckHb.update();
                __instance.deckHb.justHovered = false;
            }

            if (TogetherManager.gameMode != TogetherManager.mode.Coop) { return; }

            if (AbstractDungeon.screen == CoopCourierScreen.Enum.COURIER ||
                AbstractDungeon.screen == CoopBossRelicSelectScreen.Enum.TEAMRELIC) {
                ReflectionHacks.setPrivate(__instance, TopPanel.class, "deckButtonDisabled", false);
                __instance.deckHb.update();
                __instance.deckHb.justHovered = false;
            }
        }
    }

    @SpirePatch(clz = TopPanel.class, method="updateDeckViewButtonLogic")
    public static class updateAndScreenChangeBasedOnCurrentModScreen {
        @SpireInsertPatch(rloc=880-793)
        public static void Insert(TopPanel __instance) {
            boolean clickedDeckButton = (__instance.deckHb.hovered && InputHelper.justClickedLeft);
            if ((clickedDeckButton || InputActionSet.masterDeck.isJustPressed() || CInputActionSet.pageLeftViewDeck.isJustPressed()) && !CardCrawlGame.isPopupOpen) {
                  
              if (AbstractDungeon.screen == NewDeathScreenPatches.Enum.RACEEND) {
                AbstractDungeon.previousScreen = NewDeathScreenPatches.Enum.RACEEND;
                NewDeathScreenPatches.EndScreenBase.hide();
                AbstractDungeon.deckViewScreen.open();
              }

              if (AbstractDungeon.screen == CoopCourierScreen.Enum.COURIER) {
                AbstractDungeon.overlayMenu.cancelButton.hide();
                AbstractDungeon.deckViewScreen.open();
                AbstractDungeon.previousScreen = CoopCourierScreen.Enum.COURIER;
              } 

              else if (AbstractDungeon.screen == CoopBossRelicSelectScreen.Enum.TEAMRELIC) {
                AbstractDungeon.previousScreen = CoopBossRelicSelectScreen.Enum.TEAMRELIC;
                TogetherManager.teamRelicScreen.hide();
                AbstractDungeon.deckViewScreen.open();
              } 

            }
        }
    }
}