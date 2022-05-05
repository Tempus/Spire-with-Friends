package chronoMods.network.discord;

import com.megacrit.cardcrawl.core.CardCrawlGame;

import de.jcm.discordgamesdk.ActivityManager;
import de.jcm.discordgamesdk.DiscordEventAdapter;
import de.jcm.discordgamesdk.DiscordEventHandler;
import de.jcm.discordgamesdk.Result;
import de.jcm.discordgamesdk.activity.Activity;
import de.jcm.discordgamesdk.lobby.Lobby;
import de.jcm.discordgamesdk.lobby.LobbyMemberTransaction;
import de.jcm.discordgamesdk.lobby.LobbyTransaction;
import de.jcm.discordgamesdk.lobby.LobbyType;
import de.jcm.discordgamesdk.user.DiscordUser;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import chronoMods.TogetherManager;
import chronoMods.network.NetworkHelper;
import chronoMods.network.RemotePlayer;
import chronoMods.network.steam.SteamPlayer;
import chronoMods.ui.hud.RemotePlayerWidget;
import chronoMods.ui.hud.TopPanelPlayerPanels;
import chronoMods.ui.lobby.NewScreenUpdateRender;
import chronoMods.ui.mainMenu.NewMenuButtons;


public class DiscordLobby extends chronoMods.network.Lobby {
  public DiscordIntegration integration;
  public Lobby lobby;
  public Map<String, String> metadata;
  public DiscordLobby(Lobby lobby, DiscordIntegration integration) {
    super(integration);
    this.integration = integration;
    this.lobby = lobby;
    metadata = integration.core.lobbyManager().getLobbyMetadata(lobby);
    fetchAllMetadata();
  }
  @Override
  public String getOwnerName() {
    try {
      return integration.core.lobbyManager().getMemberUser(lobby, lobby.getOwnerId()).getUsername();
    } catch (Exception e) {
      return "Unknown Discord Player";
    }
  }

  @Override
  public long getOwner() {
    long id = lobby.getOwnerId();
    return id;
  }

  @Override
  public boolean isOwner() {
    boolean isOwner = lobby.getOwnerId() == integration.core.userManager().getCurrentUser().getUserId();
    return isOwner;
  }

  @Override
  public void newOwner() {
    for (RemotePlayer player : TogetherManager.players) {
      if (!TogetherManager.currentUser.isUser(player) && player instanceof DiscordPlayer) {
        update(txn -> {
          txn.setOwner(((DiscordPlayer)player).user.getUserId());
          txn.setMetadata("owner", player.userName);
        });
        return;
      }
    }
  }

  /*
  @Override
  public void updateOwner() {
    LobbyTransaction txn = integration.core.lobbyManager().getLobbyUpdateTransaction(lobby);
    txn.setOwner(integration.core.userManager().getCurrentUser().getUserId());
    integration.core.lobbyManager().updateLobby(lobby, txn);
  }
  */

  @Override
  public int getMemberCount() {
    int count = integration.core.lobbyManager().memberCount(lobby);
    return count;
  }

  @Override
  public CopyOnWriteArrayList<RemotePlayer> getLobbyMembers() {
    CopyOnWriteArrayList<RemotePlayer> players = integration.core.lobbyManager().getMemberUsers(lobby).stream()
        .map(u -> new DiscordPlayer(u, integration, DiscordLobby.this))
        .peek(p -> TopPanelPlayerPanels.playerWidgets.add(new RemotePlayerWidget(p)))
        .collect(Collectors.toCollection(CopyOnWriteArrayList::new));
    return players;
  }

  @Override
  public String getMemberNameList() {
    String names = integration.core.lobbyManager().getMemberUsers(lobby)
        .stream()
        .map(DiscordUser::getUsername)
        .collect(Collectors.joining("\t"));
    return names;
  }

  @Override
  public Object getID() {
    return lobby;
  }

  public boolean lobbyLeft = false;
  @Override
  public void leaveLobby() {
    stopActivity();
    integration.eventHandler.removeListener(callbacks);
    lobbyLeft = true;
    TogetherManager.players
        .forEach(p -> callbacks.onMemberDisconnect(lobby.getId(), p.getAccountID()));
    integration.core.lobbyManager().disconnectLobby(lobby);
  }

  @Override
  public void setJoinable(boolean toggle) {
    TogetherManager.log("Discord lobby setJoinable: " + toggle);
    update(txn -> txn.setLocked(!toggle), r -> updateActivity());
  }

  @Override
  public void setPrivate(boolean toggle) {
    update(txn -> txn.setType(toggle ? LobbyType.PRIVATE : LobbyType.PUBLIC), r -> updateActivity());
  }

  @Override
  public void join() {
    integration.core.lobbyManager().connectLobby(lobby, (r, l) -> {
      if (r == Result.OK) {
        lobbyLeft = false;
        metadata = integration.core.lobbyManager().getLobbyMetadata(lobby);
        fetchAllMetadata();
        setUpNetworking();
        startActivity();
        TogetherManager.currentLobby = DiscordLobby.this;
        TogetherManager.players = TogetherManager.currentLobby.getLobbyMembers();
        TogetherManager.currentUser = integration.makeCurrentUser();

        NewScreenUpdateRender.joinFlag = true;
        NetworkHelper.sendData(NetworkHelper.dataType.Version);
      }
      else {
        TogetherManager.infoPopup.show(CardCrawlGame.languagePack.getUIString("Network").TEXT[5], CardCrawlGame.languagePack.getUIString("Network").TEXT[6]);
      }
    });
  }

  public static Map<String, String> map(String... data) {
    if (data.length % 2 != 0) {
      throw new IllegalArgumentException("Must have an even number of arguments");
    }
    Map<String, String> result = new HashMap<>();
    for (int i = 0; i < data.length; i += 2) {
      result.put(data[i], data[i+1]);
    }
    return result;
  }
  public DiscordEventHandler callbacks = new DiscordEventHandler();
  {
    callbacks.addListener(new DiscordEventAdapter() {
      @Override
      public void onRouteUpdate(String routeData) {
        //TogetherManager.log("onRouteUpdate");
        setOurMetadata(map("route", routeData));
      }

      @Override
      public void onMemberConnect(long lobbyId, long userId) {
        //TogetherManager.log("onMemberConnect");
        if (lobbyId != lobby.getId()) return;
        if (NetworkHelper.embarked) {
          // Player reconnecting. Adding them to the game will break things,
          // and there's no way to kick them from the lobby, so just ignore them.
          return;
        }
        NetworkHelper.addPlayer(new DiscordPlayer(
            integration.core.lobbyManager().getMemberUser(lobby, userId),
            integration,
            DiscordLobby.this
        ));

        NetworkHelper.sendData(NetworkHelper.dataType.Version);
        NetworkHelper.sendData(NetworkHelper.dataType.Ready);
        if (TogetherManager.gameMode == TogetherManager.mode.Coop)
          NetworkHelper.sendData(NetworkHelper.dataType.Character);

        NewMenuButtons.newGameScreen.playerList.setPlayers(TogetherManager.players);

        if (TogetherManager.currentLobby.isOwner()) {
          setMetadata(map("members", getMemberNameList()));
        }
        if (TogetherManager.gameMode == TogetherManager.mode.Bingo)
          NetworkHelper.sendData(NetworkHelper.dataType.BingoRules);
        NetworkHelper.sendData(NetworkHelper.dataType.Rules);
        updateActivity();
      }
    });
  };

  public void setOurMetadata(Map<String, String> pairs) {
    //TogetherManager.log("setOurMetadata");
    LobbyMemberTransaction txn = integration.core.lobbyManager().getMemberUpdateTransaction(
        lobby,
        integration.core.userManager().getCurrentUser().getUserId()
    );
    for (Map.Entry<String, String> pair : pairs.entrySet()) {
      txn.setMetadata(pair.getKey(), pair.getValue());
    }
    integration.core.lobbyManager().updateMember(
        lobby,
        integration.core.userManager().getCurrentUser().getUserId(),
        txn
    );
  }
  public void setUpNetworking() {
    integration.eventHandler.addListener(callbacks);
    setOurMetadata(map(
        "peerID", String.valueOf(integration.core.networkManager().getPeerId()),
        "route", integration.ourRoute
    ));
  }

  public Activity activity;

  public void startActivity() {
    activity = new Activity();
    activity.setInstance(true);
    activity.setState(String.format("Spire with Friends: %s", mode)); //TODO localize
    activity.timestamps().setStart(Instant.now());
    activity.party().setID(Long.toString(lobby.getId()));
    updateActivity();
  }
  public void updateActivity() {
    if (activity == null) return;
    activity.party().size().setCurrentSize(getMemberCount());
    activity.party().size().setMaxSize(getCapacity());
    activity.secrets().setJoinSecret(integration.core.lobbyManager().getLobbyActivitySecret(lobby));
    {
      boolean joinable = !lobby.isLocked() && getMemberCount() < getCapacity();
      activity.setDetails(joinable ? "Waiting for players" : "Locked"); //TODO localize
    }
    integration.core.activityManager().updateActivity(activity);
  }

  public void stopActivity() {
    integration.core.activityManager().clearActivity();
    activity.close();
    activity = null;
  }

  @Override
  public int getCapacity() {
    return lobby.getCapacity();
  }

  @Override
  public String getMetadata(String key) {
    return metadata.get(key);
  }

  @Override
  public void setMetadata(Map<String, String> pairs) {
    //TogetherManager.log("setMetadata");
    update(txn -> {
      for (Map.Entry<String, String> pair : pairs.entrySet()) {
        txn.setMetadata(pair.getKey(), pair.getValue());
      }
    });
    metadata = pairs;
    fetchAllMetadata();
  }

  public void update(Consumer<LobbyTransaction> body) { update(body, r -> {}); }

  public void update(Consumer<LobbyTransaction> body, Consumer<Result> callback) {
    LobbyTransaction txn = integration.core.lobbyManager().getLobbyUpdateTransaction(lobby);
    body.accept(txn);
    integration.core.lobbyManager().updateLobby(lobby, txn, callback);
  }
}
