package chronospeed;

import chronocustoms.lobby.MainLobbyScreen;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.megacrit.cardcrawl.screens.mainMenu.MenuButton;

import java.lang.reflect.Field;

public class NewMenuButtons
{
    @SpireEnum
    static MenuButton.ClickResult VERSUS;
    static NewGameScreen newGameScreen = null;
    static MainLobbyScreen lobbyScreen = null;

    @SpireEnum
    static MenuButton.ClickResult COOP;
    // static CoopScreen coopScreen = null;

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
                    //SteamAPICall result = NetworkHelper.matcher.requestLobbyList();
                    if (lobbyScreen == null)
                    {
                        lobbyScreen = new MainLobbyScreen(ChronoCustoms.mode.Coop);
                    }
                    lobbyScreen.open();
                // }
                // coopScreen.open();
            }
        }
    }
}