package chronoMods.ui.lobby;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen;
import com.megacrit.cardcrawl.scenes.*;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.*;
import chronoMods.*;
import chronoMods.coop.drawable.*;
import chronoMods.steam.*;
import chronoMods.ui.deathScreen.*;
import chronoMods.ui.hud.*;
import chronoMods.ui.lobby.*;
import chronoMods.ui.mainMenu.*;
import chronoMods.utilities.*;
import java.awt.Desktop;
import java.net.*;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import basemod.*;

public class NewScreenUpdateRender
{
    static public Button patreonButton = new Button(930.0F * Settings.xScale, Settings.HEIGHT/2 - 330f * Settings.yScale, "", ImageMaster.loadImage("chrono/images/patreon.png"));
    static public Button discordButton = new Button(930.0F * Settings.xScale, Settings.HEIGHT/2 - 275f * Settings.yScale, "", ImageMaster.loadImage("chrono/images/discord.png"));

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
                    openWebpage("https://discord.gg/DMTbntH");
                    discordButton.hb.clicked = false;
                }
            }
            
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
        }
    }
}