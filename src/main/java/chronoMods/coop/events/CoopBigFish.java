package chronoMods.coop.events;

import com.evacipated.cardcrawl.modthespire.lib.*;
import basemod.interfaces.*;

import com.megacrit.cardcrawl.localization.*;
import com.megacrit.cardcrawl.core.*;
import com.megacrit.cardcrawl.helpers.*;
import com.megacrit.cardcrawl.map.*;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.cards.curses.*;
import com.megacrit.cardcrawl.cards.colorless.*;
import com.megacrit.cardcrawl.cards.status.*;
import com.megacrit.cardcrawl.relics.*;
import com.megacrit.cardcrawl.potions.*;
import com.megacrit.cardcrawl.events.*;
import com.megacrit.cardcrawl.events.beyond.*;
import com.megacrit.cardcrawl.events.city.*;
import com.megacrit.cardcrawl.events.exordium.*;
import com.megacrit.cardcrawl.events.shrines.*;
import com.megacrit.cardcrawl.relics.*;
import com.megacrit.cardcrawl.characters.*;
import com.codedisaster.steamworks.*;

import chronoMods.*;
import chronoMods.coop.*;
import chronoMods.steam.*;
import chronoMods.ui.deathScreen.*;
import chronoMods.ui.hud.*;
import chronoMods.ui.lobby.*;
import chronoMods.ui.mainMenu.*;
import chronoMods.utilities.*;

import java.util.*;

public class CoopBigFish {

	// @SpirePatch(clz = BigFish.class, method="buttonEffect")
	// public static class eventReplace {
	// 	public static void Replace(BigFish __instance, int buttonPressed) {

	// 		Regret regret;
	// 		AbstractRelic r;
	// 		switch (__instance.screen) {
	// 			case INTRO:
	// 				switch (buttonPressed) {
	// 					case 0:
	// 						AbstractDungeon.player.heal(__instance.healAmt, true);
	// 						__instance.imageEventText.updateBodyText(BANANA_RESULT);
	// 						AbstractEvent.logMetricHeal("Big Fish", "Banana", __instance.healAmt);
	// 						break;
	// 					case 1:
	// 						AbstractDungeon.player.increaseMaxHp(5, true);
	// 						__instance.imageEventText.updateBodyText(DONUT_RESULT);
	// 						AbstractEvent.logMetricMaxHPGain("Big Fish", "Donut", 5);
	// 						break;
	// 					default:
	// 						__instance.imageEventText.updateBodyText(BOX_RESULT + BOX_BAD);
	// 						regret = new Regret();
	// 						r = AbstractDungeon.returnRandomScreenlessRelic(
	// 								AbstractDungeon.returnRandomRelicTier());
	// 						AbstractEvent.logMetricObtainCardAndRelic("Big Fish", "Box", (AbstractCard)regret, r);
	// 						AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(
										
	// 									CardLibrary.getCopy(((AbstractCard)regret).cardID), (Settings.WIDTH / 2), (Settings.HEIGHT / 2)));
	// 						AbstractDungeon.getCurrRoom().spawnRelicAndObtain((Settings.WIDTH / 2), (Settings.HEIGHT / 2), r);
	// 						break;
	// 				} 
	// 				__instance.imageEventText.clearAllDialogs();
	// 				__instance.imageEventText.setDialogOption(OPTIONS[5]);
	// 				__instance.screen = CurScreen.RESULT;
	// 				return;
	// 		} 
	// 		openMap();

	// 	}
	// }
}