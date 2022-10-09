package chronoMods.ui.hud;

import chronoMods.TogetherManager;
import chronoMods.coop.CoopMultiRoom;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.map.Legend;
import com.megacrit.cardcrawl.map.LegendItem;
import com.megacrit.cardcrawl.map.MapRoomNode;

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
        String[] msg = CardCrawlGame.languagePack.getUIString("CoopLegend").TEXT;
		    __instance.items.add(new LegendItem(msg[0], new Texture("chrono/images/map/Courier.png"), msg[1], msg[2], 6));
		    __instance.items.add(new LegendItem(msg[3], new Texture("chrono/images/map/CoopEmptyRoom.png"), msg[4], msg[5], 7));
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

  // @SpirePatch(clz = Legend.class, method="render")
  // public static class LegendGraphicFix {
  //   public static void Prefix(Legend __instance, SpriteBatch sb) {
  //   	if (TogetherManager.gameMode == TogetherManager.mode.Coop) {
  //   		ImageMaster.MAP_LEGEND = CoopLegend;
  //   	} else {
  //   		ImageMaster.MAP_LEGEND = DefaultLegend;
  //   	}
  //   }
  // }

  @SpirePatch(clz = MapRoomNode.class, method="render")
  public static class HoverMultiRoom {
  	@SpireInsertPatch(rloc=459-446, localvars={"legendHovered"})
    public static void Insert(MapRoomNode __instance, SpriteBatch sb, @ByRef boolean legendHovered[]) {
      if (TogetherManager.gameMode == TogetherManager.mode.Coop) {
      	
      	if (CoopMultiRoom.secondRoomField.secondRoom.get(__instance) != null && !legendHovered[0]) {
      		legendHovered[0] = AbstractDungeon.dungeonMapScreen.map.legend.isIconHovered(CoopMultiRoom.secondRoomField.secondRoom.get(__instance).getMapSymbol());
      	}

        if (CoopMultiRoom.thirdRoomField.thirdRoom.get(__instance) != null && !legendHovered[0]) {
          legendHovered[0] = AbstractDungeon.dungeonMapScreen.map.legend.isIconHovered(CoopMultiRoom.thirdRoomField.thirdRoom.get(__instance).getMapSymbol());
        }
      }
    }
  }
}