package Lyraedan.networking.packets;

import java.nio.ByteBuffer;
import java.util.Map;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.ModHelper;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rewards.RewardItem;

import chronoMods.TogetherManager;
import chronoMods.network.RemotePlayer;

public class TransferBoosterPacket extends SpirePacket {

	@Override
	public void onDataReceived(ByteBuffer data, RemotePlayer playerInfo) {
		// Find the correct recipient
		long steamIDboost = data.getLong(4);
		if (!TogetherManager.currentUser.isUser(steamIDboost)) { return; }

		// Rarity
		int rarity = data.getInt(12);

		// Set rarity of cards
		AbstractCard.CardRarity rare = AbstractCard.CardRarity.COMMON;
		if (rarity == 1)
			rare = AbstractCard.CardRarity.UNCOMMON;
		if (rarity == 2)
			rare = AbstractCard.CardRarity.RARE;

		// Roll for cards
	    CardGroup anyCard = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
	    
	    for (Map.Entry<String, AbstractCard> c : CardLibrary.cards.entrySet()) {
	      if (((AbstractCard)c.getValue()).color == playerInfo.character.getCardColor() && ((AbstractCard)c.getValue()).rarity == rare)
	        anyCard.addToBottom(((AbstractCard)c.getValue()).makeCopy()); 
	    } 
		    anyCard.shuffle(AbstractDungeon.cardRng);

		// Create RewardItem and make sure there's no dupes
        RewardItem transferItemBooster = new RewardItem();
        transferItemBooster.cards.clear();

	    int numCards = 3;
	    for (AbstractRelic r : AbstractDungeon.player.relics)
	      numCards = r.changeNumberOfCardsInReward(numCards); 
	    if (ModHelper.isModEnabled("Binary"))
	      numCards--; 
	    for (int i = 0; i < numCards; i++) {
				boolean containsDupe = true;
			AbstractCard card = null;
			while (containsDupe) {
				containsDupe = false;
				card = anyCard.getRandomCard(false, rare).makeCopy();
				for (AbstractCard c : transferItemBooster.cards) {
					if (c.cardID.equals(card.cardID))
						containsDupe = true;
				} 
			}
			if (card != null) 
				transferItemBooster.cards.add(card);
		}          

		// Hardcoded relic shit because that's how we roll now
		if (AbstractDungeon.player.hasBlight("PneumaticPost"))
			for (AbstractCard c: transferItemBooster.cards)
				c.upgrade();

        // Add Reward to Packages for pickup
        TogetherManager.getCurrentUser().packages.add(transferItemBooster);
		
	}

	@Override
	public ByteBuffer generatePacketData() {
		ByteBuffer data = ByteBuffer.allocateDirect(16);
		data.putLong(4, TogetherManager.courierScreen.getRecipient().getAccountID()); // Selected recipient
		data.putInt(12, TogetherManager.courierScreen.transferRarity);
		return data;
	}

}
