package chronoMods.coop.infusions;

import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;


public class Infusion {

    public int indexID = 0;
    public String setID;

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
        if (damageUp > 0  ) { c.baseDamage += damageUp;        c.upgradedDamage = true; }
        if (blockUp > 0   ) { c.baseBlock += blockUp;          c.upgradedBlock = true;  }
        if (magicNumUp > 0) { c.baseMagicNumber += magicNumUp; c.upgradedMagicNumber = true; }
        if (costDown > 0  ) { c.cost -= costDown;              c.upgradedCost = true;   }

        if (shuffleBackIntoDrawPile == true)    { c.shuffleBackIntoDrawPile = true; }
        if (selfRetain == true)                 { c.selfRetain = true;              }
        if (returnToHand == true)               { c.returnToHand = true;            }
        if (isInnate == true)                   { c.isInnate = true;                }

        if (addTag != null) { c.tags.add(addTag); }

        Infusion.infusionField.infusion.set(c, this);
        c.initializeDescription();
    }

    public void ApplyStatEquivalentInfusion(AbstractCard c) {
        if (damageUp > 0  ) { c.upgradedDamage = true; }
        if (blockUp > 0   ) { c.upgradedBlock = true;  }
        if (magicNumUp > 0) { c.upgradedMagicNumber = true; }
        if (costDown > 0  ) { c.upgradedCost = true;   }

        if (shuffleBackIntoDrawPile == true)    { c.shuffleBackIntoDrawPile = true; }
        if (selfRetain == true)                 { c.selfRetain = true;              }
        if (returnToHand == true)               { c.returnToHand = true;            }
        if (isInnate == true)                   { c.isInnate = true;                }

        if (addTag != null) { c.tags.add(addTag); }

        Infusion.infusionField.infusion.set(c, this);
        c.initializeDescription();
    }

    // Description additions
    @SpirePatch(clz = AbstractCard.class, method = "initializeDescription")
    @SpirePatch(clz = AbstractCard.class,method = "initializeDescriptionCN")
    public static class DescriptionReplacement {
        public static ExprEditor Instrument() {
            return new ExprEditor() {
                public void edit(FieldAccess f) throws CannotCompileException {
                    if (f.getClassName().equals(AbstractCard.class.getName()) && f.getFieldName().equals("rawDescription")) {
                        f.replace("$_ = " + Infusion.DescriptionReplacement.class.getName() + ".calculateRawDescription(this, $proceed($$));");
                    }
                }
            };
        }
        public static String calculateRawDescription(AbstractCard card, String rawDescription) {
            Infusion i = Infusion.infusionField.infusion.get(card);
            if (i != null) {
                // Don't update description if you only added stats
                if (!(i.damageUp > 0 || i.blockUp > 0 || i.magicNumUp > 0 || i.costDown > 0)) {
                    // Innate and retain are prefixes
                    if (i.isInnate == true || i.selfRetain == true) {
                        rawDescription = i.description + " NL " + rawDescription;
                    } else {
                        rawDescription = rawDescription + " NL NL " + i.description;
                    }
                }
            }

            return rawDescription;
        }
    }

    // Preserve the card infusion between copies
    @SpirePatch(clz = AbstractCard.class, method="makeStatEquivalentCopy")
    public static class LinkedCardCopying {
        public static AbstractCard Postfix(AbstractCard __result, AbstractCard __instance) {
            Infusion i = Infusion.infusionField.infusion.get(__instance);
            if (i != null)
                i.ApplyStatEquivalentInfusion(__result);
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
