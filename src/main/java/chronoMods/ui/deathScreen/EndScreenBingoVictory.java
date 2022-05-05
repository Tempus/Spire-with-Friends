package chronoMods.ui.deathScreen;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.core.*;
import com.megacrit.cardcrawl.daily.TimeHelper;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon.*;
import com.megacrit.cardcrawl.helpers.*;
import com.megacrit.cardcrawl.helpers.controller.*;
import com.megacrit.cardcrawl.helpers.input.*;
import com.megacrit.cardcrawl.integrations.*;
import com.megacrit.cardcrawl.localization.*;
import com.megacrit.cardcrawl.metrics.*;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.relics.SpiritPoop;
import com.megacrit.cardcrawl.rooms.*;
import com.megacrit.cardcrawl.saveAndContinue.SaveAndContinue;
import com.megacrit.cardcrawl.screens.stats.*;
import com.megacrit.cardcrawl.screens.*;
import com.megacrit.cardcrawl.ui.buttons.*;
import com.megacrit.cardcrawl.unlock.*;
import com.megacrit.cardcrawl.vfx.*;
import com.megacrit.cardcrawl.audio.MusicMaster;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.*;
import com.megacrit.cardcrawl.core.CardCrawlGame.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.shop.ShopScreen;
import com.megacrit.cardcrawl.integrations.steam.SteamIntegration;

import basemod.*;
import com.codedisaster.steamworks.*;

import com.evacipated.cardcrawl.modthespire.lib.*;

import chronoMods.*;
import chronoMods.network.steam.*;
import chronoMods.network.*;
import chronoMods.ui.deathScreen.*;
import chronoMods.ui.hud.*;
import chronoMods.ui.lobby.*;
import chronoMods.ui.mainMenu.*;
import chronoMods.utilities.*;

public class EndScreenBingoVictory extends EndScreenBase {
	public EndScreenBingoVictory(MonsterGroup m, RemotePlayer player) {
		super(m);

		returnButton.appear(Settings.WIDTH / 2f, Settings.HEIGHT * 0.15f, msg[0], true);
		if (player.teamName != "")
			AbstractDungeon.dynamicBanner.appear(player.teamName + " - " + msg[11]);
		else 
			AbstractDungeon.dynamicBanner.appear(player.userName + " - " + msg[11]);

		if (player.team == TogetherManager.getCurrentUser().team) {

			// Play victory SFX
			CardCrawlGame.sound.play("BOSS_VICTORY_STINGER", true); // Or... UNLOCK_SCREEN

			// Play victory BGM
			String bgmKey = null;
			switch (MathUtils.random(0, 3)) {
				case 0:
					bgmKey = "STS_BossVictoryStinger_1_v3_MUSIC.ogg";
					break;
				case 1:
					bgmKey = "STS_BossVictoryStinger_2_v3_MUSIC.ogg";
					break;
				case 2:
					bgmKey = "STS_BossVictoryStinger_3_v3_MUSIC.ogg";
					break;
				case 3:
					bgmKey = "STS_BossVictoryStinger_4_v3_MUSIC.ogg";
					break;
				default:
					break;
			}
			CardCrawlGame.music.playTempBgmInstantly(bgmKey, false);
		} else {
			// Play death SFX
			CardCrawlGame.sound.play("DEATH_STINGER", true);

			// Play death BGM
			String bgmKey = null;
			switch (MathUtils.random(0, 3)) {
				case 0:
					bgmKey = "STS_DeathStinger_1_v3_MUSIC.ogg";
					break;
				case 1:
					bgmKey = "STS_DeathStinger_2_v3_MUSIC.ogg";
					break;
				case 2:
					bgmKey = "STS_DeathStinger_3_v3_MUSIC.ogg";
					break;
				case 3:
					bgmKey = "STS_DeathStinger_4_v3_MUSIC.ogg";
					break;
				default:
					break;
			}
			CardCrawlGame.music.playTempBgmInstantly(bgmKey, false);
		}
	}

	public void reopen() {
		super.reopen();

		AbstractDungeon.dynamicBanner.appearInstantly(TEXT[1]);
		returnButton.appear(Settings.WIDTH / 2f, Settings.HEIGHT * 0.15f, TEXT[34], true);
	}
}