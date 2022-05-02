package chronoMods.network.steam;

import com.evacipated.cardcrawl.modthespire.lib.*;

import basemod.*;
import basemod.abstracts.*;
import basemod.interfaces.*;

import org.apache.logging.log4j.*;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.files.FileHandle;

import chronoMods.*;
import chronoMods.coop.*;
import chronoMods.coop.relics.*;
import chronoMods.coop.drawable.*;
import chronoMods.network.*;
import chronoMods.network.steam.*;
import chronoMods.ui.deathScreen.*;
import chronoMods.ui.hud.*;
import chronoMods.ui.lobby.*;
import chronoMods.ui.mainMenu.*;
import chronoMods.utilities.*;

import java.util.*;
import java.lang.*;
import java.nio.*;

import com.codedisaster.steamworks.*;

public class SteamIntegration implements Integration {

	public static SteamCallbacks callbacks;

	public static SteamMatchmaking matcher;
	public static SteamFriends friends;
	public static SteamNetworking net;
	public static SteamUtils utils;

	public static int channel = 0;

	public Texture logo;

	// Convenience Function
	public static SteamPlayer getPlayer(SteamID steamID) {
		for (RemotePlayer p : TogetherManager.players) {
		    if (p.isUser(steamID.getAccountID()) && p instanceof SteamPlayer) {
		    	return (SteamPlayer)p;
		    }
		}

		return null;
	}

	// Initialize the integration
	public void initialize() {
		if (!(CardCrawlGame.publisherIntegration instanceof com.megacrit.cardcrawl.integrations.steam.SteamIntegration)) return;
		SteamApps steamApps = (SteamApps)ReflectionHacks.getPrivate(CardCrawlGame.publisherIntegration, com.megacrit.cardcrawl.integrations.steam.SteamIntegration.class, "steamApps");

		callbacks = new SteamCallbacks();

        matcher = new SteamMatchmaking(callbacks);
        net = new SteamNetworking(callbacks);
        utils = new SteamUtils(callbacks);
        friends = new SteamFriends(callbacks);

		logo = ImageMaster.loadImage("chrono/images/steam.png");
	}

	public RemotePlayer makeCurrentUser() {
        SteamUser steamUser = (SteamUser)ReflectionHacks.getPrivate(CardCrawlGame.publisherIntegration, com.megacrit.cardcrawl.integrations.steam.SteamIntegration.class, "steamUser");

        TogetherManager.log("Current User made for Steam");

        RemotePlayer r = new SteamPlayer(steamUser.getSteamID());
        if (TogetherManager.config.has("mark"))
            r.bingoMark = TogetherManager.customMark;

        return r;
	}

	public boolean isInitialized() {
		return (matcher != null && net != null && utils != null && friends != null);
	}

	// Updates the integrations lobby data
	public void updateLobbyData() {}

	// Creates a lobby on the integration service
	public void createLobby(TogetherManager.mode gameMode) {
		if (gameMode == TogetherManager.mode.Coop)
			matcher.createLobby(SteamMatchmaking.LobbyType.Public, 6);
		else
			matcher.createLobby(SteamMatchmaking.LobbyType.Public, 200);
	}

	public void setLobbyPrivate(boolean priv) {
		if (priv)
			matcher.setLobbyType((SteamID)TogetherManager.currentLobby.getID(), SteamMatchmaking.LobbyType.FriendsOnly);
		else
			matcher.setLobbyType((SteamID)TogetherManager.currentLobby.getID(), SteamMatchmaking.LobbyType.Public);		
	}
	
	// Retrieves a list of lobbies. These arrive via callback, and the results are place in NetworkHelper.lobbies 
	public void getLobbies() {
		matcher.addRequestLobbyListStringFilter("mode", TogetherManager.gameMode.toString(), SteamMatchmaking.LobbyComparison.Equal);
		matcher.addRequestLobbyListDistanceFilter(SteamMatchmaking.LobbyDistanceFilter.Worldwide);
		matcher.requestLobbyList();		
	}

	// Run every frame. Returns null if no packet, returns the packet if there's a packet. Will run multiple times until a null result is returned.
	public Packet getPacket() {
		int[] bufferSize = new int[1];
		net.isP2PPacketAvailable(channel, bufferSize);

		if (bufferSize[0] != 0) {
			ByteBuffer data = ByteBuffer.allocateDirect(bufferSize[0]);
			SteamID steamID = new SteamID();

			TogetherManager.log("We have a packet of size " + bufferSize[0]);
			try {
				net.readP2PPacket(steamID, data, channel);
			}
			catch (SteamException e) {
				TogetherManager.log("Reading the packet failed: " + e.getMessage());
				e.printStackTrace();
			}

			return new Packet(getPlayer(steamID), data);
		}

		return new Packet();
	}

	// Send the data as a packet. All packets shuld be sent Reliably, to all players in TogetherManager.players, and the max size provided size will be less than 1200 bytes to be under the MTU threshold.
	public void sendPacket(ByteBuffer data) {
		for (RemotePlayer player : TogetherManager.players) {
			try {
				boolean sent = net.sendP2PPacket(((SteamPlayer)player).steamUser, data, SteamNetworking.P2PSend.Reliable, channel);
			} catch (SteamException e) {
				e.printStackTrace();
			}
		}		
	}

	public void messageUser(RemotePlayer player) {
		friends.activateGameOverlayToUser(SteamFriends.OverlayToUserDialog.Chat, ((SteamPlayer)player).steamUser);
	}

	public Texture getLogo() { return logo; }

	@Override
	public void dispose() {

	}
}