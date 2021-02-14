package chronoMods.coop;

import com.evacipated.cardcrawl.modthespire.lib.*;

import com.megacrit.cardcrawl.localization.*;
import com.megacrit.cardcrawl.screens.custom.*;
import com.megacrit.cardcrawl.screens.*;
import com.megacrit.cardcrawl.ui.panels.*;
import com.megacrit.cardcrawl.screens.stats.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.megacrit.cardcrawl.core.*;
import com.megacrit.cardcrawl.helpers.*;
import com.megacrit.cardcrawl.helpers.input.*;
import com.megacrit.cardcrawl.map.*;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.rooms.*;
import com.codedisaster.steamworks.*;
import com.megacrit.cardcrawl.integrations.steam.SteamIntegration;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;

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
import chronoMods.coop.*;
import chronoMods.ui.deathScreen.*;
import chronoMods.ui.hud.*;
import chronoMods.ui.lobby.*;
import chronoMods.ui.mainMenu.*;
import chronoMods.utilities.*;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.shop.*;
import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;

public class CoopCourierRoom extends AbstractRoom {

  // Makes the map a little taller
  @SpirePatch(clz = AbstractDungeon.class, method="generateMap")
  public static class GonePostal {
	  @SpireInsertPatch(rloc=624-620, localvars={"mapHeight"})
	  public static void Insert(@ByRef int[] mapHeight) {
		  if (TogetherManager.gameMode != TogetherManager.mode.Coop) { return; }
		  mapHeight[0] = 16;
	  }
  }

  // Turns the second last row into Couriers
  @SpirePatch(clz = AbstractDungeon.class, method="generateMap")
  public static class AWholeRowOfPostmen {
	  @SpireInsertPatch(rloc=654-620, localvars={})
	  public static void Insert() {
		  if (TogetherManager.gameMode != TogetherManager.mode.Coop) { return; }
		  for (MapRoomNode m : AbstractDungeon.map.get(AbstractDungeon.map.size() - 2)) {
			  m.setRoom(new CoopCourierRoom());
			  CoopMultiRoom.secondRoomField.secondRoom.set(m, null);
		  }
	  }
  }

  // Adds a courier to Act 4
  @SpirePatch(clz = TheEnding.class, method="generateSpecialMap")
  public static class TheLastCourier {
	  public static SpireReturn Prefix(TheEnding __instance) {
		  if (TogetherManager.gameMode != TogetherManager.mode.Coop) { return SpireReturn.Continue(); }

		  TheEnding.map = new ArrayList<>();

		  // Create the grid
		  for (int y = 0; y < 6; y++) {
		  	  TheEnding.map.add(new ArrayList<>());
			  
			  for (int x = 0; x < 7; x++) {
			  	  TheEnding.map.get(y).add(new MapRoomNode(x, y));
			  }
		  }

		  // Add the rooms to the middle
		  TheEnding.map.get(0).get(3).room = (AbstractRoom)new RestRoom();
		  TheEnding.map.get(1).get(3).room = (AbstractRoom)new ShopRoom();
		  TheEnding.map.get(2).get(3).room = (AbstractRoom)new MonsterRoomElite();
		  TheEnding.map.get(3).get(3).room = (AbstractRoom)new CoopCourierRoom();
		  TheEnding.map.get(4).get(3).room = (AbstractRoom)new MonsterRoomBoss();
		  TheEnding.map.get(5).get(3).room = (AbstractRoom)new TrueVictoryRoom();

		  // Create the paths between rooms
		  TheEnding.map.get(0).get(3).addEdge(new MapEdge(
		  	TheEnding.map.get(0).get(3).x, TheEnding.map.get(0).get(3).y, TheEnding.map.get(0).get(3).offsetX, TheEnding.map.get(0).get(3).offsetY, 
		  	TheEnding.map.get(1).get(3).x, TheEnding.map.get(1).get(3).y, TheEnding.map.get(1).get(3).offsetX, TheEnding.map.get(1).get(3).offsetY, false));

		  TheEnding.map.get(1).get(3).addEdge(new MapEdge(
		  	TheEnding.map.get(1).get(3).x, TheEnding.map.get(1).get(3).y, TheEnding.map.get(1).get(3).offsetX, TheEnding.map.get(1).get(3).offsetY, 
		  	TheEnding.map.get(2).get(3).x, TheEnding.map.get(2).get(3).y, TheEnding.map.get(2).get(3).offsetX, TheEnding.map.get(2).get(3).offsetY, false));

		  TheEnding.map.get(2).get(3).addEdge(new MapEdge(
		  	TheEnding.map.get(2).get(3).x, TheEnding.map.get(2).get(3).y, TheEnding.map.get(2).get(3).offsetX, TheEnding.map.get(2).get(3).offsetY, 
		  	TheEnding.map.get(3).get(3).x, TheEnding.map.get(3).get(3).y, TheEnding.map.get(3).get(3).offsetX, TheEnding.map.get(3).get(3).offsetY, false));

		  TheEnding.map.get(3).get(3).addEdge(new MapEdge(
		  	TheEnding.map.get(3).get(3).x, TheEnding.map.get(3).get(3).y, TheEnding.map.get(3).get(3).offsetX, TheEnding.map.get(3).get(3).offsetY, 
		  	TheEnding.map.get(4).get(3).x, TheEnding.map.get(4).get(3).y, TheEnding.map.get(4).get(3).offsetX, TheEnding.map.get(4).get(3).offsetY, false));

		  __instance.firstRoomChosen = false;
		  __instance.fadeIn();    

		  return SpireReturn.Return(null);  
	  }
  }

  // Fixes the bullsh hardcoded dungeon map stuff
  @SpirePatch(clz = DungeonMap.class, method="update")
  public static class DungeonMapIsShitty {
	  private static final Color NOT_TAKEN_COLOR = new Color(0.34F, 0.34F, 0.34F, 1.0F);

	  public static SpireReturn Prefix(DungeonMap __instance) {
		  if (TogetherManager.gameMode != TogetherManager.mode.Coop) { return SpireReturn.Continue(); }

		  // ((Color)ReflectionHacks.getPrivate(__instance, DungeonMap.class, "bossNodeColor"));
		  // ((float)ReflectionHacks.getPrivateStatic(DungeonMap.class, "mapOffsetY"));
		  ReflectionHacks.setPrivateStaticFinal(DungeonMap.class, "BOSS_OFFSET_Y", 1516f*Settings.scale);


		  __instance.legend.update(((Color)ReflectionHacks.getPrivate(__instance, DungeonMap.class, "baseMapColor")).a, (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.MAP));
		  ((Color)ReflectionHacks.getPrivate(__instance, DungeonMap.class, "baseMapColor")).a = MathHelper.fadeLerpSnap(((Color)ReflectionHacks.getPrivate(__instance, DungeonMap.class, "baseMapColor")).a, __instance.targetAlpha);
		  __instance.bossHb.move(Settings.WIDTH / 2.0F, DungeonMapScreen.offsetY + ((float)ReflectionHacks.getPrivateStatic(DungeonMap.class, "mapOffsetY")) + (1516.0F * Settings.scale) + (512.0F * Settings.scale) / 2.0F);
		  __instance.bossHb.update();

		  // Controller Crap
		  if (!Settings.isControllerMode) {
			if (__instance.bossHb.hovered) {
			  ((Color)ReflectionHacks.getPrivate(__instance, DungeonMap.class, "reticleColor")).a += Gdx.graphics.getDeltaTime() * 3.0F;
			  if (((Color)ReflectionHacks.getPrivate(__instance, DungeonMap.class, "reticleColor")).a > 1.0F)
				((Color)ReflectionHacks.getPrivate(__instance, DungeonMap.class, "reticleColor")).a = 1.0F; 
			} else {
			  ((Color)ReflectionHacks.getPrivate(__instance, DungeonMap.class, "reticleColor")).a = 0.0F;
			} 
		  }
		  //

		  if ((AbstractDungeon.getCurrRoom()).phase == AbstractRoom.RoomPhase.COMPLETE && AbstractDungeon.screen == AbstractDungeon.CurrentScreen.MAP && (Settings.isDebug || 
			(AbstractDungeon.getCurrMapNode()).y == 15 || (AbstractDungeon.id
			.equals("TheEnding") && (AbstractDungeon.getCurrMapNode()).y == 3)))
			if (__instance.bossHb.hovered && (InputHelper.justClickedLeft || CInputActionSet.select.isJustPressed())) {
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
			  __instance.bossHb.hovered = false;
			}  
		  if (__instance.bossHb.hovered || __instance.atBoss) {
			ReflectionHacks.setPrivate(__instance, DungeonMap.class, "bossNodeColor", MapRoomNode.AVAILABLE_COLOR.cpy());
		  } else {
			((Color)ReflectionHacks.getPrivate(__instance, DungeonMap.class, "bossNodeColor")).lerp(NOT_TAKEN_COLOR, Gdx.graphics.getDeltaTime() * 8.0F);
		  } 
		  ((Color)ReflectionHacks.getPrivate(__instance, DungeonMap.class, "bossNodeColor")).a = ((Color)ReflectionHacks.getPrivate(__instance, DungeonMap.class, "baseMapColor")).a;

		  return SpireReturn.Return(null);
	  }
  }

  private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString("ShopRoom");
  public static final String[] TEXT = uiStrings.TEXT;
	
  public CoopCourier courier;
  
  public CoopCourierRoom() {
	this.phase = AbstractRoom.RoomPhase.COMPLETE;
	this.courier = null;
	this.mapSymbol = "C";
	this.mapImg = TogetherManager.mapCourier;
	this.mapImgOutline = TogetherManager.mapCourierOutline;
	this.rewards.clear();
  }
	
  public void onPlayerEntry() {
	if (!AbstractDungeon.id.equals("TheEnding"))
	  playBGM("SHOP"); 
	AbstractDungeon.overlayMenu.proceedButton.setLabel(TEXT[0]);
	this.courier = new CoopCourier();
  }
	
  public void update() {
	super.update();
	if (this.courier != null)
	  this.courier.update(); 
  }
	
  public void render(SpriteBatch sb) {
	if (this.courier != null)
	  this.courier.render(sb); 
	super.render(sb);
	renderTips(sb);
  }
  
  public void dispose() {
	super.dispose();
	if (this.courier != null) {
	  this.courier.dispose();
	  this.courier = null;
	} 
  }
}
