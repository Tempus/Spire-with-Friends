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
import com.megacrit.cardcrawl.neow.*;
import com.megacrit.cardcrawl.unlock.AbstractUnlock;

import java.util.*;

import chronoMods.*;
import chronoMods.steam.*;
import chronoMods.ui.deathScreen.*;
import chronoMods.ui.hud.*;
import chronoMods.ui.lobby.*;
import chronoMods.ui.mainMenu.*;
import chronoMods.utilities.*;

public class SendDataPatches implements StartActSubscriber {

    public void receiveStartAct() {
        TogetherManager.logger.info("receiveStartAct");
        if (TogetherManager.gameMode == TogetherManager.mode.Normal) { return; }
        NetworkHelper.sendData(NetworkHelper.dataType.Hp);
        NetworkHelper.sendData(NetworkHelper.dataType.Money);
        TogetherManager.logger.info("receiveStartActb");
    }

    @SpirePatch(clz = AbstractPlayer.class, method="gainGold")
    public static class sendGainGold {
        public static void Postfix(AbstractPlayer __instance, int amount) {
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
        	NetworkHelper.sendData(NetworkHelper.dataType.Hp);
        }
    }

    @SpirePatch(clz = AbstractPlayer.class, method="heal")
    public static class sendHeal {
        public static void Postfix(AbstractPlayer __instance, int amount) {
            if (TogetherManager.gameMode == TogetherManager.mode.Normal) { return; }
        	NetworkHelper.sendData(NetworkHelper.dataType.Hp);
        }
    }

    @SpirePatch(clz = AbstractCreature.class, method="increaseMaxHp")
    public static class sendMaxHpIncrease {
        public static void Postfix(AbstractCreature __instance, int amount, boolean showEffect) {
            if (TogetherManager.gameMode == TogetherManager.mode.Normal) { return; }
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

    // Places to mark splits
    @SpirePatch(clz = AbstractDungeon.class, method="dungeonTransitionSetup")
    public static class actTransition {
        public static void Postfix() {
            if (TogetherManager.gameMode != TogetherManager.mode.Versus) { return; }
            NetworkHelper.sendData(NetworkHelper.dataType.Splits);
        }
    }

    // Coop card share patch
    @SpirePatch(clz = AbstractDungeon.class, method="nextRoomTransition", paramtypez = {SaveFile.class})
    public static class emptyRoomCoop {
        public static void Prefix(AbstractDungeon __instance, SaveFile saveFile) {
            if (TogetherManager.gameMode == TogetherManager.mode.Coop) {
                NetworkHelper.sendData(NetworkHelper.dataType.TransferCard); 
            }
        }
    }
}
