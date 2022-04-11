package chronoMods.coop;

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

import java.util.*;

public class LinkedInfusions
{
    public static final String[] INFUSE = CardCrawlGame.languagePack.getUIString("CardInfusions").TEXT;

    public static void InfuseBatch(CardGroup pool, AbstractCard.CardRarity rarity, int numCardsToAffect, Runnable actions, String description, boolean retains) {
        AbstractCard card;
        for (int i = 0; i < numCardsToAffect; i++) {
            if (rarity == null)
                card = pool.getRandomCard(false);
            else
                card = pool.getRandomCard(false, rarity);

            LinkedCardUsePatch.modifyCard(card.cardID, actions, description);
            LinkedCardUsePatch.UpdateDescriptions(card, retains);

            float x = MathUtils.random(0.1F, 0.9F) * Settings.WIDTH;
            float y = MathUtils.random(0.2F, 0.8F) * Settings.HEIGHT;
            AbstractDungeon.effectList.add(new ShowCardBrieflyEffect(card.makeStatEquivalentCopy(), x, y));
            pool.removeCard(card);
            TogetherManager.log("Infused " + card.name + " with " + description);
        }
    }

    public static void InfuseBatch(CardGroup pool, AbstractCard.CardRarity rarity, int numCardsToAffect, Runnable actions, String description) {
        InfuseBatch(pool, rarity, numCardsToAffect, actions, description, false);
    }

    public static CardGroup getCardsThatTarget(CardGroup cGroup, AbstractCard.CardTarget target) {
        CardGroup retVal = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
        for (AbstractCard card : cGroup.group) {
          if (card.target == target)
            retVal.addToBottom(card); 
        } 
        return retVal;
    }

    public static void Infuse(AbstractPlayer youPlayer, AbstractPlayer otherPlayer) {
        AbstractCard card;

        CardGroup yourPool = new CardGroup(CardGroup.CardGroupType.CARD_POOL);
        ArrayList<AbstractCard> pool = new ArrayList();
        pool = youPlayer.getCardPool(pool);
        yourPool.group = pool;

        int choice = MathUtils.random(4);

        switch (otherPlayer.chosenClass) {
            case IRONCLAD:
                switch (choice) {
                    case 0:
                        // Debuff/Vulnerable Set
                        InfuseBatch(getCardsThatTarget(yourPool, AbstractCard.CardTarget.ENEMY), null, 3, () -> { 
                            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(LinkedCardUsePatch.lastMonster, AbstractDungeon.player, new VulnerablePower(LinkedCardUsePatch.lastMonster, 1, false), 1)); }, 
                            INFUSE[0]);

                        InfuseBatch(getCardsThatTarget(yourPool, AbstractCard.CardTarget.ALL_ENEMY), null, 2, () -> { 
                            for (AbstractMonster mo : (AbstractDungeon.getCurrRoom()).monsters.monsters)
                              AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(mo, AbstractDungeon.player, new VulnerablePower(mo, 1, false), 1, true, AbstractGameAction.AttackEffect.NONE)); 
                            }, 
                            INFUSE[12]);

                        InfuseBatch(getCardsThatTarget(yourPool, AbstractCard.CardTarget.ENEMY), null, 1, () -> { 
                            AbstractDungeon.actionManager.addToBottom(new DropkickAction(LinkedCardUsePatch.lastMonster, new DamageInfo(AbstractDungeon.player, 0, DamageInfo.DamageType.NORMAL))); }, 
                            INFUSE[13]);
                        break;
                    case 1:
                        // Block Set
                        InfuseBatch(getCardsThatTarget(yourPool.getSkills(), AbstractCard.CardTarget.SELF), null, 3, () -> { 
                            AbstractDungeon.actionManager.addToBottom(new GainBlockAction(AbstractDungeon.player, AbstractDungeon.player, 4)); }, 
                            INFUSE[14]);

                        InfuseBatch(getCardsThatTarget(yourPool.getSkills(), AbstractCard.CardTarget.SELF), null, 2, () -> { 
                            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new FlameBarrierPower(AbstractDungeon.player, 3), 3)); }, 
                            INFUSE[15]);

                        InfuseBatch(yourPool.getPowers(), null, 1, () -> { 
                            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new MetallicizePower(AbstractDungeon.player, 2), 2)); }, 
                            INFUSE[16]);
                        break;
                    case 2:
                        // Strength Set
                        InfuseBatch(getCardsThatTarget(yourPool.getAttacks(), AbstractCard.CardTarget.ENEMY), null, 3, () -> { 
                            AbstractDungeon.actionManager.addToBottom((new DamageAction(AbstractDungeon.getMonsters().getRandomMonster(null, true, AbstractDungeon.cardRandomRng), new DamageInfo((AbstractCreature)AbstractDungeon.player, 3, DamageInfo.DamageType.NORMAL), AbstractGameAction.AttackEffect.SLASH_HORIZONTAL)));}, 
                            INFUSE[17]);

                        InfuseBatch(getCardsThatTarget(yourPool.getSkills(), AbstractCard.CardTarget.SELF), null, 2, () -> { 
                            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new StrengthPower(AbstractDungeon.player, 3), 3));
                            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new LoseStrengthPower(AbstractDungeon.player, 3), 3)); }, 
                            INFUSE[18]);

                        InfuseBatch(yourPool.getPowers(), null, 1, () -> { 
                            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new StrengthPower(AbstractDungeon.player, 2), 2)); }, 
                            INFUSE[19]);
                        break;
                    case 3:
                        // Exhaust Set
                        InfuseBatch(yourPool.getAttacks(), null, 2, () -> { 
                            AbstractDungeon.actionManager.addToBottom(new ExhaustAction(1, false)); }, 
                            INFUSE[20]);

                        InfuseBatch(yourPool.getSkills(), AbstractCard.CardRarity.RARE, 1, () -> { 
                            AbstractDungeon.actionManager.addToBottom(new ExhaustAction(1, false, true, true)); }, 
                            INFUSE[21]);

                        InfuseBatch(yourPool.getPowers(), null, 1, () -> { 
                            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new FeelNoPainPower(AbstractDungeon.player, 2), 2)); }, 
                            INFUSE[22]);
                        break;
                }
                break;
            case THE_SILENT:
                switch (choice) {
                    case 0:
                        // Shiv Set
                        InfuseBatch(yourPool, AbstractCard.CardRarity.COMMON, 3, () -> { 
                            AbstractDungeon.actionManager.addToBottom(new MakeTempCardInHandAction(new Shiv(), 1)); }, 
                            INFUSE[23]);

                        InfuseBatch(yourPool.getSkills(), AbstractCard.CardRarity.UNCOMMON, 2, () -> { 
                            AbstractDungeon.actionManager.addToBottom(new MakeTempCardInHandAction(new Shiv(), 1)); }, 
                            INFUSE[23]);

                        InfuseBatch(yourPool.getPowers(), null, 1, () -> { 
                            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new AccuracyPower(AbstractDungeon.player, 3), 3)); }, 
                            INFUSE[24]);
                        break;
                    case 1:

                        // Block/Dex Set
                        InfuseBatch(getCardsThatTarget(yourPool.getSkills(), AbstractCard.CardTarget.SELF), null, 3, () -> { 
                            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new NextTurnBlockPower(AbstractDungeon.player, 4), 4)); }, 
                            INFUSE[25]);

                        InfuseBatch(yourPool.getPowers(), null, 2, () -> { 
                            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new DexterityPower(AbstractDungeon.player, 1), 1)); }, 
                            INFUSE[26]);

                        InfuseBatch(getCardsThatTarget(yourPool.getSkills(), AbstractCard.CardTarget.SELF), null, 1, () -> { 
                            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new BlurPower(AbstractDungeon.player, 1), 1)); }, 
                            INFUSE[27]);
                        break;
                    case 2:

                        // Discard/Draw Set
                        InfuseBatch(yourPool.getSkills(), null, 1, () -> { 
                            AbstractDungeon.actionManager.addToBottom(new DrawCardAction(AbstractDungeon.player, 1));
                            AbstractDungeon.actionManager.addToBottom(new DiscardAction(AbstractDungeon.player, AbstractDungeon.player, 1, false)); }, 
                            INFUSE[28]);

                        InfuseBatch(yourPool.getAttacks(), null, 2, () -> { 
                            AbstractDungeon.actionManager.addToBottom(new DrawCardAction(AbstractDungeon.player, 1));
                            AbstractDungeon.actionManager.addToBottom(new DiscardAction(AbstractDungeon.player, AbstractDungeon.player, 1, false)); }, 
                            INFUSE[28]);

                        InfuseBatch(yourPool, AbstractCard.CardRarity.COMMON, 2, () -> { 
                            AbstractDungeon.actionManager.addToBottom(new GainEnergyIfDiscardAction(1)); }, 
                            INFUSE[29]);

                        InfuseBatch(yourPool, null, 1, () -> { 
                            AbstractDungeon.actionManager.addToBottom(new DrawCardAction(AbstractDungeon.player, 1)); },
                            INFUSE[30]);
                        break;
                    case 3:

                        // Poison Set
                        InfuseBatch(getCardsThatTarget(yourPool.getAttacks(), AbstractCard.CardTarget.ENEMY), null, 3, () -> { 
                            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(LinkedCardUsePatch.lastMonster, AbstractDungeon.player, new PoisonPower(LinkedCardUsePatch.lastMonster, AbstractDungeon.player, 3), 3)); }, 
                            INFUSE[31]);

                        InfuseBatch(getCardsThatTarget(yourPool, AbstractCard.CardTarget.ALL_ENEMY), null, 2, () -> { 
                            for (AbstractMonster monster : (AbstractDungeon.getMonsters()).monsters)
                                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(monster, AbstractDungeon.player, new PoisonPower(monster, AbstractDungeon.player, 2), 2)); }, 
                            INFUSE[32]);

                        InfuseBatch(yourPool.getSkills(), null, 1, () -> { 
                            AbstractMonster randomMonster = AbstractDungeon.getMonsters().getRandomMonster(null, true, AbstractDungeon.cardRandomRng);
                            AbstractDungeon.actionManager.addToBottom(new BouncingFlaskAction(randomMonster, 2, 2)); }, 
                            INFUSE[33]);
                }
                break;

            case DEFECT:
                switch (choice) {
                    case 0:
                        // Lightning Set
                        InfuseBatch(yourPool, null, 3, () -> { 
                            AbstractDungeon.actionManager.addToBottom(new ChannelAction(new Lightning())); },
                            INFUSE[7]);

                        InfuseBatch(yourPool.getSkills(), null, 2, () -> { 
                            AbstractDungeon.actionManager.addToBottom(new AnimateOrbAction(1)); 
                            AbstractDungeon.actionManager.addToBottom(new EvokeOrbAction(1)); }, 
                            INFUSE[9]);

                        InfuseBatch(yourPool.getPowers(), null, 1, () -> { 
                            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new FocusPower(AbstractDungeon.player, 1), 1)); }, 
                            INFUSE[34]);
                        break;
                    case 1:

                        // Frost Set
                        InfuseBatch(yourPool, null, 3, () -> { 
                            AbstractDungeon.actionManager.addToBottom(new ChannelAction(new Frost())); },
                            INFUSE[8]);

                        InfuseBatch(yourPool.getSkills(), null, 2, () -> { 
                            AbstractDungeon.actionManager.addToBottom(new IncreaseMaxOrbAction(1)); },
                            INFUSE[35]);

                        InfuseBatch(yourPool.getPowers(), null, 1, () -> { 
                            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new FocusPower(AbstractDungeon.player, 1), 1)); }, 
                            INFUSE[34]);
                        break;
                    case 2:

                        // Dark Set
                        InfuseBatch(yourPool, null, 3, () -> { 
                            AbstractDungeon.actionManager.addToBottom(new ChannelAction(new Dark())); },
                            INFUSE[36]);

                        InfuseBatch(yourPool.getSkills(), null, 2, () -> { 
                            AbstractDungeon.actionManager.addToBottom(new RedoAction()); }, 
                            INFUSE[37]);

                        InfuseBatch(yourPool.getAttacks(), AbstractCard.CardRarity.UNCOMMON, 1, () -> { 
                            AbstractDungeon.actionManager.addToBottom(new AnimateOrbAction(1)); 
                            AbstractDungeon.actionManager.addToBottom(new EvokeWithoutRemovingOrbAction(1));
                            AbstractDungeon.actionManager.addToBottom(new AnimateOrbAction(1)); 
                            AbstractDungeon.actionManager.addToBottom(new EvokeOrbAction(1)); }, 
                            INFUSE[38]);
                        break;
                    case 3:

                        // Plasma Set
                        InfuseBatch(yourPool.getSkills(), AbstractCard.CardRarity.RARE, 2, () -> { 
                            AbstractDungeon.actionManager.addToBottom(new ChannelAction(new Plasma())); },
                            INFUSE[39]);

                        InfuseBatch(yourPool.getSkills(), null, 2, () -> { 
                            AbstractDungeon.actionManager.addToBottom(new AnimateOrbAction(1)); 
                            AbstractDungeon.actionManager.addToBottom(new EvokeOrbAction(1)); }, 
                            INFUSE[9]);

                        InfuseBatch(yourPool.getAttacks(), null, 2, () -> { 
                            AbstractDungeon.actionManager.addToBottom(new ChannelAction(AbstractOrb.getRandomOrb(true))); },
                            INFUSE[40]);
                        break;
                    }
                break;

            case WATCHER:
                switch (choice) {
                    case 0:
                        // Wrath/Calm Set
                        InfuseBatch(yourPool.getAttacks(), null, 2, () -> { 
                            AbstractDungeon.actionManager.addToBottom(new ChangeStanceAction("Wrath")); }, 
                            INFUSE[10]);
                        InfuseBatch(yourPool.getAttacks(), null, 2, () -> { 
                            AbstractDungeon.actionManager.addToBottom(new ChangeStanceAction("Calm")); }, 
                            INFUSE[11]);
                        InfuseBatch(yourPool.getAttacks(), null, 2, () -> { 
                            AbstractDungeon.actionManager.addToBottom(new ChangeStanceAction("Neutral")); }, 
                            INFUSE[41]);
                        break;
                    case 1:

                        // Divinity Set
                        InfuseBatch(yourPool.getSkills(), AbstractCard.CardRarity.COMMON, 3, () -> { 
                            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new MantraPower(AbstractDungeon.player, 1), 1)); }, 
                            INFUSE[42]);
                        InfuseBatch(yourPool.getSkills(), AbstractCard.CardRarity.UNCOMMON, 2, () -> { 
                            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new MantraPower(AbstractDungeon.player, 2), 2)); }, 
                            INFUSE[43]);
                        InfuseBatch(yourPool.getPowers(), null, 1, () -> { 
                            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new DevotionPower(AbstractDungeon.player, 1), 1)); }, 
                            INFUSE[44]);
                        break;
                    case 2:

                        // Scry Set
                        InfuseBatch(yourPool.getAttacks(), null, 3, () -> { 
                            AbstractDungeon.actionManager.addToBottom(new ScryAction(1)); }, 
                            INFUSE[45]);
                        InfuseBatch(yourPool.getSkills(), null, 2, () -> { 
                            AbstractDungeon.actionManager.addToBottom(new ScryAction(2)); }, 
                            INFUSE[46]);
                        InfuseBatch(yourPool, null, 1, () -> { 
                            AbstractDungeon.actionManager.addToBottom(new MakeTempCardInHandAction(new Insight(), 1)); }, 
                            INFUSE[47]);
                        break;
                    case 3:

                        // Retain Set
                        InfuseBatch(yourPool, null, 2, () -> { 
                            AbstractDungeon.actionManager.addToBottom(new MakeTempCardInHandAction(new Safety(), 1)); }, 
                            INFUSE[48]);
                        InfuseBatch(yourPool, null, 2, () -> { 
                            AbstractDungeon.actionManager.addToBottom(new MakeTempCardInHandAction(new Smite(), 1)); }, 
                            INFUSE[49]);
                        InfuseBatch(yourPool, null, 2, () -> { 
                            AbstractDungeon.actionManager.addToBottom(new MakeTempCardInHandAction(new Miracle(), 1)); }, 
                            INFUSE[50]);
                        InfuseBatch(yourPool, null, 3, () -> {}, 
                            INFUSE[51], true);
                        break;
                    }
                break;
            default:
                // For other classes, use colorless infusions?
                InfuseBatch(yourPool, null, 2, () -> { 
                    AbstractDungeon.actionManager.addToBottom(new DrawCardAction(AbstractDungeon.player, 1)); },
                    INFUSE[30]);
                InfuseBatch(yourPool, AbstractCard.CardRarity.UNCOMMON, 2, () -> { 
                    AbstractCard c = AbstractDungeon.returnTrulyRandomColorlessCardInCombat(AbstractDungeon.cardRandomRng).makeCopy();
                    AbstractDungeon.actionManager.addToBottom(new MakeTempCardInHandAction(c, 1)); },
                    INFUSE[52]);
                InfuseBatch(yourPool, null, 2, () -> { 
                    AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new ArtifactPower(AbstractDungeon.player, 2), 2)); }, 
                    INFUSE[53]);
                break;
        } 
    }

    // Infusion Effect
    @SpirePatch(clz=AbstractCard.class, method=SpirePatch.CLASS)
    public static class infusionEffect { 
        public static SpireField<Float> infuseTimer = new SpireField<>(() -> 0f); 
        public static SpireField<ArrayList<InfusionEmber>> infuseList = new SpireField<>(() -> new ArrayList()); 
    }
            
    @SpirePatch(clz = AbstractCard.class, method="renderCardBg")
    public static class InfusionEffectUpdateRender {
        public static void Postfix(AbstractCard __instance, SpriteBatch sb, float x, float y) {
            if (LinkedCardUsePatch.modifiedCardDescriptions.containsKey(__instance.cardID)) {

                LinkedInfusions.infusionEffect.infuseTimer.set(__instance, LinkedInfusions.infusionEffect.infuseTimer.get(__instance) - Gdx.graphics.getDeltaTime());

                float t = LinkedInfusions.infusionEffect.infuseTimer.get(__instance);

                if (t < 0.0F) {
                    LinkedInfusions.infusionEffect.infuseList.get(__instance).add(new InfusionEmber(__instance));
                    LinkedInfusions.infusionEffect.infuseTimer.set(__instance, 0.02F);
                } 

                for (Iterator<InfusionEmber> i = LinkedInfusions.infusionEffect.infuseList.get(__instance).iterator(); i.hasNext(); ) {
                    InfusionEmber e = i.next();
                    e.update();
                    if (e.isDone)
                        i.remove(); 
                } 

                sb.setBlendFunction(770, 1);
                for (AbstractGameEffect e : LinkedInfusions.infusionEffect.infuseList.get(__instance))
                    e.render(sb); 
                sb.setBlendFunction(770, 771);

            }
        }
    }
}