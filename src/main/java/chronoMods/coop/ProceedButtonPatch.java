package chronoMods.coop;

import com.evacipated.cardcrawl.modthespire.lib.*;
import basemod.interfaces.*;

import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.ui.buttons.*;
import com.megacrit.cardcrawl.rooms.*;

import java.util.*;

import chronoMods.*;
import chronoMods.network.steam.*;
import chronoMods.network.*;
import chronoMods.ui.deathScreen.*;
import chronoMods.ui.hud.*;
import chronoMods.ui.lobby.*;
import chronoMods.ui.mainMenu.*;
import chronoMods.utilities.*;

public class ProceedButtonPatch {

    @SpirePatch(clz = ProceedButton.class, method="goToNextDungeon")
    public static class ProceedButtonShouldNotProceed {
        public static SpireReturn Prefix(ProceedButton __instance, AbstractRoom room) {
            if (TogetherManager.gameMode != TogetherManager.mode.Coop) { return SpireReturn.Continue(); }
            // Put in boss relic skip here
            if (TogetherManager.teamRelicScreen.isDone) { return SpireReturn.Continue(); }

            AbstractDungeon.overlayMenu.cancelButton.hide();
            __instance.hide();
            AbstractDungeon.closeCurrentScreen();

            return SpireReturn.Return(null);
        }
    }

    // Calling Bell Neow Boss Swap Fix
    @SpirePatch(clz = ProceedButton.class, method="update")
    public static class ProceedButtonShouldNotProceedB {
        @SpireInsertPatch(rloc=195-89)
        public static SpireReturn Insert(ProceedButton __instance) {
            if (TogetherManager.gameMode != TogetherManager.mode.Coop) { return SpireReturn.Continue(); }

            AbstractDungeon.closeCurrentScreen();
            __instance.hide();

            return SpireReturn.Return(null);
        }
    }

    // Boss Jump Patches
    @SpirePatch(clz = ProceedButton.class, method="goToTreasureRoom")
    public static class BossJumpA {
        public static void Postfix(ProceedButton __instance) {
            if (TogetherManager.gameMode != TogetherManager.mode.Coop) { return; }

            AbstractDungeon.nextRoom.y = 16;
        }
    }

    @SpirePatch(clz = ProceedButton.class, method="goToVictoryRoomOrTheDoor")
    public static class BossJumpB {
        public static void Postfix(ProceedButton __instance) {
            if (TogetherManager.gameMode != TogetherManager.mode.Coop) { return; }

            AbstractDungeon.nextRoom.y = 16;
        }
    }

    @SpirePatch(clz = ProceedButton.class, method="goToDoubleBoss")
    public static class BossJumpC {
        public static void Postfix(ProceedButton __instance) {
            if (TogetherManager.gameMode != TogetherManager.mode.Coop) { return; }

            AbstractDungeon.nextRoom.y = 16;
        }
    }

    @SpirePatch(clz = ProceedButton.class, method="goToDemoVictoryRoom")
    public static class BossJumpD {
        public static void Postfix(ProceedButton __instance) {
            if (TogetherManager.gameMode != TogetherManager.mode.Coop) { return; }

            AbstractDungeon.nextRoom.y = 16;
        }
    }
}