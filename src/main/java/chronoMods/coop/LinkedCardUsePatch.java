package chronoMods.coop;

import com.evacipated.cardcrawl.modthespire.lib.*;
import basemod.*;

import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.dungeons.*;

import chronoMods.*;
import chronoMods.coop.*;

import java.util.*;

public class LinkedCardUsePatch
{
    public static Map<String, Runnable> modifiedCardActions = new HashMap<>();
    public static Map<String, String> modifiedCardDescriptions = new HashMap<>();
    public static AbstractMonster lastMonster;

    // Runables should accept AbstractCard c, AbstractMonster monster, int energyOnUse as args.
    public static void modifyCard(String cardID, Runnable use, String description) {
        LinkedCardUsePatch.modifiedCardActions.put(cardID, use);
        LinkedCardUsePatch.modifiedCardDescriptions.put(cardID, description);
    }
 
    @SpirePatch(clz = AbstractPlayer.class, method="useCard")
    public static class LinkedCardUse {
        @SpireInsertPatch(rloc=1700-1681)
        public static void Insert(AbstractPlayer __instance, AbstractCard c, AbstractMonster monster, int energyOnUse) {
            if (LinkedCardUsePatch.modifiedCardActions.containsKey(c.cardID)) {
                TogetherManager.log("Activate run effects for " + c.cardID);
                lastMonster = monster;
                TogetherManager.log("Runnable: " + LinkedCardUsePatch.modifiedCardActions.get(c.cardID));
                TogetherManager.log("Target is " + monster);
                LinkedCardUsePatch.modifiedCardActions.get(c.cardID).run();
            }
        }
    }

    public static void UpdateDescriptions(AbstractCard card) {
        LinkedCardUsePatch.UpdateDescriptions(card, false);
    }

    public static void UpdateDescriptions(AbstractCard card, boolean retains) {
        for (AbstractCard c : AbstractDungeon.player.masterDeck.group){
            if (c.cardID.equals(card.cardID)) {
                if (LinkedCardUsePatch.modifiedCardDescriptions.containsKey(c.cardID)) {
                    c.rawDescription += LinkedCardUsePatch.modifiedCardDescriptions.get(c.cardID);
                    c.initializeDescription();
                    if (retains) { c.selfRetain = true; };
                }
            }
        }

        for (AbstractCard c : AbstractDungeon.commonCardPool.group) {
            if (c.cardID.equals(card.cardID)) {
                if (LinkedCardUsePatch.modifiedCardDescriptions.containsKey(c.cardID)) {
                    c.rawDescription += LinkedCardUsePatch.modifiedCardDescriptions.get(c.cardID);
                    c.initializeDescription();
                    if (retains) { c.selfRetain = true; };
                }
            }
        }

        for (AbstractCard c : AbstractDungeon.uncommonCardPool.group) {
            if (c.cardID.equals(card.cardID)) {
                if (LinkedCardUsePatch.modifiedCardDescriptions.containsKey(c.cardID)) {
                    c.rawDescription += LinkedCardUsePatch.modifiedCardDescriptions.get(c.cardID);
                    c.initializeDescription();
                    if (retains) { c.selfRetain = true; };
                }
            }
        }

        for (AbstractCard c : AbstractDungeon.rareCardPool.group) {
            if (c.cardID.equals(card.cardID)) {
                if (LinkedCardUsePatch.modifiedCardDescriptions.containsKey(c.cardID)) {
                    c.rawDescription += LinkedCardUsePatch.modifiedCardDescriptions.get(c.cardID);
                    c.initializeDescription();
                    if (retains) { c.selfRetain = true; };
                }
            }
        }
    }

    @SpirePatch(clz = AbstractCard.class, method="createCardImage")
    public static class LinkedCardDescriptionCopying {
        public static void Postfix(AbstractCard __instance) {
            if (LinkedCardUsePatch.modifiedCardDescriptions.containsKey(__instance.cardID)) {
                __instance.rawDescription += LinkedCardUsePatch.modifiedCardDescriptions.get(__instance.cardID);
            }
        }
    }
}