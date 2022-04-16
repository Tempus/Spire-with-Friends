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
import com.megacrit.cardcrawl.screens.options.*;

import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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

public class SettingsMenuTextPatches
{
    public static final String[] msg = CardCrawlGame.languagePack.getUIString("RaceEnd").TEXT;

    @SpirePatch(clz = ExitGameButton.class, method="updateLabel")
    public static class ExitButtonDoesntChange {
        public static void Postfix(ExitGameButton __instance, String newLabel) {
            if (TogetherManager.gameMode != TogetherManager.mode.Normal)
                ReflectionHacks.setPrivate(__instance, ExitGameButton.class, "label", msg[14]);
        }
    }

    @SpirePatch(clz = ExitGameButton.class, method=SpirePatch.CONSTRUCTOR)
    public static class ExitButtonSaysDisconnect {
        public static void Postfix(ExitGameButton __instance) {
            if (TogetherManager.gameMode != TogetherManager.mode.Normal)
                ReflectionHacks.setPrivate(__instance, ExitGameButton.class, "label", msg[14]);
        }
    }

    @SpirePatch(clz = AbandonRunButton.class, method="render")
    public static class AbandonButtonSaysNew {
        public static SpireReturn Prefix(AbandonRunButton __instance, SpriteBatch sb) {
            if (TogetherManager.gameMode == TogetherManager.mode.Bingo) {

                float x = (float)ReflectionHacks.getPrivate(__instance, AbandonRunButton.class, "x");
                float y = (float)ReflectionHacks.getPrivate(__instance, AbandonRunButton.class, "y");
                int W = (int)ReflectionHacks.getPrivate(__instance, AbandonRunButton.class, "W");
                int H = (int)ReflectionHacks.getPrivate(__instance, AbandonRunButton.class, "H");
                Hitbox hb = (Hitbox)ReflectionHacks.getPrivate(__instance, AbandonRunButton.class, "hb");

                // Fucking final private variables hardcoded into function calls. Technically an Instrument patch would be better, but goddamn it's annoying to do.
                sb.setColor(Color.WHITE);
                sb.draw(ImageMaster.OPTION_ABANDON, x - W / 2.0F, y - H / 2.0F, W / 2.0F, H / 2.0F, W, H, Settings.scale, Settings.scale, 0.0F, 0, 0, W, H, false, false);
                FontHelper.renderFontCentered(sb, FontHelper.buttonLabelFont, msg[15], x + 15.0F * Settings.scale, y + 5.0F * Settings.scale, Settings.GOLD_COLOR);
                if (hb.hovered) {
                  sb.setBlendFunction(770, 1);
                  sb.setColor(new Color(1.0F, 1.0F, 1.0F, 0.2F));
                  sb.draw(ImageMaster.OPTION_ABANDON, x - W / 2.0F, y - H / 2.0F, W / 2.0F, H / 2.0F, W, H, Settings.scale, Settings.scale, 0.0F, 0, 0, W, H, false, false);
                  sb.setBlendFunction(770, 771);
                } 
                if (Settings.isControllerMode)
                  sb.draw(CInputActionSet.proceed
                      .getKeyImg(), x - 32.0F - 32.0F * Settings.scale - 
                      FontHelper.getSmartWidth(FontHelper.buttonLabelFont, msg[15], 99999.0F, 0.0F) / 2.0F, y - 32.0F + 5.0F * Settings.scale, 32.0F, 32.0F, 64.0F, 64.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 64, 64, false, false); 
                hb.render(sb);
                
                return SpireReturn.Return(null);
            }

            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = SettingsScreen.class, method="popup")
    public static class AbandonButtonClearerWarning {
        public static void Prefix(SettingsScreen __instance, ConfirmPopup.ConfirmType type) {
            if (TogetherManager.gameMode == TogetherManager.mode.Bingo) {
                __instance.abandonPopup.desc = msg[16];
            } else {
                __instance.abandonPopup.desc = SettingsScreen.TEXT[2];
            }
        }
    }

    @SpirePatch(clz = SettingsScreen.class, method="popup")
    public static class ExitPopupDisconnectWarning {
        @SpireInsertPatch(rloc=80-67)
        public static void Insert(SettingsScreen __instance, ConfirmPopup.ConfirmType type) {
            __instance.exitPopup.desc = msg[17];
        }
    }

}