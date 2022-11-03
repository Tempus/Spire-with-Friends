package chronoMods.coop.relics;

import chronoMods.TogetherManager;
import chronoMods.network.RemotePlayer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.blights.AbstractBlight;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.localization.BlightStrings;

public class MirrorTouch extends AbstractBlight {
    public static final String ID = "MirrorTouch";
    private static final BlightStrings blightStrings = CardCrawlGame.languagePack.getBlightString(ID);
    public static final String NAME = blightStrings.NAME;
    public static final String[] DESCRIPTIONS = blightStrings.DESCRIPTION;

    @SpirePatch(clz = AbstractCreature.class, method="increaseMaxHp")
    public static class MirrorTouchHalfMaxHPGain {
        public static void Prefix(AbstractCreature __instance, @ByRef int[] amount, boolean showEffect) {
            if (AbstractDungeon.player.hasBlight("MirrorTouch")) { amount[0] = amount[0] / 2; }
        }
    }

    public MirrorTouch() {
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
        if (!isObtained)
            this.description = this.DESCRIPTIONS[0] + (int)(getCombinedHealth() * 1.5f) + this.DESCRIPTIONS[1];
        else
            this.description = this.DESCRIPTIONS[2];
    }
    
    @Override
    public void renderTip(SpriteBatch sb) {
        updateDescription();
        this.tips.clear();
        this.tips.add(new PowerTip(name, description));

        super.renderTip(sb);
    }

    @Override
    public void onEquip() {
        if (isObtained) { return; }

        AbstractDungeon.player.maxHealth = (int)(getCombinedHealth() * 1.5f);
        AbstractDungeon.player.currentHealth = AbstractDungeon.player.maxHealth;
    }

    public int getCombinedHealth() {
        int i = 0;
        for (RemotePlayer r : TogetherManager.players) {
            i += r.maxHp;
        }

        return i / TogetherManager.players.size();
    }
}