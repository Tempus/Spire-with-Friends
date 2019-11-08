package chronoMods.ui.lobby;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen;
import com.megacrit.cardcrawl.screens.mainMenu.MenuCancelButton;
import com.megacrit.cardcrawl.screens.mainMenu.PatchNotesScreen;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;

import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.SeedHelper;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.screens.charSelect.CharacterSelectScreen;
import com.megacrit.cardcrawl.screens.custom.CustomModeCharacterButton;
import com.megacrit.cardcrawl.ui.buttons.GridSelectConfirmButton;
import com.megacrit.cardcrawl.ui.panels.SeedPanel;

import java.util.ArrayList;
import java.util.Map;

import chronoMods.*;
import chronoMods.steam.*;
import chronoMods.ui.deathScreen.*;
import chronoMods.ui.hud.*;
import chronoMods.ui.lobby.*;
import chronoMods.ui.mainMenu.*;
import chronoMods.utilities.*;

import com.codedisaster.steamworks.*;
import com.megacrit.cardcrawl.integrations.steam.SteamIntegration;
import basemod.ReflectionHacks;
import com.codedisaster.steamworks.SteamMatchmaking;

public class SeedSelectWidget
{
    // UI strings
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString("CustomModeScreen");
    public static final String[] TEXT = uiStrings.TEXT;

    // Seed Selection
    public Hitbox seedHb = new Hitbox(400.0F * Settings.scale, 90.0F * Settings.scale);
    public String currentSeed;
    public SeedPanel seedPanel = new SeedPanel();

    // Position
    public float x;
    public float y;

    public void move(float x, float y) {
      this.x = x - 16f * Settings.scale;
      this.y = y;
    }

    public void update()
    {
      this.seedPanel.update();

      this.seedHb.move(this.x + this.seedHb.width / 2f, this.y);
      this.seedHb.update();
      if (this.seedHb.justHovered) {
        CardCrawlGame.sound.playV("UI_HOVER", 0.75F);
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
        this.seedPanel.show(NewGameScreen.Enum.CREATEMULTIPLAYERGAME);
      }
    }

    public void render(SpriteBatch sb) {
        if (this.seedHb.hovered) {
          FontHelper.renderSmartText(sb, FontHelper.panelNameFont, TEXT[8] + ": " + this.currentSeed, this.x, this.y, 9999.0F, 32.0F * Settings.scale, Settings.GREEN_TEXT_COLOR);
        } else {
          FontHelper.renderSmartText(sb, FontHelper.speech_font, TEXT[8] + ": " + this.currentSeed, this.x, this.y, 9999.0F, 32.0F * Settings.scale, Settings.BLUE_TEXT_COLOR);
        }
        this.seedHb.render(sb);
        this.seedPanel.render(sb);
    }
}