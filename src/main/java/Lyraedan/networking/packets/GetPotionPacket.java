package Lyraedan.networking.packets;

import java.nio.Buffer;
import java.nio.ByteBuffer;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.potions.AbstractPotion;

import chronoMods.network.RemotePlayer;

public class GetPotionPacket extends SpirePacket {

	@Override
	public void onDataReceived(ByteBuffer data, RemotePlayer playerInfo) {
		playerInfo.potionSlots = data.getInt(4);

		// Extract the string
		((Buffer)data).position(8);
		byte[] potionBytes = new byte[data.remaining()];
		data.get(potionBytes);
		String potionsOut = new String(potionBytes);

		// Clear
		playerInfo.potions.clear();

		// Add the owned potions to the list
			for (String potionID : potionsOut.split(",")) {
				if (!potionID.equals(""))
				playerInfo.potions.add(potionID);
		}
	}

	@Override
	public ByteBuffer generatePacketData() {
		String potionsHeld = "";

		for (AbstractPotion potion : AbstractDungeon.player.potions) {
			potionsHeld += potion.ID;
			potionsHeld += ",";
		}
		potionsHeld = potionsHeld.substring(0, potionsHeld.length() - 1);

		ByteBuffer data = ByteBuffer.allocateDirect(8 + potionsHeld.getBytes().length);

		data.putInt(4, AbstractDungeon.player.potionSlots);

		((Buffer)data).position(8);
		data.put(potionsHeld.getBytes());
		((Buffer)data).rewind();
		return data;
	}

}
