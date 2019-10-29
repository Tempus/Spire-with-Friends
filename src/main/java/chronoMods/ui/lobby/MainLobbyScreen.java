package chronoMods.ui.lobby;

import chronoMods.*;
import chronoMods.steam.*;
import chronoMods.ui.deathScreen.*;
import chronoMods.ui.hud.*;
import chronoMods.ui.lobby.*;
import chronoMods.ui.mainMenu.*;
import chronoMods.utilities.*;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.screens.charSelect.CharacterSelectScreen;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen;
import com.megacrit.cardcrawl.screens.mainMenu.MenuCancelButton;
import com.megacrit.cardcrawl.screens.mainMenu.PatchNotesScreen;
import com.megacrit.cardcrawl.ui.buttons.GridSelectConfirmButton;

import java.util.ArrayList;

public class MainLobbyScreen
{
    public static class Enum
    {
        @SpireEnum
        public static MainMenuScreen.CurScreen MAIN_LOBBY;
    }

    //private static final CustomStrings customString = TogetherManager.CustomStringsMap.get("Lobby");
    //public static final String[] TEXT = customString.STRINGS;

    public ArrayList<MainLobbyInfo> gameList;


    // Buttons
    public MenuCancelButton button = new MenuCancelButton();
    public GridSelectConfirmButton confirmButton = new GridSelectConfirmButton(CharacterSelectScreen.TEXT[1]);


    public TogetherManager.mode mode;

    public MainLobbyScreen(TogetherManager.mode mode) {

        this.mode = mode;
        gameList = new ArrayList<>();

        populateDummyList();
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

    /// Creates entries for the lobby just for testing purposes.
    public void populateDummyList()
    {
        MainLobbyInfo temp = new MainLobbyInfo();
        temp.roomName = "Hello Spire";
        temp.ascension = 1;
        temp.host = "Rocket";
        gameList.add(temp);

        temp = new MainLobbyInfo();
        temp.roomName = "Chrono's Cool Club";
        temp.ascension = 20;
        temp.host = "Chronometrics";
        gameList.add(temp);

        temp = new MainLobbyInfo();
        temp.roomName = "Skyla's Nom Nom Palace";
        temp.ascension = 15;
        temp.host = "Skylawinters";
        gameList.add(temp);

        temp = new MainLobbyInfo();
        temp.roomName = "Hello Spire";
        temp.ascension = 1;
        temp.host = "Rocket";
        gameList.add(temp);

        temp = new MainLobbyInfo();
        temp.roomName = "Naps and Snax";
        temp.ascension = 10;
        temp.host = "Mieu";
        gameList.add(temp);

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

            TogetherManager.gameMode = mode;
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

        renderTitles(sb);
        renderHeaders(sb);
    }

    public void renderTitles(SpriteBatch sb)
    {
        //delete after implementing custom strings
        String[] TEXT = {"First", "Second", "Third"};

        FontHelper.renderFontLeftTopAligned(sb, FontHelper.charTitleFont, TEXT[0], 240.0F * Settings.scale, 920.0F * Settings.scale, Settings.GOLD_COLOR);
        FontHelper.renderFontLeftTopAligned(sb, FontHelper.eventBodyText, TEXT[1], 280.0F * Settings.scale, 860.0F * Settings.scale, Settings.CREAM_COLOR);
        FontHelper.renderFontLeftTopAligned(sb, FontHelper.eventBodyText, TEXT[2], 280.0F * Settings.scale, 680.0F * Settings.scale, Settings.CREAM_COLOR);
    }

    public void renderHeaders(SpriteBatch sb) {
        Color creamColor = new Color(1.0F, 0.965F, 0.886F, 1.0F);
        float RANK_X = 1000.0F * Settings.scale;
        float NAME_X = 1160.0F * Settings.scale;
        float SCORE_X = 1500.0F * Settings.scale;
        float LINE_THICKNESS = 4.0F * Settings.scale;

        FontHelper.renderFontLeftTopAligned(sb, FontHelper.charTitleFont, "test1", 960.0F * Settings.scale, 920.0F * Settings.scale, new Color(0.937F, 0.784F, 0.317F, 1.0F));

        FontHelper.renderFontLeftTopAligned(sb, FontHelper.eventBodyText, "test2", RANK_X, 860.0F * Settings.scale, creamColor);

        FontHelper.renderFontLeftTopAligned(sb, FontHelper.eventBodyText, "test3", NAME_X, 860.0F * Settings.scale, creamColor);

        FontHelper.renderFontLeftTopAligned(sb, FontHelper.eventBodyText, "test4", SCORE_X, 860.0F * Settings.scale, creamColor);

        sb.setColor(creamColor);
        sb.draw(ImageMaster.WHITE_SQUARE_IMG, 1138.0F * Settings.scale, 168.0F * Settings.scale, LINE_THICKNESS, 692.0F * Settings.scale);

        sb.draw(ImageMaster.WHITE_SQUARE_IMG, 1480.0F * Settings.scale, 168.0F * Settings.scale, LINE_THICKNESS, 692.0F * Settings.scale);

        sb.setColor(new Color(0.0F, 0.0F, 0.0F, 0.75F));
        sb.draw(ImageMaster.WHITE_SQUARE_IMG, 982.0F * Settings.scale, 814.0F * Settings.scale, 630.0F * Settings.scale, 16.0F * Settings.scale);

        sb.setColor(creamColor);
            sb.draw(ImageMaster.WHITE_SQUARE_IMG, 982.0F * Settings.scale, 820.0F * Settings.scale, 630.0F * Settings.scale, LINE_THICKNESS);
        }

    private void drawRect(SpriteBatch sb, float x, float y, float width, float height, float thickness) {
        sb.draw(ImageMaster.WHITE_SQUARE_IMG, x, y, width, thickness);
        sb.draw(ImageMaster.WHITE_SQUARE_IMG, x, y, thickness, height);
        sb.draw(ImageMaster.WHITE_SQUARE_IMG, x, y+height-thickness, width, thickness);
        sb.draw(ImageMaster.WHITE_SQUARE_IMG, x+width-thickness, y, thickness, height);
    }
}