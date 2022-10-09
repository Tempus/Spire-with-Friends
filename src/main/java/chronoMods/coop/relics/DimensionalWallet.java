package chronoMods.coop.relics;

import chronoMods.TogetherManager;
import chronoMods.network.RemotePlayer;
import com.megacrit.cardcrawl.blights.AbstractBlight;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.localization.BlightStrings;

public class DimensionalWallet extends AbstractBlight {
    public static final String ID = "DimensionalWallet";
  private static final BlightStrings blightStrings = CardCrawlGame.languagePack.getBlightString(ID);
  public static final String NAME = blightStrings.NAME;
  public static final String[] DESCRIPTIONS = blightStrings.DESCRIPTION;

    public DimensionalWallet() {
        super(ID, NAME, "", "spear.png", true);
        this.blightID = ID;
        this.name = NAME;
        updateDescription();
        this.unique = true;
        this.img = ImageMaster.loadImage("chrono/images/blights/" + ID + ".png");
        this.outlineImg = ImageMaster.loadImage("chrono/images/blights/outline/" + ID + ".png");
        this.increment = 0;
        this.tips.clear();
        this.tips.add(new PowerTip(name, description));
    }

    @Override
    public void updateDescription() {
        this.description = this.DESCRIPTIONS[0];
    }

    @Override
    public void onEquip() {
        if (isObtained) { return; }

        CardCrawlGame.sound.play("GOLD_GAIN");

        int goldSum = 0;
        for (RemotePlayer player : TogetherManager.players) {
            goldSum += player.gold;
            goldSum += 100;
        }

        AbstractDungeon.player.gold = goldSum;
    }
}