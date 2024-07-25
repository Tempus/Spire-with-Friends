package Lyraedan.networking.packets;

import java.nio.ByteBuffer;

import chronoMods.coop.CoopNeowEvent;
import chronoMods.network.RemotePlayer;

public class ChooseNeowPacket extends SpirePacket {

	// Coop specific
	
	@Override
	public void onDataReceived(ByteBuffer data, RemotePlayer playerInfo) {
		int choice = data.getInt(4);

		if (playerInfo.userName == null)
			playerInfo.userName = "Unknown Player";

		// Choice logic
		CoopNeowEvent.registerChoice(choice, playerInfo);
	}

	@Override
	public ByteBuffer generatePacketData() {
		ByteBuffer data = ByteBuffer.allocateDirect(8);
		data.putInt(4, CoopNeowEvent.chosenOption);
		return data;
	}

}
