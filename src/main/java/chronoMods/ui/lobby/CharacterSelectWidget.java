package chronoMods.ui.lobby;

import basemod.ReflectionHacks;
import chronoMods.TogetherManager;
import chronoMods.network.NetworkHelper;
import chronoMods.ui.deathScreen.EndScreenBingoLoss;
import chronoMods.ui.deathScreen.NewDeathScreenPatches;
import chronoMods.ui.mainMenu.NewMenuButtons;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.screens.custom.CustomModeCharacterButton;
import com.megacrit.cardcrawl.screens.mainMenu.ScrollBar;
import com.megacrit.cardcrawl.screens.mainMenu.ScrollBarListener;

import java.util.ArrayList;
import java.util.Comparator;

public class CharacterSelectWidget implements ScrollBarListener
{
    // UI strings
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString("CustomModeScreen");
    public static final String[] TEXT = uiStrings.TEXT;

    // Position
    public float x = 1400f * Settings.scale;
    public float y = 700f  * Settings.scale;

    // Characters
    public ArrayList<CustomModeCharacterButton> options = new ArrayList();
    private ScrollBar scrollbar;
    private int rowShown = 0;

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

        scrollbar = new ScrollBar(this, x + 400.0F * Settings.scale, y + 230.0F * Settings.scale, 350.0F * Settings.scale);
        scrollbar.parentScrolledToPercent(1.0F);
    }

    public void scrolledUsingBar(float percent) {
      scrollbar.parentScrolledToPercent(percent);
      updateRowShown(percent);
    }

    public void move(float x, float y) {
      this.x = x;
      this.y = y;
    }

    public void update() {
      scrollbar.update();
      for (int i = 0; i < this.options.size(); i++) {
        ((CustomModeCharacterButton)this.options.get(i)).update(x + (i%4) * 100.0F * Settings.scale, y + (((int)(i/4))-rowShown) * 100.0F * Settings.scale);
      }
    }

    public void updateRowShown(float scrolledPercent) {
      if (options.size() > 16) {
        int rowsOnTop = (int)Math.ceil((options.size() - 16) / 4) + 1;
        rowShown = (int)Math.round(rowsOnTop * (1.0F - scrolledPercent));
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
        for (int i = rowShown * 4; i < this.options.size(); i++) {
            if (i >= rowShown * 4 + 16)
              break;
            CustomModeCharacterButton o = ((CustomModeCharacterButton)this.options.get(i));
            if (ReflectionHacks.getPrivate(o, CustomModeCharacterButton.class, "buttonImg") == null) {
              ReflectionHacks.setPrivate(o, CustomModeCharacterButton.class, "buttonImg", o.c.getCustomModeCharacterButtonImage());
            }
            try {
              if (o.y >= y)
                o.render(sb);
            } catch (Exception e) {}
        }
        if (options.size() > 16)
          scrollbar.render(sb);
    }
}