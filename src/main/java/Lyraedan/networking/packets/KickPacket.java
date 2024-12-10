package Lyraedan.networking.packets;

import java.nio.ByteBuffer;

import com.megacrit.cardcrawl.core.CardCrawlGame;

import chronoMods.TogetherManager;
import chronoMods.network.NetworkHelper;
import chronoMods.network.RemotePlayer;
import chronoMods.ui.lobby.NewGameScreen;

public class KickPacket extends SpirePacket {

	@Override
	public void onDataReceived(ByteBuffer data, RemotePlayer playerInfo) {
		long steamIDk = data.getLong(4);
		if (TogetherManager.currentUser.isUser(steamIDk)) {
			NetworkHelper.leaveLobby();
			TogetherManager.infoPopup.show(CardCrawlGame.languagePack.getUIString("Network").TEXT[0], CardCrawlGame.languagePack.getUIString("Network").TEXT[1]);
		} else {
			RemotePlayer kickID = null;
			for (RemotePlayer playerkick : TogetherManager.players) {
				if (playerkick.getAccountID() == steamIDk)
					kickID = playerkick;
			}
			if (kickID != null)
				NetworkHelper.removePlayer(kickID);			
		}
	}

	@Override
	public ByteBuffer generatePacketData() {
		ByteBuffer data = ByteBuffer.allocateDirect(16);
		data.putLong(4, NewGameScreen.kick.getAccountID());
		return data;
	}

}
