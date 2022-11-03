package chronoMods.coop;

import chronoMods.TogetherManager;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.ui.buttons.ProceedButton;

public class ProceedButtonPatch {

    @SpirePatch(clz = ProceedButton.class, method="goToNextDungeon")
    public static class ProceedButtonShouldNotProceed {
        public static SpireReturn Prefix(ProceedButton __instance, AbstractRoom room) {
            if (TogetherManager.gameMode != TogetherManager.mode.Coop) { return SpireReturn.Continue(); }
            // Put in boss relic skip here
            TogetherManager.log("Okay, here we go.");
            if (TogetherManager.teamRelicScreen.isDone) { return SpireReturn.Continue(); }
            TogetherManager.log("No proceed, whyyyy");

            AbstractDungeon.overlayMenu.cancelButton.hideInstantly();
            __instance.hideInstantly();
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
            __instance.hideInstantly();

            return SpireReturn.Return(null);
        }
    }

    // Boss Relic Proceed Button Co-op Patches for in-between Boss Relic Acquisition and Team Relic screen
    @SpirePatch(clz = AbstractRelic.class, method="update")
    public static class ProceedButtonShouldNotShow {
        @SpireInsertPatch(rloc=388-335)
        public static void Insert(AbstractRelic __instance) {
            if (TogetherManager.gameMode != TogetherManager.mode.Coop) { return; }
            if (TogetherManager.teamRelicScreen.isDone) { return; }

            AbstractDungeon.overlayMenu.proceedButton.hideInstantly();
        }
    }

    // @SpirePatch(clz = AbstractRoom.class, method="update")
    // public static class ProceedButtonShouldNotShowB {
    //     @SpireInsertPatch(rloc=388-252)
    //     public static void Insert(AbstractRoom __instance) {
    //         if (TogetherManager.gameMode != TogetherManager.mode.Coop && !TogetherManager.teamRelicScreen.isDone) { return; }

    //         AbstractDungeon.overlayMenu.proceedButton.hideInstantly();
    //     }
    // }


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