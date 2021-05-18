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
    return integration.core.lobbyManager().getMemberUser(lobby, lobby.getOwnerId()).getUsername();
  }

  @Override
  public Long getOwner() {
    return lobby.getOwnerId();
  }

  @Override
  public boolean isOwner() {
    return lobby.getOwnerId() == integration.core.userManager().getCurrentUser().getUserId();
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
    return integration.core.lobbyManager().memberCount(lobby);
  }

  @Override
  public CopyOnWriteArrayList<RemotePlayer> getLobbyMembers() {
    return integration.core.lobbyManager().getMemberUsers(lobby).stream()
        .map(u -> new DiscordPlayer(u, integration, DiscordLobby.this))
        .map(p -> {
          TopPanelPlayerPanels.playerWidgets.add(new RemotePlayerWidget(p));
          return p;
        })
        .collect(Collectors.toCollection(CopyOnWriteArrayList::new));
  }

  @Override
  public String getMemberNameList() {
    return integration.core.lobbyManager().getMemberUsers(lobby)
        .stream()
        .map(DiscordUser::getUsername)
        .collect(Collectors.joining("\t"));
  }

  @Override
  public Object getID() {
    return lobby;
  }

  @Override
  public void leaveLobby() {
    stopActivity();
    integration.eventHandler.removeListener(callbacks);
    TogetherManager.players.stream()
        .forEach(p -> callbacks.onMemberDisconnect(lobby.getId(), p.getAccountID()));
    integration.core.lobbyManager().disconnectLobby(lobby);
  }

  @Override
  public void setJoinable(boolean toggle) {
    update(txn -> txn.setLocked(toggle), r -> updateActivity());
  }

  @Override
  public void setPrivate(boolean toggle) {
    update(txn -> txn.setType(toggle ? LobbyType.PRIVATE : LobbyType.PUBLIC), r -> updateActivity());
  }

  @Override
  public void join() {
    integration.core.lobbyManager().connectLobby(lobby, (r, l) -> {
      if (r == Result.OK) {
        metadata = integration.core.lobbyManager().getLobbyMetadata(lobby);
        fetchAllMetadata();
        setUpNetworking();
        startActivity();
        TogetherManager.currentLobby = DiscordLobby.this;
        TogetherManager.players = TogetherManager.currentLobby.getLobbyMembers();

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
        setOurMetadata(map("route", routeData));
      }

      @Override
      public void onMemberConnect(long lobbyId, long userId) {
        if (lobbyId != lobby.getId()) return;
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
        NetworkHelper.sendData(NetworkHelper.dataType.Rules);
      }

      @Override
      public void onMemberDisconnect(long lobbyId, long userId) {
        if (lobbyId != lobby.getId()) return;
        TogetherManager.players.stream()
            .filter(p -> p.isUser(userId))
            .findAny()
            .ifPresent(NetworkHelper::removePlayer);
      }
    });
  };

  public void setOurMetadata(Map<String, String> pairs) {
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
    update(txn -> {
      for (Map.Entry<String, String> pair : pairs.entrySet()) {
        txn.setMetadata(pair.getKey(), pair.getValue());
      }
    });
  }

  public void update(Consumer<LobbyTransaction> body) { update(body, r -> {}); }

  public void update(Consumer<LobbyTransaction> body, Consumer<Result> callback) {
    LobbyTransaction txn = integration.core.lobbyManager().getLobbyUpdateTransaction(lobby);
    body.accept(txn);
    integration.core.lobbyManager().updateLobby(lobby, txn, callback);
  }
}
