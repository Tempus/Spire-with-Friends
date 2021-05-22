package chronoMods.ui.lobby;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
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
import com.megacrit.cardcrawl.helpers.*;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.neow.*;
import com.megacrit.cardcrawl.daily.mods.*;
import com.megacrit.cardcrawl.screens.charSelect.CharacterSelectScreen;
import com.megacrit.cardcrawl.screens.custom.*;
import com.megacrit.cardcrawl.ui.buttons.GridSelectConfirmButton;
import com.megacrit.cardcrawl.ui.panels.SeedPanel;

import java.util.ArrayList;
import java.util.Map;

import chronoMods.*;
import chronoMods.coop.drawable.*;
import chronoMods.network.steam.*;
import chronoMods.network.*;
import chronoMods.ui.deathScreen.*;
import chronoMods.ui.hud.*;
import chronoMods.ui.lobby.*;
import chronoMods.ui.mainMenu.*;
import chronoMods.utilities.*;

import com.codedisaster.steamworks.*;
import com.megacrit.cardcrawl.integrations.steam.SteamIntegration;
import basemod.ReflectionHacks;
import com.codedisaster.steamworks.SteamMatchmaking;

public class NewGameScreen
{
    public static class Enum
    {
        @SpireEnum
        public static MainMenuScreen.CurScreen CREATEMULTIPLAYERGAME;
    }

    // UI strings
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString("CustomModeScreen");
    public static final String[] TEXT = uiStrings.TEXT;

    public static final String[] LOBBY = CardCrawlGame.languagePack.getUIString("Lobby").TEXT;

    // Buttons
    public MenuCancelButton button = new MenuCancelButton();
    public GridSelectConfirmButton confirmButton = new GridSelectConfirmButton(CharacterSelectScreen.TEXT[1]);

    // Characters
    public CharacterSelectWidget characterSelectWidget = new CharacterSelectWidget();

    // Ascension Selection
    public AscensionSelectWidget ascensionSelectWidget = new AscensionSelectWidget();

    // Player Panel
    public PlayerListWidget playerList = new PlayerListWidget(LOBBY[17]);

    // Seed Selection
    public SeedSelectWidget seedSelectWidget = new SeedSelectWidget();


    private static final float TOGGLE_X_LEFT = 1400f * Settings.xScale;
    private static final float TOOLTIP_X_OFFSET = 1.03f;
    private static final float TOOLTIP_Y_OFFSET = 50.0F * Settings.scale;


    // Act 4 Selection
    public ToggleWidget heartToggle;

    // Neow Bonus Selection
    public ToggleWidget neowToggle;

    // Neow Bonus Selection
    public ToggleWidget lamentToggle;

    // Ironman Selection
    public ToggleWidget ironmanToggle;

    // Private Game Toggle
    public ToggleWidget privateToggle;

    // Custom Mode Button
    public Button customModeButton;

    // Kick holder
    public static RemotePlayer kick;

    public NewGameScreen() {
        characterSelectWidget.move(TOGGLE_X_LEFT, Settings.HEIGHT * 0.65f);     // 780y 
        ascensionSelectWidget.move(TOGGLE_X_LEFT, Settings.HEIGHT * 0.5625f);   // 675y
        seedSelectWidget.move(TOGGLE_X_LEFT, Settings.HEIGHT * 0.458f);         // 550y
        playerList.move(Settings.WIDTH / 2.0F, Settings.HEIGHT * 0.6875f);      // -375y

        heartToggle     = new ToggleWidget(TOGGLE_X_LEFT, Settings.HEIGHT * 0.395f, LOBBY[5], Settings.isFinalActAvailable);  //475y
        neowToggle      = new ToggleWidget(TOGGLE_X_LEFT, Settings.HEIGHT * 0.333f, LOBBY[7], Settings.isTrial);             //400y
        lamentToggle    = new ToggleWidget(TOGGLE_X_LEFT, Settings.HEIGHT * 0.270f, LOBBY[21], Settings.isTrial);             //400y
        ironmanToggle   = new ToggleWidget(TOGGLE_X_LEFT, Settings.HEIGHT * 0.208f, LOBBY[9], NewDeathScreenPatches.Ironman);   //325y

        privateToggle   = new ToggleWidget(Settings.WIDTH - 256.0F * Settings.scale, 48.0F * Settings.scale, LOBBY[19], false);

        customModeButton = new Button(64.0f, Settings.HEIGHT * 0.65f, LOBBY[23], ImageMaster.END_TURN_BUTTON);

        this.confirmButton.isDisabled = true;
    }

    public void open() {
        // Screen Swap
        CardCrawlGame.mainMenuScreen.darken();
        CardCrawlGame.mainMenuScreen.screen = Enum.CREATEMULTIPLAYERGAME;

        // Buttons
        button.show(PatchNotesScreen.TEXT[0]);
        this.confirmButton.show();
        this.confirmButton.isDisabled = true;

        // Seed
        long sourceTime = System.nanoTime();
        Random rng = new Random(Long.valueOf(sourceTime));
        Settings.seed = Long.valueOf(SeedHelper.generateUnoffensiveSeed(rng));
        
        Settings.specialSeed = null;

        // Steam Stuff
        NetworkHelper.createLobby(NetworkHelper.networks.get(NewMenuButtons.lobbyScreen.serviceToggle.index));

        // Populate the player list
        for (RemotePlayer player : TogetherManager.players) {
          player.ready = false;
        }
        playerList.setPlayers(TogetherManager.players);

        // TogetherManager.getCurrentUser().character = characterSelectWidget.getChosenOptionLocalizedName();
    }

    // Like open, but we'll make things look different, and we'll join an existing lobby instead of making a new one
    public void join() {
        // Screen Swap
        CardCrawlGame.mainMenuScreen.darken();
        CardCrawlGame.mainMenuScreen.screen = Enum.CREATEMULTIPLAYERGAME;

        // Buttons
        button.show(PatchNotesScreen.TEXT[0]);
        this.confirmButton.hide();

        // Seed
        Settings.seed = null;
        Settings.specialSeed = null;

        // Populate the player list
        for (RemotePlayer player : TogetherManager.players) {
          player.ready = false;
        }
        playerList.setPlayers(TogetherManager.players);

        if (TogetherManager.currentLobby.ascension == "0") {
          ascensionSelectWidget.isAscensionMode = false;
        } else {
          ascensionSelectWidget.isAscensionMode = true;
        }
        ascensionSelectWidget.ascensionLevel = Integer.parseInt(TogetherManager.currentLobby.ascension);
        characterSelectWidget.select(TogetherManager.currentLobby.character);

        // TogetherManager.getCurrentUser().character = characterSelectWidget.getChosenOptionLocalizedName();
        NetworkHelper.sendData(NetworkHelper.dataType.Version);
    }

    public void update() {
        // Return to the Main Menu
        button.update();
        if (button.hb.clicked || InputHelper.pressedEscape) {
            button.hb.clicked = false;
            InputHelper.pressedEscape = false;
            backToMenu();
        }

        playerList.update();

        // Update the selectable options (but only if you're the owner)
        if (TogetherManager.currentLobby != null && TogetherManager.currentLobby.isOwner()) {
            this.confirmButton.show();
            this.confirmButton.isDisabled = false;

            characterSelectWidget.update();
            ascensionSelectWidget.update();
            seedSelectWidget.update();

            if (Settings.isTrial) {
                neowToggle.setTicked(false);
                lamentToggle.setTicked(false);
            }

            if (heartToggle.update())   { NetworkHelper.sendData(NetworkHelper.dataType.Rules); }
            if (neowToggle.update())    { NetworkHelper.sendData(NetworkHelper.dataType.Rules); }
            if (privateToggle.update()) { NetworkHelper.setLobbyPrivate(privateToggle.isTicked()); }

            // Make sure Lament/Neow are clicked correctly.
            if (lamentToggle.isTicked()) { neowToggle.setTicked(true); }
            if (!neowToggle.isTicked())  { lamentToggle.setTicked(false); }

            if (this.heartToggle.hb.hovered) {
                TipHelper.renderGenericTip(this.heartToggle.hb.cX * TOOLTIP_X_OFFSET, this.heartToggle.hb.cY + TOOLTIP_Y_OFFSET, LOBBY[5], LOBBY[6]); }
            if (this.neowToggle.hb.hovered) {
                TipHelper.renderGenericTip(this.neowToggle.hb.cX * TOOLTIP_X_OFFSET, this.neowToggle.hb.cY + TOOLTIP_Y_OFFSET, LOBBY[7], LOBBY[8]); }
            if (this.privateToggle.hb.hovered) {
                TipHelper.renderGenericTip(this.privateToggle.hb.cX * 0.85f, this.privateToggle.hb.cY + TOOLTIP_Y_OFFSET + 48f, LOBBY[19], LOBBY[20]); }

            if (TogetherManager.gameMode == TogetherManager.mode.Versus) {
                if (lamentToggle.update()) { NetworkHelper.sendData(NetworkHelper.dataType.Rules); }
                if (this.lamentToggle.hb.hovered) {
                    TipHelper.renderGenericTip(this.lamentToggle.hb.cX * TOOLTIP_X_OFFSET, this.lamentToggle.hb.cY + TOOLTIP_Y_OFFSET, LOBBY[21], LOBBY[22]); }

                if (ironmanToggle.update()) { NetworkHelper.sendData(NetworkHelper.dataType.Rules); }
                if (this.ironmanToggle.hb.hovered) {
                    TipHelper.renderGenericTip(this.ironmanToggle.hb.cX * TOOLTIP_X_OFFSET, this.ironmanToggle.hb.cY + TOOLTIP_Y_OFFSET, LOBBY[9], LOBBY[10]); }
            }

            customModeButton.update();
            if (this.customModeButton.hb.clicked || CInputActionSet.proceed.isJustPressed()) {
                this.customModeButton.hb.clicked = false;
                NewMenuButtons.customScreen.open();
            }
            if (customModeButton.hb.hovered) {
                TipHelper.renderGenericTip(this.customModeButton.hb.cX + 320.0F * Settings.scale / 2f, this.customModeButton.hb.cY + TOOLTIP_Y_OFFSET, LOBBY[24], LOBBY[25]); }

        } else if (TogetherManager.currentLobby != null && TogetherManager.gameMode == TogetherManager.mode.Coop) {
            characterSelectWidget.update();
        }

        seedSelectWidget.currentSeed = SeedHelper.getUserFacingSeedString();

        // Update Embark Button
        confirmButton.isDisabled = false;
        for (RemotePlayer player : TogetherManager.players) {
            if (!player.ready)
                confirmButton.isDisabled = true;
        }
        updateEmbarkButton();

        // Ready or Unready the player
        if (playerList.clicked) {
          playerList.toggleReadyState();
          if (playerList.joinButton.buttonText.equals(LOBBY[17])) {
            playerList.joinButton.updateText(LOBBY[18]);
          } else {
            playerList.joinButton.updateText(LOBBY[17]);
          }
          NetworkHelper.sendData(NetworkHelper.dataType.Ready);
        }

        // Reset the click state
        InputHelper.justClickedLeft = false;
    }

    public void backToMenu() {
        CardCrawlGame.mainMenuScreen.screen = MainMenuScreen.CurScreen.MAIN_MENU;
        CardCrawlGame.mainMenuScreen.lighten();
        NetworkHelper.leaveLobby();
        button.hide();
        playerList.joinButton.updateText("Ready");
    }

    private void updateEmbarkButton()
    {
        this.confirmButton.update();
        if ((this.confirmButton.hb.clicked) || (CInputActionSet.proceed.isJustPressed()))
        {
            this.confirmButton.hb.clicked = false;

            NetworkHelper.sendData(NetworkHelper.dataType.Rules);
            NetworkHelper.sendData(NetworkHelper.dataType.Start);
        }
    }

    // Special patch for Lament starts in Versus
    @SpirePatch(clz = NeowEvent.class, method="buttonEffect")
    public static class NeowGivesLament {
        public static void Prefix(NeowEvent __instance, int buttonPressed, @ByRef int[] ___bossCount) {
            if (TogetherManager.gameMode != TogetherManager.mode.Versus) { return; }

            ___bossCount[0] = 0;
        }
    }

    public void embark() {
        
        // Colour reset in case of many part/joins
        int i = 0;
        for (RemotePlayer player : TogetherManager.players) {
          player.setColour(RemotePlayer.colourChoices[i%(RemotePlayer.colourChoices.length-1)]);

          if (TogetherManager.gameMode == TogetherManager.mode.Coop)
            player.createMapDrawables();
          i++;
        }

        Settings.isFinalActAvailable = heartToggle.isTicked();
        Settings.isTrial = !neowToggle.isTicked();
        Settings.isTestingNeow = !lamentToggle.isTicked();
        NewDeathScreenPatches.Ironman = ironmanToggle.isTicked();

        TogetherManager.log("heart: " + Settings.isFinalActAvailable);
        TogetherManager.log("neow: " + Settings.isTrial);
        TogetherManager.log("iron: " + NewDeathScreenPatches.Ironman);

        // True, true, false is nothing, and occurs when the first toggle only is set
        // false, false, false is heart and neow, and occurs when the second toggle only is set

        CardCrawlGame.chosenCharacter = characterSelectWidget.getChosenClass();
        CardCrawlGame.mainMenuScreen.isFadingOut = true;
        CardCrawlGame.mainMenuScreen.fadeOutMusic();

        AbstractDungeon.isAscensionMode = ascensionSelectWidget.isAscensionMode;
        if (!ascensionSelectWidget.isAscensionMode) {
          AbstractDungeon.ascensionLevel = 0;
        } else {
          AbstractDungeon.ascensionLevel = ascensionSelectWidget.ascensionLevel;
        }

        AbstractDungeon.generateSeeds();
        Settings.seedSet = true;

        if (TogetherManager.currentLobby != null && TogetherManager.currentLobby.isOwner())
            TogetherManager.currentLobby.setJoinable(false);

        CardCrawlGame.mainMenuScreen.screen = MainMenuScreen.CurScreen.MAIN_MENU;
        TopPanelPlayerPanels.SortWidgets();      
    }

    public void render(SpriteBatch sb) {
        FontHelper.renderFontCentered(sb, FontHelper.SCP_cardTitleFont_small, "Lobby",
            Settings.WIDTH / 2.0f,
            Settings.HEIGHT - 70.0f * Settings.scale,
            Settings.GOLD_COLOR);

        this.button.render(sb);
        this.confirmButton.render(sb);

        playerList.render(sb);
        seedSelectWidget.render(sb);

        if (TogetherManager.currentLobby != null && TogetherManager.gameMode != TogetherManager.mode.Coop && !TogetherManager.currentLobby.isOwner())
            ShaderHelper.setShader(sb, ShaderHelper.Shader.GRAYSCALE); 

        characterSelectWidget.render(sb);

        if (TogetherManager.currentLobby != null && !TogetherManager.currentLobby.isOwner())
            ShaderHelper.setShader(sb, ShaderHelper.Shader.GRAYSCALE); 

        ascensionSelectWidget.render(sb);
        heartToggle.render(sb);
        neowToggle.render(sb);
        privateToggle.render(sb);
        if (TogetherManager.gameMode == TogetherManager.mode.Versus) {
            ironmanToggle.render(sb);
            lamentToggle.render(sb);
        }
        customModeButton.render(sb);
        ShaderHelper.setShader(sb, ShaderHelper.Shader.DEFAULT);

        // Render the selected daily mods
        int i = 1;
        for (CustomMod cm : NewMenuButtons.customScreen.modList) {
            if (!cm.selected) { continue; }
            String mID = cm.ID;
            AbstractDailyMod m = ModHelper.getMod(mID);
            i++;

            float height = 48f * Settings.scale;
            if (!cm.isDailyMod) {
            // There's a whole buncha bullshit honestly
                if (mID.equals("Daily Mods")) {
                    sb.draw(TogetherManager.cusTexDaily, 64.0f - 48.0F * Settings.scale, Settings.HEIGHT * 0.65f - height * i - 32.0F * Settings.scale, 32.0F, 32.0F, 64.0F, 64.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 64, 64, false, false);
                } else if (mID.equals("Praise Snecko")) {
                    sb.draw(TogetherManager.cusTexSnecko, 64.0f - 48.0F * Settings.scale, Settings.HEIGHT * 0.65f - height * i - 32.0F * Settings.scale, 32.0F, 32.0F, 64.0F, 64.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 128, 128, false, false);
                } else if (mID.equals("Inception")) {
                    sb.draw(TogetherManager.cusTexIncept, 64.0f - 48.0F * Settings.scale, Settings.HEIGHT * 0.65f - height * i - 32.0F * Settings.scale, 32.0F, 32.0F, 64.0F, 64.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 128, 128, false, false);
                } else if (mID.equals("My True Form")) {
                    sb.draw(TogetherManager.cusTexForm, 64.0f - 48.0F * Settings.scale, Settings.HEIGHT * 0.65f - height * i - 32.0F * Settings.scale, 32.0F, 32.0F, 64.0F, 64.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 64, 64, false, false);
                } else if (mID.equals("One Hit Wonder")) {
                    sb.draw(TogetherManager.cusTexWonder, 64.0f - 48.0F * Settings.scale, Settings.HEIGHT * 0.65f - height * i - 32.0F * Settings.scale, 32.0F, 32.0F, 64.0F, 64.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 64, 64, false, false);
                } else if (mID.equals("Starter Deck")) {
                    sb.draw(TogetherManager.cusTexStarter, 64.0f - 48.0F * Settings.scale, Settings.HEIGHT * 0.65f - height * i - 32.0F * Settings.scale, 32.0F, 32.0F, 64.0F, 64.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 64, 64, false, false);
                }
            } else {
                sb.draw(m.img, 64.0f - 48.0F * Settings.scale, Settings.HEIGHT * 0.65f - height * i - 32.0F * Settings.scale, 32.0F, 32.0F, 64.0F, 64.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 64, 64, false, false);
            }
            FontHelper.renderFontLeft(sb, FontHelper.panelEndTurnFont, cm.name, 64.0f + 32.0F * Settings.scale, Settings.HEIGHT * 0.65f - height * i, Settings.CREAM_COLOR);
        }
    }
}