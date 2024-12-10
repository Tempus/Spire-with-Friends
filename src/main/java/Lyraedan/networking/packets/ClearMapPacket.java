package Lyraedan.networking.packets;

import java.nio.ByteBuffer;

import chronoMods.TogetherManager;
import chronoMods.network.RemotePlayer;

public class ClearMapPacket extends SpirePacket{

	@Override
	public void onDataReceived(ByteBuffer data, RemotePlayer playerInfo) {
		if (playerInfo.isUser(TogetherManager.currentUser)) { return; }

		TogetherManager.log(playerInfo.userName + " has cleared their map.");

		playerInfo.drawable[playerInfo.act-1].clear();
	}

	@Override
	public ByteBuffer generatePacketData() {
		ByteBuffer data = ByteBuffer.allocateDirect(4);
		return data;
	}

}
