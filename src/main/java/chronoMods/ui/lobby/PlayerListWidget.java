package chronoMods.ui.lobby;

import chronoMods.*;
import chronoMods.network.steam.*;
import chronoMods.network.*;
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
import com.megacrit.cardcrawl.helpers.input.InputActionSet;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.screens.charSelect.CharacterSelectScreen;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen;
import com.megacrit.cardcrawl.screens.mainMenu.MenuCancelButton;
import com.megacrit.cardcrawl.screens.mainMenu.PatchNotesScreen;
import com.megacrit.cardcrawl.ui.buttons.GridSelectConfirmButton;
import com.codedisaster.steamworks.*;
import com.megacrit.cardcrawl.helpers.*;

import com.megacrit.cardcrawl.screens.mainMenu.ScrollBar;
import com.megacrit.cardcrawl.screens.mainMenu.ScrollBarListener;

import java.util.ArrayList;
import java.util.concurrent.*;

public class PlayerListWidget implements ScrollBarListener
{
    public JoinButton joinButton;
    public static CopyOnWriteArrayList<PlayerListWidgetItem> players = new CopyOnWriteArrayList();
    public boolean clicked = false;

    // Position
    public float x;
    public float y;

    // Scrolling
    private ScrollBar scrollBar = null;
    private boolean grabbedScreen = false;
    private float grabStartY = 0.0F;
    private float scrollTargetY = 0.0F;
    private float scrollY = 0.0F;
    private float scrollLowerBound = -Settings.DEFAULT_SCROLL_LIMIT;
    private float scrollUpperBound = Settings.DEFAULT_SCROLL_LIMIT;


    public PlayerListWidget(String buttonText) {
        joinButton = new JoinButton(buttonText);
        joinButton.show();

        // Setup scrollbar
        if (this.scrollBar == null && TogetherManager.gameMode != TogetherManager.mode.Coop) {
            calculateScrollBounds();
            this.scrollBar = new ScrollBar(this, 0, 0, 400.0F * Settings.scale);
        }        

        this.move(0,0);
    }

    public void move(float x, float y) {
        this.x = x;
        this.y = y;
        joinButton.move(x, y - (6 * 75f * Settings.scale) - 32f);

        if (TogetherManager.gameMode != TogetherManager.mode.Coop)
            scrollBar.setCenter(x + 270f * Settings.scale, y - 185f * Settings.scale);
    }

    public void setPlayers(CopyOnWriteArrayList<RemotePlayer> players) {
        this.players.clear();
        for (RemotePlayer p : players) 
            this.players.add(new PlayerListWidgetItem(p));

        while (this.players.size() < 7) {
            this.players.add(new PlayerListWidgetItem(null));
        }

        for (PlayerListWidgetItem pi : this.players) 
            pi.move(x,y);

        calculateScrollBounds();
    }

    public void toggleReadyState() {
        for (RemotePlayer player : TogetherManager.players) {
            if (player.isUser(TogetherManager.currentUser)) {
                player.ready = !player.ready;
                TogetherManager.currentUser.ready = player.ready;

                TogetherManager.log("Toggling ready state: " + player.userName + ", " + player.ready);
            }
        }
    }

    public void update() {
        // Join Button Clicked
        joinButton.update();
        if (!joinButton.isDisabled) {
            clicked = joinButton.hb.clicked;
        }
        joinButton.hb.clicked = false;

        // Old Debug player adding
        // if (InputActionSet.selectCard_10.isJustPressed()) {
        //     TogetherManager.log("Added Test User");
        //     RemotePlayer newPlayer = new RemotePlayer();
        //     newPlayer.userName = "Test User";
        //     newPlayer.ready = true;

        //     TogetherManager.players.add(newPlayer);
        //     TopPanelPlayerPanels.playerWidgets.add(new RemotePlayerWidget(newPlayer));
        // }

        if (getPlayersSize() != TogetherManager.players.size()) {
            TogetherManager.log("Is this triggering every frame?");
            setPlayers(TogetherManager.players);
        }

        // Scrollbar
        if (TogetherManager.gameMode != TogetherManager.mode.Coop) {
            boolean isDraggingScrollBar = this.scrollBar.update();
            if (!isDraggingScrollBar)
              updateScrolling(); 
        }

        int i = 0;
        for (PlayerListWidgetItem p : players) {
            p.update(i);
            if (TogetherManager.gameMode != TogetherManager.mode.Coop)
                p.scroll(this.scrollY);
            i++;
        }
    }

    public int getPlayersSize() {
        int i = 0;
        for (PlayerListWidgetItem p : this.players)
            if (p.player != null)
                i++;

        return i;
    }

    public int getOwnIndex() {
        int i = 0;
        for (PlayerListWidgetItem p : this.players) {
            if (p.player != null && p.player.isUser(TogetherManager.currentUser))
                return i;
            i++;
        }

        return i;
    }

    //  Begin scroll functions
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
        if (players.size() > 6)
            this.scrollUpperBound = (75f * (players.size()-6)) * Settings.scale;
        else
            this.scrollUpperBound = 1F * Settings.scale;
        this.scrollLowerBound = 0F * Settings.scale;
    }

    private void resetScrolling() {
        if (this.scrollTargetY < this.scrollLowerBound) {
          this.scrollTargetY = MathHelper.scrollSnapLerpSpeed(this.scrollTargetY, this.scrollLowerBound);
        } else if (this.scrollTargetY > this.scrollUpperBound) {
          this.scrollTargetY = MathHelper.scrollSnapLerpSpeed(this.scrollTargetY, this.scrollUpperBound);
        }
    }
    //  End scroll functions


    public void render(SpriteBatch sb) {
        renderPlayerPanel(sb);

        // Player Positioning
        // Only render items within the scroll area
        for (int i = 0; i < players.size() ; i++) {
            if (players.get(i) == null || i > players.size())
                continue;

            float y = this.y + this.scrollY - (i * 75f * Settings.yScale) - 98 / 2f;
            // players.get(i).render(sb, i);
            // float y = players.get(i).getY();
            // if (players.get(i).player != null)
            //     TogetherManager.log(players.get(i).player.userName + " is at " + y + " between " + (this.y - 456f * Settings.scale) + " and " + (this.y + 100f * Settings.scale));
            if (y > 280f * Settings.yScale && y < 820f * Settings.yScale)
                players.get(i).render(sb, i);
        }

        // Render buttons and bars
        if (TogetherManager.gameMode != TogetherManager.mode.Coop)
            this.scrollBar.render(sb);
        this.joinButton.render(sb);

        sb.setColor(Color.WHITE.cpy());

        // Title text
        sb.draw(ImageMaster.VICTORY_BANNER, 
            this.x - 900f / 2f, 
            this.y - 238.0F * 0.1f, 
            900/2f, 0, 900.0F, 238.0F, 
            Settings.scale, Settings.scale, 
            0.0F, 0, 0, 1112, 238, false, false);
      
        FontHelper.renderFontCentered(sb, FontHelper.SCP_cardTitleFont_small, CardCrawlGame.languagePack.getUIString("Lobby").TEXT[12], 
            this.x, 
            this.y + 96.0F * Settings.yScale + 22.0F * Settings.yScale, 
            new Color(0.9F, 0.9F, 0.9F, 1.0F), 1.0f);        

        if (TogetherManager.gameMode != TogetherManager.mode.Coop) {
            // Player Count
            sb.draw(TogetherManager.membersTexture, 
                this.x + 200f * Settings.scale, 
                this.y - (6 * 75f * Settings.scale) - 64f, 
                64/2f, 64/2f, 64.0F, 64.0F, 
                Settings.scale, Settings.scale, 
                0.0F, 0, 0, 64, 64, false, false);
          
            FontHelper.renderFontCentered(sb, FontHelper.SCP_cardTitleFont_small, "" + TogetherManager.players.size(), 
                this.x + 180f * Settings.scale, 
                this.y - (6 * 75f * Settings.scale) - 32f, 
                new Color(0.9F, 0.9F, 0.9F, 1.0F), 1.0f);        
        }
    }

    float BASE_X = Settings.WIDTH / 4.0F;
    float BASE_Y = Settings.HEIGHT - 275f * Settings.scale;
    private static final Color EMPTY_PLAYER_SLOT = new Color(1f, 1f, 1f, 0.3f);

    public void renderPlayerPanel(SpriteBatch sb) {
        // BG Panel        
        sb.setColor(Color.WHITE.cpy());
        sb.draw(
            ImageMaster.REWARD_SCREEN_SHEET,
            this.x - 612 / 2f,
            this.y - 218f * Settings.scale - 716 / 2f,
            612 / 2f, 716 / 2f,
            612, 716,
            Settings.scale, Settings.scale,
            0f,
            0, 0, 612, 716,
            false, false);
    }
}