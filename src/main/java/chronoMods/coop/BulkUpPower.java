package chronoMods.coop;

import com.megacrit.cardcrawl.actions.*;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.utility.*;
import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.*;

import chronoMods.coop.relics.*;

public class BulkUpPower extends AbstractPower {
  public static final String POWER_ID = "BulkUp";
  
  public BulkUpPower(AbstractCreature owner) {
    this(owner, 1);
  }
  
  public BulkUpPower(AbstractCreature owner, int amt) {
    this.name = StrangeFlame.DESCRIPTIONS[11];
    this.ID = "BulkUp";
    this.owner = owner;
    this.amount = amt;
    this.basePower = amt;
    updateDescription();
    loadRegion("controlled_change");
  }
  
  public void updateDescription() {
    this.description = StrangeFlame.DESCRIPTIONS[12];
  }
  
  public void atEndOfTurn(boolean isPlayer) {
    if (this.owner.isPlayer)
      return; 
    this.amount = this.basePower;
    updateDescription();
  }
  
  public void atEndOfRound() {
    if (!this.owner.isPlayer)
      return; 
    this.amount = this.basePower;
    updateDescription();
  }
  
  public void onAfterUseCard(AbstractCard card, UseCardAction action) {
      addToBot((AbstractGameAction)new GainBlockAction(this.owner, this.owner, this.amount));
      addToBot((AbstractGameAction)new ApplyPowerAction(this.owner, this.owner, new StrengthPower(this.owner, this.amount), this.amount));
      addToBot((AbstractGameAction)new ApplyPowerAction(this.owner, this.owner, new LoseStrengthPower(this.owner, this.amount), this.amount)); 

      flash();
      this.amount++;
      updateDescription();
  }
  
  public void stackPower(int stackAmount) {
    this.amount += stackAmount;
    this.basePower += stackAmount;
  }
    
  private int basePower;
}
