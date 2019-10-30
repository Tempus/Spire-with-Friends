package chronoMods.ui.lobby;

import chronoMods.steam.*;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;

public class MainLobbyInfo {

    public SteamLobby info;

    private final float RANK_X = 1000.0F * Settings.scale;
    private final float NAME_X = 1160.0F * Settings.scale;
    private final float SCORE_X = 1500.0F * Settings.scale;

    private Color color = Settings.CREAM_COLOR.cpy();
    private static final float START_Y = 800.0F * Settings.scale;
    private static final float LINE_SPACING = -32.0F * Settings.scale;


    public MainLobbyInfo(SteamLobby lobby) {
    	this.info = lobby;
    }

    public void render(SpriteBatch sb, int position) {
	    FontHelper.renderFontLeftTopAligned(sb, FontHelper.eventBodyText, info.ascension, RANK_X, position * LINE_SPACING + START_Y, this.color);
	    
	    FontHelper.renderFontLeftTopAligned(sb, FontHelper.leaderboardFont, info.name, NAME_X, position * LINE_SPACING + START_Y, this.color);

	    FontHelper.renderFontLeftTopAligned(sb, FontHelper.eventBodyText, info.getOwnerName(), SCORE_X, position * LINE_SPACING + START_Y, this.color);
    }
}
