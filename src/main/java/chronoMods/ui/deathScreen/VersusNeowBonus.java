package chronoMods.ui.deathScreen;

import com.evacipated.cardcrawl.modthespire.lib.*;
import basemod.interfaces.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.rooms.*;
import com.megacrit.cardcrawl.neow.*;
import com.megacrit.cardcrawl.map.*;
import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.shop.*;
import com.megacrit.cardcrawl.neow.*;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.helpers.*;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.rewards.chests.AbstractChest;
import com.megacrit.cardcrawl.vfx.ChestShineEffect;
import com.megacrit.cardcrawl.vfx.scene.SpookyChestEffect;
import com.megacrit.cardcrawl.random.Random;

import chronoMods.*;
import chronoMods.coop.*;
import chronoMods.network.steam.*;
import chronoMods.network.*;
import chronoMods.ui.deathScreen.*;
import chronoMods.ui.hud.*;
import chronoMods.ui.lobby.*;
import chronoMods.ui.mainMenu.*;
import chronoMods.utilities.*;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.localization.CharacterStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.NeowsLament;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import com.megacrit.cardcrawl.vfx.cardManip.*;
import com.megacrit.cardcrawl.vfx.*;
import java.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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