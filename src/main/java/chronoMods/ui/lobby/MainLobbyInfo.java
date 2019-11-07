package chronoMods.ui.lobby;

import chronoMods.steam.*;
import chronoMods.*;
import chronoMods.ui.mainMenu.*;

import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.*;
import com.megacrit.cardcrawl.helpers.input.InputHelper;

public class MainLobbyInfo {

    public SteamLobby info;

    public static Texture membersTexture = new Texture("chrono/images/FriendsIcon.png");


    private final float MEMBER_X = 900.0F * Settings.scale;
    private final float RANK_X = 1000.0F * Settings.scale;
    private final float NAME_X = 1160.0F * Settings.scale;
    private final float SCORE_X = 1500.0F * Settings.scale;

    private Color color = Settings.CREAM_COLOR.cpy();
    private static final float START_Y = 860.0F * Settings.scale;
    private static final float LINE_SPACING = -32.0F * Settings.scale;

    public boolean selected = false;
    public boolean justSelected = false;
    public Hitbox hb;

    public MainLobbyInfo(SteamLobby lobby) {
    	this.info = lobby;
    	this.hb = new Hitbox(SCORE_X - RANK_X + NAME_X - RANK_X, -LINE_SPACING);
    }

    public void update() {
        if ((InputHelper.justReleasedClickLeft) && (this.hb.hovered))
        {
       		NewMenuButtons.lobbyScreen.deselect();
    		this.selected = true;
            this.justSelected = true;
    	}

    	this.hb.update();
    }

    public void render(SpriteBatch sb, int position) {
    	this.hb.move(RANK_X + this.hb.width/2.0F, position * LINE_SPACING + START_Y - this.hb.height/2.0F);

    	if (this.hb.hovered) {
    		this.color = Settings.GOLD_COLOR.cpy(); 
		    FontHelper.eventBodyText.getData().setScale(1.1F);
		    FontHelper.leaderboardFont.getData().setScale(1.1F);
    	} else if (this.selected == true) {
    		this.color = Settings.GREEN_TEXT_COLOR.cpy();
    	} else {
    		this.color = Settings.CREAM_COLOR.cpy(); 
    	}


        // We want to display the following lobby info:

        // The chosen character (if a race)

        // The number of members
        sb.draw(membersTexture, SCORE_X - 4.0f * Settings.scale, position * LINE_SPACING + START_Y - 72f*Settings.scale/2f, 48f * Settings.scale, 48f * Settings.scale);
        FontHelper.renderFontLeftTopAligned(sb, FontHelper.eventBodyText, Integer.toString(info.getMemberCount()), SCORE_X + 48.0F, position * LINE_SPACING + START_Y, this.color);

        // The ascension level
        sb.draw(ImageMaster.TP_ASCENSION, RANK_X * Settings.scale, position * LINE_SPACING + START_Y - 72f*Settings.scale/2f, 48f * Settings.scale, 48f * Settings.scale);
        FontHelper.renderFontLeftTopAligned(sb, FontHelper.eventBodyText, info.ascension, RANK_X + 48f, position * LINE_SPACING + START_Y, this.color);

        // Lobby Owner
        FontHelper.renderFontLeftTopAligned(sb, FontHelper.leaderboardFont, info.getOwnerName(), NAME_X, position * LINE_SPACING + START_Y, this.color);

        // On selection, the joined players and a join button on the left. 
        if (this.selected == true) {

        }

        // Reset the Scale
		FontHelper.eventBodyText.getData().setScale(1.0F);
		FontHelper.leaderboardFont.getData().setScale(1.0F);

	    this.hb.render(sb);
    }
}
