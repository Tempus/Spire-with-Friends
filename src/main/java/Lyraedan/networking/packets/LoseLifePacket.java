package Lyraedan.networking.packets;

import java.nio.Buffer;
import java.nio.ByteBuffer;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.MonsterHelper;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

import chronoMods.TogetherManager;
import chronoMods.coop.CoopDeathNotification;
import chronoMods.coop.relics.Tombstone;
import chronoMods.network.NetworkHelper;
import chronoMods.network.RemotePlayer;
import chronoMods.ui.deathScreen.EndScreenCoopLoss;
import chronoMods.ui.deathScreen.NewDeathScreenPatches;

public class LoseLifePacket extends SpirePacket {

	@Override
	public void onDataReceived(ByteBuffer data, RemotePlayer playerInfo) {
		int counter = data.getInt(4);

		if (counter >= 0) {
			// Death notification
			AbstractDungeon.effectList.add(new CoopDeathNotification(playerInfo));

			if (AbstractDungeon.player.hasBlight("StringOfFate")) {
				// If we've ever lost a life, remove the freebie
				if (AbstractDungeon.player.getBlight("StringOfFate").increment > 0) {
					AbstractDungeon.player.getBlight("StringOfFate").increment = 0;
					return;
				}

				// Lower the counter - if we used the freebie the counter will be the same
				AbstractDungeon.player.getBlight("StringOfFate").counter = counter;

				if(NewDeathScreenPatches.MaxHpAffected) {
					AbstractDungeon.player.decreaseMaxHealth(AbstractDungeon.player.maxHealth / 4);
			        if (AbstractDungeon.player.currentHealth > AbstractDungeon.player.maxHealth)
			            AbstractDungeon.player.currentHealth = AbstractDungeon.player.maxHealth;
				}

		    } else if (AbstractDungeon.player.hasBlight("BondsOfFate")) {
				// Lower the counter
				AbstractDungeon.player.getBlight("BondsOfFate").counter = counter;

				((Buffer)data).position(8);
				byte[] bytesll = new byte[data.remaining()];
				data.get(bytesll);
				String killedBy = new String(bytesll);

				if (playerInfo.isUser(TogetherManager.currentUser))
					return;

				TogetherManager.log("Killed by: " + killedBy);
				if (killedBy == null || killedBy == "") {
					AbstractDungeon.topLevelEffects.add(new ShowCardAndObtainEffect(
						new Tombstone(playerInfo.userName, "", playerInfo.getPortrait()), Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
					if (AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT) {
						AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new MakeTempCardInHandAction(new Tombstone(playerInfo.userName, "", playerInfo.getPortrait()), 1)); 
					}
				} else {
					AbstractDungeon.topLevelEffects.add(new ShowCardAndObtainEffect(
						new Tombstone(playerInfo.userName, MonsterHelper.getEncounterName(killedBy), playerInfo.getPortrait()), Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
					if (AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT) {
						AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new MakeTempCardInHandAction(new Tombstone(playerInfo.userName, MonsterHelper.getEncounterName(killedBy), playerInfo.getPortrait()), 1)); 
					}
				}
		    } else if (AbstractDungeon.player.hasBlight("ChainsOfFate")) {
				// If we've ever lost a life, remove the freebie
				if (AbstractDungeon.player.getBlight("ChainsOfFate").increment > 0) {
					AbstractDungeon.player.getBlight("ChainsOfFate").increment = 0;
					return;
				}

				// Lower the counter - if we used the freebie the counter will be the same
				AbstractDungeon.player.getBlight("ChainsOfFate").counter = counter;

				if(NewDeathScreenPatches.MaxHpAffected) {
					AbstractDungeon.player.decreaseMaxHealth(AbstractDungeon.player.maxHealth / 4);
			        if (AbstractDungeon.player.currentHealth > AbstractDungeon.player.maxHealth)
			            AbstractDungeon.player.currentHealth = AbstractDungeon.player.maxHealth;
				}

		        // Also give a Tombstone lol
				((Buffer)data).position(8);
				byte[] bytesll = new byte[data.remaining()];
				data.get(bytesll);
				String killedBy = new String(bytesll);

				if (playerInfo.isUser(TogetherManager.currentUser))
					return;

				TogetherManager.log("Killed by: " + killedBy);
				if (killedBy == null || killedBy == "") {
					AbstractDungeon.topLevelEffects.add(new ShowCardAndObtainEffect(
						new Tombstone(playerInfo.userName, "", playerInfo.getPortrait()), Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
					if (AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT) {
						AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new MakeTempCardInHandAction(new Tombstone(playerInfo.userName, "", playerInfo.getPortrait()), 1)); 
					}
				} else {
					AbstractDungeon.topLevelEffects.add(new ShowCardAndObtainEffect(
						new Tombstone(playerInfo.userName, MonsterHelper.getEncounterName(killedBy), playerInfo.getPortrait()), Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
					if (AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT) {
						AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new MakeTempCardInHandAction(new Tombstone(playerInfo.userName, MonsterHelper.getEncounterName(killedBy), playerInfo.getPortrait()), 1)); 
					}
				}
		    }

		} else {
			// Die
			AbstractDungeon.player.currentHealth = 0;
			AbstractDungeon.player.isDead = true;

            NewDeathScreenPatches.EndScreenBase = new EndScreenCoopLoss(AbstractDungeon.getCurrRoom().monsters);
            AbstractDungeon.screen = NewDeathScreenPatches.Enum.RACEEND;
			}

			NetworkHelper.sendData(NetworkHelper.dataType.Hp);
	}

	@Override
	public ByteBuffer generatePacketData() {
		ByteBuffer data = null;
		if (AbstractDungeon.player.hasBlight("BondsOfFate")){
			if (AbstractDungeon.lastCombatMetricKey != null) {
				String killedBy = AbstractDungeon.lastCombatMetricKey;
				data = ByteBuffer.allocateDirect(8 + killedBy.getBytes().length);
				data.putInt(4, AbstractDungeon.player.getBlight("BondsOfFate").counter);

				((Buffer)data).position(8);
				data.put(killedBy.getBytes());
				((Buffer)data).rewind();
			} else {
				data = ByteBuffer.allocateDirect(8);
				data.putInt(4, AbstractDungeon.player.getBlight("BondsOfFate").counter);
			}
		}
		else if (AbstractDungeon.player.hasBlight("ChainsOfFate")) {
			if (AbstractDungeon.lastCombatMetricKey != null) {
				String killedBy = AbstractDungeon.lastCombatMetricKey;
				data = ByteBuffer.allocateDirect(8 + killedBy.getBytes().length);
				data.putInt(4, AbstractDungeon.player.getBlight("ChainsOfFate").counter);

				((Buffer)data).position(8);
				data.put(killedBy.getBytes());
				((Buffer)data).rewind();
			} else {
				data = ByteBuffer.allocateDirect(8);
				data.putInt(4, AbstractDungeon.player.getBlight("ChainsOfFate").counter);
			}
		}
		else {
			data = ByteBuffer.allocateDirect(8);
			data.putInt(4, AbstractDungeon.player.getBlight("StringOfFate").counter);
		}
		return data;
	}

}
