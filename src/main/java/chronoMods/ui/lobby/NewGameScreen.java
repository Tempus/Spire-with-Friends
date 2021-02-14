package chronoMods.ui.lobby;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
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
import com.megacrit.cardcrawl.helpers.*;
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

    // Buttons
    public MenuCancelButton button = new MenuCancelButton();
    public GridSelectConfirmButton confirmButton = new GridSelectConfirmButton(CharacterSelectScreen.TEXT[1]);

    // Characters
    public CharacterSelectWidget characterSelectWidget = new CharacterSelectWidget();

    // Ascension Selection
    public AscensionSelectWidget ascensionSelectWidget = new AscensionSelectWidget();

    // Player Panel
    public PlayerListWidget playerList = new PlayerListWidget("Ready");

    // Seed Selection
    public SeedSelectWidget seedSelectWidget = new SeedSelectWidget();


    private static final float TOGGLE_X_LEFT = 1400f * Settings.xScale;
    private static final float TOOLTIP_X_OFFSET = 1.03f;
    private static final float TOOLTIP_Y_OFFSET = 50.0F * Settings.scale;


    // Act 4 Selection
    public ToggleWidget heartToggle;

    // Neow Bonus Selection
    public ToggleWidget neowToggle;

    // Ironman Selection
    public ToggleWidget ironmanToggle;

    // Private Game Toggle
    public ToggleWidget privateToggle;

    // Kick holder
    public static RemotePlayer kick;


    public NewGameScreen() {
        characterSelectWidget.move(TOGGLE_X_LEFT, Settings.HEIGHT * 0.65f);     // 780y 
        ascensionSelectWidget.move(TOGGLE_X_LEFT, Settings.HEIGHT * 0.5625f);   // 675y
        seedSelectWidget.move(TOGGLE_X_LEFT, Settings.HEIGHT * 0.458f);         // 550y
        playerList.move(Settings.WIDTH / 2.0F, Settings.HEIGHT * 0.6875f);      // -375y

        heartToggle     = new ToggleWidget(TOGGLE_X_LEFT, Settings.HEIGHT * 0.395f, "Heart Run", Settings.isFinalActAvailable);  //475y
        neowToggle      = new ToggleWidget(TOGGLE_X_LEFT, Settings.HEIGHT * 0.333f, "Neow Bonus", Settings.isTrial);             //400y
        ironmanToggle   = new ToggleWidget(TOGGLE_X_LEFT, Settings.HEIGHT * 0.270f, "Ironman", NewDeathScreenPatches.Ironman);   //325y

        privateToggle   = new ToggleWidget(Settings.WIDTH - 256.0F * Settings.scale, 48.0F * Settings.scale, "Private", false);
    }

    public void open() {
        // Screen Swap
        CardCrawlGame.mainMenuScreen.darken();
        CardCrawlGame.mainMenuScreen.screen = Enum.CREATEMULTIPLAYERGAME;

        // Buttons
        button.show(PatchNotesScreen.TEXT[0]);
        this.confirmButton.show();
        this.confirmButton.isDisabled = false;

        // Seed
        long sourceTime = System.nanoTime();
        Random rng = new Random(Long.valueOf(sourceTime));
        Settings.seed = Long.valueOf(SeedHelper.generateUnoffensiveSeed(rng));
        
        Settings.specialSeed = null;

        // Steam Stuff
        NetworkHelper.createLobby();

        // Populate the player list
        for (RemotePlayer player : TogetherManager.players) {
          player.ready = false;
        }
        playerList.setPlayers(TogetherManager.players);
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
    }

    public void update() {
        // Return to the Main Menu
        button.update();
        if (button.hb.clicked || InputHelper.pressedEscape) {
            button.hb.clicked = false;
            InputHelper.pressedEscape = false;
            backToMenu();
        }

        // Update the selectable options (but only if you're the owner)
        if (TogetherManager.currentLobby != null && TogetherManager.currentLobby.isOwner()) {
            this.confirmButton.show();
            this.confirmButton.isDisabled = false;

            characterSelectWidget.update();
            ascensionSelectWidget.update();
            seedSelectWidget.update();

            if (heartToggle.update())   { NetworkHelper.sendData(NetworkHelper.dataType.Rules); }
            if (neowToggle.update())    { NetworkHelper.sendData(NetworkHelper.dataType.Rules); }
            if (privateToggle.update()) { NetworkHelper.setLobbyPrivate(privateToggle.isTicked()); }

            if (this.heartToggle.hb.hovered) {
                TipHelper.renderGenericTip(this.heartToggle.hb.cX * TOOLTIP_X_OFFSET, this.heartToggle.hb.cY + TOOLTIP_Y_OFFSET, "Heart Run", "This speedrun will finish with an Act 4 Heart kill. Disabling this finishes the run after Act 3."); }
            if (this.neowToggle.hb.hovered) {
                TipHelper.renderGenericTip(this.neowToggle.hb.cX * TOOLTIP_X_OFFSET, this.neowToggle.hb.cY + TOOLTIP_Y_OFFSET, "Neow Bonus", "The run begins with a 4 option choice from Neow. Disabling it skips the choice."); }
            if (this.privateToggle.hb.hovered) {
                TipHelper.renderGenericTip(this.privateToggle.hb.cX * 0.85f, this.privateToggle.hb.cY + TOOLTIP_Y_OFFSET + 48f, "Private", "Changes this to a private lobby. NL #rInvites #rand #rfriend #rjoins #rcoming #rsoon."); }

            if (TogetherManager.gameMode == TogetherManager.mode.Versus) {
                if (ironmanToggle.update()) { NetworkHelper.sendData(NetworkHelper.dataType.Rules); }
                if (this.ironmanToggle.hb.hovered) {
                    TipHelper.renderGenericTip(this.ironmanToggle.hb.cX * TOOLTIP_X_OFFSET, this.ironmanToggle.hb.cY + TOOLTIP_Y_OFFSET, "Ironman", "No retries are allowed this run. When disabled, dying will reset players to the start without reseting their clock."); }
            }
        } else if (TogetherManager.currentLobby != null && TogetherManager.gameMode == TogetherManager.mode.Coop) {
            characterSelectWidget.update();
        }
        playerList.update();

        // Update Embark Button
        confirmButton.isDisabled = false;
        for (RemotePlayer player : TogetherManager.players) {
          if (!player.ready) {
            confirmButton.isDisabled = true;
          }
        }
        updateEmbarkButton();
        seedSelectWidget.currentSeed = SeedHelper.getUserFacingSeedString();

        // Ready or Unready the player
        if (playerList.clicked) {
          playerList.toggleReadyState();
          if (playerList.joinButton.buttonText == "Ready") {
            playerList.joinButton.updateText("Unready");
          } else {
            playerList.joinButton.updateText("Ready");
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

    public void embark() {
        Settings.isFinalActAvailable = heartToggle.isTicked();
        Settings.isTrial = !neowToggle.isTicked();
        NewDeathScreenPatches.Ironman = ironmanToggle.isTicked();

        TogetherManager.logger.info("heart: " + Settings.isFinalActAvailable);
        TogetherManager.logger.info("neow: " + Settings.isTrial);
        TogetherManager.logger.info("iron: " + NewDeathScreenPatches.Ironman);

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

        if (TogetherManager.currentLobby != null && TogetherManager.currentLobby.isOwner()) {
            NetworkHelper.matcher.setLobbyJoinable(TogetherManager.currentLobby.steamID, false);
        }
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
        if (TogetherManager.gameMode == TogetherManager.mode.Versus)
            ironmanToggle.render(sb);
        ShaderHelper.setShader(sb, ShaderHelper.Shader.DEFAULT);
    }
}