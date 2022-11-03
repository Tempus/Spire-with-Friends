package chronoMods.ui.lobby;

import basemod.ReflectionHacks;
import chronoMods.coop.drawable.Button;
import chronoMods.ui.mainMenu.NewMenuButtons;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.scenes.TitleBackground;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen;

import java.awt.*;
import java.net.URL;

public class NewScreenUpdateRender
{
    static public Button patreonButton = new Button(930.0F * Settings.xScale, Settings.HEIGHT/2 - 330f * Settings.yScale, "", ImageMaster.loadImage("chrono/images/patreon.png"));
    static public Button discordButton = new Button(930.0F * Settings.xScale, Settings.HEIGHT/2 - 275f * Settings.yScale, "", ImageMaster.loadImage("chrono/images/discord.png"));
    static public boolean joinFlag = false;

    public static void openWebpage(String urlString) {
        try {
            Desktop.getDesktop().browse(new URL(urlString).toURI());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SpirePatch(
        clz=MainMenuScreen.class,
        method="update"
    )
    public static class Update
    {
        public static void Postfix(MainMenuScreen __instance)
        {
            if (__instance.screen == MainMenuScreen.CurScreen.MAIN_MENU) {
                patreonButton.update();
                discordButton.update();

                float a = ReflectionHacks.getPrivate(__instance.bg, TitleBackground.class, "logoAlpha");

                patreonButton.alpha = a;
                discordButton.alpha = a;

                patreonButton.DRAW_Y = Settings.HEIGHT/2 - 330f * Settings.yScale -70.0F * Settings.scale * __instance.bg.slider;
                discordButton.DRAW_Y = Settings.HEIGHT/2 - 275f * Settings.yScale -70.0F * Settings.scale * __instance.bg.slider;

                if (patreonButton.hb.clicked == true) {
                    openWebpage("https://www.patreon.com/chronometrics");
                    patreonButton.hb.clicked = false;
                }

                if (discordButton.hb.clicked == true) {
                    openWebpage("https://discord.gg/DFvbFpYt6b");
                    discordButton.hb.clicked = false;
                }
            }
            
            if (__instance.screen == NewGameScreen.Enum.CREATEMULTIPLAYERGAME) {
                NewMenuButtons.newGameScreen.update();
            }

            if (__instance.screen == MainLobbyScreen.Enum.MAIN_LOBBY) {
                NewMenuButtons.lobbyScreen.update();
            }

            if (__instance.screen == CustomModePopOver.Enum.MPCUSTOMMODE) {
                NewMenuButtons.customScreen.update();
            }

            if (joinFlag) {
                joinFlag = false;
                NewMenuButtons.joinNewGame();
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
            if (__instance.screen == MainMenuScreen.CurScreen.MAIN_MENU) {
                patreonButton.render(sb);
                discordButton.render(sb);
            }

            if (__instance.screen == NewGameScreen.Enum.CREATEMULTIPLAYERGAME) {
                NewMenuButtons.newGameScreen.render(sb);
            }
            if (__instance.screen == MainLobbyScreen.Enum.MAIN_LOBBY) {
                NewMenuButtons.lobbyScreen.render(sb);
            }
            if (__instance.screen == CustomModePopOver.Enum.MPCUSTOMMODE) {
                NewMenuButtons.customScreen.render(sb);
            }
        }
    }
}