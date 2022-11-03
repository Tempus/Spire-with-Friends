package chronoMods.coop.hardmode;

import basemod.ReflectionHacks;
import chronoMods.TogetherManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.CampfireUI;
import com.megacrit.cardcrawl.ui.campfire.AbstractCampfireOption;

import java.util.ArrayList;

public class HeartHearth {

	public static final String[] TEXT = CardCrawlGame.languagePack.getUIString("Hearth").TEXT;

	public static ArrayList<Integer> chosenIndices = new ArrayList();

	public static com.megacrit.cardcrawl.random.Random hearthRng;

	// Hearth Buttons
    @SpirePatch(clz = CampfireUI.class, method="initializeButtons")
    public static class HearthButtonPatch {
        public static SpireReturn Prefix(CampfireUI __instance) {
            if (AbstractDungeon.id.equals("TheEnding") && AbstractDungeon.player.hasBlight("StrangeFlame")) {
            	ArrayList<AbstractCampfireOption> buttons = (ArrayList<AbstractCampfireOption>)ReflectionHacks.getPrivate(__instance, CampfireUI.class, "buttons");

            	buttons.clear();
            	chosenIndices.clear();
				
				hearthRng = new com.megacrit.cardcrawl.random.Random(Settings.seed);

            	if (TogetherManager.players.size() == 2) {
					buttons.add(new HearthOption(getSeeded(7,9)));
					buttons.add(new HearthOption(getSeeded(1,3)));
            	} else if (TogetherManager.players.size() == 3) {
					buttons.add(new HearthOption(getSeeded(1,3)));
					buttons.add(new HearthOption(getSeeded(4,6)));
					buttons.add(new HearthOption(getSeeded(7,9)));
            	} else if (TogetherManager.players.size() == 4) {
					buttons.add(new HearthOption(getSeeded(1,3)));
					buttons.add(new HearthOption(getSeeded(1,6)));
					buttons.add(new HearthOption(getSeeded(4,6)));
					buttons.add(new HearthOption(getSeeded(7,9)));
            	} else if (TogetherManager.players.size() == 5) {
					buttons.add(new HearthOption(getSeeded(1,3)));
					buttons.add(new HearthOption(getSeeded(1,3)));
					buttons.add(new HearthOption(getSeeded(4,6)));
					buttons.add(new HearthOption(getSeeded(4,6)));
					buttons.add(new HearthOption(getSeeded(7,9)));
            	} else if (TogetherManager.players.size() == 6) {
					buttons.add(new HearthOption(getSeeded(1,3)));
					buttons.add(new HearthOption(getSeeded(1,3)));
					buttons.add(new HearthOption(getSeeded(4,6)));
					buttons.add(new HearthOption(getSeeded(4,6)));
					buttons.add(new HearthOption(getSeeded(7,9)));
					buttons.add(new HearthOption(getSeeded(7,9)));
            	}

				return SpireReturn.Return(null);
			}

			return SpireReturn.Continue();
		}
	}

	public static Integer getSeeded(int start, int end) {
		StrangeFlame sf = (StrangeFlame)AbstractDungeon.player.getBlight("StrangeFlame");

		int ret;
		do {
			ret = hearthRng.random(start,end);
		} while (chosenIndices.contains(ret) || sf.bossList.contains(ret));

		chosenIndices.add(ret);
		return ret;
	}

	// Replace the final fire message
    @SpirePatch(clz = CampfireUI.class, method="getCampMessage")
    public static class HearthButtonPatchMessage {
        public static String Postfix(String __result, CampfireUI __instance) {
            if (AbstractDungeon.id.equals("TheEnding") && AbstractDungeon.player.hasBlight("StrangeFlame"))
				return TEXT[0];

			return __result;
		}
	}

    @SpirePatch(clz = AbstractPlayer.class, method="renderShoulderImg")
    public static class HearthPlayerImagePatch {
        public static SpireReturn Prefix(AbstractPlayer __instance, SpriteBatch sb) {
            if (AbstractDungeon.id.equals("TheEnding") && AbstractDungeon.player.hasBlight("StrangeFlame")) { return SpireReturn.Return(null); }

            return SpireReturn.Continue();
        }
    }
}