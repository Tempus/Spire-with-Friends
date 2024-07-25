package Lyraedan.networking.packets;

import java.nio.ByteBuffer;

import chronoMods.network.RemotePlayer;

public class SendCardPacket extends SpirePacket {

	// Coop specific
	
	@Override
	public void onDataReceived(ByteBuffer data, RemotePlayer playerInfo) {
		// Unused
		// TogetherManager.log("Send card direct: " + stringOuts);
		// AbstractDungeon.player.masterDeck.addToTop(CardLibrary.getCopy(stringOuts, upgrades, miscs));
	}

	@Override
	public ByteBuffer generatePacketData() {
		// Unused
		ByteBuffer data = ByteBuffer.allocateDirect(4);
		return data;
	}

}
