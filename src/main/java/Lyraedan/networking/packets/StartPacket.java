package Lyraedan.networking.packets;

import java.nio.ByteBuffer;

import chronoMods.TogetherManager;
import chronoMods.network.RemotePlayer;
import chronoMods.ui.deathScreen.customMetrics;
import chronoMods.ui.mainMenu.NewMenuButtons;

public class StartPacket extends SpirePacket {

	@Override
	public void onDataReceived(ByteBuffer data, RemotePlayer playerInfo) {
		TogetherManager.log("Start Run");
		NewMenuButtons.newGameScreen.embark();

		// Report to server - this is a blank entry to protect against rage quitters
		customMetrics startmetrics = new customMetrics();
		Thread st = new Thread((Runnable)startmetrics);
		st.start();
	}

	@Override
	public ByteBuffer generatePacketData() {
		ByteBuffer data = ByteBuffer.allocateDirect(8);
		data.putInt(4, 1);
		return data;
	}

}
