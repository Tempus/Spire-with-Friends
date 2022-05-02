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
import com.megacrit.cardcrawl.rooms.*;
import com.megacrit.cardcrawl.map.*;
import com.megacrit.cardcrawl.saveAndContinue.*;
import com.megacrit.cardcrawl.rewards.*;
import com.megacrit.cardcrawl.unlock.*;
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

public class SavePatching
{
    @SpirePatch(clz = SaveHelper.class, method="shouldSave")
    public static class DontSaveRuns {
        public static boolean Postfix(boolean __result) {
            if (TogetherManager.gameMode != TogetherManager.mode.Normal)
                __result = false;
            return __result;
        }
    }

    @SpirePatch(clz = SaveAndContinue.class, method="save")
    public static class DontSaveAnything {
        public static SpireReturn Prefix(SaveFile save) {
            return SpireReturn.Return(null);
        }
    }

    @SpirePatch(clz = SaveAndContinue.class, method="deleteSave")
    public static class DontDeleteAnythingEither {
        public static SpireReturn Prefix(AbstractPlayer p) {
            return SpireReturn.Return(null);
        }
    }    

    @SpirePatch(clz = Prefs.class, method="putInteger")
    public static class DontChangeSpirits {
        public static SpireReturn Prefix(Prefs __instance, String key, int value) {
        	if (key.contains("_SPIRITS"))
	            return SpireReturn.Return(null);
	        return SpireReturn.Continue();
        }
    }   

    // Deal with shitty heart saving stuff
    @SpirePatch(clz = Settings.class, method="setFinalActAvailability")
    public static class StupidFixForHardCheckAgainstHeart {
        public static SpireReturn Prefix() {
            if (TogetherManager.gameMode == TogetherManager.mode.Normal) { return SpireReturn.Continue(); }
            return SpireReturn.Return(null);
        }
    }

    // Dispose of the lobby when we leave
    @SpirePatch(clz = CardCrawlGame.class, method="dispose")
    public static class ForceExitLobby {
        public static void Prefix() {
            NetworkHelper.leaveLobby();
            NetworkHelper.networks.forEach(Integration::dispose);
        }
    }

    // Make sure everything is unlocked
    @SpirePatch(clz = Settings.class, method="treatEverythingAsUnlocked")
    public static class UnlockCardsAndRelics {
        public static boolean Postfix(boolean __result) {
            return true;
        }
    }

    @SpirePatch(clz = UnlockTracker.class, method="isBossSeen")
    public static class UnlockBosses {
        public static boolean Postfix(boolean __result) {
            return true;
        }
    }

    @SpirePatch(clz = UnlockTracker.class, method="isAscensionUnlocked")
    public static class UnlocAscensions {
        public static boolean Postfix(boolean __result, AbstractPlayer p) {
            return true;
        }
    }

    @SpirePatch(clz = UnlockTracker.class, method="isCardLocked")
    public static class UnlockCards {
        public static boolean Postfix(boolean __result, String key) {
            return false;
        }
    }

    @SpirePatch(clz = UnlockTracker.class, method="isCharacterLocked")
    public static class UnlockCharacters {
        public static boolean Postfix(boolean __result, String key) {
            return false;
        }
    }

    @SpirePatch(clz = UnlockTracker.class, method="isRelicLocked")
    public static class UnlockRelics {
        public static boolean Postfix(boolean __result, String key) {
            return false;
        }
    }

}