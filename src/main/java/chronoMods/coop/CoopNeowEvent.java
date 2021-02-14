package chronoMods.coop;

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

import chronoMods.*;
import chronoMods.coop.*;
import chronoMods.steam.*;
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

    public static void dismissBubble() {
	    for (AbstractGameEffect e : AbstractDungeon.effectList) {
	      if (e instanceof InfiniteSpeechBubble)
	        ((InfiniteSpeechBubble)e).dismiss(); 
	    } 
    }

    public static void talk(String msg) {
   		AbstractDungeon.effectList.add(new InfiniteSpeechBubble(1100.0F * Settings.xScale, AbstractDungeon.floorY + 60.0F * Settings.yScale, msg));
	}

	public static void advanceScreen() {
		if (screenNum == 2) {
	        AbstractDungeon.getCurrRoom().event.roomEventText.updateDialogOption(0, "[Leave]");
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

			for (CoopNeowReward r : CoopNeowEvent.rewards)
				r.update(); 
			for (CoopNeowReward r : CoopNeowEvent.penalties)
				r.update(); 
        }
    }

    @SpirePatch(clz = NeowEvent.class, method="buttonEffect")
    public static class ControlNeowEvent {
        public static SpireReturn Prefix(NeowEvent __instance, int buttonPressed) {
        	if (TogetherManager.gameMode != TogetherManager.mode.Coop) { return SpireReturn.Continue(); }

		    switch (CoopNeowEvent.screenNum) {
		      // Room arrival
		      case 0:
		        CoopNeowEvent.dismissBubble();
  	            CoopNeowEvent.ControlNeowEvent.blessing(__instance);
		        return SpireReturn.Return(null);

		      // Choose a blessing and wait
		      case 1:
		        CoopNeowEvent.dismissBubble();

		        CoopNeowEvent.rewards.get(buttonPressed).activate();
		        CoopNeowEvent.talk("~Granted...~ ~now~ ~wait...~");

		        CoopNeowEvent.chosenOption = buttonPressed;
				NetworkHelper.sendData(NetworkHelper.dataType.ChooseNeow);
		        return SpireReturn.Return(null);

		      // Choose a penalty and wait
		      case 2:
		        CoopNeowEvent.dismissBubble();

		        CoopNeowEvent.penalties.get(buttonPressed).activate();
		        CoopNeowEvent.talk("~Risk...~ NL ~..reward....~");

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

		    // talk()
		    CoopNeowEvent.talk("~You~ ~must~ ~all~ ~make~ ~a~ ~choice...~");
		    __instance.roomEventText.clear();

   		    // Make Rewards
			CoopNeowReward cr = CoopNeowReward.getWeakReward();
		    __instance.roomEventText.addDialogOption(cr.optionLabel);

		    for (CoopNeowReward c : CoopNeowEvent.rewards) {
				__instance.roomEventText.addDialogOption(c.optionLabel);
		    }

		    CoopNeowEvent.rewards.add(0, cr);
			cr = CoopNeowReward.getBossSwap();
		    CoopNeowEvent.rewards.add(cr);
	    	__instance.roomEventText.addDialogOption(cr.optionLabel);
		    
		    // Set Screen
		    CoopNeowEvent.screenNum = 1;
        }

        public static void penalty(NeowEvent __instance) {

		    // DismissBubble()
		    CoopNeowEvent.dismissBubble();

		    // talk()
		    CoopNeowEvent.talk("~With~ ~each~ ~choice~ ~a~ ~consequence...~");
		    __instance.roomEventText.clearRemainingOptions();

   		    // Make Rewards
			CoopNeowReward cr = CoopNeowReward.getNoPenalty();
		    __instance.roomEventText.updateDialogOption(0, cr.optionLabel);

		    for (CoopNeowReward c : CoopNeowEvent.penalties) {
				__instance.roomEventText.addDialogOption(c.optionLabel);
		    }
		    
		    CoopNeowEvent.penalties.add(0, cr);

		    // Set Screen
		    CoopNeowEvent.screenNum = 2;
        }
    }
}