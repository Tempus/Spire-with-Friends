package chronoMods.coop.relics;

import com.evacipated.cardcrawl.modthespire.lib.*;

import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;

import com.megacrit.cardcrawl.localization.*;
import com.megacrit.cardcrawl.core.*;
import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.blights.*;
import com.megacrit.cardcrawl.rooms.*;
import com.megacrit.cardcrawl.helpers.*;

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

public class GhostWriter extends AbstractBlight {
    public static final String ID = "GhostWriter";
    public static AbstractCard sendCard;
    public static RemotePlayer sendPlayer;

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
        this.tips.add(new PowerTip(name, description));
    }

    @Override
    public void updateDescription() {
        this.description = this.DESCRIPTIONS[0];
        // Specify teammate?
    }

    @SpirePatch(clz = CardGroup.class, method="moveToExhaustPile")
    public static class onExhaust {
        public static void Postfix(CardGroup __instance, AbstractCard c) {
            if (TogetherManager.gameMode != TogetherManager.mode.Coop) { return; }
            if (AbstractDungeon.player.hasBlight("GhostWriter") && c.type != AbstractCard.CardType.CURSE && c.type != AbstractCard.CardType.STATUS) {
                // Remove from Exhaust Pile
                AbstractDungeon.player.exhaustPile.removeCard(c);
                // Remove from Master Deck
                boolean found = false;
                TogetherManager.logger.info("Looking for card to remove: " + c.uuid);
                for (int i = 0; i <  AbstractDungeon.player.masterDeck.group.size(); i++) {
                  if (AbstractDungeon.player.masterDeck.group.get(i).uuid.equals(c.uuid)) {
                    AbstractDungeon.player.masterDeck.removeCard(AbstractDungeon.player.masterDeck.group.get(i)); 
                    TogetherManager.logger.info("Card to remove, found!");    
                    found = true;                
                  }
                }
                
                // Send to other player. Next? Random?
                GhostWriter.sendCard = c;

                int index = TogetherManager.players.indexOf(TogetherManager.getCurrentUser());
                if (index + 1 == TogetherManager.players.size() - 1)
                    GhostWriter.sendPlayer = TogetherManager.players.get(index + 1);
                else
                    GhostWriter.sendPlayer = TogetherManager.players.get(0);

                NetworkHelper.sendData(NetworkHelper.dataType.SendCard);
            }
        }
    }
}