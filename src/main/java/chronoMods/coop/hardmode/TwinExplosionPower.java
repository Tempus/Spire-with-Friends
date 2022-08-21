package chronoMods.coop.hardmode;

import com.megacrit.cardcrawl.actions.*;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.utility.*;
import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.*;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.common.SuicideAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.combat.ExplosionSmallEffect;

import chronoMods.coop.relics.*;

public class TwinExplosionPower extends AbstractPower {
  public static final String POWER_ID = "TwinExplosion";
    
  public AbstractCreature friend;

  public TwinExplosionPower(AbstractCreature owner, AbstractCreature friend) {
    this.name = StrangeFlame.DESCRIPTIONS[13];
    this.ID = POWER_ID;
    this.owner = owner;
    this.amount = friend.currentHealth;
    this.friend = friend;
    updateDescription();
    loadRegion("explosive");
  }
  
  public void updateDescription() {
    this.description = StrangeFlame.DESCRIPTIONS[14] + friend.currentHealth + StrangeFlame.DESCRIPTIONS[15] + friend.name + StrangeFlame.DESCRIPTIONS[16];
  }
  
  public void updateValue() {
    this.amount = friend.currentHealth;
    updateDescription();
  }
  
  public float atDamageFinalReceive(float damage, DamageInfo.DamageType type) {
    ((TwinExplosionPower)friend.getPower("TwinExplosion")).updateValue();

    return damage;
  }

  public void onDeath() {
    addToBot(new VFXAction(new ExplosionSmallEffect(this.owner.hb.cX, this.owner.hb.cY), 0.1F));
    DamageInfo damageInfo = new DamageInfo(this.owner, friend.currentHealth, DamageInfo.DamageType.THORNS);
    addToBot(new DamageAction(AbstractDungeon.player, damageInfo, AbstractGameAction.AttackEffect.FIRE, true));
    addToBot(new RemoveSpecificPowerAction(friend, owner, POWER_ID));
  }
}
