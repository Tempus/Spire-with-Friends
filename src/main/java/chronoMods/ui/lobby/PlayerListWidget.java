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
import com.codedisaster.steamworks.*;

import java.util.ArrayList;

public class PlayerListWidget
{
    public JoinButton joinButton;
    public static ArrayList<RemotePlayer> players = new ArrayList();
    public boolean clicked = false;

    // Position
    public float x;
    public float y;

    public PlayerListWidget(String buttonText) {
        joinButton = new JoinButton(buttonText);
        joinButton.show();
        this.move(0,0);
    }

    public void move(float x, float y) {
        this.x = x;
        this.y = y;
        joinButton.move(x, y - (6 * 75f * Settings.scale) - 32f);
    }

    public void setPlayers(ArrayList<RemotePlayer> players) {
        this.players.clear();
        this.players = players;
    }

    public void toggleReadyState() {
        for (RemotePlayer player : players) {
            if (player.isUser(TogetherManager.currentUser.steamUser)) {
                player.ready = !player.ready;
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
    }

    public void render(SpriteBatch sb) {
        renderPlayerPanel(sb);

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
            this.x - 612 / 2f,
            this.y - 218f * Settings.scale - 716 / 2f,
            612 / 2f, 716 / 2f,
            612, 716,
            Settings.scale, Settings.scale,
            0f,
            0, 0, 612, 716,
            false, false);

        // Title text

        sb.draw(ImageMaster.VICTORY_BANNER, 
            this.x - 556.0F * Settings.scale, 
            this.y - 24.0F * Settings.scale, 
            556.0F, 119.0F, 1112.0F, 238.0F, Settings.scale * 0.8f, Settings.scale, 0.0F, 0, 0, 1112, 238, false, false);
      
        FontHelper.renderFontCentered(sb, FontHelper.bannerFont, "Players", 
            this.x, 
            this.y + 96.0F * Settings.scale + 22.0F * Settings.scale, 
            new Color(0.9F, 0.9F, 0.9F, 1.0F), 1.0f);

        // Reward Positioning
        if (players.size() > 0) { this.renderPlayerList(sb); }
    }

    public void renderPlayerList(SpriteBatch sb) {
        for (int i = 0; i < 6; i++) {
            if (i < players.size()) {
                // Background
                sb.draw(
                    ImageMaster.REWARD_SCREEN_ITEM,
                    this.x - 464 / 2f,
                    this.y - (i * 75f * Settings.scale) - 98 / 2f,
                    464 / 2f, 98 / 2f,
                    464, 98,
                    Settings.scale,Settings.scale*0.75f,
                    0f,
                    0, 0, 464, 98,
                    false, false);

                // Player Portrait
                if (players.get(i).portraitImg != null) {
                    sb.draw(
                        players.get(i).portraitImg,
                        this.x - 56 / 2f - 164f * Settings.scale,
                        this.y - (i * 75f * Settings.scale) - 56 / 2f - 2f * Settings.scale,
                        56 / 2f,
                        56 / 2f,
                        56,
                        56,
                        Settings.scale,
                        Settings.scale,
                        0f,
                        0,
                        0,
                        players.get(i).portraitImg.getWidth(),
                        players.get(i).portraitImg.getHeight(),
                        false,
                        false); }

                // Portrait Frame
                sb.draw(TogetherManager.portraitFrames.get(0), 
                    this.x - 64 / 2f - 164f * Settings.scale    - 184.0F * Settings.scale, 
                    this.y - (i * 75f * Settings.scale) - 64 / 2f - 2f * Settings.scale    - 104.0F * Settings.scale, 
                    0.0F, 0.0F, 432.0F, 243.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 1920, 1080, false, false);

                // Player Name
                Color color = Settings.CREAM_COLOR;

                FontHelper.renderSmartText(
                    sb,
                    FontHelper.cardDescFont_N,
                    players.get(i).userName,
                    this.x - 112f * Settings.scale,
                    this.y - (i * 75f * Settings.scale) + 5f * Settings.scale,
                    1000f * Settings.scale,
                    0f,
                    color);

                // Ready Tick
                if (players.get(i).ready) {
                    sb.draw(
                        ImageMaster.TICK,
                        this.x - 64 / 2f + 164f * Settings.scale,
                        this.y - (i * 75f * Settings.scale) - 64 / 2f - 2f * Settings.scale,
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
                }

            } else {
                sb.setColor(EMPTY_PLAYER_SLOT);
                // Background
                sb.draw(
                    ImageMaster.REWARD_SCREEN_ITEM,
                    this.x - 464 / 2f,
                    this.y - (i * 75f * Settings.scale) - 98 / 2f,
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
}