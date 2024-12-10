package Lyraedan.networking.packets;

import java.nio.Buffer;
import java.nio.ByteBuffer;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import chronoMods.TogetherManager;
import chronoMods.network.CardDataBuffer;
import chronoMods.network.RemotePlayer;
import chronoMods.network.SendDataPatches;

public class DeckInfoPacket extends SpirePacket {

	@Override
	public void onDataReceived(ByteBuffer data, RemotePlayer playerInfo) {
		playerInfo.cards = data.getInt(4);
		playerInfo.upgrades = data.getInt(8);

		// Get upgrade
		int updateDeckCard = data.getInt(12);
		int removeDeckCard = data.getInt(16);

		// Get card
		((Buffer)data).position(20);
		byte[] bytesDeckInfo = new byte[data.remaining()];
		data.get(bytesDeckInfo);

		CardDataBuffer bufferCard = CardDataBuffer.fromJson(new String(bytesDeckInfo));
		AbstractCard deckInfoOutCard = bufferCard.generateCard();
		TogetherManager.log("Update Deck Cards: " + updateDeckCard + ", " + removeDeckCard + " - " + deckInfoOutCard.toString());

		AbstractCard removeMeFromDeck = null;
		// Add it to the deck
		if (updateDeckCard > 0) {
			for (AbstractCard c : playerInfo.deck.group)
				if (bufferCard.isCard(c) && !c.upgraded)
					c.upgrade();

		} else if (removeDeckCard > 0) {
			for (AbstractCard c : playerInfo.deck.group) {
				if (bufferCard.isCard(c) && c.timesUpgraded == deckInfoOutCard.timesUpgraded)
					removeMeFromDeck = c;
			}
			playerInfo.deck.removeCard(removeMeFromDeck);
		} else { 
        	playerInfo.deck.addToBottom(deckInfoOutCard);
		}

		if (playerInfo.widget != null)
			playerInfo.widget.updateCardDisplay();

	}

	@Override
	public ByteBuffer generatePacketData() {
		CardDataBuffer deckCard = new CardDataBuffer(SendDataPatches.sendCard);
		TogetherManager.log("DeckInfo sent: " + deckCard.toString());

		// Deck Stats.
		ByteBuffer data = ByteBuffer.allocateDirect(20 + deckCard.getBytes().length);

		data.putInt(4, AbstractDungeon.player.masterDeck.size());

		int upgraded = 0;
	    for (AbstractCard cup : AbstractDungeon.player.masterDeck.group) {
	    	upgraded += cup.timesUpgraded; 
	    } 

			data.putInt(8, upgraded);
		((Buffer)data).position(12);

		// Card Update Stats
		data.putInt(12, SendDataPatches.sendUpdate ? 1 : 0);
		data.putInt(16, SendDataPatches.sendRemove ? 1 : 0);

		((Buffer)data).position(20);
		data.put(deckCard.getBytes());
		((Buffer)data).rewind();

		SendDataPatches.sendCard = null;
		SendDataPatches.sendUpdate = false;
		SendDataPatches.sendRemove = false;
		return data;
	}

}
