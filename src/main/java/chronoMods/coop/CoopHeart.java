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


public class CoopHeart {

  public static int maxHP;
  public static int HP;

  public static void init() {
    CoopHeart.maxHP = TogetherManager.players.size() * 800;
    CoopHeart.HP = CoopHeart.maxHP;
  }
  
  public static void updateHP(int HP) {
    CoopHeart.HP = HP;
    // if (AbstractDungeon.getCurrRoom().isHeartFight) {
    //  AbstractDungeon.getCurrRoom().monsters().getHeart().updateHealthBar()
  }

    // Change this to patch on heart health loss
    @SpirePatch(clz = ProceedButton.class, method="goToNextDungeon")
    public static class ProceedButtonShouldNotProceed {
        public static SpireReturn Prefix(ProceedButton __instance, AbstractRoom room) {
            if (TogetherManager.gameMode != TogetherManager.mode.Coop) { return SpireReturn.Continue(); }

            return SpireReturn.Return(null);
        }
    }

    // Change this patch to make the Invulnerable on the heart get more strict as you deal more damage
    // The basic idea here is that it would suck to play the game, and then everyone else beats the heart for you.
    // If you're 2 players, you could limit it such that a single player can't do more than 1200
    // But then if you're three players, two players could invalidate the third player's heart fight.
    @SpirePatch(clz = ProceedButton.class, method="goToNextDungeon")
    public static class ProceedButtonShouldNotProceed {
        public static SpireReturn Prefix(ProceedButton __instance, AbstractRoom room) {
            if (TogetherManager.gameMode != TogetherManager.mode.Coop) { return SpireReturn.Continue(); }

            return SpireReturn.Return(null);
        }
    }
}
