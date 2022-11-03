package chronoMods.ui.deathScreen;

import chronoMods.TogetherManager;
import chronoMods.network.RemotePlayer;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.MonsterGroup;

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