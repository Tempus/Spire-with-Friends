package Lyraedan.networking.packets;

import java.nio.ByteBuffer;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import chronoMods.TogetherManager;
import chronoMods.network.RemotePlayer;
import chronoMods.ui.hud.TopPanelPlayerPanels;
import chronoMods.ui.hud.VersusTimer;

public class SplitsPacket extends SpirePacket {

	// Versus specific
	@Override
	public void onDataReceived(ByteBuffer data, RemotePlayer playerInfo) {
		int actNum = data.getInt(4);
		float playtime = data.getFloat(8);

		TogetherManager.log("Splits, Act: " + (actNum-1) + " - " + VersusTimer.returnTimeString(playtime));
		switch (actNum) {
			case 1:
				playerInfo.splits.get("Act 1").activate(AbstractDungeon.bossKey);
				break;
			case 2:
				playerInfo.splits.get("Act 1").finish(playtime);
				playerInfo.splits.get("Act 2").activate(AbstractDungeon.bossKey);
				break;
			case 3:
				playerInfo.splits.get("Act 2").finish(playtime);
				playerInfo.splits.get("Act 3").activate(AbstractDungeon.bossKey);
				break;
			case 4:
				playerInfo.splits.get("Act 3").finish(playtime);
				playerInfo.splits.get("Final").activate(AbstractDungeon.bossKey);
				break;
			default:
				playerInfo.splits.get("Final").finish(playtime);
				break;
		}

		TopPanelPlayerPanels.SortWidgets();
	}

	@Override
	public ByteBuffer generatePacketData() {
		ByteBuffer data = ByteBuffer.allocateDirect(12);
		data.putInt(4, AbstractDungeon.actNum);
		data.putFloat(8, VersusTimer.timer);
		return data;
	}

}
