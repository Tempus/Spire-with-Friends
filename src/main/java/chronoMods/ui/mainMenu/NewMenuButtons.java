package chronoMods.ui.mainMenu;

import chronoMods.*;
import chronoMods.steam.*;
import chronoMods.ui.deathScreen.*;
import chronoMods.ui.hud.*;
import chronoMods.ui.lobby.*;
import chronoMods.ui.mainMenu.*;
import chronoMods.utilities.*;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.megacrit.cardcrawl.screens.mainMenu.MenuButton;

import java.lang.reflect.Field;

public class NewMenuButtons
{
    @SpireEnum
    static MenuButton.ClickResult VERSUS;

    @SpireEnum
    static MenuButton.ClickResult COOP;

    static public NewGameScreen newGameScreen = null;
    static public MainLobbyScreen lobbyScreen = null;

    @SpirePatch(
        clz=MenuButton.class,
        method="setLabel"
    )
    public static class SetLabel
    {
        public static void Postfix(MenuButton __instance)
        {
            try {
                if (__instance.result == VERSUS) {
                    Field f_label = MenuButton.class.getDeclaredField("label");
                    f_label.setAccessible(true);
                    f_label.set(__instance, "Versus");
                }
                if (__instance.result == COOP) {
                    Field f_label = MenuButton.class.getDeclaredField("label");
                    f_label.setAccessible(true);
                    f_label.set(__instance, "Coop");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @SpirePatch(
        clz=MenuButton.class,
        method="buttonEffect"
    )
    public static class ButtonEffect
    {
        public static void Postfix(MenuButton __instance)
        {
            if (__instance.result == VERSUS) {
                if (newGameScreen == null) {
                     newGameScreen = new NewGameScreen();
                }
                newGameScreen.open();
            }

            if (__instance.result == COOP) {
                // if (coopScreen == null) {
                    if (lobbyScreen == null)
                    {
                        lobbyScreen = new MainLobbyScreen(TogetherManager.mode.Coop);
                    }
                    lobbyScreen.open();
                // }
                // coopScreen.open();
            }
        }
    }
}