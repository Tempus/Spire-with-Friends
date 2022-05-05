package chronoMods.coop;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.MathHelper;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import com.badlogic.gdx.Gdx;
import com.megacrit.cardcrawl.vfx.RarePotionParticleEffect;
import chronoMods.*;
import chronoMods.coop.*;
import chronoMods.network.steam.*;
import chronoMods.network.*;


public class CoopCourierRelic {
  public AbstractRelic relic;
  
  private CoopCourierScreen shopScreen;
  
  public int price;
  
  public int slot;

  public float glowTimer;
  
  public boolean isPurchased = false;
  
  private static final float RELIC_GOLD_OFFSET_X = -56.0F * Settings.scale;
  
  private static final float RELIC_GOLD_OFFSET_Y = -100.0F * Settings.scale;
  
  private static final float RELIC_PRICE_OFFSET_X = 14.0F * Settings.scale;
  
  private static final float RELIC_PRICE_OFFSET_Y = -62.0F * Settings.scale;
  
  private static final float GOLD_IMG_WIDTH = ImageMaster.UI_GOLD.getWidth() * Settings.scale;
  
  public CoopCourierRelic(AbstractRelic relic, int slot, CoopCourierScreen screenRef) {
    this.relic = relic;
    this.price = (int)(relic.getPrice() / 5);
    this.slot = slot;
    if (slot > 2 && AbstractDungeon.player.hasBlight("Dimensioneel"))
      this.price = (int)(relic.getPrice() / 4);
    this.shopScreen = screenRef;
  }
  
  public void update(float rugY) {
    if (this.relic != null) {
      if (!this.isPurchased) {
        if (slot > 2 && AbstractDungeon.player.hasBlight("Dimensioneel")) {
          this.glowTimer -= Gdx.graphics.getDeltaTime();
          if (this.glowTimer < 0.0F) {
            this.glowTimer = 0.25F;
            AbstractDungeon.topLevelEffects.add(new RarePotionParticleEffect(this.relic.hb));
          } 
        }

        this.relic.currentX = Settings.WIDTH * 0.33F + 150.0F * this.slot * Settings.xScale - 150.0F * Settings.xScale;
        this.relic.currentY = rugY + 450.0F * Settings.yScale;
        this.relic.hb.move(this.relic.currentX, this.relic.currentY);
        this.relic.hb.update();
        if (this.relic.hb.hovered) {
          this.shopScreen.moveHand(this.relic.currentX - 190.0F * Settings.xScale, this.relic.currentY - 70.0F * Settings.yScale);
          if (InputHelper.justClickedLeft)
            this.relic.hb.clickStarted = true; 
          this.relic.scale = Settings.scale * 1.25F;
        } else {
          this.relic.scale = MathHelper.scaleLerpSnap(this.relic.scale, Settings.scale);
        } 
        if (this.relic.hb.hovered && InputHelper.justClickedRight)
          CardCrawlGame.relicPopup.open(this.relic); 
      } 
      if (this.relic.hb.clicked || (this.relic.hb.hovered && CInputActionSet.select.isJustPressed())) {
        this.relic.hb.clicked = false;
        purchaseRelic();
      } 
    } 
  }
  
  public void setPrice(int price) {
    if (slot > 2 && AbstractDungeon.player.hasBlight("Dimensioneel") && price == 0)
      return;
    else 
      this.price = price;
  }

  public void purchaseRelic() {
    if (AbstractDungeon.player.gold >= this.price) {
      if (this.shopScreen.getRecipient() != null) {
        AbstractDungeon.player.loseGold(this.price);
        CardCrawlGame.sound.play("SHOP_PURCHASE", 0.1F);
        
        this.shopScreen.transferRelic = this.relic;
        NetworkHelper.sendData(NetworkHelper.dataType.TransferRelic);

        if (slot > 2 && AbstractDungeon.player.hasBlight("Dimensioneel")) {
          this.shopScreen.playBuySfx();
          this.shopScreen.createSpeech(CoopCourierScreen.getBuyMsg());

          this.isPurchased = true;
          hide();
          return;
        }

        AbstractDungeon.player.loseRelic(this.relic.relicId);

        // If we just ditched the Membership card, double the cost
        if (this.relic.relicId.equals("Membership Card"))
          this.shopScreen.applyDiscount(2.0F); 

        this.shopScreen.playBuySfx();
        this.shopScreen.createSpeech(CoopCourierScreen.getBuyMsg());

        // If we just ditched the courier, increase cost by 1.25x
        if (this.relic.relicId.equals("The Courier")) {
          this.shopScreen.applyDiscount(1.25F); 
        }
        this.isPurchased = true;
        hide();
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
    if (this.relic != null)
      this.relic.currentY = Settings.HEIGHT + 200.0F * Settings.scale; 
  }
  
  public void render(SpriteBatch sb) {
    if (this.relic != null) {
      this.relic.renderWithoutAmount(sb, new Color(0.0F, 0.0F, 0.0F, 0.25F));
      sb.setColor(Color.WHITE);
      sb.draw(ImageMaster.UI_GOLD, this.relic.currentX + RELIC_GOLD_OFFSET_X, this.relic.currentY + RELIC_GOLD_OFFSET_Y, GOLD_IMG_WIDTH, GOLD_IMG_WIDTH);
      Color color = Color.WHITE;
      if (this.price > AbstractDungeon.player.gold)
        color = Color.SALMON; 
      FontHelper.renderFontLeftTopAligned(sb, FontHelper.tipHeaderFont, 
          
          Integer.toString(this.price), this.relic.currentX + RELIC_PRICE_OFFSET_X, this.relic.currentY + RELIC_PRICE_OFFSET_Y, color);
    } 
  }
}
