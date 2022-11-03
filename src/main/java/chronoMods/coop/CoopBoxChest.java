package chronoMods.coop;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.rewards.chests.AbstractChest;

public class CoopBoxChest extends AbstractChest {
  public CoopBoxChest() {
    this.img = ImageMaster.loadImage("chrono/images/chests/bag.png");
    this.openedImg = ImageMaster.loadImage("chrono/images/chests/bagOpen.png");
    this.hb = new Hitbox(256.0F * Settings.scale, 270.0F * Settings.scale);
    this.hb.move(CHEST_LOC_X, CHEST_LOC_Y - 90.0F * Settings.scale);

    this.GOLD_AMT = 15;
  }

  public void open(boolean bossChest) {
    AbstractDungeon.getCurrRoom().rewards.clear();
    AbstractDungeon.overlayMenu.proceedButton.setLabel(TEXT[0]);
    CardCrawlGame.sound.play("CHEST_OPEN");

    // Gold Chance
    // if (AbstractDungeon.treasureRng.random(0,100) < 80)
    AbstractDungeon.getCurrRoom().addGoldToRewards(Math.round(AbstractDungeon.treasureRng.random(this.GOLD_AMT * 0.9F, this.GOLD_AMT * 1.1F)));

    // Card Chance
    if (AbstractDungeon.treasureRng.random(0,100) < 80) {
        AbstractDungeon.getCurrRoom().addCardToRewards();        

    // Colourless card chance
    } else {
        RewardItem r = new RewardItem(AbstractCard.CardColor.COLORLESS);
        // if (r.cards.size() > 1)
        //     r.cards.subList(1, r.cards.size()).clear();
        if (r.cards.size() > 0)
            AbstractDungeon.getCurrRoom().rewards.add(r);
    }

    // Potion Chance
    if (AbstractDungeon.treasureRng.random(0,100) < 20)
        AbstractDungeon.getCurrRoom().addPotionToRewards(AbstractDungeon.returnRandomPotion());

    // Relic Chance
    if (AbstractDungeon.treasureRng.random(0,100) < 5)
        AbstractDungeon.getCurrRoom().addRelicToRewards(AbstractRelic.RelicTier.COMMON);

    AbstractDungeon.combatRewardScreen.open();

    int remove = -1;
    for (int j = 0; j < AbstractDungeon.combatRewardScreen.rewards.size(); j++) {
        if (((RewardItem)AbstractDungeon.combatRewardScreen.rewards.get(j)).type == RewardItem.RewardType.CARD) {
            remove = j;
            break;
        } 
    } 
    if (remove != -1)
        AbstractDungeon.combatRewardScreen.rewards.remove(remove);
  }
}
