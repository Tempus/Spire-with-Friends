package chronoMods.steam;

import com.evacipated.cardcrawl.modthespire.lib.*;

import basemod.*;
import basemod.abstracts.*;
import basemod.interfaces.*;

import org.apache.logging.log4j.*;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.characters.AbstractPlayer;

import chronoMods.*;
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

			if (data.capacity() != bufferSize) {
				data = ByteBuffer.allocateDirect(bufferSize);
			}

			if (bufferSize != 0) {
				logger.info("A packet is available of size " + bufferSize);
				try {
					net.readP2PPacket(steamID, data, NetworkHelper.channel);
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
						NewMenuButtons.newGameScreen.characterSelectWidget.selectOption(data.getInt(4));
						// Ascenseion
						NewMenuButtons.newGameScreen.ascensionSelectWidget.ascensionLevel = data.getInt(8);
						if (NewMenuButtons.newGameScreen.ascensionSelectWidget.ascensionLevel == 0) {
							NewMenuButtons.newGameScreen.ascensionSelectWidget.isAscensionMode = false;
						} else {
							NewMenuButtons.newGameScreen.ascensionSelectWidget.isAscensionMode = true;
						}
						// seed
						Settings.seed = data.getLong(12);

						break;
					case Start:
			            CardCrawlGame.chosenCharacter = NewMenuButtons.newGameScreen.characterSelectWidget.getChosenClass();
			            CardCrawlGame.mainMenuScreen.isFadingOut = true;
			            CardCrawlGame.mainMenuScreen.fadeOutMusic();
			            Settings.isTrial = true;
			            Settings.isDailyRun = false;
			            Settings.isEndless = false;
			            if (TogetherManager.gameMode == TogetherManager.mode.Coop) {
			              Settings.isFinalActAvailable = true; }

			            AbstractDungeon.generateSeeds();

						logger.info("Start Run");
						break;
					case Ready:
						char start = data.getChar(4);
						if (start == 0) {
							playerInfo.ready = false;
						} else {
							playerInfo.ready = true;
						}
						logger.info("Ready: " + playerInfo.userName);
						break;
					// case Version:
					// 	data.getChar(1, );
					// 	break;
					case Floor:
						int floorNum = data.getInt(4);
						playerInfo.floor = floorNum;

						playerInfo.x = data.getInt(8);
						playerInfo.y = data.getInt(12);

						playerInfo.markMapNode();

						logger.info("Floor: " + floorNum + " - Position: " + playerInfo.x + ", " + playerInfo.y);
						break;
					case Hp:
						int Hp = data.getInt(4);
						playerInfo.hp = Hp;
						logger.info("Player HP: " + Hp);
						break;
					case Money:
						int Money = data.getInt(4);
						playerInfo.gold = Money;
						logger.info("Gold: " + Money);
						break;
					// case BossRelic:
					// 	data.getChar(1, );
					// 	break;
					// case Finish:
					// 	data.getChar(1, );
					// 	break;
					// case TransferCard:
					// 	data.getChar(1, );
					// 	break;
					// case TransferRelic:
					// 	data.getChar(1, );
					// 	break;
					// case EmptyRoom:
					// 	data.getChar(1, );
					// 	break;
					// case BossChosen:
					// 	data.getChar(1, );
					// 	break;
				}
			}
		}
	}

    public static enum dataType
    {
      	Rules, Start, Ready, Version, Floor, Hp, Money, BossRelic, Finish, TransferCard, TransferRelic, EmptyRoom, BossChosen;
      
    	private dataType() {}
    }

	public static void sendData(NetworkHelper.dataType type) {
		ByteBuffer data = NetworkHelper.generateData(type);	

		for (RemotePlayer player:  TogetherManager.players) {
			try {
				boolean sent = net.sendP2PPacket(player.steamUser, data, SteamNetworking.P2PSend.Reliable, NetworkHelper.channel);
				logger.info("SteamID is valid: " + player.steamUser.isValid());
				logger.info("Packet of type " + type.toString() + " to " + player.steamUser.getAccountID() + " was " + sent);
			} catch (SteamException e) {
				logger.info("Sending the packet of type " + type.toString() + " failed: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	private static ByteBuffer generateData(NetworkHelper.dataType type) {
		ByteBuffer data;

		switch (type) {
			case Rules:
				data = ByteBuffer.allocateDirect(20);
				// Rules are character, ascension, seed
				data.putInt(4, NewMenuButtons.newGameScreen.characterSelectWidget.getChosenOption());
				data.putInt(8, NewMenuButtons.newGameScreen.ascensionSelectWidget.ascensionLevel);
				data.putLong(12, Settings.seed);
				break;
			case Start:
				data = ByteBuffer.allocateDirect(8);
				data.putInt(4, 1);
				break;
			case Ready:
				data = ByteBuffer.allocateDirect(8);
				if (TogetherManager.currentUser.ready) {
					data.putInt(4, 1);
				} else {
					data.putInt(4, 0);
				}
				break;
			// case Version:
			// 	data.allocate(3);
			// 	data.putChar(1, );
			// 	break;
			case Floor:
				data = ByteBuffer.allocateDirect(16);
				data.putInt(4, AbstractDungeon.floorNum);
				data.putInt(8, AbstractDungeon.getCurrMapNode().x);
				data.putInt(12, AbstractDungeon.getCurrMapNode().y);
				break;
			case Hp:
				data = ByteBuffer.allocateDirect(8);
				data.putInt(4, AbstractDungeon.player.currentHealth);
				break;
			case Money:
				data = ByteBuffer.allocateDirect(8);
				data.putInt(4, AbstractDungeon.player.gold);
				break;
			// case BossRelic:
			// 	data.allocate(3);
			// 	data.putChar(1, );
			// 	break;
			// case Finish:
			// 	data.allocate(3);
			// 	data.putChar(1, );
			// 	break;
			// case TransferCard:
			// 	data.allocate(3);
			// 	data.putChar(1, );
			// 	break;
			// case TransferRelic:
			// 	data.allocate(3);
			// 	data.putChar(1, );
			// 	break;
			// case EmptyRoom:
			// 	data.allocate(3);
			// 	data.putChar(1, );
			// 	break;
			// case BossChosen:
			// 	data.allocate(3);
			// 	data.putChar(1, );
			// 	break;
			default:
				data = ByteBuffer.allocateDirect(4);
				break;
		}

		data.putInt(0, type.ordinal());

		return data;
	}


	public static void createLobby() {
		matcher.createLobby(SteamMatchmaking.LobbyType.Public, 6);
	}

	public static void leaveLobby(){
		if (TogetherManager.currentLobby != null) {
			matcher.leaveLobby(TogetherManager.currentLobby.steamID);
			TogetherManager.currentLobby = null;
		}
	}

	public static ArrayList<SteamLobby> getLobbies() {
		NetworkHelper.matcher.addRequestLobbyListStringFilter("mode", TogetherManager.gameMode.toString(), SteamMatchmaking.LobbyComparison.Equal);
		NetworkHelper.matcher.requestLobbyList();

		return steamLobbies;
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
	}

	public static void removePlayer(SteamID steamID) {
		// Remove from player list
		for (Iterator<RemotePlayer> iterator = TogetherManager.players.iterator(); iterator.hasNext();) {
		    RemotePlayer player = iterator.next();
		    if (player.isUser(steamID)) {
		        iterator.remove();
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