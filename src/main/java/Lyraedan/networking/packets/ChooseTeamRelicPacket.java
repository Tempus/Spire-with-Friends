package Lyraedan.networking.packets;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import chronoMods.TogetherManager;
import chronoMods.coop.CoopBossRelicSelectScreen;
import chronoMods.network.RemotePlayer;

public class ChooseTeamRelicPacket extends SpirePacket {

	@Override
	public void onDataReceived(ByteBuffer data, RemotePlayer playerInfo) {
		int choicer = data.getInt(4);

		if (playerInfo.isUser(TogetherManager.currentUser)) { return; }

		// Set your current choice
		CoopBossRelicSelectScreen teamScreen = TogetherManager.teamRelicScreen;

		for (ArrayList<RemotePlayer> pList : teamScreen.selected) {
			pList.remove(playerInfo);
		}
		teamScreen.selected.get(choicer).add(playerInfo);

		// Advance if selected
		if (teamScreen.selected.get(choicer).size() == TogetherManager.players.size()) {
			TogetherManager.log("Got a team relic: " + teamScreen.blights.get(choicer).name);
			teamScreen.blights.get(choicer).obtain();
			teamScreen.blights.get(choicer).onEquip();
			teamScreen.blights.get(choicer).isObtained = true;
			TogetherManager.log("Closing up: " + teamScreen.blights.get(choicer).name);
			TogetherManager.teamRelicScreen.blightChoiceComplete();
		}
	}

	@Override
	public ByteBuffer generatePacketData() {
		ByteBuffer data = ByteBuffer.allocateDirect(8);
		data.putInt(4, TogetherManager.teamRelicScreen.selectedIndex);
		return data;
	}

}
