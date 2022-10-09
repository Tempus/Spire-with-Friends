package chronoMods.coop.hardmode;

import chronoMods.TogetherManager;
import chronoMods.network.NetworkHelper;
import chronoMods.network.RemotePlayer;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.ui.campfire.AbstractCampfireOption;

import java.util.ArrayList;

public class HearthOption extends AbstractCampfireOption {

	public int choice = 0;
	public String playerTaken;

	public static HearthOption.Options heartMerge;

	public static final String path = "chrono/images/hearthOptions/";
    public static ArrayList<Texture> buttons = new ArrayList();


    public enum Options {
    	NOPENALTY, SLIMEBOSS, GUARDIAN, HEXAGHOST, CHAMP, COLLECTOR, AUTOMATON, TIMEEATER, AWAKENED, DONUDECA;
    }

    public static void generateTextures() {
    	for(HearthOption.Options opt: HearthOption.Options.values())
        	buttons.add(new Texture(path + opt + ".png"));
    }

	public HearthOption(int choice) {
		this.choice = choice;

		this.label = HeartHearth.TEXT[choice*2+1];        // Grab the choice label, offset by 1
		this.description = HeartHearth.TEXT[choice*2+2];  // Grab the choice description, offset by 2
		this.img = buttons.get(choice);					  // Grab the choice image

		checkUsable();
	}

	public void checkUsable() {
		for (RemotePlayer p : TogetherManager.players) {
			if (p.heartChosen == HearthOption.Options.values()[choice]) {
				usable = false;
				playerTaken = p.userName;
				return;
			}
		}
	}

	public void useOption() {
		HardModeHeart.HeartChoice = choice;
		heartMerge = HearthOption.Options.values()[choice];
		NetworkHelper.sendData(NetworkHelper.dataType.HeartChoice);

		AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.COMPLETE;
	}

	public void render(SpriteBatch sb) {
		super.render(sb);

		if (!usable) {
			FontHelper.renderWrappedText(sb, FontHelper.cardTitleFont, "Chosen by", hb.cX, hb.cY + 18f*Settings.scale, 160*Settings.scale, Settings.PURPLE_COLOR, .6f);
			FontHelper.renderWrappedText(sb, FontHelper.cardTitleFont, playerTaken,   hb.cX, hb.cY - 5f*Settings.scale, 160*Settings.scale, Settings.PURPLE_COLOR, .75f);
		}
	}
}
