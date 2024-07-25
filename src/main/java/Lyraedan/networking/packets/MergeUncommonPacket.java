package Lyraedan.networking.packets;

import java.nio.Buffer;
import java.nio.ByteBuffer;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;

import chronoMods.TogetherManager;
import chronoMods.coop.CoopNeowReward;
import chronoMods.network.RemotePlayer;

public class MergeUncommonPacket extends SpirePacket {

	// Coop specific
	
	@Override
	public void onDataReceived(ByteBuffer data, RemotePlayer playerInfo) {
		if (playerInfo.isUser(TogetherManager.currentUser)) { return; }

		// Extract the string
		((Buffer)data).position(4);
		byte[] bytesmu = new byte[data.remaining()];
		data.get(bytesmu);
		String stringOutmu = new String(bytesmu);

		AbstractCard theirCard = CardLibrary.getCopy(stringOutmu, 0, 0);
		CoopNeowReward.mergeWaitCard = theirCard;
	}

	@Override
	public ByteBuffer generatePacketData() {
		AbstractCard cu = AbstractDungeon.player.masterDeck.group.get(AbstractDungeon.player.masterDeck.group.size()-1);
		String mergeCardu = cu.cardID;

		ByteBuffer data = ByteBuffer.allocateDirect(4 + mergeCardu.getBytes().length);

		((Buffer)data).position(4);
		data.put(mergeCardu.getBytes());
		((Buffer)data).rewind();
		return data;

	}

}
