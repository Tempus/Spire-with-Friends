package chronoMods.coop;

import com.evacipated.cardcrawl.modthespire.lib.*;

import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;

import com.megacrit.cardcrawl.localization.*;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.core.*;
import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.cards.purple.*;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.rooms.*;
import com.megacrit.cardcrawl.helpers.*;
import com.megacrit.cardcrawl.events.city.*;
import com.megacrit.cardcrawl.events.shrines.*;
import com.megacrit.cardcrawl.rewards.*;
import com.megacrit.cardcrawl.relics.*;
import com.megacrit.cardcrawl.actions.utility.*;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.unique.*;
import com.megacrit.cardcrawl.vfx.cardManip.*;
import com.megacrit.cardcrawl.vfx.*;

import basemod.*;
import basemod.abstracts.*;
import basemod.interfaces.*;

import java.util.*;

import chronoMods.*;
import chronoMods.network.steam.*;
import chronoMods.network.*;
import chronoMods.coop.*;
import chronoMods.ui.deathScreen.*;
import chronoMods.ui.hud.*;
import chronoMods.ui.lobby.*;
import chronoMods.ui.mainMenu.*;

public class WatcherHardModePatch {

    @SpirePatch(clz = Watcher.class, method="getStartingDeck")
    public static class WatcherHardMode {
        public static ArrayList<String> Postfix(ArrayList<String> __result, Watcher __instance) {
            if (NewMenuButtons.newGameScreen != null) {
                if (NewMenuButtons.newGameScreen.hardToggle.isTicked()) {
                    __result.remove(__result.indexOf("Eruption"));
                    __result.add("SashWhip");
                }
            }

            return __result;
        }
    }

    @SpirePatch(clz = AbstractPlayer.class, method="initializeStarterDeck")
    public static class WatcherHardModePoolAdjust {
        public static void Postfix(AbstractPlayer __instance) {
            if (NewMenuButtons.newGameScreen != null) {
                if (NewMenuButtons.newGameScreen.hardToggle.isTicked() && __instance.chosenClass == AbstractPlayer.PlayerClass.WATCHER) {
                    AbstractDungeon.uncommonCardPool.removeCard("SashWhip");

                    AbstractCard c = new Eruption();
                    c.rarity = AbstractCard.CardRarity.UNCOMMON;
                    AbstractDungeon.uncommonCardPool.addToBottom(c);
                }
            }
        }
    }
}