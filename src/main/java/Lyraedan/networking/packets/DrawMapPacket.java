package Lyraedan.networking.packets;

import java.nio.ByteBuffer;

import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.screens.DungeonMapScreen;

import chronoMods.TogetherManager;
import chronoMods.coop.drawable.MapCanvas;
import chronoMods.network.RemotePlayer;

public class DrawMapPacket extends SpirePacket {

	// Coop specific
	
	@Override
	public void onDataReceived(ByteBuffer data, RemotePlayer playerInfo) {
		if (playerInfo.isUser(TogetherManager.currentUser)) { return; }

		float xSize = playerInfo.drawable[playerInfo.act-1].pixmap.getWidth();
		float ySize = playerInfo.drawable[playerInfo.act-1].pixmap.getHeight();

		Vector2 curr = new Vector2(data.getFloat(4)  * xSize, data.getFloat(8)  * ySize);
		Vector2 last = new Vector2(data.getFloat(12) * xSize, data.getFloat(16) * ySize);

		playerInfo.drawable[playerInfo.act-1].brushSize = data.getFloat(20);
		float offset = data.getFloat(24) * ySize;

		if (last.x == 0f && last.y == 0f)
			playerInfo.drawable[playerInfo.act-1].draw(curr, offset);
		else
			playerInfo.drawable[playerInfo.act-1].drawLerped(curr, last, offset);

		playerInfo.drawable[playerInfo.act-1].dirty = true;		
	}

	@Override
	public ByteBuffer generatePacketData() {
		ByteBuffer data = ByteBuffer.allocateDirect(28);
		MapCanvas c = TogetherManager.getCurrentUser().drawable[AbstractDungeon.actNum-1];
		if (c.pointQueue.size() == 0) { return null; }

		Vector2[] points = c.pointQueue.remove(0);
		float xSize = c.pixmap.getWidth();
		float ySize = c.pixmap.getHeight();

		data.putFloat(4, points[0].x / xSize);
		data.putFloat(8, points[0].y / ySize);

		if (points[1] != null) {
			data.putFloat(12, points[1].x / xSize);
			data.putFloat(16, points[1].y / ySize);
		} else {
			data.putFloat(12, 0f);
			data.putFloat(16, 0f);
		}

		data.putFloat(20, c.brushSize);
		data.putFloat(24, DungeonMapScreen.offsetY / ySize);
		return data;
	}

}
