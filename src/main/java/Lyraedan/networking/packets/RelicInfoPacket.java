package Lyraedan.networking.packets;

import java.nio.ByteBuffer;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import chronoMods.network.RemotePlayer;

public class RelicInfoPacket extends SpirePacket {

	// Coop specific
	
	@Override
	public void onDataReceived(ByteBuffer data, RemotePlayer playerInfo) {
		playerInfo.relics = data.getInt(4);
	}

	@Override
	public ByteBuffer generatePacketData() {
		ByteBuffer data = ByteBuffer.allocateDirect(8);
		data.putInt(4, AbstractDungeon.player.relics.size());
		return data;
	}

}
