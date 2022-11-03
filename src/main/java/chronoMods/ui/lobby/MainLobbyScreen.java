package chronoMods.ui.lobby;

import chronoMods.TogetherManager;
import chronoMods.coop.drawable.CycleButton;
import chronoMods.network.Integration;
import chronoMods.network.Lobby;
import chronoMods.network.NetworkHelper;
import chronoMods.ui.mainMenu.NewMenuButtons;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.MathHelper;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.screens.mainMenu.*;
import com.megacrit.cardcrawl.ui.buttons.GridSelectConfirmButton;

import java.util.concurrent.CopyOnWriteArrayList;

public class MainLobbyScreen implements ScrollBarListener
{
    public static class Enum
    {
        @SpireEnum
        public static MainMenuScreen.CurScreen MAIN_LOBBY;
    }

    //private static final CustomStrings customString = TogetherManager.CustomStringsMap.get("Lobby");
    //public static final String[] TEXT = customString.STRINGS;

    public CopyOnWriteArrayList<MainLobbyInfo> gameList;
    public int page = 0;

    // Buttons
    public CycleButton serviceToggle = new CycleButton(Settings.WIDTH - 64f * Settings.xScale, 64f * Settings.xScale, "");
    public MenuCancelButton button = new MenuCancelButton();
    public GridSelectConfirmButton confirmButton = new GridSelectConfirmButton(CardCrawlGame.languagePack.getUIString("Lobby").TEXT[13]);

    public TogetherManager.mode mode;
    public MainLobbyInfo selectedLobby;

    // Player Panel
    public LobbyWidget lobbyDetails = new LobbyWidget(CardCrawlGame.languagePack.getUIString("Lobby").TEXT[0]);

    // Refresh Network info timer
    public float refresh = 2f;
    public float refreshPeriod = 2f;

    // Scrolling
    private ScrollBar scrollBar = null;
    private boolean grabbedScreen = false;
    private float grabStartY = 0.0F;
    private float scrollTargetY = 0.0F;
    private float scrollY = 0.0F;
    private float scrollLowerBound = -Settings.DEFAULT_SCROLL_LIMIT;
    private float scrollUpperBound = Settings.DEFAULT_SCROLL_LIMIT;


    public MainLobbyScreen() {
        gameList = new CopyOnWriteArrayList();
        lobbyDetails.move(Settings.WIDTH / 4.0F, Settings.HEIGHT * 0.72f);
    }

    public void open() {
        this.mode = mode;

        // Service Toggle
        for (Integration service : NetworkHelper.networks)
            serviceToggle.addOption(service.getLogo());

        // Screen Swap
        CardCrawlGame.mainMenuScreen.darken();
        CardCrawlGame.mainMenuScreen.screen = Enum.MAIN_LOBBY;

        // Buttons
        button.show(PatchNotesScreen.TEXT[0]);
        this.confirmButton.show();
        this.confirmButton.isDisabled = false;

        // Add items to the list
        refreshGameList();
        lobbyDetails.players.clear();
        lobbyDetails.joinButton.isDisabled = true;

        // Setup scrollbar
        if (this.scrollBar == null) {
            calculateScrollBounds();
            this.scrollBar = new ScrollBar(this, Settings.WIDTH - 176.0F * Settings.scale, Settings.HEIGHT / 2.0F, 600.0F * Settings.scale);
        }
    }

    public void refreshGameList() {
        NetworkHelper.getLobbies();

        // for (int i = 0; i < 40; i++) {
        //     gameList.add(new MainLobbyInfo(new SteamLobby()));
        // }

        calculateScrollBounds();
    }

    public void createFreshGameList() {
        gameList.clear();

        for (Lobby l : NetworkHelper.lobbies) {
            gameList.add(new MainLobbyInfo(l));
        }

        calculateScrollBounds();
    }

    public void update() {
        // Return to the Main Menu
        button.update();
        if (button.hb.clicked || InputHelper.pressedEscape) {
            button.hb.clicked = false;
            InputHelper.pressedEscape = false;
            backToMenu();
        }

        // Service Toggle
        serviceToggle.update();

        // Lobby list
        for (MainLobbyInfo lobby : gameList) {
            if (lobby == null)
                continue;

            lobby.update();

            // Lobby selected
            if (lobby.justSelected) {
                lobby.justSelected = false;
                lobbyDetails.joinButton.isDisabled = false;

                lobbyDetails.setLobby(lobby.info);
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
        lobbyDetails.update();
        if (lobbyDetails.clicked) {
            selectedLobby.info.join();
            lobbyDetails.clicked = false;
        }

        // Reset input
        InputHelper.justClickedLeft = false;

        // Lobby Refresh Timer
        refresh -= Gdx.graphics.getDeltaTime();
        if (refresh < 0f) {
          refreshGameList();
          refresh = refreshPeriod;
        }

        // Scrollbar
        boolean isDraggingScrollBar = this.scrollBar.update();
        if (!isDraggingScrollBar)
          updateScrolling(); 
    }

    public void deselect() {
        for (MainLobbyInfo lobby : gameList) {
            lobby.selected = false;
        }
    }

    private void updateScrolling() {
        int y = InputHelper.mY;
        if (!this.grabbedScreen) {
        if (InputHelper.scrolledDown) {
            this.scrollTargetY += Settings.SCROLL_SPEED;
        } else if (InputHelper.scrolledUp) {
            this.scrollTargetY -= Settings.SCROLL_SPEED;
        } 
        if (InputHelper.justClickedLeft) {
            this.grabbedScreen = true;
            this.grabStartY = y - this.scrollTargetY;
        } 
        } else if (InputHelper.isMouseDown) {
            this.scrollTargetY = y - this.grabStartY;
        } else {
            this.grabbedScreen = false;
        } 
        this.scrollY = MathHelper.scrollSnapLerpSpeed(this.scrollY, this.scrollTargetY);
        resetScrolling();
        updateBarPosition();
    }

    public void scrolledUsingBar(float newPercent) {
        this.scrollY = MathHelper.valueFromPercentBetween(this.scrollLowerBound, this.scrollUpperBound, newPercent);
        this.scrollTargetY = this.scrollY;
        updateBarPosition();
    }

    private void updateBarPosition() {
        float percent = MathHelper.percentFromValueBetween(this.scrollLowerBound, this.scrollUpperBound, this.scrollY);
        this.scrollBar.parentScrolledToPercent(percent);
    }

    private void calculateScrollBounds() {
        this.scrollUpperBound = (16f * gameList.size()) * Settings.scale;
        this.scrollLowerBound = 0F * Settings.scale;
    }

    private void resetScrolling() {
        if (this.scrollTargetY < this.scrollLowerBound) {
          this.scrollTargetY = MathHelper.scrollSnapLerpSpeed(this.scrollTargetY, this.scrollLowerBound);
        } else if (this.scrollTargetY > this.scrollUpperBound) {
          this.scrollTargetY = MathHelper.scrollSnapLerpSpeed(this.scrollTargetY, this.scrollUpperBound);
        }
    }

    public void backToMenu() {
        TogetherManager.gameMode = TogetherManager.mode.Normal;
        CardCrawlGame.mainMenuScreen.screen = MainMenuScreen.CurScreen.MAIN_MENU;
        CardCrawlGame.mainMenuScreen.lighten();
        button.hide();
        deselect();
        NetworkHelper.leaveLobby();
        gameList.clear();
    }

    public void render(SpriteBatch sb) {
        FontHelper.renderFontCentered(sb, FontHelper.SCP_cardTitleFont_small, TogetherManager.gameMode + " Lobbies",
            Settings.WIDTH / 2.0f,
            Settings.HEIGHT - 70.0f * Settings.yScale,
            Settings.GOLD_COLOR);

        this.button.render(sb);
        this.confirmButton.render(sb);
        serviceToggle.render(sb);

        renderHeaders(sb);
        lobbyDetails.render(sb);

        // Only render items within the scroll area
        float renderY = this.scrollY;

        for (int i = 0; i < gameList.size() ; i++ ) {
            if (i >= gameList.size())
                continue;

            if (gameList.get(i) == null)
                continue;

            float y = ((-32.0F * i) + 860.0F + this.scrollY) * Settings.scale;

            if (y > 300f && y < 875f * Settings.scale) {
                gameList.get(i).render(sb, y);
            }
        }

        this.scrollBar.render(sb);
    }

    public void renderHeaders(SpriteBatch sb) {
        Color creamColor = new Color(1.0F, 0.965F, 0.886F, 1.0F);
        float RANK_X = 1000.0F * Settings.scale;
        float NAME_X = 1160.0F * Settings.scale;
        float SCORE_X = 1500.0F * Settings.scale;
        float LINE_THICKNESS = 4.0F * Settings.scale;

        String[] msg = CardCrawlGame.languagePack.getUIString("Lobby").TEXT;
        
        FontHelper.renderFontLeftTopAligned(sb, FontHelper.smallDialogOptionFont, msg[14], RANK_X, 920.0F * Settings.yScale, creamColor);

        FontHelper.renderFontLeftTopAligned(sb, FontHelper.smallDialogOptionFont, msg[15], NAME_X, 920.0F * Settings.yScale, creamColor);

        FontHelper.renderFontLeftTopAligned(sb, FontHelper.smallDialogOptionFont, msg[16], SCORE_X, 920.0F * Settings.yScale, creamColor);

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