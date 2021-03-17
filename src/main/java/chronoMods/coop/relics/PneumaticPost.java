package chronoMods.coop.relics;

import com.evacipated.cardcrawl.modthespire.lib.*;

import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;

import com.megacrit.cardcrawl.localization.*;
import com.megacrit.cardcrawl.core.*;
import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.blights.*;
import com.megacrit.cardcrawl.helpers.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.city.*;
import com.megacrit.cardcrawl.events.shrines.*;
import com.megacrit.cardcrawl.rewards.*;
import com.megacrit.cardcrawl.shop.*;

import basemod.*;
import basemod.abstracts.*;
import basemod.interfaces.*;

import java.util.*;

import chronoMods.*;
import chronoMods.steam.*;
import chronoMods.coop.*;
import chronoMods.ui.deathScreen.*;
import chronoMods.ui.hud.*;
import chronoMods.ui.lobby.*;
import chronoMods.ui.mainMenu.*;


import com.megacrit.cardcrawl.actions.utility.*;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.unique.*;
import com.megacrit.cardcrawl.vfx.cardManip.*;
import com.megacrit.cardcrawl.vfx.*;

public class PneumaticPost extends AbstractBlight {
    public static final String ID = "PneumaticPost";
    private static final BlightStrings blightStrings = CardCrawlGame.languagePack.getBlightString(ID);
    public static final String NAME = blightStrings.NAME;
    public static final String[] DESCRIPTIONS = blightStrings.DESCRIPTION;

    public PneumaticPost() {
        super(ID, NAME, "", "spear.png", true);
        this.blightID = ID;
        this.name = NAME;
        updateDescription();
        this.unique = true;
        this.img = ImageMaster.loadImage("chrono/images/blights/" + ID + ".png");
        this.outlineImg = ImageMaster.loadImage("chrono/images/blights/outline/" + ID + ".png");
        this.increment = 0;
        this.tips.clear();
        this.tips.add(new PowerTip(name, description));
    }

    @Override
    public void updateDescription() {
        this.description = this.DESCRIPTIONS[0];
    }

    public static void pneumaticUpgrade(AbstractCard c) {
        if (com.megacrit.cardcrawl.dungeons.AbstractDungeon.player.hasBlight("PneumaticPost") && c.color != com.megacrit.cardcrawl.dungeons.AbstractDungeon.player.getCardColor() && c.canUpgrade() && c.type != AbstractCard.CardType.CURSE && c.type != AbstractCard.CardType.STATUS && c.rarity != AbstractCard.CardRarity.SPECIAL)
            c.upgrade();
    }


    // To make this work, we need to patch all the hardcoded locations of MasterOfRealityPower, as well as onPreviewObtain and onObtainCard
    // As a side note... what the fuck guys

    // Patches for actions
    @SpirePatch(clz = MakeTempCardAtBottomOfDeckAction.class, method="update")
    public static class ppMakeTempCardAtBottomOfDeckAction {
        @SpireInsertPatch(rloc=33-24, localvars={"c"})
        public static void Insert(MakeTempCardAtBottomOfDeckAction __instance, AbstractCard c) {
            PneumaticPost.pneumaticUpgrade(c);
        }
    }

    @SpirePatch(clz = MakeTempCardInDiscardAction.class, method=SpirePatch.CONSTRUCTOR, paramtypez={AbstractCard.class, boolean.class})
    public static class ppMakeTempCardInDiscardAction {
        public static void Prefix(MakeTempCardInDiscardAction __instance, AbstractCard card, boolean sameUUID) {
            PneumaticPost.pneumaticUpgrade(card);
        }
    }

    @SpirePatch(clz = MakeTempCardInDrawPileAction.class, method="update")
    public static class ppMakeTempCardInDrawPileAction {
        @SpireInsertPatch(rloc=68-56, localvars={"c"})
        public static void Insert(MakeTempCardInDrawPileAction __instance, AbstractCard c) {
            PneumaticPost.pneumaticUpgrade(c);
        }
    }

    @SpirePatch(clz = MakeTempCardInDrawPileAction.class, method="update")
    public static class ppMakeTempCardInDrawPileActionb {
        @SpireInsertPatch(rloc=79-56, localvars={"c"})
        public static void Insert(MakeTempCardInDrawPileAction __instance, AbstractCard c) {
            PneumaticPost.pneumaticUpgrade(c);
        }
    }

    @SpirePatch(clz = MakeTempCardInHandAction.class, method=SpirePatch.CONSTRUCTOR, paramtypez={AbstractCard.class, boolean.class})
    public static class ppMakeTempCardInHandAction {
        public static void Postfix(MakeTempCardInHandAction __instance, AbstractCard card, boolean isOtherCardInCenter) {
            PneumaticPost.pneumaticUpgrade(card);
        }
    }

    @SpirePatch(clz = MakeTempCardInHandAction.class, method=SpirePatch.CONSTRUCTOR, paramtypez={AbstractCard.class, int.class})
    public static class ppMakeTempCardInHandActionb {
        public static void Postfix(MakeTempCardInHandAction __instance, AbstractCard card, int amount) {
            PneumaticPost.pneumaticUpgrade(card);
        }
    }

    // Patches for other actions
    @SpirePatch(clz = DiscoveryAction.class, method="update")
    public static class ppDiscoveryAction {
        @SpireInsertPatch(rloc=67-42, localvars={"disCard", "disCard2"})
        public static void Insert(DiscoveryAction __instance, AbstractCard disCard, AbstractCard disCard2) {
            PneumaticPost.pneumaticUpgrade(disCard);
            PneumaticPost.pneumaticUpgrade(disCard2);
        }
    }

    @SpirePatch(clz = ChooseOneColorless.class, method="update")
    public static class ppChooseOneColorless {
        @SpireInsertPatch(rloc=39-25, localvars={"disCard"})
        public static void Insert(ChooseOneColorless __instance, AbstractCard disCard) {
            PneumaticPost.pneumaticUpgrade(disCard);
        }
    }

    // Patches for vfx logic, because logic belongs in vfx
    @SpirePatch(clz = ShowCardAndAddToDiscardEffect.class, method=SpirePatch.CONSTRUCTOR, paramtypez={AbstractCard.class, float.class, float.class})
    public static class ppShowCardAndAddToDiscardEffect {
        public static void Prefix(ShowCardAndAddToDiscardEffect __instance, AbstractCard srcCard, float x, float y) {
            PneumaticPost.pneumaticUpgrade(srcCard);
        }
    }

    @SpirePatch(clz = ShowCardAndAddToDiscardEffect.class, method=SpirePatch.CONSTRUCTOR, paramtypez={AbstractCard.class})
    public static class ppShowCardAndAddToDiscardEffectb {
        public static void Prefix(ShowCardAndAddToDiscardEffect __instance, AbstractCard card) {
            PneumaticPost.pneumaticUpgrade(card);
        }
    }

    @SpirePatch(clz = ShowCardAndAddToDrawPileEffect.class, method=SpirePatch.CONSTRUCTOR, paramtypez={AbstractCard.class, float.class, float.class, boolean.class, boolean.class, boolean.class})
    public static class ppShowCardAndAddToDrawPileEffect {
        public static void Prefix(ShowCardAndAddToDrawPileEffect __instance, AbstractCard srcCard, float x, float y, boolean randomSpot, boolean cardOffset, boolean toBottom) {
            PneumaticPost.pneumaticUpgrade(srcCard);
        }
    }

    @SpirePatch(clz = ShowCardAndAddToHandEffect.class, method=SpirePatch.CONSTRUCTOR, paramtypez={AbstractCard.class, float.class, float.class})
    public static class ppShowCardAndAddToHandEffect {
        public static void Prefix(ShowCardAndAddToHandEffect __instance, AbstractCard card, float offsetX, float offsetY) {
            PneumaticPost.pneumaticUpgrade(card);
        }
    }

    @SpirePatch(clz = ShowCardAndAddToHandEffect.class, method=SpirePatch.CONSTRUCTOR, paramtypez={AbstractCard.class})
    public static class ppShowCardAndAddToHandEffectb {
        public static void Prefix(ShowCardAndAddToHandEffect __instance, AbstractCard card) {
            PneumaticPost.pneumaticUpgrade(card);
        }
    }

    // And lastly, the card draft options to affect shops, prismatic, and colourless rewards. Which, because we are not a relic, we patch manually as well
    @SpirePatch(clz = AbstractDungeon.class, method="getRewardCards")
    public static class ppAbstractDungeon {
        @SpireInsertPatch(rloc=1860-1792, localvars={"c"})
        public static void Insert(AbstractCard c) {
            PneumaticPost.pneumaticUpgrade(c);
        }
    }

    @SpirePatch(clz = com.megacrit.cardcrawl.events.city.TheLibrary.class, method="buttonEffect")
    public static class ppTheLibrary {
        @SpireInsertPatch(rloc=94-64, localvars={"card"})
        public static void Insert(com.megacrit.cardcrawl.events.city.TheLibrary __instance, int buttonPressed, AbstractCard card) {
            PneumaticPost.pneumaticUpgrade(card);
        }
    }

    @SpirePatch(clz = GremlinMatchGame.class, method="initializeCards")
    public static class ppGremlinMatchGame {
        @SpireInsertPatch(rloc=79-59, localvars={"c"})
        public static void Insert(GremlinMatchGame __instance, AbstractCard c) {
            PneumaticPost.pneumaticUpgrade(c);
        }
    }

    @SpirePatch(clz = RewardItem.class, method=SpirePatch.CONSTRUCTOR, paramtypez={AbstractCard.CardColor.class})
    public static class ppRewardItem {
        @SpireInsertPatch(rloc=175-166)
        public static void Insert(RewardItem __instance, AbstractCard.CardColor colorType) {
            for (AbstractCard c : __instance.cards)
                PneumaticPost.pneumaticUpgrade(c);
        }
    }

    @SpirePatch(clz = ShopScreen.class, method="initCards")
    public static class ppShopScreen {
        @SpireInsertPatch(rloc=265-250, localvars={"c"})
        public static void Insert(ShopScreen __instance, AbstractCard c) {
            PneumaticPost.pneumaticUpgrade(c);
        }
    }

    @SpirePatch(clz = ShopScreen.class, method="initCards")
    public static class ppShopScreenb {
        @SpireInsertPatch(rloc=285-250, localvars={"c"})
        public static void Insert(ShopScreen __instance, AbstractCard c) {
            PneumaticPost.pneumaticUpgrade(c);
        }
    }

    @SpirePatch(clz = ShopScreen.class, method="purchaseCard")
    public static class ppShopScreenc {
        @SpireInsertPatch(rloc=719-686, localvars={"c"})
        public static void Insert(ShopScreen __instance, AbstractCard hoveredCard, AbstractCard c) {
            PneumaticPost.pneumaticUpgrade(c);
        }
    }

    @SpirePatch(clz = ShopScreen.class, method="purchaseCard")
    public static class ppShopScreend {
        @SpireInsertPatch(rloc=700-686, localvars={"c"})
        public static void Insert(ShopScreen __instance, AbstractCard hoveredCard, AbstractCard c) {
            PneumaticPost.pneumaticUpgrade(c);
        }
    }

    @SpirePatch(clz = StoreRelic.class, method="purchaseRelic")
    public static class ppStoreRelic {
        @SpireInsertPatch(rloc=112-96, localvars={"c"})
        public static void Insert(StoreRelic __instance, AbstractCard c) {
            PneumaticPost.pneumaticUpgrade(c);
        }
    }

    @SpirePatch(clz = StoreRelic.class, method="purchaseRelic")
    public static class ppStoreRelicb {
        @SpireInsertPatch(rloc=115-96, localvars={"c"})
        public static void Insert(StoreRelic __instance, AbstractCard c) {
            PneumaticPost.pneumaticUpgrade(c);
        }
    }

    // Those were just for previewing cards, the following patches are for obtaining cards you don't preview
    @SpirePatch(clz = NoteForYourself.class, method="buttonEffect")
    public static class ppNoteForYourself {
        @SpireInsertPatch(rloc=55-40)
        public static void Insert(NoteForYourself __instance, int buttonPressed) {
            PneumaticPost.pneumaticUpgrade((AbstractCard)ReflectionHacks.getPrivate(__instance, NoteForYourself.class, "obtainCard"));
        }
    }

    @SpirePatch(clz = ShowCardAndObtainEffect.class, method="update")
    public static class ppShowCardAndObtainEffect {
        @SpireInsertPatch(rloc=100-94)
        public static void Insert(ShowCardAndObtainEffect __instance) {
            PneumaticPost.pneumaticUpgrade((AbstractCard)ReflectionHacks.getPrivate(__instance, ShowCardAndObtainEffect.class, "card"));
        }
    }

    @SpirePatch(clz = FastCardObtainEffect.class, method="update")
    public static class ppFastCardObtainEffect {
        @SpireInsertPatch(rloc=52-42)
        public static void Insert(FastCardObtainEffect __instance) {
            PneumaticPost.pneumaticUpgrade((AbstractCard)ReflectionHacks.getPrivate(__instance, FastCardObtainEffect.class, "card"));
        }
    }
}