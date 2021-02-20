package chronoMods.coop.relics;

import com.evacipated.cardcrawl.modthespire.lib.*;

import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;

import com.megacrit.cardcrawl.localization.*;
import com.megacrit.cardcrawl.core.*;
import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.blights.*;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.helpers.*;
import com.megacrit.cardcrawl.ui.panels.TopPanel;
import com.megacrit.cardcrawl.potions.*;
import com.megacrit.cardcrawl.relics.*;

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

public class SiphonPump extends AbstractBlight {
    public static final String ID = "SiphonPump";
    public static int potSlot = -1;
    public static String potName = "";

    public int maxPotionSlots = 6;

  private static final BlightStrings blightStrings = CardCrawlGame.languagePack.getBlightString(ID);
  public static final String NAME = blightStrings.NAME;
  public static final String[] DESCRIPTIONS = blightStrings.DESCRIPTION;


    @SpirePatch(clz = TopPanel.class, method="destroyPotion")
    public static class SharedPotionRemove {
        public static void Postfix(TopPanel __instance, int slot) {
            if (TogetherManager.gameMode != TogetherManager.mode.Coop) { return; }
            if (AbstractDungeon.player.hasBlight("SiphonPump")) {
                SiphonPump.potSlot = slot;
                NetworkHelper.sendData(NetworkHelper.dataType.UsePotion);
            }
        }
    }

    @SpirePatch(clz = AbstractPotion.class, method="setAsObtained")
    public static class SharedPotionAdd {
        public static void Postfix(AbstractPotion __instance, int potionSlot) {
            if (TogetherManager.gameMode != TogetherManager.mode.Coop) { return; }

            if (AbstractDungeon.player.hasBlight("SiphonPump")) {
                SiphonPump.potSlot = potionSlot;
                SiphonPump.potName = __instance.ID;
                NetworkHelper.sendData(NetworkHelper.dataType.SendPotion);
            }
        }
    }

    @SpirePatch(clz = PotionBelt.class, method="onEquip")
    public static class PotionBeltPostAcquire {
        public static SpireReturn Prefix(PotionBelt __instance) {
            if (!AbstractDungeon.player.hasBlight("SiphonPump")) { return SpireReturn.Continue(); }

            NetworkHelper.sendData(NetworkHelper.dataType.AddPotionSlot);
            return SpireReturn.Return(null);
        }
    }

    public SiphonPump() {
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
        this.description = this.DESCRIPTIONS[0] + getMergedPotionSlotCount() + this.DESCRIPTIONS[1];
    }

    public int getMergedPotionSlotCount() {
        int potionSlotBaseline = 3;
        int potionSlotTotal = 0;
        int potionBelts = 0;
        int potionPenalties = 0;

        if (AbstractDungeon.ascensionLevel > 10) 
            potionSlotBaseline = 2;

        for (RemotePlayer player : TogetherManager.players) {
            potionSlotTotal += player.potionSlots;

            if (player.potionSlots > potionSlotBaseline)
                potionBelts++;

            if (player.potionSlots < potionSlotBaseline)
                potionPenalties++;
        }

        return 3 - potionPenalties + potionBelts + potionSlotBaseline;
    }
    
    @Override
    public void onEquip() {
        // Create the Slots
        AbstractDungeon.player.potionSlots = getMergedPotionSlotCount();
        AbstractDungeon.player.potions.clear();

        // Grab all the existing potions from everyone
        ArrayList<AbstractPotion> potionList = new ArrayList();
        for (RemotePlayer player : TogetherManager.players) {
            for (String pid : player.potions) {
                AbstractPotion newPot = PotionHelper.getPotion(pid);
                if (newPot != null) {
                    potionList.add(newPot);
                    TogetherManager.logger.info("Added potion '" + pid + "'");
                } else {
                    TogetherManager.logger.info("Didn't add potion '" + pid + "'");
                }
            }
        }

        // Fill up as much as we can, then the rest with empty slots
        while (AbstractDungeon.player.potions.size() < AbstractDungeon.player.potionSlots) {
            if (potionList.size() > 0) {
                AbstractPotion p = potionList.remove(0);
                p.isObtained = true;

                p.slot = AbstractDungeon.player.potions.size();
                p.adjustPosition(AbstractDungeon.player.potions.size());

                AbstractDungeon.player.potions.add(p);
                p.flash();
                AbstractPotion.playPotionSound();
            }
            else
                AbstractDungeon.player.potions.add(new PotionSlot(AbstractDungeon.player.potions.size()-1));
        }

        // Readjust layout
        int index = 0;
        for (AbstractPotion tmpPotion : AbstractDungeon.player.potions) {
            tmpPotion.adjustPosition(index);
            index++;
        } 
    }
}