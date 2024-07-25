package Lyraedan.networking.packets;

import java.nio.ByteBuffer;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.ui.DialogWord;
import com.megacrit.cardcrawl.vfx.ObtainKeyEffect;
import com.megacrit.cardcrawl.vfx.SpeechTextEffect;

import chronoMods.TogetherManager;
import chronoMods.coop.CoopKeySharing;
import chronoMods.network.RemotePlayer;

public class GetBlueKeyPacket extends SpirePacket {

	// Coop specific
	
	@Override
	public void onDataReceived(ByteBuffer data, RemotePlayer playerInfo) {
		long steamIDbk = data.getLong(4);

		for (RemotePlayer playerbk : TogetherManager.players) {
			if (playerbk.isUser(steamIDbk))
				playerbk.sapphireKey = true;
		}

		if (TogetherManager.currentUser.isUser(steamIDbk) && !Settings.hasSapphireKey) {
			AbstractDungeon.topLevelEffects.add(new ObtainKeyEffect(ObtainKeyEffect.KeyColor.BLUE)); 
			AbstractDungeon.topLevelEffects.add(new SpeechTextEffect(Settings.WIDTH/2.0f, Settings.HEIGHT/2.0f, 5f, "#b" + playerInfo.userName + CardCrawlGame.languagePack.getUIString("Keys").TEXT[1], DialogWord.AppearEffect.FADE_IN));
		}
	}

	@Override
	public ByteBuffer generatePacketData() {
		ByteBuffer data = ByteBuffer.allocateDirect(16);
		data.putLong(4, CoopKeySharing.blueKeyPlayer.getAccountID());
		return data;
	}

}
