package chronoMods.coop.infusions;

import com.evacipated.cardcrawl.modthespire.lib.*;

import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;

import com.megacrit.cardcrawl.localization.*;
import com.megacrit.cardcrawl.core.*;
import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.blights.*;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.map.*;
import com.megacrit.cardcrawl.helpers.*;
import com.megacrit.cardcrawl.helpers.input.*;
import com.megacrit.cardcrawl.relics.*;
import com.megacrit.cardcrawl.rooms.*;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.rewards.*;
import com.megacrit.cardcrawl.screens.*;
import com.megacrit.cardcrawl.ui.buttons.*;

import basemod.*;
import basemod.abstracts.*;

import java.util.*;
import java.lang.Math;

import chronoMods.*;
import chronoMods.network.steam.*;
import chronoMods.network.*;
import chronoMods.coop.*;
import chronoMods.coop.infusions.*;
import chronoMods.ui.deathScreen.*;
import chronoMods.ui.hud.*;
import chronoMods.ui.lobby.*;
import chronoMods.ui.mainMenu.*;
import chronoMods.coop.hubris.*;

public class NeowInfusion extends CustomRelic {
    public static final String ID = "NeowInfusion";
    public InfusionSet infusionSet;
    public AbstractPlayer.PlayerClass pClass = AbstractPlayer.PlayerClass.IRONCLAD;
    
    public NeowInfusion(AbstractPlayer.PlayerClass pClass) {
        super(ID, new Texture("chrono/images/infusions/NeowInfusion.png"), RelicTier.SPECIAL, LandingSound.MAGICAL);

        this.pClass = pClass;
        infusionSet = InfusionHelper.getInfusionSet(pClass);
        counter = 4;

        this.description = getUpdatedDescription();
        this.tips.clear();
        this.tips.add(new PowerTip(this.name, this.description));
        initializeTips();        
    }
    
    @Override
    public String getUpdatedDescription() {
        if (infusionSet == null)
            return "";
        return DESCRIPTIONS[0] + infusionSet.description;
    }
        
    @Override
    public AbstractRelic makeCopy() { // always override this method to return a new instance of your relic
        return new NeowInfusion(pClass);
    }
    
    // Add the card into the card draft
    @SpirePatch(clz = CardRewardScreen.class, method="open")
    public static class CardsGainInfusions {
        public static void Postfix(CardRewardScreen __instance, ArrayList<AbstractCard> cards, RewardItem rItem, String header) {
            if (AbstractDungeon.player.hasRelic("NeowInfusion") && AbstractDungeon.actNum == 1) {
                for (AbstractCard c : __instance.rewardGroup)
                    if (Infusion.infusionField.infusion.get(c) != null) // Already been here.
                        return;

                for (AbstractCard c : __instance.rewardGroup) {
                    Infusion i = ((NeowInfusion)AbstractDungeon.player.getRelic("NeowInfusion")).infusionSet.getValidInfusion(c);
                    if (i != null) {
                        i.ApplyInfusion(c);
                        Collections.shuffle(__instance.rewardGroup, new java.util.Random());
                        ((NeowInfusion)AbstractDungeon.player.getRelic("NeowInfusion")).counter--;

                        if (((NeowInfusion)AbstractDungeon.player.getRelic("NeowInfusion")).counter <= 0)
                            AbstractDungeon.player.loseRelic("NeowInfusion");
                        return;
                    }
                }

            }
        }
    }

    @Override
    public void renderInTopPanel(SpriteBatch sb) {
        super.renderInTopPanel(sb);
        sb.setColor(Color.WHITE);
        sb.draw(infusionSet.icon, this.currentX - 48.0F + (float)ReflectionHacks.getPrivate(this, AbstractRelic.class, "offsetX"), this.currentY - 48.0F - 10f*Settings.scale, 48.0F, 48.0F, 96.0F, 96.0F, this.scale * 0.35f, this.scale * 0.35f, (float)ReflectionHacks.getPrivate(this, AbstractRelic.class, "rotation"), 0, 0, 96, 96, false, false);
    }

    // @SpirePatch(clz = AbstractDungeon.class, method="dungeonTransitionSetup")
    // public static class RemoveInfusionCrystal {
    //     public static void Postfix() {
    //         if (AbstractDungeon.player.hasRelic("NeowInfusion") && counter <= 0)
    //             AbstractDungeon.player.loseRelic("NeowInfusion");
    //     }
    // }
}