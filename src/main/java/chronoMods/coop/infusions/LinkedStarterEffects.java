package chronoMods.coop.infusions;

import com.evacipated.cardcrawl.modthespire.lib.*;
import basemod.*;

import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.core.*;

import com.megacrit.cardcrawl.actions.*;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.unique.*;
import com.megacrit.cardcrawl.actions.defect.*;
import com.megacrit.cardcrawl.actions.watcher.*;
import com.megacrit.cardcrawl.actions.utility.*;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.powers.*;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.orbs.*;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;

import chronoMods.*;
import chronoMods.coop.*;
import chronoMods.coop.infusions.*;

import java.util.*;

public class LinkedStarterEffects
{
    public static final String[] INFUSE = CardCrawlGame.languagePack.getUIString("CardInfusions").TEXT;

    public static void infuseStarter(AbstractPlayer youPlayer, AbstractPlayer otherPlayer) {
        InfusionSet iSet = InfusionHelper.getInfusionSet(otherPlayer.chosenClass);
        TogetherManager.log("Starter Infusion: " + iSet.name);

        for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
            if (c.rarity == AbstractCard.CardRarity.BASIC && !(c.hasTag(AbstractCard.CardTags.STARTER_DEFEND) || c.hasTag(AbstractCard.CardTags.STARTER_STRIKE))) {
                TogetherManager.log("Found a starter card: " + c.name);
                Infusion i = iSet.getUnshuffledValidInfusion(c);
                if (i != null){
                    TogetherManager.log("Applying " + i.description);
                    i.ApplyInfusion(c);
                }
            }
        }
    }
}