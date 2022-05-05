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
import com.megacrit.cardcrawl.cards.tempCards.*;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.powers.*;
import com.megacrit.cardcrawl.powers.watcher.*;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.relics.*;
import com.megacrit.cardcrawl.orbs.*;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;
import com.megacrit.cardcrawl.vfx.*;

import chronoMods.*;
import chronoMods.coop.*;
import chronoMods.coop.infusions.*;

import java.util.*;

public class LinkedInfusions
{
    public static final String[] INFUSE = CardCrawlGame.languagePack.getUIString("CardInfusions").TEXT;

    public static Map<AbstractPlayer.PlayerClass, ArrayList<InfusionSet>> characterInfusionMasterList = new HashMap();
    public static InfusionSet defaultInfusions;

    public static void setupInfusions() {
        InfusionSet set;
        ArrayList<InfusionSet> charSet;

        // Ironclad
            ArrayList<InfusionSet> ironclad = new ArrayList();

            // Vulnerable
                InfusionSet vuln = new InfusionSet("Vuln", InfusionVFXEmber.class);

                vuln.add( new Infusion( vuln.actText[0], AbstractCard.CardTarget.ENEMY, () -> { 
                            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(LinkedCardUsePatch.lastMonster, AbstractDungeon.player, new VulnerablePower(LinkedCardUsePatch.lastMonster, 1, false), 1)); }));
                vuln.add( new Infusion( vuln.actText[1], AbstractCard.CardTarget.ALL_ENEMY, () -> { 
                            for (AbstractMonster mo : (AbstractDungeon.getCurrRoom()).monsters.monsters)
                              AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(mo, AbstractDungeon.player, new VulnerablePower(mo, 1, false), 1, true, AbstractGameAction.AttackEffect.NONE)); 
                            }));
                vuln.add( new Infusion( vuln.actText[2], 5, 0, 0, 0));
                vuln.add( new Infusion( vuln.actText[3], () -> { 
                            AbstractDungeon.actionManager.addToBottom(new ArmamentsAction(false)); }));

                ironclad.add(vuln);

            // Block
                InfusionSet block = new InfusionSet("Block", InfusionVFXEmber.class);

                block.add( new Infusion( block.actText[0], 0, 4, 0, 0));
                block.add( new Infusion( block.actText[0], 0, 4, 0, 0));
                block.add( new Infusion( block.actText[1], () -> { 
                            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new FlameBarrierPower(AbstractDungeon.player, 3), 3)); }));
                block.add( new Infusion( block.actText[2], AbstractCard.CardType.POWER, () -> { 
                            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new MetallicizePower(AbstractDungeon.player, 2), 2)); }));
                block.add( new Infusion( block.actText[3], AbstractCard.CardTarget.ENEMY, () -> { 
                            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(LinkedCardUsePatch.lastMonster, AbstractDungeon.player, new WeakPower(AbstractDungeon.player, 1, false), 1)); }));

                ironclad.add(block);

            // Str
                InfusionSet str = new InfusionSet("Str", InfusionVFXEmber.class);

                str.add( new Infusion( str.actText[0], AbstractCard.CardTarget.ENEMY, () -> { 
                            AbstractDungeon.actionManager.addToBottom((new DamageAction(AbstractDungeon.getMonsters().getRandomMonster(null, true, AbstractDungeon.cardRandomRng), new DamageInfo((AbstractCreature)AbstractDungeon.player, calculateCardDamage(3), DamageInfo.DamageType.NORMAL), AbstractGameAction.AttackEffect.SLASH_HORIZONTAL)));}));
                str.add( new Infusion( str.actText[1], AbstractCard.CardTarget.SELF, () -> { 
                            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new StrengthPower(AbstractDungeon.player, 3), 3));
                            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new LoseStrengthPower(AbstractDungeon.player, 3), 3)); }));
                str.add( new Infusion( str.actText[1], AbstractCard.CardTarget.NONE, () -> { 
                            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new StrengthPower(AbstractDungeon.player, 3), 3));
                            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new LoseStrengthPower(AbstractDungeon.player, 3), 3)); }));
                str.add( new Infusion( str.actText[2], AbstractCard.CardType.POWER, () -> { 
                            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new StrengthPower(AbstractDungeon.player, 2), 2)); }));

                ironclad.add(str);

            // Exhaust
                InfusionSet exhaust = new InfusionSet("Exhaust", InfusionVFXEmber.class);

                exhaust.add( new Infusion( exhaust.actText[0], AbstractCard.CardTarget.ENEMY, () -> { 
                            AbstractDungeon.actionManager.addToBottom(new ExhaustAction(1, true, false, false)); }));
                exhaust.add( new Infusion( exhaust.actText[1], AbstractCard.CardTarget.SELF, () -> { 
                            AbstractDungeon.actionManager.addToBottom(new ExhaustAction(1, false)); }));
                exhaust.add( new Infusion( exhaust.actText[2], AbstractCard.CardType.POWER, () -> { 
                            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new FeelNoPainPower(AbstractDungeon.player, 2), 2)); }));

                ironclad.add(exhaust);

        characterInfusionMasterList.put(AbstractPlayer.PlayerClass.IRONCLAD, ironclad);

        // Silent
            charSet = new ArrayList();

            // Shiv
                set = new InfusionSet("Shiv", InfusionVFXPoison.class);

                set.add( new Infusion( set.actText[0], () -> { 
                            AbstractDungeon.actionManager.addToBottom(new MakeTempCardInHandAction(new Shiv(), 1)); }));
                set.add( new Infusion( set.actText[1], AbstractCard.CardTarget.SELF, () -> { 
                            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new AccuracyPower(AbstractDungeon.player, 1), 1)); }));
                set.add( new Infusion( set.actText[2], AbstractCard.CardType.POWER, () -> { 
                            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new AccuracyPower(AbstractDungeon.player, 3), 3)); }));
                set.add( new Infusion( set.actText[3], AbstractCard.CardTarget.SELF, () -> { 
                            AbstractDungeon.actionManager.addToBottom(new DiscardAction(AbstractDungeon.player, AbstractDungeon.player, 1, false));
                            AbstractDungeon.actionManager.addToBottom(new MakeTempCardInHandAction(new Shiv(), 2)); }));

                charSet.add(set);

            // Dex
                set = new InfusionSet("Dex", InfusionVFXPoison.class);

                set.add( new Infusion( set.actText[0], () -> { 
                            AbstractDungeon.actionManager.addToBottom(new GainBlockAction(AbstractDungeon.player, AbstractDungeon.player, applyPowersToBlock(4))); }));
                set.add( new Infusion( set.actText[0], () -> { 
                            AbstractDungeon.actionManager.addToBottom(new GainBlockAction(AbstractDungeon.player, AbstractDungeon.player, applyPowersToBlock(4))); }));
                set.add( new Infusion( set.actText[1], AbstractCard.CardTarget.SELF, () -> { 
                            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new NextTurnBlockPower(AbstractDungeon.player, applyPowersToBlock(4)), applyPowersToBlock(4))); }));
                set.add( new Infusion( set.actText[1], AbstractCard.CardTarget.SELF, () -> { 
                            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new NextTurnBlockPower(AbstractDungeon.player, applyPowersToBlock(4)), applyPowersToBlock(4))); }));
                set.add( new Infusion( set.actText[2], AbstractCard.CardType.POWER, () -> { 
                            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new DexterityPower(AbstractDungeon.player, 1), 1)); }));
                set.add( new Infusion( set.actText[3], AbstractCard.CardTarget.SELF, () -> { 
                            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new BlurPower(AbstractDungeon.player, 1), 1)); }));
                set.add( new Infusion( set.actText[4], AbstractCard.CardTarget.ENEMY, () -> { 
                            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(LinkedCardUsePatch.lastMonster, AbstractDungeon.player, new WeakPower(AbstractDungeon.player, 1, false), 1)); }));
                set.add( new Infusion( set.actText[4], AbstractCard.CardTarget.ENEMY, () -> { 
                            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(LinkedCardUsePatch.lastMonster, AbstractDungeon.player, new WeakPower(AbstractDungeon.player, 1, false), 1)); }));

                charSet.add(set);

            // Discard
                set = new InfusionSet("Discard", InfusionVFXPoison.class);

                set.add( new Infusion( set.actText[0], () -> { 
                            AbstractDungeon.actionManager.addToBottom(new DiscardAction(AbstractDungeon.player, AbstractDungeon.player, 1, false)); }));
                set.add( new Infusion( set.actText[1], () -> { 
                            AbstractDungeon.actionManager.addToBottom(new DrawCardAction(AbstractDungeon.player, 1));
                            AbstractDungeon.actionManager.addToBottom(new DiscardAction(AbstractDungeon.player, AbstractDungeon.player, 1, false)); }));
                set.add( new Infusion( set.actText[2], () -> { 
                            AbstractDungeon.actionManager.addToBottom(new GainEnergyIfDiscardAction(1)); }));
                set.add( new Infusion( set.actText[3], () -> { 
                            AbstractDungeon.actionManager.addToBottom(new DrawCardAction(AbstractDungeon.player, 1)); }));

                charSet.add(set);

            // Poison
                set = new InfusionSet("Poison", InfusionVFXPoison.class);

                set.add( new Infusion( set.actText[0], AbstractCard.CardTarget.ENEMY, () -> { 
                            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(LinkedCardUsePatch.lastMonster, AbstractDungeon.player, new PoisonPower(LinkedCardUsePatch.lastMonster, AbstractDungeon.player, 3), 3)); }));
                set.add( new Infusion( set.actText[1], AbstractCard.CardTarget.ALL_ENEMY, () -> { 
                            for (AbstractMonster monster : (AbstractDungeon.getMonsters()).monsters)
                                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(monster, AbstractDungeon.player, new PoisonPower(monster, AbstractDungeon.player, 2), 2)); }));
                set.add( new Infusion( set.actText[2], AbstractCard.CardType.SKILL, () -> { 
                            AbstractMonster randomMonster = AbstractDungeon.getMonsters().getRandomMonster(null, true, AbstractDungeon.cardRandomRng);
                            AbstractDungeon.actionManager.addToBottom(new BouncingFlaskAction(randomMonster, 2, 2)); }));

                charSet.add(set);

        characterInfusionMasterList.put(AbstractPlayer.PlayerClass.THE_SILENT, charSet);

        // DEFECT
            charSet = new ArrayList();

            // Lightning
                set = new InfusionSet("Lightning", InfusionVFXShock.class);

                set.add( new Infusion( set.actText[0], () -> { 
                            AbstractDungeon.actionManager.addToBottom(new ChannelAction(new Lightning())); }));
                set.add( new Infusion( set.actText[0], () -> { 
                            AbstractDungeon.actionManager.addToBottom(new ChannelAction(new Lightning())); }));
                set.add( new Infusion( set.actText[0], () -> { 
                            AbstractDungeon.actionManager.addToBottom(new ChannelAction(new Lightning())); }));
                set.add( new Infusion( set.actText[1], AbstractCard.CardTarget.ENEMY, () -> { 
                            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(LinkedCardUsePatch.lastMonster, AbstractDungeon.player, new LockOnPower(LinkedCardUsePatch.lastMonster, 1), 1)); }));
                set.add( new Infusion( set.actText[2], AbstractCard.CardType.POWER, () -> { 
                            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new FocusPower(AbstractDungeon.player, 1), 1)); }));
                set.add( new Infusion( set.actText[2], () -> { 
                            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new FocusPower(AbstractDungeon.player, 1), 1)); }));

                charSet.add(set);

            // Frost
                set = new InfusionSet("Frost", InfusionVFXShock.class);

                set.add( new Infusion( set.actText[0], () -> { 
                            AbstractDungeon.actionManager.addToBottom(new ChannelAction(new Frost())); }));
                set.add( new Infusion( set.actText[0], () -> { 
                            AbstractDungeon.actionManager.addToBottom(new ChannelAction(new Frost())); }));
                set.add( new Infusion( set.actText[0], () -> { 
                            AbstractDungeon.actionManager.addToBottom(new ChannelAction(new Frost())); }));
                set.add( new Infusion( set.actText[1], () -> { 
                            AbstractDungeon.actionManager.addToBottom(new AnimateOrbAction(1)); 
                            AbstractDungeon.actionManager.addToBottom(new EvokeWithoutRemovingOrbAction(1)); }));
                set.add( new Infusion( set.actText[2], AbstractCard.CardType.POWER, () -> { 
                            AbstractDungeon.actionManager.addToBottom(new IncreaseMaxOrbAction(1)); }));

                charSet.add(set);

            // Dark
                set = new InfusionSet("Dark", InfusionVFXShock.class);

                set.add( new Infusion( set.actText[0], () -> { 
                            AbstractDungeon.actionManager.addToBottom(new ChannelAction(new Dark())); }));
                set.add( new Infusion( set.actText[0], () -> { 
                            AbstractDungeon.actionManager.addToBottom(new ChannelAction(new Dark())); }));
                set.add( new Infusion( set.actText[1], () -> { 
                            AbstractDungeon.actionManager.addToBottom(new AnimateOrbAction(1)); 
                            AbstractDungeon.actionManager.addToBottom(new EvokeOrbAction(1)); }));
                set.add( new Infusion( set.actText[2], AbstractCard.CardType.POWER, () -> { 
                            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new LoopPower(AbstractDungeon.player, 1), 1)); }));
                set.add( new Infusion( set.actText[3], () -> { 
                            AbstractDungeon.actionManager.addToBottom(new DarkImpulseAction()); }));

                charSet.add(set);

            // Plasma
                set = new InfusionSet("Plasma", InfusionVFXShock.class);

                set.add( new Infusion( set.actText[0], () -> { 
                            AbstractDungeon.actionManager.addToBottom(new ChannelAction(AbstractOrb.getRandomOrb(true))); }));
                set.add( new Infusion( set.actText[0], () -> { 
                            AbstractDungeon.actionManager.addToBottom(new ChannelAction(AbstractOrb.getRandomOrb(true))); }));
                set.add( new Infusion( set.actText[2], AbstractCard.CardType.POWER, () -> { 
                            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new ChannelPower(AbstractDungeon.player, 1), 1)); }));
                set.add( new Infusion( set.actText[3], () -> { 
                            AbstractDungeon.actionManager.addToBottom(new IncreaseMaxOrbAction(1)); }));

                charSet.add(set);

        characterInfusionMasterList.put(AbstractPlayer.PlayerClass.DEFECT, charSet);

        // WATCHER
            charSet = new ArrayList();

            // Stance
                set = new InfusionSet("Stance", InfusionVFXPetals.class);

                set.add( new Infusion( set.actText[0], () -> { 
                            AbstractDungeon.actionManager.addToBottom(new ChangeStanceAction("Wrath")); }));
                set.add( new Infusion( set.actText[1], () -> { 
                            AbstractDungeon.actionManager.addToBottom(new ChangeStanceAction("Calm")); }));
                set.add( new Infusion( set.actText[0], () -> { 
                            AbstractDungeon.actionManager.addToBottom(new ChangeStanceAction("Wrath")); }));
                set.add( new Infusion( set.actText[1], () -> { 
                            AbstractDungeon.actionManager.addToBottom(new ChangeStanceAction("Calm")); }));
                set.add( new Infusion( set.actText[2], () -> { 
                            AbstractDungeon.actionManager.addToBottom(new ChangeStanceAction("Neutral")); }));

                charSet.add(set);

            // Mantra
                set = new InfusionSet("Mantra", InfusionVFXPetals.class);

                set.add( new Infusion( set.actText[0], () -> { 
                            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new MantraPower(AbstractDungeon.player, 1), 1)); }));
                set.add( new Infusion( set.actText[1], () -> { 
                            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new MantraPower(AbstractDungeon.player, 2), 2)); }));
                set.add( new Infusion( set.actText[2], AbstractCard.CardType.POWER, () -> { 
                            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new DevotionPower(AbstractDungeon.player, 1), 1)); }));

                charSet.add(set);

            // Scry
                set = new InfusionSet("Scry", InfusionVFXPetals.class);

                set.add( new Infusion( set.actText[0], () -> { 
                            AbstractDungeon.actionManager.addToBottom(new ScryAction(2)); }));
                set.add( new Infusion( set.actText[0], () -> { 
                            AbstractDungeon.actionManager.addToBottom(new ScryAction(2)); }));
                set.add( new Infusion( set.actText[0], () -> { 
                            AbstractDungeon.actionManager.addToBottom(new ScryAction(2)); }));
                set.add( new Infusion( set.actText[0], () -> { 
                            AbstractDungeon.actionManager.addToBottom(new ScryAction(2)); }));
                set.add( new Infusion( set.actText[1], true, false, false, false));
                set.add( new Infusion( set.actText[1], true, false, false, false));
                set.add( new Infusion( set.actText[1], true, false, false, false));
                set.add( new Infusion( set.actText[2], false, false, true, false));

                charSet.add(set);

            // Retain
                set = new InfusionSet("Retain", InfusionVFXPetals.class);

                set.add( new Infusion( set.actText[0], false, true, false, false));
                set.add( new Infusion( set.actText[0], false, true, false, false));
                set.add( new Infusion( set.actText[0], false, true, false, false));
                set.add( new Infusion( set.actText[1], () -> { 
                            AbstractDungeon.actionManager.addToBottom(new MakeTempCardInHandAction(new Safety(), 1)); }));
                set.add( new Infusion( set.actText[2], () -> { 
                            AbstractDungeon.actionManager.addToBottom(new MakeTempCardInHandAction(new Smite(), 1)); }));
                set.add( new Infusion( set.actText[3], AbstractCard.CardType.POWER, () -> { 
                            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new RetainCardPower(AbstractDungeon.player, 1), 1)); }));

                set.add( new Infusion( set.actText[3], AbstractCard.CardType.POWER, () -> { 
                            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new RetainCardPower(AbstractDungeon.player, 1), 1)); }));
                charSet.add(set);

        characterInfusionMasterList.put(AbstractPlayer.PlayerClass.WATCHER, charSet);

        // Colorless
            defaultInfusions = new InfusionSet("Colorless");

            defaultInfusions.add( new Infusion( defaultInfusions.actText[0], false, false, false, true));
            defaultInfusions.add( new Infusion( defaultInfusions.actText[1], () -> { 
                        AbstractCard c = AbstractDungeon.returnTrulyRandomColorlessCardInCombat(AbstractDungeon.cardRandomRng).makeCopy();
                        AbstractDungeon.actionManager.addToBottom(new MakeTempCardInHandAction(c, 1)); }));
            defaultInfusions.add( new Infusion( defaultInfusions.actText[2], AbstractCard.CardType.POWER, () -> { 
                        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new ArtifactPower(AbstractDungeon.player, 1), 1)); }));
            defaultInfusions.add( new Infusion( defaultInfusions.actText[3], () -> { 
                        AbstractDungeon.actionManager.addToBottom(new DrawCardAction(AbstractDungeon.player, 1)); }));
    }

    public static int applyPowersToBlock(int block) {
        float tmp = block;

        for (AbstractPower p : AbstractDungeon.player.powers)
          tmp = p.modifyBlock(tmp, LinkedCardUsePatch.lastCard); 
        for (AbstractPower p : AbstractDungeon.player.powers)
          tmp = p.modifyBlockLast(tmp); 
        if (tmp < 0.0F)
          tmp = 0.0F; 
        return MathUtils.floor(tmp);
    }

    public static int calculateCardDamage(int dmg) {
        float tmp = dmg;

        AbstractMonster mo = LinkedCardUsePatch.lastMonster;
        AbstractPlayer player = AbstractDungeon.player;

        if (mo != null) {
          for (AbstractRelic r : player.relics) {
            tmp = r.atDamageModify(tmp, LinkedCardUsePatch.lastCard);
          } 
          for (AbstractPower p : player.powers)
            tmp = p.atDamageGive(tmp, LinkedCardUsePatch.lastCard.damageTypeForTurn, LinkedCardUsePatch.lastCard); 
          tmp = player.stance.atDamageGive(tmp, LinkedCardUsePatch.lastCard.damageTypeForTurn, LinkedCardUsePatch.lastCard);
          for (AbstractPower p : mo.powers)
            tmp = p.atDamageReceive(tmp, LinkedCardUsePatch.lastCard.damageTypeForTurn, LinkedCardUsePatch.lastCard); 
          for (AbstractPower p : player.powers)
            tmp = p.atDamageFinalGive(tmp, LinkedCardUsePatch.lastCard.damageTypeForTurn, LinkedCardUsePatch.lastCard); 
          for (AbstractPower p : mo.powers)
            tmp = p.atDamageFinalReceive(tmp, LinkedCardUsePatch.lastCard.damageTypeForTurn, LinkedCardUsePatch.lastCard); 
          if (tmp < 0.0F)
            tmp = 0.0F; 
          return MathUtils.floor(tmp);
        }
        return dmg;
    }
}