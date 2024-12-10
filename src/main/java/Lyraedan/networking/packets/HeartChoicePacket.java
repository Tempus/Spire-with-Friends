package Lyraedan.networking.packets;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.CampfireUI;
import com.megacrit.cardcrawl.rooms.RestRoom;

import basemod.ReflectionHacks;
import chronoMods.coop.hardmode.HardModeHeart;
import chronoMods.coop.hardmode.HearthOption;
import chronoMods.network.RemotePlayer;

public class HeartChoicePacket extends SpirePacket {

	@Override
	public void onDataReceived(ByteBuffer data, RemotePlayer playerInfo) {
		playerInfo.heartChosen = HearthOption.Options.values()[data.getInt(4)];

		if (AbstractDungeon.actNum == 4 && AbstractDungeon.player.hasBlight("StrangeFlame"))
			if (AbstractDungeon.getCurrRoom() instanceof RestRoom)
				for (HearthOption h : (ArrayList<HearthOption>)ReflectionHacks.getPrivate(((RestRoom)(AbstractDungeon.getCurrRoom())).campfireUI, CampfireUI.class, "buttons"))
					h.checkUsable();

	}

	@Override
	public ByteBuffer generatePacketData() {
		ByteBuffer data = ByteBuffer.allocateDirect(8);
		data.putInt(4, HardModeHeart.HeartChoice);
		return data;
	}

}
