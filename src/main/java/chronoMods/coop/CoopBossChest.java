package chronoMods.coop;

import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.rewards.chests.*;
import com.megacrit.cardcrawl.rewards.*;
import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.relics.*;
import com.megacrit.cardcrawl.core.*;
import com.megacrit.cardcrawl.rooms.*;
import com.megacrit.cardcrawl.screens.select.*;
import com.megacrit.cardcrawl.ui.buttons.*;
import com.megacrit.cardcrawl.helpers.input.*;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.blights.AbstractBlight;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.*;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import java.util.ArrayList;

import chronoMods.*;
import com.evacipated.cardcrawl.modthespire.lib.*;
import basemod.*;
import basemod.interfaces.*;

public class CoopBossChest extends BossChest {
  private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString("BossChest");
  public static final String[] TEXT = uiStrings.TEXT;
    
  public ArrayList<AbstractBlight> blights = new ArrayList<>();

    @SpirePatch(clz=BossRelicSelectScreen.class, method="update")
    public static class InsertNextChestIntoBossRoom
    {
        @SpireInsertPatch(rloc=130-93, localvars={})
        public static void Insert(BossRelicSelectScreen __instance)
        {
            if (TogetherManager.gameMode == TogetherManager.mode.Coop) { 
                TogetherManager.log ("We're putting in the boss relic! Hooray");
                ((TreasureRoomBoss)AbstractDungeon.getCurrRoom()).chest = new CoopBossChest();
            }
        }
    }

  public CoopBossChest() {
    this.img = ImageMaster.loadImage("chrono/images/chests/friendChest.png");
    this.openedImg = ImageMaster.loadImage("chrono/images/chests/friendChestOpen.png");
    this.hb = new Hitbox(256.0F * Settings.scale, 200.0F * Settings.scale);
    this.hb.move(CHEST_LOC_X, CHEST_LOC_Y - 100.0F * Settings.scale);

    int choice;
    for (int i = 0; i < 2; i++) {
      if (TogetherManager.teamBlights.get(0).blightID.equals("MessageInABottle") && AbstractDungeon.actNum == 2) {
        this.blights.add(TogetherManager.teamBlights.get(1));
        TogetherManager.teamBlights.remove(1);
      } else {
        this.blights.add(TogetherManager.teamBlights.get(0));
        TogetherManager.teamBlights.remove(0);        
      }
    }

    AbstractDungeon.overlayMenu.proceedButton.hide();
    (AbstractDungeon.getCurrRoom()).phase = AbstractRoom.RoomPhase.INCOMPLETE;
  }
  
  // public void update() {
  //   super.update();
  //   if (TogetherManager.teamRelicScreen.isDone && AbstractDungeon.screen == AbstractDungeon.CurrentScreen.NONE) {
  //     AbstractDungeon.overlayMenu.proceedButton.show(); 
  //   }
  // }

  @Override
  public void open(boolean bossChest) {
      CardCrawlGame.sound.play("CHEST_OPEN");
      TogetherManager.teamRelicScreen.open(this.blights);
  }
  
  @Override
  public void close() {
    CardCrawlGame.sound.play("CHEST_OPEN");
    this.isOpen = false;
  }
}