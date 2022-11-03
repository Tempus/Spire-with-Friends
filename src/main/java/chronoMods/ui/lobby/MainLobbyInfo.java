package chronoMods.ui.lobby;

import chronoMods.TogetherManager;
import chronoMods.network.Lobby;
import chronoMods.ui.mainMenu.NewMenuButtons;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.input.InputHelper;

public class MainLobbyInfo {

    public Lobby info;

    private final float MEMBER_X = 900.0F * Settings.scale;
    private final float RANK_X = 1000.0F * Settings.scale;
    private final float NAME_X = 1160.0F * Settings.scale;
    private final float SCORE_X = 1500.0F * Settings.scale;

    private Color color = Settings.CREAM_COLOR.cpy();
    // private static final float START_Y = 860.0F * Settings.scale;
    private static final float START_Y = 0.0F * Settings.scale;
    // private static final float LINE_SPACING = -32.0F * Settings.scale;
    private static final float LINE_SPACING = 1;

    public boolean selected = false;
    public boolean justSelected = false;
    public Hitbox hb;

    public MainLobbyInfo(Lobby lobby) {
    	this.info = lobby;
    	this.hb = new Hitbox(SCORE_X - RANK_X + NAME_X - RANK_X, 32);
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

    public void render(SpriteBatch sb, float position) {
    	this.hb.move(RANK_X + this.hb.width/2.0F, position * LINE_SPACING + START_Y - this.hb.height/2.0F);

    	if (this.hb.hovered) {
    		this.color = Settings.GOLD_COLOR.cpy(); 
		    FontHelper.smallDialogOptionFont.getData().setScale(1.1F);
		    FontHelper.leaderboardFont.getData().setScale(1.1F);
    	} else if (this.selected == true) {
    		this.color = Settings.GREEN_TEXT_COLOR.cpy();
    	} else {
    		this.color = Settings.CREAM_COLOR.cpy(); 
    	}


        // We want to display the following lobby info:

        // The chosen character (if a race)

        // The number of members
        sb.draw(TogetherManager.membersTexture, SCORE_X - 4.0f * Settings.scale, position * LINE_SPACING + START_Y - 72f*Settings.scale/2f, 48f * Settings.scale, 48f * Settings.scale);
        FontHelper.renderFontLeftTopAligned(sb, FontHelper.smallDialogOptionFont, Integer.toString(info.getMemberCount()), SCORE_X + 48.0F, position * LINE_SPACING + START_Y, this.color);

        // The ascension level
        sb.draw(ImageMaster.TP_ASCENSION, RANK_X, position * LINE_SPACING + START_Y - 72f*Settings.scale/2f, 48f * Settings.scale, 48f * Settings.scale);
        FontHelper.renderFontLeftTopAligned(sb, FontHelper.smallDialogOptionFont, info.ascension, RANK_X + 48f, position * LINE_SPACING + START_Y, this.color);

        // Lobby Owner
        FontHelper.renderFontLeftTopAligned(sb, FontHelper.leaderboardFont, info.owner, NAME_X, position * LINE_SPACING + START_Y, this.color);

        // On selection, the joined players and a join button on the left. 
        if (this.selected == true) {

        }

        // Reset the Scale
		FontHelper.smallDialogOptionFont.getData().setScale(1.0F);
		FontHelper.leaderboardFont.getData().setScale(1.0F);

	    this.hb.render(sb);
    }
}
