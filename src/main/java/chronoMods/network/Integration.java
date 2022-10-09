package chronoMods.network;

import chronoMods.TogetherManager;
import com.badlogic.gdx.graphics.Texture;

import java.nio.ByteBuffer;

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

	// Run every frame. Returns `new Packet()` if no packet, returns the packet if there's a packet. Will run multiple times until an empty result is returned.
	void getPacket(Packet packet);

	// Send the data as a packet. All packets shuld be sent Reliably, to all players in TogetherManager.players, and the max size provided size will be less than 1200 bytes to be under the MTU threshold.
	void sendPacket(ByteBuffer data);

	// Open a direct message to the individual
	void messageUser(RemotePlayer player);

	void dispose();

	// Gets the service logo - this should probably be 92x92
	Texture getLogo();
}