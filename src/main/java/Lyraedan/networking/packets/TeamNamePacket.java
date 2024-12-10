package Lyraedan.networking.packets;

import java.nio.Buffer;
import java.nio.ByteBuffer;

import chronoMods.TogetherManager;
import chronoMods.network.RemotePlayer;

public class TeamNamePacket extends SpirePacket {

	// Coop specific
	
	@Override
	public void onDataReceived(ByteBuffer data, RemotePlayer playerInfo) {
		int teamBt = data.getInt(4);

		// Extract the string
		((Buffer)data).position(8);
		byte[] bytesbt = new byte[data.remaining()];
		data.get(bytesbt);
		String stringOutbt = new String(bytesbt);

		for (RemotePlayer bingoUser : TogetherManager.players) {
			if (bingoUser.team == teamBt) {
				bingoUser.teamName = stringOutbt;
			}
		}

	}

	@Override
	public ByteBuffer generatePacketData() {
		ByteBuffer data = ByteBuffer.allocateDirect(8 + TogetherManager.getCurrentUser().teamName.getBytes().length);
		data.putInt(4, TogetherManager.getCurrentUser().team);

		((Buffer)data).position(8);
		data.put(TogetherManager.getCurrentUser().teamName.getBytes());
		((Buffer)data).rewind();
		return data;

	}

}
