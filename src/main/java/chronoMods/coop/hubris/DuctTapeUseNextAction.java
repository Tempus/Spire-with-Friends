package chronoMods.coop.hubris;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import chronoMods.coop.hubris.DuctTapeCard;


import com.megacrit.cardcrawl.cards.red.Anger;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDiscardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.combat.VerticalAuraEffect;

import chronoMods.TogetherManager;
import java.util.List;

public class DuctTapeUseNextAction extends AbstractGameAction
{
    private DuctTapeCard origin;
    private List<AbstractCard> cards;
    private int cardIndex;
    private AbstractPlayer player;
    private AbstractMonster monster;

    public DuctTapeUseNextAction(DuctTapeCard origin, List<AbstractCard> cards, int index, AbstractPlayer p, AbstractMonster m)
    {
        this.origin = origin;
        this.cards = cards;
        cardIndex = index;
        player = p;
        monster = m;
    }

    @Override
    public void update()
    {
        cards.get(cardIndex).calculateCardDamage(monster);
        if (cards.get(cardIndex).costForTurn != -1) {
            // Fix not having energy stopping the second card from being played
            cards.get(cardIndex).freeToPlayOnce = true;
        }
        if (cards.get(cardIndex).canUse(player, monster)) {

            // Special cases (gross)
            TogetherManager.log(cards.get(cardIndex).cardID);
            if (cards.get(cardIndex).cardID.equals("Anger")) {
                addToBot(new DamageAction(monster, new DamageInfo(monster, cards.get(cardIndex).damage, cards.get(cardIndex).damageTypeForTurn), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
                addToBot(new VFXAction(player, (AbstractGameEffect)new VerticalAuraEffect(Color.FIREBRICK, player.hb.cX, player.hb.cY), 0.0F));
                addToBot(new MakeTempCardInDiscardAction(origin.makeStatEquivalentCopy(), 1));
            } else {
                cards.get(cardIndex).use(player, monster);
            }
        }

        // Reset the free to play so that the energy display of the card previews works again
        cards.get(cardIndex).freeToPlayOnce = false;

        ++cardIndex;
        if (cardIndex < cards.size()) {
            AbstractDungeon.actionManager.addToBottom(new DuctTapeUseNextAction(origin, cards, cardIndex, player, monster));
        }

        isDone = true;
        origin.calculateCost(); // For cards that modify cost when used

    }
}