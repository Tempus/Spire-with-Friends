package chronoMods.ui.deathScreen;

import basemod.abstracts.CustomReward;
import basemod.patches.com.megacrit.cardcrawl.ui.panels.TopPanel.TopPanelPatches;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.ModHelper;

import java.util.Arrays;

public class FlightReward extends CustomReward {

	public FlightReward()
	{
		super(ImageMaster.loadImage("images/ui/run_mods/flight.png"), "Flight", RewardTypePatch.FLIGHT);
	}

	public boolean claimReward() {
	    ModHelper.setMods(Arrays.asList("Flight"));
        TopPanelPatches.SetPlayerNamePatch.Postfix(AbstractDungeon.topPanel);

    	return true;
	}
}