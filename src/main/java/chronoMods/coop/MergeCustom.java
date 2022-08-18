package chronoMods.coop;

import com.evacipated.cardcrawl.modthespire.lib.*;

import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;

import com.megacrit.cardcrawl.localization.*;
import com.megacrit.cardcrawl.core.*;
import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.blights.*;
import com.megacrit.cardcrawl.map.*;
import com.megacrit.cardcrawl.helpers.*;
import com.megacrit.cardcrawl.helpers.input.*;
import com.megacrit.cardcrawl.relics.*;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.rewards.*;
import com.megacrit.cardcrawl.screens.*;
import com.megacrit.cardcrawl.ui.buttons.*;

import basemod.*;
import basemod.abstracts.*;

import java.util.*;
import java.lang.Math;

import chronoMods.*;
import chronoMods.network.steam.*;
import chronoMods.network.*;
import chronoMods.coop.*;
import chronoMods.coop.infusions.*;
import chronoMods.ui.deathScreen.*;
import chronoMods.ui.hud.*;
import chronoMods.ui.lobby.*;
import chronoMods.ui.mainMenu.*;
import chronoMods.coop.hubris.*;

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