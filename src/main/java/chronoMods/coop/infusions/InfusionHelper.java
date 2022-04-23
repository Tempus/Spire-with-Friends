package chronoMods.coop.infusions;

import com.evacipated.cardcrawl.modthespire.lib.*;
import basemod.*;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;

import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.core.*;

import com.megacrit.cardcrawl.actions.*;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.unique.*;
import com.megacrit.cardcrawl.actions.defect.*;
import com.megacrit.cardcrawl.actions.watcher.*;
import com.megacrit.cardcrawl.actions.utility.*;
import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.cards.tempCards.*;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.powers.*;
import com.megacrit.cardcrawl.powers.watcher.*;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.orbs.*;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;
import com.megacrit.cardcrawl.vfx.*;

import chronoMods.*;
import chronoMods.coop.*;
import chronoMods.coop.infusions.*;

import java.util.*;

public class InfusionHelper {

	public static InfusionSet getInfusionSet(AbstractPlayer.PlayerClass playerClass) {
		if (LinkedInfusions.characterInfusionMasterList.containsKey(playerClass)) {
			ArrayList<InfusionSet> as = LinkedInfusions.characterInfusionMasterList.get(playerClass);

			return as.get(MathUtils.random(as.size()-1));
		}

		return LinkedInfusions.defaultInfusions;
	}

	public static InfusionSet getSetByID(String setID) {
		for (ArrayList<InfusionSet> setHolder: LinkedInfusions.characterInfusionMasterList.values())
			for (InfusionSet set: setHolder)
				if (set.setID.equals(setID))
					return set;

		return LinkedInfusions.defaultInfusions;
	}

	public static Infusion getInfusionByID(String setID, int indexID) {
		for (ArrayList<InfusionSet> setHolder: LinkedInfusions.characterInfusionMasterList.values())
			for (InfusionSet set: setHolder)
				if (set.setID.equals(setID))
					return set.infusions.get(indexID);

		return LinkedInfusions.defaultInfusions.infusions.get(0);
	}
}