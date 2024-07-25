package chronoMods.network;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.cutscenes.CutscenePanel;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen;

import Lyraedan.networking.packets.ActPacket;
import Lyraedan.networking.packets.AddPotionSlotPacket;
import Lyraedan.networking.packets.AtDoorPacket;
import Lyraedan.networking.packets.BingoCardPacket;
import Lyraedan.networking.packets.BingoPacket;
import Lyraedan.networking.packets.BingoRulesPacket;
import Lyraedan.networking.packets.BluntScissorCardPacket;
import Lyraedan.networking.packets.CharacterPacket;
import Lyraedan.networking.packets.ChooseNeowPacket;
import Lyraedan.networking.packets.ChooseTeamRelicPacket;
import Lyraedan.networking.packets.ClearMapPacket;
import Lyraedan.networking.packets.ClearRoomPacket;
import Lyraedan.networking.packets.CustomMarkPacket;
import Lyraedan.networking.packets.DeckInfoPacket;
import Lyraedan.networking.packets.DrawMapPacket;
import Lyraedan.networking.packets.FinishPacket;
import Lyraedan.networking.packets.FloorPacket;
import Lyraedan.networking.packets.GetBlueKeyPacket;
import Lyraedan.networking.packets.GetGreenKeyPacket;
import Lyraedan.networking.packets.GetPotionPacket;
import Lyraedan.networking.packets.GetRedKeyPacket;
import Lyraedan.networking.packets.HeartChoicePacket;
import Lyraedan.networking.packets.HpPacket;
import Lyraedan.networking.packets.InfusionPacket;
import Lyraedan.networking.packets.KickPacket;
import Lyraedan.networking.packets.LastBossPacket;
import Lyraedan.networking.packets.LockRoomPacket;
import Lyraedan.networking.packets.LoseLifePacket;
import Lyraedan.networking.packets.MergeUncommonPacket;
import Lyraedan.networking.packets.ModifyBrainFreezePacket;
import Lyraedan.networking.packets.MoneyPacket;
import Lyraedan.networking.packets.NeowReadyPacket;
import Lyraedan.networking.packets.ReadyPacket;
import Lyraedan.networking.packets.RelicInfoPacket;
import Lyraedan.networking.packets.RequestVersionPacket;
import Lyraedan.networking.packets.RulesPacket;
import Lyraedan.networking.packets.SendCardGhostPacket;
import Lyraedan.networking.packets.SendCardMessageBottlePacket;
import Lyraedan.networking.packets.SendCardPacket;
import Lyraedan.networking.packets.SendMessagePacket;
import Lyraedan.networking.packets.SendPotionPacket;
import Lyraedan.networking.packets.SendRelicPacket;
import Lyraedan.networking.packets.SetDisplayRelicsPacket;
import Lyraedan.networking.packets.SpirePacket;
import Lyraedan.networking.packets.SplitsPacket;
import Lyraedan.networking.packets.StartPacket;
import Lyraedan.networking.packets.TeamChangePacket;
import Lyraedan.networking.packets.TeamNamePacket;
import Lyraedan.networking.packets.TransferBoosterPacket;
import Lyraedan.networking.packets.TransferCardPacket;
import Lyraedan.networking.packets.TransferPotionPacket;
import Lyraedan.networking.packets.TransferRelicPacket;
import Lyraedan.networking.packets.UsePotionPacket;
import Lyraedan.networking.packets.VersionPacket;
import Lyraedan.networking.packets.VictoryPacket;
import chronoMods.TogetherManager;
import chronoMods.coop.CoopDoorUnlockScreen;
import chronoMods.coop.CoopEmptyRoom;
import chronoMods.coop.CoopMultiRoom;
import chronoMods.network.discord.DiscordIntegration;
import chronoMods.network.steam.SteamIntegration;
import chronoMods.ui.hud.TopPanelPlayerPanels;
import chronoMods.ui.mainMenu.NewMenuButtons;

public class NetworkHelper {

	public static chronoMods.network.steam.SteamIntegration steam;
	//public static DiscordIntegration discord;
    public static ArrayList<Integration> networks = new ArrayList();

    public static ArrayList<Lobby> lobbies = new ArrayList();

    private static final Logger logger = LogManager.getLogger("Network Data");
    public static boolean embarked = false;

    private static Map<dataType, SpirePacket> spirePackets = new HashMap<dataType, SpirePacket>() {{
    	put(dataType.Version, new VersionPacket());
    	put(dataType.Rules, new RulesPacket());
    	put(dataType.Start, new StartPacket());
    	put(dataType.Ready, new ReadyPacket());
    	put(dataType.Floor, new FloorPacket());
    	put(dataType.Act, new ActPacket());
    	put(dataType.Hp, new HpPacket());
    	put(dataType.Money, new MoneyPacket());
    	put(dataType.Character, new CharacterPacket());
    	put(dataType.SetDisplayRelics, new SetDisplayRelicsPacket());
    	put(dataType.SendRelic, new SendRelicPacket());
    	put(dataType.Finish, new FinishPacket());
    	
    	// Versus
    	put(dataType.Splits, new SplitsPacket());
    	
    	// Coop
    	put(dataType.ClearRoom, new ClearRoomPacket());
    	put(dataType.LockRoom, new LockRoomPacket());
    	
    	put(dataType.SendCard, new SendCardPacket()); // Unused
    	put(dataType.SendCardGhost, new SendCardGhostPacket());
    	put(dataType.SendCardMessageBottle, new SendCardMessageBottlePacket());
    	put(dataType.TransferCard, new TransferCardPacket());
    	put(dataType.TransferRelic, new TransferRelicPacket());
    	put(dataType.TransferPotion, new TransferPotionPacket());
    	put(dataType.UsePotion, new UsePotionPacket());
    	put(dataType.SendPotion, new SendPotionPacket());
    	put(dataType.ChooseNeow, new ChooseNeowPacket());
    	put(dataType.NeowReadyUpdateStateChange, new NeowReadyPacket()); // Lukes (TODO CHANGE THIS)
    	put(dataType.ChooseTeamRelic, new ChooseTeamRelicPacket());
    	put(dataType.LoseLife, new LoseLifePacket());
    	put(dataType.Kick, new KickPacket());
    	put(dataType.GetRedKey, new GetRedKeyPacket());
    	put(dataType.GetBlueKey, new GetBlueKeyPacket());
    	put(dataType.GetGreenKey, new GetGreenKeyPacket());
    	put(dataType.GetPotion, new GetPotionPacket());
    	put(dataType.AddPotionSlot, new AddPotionSlotPacket());
    	put(dataType.ModifyBrainFreeze, new ModifyBrainFreezePacket());
    	put(dataType.DrawMap, new DrawMapPacket());
    	put(dataType.ClearMap, new ClearMapPacket());
    	put(dataType.DeckInfo, new DeckInfoPacket());
    	put(dataType.RelicInfo, new RelicInfoPacket());
    	put(dataType.RequestVersion, new RequestVersionPacket());
    	put(dataType.AtDoor, new AtDoorPacket());
    	put(dataType.Victory, new VictoryPacket());
    	put(dataType.TransferBooster, new TransferBoosterPacket());
    	put(dataType.Bingo, new BingoPacket());
    	put(dataType.BingoRules, new BingoRulesPacket());
    	put(dataType.TeamChange, new TeamChangePacket());
    	put(dataType.TeamName, new TeamNamePacket());
    	put(dataType.BingoCard, new BingoCardPacket());
    	put(dataType.CustomMark, new CustomMarkPacket());
    	put(dataType.LastBoss, new LastBossPacket());
    	put(dataType.SendMessage, new SendMessagePacket());
    	put(dataType.BluntScissorCard, new BluntScissorCardPacket());
    	put(dataType.MergeUncommon, new MergeUncommonPacket());
    	put(dataType.Infusion, new InfusionPacket());
    	put(dataType.HeartChoice, new HeartChoicePacket());
    }
    };
    
	public void NetworkHelper() {}

	public static void initialize() {
		// If Steam available, add SteamIntegration
		steam = new SteamIntegration();
		steam.initialize();

		if (steam.isInitialized()) {
			TogetherManager.log("Steam Started.");
			networks.add(steam);
		} else {
			TogetherManager.log("Steam Integration not found.");
		}
		// If Discord available, add DiscordIntegration
		DiscordIntegration discord = new DiscordIntegration();
		discord.initialize();
		if (discord.isInitialized()) {
			TogetherManager.log("Discord Started.");
			networks.add(discord);
		}
		else {
			TogetherManager.log("Discord Integration not found.");
		}
	}

    @SpirePatch(clz=CardCrawlGame.class, method="update")
    public static class SteamUpdate
    {
        public static void Postfix(CardCrawlGame __instance)
        {
        	NetworkHelper.update();
        }
    }

    static public Packet packet = new Packet();

	// Check every frame for incoming packets.
	public static void update() {
		if (service() == null) { return; }

		service().getPacket(packet);

		while (packet.hasPacket()) {

			if (TogetherManager.currentLobby != null) 
				parseData(packet.data(), packet.player());
			else
				return;

			if (service() != null)
				service().getPacket(packet);
			else
				return;
		} 
	}

	public static void parseData(ByteBuffer data, RemotePlayer playerInfo) {

		int enumIndex = data.getInt();
		if (enumIndex > dataType.values().length || enumIndex < 0) {
			TogetherManager.log("Unknown Enum value for data type: " + enumIndex);
			return;
		}
		dataType type = dataType.values()[enumIndex];
		
		try {
			SpirePacket incomingPacket = spirePackets.get(type);
			incomingPacket.parseData(data, playerInfo);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    public static enum dataType
    {
      	Rules, Start, Ready, Version, Floor, Act, Hp, Money, BossRelic, Finish, SendCard, SendCardGhost, TransferCard, TransferRelic, TransferPotion, UsePotion, SendPotion, EmptyRoom, BossChosen, Splits, SetDisplayRelics, ClearRoom, LockRoom, ChooseNeow, ChooseTeamRelic, LoseLife, Kick, GetRedKey, GetBlueKey, GetGreenKey, Character, GetPotion, AddPotionSlot, SendRelic, ModifyBrainFreeze, DrawMap, ClearMap, DeckInfo, RelicInfo, RequestVersion, SendCardMessageBottle, AtDoor, Victory, TransferBooster, Bingo, BingoRules, TeamChange, BingoCard, TeamName, CustomMark, LastBoss, SendMessage, BluntScissorCard, MergeUncommon, Infusion, HeartChoice, NeowReadyUpdateStateChange;
      
    	private dataType() {}
    }

	public static void sendData(NetworkHelper.dataType type) {
		ByteBuffer data = NetworkHelper.generateData(type);	
		if (data == null) { return; }

		service().sendPacket(data);
	}

	private static ByteBuffer generateData(NetworkHelper.dataType type) {
		ByteBuffer data = null;

		SpirePacket packet = spirePackets.get(type);
		if(packet != null) {
			data = packet.generateData();
		} else {
			data = ByteBuffer.allocateDirect(4);
		}
		data.putInt(0, type.ordinal());

		return data;
	}

	public static Integration service() {
		if (TogetherManager.currentLobby == null) { 
			return null; 
		}

		if (TogetherManager.currentLobby.service == null) {
			return null; 
		}

		return TogetherManager.currentLobby.service;
	}

	public static void updateLobbyData() {
		if (TogetherManager.currentLobby != null) {
			Map<String,String> metadata = new HashMap();

			metadata.put("mode", TogetherManager.gameMode.toString());
			metadata.put("ascension", Integer.toString(NewMenuButtons.newGameScreen.ascensionSelectWidget.ascensionLevel));
			metadata.put("character", NewMenuButtons.newGameScreen.characterSelectWidget.getChosenOptionName());
			metadata.put("heart",   Boolean.toString(NewMenuButtons.newGameScreen.heartToggle.isTicked()));
			metadata.put("neow",    Boolean.toString(NewMenuButtons.newGameScreen.neowToggle.isTicked()));
			metadata.put("ironman", Boolean.toString(NewMenuButtons.newGameScreen.ironmanToggle.isTicked()));
			metadata.put("loseMaxHPOnDeath", Boolean.toString(NewMenuButtons.newGameScreen.loseMaxHPOnDeathToggle.isTicked()));
			metadata.put("owner", TogetherManager.currentUser.userName);
			metadata.put("members", TogetherManager.currentLobby.getMemberNameList());

			TogetherManager.currentLobby.setMetadata(metadata);
		}
	}

	public static void createLobby(Integration service) {
		TogetherManager.log("Creating Lobby...");
		service.createLobby(TogetherManager.gameMode);
	}

	public static void setLobbyPrivate(boolean toggle) {
		TogetherManager.currentLobby.setPrivate(toggle);
	}

	public static void leaveLobby(){
		if (TogetherManager.currentLobby != null) {

			// Handle Ownership transfer
        	if (TogetherManager.currentLobby.isOwner())
        		TogetherManager.currentLobby.newOwner();


			// Leave Lobby
	        CardCrawlGame.mainMenuScreen.screen = MainMenuScreen.CurScreen.MAIN_MENU;
    	    CardCrawlGame.mainMenuScreen.lighten();


    	    TogetherManager.currentLobby.leaveLobby();

			TogetherManager.clearMultiplayerData();
		}
	}

	public static void getLobbies() {
	    NetworkHelper.lobbies.clear();

	    for (Integration service : networks)
			service.getLobbies();
	}

	public static void addPlayer(RemotePlayer player) {		
		// Make sure we're not adding a dupe
		for (RemotePlayer oldplayer : TogetherManager.players)
			if (oldplayer.isUser(player))
				return;

        TogetherManager.players.add(player);
        // if (TogetherManager.gameMode == TogetherManager.mode.Bingo)
        // 	TopPanelPlayerPanels.playerWidgets.add(new BingoPlayerWidget(player));
        // else
       	// 	TopPanelPlayerPanels.playerWidgets.add(new RemotePlayerWidget(player));
		
		TogetherManager.log("Member joined: " + player.userName);
	}

	public static void removePlayer(RemotePlayer player) {
		if (player == null) { return; }

		if (player.isUser(TogetherManager.currentUser) && CardCrawlGame.isInARun())
			TogetherManager.infoPopup.show(CardCrawlGame.languagePack.getUIString("Network").TEXT[0], CardCrawlGame.languagePack.getUIString("Network").TEXT[2]);

		if (embarked && TogetherManager.gameMode != TogetherManager.mode.Coop) {
			player.connection = false;
		} else {
			// Unlocks a room if a player disconnects.
			if (embarked) {
				int xc = player.x;
				int yc = player.y;
				if (xc != -1 && yc != -1 && yc < 16 && !AbstractDungeon.id.equals("TheEnding")) {
		            MapRoomNode currentNodec = AbstractDungeon.map.get(yc).get(xc);

		            // Safety first? This triggers if games are desynced, but I hate getting reports about it.
		            if (currentNodec == null) 			{ return; }
		            if (currentNodec.getRoom() == null) { return; }

		            // Unlocks a room we are leaving
					CoopEmptyRoom.LockedRoomField.locked.set(currentNodec.getRoom(), false);
				
					// Sets the next room of a multi-room
					AbstractRoom secondRoom = CoopMultiRoom.secondRoomField.secondRoom.get(currentNodec);
					AbstractRoom thirdRoom  = CoopMultiRoom.thirdRoomField.thirdRoom.get(currentNodec);

					// Resolve the multinodes by advancing the 'queue' 
					currentNodec.room = secondRoom;
					CoopMultiRoom.secondRoomField.secondRoom.set(currentNodec, thirdRoom);
					CoopMultiRoom.thirdRoomField.thirdRoom.set(currentNodec, null);

					if (currentNodec.room == null)
						currentNodec.setRoom(new CoopEmptyRoom());
				}
			}

			// If a player disconnects, ensure waiting heart beaten players or door key players can advance
			boolean openDoor = true;
			for (RemotePlayer r: TogetherManager.players)
				if (!r.act4arrived)
					openDoor = false;

			if (openDoor && AbstractDungeon.actNum == 3)
				((CoopDoorUnlockScreen)CardCrawlGame.mainMenuScreen.doorUnlockScreen).proceed();

			// Advance pas tthe heart
			boolean passHeart = true;
		    for (RemotePlayer r: TogetherManager.players) 
		      if (!r.victory)
		        passHeart = false;

		    // If we're all done, add the last panel as well.
		    if (passHeart) {
			    TogetherManager.cutscene.endingTimer = 8.0F;
			    CutscenePanel panel = new CutscenePanel("chrono/images/cutscenes/AllTogether.png");
			    TogetherManager.cutscene.panels.add(panel);
			    panel.activate();
			}

			// Remove from player list
			TogetherManager.players.remove(player);
    		TogetherManager.log("Member left: " + player.userName);

			// Remove the widget
	        TopPanelPlayerPanels.playerWidgets.remove(player.widget);
		}

		int connected = 0;
		for (RemotePlayer np : TogetherManager.players) {
			if (np.connection) { connected++; }
		}

		if ((TogetherManager.players.size() <= 1 || connected <= 1) && CardCrawlGame.isInARun()) {
			TogetherManager.infoPopup.show(CardCrawlGame.languagePack.getUIString("Network").TEXT[3], CardCrawlGame.languagePack.getUIString("Network").TEXT[4]);
		}
	}
}
