package Lyraedan.networking.packets;

import java.nio.Buffer;
import java.nio.ByteBuffer;

import com.megacrit.cardcrawl.helpers.PotionHelper;
import com.megacrit.cardcrawl.rewards.RewardItem;

import chronoMods.TogetherManager;
import chronoMods.network.RemotePlayer;

public class TransferPotionPacket extends SpirePacket {

	@Override
	public void onDataReceived(ByteBuffer data, RemotePlayer playerInfo) {
		// Find the correct recipient
		long steamIDp = data.getLong(4);
		if (!TogetherManager.currentUser.isUser(steamIDp)) { return; }

		// Extract the string
		((Buffer)data).position(12);
		byte[] bytesp = new byte[data.remaining()];
		data.get(bytesp);
		String stringOutp = new String(bytesp);

		TogetherManager.log("Transfer potion: " + stringOutp);

		// Creat RewardItem
        RewardItem transferItemp = new RewardItem(PotionHelper.getPotion(stringOutp));

        // Add Reward to Packages for pickup
        TogetherManager.getCurrentUser().packages.add(transferItemp);
	}

	@Override
	public ByteBuffer generatePacketData() {
		String rewardp = TogetherManager.courierScreen.transferPotion.ID;

		ByteBuffer data = ByteBuffer.allocateDirect(12 + rewardp.getBytes().length);

		data.putLong(4, TogetherManager.courierScreen.getRecipient().getAccountID()); // Selected recipient

		((Buffer)data).position(12);
		data.put(rewardp.getBytes());
		((Buffer)data).rewind();

		TogetherManager.courierScreen.transferPotion = null; 
		return data;
	}

}
