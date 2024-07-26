package Lyraedan.networking.packets;

import java.nio.ByteBuffer;

import com.megacrit.cardcrawl.core.Settings;

import chronoMods.TogetherManager;
import chronoMods.network.NetworkHelper;
import chronoMods.network.RemotePlayer;
import chronoMods.ui.deathScreen.customMetrics;
import chronoMods.ui.mainMenu.NewMenuButtons;

public class StartPacket extends SpirePacket {

	@Override
	public void onDataReceived(ByteBuffer data, RemotePlayer playerInfo) {
		TogetherManager.log("Start Run");
		NewMenuButtons.newGameScreen.embark();
		
		// Start new game flag as false to be safe
		TogetherManager.getCurrentUser().neowReady = false;
		NetworkHelper.sendData(NetworkHelper.dataType.NeowReady);

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
