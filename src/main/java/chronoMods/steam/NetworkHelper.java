package chronoMods.steam;

import com.evacipated.cardcrawl.modthespire.lib.*;

import basemod.*;
import basemod.abstracts.*;
import basemod.interfaces.*;

import org.apache.logging.log4j.*;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.actions.*;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.helpers.*;
import com.megacrit.cardcrawl.rooms.*;
import com.megacrit.cardcrawl.map.*;
import com.megacrit.cardcrawl.rewards.*;
import com.megacrit.cardcrawl.relics.*;
import com.megacrit.cardcrawl.potions.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.ui.buttons.*;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.vfx.campfire.*;
import com.megacrit.cardcrawl.vfx.*;
import com.megacrit.cardcrawl.screens.*;
import com.megacrit.cardcrawl.ui.campfire.*;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import com.badlogic.gdx.math.*;

import chronoMods.*;
import chronoMods.coop.*;
import chronoMods.coop.relics.*;
import chronoMods.coop.drawable.*;
import chronoMods.steam.*;
import chronoMods.ui.deathScreen.*;
import chronoMods.ui.hud.*;
import chronoMods.ui.lobby.*;
import chronoMods.ui.mainMenu.*;
import chronoMods.utilities.*;

import java.util.*;
import java.lang.*;
import java.nio.*;

import com.codedisaster.steamworks.*;
import com.megacrit.cardcrawl.integrations.steam.*;

public class NetworkHelper {

	public static SteamMatchmaking matcher;
	public static SteamFriends friends;
	public static SteamNetworking net;
	public static SteamUtils utils;

	public static SteamID id;

	public static int channel = 0;
    private static final Logger logger = LogManager.getLogger("Network Data");

    public static ArrayList<SteamLobby> steamLobbies = new ArrayList();
    public static boolean embarked = false;

	public void NetworkHelper() {}

	public static void initialize() {
		SteamApps steamApps = (SteamApps)ReflectionHacks.getPrivate(CardCrawlGame.publisherIntegration, SteamIntegration.class, "steamApps");

        matcher = new SteamMatchmaking(new SMCallback());
        net = new SteamNetworking(new SNCallback(), SteamNetworking.API.Client);
        utils = new SteamUtils(new SUtilsCallback());
        friends = new SteamFriends(new SFCallback());

		id = steamApps.getAppOwner();
	}

    @SpirePatch(clz=CardCrawlGame.class, method="update")
    public static class SteamUpdate
    {
        public static void Postfix(CardCrawlGame __instance)
        {
        	NetworkHelper.update();
        }
    }

	// Check every frame for incoming packets.
	public static void update() {
		boolean noPackets = true;
		int bufferSize;
		ByteBuffer data = ByteBuffer.allocateDirect(0); 
		SteamID steamID = new SteamID();

		while (noPackets) {
			bufferSize = net.isP2PPacketAvailable(NetworkHelper.channel);
			data = ByteBuffer.allocateDirect(bufferSize);

			if (bufferSize != 0) {
				TogetherManager.log("A packet is available of size " + bufferSize);
				try {
					net.readP2PPacket(steamID, data, NetworkHelper.channel);
					if (TogetherManager.currentLobby != null)
						parseData(data, steamID);
				}
				catch (SteamException e) {
					TogetherManager.log("Reading the packet failed: " + e.getMessage());
					e.printStackTrace();
				}
			} else {
				noPackets = false;
			}
		}
	}

	public static void parseData(ByteBuffer data, SteamID player) {

		for (RemotePlayer playerInfo : TogetherManager.players) {
			if (playerInfo.isUser(player)) {
				dataType type = dataType.values()[data.getInt()];

				switch (type) {

					case Version:

						playerInfo.version = data.getFloat(4);
						playerInfo.modHash = data.getInt(8);
						playerInfo.safeMods = data.getInt(12) == 1 ? true : false;

						TogetherManager.log("V: " + playerInfo.version);
						TogetherManager.log("H: " + playerInfo.modHash);
						TogetherManager.log("S: " + playerInfo.safeMods);
						break;
					case Rules:
						// Backup plan for slow loaders?
						if (NewMenuButtons.newGameScreen == null || NewMenuButtons.newGameScreen.ascensionSelectWidget == null) { return; }

						if (TogetherManager.gameMode != TogetherManager.mode.Coop) {
							NewMenuButtons.newGameScreen.characterSelectWidget.selectOption(data.getInt(4));
						}

						// Ascension
						NewMenuButtons.newGameScreen.ascensionSelectWidget.ascensionLevel = data.getInt(8);
						if (NewMenuButtons.newGameScreen.ascensionSelectWidget.ascensionLevel == 0) {
							NewMenuButtons.newGameScreen.ascensionSelectWidget.isAscensionMode = false;
						} else {
							NewMenuButtons.newGameScreen.ascensionSelectWidget.isAscensionMode = true;
						}

						// toggle boxes
						boolean heart = data.getInt(12)>0 ? true : false;
						NewMenuButtons.newGameScreen.heartToggle.setTicked(heart);
			            Settings.isFinalActAvailable = heart;

			            boolean neow = data.getInt(16)>0 ? true : false;
						NewMenuButtons.newGameScreen.neowToggle.setTicked(neow);
			            Settings.isTrial = !neow;

						boolean lament = data.getInt(20)>0 ? true : false;
						NewMenuButtons.newGameScreen.lamentToggle.setTicked(lament);
						if (lament) {
							NewMenuButtons.newGameScreen.neowToggle.setTicked(true);
				            Settings.isTrial = false;
						}
			            Settings.isTestingNeow = lament;

						boolean ironman = data.getInt(24)>0 ? true : false;
						NewMenuButtons.newGameScreen.ironmanToggle.setTicked(ironman);
			            NewDeathScreenPatches.Ironman = ironman;

						// seed
						Settings.seed = data.getLong(28);

						// Update version every time you recieve a rules, as a fallback
						NetworkHelper.sendData(NetworkHelper.dataType.Version);
						// TogetherManager.log("Updated rules with Char " + data.getInt(4) + ", Asc " + data.getInt(8) + ", and seed " + data.getLong(12));
						break;
					case Start:
						TogetherManager.log("Start Run");
						NewMenuButtons.newGameScreen.embark();

						// Report to server - this is a blank entry to protect against rage quitters
						customMetrics startmetrics = new customMetrics();
						Thread st = new Thread((Runnable)startmetrics);
						st.start();

						break;
					case Ready:
						int start = data.getInt(4);
						if (start == 0) {
							playerInfo.ready = false;
							TogetherManager.log("Unready: " + playerInfo.userName);
						} else {
							playerInfo.ready = true;
							TogetherManager.log("Ready: " + playerInfo.userName);
						}
						break;
					case Floor:
						int floorNum = data.getInt(4);
						playerInfo.floor = floorNum;
						playerInfo.highestFloor = Math.max(floorNum, playerInfo.highestFloor);

						playerInfo.x = data.getInt(8);
						playerInfo.y = data.getInt(12);
						playerInfo.act = data.getInt(16);

						TogetherManager.log("Act: " + playerInfo.act + " - Floor: " + floorNum + " - Position: " + playerInfo.x + ", " + playerInfo.y);
						playerInfo.markMapNode();

						TopPanelPlayerPanels.SortWidgets();

						break;
					case Act:
						playerInfo.act = data.getInt(4);
						break;
					case Hp:
						int Hp = data.getInt(4);
						int maxHp = data.getInt(8);

						if (AbstractDungeon.player.hasBlight("MirrorTouch")) {
							AbstractDungeon.player.currentHealth = Hp;
							AbstractDungeon.player.maxHealth = maxHp;

			            	for (RemotePlayer playerhp : TogetherManager.players) {
			            		playerhp.hp = Hp;
			            	}
						}

						playerInfo.hp = Hp;
						playerInfo.maxHp = maxHp;
						TogetherManager.log("Player HP: " + Hp);
						break;
					case Money:
						int Money = data.getInt(4);

			            if (TogetherManager.gameMode == TogetherManager.mode.Coop && AbstractDungeon.player.hasBlight("DimensionalWallet")) {
			            	AbstractDungeon.player.gold = Money;
			            	for (RemotePlayer playergld : TogetherManager.players) {
			            		playergld.gold = Money;
			            	}
			            }

						playerInfo.gold = Money;
						TogetherManager.log("Gold: " + Money);
						break;
					case Character:
						// Extract the string
						try {
							String characterNameOut = NewMenuButtons.newGameScreen.characterSelectWidget.options.get(data.getInt(4)).c.getLocalizedCharacterName();
							playerInfo.character = characterNameOut;
						} catch (Exception e) {}
						break;
					case SetDisplayRelics:
						// Extract the string
						byte[] bytes = new byte[data.remaining()];
						data.get(bytes);
						String stringOut = new String(bytes);

						// Clear
						playerInfo.displayRelics.clear();

						// Make the relic
			 			for (String relicID : stringOut.split(",")) {
							AbstractRelic relic = RelicLibrary.getRelic(relicID).makeCopy();
							relic.isAnimating = true;
							playerInfo.displayRelics.add(relic);
							TogetherManager.log("Display Relic: " + relicID);
						}

						break;
					case SendRelic:
						Long steamIDsr = data.getLong(4);
						if (TogetherManager.currentUser.steamUser.getAccountID() != steamIDsr) { break; }

						// Extract the string
						((Buffer)data).position(12);
						byte[] byteSentRelics = new byte[data.remaining()];
						data.get(byteSentRelics);
						String sentRelicID = new String(byteSentRelics);

						// Make the relic
						AbstractDungeon.getCurrRoom().spawnRelicAndObtain(Settings.WIDTH/2.0f, Settings.HEIGHT/2.0f, RelicLibrary.getRelic(sentRelicID).makeCopy());

						break;
					case Finish:
						float finishtime = data.getFloat(4);
						playerInfo.finalTime = finishtime;
						playerInfo.splits.get("Final").finish(finishtime);

						TopPanelPlayerPanels.SortWidgets();

						// Report to server - this should replace the earlier entry
						customMetrics metrics = new customMetrics();
						Thread t = new Thread((Runnable)metrics);
						t.start();

						break;

					case SendCard:
						// Find the correct recipient
						Long steamIDs = data.getLong(4);
						if (TogetherManager.currentUser.steamUser.getAccountID() != steamIDs) { break; }

						// Get upgrade
						int upgrades = data.getInt(12);
						int miscs = data.getInt(16);

						// Extract the string
						((Buffer)data).position(20);
						byte[] bytess = new byte[data.remaining()];
						data.get(bytess);
						String stringOuts = new String(bytess);

						TogetherManager.log("Send card direct: " + stringOuts);

						// Creat RewardItem
						AbstractDungeon.player.masterDeck.addToTop(CardLibrary.getCopy(stringOuts, upgrades, miscs));

						break;
					case TransferCard:
						// Find the correct recipient
						Long steamIDc = data.getLong(4);
						if (TogetherManager.currentUser.steamUser.getAccountID() != steamIDc) { break; }

						// Get upgrade
						int upgradec = data.getInt(12);
						int miscc = data.getInt(16);

						// Hardcoded relic shit because that's how we roll now
						if (AbstractDungeon.player.hasBlight("PneumaticPost")){
							TogetherManager.log("Upgrading");
							upgradec++;
						}

						// Extract the string
						((Buffer)data).position(20);
						byte[] bytesc = new byte[data.remaining()];
						data.get(bytesc);
						String stringOutc = new String(bytesc);

						TogetherManager.log("Transfer card: " + stringOutc);

						// Creat RewardItem
			            RewardItem transferItemc = new RewardItem();
			            transferItemc.cards.clear();
			            transferItemc.cards.add(CardLibrary.getCopy(stringOutc, upgradec, miscc));

			            // Add Reward to Packages for pickup
			            TogetherManager.getCurrentUser().packages.add(transferItemc);
						break;
					case TransferRelic:
						// Find the correct recipient
						Long steamIDr = data.getLong(4);
						if (TogetherManager.currentUser.steamUser.getAccountID() != steamIDr) { break; }

						// Extract the string
						((Buffer)data).position(12);
						byte[] bytesr = new byte[data.remaining()];
						data.get(bytesr);
						String stringOutr = new String(bytesr);

						TogetherManager.log("Transfer relic: " + stringOutr);

						// Creat RewardItem
			            RewardItem transferItemr = new RewardItem(RelicLibrary.getRelic(stringOutr).makeCopy());

			            // Add Reward to Packages for pickup
			            TogetherManager.getCurrentUser().packages.add(transferItemr);
						break;
					case TransferPotion:
						// Find the correct recipient
						Long steamIDp = data.getLong(4);
						if (TogetherManager.currentUser.steamUser.getAccountID() != steamIDp) { break; }

						// Extract the string
						((Buffer)data).position(12);
						byte[] bytesp = new byte[data.remaining()];
						data.get(bytesp);
						String stringOutp = new String(bytesp);

						TogetherManager.log("Transfer potion: " + stringOutp);

						// Creat RewardItem
			            RewardItem transferItemp = new RewardItem(PotionHelper.getPotion(stringOutp));

			            // Add Reward to Packages for pickup
			            TogetherManager.getCurrentUser().packages.add(transferItemp);
						break;
					case UsePotion:
						// Find the correct recipient
						int potslot = data.getInt(4);
						AbstractDungeon.player.potions.set(potslot, new PotionSlot(potslot));
						break;
					case SendPotion:
						// Find the correct recipient
						int potslotb = data.getInt(4);

						// Extract the string
						((Buffer)data).position(8);
						byte[] bytesb = new byte[data.remaining()];
						data.get(bytesb);
						String stringOutb = new String(bytesb);

						TogetherManager.log("Send potion: " + stringOutb);

						// Obtain the potion
						if (AbstractDungeon.player.potions.get(potslotb) instanceof PotionSlot) {
				            AbstractDungeon.player.obtainPotion(potslotb, PotionHelper.getPotion(stringOutb));
				        }

						break;
					// case BossChosen:
					// 	data.getChar(1, );
					// 	break;
					case Splits:
						int actNum = data.getInt(4);
						float playtime = data.getFloat(8);

						TogetherManager.log("Splits, Act: " + (actNum-1) + " - " + VersusTimer.returnTimeString(playtime));
						switch (actNum) {
							case 1:
								playerInfo.splits.get("Act 1").activate(AbstractDungeon.bossKey);
								break;
							case 2:
								playerInfo.splits.get("Act 1").finish(playtime);
								playerInfo.splits.get("Act 2").activate(AbstractDungeon.bossKey);
								break;
							case 3:
								playerInfo.splits.get("Act 2").finish(playtime);
								playerInfo.splits.get("Act 3").activate(AbstractDungeon.bossKey);
								break;
							case 4:
								playerInfo.splits.get("Act 3").finish(playtime);
								playerInfo.splits.get("Final").activate(AbstractDungeon.bossKey);
								break;
							default:
								playerInfo.splits.get("Final").finish(playtime);
								break;
						}

						TopPanelPlayerPanels.SortWidgets();
						break;

					case ClearRoom:
						int xc = data.getInt(4);
						int yc = data.getInt(8);
						if (xc != -1 && yc != -1 && yc < 16 && !AbstractDungeon.id.equals("TheEnding")) {
				            MapRoomNode currentNodec = AbstractDungeon.map.get(yc).get(xc);

				            // Safety first? This triggers if games are desynced, but I hate getting reports about it.
				            if (currentNodec == null) 			{ return; }
				            if (currentNodec.getRoom() == null) { return; }

				            // Unlocks a room we are leaving
							CoopEmptyRoom.LockedRoomField.locked.set(currentNodec.getRoom(), false);
						
							// Sets the next room of a multi-room
							AbstractRoom secondRoom = CoopMultiRoom.secondRoomField.secondRoom.get(currentNodec);
							AbstractRoom thirdRoom  = CoopMultiRoom.thirdRoomField.thirdRoom.get(currentNodec);

							// Resolve the multinodes by advancing the 'queue' 
							currentNodec.room = secondRoom;
							CoopMultiRoom.secondRoomField.secondRoom.set(currentNodec, thirdRoom);
							CoopMultiRoom.thirdRoomField.thirdRoom.set(currentNodec, null);

							if (currentNodec.room == null)
								currentNodec.setRoom(new CoopEmptyRoom());
						}

						TogetherManager.log("Clearing: " + xc + ", " + yc);
						break;

					case LockRoom:
						int xl = data.getInt(4);
						int yl = data.getInt(8);
						if (xl != -1 && yl != -1 && yl < 16 && !AbstractDungeon.id.equals("TheEnding")) {
							try {
					            MapRoomNode currentNodel = AbstractDungeon.map.get(yl).get(xl);

								CoopEmptyRoom.LockedRoomField.locked.set(currentNodel.getRoom(), true);
							} catch (Exception e) {}
						}
						TogetherManager.log("Locking: " + xl + ", " + yl);
						break;

					case ChooseNeow:
						int choice = data.getInt(4);

						if (CoopNeowEvent.screenNum == 1)
							CoopNeowEvent.rewards.get(choice).chosenBy = playerInfo.userName;
						else 
							CoopNeowEvent.penalties.get(choice).chosenBy = playerInfo.userName;

			        	String neowMsg = String.format(CardCrawlGame.languagePack.getUIString("Neow").TEXT[0], playerInfo.userName, AbstractDungeon.getCurrRoom().event.roomEventText.optionList.get(choice).msg);

						AbstractDungeon.getCurrRoom().event.roomEventText.optionList.get(choice).msg = neowMsg;
						AbstractDungeon.getCurrRoom().event.roomEventText.optionList.get(choice).isDisabled = true;

						if (playerInfo.isUser(TogetherManager.currentUser.steamUser)) {
							for (LargeDialogOptionButton choiceButton : AbstractDungeon.getCurrRoom().event.roomEventText.optionList) {
								choiceButton.isDisabled = true;
							}
						}

						boolean singleAllowance = false;


						if (CoopNeowEvent.screenNum == 1) {

							// Stop here if not everyone has chosen
							for (CoopNeowReward r : CoopNeowEvent.rewards) {
								if (r.chosenBy == "") { 
									if (singleAllowance) {
										return; }
									else {
										singleAllowance = true;
									}
								}
							}
						} else {

							for (CoopNeowReward r : CoopNeowEvent.penalties) {
								if (r.chosenBy == "") { 
									if (singleAllowance) {
										return; }
									else {
										singleAllowance = true;
									}
								}
							}
						}

						TogetherManager.log("Advance the screen!");

						CoopNeowEvent.advanceScreen();

						break;
		
					case ChooseTeamRelic:
						int choicer = data.getInt(4);

						if (playerInfo.isUser(TogetherManager.currentUser.steamUser)) { break; }

						// Set your current choice
						CoopBossRelicSelectScreen teamScreen = TogetherManager.teamRelicScreen;

						for (ArrayList<RemotePlayer> pList : teamScreen.selected) {
							pList.remove(playerInfo);
						}
						teamScreen.selected.get(choicer).add(playerInfo);

						// Advance if selected
						if (teamScreen.selected.get(choicer).size() == TogetherManager.players.size()) {
							teamScreen.blights.get(choicer).obtain();
							teamScreen.blights.get(choicer).isObtained = true;
						}
						break;

					case LoseLife:
						int counter = data.getInt(4);

						if (counter >= 0) {
							// Death notification
							AbstractDungeon.effectList.add(new CoopDeathNotification(playerInfo));

							if (AbstractDungeon.player.hasBlight("StringOfFate")) {
								// Lower the counter
								AbstractDungeon.player.getBlight("StringOfFate").counter = counter;

								AbstractDungeon.player.decreaseMaxHealth(AbstractDungeon.player.maxHealth / 4); // +2 because -1 for the life lost, and -1 for zero index
						        if (AbstractDungeon.player.currentHealth > AbstractDungeon.player.maxHealth)
						            AbstractDungeon.player.currentHealth = AbstractDungeon.player.maxHealth;

						    } else if (AbstractDungeon.player.hasBlight("BondsOfFate")) {
								// Lower the counter
								AbstractDungeon.player.getBlight("BondsOfFate").counter = counter;

								((Buffer)data).position(8);
								byte[] bytesll = new byte[data.remaining()];
								data.get(bytesll);
								String killedBy = new String(bytesll);

								TogetherManager.log("Killed by: " + killedBy);
								if (killedBy == null || killedBy == "") {
									AbstractDungeon.topLevelEffects.add(new ShowCardAndObtainEffect(
										new Tombstone(playerInfo.userName, "", playerInfo.portraitImg), Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
									if (AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT) {
										AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new MakeTempCardInHandAction(new Tombstone(playerInfo.userName, "", playerInfo.portraitImg), 1)); 
									}
								} else {
									AbstractDungeon.topLevelEffects.add(new ShowCardAndObtainEffect(
										new Tombstone(playerInfo.userName, MonsterHelper.getEncounterName(killedBy), playerInfo.portraitImg), Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
									if (AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT) {
										AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new MakeTempCardInHandAction(new Tombstone(playerInfo.userName, "", playerInfo.portraitImg), 1)); 
									}
								}
						    }

						} else {
							// Die
							AbstractDungeon.player.currentHealth = 0;
							AbstractDungeon.player.isDead = true;

				            NewDeathScreenPatches.raceEndScreen = new RaceEndScreen(AbstractDungeon.getCurrRoom().monsters);
				            AbstractDungeon.screen = NewDeathScreenPatches.Enum.RACEEND;
   						}

   						NetworkHelper.sendData(NetworkHelper.dataType.Hp);
						break;

					case Kick:
						Long steamIDk = data.getLong(4);
						if (TogetherManager.currentUser.steamUser.getAccountID() == steamIDk) {
							NetworkHelper.leaveLobby();
							TogetherManager.infoPopup.show(CardCrawlGame.languagePack.getUIString("Network").TEXT[0], CardCrawlGame.languagePack.getUIString("Network").TEXT[1]);
						}

						break;

					case GetRedKey:
						Long steamIDrk = data.getLong(4);

						for (RemotePlayer playerrk : TogetherManager.players) {
							if (playerrk.steamUser.getAccountID() == steamIDrk)
								playerrk.rubyKey = true;
						}

						if (TogetherManager.currentUser.steamUser.getAccountID() == steamIDrk && !Settings.hasRubyKey)
							AbstractDungeon.topLevelEffects.add(new ObtainKeyEffect(ObtainKeyEffect.KeyColor.RED)); 

						break;

					case GetBlueKey:
						Long steamIDbk = data.getLong(4);

						for (RemotePlayer playerbk : TogetherManager.players) {
							if (playerbk.steamUser.getAccountID() == steamIDbk)
								playerbk.sapphireKey = true;
						}

						if (TogetherManager.currentUser.steamUser.getAccountID() == steamIDbk && !Settings.hasSapphireKey)
							AbstractDungeon.topLevelEffects.add(new ObtainKeyEffect(ObtainKeyEffect.KeyColor.BLUE)); 

						break;

					case GetGreenKey:
						Long steamIDgk = data.getLong(4);

						for (RemotePlayer playergk : TogetherManager.players) {
							if (playergk.steamUser.getAccountID() == steamIDgk)
								playergk.emeraldKey = true;
						}

						if (TogetherManager.currentUser.steamUser.getAccountID() == steamIDgk && !Settings.hasEmeraldKey)
							AbstractDungeon.topLevelEffects.add(new ObtainKeyEffect(ObtainKeyEffect.KeyColor.GREEN)); 

						break;
					case GetPotion:
						playerInfo.potionSlots = data.getInt(4);

						// Extract the string
						((Buffer)data).position(8);
						byte[] potionBytes = new byte[data.remaining()];
						data.get(potionBytes);
						String potionsOut = new String(potionBytes);

						// Clear
						playerInfo.potions.clear();

						// Add the owned potions to the list
			 			for (String potionID : potionsOut.split(",")) {
			 				if (!potionID.equals(""))
								playerInfo.potions.add(potionID);
						}
						break;
					case AddPotionSlot:
					    AbstractDungeon.player.potionSlots += 1;
					    AbstractDungeon.player.potions.add(new PotionSlot(AbstractDungeon.player.potionSlots - 1));
   						break;
					case ModifyBrainFreeze:
						AbstractDungeon.player.getBlight("BrainFreeze").counter += data.getInt(4);
						break;

					case DrawMap:
						if (playerInfo.isUser(TogetherManager.currentUser.steamUser)) { break; }

						float xSize = playerInfo.drawable[playerInfo.act-1].pixmap.getWidth();
						float ySize = playerInfo.drawable[playerInfo.act-1].pixmap.getHeight();

						Vector2 curr = new Vector2(data.getFloat(4)  * xSize, data.getFloat(8)  * ySize);
						Vector2 last = new Vector2(data.getFloat(12) * xSize, data.getFloat(16) * ySize);

						playerInfo.drawable[playerInfo.act-1].brushSize = data.getFloat(20);
						float offset = data.getFloat(24) * ySize;

						if (last.x == 0f && last.y == 0f)
							playerInfo.drawable[playerInfo.act-1].draw(curr, offset);
						else
							playerInfo.drawable[playerInfo.act-1].drawLerped(curr, last, offset);

						playerInfo.drawable[playerInfo.act-1].dirty = true;

						break;
					case ClearMap:
						if (playerInfo.isUser(TogetherManager.currentUser.steamUser)) { break; }

						TogetherManager.log(playerInfo.userName + " has cleared their map.");

						playerInfo.drawable[playerInfo.act-1].clear();
						break;
					case DeckInfo:
						playerInfo.cards = data.getInt(4);
						playerInfo.upgrades = data.getInt(8);
						break;
					case RelicInfo:
						playerInfo.relics = data.getInt(4);
						break;
					case RequestVersion:
						sendData(dataType.Version);
						break;
				}
			}
		}
	}

    public static enum dataType
    {
      	Rules, Start, Ready, Version, Floor, Act, Hp, Money, BossRelic, Finish, SendCard, TransferCard, TransferRelic, TransferPotion, UsePotion, SendPotion, EmptyRoom, BossChosen, Splits, SetDisplayRelics, ClearRoom, LockRoom, ChooseNeow, ChooseTeamRelic, LoseLife, Kick, GetRedKey, GetBlueKey, GetGreenKey, Character, GetPotion, AddPotionSlot, SendRelic, ModifyBrainFreeze, DrawMap, ClearMap, DeckInfo, RelicInfo, RequestVersion;
      
    	private dataType() {}
    }

	public static void sendData(NetworkHelper.dataType type) {
		ByteBuffer data = NetworkHelper.generateData(type);	
		if (data == null) { return; }

		for (RemotePlayer player : TogetherManager.players) {
			TogetherManager.log("Sending packet of type " + type.toString() + " to " + player);
			try {
				boolean sent = net.sendP2PPacket(player.steamUser, data, SteamNetworking.P2PSend.Reliable, NetworkHelper.channel);
				//TogetherManager.log("SteamID is valid: " + player.steamUser.isValid());
				//TogetherManager.log("Packet of type " + type.toString() + " to " + player.steamUser.getAccountID() + " was " + sent);
			} catch (SteamException e) {
				TogetherManager.log("Sending the packet of type " + type.toString() + " failed: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	private static ByteBuffer generateData(NetworkHelper.dataType type) {
		ByteBuffer data;

		switch (type) {

			// Packets used by both
			case Version:
				data = ByteBuffer.allocateDirect(32);
				data.putFloat(4, TogetherManager.VERSION);
				data.putInt(8, TogetherManager.getModHash());
				data.putInt(12, TogetherManager.areModsSafe() ? 1 : 0);
				break;
			case Rules:
        		if (!TogetherManager.currentLobby.isOwner()) { return null; }

				data = ByteBuffer.allocateDirect(36);
				// Rules are character, ascension, seed
				data.putInt(4, NewMenuButtons.newGameScreen.characterSelectWidget.getChosenOption());

				if (NewMenuButtons.newGameScreen.ascensionSelectWidget.isAscensionMode)
					data.putInt(8, NewMenuButtons.newGameScreen.ascensionSelectWidget.ascensionLevel);
				else
					data.putInt(8, 0);

				data.putInt(12, NewMenuButtons.newGameScreen.heartToggle.getTicked());
				data.putInt(16, NewMenuButtons.newGameScreen.neowToggle.getTicked());
				data.putInt(20, NewMenuButtons.newGameScreen.lamentToggle.getTicked());
				data.putInt(24, NewMenuButtons.newGameScreen.ironmanToggle.getTicked());

				if (Settings.seed != null){
					data.putLong(28, Settings.seed);
				} else {
					data.putLong(28, 0);
				}

				updateLobbyData();
				break;
			case Start:
				data = ByteBuffer.allocateDirect(8);
				data.putInt(4, 1);
				break;
			case Ready:
				data = ByteBuffer.allocateDirect(8);
				TogetherManager.log("Sending ready state: " + TogetherManager.getCurrentUser().userName + ", " + TogetherManager.getCurrentUser().ready);

				if (TogetherManager.getCurrentUser().ready) {
					TogetherManager.log("Sent Ready");
					data.putInt(4, 1);
				} else {
					TogetherManager.log("Sent Unready");
					data.putInt(4, 0);
				}
				break;
			case Floor:
				data = ByteBuffer.allocateDirect(20);
				data.putInt(4, AbstractDungeon.floorNum);
				data.putInt(8, AbstractDungeon.getCurrMapNode().x);
				data.putInt(12, AbstractDungeon.getCurrMapNode().y);
				data.putInt(16, AbstractDungeon.actNum);
				break;
			case Act:
				data = ByteBuffer.allocateDirect(8);
				data.putInt(4, AbstractDungeon.actNum);
				break;
			case Hp:
				data = ByteBuffer.allocateDirect(12);
				data.putInt(4, AbstractDungeon.player.currentHealth);
				data.putInt(8, AbstractDungeon.player.maxHealth);
				break;
			case Money:
				data = ByteBuffer.allocateDirect(8);
				data.putInt(4, AbstractDungeon.player.gold);
				break;
			case Character:
				data = ByteBuffer.allocateDirect(8);
				data.putInt(4, NewMenuButtons.newGameScreen.characterSelectWidget.getChosenOption());
				// String characterName = NewMenuButtons.newGameScreen.characterSelectWidget.getChosenOptionLocalizedName();
				// data = ByteBuffer.allocateDirect(4 + characterName.getBytes().length);

				// ((Buffer)data).position(4);
				// data.put(characterName.getBytes());
				// ((Buffer)data).rewind();
				break;
			case SetDisplayRelics:
				String relicID = "";

				for (AbstractRelic relic : AbstractDungeon.player.relics) {
					if (relic.tier == AbstractRelic.RelicTier.STARTER || relic.tier == AbstractRelic.RelicTier.BOSS) {
						relicID += relic.relicId + ",";
					}
				}

				relicID = relicID.substring(0, relicID.length() - 1);
				data = ByteBuffer.allocateDirect(4 + relicID.getBytes().length);

				((Buffer)data).position(4);
				data.put(relicID.getBytes());
				((Buffer)data).rewind();
				break;
			case SendRelic:
				AbstractDungeon.player.loseRelic(Dimensioneel.relicID);

				data = ByteBuffer.allocateDirect(12 + Dimensioneel.relicID.getBytes().length);
				data.putLong(4, Dimensioneel.sendPlayer.steamUser.getAccountID()); // Selected recipient

				((Buffer)data).position(12);
				data.put(Dimensioneel.relicID.getBytes());
				((Buffer)data).rewind();
				break;
			case Finish:
				data = ByteBuffer.allocateDirect(8);
				data.putFloat(4, VersusTimer.timer);
				break;

			// Versus specific
			case Splits:
				data = ByteBuffer.allocateDirect(12);
				data.putInt(4, AbstractDungeon.actNum);
				data.putFloat(8, VersusTimer.timer);
				break;

			// Coop specific packets
			case ClearRoom:
				data = ByteBuffer.allocateDirect(12);
				data.putInt(4, AbstractDungeon.getCurrMapNode().x);
				data.putInt(8, AbstractDungeon.getCurrMapNode().y);
				break;
			case LockRoom:
				data = ByteBuffer.allocateDirect(12);
				data.putInt(4, SendDataPatches.lockX);
				data.putInt(8, SendDataPatches.lockY);
				break;

			case SendCard:
				String rewards = GhostWriter.sendCard.cardID;

				data = ByteBuffer.allocateDirect(20 + rewards.getBytes().length);

				data.putLong(4, GhostWriter.sendPlayer.steamUser.getAccountID()); // Selected recipient
				data.putInt(12, GhostWriter.sendCard.timesUpgraded);
				data.putInt(16, GhostWriter.sendCard.misc);

				((Buffer)data).position(20);
				data.put(rewards.getBytes());
				((Buffer)data).rewind();

				GhostWriter.sendCard = null; 
				break;
			case TransferCard:
				String rewardc = TogetherManager.courierScreen.transferCard.cardID;

				data = ByteBuffer.allocateDirect(20 + rewardc.getBytes().length);

				data.putLong(4, TogetherManager.courierScreen.getRecipient().steamUser.getAccountID()); // Selected recipient
				data.putInt(12, TogetherManager.courierScreen.transferCard.timesUpgraded);
				data.putInt(16, TogetherManager.courierScreen.transferCard.misc);

				((Buffer)data).position(20);
				data.put(rewardc.getBytes());
				((Buffer)data).rewind();

				TogetherManager.courierScreen.transferCard = null; 
				break;
			case TransferRelic:
				String rewardr = TogetherManager.courierScreen.transferRelic.relicId;

				data = ByteBuffer.allocateDirect(12 + rewardr.getBytes().length);

				data.putLong(4, TogetherManager.courierScreen.getRecipient().steamUser.getAccountID()); // Selected recipient

				((Buffer)data).position(12);
				data.put(rewardr.getBytes());
				((Buffer)data).rewind();

				TogetherManager.courierScreen.transferRelic = null; 
				break;
			case TransferPotion:
				String rewardp = TogetherManager.courierScreen.transferPotion.ID;

				data = ByteBuffer.allocateDirect(12 + rewardp.getBytes().length);

				data.putLong(4, TogetherManager.courierScreen.getRecipient().steamUser.getAccountID()); // Selected recipient

				((Buffer)data).position(12);
				data.put(rewardp.getBytes());
				((Buffer)data).rewind();

				TogetherManager.courierScreen.transferPotion = null; 
				break;
			case UsePotion:
				data = ByteBuffer.allocateDirect(8);
				data.putInt(4, VaporFunnel.potSlot);
				break;
			case SendPotion:
				String rewardb = VaporFunnel.potName;
				TogetherManager.log(VaporFunnel.potName);
				data = ByteBuffer.allocateDirect(8 + rewardb.getBytes().length);

				data.putInt(4, VaporFunnel.potSlot); // Selected recipient

				((Buffer)data).position(8);
				data.put(rewardb.getBytes());
				((Buffer)data).rewind();
				break;
			// case BossChosen:
			// 	data.allocate(3);
			// 	data.putChar(1, );
			// 	break;

			case ChooseNeow:
				data = ByteBuffer.allocateDirect(8);
				data.putInt(4, CoopNeowEvent.chosenOption);
				break;
			case ChooseTeamRelic:
				data = ByteBuffer.allocateDirect(8);
				data.putInt(4, TogetherManager.teamRelicScreen.selectedIndex);
				break;

			case LoseLife:
				if (AbstractDungeon.player.hasBlight("BondsOfFate")){
					if (AbstractDungeon.lastCombatMetricKey != null) {
						String killedBy = AbstractDungeon.lastCombatMetricKey;
						data = ByteBuffer.allocateDirect(8 + killedBy.getBytes().length);
						data.putInt(4, AbstractDungeon.player.getBlight("BondsOfFate").counter);

						((Buffer)data).position(8);
						data.put(killedBy.getBytes());
						((Buffer)data).rewind();
					} else {
						data = ByteBuffer.allocateDirect(8);
						data.putInt(4, AbstractDungeon.player.getBlight("BondsOfFate").counter);
					}
				}
				else {
					data = ByteBuffer.allocateDirect(8);
					data.putInt(4, AbstractDungeon.player.getBlight("StringOfFate").counter);
				}
				break;

			case Kick:
				data = ByteBuffer.allocateDirect(16);
				data.putLong(4, NewGameScreen.kick.steamUser.getAccountID());
				break;

			case GetRedKey:
				data = ByteBuffer.allocateDirect(16);
				data.putLong(4, CoopKeySharing.redKeyPlayer.steamUser.getAccountID());
				break;

			case GetBlueKey:
				data = ByteBuffer.allocateDirect(16);
				data.putLong(4, CoopKeySharing.blueKeyPlayer.steamUser.getAccountID());
				break;

			case GetGreenKey:
				data = ByteBuffer.allocateDirect(16);
				data.putLong(4, CoopKeySharing.greenKeyPlayer.steamUser.getAccountID());
				break;

			case GetPotion:
				String potionsHeld = "";

				for (AbstractPotion potion : AbstractDungeon.player.potions) {
					potionsHeld += potion.ID;
					potionsHeld += ",";
				}
				potionsHeld = potionsHeld.substring(0, potionsHeld.length() - 1);

				data = ByteBuffer.allocateDirect(8 + potionsHeld.getBytes().length);

				data.putInt(4, AbstractDungeon.player.potionSlots);

				((Buffer)data).position(8);
				data.put(potionsHeld.getBytes());
				((Buffer)data).rewind();
				break;
			case AddPotionSlot:
				data = ByteBuffer.allocateDirect(4);
				break;
			case ModifyBrainFreeze:
				data = ByteBuffer.allocateDirect(8);
				data.putInt(4, BrainFreeze.modEnergy);
				BrainFreeze.modEnergy = 0;
				break;
			case DrawMap:
				data = ByteBuffer.allocateDirect(28);
				MapCanvas c = TogetherManager.getCurrentUser().drawable[AbstractDungeon.actNum-1];
				if (c.pointQueue.size() == 0) { break; }

				Vector2[] points = c.pointQueue.remove(0);
				float xSize = c.pixmap.getWidth();
				float ySize = c.pixmap.getHeight();

				data.putFloat(4, points[0].x / xSize);
				data.putFloat(8, points[0].y / ySize);

				if (points[1] != null) {
					data.putFloat(12, points[1].x / xSize);
					data.putFloat(16, points[1].y / ySize);
				} else {
					data.putFloat(12, 0f);
					data.putFloat(16, 0f);
				}

				data.putFloat(20, c.brushSize);
				data.putFloat(24, DungeonMapScreen.offsetY / ySize);
				break;
			case ClearMap:
				data = ByteBuffer.allocateDirect(4);
				break;
			case DeckInfo:
				data = ByteBuffer.allocateDirect(12);
				data.putInt(4, AbstractDungeon.player.masterDeck.size());

				int upgraded = 0;
			    for (AbstractCard cup : AbstractDungeon.player.masterDeck.group) {
			    	upgraded += cup.timesUpgraded; 
			    } 

   				data.putInt(8, upgraded);
				break;
			case RelicInfo:
				data = ByteBuffer.allocateDirect(8);
				data.putInt(4, AbstractDungeon.player.relics.size());
				break;
			case RequestVersion:
				data = ByteBuffer.allocateDirect(4);
				break;
			default:
				data = ByteBuffer.allocateDirect(4);
				break;
		}

		data.putInt(0, type.ordinal());

		return data;
	}


	public static void updateLobbyData() {
		if (TogetherManager.currentLobby != null) {
		    matcher.setLobbyData(TogetherManager.currentLobby.steamID, "mode", TogetherManager.gameMode.toString());
		    matcher.setLobbyData(TogetherManager.currentLobby.steamID, "ascension", Integer.toString(NewMenuButtons.newGameScreen.ascensionSelectWidget.ascensionLevel));
		    matcher.setLobbyData(TogetherManager.currentLobby.steamID, "character", NewMenuButtons.newGameScreen.characterSelectWidget.getChosenOptionName());
		    matcher.setLobbyData(TogetherManager.currentLobby.steamID, "heart",   Boolean.toString(NewMenuButtons.newGameScreen.heartToggle.isTicked()));
		    matcher.setLobbyData(TogetherManager.currentLobby.steamID, "neow",    Boolean.toString(NewMenuButtons.newGameScreen.neowToggle.isTicked()));
		    matcher.setLobbyData(TogetherManager.currentLobby.steamID, "ironman", Boolean.toString(NewMenuButtons.newGameScreen.ironmanToggle.isTicked()));

		    matcher.setLobbyData(TogetherManager.currentLobby.steamID, "owner", TogetherManager.currentUser.userName);
		    matcher.setLobbyData(TogetherManager.currentLobby.steamID, "members", TogetherManager.currentLobby.getMemberNameList());
		}
	}

	public static void createLobby() {
		if (TogetherManager.gameMode == TogetherManager.mode.Coop)
			matcher.createLobby(SteamMatchmaking.LobbyType.Public, 6);
		else
			matcher.createLobby(SteamMatchmaking.LobbyType.Public, 200);
	}

	public static void setLobbyPrivate(boolean priv) {
		if (priv)
			matcher.setLobbyType(TogetherManager.currentLobby.steamID, SteamMatchmaking.LobbyType.FriendsOnly);
		else
			matcher.setLobbyType(TogetherManager.currentLobby.steamID, SteamMatchmaking.LobbyType.Public);
	}

	public static void leaveLobby(){
		if (TogetherManager.currentLobby != null) {

			// Handle Ownership transfer
        	if (TogetherManager.currentLobby.isOwner()) {
        		for (RemotePlayer player : TogetherManager.players) {
        			if (!TogetherManager.currentUser.isUser(player.steamUser)) {
		    			matcher.setLobbyData(TogetherManager.currentLobby.steamID, "owner", player.userName);
        				matcher.setLobbyOwner(TogetherManager.currentLobby.steamID, player.steamUser);
        				break;
        			}
        		}
        	}

			// Leave Lobby
	        CardCrawlGame.mainMenuScreen.screen = MainMenuScreen.CurScreen.MAIN_MENU;
    	    CardCrawlGame.mainMenuScreen.lighten();

			matcher.leaveLobby(TogetherManager.currentLobby.steamID);
			TogetherManager.clearMultiplayerData();
		}
	}

	public static void getLobbies() {
		NetworkHelper.matcher.addRequestLobbyListStringFilter("mode", TogetherManager.gameMode.toString(), SteamMatchmaking.LobbyComparison.Equal);
		NetworkHelper.matcher.addRequestLobbyListDistanceFilter(SteamMatchmaking.LobbyDistanceFilter.Worldwide);
		NetworkHelper.matcher.requestLobbyList();
	}

	public static void addPlayer(SteamID steamID) {
		// Make sure we're not adding a dupe
		for (RemotePlayer player : TogetherManager.players) {
			if (player.isUser(steamID)) {
				return;
			}
		}

        RemotePlayer newPlayer = new RemotePlayer(steamID);

        TogetherManager.players.add(newPlayer);
        TopPanelPlayerPanels.playerWidgets.add(new RemotePlayerWidget(newPlayer));
		
		TogetherManager.log("Member joined: " + newPlayer.userName);
	}

	public static void removePlayer(SteamID steamID) {
		RemotePlayer player = null;
		for (RemotePlayer p : TogetherManager.players) {
		    if (p.isUser(steamID))
		    	player = p;
		}

		if (player == null) { return; }

		if (player.isUser(TogetherManager.currentUser.steamUser) && CardCrawlGame.isInARun())
			TogetherManager.infoPopup.show(CardCrawlGame.languagePack.getUIString("Network").TEXT[0], CardCrawlGame.languagePack.getUIString("Network").TEXT[2]);

		if (embarked && TogetherManager.gameMode == TogetherManager.mode.Versus) {
			player.connection = false;
		} else {
			// Remove from player list
			TogetherManager.players.remove(player);
    		TogetherManager.log("Member left: " + player.userName);

			// Remove the widget
			for (RemotePlayerWidget widget : TopPanelPlayerPanels.playerWidgets) {
			    if (widget.player.isUser(steamID)) {
			        TopPanelPlayerPanels.playerWidgets.remove(widget);
			    }
			}
		}

		int connected = 0;
		for (RemotePlayer np : TogetherManager.players) {
			if (np.connection) { connected++; }
		}

		if ((TogetherManager.players.size() <= 1 || connected <= 1) && CardCrawlGame.isInARun()) {
			TogetherManager.infoPopup.show(CardCrawlGame.languagePack.getUIString("Network").TEXT[3], CardCrawlGame.languagePack.getUIString("Network").TEXT[4]);
		}
	}
}