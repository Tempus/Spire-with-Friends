package chronoMods.ui.deathScreen;

import chronoMods.TogetherManager;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.dungeons.Exordium;
import com.megacrit.cardcrawl.helpers.PotionHelper;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.rewards.RewardItem;

import java.util.ArrayList;

public class VersusNeowBonus {

    @SpirePatch(clz = Exordium.class, method=SpirePatch.CONSTRUCTOR, paramtypez= {AbstractPlayer.class, ArrayList.class})
    public static class BeginNeowRoom {
		public static void Postfix(Exordium __instance, AbstractPlayer p, ArrayList<String> emptyList) {
        	if (TogetherManager.gameMode != TogetherManager.mode.Versus) { return; }

        	int floor = TogetherManager.getCurrentUser().highestFloor;
        	if (floor == 0) { return; }


        	// Gain some rewards! Start with Gold for each floor
        	AbstractDungeon.getCurrRoom().addGoldToRewards(10 * floor);

        	// Then a better potion for each midway chest cleared
        	if (floor > 41) {
        		AbstractDungeon.getCurrRoom().addPotionToRewards(PotionHelper.getPotion("EntropicBrew"));
        		AbstractDungeon.getCurrRoom().addRelicToRewards(RelicLibrary.getRelic("Potion Belt").makeCopy());
        	}
        	else if (floor > 24) {
        		AbstractDungeon.getCurrRoom().addPotionToRewards(PotionHelper.getPotion("DuplicationPotion"));
        		AbstractDungeon.getCurrRoom().addPotionToRewards(PotionHelper.getPotion("DuplicationPotion"));
        	}
        	else if (floor > 7) {
        		AbstractDungeon.getCurrRoom().addPotionToRewards(PotionHelper.getPotion("Fire Potion"));
        		AbstractDungeon.getCurrRoom().addPotionToRewards(PotionHelper.getPotion("Fire Potion"));
        	}


        	// Then special bonuses for each Act Boss cleared
        	// Cleared Act 3, Get 2 Astrolabes and Flight
        	if (floor > 50) {
	        	AbstractDungeon.getCurrRoom().addRelicToRewards(RelicLibrary.getRelic("Astrolabe").makeCopy());
	        	AbstractDungeon.getCurrRoom().addRelicToRewards(RelicLibrary.getRelic("Astrolabe").makeCopy());
	        	AbstractDungeon.getCurrRoom().rewards.add(new FlightReward());
        	}

        	// Cleared Act 2, Upgrade Starter Relic and get a Winged Boots
        	else if (floor > 33) {
	        	AbstractDungeon.getCurrRoom().rewards.add(new StarterRelicUpgradeReward());
	        	AbstractDungeon.getCurrRoom().addRelicToRewards(RelicLibrary.getRelic("WingedGreaves").makeCopy());
        	}

        	// Cleared Act 1, get a class specific stat relic
        	else if (floor > 16) {
				if (AbstractDungeon.player.getStartingRelics().get(0).equals("Burning Blood")) 
					AbstractDungeon.getCurrRoom().spawnRelicAndObtain((Settings.WIDTH / 2), (Settings.HEIGHT / 2), RelicLibrary.getRelic("Vajra").makeCopy());
				else if (AbstractDungeon.player.getStartingRelics().get(0).equals("Ring of the Snake")) 
					AbstractDungeon.getCurrRoom().spawnRelicAndObtain((Settings.WIDTH / 2), (Settings.HEIGHT / 2), RelicLibrary.getRelic("Oddly Smooth Stone").makeCopy());
				else if (AbstractDungeon.player.getStartingRelics().get(0).equals("Cracked Core")) 
					AbstractDungeon.getCurrRoom().spawnRelicAndObtain((Settings.WIDTH / 2), (Settings.HEIGHT / 2), RelicLibrary.getRelic("DataDisk").makeCopy());
				else if (AbstractDungeon.player.getStartingRelics().get(0).equals("PureWater")) 
					AbstractDungeon.getCurrRoom().spawnRelicAndObtain((Settings.WIDTH / 2), (Settings.HEIGHT / 2), RelicLibrary.getRelic("Lantern").makeCopy());
				else
					AbstractDungeon.getCurrRoom().spawnRelicAndObtain((Settings.WIDTH / 2), (Settings.HEIGHT / 2), RelicLibrary.getRelic("Anchor").makeCopy());
        	}

		    AbstractDungeon.combatRewardScreen.open(CardCrawlGame.languagePack.getUIString("Neow").TEXT[1]);
            (AbstractDungeon.getCurrRoom()).rewardPopOutTimer = 0.0F;

		    int remove = -1;
		    for (int j = 0; j < AbstractDungeon.combatRewardScreen.rewards.size(); j++) {
		        if (((RewardItem)AbstractDungeon.combatRewardScreen.rewards.get(j)).type == RewardItem.RewardType.CARD) {
		            remove = j;
		            break;
		        } 
		    } 
		    if (remove != -1)
		        AbstractDungeon.combatRewardScreen.rewards.remove(remove);
		}
	}
}