package chronoMods;

import com.evacipated.cardcrawl.modthespire.lib.*;

import com.megacrit.cardcrawl.localization.*;
import com.megacrit.cardcrawl.screens.custom.CustomMod;
import com.megacrit.cardcrawl.ui.panels.TopPanel;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.screens.stats.CharStat;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.input.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.codedisaster.steamworks.*;
import com.megacrit.cardcrawl.integrations.steam.SteamIntegration;

import basemod.*;
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
import chronoMods.ui.deathScreen.*;
import chronoMods.ui.hud.*;
import chronoMods.ui.lobby.*;
import chronoMods.ui.mainMenu.*;
import chronoMods.utilities.*;

@SpireInitializer
public class TogetherManager implements PostDeathSubscriber, PostInitializeSubscriber {

    // Setup the basic logger
    public static final Logger logger = LogManager.getLogger(TogetherManager.class.getName());

    //This is for the in-game mod settings panel.
    private static final String MODNAME = "Slay the Spire Together";
    private static final String AUTHOR = "Chronometrics";
    private static final String DESCRIPTION = "Enables new Coop and Versus Race modes via Steam Networking.";

    // Stores a list of all the players and the lobby you're connected to
    public static ArrayList<RemotePlayer> players = new ArrayList();
    public static SteamLobby currentLobby;
    public static RemotePlayer currentUser;

    // Images are stored here because of funky basemod junk, these actually should be loaded in RemotePlayerWidget
    public static Texture panelImg;
    public static Texture colourIndicatorImg;
    public static Texture splitPanelImg;
    public static ArrayList<Texture> portraitFrames = new ArrayList();
    public static Texture membersTexture;

    public static Texture mapEmpty;
    public static Texture mapEmptyOutline;

    // Custom UI strings for the mod
    public static Map<String, CustomStrings> CustomStringsMap;

    // Game Mode, just an enum to help mode specific mechanics figure out what's going on
    public static mode gameMode = TogetherManager.mode.Normal;

    // The split tracker
    public static SplitTracker splitTracker = new SplitTracker();

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
        
        mapEmpty = new Texture("chrono/images/mapCoopEmptyRoom.png");
        mapEmptyOutline = new Texture("chrono/images/mapCoopEmptyRoomOutline.png");

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
    }

    // Replace this with uploading versus times to the remote leaderboard on my server later
    public void receivePostDeath() {
        // customMetrics metrics = new customMetrics();
        
        // Thread t = new Thread(metrics);
        // t.setName("Metrics");
        // t.start();
    }

    public static RemotePlayer getCurrentUser() {
        for (RemotePlayer playerInfo : TogetherManager.players) {
            if (playerInfo.isUser(TogetherManager.currentUser.steamUser)) {
                return playerInfo;
            }
        }
        return currentUser;
    }

    @SpirePatch(clz = AbstractDungeon.class, method="update")
    public static class ConvenienceDebugPresses {
        public static void Postfix(AbstractDungeon __instance) {

            // if (InputActionSet.selectCard_1.isJustPressed()) {
            //     NewDeathScreenPatches.raceEndScreen = new RaceEndScreen(null);
            //     AbstractDungeon.screen = NewDeathScreenPatches.Enum.RACEEND;
            // }


        }
    }
}
