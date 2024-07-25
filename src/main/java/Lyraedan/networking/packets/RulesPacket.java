package Lyraedan.networking.packets;

import java.nio.Buffer;
import java.nio.ByteBuffer;

import com.evacipated.cardcrawl.modthespire.Loader;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;

import chronoMods.TogetherManager;
import chronoMods.network.NetworkHelper;
import chronoMods.network.RemotePlayer;
import chronoMods.ui.deathScreen.NewDeathScreenPatches;
import chronoMods.ui.mainMenu.NewMenuButtons;
import downfall.patches.EvilModeCharacterSelect;

public class RulesPacket extends SpirePacket {

	@Override
	public void onDataReceived(ByteBuffer data, RemotePlayer playerInfo) {
		// Backup plan for slow loaders?
		if (NewMenuButtons.newGameScreen == null || NewMenuButtons.newGameScreen.ascensionSelectWidget == null) { return; }
		if (CardCrawlGame.isInARun()) { return; } // Fix for rules changing during the middle of bingo somehow

		int character = data.getInt(4);
		if (TogetherManager.gameMode == TogetherManager.mode.Versus) {
			NewMenuButtons.newGameScreen.characterSelectWidget.selectOption(character);
		}
		playerInfo.character = NewMenuButtons.newGameScreen.characterSelectWidget.options.get(character).c;

		// Ascension
		NewMenuButtons.newGameScreen.ascensionSelectWidget.ascensionLevel = data.getInt(8);
		if (NewMenuButtons.newGameScreen.ascensionSelectWidget.ascensionLevel == 0) {
			NewMenuButtons.newGameScreen.ascensionSelectWidget.isAscensionMode = false;
		} else {
			NewMenuButtons.newGameScreen.ascensionSelectWidget.isAscensionMode = true;
		}

		// toggle boxes
		boolean heart = data.getInt(12)>0 ? true : false;
		NewMenuButtons.newGameScreen.heartToggle.setTicked(heart);
        Settings.isFinalActAvailable = heart;

        boolean neow = data.getInt(16)>0 ? true : false;
		NewMenuButtons.newGameScreen.neowToggle.setTicked(neow);
        Settings.isTrial = !neow;

		boolean lament = data.getInt(20)>0 ? true : false;
		NewMenuButtons.newGameScreen.lamentToggle.setTicked(lament);
		if (lament) {
			NewMenuButtons.newGameScreen.neowToggle.setTicked(true);
            Settings.isTrial = false;
		}
        Settings.isTestingNeow = lament;

		boolean ironman = data.getInt(24)>0 ? true : false;
		NewMenuButtons.newGameScreen.ironmanToggle.setTicked(ironman);
        NewDeathScreenPatches.Ironman = ironman;

		boolean downfall = data.getInt(28)>0 ? true : false;
		NewMenuButtons.newGameScreen.downfallToggle.setTicked(downfall);
		if (Loader.isModLoaded("downfall"))
			EvilModeCharacterSelect.evilMode = downfall;

		boolean hardmode = data.getInt(32)>0 ? true : false;
		NewMenuButtons.newGameScreen.hardToggle.setTicked(hardmode);

		// seed
		Settings.seed = data.getLong(36);

		((Buffer)data).position(44);
		for (int b = 0; b < NewMenuButtons.customScreen.getActiveModData().size(); b++) {
			if (data.hasRemaining()) {
				if (data.get() == (byte)1) {
					NewMenuButtons.customScreen.modList.get(b).selected = true;
					TogetherManager.log("Selected: " + NewMenuButtons.customScreen.modList.get(b).name);
				}
				else {
					NewMenuButtons.customScreen.modList.get(b).selected = false;
					TogetherManager.log("Unselect: " + NewMenuButtons.customScreen.modList.get(b).name);
				}
			}
		}
		NewMenuButtons.customScreen.updateValues();

		// Update version every time you recieve a rules, as a fallback
		NetworkHelper.sendData(NetworkHelper.dataType.Version);
		// TogetherManager.log("Updated rules with Char " + data.getInt(4) + ", Asc " + data.getInt(8) + ", and seed " + data.getLong(12));
	}

	@Override
	public ByteBuffer generatePacketData() {
		if (!TogetherManager.currentLobby.isOwner()) { return null; }

		ByteBuffer data = ByteBuffer.allocateDirect(48 + NewMenuButtons.customScreen.getActiveModData().size());
		// Rules are character, ascension, seed
		data.putInt(4, NewMenuButtons.newGameScreen.characterSelectWidget.getChosenOption());

		if (NewMenuButtons.newGameScreen.ascensionSelectWidget.isAscensionMode)
			data.putInt(8, NewMenuButtons.newGameScreen.ascensionSelectWidget.ascensionLevel);
		else
			data.putInt(8, 0);

		data.putInt(12, NewMenuButtons.newGameScreen.heartToggle.getTicked());
		data.putInt(16, NewMenuButtons.newGameScreen.neowToggle.getTicked());
		data.putInt(20, NewMenuButtons.newGameScreen.lamentToggle.getTicked());
		data.putInt(24, NewMenuButtons.newGameScreen.ironmanToggle.getTicked());
		data.putInt(28, NewMenuButtons.newGameScreen.downfallToggle.getTicked());
		data.putInt(32, NewMenuButtons.newGameScreen.hardToggle.getTicked());

		if (Settings.seed != null){
			data.putLong(36, Settings.seed);
		} else {
			data.putLong(36, 0);
		}

		((Buffer)data).position(44);
		for (boolean on : NewMenuButtons.customScreen.getActiveModData()) {
			if (on)
				data.put((byte)1);
			else
				data.put((byte)0);
		}
		
		data.putInt(48, NewMenuButtons.newGameScreen.loseMaxHPOnDeathToggle.getTicked());
		((Buffer)data).rewind();

		NetworkHelper.updateLobbyData();
		return data;
	}

}
