package Lyraedan.networking.packets;

import java.nio.Buffer;
import java.nio.ByteBuffer;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.rewards.RewardItem;

import chronoMods.TogetherManager;
import chronoMods.network.CardDataBuffer;
import chronoMods.network.RemotePlayer;

public class TransferCardPacket extends SpirePacket {
	
	@Override
	public void onDataReceived(ByteBuffer data, RemotePlayer playerInfo) {
		// Find the correct recipient
		long steamIDc = data.getLong(4);
		if (!TogetherManager.currentUser.isUser(steamIDc)) { return; }

		// Get card
		((Buffer)data).position(12);
		byte[] bytesc = new byte[data.remaining()];
		data.get(bytesc);

		AbstractCard transferOutCard = CardDataBuffer.fromJson(new String(bytesc)).generateCard();
		TogetherManager.log("Transfer card: " + transferOutCard.cardID);

		// Creat RewardItem
        RewardItem transferItemc = new RewardItem();
        transferItemc.cards.clear();
    	transferItemc.cards.add(transferOutCard);

        // Add Reward to Packages for pickup
        TogetherManager.getCurrentUser().packages.add(transferItemc);
	}

	@Override
	public ByteBuffer generatePacketData() {
		CardDataBuffer rewardc = new CardDataBuffer(TogetherManager.courierScreen.transferCard);

		ByteBuffer data = ByteBuffer.allocateDirect(12 + rewardc.getBytes().length);

		data.putLong(4, TogetherManager.courierScreen.getRecipient().getAccountID()); // Selected recipient

		((Buffer)data).position(12);
		data.put(rewardc.getBytes());
		((Buffer)data).rewind();

		TogetherManager.courierScreen.transferCard = null; 
		return data;
	}

}
