package Lyraedan.networking.packets;

import java.nio.ByteBuffer;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import chronoMods.coop.hardmode.StrangeFlame;
import chronoMods.network.RemotePlayer;

public class LastBossPacket extends SpirePacket {

	@Override
	public void onDataReceived(ByteBuffer data, RemotePlayer playerInfo) {
		playerInfo.lastBoss = playerInfo.act;
	}

	@Override
	public ByteBuffer generatePacketData() {
		ByteBuffer data = ByteBuffer.allocateDirect(4);
		if (StrangeFlame.isFirst())
			StrangeFlame.fightingBoss = AbstractDungeon.actNum;
		return data;
	}

}
