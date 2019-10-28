package chronospeed;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.ModInfo;
import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.*;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen;
import com.megacrit.cardcrawl.screens.mainMenu.MenuCancelButton;
import com.megacrit.cardcrawl.screens.mainMenu.PatchNotesScreen;
import org.apache.commons.lang3.StringUtils;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.audio.SoundMaster;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.characters.CharacterManager;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.MathHelper;
import com.megacrit.cardcrawl.helpers.SeedHelper;
import com.megacrit.cardcrawl.helpers.controller.CInputAction;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.controller.CInputHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.LocalizedStrings;
import com.megacrit.cardcrawl.localization.RunModStrings;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.screens.charSelect.CharacterSelectScreen;
import com.megacrit.cardcrawl.screens.custom.CustomModeCharacterButton;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen.CurScreen;
import com.megacrit.cardcrawl.screens.mainMenu.MenuCancelButton;
import com.megacrit.cardcrawl.screens.mainMenu.MenuPanelScreen;
import com.megacrit.cardcrawl.screens.mainMenu.ScrollBar;
import com.megacrit.cardcrawl.screens.mainMenu.ScrollBarListener;
import com.megacrit.cardcrawl.trials.AbstractTrial;
import com.megacrit.cardcrawl.trials.CustomTrial;
import com.megacrit.cardcrawl.ui.buttons.GridSelectConfirmButton;
import com.megacrit.cardcrawl.ui.panels.SeedPanel;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import java.util.ArrayList;
import java.util.Arrays;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

import chronospeed.*;
import com.codedisaster.steamworks.*;
import com.megacrit.cardcrawl.integrations.steam.SteamIntegration;
import basemod.ReflectionHacks;
import com.codedisaster.steamworks.SteamMatchmaking;

public class VersusScreen
{
    public static class Enum
    {
        @SpireEnum
        public static MainMenuScreen.CurScreen VERSUS_LOBBY;
    }

    // UI strings
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString("CustomModeScreen");
    public static final String[] TEXT = uiStrings.TEXT;

    // Settings.HEIGHT - 200.0F * Settings.scale
    // x = 300.0F * Settings.scale;

    // Buttons
    public MenuCancelButton button = new MenuCancelButton();
    public GridSelectConfirmButton confirmButton = new GridSelectConfirmButton(CharacterSelectScreen.TEXT[1]);

    // Characters
    public ArrayList<CustomModeCharacterButton> options = new ArrayList();

    // Ascension Selection
    private Hitbox ascensionModeHb;
    private Hitbox ascLeftHb;
    private Hitbox ascRightHb;
    public int ascensionLevel = 0;
    public boolean isAscensionMode = false;

    private float ASCENSION_TEXT_Y = 480.0F;
    private static float ASC_RIGHT_W;

    // Seed Selection
    private Hitbox seedHb = new Hitbox(400.0F * Settings.scale, 90.0F * Settings.scale);
    private SeedPanel seedPanel;
    public String currentSeed;


    public VersusScreen() {

        initializeCharacters();
        this.seedPanel = new SeedPanel();
    }

    public void initializeCharacters() {
        this.options.clear();
        this.options.add(new CustomModeCharacterButton(CardCrawlGame.characterManager
          .setChosenCharacter(AbstractPlayer.PlayerClass.IRONCLAD), false));
        
        this.options.add(new CustomModeCharacterButton(CardCrawlGame.characterManager
          .setChosenCharacter(AbstractPlayer.PlayerClass.THE_SILENT), false));

        this.options.add(new CustomModeCharacterButton(CardCrawlGame.characterManager
          .setChosenCharacter(AbstractPlayer.PlayerClass.DEFECT), false));

        // this.options.add(new CustomModeCharacterButton(CardCrawlGame.characterManager
        //   .setChosenCharacter(AbstractPlayer.PlayerClass.WATCHER), false));
        
        int count = this.options.size();
        for (int i = 0; i < count; i++) {
          ((CustomModeCharacterButton)this.options.get(i)).move((Settings.WIDTH / 2.0F) + i * 100.0F * Settings.scale - 200.0F * Settings.scale, Settings.HEIGHT - 200.0F * Settings.scale);
        }
        ((CustomModeCharacterButton)this.options.get(0)).hb.clicked = true;
    }

    public void open() {
        // Screen Swap
        CardCrawlGame.mainMenuScreen.darken();
        CardCrawlGame.mainMenuScreen.screen = Enum.VERSUS_LOBBY;        

        // Buttons
        button.show(PatchNotesScreen.TEXT[0]);
        this.confirmButton.show();
        this.confirmButton.isDisabled = false;

        // Seed
        Settings.seed = null;
        Settings.specialSeed = null;

        // Ascension
        ASC_RIGHT_W = FontHelper.getSmartWidth(FontHelper.charDescFont, TEXT[4] + "22", 9999.0F, 0.0F);
        this.ascensionModeHb = new Hitbox(80.0F * Settings.scale, 80.0F * Settings.scale);
        this.ascensionModeHb.move(300.0F * Settings.scale, Settings.HEIGHT - 480.0F * Settings.scale);
        this.ascLeftHb = new Hitbox(70.0F * Settings.scale, 70.0F * Settings.scale);
        this.ascRightHb = new Hitbox(70.0F * Settings.scale, 70.0F * Settings.scale);
        this.ascLeftHb.move(300.0F * Settings.scale - ASC_RIGHT_W * 0.5F, Settings.HEIGHT - 480.0F * Settings.scale);
        this.ascRightHb.move(300.0F * Settings.scale + ASC_RIGHT_W * 1.5F, Settings.HEIGHT - 480.0F * Settings.scale);


        // Steam Stuff
        NetworkHelper.createLobby();

    }

    public void update() {
        // Return to the Main Menu
        button.update();
        if (button.hb.clicked || InputHelper.pressedEscape) {
            button.hb.clicked = false;
            InputHelper.pressedEscape = false;
            CardCrawlGame.mainMenuScreen.screen = MainMenuScreen.CurScreen.MAIN_MENU;
            button.hide();
            CardCrawlGame.mainMenuScreen.lighten();
        }

        this.seedPanel.update();
        // if (!this.seedPanel.shown)
        // {
            updateCharacterButtons();
            updateAscension();
            updateSeed();
            updateEmbarkButton();
        // }

        this.currentSeed = SeedHelper.getUserFacingSeedString();

        InputHelper.justClickedLeft = false;
    }

    private void updateEmbarkButton()
    {
        this.confirmButton.update();
        if ((this.confirmButton.hb.clicked) || (CInputActionSet.proceed.isJustPressed()))
        {
            this.confirmButton.hb.clicked = false;
            for (CustomModeCharacterButton b : this.options) {
              if (b.selected)
              {
                CardCrawlGame.chosenCharacter = b.c.chosenClass;
                break;
              }
            }
            CardCrawlGame.mainMenuScreen.isFadingOut = true;
            CardCrawlGame.mainMenuScreen.fadeOutMusic();
            Settings.isTrial = true;
            Settings.isDailyRun = false;
            Settings.isEndless = false;
            // finalActAvailable = true;
            
            AbstractDungeon.isAscensionMode = this.isAscensionMode;
            if (!this.isAscensionMode) {
              AbstractDungeon.ascensionLevel = 0;
            } else {
              AbstractDungeon.ascensionLevel = this.ascensionLevel;
            }
            if (this.currentSeed.isEmpty())
            {
              long sourceTime = System.nanoTime();
              Random rng = new Random(Long.valueOf(sourceTime));
              Settings.seed = Long.valueOf(SeedHelper.generateUnoffensiveSeed(rng));
            }
            AbstractDungeon.generateSeeds();

            ChronoCustoms.gameMode = ChronoCustoms.mode.Versus;
            NetworkHelper.sendData(NetworkHelper.dataType.Start);
            // NetworkHelper.matcher.leaveLobby();
        }
    }

    private void updateCharacterButtons()
    {
      for (int i = 0; i < this.options.size(); i++) {
        ((CustomModeCharacterButton)this.options.get(i)).update((Settings.WIDTH / 2.0F) + i * 100.0F * Settings.scale - 200.0F * Settings.scale, Settings.HEIGHT - 200.0F * Settings.scale);
      }
    }
    
    @SpirePatch(
        clz=CustomModeCharacterButton.class,
        method="updateHitbox"
    )
    public static class updateHitboxCharButtons
    {

        @SpireInsertPatch(
            rloc=16,
            localvars={}
        )
        public static void Insert(CustomModeCharacterButton __instance)
        {
            NewMenuButtons.versusScreen.deselectOtherOptions(__instance);
        }
    }

    public void deselectOtherOptions(CustomModeCharacterButton characterOption)
    {
      for (CustomModeCharacterButton o : this.options) {
        if (o != characterOption) {
          o.selected = false;
        }
      }
    }

    private void updateSeed()
    {
      this.seedHb.move(580.0F * Settings.scale, 320.0F * Settings.scale);
      this.seedHb.update();
      if (this.seedHb.justHovered) {
        playHoverSound();
      }
      if ((this.seedHb.hovered) && (InputHelper.justClickedLeft)) {
        this.seedHb.clickStarted = true;
      }
      if ((this.seedHb.clicked) || ((CInputActionSet.select.isJustPressed()) && (this.seedHb.hovered)))
      {
        this.seedHb.clicked = false;
        if (Settings.seed == null) {
          Settings.seed = Long.valueOf(0L);
        }
        this.seedPanel.show(Enum.VERSUS_LOBBY);
      }
    }
  
    private void updateAscension()
    {
      this.ascLeftHb.move(300.0F * Settings.scale - ASC_RIGHT_W * 0.5F + 405.0F * Settings.scale, Settings.HEIGHT - 480.0F * Settings.scale);
      
      this.ascRightHb.move(300.0F * Settings.scale + ASC_RIGHT_W * 1.5F + 250.0F * Settings.scale, Settings.HEIGHT - 480.0F * Settings.scale);
      
      this.ascensionModeHb.move(430.0F * Settings.scale, Settings.HEIGHT - 480.0F * Settings.scale);
      
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
      }
      else if ((this.ascLeftHb.clicked) || (CInputActionSet.pageLeftViewDeck.isJustPressed()))
      {
        playClickFinishSound();
        this.ascLeftHb.clicked = false;
        this.ascensionLevel -= 1;
        if (this.ascensionLevel < 1) {
          this.ascensionLevel = 20;
        }
      }
      else if ((this.ascRightHb.clicked) || (CInputActionSet.pageRightViewExhaust.isJustPressed()))
      {
        playClickFinishSound();
        this.ascRightHb.clicked = false;
        this.ascensionLevel += 1;
        if (this.ascensionLevel > 20) {
          this.ascensionLevel = 1;
        }
        this.isAscensionMode = true;
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
        FontHelper.renderFontCentered(sb, FontHelper.SCP_cardTitleFont_small, "Test Screen",
            Settings.WIDTH / 2.0f,
            Settings.HEIGHT - 70.0f * Settings.scale,
            Settings.GOLD_COLOR);

        this.button.render(sb);
        this.confirmButton.render(sb);

        for (CustomModeCharacterButton o : this.options) {
            o.render(sb);
        }
        renderAscension(sb);
        renderSeed(sb);

        this.seedPanel.render(sb);
    }

    private void renderAscension(SpriteBatch sb) {
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
          FontHelper.renderFontCentered(sb, FontHelper.charDescFont, TEXT[4] + this.ascensionLevel, 300.0F * Settings.scale + 240.0F * Settings.scale, Settings.HEIGHT - 480.0F * Settings.scale, Color.CYAN);
        } else {
          FontHelper.renderFontCentered(sb, FontHelper.charDescFont, TEXT[4] + this.ascensionLevel, 300.0F * Settings.scale + 240.0F * Settings.scale, Settings.HEIGHT - 480.0F * Settings.scale, Settings.BLUE_TEXT_COLOR);
        }
        if (this.isAscensionMode) {
          sb.draw(ImageMaster.TICK, this.ascensionModeHb.cX - 32.0F, this.ascensionModeHb.cY - 32.0F, 32.0F, 32.0F, 64.0F, 64.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 64, 64, false, false);
        }
        if (this.ascensionLevel != 0) {
          FontHelper.renderSmartText(sb, FontHelper.charDescFont, CardCrawlGame.mainMenuScreen.charSelectScreen.ascLevelInfoString = CharacterSelectScreen.A_TEXT[(this.ascensionLevel - 1)], 300.0F * Settings.scale + 475.0F * Settings.scale, this.ascensionModeHb.cY + 10.0F * Settings.scale, 9999.0F, 32.0F * Settings.scale, Settings.CREAM_COLOR);
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

    private void renderSeed(SpriteBatch sb) {
        if (this.seedHb.hovered) {
          FontHelper.renderSmartText(sb, FontHelper.panelNameFont, TEXT[8] + ": " + this.currentSeed, 300.0F * Settings.scale + 96.0F * Settings.scale, this.seedHb.cY, 9999.0F, 32.0F * Settings.scale, Settings.GREEN_TEXT_COLOR);
        } else {
          FontHelper.renderSmartText(sb, FontHelper.speech_font, TEXT[8] + ": " + this.currentSeed, 300.0F * Settings.scale + 96.0F * Settings.scale, this.seedHb.cY, 9999.0F, 32.0F * Settings.scale, Settings.BLUE_TEXT_COLOR);
        }
        this.seedHb.render(sb);
    }

    private void drawRect(SpriteBatch sb, float x, float y, float width, float height, float thickness) {
        sb.draw(ImageMaster.WHITE_SQUARE_IMG, x, y, width, thickness);
        sb.draw(ImageMaster.WHITE_SQUARE_IMG, x, y, thickness, height);
        sb.draw(ImageMaster.WHITE_SQUARE_IMG, x, y+height-thickness, width, thickness);
        sb.draw(ImageMaster.WHITE_SQUARE_IMG, x+width-thickness, y, thickness, height);
    }
}