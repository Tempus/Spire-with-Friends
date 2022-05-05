package chronoMods.coop.relics;

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

public class BluntScissors extends AbstractBlight {
    public static final String ID = "BluntScissors";
    private static final BlightStrings blightStrings = CardCrawlGame.languagePack.getBlightString(ID);
    public static final String NAME = blightStrings.NAME;
    public static final String[] DESCRIPTIONS = blightStrings.DESCRIPTION;

    // Skip button should say 'send' instead
    // Clicking it adds an effect to the draft screen and changes the text to 'Don't Send'
    // Clicking it again removes the effect and swaps the text back.
    // When in 'Send' mode, clicking on a card will send a network message and add the card to a list
    // When entering a card draft, the top card in that list will be chosen to randomly duct tape merge with one of the draft cards.
    //      As a collorary rule to this, perhaps limit powers to only merge with powers, possibly exhaust to only merge with exhaust?

    // Happens when 'Skip Card' is clicked.
    @SpirePatch(clz = SkipCardButton.class, method="update")
    public static class SendCardMerge {
        @SpireInsertPatch(rloc = 67-49)
        public static SpireReturn Insert(SkipCardButton __instance) {
            
            if (AbstractDungeon.player.hasBlight("BluntScissors") && AbstractDungeon.cardRewardScreen.rItem != null) {
                BluntScissors.skipCardToggle = !BluntScissors.skipCardToggle;
                return SpireReturn.Return(null);
            }

            return SpireReturn.Continue();
        }
    }

    // Patches to render "Send/Keep" instead of 'Skip'
    @SpirePatch(clz = SkipCardButton.class, method="render")
    public static class SendCardDraw {
        @SpireInsertPatch(rloc = 119-114)
        public static SpireReturn Insert(SkipCardButton __instance, SpriteBatch sb) {
            if (AbstractDungeon.player.hasBlight("BluntScissors") && AbstractDungeon.cardRewardScreen.rItem != null) {
                float x = (float)ReflectionHacks.getPrivate(__instance, SkipCardButton.class, "current_x");
                Color c = (Color)ReflectionHacks.getPrivate(__instance, SkipCardButton.class, "textColor");
                String action = BluntScissors.DESCRIPTIONS[2];
                if (skipCardToggle) { action = BluntScissors.DESCRIPTIONS[3]; }

                if (FontHelper.getSmartWidth(FontHelper.buttonLabelFont, action, 9999.0F, 0.0F) > 200.0F * Settings.scale) {
                    FontHelper.renderFontCentered(sb, FontHelper.buttonLabelFont, action, x, SkipCardButton.TAKE_Y, c, 0.8F);
                } else {
                    FontHelper.renderFontCentered(sb, FontHelper.buttonLabelFont, action, x, SkipCardButton.TAKE_Y, c);
                }
                if (skipCardToggle) 
                    FontHelper.renderFontCentered(sb, FontHelper.buttonLabelFont, BluntScissors.DESCRIPTIONS[4], Settings.WIDTH/2f, SkipCardButton.TAKE_Y + 64f*Settings.scale, c);
                return SpireReturn.Return(null);
            }

            return SpireReturn.Continue();
        }
    }

    // Patches to modify 
    @SpirePatch(clz = CardRewardScreen.class, method="acquireCard")
    public static class AcquireCardOverride {
        public static SpireReturn Prefix(CardRewardScreen __instance, AbstractCard hoveredCard) {
            if (AbstractDungeon.player.hasBlight("BluntScissors") && __instance.rItem != null) {
                if (skipCardToggle) {
                    InputHelper.justClickedLeft = false;
                    // Show card and add to Relic
                    // AbstractDungeon.effectsQueue.add(new FastCardObtainEffect(hoveredCard, hoveredCard.current_x, hoveredCard.current_y));
                    BluntScissors.cardSent = hoveredCard;
                    if (hoveredCard.cardID.equals("MergeCard"))
                        BluntScissors.cardSent = ((DuctTapeCard)hoveredCard).cards.get(0);

                    NetworkHelper.sendData(NetworkHelper.dataType.BluntScissorCard);

                    return SpireReturn.Return(null);
                }
            }

            return SpireReturn.Continue();
        }
    }

    // Add the card into the card draft
    @SpirePatch(clz = CardRewardScreen.class, method="open")
    public static class CardsBecomeAsOne {
        public static void Postfix(CardRewardScreen __instance, ArrayList<AbstractCard> cards, RewardItem rItem, String header) {
            if (AbstractDungeon.player.hasBlight("BluntScissors")) {

                AbstractCard validCard = FindValidMergeCard(__instance.rewardGroup);
                BluntScissors bs = (BluntScissors)AbstractDungeon.player.getBlight("BluntScissors");
                
                BluntScissors.skipCardToggle = false;

                while (validCard != null && bs.cardsToMerge.size() > 0) {

                    ArrayList<AbstractCard> tmp = new ArrayList();
                    tmp.add(validCard);
                    tmp.add(bs.cardsToMerge.get(0));
                    bs.cardsToMerge.remove(0);

                    __instance.rewardGroup.set(__instance.rewardGroup.indexOf(validCard), new DuctTapeCard(tmp));
                    
                    // Check if there's more
                    validCard = FindValidMergeCard(__instance.rewardGroup);
                }

                Collections.shuffle(__instance.rewardGroup, new java.util.Random());
            }
        }
    }

    public static AbstractCard FindValidMergeCard(ArrayList<AbstractCard> cardList) {
        for (AbstractCard c : cardList) {
            if (c.cardID != "MergeCard" && Infusion.infusionField.infusion.get(c) == null)
                return c;
        }
        return null;
    }

    public static AbstractCard cardSent;
    public ArrayList<AbstractCard> cardsToMerge = new ArrayList();
    public static boolean skipCardToggle = false;

    public BluntScissors() {
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
        if (this.cardsToMerge.size() > 0) {
            this.description += this.DESCRIPTIONS[1];
            for (AbstractCard c : cardsToMerge) {
                this.description += c.name + " NL ";
            }
        }
    }

    @Override
    public void renderTip(SpriteBatch sb) {
        updateDescription();
        this.tips.clear();
        this.tips.add(new PowerTip(name, description));

        super.renderTip(sb);
    }

    @Override
    public void onEquip() {
        if (isObtained) { return; }
        
        cardsToMerge.clear();
        updateDescription();
    }
}