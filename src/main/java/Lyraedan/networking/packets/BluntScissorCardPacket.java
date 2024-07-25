package Lyraedan.networking.packets;

import java.nio.Buffer;
import java.nio.ByteBuffer;

import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;

import chronoMods.TogetherManager;
import chronoMods.coop.relics.BluntScissors;
import chronoMods.network.RemotePlayer;

public class BluntScissorCardPacket extends SpirePacket {

	// Coop specific
	
	@Override
	public void onDataReceived(ByteBuffer data, RemotePlayer playerInfo) {
		if (playerInfo.isUser(TogetherManager.currentUser)) { return; }

		// Don't always recieve the card. 0.15 chance for every player past the second not to get it
		// 2p = 100%
		// 3p = 85%
		// 4p = 70%
		// 5p = 55%
		// 6p = 40%
		float chanceDecrement = MathUtils.clamp(TogetherManager.players.size()-2 * 0.1f, 0f, 0.5f);
		if (MathUtils.randomBoolean(1.0f - chanceDecrement)) { return; }

		// Get upgrade
		int upgradebs = data.getInt(4);
		int miscbs = data.getInt(8);

		// Extract the string
		((Buffer)data).position(12);
		byte[] bytesbs = new byte[data.remaining()];
		data.get(bytesbs);
		String stringOutbs = new String(bytesbs);

		TogetherManager.log("Send card blunt scissors: " + stringOutbs);

		// Add the card and update text
		if (AbstractDungeon.player.hasBlight("BluntScissors")) {
			((BluntScissors)AbstractDungeon.player.getBlight("BluntScissors")).cardsToMerge.add(CardLibrary.getCopy(stringOutbs, upgradebs, miscbs));
			((BluntScissors)AbstractDungeon.player.getBlight("BluntScissors")).updateDescription();
		}

	}

	@Override
	public ByteBuffer generatePacketData() {
		String mergeCard = BluntScissors.cardSent.cardID;

		ByteBuffer data = ByteBuffer.allocateDirect(12 + mergeCard.getBytes().length);

		data.putInt(4, BluntScissors.cardSent.timesUpgraded);
		data.putInt(8, BluntScissors.cardSent.misc);

		((Buffer)data).position(12);
		data.put(mergeCard.getBytes());
		((Buffer)data).rewind();

		BluntScissors.cardSent = null;
		return data;
	}

}
