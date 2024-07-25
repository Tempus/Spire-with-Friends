package Lyraedan.networking.packets;

import java.nio.ByteBuffer;

import chronoMods.TogetherManager;
import chronoMods.network.RemotePlayer;

public class ReadyPacket extends SpirePacket {

	@Override
	public void onDataReceived(ByteBuffer data, RemotePlayer playerInfo) {
		int start = data.getInt(4);
		if (start == 0) {
			playerInfo.ready = false;
			TogetherManager.log("Unready: " + playerInfo.userName);
		} else {
			playerInfo.ready = true;
			TogetherManager.log("Ready: " + playerInfo.userName);
		}
	}

	@Override
	public ByteBuffer generatePacketData() {
		ByteBuffer data = ByteBuffer.allocateDirect(8);
		TogetherManager.log("Sending ready state: " + TogetherManager.getCurrentUser().userName + ", " + TogetherManager.getCurrentUser().ready);

		if (TogetherManager.getCurrentUser().ready) {
			TogetherManager.log("Sent Ready");
			data.putInt(4, 1);
		} else {
			TogetherManager.log("Sent Unready");
			data.putInt(4, 0);
		}
		return data;
	}

}
