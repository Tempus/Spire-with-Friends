package chronoMods.ui.lobby;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen;
import chronoMods.*;
import chronoMods.steam.*;
import chronoMods.ui.deathScreen.*;
import chronoMods.ui.hud.*;
import chronoMods.ui.lobby.*;
import chronoMods.ui.mainMenu.*;
import chronoMods.utilities.*;

public class NewScreenUpdateRender
{
    @SpirePatch(
        clz=MainMenuScreen.class,
        method="update"
    )
    public static class Update
    {
        public static void Postfix(MainMenuScreen __instance)
        {
            if (__instance.screen == NewGameScreen.Enum.CREATEMULTIPLAYERGAME) {
                NewMenuButtons.newGameScreen.update();
            }

            if (__instance.screen == MainLobbyScreen.Enum.MAIN_LOBBY) {
                NewMenuButtons.lobbyScreen.update();
            }
        }
    }

    @SpirePatch(
        clz=MainMenuScreen.class,
        method="render"
    )
    public static class Render
    {
        public static void Postfix(MainMenuScreen __instance, SpriteBatch sb)
        {
            if (__instance.screen == NewGameScreen.Enum.CREATEMULTIPLAYERGAME) {
                NewMenuButtons.newGameScreen.render(sb);
            }
            if (__instance.screen == MainLobbyScreen.Enum.MAIN_LOBBY) {
                NewMenuButtons.lobbyScreen.render(sb);
            }
        }
    }
}