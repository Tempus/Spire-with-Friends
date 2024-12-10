package Lyraedan.networking.packets;

import java.nio.ByteBuffer;

import chronoMods.network.RemotePlayer;
import chronoMods.network.NetworkHelper;
import chronoMods.network.NetworkHelper.dataType;

public class RequestVersionPacket extends SpirePacket {

	@Override
	public void onDataReceived(ByteBuffer data, RemotePlayer playerInfo) {
		NetworkHelper.sendData(dataType.Version);		
	}

	@Override
	public ByteBuffer generatePacketData() {
		ByteBuffer data = ByteBuffer.allocateDirect(4);
		return data;
	}

}
