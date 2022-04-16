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
import java.util.*;
import static java.util.Comparator.comparing;

import chronoMods.*;
import chronoMods.network.steam.*;
import chronoMods.network.*;
import chronoMods.ui.deathScreen.*;
import chronoMods.ui.hud.*;
import chronoMods.ui.lobby.*;
import chronoMods.ui.mainMenu.*;
import chronoMods.utilities.*;

import com.codedisaster.steamworks.*;
import com.megacrit.cardcrawl.integrations.steam.SteamIntegration;
import basemod.*;
import com.codedisaster.steamworks.SteamMatchmaking;

public class CharacterSelectWidget
{
    // UI strings
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString("CustomModeScreen");
    public static final String[] TEXT = uiStrings.TEXT;

    // Position
    public float x = 1400f * Settings.scale;
    public float y = 700f  * Settings.scale;

    // Characters
    public ArrayList<CustomModeCharacterButton> options = new ArrayList();

    public class CustomComparator implements Comparator<CustomModeCharacterButton> {
        @Override
        public int compare(CustomModeCharacterButton a, CustomModeCharacterButton b) {
            return a.c.name.compareTo(b.c.name);
        }
    }
   
    public CharacterSelectWidget() {
        this.options.clear();
        for (AbstractPlayer p : CardCrawlGame.characterManager.getAllCharacters())
          this.options.add(new CustomModeCharacterButton(p, false));

        // this.options.add(new CustomModeCharacterButton(CardCrawlGame.characterManager
        //   .recreateCharacter(AbstractPlayer.PlayerClass.IRONCLAD), false));
        
        // this.options.add(new CustomModeCharacterButton(CardCrawlGame.characterManager
        //   .recreateCharacter(AbstractPlayer.PlayerClass.THE_SILENT), false));

        // this.options.add(new CustomModeCharacterButton(CardCrawlGame.characterManager
        //   .recreateCharacter(AbstractPlayer.PlayerClass.DEFECT), false));

        // this.options.add(new CustomModeCharacterButton(CardCrawlGame.characterManager
        //   .recreateCharacter(AbstractPlayer.PlayerClass.WATCHER), false));
        
        // Modded character select
        // this.options.addAll(Collections.sort(BaseMod.generateCustomCharacterOptions(), (CustomModeCharacterButton o1, CustomModeCharacterButton o2) -> { return o1.c.class.getName().compareTo(o2.c.class.getName()); } ));
        // ArrayList<CustomModeCharacterButton> custom = BaseMod.generateCustomCharacterOptions();
        // custom.sort(new CustomComparator());
        // this.options.addAll(custom);

        int count = this.options.size();
        for (int i = 0; i < count; i++) {
          ((CustomModeCharacterButton)this.options.get(i)).move(x + (i%4) * 100.0F * Settings.scale, y + ((int)(i/4)) * 100.0F * Settings.scale);
        }
        selectOption(0);
    }

    public void move(float x, float y) {
      this.x = x;
      this.y = y;
    }

    public void update() {
      for (int i = 0; i < this.options.size(); i++) {
        ((CustomModeCharacterButton)this.options.get(i)).update(x + (i%4) * 100.0F * Settings.scale, y + ((int)(i/4)) * 100.0F * Settings.scale);
      }
    }
    
    // Special code to bypass hardcoded deselect
    @SpirePatch(clz=CustomModeCharacterButton.class,method="updateHitbox")
    public static class updateHitboxCharButtons {
        @SpireInsertPatch(rloc=16,localvars={})
        public static void Insert(CustomModeCharacterButton __instance) {
            if (TogetherManager.gameMode == TogetherManager.mode.Bingo) {
              if (NewDeathScreenPatches.EndScreenBase != null && NewDeathScreenPatches.EndScreenBase instanceof EndScreenBingoLoss)
                ((EndScreenBingoLoss)NewDeathScreenPatches.EndScreenBase).characterSelectWidget.deselectOtherOptions(__instance);
              if (!CardCrawlGame.isInARun())
                NewMenuButtons.newGameScreen.characterSelectWidget.deselectOtherOptions(__instance);
            } else {
              NewMenuButtons.newGameScreen.characterSelectWidget.deselectOtherOptions(__instance);
              NetworkHelper.sendData(NetworkHelper.dataType.Rules);
              if (TogetherManager.gameMode == TogetherManager.mode.Coop)
                NetworkHelper.sendData(NetworkHelper.dataType.Character);
            }
        }
    }

    public AbstractPlayer.PlayerClass getChosenClass() {
        for (CustomModeCharacterButton b : this.options) {
          if (b.selected)
          {
            TogetherManager.logger.info("Chosen Class is: " + b.c.chosenClass.toString());
            return b.c.chosenClass;
          }
        }

        // Fallback for no one selected
        selectOption(0);
        return this.options.get(0).c.chosenClass;
    }

    public int getChosenOption() {
        for (CustomModeCharacterButton b : this.options) {
          if (b.selected)
          {
            return this.options.indexOf(b);
          }
        }

        selectOption(0);
        return 0;
    }

    public String getChosenOptionName() {
        int i = 0;
        for (CustomModeCharacterButton b : this.options) {
          i++;
          if (b.selected)
          {
            if (i < 4)
              return b.c.getLeaderboardCharacterName();
            return b.c.getLocalizedCharacterName();
          }
        }

        selectOption(0);
        return this.options.get(0).c.getLeaderboardCharacterName();
    }

    public String getChosenOptionLocalizedName() {
        for (CustomModeCharacterButton b : this.options) {
          if (b.selected)
          {
            return b.c.getLocalizedCharacterName();
          }
        }

        return this.options.get(0).c.getLocalizedCharacterName();
    }

    public void selectOption(int Index) {
      TogetherManager.log("Selecting Index: " + Index);
      int i = 0;
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
      TogetherManager.log("deselectOtherOptions: " + characterOption);
      for (CustomModeCharacterButton o : this.options) {
        if (o != characterOption) {
          o.selected = false;
        }
      }
    }

    public void select(String character) {
      TogetherManager.log("Selecting Character: " + character);
      for (CustomModeCharacterButton o : this.options) {
        if (o.c.getCharacterString().NAMES[0] == character) {
          o.selected = true;
        } else {
          o.selected = false;
        }
      }
    }

    public void selectClass(AbstractPlayer.PlayerClass character) {
      for (CustomModeCharacterButton o : this.options) {
        if (o.c.chosenClass == character) {
          o.selected = true;
          TogetherManager.log("Class selected: " + character);
        } else {
          o.selected = false;
        }
      }
    }

    public void render(SpriteBatch sb) {
        for (CustomModeCharacterButton o : this.options) {
            if (ReflectionHacks.getPrivate(o, CustomModeCharacterButton.class, "buttonImg") == null) {
              ReflectionHacks.setPrivate(o, CustomModeCharacterButton.class, "buttonImg", o.c.getCustomModeCharacterButtonImage());
            }
            try {
                o.render(sb);
            } catch (Exception e) {}
        }
    }
}