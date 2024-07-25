package Lyraedan.networking.packets;

import java.nio.ByteBuffer;

import chronoMods.network.RemotePlayer;
import chronoMods.ui.mainMenu.NewMenuButtons;

public class CharacterPacket extends SpirePacket {

	@Override
	public void onDataReceived(ByteBuffer data, RemotePlayer playerInfo) {
		// Extract the string
		try {
			playerInfo.character = NewMenuButtons.newGameScreen.characterSelectWidget.options.get(data.getInt(4)).c;
		} catch (Exception e) {
			
		}		
	}

	@Override
	public ByteBuffer generatePacketData() {
		ByteBuffer data = ByteBuffer.allocateDirect(8);
		data.putInt(4, NewMenuButtons.newGameScreen.characterSelectWidget.getChosenOption());
		// String characterName = NewMenuButtons.newGameScreen.characterSelectWidget.getChosenOptionLocalizedName();
		// data = ByteBuffer.allocateDirect(4 + characterName.getBytes().length);

		// ((Buffer)data).position(4);
		// data.put(characterName.getBytes());
		// ((Buffer)data).rewind();
		return data;
	}

}
