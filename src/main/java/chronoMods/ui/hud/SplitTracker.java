package chronoMods.ui.hud;

import com.evacipated.cardcrawl.modthespire.lib.*;
import basemod.ReflectionHacks;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;

import com.megacrit.cardcrawl.core.*;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.integrations.steam.*;
import com.megacrit.cardcrawl.helpers.*;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.map.*;
import com.megacrit.cardcrawl.helpers.input.*;
import com.codedisaster.steamworks.*;

import java.util.*;
import java.nio.*;

import chronoMods.*;
import chronoMods.network.steam.*;
import chronoMods.network.*;
import chronoMods.ui.deathScreen.*;
import chronoMods.ui.hud.*;
import chronoMods.ui.lobby.*;
import chronoMods.ui.mainMenu.*;
import chronoMods.utilities.*;

public class SplitTracker {
  public static float X = 1670.0F;
  public static float Y = 700.0F;

  public static float titleOffset = 295.0F;
    
  public Color c = new Color(1.0F, 1.0F, 1.0F, 0.0F);  

  public String[] msg = CardCrawlGame.languagePack.getUIString("Splits").TEXT;
    
  public SplitTracker () {
  }

  // Fix the render position for the legend
  // @SpirePatch(clz = Legend.class, method="update")
  // public static class moveLegendDown {
  //   public static void Postfix(Legend __instance) {
      // if (TogetherManager.gameMode == TogetherManager.mode.Versus) {
      //   ReflectionHacks.setPrivateStaticFinal(Legend.class, "Y", 320.F * Settings.yScale);
      //   TogetherManager.log("Versus Legend Y: " + Legend.Y);
      // }
      // else {
      //   ReflectionHacks.setPrivateStaticFinal(Legend.class, "Y", 600.F * Settings.yScale);
      //   TogetherManager.log("Coop Legend Y: " + Legend.Y);
      // }
  //   }
  // }

  public void update(float mapAlpha, boolean isMapScreen) {
    if (mapAlpha >= 0.8F && isMapScreen) {
      this.c.a = MathHelper.fadeLerpSnap(this.c.a, 1.0F);
    } else {
      this.c.a = MathHelper.fadeLerpSnap(this.c.a, 0.0F);
    } 
  }
  
  public void render(SpriteBatch sb) {
    sb.setColor(this.c);

    // Draw the Panel and Title
    sb.draw(TogetherManager.splitPanelImg, X * Settings.scale - 256.0F, Y * Settings.scale - 400.0F, 256.0F, 400.0F, 512.0F, 800.0F, Settings.scale, Settings.yScale, 0.0F, 0, 0, 512, 800, false, false);

    Color c2 = new Color(MapRoomNode.AVAILABLE_COLOR.r, MapRoomNode.AVAILABLE_COLOR.g, MapRoomNode.AVAILABLE_COLOR.b, this.c.a);
    FontHelper.renderFontCentered(sb, FontHelper.menuBannerFont, msg[0], X * Settings.scale + 4.0F, Y * Settings.scale + titleOffset * Settings.yScale, c);

    // Render our splits
    TogetherManager.getCurrentUser().splits.get("Act 1").render(sb, 1, c);
    TogetherManager.getCurrentUser().splits.get("Act 2").render(sb, 2, c);

    if (Settings.isFinalActAvailable) {
      TogetherManager.getCurrentUser().splits.get("Act 3").render(sb, 3, c);
      TogetherManager.getCurrentUser().splits.get("Final").render(sb, 4, c);
    } else {
      TogetherManager.getCurrentUser().splits.get("Final").render(sb, 3, c);
    }
  }

  // Inject the updates and the rendering
  @SpirePatch(clz = DungeonMap.class, method="update")
  public static class updateSplitTracker {
    public static void Postfix(DungeonMap __instance) {
      if (TogetherManager.gameMode != TogetherManager.mode.Versus) { return; }
      float a = (float)((Color)ReflectionHacks.getPrivate(__instance, DungeonMap.class, "baseMapColor")).a;

      TogetherManager.splitTracker.update(a, (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.MAP));
    }
  }

  @SpirePatch(clz = DungeonMap.class, method="render")
  public static class renderSplitTracker {
    public static void Postfix(DungeonMap __instance, SpriteBatch sb) {
      if (TogetherManager.gameMode != TogetherManager.mode.Versus) { return; }
      TogetherManager.splitTracker.render(sb);
    }
  }

}


