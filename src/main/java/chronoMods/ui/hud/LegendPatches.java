package chronoMods.ui.hud;

import chronoMods.TogetherManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.map.Legend;
import com.megacrit.cardcrawl.map.LegendItem;
import com.megacrit.cardcrawl.map.MapRoomNode;

public class LegendPatches {

	// Fix the render position for the legend
	@SpirePatch(clz = Legend.class, method="render")
	public static class replaceLegendRender {
		public static SpireReturn Prefix(Legend __instance, SpriteBatch sb, Texture ___img) {
			float Y = 600.0F * Settings.yScale;
			if (TogetherManager.gameMode == TogetherManager.mode.Versus)
				Y = 320.F * Settings.yScale;

			if (TogetherManager.gameMode == TogetherManager.mode.Coop) {
    			ImageMaster.MAP_LEGEND = CoopLegend.CoopLegend;
	    	} else {
	    		ImageMaster.MAP_LEGEND = CoopLegend.DefaultLegend;
	    	}
	    	
			sb.setColor(__instance.c);
			sb.draw(ImageMaster.MAP_LEGEND, 1670.0F * Settings.xScale - 256.0F, Y - 400.0F, 256.0F, 400.0F, 512.0F, 800.0F, Settings.scale, Settings.yScale, 0.0F, 0, 0, 512, 800, false, false);

			Color c2 = new Color(MapRoomNode.AVAILABLE_COLOR.r, MapRoomNode.AVAILABLE_COLOR.g, MapRoomNode.AVAILABLE_COLOR.b, __instance.c.a);
			FontHelper.renderFontCentered(sb, FontHelper.menuBannerFont, __instance.TEXT[18], 1670.0F * Settings.xScale, Y + 170.0F * Settings.yScale, c2);

			sb.setColor(c2);
			for (LegendItem i : __instance.items)
				i.render(sb, c2); 
			if (Settings.isControllerMode) {
				sb.setColor(new Color(1.0F, 1.0F, 1.0F, c2.a));
				sb.draw(CInputActionSet.proceed
						.getKeyImg(), 1570.0F * Settings.xScale - 32.0F, Y + 170.0F * Settings.yScale - 32.0F, 32.0F, 32.0F, 64.0F, 64.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 64, 64, false, false);
				if (__instance.isLegendHighlighted) {
					sb.setColor(new Color(1.0F, 0.9F, 0.5F, 0.6F + 
								MathUtils.cosDeg((float)(System.currentTimeMillis() / 2L % 360L)) / 5.0F));
					float doop = 1.0F + (1.0F + MathUtils.cosDeg((float)(System.currentTimeMillis() / 2L % 360L))) / 50.0F;


			    	if (TogetherManager.gameMode == TogetherManager.mode.Coop)
						sb.draw(CoopLegend.CoopLegend, 1670.0F * Settings.scale - 160.0F, (Settings.HEIGHT - Gdx.input.getY()) - 52.0F + 4.0F * Settings.scale, 160.0F, 52.0F, 320.0F, 104.0F, Settings.scale * doop, Settings.scale * doop, 0.0F, 0, 0, 320, 104, false, false);
					else
						sb.draw(___img, 1670.0F * Settings.scale - 160.0F, (Settings.HEIGHT - Gdx.input.getY()) - 52.0F + 4.0F * Settings.scale, 160.0F, 52.0F, 320.0F, 104.0F, Settings.scale * doop, Settings.scale * doop, 0.0F, 0, 0, 320, 104, false, false);
				} 
			} 

		  	return SpireReturn.Return(null);
		}	
	}

	@SpirePatch(clz = LegendItem.class, method="render")
	public static class replaceLegendItemRender {
		public static SpireReturn Prefix(LegendItem __instance, SpriteBatch sb, Color c, Texture ___img, int ___index, String ___label) {
			float Y = 600.0F * Settings.yScale;
			if (TogetherManager.gameMode == TogetherManager.mode.Versus)
				Y = 320.F * Settings.yScale;

			// Texture img = (Texture)ReflectionHacks.getPrivate(__instance, LegendItem.class, "img");
			// int index = (int)ReflectionHacks.getPrivate(__instance, LegendItem.class, "index");



		    sb.setColor(c);
		    if (!Settings.isMobile) {
		      if (__instance.hb.hovered) {
		        sb.draw(___img, 1575.0F * Settings.xScale - 64.0F, Y - 58.0F * Settings.yScale * ___index + 100.0F * Settings.yScale - 64.0F, 64.0F, 64.0F, 128.0F, 128.0F, Settings.scale / 1.2F, Settings.scale / 1.2F, 0.0F, 0, 0, 128, 128, false, false);
		      } else {
		        sb.draw(___img, 1575.0F * Settings.xScale - 64.0F, Y - 58.0F * Settings.yScale * ___index + 100.0F * Settings.yScale - 64.0F, 64.0F, 64.0F, 128.0F, 128.0F, Settings.scale / 1.65F, Settings.scale / 1.65F, 0.0F, 0, 0, 128, 128, false, false);
		      } 
		    } else if (__instance.hb.hovered) {
		      sb.draw(___img, 1575.0F * Settings.xScale - 64.0F, Y - 58.0F * Settings.yScale * ___index + 100.0F * Settings.yScale - 64.0F, 64.0F, 64.0F, 128.0F, 128.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 128, 128, false, false);
		    } 

		    FontHelper.renderFontLeftTopAligned(sb, FontHelper.panelNameFont, ___label, 1670.0F * Settings.xScale - 50.0F * Settings.scale, Y - 58.0F * Settings.yScale * ___index + 100.0F * Settings.yScale + 13.0F * Settings.yScale, c);

		    __instance.hb.move(1670.0F * Settings.xScale, Y - 58.0F * Settings.yScale * ___index + 100.0F * Settings.yScale);
		    if (c.a != 0.0F)
		      __instance.hb.render(sb); 

		  	return SpireReturn.Return(null);		  
		}
	}
}