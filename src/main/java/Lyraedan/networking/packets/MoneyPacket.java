package Lyraedan.networking.packets;

import java.nio.ByteBuffer;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import chronoMods.TogetherManager;
import chronoMods.network.RemotePlayer;

public class MoneyPacket extends SpirePacket {

	@Override
	public void onDataReceived(ByteBuffer data, RemotePlayer playerInfo) {
		int Money = data.getInt(4);

		if (AbstractDungeon.player != null) {
            if (TogetherManager.gameMode == TogetherManager.mode.Coop && AbstractDungeon.player.hasBlight("DimensionalWallet")) {
            	AbstractDungeon.player.gold = Money;
            	for (RemotePlayer playergld : TogetherManager.players) {
            		playergld.gold = Money;
            	}
            }
        }

		playerInfo.gold = Money;
		TogetherManager.log("Gold: " + Money);
	}

	@Override
	public ByteBuffer generatePacketData() {
		ByteBuffer data = ByteBuffer.allocateDirect(8);
		data.putInt(4, AbstractDungeon.player.gold);
		return data;
	}

}
