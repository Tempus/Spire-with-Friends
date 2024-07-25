package Lyraedan.networking.packets;

import java.nio.ByteBuffer;

import chronoMods.TogetherManager;
import chronoMods.network.RemotePlayer;

public class BingoCardPacket extends SpirePacket {

	@Override
	public void onDataReceived(ByteBuffer data, RemotePlayer playerInfo) {
		for (int x = 0; x < 5; x++) {
			for (int y = 0; y < 5; y++) {
				playerInfo.bingoCardIndices[x][y] = data.getInt((x*5+y)*4 + 4);
			}
		}
	}

	@Override
	public ByteBuffer generatePacketData() {
		ByteBuffer data = ByteBuffer.allocateDirect(4+4*25);

		int bufferCounter = 0;
		for (int[] row : TogetherManager.getCurrentUser().bingoCardIndices){
			for (int value : row){
				bufferCounter++;
				data.putInt(bufferCounter*4, value);
			}
		}
		return data;
	}

}
