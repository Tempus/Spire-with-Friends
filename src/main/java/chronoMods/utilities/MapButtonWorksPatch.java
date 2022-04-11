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
import chronoMods.network.steam.*;
import chronoMods.network.*;
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

public class MapButtonWorksPatch
{
    @SpirePatch(clz = TopPanel.class, method="updateMapButtonLogic")
    public static class updateBasedOnCurrentModScreen {
        @SpireInsertPatch(rloc=917-888)
            public static void Insert(TopPanel __instance) {
            if (TogetherManager.gameMode != TogetherManager.mode.Coop) { return; }

            if (AbstractDungeon.screen == CoopCourierScreen.Enum.COURIER ||
                AbstractDungeon.screen == CoopBossRelicSelectScreen.Enum.TEAMRELIC ||
                AbstractDungeon.screen == NewDeathScreenPatches.Enum.RACEEND) {
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
            if (TogetherManager.gameMode != TogetherManager.mode.Coop) { return; }

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