package chronoMods.coop.hardmode;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.*;
import com.megacrit.cardcrawl.vfx.BorderFlashEffect;
import com.megacrit.cardcrawl.vfx.combat.TimeWarpTurnEndEffect;

public class TimeWarpHeartPower extends AbstractPower {
  public static final String POWER_ID = "Time Warp Heart";
  
  private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings("Time Warp");
  
  public static final String NAME = powerStrings.NAME;
  
  public static final String[] DESC = powerStrings.DESCRIPTIONS;
  
  private static int STR_AMT = 1;
  
  private static int COUNTDOWN_AMT = 12;
  
  public TimeWarpHeartPower(AbstractCreature owner) {
    this.name = NAME;
    this.ID = "Time Warp";
    this.owner = owner;
    this.amount = 0;
    updateDescription();
    loadRegion("time");
    this.type = AbstractPower.PowerType.BUFF;
  }
  
  public void playApplyPowerSfx() {
    CardCrawlGame.sound.play("POWER_TIME_WARP", 0.05F);
  }
  
  public void updateDescription() {
    this.description = DESC[0] + COUNTDOWN_AMT + DESC[1] + STR_AMT + DESC[2];
  }
  
  public void onAfterUseCard(AbstractCard card, UseCardAction action) {
    flashWithoutSound();
    this.amount++;
    if (this.amount == COUNTDOWN_AMT) {
      this.amount = 0;
      playApplyPowerSfx();
      AbstractDungeon.actionManager.callEndTurnEarlySequence();
      CardCrawlGame.sound.play("POWER_TIME_WARP", 0.05F);
      AbstractDungeon.effectsQueue.add(new BorderFlashEffect(Color.GOLD, true));
      AbstractDungeon.topLevelEffectsQueue.add(new TimeWarpTurnEndEffect());
      for (AbstractMonster m : (AbstractDungeon.getMonsters()).monsters)
        addToBot(new ApplyPowerAction(m, m, new StrengthPower(m, STR_AMT), STR_AMT));
    } 
    updateDescription();
  }
}