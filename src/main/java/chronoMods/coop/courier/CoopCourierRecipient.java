package chronoMods.coop;

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
import com.megacrit.cardcrawl.helpers.*;
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

import java.util.ArrayList;

public class CoopCourierRecipient
{
    // EXPECTED HEIGHT + Buffer: 75f

    public RemotePlayer player;
    public boolean selected = false;

    // Position
    public float x;
    public float y;
    float scale = 1.0f;

    float WIDTH = 376f; // Full size is 464
    float HEIGHT = Settings.scale*98f*0.75f; 
    public Hitbox hb = new Hitbox(0f,0f, WIDTH, HEIGHT);

    public CoopCourierScreen parent;


    public CoopCourierRecipient(RemotePlayer player, float x, float y, CoopCourierScreen parent) {
        this.player = player;
        this.x = x;
        this.y = y;
        this.hb.move(x,y);

        this.parent = parent;
    }

    public void update() {
        hb.move(this.x, this.y);
        hb.update();
        scale = 1.0f;

        if (hb.hovered) {
            scale = 1.1f;
            if (InputHelper.justClickedLeft)
                hb.clickStarted = true; 
        }

        if (hb.clicked && !selected) {
            parent.deselect();
            selected = true;
            hb.clicked = false;
        }
    }

    public void render(SpriteBatch sb) {
        sb.setColor(Color.SLATE.cpy());
        if (selected)
            sb.setColor(Color.GOLD.cpy());

        // Background
        sb.draw(
            ImageMaster.REWARD_SCREEN_ITEM,
            this.x - WIDTH / 2f,
            this.y - 98 / 2f,
            WIDTH / 2f, 98 / 2f,
            WIDTH, 98,
            Settings.scale*scale,Settings.scale*0.75f*scale,
            0f,
            0, 0, 464, 98,
            false, false);

        sb.setColor(Color.WHITE.cpy());

        // Player Portrait
        if (player.getPortrait() != null) {
            sb.draw(
                player.getPortrait(),
                this.x - 56 / 2f - 140f * Settings.scale,
                this.y - 56 / 2f - 2f * Settings.scale,
                56 / 2f,
                56 / 2f,
                56,
                56,
                Settings.scale*scale,
                Settings.scale*scale,
                0f,
                0,
                0,
                player.getPortrait().getWidth(),
                player.getPortrait().getHeight(),
                false,
                false); }

        // Portrait Frame
        sb.draw(TogetherManager.portraitFrames.get(0), 
            this.x - (64 / 2f) * Settings.scale - 140f * Settings.scale  - 184.0F * Settings.scale*scale, 
            this.y - (64 / 2f) * Settings.scale - 2f * Settings.scale    - 104.0F * Settings.scale*scale, 
            0.0F, 0.0F, 432.0F, 243.0F, Settings.scale*scale, Settings.scale*scale, 0.0F, 0, 0, 1920, 1080, false, false);

        // Player Name
        Color color = Settings.CREAM_COLOR;

        FontHelper.renderSmartText(
            sb,
            FontHelper.cardDescFont_N,
            player.userName,
            this.x - 88f * Settings.scale,
            this.y + 5f * Settings.scale,
            1000f * Settings.scale,
            0f,
            color,
            scale);

        hb.render(sb);
    }
}
