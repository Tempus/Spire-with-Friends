package Lyraedan.networking.packets;

import java.nio.Buffer;
import java.nio.ByteBuffer;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PotionHelper;
import com.megacrit.cardcrawl.potions.PotionSlot;

import chronoMods.TogetherManager;
import chronoMods.coop.relics.VaporFunnel;
import chronoMods.network.RemotePlayer;

public class SendPotionPacket extends SpirePacket {

	@Override
	public void onDataReceived(ByteBuffer data, RemotePlayer playerInfo) {
		// Find the correct recipient
		int potslotb = data.getInt(4);

		// Extract the string
		((Buffer)data).position(8);
		byte[] bytesb = new byte[data.remaining()];
		data.get(bytesb);
		String stringOutb = new String(bytesb);

		TogetherManager.log("Send potion: " + stringOutb);

		// Obtain the potion
		if (AbstractDungeon.player.potions.size() > potslotb)
			if (AbstractDungeon.player.potions.get(potslotb) instanceof PotionSlot) 
            	AbstractDungeon.player.obtainPotion(potslotb, PotionHelper.getPotion(stringOutb));
	}

	@Override
	public ByteBuffer generatePacketData() {
		String rewardb = VaporFunnel.potName;
		TogetherManager.log(VaporFunnel.potName);
		ByteBuffer data = ByteBuffer.allocateDirect(8 + rewardb.getBytes().length);

		data.putInt(4, VaporFunnel.potSlot); // Selected recipient

		((Buffer)data).position(8);
		data.put(rewardb.getBytes());
		((Buffer)data).rewind();
		return data;
	}

}
