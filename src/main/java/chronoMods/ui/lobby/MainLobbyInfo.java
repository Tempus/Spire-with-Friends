package chronoMods.ui.lobby;

import chronoMods.steam.*;
import chronoMods.*;
import chronoMods.ui.mainMenu.*;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.*;
import com.megacrit.cardcrawl.helpers.input.InputHelper;

public class MainLobbyInfo {

    public SteamLobby info;

    private final float RANK_X = 1000.0F * Settings.scale;
    private final float NAME_X = 1160.0F * Settings.scale;
    private final float SCORE_X = 1500.0F * Settings.scale;

    private Color color = Settings.CREAM_COLOR.cpy();
    private static final float START_Y = 800.0F * Settings.scale;
    private static final float LINE_SPACING = -32.0F * Settings.scale;

    public boolean selected = false;
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

	    FontHelper.renderFontLeftTopAligned(sb, FontHelper.eventBodyText, info.ascension, RANK_X, position * LINE_SPACING + START_Y, this.color);
	    FontHelper.renderFontLeftTopAligned(sb, FontHelper.leaderboardFont, info.name, NAME_X, position * LINE_SPACING + START_Y, this.color);
	    FontHelper.renderFontLeftTopAligned(sb, FontHelper.eventBodyText, info.getOwnerName(), SCORE_X, position * LINE_SPACING + START_Y, this.color);

		FontHelper.eventBodyText.getData().setScale(1.0F);
		FontHelper.leaderboardFont.getData().setScale(1.0F);

	    this.hb.render(sb);
    }
}
