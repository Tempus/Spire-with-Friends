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

public class GetGreenKeyPacket extends SpirePacket {

	// Coop specific
	
	@Override
	public void onDataReceived(ByteBuffer data, RemotePlayer playerInfo) {
		long steamIDgk = data.getLong(4);

		for (RemotePlayer playergk : TogetherManager.players) {
			if (playergk.isUser(steamIDgk))
				playergk.emeraldKey = true;
		}

		if (TogetherManager.currentUser.isUser(steamIDgk) && !Settings.hasEmeraldKey) {
			AbstractDungeon.topLevelEffects.add(new ObtainKeyEffect(ObtainKeyEffect.KeyColor.GREEN)); 
			AbstractDungeon.topLevelEffects.add(new SpeechTextEffect(Settings.WIDTH/2.0f, Settings.HEIGHT/2.0f, 5f, "#g" + playerInfo.userName + CardCrawlGame.languagePack.getUIString("Keys").TEXT[2], DialogWord.AppearEffect.FADE_IN));
		}

	}

	@Override
	public ByteBuffer generatePacketData() {
		ByteBuffer data = ByteBuffer.allocateDirect(16);
		data.putLong(4, CoopKeySharing.greenKeyPlayer.getAccountID());
		return data;
	}

}
