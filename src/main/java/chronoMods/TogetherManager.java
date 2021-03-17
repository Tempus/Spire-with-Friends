package chronoMods;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.*;

import com.megacrit.cardcrawl.localization.*;
import com.megacrit.cardcrawl.screens.custom.*;
import com.megacrit.cardcrawl.screens.*;
import com.megacrit.cardcrawl.ui.panels.*;
import com.megacrit.cardcrawl.screens.stats.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.megacrit.cardcrawl.core.*;
import com.megacrit.cardcrawl.helpers.*;
import com.megacrit.cardcrawl.helpers.input.*;
import com.megacrit.cardcrawl.map.*;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.relics.*;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.rewards.*;
import com.megacrit.cardcrawl.blights.*;
import com.megacrit.cardcrawl.screens.options.*;
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
import java.util.concurrent.*;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import chronoMods.*;
import chronoMods.steam.*;
import chronoMods.coop.*;
import chronoMods.coop.relics.*;
import chronoMods.coop.drawable.*;
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
    public static final String MODNAME = "Spire with Friends";
    public static final String AUTHOR = "Chronometrics";
    public static final String DESCRIPTION = "Enables new Coop and Versus Race modes via Steam Networking.";
    public static final float VERSION = 1.4f;

    // Stores a list of all the players and the lobby you're connected to
    public static CopyOnWriteArrayList<RemotePlayer> players = new CopyOnWriteArrayList();
    public static SteamLobby currentLobby;
    public static RemotePlayer currentUser;

    // Fallback font for names with unsupported Glyphs
    public static BitmapFont fallbackFont;

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

    public static Texture TP_WhiteHeart;

    public static Texture mapEmpty;
    public static Texture mapEmptyOutline;
    public static Texture mapCourier;
    public static Texture mapCourierOutline;

    // Custom UI strings for the mod
    public static Map<String, CustomStrings> CustomStringsMap;

    // Game Mode, just an enum to help mode specific mechanics figure out what's going on
    public static mode gameMode = TogetherManager.mode.Normal;

    // The split tracker
    public static SplitTracker splitTracker;

    // The Courier screen
    public static CoopCourierScreen courierScreen; 

    // The Team Relic screen
    public static CoopBossRelicSelectScreen teamRelicScreen; 

    // The Info Popup Overlay
    public static InfoPopup infoPopup; 

    // The Map Drawing Controller Widget screen
    public static MapCanvasController paintWidget; 

    // List of Team Relics (they are Blights though!)
    public static ArrayList<AbstractBlight> teamBlights = new ArrayList();

    // Incomplete List of mods that may break seeds
    public ArrayList<String> unsafeMods = new ArrayList();

    // Debug flag
    private static boolean debug = true;

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

    public static void log(String outmessage) {
        if (debug)
            TogetherManager.logger.info(outmessage);
    }

    // Do stuff here - the game has been safely loaded.
    @Override
    public void receivePostInitialize() {
        TogetherManager.logger.info("============= Spire with Friends " + VERSION + " by Chronometrics =============");

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

        TP_WhiteHeart = ImageMaster.loadImage("images/ui/topPanel/panel_heart_white.png");
        
        mapEmpty = new Texture("chrono/images/map/CoopEmptyRoom.png");
        mapEmptyOutline = new Texture("chrono/images/map/CoopEmptyRoomOutline.png");
        mapCourier = new Texture("chrono/images/map/Courier.png");
        mapCourierOutline = new Texture("chrono/images/map/Courieroutline.png");

        // Create the fallback font
        CreateFallbackFont();

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
        DevConsole.enabled = debug;

        // Register custom rewards
        BaseMod.registerCustomReward(
            RewardTypePatch.STARTERUP, 
            (rewardSave) -> { // this handles what to do when this quest type is loaded.
                return new StarterRelicUpgradeReward();
            }, 
            (customReward) -> { // this handles what to do when this quest type is saved.
                return new RewardSave(customReward.type.toString(), null, 0, 0);
            });

        BaseMod.registerCustomReward(
            RewardTypePatch.FLIGHT, 
            (rewardSave) -> { // this handles what to do when this quest type is loaded.
                return new FlightReward();
            }, 
            (customReward) -> { // this handles what to do when this quest type is saved.
                return new RewardSave(customReward.type.toString(), null, 0, 0);
            });
    
        // Some more UI element creation
        splitTracker = new SplitTracker();
        infoPopup = new InfoPopup();

        // Specific mod comaptibility fixes :chronoTicked:
        foundmod_colormap = checkForMod("coloredmap.ColoredMap");
        if (foundmod_colormap) {
            colormapPrefs = SaveHelper.getPrefs("ColoredMapPrefs");
        }
    }

    public void CreateFallbackFont() {

        FileHandle fontFile = Gdx.files.internal("font/zhs/NotoSansMonoCJKsc-Regular.otf");
        FreeTypeFontGenerator g = new FreeTypeFontGenerator(fontFile);

        FreeTypeFontGenerator.FreeTypeFontParameter p = new FreeTypeFontGenerator.FreeTypeFontParameter();
        p.characters = "";
        p.incremental = true;
        p.size = Math.round(26.0F * (Settings.BIG_TEXT_MODE ? 1.2f : 1.0f) * Settings.scale);
        p.gamma = 2.0F;
        p.spaceX = (int)(-0.9F * Settings.scale);
        p.borderColor = Color.DARK_GRAY;
        p.borderStraight = true;
        p.borderWidth = 2.0F * Settings.scale;
        p.borderGamma = 2.0F;
        p.shadowColor = new Color(0.0F, 0.0F, 0.0F, 0.33F);
        p.shadowOffsetX = 2;
        p.shadowOffsetY = 2;
        p.minFilter = Texture.TextureFilter.Nearest;
        p.magFilter = Texture.TextureFilter.MipMapLinearNearest;

        g.scaleForPixelHeight(p.size);
        
        fallbackFont = g.generateFont(p);
        fallbackFont.setUseIntegerPositions(true);
        (fallbackFont.getData()).markupEnabled = true;
        (fallbackFont.getData()).fontFile = fontFile;
    }

    // Replace this with uploading versus times to the remote leaderboard on my server later
    public void receivePostDeath() {
        // customMetrics metrics = new customMetrics();
        
        // Thread t = new Thread(metrics);
        // t.setName("Metrics");
        // t.start();
    }

    // Despite the name, published once at the beginning of a run after the first Dungeon inits and never again
    public void receivePostDungeonInitialize() {
        if (gameMode != mode.Coop) { return; }

        courierScreen = new CoopCourierScreen();
        teamRelicScreen = new CoopBossRelicSelectScreen();
        paintWidget = new MapCanvasController();

        teamBlights.clear();
        teamBlights.add(new BlueLadder());
        teamBlights.add(new DimensionalWallet());
        teamBlights.add(new GhostWriter());
        teamBlights.add(new DowsingRod());
        teamBlights.add(new MirrorTouch());
        teamBlights.add(new PneumaticPost());
        teamBlights.add(new VaporFunnel());
        teamBlights.add(new BondsOfFate());
        teamBlights.add(new Dimensioneel());
        teamBlights.add(new BrainFreeze());
        teamBlights.add(new BigHouse());
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
            case ZHS:
                language = "zhs";
                break;
            // case ZHT:
            //     language = "zht";
            //     break;
            case FRA:
                language = "fra";
                break;
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
        String uiStrings = Gdx.files.internal("chrono/localization/" + language + "/ui.json").readString(String.valueOf(StandardCharsets.UTF_8));
        BaseMod.loadCustomStrings(UIStrings.class, uiStrings);
    }

    public void receiveStartGame() {
        NetworkHelper.embarked = true;

        // Reset the game timer
        VersusTimer.timer = 0;
        VersusTimer.startTime = System.currentTimeMillis();

        if (TogetherManager.gameMode == TogetherManager.mode.Coop) {
            (new CoopDeathRevival()).instantObtain(AbstractDungeon.player, 0, false);
            // (new BlueLadder()).instantObtain(AbstractDungeon.player, 1, false);
            // (new DimensionalWallet()).instantObtain(AbstractDungeon.player, 2, false);
            // (new MirrorTouch()).instantObtain(AbstractDungeon.player, 5, false);
            // (new VaporFunnel()).instantObtain(AbstractDungeon.player, 7, false);
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

    public static int getModHash() {
        List<ModInfo> mods = Arrays.asList(Loader.MODINFOS);
        Collections.sort(mods, (o1, o2) -> o1.ID.compareTo(o2.ID));
        return Arrays.hashCode(mods.toArray());
    }

    public static boolean areModsSafe() {
        // Iterate over everything, then call findModName(Class<?> cls) to get a string that returns null, "Unknown", or the mod name. Also exclude Spire with Friends, duh
        return true;
    }

    public static boolean foundmod_colormap = false;
    public static Prefs colormapPrefs;

    // Yoinked from pickle who yoinked it from blank =D
    public static boolean checkForMod(final String classPath) {
        try {
            Class.forName(classPath);
            TogetherManager.log("Found mod: " + classPath);
            return true;
        }
        catch (ClassNotFoundException | NoClassDefFoundError ex) {
            TogetherManager.log("Could not find mod: " + classPath);
            return false;
        }
    }

    @SpirePatch(clz = AbstractDungeon.class, method="update")
    public static class ConvenienceDebugPresses {
        public static void Postfix(AbstractDungeon __instance) {

        DevConsole.enabled = debug;

        // if (InputActionSet.selectCard_9.isJustPressed()) {
        //     TogetherManager.getCurrentUser().gold++;
        // }

        // if (InputActionSet.selectCard_10.isJustPressed()) {
        //     TogetherManager.currentUser.gold++;
        // }
        //     int y = (int)(Math.random()*AbstractDungeon.map.size());
        //     int x = (int)(Math.random()*AbstractDungeon.map.get(y).size());

        //     for (RemotePlayer p : TogetherManager.players) {
        //         p.x = x;
        //         p.y = y;
        //         p.act = 1;

        //         p.markMapNode();
        //     }
        // }

        // if (Gdx.input.isKeyPressed(60)) {
        //     for (int i = 0; i < 60 ; i++ ) {
        //                     TogetherManager.log("SHIFT");

        //     }
        // }

        // if (Gdx.input.isKeyJustPressed(60)) {
        //     TogetherManager.log("SHIFT");
        //     AbstractDungeon.topLevelEffects.add(new CoopDeathNotification(TogetherManager.getCurrentUser()));
        // }

        }
    }
}
