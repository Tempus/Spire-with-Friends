package chronoMods.coop.infusions;

import com.evacipated.cardcrawl.modthespire.lib.*;
import basemod.*;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;

import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.core.*;

import com.megacrit.cardcrawl.actions.*;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.unique.*;
import com.megacrit.cardcrawl.actions.defect.*;
import com.megacrit.cardcrawl.actions.watcher.*;
import com.megacrit.cardcrawl.actions.utility.*;
import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.helpers.*;
import com.megacrit.cardcrawl.cards.tempCards.*;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.powers.*;
import com.megacrit.cardcrawl.powers.watcher.*;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.orbs.*;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;
import com.megacrit.cardcrawl.vfx.*;

import chronoMods.*;
import chronoMods.coop.*;
import chronoMods.coop.infusions.*;

import java.util.*;

/*

How can Infusions work?

1. Infuse a card directly. This is excellent for Starter Cards. Infuse an "X" into your starter, very handy.
    
    This could affect all cards of the ID
     - Any further cards you draft of that type would be infused
    
    This could affect only a card instance (UUID)
     - Any further cards wouldn't be infused
     - Could affect Deck, or Drafts

2. Infuse Draft Card
    Infuses a random draft card, kind of like Blunt Scissors.
        + Works on drafts, which could be more interactive
        + Makes players take unusual cards since they are buffed
        - Overlaps Blunt Scissors
        - Not a lot of control
    
3. Infuse Pool Card
    Makes all pool cards of that ID infused
        + Works on Card Pools, an area that isn't really affect by any team relics or bonuses
        + Makes players take unusual cards since they are buffed
        - You aren't guaranteed to see any of the Infused cards
        - Low player visibility into what is even happening

4. Infuse Attachments
    Attach a known or random infusion of a theme onto a card of choice
        + Lots of player agency
        + Could be sent via Courier
        - Really freaking strong
        - Doesn't encourage shenanigans



Neow 
    Infuse a card in every draft in Act 1 (Guaranteed to see them, and avoids Blunt Scissors)
    Infuse your starter card(s)

Team Relics
    Relic that grants an infusion to a player of their choice immediately
    At the courier, add a curse to your deck and another player gets some Infusion Rewards in exchange
        - Infusion Reward allows a player to Infuse a card in their deck
        - these could remove cards from your pool as well that correspond to the Infusion type

*/

public class Infusion {

    // Adding an infusion field to AbstractCard
    @SpirePatch(clz=AbstractCard.class, method=SpirePatch.CLASS)
    public static class infusionField { 
        public static SpireField<Infusion> infusion = new SpireField<>(() -> null); 
    }

    // Added Actions
    public Runnable actions;
    public String description;

    // Acceptable Infusion Filters
    public AbstractCard.CardRarity targetRarity;
    public AbstractCard.CardTarget targetTarget;
    public AbstractCard.CardType targetType;

    // Card Stats
    public int damageUp;
    public int blockUp;
    public int magicNumUp;
    public int costDown;

    // Card Properties
    public boolean shuffleBackIntoDrawPile;
    public boolean selfRetain;
    public boolean returnToHand;
    public boolean isInnate;

    // Card Tags
    public AbstractCard.CardTags addTag;

    // Visuals
    public Texture icon;
    public Class particle;

    // Constructors
    public Infusion(String description, Runnable actions) {
        this(description, actions, null, null, null, 0, 0, 0, 0, false, false, false, false, null);
    }

    public Infusion(String description, AbstractCard.CardTarget targetTarget, Runnable actions) {
        this(description, actions, null, targetTarget, null, 0, 0, 0, 0, false, false, false, false, null);
    }

    public Infusion(String description, AbstractCard.CardType targetType, Runnable actions) {
        this(description, actions, null, null, targetType, 0, 0, 0, 0, false, false, false, false, null);
    }

    public Infusion(String description, AbstractCard.CardTarget targetTarget, AbstractCard.CardType targetType, Runnable actions) {
        this(description, actions, null, targetTarget, targetType, 0, 0, 0, 0, false, false, false, false, null);
    }

    public Infusion(String description, int damageUp, int blockUp, int magicNumUp, int costDown) {
        this(description, null, null, null, null, damageUp, blockUp, magicNumUp, costDown, false, false, false, false, null);
    }

    public Infusion(String description, boolean shuffleBackIntoDrawPile, boolean selfRetain, boolean returnToHand, boolean isInnate) {
        this(description, null, null, null, null, 0, 0, 0, 0, shuffleBackIntoDrawPile, selfRetain, returnToHand, isInnate, null);
    }

    public Infusion(String description, Runnable actions, AbstractCard.CardRarity targetRarity, AbstractCard.CardTarget targetTarget, AbstractCard.CardType targetType, int damageUp, int blockUp, int magicNumUp, int costDown, boolean shuffleBackIntoDrawPile, boolean selfRetain, boolean returnToHand, boolean isInnate, AbstractCard.CardTags addTag) {
        this.actions = actions;
        this.description = description;

        // Acceptable Infusion Filters
        this.targetRarity = targetRarity;
        this.targetTarget = targetTarget;
        this.targetType = targetType;

        // Card Stats
        this.damageUp = damageUp;
        this.blockUp = blockUp;
        this.magicNumUp = magicNumUp;
        this.costDown = costDown;

        // Card Properties
        this.shuffleBackIntoDrawPile = shuffleBackIntoDrawPile;
        this.selfRetain = selfRetain;
        this.returnToHand = returnToHand;
        this.isInnate = isInnate;

        // Card Tags
        this.addTag = addTag;
    }

    public boolean canInfuse(AbstractCard c) {
        if (c.cost == -2) { return false; } // Unplayable cards
        if (c.cardID == "MergeCard") { return false; } // Shouldn't infuse Merge cards... because the text and confusion is excessive
        if (Infusion.infusionField.infusion.get(c) != null) { return false; } // No double infuses thanks

        if (targetRarity != null && c.rarity != targetRarity) { return false; }
        if (targetTarget != null && c.target != targetTarget) { return false; }
        if (targetType != null   && c.type != targetType)     { return false; }
        if (targetType != AbstractCard.CardType.POWER && c.type == AbstractCard.CardType.POWER)     { return false; } // Only specifically targeting a power can hit a power

        if (damageUp > 0    && c.baseDamage <= 0)             { return false; }
        if (blockUp > 0     && c.baseBlock <= 0)              { return false; }
        if (magicNumUp > 0  && c.baseMagicNumber <= 0)        { return false; }
        if (costDown > 0    && c.cost < costDown)             { return false; }

        if (shuffleBackIntoDrawPile == true && c.shuffleBackIntoDrawPile == true)  { return false; }
        if (selfRetain == true && c.selfRetain == true)       { return false; }
        if (returnToHand == true && c.returnToHand == true)   { return false; }
        if (isInnate == true && c.isInnate == true)           { return false; }

        return true;
    }

    public CardGroup getInfuseable(CardGroup g) {
        CardGroup retPool = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
        for (AbstractCard c : g.group)
            if (canInfuse(c))
                retPool.addToBottom(c);

        return retPool;
    }

    public void ApplyInfusion(AbstractCard c) {
        if (damageUp > 0  ) { c.baseDamage += damageUp;        }
        if (blockUp > 0   ) { c.baseBlock += blockUp;          }
        if (magicNumUp > 0) { c.baseMagicNumber += magicNumUp; }
        if (costDown > 0  ) { c.cost -= costDown;              }

        if (shuffleBackIntoDrawPile == true)    { c.shuffleBackIntoDrawPile = true; }
        if (selfRetain == true)                 { c.selfRetain = true;              }
        if (returnToHand == true)               { c.returnToHand = true;            }
        if (isInnate == true)                   { c.isInnate = true;                }

        if (addTag != null) { c.tags.add(addTag); }

        Infusion.infusionField.infusion.set(c, this);

        if (!(damageUp > 0 || blockUp > 0 || magicNumUp > 0 || costDown > 0)) {
            if (isInnate == true || selfRetain == true) {
                c.rawDescription = description + " NL " + c.rawDescription;
                c.initializeDescription();                
            } else {
                c.rawDescription += " NL NL " + description;
                c.initializeDescription();                
            }
        }
        // Infusion.infusionField.infusion.get(__instance));
    }

    @SpirePatch(clz = AbstractCard.class, method="makeStatEquivalentCopy")
    public static class LinkedCardCopying {
        public static AbstractCard Postfix(AbstractCard __result, AbstractCard __instance) {
            Infusion i = Infusion.infusionField.infusion.get(__instance);
            if (i != null)
                i.ApplyInfusion(__result);
            return __result;
        }
    }

    // @SpirePatch(clz = AbstractCard.class, method="renderCardBg")
    // public static class RenderLinkedSetIcon {
    //     public static void Postfix(AbstractCard __instance, SpriteBatch sb) {
    //         Infusion i = Infusion.infusionField.infusion.get(__instance);
    //         if (i != null) {

    //             ShaderHelper.setShader(sb, ShaderHelper.Shader.GRAYSCALE); 
    //             sb.setBlendFunction(gl[glA], gl[glB]);

    //             float x = (130.0F - i.icon.getHeight()) * Settings.scale;
    //             float y = -188f * Settings.scale;

    //             sb.draw(i.icon, __instance.current_x + x * __instance.drawScale, __instance.current_y + y * __instance.drawScale, 0f, 0f, i.icon.getWidth(), i.icon.getHeight(), __instance.drawScale, __instance.drawScale, __instance.angle, 0, 0, i.icon.getWidth(), i.icon.getHeight(), false, false);
            
    //             sb.setBlendFunction(770, 771);
    //             ShaderHelper.setShader(sb, ShaderHelper.Shader.DEFAULT);
    //         }
    //     }
    // }
}
