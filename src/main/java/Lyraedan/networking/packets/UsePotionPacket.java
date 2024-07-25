package Lyraedan.networking.packets;

import java.nio.ByteBuffer;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.potions.PotionSlot;

import chronoMods.coop.relics.VaporFunnel;
import chronoMods.network.RemotePlayer;

public class UsePotionPacket extends SpirePacket {

	// Coop specific
	
	@Override
	public void onDataReceived(ByteBuffer data, RemotePlayer playerInfo) {
		// Find the correct recipient
		int potslot = data.getInt(4);
		AbstractDungeon.player.potions.set(potslot, new PotionSlot(potslot));
		AbstractDungeon.topPanel.potionUi.close();
	}

	@Override
	public ByteBuffer generatePacketData() {
		ByteBuffer data = ByteBuffer.allocateDirect(8);
		data.putInt(4, VaporFunnel.potSlot);
		return data;
	}

}
