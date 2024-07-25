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

    // Spire Packet references - These only need to be init once.
    private static VersionPacket versionPacket = new VersionPacket();
    private static StartPacket startPacket = new StartPacket();
    private static RulesPacket rulesPacket = new RulesPacket();
    private static ReadyPacket readyPacket = new ReadyPacket();
    private static FloorPacket floorPacket = new FloorPacket();
    private static ActPacket actPacket = new ActPacket();
    private static HpPacket hpPacket = new HpPacket();
    private static MoneyPacket moneyPacket = new MoneyPacket();
    private static CharacterPacket characterPacket = new CharacterPacket();
    private static SetDisplayRelicsPacket setDisplayRelicsPacket = new SetDisplayRelicsPacket();
    private static SendRelicPacket sendRelicPacket = new SendRelicPacket();
    private static FinishPacket finishPacket = new FinishPacket();
    private static SendCardPacket sendCardPacket = new SendCardPacket();
    // Versus specific packets
    private static SplitsPacket splitsPacket = new SplitsPacket();
    // Coop specific packets
    private static NeowReadyPacket neowReadyPacket = new NeowReadyPacket();
    private static SendCardGhostPacket sendCardGhostPacket = new SendCardGhostPacket();
    private static SendCardMessageBottlePacket sendCardMessageBottlePacket = new SendCardMessageBottlePacket();
    private static TransferCardPacket transferCardPacket = new TransferCardPacket();
    private static TransferRelicPacket transferRelicPacket = new TransferRelicPacket();
    private static TransferPotionPacket transferPotionPacket = new TransferPotionPacket();
    private static UsePotionPacket usePotionPacket = new UsePotionPacket();
    private static ClearRoomPacket clearRoomPacket = new ClearRoomPacket();
    private static LockRoomPacket lockRoomPacket = new LockRoomPacket();
    private static ChooseNeowPacket chooseNeowPacket = new ChooseNeowPacket();
    private static ChooseTeamRelicPacket chooseTeamRelicPacket = new ChooseTeamRelicPacket();
    private static SendPotionPacket sendPotionPacket = new SendPotionPacket();
    private static LoseLifePacket loseLifePacket = new LoseLifePacket();
    private static KickPacket kickPacket = new KickPacket();
    private static GetRedKeyPacket getRedKeyPacket = new GetRedKeyPacket();
    private static GetBlueKeyPacket getBlueKeyPacket = new GetBlueKeyPacket();
    private static GetGreenKeyPacket getGreenKeyPacket = new GetGreenKeyPacket();
    private static GetPotionPacket getPotionPacket = new GetPotionPacket();
    private static AddPotionSlotPacket addPotionSlotPacket = new AddPotionSlotPacket();
    private static ModifyBrainFreezePacket modifyBrainFreezePacket = new ModifyBrainFreezePacket();
    private static DrawMapPacket drawMapPacket = new DrawMapPacket();
    private static ClearMapPacket clearMapPacket = new ClearMapPacket();
    private static DeckInfoPacket deckInfoPacket = new DeckInfoPacket();
    private static RelicInfoPacket relicInfoPacket = new RelicInfoPacket();
    private static RequestVersionPacket requestVersionPacket = new RequestVersionPacket();
    private static AtDoorPacket atDoorPacket = new AtDoorPacket();
    private static VictoryPacket victoryPacket = new VictoryPacket();
    private static TransferBoosterPacket transferBoosterPacket = new TransferBoosterPacket();
    private static BingoPacket bingoPacket = new BingoPacket();
    private static BingoRulesPacket bingoRulesPacket = new BingoRulesPacket();
    private static TeamChangePacket teamChangePacket = new TeamChangePacket();
    private static TeamNamePacket teamNamePacket = new TeamNamePacket();
    private static BingoCardPacket bingoCardPacket = new BingoCardPacket();
    private static CustomMarkPacket customMarkPacket = new CustomMarkPacket();
    private static LastBossPacket lastBossPacket = new LastBossPacket();
    private static SendMessagePacket sendMessagePacket = new SendMessagePacket();
    private static BluntScissorCardPacket bluntScissorCardPacket = new BluntScissorCardPacket();
    private static MergeUncommonPacket mergeUncommonPacket = new MergeUncommonPacket();
    private static InfusionPacket infusionPacket = new InfusionPacket();
    private static HeartChoicePacket heartChoicePacket = new HeartChoicePacket();
    
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
			SpirePacket incomingPacket = dataTypeToSpirePacket(type);
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

		SpirePacket packet = dataTypeToSpirePacket(type);
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
	
	public static SpirePacket dataTypeToSpirePacket(dataType type) {
		switch(type) {
		case Version:
			return versionPacket;
		case Rules:
    		return rulesPacket;
		case Start:
			return startPacket;
		case Ready:
			return readyPacket;
		case Floor:
			return floorPacket;
		case Act:
			return actPacket;
		case Hp:
			return hpPacket;
		case Money:
			return moneyPacket;
		case Character:
			return characterPacket;
		case SetDisplayRelics:
			return setDisplayRelicsPacket;
		case SendRelic:
			return sendRelicPacket;
		case Finish:
			return finishPacket;

		// Versus specific
		case Splits:
			return splitsPacket;

		// Coop specific packets
		case ClearRoom:
			return clearRoomPacket;
		case LockRoom:
			return lockRoomPacket;
			
		case SendCard: // Unused
			return sendCardPacket;
		case SendCardGhost:
			return sendCardGhostPacket;
		case SendCardMessageBottle:
			return sendCardMessageBottlePacket;
		case TransferCard:
			return transferCardPacket;
		case TransferRelic:
			return transferRelicPacket;
		case TransferPotion:
			return transferPotionPacket;
		case UsePotion:
			return usePotionPacket;
		case SendPotion:
			return sendPotionPacket;
		case ChooseNeow:
			return chooseNeowPacket;
		case NeowReadyUpdateStateChange:
			return neowReadyPacket;
		case ChooseTeamRelic:
			return chooseTeamRelicPacket;
		case LoseLife:
			return loseLifePacket;
		case Kick:
			return kickPacket;
		case GetRedKey:
			return getRedKeyPacket;
		case GetBlueKey:
			return getBlueKeyPacket;
		case GetGreenKey:
			return getGreenKeyPacket;
		case GetPotion:
			return getPotionPacket;
		case AddPotionSlot:
			return addPotionSlotPacket;
		case ModifyBrainFreeze:
			return modifyBrainFreezePacket;
		case DrawMap:
			return drawMapPacket;
		case ClearMap:
			return clearMapPacket;
		case DeckInfo:
			return deckInfoPacket;
		case RelicInfo:
			return relicInfoPacket;
		case RequestVersion:
			return requestVersionPacket;
		case AtDoor:
			return atDoorPacket;
		case Victory:
			return victoryPacket;
		case TransferBooster:
			return transferBoosterPacket;
		case Bingo:
			return bingoPacket;
		case BingoRules:
			return bingoRulesPacket;
		case TeamChange:
			return teamChangePacket;
		case TeamName:
			return teamNamePacket;
		case BingoCard:
			return bingoCardPacket;
		case CustomMark:				
			return customMarkPacket;
		case LastBoss:
			return lastBossPacket;
		case SendMessage:
			return sendMessagePacket;
        case BluntScissorCard:
        	return bluntScissorCardPacket;
		case MergeUncommon:
			return mergeUncommonPacket;
		case Infusion:
			return infusionPacket;
		case HeartChoice:
			return heartChoicePacket;
		default:
			return null;
		}
	}
}
