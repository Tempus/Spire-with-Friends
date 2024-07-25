package Lyraedan.networking.packets;

import java.nio.Buffer;
import java.nio.ByteBuffer;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import chronoMods.TogetherManager;
import chronoMods.coop.relics.MessageInABottle;
import chronoMods.network.CardDataBuffer;
import chronoMods.network.RemotePlayer;

public class SendCardMessageBottlePacket extends SpirePacket {

	// Coop specific
	
	@Override
	public void onDataReceived(ByteBuffer data, RemotePlayer playerInfo) {
		if (playerInfo.isUser(TogetherManager.currentUser)) { return; }

		// Get card
		((Buffer)data).position(4);
		byte[] bytesmb = new byte[data.remaining()];
		data.get(bytesmb);

		AbstractCard bottleOutCard = CardDataBuffer.fromJson(new String(bytesmb)).generateCard();
		TogetherManager.log("Message In a Bottle card: " + bottleOutCard.cardID);

		MessageInABottle.bottleCards.addToBottom(bottleOutCard);

		if (AbstractDungeon.player.hasBlight("MessageInABottle"))
			((MessageInABottle)AbstractDungeon.player.getBlight("MessageInABottle")).setDescriptionAfterLoading();
	}

	@Override
	public ByteBuffer generatePacketData() {
		CardDataBuffer messageCard = new CardDataBuffer(MessageInABottle.sendCard);

		ByteBuffer data = ByteBuffer.allocateDirect(4 + messageCard.getBytes().length);

		((Buffer)data).position(4);
		data.put(messageCard.getBytes());
		((Buffer)data).rewind();

		MessageInABottle.sendCard = null;
		return data;
	}

}
