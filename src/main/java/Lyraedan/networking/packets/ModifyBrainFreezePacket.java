package Lyraedan.networking.packets;

import java.nio.ByteBuffer;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import chronoMods.coop.relics.BrainFreeze;
import chronoMods.network.RemotePlayer;

public class ModifyBrainFreezePacket extends SpirePacket {

	@Override
	public void onDataReceived(ByteBuffer data, RemotePlayer playerInfo) {
		AbstractDungeon.player.getBlight("BrainFreeze").counter += data.getInt(4);
	}

	@Override
	public ByteBuffer generatePacketData() {
		ByteBuffer data = ByteBuffer.allocateDirect(8);
		data.putInt(4, BrainFreeze.modEnergy);
		BrainFreeze.modEnergy = 0;
		return data;
	}

}
