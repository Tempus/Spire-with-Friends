package chronoMods.network;

import com.evacipated.cardcrawl.modthespire.lib.*;
import basemod.interfaces.*;

import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.potions.*;
import com.megacrit.cardcrawl.map.*;
import com.megacrit.cardcrawl.neow.*;
import com.megacrit.cardcrawl.relics.*;
import com.megacrit.cardcrawl.screens.select.*;
import com.megacrit.cardcrawl.screens.select.*;
import com.megacrit.cardcrawl.ui.panels.TopPanel;

import chronoMods.*;
import chronoMods.network.*;
import chronoMods.network.steam.*;

public class SendDataPatches implements StartActSubscriber {

    public void receiveStartAct() {
        if (TogetherManager.gameMode == TogetherManager.mode.Normal) { return; }
        if (TogetherManager.teamRelicScreen != null)
            TogetherManager.teamRelicScreen.isDone = false;
    
        NetworkHelper.sendData(NetworkHelper.dataType.Hp);
        NetworkHelper.sendData(NetworkHelper.dataType.Money);
    }

    @SpirePatch(clz = TopPanel.class, method="setPlayerName")
    public static class sendStartingInfo {
        public static void Postfix(TopPanel __instance) {
            if (TogetherManager.gameMode == TogetherManager.mode.Normal) { return; }
            NetworkHelper.sendData(NetworkHelper.dataType.Hp);
            NetworkHelper.sendData(NetworkHelper.dataType.Money);
        }
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
    public static AbstractCard sendCard;
    public static boolean sendUpdate = false;
    public static boolean sendRemove = false;

    @SpirePatch(clz = AbstractCard.class, method="onRemoveFromMasterDeck")
    public static class OnRemoveFromMasterDeck {
        public static void Prefix(AbstractCard __instance) {
            if (TogetherManager.gameMode == TogetherManager.mode.Normal) { return; }

            sendCard = __instance;
            sendUpdate = false;
            sendRemove = true;
            TogetherManager.log("Removing Master Deck: " + sendCard.name);
            NetworkHelper.sendData(NetworkHelper.dataType.DeckInfo);
        }
    }

    @SpirePatch(clz = CardGroup.class, method="removeCard", paramtypez={String.class})
    public static class OnRemoveFromMasterDeck2 {
        @SpireInsertPatch(rloc=208-205, localvars={"e"})
        public static void Insert(CardGroup __instance, String targetID, AbstractCard e) {
            if (TogetherManager.gameMode == TogetherManager.mode.Normal) { return; }

            if (__instance.type == CardGroup.CardGroupType.MASTER_DECK) {
                sendCard = e;
                sendUpdate = false;
                sendRemove = true;
                TogetherManager.log("Removing Card Group: " + sendCard.name);
                NetworkHelper.sendData(NetworkHelper.dataType.DeckInfo);
            }
        }
    }

    @SpirePatch(clz = PandorasBox.class, method="onEquip")
    public static class OnRemoveFromMasterDeck3 {
        @SpireInsertPatch(rloc=57-51, localvars={"e"})
        public static void Insert(PandorasBox __instance, AbstractCard e) {
            if (TogetherManager.gameMode == TogetherManager.mode.Normal) { return; }

            sendCard = e;
            sendUpdate = false;
            sendRemove = true;
            TogetherManager.log("Removing Pandora's: " + sendCard.name);
            NetworkHelper.sendData(NetworkHelper.dataType.DeckInfo);
        }
    }

    @SpirePatch(clz = CardGroup.class, method="addToTop")
    public static class UpdateDeckCount {
        public static void Postfix(CardGroup __instance, AbstractCard c) {
            if (TogetherManager.gameMode == TogetherManager.mode.Normal) { return; }

            if (__instance.type == CardGroup.CardGroupType.MASTER_DECK) {
                sendCard = c;
                sendUpdate = false;
                sendRemove = false;
                TogetherManager.log("Adding Master Deck: " + sendCard.name);
                NetworkHelper.sendData(NetworkHelper.dataType.DeckInfo);
            }
        }
    }

    @SpirePatch(clz = AbstractCard.class, method="upgradeName")
    public static class gwUpgrade {
        public static void Postfix(AbstractCard __instance) {
            if (!CardCrawlGame.isInARun()) { return; }
            if (TogetherManager.gameMode == TogetherManager.mode.Normal) { return; }

            if (AbstractDungeon.player.masterDeck.contains(__instance)) {
                sendCard = __instance;
                sendUpdate = true;
                sendRemove = false;
                TogetherManager.log("Upgrading Master Deck: " + sendCard.name);
                NetworkHelper.sendData(NetworkHelper.dataType.DeckInfo);
           }
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
