package chronoMods.coop;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.badlogic.gdx.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;

import com.megacrit.cardcrawl.core.*;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.helpers.*;
import com.megacrit.cardcrawl.relics.*;
import com.megacrit.cardcrawl.screens.*;
import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.rewards.*;
import com.megacrit.cardcrawl.actions.*;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.unique.*;
import com.megacrit.cardcrawl.actions.defect.*;
import com.megacrit.cardcrawl.actions.watcher.*;
import com.megacrit.cardcrawl.powers.*;
import com.megacrit.cardcrawl.rooms.*;
import com.megacrit.cardcrawl.ui.buttons.*;
import com.megacrit.cardcrawl.ui.campfire.*;
import com.megacrit.cardcrawl.actions.defect.*;
import com.megacrit.cardcrawl.orbs.*;
import com.megacrit.cardcrawl.vfx.*;
import com.megacrit.cardcrawl.vfx.campfire.*;
import com.megacrit.cardcrawl.vfx.cardManip.*;
import com.megacrit.cardcrawl.map.*;

import basemod.*;
import basemod.abstracts.*;
import basemod.interfaces.*;

import java.util.*;

import chronoMods.*;

public class CoopRelicStackingPatches {

    /*
    Non-stacking relics:
        Dream Catcher
        Strange Spoon
        N'loths Gift
        Odd Mushroom
        Meat on the Bone
        Gambling Chip
        Regal Pillow
        Centennial Puzzle

    Relics that make no sense to stack:
        Frozen Egg
        Toxic Egg
        Blue Candle
        Juzu Bracelet
        Turnip 
        Smiling Mask
        Calipers
        Courier
        Membership Card
        Teardrop Locket
        Torii

        Ginger
        Frozen Eye
        Medical Kit
        Orange Pellets
        Prismatic Shard
    */

    public static int relicCount(String relicId, boolean skipOnce) {
        int count = 0;
        for (AbstractRelic r : AbstractDungeon.player.relics)
            if (r.relicId.equals(relicId))
                count++;

        if (skipOnce && count > 0)
            count--;
    
        return count;
    }

    // Preserved Insect
    @SpirePatch(clz = PreservedInsect.class, method="atBattleStart")
    public static class PreservedInsectHPStacking {
        public static void Replace(PreservedInsect __instance) {
            if ((AbstractDungeon.getCurrRoom()).eliteTrigger) {
                __instance.flash();
                for (AbstractMonster m : (AbstractDungeon.getCurrRoom()).monsters.monsters) {
                    m.currentHealth = (int)(m.currentHealth * 0.75F);
                    if (m.currentHealth < 1)
                        m.currentHealth = 1;
                    m.healthBarUpdatedEvent();
                } 
                AbstractDungeon.actionManager.addToTop(new RelicAboveCreatureAction(AbstractDungeon.player, __instance));
            } 
        }
    }

    @SpirePatch(clz = AbstractCreature.class, method="loadAnimation")
    public static class PreservedInsectSizeStacking {
        @SpireInsertPatch(rloc=408-401)
        public static void Insert(AbstractCreature __instance, String atlasUrl, String skeletonUrl, @ByRef float[] scale) {

            if (!__instance.isPlayer && (AbstractDungeon.getCurrRoom()).eliteTrigger)
                for (int i = 0; i < relicCount("PreservedInsect", true); i++)
                    scale[0] += 0.3F;
        }
    }

    // Boot
    @SpirePatch(clz = Boot.class, method="onAttackToChangeDamage")
    public static class BootStacking {
        public static int Postfix(int __result, Boot __instance, DamageInfo info, int damageAmount) {
            int Boots = 4;
            Boots += relicCount("Boot", false);

            if (info.owner != null && info.type != DamageInfo.DamageType.HP_LOSS && info.type != DamageInfo.DamageType.THORNS && damageAmount > 0 && damageAmount < Boots) {
              __instance.flash();
              AbstractDungeon.actionManager.addToBottom(new RelicAboveCreatureAction(AbstractDungeon.player, __instance));
              return Boots;
            } 

            return __result;
        }
    }

    // Prayer Wheel
    @SpirePatch(clz = CombatRewardScreen.class, method="setupItemReward")
    public static class PrayerWheelStacking {
        @SpireInsertPatch(rloc=96-72)
        public static void Insert(CombatRewardScreen __instance) {
            
            RewardItem cardReward;
            for (int i = 0; i < relicCount("Prayer Wheel", true); i++) {
                cardReward = new RewardItem();
                if (cardReward.cards.size() > 0)
                  __instance.rewards.add(cardReward); 
            }
        }
    }

    // Snecko Skull
    @SpirePatch(clz = ApplyPowerAction.class, method=SpirePatch.CONSTRUCTOR, paramtypez = {AbstractCreature.class, AbstractCreature.class, AbstractPower.class, int.class, boolean.class, AbstractGameAction.AttackEffect.class})
    public static class SneckoSkullStacking {
        @SpireInsertPatch(rloc=64-54)
        public static void Insert(ApplyPowerAction __instance, AbstractCreature target, AbstractCreature source, @ByRef AbstractPower[] powerToApply, @ByRef int[] stackAmount, boolean isFast, AbstractGameAction.AttackEffect effect) {
            if (AbstractDungeon.player.hasRelic("Snake Skull") && source != null && source.isPlayer && target != source && powerToApply[0].ID.equals("Poison")) {

                for (int i = 0; i < relicCount("Snake Skull", true); i++) {
                    powerToApply[0].amount++;
                    stackAmount[0]++;
                }   
            }
        }
    }

    // Champion Belt
    @SpirePatch(clz = ApplyPowerAction.class, method="update")
    public static class ChampionBeltStacking {
        @SpireInsertPatch(rloc=167-141)
        public static void Insert(ApplyPowerAction __instance, AbstractPower ___powerToApply) {
            if (AbstractDungeon.player.hasRelic("Champion Belt") && __instance.source != null && __instance.source.isPlayer && __instance.target != __instance.source && ___powerToApply.ID
                .equals("Vulnerable") && !__instance.target.hasPower("Artifact")) {

                boolean skipOnce = true;
                for (AbstractRelic r : AbstractDungeon.player.relics) {
                    if (r.relicId.equals("Champion Belt")) {
                        if (skipOnce) {
                            skipOnce = false;
                            continue;
                        }
                        r.onTrigger(__instance.target);
                    }
                }   
            }
        }
    }

    // SingingBowl
    @SpirePatch(clz = SingingBowlButton.class, method="onClick")
    public static class SingingBowlStacking {
        public static void Prefix(SingingBowlButton __instance) {
            for (int i = 0; i < relicCount("Singing Bowl", true); i++) {
                AbstractDungeon.player.increaseMaxHp(2, true);
            }
        }
    }

    @SpirePatch(clz = SingingBowlButton.class, method="render")
    public static class SingingBowlTextChange {
        @SpireInsertPatch(rloc=114-109)
        public static SpireReturn Insert(SingingBowlButton __instance, SpriteBatch sb, float ___current_x, Color ___btnColor) {
            FontHelper.renderFontCentered(sb, FontHelper.buttonLabelFont, "+" + (relicCount("Singing Bowl", false) * 2) + " Max HP", ___current_x, SkipCardButton.TAKE_Y, ___btnColor);

            return SpireReturn.Return(null);
        }
    }

    // Paper Phrog
    @SpirePatch(clz = VulnerablePower.class, method="atDamageReceive")
    public static class PaperPhrogStacking {
        @SpireInsertPatch(rloc=88-80)
        public static SpireReturn Insert(VulnerablePower __instance, float damage, DamageInfo.DamageType type) {
            float mod = 1.5f;
            for (AbstractRelic r : AbstractDungeon.player.relics) {
                if (r.relicId.equals("Paper Frog")) {
                    // r.flash();
                    mod += 0.25f;
                }
            }
            return SpireReturn.Return(damage * mod);
        }
    }

    // Paper Krane
    @SpirePatch(clz = WeakPower.class, method="atDamageGive")
    public static class PaperKraneStacking {
        @SpireInsertPatch(rloc=77-75)
        public static SpireReturn Insert(WeakPower __instance, float damage, DamageInfo.DamageType type) {
            float mod = 0.75f;
            for (AbstractRelic r : AbstractDungeon.player.relics) {
                if (r.relicId.equals("Paper Crane")) {
                    // r.flash();
                    mod -= 0.15f;
                }
            }
            if (mod < 0f) { mod = 0f; }
            return SpireReturn.Return(damage * mod);
        }
    }

    // Gold Plated Cables
    @SpirePatch(clz = AbstractPlayer.class, method="applyStartOfTurnOrbs")
    public static class GoldPlatedPlayerStacking {
        public static void Postfix(AbstractPlayer __instance) {
            for (int i = 0; i < relicCount("Cables", true); i++) {
                if (!__instance.orbs.isEmpty())
                  if (!(__instance.orbs.get(0) instanceof EmptyOrbSlot))
                    __instance.orbs.get(0).onStartOfTurn(); 
            }
        }
    }

    @SpirePatch(clz = TriggerEndOfTurnOrbsAction.class, method="update")
    public static class GoldPlatedActionStacking {
        public static void Prefix(TriggerEndOfTurnOrbsAction __instance) {
            for (int i = 0; i < relicCount("Cables", true); i++) {
                if (!AbstractDungeon.player.orbs.isEmpty())
                  if (!(AbstractDungeon.player.orbs.get(0) instanceof EmptyOrbSlot))
                    AbstractDungeon.player.orbs.get(0).onEndOfTurn(); 
            }
        }
    }

    @SpirePatch(clz = DarkImpulseAction.class, method="update")
    public static class GoldPlatedDarknessStacking {
        @SpireInsertPatch(rloc=28-19)
        public static void Insert(DarkImpulseAction __instance) {
            for (int i = 0; i < relicCount("Cables", true); i++) {
                if (!AbstractDungeon.player.orbs.isEmpty())
                  if (!(AbstractDungeon.player.orbs.get(0) instanceof EmptyOrbSlot))
                    if (AbstractDungeon.player.orbs.get(0) instanceof com.megacrit.cardcrawl.orbs.Dark) {
                      AbstractDungeon.player.orbs.get(0).onStartOfTurn();
                      AbstractDungeon.player.orbs.get(0).onEndOfTurn();
                    }  
            }
        }
    }

    // Pen Nib
    @SpirePatch(clz = PenNibPower.class, method="atDamageGive")
    public static class PenNibStacking {
        public static float Postfix(float __result, PenNibPower __instance, float damage, DamageInfo.DamageType type) {

            if (type == DamageInfo.DamageType.NORMAL) {
                return damage * (__instance.amount + 1); 
            }

            return __result;
        }
    }

    // Omamori
    @SpirePatch(clz = ShowCardAndObtainEffect.class, method=SpirePatch.CONSTRUCTOR, paramtypez = {AbstractCard.class, float.class, float.class, boolean.class})
    public static class OmamoriObtainStacking {
        @SpireInsertPatch(rloc=32-24)
        public static void Insert(ShowCardAndObtainEffect __instance, AbstractCard card, float x, float y, boolean convergeCards) {
            if (card.color == AbstractCard.CardColor.CURSE && !__instance.isDone) {
                for (AbstractRelic r : AbstractDungeon.player.relics) {
                    if (r.relicId.equals("Omamori")) {
                        if (r.counter > 0) {
                            ((Omamori)r).use();
                            __instance.duration = 0.0F;
                            __instance.isDone = true;
                            return;
                        }
                    }
                }
            }
        }
    }

    @SpirePatch(clz = FastCardObtainEffect.class, method=SpirePatch.CONSTRUCTOR)
    public static class OmamoriFastObtainStacking {
        @SpireInsertPatch(rloc=32-25)
        public static void Insert(FastCardObtainEffect __instance, AbstractCard card, float x, float y) {
            if (card.color == AbstractCard.CardColor.CURSE && !__instance.isDone) {
                for (AbstractRelic r : AbstractDungeon.player.relics) {
                    if (r.relicId.equals("Omamori")) {
                        if (r.counter > 0) {
                            ((Omamori)r).use();
                            __instance.duration = 0.0F;
                            __instance.isDone = true;
                            return;
                        }
                    }
                }
            }
        }
    }

    // Tiny Chest
    @SpirePatch(clz = EventHelper.class, method="roll", paramtypez = {com.megacrit.cardcrawl.random.Random.class})
    public static class TinyChestStacking {
        @SpireInsertPatch(rloc=133-111, localvars = {"forceChest"})
        public static void Insert(com.megacrit.cardcrawl.random.Random eventRng, @ByRef boolean[] forceChest) {
            if (!AbstractDungeon.player.hasRelic("Tiny Chest")) { return; }

            boolean skipOnce = true;
            for (AbstractRelic r : AbstractDungeon.player.relics) {
                if (r.relicId.equals("Tiny Chest")) {
                    if (skipOnce) {
                        skipOnce = false;
                        continue;
                    }

                    r.counter++;
                    if (r.counter > 3) {
                        r.counter = 4;
                    }

                    if (r.counter == 4 && !forceChest[0]) {
                        r.counter = 0;
                        r.flash();
                        forceChest[0] = true;
                    } 
                }
            }
        }
    }

    // Chem X
    @SpirePatch(clz = ReinforcedBodyAction.class, method="update")
    public static class ChemXStackingReinforce {
        @SpireInsertPatch(rloc=34-27, localvars={"effect"})
        public static void Insert(ReinforcedBodyAction __instance, @ByRef int[] effect) {
            effect[0] += relicCount("Chemical X", true) * 2;
        }
    }

    @SpirePatch(clz = DoppelgangerAction.class, method="update")
    public static class ChemXStackingDoppelgangerAction {
        @SpireInsertPatch(rloc=35-29, localvars={"effect"})
        public static void Insert(DoppelgangerAction __instance, @ByRef int[] effect) {
            effect[0] += relicCount("Chemical X", true) * 2;
        }
    }

    @SpirePatch(clz = MalaiseAction.class, method="update")
    public static class ChemXStackingMalaiseAction {
        @SpireInsertPatch(rloc=43-36, localvars={"effect"})
        public static void Insert(MalaiseAction __instance, @ByRef int[] effect) {
            effect[0] += relicCount("Chemical X", true) * 2;
        }
    }

    @SpirePatch(clz = MulticastAction.class, method="update")
    public static class ChemXStackingMulticastAction {
        @SpireInsertPatch(rloc=38-30, localvars={"effect"})
        public static void Insert(MulticastAction __instance, @ByRef int[] effect) {
            effect[0] += relicCount("Chemical X", true) * 2;
        }
    }

    @SpirePatch(clz = SkewerAction.class, method="update")
    public static class ChemXStackingSkewerAction {
        @SpireInsertPatch(rloc=47-40, localvars={"effect"})
        public static void Insert(SkewerAction __instance, @ByRef int[] effect) {
            effect[0] += relicCount("Chemical X", true) * 2;
        }
    }

    @SpirePatch(clz = TempestAction.class, method="update")
    public static class ChemXStackingTempestAction {
        @SpireInsertPatch(rloc=36-29, localvars={"effect"})
        public static void Insert(TempestAction __instance, @ByRef int[] effect) {
            effect[0] += relicCount("Chemical X", true) * 2;
        }
    }

    @SpirePatch(clz = TransmutationAction.class, method="update")
    public static class ChemXStackingTransmutationAction {
        @SpireInsertPatch(rloc=36-29, localvars={"effect"})
        public static void Insert(TransmutationAction __instance, @ByRef int[] effect) {
            effect[0] += relicCount("Chemical X", true) * 2;
        }
    }

    @SpirePatch(clz = WhirlwindAction.class, method="update")
    public static class ChemXStackingWhirlwindAction {
        @SpireInsertPatch(rloc=46-39, localvars={"effect"})
        public static void Insert(WhirlwindAction __instance, @ByRef int[] effect) {
            effect[0] += relicCount("Chemical X", true) * 2;
        }
    }

    @SpirePatch(clz = BrillianceAction.class, method="update")
    public static class ChemXStackingBrillianceAction {
        @SpireInsertPatch(rloc=34-27, localvars={"effect"})
        public static void Insert(BrillianceAction __instance, @ByRef int[] effect) {
            effect[0] += relicCount("Chemical X", true) * 2;
        }
    }

    @SpirePatch(clz = CollectAction.class, method="update")
    public static class ChemXStackingCollectAction {
        @SpireInsertPatch(rloc=34-27, localvars={"effect"})
        public static void Insert(CollectAction __instance, @ByRef int[] effect) {
            effect[0] += relicCount("Chemical X", true) * 2;
        }
    }

    @SpirePatch(clz = ConjureBladeAction.class, method="update")
    public static class ChemXStackingConjureBladeAction {
        @SpireInsertPatch(rloc=34-27, localvars={"effect"})
        public static void Insert(ConjureBladeAction __instance, @ByRef int[] effect) {
            effect[0] += relicCount("Chemical X", true) * 2;
        }
    }

    @SpirePatch(clz = DivinePunishmentAction.class, method="update")
    public static class ChemXStackingDivinePunishmentAction {
        @SpireInsertPatch(rloc=36-27, localvars={"effect"})
        public static void Insert(DivinePunishmentAction __instance, @ByRef int[] effect) {
            effect[0] += relicCount("Chemical X", true) * 2;
        }
    }

    // Shovel
    @SpirePatch(clz = CampfireDigEffect.class, method="update")
    public static class ShovelStacking {
        @SpireInsertPatch(rloc=41-30)
        public static void Insert(CampfireDigEffect __instance) {
            for (int i = 0; i < relicCount("Shovel", true); i++)
                (AbstractDungeon.getCurrRoom()).rewards.add(new RewardItem(AbstractDungeon.returnRandomRelic(AbstractDungeon.returnRandomRelicTier())));
        }
    }

    @SpirePatch(clz = Shovel.class, method="addCampfireOption")
    public static class ShovelNoDoubleOptions {
        public static SpireReturn Prefix(Shovel __instance, ArrayList<AbstractCampfireOption> options) {
            for (AbstractCampfireOption option : options)
                if (option instanceof DigOption)
                    return SpireReturn.Return(null);

            return SpireReturn.Continue();
        }
    }

    // Unceasing Top
    @SpirePatch(clz = UnceasingTop.class, method="onRefreshHand")
    public static class TopStacking {
        @SpireInsertPatch(rloc=48-41)
        public static void Insert(UnceasingTop __instance) {
            for (int i = 0; i < relicCount("Unceasing Top", true); i++)
                AbstractDungeon.actionManager.addToBottom(new DrawCardAction((AbstractCreature)AbstractDungeon.player, 1));
        }
    }

    // Molten Egg
    @SpirePatch(clz = MoltenEgg2.class, method="onObtainCard")
    public static class MoltenEgg2Stacking {
        public static void Replace(MoltenEgg2 __instance, AbstractCard c) {
            if (c.type == AbstractCard.CardType.ATTACK && c.canUpgrade())
                c.upgrade(); 
        }
    }

    // Peace Pipe
    @SpirePatch(clz = CampfireTokeEffect.class, method="update")
    public static class PeacePipeStacking {
        public static SpireReturn Prefix(CampfireTokeEffect __instance, boolean ___openedScreen, Color ___screenColor) {
            if (TogetherManager.gameMode != TogetherManager.mode.Coop) { return SpireReturn.Continue(); }

            if (!AbstractDungeon.isScreenUp) {
                __instance.duration -= Gdx.graphics.getDeltaTime();
                if (__instance.duration > 1.0F) {
                  ___screenColor.a = Interpolation.fade.apply(1.0F, 0.0F, (__instance.duration - 1.0F) * 2.0F);
                } else {
                  ___screenColor.a = Interpolation.fade.apply(0.0F, 1.0F, __instance.duration / 1.5F);
                } 
            } 
            if (!AbstractDungeon.isScreenUp && !AbstractDungeon.gridSelectScreen.selectedCards.isEmpty() && AbstractDungeon.gridSelectScreen.forPurge) {
                int i = 0;
                for (AbstractCard card : AbstractDungeon.gridSelectScreen.selectedCards) {
                  CardCrawlGame.metricData.addCampfireChoiceData("PURGE", card.getMetricID());
                  CardCrawlGame.sound.play("CARD_EXHAUST");
                  AbstractDungeon.topLevelEffects.add(new PurgeCardEffect(card, (Settings.WIDTH / (AbstractDungeon.gridSelectScreen.selectedCards.size()+1-i)), (Settings.HEIGHT / 2)));
                  AbstractDungeon.player.masterDeck.removeCard(card);
                  i++;                 
                }
                AbstractDungeon.gridSelectScreen.selectedCards.clear();
            } 
            if (__instance.duration < 1.0F && !___openedScreen) {
              ReflectionHacks.setPrivate(__instance, CampfireTokeEffect.class, "openedScreen", true);
              AbstractDungeon.gridSelectScreen.open(
                  CardGroup.getGroupWithoutBottledCards(AbstractDungeon.player.masterDeck.getPurgeableCards()), relicCount("Peace Pipe", false), __instance.TEXT[0], false, false, true, true);
            } 
            if (__instance.duration < 0.0F) {
              __instance.isDone = true;
              if (CampfireUI.hidden) {
                AbstractRoom.waitTimer = 0.0F;
                (AbstractDungeon.getCurrRoom()).phase = AbstractRoom.RoomPhase.COMPLETE;
                ((RestRoom)AbstractDungeon.getCurrRoom()).cutFireSound();
              } 
            } 

            return SpireReturn.Return(null);
        }
    }

    @SpirePatch(clz = PeacePipe.class, method="addCampfireOption")
    public static class PeacePipeNoDoubleOptions {
        public static SpireReturn Prefix(PeacePipe __instance, ArrayList<AbstractCampfireOption> options) {
            for (AbstractCampfireOption option : options)
                if (option instanceof TokeOption)
                    return SpireReturn.Return(null);

            return SpireReturn.Continue();
        }
    }

    // Girya
    @SpirePatch(clz = CampfireLiftEffect.class, method="update")
    public static class GiryaStacking {
        public static void Postfix(CampfireLiftEffect __instance) {
            int equalizer = 0;
            for (AbstractRelic r : AbstractDungeon.player.relics) {
                if (r.relicId.equals("Girya")) {
                    if (r.counter > 3) {
                        r.counter = 3;
                        equalizer++;
                    } else if (equalizer > 0 && r.counter <3) {
                        r.counter++;
                        equalizer--;
                    }
                }
            }
        }
    }

    @SpirePatch(clz = Girya.class, method="addCampfireOption")
    public static class GiryaNoDoubleOptions {
        public static SpireReturn Prefix(Girya __instance, ArrayList<AbstractCampfireOption> options) {
            for (AbstractCampfireOption option : options) {
                if (option instanceof LiftOption) {
                    if (__instance.counter < 3)
                        option.usable = true;
                    return SpireReturn.Return(null);
                }
            }

            return SpireReturn.Continue();
        }
    }

    // White Beast Statue
    @SpirePatch(clz = AbstractRoom.class, method="addPotionToRewards", paramtypez={})
    public static class WhiteBeastStacking {
        public static void Postfix(AbstractRoom __instance) {
            for (int i = 0; i < relicCount("White Beast Statue", true); i++)
                __instance.rewards.add(new RewardItem(AbstractDungeon.returnRandomPotion()));
        }
    }


    // Wing Boots 
    @SpirePatch(clz = MapRoomNode.class, method="update")
    public static class BlueLadderWingBootAdjustments {
        @SpireInsertPatch(rloc = 293-219)
        public static void Insert(MapRoomNode __instance) {
            
            AbstractDungeon.player.getRelic("WingedGreaves").counter++;

            for (AbstractRelic r : AbstractDungeon.player.relics) {
                if (r.relicId.equals("WingedGreaves")) {
                    if (r.counter > 0) { 
                        r.counter--;
                        return;
                    }
                }
            }
        }
    }

    // Lizard Tail
    @SpirePatch(clz = AbstractPlayer.class, method="damage")
    public static class LizardTailStacking {
        @SpireInsertPatch(rloc = 1866-1725)
        public static SpireReturn Insert(AbstractPlayer __instance, DamageInfo info) {

            for (AbstractRelic r : AbstractDungeon.player.relics) {
                if (r.relicId.equals("Lizard Tail")) {
                    if (r.counter == -1) {
                        __instance.currentHealth = 0;
                        r.onTrigger();
                        return SpireReturn.Return(null);
                    }
                }
            }
            
            return SpireReturn.Continue();
        }
    }

    // Regal Pillow
    @SpirePatch(clz = CampfireSleepEffect.class, method=SpirePatch.CONSTRUCTOR)
    public static class RegalPillowStacking {
        public static void Postfix(CampfireSleepEffect __instance, @ByRef int[] ___healAmount) {
            for (int i = 0; i < relicCount("Regal Pillow", true); i++)
                ___healAmount[0] += 15;
        }
    }

}























