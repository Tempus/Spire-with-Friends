package chronoMods.coop;

import com.evacipated.cardcrawl.modthespire.lib.*;

import com.megacrit.cardcrawl.localization.*;
import com.megacrit.cardcrawl.screens.custom.*;
import com.megacrit.cardcrawl.screens.*;
import com.megacrit.cardcrawl.ui.panels.*;
import com.megacrit.cardcrawl.screens.stats.*;
import com.megacrit.cardcrawl.core.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.megacrit.cardcrawl.core.*;
import com.megacrit.cardcrawl.helpers.*;
import com.megacrit.cardcrawl.helpers.input.*;
import com.megacrit.cardcrawl.map.*;
import com.megacrit.cardcrawl.dungeons.*;
import com.codedisaster.steamworks.*;
import com.megacrit.cardcrawl.integrations.steam.SteamIntegration;

import basemod.*;
import basemod.abstracts.*;
import basemod.interfaces.*;

import org.apache.logging.log4j.*;
import java.nio.charset.StandardCharsets;
import java.lang.reflect.Type;
import java.util.*;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import chronoMods.*;
import chronoMods.network.steam.*;
import chronoMods.network.*;
import chronoMods.coop.*;
import chronoMods.ui.deathScreen.*;
import chronoMods.ui.hud.*;
import chronoMods.ui.lobby.*;
import chronoMods.ui.mainMenu.*;
import chronoMods.utilities.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Disposable;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AnimatedNpc;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.CharacterStrings;
import com.megacrit.cardcrawl.vfx.SpeechBubble;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class CoopCourier implements Disposable {
  private static final CharacterStrings characterStrings = CardCrawlGame.languagePack.getCharacterString("Merchant");
  public static final String[] NAMES = characterStrings.NAMES;
  public static final String[] TEXT = characterStrings.TEXT;
  public static final String[] ENDING_TEXT = characterStrings.OPTIONS;

  public static final String[] TALK = CardCrawlGame.languagePack.getUIString("Courier").TEXT;
  
  public AnimatedNpc anim;
  
  public static final float DRAW_X = Settings.WIDTH * 0.5F + 34.0F * Settings.xScale;
  public static final float DRAW_Y = AbstractDungeon.floorY - 109.0F * Settings.scale;
  
  public Hitbox hb = new Hitbox(360.0F * Settings.scale, 300.0F * Settings.scale);
  
  private ArrayList<String> idleMessages = new ArrayList<>();
  
  private float speechTimer = 1.5F;
  private boolean saidWelcome = false;
  
  private static final float MIN_IDLE_MSG_TIME = 40.0F;
  private static final float MAX_IDLE_MSG_TIME = 60.0F;
  private static final float SPEECH_DURATION = 3.0F;
  
  private int shopScreen = 1;
  
  protected float modX;
  protected float modY;
  
  public CoopCourier() {
    this(0.0F, 0.0F);
  }
  
  public CoopCourier(float x, float y) {
    this.anim = new AnimatedNpc(DRAW_X + 256.0F * Settings.scale, AbstractDungeon.floorY + 30.0F * Settings.scale, "chrono/images/courier/Courier.atlas", "chrono/images/courier/Courier.json", "idle");

    if (AbstractDungeon.id.equals("TheEnding")) {
      this.idleMessages.add(TALK[0]);
      this.idleMessages.add(TALK[1]);
      this.idleMessages.add(TALK[2]);
    } else {
      this.idleMessages.add(TALK[3]);
      this.idleMessages.add(TALK[4]);
      this.idleMessages.add(TALK[5]);
      this.idleMessages.add(TALK[6]);
      this.idleMessages.add(TALK[7]);
      this.idleMessages.add(TALK[8]);
      this.idleMessages.add(TALK[9]);
    } 

    this.speechTimer = 1.5F;
    this.modX = x;
    this.modY = y;
    this.hb.move(DRAW_X + (250.0F + x) * Settings.scale, DRAW_Y + (130.0F + y) * Settings.scale);
    TogetherManager.courierScreen.init();
  }
  
  public void update() {
    this.hb.update();
    if (((this.hb.hovered && InputHelper.justClickedLeft) || CInputActionSet.select.isJustPressed()) && !AbstractDungeon.isScreenUp && !AbstractDungeon.isFadingOut && !AbstractDungeon.player.viewingRelics) {
      AbstractDungeon.overlayMenu.proceedButton.setLabel(NAMES[0]);
      this.saidWelcome = true;
      TogetherManager.courierScreen.open();
      this.hb.hovered = false;
    } 
    this.speechTimer -= Gdx.graphics.getDeltaTime();
    if (this.speechTimer < 0.0F && this.shopScreen == 1) {
      String msg = this.idleMessages.get(MathUtils.random(0, this.idleMessages.size() - 1));
      if (!this.saidWelcome) {
        this.saidWelcome = true;
        welcomeSfx();
        msg = NAMES[1];
      } else {
        playMiscSfx();
      } 
      if (MathUtils.randomBoolean()) {
        AbstractDungeon.effectList.add(new SpeechBubble(this.hb.cX - 50.0F * Settings.xScale, this.hb.cY + 70.0F * Settings.yScale, 3.0F, msg, false));
      } else {
        AbstractDungeon.effectList.add(new SpeechBubble(this.hb.cX - 50.0F * Settings.xScale, this.hb.cY + 70.0F * Settings.yScale, 3.0F, msg, true));
      } 
      this.speechTimer = MathUtils.random(40.0F, 60.0F);
    } 
  }
  
  private void welcomeSfx() {
    int roll = MathUtils.random(2);
    if (roll == 0) {
      CardCrawlGame.sound.play("VO_MERCHANT_3A");
    } else if (roll == 1) {
      CardCrawlGame.sound.play("VO_MERCHANT_3B");
    } else {
      CardCrawlGame.sound.play("VO_MERCHANT_3C");
    } 
  }
  
  private void playMiscSfx() {
    int roll = MathUtils.random(5);
    if (roll == 0) {
      CardCrawlGame.sound.play("VO_MERCHANT_MA");
    } else if (roll == 1) {
      CardCrawlGame.sound.play("VO_MERCHANT_MB");
    } else if (roll == 2) {
      CardCrawlGame.sound.play("VO_MERCHANT_MC");
    } else if (roll == 3) {
      CardCrawlGame.sound.play("VO_MERCHANT_3A");
    } else if (roll == 4) {
      CardCrawlGame.sound.play("VO_MERCHANT_3B");
    } else {
      CardCrawlGame.sound.play("VO_MERCHANT_3C");
    } 
  }
  
  public void render(SpriteBatch sb) {
    sb.setColor(Color.WHITE);
    // sb.draw(ImageMaster.MERCHANT_RUG_IMG, DRAW_X + this.modX, DRAW_Y + this.modY, 512.0F * Settings.scale, 512.0F * Settings.scale);
    // if (this.hb.hovered) {
    //   sb.setBlendFunction(770, 1);
    //   sb.setColor(Settings.HALF_TRANSPARENT_WHITE_COLOR);
    //   sb.draw(ImageMaster.MERCHANT_RUG_IMG, DRAW_X + this.modX, DRAW_Y + this.modY, 512.0F * Settings.scale, 512.0F * Settings.scale);
    //   sb.setBlendFunction(770, 771);
    // } 
    if (this.anim != null) {
      AbstractCreature.sr.setPremultipliedAlpha(false);
      this.anim.render(sb); 
      AbstractCreature.sr.setPremultipliedAlpha(true);
    }
    if (Settings.isControllerMode) {
      sb.setColor(Color.WHITE);
      sb.draw(CInputActionSet.select
          .getKeyImg(), DRAW_X - 32.0F + 150.0F * Settings.scale, DRAW_Y - 32.0F + 100.0F * Settings.scale, 32.0F, 32.0F, 64.0F, 64.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 64, 64, false, false);
    } 
    this.hb.render(sb);
  }
  
  public void dispose() {
    if (this.anim != null)
      this.anim.dispose(); 
  }
}
