package Lyraedan.networking.packets;

import java.nio.ByteBuffer;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.combat.DamageNumberEffect;
import com.megacrit.cardcrawl.vfx.combat.HealNumberEffect;

import chronoMods.TogetherManager;
import chronoMods.network.RemotePlayer;

public class HpPacket extends SpirePacket {

	@Override
	public void onDataReceived(ByteBuffer data, RemotePlayer playerInfo) {
		int Hp = data.getInt(4);
		int maxHp = data.getInt(8);

		if (AbstractDungeon.player != null) {
			if (AbstractDungeon.player.hasBlight("MirrorTouch")) {

				if (!playerInfo.isUser(TogetherManager.currentUser)) {
					if (Hp > AbstractDungeon.player.currentHealth)
						AbstractDungeon.topLevelEffects.add(new HealNumberEffect(playerInfo.widget.x + 64f, playerInfo.widget.y, Hp - AbstractDungeon.player.currentHealth));
					else
						AbstractDungeon.topLevelEffects.add(new DamageNumberEffect(AbstractDungeon.player, playerInfo.widget.x + 64f, playerInfo.widget.y, AbstractDungeon.player.currentHealth - Hp));
				}

				AbstractDungeon.player.currentHealth = Hp;
				AbstractDungeon.player.maxHealth = maxHp;

            	AbstractDungeon.player.healthBarUpdatedEvent();

            	for (RemotePlayer playerhp : TogetherManager.players)
            		playerhp.hp = Hp;
			}
		}

		playerInfo.hp = Hp;
		playerInfo.maxHp = maxHp;
		TogetherManager.log("Player HP: " + Hp);
		
	}

	@Override
	public ByteBuffer generatePacketData() {
		ByteBuffer data = ByteBuffer.allocateDirect(12);
		data.putInt(4, AbstractDungeon.player.currentHealth);
		data.putInt(8, AbstractDungeon.player.maxHealth);
		return data;
	}

}
