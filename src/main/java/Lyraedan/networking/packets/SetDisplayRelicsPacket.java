package Lyraedan.networking.packets;

import java.nio.Buffer;
import java.nio.ByteBuffer;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import chronoMods.TogetherManager;
import chronoMods.network.RemotePlayer;

public class SetDisplayRelicsPacket extends SpirePacket {

	@Override
	public void onDataReceived(ByteBuffer data, RemotePlayer playerInfo) {
		// Extract the string
		byte[] bytes = new byte[data.remaining()];
		data.get(bytes);
		String stringOut = new String(bytes);

		// Clear
		playerInfo.displayRelics.clear();

		// Make the relic
		for (String relicID : stringOut.split(",")) {
			if (!relicID.equals("")) {
				AbstractRelic relic = RelicLibrary.getRelic(relicID).makeCopy();
				relic.isAnimating = true;
				playerInfo.displayRelics.add(relic);
				TogetherManager.log("Display Relic: " + relicID);
			}
		}
	}

	@Override
	public ByteBuffer generatePacketData() {
		String relicID = "";

		for (AbstractRelic relic : AbstractDungeon.player.relics) {
			if (relic.tier == AbstractRelic.RelicTier.STARTER || relic.tier == AbstractRelic.RelicTier.BOSS) {
				relicID += relic.relicId + ",";
			}
		}

		if (relicID.length() > 1) {
			relicID = relicID.substring(0, relicID.length() - 1);
		}
		ByteBuffer data = ByteBuffer.allocateDirect(4 + relicID.getBytes().length);

		((Buffer)data).position(4);
		data.put(relicID.getBytes());
		((Buffer)data).rewind();
		return data;
	}

}
