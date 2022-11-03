package chronoMods.coop.infusions;

import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class LinkedCardUsePatch
{
    public static AbstractMonster lastMonster;
    public static AbstractCard lastCard;

    @SpirePatch(clz = AbstractPlayer.class, method="useCard")
    public static class LinkedCardUse {
        @SpireInsertPatch(rloc=1700-1681)
        public static void Insert(AbstractPlayer __instance, AbstractCard c, AbstractMonster monster, int energyOnUse) {
            Infusion i = Infusion.infusionField.infusion.get(c);
            if (i != null) {
                if (i.actions != null) {
                    lastMonster = monster;
                    lastCard = c;
                    i.actions.run();
                }
            }
        }
    }

    // @SpirePatch(clz = AbstractCard.class, method="createCardImage")
    // public static class LinkedCardDescriptionCopying {
    //     public static void Postfix(AbstractCard __instance) {
    //         if (LinkedCardUsePatch.modifiedCardDescriptions.containsKey(__instance.cardID)) {
    //             __instance.rawDescription += LinkedCardUsePatch.modifiedCardDescriptions.get(__instance.cardID);
    //         }
    //     }
    // }
}