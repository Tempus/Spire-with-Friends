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

public class CharacterSelectWidget
{
    // UI strings
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString("CustomModeScreen");
    public static final String[] TEXT = uiStrings.TEXT;

    // Position
    public float x;
    public float y;

    // Characters
    public ArrayList<CustomModeCharacterButton> options = new ArrayList();

    public CharacterSelectWidget() {
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
          ((CustomModeCharacterButton)this.options.get(i)).move(x + i * 100.0F * Settings.scale, y);
        }
        ((CustomModeCharacterButton)this.options.get(0)).hb.clicked = true;
    }

    public void move(float x, float y) {
      this.x = x;
      this.y = y;
    }

    public void update() {
      for (int i = 0; i < this.options.size(); i++) {
        ((CustomModeCharacterButton)this.options.get(i)).update(x + i * 100.0F * Settings.scale, y);
      }
    }
    
    // Special code to bypass hardcoded deselect
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
            NewMenuButtons.newGameScreen.characterSelectWidget.deselectOtherOptions(__instance);
        }
    }

    public AbstractPlayer.PlayerClass getChosenClass() {
        for (CustomModeCharacterButton b : this.options) {
          if (b.selected)
          {
            return b.c.chosenClass;
          }
        }
    }

    public int getChosenOption() {
        for (CustomModeCharacterButton b : this.options) {
          if (b.selected)
          {
            return this.options.indexOf(b);
          }
        }
    }

    public void selectOption(int Index) {
      i = 0;
      for (CustomModeCharacterButton o : this.options) {
        if (i == Index) {
          o.selected = true;
        } else {
          o.selected = false;
        }
        i++;
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

    public void select(String character) {
      NetworkHelper.sendData(NetworkHelper.dataType.Rules);
      for (CustomModeCharacterButton o : this.options) {
        if (o.c.getCharacterString() == character) {
          o.selected = true;
        } else {
          o.selected = false;
        }
      }
    }

    public void render(SpriteBatch sb) {
        for (CustomModeCharacterButton o : this.options) {
            o.render(sb);
        }
    }
}