package Lyraedan.networking.packets;

import java.nio.Buffer;
import java.nio.ByteBuffer;

import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.RelicLibrary;

import chronoMods.TogetherManager;
import chronoMods.coop.relics.Dimensioneel;
import chronoMods.network.RemotePlayer;

public class SendRelicPacket extends SpirePacket {

	@Override
	public void onDataReceived(ByteBuffer data, RemotePlayer playerInfo) {
		long steamIDsr = data.getLong(4);
		if (!TogetherManager.currentUser.isUser(steamIDsr)) { return; }

		// Extract the string
		((Buffer)data).position(12);
		byte[] byteSentRelics = new byte[data.remaining()];
		data.get(byteSentRelics);
		String sentRelicID = new String(byteSentRelics);

		// Make the relic
		AbstractDungeon.getCurrRoom().spawnRelicAndObtain(Settings.WIDTH/2.0f, Settings.HEIGHT/2.0f, RelicLibrary.getRelic(sentRelicID).makeCopy());
	}

	@Override
	public ByteBuffer generatePacketData() {
		ByteBuffer data = ByteBuffer.allocateDirect(12 + Dimensioneel.relicID.getBytes().length);
		data.putLong(4, Dimensioneel.sendPlayer.getAccountID()); // Selected recipient

		((Buffer)data).position(12);
		data.put(Dimensioneel.relicID.getBytes());
		((Buffer)data).rewind();
		return data;
	}

}
