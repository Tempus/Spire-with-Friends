package Lyraedan.networking.packets;

import java.nio.ByteBuffer;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.map.MapRoomNode;

import chronoMods.TogetherManager;
import chronoMods.coop.CoopEmptyRoom;
import chronoMods.network.RemotePlayer;
import chronoMods.network.SendDataPatches;

public class LockRoomPacket extends SpirePacket {

	// Coop specific
	
	@Override
	public void onDataReceived(ByteBuffer data, RemotePlayer playerInfo) {
		int xl = data.getInt(4);
		int yl = data.getInt(8);
		try {
			if (xl != -1 && yl != -1 && yl < 16 && !AbstractDungeon.id.equals("TheEnding")) {
	            MapRoomNode currentNodel = AbstractDungeon.map.get(yl).get(xl);

				CoopEmptyRoom.LockedRoomField.locked.set(currentNodel.getRoom(), true);
			}
		} catch (Exception e) {}
		TogetherManager.log("Locking: " + xl + ", " + yl);
	}

	@Override
	public ByteBuffer generatePacketData() {
		ByteBuffer data = ByteBuffer.allocateDirect(12);
		data.putInt(4, SendDataPatches.lockX);
		data.putInt(8, SendDataPatches.lockY);
		return data;
	}

}
