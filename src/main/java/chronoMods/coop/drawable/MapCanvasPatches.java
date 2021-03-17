package chronoMods.coop.drawable;

import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen;
import com.megacrit.cardcrawl.helpers.input.*;
import com.megacrit.cardcrawl.core.*;
import com.megacrit.cardcrawl.map.*;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.screens.DungeonMapScreen;

import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.graphics.Pixmap.Blending;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.utils.Disposable;
import basemod.*;

import chronoMods.*;

public class MapCanvasPatches {

	@SpirePatch(clz=DungeonMapScreen.class, method="update")
	public static class MapCanvasUpdate {
	    public static void Prefix(DungeonMapScreen __instance)
	    {
	    	if (TogetherManager.gameMode == TogetherManager.mode.Coop && AbstractDungeon.screen == AbstractDungeon.CurrentScreen.MAP) {
	    		if (TogetherManager.getCurrentUser() == null || TogetherManager.getCurrentUser().drawable[AbstractDungeon.actNum-1] == null || AbstractDungeon.actNum-1 < 0 || AbstractDungeon.actNum-1 > 3) { return; }

				TogetherManager.getCurrentUser().drawable[AbstractDungeon.actNum-1].update();
	    		TogetherManager.paintWidget.update();
	    	}
	    }
	}

	@SpirePatch(clz=DungeonMap.class, method="render")
	public static class MapCanvasRender {
	    public static void Postfix(DungeonMap __instance, SpriteBatch sb)
	    {
	    	if (TogetherManager.gameMode == TogetherManager.mode.Coop) {
	    		if (TogetherManager.getCurrentUser() == null || TogetherManager.getCurrentUser().drawable[AbstractDungeon.actNum-1] == null || AbstractDungeon.actNum-1 < 0 || AbstractDungeon.actNum-1 > 3) { return; }

	    		float a = ((Color)ReflectionHacks.getPrivate(__instance, DungeonMap.class, "baseMapColor")).a;

		    	for (RemotePlayer p : TogetherManager.players) {
			    	p.drawable[AbstractDungeon.actNum-1].render(sb, a);
		    	}
	    		TogetherManager.paintWidget.render(sb, a);
		    }
	    }
	}

	@SpirePatch(clz=DungeonMapScreen.class, method="render")
	public static class MapCanvasPaletteRender {
	    public static void Postfix(DungeonMapScreen __instance, SpriteBatch sb)
	    {
	    	if (TogetherManager.gameMode == TogetherManager.mode.Coop) {
	    		TogetherManager.paintWidget.render(sb, 1f);
		    }
	    }
	}
}