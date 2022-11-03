package chronoMods.ui.lobby;

import chronoMods.coop.MergeCustom;
import chronoMods.network.NetworkHelper;
import chronoMods.ui.mainMenu.NewMenuButtons;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.MathHelper;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.controller.CInputHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.RunModStrings;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.screens.charSelect.CharacterSelectScreen;
import com.megacrit.cardcrawl.screens.custom.CustomMod;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen;
import com.megacrit.cardcrawl.screens.mainMenu.MenuCancelButton;
import com.megacrit.cardcrawl.screens.mainMenu.ScrollBar;
import com.megacrit.cardcrawl.screens.mainMenu.ScrollBarListener;
import com.megacrit.cardcrawl.trials.AbstractTrial;
import com.megacrit.cardcrawl.trials.CustomTrial;

import java.util.ArrayList;
import java.util.Arrays;


public class CustomModePopOver implements ScrollBarListener {
  public static class Enum
  {
      @SpireEnum
      public static MainMenuScreen.CurScreen MPCUSTOMMODE;
  }

  private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString("CustomModeScreen");
  
  public static final String[] TEXT = uiStrings.TEXT;
  
  private final float imageScale;
  
  private MenuCancelButton cancelButton = new MenuCancelButton();
    
  private Hitbox controllerHb;
    
  private static float ASC_RIGHT_W;
        
  public ArrayList<CustomMod> modList;
  
  private static final String DAILY_MODS = "Daily Mods";
  
  private static final String MOD_BLIGHT_CHESTS = "Blight Chests";
  
  private static final String MOD_ONE_HIT_WONDER = "One Hit Wonder";
  
  private static final String MOD_PRAISE_SNECKO = "Praise Snecko";
  
  private static final String MOD_INCEPTION = "Inception";
  
  private static final String MOD_MY_TRUE_FORM = "My True Form";
  
  private static final String MOD_STARTER_DECK = "Starter Deck";
  
  private static final String NEUTRAL_COLOR = "b";
  
  private static final String POSITIVE_COLOR = "g";
  
  private static final String NEGATIVE_COLOR = "r";
  
  public boolean screenUp = false;
  
  public static float screenX;
  
  private float ASCENSION_TEXT_Y = 480.0F;
  
  private boolean grabbedScreen = false;
  
  private float grabStartY = 0.0F;
  
  private float targetY = 0.0F;
  
  private float scrollY = 0.0F;
  
  private float scrollLowerBound;
  
  private float scrollUpperBound;
  
  private ScrollBar scrollBar;
  
  public CustomModePopOver() {
    screenX = Settings.isMobile ? (240.0F * Settings.xScale) : (300.0F * Settings.xScale);
    this.imageScale = Settings.isMobile ? (Settings.scale * 1.2F) : Settings.scale;
    initializeMods();
    calculateScrollBounds();
    if (Settings.isMobile) {
      this.scrollBar = new ScrollBar(this, Settings.WIDTH - 280.0F * Settings.xScale - ScrollBar.TRACK_W / 2.0F, Settings.HEIGHT / 2.0F, Settings.HEIGHT - 256.0F * Settings.yScale, true);
    } else {
      this.scrollBar = new ScrollBar(this, Settings.WIDTH - 280.0F * Settings.xScale - ScrollBar.TRACK_W / 2.0F, Settings.HEIGHT / 2.0F, Settings.HEIGHT - 256.0F * Settings.yScale);
    } 
  }
  
  private void initializeMods() {
    this.modList = new ArrayList<>();

    // Spire w/ Friends mods
    CustomMod draftMod = addDailyMod("Draft", "b");
    CustomMod sealedMod = addDailyMod("SealedDeck", "b");
    addDailyMod("Hoarder", "b");
    CustomMod insanityMod = addDailyMod("Insanity", "b");
    addDailyMod("Chimera", "b");
    addMod("Praise Snecko", "b", false);
    CustomMod shinyMod = addDailyMod("Shiny", "b");
    addDailyMod("Specialized", "b");
    addDailyMod("Vintage", "b");
    addDailyMod("ControlledChaos", "b");
    addMod("Inception", "b", false);
    addDailyMod("Allstar", "g");
    CustomMod diverseMod = addDailyMod("Diverse", "g");
    CustomMod redMod = addDailyMod("Red Cards", "g");
    CustomMod greenMod = addDailyMod("Green Cards", "g");
    CustomMod blueMod = addDailyMod("Blue Cards", "g");
    CustomMod purpleMod = addDailyMod("Purple Cards", "g"); 
    addDailyMod("Colorless Cards", "g");
    addDailyMod("Heirloom", "g");
    addDailyMod("Time Dilation", "g");
    addDailyMod("Flight", "g");
    addMod("My True Form", "g", false);
    addMod("MergeCards", "g", false);
    addDailyMod("DeadlyEvents", "r");
    addDailyMod("Binary", "r");
    addMod("One Hit Wonder", "r", false);
    addDailyMod("Cursed Run", "r");
    addDailyMod("Elite Swarm", "r");
    addDailyMod("Lethality", "r");
    addDailyMod("Midas", "r");
    addDailyMod("Night Terrors", "r");
    addDailyMod("Terminal", "r");
    addDailyMod("Uncertain Future", "r");
    addMod("Starter Deck", "r", false);
    insanityMod.setMutualExclusionPair(shinyMod);
    sealedMod.setMutualExclusionPair(draftMod);
    diverseMod.setMutualExclusionPair(redMod);
    diverseMod.setMutualExclusionPair(greenMod);
    diverseMod.setMutualExclusionPair(blueMod);
    diverseMod.setMutualExclusionPair(purpleMod); 
  }
  
  private CustomMod addMod(String id, String color, boolean isDailyMod) {
    RunModStrings modString = CardCrawlGame.languagePack.getRunModString(id);
    if (modString != null) {
      CustomMod mod = new CustomMod(id, color, isDailyMod);
      this.modList.add(mod);
      return mod;
    } 
    return null;
  }
  
  private CustomMod addDailyMod(String id, String color) {
    return addMod(id, color, true);
  }
  
  public void open() {
    this.controllerHb = null;
    this.targetY = 0.0F;
    this.screenUp = true;
    CardCrawlGame.mainMenuScreen.screen = CustomModePopOver.Enum.MPCUSTOMMODE;
    CardCrawlGame.mainMenuScreen.darken();
    this.cancelButton.show(CharacterSelectScreen.TEXT[5]);
  }
    
  public void update() {
    updateControllerInput();
    if (Settings.isControllerMode && this.controllerHb != null)
      if (Gdx.input.getY() > Settings.HEIGHT * 0.75F) {
        this.targetY += Settings.SCROLL_SPEED;
      } else if (Gdx.input.getY() < Settings.HEIGHT * 0.25F) {
        this.targetY -= Settings.SCROLL_SPEED;
      }  

    boolean isDraggingScrollBar = this.scrollBar.update();
    if (!isDraggingScrollBar)
      updateScrolling(); 
    updateMods();
    updateCancelButton();

    if (Settings.isControllerMode && this.controllerHb != null)
      CInputHelper.setCursor(this.controllerHb); 
  }
  
  private void updateCancelButton() {
    this.cancelButton.update();
    if (this.cancelButton.hb.clicked || InputHelper.pressedEscape) {
      InputHelper.pressedEscape = false;
      this.cancelButton.hb.clicked = false;
      this.cancelButton.hide();
      NetworkHelper.sendData(NetworkHelper.dataType.Rules);
      CardCrawlGame.mainMenuScreen.screen = NewGameScreen.Enum.CREATEMULTIPLAYERGAME;
      updateValues();
    } 
  }
  
  public void updateValues() {
      for (CustomMod cm : NewMenuButtons.customScreen.modList) {
          if (cm.selected) { 
              Settings.isTrial = true;
              CustomTrial trial = new CustomTrial();
              trial.addDailyMods(getActiveDailyModIds());
              addNonDailyMods(trial, getActiveNonDailyMods());
              CardCrawlGame.trial = (AbstractTrial)trial;
              AbstractPlayer.customMods = CardCrawlGame.trial.dailyModIDs();
              return;
          }
      }

      Settings.isTrial = false;
      CardCrawlGame.trial = null;
      AbstractPlayer.customMods = null;
  }
    
  private void updateMods() {
    float offset = 510.0F * Settings.yScale;
    for (int i = 0; i < this.modList.size(); i++) {
      ((CustomMod)this.modList.get(i)).update(this.scrollY + offset);
      offset -= ((CustomMod)this.modList.get(i)).height;
    } 
  }
  
  private ArrayList<String> getActiveDailyModIds() {
    ArrayList<String> active = new ArrayList<>();
    for (CustomMod mod : this.modList) {
      if (mod.selected && mod.isDailyMod)
        active.add(mod.ID); 
    } 
    return active;
  }
  
  private ArrayList<String> getActiveNonDailyMods() {
    ArrayList<String> active = new ArrayList<>();
    for (CustomMod mod : this.modList) {
      if (mod.selected && !mod.isDailyMod) {
        active.add(mod.ID); 
      }
    } 
    return active;
  }
  
  public ArrayList<Boolean> getActiveModData() {
    ArrayList<Boolean> active = new ArrayList<>();
    for (CustomMod mod : this.modList) {
      active.add(mod.selected); 
    } 
    return active;
  }

  private void addNonDailyMods(CustomTrial trial, ArrayList<String> modIds) {
    MergeCustom.isActive = false;

    for (String modId : modIds) {
      switch (modId) {
        // case "Daily Mods":
        //   trial.setRandomDailyMods();
        //   break;
        case "One Hit Wonder":
          trial.setMaxHpOverride(1);
          break;
        case "Praise Snecko":
          trial.addStarterRelic("Snecko Eye");
          trial.setShouldKeepStarterRelic(false);
          break;
        case "Inception":
          trial.addStarterRelic("Unceasing Top");
          trial.setShouldKeepStarterRelic(false);
          break;
        case "My True Form":
          trial.addStarterCards(Arrays.asList(new String[] { "Demon Form", "Wraith Form v2", "Echo Form", "DevaForm" }));
          break;
        case "Starter Deck":
          trial.addStarterRelic("Busted Crown");
          trial.addDailyMod("Binary");
          break;
        case "Blight Chests":
          trial.addDailyMod("Blight Chests");
          break;
        case "MergeCards":
          MergeCustom.isActive = true;
          break;
      } 
    } 
  }
  
  private void playClickStartSound() {
    CardCrawlGame.sound.playA("UI_CLICK_1", -0.1F);
  }
  
  private void playClickFinishSound() {
    CardCrawlGame.sound.playA("UI_CLICK_1", -0.1F);
  }
  
  private void playHoverSound() {
    CardCrawlGame.sound.playV("UI_HOVER", 0.75F);
  }
  
  public void render(SpriteBatch sb) {
    renderScreen(sb);
    this.scrollBar.render(sb);
    this.cancelButton.render(sb);
    sb.setColor(Color.WHITE.cpy());
    for (CustomMod m : this.modList)
      m.render(sb); 
  }
  
  public void renderScreen(SpriteBatch sb) {
    renderTitle(sb, TEXT[0], this.scrollY - 50.0F * Settings.scale);
    renderHeader(sb, TEXT[6], this.scrollY - 120.0F * Settings.scale); // used to be 630
  }
  
  private void renderHeader(SpriteBatch sb, String text, float y) {
    if (Settings.isMobile) {
      FontHelper.renderSmartText(sb, FontHelper.panelNameFont, text, screenX + 50.0F * Settings.scale, y + 850.0F * Settings.yScale, 9999.0F, 32.0F * Settings.scale, Settings.GOLD_COLOR, 1.2F);
    } else {
      FontHelper.renderSmartText(sb, FontHelper.panelNameFont, text, screenX + 50.0F * Settings.scale, y + 850.0F * Settings.yScale, 9999.0F, 32.0F * Settings.scale, Settings.GOLD_COLOR);
    } 
  }
  
  private void renderTitle(SpriteBatch sb, String text, float y) {
    FontHelper.renderSmartText(sb, FontHelper.charTitleFont, text, screenX, y + 900.0F * Settings.yScale, 9999.0F, 32.0F * Settings.scale, Settings.GOLD_COLOR);
    if (!Settings.usesTrophies) {
      FontHelper.renderSmartText(sb, FontHelper.tipBodyFont, TEXT[1], screenX + 
          
          FontHelper.getSmartWidth(FontHelper.charTitleFont, text, 9999.0F, 9999.0F) + 18.0F * Settings.xScale, y + 888.0F * Settings.yScale, 9999.0F, 32.0F * Settings.scale, Settings.RED_TEXT_COLOR);
    } else {
      FontHelper.renderSmartText(sb, FontHelper.tipBodyFont, TEXT[9], screenX + 
          
          FontHelper.getSmartWidth(FontHelper.charTitleFont, text, 9999.0F, 9999.0F) + 18.0F * Settings.xScale, y + 888.0F * Settings.yScale, 9999.0F, 32.0F * Settings.scale, Settings.RED_TEXT_COLOR);
    } 
  }
  
  private void updateControllerInput() {
    if (!Settings.isControllerMode)
      return; 
    boolean anyHovered = false;
    int index = 0;
    if (!anyHovered) {
      index = 0;
      for (CustomMod m : this.modList) {
        if (m.hb.hovered) {
          anyHovered = true;
          if (CInputActionSet.up.isJustPressed() || CInputActionSet.altUp.isJustPressed()) {
            index--;
            CInputHelper.setCursor(((CustomMod)this.modList.get(index)).hb);
            this.controllerHb = ((CustomMod)this.modList.get(index)).hb;
            break;
          } 
          if (CInputActionSet.down.isJustPressed() || CInputActionSet.altDown.isJustPressed()) {
            index++;
            if (index > this.modList.size() - 1)
              index = this.modList.size() - 1; 
            CInputHelper.setCursor(((CustomMod)this.modList.get(index)).hb);
            this.controllerHb = ((CustomMod)this.modList.get(index)).hb;
            break;
          } 
          if (CInputActionSet.select.isJustPressed()) {
            CInputActionSet.select.unpress();
            ((CustomMod)this.modList.get(index)).hb.clicked = true;
          } 
          break;
        } 
        index++;
      } 
    } 
  }
  
  private void updateScrolling() {
    int y = InputHelper.mY;
    if (this.scrollUpperBound > 0.0F)
      if (!this.grabbedScreen) {
        if (InputHelper.scrolledDown) {
          this.targetY += Settings.SCROLL_SPEED;
        } else if (InputHelper.scrolledUp) {
          this.targetY -= Settings.SCROLL_SPEED;
        } 
        if (InputHelper.justClickedLeft) {
          this.grabbedScreen = true;
          this.grabStartY = y - this.targetY;
        } 
      } else if (InputHelper.isMouseDown) {
        this.targetY = y - this.grabStartY;
      } else {
        this.grabbedScreen = false;
      }  
    this.scrollY = MathHelper.scrollSnapLerpSpeed(this.scrollY, this.targetY);
    if (this.targetY < this.scrollLowerBound) {
      this.targetY = MathHelper.scrollSnapLerpSpeed(this.targetY, this.scrollLowerBound);
    } else if (this.targetY > this.scrollUpperBound) {
      this.targetY = MathHelper.scrollSnapLerpSpeed(this.targetY, this.scrollUpperBound);
    } 
    updateBarPosition();
  }
  
  private void calculateScrollBounds() {
    this.scrollUpperBound = this.modList.size() * 90.0F * Settings.yScale + 270.0F * Settings.yScale;
    this.scrollLowerBound = 100.0F * Settings.yScale;
  }
  
  public void scrolledUsingBar(float newPercent) {
    float newPosition = MathHelper.valueFromPercentBetween(this.scrollLowerBound, this.scrollUpperBound, newPercent);
    this.scrollY = newPosition;
    this.targetY = newPosition;
    updateBarPosition();
  }
  
  private void updateBarPosition() {
    float percent = MathHelper.percentFromValueBetween(this.scrollLowerBound, this.scrollUpperBound, this.scrollY);
    this.scrollBar.parentScrolledToPercent(percent);
  }
}
