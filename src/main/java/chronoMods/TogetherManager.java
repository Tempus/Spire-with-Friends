package chronoMods;

import com.evacipated.cardcrawl.modthespire.lib.*;

import com.megacrit.cardcrawl.localization.*;
import com.megacrit.cardcrawl.screens.custom.*;
import com.megacrit.cardcrawl.screens.*;
import com.megacrit.cardcrawl.ui.panels.*;
import com.megacrit.cardcrawl.screens.stats.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.megacrit.cardcrawl.core.*;
import com.megacrit.cardcrawl.helpers.*;
import com.megacrit.cardcrawl.helpers.input.*;
import com.megacrit.cardcrawl.map.*;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.relics.*;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.blights.*;
import com.codedisaster.steamworks.*;
import com.megacrit.cardcrawl.integrations.steam.SteamIntegration;

import basemod.*;
import basemod.helpers.*;
import basemod.abstracts.*;
import basemod.interfaces.*;

import org.apache.logging.log4j.*;
import java.nio.charset.StandardCharsets;
import java.lang.reflect.Type;
import java.util.*;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import chronoMods.*;
import chronoMods.steam.*;
import chronoMods.coop.*;
import chronoMods.coop.relics.*;
import chronoMods.ui.deathScreen.*;
import chronoMods.ui.hud.*;
import chronoMods.ui.lobby.*;
import chronoMods.ui.mainMenu.*;
import chronoMods.utilities.*;

@SpireInitializer
public class TogetherManager implements PostDeathSubscriber, PostInitializeSubscriber, PostDungeonInitializeSubscriber, EditStringsSubscriber, StartGameSubscriber {

    // Setup the basic logger
    public static final Logger logger = LogManager.getLogger(TogetherManager.class.getName());

    //This is for the in-game mod settings panel.
    private static final String MODNAME = "Spire with Friends";
    private static final String AUTHOR = "Chronometrics";
    private static final String DESCRIPTION = "Enables new Coop and Versus Race modes via Steam Networking.";

    // Stores a list of all the players and the lobby you're connected to
    public static ArrayList<RemotePlayer> players = new ArrayList();
    public static SteamLobby currentLobby;
    public static RemotePlayer currentUser;

    // Images are stored here because of funky basemod junk, these should probably have their own place
    public static Texture panelImg;
    public static Texture colourIndicatorImg;
    public static Texture splitPanelImg;
    public static ArrayList<Texture> portraitFrames = new ArrayList();
    public static Texture membersTexture;

    public static Texture ascOnImg;
    public static Texture ascOffImg;
    public static Texture heartOnImg;
    public static Texture heartOffImg;
    public static Texture whaleOnImg;
    public static Texture whaleOffImg;
    public static Texture ironmanOnImg;
    public static Texture ironmanOffImg;
    public static Texture ironcladOn;
    public static Texture silentOn;
    public static Texture defectOn;
    public static Texture watcherOn;

    public static Texture mapEmpty;
    public static Texture mapEmptyOutline;
    public static Texture mapCourier;
    public static Texture mapCourierOutline;
    public static Texture mapelitechest;
    public static Texture mapelitechestOutline;
    public static Texture mapeliterest;
    public static Texture mapeliterestOutline;
    public static Texture mapmonstershop;
    public static Texture mapmonstershopOutline;
    public static Texture mapmonstercourier;
    public static Texture mapmonstercourierOutline;
    public static Texture mapmonstermonster;
    public static Texture mapmonstermonsterOutline;

    // Custom UI strings for the mod
    public static Map<String, CustomStrings> CustomStringsMap;

    // Game Mode, just an enum to help mode specific mechanics figure out what's going on
    public static mode gameMode = TogetherManager.mode.Normal;

    // The split tracker
    public static SplitTracker splitTracker = new SplitTracker();

    // The Courier screen
    public static CoopCourierScreen courierScreen; 

    // The Courier screen
    public static CoopBossRelicSelectScreen teamRelicScreen; 

    // List of Team Relics (they are Blights though!)
    public static ArrayList<AbstractBlight> teamBlights = new ArrayList();

    // Debug flag
    private static boolean disableCheats = false;


    public static enum mode
    {
      Normal, Versus, Coop;
      
      private mode() {}
    }

    // Constructor, can't do stuff here due to the game not being loaded yet.
    public TogetherManager() {
        BaseMod.subscribe(this);
        BaseMod.subscribe(new SendDataPatches());
    }

    @SuppressWarnings("unused")
    public static void initialize() { 
        new TogetherManager();
    }

    // Do stuff here - the game has been safely loaded.
    @Override
    public void receivePostInitialize() {
        // Load textures. Why here? Dunno, they only work here.
        Texture badgeTexture = new Texture("chrono/images/Badge.png");
        panelImg = new Texture("chrono/images/playerPanel.png");
        colourIndicatorImg = new Texture("chrono/images/playerColourIndicator.png");
        splitPanelImg = new Texture("chrono/images/splitPanel.png");
        portraitFrames.add(ImageMaster.loadImage("images/ui/relicFrameRare.png"));
        portraitFrames.add(ImageMaster.loadImage("images/ui/relicFrameUncommon.png"));
        portraitFrames.add(ImageMaster.loadImage("images/ui/relicFrameCommon.png"));
        portraitFrames.add(ImageMaster.loadImage("images/ui/relicFrameBoss.png"));
        membersTexture = new Texture("chrono/images/FriendsIcon.png");

        ascOnImg = new Texture("chrono/images/AscensionOn.png");
        ascOffImg = new Texture("chrono/images/AscensionOff.png");
        heartOnImg = new Texture("chrono/images/HeartOn.png");
        heartOffImg = new Texture("chrono/images/HeartOff.png");
        whaleOnImg = new Texture("chrono/images/WhaleOn.png");
        whaleOffImg = new Texture("chrono/images/WhaleOff.png");
        ironmanOnImg = new Texture("chrono/images/IronmanOn.png");
        ironmanOffImg = new Texture("chrono/images/IronmanOff.png");
        ironcladOn = new Texture("chrono/images/Ironcladc.png");
        silentOn = new Texture("chrono/images/Silentc.png");
        defectOn = new Texture("chrono/images/Defectc.png");
        watcherOn = new Texture("chrono/images/Watcherc.png");
        
        mapEmpty = new Texture("chrono/images/map/CoopEmptyRoom.png");
        mapEmptyOutline = new Texture("chrono/images/map/CoopEmptyRoomOutline.png");
        mapCourier = new Texture("chrono/images/map/Courier.png");
        mapCourierOutline = new Texture("chrono/images/map/Courieroutline.png");
        mapelitechest = new Texture("chrono/images/map/elitechest.png");
        mapelitechestOutline = new Texture("chrono/images/map/elitechestoutline.png");
        mapeliterest = new Texture("chrono/images/map/eliterest.png");
        mapeliterestOutline = new Texture("chrono/images/map/eliterestoutline.png");
        mapmonstershop = new Texture("chrono/images/map/monstershop.png");
        mapmonstershopOutline = new Texture("chrono/images/map/monstershopoutline.png");
        mapmonstercourier = new Texture("chrono/images/map/monstercourier.png");
        mapmonstercourierOutline = new Texture("chrono/images/map/monstercourieroutline.png");
        mapmonstermonster = new Texture("chrono/images/map/monstermonster.png");
        mapmonstermonsterOutline = new Texture("chrono/images/map/monstermonsteroutline.png");

        // Create the Mod Menu
        ModPanel settingsPanel = new ModPanel();
        BaseMod.registerModBadge(badgeTexture, MODNAME, AUTHOR, DESCRIPTION, settingsPanel);

        // Initialize the new Steam Networking functions
        NetworkHelper.initialize();

        // Custom strings
        CustomStringsMap = CustomStrings.importCustomStrings();

        // Store in the current user's steam ID
        SteamUser steamUser = (SteamUser)ReflectionHacks.getPrivate(CardCrawlGame.publisherIntegration, SteamIntegration.class, "steamUser");
        currentUser = new RemotePlayer(steamUser.getSteamID());

        // Disable cheaty console
        DevConsole.enabled = !disableCheats;
    }

    // Replace this with uploading versus times to the remote leaderboard on my server later
    public void receivePostDeath() {
        // customMetrics metrics = new customMetrics();
        
        // Thread t = new Thread(metrics);
        // t.setName("Metrics");
        // t.start();
    }

    public void receivePostDungeonInitialize() {
        courierScreen = new CoopCourierScreen();
        teamRelicScreen = new CoopBossRelicSelectScreen();

        teamBlights.clear();
        teamBlights.add(new Auger());
        teamBlights.add(new DimensionalWallet());
        teamBlights.add(new GhostWriter());
        teamBlights.add(new MetalDetector());
        teamBlights.add(new MirrorTouch());
        teamBlights.add(new PneumaticPost());
        teamBlights.add(new SiphonPump());
        Collections.shuffle(teamBlights, new Random(AbstractDungeon.relicRng.randomLong()));
    }

    public static RemotePlayer getCurrentUser() {
        for (RemotePlayer playerInfo : TogetherManager.players) {
            if (playerInfo.isUser(TogetherManager.currentUser.steamUser)) {
                return playerInfo;
            }
        }
        return currentUser;
    }

    @Override
    public void receiveEditStrings() {

        String language;
        switch (Settings.language) {
            // case KOR:
            //     language = "kor";
            //     break;
            // case ZHS:
            //     language = "zhs";
            //     break;
            // case ZHT:
            //     language = "zht";
            //     break;
            // case FRA:
            //     language = "fra";
            //     break;
            // case JPN:
            //     language = "jpn";
            //     break;
            default:
                language = "eng";
        }
    
        // RelicStrings
        String relicStrings = Gdx.files.internal("chrono/localization/" + language + "/blights.json").readString(String.valueOf(StandardCharsets.UTF_8));
        BaseMod.loadCustomStrings(BlightStrings.class, relicStrings);

        // UIstring
        // String uiStrings = Gdx.files.internal("localization/" + language + "/chronoUI.json").readString(String.valueOf(StandardCharsets.UTF_8));
        // BaseMod.loadCustomStrings(UIStrings.class, uiStrings);
    }

    public void receiveStartGame() {
        if (TogetherManager.gameMode == TogetherManager.mode.Coop) {
            (new CoopDeathRevival()).instantObtain(AbstractDungeon.player, 0, false);
            // (new Auger()).instantObtain(AbstractDungeon.player, 1, false);
            // (new DimensionalWallet()).instantObtain(AbstractDungeon.player, 2, false);
            // (new GhostWriter()).instantObtain(AbstractDungeon.player, 3, false);
            // (new MetalDetector()).instantObtain(AbstractDungeon.player, 4, false);
            // (new MirrorTouch()).instantObtain(AbstractDungeon.player, 5, false);
            // (new PneumaticPost()).instantObtain(AbstractDungeon.player, 6, false);
            // (new SiphonPump()).instantObtain(AbstractDungeon.player, 7, false);
        }
    }

    public static void clearMultiplayerData() {
        // Reset Multiplayer components
        TogetherManager.currentLobby = null;
        TogetherManager.players.clear();
        TopPanelPlayerPanels.playerWidgets.clear();
        NetworkHelper.leaveLobby();
        SteamUser steamUser = (SteamUser)ReflectionHacks.getPrivate(CardCrawlGame.publisherIntegration, SteamIntegration.class, "steamUser");
        TogetherManager.currentUser = new RemotePlayer(steamUser.getSteamID());
    }

    @SpirePatch(clz = AbstractDungeon.class, method="update")
    public static class ConvenienceDebugPresses {
        public static void Postfix(AbstractDungeon __instance) {

            DevConsole.enabled = !disableCheats;
            // if (InputActionSet.selectCard_10.isJustPressed()) {
            //     int y = (int)(Math.random()*AbstractDungeon.map.size());
            //     int x = (int)(Math.random()*AbstractDungeon.map.get(y).size());
            //     MapRoomNode currentNode = AbstractDungeon.map.get(y).get(x);

            //     currentNode.setRoom(new CoopCourierRoom());
            // }

        // if (Gdx.input.isKeyJustPressed(60)) {
        //     TogetherManager.logger.info("SHIFT");
        //     AbstractDungeon.topLevelEffects.add(new CoopDeathNotification(TogetherManager.getCurrentUser()));
        // }
        // float dv = 1f;
        // if (Gdx.input.isKeyPressed(60)) {
        //     dv = -1f;
        // }


        // if (InputActionSet.selectCard_1.isPressed()) {
        //     ICON_SIZE += dv;
        // }

        }
    }
}
