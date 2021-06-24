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
import com.megacrit.cardcrawl.relics.*;
import com.megacrit.cardcrawl.blights.*;
import com.megacrit.cardcrawl.screens.options.*;
import com.codedisaster.steamworks.*;

import basemod.*;
import basemod.eventUtil.*;
import basemod.helpers.*;
import basemod.abstracts.*;
import basemod.interfaces.*;
import basemod.patches.whatmod.*;

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
import chronoMods.network.steam.*;
import chronoMods.network.*;
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
    public static final String DESCRIPTION = "Enables new Coop and Versus Race modes via Steam or Discord Networking.";
    public static final float VERSION = 2.0f;

    public static int modHash;
    public static boolean safeMods = true;

    // Stores a list of all the players and the lobby you're connected to
    public static CopyOnWriteArrayList<RemotePlayer> players = new CopyOnWriteArrayList();
    public static Lobby currentLobby;
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

    public static Texture cusTexDaily;
    public static Texture cusTexSnecko;
    public static Texture cusTexIncept;
    public static Texture cusTexForm;
    public static Texture cusTexWonder;
    public static Texture cusTexStarter;

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

    // The Heart Cutscene
    public static CoopCutscene cutscene; 

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

        cusTexDaily = ImageMaster.loadImage("chrono/images/uncertain_future.png");
        cusTexSnecko = ImageMaster.loadImage("chrono/images/sneckoEye.png");
        cusTexIncept = ImageMaster.loadImage("chrono/images/top.png");
        cusTexForm = ImageMaster.loadImage("chrono/images/colossus.png");
        cusTexWonder = ImageMaster.loadImage("chrono/images/7.png");
        cusTexStarter = ImageMaster.loadImage("chrono/images/deck.png");

        // Create the fallback font
        CreateFallbackFont();
        // if (FontHelper.leaderboardFont == null)
        //     TogetherManager.log("Big fricking Oopsie, no leaderboard font.");
        // TogetherManager.fallbackFont = FontHelper.leaderboardFont;

        // Create the Mod Menu
        ModPanel settingsPanel = new ModPanel();
        BaseMod.registerModBadge(badgeTexture, MODNAME, AUTHOR, DESCRIPTION, settingsPanel);

        // Initialize the Networking functions
        NetworkHelper.initialize();

        // Custom strings
        CustomStringsMap = CustomStrings.importCustomStrings();

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

        // Check for compliant mods
        modHash = getModHash();
        safeMods = areModsSafe();
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

        teamBlights.clear();
    }

    // Despite the name, published once at the beginning of a run after the first Dungeon inits and never again
    public void receivePostDungeonInitialize() {
        teamBlights.clear();
        CardCrawlGame.mainMenuScreen.doorUnlockScreen = new DoorUnlockScreen();

        if (gameMode != mode.Coop) { return; }

        // Make all the UI screens we need nice and fresh
        courierScreen = new CoopCourierScreen();
        teamRelicScreen = new CoopBossRelicSelectScreen();
        CardCrawlGame.mainMenuScreen.doorUnlockScreen = new CoopDoorUnlockScreen();
        paintWidget = new MapCanvasController();
        cutscene = new CoopCutscene();

        // Add in all the team relics
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
        teamBlights.add(new MessageInABottle());
        Collections.shuffle(teamBlights, new Random(AbstractDungeon.relicRng.randomLong()));
    }

    public static RemotePlayer getCurrentUser() {
        for (RemotePlayer playerInfo : TogetherManager.players) {
            if (playerInfo.isUser(TogetherManager.currentUser)) {
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
            case DEU:
                language = "deu";
                break;
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

        // Reset the game timer
        if (!NetworkHelper.embarked) {
            VersusTimer.timer = 0;
            VersusTimer.startTime = System.currentTimeMillis();
        }

        NetworkHelper.embarked = true;

        if (TogetherManager.gameMode == TogetherManager.mode.Coop) {
            (new StringOfFate()).instantObtain(AbstractDungeon.player, 0, false);
            // AbstractDungeon.player.getBlight("StringOfFate").counter = 1;
            // AbstractDungeon.player.getBlight("StringOfFate").increment = 1;
            // (new Dimensioneel()).instantObtain(AbstractDungeon.player, 1, false);
            // (new PneumaticPost()).instantObtain(AbstractDungeon.player, 2, false);
        }
    }

    public static void clearMultiplayerData() {
        // Reset Multiplayer components
        TogetherManager.currentLobby = null;
        TogetherManager.players.clear();
        TopPanelPlayerPanels.playerWidgets.clear();
        NetworkHelper.leaveLobby();
    }

    public static int getModHash() {
        List<ModInfo> mods = Arrays.asList(Loader.MODINFOS);
        Collections.sort(mods, (o1, o2) -> o1.ID.compareTo(o2.ID));
        return Arrays.hashCode(mods.toArray());
    }

    public static boolean areModsSafe() {
        // Iterate over everything, then call findModName(Class<?> cls) to get a string that returns null, "Unknown", or the mod name. Also exclude Spire with Friends, duh
        // No, better plan. Just see if basemod has any stuff to it's add lists.

        // Example of whatmod for cards. Useful in the future if I decide to list out infringing mods.
        //
        // for (AbstractCard c : CardLibrary.cards) {
        //     String modName = WhatMod.findModName(c.getClass());
        //     if (modName != null || !modName.equals("Unknown"))
        //         return false;
        // }

        // Any custom cards?
        if (BaseMod.getRedCardsToAdd().size() > 0)              { return false; }
        if (BaseMod.getRedCardsToRemove().size() > 0)           { return false; }   
        if (BaseMod.getGreenCardsToAdd().size() > 0)            { return false; }  
        if (BaseMod.getGreenCardsToRemove().size() > 0)         { return false; }     
        if (BaseMod.getBlueCardsToAdd().size() > 0)             { return false; } 
        if (BaseMod.getBlueCardsToRemove().size() > 0)          { return false; }    
        if (BaseMod.getPurpleCardsToAdd().size() > 0)           { return false; }   
        if (BaseMod.getPurpleCardsToRemove().size() > 0)        { return false; }      
        if (BaseMod.getColorlessCardsToAdd().size() > 0)        { return false; }      
        if (BaseMod.getColorlessCardsToRemove().size() > 0)     { return false; }
        if (BaseMod.getCurseCardsToAdd().size() > 0)            { return false; }  
        if (BaseMod.getCurseCardsToRemove().size() > 0)         { return false; }  
        if (BaseMod.getCustomCardsToAdd().size() > 0)           { return false; }   
        if (BaseMod.getCustomCardsToRemove().size() > 0)        { return false; }      
        if (BaseMod.getCustomCardsToRemoveColors().size() > 0)  { return false; }

        // Any custom relics?
        if (BaseMod.listAllRelicIDs().size() > 0)               { return false; }

        // Events
        if (((HashSet<String>)ReflectionHacks.getPrivateStatic(EventUtils.class, "eventIDs")).size() > 0)                       { return false; }

        // Monsters
        if (((ArrayList<String>)ReflectionHacks.getPrivateStatic(BaseMod.class, "encounterList")).size() > 0)                   { return false; }

        // Bosses
        if (((HashMap<String, List<BaseMod.BossInfo>>)ReflectionHacks.getPrivateStatic(BaseMod.class, "customBosses")).size() > 0)      { return false; }

        // Characters
        if (BaseMod.getModdedCharacters().size() > 0)            { return false; }

        // Potions
        if (BaseMod.getPotionIDs().size() > 0)                   { return false; }
        if (BaseMod.getPotionsToRemove().size() > 0)             { return false; }

        // All Clear
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
        //     TogetherManager.log("Rare Relics");
        //     for (String r: AbstractDungeon.rareRelicPool) {
        //         TogetherManager.log(r);
        //     }

        //     TogetherManager.log("Boss Relics");
        //     for (String r: AbstractDungeon.bossRelicPool) {
        //         TogetherManager.log(r);
        //     }
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
