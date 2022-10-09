package chronoMods.coop.relics;

import chronoMods.TogetherManager;
import chronoMods.network.NetworkHelper;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.blights.AbstractBlight;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.helpers.ShaderHelper;
import com.megacrit.cardcrawl.localization.BlightStrings;

public class BondsOfFate extends AbstractBlight {
    // When one player dies, they are 'sustained' by their ally's health.
    //  If either player takes damage during this time, both their health drops.
    //  Extra 'overkill' damage will also be applied to both players
    //  If both players have 0 hp, they die

    public static final String ID = "BondsOfFate";
    private static final BlightStrings blightStrings = CardCrawlGame.languagePack.getBlightString(ID);
    public static final String NAME = blightStrings.NAME;
    public static final String[] DESCRIPTIONS = blightStrings.DESCRIPTION;

    public BondsOfFate() {
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
        this.counter = TogetherManager.players.size() - 1;
    }

    @Override
    public void updateDescription() {
        this.description = this.DESCRIPTIONS[0];
    }

    public void obtain() {
        if (AbstractDungeon.player.hasBlight("StringOfFate") || AbstractDungeon.player.hasBlight("ChainsOfFate")) {
            for (int i=0; i<AbstractDungeon.player.blights.size(); ++i) {
                if (AbstractDungeon.player.blights.get(i).blightID.equals("StringOfFate")) {
                    this.counter += AbstractDungeon.player.blights.get(i).counter + 1;
                    instantObtain(AbstractDungeon.player, i, true);
                    break;
                }
                if (AbstractDungeon.player.blights.get(i).blightID.equals("ChainsOfFate")) {
                    this.counter += AbstractDungeon.player.blights.get(i).counter + 1;
                    instantObtain(AbstractDungeon.player, i, true);
                    break;
                }
            }
        } else {
            super.obtain();
        }
    }

    @Override
    public void effect() {
        if (counter > 0) {
            flash();
            AbstractDungeon.player.isDead = false;
            if (AbstractDungeon.player.hasRelic("Mark of the Bloom")) {
                AbstractDungeon.player.currentHealth = AbstractDungeon.player.maxHealth;
            } else {
                AbstractDungeon.player.heal(AbstractDungeon.player.maxHealth, true);
            }
        }
        
        if (this.increment > 0)
            this.increment--;
        else
            this.counter--;

        NetworkHelper.sendData(NetworkHelper.dataType.Hp);
        NetworkHelper.sendData(NetworkHelper.dataType.LoseLife);
    }

    @Override
    public void renderInTopPanel(SpriteBatch sb) {
        if (this.counter <= 0)
          ShaderHelper.setShader(sb, ShaderHelper.Shader.GRAYSCALE); 
        super.renderInTopPanel(sb);
        if (this.counter <= 0)
          ShaderHelper.setShader(sb, ShaderHelper.Shader.DEFAULT);
    }

    // This patch prevents death
    @SpirePatch(clz = AbstractPlayer.class, method="damage")
    public static class RevivePlayer {
        @SpireInsertPatch(rloc=1875-1725, localvars={})
        public static SpireReturn Insert(AbstractPlayer player, DamageInfo info) {

            if (player.isDead) {
                if (player.hasBlight("BondsOfFate")) {
                    player.currentHealth = 0;
                    player.getBlight("BondsOfFate").effect();
                    return SpireReturn.Return(null);
                } 
            }

            return SpireReturn.Continue();
        }
    }
}
