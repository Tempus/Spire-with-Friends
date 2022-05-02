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
import com.megacrit.cardcrawl.characters.AbstractPlayer;

import basemod.*;
import basemod.abstracts.*;
import basemod.interfaces.*;

import java.util.*;

import chronoMods.*;
import chronoMods.network.steam.*;
import chronoMods.network.*;
import chronoMods.coop.*;
import chronoMods.ui.deathScreen.*;
import chronoMods.ui.hud.*;
import chronoMods.ui.lobby.*;
import chronoMods.ui.mainMenu.*;

public class VaporFunnel extends AbstractBlight {
    public static final String ID = "VaporFunnel";
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
            if (AbstractDungeon.player.hasBlight("VaporFunnel")) {
                VaporFunnel.potSlot = slot;
                NetworkHelper.sendData(NetworkHelper.dataType.UsePotion);
            }

            // Discarding potions clears the courier slot
            CoopCourierPotion removeMe = null;
            for (CoopCourierPotion p : TogetherManager.courierScreen.potions) {
                if (p.slot == slot)
                    removeMe = p;
            }
            if (removeMe != null)
                TogetherManager.courierScreen.potions.remove(removeMe);
        }
    }

    @SpirePatch(clz = AbstractPotion.class, method="setAsObtained")
    public static class SharedPotionAdd {
        public static void Postfix(AbstractPotion __instance, int potionSlot) {
            if (TogetherManager.gameMode != TogetherManager.mode.Coop) { return; }

            if (AbstractDungeon.player.hasBlight("VaporFunnel")) {
                VaporFunnel.potSlot = potionSlot;
                VaporFunnel.potName = __instance.ID;
                NetworkHelper.sendData(NetworkHelper.dataType.SendPotion);
            }
        }
    }

    @SpirePatch(clz = AbstractPlayer.class, method="removePotion", paramtypez = {AbstractPotion.class})
    public static class losePotion {
        public static void Postfix(AbstractPlayer __instance, AbstractPotion potionToObtain) {
            if (TogetherManager.gameMode == TogetherManager.mode.Normal) { return; }

            if (AbstractDungeon.player.hasBlight("VaporFunnel")) {
                VaporFunnel.potSlot = potionToObtain.slot;
                NetworkHelper.sendData(NetworkHelper.dataType.UsePotion);
            }

            NetworkHelper.sendData(NetworkHelper.dataType.GetPotion);
        }
    }

    @SpirePatch(clz = PotionBelt.class, method="onEquip")
    public static class PotionBeltPostAcquire {
        public static SpireReturn Prefix(PotionBelt __instance) {
            if (!AbstractDungeon.player.hasBlight("VaporFunnel")) { return SpireReturn.Continue(); }

            NetworkHelper.sendData(NetworkHelper.dataType.AddPotionSlot);
            return SpireReturn.Return(null);
        }
    }

    public VaporFunnel() {
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
        if (isObtained) 
            this.description = this.DESCRIPTIONS[2];
        else
            this.description = this.DESCRIPTIONS[0] + getMergedPotionSlotCount() + this.DESCRIPTIONS[1];
    }

    @Override
    public void renderTip(SpriteBatch sb) {
        updateDescription();
        this.tips.clear();
        this.tips.add(new PowerTip(name, description));

        super.renderTip(sb);
    }

    public int getMergedPotionSlotCount() {
        int potionSlotBaseline = 3;
        int potionBelts = 0;
        int potionPenalties = 0;

        if (AbstractDungeon.ascensionLevel > 10) 
            potionSlotBaseline = 2;

        for (RemotePlayer player : TogetherManager.players) {
            if (player.potionSlots > potionSlotBaseline)
                potionBelts++;

            if (player.potionSlots < potionSlotBaseline)
                potionPenalties++;
        }

        return potionPenalties + potionBelts + potionSlotBaseline - 1 + TogetherManager.players.size();
    }
    
    @Override
    public void onEquip() {
        if (isObtained) { return; }

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
                    TogetherManager.log("Added potion '" + pid + "'");
                } else {
                    TogetherManager.log("Didn't add potion '" + pid + "'");
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