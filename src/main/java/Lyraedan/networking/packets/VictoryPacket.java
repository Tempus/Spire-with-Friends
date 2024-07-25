package Lyraedan.networking.packets;

import java.nio.ByteBuffer;

import chronoMods.TogetherManager;
import chronoMods.network.RemotePlayer;

public class VictoryPacket extends SpirePacket {

	// Coop specific
	
	@Override
	public void onDataReceived(ByteBuffer data, RemotePlayer playerInfo) {
		playerInfo.victory = true;
		TogetherManager.cutscene.playerWins(playerInfo);
	}

	@Override
	public ByteBuffer generatePacketData() {
		ByteBuffer data = ByteBuffer.allocateDirect(4);
		return data;
	}

}
