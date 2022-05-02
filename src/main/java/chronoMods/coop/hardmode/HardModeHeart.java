package chronoMods.coop.hardmode;

import com.evacipated.cardcrawl.modthespire.lib.*;

import com.badlogic.gdx.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;

import com.megacrit.cardcrawl.localization.*;
import com.megacrit.cardcrawl.core.*;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.cards.curses.*;
import com.megacrit.cardcrawl.cards.status.*;
import com.megacrit.cardcrawl.blights.*;
import com.megacrit.cardcrawl.helpers.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.city.*;
import com.megacrit.cardcrawl.events.shrines.*;
import com.megacrit.cardcrawl.rewards.*;
import com.megacrit.cardcrawl.rooms.*;
import com.megacrit.cardcrawl.map.*;
import com.megacrit.cardcrawl.shop.*;
import com.megacrit.cardcrawl.powers.*;
import com.megacrit.cardcrawl.actions.*;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.monsters.beyond.*;
import com.megacrit.cardcrawl.monsters.city.*;
import com.megacrit.cardcrawl.monsters.exordium.*;
import com.megacrit.cardcrawl.monsters.ending.*;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.scene.*;
import com.megacrit.cardcrawl.vfx.campfire.*;
import com.megacrit.cardcrawl.screens.*;

import com.badlogic.gdx.graphics.Color;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDrawPileAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.status.Burn;
import com.megacrit.cardcrawl.cards.status.Dazed;
import com.megacrit.cardcrawl.cards.status.Slimed;
import com.megacrit.cardcrawl.cards.status.VoidCard;
import com.megacrit.cardcrawl.cards.status.Wound;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.HeartAnimListener;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.ArtifactPower;
import com.megacrit.cardcrawl.powers.BeatOfDeathPower;
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.InvinciblePower;
import com.megacrit.cardcrawl.powers.PainfulStabsPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.BorderFlashEffect;
import com.megacrit.cardcrawl.vfx.combat.BloodShotEffect;
import com.megacrit.cardcrawl.vfx.combat.HeartBuffEffect;
import com.megacrit.cardcrawl.vfx.combat.HeartMegaDebuffEffect;
import com.megacrit.cardcrawl.vfx.combat.ViceCrushEffect;

import basemod.*;
import basemod.abstracts.*;
import basemod.interfaces.*;

import java.util.*;

import chronoMods.*;
import chronoMods.network.steam.*;
import chronoMods.network.*;
import chronoMods.coop.*;
import chronoMods.coop.hardmode.*;
import chronoMods.coop.relics.*;
import chronoMods.ui.deathScreen.*;
import chronoMods.ui.hud.*;
import chronoMods.ui.lobby.*;
import chronoMods.ui.mainMenu.*;


import com.megacrit.cardcrawl.actions.utility.*;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.unique.*;
import com.megacrit.cardcrawl.vfx.combat.*;
import com.megacrit.cardcrawl.vfx.cardManip.*;
import com.megacrit.cardcrawl.vfx.*;

public class HardModeHeart {

    public static int HeartChoice = -1;
	public static HashMap<Integer, AbstractMonster> enemySlots = new HashMap<>();

	// Heart Pre-Battle changes
    @SpirePatch(clz = CorruptHeart.class, method="usePreBattleAction")
    public static class emeraldCorruptHeartA {
        public static void Postfix(CorruptHeart __instance) {
            if (AbstractDungeon.player.hasBlight("StrangeFlame")) {
            	if (HearthOption.heartMerge == HearthOption.Options.GUARDIAN)
            		AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(__instance, __instance, new ThornsPower(__instance, 3), 3));

            	if (HearthOption.heartMerge == HearthOption.Options.CHAMP)
            		AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(__instance, __instance, new MetallicizePower(__instance, 10), 10));

            	if (HearthOption.heartMerge == HearthOption.Options.COLLECTOR) {
            		HardModeHeart.enemySlots.clear();

					for (int i = 1; i < 3; i++) {
						AbstractMonster m = new TorchHead(-130f + -185.0F * i, MathUtils.random(-5.0F, 25.0F));
						AbstractDungeon.actionManager.addToBottom(new SFXAction("MONSTER_COLLECTOR_SUMMON"));
						AbstractDungeon.actionManager.addToBottom(new SpawnMonsterAction(m, true));
						HardModeHeart.enemySlots.put(Integer.valueOf(i), m);
					} 
               	}

            	if (HearthOption.heartMerge == HearthOption.Options.AUTOMATON) {
			        if (MathUtils.randomBoolean())
			          AbstractDungeon.actionManager.addToBottom(new SFXAction("AUTOMATON_ORB_SPAWN", MathUtils.random(-0.1F, 0.1F)));
			        else
			          AbstractDungeon.actionManager.addToBottom(new SFXAction("MONSTER_AUTOMATON_SUMMON", MathUtils.random(-0.1F, 0.1F)));

			        AbstractDungeon.actionManager.addToBottom(new SpawnMonsterAction(new BronzeOrb(-300.0F, 200.0F, 0), true));

			        if (MathUtils.randomBoolean())
			          AbstractDungeon.actionManager.addToBottom(new SFXAction("AUTOMATON_ORB_SPAWN", MathUtils.random(-0.1F, 0.1F)));
			        else
			          AbstractDungeon.actionManager.addToBottom(new SFXAction("MONSTER_AUTOMATON_SUMMON", MathUtils.random(-0.1F, 0.1F)));

			        AbstractDungeon.actionManager.addToBottom(new SpawnMonsterAction(new BronzeOrb(200.0F, 130.0F, 1), true));
			    }

            	if (HearthOption.heartMerge == HearthOption.Options.TIMEEATER) 
					AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(__instance, __instance, new TimeWarpHeartPower(__instance)));

            	if (HearthOption.heartMerge == HearthOption.Options.AWAKENED) 
					AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(__instance, __instance, (AbstractPower)new CuriosityPower(__instance, 1)));			}
		}
	}

	// Heart Pre-Status Card giving changes
    @SpirePatch(clz = CorruptHeart.class, method="takeTurn")
    public static class emeraldCorruptHeartB {
    	@SpireInsertPatch(rloc=121-100)
        public static SpireReturn Insert(CorruptHeart __instance) {
            if (AbstractDungeon.player.hasBlight("StrangeFlame")) {

            	if (HearthOption.heartMerge == HearthOption.Options.SLIMEBOSS) {
			        AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDrawPileAction(new Slimed(), 1, true, false, false, Settings.WIDTH * 0.2F, Settings.HEIGHT / 2.0F));
			        AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDrawPileAction(new Slimed(), 1, true, false, false, Settings.WIDTH * 0.35F, Settings.HEIGHT / 2.0F));
			        AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDrawPileAction(new Slimed(), 1, true, false, false, Settings.WIDTH * 0.5F, Settings.HEIGHT / 2.0F));
			        AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDrawPileAction(new Slimed(), 1, true, false, false, Settings.WIDTH * 0.65F, Settings.HEIGHT / 2.0F));
			        AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDrawPileAction(new Slimed(), 1, true, false, false, Settings.WIDTH * 0.8F, Settings.HEIGHT / 2.0F));

	            	AbstractDungeon.actionManager.addToBottom(new RollMoveAction(__instance));
	            	return SpireReturn.Return(null);
            	}

            	if (HearthOption.heartMerge == HearthOption.Options.HEXAGHOST) {
			        AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDrawPileAction(new Burn(), 1, true, false, false, Settings.WIDTH * 0.2F, Settings.HEIGHT / 2.0F));
			        AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDrawPileAction(new Burn(), 1, true, false, false, Settings.WIDTH * 0.35F, Settings.HEIGHT / 2.0F));
			        AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDrawPileAction(new Burn(), 1, true, false, false, Settings.WIDTH * 0.5F, Settings.HEIGHT / 2.0F));
			        AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDrawPileAction(new Burn(), 1, true, false, false, Settings.WIDTH * 0.65F, Settings.HEIGHT / 2.0F));
			        AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDrawPileAction(new Burn(), 1, true, false, false, Settings.WIDTH * 0.8F, Settings.HEIGHT / 2.0F));

	            	AbstractDungeon.actionManager.addToBottom(new RollMoveAction(__instance));
	            	return SpireReturn.Return(null);
            	}

			}

			return SpireReturn.Continue();
		}
	}

	// Heart Buff Action after Strength before other buffs
    @SpirePatch(clz = CorruptHeart.class, method="takeTurn")
    public static class emeraldCorruptHeartC {
    	@SpireInsertPatch(rloc=184-100)
        public static SpireReturn Insert(CorruptHeart __instance) {
            if (AbstractDungeon.player.hasBlight("StrangeFlame")) {

            	if (HearthOption.heartMerge == HearthOption.Options.SLIMEBOSS) {
			        AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDrawPileAction(new Slimed(), 1, true, false, false, Settings.WIDTH * 0.2F, Settings.HEIGHT / 2.0F));
			        AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDrawPileAction(new Slimed(), 1, true, false, false, Settings.WIDTH * 0.35F, Settings.HEIGHT / 2.0F));
			        AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDrawPileAction(new Slimed(), 1, true, false, false, Settings.WIDTH * 0.5F, Settings.HEIGHT / 2.0F));
			        AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDrawPileAction(new Slimed(), 1, true, false, false, Settings.WIDTH * 0.65F, Settings.HEIGHT / 2.0F));
			        AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDrawPileAction(new Slimed(), 1, true, false, false, Settings.WIDTH * 0.8F, Settings.HEIGHT / 2.0F));
			    }

            	if (HearthOption.heartMerge == HearthOption.Options.HEXAGHOST) {
			    	AbstractDungeon.actionManager.addToBottom(new BurnIncreaseAction());
            	}

            	if (HearthOption.heartMerge == HearthOption.Options.CHAMP)
            		AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(__instance, __instance, new MetallicizePower(__instance, 10), 10));

            	if (HearthOption.heartMerge == HearthOption.Options.COLLECTOR) {

			        for (AbstractMonster m : (AbstractDungeon.getCurrRoom()).monsters.monsters)
			          if (!m.isDead && !m.isDying && !m.isEscaping && m.id.equals("TorchHead"))
			            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, __instance, new StrengthPower(m, 7), 7)); 

			        for (Map.Entry<Integer, AbstractMonster> m : HardModeHeart.enemySlots.entrySet()) {
			          if ((m.getValue()).isDying) {
			            AbstractMonster newMonster = new TorchHead( -130.0F + -185.0F * (m.getKey()).intValue(), MathUtils.random(-5.0F, 25.0F));
			            int key = (m.getKey()).intValue();
			            HardModeHeart.enemySlots.put(Integer.valueOf(key), newMonster);
			            AbstractDungeon.actionManager.addToBottom(new SpawnMonsterAction(newMonster, true));
			          } 
			        } 
            	}


			}

			return SpireReturn.Continue();
		}
	}

	// Heart Post Big Single Bash
    @SpirePatch(clz = CorruptHeart.class, method="takeTurn")
    public static class emeraldCorruptHeartD {
    	@SpireInsertPatch(rloc=238-100)
        public static SpireReturn Insert(CorruptHeart __instance) {
            if (AbstractDungeon.player.hasBlight("StrangeFlame")) {

            	if (HearthOption.heartMerge == HearthOption.Options.AUTOMATON) 
            		AbstractDungeon.actionManager.addToBottom(new ApplyStasisAction(__instance));

            	if (HearthOption.heartMerge == HearthOption.Options.DONUDECA) {
			        AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDrawPileAction(new Burn(), 1, true, false, false, Settings.WIDTH * 0.2F, Settings.HEIGHT / 2.0F));
			        AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDrawPileAction(new Dazed(), 1, true, false, false, Settings.WIDTH * 0.35F, Settings.HEIGHT / 2.0F));
			        AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDrawPileAction(new Slimed(), 1, true, false, false, Settings.WIDTH * 0.5F, Settings.HEIGHT / 2.0F));
			        AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDrawPileAction(new VoidCard(), 1, true, false, false, Settings.WIDTH * 0.65F, Settings.HEIGHT / 2.0F));
			        AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDrawPileAction(new Wound(), 1, true, false, false, Settings.WIDTH * 0.8F, Settings.HEIGHT / 2.0F));
			    }
			}

			return SpireReturn.Continue();
		}
	}

	// Bronze Orb fix for Heart
    @SpirePatch(clz = BronzeOrb.class, method="takeTurn")
    public static class bronzeOrbHeart {
    	@SpireInsertPatch(rloc=72-60)
        public static SpireReturn Insert(BronzeOrb __instance) {
            if (AbstractDungeon.player.hasBlight("StrangeFlame") && AbstractDungeon.actNum == 4) {

            	if (HearthOption.heartMerge == HearthOption.Options.AUTOMATON) {
			        AbstractDungeon.actionManager.addToBottom(new GainBlockAction(AbstractDungeon.getMonsters().getMonster("CorruptHeart"), __instance, 12));
					AbstractDungeon.actionManager.addToBottom(new RollMoveAction(__instance));

					return SpireReturn.Return(null);
				}
			}

			return SpireReturn.Continue();
		}
	}
}