package chronoMods.steam;

import com.evacipated.cardcrawl.modthespire.lib.*;
import basemod.interfaces.*;

import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import com.megacrit.cardcrawl.core.Settings;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.potions.*;
import com.megacrit.cardcrawl.map.*;
import com.megacrit.cardcrawl.neow.*;
import com.megacrit.cardcrawl.relics.*;
import com.megacrit.cardcrawl.screens.select.*;
import com.megacrit.cardcrawl.unlock.AbstractUnlock;
import com.megacrit.cardcrawl.vfx.*;
import com.megacrit.cardcrawl.vfx.cardManip.*;

import java.util.*;

import chronoMods.*;
import chronoMods.coop.relics.*;
import chronoMods.steam.*;
import chronoMods.ui.deathScreen.*;
import chronoMods.ui.hud.*;
import chronoMods.ui.lobby.*;
import chronoMods.ui.mainMenu.*;
import chronoMods.utilities.*;

public class SendDataPatches implements StartActSubscriber {

    public void receiveStartAct() {
        if (TogetherManager.gameMode == TogetherManager.mode.Normal) { return; }
        if (TogetherManager.teamRelicScreen != null)
            TogetherManager.teamRelicScreen.isDone = false;
    
        NetworkHelper.sendData(NetworkHelper.dataType.Hp);
        NetworkHelper.sendData(NetworkHelper.dataType.Money);
    }

    @SpirePatch(clz = AbstractPlayer.class, method="gainGold")
    public static class sendGainGold {
        public static void Postfix(AbstractPlayer __instance, int amount) {
            if (TogetherManager.gameMode == TogetherManager.mode.Normal) { return; }
        	NetworkHelper.sendData(NetworkHelper.dataType.Money);
        }
    }

    @SpirePatch(clz = AbstractPlayer.class, method="loseGold")
    public static class sendLoseGold {
        public static void Postfix(AbstractPlayer __instance, int amount) {
            if (TogetherManager.gameMode == TogetherManager.mode.Normal) { return; }
        	NetworkHelper.sendData(NetworkHelper.dataType.Money);
        }
    }

    @SpirePatch(clz = AbstractPlayer.class, method="damage")
    public static class sendDamage {
        public static void Postfix(AbstractPlayer __instance, DamageInfo amount) {
            if (TogetherManager.gameMode == TogetherManager.mode.Normal) { return; }
            if (amount.base == 0) { return; }
        	NetworkHelper.sendData(NetworkHelper.dataType.Hp);
        }
    }

    @SpirePatch(clz = AbstractPlayer.class, method="heal")
    public static class sendHeal {
        public static void Postfix(AbstractPlayer __instance, int amount) {
            if (TogetherManager.gameMode == TogetherManager.mode.Normal) { return; }
            if (amount == 0) { return; }
        	NetworkHelper.sendData(NetworkHelper.dataType.Hp);
        }
    }

    @SpirePatch(clz = AbstractCreature.class, method="heal", paramtypez = {int.class, boolean.class})
    public static class sendHealB {
        public static void Postfix(AbstractCreature __instance, int amount, boolean showEffect) {
            if (TogetherManager.gameMode == TogetherManager.mode.Normal) { return; }
            if (amount == 0) { return; }
            NetworkHelper.sendData(NetworkHelper.dataType.Hp);
        }
    }

    @SpirePatch(clz = AbstractCreature.class, method="increaseMaxHp")
    public static class sendMaxHpIncrease {
        public static void Postfix(AbstractCreature __instance, int amount, boolean showEffect) {
            if (TogetherManager.gameMode == TogetherManager.mode.Normal) { return; }
            if (amount == 0) { return; }
            NetworkHelper.sendData(NetworkHelper.dataType.Hp);
        }
    }

    @SpirePatch(clz = AbstractDungeon.class, method="nextRoomTransition", paramtypez = {SaveFile.class})
    public static class sendNextRoom {
        public static void Postfix(AbstractDungeon __instance, SaveFile saveFile) {
            if (TogetherManager.gameMode == TogetherManager.mode.Normal) { return; }
        	NetworkHelper.sendData(NetworkHelper.dataType.Floor);
        }
    }

    @SpirePatch(clz = AbstractDungeon.class, method="dungeonTransitionSetup")
    public static class sendNextAct {
        public static void Postfix() {
            if (TogetherManager.gameMode == TogetherManager.mode.Normal) { return; }
            NetworkHelper.sendData(NetworkHelper.dataType.Act);
        }
    }

    // Potion acquisition
    @SpirePatch(clz = AbstractPlayer.class, method="obtainPotion", paramtypez = {int.class, AbstractPotion.class})
    public static class getPotionSpecificSlot {
        public static void Postfix(AbstractPlayer __instance, int slot, AbstractPotion potionToObtain) {
            if (TogetherManager.gameMode == TogetherManager.mode.Normal) { return; }
            NetworkHelper.sendData(NetworkHelper.dataType.GetPotion);
        }
    }

    @SpirePatch(clz = AbstractPlayer.class, method="obtainPotion", paramtypez = {AbstractPotion.class})
    public static class getPotion {
        public static void Postfix(AbstractPlayer __instance, AbstractPotion potionToObtain) {
            if (TogetherManager.gameMode == TogetherManager.mode.Normal) { return; }
            NetworkHelper.sendData(NetworkHelper.dataType.GetPotion);
        }
    }

    @SpirePatch(clz = AbstractPlayer.class, method="removePotion", paramtypez = {AbstractPotion.class})
    public static class losePotion {
        public static void Postfix(AbstractPlayer __instance, AbstractPotion potionToObtain) {
            if (TogetherManager.gameMode == TogetherManager.mode.Normal) { return; }
            VaporFunnel.potSlot = potionToObtain.slot;
            NetworkHelper.sendData(NetworkHelper.dataType.UsePotion);
        }
    }

    // Places to mark splits
    @SpirePatch(clz = AbstractDungeon.class, method="setBoss")
    public static class actTransition {
        public static void Postfix(AbstractDungeon __instance, String key) {
            if (TogetherManager.gameMode != TogetherManager.mode.Versus) { return; }
            NetworkHelper.sendData(NetworkHelper.dataType.Splits);
        }
    }

    // Change the relic display
    @SpirePatch(clz = BossRelicSelectScreen.class, method="relicObtainLogic")
    public static class ignoreBitchesAcquireRelics {
        public static void Postfix() {
            if (TogetherManager.gameMode == TogetherManager.mode.Normal) { return; }
            NetworkHelper.sendData(NetworkHelper.dataType.SetDisplayRelics);
        }
    }

    @SpirePatch(clz = NeowEvent.class, method="buttonEffect")
    public static class ignoreBitchesAcquireRelicsD {
        public static void Postfix() {
            if (TogetherManager.gameMode == TogetherManager.mode.Normal) { return; }
            NetworkHelper.sendData(NetworkHelper.dataType.SetDisplayRelics);
        }
    }

    // Relic Count
    @SpirePatch(clz = AbstractRelic.class, method="relicTip")
    public static class RelicCountUpdate {
        public static void Postfix() {
            if (TogetherManager.gameMode == TogetherManager.mode.Normal) { return; }
            NetworkHelper.sendData(NetworkHelper.dataType.RelicInfo);
        }
    }

    // Deck Count
    @SpirePatch(clz = CardGroup.class, method="removeCard", paramtypez = {AbstractCard.class})
    public static class UpdateDeckCountA {
        @SpireInsertPatch(rloc=192-190)
        public static void Postfix(CardGroup __instance, AbstractCard c) {
            if (TogetherManager.gameMode == TogetherManager.mode.Normal) { return; }
            NetworkHelper.sendData(NetworkHelper.dataType.DeckInfo);
        }
    }

    @SpirePatch(clz = ShowCardAndObtainEffect.class, method="update")
    public static class UpdateDeckCountB {
        @SpireInsertPatch(rloc=106-94)
        public static void Postfix(ShowCardAndObtainEffect __instance) {
            if (TogetherManager.gameMode == TogetherManager.mode.Normal) { return; }
            NetworkHelper.sendData(NetworkHelper.dataType.DeckInfo);
        }
    }

    @SpirePatch(clz = FastCardObtainEffect.class, method="update")
    public static class UpdateDeckCountC {
        @SpireInsertPatch(rloc=58-42)
        public static void Postfix(FastCardObtainEffect __instance) {
            if (TogetherManager.gameMode == TogetherManager.mode.Normal) { return; }
            NetworkHelper.sendData(NetworkHelper.dataType.DeckInfo);
        }
    }

    // Coop empty room patches
    @SpirePatch(clz = AbstractDungeon.class, method="setCurrMapNode")
    public static class emptyRoomCoopExit {
        public static void Prefix() {
            if (TogetherManager.gameMode == TogetherManager.mode.Coop && !AbstractDungeon.id.equals("TheEnding")) {
                NetworkHelper.sendData(NetworkHelper.dataType.ClearRoom);
            }
        }
    }

    public static int lockX;
    public static int lockY;

    @SpirePatch(clz = MapRoomNode.class, method="playNodeSelectedSound")
    public static class emptyRoomCoopEnter {
        public static void Postfix(MapRoomNode __instance) {
            if (TogetherManager.gameMode == TogetherManager.mode.Coop && !AbstractDungeon.id.equals("TheEnding")) {
                lockX = __instance.x;
                lockY = __instance.y;
                NetworkHelper.sendData(NetworkHelper.dataType.LockRoom);
            }
        }
    }
}
