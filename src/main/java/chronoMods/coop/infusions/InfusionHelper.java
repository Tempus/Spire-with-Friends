package chronoMods.coop.infusions;

import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.characters.AbstractPlayer;

import java.util.ArrayList;

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