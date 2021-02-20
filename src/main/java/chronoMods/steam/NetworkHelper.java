package chronoMods.steam;

import com.evacipated.cardcrawl.modthespire.lib.*;

import basemod.*;
import basemod.abstracts.*;
import basemod.interfaces.*;

import org.apache.logging.log4j.*;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
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
import com.megacrit.cardcrawl.ui.campfire.*;

import chronoMods.*;
import chronoMods.coop.*;
import chronoMods.coop.relics.*;
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
				logger.info("A packet is available of size " + bufferSize);
				try {
					net.readP2PPacket(steamID, data, NetworkHelper.channel);
					if (TogetherManager.currentLobby != null)
						parseData(data, steamID);
				}
				catch (SteamException e) {
					logger.info("Reading the packet failed: " + e.getMessage());
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

						boolean ironman = data.getInt(20)>0 ? true : false;
						NewMenuButtons.newGameScreen.ironmanToggle.setTicked(ironman);
			            NewDeathScreenPatches.Ironman = ironman;

						// seed
						Settings.seed = data.getLong(24);

						// logger.info("Updated rules with Char " + data.getInt(4) + ", Asc " + data.getInt(8) + ", and seed " + data.getLong(12));
						break;
					case Start:
						logger.info("Start Run");
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
							logger.info("Unready: " + playerInfo.userName);
						} else {
							playerInfo.ready = true;
							logger.info("Ready: " + playerInfo.userName);
						}
						break;
					// case Version:
					// 	data.getChar(1, );
					// 	break;
					case Floor:
						int floorNum = data.getInt(4);
						playerInfo.floor = floorNum;

						playerInfo.x = data.getInt(8);
						playerInfo.y = data.getInt(12);
						playerInfo.act = data.getInt(16);

						logger.info("Act: " + playerInfo.act + " - Floor: " + floorNum + " - Position: " + playerInfo.x + ", " + playerInfo.y);
						playerInfo.markMapNode();

						TopPanelPlayerPanels.SortWidgets();

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
						logger.info("Player HP: " + Hp);
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
						logger.info("Gold: " + Money);
						break;
					case Character:
						// Extract the string
						((Buffer)data).position(4);
						byte[] characterNameBytes = new byte[data.remaining()];
						data.get(characterNameBytes);
						String characterNameOut = new String(characterNameBytes);

						playerInfo.character = characterNameOut;
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
							TogetherManager.logger.info("Display Relic: " + relicID);
						}

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

						// Extract the string
						((Buffer)data).position(16);
						byte[] bytess = new byte[data.remaining()];
						data.get(bytess);
						String stringOuts = new String(bytess);

						TogetherManager.logger.info("Send card direct: " + stringOuts);

						// Creat RewardItem
						AbstractDungeon.player.masterDeck.addToTop(CardLibrary.getCopy(stringOuts, upgrades, 0));

						break;
					case TransferCard:
						// Find the correct recipient
						Long steamIDc = data.getLong(4);
						RemotePlayer recipientc = null;
						for (RemotePlayer playerc : TogetherManager.players) {
							if (playerc.steamUser.getAccountID() == steamIDc)
								recipientc = playerc; 
						}

						// Get upgrade
						int upgradec = data.getInt(12);

						// Extract the string
						((Buffer)data).position(16);
						byte[] bytesc = new byte[data.remaining()];
						data.get(bytesc);
						String stringOutc = new String(bytesc);

						TogetherManager.logger.info("Transfer card: " + stringOutc);

						// Hardcoded relic shit because that's how we roll now
						if (AbstractDungeon.player.hasBlight("PneumaticPost")){
							TogetherManager.logger.info("Upgrading");
							upgradec++;
						}

						// Creat RewardItem
			            RewardItem transferItemc = new RewardItem();
			            transferItemc.cards.clear();
			            transferItemc.cards.add(CardLibrary.getCopy(stringOutc, upgradec, 0));

			            // Add Reward to Packages for pickup
			            recipientc.packages.add(transferItemc);
						break;
					case TransferRelic:
						// Find the correct recipient
						Long steamIDr = data.getLong(4);
						RemotePlayer recipientr = null;
						for (RemotePlayer playerr : TogetherManager.players) {
							if (playerr.steamUser.getAccountID() == steamIDr)
								recipientr = playerr;
						}

						// Extract the string
						((Buffer)data).position(12);
						byte[] bytesr = new byte[data.remaining()];
						data.get(bytesr);
						String stringOutr = new String(bytesr);

						TogetherManager.logger.info("Transfer relic: " + stringOutr);

						// Creat RewardItem
			            RewardItem transferItemr = new RewardItem(RelicLibrary.getRelic(stringOutr).makeCopy());

			            // Add Reward to Packages for pickup
			            recipientr.packages.add(transferItemr);
						break;
					case TransferPotion:
						// Find the correct recipient
						Long steamIDp = data.getLong(4);
						RemotePlayer recipientp = null;
						for (RemotePlayer playerp : TogetherManager.players) {
							if (playerp.steamUser.getAccountID() == steamIDp)
								recipientp = playerp;
						}

						// Extract the string
						((Buffer)data).position(12);
						byte[] bytesp = new byte[data.remaining()];
						data.get(bytesp);
						String stringOutp = new String(bytesp);

						TogetherManager.logger.info("Transfer potion: " + stringOutp);

						// Creat RewardItem
			            RewardItem transferItemp = new RewardItem(PotionHelper.getPotion(stringOutp));

			            // Add Reward to Packages for pickup
			            recipientp.packages.add(transferItemp);
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

						TogetherManager.logger.info("Send potion: " + stringOutb);

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

						logger.info("Splits, Act: " + (actNum-1) + " - " + VersusTimer.returnTimeString(playtime));
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

				            // Unlocks a room we are leaving
							CoopEmptyRoom.LockedRoomField.locked.set(currentNodec.getRoom(), false);
						
							// Fixes the monster pool glitch
						    // if (AbstractDungeon.getCurrRoom() instanceof MonsterRoomElite) {
						    //     AbstractDungeon.eliteMonsterList.remove(0);
						    // } else if (AbstractDungeon.getCurrRoom() instanceof MonsterRoom) {
						    //     AbstractDungeon.monsterList.remove(0); }

							// Sets the next room of a multi-room
							AbstractRoom secondRoom = CoopMultiRoom.secondRoomField.secondRoom.get(currentNodec);
							if (secondRoom != null) {
								currentNodec.room = secondRoom;
								break;
							} 

							currentNodec.setRoom(new CoopEmptyRoom());
						}
						TogetherManager.logger.info("Clearing: " + xc + ", " + yc);
						break;

					case LockRoom:
						int xl = data.getInt(4);
						int yl = data.getInt(8);
						if (xl != -1 && yl != -1 && yl < 16 && !AbstractDungeon.id.equals("TheEnding")) {
				            MapRoomNode currentNodel = AbstractDungeon.map.get(yl).get(xl);

							CoopEmptyRoom.LockedRoomField.locked.set(currentNodel.getRoom(), true);
						}
						TogetherManager.logger.info("Locking: " + xl + ", " + yl);
						break;

					case ChooseNeow:
						int choice = data.getInt(4);

						if (CoopNeowEvent.screenNum == 1)
							CoopNeowEvent.rewards.get(choice).chosenBy = playerInfo.userName;
						else 
							CoopNeowEvent.penalties.get(choice).chosenBy = playerInfo.userName;

						AbstractDungeon.getCurrRoom().event.roomEventText.optionList.get(choice).msg = "#pChosen #pby #p" + playerInfo.userName + " - " + AbstractDungeon.getCurrRoom().event.roomEventText.optionList.get(choice).msg;
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

						TogetherManager.logger.info("Advance the screen!");

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
							AbstractDungeon.player.getBlight("StringOfFate").counter = counter;

							// Lower the counter and display who died
							Long steamIDl = data.getLong(8);
							RemotePlayer recipientl = null;
							for (RemotePlayer playerl : TogetherManager.players) {
								if (playerl.steamUser.getAccountID() == steamIDl)
									recipientl = playerl;
							}

							AbstractDungeon.effectList.add(new CoopDeathNotification(recipientl));

							AbstractDungeon.player.decreaseMaxHealth(AbstractDungeon.player.maxHealth / (counter+2)); // +2 because -1 for the life lost, and -1 for zero index
					        if (AbstractDungeon.player.currentHealth > AbstractDungeon.player.maxHealth)
					            AbstractDungeon.player.currentHealth = AbstractDungeon.player.maxHealth;
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
						RemotePlayer recipientk = null;
						if (TogetherManager.currentUser.steamUser.getAccountID() == steamIDk)
							NetworkHelper.leaveLobby();

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
				}
			}
		}
	}

    public static enum dataType
    {
      	Rules, Start, Ready, Version, Floor, Hp, Money, BossRelic, Finish, SendCard, TransferCard, TransferRelic, TransferPotion, UsePotion, SendPotion, EmptyRoom, BossChosen, Splits, SetDisplayRelics, ClearRoom, LockRoom, ChooseNeow, ChooseTeamRelic, LoseLife, Kick, GetRedKey, GetBlueKey, GetGreenKey, Character, GetPotion, AddPotionSlot;
      
    	private dataType() {}
    }

	public static void sendData(NetworkHelper.dataType type) {
		ByteBuffer data = NetworkHelper.generateData(type);	
		if (data == null) { return; }

		for (RemotePlayer player : TogetherManager.players) {
			logger.info("Sending packet of type " + type.toString() + " to " + player);
			try {
				boolean sent = net.sendP2PPacket(player.steamUser, data, SteamNetworking.P2PSend.Reliable, NetworkHelper.channel);
				//logger.info("SteamID is valid: " + player.steamUser.isValid());
				//logger.info("Packet of type " + type.toString() + " to " + player.steamUser.getAccountID() + " was " + sent);
			} catch (SteamException e) {
				logger.info("Sending the packet of type " + type.toString() + " failed: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	private static ByteBuffer generateData(NetworkHelper.dataType type) {
		ByteBuffer data;

		switch (type) {

			// Packets used by both
			case Rules:
        		if (!TogetherManager.currentLobby.isOwner()) { return null; }

				data = ByteBuffer.allocateDirect(32);
				// Rules are character, ascension, seed
				data.putInt(4, NewMenuButtons.newGameScreen.characterSelectWidget.getChosenOption());

				if (NewMenuButtons.newGameScreen.ascensionSelectWidget.isAscensionMode)
					data.putInt(8, NewMenuButtons.newGameScreen.ascensionSelectWidget.ascensionLevel);
				else
					data.putInt(8, 0);

				data.putInt(12, NewMenuButtons.newGameScreen.heartToggle.getTicked());
				data.putInt(16, NewMenuButtons.newGameScreen.neowToggle.getTicked());
				data.putInt(20, NewMenuButtons.newGameScreen.ironmanToggle.getTicked());

				if (Settings.seed != null){
					data.putLong(24, Settings.seed);
				} else {
					data.putLong(24, 0);
				}

				updateLobbyData();
				break;
			case Start:
				data = ByteBuffer.allocateDirect(8);
				data.putInt(4, 1);
				break;
			case Ready:
				data = ByteBuffer.allocateDirect(8);
				logger.info("Sending ready state: " + TogetherManager.getCurrentUser().userName + ", " + TogetherManager.getCurrentUser().ready);

				if (TogetherManager.getCurrentUser().ready) {
					logger.info("Sent Ready");
					data.putInt(4, 1);
				} else {
					logger.info("Sent Unready");
					data.putInt(4, 0);
				}
				break;
			// case Version:
			// 	data.allocate(3);
			// 	data.putChar(1, );
			// 	break;
			case Floor:
				data = ByteBuffer.allocateDirect(20);
				data.putInt(4, AbstractDungeon.floorNum);
				data.putInt(8, AbstractDungeon.getCurrMapNode().x);
				data.putInt(12, AbstractDungeon.getCurrMapNode().y);
				data.putInt(16, AbstractDungeon.actNum);
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
				String characterName = NewMenuButtons.newGameScreen.characterSelectWidget.getChosenOptionLocalizedName();
				data = ByteBuffer.allocateDirect(4 + characterName.getBytes().length);

				((Buffer)data).position(4);
				data.put(characterName.getBytes());
				((Buffer)data).rewind();
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

				data = ByteBuffer.allocateDirect(16 + rewards.getBytes().length);

				data.putLong(4, GhostWriter.sendPlayer.steamUser.getAccountID()); // Selected recipient
				data.putInt(12, GhostWriter.sendCard.upgraded ? 1 : 0);

				((Buffer)data).position(16);
				data.put(rewards.getBytes());
				((Buffer)data).rewind();

				GhostWriter.sendCard = null; 
				break;
			case TransferCard:
				String rewardc = TogetherManager.courierScreen.transferCard.cardID;

				data = ByteBuffer.allocateDirect(16 + rewardc.getBytes().length);

				data.putLong(4, TogetherManager.courierScreen.getRecipient().steamUser.getAccountID()); // Selected recipient
				data.putInt(12, TogetherManager.courierScreen.transferCard.upgraded ? 1 : 0);

				((Buffer)data).position(16);
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
				data.putInt(4, SiphonPump.potSlot);
				break;
			case SendPotion:
				String rewardb = SiphonPump.potName;
				TogetherManager.logger.info(SiphonPump.potName);
				data = ByteBuffer.allocateDirect(8 + rewardb.getBytes().length);

				data.putInt(4, SiphonPump.potSlot); // Selected recipient

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
				data = ByteBuffer.allocateDirect(16);
				data.putInt(4, AbstractDungeon.player.getBlight("StringOfFate").counter);
				data.putLong(8, TogetherManager.getCurrentUser().steamUser.getAccountID());
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
		
		TogetherManager.logger.info("Member joined: " + newPlayer.userName);
	}

	public static void removePlayer(SteamID steamID) {
		if (embarked) {
			for (Iterator<RemotePlayer> iterator = TogetherManager.players.iterator(); iterator.hasNext();) {
			    RemotePlayer player = iterator.next();
			    if (player.isUser(steamID)) {
			    	player.connection = false;
			    }
			}
		} else {
			// Remove from player list
			for (Iterator<RemotePlayer> iterator = TogetherManager.players.iterator(); iterator.hasNext();) {
			    RemotePlayer player = iterator.next();
			    if (player.isUser(steamID)) {
			        iterator.remove();        

	        		TogetherManager.logger.info("Member left: " + player.userName);
			    }
			}

			// Remove the widget
			for (Iterator<RemotePlayerWidget> iterator = TopPanelPlayerPanels.playerWidgets.iterator(); iterator.hasNext();) {
			    RemotePlayerWidget widget = iterator.next();
			    if (widget.player.isUser(steamID)) {
			        iterator.remove();
			    }
			}
		}
	}
}