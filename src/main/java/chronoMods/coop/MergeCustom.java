package chronoMods.coop;

import chronoMods.TogetherManager;
import chronoMods.coop.hubris.DuctTapeCard;
import chronoMods.coop.infusions.Infusion;
import chronoMods.network.RemotePlayer;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.util.ArrayList;
import java.util.ListIterator;

public class MergeCustom {

    public static boolean isActive = false;

    // Add the card into the card draft
    @SpirePatch(clz = AbstractDungeon.class, method="getRewardCards")
    public static class CardsBecomeAsOne {
        public static ArrayList<AbstractCard> Postfix(ArrayList<AbstractCard> __result) {
            if (MergeCustom.isActive) {

                // Make a weighted pool of all character cards to choose from
                ArrayList<AbstractCard> masterCardList = new ArrayList<>();

                for (RemotePlayer p : TogetherManager.players) {
                    p.character.getCardPool(masterCardList);
                }

                CardGroup g = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
                g.group = masterCardList;

                for (ListIterator<AbstractCard> i = __result.listIterator(); i.hasNext(); ) {
                    AbstractCard c = i.next();

                    ArrayList<AbstractCard> tmp = new ArrayList();

                    if (c.cardID == "MergeCard" || Infusion.infusionField.infusion.get(c) != null)
                        continue;

                    // 33% chance of this card getting Merged
                    if (AbstractDungeon.cardRng.random(0, 3) > 0) { continue; }

                    // Pull from the proper rarity...?
                    tmp.add(g.getRandomCard(true, AbstractDungeon.rollRarity()));                    
                    tmp.add(c);

                    // Replace the card reward with the merged one
                    i.set(((AbstractCard)new DuctTapeCard(tmp)));
                }
            }
            return __result;

        }
    }

}