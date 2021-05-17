package chronoMods.network;

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

import chronoMods.*;
import chronoMods.coop.*;
import chronoMods.coop.relics.*;
import chronoMods.coop.drawable.*;
import chronoMods.network.*;
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

public interface Integration {

	// Initialize the integration
	void initialize();
	boolean isInitialized();

	// Create Current User
	RemotePlayer makeCurrentUser();

	// Updates the integrations lobby data
	void updateLobbyData();

	// Creates a lobby on the integration service
	void createLobby(TogetherManager.mode gameMode);
	void setLobbyPrivate(boolean priv);
	
	// Retrieves a list of lobbies. These arrive via callback, and the results are place in NetworkHelper.lobbies 
	void getLobbies();

	// Run every frame. Returns null if no packet, returns the packet if there's a packet. Will run multiple times until a null result is returned.
	Packet getPacket();

	// Send the data as a packet. All packets shuld be sent Reliably, to all players in TogetherManager.players, and the max size provided size will be less than 1200 bytes to be under the MTU threshold.
	void sendPacket(ByteBuffer data);

	// Open a direct message to the individual
	void messageUser(RemotePlayer player);

	// Gets the service logo - this should probably be 92x92
	Texture getLogo();
}