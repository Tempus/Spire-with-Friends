package chronoMods.coop;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.potions.AbstractPotion;

import chronoMods.*;
import chronoMods.coop.*;
import chronoMods.network.steam.*;
import chronoMods.network.*;

public class CoopCourierPotion {
  private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString("StorePotion");
  
  public static final String[] TEXT = uiStrings.TEXT;
  
  public AbstractPotion potion;
  
  private CoopCourierScreen shopScreen;
  
  public int price;
  
  public int slot;
  public int originalSlot = 0;
  
  public boolean isPurchased = false;
  
  private static final float RELIC_GOLD_OFFSET_X = -56.0F * Settings.scale;
  
  private static final float RELIC_GOLD_OFFSET_Y = -100.0F * Settings.scale;
  
  private static final float RELIC_PRICE_OFFSET_X = 14.0F * Settings.scale;
  
  private static final float RELIC_PRICE_OFFSET_Y = -62.0F * Settings.scale;
  
  private static final float GOLD_IMG_WIDTH = ImageMaster.UI_GOLD.getWidth() * Settings.scale;
  
  public CoopCourierPotion(AbstractPotion potion, int slot, int originalSlot, CoopCourierScreen screenRef) {
    this.potion = potion;
    this.price = (int)(potion.getPrice() / 4);
    this.slot = slot;
    this.originalSlot = originalSlot;
    this.shopScreen = screenRef;
  }
  
  public void update(float rugY) {
    if (this.potion != null) {
      if (!this.isPurchased) {
        this.potion.posX = Settings.WIDTH * 0.33F + 150.0F * this.slot * Settings.xScale;
        this.potion.posY = rugY + 275.0F * Settings.yScale;
        this.potion.hb.move(this.potion.posX, this.potion.posY);
        this.potion.hb.update();
        if (this.potion.hb.hovered) {
          this.shopScreen.moveHand(this.potion.posX - 190.0F * Settings.scale, this.potion.posY - 70.0F * Settings.scale);
          if (InputHelper.justClickedLeft)
            this.potion.hb.clickStarted = true; 
        } 
      } 
      if (this.potion.hb.clicked || (this.potion.hb.hovered && CInputActionSet.select.isJustPressed())) {
        this.potion.hb.clicked = false;
        purchasePotion();
      } 
    } 
  }
  
  public void purchasePotion() {
    if (AbstractDungeon.player.gold >= this.price) {
      if (this.shopScreen.getRecipient() != null) {

        AbstractDungeon.player.loseGold(this.price);
        CardCrawlGame.sound.play("SHOP_PURCHASE", 0.1F);

        this.shopScreen.transferPotion = this.potion;
        NetworkHelper.sendData(NetworkHelper.dataType.TransferPotion);

        AbstractDungeon.topPanel.destroyPotion(originalSlot);

        this.shopScreen.playBuySfx();
        this.shopScreen.createSpeech(CoopCourierScreen.getBuyMsg());

        this.isPurchased = true;
        hide();

        return;
      } else {
        this.shopScreen.playCantBuySfx();
        this.shopScreen.createSpeech(CoopCourierScreen.getNoRecipientMsg());
      }
    } else {
      this.shopScreen.playCantBuySfx();
      this.shopScreen.createSpeech(CoopCourierScreen.getCantBuyMsg());
    } 
  }
  
  public void hide() {
    if (this.potion != null)
      this.potion.posY = Settings.HEIGHT + 200.0F * Settings.scale; 
  }
  
  public void render(SpriteBatch sb) {
    if (this.potion != null) {
      this.potion.shopRender(sb);
      sb.setColor(Color.WHITE);
      sb.draw(ImageMaster.UI_GOLD, this.potion.posX + RELIC_GOLD_OFFSET_X, this.potion.posY + RELIC_GOLD_OFFSET_Y, GOLD_IMG_WIDTH, GOLD_IMG_WIDTH);
      Color color = Color.WHITE;
      if (this.price > AbstractDungeon.player.gold)
        color = Color.SALMON; 
      FontHelper.renderFontLeftTopAligned(sb, FontHelper.tipHeaderFont, 
          
          Integer.toString(this.price), this.potion.posX + RELIC_PRICE_OFFSET_X, this.potion.posY + RELIC_PRICE_OFFSET_Y, color);
    } 
  }
}
