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

public class MapButtonWorksPatch
{
    @SpirePatch(clz = TopPanel.class, method="updateMapButtonLogic")
    public static class updateBasedOnCurrentModScreen {
        @SpireInsertPatch(rloc=917-888)
            public static void Insert(TopPanel __instance) {

            if (AbstractDungeon.screen == NewDeathScreenPatches.Enum.RACEEND) {
                ReflectionHacks.setPrivate(__instance, TopPanel.class, "mapButtonDisabled", false);
                __instance.mapHb.update();
                __instance.mapHb.justHovered = false;
            }

            if (TogetherManager.gameMode != TogetherManager.mode.Coop) { return; }

            if (AbstractDungeon.screen == CoopCourierScreen.Enum.COURIER ||
                AbstractDungeon.screen == CoopBossRelicSelectScreen.Enum.TEAMRELIC) {
                ReflectionHacks.setPrivate(__instance, TopPanel.class, "mapButtonDisabled", false);
                __instance.mapHb.update();
                __instance.mapHb.justHovered = false;
            }
        }
    }

    @SpirePatch(clz = TopPanel.class, method="updateMapButtonLogic")
    public static class updateAndScreenChangeBasedOnCurrentModScreen {
        @SpireInsertPatch(rloc=989-888)
        public static void Insert(TopPanel __instance) {
            boolean clickedMapButton = (__instance.mapHb.hovered && InputHelper.justClickedLeft);
            if ((clickedMapButton || InputActionSet.masterDeck.isJustPressed() || CInputActionSet.pageLeftViewDeck.isJustPressed()) && !CardCrawlGame.isPopupOpen) {
              
              if (AbstractDungeon.screen == CoopCourierScreen.Enum.COURIER) {
                AbstractDungeon.overlayMenu.cancelButton.hide();
                AbstractDungeon.dungeonMapScreen.open(false);
                AbstractDungeon.previousScreen = CoopCourierScreen.Enum.COURIER;
              } 

              else if (AbstractDungeon.screen == CoopBossRelicSelectScreen.Enum.TEAMRELIC) {
                AbstractDungeon.previousScreen = CoopBossRelicSelectScreen.Enum.TEAMRELIC;
                TogetherManager.teamRelicScreen.hide();
                AbstractDungeon.dungeonMapScreen.open(false);
              } 

              else if (AbstractDungeon.screen == NewDeathScreenPatches.Enum.RACEEND) {
                AbstractDungeon.previousScreen = NewDeathScreenPatches.Enum.RACEEND;
                NewDeathScreenPatches.EndScreenBase.hide();
                AbstractDungeon.dungeonMapScreen.open(false);
              }
            }
        }
    }
}