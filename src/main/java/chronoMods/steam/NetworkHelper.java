package chronoMods.steam;

import com.evacipated.cardcrawl.modthespire.lib.*;

import basemod.*;
import basemod.abstracts.*;
import basemod.interfaces.*;

import org.apache.logging.log4j.*;

import com.megacrit.cardcrawl.core.CardCrawlGame;
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
			if (playerInfo.steamUser.getAccountID() == player.getAccountID()) {
				dataType type = dataType.values()[data.getInt()];

				switch (type) {
					// case NetworkHelper.dataType.Rules:
					// 	data.getChar(1, );
					// 	break;
					case Start:
						int start = data.getInt(4);
						logger.info("Start Run: " + start);
						break;
					// case NetworkHelper.dataType.Ready:
					// 	data.getChar(1, );
					// 	break;
					// case NetworkHelper.dataType.Version:
					// 	data.getChar(1, );
					// 	break;
					case Floor:
						int floorNum = data.getInt(4);
						playerInfo.floor = floorNum;

						playerInfo.x = data.getInt(8);
						playerInfo.y = data.getInt(12);

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
					// case NetworkHelper.dataType.BossRelic:
					// 	data.getChar(1, );
					// 	break;
					// case NetworkHelper.dataType.Finish:
					// 	data.getChar(1, );
					// 	break;
					// case NetworkHelper.dataType.TransferCard:
					// 	data.getChar(1, );
					// 	break;
					// case NetworkHelper.dataType.TransferRelic:
					// 	data.getChar(1, );
					// 	break;
					// case NetworkHelper.dataType.EmptyRoom:
					// 	data.getChar(1, );
					// 	break;
					// case NetworkHelper.dataType.BossChosen:
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
			// case NetworkHelper.dataType.Rules:
			// 	data.allocate(3);
			// 	data.putChar(1, );
			// 	break;
			case Start:
				data = ByteBuffer.allocateDirect(8);
				data.putInt(4, 1);
				break;
			// case NetworkHelper.dataType.Ready:
			// 	data.allocate(3);
			// 	data.putChar(1, );
			// 	break;
			// case NetworkHelper.dataType.Version:
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
			// case NetworkHelper.dataType.BossRelic:
			// 	data.allocate(3);
			// 	data.putChar(1, );
			// 	break;
			// case NetworkHelper.dataType.Finish:
			// 	data.allocate(3);
			// 	data.putChar(1, );
			// 	break;
			// case NetworkHelper.dataType.TransferCard:
			// 	data.allocate(3);
			// 	data.putChar(1, );
			// 	break;
			// case NetworkHelper.dataType.TransferRelic:
			// 	data.allocate(3);
			// 	data.putChar(1, );
			// 	break;
			// case NetworkHelper.dataType.EmptyRoom:
			// 	data.allocate(3);
			// 	data.putChar(1, );
			// 	break;
			// case NetworkHelper.dataType.BossChosen:
			// 	data.allocate(3);
			// 	data.putChar(1, );
			// 	break;
			default:
				data = ByteBuffer.allocate(1);
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
			matcher.leaveLobby(TogetherManager.currentLobby);
			TogetherManager.currentLobby = null;
		}
	}

	public static ArrayList<SteamLobby> getLobbies() {
		NetworkHelper.matcher.requestLobbyList();

		return steamLobbies;
	}

	public static void addPlayer(SteamID steamID) {
        RemotePlayer newPlayer = new RemotePlayer(steamID);

        TogetherManager.players.add(newPlayer);
        TopPanelPlayerPanels.playerWidgets.add(new RemotePlayerWidget(newPlayer));
	}

	public static void removePlayer(SteamID steamID) {
		// Remove from player list
		for (Iterator<RemotePlayer> iterator = TogetherManager.players.iterator(); iterator.hasNext();) {
		    RemotePlayer player = iterator.next();
		    if (player.steamUser.getAccountID() == steamID.getAccountID()) {
		        iterator.remove();
		    }
		}

		// Remove the widget
		for (Iterator<RemotePlayerWidget> iterator = TopPanelPlayerPanels.playerWidgets.iterator(); iterator.hasNext();) {
		    RemotePlayerWidget player = iterator.next();
		    if (player.player.steamUser.getAccountID() == steamID.getAccountID()) {
		        iterator.remove();
		    }
		}
	}
}