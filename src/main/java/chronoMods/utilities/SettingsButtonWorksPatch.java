package chronoMods.utilities;

import com.evacipated.cardcrawl.modthespire.lib.*;

import basemod.*;
import basemod.abstracts.*;
import basemod.interfaces.*;

import org.apache.logging.log4j.*;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.helpers.*;
import com.megacrit.cardcrawl.helpers.input.*;
import com.megacrit.cardcrawl.helpers.controller.*;
import com.megacrit.cardcrawl.rooms.*;
import com.megacrit.cardcrawl.map.*;
import com.megacrit.cardcrawl.saveAndContinue.*;
import com.megacrit.cardcrawl.rewards.*;
import com.megacrit.cardcrawl.ui.panels.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.characters.AbstractPlayer;

import chronoMods.*;
import chronoMods.coop.*;
import chronoMods.steam.*;
import chronoMods.ui.deathScreen.*;
import chronoMods.ui.hud.*;
import chronoMods.ui.lobby.*;
import chronoMods.ui.mainMenu.*;
import chronoMods.utilities.*;

import java.util.*;
import java.lang.*;
import java.nio.*;

import com.codedisaster.steamworks.*;
import com.megacrit.cardcrawl.integrations.steam.*;

public class SettingsButtonWorksPatch
{
    @SpirePatch(clz = TopPanel.class, method="updateSettingsButtonLogic")
    public static class updateAndScreenChangeBasedOnCurrentModScreen {
        @SpireInsertPatch(rloc=782-654)
        public static void Insert(TopPanel __instance) {

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

              else if (AbstractDungeon.screen == NewDeathScreenPatches.Enum.RACEEND) {
                AbstractDungeon.previousScreen = NewDeathScreenPatches.Enum.RACEEND;
                NewDeathScreenPatches.raceEndScreen.hide();
                //AbstractDungeon.settingsScreen.open();
              }
            // }
        }
    }
}