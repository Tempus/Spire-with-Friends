package Lyraedan.networking.packets;

import java.nio.ByteBuffer;

import chronoMods.TogetherManager;
import chronoMods.network.RemotePlayer;

public class VersionPacket extends SpirePacket {

	@Override
	public void onDataReceived(ByteBuffer data, RemotePlayer playerInfo) {
		playerInfo.version = data.getFloat(4);
		playerInfo.modHash = data.getInt(8);
		playerInfo.safeMods = data.getInt(12) == 1 ? true : false;

		TogetherManager.log("V: " + playerInfo.version);
		TogetherManager.log("H: " + playerInfo.modHash);
		TogetherManager.log("S: " + playerInfo.safeMods);
	}

	@Override
	public ByteBuffer generatePacketData() {
		ByteBuffer data = ByteBuffer.allocateDirect(36);
		data.putFloat(4, TogetherManager.VERSION);
		data.putInt(8, TogetherManager.modHash);
		data.putInt(12, TogetherManager.safeMods ? 1 : 0);
		return data;
	}


}
