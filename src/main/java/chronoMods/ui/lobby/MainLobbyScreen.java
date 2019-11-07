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
    public int page = 0;

    // Buttons
    public MenuCancelButton button = new MenuCancelButton();
    public GridSelectConfirmButton confirmButton = new GridSelectConfirmButton("New Lobby");
    public JoinButton joinButton = new JoinButton("Join");

    public TogetherManager.mode mode;

    public MainLobbyScreen(TogetherManager.mode mode) {

        this.mode = mode;
        gameList = new ArrayList<>();

    }

    public void open() {
        // Screen Swap
        CardCrawlGame.mainMenuScreen.darken();
        CardCrawlGame.mainMenuScreen.screen = Enum.MAIN_LOBBY;

        // Buttons
        button.show(PatchNotesScreen.TEXT[0]);
        this.confirmButton.show();
        this.confirmButton.isDisabled = false;

        joinButton.show();
        joinButton.move(BASE_X, BASE_Y - (6 * 75f * Settings.scale) - 32f);

        // Add items to the list
        refreshGameList();
        populateDummyList();
    }

    public void refreshGameList() {
        gameList.clear();

        for (SteamLobby l : NetworkHelper.getLobbies()) {
            gameList.add(new MainLobbyInfo(l));
        }
    }

    /// Creates entries for the lobby just for testing purposes.
    public void populateDummyList()
    {
        SteamLobby temp = new SteamLobby(null);
        temp.name = "Hello Spire";
        temp.ascension = "1";
        temp.character = "Rocket";
        MainLobbyInfo tempb = new MainLobbyInfo(temp);
        gameList.add(tempb);

        temp = new SteamLobby(null);
        temp.name = "Chrono's Cool Club";
        temp.ascension = "20";
        temp.character = "Chronometrics";
        tempb = new MainLobbyInfo(temp);
        gameList.add(tempb);

        temp = new SteamLobby(null);
        temp.name = "Skyla's Nom Nom Palace";
        temp.ascension = "15";
        temp.character = "Skylawinters";
        tempb = new MainLobbyInfo(temp);
        gameList.add(tempb);

        temp = new SteamLobby(null);
        temp.name = "Hello Spire";
        temp.ascension = "1";
        temp.character = "Rocket";
        tempb = new MainLobbyInfo(temp);
        gameList.add(tempb);

        temp = new SteamLobby(null);
        temp.name = "Naps and Snax";
        temp.ascension = "10";
        temp.character = "Mieu";
        tempb = new MainLobbyInfo(temp);
        gameList.add(tempb);

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

        // Lobby list
        for (MainLobbyInfo lobby : gameList) {
            lobby.update();

            // Lobby selected
            if (lobby.justSelected) {
                lobby.justSelected = false;
            }
        }

        // Create New Lobby Button Clicked
        this.confirmButton.update();
        if ((this.confirmButton.hb.clicked) || (CInputActionSet.proceed.isJustPressed()))
        {
            this.confirmButton.hb.clicked = false;
        }

        // Join Button Clicked
        joinButton.update();
        if ((this.joinButton.hb.clicked) || (CInputActionSet.proceed.isJustPressed()))
        {
            this.joinButton.hb.clicked = false;
        }

        InputHelper.justClickedLeft = false;
    }

    public void deselect() {
        for (MainLobbyInfo lobby : gameList) {
            lobby.selected = false;
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
        FontHelper.renderFontCentered(sb, FontHelper.SCP_cardTitleFont_small, "Gamelist",
            Settings.WIDTH / 2.0f,
            Settings.HEIGHT - 70.0f * Settings.scale,
            Settings.GOLD_COLOR);

        this.button.render(sb);
        this.confirmButton.render(sb);

        renderHeaders(sb);
        renderPlayerPanel(sb);

        // Iterates over available lobbies per page, and renders the correct amount up to 20
        for (int i = 0; i < 20; i++) {
            if (i + page*20 < gameList.size()) {
                gameList.get(i+ page*20).render(sb, i);
            }
        }

        this.joinButton.render(sb);
    }

    float BASE_X = Settings.WIDTH / 4.0F;
    float BASE_Y = Settings.HEIGHT - 275f * Settings.scale;
    private static final Color EMPTY_PLAYER_SLOT = new Color(1f, 1f, 1f, 0.3f);

    public void renderPlayerPanel(SpriteBatch sb) {
        // BG Panel        
        sb.setColor(Color.WHITE.cpy());
        sb.draw(
            ImageMaster.REWARD_SCREEN_SHEET,
            BASE_X - 612 / 2f,
            BASE_Y - 218f * Settings.scale - 716 / 2f,
            612 / 2f, 716 / 2f,
            612, 716,
            Settings.scale, Settings.scale,
            0f,
            0, 0, 612, 716,
            false, false);

        // Title text

        sb.draw(ImageMaster.VICTORY_BANNER, 
            BASE_X - 556.0F * Settings.scale, 
            BASE_Y - 24.0F * Settings.scale, 
            556.0F, 119.0F, 1112.0F, 238.0F, Settings.scale * 0.8f, Settings.scale, 0.0F, 0, 0, 1112, 238, false, false);
      
        FontHelper.renderFontCentered(sb, FontHelper.bannerFont, "Players", 
            Settings.WIDTH / 4.0F, 
            BASE_Y + 96.0F * Settings.scale + 22.0F * Settings.scale, 
            new Color(0.9F, 0.9F, 0.9F, 1.0F), 1.0f);

        // Reward Positioning
        for (int i = 0; i < 6; i++) {
            if (i < TogetherManager.players.size()) {
                // Background
                sb.draw(
                    ImageMaster.REWARD_SCREEN_ITEM,
                    BASE_X - 464 / 2f,
                    BASE_Y - (i * 75f * Settings.scale) - 98 / 2f,
                    464 / 2f, 98 / 2f,
                    464, 98,
                    Settings.scale,Settings.scale*0.75f,
                    0f,
                    0, 0, 464, 98,
                    false, false);

                // Player Portrait
                sb.draw(
                    TogetherManager.players.get(i).portraitImg,
                    BASE_X - 64 / 2f - 164f * Settings.scale,
                    BASE_Y - (i * 75f * Settings.scale) - 64 / 2f - 2f * Settings.scale,
                    64 / 2f,
                    64 / 2f,
                    64,
                    64,
                    Settings.scale,
                    Settings.scale,
                    0f,
                    0,
                    0,
                    64,
                    64,
                    false,
                    false);

                // Portrait Frame
                sb.draw(TogetherManager.portraitFrames.get(0), 
                    BASE_X - 64 / 2f - 164f * Settings.scale    - 184.0F * Settings.scale, 
                    BASE_Y - (i * 75f * Settings.scale) - 64 / 2f - 2f * Settings.scale    - 104.0F * Settings.scale, 
                    0.0F, 0.0F, 432.0F, 243.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 1920, 1080, false, false);

                // Player Name
                Color color = Settings.CREAM_COLOR;

                FontHelper.renderSmartText(
                    sb,
                    FontHelper.cardDescFont_N,
                    TogetherManager.players.get(i).userName,
                    BASE_X - 112f * Settings.scale,
                    BASE_Y - (i * 75f * Settings.scale) + 5f * Settings.scale,
                    1000f * Settings.scale,
                    0f,
                    color);
            } else {
                sb.setColor(EMPTY_PLAYER_SLOT);
                // Background
                sb.draw(
                    ImageMaster.REWARD_SCREEN_ITEM,
                    BASE_X - 464 / 2f,
                    BASE_Y - (i * 75f * Settings.scale) - 98 / 2f,
                    464 / 2f, 98 / 2f,
                    464, 98,
                    Settings.scale,Settings.scale*0.75f,
                    0f,
                    0, 0, 464, 98,
                    false, false);
                sb.setColor(Color.WHITE);
            }
        }

    }

    public void renderHeaders(SpriteBatch sb) {
        Color creamColor = new Color(1.0F, 0.965F, 0.886F, 1.0F);
        float RANK_X = 1000.0F * Settings.scale;
        float NAME_X = 1160.0F * Settings.scale;
        float SCORE_X = 1500.0F * Settings.scale;
        float LINE_THICKNESS = 4.0F * Settings.scale;

        FontHelper.renderFontLeftTopAligned(sb, FontHelper.eventBodyText, "Ascension", RANK_X, 920.0F * Settings.scale, creamColor);

        FontHelper.renderFontLeftTopAligned(sb, FontHelper.eventBodyText, "Owner", NAME_X, 920.0F * Settings.scale, creamColor);

        FontHelper.renderFontLeftTopAligned(sb, FontHelper.eventBodyText, "Members", SCORE_X, 920.0F * Settings.scale, creamColor);

        // Weird separator lines
/*        sb.setColor(creamColor);
        sb.draw(ImageMaster.WHITE_SQUARE_IMG, 1138.0F * Settings.scale, 168.0F * Settings.scale, LINE_THICKNESS, 692.0F * Settings.scale);

        sb.draw(ImageMaster.WHITE_SQUARE_IMG, 1480.0F * Settings.scale, 168.0F * Settings.scale, LINE_THICKNESS, 692.0F * Settings.scale);

        sb.setColor(new Color(0.0F, 0.0F, 0.0F, 0.75F));
        sb.draw(ImageMaster.WHITE_SQUARE_IMG, 982.0F * Settings.scale, 814.0F * Settings.scale, 630.0F * Settings.scale, 16.0F * Settings.scale);

        sb.setColor(creamColor);
            sb.draw(ImageMaster.WHITE_SQUARE_IMG, 982.0F * Settings.scale, 820.0F * Settings.scale, 630.0F * Settings.scale, LINE_THICKNESS);
*/    }

    private void drawRect(SpriteBatch sb, float x, float y, float width, float height, float thickness) {
        sb.draw(ImageMaster.WHITE_SQUARE_IMG, x, y, width, thickness);
        sb.draw(ImageMaster.WHITE_SQUARE_IMG, x, y, thickness, height);
        sb.draw(ImageMaster.WHITE_SQUARE_IMG, x, y+height-thickness, width, thickness);
        sb.draw(ImageMaster.WHITE_SQUARE_IMG, x+width-thickness, y, thickness, height);
    }
}