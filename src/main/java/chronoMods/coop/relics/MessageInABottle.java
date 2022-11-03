package chronoMods.coop.relics;

import chronoMods.TogetherManager;
import chronoMods.network.NetworkHelper;
import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.blights.AbstractBlight;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.localization.BlightStrings;
import com.megacrit.cardcrawl.localization.LocalizedStrings;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;

import java.util.ArrayList;

public class MessageInABottle extends AbstractBlight {
    public static final String ID = "MessageInABottle";
    private static final BlightStrings blightStrings = CardCrawlGame.languagePack.getBlightString(ID);
    public static final String NAME = blightStrings.NAME;
    public static final String[] DESCRIPTIONS = blightStrings.DESCRIPTION;

    public static CardGroup bottleCards = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
    private boolean cardSelected = true;
    public static AbstractCard sendCard;
    public AbstractCard card;

    public MessageInABottle() {
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
        bottleCards.clear();
    }

    @Override
    public void updateDescription() {
        this.description = this.DESCRIPTIONS[0];
    }

    @Override
    public void onEquip() {

        TogetherManager.log("Opening Bottle obtained: " + isObtained);
        if (isObtained) { return; }

        if (AbstractDungeon.player.masterDeck.getPurgeableCards().size() > 0) {
          this.cardSelected = false;
          AbstractDungeon.closeCurrentScreen();
          AbstractDungeon.closeCurrentScreen();
          AbstractDungeon.dynamicBanner.hide();
          AbstractDungeon.overlayMenu.cancelButton.hide();
          AbstractDungeon.screen = AbstractDungeon.CurrentScreen.NONE;
          AbstractDungeon.isScreenUp = false;

          // (AbstractDungeon.getCurrRoom()).phase = AbstractRoom.RoomPhase.INCOMPLETE;
          TogetherManager.log("Opening Bottle");
          AbstractDungeon.gridSelectScreen.open(AbstractDungeon.player.masterDeck
              .getPurgeableCards(), 1, this.DESCRIPTIONS[1] + this.name + LocalizedStrings.PERIOD, false, false, false, false);
        } 
    }

    public void setDescriptionAfterLoading() {
        this.description = this.DESCRIPTIONS[2];
        for (AbstractCard c : bottleCards.group)
            this.description += FontHelper.colorString(c.name, "y") + " NL ";
        this.tips.clear();
        this.tips.add(new PowerTip(this.name, this.description));
        initializeTips();
        this.counter = bottleCards.size();
    }

    public void atBattleStart() {
        flash();
    }

    @SpirePatch(clz = CardGroup.class, method="initializeDeck")
    public static class gwNoteForYourself {
        @SpireInsertPatch(rloc=1035-1029, localvars={"placeOnTop"})
        public static void Insert(CardGroup __instance, CardGroup masterDeck, @ByRef ArrayList<AbstractCard>[] placeOnTop) {
            for (AbstractCard c : bottleCards.group) {
                placeOnTop[0].add(c.makeStatEquivalentCopy());
            }
        }
    }

    public void update() {
        super.update();
        if (!this.cardSelected && 
            !AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {

            this.cardSelected = true;
            this.card = AbstractDungeon.gridSelectScreen.selectedCards.get(0);
            
            sendCard = this.card;
            NetworkHelper.sendData(NetworkHelper.dataType.SendCardMessageBottle);

            this.card.untip();
            this.card.unhover();
            AbstractDungeon.topLevelEffects.add(new PurgeCardEffect(this.card, Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
            AbstractDungeon.player.masterDeck.removeCard(this.card);

            AbstractDungeon.gridSelectScreen.selectedCards.clear();
            AbstractDungeon.gridSelectScreen.cancelUpgrade();

            setDescriptionAfterLoading();

            // Desperate attempts to not softlock
            // (AbstractDungeon.getCurrRoom()).phase = AbstractRoom.RoomPhase.COMPLETE;

            AbstractDungeon.overlayMenu.cancelButton.hide();
            AbstractDungeon.overlayMenu.hideBlackScreen();
            AbstractDungeon.isScreenUp = false;

            if ((AbstractDungeon.getCurrRoom()).phase == AbstractRoom.RoomPhase.COMBAT && !AbstractDungeon.player.isDead)
              AbstractDungeon.overlayMenu.showCombatPanels(); 

            AbstractDungeon.closeCurrentScreen();
            AbstractDungeon.overlayMenu.proceedButton.show();

        } 
    }
}