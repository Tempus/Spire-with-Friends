package chronoMods.ui.lobby;

import chronoMods.*;
import chronoMods.steam.*;
import chronoMods.ui.deathScreen.*;
import chronoMods.ui.hud.*;
import chronoMods.ui.lobby.*;
import chronoMods.ui.mainMenu.*;
import chronoMods.utilities.*;

import com.badlogic.gdx.Gdx;
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

    public TogetherManager.mode mode;
    public MainLobbyInfo selectedLobby;

    // Player Panel
    public PlayerListWidget playerList = new PlayerListWidget("Join");

    // Refresh Network info timer
    public float refresh = 10f;
    public float refreshPeriod = 10f;

    public MainLobbyScreen() {
        gameList = new ArrayList();
        playerList.move(Settings.WIDTH / 4.0F, Settings.HEIGHT - 275f * Settings.scale);
    }

    public void open() {
        this.mode = mode;

        // Screen Swap
        CardCrawlGame.mainMenuScreen.darken();
        CardCrawlGame.mainMenuScreen.screen = Enum.MAIN_LOBBY;

        // Buttons
        button.show(PatchNotesScreen.TEXT[0]);
        this.confirmButton.show();
        this.confirmButton.isDisabled = false;

        // Add items to the list
        refreshGameList();
        playerList.players.clear();
        playerList.joinButton.isDisabled = true;
    }

    public void refreshGameList() {
        NetworkHelper.getLobbies();
    }

    public void createFreshGameList() {
        gameList.clear();

        for (SteamLobby l : NetworkHelper.steamLobbies) {
            gameList.add(new MainLobbyInfo(l));
        }
    }

    public void update() {
        // Return to the Main Menu
        button.update();
        if (button.hb.clicked || InputHelper.pressedEscape) {
            button.hb.clicked = false;
            InputHelper.pressedEscape = false;
            backToMenu();
        }

        // Lobby list
        for (MainLobbyInfo lobby : gameList) {
            lobby.update();

            // Lobby selected
            if (lobby.justSelected) {
                lobby.justSelected = false;
                playerList.joinButton.isDisabled = false;

                playerList.setPlayers(lobby.info.getLobbyMembers());
                selectedLobby = lobby;
            }
        }

        // Create New Lobby Button Clicked
        this.confirmButton.update();
        if ((this.confirmButton.hb.clicked) || (CInputActionSet.proceed.isJustPressed()))
        {
            this.confirmButton.hb.clicked = false;
            NewMenuButtons.openNewGame();
        }

        // Join Button Clicked
        playerList.update();
        if (playerList.clicked) {
            NetworkHelper.matcher.joinLobby(selectedLobby.info.steamID);
            NewMenuButtons.joinNewGame();
            playerList.clicked = false;
        }

        InputHelper.justClickedLeft = false;

        refresh -= Gdx.graphics.getDeltaTime();
        if (refresh < 0f) {
          refreshGameList();
          refresh = refreshPeriod;
        }
    }

    public void deselect() {
        for (MainLobbyInfo lobby : gameList) {
            lobby.selected = false;
        }
    }

    public void backToMenu() {
        TogetherManager.gameMode = TogetherManager.mode.Normal;
        CardCrawlGame.mainMenuScreen.screen = MainMenuScreen.CurScreen.MAIN_MENU;
        CardCrawlGame.mainMenuScreen.lighten();
        button.hide();
        deselect();
        NetworkHelper.leaveLobby();
    }

    public void render(SpriteBatch sb) {
        FontHelper.renderFontCentered(sb, FontHelper.SCP_cardTitleFont_small, "Gamelist",
            Settings.WIDTH / 2.0f,
            Settings.HEIGHT - 70.0f * Settings.scale,
            Settings.GOLD_COLOR);

        this.button.render(sb);
        this.confirmButton.render(sb);

        renderHeaders(sb);
        playerList.render(sb);

        // Iterates over available lobbies per page, and renders the correct amount up to 20
        for (int i = 0; i < 20; i++) {
            if (i + page*20 < gameList.size()) {
                gameList.get(i+ page*20).render(sb, i);
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
        /*sb.setColor(creamColor);
        sb.draw(ImageMaster.WHITE_SQUARE_IMG, 1138.0F * Settings.scale, 168.0F * Settings.scale, LINE_THICKNESS, 692.0F * Settings.scale);

        sb.draw(ImageMaster.WHITE_SQUARE_IMG, 1480.0F * Settings.scale, 168.0F * Settings.scale, LINE_THICKNESS, 692.0F * Settings.scale);

        sb.setColor(new Color(0.0F, 0.0F, 0.0F, 0.75F));
        sb.draw(ImageMaster.WHITE_SQUARE_IMG, 982.0F * Settings.scale, 814.0F * Settings.scale, 630.0F * Settings.scale, 16.0F * Settings.scale);

        sb.setColor(creamColor);
            sb.draw(ImageMaster.WHITE_SQUARE_IMG, 982.0F * Settings.scale, 820.0F * Settings.scale, 630.0F * Settings.scale, LINE_THICKNESS);*/ 
    }

    private void drawRect(SpriteBatch sb, float x, float y, float width, float height, float thickness) {
        sb.draw(ImageMaster.WHITE_SQUARE_IMG, x, y, width, thickness);
        sb.draw(ImageMaster.WHITE_SQUARE_IMG, x, y, thickness, height);
        sb.draw(ImageMaster.WHITE_SQUARE_IMG, x, y+height-thickness, width, thickness);
        sb.draw(ImageMaster.WHITE_SQUARE_IMG, x+width-thickness, y, thickness, height);
    }
}