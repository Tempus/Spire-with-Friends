package chronoMods.coop.hardmode;

import com.evacipated.cardcrawl.modthespire.lib.*;

import com.badlogic.gdx.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;

import com.megacrit.cardcrawl.localization.*;
import com.megacrit.cardcrawl.core.*;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.cards.curses.*;
import com.megacrit.cardcrawl.cards.status.*;
import com.megacrit.cardcrawl.blights.*;
import com.megacrit.cardcrawl.helpers.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.city.*;
import com.megacrit.cardcrawl.events.shrines.*;
import com.megacrit.cardcrawl.rewards.*;
import com.megacrit.cardcrawl.rooms.*;
import com.megacrit.cardcrawl.map.*;
import com.megacrit.cardcrawl.shop.*;
import com.megacrit.cardcrawl.powers.*;
import com.megacrit.cardcrawl.actions.*;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.monsters.beyond.*;
import com.megacrit.cardcrawl.monsters.city.*;
import com.megacrit.cardcrawl.monsters.exordium.*;
import com.megacrit.cardcrawl.monsters.ending.*;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.scene.*;
import com.megacrit.cardcrawl.vfx.campfire.*;
import com.megacrit.cardcrawl.screens.*;
import com.megacrit.cardcrawl.ui.campfire.*;

import basemod.*;
import basemod.abstracts.*;
import basemod.interfaces.*;

import java.util.*;

import chronoMods.*;
import chronoMods.network.steam.*;
import chronoMods.network.*;
import chronoMods.coop.*;
import chronoMods.coop.relics.*;
import chronoMods.ui.deathScreen.*;
import chronoMods.ui.hud.*;
import chronoMods.ui.lobby.*;
import chronoMods.ui.mainMenu.*;


import com.megacrit.cardcrawl.actions.utility.*;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.unique.*;
import com.megacrit.cardcrawl.vfx.combat.*;
import com.megacrit.cardcrawl.vfx.cardManip.*;
import com.megacrit.cardcrawl.vfx.*;

public class HeartHearth {

	public static final String[] TEXT = CardCrawlGame.languagePack.getUIString("Hearth").TEXT;

	public static ArrayList<Integer> chosenIndices = new ArrayList();

	// Hearth Buttons
    @SpirePatch(clz = CampfireUI.class, method="initializeButtons")
    public static class HearthButtonPatch {
        public static SpireReturn Prefix(CampfireUI __instance) {
            if (AbstractDungeon.id.equals("TheEnding") && AbstractDungeon.player.hasBlight("StrangeFlame")) {
            	ArrayList<AbstractCampfireOption> buttons = (ArrayList<AbstractCampfireOption>)ReflectionHacks.getPrivate(__instance, CampfireUI.class, "buttons");

            	buttons.clear();
            	chosenIndices.clear();

            	buttons.add(new HearthOption(0));

            	if (TogetherManager.players.size() == 2) {
					buttons.add(new HearthOption(getSeeded(7,9)));
            	} else if (TogetherManager.players.size() == 3) {
					buttons.add(new HearthOption(getSeeded(1,3)));
					buttons.add(new HearthOption(getSeeded(7,9)));
            	} else if (TogetherManager.players.size() == 4) {
					buttons.add(new HearthOption(getSeeded(1,3)));
					buttons.add(new HearthOption(getSeeded(4,6)));
					buttons.add(new HearthOption(getSeeded(7,9)));
            	} else if (TogetherManager.players.size() == 5) {
					buttons.add(new HearthOption(getSeeded(1,3)));
					buttons.add(new HearthOption(getSeeded(1,6)));
					buttons.add(new HearthOption(getSeeded(4,6)));
					buttons.add(new HearthOption(getSeeded(7,9)));
            	} else if (TogetherManager.players.size() == 6) {
					buttons.add(new HearthOption(getSeeded(1,3)));
					buttons.add(new HearthOption(getSeeded(1,3)));
					buttons.add(new HearthOption(getSeeded(4,6)));
					buttons.add(new HearthOption(getSeeded(4,6)));
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
			ret = AbstractDungeon.mapRng.random(start,end);
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