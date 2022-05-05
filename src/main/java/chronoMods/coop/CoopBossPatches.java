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
import chronoMods.coop.hardmode.*;
import chronoMods.ui.deathScreen.*;
import chronoMods.ui.hud.*;
import chronoMods.ui.lobby.*;
import chronoMods.ui.mainMenu.*;
import chronoMods.utilities.*;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.shop.*;
import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;
import com.megacrit.cardcrawl.screens.options.ConfirmPopup;


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

	  public static SpireReturn Prefix(DungeonMap __instance) {
			if (TogetherManager.gameMode != TogetherManager.mode.Coop) { return SpireReturn.Continue(); }
			if (Loader.isModLoaded("downfall") && EvilModeCharacterSelect.evilMode == true) { return SpireReturn.Continue(); }

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
			((Color)ReflectionHacks.getPrivate(__instance, DungeonMap.class, "bossNodeColor")).lerp(NOT_TAKEN_COLOR, Gdx.graphics.getDeltaTime() * 8.0F);
			} 
			((Color)ReflectionHacks.getPrivate(__instance, DungeonMap.class, "bossNodeColor")).a = ((Color)ReflectionHacks.getPrivate(__instance, DungeonMap.class, "baseMapColor")).a;

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