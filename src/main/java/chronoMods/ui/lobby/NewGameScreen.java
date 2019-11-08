package chronoMods.ui.lobby;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen;
import com.megacrit.cardcrawl.screens.mainMenu.MenuCancelButton;
import com.megacrit.cardcrawl.screens.mainMenu.PatchNotesScreen;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;

import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.SeedHelper;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.screens.charSelect.CharacterSelectScreen;
import com.megacrit.cardcrawl.screens.custom.CustomModeCharacterButton;
import com.megacrit.cardcrawl.ui.buttons.GridSelectConfirmButton;
import com.megacrit.cardcrawl.ui.panels.SeedPanel;

import java.util.ArrayList;
import java.util.Map;

import chronoMods.*;
import chronoMods.steam.*;
import chronoMods.ui.deathScreen.*;
import chronoMods.ui.hud.*;
import chronoMods.ui.lobby.*;
import chronoMods.ui.mainMenu.*;
import chronoMods.utilities.*;

import com.codedisaster.steamworks.*;
import com.megacrit.cardcrawl.integrations.steam.SteamIntegration;
import basemod.ReflectionHacks;
import com.codedisaster.steamworks.SteamMatchmaking;

public class NewGameScreen
{
    public static class Enum
    {
        @SpireEnum
        public static MainMenuScreen.CurScreen CREATEMULTIPLAYERGAME;
    }

    // UI strings
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString("CustomModeScreen");
    public static final String[] TEXT = uiStrings.TEXT;

    // Settings.HEIGHT - 200.0F * Settings.scale
    // x = 300.0F * Settings.scale;

    // Buttons
    public MenuCancelButton button = new MenuCancelButton();
    public GridSelectConfirmButton confirmButton = new GridSelectConfirmButton(CharacterSelectScreen.TEXT[1]);

    // Characters
    public CharacterSelectWidget characterSelectWidget = new CharacterSelectWidget();

    // Ascension Selection
    public AscensionSelectWidget ascensionSelectWidget = new AscensionSelectWidget();

    // Seed Selection
    public SeedSelectWidget seedSelectWidget = new SeedSelectWidget();


    public NewGameScreen() {
        characterSelectWidget.move(1400f, 700f);
        ascensionSelectWidget.move(1400f, 575f);
        seedSelectWidget.move(1400f, 450f);
    }

    public void open() {
        // Screen Swap
        CardCrawlGame.mainMenuScreen.darken();
        CardCrawlGame.mainMenuScreen.screen = Enum.CREATEMULTIPLAYERGAME;

        // Buttons
        button.show(PatchNotesScreen.TEXT[0]);
        this.confirmButton.show();
        this.confirmButton.isDisabled = false;

        // Seed
        Settings.seed = null;
        Settings.specialSeed = null;

        // Steam Stuff
        NetworkHelper.createLobby();
    }

    // Like open, but we'll make things look different, and we'll join an existing lobby instead of making a new one
    public void join() {
      open();
    }

    public void update() {
        // Return to the Main Menu
        button.update();
        if (button.hb.clicked || InputHelper.pressedEscape) {
            button.hb.clicked = false;
            InputHelper.pressedEscape = false;
            CardCrawlGame.mainMenuScreen.screen = MainMenuScreen.CurScreen.MAIN_MENU;
            button.hide();
            CardCrawlGame.mainMenuScreen.lighten();
        }

        // if ()
        // {
            characterSelectWidget.update();
            ascensionSelectWidget.update();
            seedSelectWidget.update();
        // }

        updateEmbarkButton();
        seedSelectWidget.currentSeed = SeedHelper.getUserFacingSeedString();

        InputHelper.justClickedLeft = false;
    }

    private void updateEmbarkButton()
    {
        this.confirmButton.update();
        if ((this.confirmButton.hb.clicked) || (CInputActionSet.proceed.isJustPressed()))
        {
            this.confirmButton.hb.clicked = false;
            for (CustomModeCharacterButton b : characterSelectWidget.options) {
              if (b.selected)
              {
                CardCrawlGame.chosenCharacter = b.c.chosenClass;
                break;
              }
            }
            CardCrawlGame.mainMenuScreen.isFadingOut = true;
            CardCrawlGame.mainMenuScreen.fadeOutMusic();
            Settings.isTrial = true;
            Settings.isDailyRun = false;
            Settings.isEndless = false;
            // finalActAvailable = true;
            
            AbstractDungeon.isAscensionMode = ascensionSelectWidget.isAscensionMode;
            if (!ascensionSelectWidget.isAscensionMode) {
              AbstractDungeon.ascensionLevel = 0;
            } else {
              AbstractDungeon.ascensionLevel = ascensionSelectWidget.ascensionLevel;
            }
            if (seedSelectWidget.currentSeed.isEmpty())
            {
              long sourceTime = System.nanoTime();
              Random rng = new Random(Long.valueOf(sourceTime));
              Settings.seed = Long.valueOf(SeedHelper.generateUnoffensiveSeed(rng));
            }
            AbstractDungeon.generateSeeds();

            TogetherManager.gameMode = TogetherManager.mode.Versus;
            NetworkHelper.sendData(NetworkHelper.dataType.Start);
            // NetworkHelper.matcher.leaveLobby();
        }
    }


    public void render(SpriteBatch sb) {
        FontHelper.renderFontCentered(sb, FontHelper.SCP_cardTitleFont_small, "Test Screen",
            Settings.WIDTH / 2.0f,
            Settings.HEIGHT - 70.0f * Settings.scale,
            Settings.GOLD_COLOR);

        this.button.render(sb);
        this.confirmButton.render(sb);

        characterSelectWidget.render(sb);
        ascensionSelectWidget.render(sb);
        seedSelectWidget.render(sb);
    }
}