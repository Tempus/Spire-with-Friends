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

        // Fix not having energy stopping the second card from being played
        cards.get(cardIndex).freeToPlayOnce = true;


        // Instead of canUse, we just want cardPlayable. BUT, derp cards keep subclassing canUse when they should be reimplementing cardPlayable, so we handle those manually.
        if (cardPlayable(cards.get(cardIndex), monster)) {

            // Special cases (gross)
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

    public boolean cardPlayable(AbstractCard c, AbstractMonster m) {
        boolean specialCase = false;

        if (c.cardID.equals("Secret Technique")) {
            for (AbstractCard ca : AbstractDungeon.player.drawPile.group) {
              if (ca.type == AbstractCard.CardType.SKILL)
                specialCase = true; 
            }
        } else if (c.cardID.equals("Secret Weapon")) {
            for (AbstractCard ca : AbstractDungeon.player.drawPile.group) {
              if (ca.type == AbstractCard.CardType.ATTACK)
                specialCase = true; 
            }
        } else if (c.cardID.equals("Grand Finale")) {
            if (AbstractDungeon.player.drawPile.size() == 0)
                specialCase = true; 
        } else if (c.cardID.equals("Reflex") || c.cardID.equals("Tactician") || c.cardID.equals("DeusExMachina")) {
            specialCase = false; 
        } else if (c.cardID.equals("SignatureMove")) {
            specialCase = true;
            for (AbstractCard ca : AbstractDungeon.player.hand.group) {
              if (ca.type == AbstractCard.CardType.ATTACK && ca != c) {
                specialCase = false;
              } 
            } 
        } else if (c.cardID.equals("Clash")) {
            specialCase = true;
            for (AbstractCard ca : AbstractDungeon.player.hand.group) {
              if (ca.type != AbstractCard.CardType.ATTACK) {
                specialCase = false;
              } 
            } 
        } else {
            specialCase = true; 
        }

        return c.cardPlayable(m) && specialCase;
    }
}