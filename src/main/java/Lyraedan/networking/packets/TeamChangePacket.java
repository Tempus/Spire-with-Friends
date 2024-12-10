package Lyraedan.networking.packets;

import java.nio.ByteBuffer;

import chronoMods.TogetherManager;
import chronoMods.network.RemotePlayer;

public class TeamChangePacket extends SpirePacket {

	@Override
	public void onDataReceived(ByteBuffer data, RemotePlayer playerInfo) {
		int newTeam = data.getInt(4);
		for (RemotePlayer bingoUser : TogetherManager.players) {
			if (bingoUser.team == newTeam) {
				playerInfo.teamName = bingoUser.teamName;
			}
		}
		playerInfo.team = newTeam;
	}

	@Override
	public ByteBuffer generatePacketData() {
		ByteBuffer data = ByteBuffer.allocateDirect(8);
		data.putInt(4, TogetherManager.getCurrentUser().team);
		return data;
	}

}
