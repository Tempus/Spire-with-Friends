package chronoMods.coop.drawable;

import chronoMods.TogetherManager;
import chronoMods.network.RemotePlayer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.map.DungeonMap;
import com.megacrit.cardcrawl.screens.DungeonMapScreen;

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
	    public static void Postfix(DungeonMap __instance, SpriteBatch sb, Color ___baseMapColor)
	    {
	    	if (TogetherManager.gameMode == TogetherManager.mode.Coop) {
	    		if (TogetherManager.getCurrentUser() == null || TogetherManager.getCurrentUser().drawable[AbstractDungeon.actNum-1] == null || AbstractDungeon.actNum-1 < 0 || AbstractDungeon.actNum-1 > 3) { return; }

	    		float a = ___baseMapColor.a;

		    	for (RemotePlayer p : TogetherManager.players) {
		    		if (p != null)
						if (p.drawable[AbstractDungeon.actNum-1] != null)
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