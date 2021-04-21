package chronoMods.coop;

import com.evacipated.cardcrawl.modthespire.lib.*;
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
import com.megacrit.cardcrawl.powers.*;
import com.megacrit.cardcrawl.ui.buttons.*;

import basemod.*;
import basemod.abstracts.*;
import basemod.interfaces.*;

import java.util.*;

import chronoMods.*;

public class CoopRelicStackingPatches {

    /*
    Non-stacking relics:
        Dream Catcher
        Juzu Bracelet
        Smiling Mask
        Blue Candle
        Molten Egg
        Frozen Egg
        Toxic Egg
        Courier
        Teardrop Locket
        Calipers
        Girya
        Peace Pipe
        Shovel
        Torii
        Turnip
        Ginger
        Unceasing Top
        Magic Flower
        Frozen Eye
        Medical Kit
        Membership Card
        Orange Pellets
        Prismatic Shard
        Strange Spoon
        N'loths Gift
        Odd Mushroom
        Wing Boots
    */

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
            boolean skipOnce = true;
          if (!__instance.isPlayer && (AbstractDungeon.getCurrRoom()).eliteTrigger)
            for (AbstractRelic r : AbstractDungeon.player.relics) {
                if (r.relicId == "PreservedInsect") {
                    if (skipOnce) {
                        skipOnce = false;
                        continue;
                    }

                    scale[0] += 0.3F; 
                }
            }
        }
    }

    // Boot
    @SpirePatch(clz = Boot.class, method="onAttackToChangeDamage")
    public static class BootStacking {
        public static int Postfix(int __result, Boot __instance, DamageInfo info, int damageAmount) {
            int Boots = 4;
            for (AbstractRelic r : AbstractDungeon.player.relics)
                if (r.relicId == "Boot")
                    Boots++;

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
            boolean skipOnce = true;
            RewardItem cardReward;
            for (AbstractRelic r : AbstractDungeon.player.relics) {
                if (r.relicId == "Prayer Wheel") {
                    if (skipOnce) {
                        skipOnce = false;
                        continue;
                    }

                    cardReward = new RewardItem();
                    if (cardReward.cards.size() > 0)
                      __instance.rewards.add(cardReward); 
                }
            }

        }
    }

    // Snecko Skull
    @SpirePatch(clz = ApplyPowerAction.class, method=SpirePatch.CONSTRUCTOR, paramtypez = {AbstractCreature.class, AbstractCreature.class, AbstractPower.class, int.class, boolean.class, AbstractGameAction.AttackEffect.class})
    public static class SneckoSkullStacking {
        @SpireInsertPatch(rloc=64-54)
        public static void Insert(ApplyPowerAction __instance, AbstractCreature target, AbstractCreature source, @ByRef AbstractPower[] powerToApply, @ByRef int[] stackAmount, boolean isFast, AbstractGameAction.AttackEffect effect) {
            if (AbstractDungeon.player.hasRelic("Snake Skull") && source != null && source.isPlayer && target != source && powerToApply[0].ID.equals("Poison")) {

                boolean skipOnce = true;
                for (AbstractRelic r : AbstractDungeon.player.relics) {
                    if (r.relicId == "Snake Skull") {
                        if (skipOnce) {
                            skipOnce = false;
                            continue;
                        }

                        powerToApply[0].amount++;
                        stackAmount[0]++;
                        r.flash();
                    }
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
                    if (r.relicId == "Champion Belt") {
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
            boolean skipOnce = true;
            for (AbstractRelic r : AbstractDungeon.player.relics) {
                if (r.relicId == "Singing Bowl") {
                    if (skipOnce) {
                        skipOnce = false;
                        continue;
                    }
                    r.flash();
                    AbstractDungeon.player.increaseMaxHp(2, true);
                }
            }   
        }
    }

    @SpirePatch(clz = SingingBowlButton.class, method="render")
    public static class SingingBowlTextChange {
        @SpireInsertPatch(rloc=114-109)
        public static SpireReturn Insert(SingingBowlButton __instance, SpriteBatch sb, float ___current_x, Color ___btnColor) {
            boolean skipOnce = true;
            int i = 0;
            for (AbstractRelic r : AbstractDungeon.player.relics) {
                if (r.relicId == "Singing Bowl") {
                    i++;
                    if (skipOnce) {
                        skipOnce = false;
                        continue;
                    }
                    r.flash();
                    AbstractDungeon.player.increaseMaxHp(2, true);
                }
            }   

            FontHelper.renderFontCentered(sb, FontHelper.buttonLabelFont, "+" + i + " Max HP", ___current_x, SkipCardButton.TAKE_Y, ___btnColor);

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
                if (r.relicId == "Paper Frog") {
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
                if (r.relicId == "Paper Crane") {
                    // r.flash();
                    mod -= 0.15f;
                }
            }
            if (mod < 0f) { mod = 0f; }
            return SpireReturn.Return(damage * mod);
        }
    }
}