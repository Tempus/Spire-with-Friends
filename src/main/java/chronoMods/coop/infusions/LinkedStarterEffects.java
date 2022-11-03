package chronoMods.coop.infusions;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;

public class LinkedStarterEffects
{
    public static final String[] INFUSE = CardCrawlGame.languagePack.getUIString("CardInfusions").TEXT;

    public static void infuseStarter(AbstractPlayer youPlayer, AbstractPlayer otherPlayer) {
        InfusionSet iSet = InfusionHelper.getInfusionSet(otherPlayer.chosenClass);

        int pos = 0;
        for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
            if (c.rarity == AbstractCard.CardRarity.BASIC && !(c.hasTag(AbstractCard.CardTags.STARTER_DEFEND) || c.hasTag(AbstractCard.CardTags.STARTER_STRIKE))) {
                Infusion i = iSet.getUnshuffledValidInfusion(c);
                if (i != null){
                    i.ApplyInfusion(c);
                    AbstractDungeon.topLevelEffects.add(new ShowCardBrieflyEffect(c.makeStatEquivalentCopy(), Settings.WIDTH / 2.0F + AbstractCard.IMG_WIDTH - 30.0F * Settings.scale + pos * 60f * Settings.scale, Settings.HEIGHT / 2.0F));
                    pos++;
                }
            }
        }
    }
}