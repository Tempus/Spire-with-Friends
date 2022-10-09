package chronoMods.coop.infusions;

import com.megacrit.cardcrawl.actions.defect.ChannelAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.powers.AbstractPower;

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
