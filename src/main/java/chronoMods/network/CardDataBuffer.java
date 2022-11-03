package chronoMods.network;

import chronoMods.coop.hubris.DuctTapeCard;
import chronoMods.coop.infusions.Infusion;
import chronoMods.coop.infusions.InfusionHelper;
import com.badlogic.gdx.utils.Json;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;

import java.util.ArrayList;

public class CardDataBuffer {
	
	public static Json json = new Json();

	public String cardID;
	public int misc;
	public int timesUpgraded;
	
	public String mergeCardID;

	public String iSet = "";
	public int iIndex;

	public CardDataBuffer() {}

	public CardDataBuffer(AbstractCard card) {
		cardID = card.cardID;
		misc = card.misc;
		timesUpgraded = card.timesUpgraded;

		if (cardID.equals("MergeCard"))
			mergeCardID = ((DuctTapeCard)card).generateTransferID();

        Infusion i = Infusion.infusionField.infusion.get(card);
		if (i != null) {
			iSet = i.setID;
			iIndex = i.indexID;
		}
	}

	public String toString() {
		if (cardID.equals("MergeCard"))
			return cardID + " - " + mergeCardID;

		if (!iSet.equals(""))
			return cardID + " +" + timesUpgraded + " - Infused with " + InfusionHelper.getInfusionByID(iSet, iIndex).setID;

		return cardID + " +" + timesUpgraded;
	}

	public boolean isCard(AbstractCard otherCard) {
		if (otherCard.cardID.equals("MergeCard"))
			return ((DuctTapeCard)otherCard).generateTransferID().equals(mergeCardID);

		if (cardID.equals(otherCard.cardID) && !iSet.equals("")) {
	        Infusion i = Infusion.infusionField.infusion.get(otherCard);
			if (i != null) 
				return cardID.equals(otherCard.cardID) && iSet == i.setID && iIndex == i.indexID;
			else
				return false;
		}

		return cardID.equals(otherCard.cardID);
	}

	public static CardDataBuffer fromJson(String jsonIn) {
		return json.fromJson(CardDataBuffer.class, jsonIn);
	}

	public AbstractCard generateCard() {
		if (AbstractDungeon.player != null && AbstractDungeon.player.hasBlight("PneumaticPost"))
			timesUpgraded++;

		// Merged Card
		if (cardID.equals("MergeCard")) {
			ArrayList<AbstractCard> cards = new ArrayList();
			for (String cardIds : mergeCardID.split(";"))
				cards.add(CardLibrary.getCopy(cardIds, timesUpgraded, misc));

			return new DuctTapeCard(cards);
		} 

		// Normal Card
		AbstractCard card = CardLibrary.getCopy(cardID, timesUpgraded, misc);

		// Infused
		if (!iSet.equals("")) 
			InfusionHelper.getInfusionByID(iSet, iIndex).ApplyInfusion(card);

    	return card;
	}

	public byte[] getBytes() {
		String dataString = json.toJson(this);
		return dataString.getBytes();
	}

	public int getBufferSize() {
		return getBytes().length;
	}
}