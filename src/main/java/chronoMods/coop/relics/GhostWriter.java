package chronoMods.coop.relics;

import chronoMods.TogetherManager;
import chronoMods.network.NetworkHelper;
import chronoMods.network.RemotePlayer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDrawPileAction;
import com.megacrit.cardcrawl.blights.AbstractBlight;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.shrines.NoteForYourself;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.localization.BlightStrings;
import com.megacrit.cardcrawl.vfx.FastCardObtainEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

public class GhostWriter extends AbstractBlight {
    public static final String ID = "GhostWriter";
    public static AbstractCard sendCard;
    public static RemotePlayer sendPlayer;
    public static boolean sendUpdate = false;
    public static boolean sendRemove = false;

    public static CardGroup rareCards = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);

    private static final BlightStrings blightStrings = CardCrawlGame.languagePack.getBlightString(ID);
    public static final String NAME = blightStrings.NAME;
    public static final String[] DESCRIPTIONS = blightStrings.DESCRIPTION;

    public GhostWriter() {
        super(ID, NAME, "", "spear.png", true);
        this.blightID = ID;
        this.name = NAME;
        updateDescription();
        this.unique = true;
        this.img = ImageMaster.loadImage("chrono/images/blights/" + ID + ".png");
        this.outlineImg = ImageMaster.loadImage("chrono/images/blights/outline/" + ID + ".png");
        this.increment = 0;
        rareCards.group.clear();
        this.tips.clear();
        this.tips.add(new PowerTip(name, description));
    }

    @Override
    public void updateDescription() {
        this.description = this.DESCRIPTIONS[0];
        for (AbstractCard c : rareCards.group) {
            this.description += c.name;
            this.description += this.DESCRIPTIONS[1];
        }
        this.description = this.description.substring(0, this.description.length() - this.DESCRIPTIONS[1].length());
        this.description += this.DESCRIPTIONS[2];
    }

    public void renderTip(SpriteBatch sb) {
        updateDescription();
        this.tips.clear();
        this.tips.add(new PowerTip(name, description));

        super.renderTip(sb);
    }
    // public void onEquip() {
    //     for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
    //         if (c.rarity == AbstractCard.CardRarity.RARE) {
    //             GhostWriter.sendCard = c;
    //             NetworkHelper.sendData(NetworkHelper.dataType.SendCardGhost);
    //         }
    //     }
    // }

    public void atBattleStart() {
        rareCards.shuffle();
        if (rareCards.size() > 0)
            AbstractDungeon.actionManager.addToTop(new MakeTempCardInDrawPileAction(rareCards.getTopCard(),       1, true, false, false, Settings.WIDTH/5f*2f, Settings.HEIGHT/2f));
        if (rareCards.size() > 1)
            AbstractDungeon.actionManager.addToTop(new MakeTempCardInDrawPileAction(rareCards.getNCardFromTop(1), 1, true, false, false, Settings.WIDTH/5f*3f, Settings.HEIGHT/2f));
    }

    public static void Haunt(AbstractCard c, boolean update, boolean remove) {
        if (c.rarity == AbstractCard.CardRarity.RARE) {
            GhostWriter.sendCard = c;
            GhostWriter.sendUpdate = update;
            GhostWriter.sendRemove = remove;
            NetworkHelper.sendData(NetworkHelper.dataType.SendCardGhost);
        }
    }

    // Card Obtains
    @SpirePatch(clz = NoteForYourself.class, method="buttonEffect")
    public static class gwNoteForYourself {
        @SpireInsertPatch(rloc=55-40)
        public static void Insert(NoteForYourself __instance, int buttonPressed, AbstractCard ___obtainCard) {
            if (TogetherManager.gameMode != TogetherManager.mode.Coop) { return; }
            GhostWriter.Haunt(___obtainCard, false, false); 
        }
    }

    @SpirePatch(clz = ShowCardAndObtainEffect.class, method="update")
    public static class gwShowCardAndObtainEffect {
        @SpireInsertPatch(rloc=100-94)
        public static void Insert(ShowCardAndObtainEffect __instance, AbstractCard ___card) {
            if (TogetherManager.gameMode != TogetherManager.mode.Coop) { return; }
            GhostWriter.Haunt(___card, false, false); 
        }
    }

    @SpirePatch(clz = FastCardObtainEffect.class, method="update")
    public static class gwFastCardObtainEffect {
        @SpireInsertPatch(rloc=52-42)
        public static void Insert(FastCardObtainEffect __instance, AbstractCard ___card) {
            if (TogetherManager.gameMode != TogetherManager.mode.Coop) { return; }
            GhostWriter.Haunt(___card, false, false); 
        }
    }

    // Permanent Upgrades (Campfire, Relics, Lessons Learned, Event)
    @SpirePatch(clz = AbstractCard.class, method="upgradeName")
    public static class gwUpgrade {
        public static void Postfix(AbstractCard __instance) {
            if (!CardCrawlGame.isInARun()) { return; }
            if (TogetherManager.gameMode != TogetherManager.mode.Coop) { return; }
            if (AbstractDungeon.player.masterDeck.contains(__instance))
                GhostWriter.Haunt(__instance, true, false); 
        }
    }

    // @SpirePatch(clz = CampfireSmithEffect.class, method="update")
    // public static class gwCampfireSmithEffect {
    //     @SpireInsertPatch(rloc=58-45, localvars={"c"})
    //     public static void Insert(CampfireSmithEffect __instance) {
    //         GhostWriter.Haunt(c, true, false); 
    //     }
    // }

    // Card Removals
    @SpirePatch(clz = AbstractCard.class, method="onRemoveFromMasterDeck")
    public static class gwOnRemoveFromMasterDeck {
        public static void Prefix(AbstractCard __instance) {
            if (TogetherManager.gameMode != TogetherManager.mode.Coop) { return; }
            GhostWriter.Haunt(__instance, false, true); 
        }
    }
}