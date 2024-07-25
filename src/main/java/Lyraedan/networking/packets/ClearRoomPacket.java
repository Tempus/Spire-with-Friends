package Lyraedan.networking.packets;

import java.nio.ByteBuffer;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

import chronoMods.TogetherManager;
import chronoMods.coop.CoopEmptyRoom;
import chronoMods.coop.CoopMultiRoom;
import chronoMods.network.RemotePlayer;

public class ClearRoomPacket extends SpirePacket {

	// Coop specific
	
	@Override
	public void onDataReceived(ByteBuffer data, RemotePlayer playerInfo) {
		int xc = data.getInt(4);
		int yc = data.getInt(8);
		if (xc != -1 && yc != -1 && yc < 16 && !AbstractDungeon.id.equals("TheEnding")) {
            MapRoomNode currentNodec = AbstractDungeon.map.get(yc).get(xc);

            // Safety first? This triggers if games are desynced, but I hate getting reports about it.
            if (currentNodec == null) 			{ return; }
            if (currentNodec.getRoom() == null) { return; }

            // Unlocks a room we are leaving
			CoopEmptyRoom.LockedRoomField.locked.set(currentNodec.getRoom(), false);
		
			// Sets the next room of a multi-room
			AbstractRoom secondRoom = CoopMultiRoom.secondRoomField.secondRoom.get(currentNodec);
			AbstractRoom thirdRoom  = CoopMultiRoom.thirdRoomField.thirdRoom.get(currentNodec);

			// Resolve the multinodes by advancing the 'queue' 
			currentNodec.room = secondRoom;
			CoopMultiRoom.secondRoomField.secondRoom.set(currentNodec, thirdRoom);
			CoopMultiRoom.thirdRoomField.thirdRoom.set(currentNodec, null);

			if (currentNodec.room == null)
				currentNodec.setRoom(new CoopEmptyRoom());

			// Blue Ladder Edges
			// BlueLadder.addLadderEdges(currentNodec, xc, yc);
		}

		TogetherManager.log("Clearing: " + xc + ", " + yc);
	}

	@Override
	public ByteBuffer generatePacketData() {
		ByteBuffer data = ByteBuffer.allocateDirect(12);
		data.putInt(4, AbstractDungeon.getCurrMapNode().x);
		data.putInt(8, AbstractDungeon.getCurrMapNode().y);
		return data;
	}

}
