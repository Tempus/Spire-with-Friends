package Lyraedan.networking.packets;

import java.nio.ByteBuffer;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import chronoMods.TogetherManager;
import chronoMods.bingo.BingoPanelCompleteNotification;
import chronoMods.bingo.Caller;
import chronoMods.bingo.SendBingoPatches;
import chronoMods.network.RemotePlayer;
import chronoMods.ui.deathScreen.EndScreenBingoVictory;
import chronoMods.ui.deathScreen.NewDeathScreenPatches;
import chronoMods.ui.hud.BingoPlayerWidget;

public class BingoPacket extends SpirePacket {

	@Override
	public void onDataReceived(ByteBuffer data, RemotePlayer playerInfo) {
		for (RemotePlayer bingoUser : TogetherManager.players) {
			boolean marked = Caller.markCard(playerInfo, data.getInt(4));

			int victory = Caller.isWin(playerInfo.bingoCard);
			if (victory > 0) {
				((BingoPlayerWidget)playerInfo.widget).winningLine = victory;
	            NewDeathScreenPatches.EndScreenBase = new EndScreenBingoVictory(AbstractDungeon.getCurrRoom().monsters, playerInfo);
	            AbstractDungeon.screen = NewDeathScreenPatches.Enum.RACEEND;
	        }

			if (marked) {
				((BingoPlayerWidget)playerInfo.widget).flash();

				if (bingoUser.team == playerInfo.team) {
					Caller.notifications.add(new BingoPanelCompleteNotification(data.getInt(4), playerInfo));
				}
			}
		}
	}

	@Override
	public ByteBuffer generatePacketData() {
		ByteBuffer data = ByteBuffer.allocateDirect(8);
		data.putInt(4, SendBingoPatches.lastBingo);
		return data;
	}

}
