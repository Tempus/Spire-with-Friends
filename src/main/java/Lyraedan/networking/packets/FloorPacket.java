package Lyraedan.networking.packets;

import java.nio.ByteBuffer;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import chronoMods.TogetherManager;
import chronoMods.network.RemotePlayer;
import chronoMods.ui.hud.TopPanelPlayerPanels;

public class FloorPacket extends SpirePacket {

	@Override
	public void onDataReceived(ByteBuffer data, RemotePlayer playerInfo) {
		int floorNum = data.getInt(4);
		playerInfo.floor = floorNum;
		playerInfo.highestFloor = Math.max(floorNum, playerInfo.highestFloor);


		playerInfo.x = data.getInt(8);

		int yFloor = data.getInt(12);
    	if (AbstractDungeon.player != null && AbstractDungeon.player.hasBlight("BlueLadder") && playerInfo.y == yFloor)
    		AbstractDungeon.player.getBlight("BlueLadder").counter--;

		playerInfo.y = data.getInt(12);
		playerInfo.act = data.getInt(16);

		TogetherManager.log("Act: " + playerInfo.act + " - Floor: " + floorNum + " - Position: " + playerInfo.x + ", " + playerInfo.y);
		playerInfo.markMapNode();

		TopPanelPlayerPanels.SortWidgets();

	}

	@Override
	public ByteBuffer generatePacketData() {
		ByteBuffer data = ByteBuffer.allocateDirect(20);
		data.putInt(4, AbstractDungeon.floorNum);
		data.putInt(8, AbstractDungeon.getCurrMapNode().x);
		data.putInt(12, AbstractDungeon.getCurrMapNode().y);
		data.putInt(16, AbstractDungeon.actNum);
		return data;
	}

}
