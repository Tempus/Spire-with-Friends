package Lyraedan.networking.packets;

import java.nio.ByteBuffer;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.potions.PotionSlot;

import chronoMods.network.RemotePlayer;

public class AddPotionSlotPacket extends SpirePacket {

	// Coop specific
	
	@Override
	public void onDataReceived(ByteBuffer data, RemotePlayer playerInfo) {
		AbstractDungeon.player.potionSlots += 1;
	    AbstractDungeon.player.potions.add(new PotionSlot(AbstractDungeon.player.potionSlots - 1));
	}

	@Override
	public ByteBuffer generatePacketData() {
		ByteBuffer data = ByteBuffer.allocateDirect(4);
		return data;
	}

}
