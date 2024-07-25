package Lyraedan.networking.packets;

import java.nio.Buffer;
import java.nio.ByteBuffer;

import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.rewards.RewardItem;

import chronoMods.TogetherManager;
import chronoMods.network.RemotePlayer;

public class TransferRelicPacket extends SpirePacket {

	// Coop specific
	
	@Override
	public void onDataReceived(ByteBuffer data, RemotePlayer playerInfo) {
		// Find the correct recipient
		long steamIDr = data.getLong(4);
		if (!TogetherManager.currentUser.isUser(steamIDr)) { return; }

		// Extract the string
		((Buffer)data).position(12);
		byte[] bytesr = new byte[data.remaining()];
		data.get(bytesr);
		String stringOutr = new String(bytesr);

		TogetherManager.log("Transfer relic: " + stringOutr);

		// Creat RewardItem
        RewardItem transferItemr = new RewardItem(RelicLibrary.getRelic(stringOutr).makeCopy());

        // Add Reward to Packages for pickup
        TogetherManager.getCurrentUser().packages.add(transferItemr);
	}

	@Override
	public ByteBuffer generatePacketData() {
		String rewardr = TogetherManager.courierScreen.transferRelic.relicId;

		ByteBuffer data = ByteBuffer.allocateDirect(12 + rewardr.getBytes().length);

		data.putLong(4, TogetherManager.courierScreen.getRecipient().getAccountID()); // Selected recipient

		((Buffer)data).position(12);
		data.put(rewardr.getBytes());
		((Buffer)data).rewind();

		TogetherManager.courierScreen.transferRelic = null;
		return data;
	}

}
