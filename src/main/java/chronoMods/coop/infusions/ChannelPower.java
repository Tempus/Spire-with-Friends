package chronoMods.coop.infusions;

import com.megacrit.cardcrawl.actions.*;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.defect.*;
import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.orbs.*;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.*;

import chronoMods.coop.relics.*;

public class ChannelPower extends AbstractPower {
  public static final String POWER_ID = "Channel";

  public ChannelPower(AbstractCreature owner) {
    this(owner, 1);
  }
  
  public ChannelPower(AbstractCreature owner, int amt) {
    this.name = CardCrawlGame.languagePack.getUIString("Inf:Plasma").EXTRA_TEXT[1];
    this.ID = "Channel";
    this.owner = AbstractDungeon.player;
    this.amount = amt;
    updateDescription();
    loadRegion("skillBurn");
  }
  
  public void updateDescription() {
    this.description = CardCrawlGame.languagePack.getUIString("Inf:Plasma").EXTRA_TEXT[2];
  }
  
  public void atStartOfTurn() {
    AbstractDungeon.actionManager.addToBottom(new ChannelAction(AbstractOrb.getRandomOrb(true))); 
  }
    
  public void stackPower(int stackAmount) {
    this.amount += stackAmount;
  }
}
