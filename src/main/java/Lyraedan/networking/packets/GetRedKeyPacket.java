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

public class GetRedKeyPacket extends SpirePacket {

	// Coop specific
	
	@Override
	public void onDataReceived(ByteBuffer data, RemotePlayer playerInfo) {
		long steamIDrk = data.getLong(4);

		for (RemotePlayer playerrk : TogetherManager.players) {
			if (playerrk.isUser(steamIDrk))
				playerrk.rubyKey = true;
		}

		if (TogetherManager.currentUser.isUser(steamIDrk) && !Settings.hasRubyKey) {
			AbstractDungeon.topLevelEffects.add(new ObtainKeyEffect(ObtainKeyEffect.KeyColor.RED)); 
			AbstractDungeon.topLevelEffects.add(new SpeechTextEffect(Settings.WIDTH/2.0f, Settings.HEIGHT/2.0f, 5f, "#r" + playerInfo.userName + CardCrawlGame.languagePack.getUIString("Keys").TEXT[0], DialogWord.AppearEffect.FADE_IN));
		}

	}

	@Override
	public ByteBuffer generatePacketData() {
		ByteBuffer data = ByteBuffer.allocateDirect(16);
		data.putLong(4, CoopKeySharing.redKeyPlayer.getAccountID());
		return data;
	}

}
