package Lyraedan.networking.packets;

import java.nio.ByteBuffer;

import chronoMods.TogetherManager;
import chronoMods.network.RemotePlayer;

public class NeowReadyPacket extends SpirePacket {

	@Override
	public void onDataReceived(ByteBuffer data, RemotePlayer playerInfo) {
		int start = data.getInt(4);
		if (start == 0) {
			playerInfo.neowReady = false;
			TogetherManager.log("Unready: " + playerInfo.userName);
		} else {
			playerInfo.neowReady = true;
			TogetherManager.log("Ready: " + playerInfo.userName);
		}
	}

	@Override
	public ByteBuffer generatePacketData() {
		ByteBuffer data = ByteBuffer.allocateDirect(8);
		TogetherManager.log("Sending neow ready state: " + TogetherManager.getCurrentUser().userName + ", " + TogetherManager.getCurrentUser().neowReady);

		if (TogetherManager.getCurrentUser().ready) {
			TogetherManager.log("Sent Neow Ready");
			data.putInt(4, 1);
		} else {
			TogetherManager.log("Sent Neow Unready");
			data.putInt(4, 0);
		}
		return data;
	}

}
