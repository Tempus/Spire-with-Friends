package Lyraedan.networking.packets;

import java.nio.Buffer;
import java.nio.ByteBuffer;

import com.megacrit.cardcrawl.cards.AbstractCard;

import chronoMods.TogetherManager;
import chronoMods.coop.relics.GhostWriter;
import chronoMods.network.CardDataBuffer;
import chronoMods.network.RemotePlayer;

public class SendCardGhostPacket extends SpirePacket {
	
	@Override
	public void onDataReceived(ByteBuffer data, RemotePlayer playerInfo) {
		if (playerInfo.isUser(TogetherManager.currentUser)) { return; }

		int update = data.getInt(4);
		int remove = data.getInt(8);

		// Get card
		((Buffer)data).position(12);
		byte[] bytesghost = new byte[data.remaining()];
		data.get(bytesghost);

		AbstractCard ghostOutCard = CardDataBuffer.fromJson(new String(bytesghost)).generateCard();
		TogetherManager.log("Send card ghost: " + ghostOutCard.cardID);

		AbstractCard removeMe = null;

		// Add it to GhostWriter, or update/remove
		if (update > 0) {
			for (AbstractCard c : GhostWriter.rareCards.group) {
				if (c.cardID.equals(ghostOutCard.cardID) && !c.upgraded) {
					c.upgrade();
					return;
				}
			}
		} else if (remove > 0) {
			for (AbstractCard c : GhostWriter.rareCards.group) {
				if (c.cardID.equals(ghostOutCard.cardID) && c.timesUpgraded == ghostOutCard.timesUpgraded)
					removeMe = c;
			}
			GhostWriter.rareCards.removeCard(removeMe);
		} else {
        	GhostWriter.rareCards.addToBottom(ghostOutCard);					
		}

	}

	@Override
	public ByteBuffer generatePacketData() {
		CardDataBuffer rewardghost = new CardDataBuffer(GhostWriter.sendCard);

		ByteBuffer data = ByteBuffer.allocateDirect(12 + rewardghost.getBufferSize());

		data.putInt(4, GhostWriter.sendUpdate ? 1 : 0);
		data.putInt(8, GhostWriter.sendRemove ? 1 : 0);

		((Buffer)data).position(12);
		data.put(rewardghost.getBytes());
		((Buffer)data).rewind();

		GhostWriter.sendCard = null; 
		return data;
	}

}
