package chronoMods.ui.lobby;

import chronoMods.TogetherManager;
import chronoMods.network.Lobby;
import chronoMods.network.RemotePlayer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.TipHelper;

import java.util.ArrayList;

public class LobbyWidget
{
    public JoinButton joinButton;
    public static ArrayList<RemotePlayer> players = new ArrayList();
    public boolean clicked = false;

    // Positioning Magic Numbers
    public float OPTION_X = -190f * Settings.scale;
    public float OPTION_Y = -443f * Settings.yScale;
    public float FTUE_X = -243f * Settings.scale;
    public float FTUE_Y = -62f * Settings.yScale;
    public float TITLE_X = -70f * Settings.scale;
    public float TITLE_Y = 10f * Settings.yScale;
    public float BUTTON_X = 490f * Settings.scale;
    public float BUTTON_Y = -510f * Settings.yScale;

    public float COLUMN_HALVER = (220f / 2.f);

    public float ICON_SIZE = 64f * Settings.yScale;
    public float BIG_ICON_SIZE = 96f * Settings.yScale;
    public float TEXT_SPACING = 75f * Settings.yScale;
    public float ICON_SPACING = 72f * Settings.yScale;

    // Hitboxes for Tooltips
    public Hitbox characterHB = new Hitbox(BIG_ICON_SIZE, BIG_ICON_SIZE);
    public Hitbox ascensionHB = new Hitbox(BIG_ICON_SIZE, BIG_ICON_SIZE);
    public Hitbox heartHB   = new Hitbox(ICON_SIZE, ICON_SIZE);
    public Hitbox neowHB    = new Hitbox(ICON_SIZE, ICON_SIZE);
    public Hitbox ironmanHB = new Hitbox(ICON_SIZE, ICON_SIZE);

    // Position
    public float x;
    public float y;

    // Lobby
    public Lobby info;

    public LobbyWidget(String buttonText) {
        joinButton = new JoinButton(buttonText);
        joinButton.show();
        this.move(0,0);
    }

    public void move(float x, float y) {
        this.x = x;
        this.y = y;

        joinButton.move(x, this.y + BUTTON_Y);
    }

    public void setLobby(Lobby selectedLobby) {
        this.info = selectedLobby;
    }

    public void update() {
        // Join Button Clicked
        joinButton.update();
        if (!joinButton.isDisabled) {
            clicked = joinButton.hb.clicked;
        }
        joinButton.hb.clicked = false;

        characterHB.update();
        ascensionHB.update();
        heartHB.update();
        neowHB.update();
        ironmanHB.update();

        String[] msg = CardCrawlGame.languagePack.getUIString("Lobby").TEXT;

        if (characterHB.hovered)
            TipHelper.renderGenericTip(characterHB.cX + BIG_ICON_SIZE, characterHB.cY + BIG_ICON_SIZE, msg[1], msg[2]);
        if (ascensionHB.hovered)
            TipHelper.renderGenericTip(ascensionHB.cX + BIG_ICON_SIZE, ascensionHB.cY + BIG_ICON_SIZE, msg[3], msg[4]);
        if (heartHB.hovered)
            TipHelper.renderGenericTip(heartHB.cX + ICON_SIZE, heartHB.cY + ICON_SIZE, msg[5], msg[6]);
        if (neowHB.hovered)
            TipHelper.renderGenericTip(neowHB.cX + ICON_SIZE, neowHB.cY + ICON_SIZE, msg[7], msg[8]);
        if (ironmanHB.hovered)
            TipHelper.renderGenericTip(ironmanHB.cX + ICON_SIZE, ironmanHB.cY + ICON_SIZE, msg[9], msg[10]);
    }

    public void render(SpriteBatch sb) {
        renderPanel(sb);
        if (info != null)
            renderLobbyDetails(sb);

        this.joinButton.render(sb);
    }

    public void renderPanel(SpriteBatch sb) {

        // BG Panel        
        sb.setColor(Color.GRAY.cpy());
        sb.draw(ImageMaster.OPTION_CONFIRM, 
            this.x + OPTION_X, this.y + OPTION_Y - 80f * Settings.yScale, 
            80.0F, 0F, 160.0F, 360.0F, Settings.xScale * 1.5F, Settings.yScale * 1.5F, 0.0F, 0, 0, 360, 414, false, false);

        sb.setColor(Color.WHITE.cpy());
        sb.draw(ImageMaster.OPTION_CONFIRM, 
            this.x + OPTION_X + 180f * Settings.scale, this.y + OPTION_Y - 100f * Settings.yScale, 
            100.0F, 0, 200.0F, 414.0F, Settings.xScale * 1.5F, Settings.yScale * 1.5F, 0.0F, 0, 0, 360, 414, false, false);
        
        // Titles and text
        sb.draw(ImageMaster.OPTION_ABANDON, 
            this.x + FTUE_X, this.y + FTUE_Y, 
            220f / 2.0F, 100f / 2.0F, 220f, 100f, Settings.scale, Settings.scale, 0.0F, 0, 0, 440, 100, true, false);

        FontHelper.renderFontCentered(sb, FontHelper.SCP_cardTitleFont_small, CardCrawlGame.languagePack.getUIString("Lobby").TEXT[11], 
            this.x + FTUE_X + 220f / 2.0F, this.y + FTUE_Y + 100f / 2.0f, 
            new Color(0.9F, 0.9F, 0.9F, 1.0F), 1.0f);


        sb.draw(ImageMaster.OPTION_ABANDON, 
            this.x + TITLE_X, this.y + TITLE_Y, 
            330f / 2.0F, 100f / 2.0F, 330f, 100f, Settings.scale, Settings.scale, 0.0F, 0, 0, 440, 100, false, false);

        FontHelper.renderFontCentered(sb, FontHelper.SCP_cardTitleFont_small, CardCrawlGame.languagePack.getUIString("Lobby").TEXT[12], 
            this.x + TITLE_X + 330f / 2.0F, this.y + TITLE_Y + 100f / 2.0f, 
            new Color(0.9F, 0.9F, 0.9F, 1.0F), 1.0f);

    }

    public void renderLobbyDetails(SpriteBatch sb) {
        Color c = new Color(0.9F, 0.9F, 0.9F, 1.0F);
        float p = 40f;

        // Character
        // FontHelper.renderFontCentered(sb, FontHelper.smallDialogOptionFont, "Character", 
        //     this.x + FTUE_X + 220f / 2.0F, this.y + FTUE_Y - p, 
        //     new Color(0.9F, 0.9F, 0.9F, 1.0F), 1.0f);
        if (info.character.equals("IRONCLAD")) {
            sb.draw(TogetherManager.ironcladOn, 
                this.x + FTUE_X + COLUMN_HALVER - BIG_ICON_SIZE/2f, this.y + FTUE_Y - p - BIG_ICON_SIZE/2f,
                BIG_ICON_SIZE, BIG_ICON_SIZE);
        }

        if (info.character.equals("SILENT")) {
            sb.draw(TogetherManager.silentOn, 
                this.x + FTUE_X + COLUMN_HALVER - BIG_ICON_SIZE/2f, this.y + FTUE_Y - p - BIG_ICON_SIZE/2f,
                BIG_ICON_SIZE, BIG_ICON_SIZE);
        }

        if (info.character.equals("DEFECT")) {
            sb.draw(TogetherManager.defectOn, 
                this.x + FTUE_X + COLUMN_HALVER - BIG_ICON_SIZE/2f, this.y + FTUE_Y - p - BIG_ICON_SIZE/2f,
                BIG_ICON_SIZE, BIG_ICON_SIZE);
        }

        if (info.character.equals("WATCHER")) {
            sb.draw(TogetherManager.watcherOn, 
                this.x + FTUE_X + COLUMN_HALVER - BIG_ICON_SIZE/2f, this.y + FTUE_Y - p - BIG_ICON_SIZE/2f,
                BIG_ICON_SIZE, BIG_ICON_SIZE);
        }
        characterHB.move(this.x + FTUE_X + COLUMN_HALVER, this.y + FTUE_Y - p);

        // p += TEXT_SPACING;

        // Ascension
        // FontHelper.renderFontCentered(sb, FontHelper.smallDialogOptionFont, "Ascension: " + info.character, 
        //     this.x + FTUE_X + 220f / 2.0F, this.y + FTUE_Y - p, 
        //     new Color(0.9F, 0.9F, 0.9F, 1.0F), 1.0f);

        p += BIG_ICON_SIZE;
        p += 8f;

        sb.draw(TogetherManager.ascOffImg, 
            this.x + FTUE_X + COLUMN_HALVER - BIG_ICON_SIZE/2f, this.y + FTUE_Y - p - BIG_ICON_SIZE/2f,
            BIG_ICON_SIZE, BIG_ICON_SIZE);

        if (!info.ascension.equals("0")) {
            sb.draw(TogetherManager.ascOnImg, 
                this.x + FTUE_X + COLUMN_HALVER - BIG_ICON_SIZE/2f, this.y + FTUE_Y - p - BIG_ICON_SIZE/2f,
                BIG_ICON_SIZE, BIG_ICON_SIZE);
        }

        FontHelper.renderFontCentered(sb, FontHelper.SCP_cardTitleFont_small, info.ascension.trim(), 
            this.x + FTUE_X + COLUMN_HALVER + 2f, this.y + FTUE_Y - p - 9f, 
            new Color(0.9F, 0.9F, 0.9F, 1.0F), 0.8f);
        ascensionHB.move(this.x + FTUE_X + COLUMN_HALVER, this.y + FTUE_Y - p);

        // p += TEXT_SPACING;

        // Heart
        // FontHelper.renderFontCentered(sb, FontHelper.smallDialogOptionFont, "Heart: " + info.character, 
        //     this.x + FTUE_X + 220f / 2.0F, this.y + FTUE_Y - p, 
        //     new Color(0.9F, 0.9F, 0.9F, 1.0F), 1.0f);

        p += BIG_ICON_SIZE;
        p += 4f;

        sb.draw(TogetherManager.heartOffImg, 
            this.x + FTUE_X + COLUMN_HALVER - ICON_SIZE, this.y + FTUE_Y - p - ICON_SIZE/2f,
                ICON_SIZE, ICON_SIZE);

        if (info.heart) {
            sb.draw(TogetherManager.heartOnImg, 
                this.x + FTUE_X + COLUMN_HALVER - ICON_SIZE, this.y + FTUE_Y - p - ICON_SIZE/2f,
                ICON_SIZE, ICON_SIZE);
        }
        heartHB.move(this.x + FTUE_X + COLUMN_HALVER - ICON_SIZE/2f, this.y + FTUE_Y - p);

        // p += TEXT_SPACING;

        // Neow
        // FontHelper.renderFontCentered(sb, FontHelper.smallDialogOptionFont, "Neow: ", 
        //     this.x + FTUE_X + 220f / 2.0F, this.y + FTUE_Y - p, 
        //     new Color(0.9F, 0.9F, 0.9F, 1.0F), 1.0f);

        p += ICON_SPACING;

        sb.draw(TogetherManager.whaleOffImg, 
            this.x + FTUE_X + COLUMN_HALVER - ICON_SIZE/2f, this.y + FTUE_Y - p - ICON_SIZE/2f,
                ICON_SIZE, ICON_SIZE);

        if (info.neow) {
            sb.draw(TogetherManager.whaleOnImg, 
                this.x + FTUE_X + COLUMN_HALVER - ICON_SIZE/2f, this.y + FTUE_Y - p - ICON_SIZE/2f,
                ICON_SIZE, ICON_SIZE);
        }
        neowHB.move(this.x + FTUE_X + COLUMN_HALVER, this.y + FTUE_Y - p);

        // p += TEXT_SPACING;

        // Ironman
        // FontHelper.renderFontCentered(sb, FontHelper.smallDialogOptionFont, "Ironman: ", 
        //     this.x + FTUE_X + 220f / 2.0F, this.y + FTUE_Y - p, 
        //     new Color(0.9F, 0.9F, 0.9F, 1.0F), 1.0f);

        p += ICON_SPACING;

        sb.draw(TogetherManager.ironmanOffImg, 
            this.x + FTUE_X + COLUMN_HALVER, this.y + FTUE_Y - p - ICON_SIZE/2f,
                ICON_SIZE, ICON_SIZE);

        if (info.ironman) {
            sb.draw(TogetherManager.ironmanOnImg, 
                this.x + FTUE_X + COLUMN_HALVER, this.y + FTUE_Y - p - ICON_SIZE/2f,
                ICON_SIZE, ICON_SIZE);
        }
        ironmanHB.move(this.x + FTUE_X + COLUMN_HALVER + ICON_SIZE/2f, this.y + FTUE_Y - p);


        // Member List
        int i = 0;
        c = Settings.GOLD_COLOR.cpy();
        float scale = 1.1f;
        for (String name : info.memberNames) {
            i++;
            FontHelper.renderFontRightTopAligned(sb, FontHelper.smallDialogOptionFont, name, 
                this.x + TITLE_X + 260f * Settings.scale , this.y + TITLE_Y - i * 36f * Settings.scale,  
                scale, c);
            c = new Color(0.9F, 0.9F, 0.9F, 1.0F);
            scale = 1.0f;
        }

        characterHB.render(sb);
        ascensionHB.render(sb);
        heartHB.render(sb);
        neowHB.render(sb);
        ironmanHB.render(sb);        
    }
}