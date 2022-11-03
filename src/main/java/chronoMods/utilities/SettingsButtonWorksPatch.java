package chronoMods.utilities;

import chronoMods.TogetherManager;
import chronoMods.coop.CoopBossRelicSelectScreen;
import chronoMods.coop.courier.CoopCourierScreen;
import chronoMods.ui.deathScreen.NewDeathScreenPatches;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.ui.panels.TopPanel;

public class SettingsButtonWorksPatch
{
    @SpirePatch(clz = TopPanel.class, method="updateSettingsButtonLogic")
    public static class updateAndScreenChangeBasedOnCurrentModScreen {
        @SpireInsertPatch(rloc=782-654)
        public static void Insert(TopPanel __instance) {
            if (TogetherManager.gameMode == TogetherManager.mode.Coop) { 

            // if ((__instance.settingsHb.hovered && InputHelper.justClickedLeft) || InputHelper.pressedEscape || CInputActionSet.settings.isJustPressed()) {

              if (AbstractDungeon.screen == CoopCourierScreen.Enum.COURIER) {
                TogetherManager.log("Setting Previous Screen to COURIER");
                AbstractDungeon.overlayMenu.cancelButton.hide();
                //AbstractDungeon.settingsScreen.open();
                AbstractDungeon.previousScreen = CoopCourierScreen.Enum.COURIER;
              } 

              else if (AbstractDungeon.screen == CoopBossRelicSelectScreen.Enum.TEAMRELIC) {
                TogetherManager.log("Setting Previous Screen to TEAM RELIC");
                AbstractDungeon.previousScreen = CoopBossRelicSelectScreen.Enum.TEAMRELIC;
                //AbstractDungeon.settingsScreen.open();
                TogetherManager.teamRelicScreen.hide();
              } 

            }

            if (AbstractDungeon.screen == NewDeathScreenPatches.Enum.RACEEND) {
              AbstractDungeon.previousScreen = NewDeathScreenPatches.Enum.RACEEND;
              NewDeathScreenPatches.EndScreenBase.hide();
              //AbstractDungeon.settingsScreen.open();
            }
            // }
        }
    }
}