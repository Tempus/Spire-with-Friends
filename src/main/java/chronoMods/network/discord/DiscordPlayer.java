package chronoMods.network.discord;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

import de.jcm.discordgamesdk.DiscordEventAdapter;
import de.jcm.discordgamesdk.Result;
import de.jcm.discordgamesdk.image.ImageDimensions;
import de.jcm.discordgamesdk.image.ImageHandle;
import de.jcm.discordgamesdk.image.ImageType;
import de.jcm.discordgamesdk.user.DiscordUser;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import chronoMods.TogetherManager;
import chronoMods.network.NetworkHelper;
import chronoMods.network.Packet;
import chronoMods.network.RemotePlayer;
import chronoMods.ui.mainMenu.NewMenuButtons;

public class DiscordPlayer extends RemotePlayer {
  public DiscordUser user;
  public DiscordIntegration integration;
  public DiscordLobby lobby;
  public long peerID;
  // Opening a connection takes time, but we might be given messages to send before then.
  // Queue them up, then send them once a connection is established.
  public ConcurrentLinkedQueue<ByteBuffer> packetsToSend = new ConcurrentLinkedQueue<>();
  public boolean isConnected = false;
  public DiscordEventAdapter callbacks = new DiscordEventAdapter() {
    @Override
    public void onMemberUpdate(long lobbyId, long userId) {
      if (lobbyId != lobby.lobby.getId()) return;
      if (userId != user.getUserId()) return;
      if (isConnected) {
        integration.core.networkManager().updatePeer(
            peerID,
            integration.core.lobbyManager().getMemberMetadataValue(lobbyId, userId, "route")
        );
      }
      else {
        Map<String, String> metadata = integration.core.lobbyManager().getMemberMetadata(lobbyId, userId);
        if (metadata.containsKey("peerID") && metadata.containsKey("route")) {
          peerID = Long.parseLong(metadata.get("peerID"));
          integration.core.networkManager().openPeer(peerID, metadata.get("route"));

          // main channel
          integration.core.networkManager().openChannel(peerID, (byte)0, true);

          // meta channel, used to talk about whether the main channel is open on both ends
          integration.core.networkManager().openChannel(peerID, (byte)1, true);
          integration.core.networkManager().sendMessage(peerID, (byte)1, new byte[0]);

          integration.core.networkManager().flush();
        }
      }
    }

    @Override
    public void onMessage(long peerId, byte channelId, byte[] data) {
      if (peerId != peerID) return;
      if (channelId == 1) {
        // meta channel
        // this exists because messages will only be received if the channel is open on both ends
        // when we open the channels, we also send a message on channel 1
        // if you receive a message on channel 1, you know the channels must be open on both ends,
        // and it's safe to start sending real messages
        if (isConnected) return;
        isConnected = true;

        // since we were the first to open the channels, the other party never got our channel 1 message
        // send them another to let them know it's open
        integration.core.networkManager().sendMessage(peerID, (byte)1, new byte[0]);

        // since we know the connection's open, send all queued messages on the main channel
        for (ByteBuffer b = packetsToSend.poll(); b != null; b = packetsToSend.poll()) {
          integration.core.networkManager().sendMessage(peerID, (byte)0, b.array());
        }
        integration.core.networkManager().flush();
      }
      else {
        // real message
        integration.incomingMessages.add(new Packet(DiscordPlayer.this, ByteBuffer.wrap(data)));
      }
    }

    @Override
    public void onMemberDisconnect(long lobbyId, long userId) {
      if (lobbyId != lobby.lobby.getId()) return;
      if (userId != user.getUserId()) return;
      integration.eventHandler.removeListener(this);
      NetworkHelper.removePlayer(DiscordPlayer.this);
      NewMenuButtons.newGameScreen.playerList.setPlayers(TogetherManager.players);
      if (TogetherManager.currentLobby.isOwner()) {
        lobby.setMetadata(DiscordLobby.map("members", lobby.getMemberNameList()));
      }
    }
  };
  public DiscordPlayer(DiscordUser user, DiscordIntegration integration, DiscordLobby lobby) {
    super();
    this.user = user;
    this.lobby = lobby;
    this.integration = integration;

    this.userName = user.getUsername();
    updateAvatar();

    integration.eventHandler.addListener(callbacks);
    // parse metadata's initial values
    callbacks.onMemberUpdate(lobby.lobby.getId(), user.getUserId());
  }

  public void updateAvatar() {
    integration.core.imageManager().fetch(
        new ImageHandle(ImageType.USER, user.getUserId(), 128),
        false,
        (result, handle) -> {
          if (result != Result.OK) {
            TogetherManager.log("Got result" + result.name() + "trying to fetch avatar for user with id " + user.getUserId());
            return;
          }
          ImageDimensions dimensions = integration.core.imageManager().getDimensions(handle);
          Pixmap pixmap = new Pixmap(dimensions.getWidth(), dimensions.getHeight(), Pixmap.Format.RGBA8888);
          BufferedImage source = integration.core.imageManager().getAsBufferedImage(handle, dimensions);
          for (int x = 0; x < dimensions.getWidth(); x++) {
            for (int y = 0; y < dimensions.getHeight(); y++) {
              pixmap.drawPixel(x, y, source.getRGB(x, y));
            }
          }
          // Runnable needed to establish GL Context
          Gdx.app.postRunnable(() -> {
            for (RemotePlayer player : TogetherManager.players) {
              if (player.isUser(this)) {
                player.portraitImg = new Texture(pixmap);
                player.portraitImg.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
              }
            }
          });
        }
      );
  }

  public void sendMessage(ByteBuffer bytes) {
    if (isConnected) {
      integration.core.networkManager().sendMessage(peerID, (byte)0, bytes.array());
    }
    else {
      packetsToSend.add(bytes);
    }
  }

  public boolean isUser(Object player) {
    if (player instanceof DiscordPlayer)
      return ((DiscordPlayer) player).user.getUserId() == user.getUserId();
    return false;
  }

  public Long getAccountID() { return user.getUserId(); }
}
