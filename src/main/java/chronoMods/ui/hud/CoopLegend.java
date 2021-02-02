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
import chronoMods.steam.*;
import chronoMods.coop.*;
import chronoMods.ui.deathScreen.*;
import chronoMods.ui.hud.*;
import chronoMods.ui.lobby.*;
import chronoMods.ui.mainMenu.*;
import chronoMods.utilities.*;

public class CoopLegend {

    public static Texture CoopLegend;
    public static Texture DefaultLegend;


  // Fix the render position for the legend
  @SpirePatch(clz = Legend.class, method=SpirePatch.CONSTRUCTOR)
  public static class addCourierLegend {
    public static void Postfix(Legend __instance) {
		CoopLegend = ImageMaster.loadImage("chrono/images/CoopLegend.png");
		DefaultLegend = ImageMaster.MAP_LEGEND;
    }
  }

  @SpirePatch(clz = Legend.class, method="update")
  public static class AddMissingItems {
    public static void Prefix(Legend __instance) {
    	if (TogetherManager.gameMode == TogetherManager.mode.Coop && __instance.items.size() == 6) {
		    __instance.items.add(new LegendItem("Courier", new Texture("chrono/images/map/Courier.png"), "Couriers", "Traveling between dimensions and times, the mysetrious Courier offers to send packages to friends... for a small fee.", 6));
		    __instance.items.add(new LegendItem("Empty", new Texture("chrono/images/map/CoopEmptyRoom.png"), "Empty Rooms", "It seems as if you are not the first one to pass this way.", 7));
    	} else if (TogetherManager.gameMode != TogetherManager.mode.Coop && __instance.items.size() > 6) {
            __instance.items.remove(__instance.items.size()-1);
            __instance.items.remove(__instance.items.size()-1);
    	}
    }
  }

  @SpirePatch(clz = Legend.class, method="isIconHovered")
  public static class HoverCourierAndEmpty {
    public static boolean Postfix(boolean __result, Legend __instance, String nodeHovered) {
      if (TogetherManager.gameMode == TogetherManager.mode.Coop) {
	    switch (nodeHovered) {
	      case "C":
	        if (((LegendItem)__instance.items.get(6)).hb.hovered)
	          return true; 
	        return false;
	      case "-":
	        if (((LegendItem)__instance.items.get(7)).hb.hovered)
	          return true; 
	        return false;
	    } 
      }
      return __result;
    }
  }

  @SpirePatch(clz = Legend.class, method="render")
  public static class LegendGraphicFix {
    public static void Prefix(Legend __instance, SpriteBatch sb) {
    	if (TogetherManager.gameMode == TogetherManager.mode.Coop) {
    		ImageMaster.MAP_LEGEND = CoopLegend;
    	} else {
    		ImageMaster.MAP_LEGEND = DefaultLegend;
    	}
    }
  }

  @SpirePatch(clz = MapRoomNode.class, method="render")
  public static class HoverMultiRoom {
  	@SpireInsertPatch(rloc=459-446, localvars={"legendHovered"})
    public static void Insert(MapRoomNode __instance, SpriteBatch sb, @ByRef boolean legendHovered[]) {
      if (TogetherManager.gameMode == TogetherManager.mode.Coop) {
      	
      	if (CoopMultiRoom.secondRoomField.secondRoom.get(__instance) != null && !legendHovered[0]) {
      		legendHovered[0] = AbstractDungeon.dungeonMapScreen.map.legend.isIconHovered(CoopMultiRoom.secondRoomField.secondRoom.get(__instance).getMapSymbol());
      	}

      }
    }
  }
}