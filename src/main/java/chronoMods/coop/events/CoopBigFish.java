package chronoMods.coop.events;

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