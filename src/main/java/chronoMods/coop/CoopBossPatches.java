package chronoMods.coop;

import basemod.ReflectionHacks;
import chronoMods.TogetherManager;
import chronoMods.coop.hardmode.StrangeFlame;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.MathHelper;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.map.DungeonMap;
import com.megacrit.cardcrawl.map.MapEdge;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import com.megacrit.cardcrawl.screens.DungeonMapScreen;
import downfall.patches.EvilModeCharacterSelect;


public class CoopBossPatches {
  // Special Downfall Patch to move the nodes to accomodate
  // @SpirePatch(clz = MapRoomNode.class, method=SpirePatch.CONSTRUCTOR)
  // public static class DownfallRaise {
	 //  public static void Prefix(MapRoomNode __instance) {

		//   	__instance.offsetY = __instance.offsetY + 800f * Settings.scale;
		//   TogetherManager.logger.info("Moving to " + __instance.offsetY);
	 //  }
  // }
  protected static final String[] TEXT = (CardCrawlGame.languagePack.getUIString("HardModeSafetyPanel")).TEXT;

  // Fixes the bullsh hardcoded dungeon map stuff
  @SpirePatch(clz = DungeonMap.class, method="update")
  public static class DungeonMapIsShitty {
	  private static final Color NOT_TAKEN_COLOR = new Color(0.34F, 0.34F, 0.34F, 1.0F);

	  public static SpireReturn Prefix(DungeonMap __instance, Color ___baseMapColor, float ___mapOffsetY, Color ___reticleColor, @ByRef Color[] ___bossNodeColor) {
			if (TogetherManager.gameMode != TogetherManager.mode.Coop) { return SpireReturn.Continue(); }
			if (Loader.isModLoaded("downfall") && EvilModeCharacterSelect.evilMode == true) { return SpireReturn.Continue(); }

			// ((Color)ReflectionHacks.getPrivate(__instance, DungeonMap.class, "bossNodeColor"));
			// ((float)ReflectionHacks.getPrivateStatic(DungeonMap.class, "mapOffsetY"));
			ReflectionHacks.setPrivateStaticFinal(DungeonMap.class, "BOSS_OFFSET_Y", 1516f*Settings.scale);

			__instance.legend.update(___baseMapColor.a, (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.MAP));
			___baseMapColor.a = MathHelper.fadeLerpSnap(___baseMapColor.a, __instance.targetAlpha);
			__instance.bossHb.move(Settings.WIDTH / 2.0F, DungeonMapScreen.offsetY + ___mapOffsetY + (1516.0F * Settings.scale) + (512.0F * Settings.scale) / 2.0F);
			__instance.bossHb.update();

			// Controller Crap
			if (!Settings.isControllerMode) {
				if (__instance.bossHb.hovered) {
					___reticleColor.a += Gdx.graphics.getDeltaTime() * 3.0F;
					if (___reticleColor.a > 1.0F)
						___reticleColor.a = 1.0F; 
				} else {
					___reticleColor.a = 0.0F;
				} 
			}

			// How to skip to the boss room
			if (AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMPLETE 
				&& AbstractDungeon.screen == AbstractDungeon.CurrentScreen.MAP 
				&& AbstractDungeon.firstRoomChosen 
				&& !(AbstractDungeon.getCurrMapNode().getRoom() instanceof MonsterRoomBoss)
				&& (AbstractDungeon.getCurrMapNode().y == 15 || (AbstractDungeon.id.equals("TheEnding") && AbstractDungeon.getCurrMapNode().y == 3))
				) {
					
				// if we click, do these things
				if (__instance.bossHb.hovered && (InputHelper.justClickedLeft || CInputActionSet.select.isJustPressed())) {
            		if (AbstractDungeon.player.hasBlight("StrangeFlame") && StrangeFlame.isFirst())
						TogetherManager.infoPopup.show(TEXT[0], TEXT[1], true);
					else
						activateBossNode();

					__instance.bossHb.hovered = false;
				} 

				if (TogetherManager.infoPopup.confirmed && AbstractDungeon.player.hasBlight("StrangeFlame") && StrangeFlame.isFirst()) {
					CoopBossPatches.activateBossNode();
					TogetherManager.infoPopup.confirmed = false;
				}
			}

			if (__instance.bossHb.hovered || __instance.atBoss) {
			ReflectionHacks.setPrivate(__instance, DungeonMap.class, "bossNodeColor", MapRoomNode.AVAILABLE_COLOR.cpy());
			} else {
			___bossNodeColor[0].lerp(NOT_TAKEN_COLOR, Gdx.graphics.getDeltaTime() * 8.0F);
			} 
			___bossNodeColor[0].a = ___baseMapColor.a;

			return SpireReturn.Return(null);
	  }
  }

  // Activate Boss Room
  public static void activateBossNode() {
	(AbstractDungeon.getCurrMapNode()).taken = true;
	MapRoomNode node2 = AbstractDungeon.getCurrMapNode();
	for (MapEdge e : node2.getEdges()) {
		if (e != null)
		  e.markAsTaken(); 
	} 
	InputHelper.justClickedLeft = false;
	CardCrawlGame.music.fadeOutTempBGM();
	MapRoomNode node = new MapRoomNode(-1, 16);
	node.room = (AbstractRoom)new MonsterRoomBoss();
	AbstractDungeon.nextRoom = node;
	if (AbstractDungeon.pathY.size() > 1) {
		AbstractDungeon.pathX.add(AbstractDungeon.pathX.get(AbstractDungeon.pathX.size() - 1));
		AbstractDungeon.pathY.add(Integer.valueOf(((Integer)AbstractDungeon.pathY.get(AbstractDungeon.pathY.size() - 1)).intValue() + 1));
	} else {
		AbstractDungeon.pathX.add(Integer.valueOf(1));
		AbstractDungeon.pathY.add(Integer.valueOf(16));
	} 
	AbstractDungeon.nextRoomTransitionStart();
  }

}