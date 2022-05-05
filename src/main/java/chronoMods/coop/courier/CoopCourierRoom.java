package chronoMods.coop;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.*;

import downfall.patches.EvilModeCharacterSelect;

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
import chronoMods.network.steam.*;
import chronoMods.network.*;
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
				m.hasEmeraldKey = false;

	            CoopMultiRoom.secondRoomField.secondRoom.set(m, new CoopCourierRoom());   
	            CoopMultiRoom.thirdRoomField.thirdRoom.set(m, null);            
		  }
	  }
  }

  // Adds a courier to Act 4
  @SpirePatch(clz = TheEnding.class, method="generateSpecialMap")
  public static class TheLastCourier {
	  public static SpireReturn Prefix(TheEnding __instance) {
		  if (TogetherManager.gameMode != TogetherManager.mode.Coop) { return SpireReturn.Continue(); }
		  if (NewMenuButtons.newGameScreen.downfallToggle.isTicked()) { return SpireReturn.Continue(); }

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
		  if (AbstractDungeon.player.hasBlight("StrangeFlame"))
			  TheEnding.map.get(0).get(3).hasEmeraldKey = true;
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
