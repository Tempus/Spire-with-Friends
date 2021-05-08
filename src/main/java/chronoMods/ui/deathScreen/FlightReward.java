package chronoMods.ui.deathScreen;

import com.evacipated.cardcrawl.modthespire.lib.*;
import basemod.abstracts.*;
import basemod.patches.com.megacrit.cardcrawl.ui.panels.TopPanel.TopPanelPatches;

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
import com.megacrit.cardcrawl.rewards.*;
import com.megacrit.cardcrawl.shop.*;
import com.megacrit.cardcrawl.neow.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
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