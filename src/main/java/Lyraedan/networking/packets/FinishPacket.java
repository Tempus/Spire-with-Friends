package Lyraedan.networking.packets;

import java.nio.ByteBuffer;

import chronoMods.network.RemotePlayer;
import chronoMods.ui.deathScreen.customMetrics;
import chronoMods.ui.hud.TopPanelPlayerPanels;
import chronoMods.ui.hud.VersusTimer;

public class FinishPacket extends SpirePacket {

	@Override
	public void onDataReceived(ByteBuffer data, RemotePlayer playerInfo) {
		float finishtime = data.getFloat(4);
		playerInfo.finalTime = finishtime;
		playerInfo.splits.get("Final").finish(finishtime);

		TopPanelPlayerPanels.SortWidgets();

		// Report to server - this should replace the earlier entry
		customMetrics metrics = new customMetrics();
		Thread t = new Thread((Runnable)metrics);
		t.start();

	}

	@Override
	public ByteBuffer generatePacketData() {
		ByteBuffer data = ByteBuffer.allocateDirect(8);
		data.putFloat(4, VersusTimer.timer);
		return data;
	}

}
