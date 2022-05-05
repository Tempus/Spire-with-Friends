package chronoMods.ui.lobby;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.*;
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
import com.megacrit.cardcrawl.screens.options.*;
import com.megacrit.cardcrawl.ui.buttons.GridSelectConfirmButton;
import com.megacrit.cardcrawl.ui.panels.SeedPanel;

import downfall.patches.EvilModeCharacterSelect;

import java.util.function.Function;
import java.util.stream.*;
import java.util.*;

import chronoMods.*;
import chronoMods.bingo.*;
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


public class NewGameScreen implements DropdownMenuListener
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
	public static final String[] BINGO = CardCrawlGame.languagePack.getUIString("BingoDifficulty").TEXT;

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


	private static final float TOGGLE_X_RIGHT = 1400f * Settings.xScale;
	private static final float TOGGLE_X_LEFT = 640.0F * Settings.xScale;
	
	private static final float TOOLTIP_X_OFFSET = 1.03f;
	private static final float TOOLTIP_Y_OFFSET = 50.0F * Settings.scale;

	public Color uiColor = new Color(1.0F, 0.965F, 0.886F, 0.0F);

	// Act 4 Selection
	public ToggleWidget heartToggle;

	// Neow Bonus Selection
	public ToggleWidget neowToggle;

	// Co-op Hard Mode Selection
	public ToggleWidget hardToggle;

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

	// Bingo Difficulty dropdown
	public DropdownMenu bingoDifficulty;

	// Bingo Team Selection
	public ToggleWidget teamsToggle;
	public ToggleWidget uniqueBoardToggle;

	// Bingo Team Name and Rename
	public RenamePopup renamePopup = new RenamePopup();
	public Hitbox renameHb;

	// Downfall Specific Toggle
	public ToggleWidget downfallToggle;


	public NewGameScreen() {
		characterSelectWidget.move(TOGGLE_X_RIGHT, Settings.HEIGHT * 0.65f);     // 780y 
		ascensionSelectWidget.move(TOGGLE_X_RIGHT, Settings.HEIGHT * 0.5625f);   // 675y
		seedSelectWidget.move(TOGGLE_X_RIGHT, Settings.HEIGHT * 0.458f);         // 550y
		playerList.move(Settings.WIDTH / 2.0F, Settings.HEIGHT * 0.6875f);      // -375y

		heartToggle     = new ToggleWidget(TOGGLE_X_RIGHT, Settings.HEIGHT * 0.395f, LOBBY[5], Settings.isFinalActAvailable);  //475y
		neowToggle      = new ToggleWidget(TOGGLE_X_RIGHT, Settings.HEIGHT * 0.333f, LOBBY[7], Settings.isTrial);             //400y
		hardToggle      = new ToggleWidget(TOGGLE_X_RIGHT, Settings.HEIGHT * 0.270f, LOBBY[33], false);				             //400y
		lamentToggle    = new ToggleWidget(TOGGLE_X_RIGHT, Settings.HEIGHT * 0.270f, LOBBY[21], Settings.isTrial);             //400y
		ironmanToggle   = new ToggleWidget(TOGGLE_X_RIGHT, Settings.HEIGHT * 0.208f, LOBBY[9], NewDeathScreenPatches.Ironman);   //325y
		downfallToggle  = new ToggleWidget(TOGGLE_X_RIGHT, Settings.HEIGHT * 0.146f, "Downfall", false);

		privateToggle   = new ToggleWidget(Settings.WIDTH - 256.0F * Settings.scale, 48.0F * Settings.scale, LOBBY[19], false);

		customModeButton = new Button(64.0f * Settings.xScale, Settings.HEIGHT * 0.65f, LOBBY[23], ImageMaster.END_TURN_BUTTON);

		bingoDifficulty   = new DropdownMenu(this, BINGO, FontHelper.tipBodyFont, Settings.CREAM_COLOR);    
		teamsToggle       = new ToggleWidget(TOGGLE_X_RIGHT, Settings.HEIGHT * 0.458f, LOBBY[26], false);
		uniqueBoardToggle = new ToggleWidget(TOGGLE_X_RIGHT, Settings.HEIGHT * 0.395f, LOBBY[28], false);

    	renameHb = new Hitbox(90.0F * Settings.scale, 90.0F * Settings.scale);
    	renameHb.move(TOGGLE_X_LEFT, Settings.HEIGHT * 0.65f);

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


		// Find the team with the fewest people
		TogetherManager.getCurrentUser().team = TogetherManager.players.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream().min((o1, o2) -> o1.getKey().team - o2.getKey().team)
                .map(Map.Entry::getKey).get().team;

        TogetherManager.log("Team: " + TogetherManager.getCurrentUser().team);

		NetworkHelper.sendData(NetworkHelper.dataType.TeamChange);

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

			// Exclusions if the bingo dropdowns are open
			if (TogetherManager.gameMode == TogetherManager.mode.Bingo) {
				if (bingoDifficulty.isOpen) {
					bingoDifficulty.update();
					return;
				} 
			}

			// Typical Buttons
			this.confirmButton.show();
			this.confirmButton.isDisabled = false;

			characterSelectWidget.update();
			ascensionSelectWidget.update();

			if (TogetherManager.gameMode != TogetherManager.mode.Bingo)
				seedSelectWidget.update();

			if (Settings.isTrial) {
				neowToggle.setTicked(false);
				lamentToggle.setTicked(false);
			}

			if (TogetherManager.gameMode != TogetherManager.mode.Bingo) {
				if (heartToggle.update())   { NetworkHelper.sendData(NetworkHelper.dataType.Rules); }
				if (neowToggle.update())    { NetworkHelper.sendData(NetworkHelper.dataType.Rules); }
				if (Loader.isModLoaded("downfall"))
					if (downfallToggle.update())
						{ NetworkHelper.sendData(NetworkHelper.dataType.Rules); }
			}

			if (TogetherManager.gameMode == TogetherManager.mode.Coop)
				if (hardToggle.update())   { NetworkHelper.sendData(NetworkHelper.dataType.Rules); }

			if (privateToggle.update()) { NetworkHelper.setLobbyPrivate(privateToggle.isTicked()); }

			// Bingo Updates
			if (TogetherManager.gameMode == TogetherManager.mode.Bingo) {
				bingoDifficulty.update();
				if (teamsToggle.update())   	  { NetworkHelper.sendData(NetworkHelper.dataType.BingoRules); }
				if (uniqueBoardToggle.update())   { NetworkHelper.sendData(NetworkHelper.dataType.BingoRules); }
			}

			// Make sure Lament/Neow are clicked correctly.
			if (lamentToggle.isTicked()) { neowToggle.setTicked(true); }
			if (!neowToggle.isTicked())  { lamentToggle.setTicked(false); }

			if (this.heartToggle.hb.hovered) {
				TipHelper.renderGenericTip(this.heartToggle.hb.cX * TOOLTIP_X_OFFSET, this.heartToggle.hb.cY + TOOLTIP_Y_OFFSET, LOBBY[5], LOBBY[6]); }
			if (this.neowToggle.hb.hovered) {
				TipHelper.renderGenericTip(this.neowToggle.hb.cX * TOOLTIP_X_OFFSET, this.neowToggle.hb.cY + TOOLTIP_Y_OFFSET, LOBBY[7], LOBBY[8]); }
			if (this.privateToggle.hb.hovered) {
				TipHelper.renderGenericTip(this.privateToggle.hb.cX * 0.85f, this.privateToggle.hb.cY + TOOLTIP_Y_OFFSET + 48f, LOBBY[19], LOBBY[20]); }
			if (this.hardToggle.hb.hovered) {
				TipHelper.renderGenericTip(this.hardToggle.hb.cX * TOOLTIP_X_OFFSET, this.hardToggle.hb.cY + TOOLTIP_Y_OFFSET + 48f, LOBBY[33], LOBBY[34]); }

			if (TogetherManager.gameMode == TogetherManager.mode.Versus) {
				if (lamentToggle.update()) { NetworkHelper.sendData(NetworkHelper.dataType.Rules); }
				if (this.lamentToggle.hb.hovered) {
					TipHelper.renderGenericTip(this.lamentToggle.hb.cX * TOOLTIP_X_OFFSET, this.lamentToggle.hb.cY + TOOLTIP_Y_OFFSET, LOBBY[21], LOBBY[22]); }

				if (ironmanToggle.update()) { NetworkHelper.sendData(NetworkHelper.dataType.Rules); }
				if (this.ironmanToggle.hb.hovered) {
					TipHelper.renderGenericTip(this.ironmanToggle.hb.cX * TOOLTIP_X_OFFSET, this.ironmanToggle.hb.cY + TOOLTIP_Y_OFFSET, LOBBY[9], LOBBY[10]); }
			}

			if (TogetherManager.gameMode == TogetherManager.mode.Bingo) {
				if (teamsToggle.hb.hovered)
					TipHelper.renderGenericTip(this.teamsToggle.hb.cX * TOOLTIP_X_OFFSET, this.teamsToggle.hb.cY + TOOLTIP_Y_OFFSET, LOBBY[26], LOBBY[27]); 
				if (uniqueBoardToggle.hb.hovered)
					TipHelper.renderGenericTip(this.uniqueBoardToggle.hb.cX * TOOLTIP_X_OFFSET, this.uniqueBoardToggle.hb.cY + TOOLTIP_Y_OFFSET, LOBBY[28], LOBBY[29]); 
				if (bingoDifficulty.getHitbox().hovered)
					TipHelper.renderGenericTip(this.bingoDifficulty.getHitbox().cX * TOOLTIP_X_OFFSET, this.bingoDifficulty.getHitbox().cY + TOOLTIP_Y_OFFSET, LOBBY[30], LOBBY[31]); 
			}

			customModeButton.update();
			if (this.customModeButton.hb.clicked || CInputActionSet.proceed.isJustPressed()) {
				this.customModeButton.hb.clicked = false;
				NewMenuButtons.customScreen.open();
			}
			if (customModeButton.hb.hovered) {
				TipHelper.renderGenericTip(this.customModeButton.hb.cX + 320.0F * Settings.scale / 2f, this.customModeButton.hb.cY + TOOLTIP_Y_OFFSET, LOBBY[24], LOBBY[25]); }

		} else if (TogetherManager.currentLobby != null && TogetherManager.gameMode != TogetherManager.mode.Versus) {
			characterSelectWidget.update();
		}

		seedSelectWidget.currentSeed = SeedHelper.getUserFacingSeedString();

		// Bingo Team Renaming
		if (TogetherManager.gameMode == TogetherManager.mode.Bingo && teamsToggle.isTicked()) {
			this.renameHb.update();

			if (this.renameHb.justHovered) {
				CardCrawlGame.sound.play("UI_HOVER");
			} else if (this.renameHb.hovered && InputHelper.justClickedLeft) {
				this.renameHb.clickStarted = true;
				CardCrawlGame.sound.play("UI_CLICK_1");
			} else if (this.renameHb.clicked) {
				this.renameHb.clicked = false;

				String teamName = TogetherManager.getCurrentUser().teamName;
				if (teamName.equals(""))
					teamName = "Team " + TogetherManager.getCurrentUser().team;

				this.renamePopup.open(teamName);
			} 

			this.renamePopup.update();
		}

		// Update Embark Button
		confirmButton.isDisabled = false;
		for (RemotePlayer player : TogetherManager.players) {
			if (!player.ready)
				confirmButton.isDisabled = true;
		}
		if (TogetherManager.players.size() == 0 || TogetherManager.currentLobby == null)
			confirmButton.isDisabled = true;
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

			if (TogetherManager.gameMode == TogetherManager.mode.Bingo)
				NetworkHelper.sendData(NetworkHelper.dataType.BingoRules);
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
		// Recreate Watcher for Hard Mode
		CardCrawlGame.characterManager.recreateCharacter(AbstractPlayer.PlayerClass.WATCHER);

		// Custom bingo images		
		if (TogetherManager.gameMode == TogetherManager.mode.Bingo && TogetherManager.customMark != null)
			NetworkHelper.sendData(NetworkHelper.dataType.CustomMark);

		// Colour reset in case of many part/joins
		int i = 0;
		for (RemotePlayer player : TogetherManager.players) {
		  player.setColour(RemotePlayer.colourChoices[i%(RemotePlayer.colourChoices.length-1)]);

		  if (TogetherManager.gameMode == TogetherManager.mode.Coop)
			player.createMapDrawables();
		  i++;
		}

		NetworkHelper.sendData(NetworkHelper.dataType.Character);

		Settings.isFinalActAvailable = heartToggle.isTicked();
		Settings.isTrial = !neowToggle.isTicked();
		Settings.isTestingNeow = !lamentToggle.isTicked();
		NewDeathScreenPatches.Ironman = ironmanToggle.isTicked();

		if (Loader.isModLoaded("downfall"))
			if (downfallToggle.isTicked())
				{ EvilModeCharacterSelect.evilMode = true; }

		// Bingo means heart and Neow are always on
		if (TogetherManager.gameMode == TogetherManager.mode.Bingo) {
			Settings.isFinalActAvailable = true;
			Settings.isTrial = false;
			Settings.isTestingNeow = true;
		}

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

		if (TogetherManager.gameMode != TogetherManager.mode.Bingo) {
			Settings.seedSet = true;
			AbstractDungeon.generateSeeds();
		} else {
			long sourceTime = System.nanoTime();
			Random rng = new Random(Long.valueOf(sourceTime));
			Settings.seedSourceTimestamp = sourceTime;
			Settings.seed = Long.valueOf(SeedHelper.generateUnoffensiveSeed(rng));
			Settings.seedSet = false;

			AbstractDungeon.generateSeeds();
		}

		if (TogetherManager.gameMode == TogetherManager.mode.Bingo) {
			switch (bingoDifficulty.getSelectedIndex()) {
				case 0:
					TogetherManager.getCurrentUser().bingoCardIndices = Caller.makeBingoCard(4,1,0);
					break;
				case 1:
					TogetherManager.getCurrentUser().bingoCardIndices = Caller.makeBingoCard(3,2,0);
					break;
				case 2:
					TogetherManager.getCurrentUser().bingoCardIndices = Caller.makeBingoCard(1,3,1);
					break;
				case 3:
					TogetherManager.getCurrentUser().bingoCardIndices = Caller.makeBingoCard(0,3,2);
					break;
				case 4:
					TogetherManager.getCurrentUser().bingoCardIndices = Caller.makeBingoCard(0,1,4);
					break;
				default:
					TogetherManager.getCurrentUser().bingoCardIndices = Caller.makeBingoCard(1,3,1);
					break;
			}
			
			if (teamsToggle.isTicked()) {
		  		TogetherManager.getCurrentUser().setColour(RemotePlayer.colourChoices[TogetherManager.getCurrentUser().team%(RemotePlayer.colourChoices.length-1)]);
			} else {
				// this is just so everyone is on a different team for victory conditions
				for (int j = 0; j < TogetherManager.players.size(); j++) {
					TogetherManager.players.get(j).team = j;
				}
			}

			NetworkHelper.sendData(NetworkHelper.dataType.BingoCard);
		}


		if (TogetherManager.currentLobby != null && TogetherManager.currentLobby.isOwner())
			TogetherManager.currentLobby.setJoinable(false);

		CardCrawlGame.mainMenuScreen.screen = MainMenuScreen.CurScreen.MAIN_MENU;


		Map<Integer, ArrayList<RemotePlayer>> teamMap = new HashMap();
		for (RemotePlayer p : TogetherManager.players) {
			// Bingo Widgets
	        if (TogetherManager.gameMode == TogetherManager.mode.Bingo) {
	        	if (teamsToggle.isTicked()) {
	        		// Do team aggregation into panels here
	        		TogetherManager.log("Adding " + p.userName + " to team " + p.team);
	        		teamMap.putIfAbsent(p.team, new ArrayList());
	        		teamMap.get(p.team).add(p);
	        	} 
	        	// Single player Bingo Widgets
	        	else {
		        	TopPanelPlayerPanels.playerWidgets.add(new BingoPlayerWidget(p));
		        }
	        }
	        // Normal Widgets
	        else {
	       		TopPanelPlayerPanels.playerWidgets.add(new RemotePlayerWidget(p));
	        }
	    }
        if (TogetherManager.gameMode == TogetherManager.mode.Bingo && teamsToggle.isTicked()) {

    		teamMap.forEach((id, teamList) -> { TogetherManager.log("Team " + id + " size " + teamList.size()); TopPanelPlayerPanels.playerWidgets.add(new BingoPlayerWidget(teamList)); });
        }

		TopPanelPlayerPanels.SortWidgets();      
	}

	public void changedSelectionTo(DropdownMenu dropdownMenu, int index, String optionText) {
		bingoDifficulty.setSelectedIndex(index);
		if (TogetherManager.currentLobby.isOwner())
			NetworkHelper.sendData(NetworkHelper.dataType.BingoRules);
   	}
  
	public void render(SpriteBatch sb) {
		FontHelper.renderFontCentered(sb, FontHelper.SCP_cardTitleFont_small, "Lobby",
			Settings.WIDTH / 2.0f,
			Settings.HEIGHT - 70.0f * Settings.scale,
			Settings.GOLD_COLOR);

		this.button.render(sb);
		this.confirmButton.render(sb);

		// Bingo Team Interaction
		if (TogetherManager.gameMode == TogetherManager.mode.Bingo) {
			if (teamsToggle.isTicked()) {
				// Draw Team Title
				String teamName = TogetherManager.getCurrentUser().teamName;
				if (teamName.equals(""))
					teamName = "Team " + TogetherManager.getCurrentUser().team;

			    if (this.renameHb.hovered) {
					FontHelper.renderFontRightAligned(sb, FontHelper.panelEndTurnFont, teamName, this.renameHb.cX - 35.0F*Settings.scale, this.renameHb.cY, Settings.GREEN_TEXT_COLOR);
			    } else {
					FontHelper.renderFontRightAligned(sb, FontHelper.panelEndTurnFont, teamName, this.renameHb.cX - 35.0F*Settings.scale, this.renameHb.cY, Settings.CREAM_COLOR);
			    } 

				// Draw Team Members
				int tm = 2; // Offsetting
				for (RemotePlayer p : TogetherManager.players) {
					if (p.team == TogetherManager.getCurrentUser().team) {
						// (Texture texture, float x, float y, float originX, float originY, float width, float height, float scaleX, float scaleY, float rotation, int srcX, int srcY, int srcWidth, int srcHeight, boolean flipX, boolean flipY) {
						sb.draw(TogetherManager.teamTags, this.renameHb.cX - 160f, this.renameHb.cY-tm*70f*Settings.scale, 
							329f/2f, 52f/2f, 329f, 52f,
							Settings.scale, Settings.scale, 0f,
							0,0,329,52,
							true, false);
						FontHelper.renderFontRightAligned(sb, FontHelper.cardDescFont_N, p.userName, this.renameHb.cX + 45f*Settings.scale, this.renameHb.cY-tm*70f*Settings.scale+28f, Settings.CREAM_COLOR);
						tm++;
					}
				}

				// Draw Rename Button
      			this.renameHb.render(sb);

				float scale = Settings.scale;
				if (this.renameHb.hovered)
					scale = Settings.scale * 1.04F; 
				sb.draw(ImageMaster.PROFILE_RENAME, this.renameHb.cX - 50.0F, this.renameHb.cY - 50.0F, 50.0F, 50.0F, 100.0F, 100.0F, scale/2f, scale/2f, 0.0F, 0, 0, 100, 100, false, false);
				if (this.renameHb.hovered) {
					sb.setColor(new Color(1.0F, 1.0F, 1.0F, 0.4F));
					sb.setBlendFunction(770, 1);
					sb.draw(ImageMaster.PROFILE_RENAME, this.renameHb.cX - 50.0F, this.renameHb.cY - 50.0F, 50.0F, 50.0F, 100.0F, 100.0F, scale/2f, scale/2f, 0.0F, 0, 0, 100, 100, false, false);
					sb.setBlendFunction(770, 771);
					sb.setColor(uiColor);
				} 
			}
		}

		playerList.render(sb);

		if (TogetherManager.currentLobby != null && TogetherManager.gameMode != TogetherManager.mode.Coop && !TogetherManager.currentLobby.isOwner())
			ShaderHelper.setShader(sb, ShaderHelper.Shader.GRAYSCALE); 

		characterSelectWidget.render(sb);

		if (TogetherManager.currentLobby != null && !TogetherManager.currentLobby.isOwner())
			ShaderHelper.setShader(sb, ShaderHelper.Shader.GRAYSCALE); 

		ascensionSelectWidget.render(sb);
		privateToggle.render(sb);
		if (Loader.isModLoaded("downfall"))
			downfallToggle.render(sb);

		if (TogetherManager.gameMode == TogetherManager.mode.Coop) 
			hardToggle.render(sb);

		if (TogetherManager.gameMode != TogetherManager.mode.Bingo) {
			seedSelectWidget.render(sb);
			heartToggle.render(sb);
			neowToggle.render(sb);
		} else {
			FontHelper.renderSmartText(sb, FontHelper.cardDescFont_N, LOBBY[30], TOGGLE_X_RIGHT-16f, Settings.HEIGHT * 0.343f, 10000.0F, 40.0F * Settings.scale, Settings.CREAM_COLOR);
			bingoDifficulty.render(sb, TOGGLE_X_RIGHT, Settings.HEIGHT * 0.313f);
			teamsToggle.render(sb);
			uniqueBoardToggle.render(sb);
		}

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


		// Rename Popup for Bingo Team Name
	    this.renamePopup.render(sb);
	}
}