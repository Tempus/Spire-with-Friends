package chronoMods.ui.mainMenu;

import chronoMods.*;
import chronoMods.steam.*;
import chronoMods.coop.drawable.*;
import chronoMods.ui.deathScreen.*;
import chronoMods.ui.hud.*;
import chronoMods.ui.lobby.*;
import chronoMods.ui.mainMenu.*;
import chronoMods.utilities.*;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.megacrit.cardcrawl.screens.mainMenu.MenuButton;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.map.*;
import com.megacrit.cardcrawl.core.*;
import basemod.ReflectionHacks;

import java.lang.reflect.Field;

public class NewMenuButtons
{
    @SpireEnum
    static MenuButton.ClickResult VERSUS;

    @SpireEnum
    static MenuButton.ClickResult COOP;

    static public NewGameScreen newGameScreen = null;
    static public MainLobbyScreen lobbyScreen = null;

    @SpirePatch(clz=MenuButton.class, method="setLabel")
    public static class SetLabel {
        public static void Postfix(MenuButton __instance)
        {
            try {
                if (__instance.result == VERSUS) {
                    Field f_label = MenuButton.class.getDeclaredField("label");
                    f_label.setAccessible(true);
                    f_label.set(__instance, CardCrawlGame.languagePack.getUIString("MainMenu").TEXT[0]);
                }
                if (__instance.result == COOP) {
                    Field f_label = MenuButton.class.getDeclaredField("label");
                    f_label.setAccessible(true);
                    f_label.set(__instance, CardCrawlGame.languagePack.getUIString("MainMenu").TEXT[1]);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @SpirePatch(clz=MenuButton.class, method="buttonEffect")
    public static class ButtonEffect {
        public static void Postfix(MenuButton __instance)
        {
            NetworkHelper.embarked = false;

            if (__instance.result == VERSUS) { 
                TogetherManager.gameMode = TogetherManager.mode.Versus; 
                RichPresencePatch.setRP(CardCrawlGame.languagePack.getUIString("RichPresence").TEXT[0]);
                ReflectionHacks.setPrivateStaticFinal(Legend.class, "Y", 320.F * Settings.yScale); 
                NewMenuButtons.openLobby(); 
            }
            if (__instance.result == COOP)   { 
                TogetherManager.gameMode = TogetherManager.mode.Coop;   
                RichPresencePatch.setRP(CardCrawlGame.languagePack.getUIString("RichPresence").TEXT[1]);
                ReflectionHacks.setPrivateStaticFinal(Legend.class, "Y", 600.F * Settings.yScale); 
                NewMenuButtons.openLobby(); 
            }
        }
    }

    public static void openNewGame() {
        newGameScreen = new NewGameScreen();
        newGameScreen.open();
    }

    public static void joinNewGame() {
        newGameScreen = new NewGameScreen();
        newGameScreen.join();
    }

    public static void openLobby() {
        TogetherManager.clearMultiplayerData();

        lobbyScreen = new MainLobbyScreen();
        lobbyScreen.open();            
    }
}