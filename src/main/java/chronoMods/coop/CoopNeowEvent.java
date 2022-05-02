package chronoMods.coop;

import com.evacipated.cardcrawl.modthespire.lib.*;
import basemod.interfaces.*;
import basemod.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.rooms.*;
import com.megacrit.cardcrawl.neow.*;
import com.megacrit.cardcrawl.events.*;
import com.megacrit.cardcrawl.map.*;
import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.shop.*;
import com.megacrit.cardcrawl.neow.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.*;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.rewards.chests.AbstractChest;
import com.megacrit.cardcrawl.vfx.ChestShineEffect;
import com.megacrit.cardcrawl.vfx.scene.SpookyChestEffect;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.ui.buttons.*;

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
import java.util.ArrayList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CoopNeowEvent {

	public static int screenNum = 0;
	public static int chosenOption = 0;
	public static ArrayList<CoopNeowReward> rewards = new ArrayList<>();
	public static ArrayList<CoopNeowReward> penalties = new ArrayList<>();
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString("Neow").TEXT;

    public static ArrayList<CoopNeowChoice> choices = new ArrayList();


    public static void registerChoice(int choice, RemotePlayer playerInfo) {
		// Safety patch to prevent crashes
		if (RoomEventDialog.optionList.size() < choice) { return; }

    	// Java's tools for list modification are shit-tacular, as usual.
    	CoopNeowChoice conflicter = null;
    	boolean addMe = true;

    	// Check for conflicts
    	for (CoopNeowChoice playerChoice : CoopNeowEvent.choices)
    		if (choice == playerChoice.choice) // We have a conflict
    			if (playerInfo.getAccountID() < playerChoice.playerInfo.getAccountID())
    				conflicter = playerChoice;
    			else
    				addMe = false;

    	// Person with the lower ID is the winner
    	if (conflicter != null)
    		CoopNeowEvent.choices.remove(conflicter);

    	if (addMe)
	    	CoopNeowEvent.choices.add(new CoopNeowChoice(choice, playerInfo));
    }

    public static void dismissBubble() {
	    for (AbstractGameEffect e : AbstractDungeon.effectList) {
	      if (e instanceof InfiniteSpeechBubble)
	        ((InfiniteSpeechBubble)e).dismiss(); 
	    } 
    }

    public static void talk(String msg) {
    	RoomEventDialog.optionList.get(0).calculateY(RoomEventDialog.optionList.size());
    	float y = (float)ReflectionHacks.getPrivate(RoomEventDialog.optionList.get(0), LargeDialogOptionButton.class, "y");
   		AbstractDungeon.effectList.add(new InfiniteSpeechBubble(1100.0F * Settings.xScale, y + 45.0F * Settings.yScale, msg));
	}

	public static void advanceScreen() {
		CoopNeowEvent.choices = new ArrayList();

		if (screenNum == 2) {
	        AbstractDungeon.getCurrRoom().event.roomEventText.updateDialogOption(0, TEXT[2]);
	        AbstractDungeon.getCurrRoom().event.roomEventText.clearRemainingOptions();
	        screenNum = 3;
		}
		if (screenNum == 1) {
			CoopNeowEvent.ControlNeowEvent.penalty((NeowEvent)(AbstractDungeon.getCurrRoom().event));
		}
	}

    @SpirePatch(clz = NeowEvent.class, method=SpirePatch.CONSTRUCTOR, paramtypez={boolean.class})
    public static class BeginNeowEvent {
        public static void Prefix(NeowEvent __instance, boolean isDone) {
        	if (TogetherManager.gameMode != TogetherManager.mode.Coop) { return; }
        	if (Settings.isTrial) { return; }

			CoopNeowEvent.choices = new ArrayList();
	
			CoopNeowEvent.screenNum = 1;
        	if (Settings.isTrial)
				CoopNeowEvent.screenNum = 99;

		    __instance.rng = new Random(Settings.seed);
			CoopNeowEvent.chosenOption = 0;
			CoopNeowEvent.rewards = CoopNeowReward.getRewards(TogetherManager.players.size()-1);
			CoopNeowEvent.penalties = CoopNeowReward.getPenalties(TogetherManager.players.size());

		}

		public static void Postfix(NeowEvent __instance, boolean isDone) {
        	if (TogetherManager.gameMode != TogetherManager.mode.Coop) { return; }

			if (CoopNeowEvent.screenNum == 1) {
		        CoopNeowEvent.dismissBubble();
  	            CoopNeowEvent.ControlNeowEvent.blessing(__instance);
			}
		}
	}
    
    @SpirePatch(clz = NeowEvent.class, method="update")
    public static class NeowUpdateAdditions {
        public static void Postfix(NeowEvent __instance) {
        	if (TogetherManager.gameMode != TogetherManager.mode.Coop) { return; }
        	if (Settings.isTrial) { return; }

			for (CoopNeowChoice r : CoopNeowEvent.choices)
				r.update();
			for (CoopNeowReward r : CoopNeowEvent.rewards)
				r.update(); 
			for (CoopNeowReward r : CoopNeowEvent.penalties)
				r.update(); 
        }
    }

    @SpirePatch(clz = RoomEventDialog.class, method="render")
    public static class NeowLinkRender {
        public static void Postfix(RoomEventDialog __instance, SpriteBatch sb) {
        	if (TogetherManager.gameMode != TogetherManager.mode.Coop) { return; }
        	if (Settings.isTrial) { return; }

        	if (CoopNeowEvent.screenNum != 1) { return; }
        	sb.setColor(Color.WHITE.cpy());

        	if (__instance.optionList == null || __instance.optionList.size() == 0) { return; }
			LargeDialogOptionButton opt = __instance.optionList.get(0);

			float x = ReflectionHacks.getPrivate(opt, LargeDialogOptionButton.class, "x");
			float y = ReflectionHacks.getPrivate(opt, LargeDialogOptionButton.class, "y");

		    sb.draw(ImageMaster.RELIC_LINKED, x - 64.0F, y - 64.0F + 52.0F * Settings.scale - 96f * Settings.scale, 64.0F, 64.0F, 128.0F, 128.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 128, 128, false, false);
        }
    }

    @SpirePatch(clz = NeowEvent.class, method="buttonEffect")
    public static class ControlNeowEvent {
        public static SpireReturn Prefix(NeowEvent __instance, int buttonPressed) {
        	if (TogetherManager.gameMode != TogetherManager.mode.Coop) { return SpireReturn.Continue(); }
        	if (Settings.isTrial) { return SpireReturn.Continue(); }

		    switch (CoopNeowEvent.screenNum) {
		      // Room arrival
		      case 0:
		        CoopNeowEvent.dismissBubble();
  	            CoopNeowEvent.ControlNeowEvent.blessing(__instance);
		        return SpireReturn.Return(null);

		      // Choose a blessing and wait
		      case 1:
		        CoopNeowEvent.dismissBubble();

		        CoopNeowEvent.talk(TEXT[3]);

		        CoopNeowEvent.chosenOption = buttonPressed;
				NetworkHelper.sendData(NetworkHelper.dataType.ChooseNeow);
		        return SpireReturn.Return(null);

		      // Choose a penalty and wait
		      case 2:
		        CoopNeowEvent.dismissBubble();

		        CoopNeowEvent.talk(TEXT[4]);

		        CoopNeowEvent.chosenOption = buttonPressed;
				NetworkHelper.sendData(NetworkHelper.dataType.ChooseNeow);
		        return SpireReturn.Return(null);
		    } 

		    // Okay, let's go.
		    AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.COMPLETE;
		    AbstractDungeon.dungeonMapScreen.open(false);

			CoopNeowEvent.screenNum = 99;
			CoopNeowEvent.chosenOption = 0;

		    return SpireReturn.Return(null);
        }

        public static void blessing(NeowEvent __instance) {

		    // DismissBubble()
		    CoopNeowEvent.dismissBubble();

		    __instance.roomEventText.clear();

   		    // Make Rewards
			// CoopNeowReward cr = CoopNeowReward.getWeakReward();
			// __instance.roomEventText.addDialogOption(cr.optionLabel);

			CoopNeowReward.NeowRewardDef cr = CoopNeowReward.getLinkReward();
			CoopNeowReward cA = new CoopNeowReward(cr);
			CoopNeowReward cB = new CoopNeowReward(cr);

			cA.link = cB;
			cB.link = cA;

		    __instance.roomEventText.addDialogOption(cA.optionLabel);
		    __instance.roomEventText.addDialogOption(cB.optionLabel);

		    for (CoopNeowReward c : CoopNeowEvent.rewards) {
				__instance.roomEventText.addDialogOption(c.optionLabel);
		    }

		    CoopNeowEvent.rewards.add(0, cA);
		    CoopNeowEvent.rewards.add(0, cB);
			CoopNeowReward cBoss = CoopNeowReward.getBossSwap();
		    CoopNeowEvent.rewards.add(cBoss);
	    	__instance.roomEventText.addDialogOption(cBoss.optionLabel);
		    
		    // talk()
		    CoopNeowEvent.talk(TEXT[5]);

		    // Set Screen
		    CoopNeowEvent.screenNum = 1;
        }

        public static void penalty(NeowEvent __instance) {

		    // DismissBubble()
		    CoopNeowEvent.dismissBubble();

		    __instance.roomEventText.clearRemainingOptions();

   		    // Make Rewards
			CoopNeowReward cr = CoopNeowReward.getNoPenalty();
		    __instance.roomEventText.updateDialogOption(0, cr.optionLabel);

		    for (CoopNeowReward c : CoopNeowEvent.penalties) {
				__instance.roomEventText.addDialogOption(c.optionLabel);
		    }
		    
		    CoopNeowEvent.penalties.add(0, cr);

		    // talk()
		    CoopNeowEvent.talk(TEXT[6]);

		    // Set Screen
		    CoopNeowEvent.screenNum = 2;
        }
    }
}