package chronoMods.steam;

import com.evacipated.cardcrawl.modthespire.lib.*;
import basemod.interfaces.*;

import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.ui.buttons.*;
import com.megacrit.cardcrawl.rooms.*;

import java.util.*;

import chronoMods.*;
import chronoMods.steam.*;
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

        	TogetherManager.logger.info("teamRelicScreen: " + TogetherManager.teamRelicScreen.isDone);

        	if (TogetherManager.teamRelicScreen.isDone) { return SpireReturn.Continue(); }

        	AbstractDungeon.closeCurrentScreen();
        	AbstractDungeon.overlayMenu.cancelButton.hide();
    		__instance.hide();

    		return SpireReturn.Return(null);
		}
	}
}