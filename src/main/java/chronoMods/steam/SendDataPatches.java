package chronoMods.steam;

import com.evacipated.cardcrawl.modthespire.lib.*;

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

import chronoMods.*;
import chronoMods.steam.*;
import chronoMods.ui.deathScreen.*;
import chronoMods.ui.hud.*;
import chronoMods.ui.lobby.*;
import chronoMods.ui.mainMenu.*;
import chronoMods.utilities.*;

public class SendDataPatches {

    @SpirePatch(clz = AbstractPlayer.class, method="gainGold")
    public static class sendGainGold {
        public static void Postfix(AbstractPlayer __instance, int amount) {
        	NetworkHelper.sendData(NetworkHelper.dataType.Money);
        }
    }

    @SpirePatch(clz = AbstractPlayer.class, method="loseGold")
    public static class sendLoseGold {
        public static void Postfix(AbstractPlayer __instance, int amount) {
        	NetworkHelper.sendData(NetworkHelper.dataType.Money);
        }
    }

    @SpirePatch(clz = AbstractPlayer.class, method="damage")
    public static class sendDamage {
        public static void Postfix(AbstractPlayer __instance, DamageInfo amount) {
        	NetworkHelper.sendData(NetworkHelper.dataType.Hp);
        }
    }

    @SpirePatch(clz = AbstractPlayer.class, method="heal")
    public static class sendHeal {
        public static void Postfix(AbstractPlayer __instance, int amount) {
        	NetworkHelper.sendData(NetworkHelper.dataType.Hp);
        }
    }

    @SpirePatch(clz = AbstractDungeon.class, method="nextRoomTransition", paramtypez = {SaveFile.class})
    public static class sendNextRoom {
        public static void Postfix(AbstractDungeon __instance, SaveFile saveFile) {
        	NetworkHelper.sendData(NetworkHelper.dataType.Floor);
        }
    }
}
