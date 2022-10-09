package chronoMods.ui.lobby;

import chronoMods.TogetherManager;
import chronoMods.network.NetworkHelper;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.screens.charSelect.CharacterSelectScreen;

public class AscensionSelectWidget
{
    // UI strings
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString("CustomModeScreen");
    public static final String[] TEXT = uiStrings.TEXT;

    // Position
    public float x;
    public float y;

    // Ascension Selection
    private Hitbox ascensionModeHb;
    private Hitbox ascLeftHb;
    private Hitbox ascRightHb;
    public int ascensionLevel = 0;
    public boolean isAscensionMode = false;

    private static float ASC_RIGHT_W;


    public AscensionSelectWidget() {
        ASC_RIGHT_W = FontHelper.getSmartWidth(FontHelper.charDescFont, TEXT[4] + "22", 9999.0F, 0.0F);
        this.ascensionModeHb = new Hitbox(80.0F * Settings.scale, 80.0F * Settings.scale);
        this.ascLeftHb = new Hitbox(70.0F * Settings.scale, 70.0F * Settings.scale);
        this.ascRightHb = new Hitbox(70.0F * Settings.scale, 70.0F * Settings.scale);
        if (Loader.isModLoaded("AscensionPlus"))
          TogetherManager.log("Ascension Plus detected");

        for (int i=0; i<Loader.MODINFOS.length; ++i) {
            TogetherManager.log(Loader.MODINFOS[i].ID);
        }
    }

    public void move(float x, float y) {
      this.x = x;
      this.y = y;

      this.ascensionModeHb.move(this.x, this.y); // 130x
      this.ascLeftHb.move( this.x + this.ascensionModeHb.width * 1.5f - ASC_RIGHT_W * 0.5F, this.y); //405x
      this.ascRightHb.move(this.x + this.ascensionModeHb.width * 1.5f + ASC_RIGHT_W * 1.5F, this.y); //250x
    }

    public void update() {
      move(this.x, this.y);
      
      this.ascensionModeHb.update();
      this.ascLeftHb.update();
      this.ascRightHb.update();

      if ((this.ascensionModeHb.justHovered) || (this.ascRightHb.justHovered) || (this.ascLeftHb.justHovered)) {
        playHoverSound();
      }
      if ((this.ascensionModeHb.hovered) && (InputHelper.justClickedLeft))
      {
        playClickStartSound();
        this.ascensionModeHb.clickStarted = true;
      }
      else if ((this.ascLeftHb.hovered) && (InputHelper.justClickedLeft))
      {
        playClickStartSound();
        this.ascLeftHb.clickStarted = true;
      }
      else if ((this.ascRightHb.hovered) && (InputHelper.justClickedLeft))
      {
        playClickStartSound();
        this.ascRightHb.clickStarted = true;
      }
      if ((this.ascensionModeHb.clicked) || (CInputActionSet.topPanel.isJustPressed()))
      {
        CInputActionSet.topPanel.unpress();
        playClickFinishSound();
        this.ascensionModeHb.clicked = false;
        this.isAscensionMode = (!this.isAscensionMode);
        if ((this.isAscensionMode) && (this.ascensionLevel == 0)) {
          this.ascensionLevel = 1;
        }
        TogetherManager.log("Ascension: " + this.ascensionLevel);
        NetworkHelper.sendData(NetworkHelper.dataType.Rules);
      }
      else if ((this.ascLeftHb.clicked) || (CInputActionSet.pageLeftViewDeck.isJustPressed()))
      {
        playClickFinishSound();
        this.ascLeftHb.clicked = false;
        this.ascensionLevel -= 1;

        if (Loader.isModLoaded("AscensionPlus")) {
          TogetherManager.log("Ascension Plus detected");
          if (this.ascensionLevel < 1) {
            this.ascensionLevel = 25;
          }
        } else {
          TogetherManager.log("No Ascension+");
          if (this.ascensionLevel < 1) {
            this.ascensionLevel = 20;
          }
        }

        TogetherManager.log("Ascension: " + this.ascensionLevel);
        NetworkHelper.sendData(NetworkHelper.dataType.Rules);
      }
      else if ((this.ascRightHb.clicked) || (CInputActionSet.pageRightViewExhaust.isJustPressed()))
      {
        playClickFinishSound();
        this.ascRightHb.clicked = false;
        this.ascensionLevel += 1;

        if (Loader.isModLoaded("AscensionPlus")) {
          TogetherManager.log("Ascension Plus detected");
          if (this.ascensionLevel > 25) {
            this.ascensionLevel = 1;
          }
        } else {
          if (this.ascensionLevel > 20) {
            this.ascensionLevel = 1;
          }
        }

        this.isAscensionMode = true;
        TogetherManager.log("Ascension: " + this.ascensionLevel);
        NetworkHelper.sendData(NetworkHelper.dataType.Rules);
      }
    }

    private void playClickStartSound()
    {
      CardCrawlGame.sound.playA("UI_CLICK_1", -0.1F);
    }
    
    private void playClickFinishSound()
    {
      CardCrawlGame.sound.playA("UI_CLICK_1", -0.1F);
    }
    
    private void playHoverSound()
    {
      CardCrawlGame.sound.playV("UI_HOVER", 0.75F);
    }

    public void render(SpriteBatch sb) {
        sb.setColor(Color.WHITE);
        if (this.ascensionModeHb.hovered)
        {
          sb.draw(ImageMaster.CHECKBOX, this.ascensionModeHb.cX - 32.0F, this.ascensionModeHb.cY - 32.0F, 32.0F, 32.0F, 64.0F, 64.0F, Settings.scale * 1.2F, Settings.scale * 1.2F, 0.0F, 0, 0, 64, 64, false, false);
          
          sb.setColor(Color.GOLD);
          sb.setBlendFunction(770, 1);
          sb.draw(ImageMaster.CHECKBOX, this.ascensionModeHb.cX - 32.0F, this.ascensionModeHb.cY - 32.0F, 32.0F, 32.0F, 64.0F, 64.0F, Settings.scale * 1.2F, Settings.scale * 1.2F, 0.0F, 0, 0, 64, 64, false, false);
          
          sb.setBlendFunction(770, 771);
        }
        else
        {
          sb.draw(ImageMaster.CHECKBOX, this.ascensionModeHb.cX - 32.0F, this.ascensionModeHb.cY - 32.0F, 32.0F, 32.0F, 64.0F, 64.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 64, 64, false, false);
        }
        if (this.ascensionModeHb.hovered) {
          FontHelper.renderFontCentered(sb, FontHelper.charDescFont, TEXT[4] + this.ascensionLevel, this.ascLeftHb.cX + (this.ascRightHb.cX - this.ascLeftHb.cX) / 2f, this.y, Color.CYAN);
        } else {
          FontHelper.renderFontCentered(sb, FontHelper.charDescFont, TEXT[4] + this.ascensionLevel, this.ascLeftHb.cX + (this.ascRightHb.cX - this.ascLeftHb.cX) / 2f, this.y, Settings.BLUE_TEXT_COLOR);
        }
        if (this.isAscensionMode) {
          sb.draw(ImageMaster.TICK, this.ascensionModeHb.cX - 32.0F, this.ascensionModeHb.cY - 32.0F, 32.0F, 32.0F, 64.0F, 64.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 64, 64, false, false);
        }
        if (this.ascensionLevel != 0) {

          if (this.ascensionLevel > 20) 
            CardCrawlGame.mainMenuScreen.charSelectScreen.ascLevelInfoString = (CardCrawlGame.languagePack.getUIString("AscensionPlus:CustomAscension")).TEXT[this.ascensionLevel - 21]; 
          else 
            CardCrawlGame.mainMenuScreen.charSelectScreen.ascLevelInfoString = CharacterSelectScreen.A_TEXT[(this.ascensionLevel - 1)];

          FontHelper.renderSmartText(sb, FontHelper.charDescFont, CardCrawlGame.mainMenuScreen.charSelectScreen.ascLevelInfoString, this.ascensionModeHb.cX, this.ascensionModeHb.cY - this.ascensionModeHb.height / 1.5f, 9999.0F, 32.0F * Settings.scale, Settings.CREAM_COLOR);
        }
        if ((this.ascLeftHb.hovered) || (Settings.isControllerMode)) {
          sb.setColor(Color.WHITE);
        } else {
          sb.setColor(Color.LIGHT_GRAY);
        }
        sb.draw(ImageMaster.CF_LEFT_ARROW, this.ascLeftHb.cX - 24.0F, this.ascLeftHb.cY - 24.0F, 24.0F, 24.0F, 48.0F, 48.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 48, 48, false, false);
        if ((this.ascRightHb.hovered) || (Settings.isControllerMode)) {
          sb.setColor(Color.WHITE);
        } else {
          sb.setColor(Color.LIGHT_GRAY);
        }
        sb.draw(ImageMaster.CF_RIGHT_ARROW, this.ascRightHb.cX - 24.0F, this.ascRightHb.cY - 24.0F, 24.0F, 24.0F, 48.0F, 48.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 48, 48, false, false);
        if (Settings.isControllerMode)
        {
          sb.draw(CInputActionSet.topPanel
            .getKeyImg(), this.ascensionModeHb.cX - 64.0F * Settings.scale - 32.0F, this.ascensionModeHb.cY - 32.0F, 32.0F, 32.0F, 64.0F, 64.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 64, 64, false, false);
          
          sb.draw(CInputActionSet.pageLeftViewDeck
            .getKeyImg(), this.ascLeftHb.cX - 12.0F * Settings.scale - 32.0F, this.ascLeftHb.cY + 40.0F * Settings.scale - 32.0F, 32.0F, 32.0F, 64.0F, 64.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 64, 64, false, false);
          
          sb.draw(CInputActionSet.pageRightViewExhaust
            .getKeyImg(), this.ascRightHb.cX + 12.0F * Settings.scale - 32.0F, this.ascRightHb.cY + 40.0F * Settings.scale - 32.0F, 32.0F, 32.0F, 64.0F, 64.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 64, 64, false, false);
        }
        this.ascensionModeHb.render(sb);
        this.ascLeftHb.render(sb);
        this.ascRightHb.render(sb);
    }
}