package chronoMods.ui.deathScreen;

import basemod.ReflectionHacks;
import chronoMods.TogetherManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.HttpRequestBuilder;
import com.codedisaster.steamworks.SteamApps;
import com.codedisaster.steamworks.SteamID;
import com.codedisaster.steamworks.SteamNativeHandle;
import com.google.gson.Gson;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.integrations.steam.SteamIntegration;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class customMetrics implements Runnable {

  private HashMap<Object, Object> params = new HashMap();
  private Gson gson = new Gson();
  private long lastPlaytimeEnd;
  public static final SimpleDateFormat timestampFormatter = new SimpleDateFormat("yyyyMMddHHmmss");


  // The URL for the json parsing
  public static final String URL = "http://www.chronometry.ca/League/seasonfour/speedrun.php";
  
  public void uploadResults()
  {
    // Make a Hashmap to convert to json later
    HashMap<String, Serializable> event = new HashMap();

    // Add the in-game player name, and the alias
    event.put("name", TogetherManager.getCurrentUser().userName);

    // Add the Steam ID
    SteamApps steamApps = (SteamApps)ReflectionHacks.getPrivate(CardCrawlGame.publisherIntegration, SteamIntegration.class, "steamApps");
    SteamID id = steamApps.getAppOwner();
    long newid = (long)ReflectionHacks.getPrivate(id, SteamNativeHandle.class, "handle");

    event.put("steam", newid);
    event.put("steamUser", TogetherManager.currentUser.getAccountID());

    // Add details about the ruleset
    event.put("character", AbstractDungeon.player.chosenClass.name());
    event.put("seed", Settings.seed.toString());
    event.put("ascension", Integer.valueOf(AbstractDungeon.ascensionLevel));

    event.put("ironman", NewDeathScreenPatches.Ironman);
    event.put("neowBonus", Boolean.valueOf(Settings.isTrial));
    event.put("heart", Settings.isFinalActAvailable);

    // Run information

    // Timestamp
    event.put("local_time", timestampFormatter.format(Calendar.getInstance().getTime()));

    // Start info
    event.put("neow_bonus", CardCrawlGame.metricData.neowBonus);
    event.put("neow_cost", CardCrawlGame.metricData.neowCost);

    // Death info
    event.put("floor_reached", Integer.valueOf(AbstractDungeon.floorNum));

    if (NewDeathScreenPatches.EndScreenBase != null) {
      if (NewDeathScreenPatches.EndScreenBase.monsters != null) {
        event.put("killed_by", AbstractDungeon.lastCombatMetricKey);
      }
    } else {
      event.put("killed_by", null);
    }

    // Splits
    ArrayList<Float> splitList = new ArrayList<>();

    splitList.add(TogetherManager.currentUser.splits.get("act_1").playtime);
    splitList.add(TogetherManager.currentUser.splits.get("act_2").playtime);
    splitList.add(TogetherManager.currentUser.splits.get("act_3").playtime);
    splitList.add(TogetherManager.currentUser.splits.get("final").playtime);

    event.put("splits", splitList);

    // Cards and Relics
    event.put("master_deck", AbstractDungeon.player.masterDeck.getCardIdsForMetrics());
    event.put("relics", AbstractDungeon.player.getRelicNames());



    // Convert to json and ship it.
    String data = this.gson.toJson(event);
    HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
    
    Net.HttpRequest httpRequest = requestBuilder.newRequest().method("POST").url(URL).header("Content-Type", "application/json").header("Accept", "application/json").header("User-Agent", "curl/7.43.0").build();
    httpRequest.setContent(data);
    Gdx.net.sendHttpRequest(httpRequest, new Net.HttpResponseListener()
    {
      public void handleHttpResponse(Net.HttpResponse httpResponse) {}
      public void failed(Throwable t) {}
      public void cancelled() {}
    });
  }
  
  public void run()
  {
    uploadResults();
  }
}
