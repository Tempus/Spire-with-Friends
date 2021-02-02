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
        this.description = this.DESCRIPTIONS[0] + maxPotionSlots + this.DESCRIPTIONS[1];
    }
    
    @Override
    public void onEquip() {
        AbstractDungeon.player.potionSlots = 2;
        while (AbstractDungeon.player.potions.size() < 6) {
            AbstractDungeon.player.potions.add(new PotionSlot(AbstractDungeon.player.potions.size()-1));
        }
    }
}