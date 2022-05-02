package chronoMods.network.discord;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ByteArray;

import de.jcm.discordgamesdk.DiscordEventAdapter;
import de.jcm.discordgamesdk.DiscordEventHandler;
import de.jcm.discordgamesdk.GameSDKException;
import de.jcm.discordgamesdk.Result;
import de.jcm.discordgamesdk.image.ImageDimensions;
import de.jcm.discordgamesdk.image.ImageHandle;
import de.jcm.discordgamesdk.image.ImageType;
import de.jcm.discordgamesdk.user.DiscordUser;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.ReadOnlyBufferException;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.*;
import java.lang.*;
import java.nio.*;

// import javax.xml.bind.DatatypeConverter;

import chronoMods.TogetherManager;
import chronoMods.network.NetworkHelper;
import chronoMods.network.Packet;
import chronoMods.network.RemotePlayer;
import chronoMods.ui.mainMenu.NewMenuButtons;

public class DiscordPlayer extends RemotePlayer
  implements AutoCloseable {
  public DiscordUser user;
  public DiscordIntegration integration;
  public DiscordLobby lobby;
  public long peerID;
  public Pixmap pixmap;

  // Opening a connection takes time, but we might be given messages to send before then.
  // Queue them up, then send them once a connection is established.
  public ConcurrentLinkedQueue<ByteBuffer> packetsToSend = new ConcurrentLinkedQueue<>();
  public boolean isConnected = false;
  public Timer reconnectTimer;
  public boolean timedOut = false;
  public boolean peerOpened = false;
  public DiscordEventAdapter callbacks = new DiscordEventAdapter() {
    @Override
    public void onMemberUpdate(long lobbyId, long userId) {
      if (userId == integration.core.userManager().getCurrentUser().getUserId()) {
        return;
      }
      if (lobbyId != lobby.lobby.getId()) return;
      if (userId != user.getUserId()) return;
      //TogetherManager.log("OnMemberUpdate for DiscordPlayer " + user.getUsername());
      if (isConnected || peerOpened) {
        //TogetherManager.log("Updating peer");
        integration.core.networkManager().updatePeer(
            peerID,
            integration.core.lobbyManager().getMemberMetadataValue(lobbyId, userId, "route")
        );
        integration.needsFlush = true;
      }
      else {
        //TogetherManager.log("Trying to connect");
        Map<String, String> metadata = integration.core.lobbyManager().getMemberMetadata(lobbyId, userId);
        if (metadata.containsKey("peerID") && metadata.containsKey("route")) {
          TogetherManager.log("Keys found");
          peerID = Long.parseLong(metadata.get("peerID"));
          integration.core.networkManager().openPeer(peerID, metadata.get("route"));

          // main channel
          integration.core.networkManager().openChannel(peerID, (byte)0, true);

          // meta channel, used to talk about whether the main channel is open on both ends
          integration.core.networkManager().openChannel(peerID, (byte)1, true);
          integration.core.networkManager().sendMessage(peerID, (byte)1, new byte[1]);
          peerOpened = true;

          //integration.core.networkManager().flush();
          integration.needsFlush = true;
        }
      }
    }

    @Override
    public void onMessage(long peerId, byte channelId, byte[] data) {
      if (peerId != peerID) return;
      if (channelId == 1) {
        //TogetherManager.log("Got meta channel message");
        // meta channel
        // this exists because messages will only be received if the channel is open on both ends
        // when we open the channels, we also send a message on channel 1
        // if you receive a message on channel 1, you know the channels must be open on both ends,
        // and it's safe to start sending real messages
        if (isConnected) return;
        isConnected = true;

        // since we were the first to open the channels, the other party never got our channel 1 message
        // send them another to let them know it's open
        //TogetherManager.log("Sending one back");
        integration.core.networkManager().sendMessage(peerID, (byte)1, new byte[0]);

        // since we know the connection's open, send all queued messages on the main channel
        for (ByteBuffer b = packetsToSend.poll(); b != null; b = packetsToSend.poll()) {
          //TogetherManager.log("Sending a queued message");
          //TogetherManager.log("Queued message is: " + b.order());
          byte[] array = toBytes(b);
          //TogetherManager.log(DatatypeConverter.printHexBinary(array));
          integration.core.networkManager().sendMessage(peerID, (byte)0, array);
        }
        //integration.core.networkManager().flush();
        integration.needsFlush = true;
      }
      else {
        // real message
        //TogetherManager.log("Got main channel message");
        //TogetherManager.log("Length: " + data.length);
        //TogetherManager.log(DatatypeConverter.printHexBinary(data));
        ByteBuffer buf = ByteBuffer.allocate(data.length);
        buf.put(data);
        ((Buffer)buf).rewind();
        //TogetherManager.log("Incoming message is: " + buf.order());
        integration.incomingMessages.add(new Packet(DiscordPlayer.this, buf));
      }
    }

    @Override
    public void onMemberConnect(long lobbyId, long userId) {
      if (lobbyId != lobby.lobby.getId()) return;
      if (userId != user.getUserId()) return;
      if (timedOut) return;
      if (reconnectTimer != null) {
        reconnectTimer.cancel();
        reconnectTimer = null;
        // simulate a route update to reestablish connection
        lobby.callbacks.onRouteUpdate(integration.ourRoute);
      }
    }

    @Override
    public void onMemberDisconnect(long lobbyId, long userId) {
      if (lobbyId != lobby.lobby.getId()) return;
      if (userId != user.getUserId()) return;
      if (lobby.lobbyLeft) {
        TogetherManager.log("OnMemberDisconnect: lobbyLeft is true");
      }
      else {
        TogetherManager.log("OnMemberDisconnect: lobbyLeft is false");
      }
      close();
      Runnable completeDisconnect = () -> {
        TogetherManager.log("Completing disconnect");
        if (reconnectTimer != null) reconnectTimer.cancel();
        timedOut = true;
        lobby.callbacks.removeListener(callbacks);
        NetworkHelper.removePlayer(DiscordPlayer.this);
        NewMenuButtons.newGameScreen.playerList.setPlayers(TogetherManager.players);
        if (TogetherManager.currentLobby.isOwner()) {
          lobby.setMetadata(DiscordLobby.map("members", lobby.getMemberNameList()));
        }
      };
      if (NetworkHelper.embarked && !lobby.lobbyLeft) {
        TogetherManager.log("Starting timer...");
        reconnectTimer = new Timer();
        reconnectTimer.schedule(
            new TimerTask() {
              @Override
              public void run() {
                TogetherManager.log("Posting runnable...");
                DiscordIntegration.postRunnable(completeDisconnect);
              }
            },
            1000L
        );
      }
      else {
        completeDisconnect.run();
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

    lobby.callbacks.addListener(callbacks);
    // parse metadata's initial values
    callbacks.onMemberUpdate(lobby.lobby.getId(), user.getUserId());
  }

  public void updateAvatar() {
    integration.core.imageManager().fetch(
        new ImageHandle(ImageType.USER, user.getUserId(), 128),
        false,
        (result, handle) -> {
          if (result != Result.OK) {
            TogetherManager.log("Got result " + result.name() + " trying to fetch avatar for user with id " + user.getUserId());
            return;
          }
          ImageDimensions dimensions = integration.core.imageManager().getDimensions(handle);
          pixmap = new Pixmap(dimensions.getWidth(), dimensions.getHeight(), Pixmap.Format.RGBA8888);
          // both Discord and our Pixmap use RGBA8888
          // unfortunately, BufferedImage.getRGB can only return ARGB8888, so we'll do this directly with bytes
          // even though using an image class would be more elegant
          // BufferedImage source = integration.core.imageManager().getAsBufferedImage(handle, dimensions);
          ByteBuffer source = ByteBuffer.wrap(integration.core.imageManager().getData(handle, dimensions));
          for (int y = 0; y < dimensions.getHeight(); y++) {
            for (int x = 0; x < dimensions.getWidth(); x++) {
              pixmap.drawPixel(x, y, source.getInt());
            }
          }
          // // Runnable needed to establish GL Context
          // Gdx.app.postRunnable(() -> {
          //   for (RemotePlayer player : TogetherManager.players) {
          //     if (player.isUser(this)) {
          //       player.portraitImg = new Texture(pixmap);
          //       player.getPortrait().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
          //     }
          //   }
          // });
        }
      );
  }

  public Texture getPortrait() {
    if (portraitImg == null) {
      if (pixmap == null)
        portraitImg = new Texture(new Pixmap(120, 120, Pixmap.Format.RGBA8888));
      else 
        portraitImg = new Texture(pixmap);
      portraitImg.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
    }

    return portraitImg;
  }

  public void sendMessage(ByteBuffer bytes) {
    if (user.getUserId() == integration.core.userManager().getCurrentUser().getUserId()) {
      ((Buffer)bytes).rewind();
      //TogetherManager.log("loopback message buffer is: " + bytes.order());
      //TogetherManager.log(DatatypeConverter.printHexBinary(toBytes(bytes)));
      integration.incomingMessages.add(new Packet(this, bytes));
      return;
    }
    if (isConnected) {
      //TogetherManager.log("sendMessage buffer is: " + bytes.order());
      byte[] array = toBytes(bytes);
      //TogetherManager.log(DatatypeConverter.printHexBinary(array));
      integration.core.networkManager().sendMessage(peerID, (byte)0, array);
    }
    else {
      packetsToSend.add(bytes);
    }
  }

  public static byte[] toBytes(ByteBuffer buf) {
    byte[] array;
    if (buf.hasArray()) array = buf.array();
    else {
      ((Buffer)buf).rewind();
      array = new byte[buf.remaining()];
      buf.get(array, 0, buf.remaining());
      ((Buffer)buf).rewind();
    }
    return array;
  }

  public boolean isUser(Object player) {
    if (player instanceof DiscordPlayer)
      return ((DiscordPlayer) player).user.getUserId() == user.getUserId();
    return false;
  }

  public long getAccountID() { return user.getUserId(); }
  public void close() {
    if (isConnected) {
      isConnected = false;
      try {
        integration.core.networkManager().closeChannel(peerID, (byte) 0);
        integration.core.networkManager().closeChannel(peerID, (byte) 1);
        integration.core.networkManager().closePeer(peerID);
      }
      catch (GameSDKException e) {
        // maybe this is fine?
      }
      peerOpened = false;
    }
  }
}
