package chronospeed;

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

import chronospeed.*;

@SpireInitializer
public class ChronoCustoms implements PostDeathSubscriber, PostInitializeSubscriber {

    // Setup the basic logger
    public static final Logger logger = LogManager.getLogger(ChronoCustoms.class.getName());

    //This is for the in-game mod settings panel.
    private static final String MODNAME = "Chronoleague Speedruns";
    private static final String AUTHOR = "Chronometrics";
    private static final String DESCRIPTION = "Reports speedruns for the chronoleauge.";

    public static mode gameMode = ChronoCustoms.mode.Normal;

    public static ArrayList<RemotePlayer> players = new ArrayList();

    public static Texture panelImg;
    public static ArrayList<Texture> portraitFrames = new ArrayList();

    public static enum mode
    {
      Normal, Versus, Coop;
      
      private mode() {}
    }

    // Constructor, can't do stuff here due to loading reasons
    public ChronoCustoms() {
        BaseMod.subscribe(this);
    }

    @SuppressWarnings("unused")
    public static void initialize() { 
        new ChronoCustoms();
    }

    @Override
    public void receivePostInitialize() {
        // Load the Mod Badge
        Texture badgeTexture = new Texture("chrono/images/Badge.png");
        panelImg = new Texture("chrono/images/playerPanel.png");
        portraitFrames.add(ImageMaster.loadImage("images/ui/relicFrameRare.png"));
        portraitFrames.add(ImageMaster.loadImage("images/ui/relicFrameUncommon.png"));
        portraitFrames.add(ImageMaster.loadImage("images/ui/relicFrameCommon.png"));
        portraitFrames.add(ImageMaster.loadImage("images/ui/relicFrameBoss.png"));

        // Create the Mod Menu
        ModPanel settingsPanel = new ModPanel();
        BaseMod.registerModBadge(badgeTexture, MODNAME, AUTHOR, DESCRIPTION, settingsPanel);

        NetworkHelper.initialize();
    }

    public void receivePostDeath() {
        customMetrics metrics = new customMetrics();
        
        Thread t = new Thread(metrics);
        t.setName("Metrics");
        t.start();
    }
}
