package chronocustoms.lobby;

import chronospeed.ChronoCustoms;
import chronospeed.NetworkHelper;
import chronospeed.NewMenuButtons;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.SeedHelper;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.screens.charSelect.CharacterSelectScreen;
import com.megacrit.cardcrawl.screens.custom.CustomModeCharacterButton;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen;
import com.megacrit.cardcrawl.screens.mainMenu.MenuCancelButton;
import com.megacrit.cardcrawl.screens.mainMenu.PatchNotesScreen;
import com.megacrit.cardcrawl.ui.buttons.GridSelectConfirmButton;
import com.megacrit.cardcrawl.ui.panels.SeedPanel;

import java.util.ArrayList;

public class LobbyScreen
{
    public static class Enum
    {
        @SpireEnum
        public static MainMenuScreen.CurScreen MAIN_LOBBY;
    }

    // UI strings
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString("CustomModeScreen");
    public static final String[] TEXT = uiStrings.TEXT;

    // Buttons
    public MenuCancelButton button = new MenuCancelButton();
    public GridSelectConfirmButton confirmButton = new GridSelectConfirmButton(CharacterSelectScreen.TEXT[1]);


    public ChronoCustoms.mode mode;

    public LobbyScreen(ChronoCustoms.mode mode) {

        this.mode = mode;

    }

    public void open() {
        // Screen Swap
        CardCrawlGame.mainMenuScreen.darken();
        CardCrawlGame.mainMenuScreen.screen = Enum.MAIN_LOBBY;

        // Buttons
        button.show(PatchNotesScreen.TEXT[0]);
        this.confirmButton.show();
        this.confirmButton.isDisabled = false;

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

        InputHelper.justClickedLeft = false;
    }

    private void updateEmbarkButton()
    {
        this.confirmButton.update();
        if ((this.confirmButton.hb.clicked) || (CInputActionSet.proceed.isJustPressed()))
        {
            this.confirmButton.hb.clicked = false;

            CardCrawlGame.mainMenuScreen.isFadingOut = true;
            CardCrawlGame.mainMenuScreen.fadeOutMusic();
            Settings.isTrial = true;
            Settings.isDailyRun = false;
            Settings.isEndless = false;
            // finalActAvailable = true;

            ChronoCustoms.gameMode = mode;
            //NetworkHelper.sendData(NetworkHelper.dataType.Start);
            // NetworkHelper.matcher.leaveLobby();
        }
    }

    private void playClickStartSound()
    {
      CardCrawlGame.sound.playA("UI_CLICK_1", -0.1F);
    }
    
    private void playClickFinishSound()
    {
      CardCrawlGame.sound.playA("UI_CLICK_1", -0.1F);
    }
    
    private void playHoverSound()
    {
      CardCrawlGame.sound.playV("UI_HOVER", 0.75F);
    }

    public void render(SpriteBatch sb) {
        FontHelper.renderFontCentered(sb, FontHelper.SCP_cardTitleFont_small, "Test Screen",
            Settings.WIDTH / 2.0f,
            Settings.HEIGHT - 70.0f * Settings.scale,
            Settings.GOLD_COLOR);

        this.button.render(sb);
        this.confirmButton.render(sb);

    }

    private void drawRect(SpriteBatch sb, float x, float y, float width, float height, float thickness) {
        sb.draw(ImageMaster.WHITE_SQUARE_IMG, x, y, width, thickness);
        sb.draw(ImageMaster.WHITE_SQUARE_IMG, x, y, thickness, height);
        sb.draw(ImageMaster.WHITE_SQUARE_IMG, x, y+height-thickness, width, thickness);
        sb.draw(ImageMaster.WHITE_SQUARE_IMG, x+width-thickness, y, thickness, height);
    }
}