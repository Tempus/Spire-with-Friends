package Lyraedan.networking.packets;

import java.nio.ByteBuffer;

import com.megacrit.cardcrawl.helpers.SeedHelper;

import chronoMods.bingo.Caller;
import chronoMods.network.RemotePlayer;
import chronoMods.ui.mainMenu.NewMenuButtons;

public class BingoRulesPacket extends SpirePacket {

	@Override
	public void onDataReceived(ByteBuffer data, RemotePlayer playerInfo) {
		// Select the difficulty
		int difficultyIndex = data.getInt(4);
		if (NewMenuButtons.newGameScreen.bingoDifficulty.getSelectedIndex() != difficultyIndex)
			NewMenuButtons.newGameScreen.bingoDifficulty.setSelectedIndex(difficultyIndex);

		// Teams were turned on, set yourself to a team and spread the word.
		boolean teams = data.getInt(8)>0 ? true : false;
		NewMenuButtons.newGameScreen.teamsToggle.setTicked(teams);
		// if (teams) {
		// 	TogetherManager.getCurrentUser().team = NewMenuButtons.newGameScreen.playerList.getOwnIndex() / 2;

		// 	NetworkHelper.sendData(NetworkHelper.dataType.TeamChange);
		// }

		// Unique board or not
		boolean unique = data.getInt(12)>0 ? true : false;
		NewMenuButtons.newGameScreen.uniqueBoardToggle.setTicked(unique);

		// Blackout or not
			boolean blackout = data.getInt(16)>0 ? true : false;
			NewMenuButtons.newGameScreen.blackoutToggle.setTicked(blackout);

			Caller.bingoSeed = data.getLong(20);
	}

	@Override
	public ByteBuffer generatePacketData() {
		ByteBuffer data = ByteBuffer.allocateDirect(28);
		data.putInt(4, NewMenuButtons.newGameScreen.bingoDifficulty.getSelectedIndex());
		data.putInt(8, NewMenuButtons.newGameScreen.teamsToggle.getTicked());
		data.putInt(12, NewMenuButtons.newGameScreen.uniqueBoardToggle.getTicked());
		data.putInt(16, NewMenuButtons.newGameScreen.blackoutToggle.getTicked());

		long sourceTime = System.nanoTime();
		com.megacrit.cardcrawl.random.Random rng = new com.megacrit.cardcrawl.random.Random(Long.valueOf(sourceTime));
		data.putLong(20, Long.valueOf(SeedHelper.generateUnoffensiveSeed(rng)));
		return data;
	}

}
