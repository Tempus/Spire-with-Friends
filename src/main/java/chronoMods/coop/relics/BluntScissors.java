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
import com.megacrit.cardcrawl.relics.*;
import com.megacrit.cardcrawl.dungeons.*;

import basemod.*;
import basemod.abstracts.*;

import java.util.*;

import chronoMods.*;
import chronoMods.network.steam.*;
import chronoMods.network.*;
import chronoMods.coop.*;
import chronoMods.ui.deathScreen.*;
import chronoMods.ui.hud.*;
import chronoMods.ui.lobby.*;
import chronoMods.ui.mainMenu.*;

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
            
            if (AbstractDungeon.player.hasBlight("BluntScissors"))
                if ((BluntScissors)(AbstractDungeon.player.getBlight("BluntScissors")).cardsToMerge.size() > 0)
                    (AbstractDungeon.player.getRelic("WingedGreaves")).counter++;
        }
    }

    public ArrayList<AbstratCard> cardsToMerge = new ArrayList();
    public boolean skipCardToggle = false;

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
        this.description = this.DESCRIPTIONS[0] 
        if (this.cardsToMerge.size() > 0) {
            this.description += this.DESCRIPTIONS[1];
            for (AbstractCard c : cardsToMerge) {
                this.description += c.name;
            }
        }
    }

    @Override
    public void onEquip() {
        cardsToMerge.clear();
        updateDescription();
    }
}