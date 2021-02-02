package chronoMods.coop;

import com.evacipated.cardcrawl.modthespire.lib.*;
import basemod.interfaces.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.rooms.*;
import com.megacrit.cardcrawl.map.*;
import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.shop.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.rewards.chests.AbstractChest;
import com.megacrit.cardcrawl.vfx.ChestShineEffect;
import com.megacrit.cardcrawl.vfx.scene.SpookyChestEffect;

import chronoMods.*;
import chronoMods.coop.*;
import chronoMods.steam.*;
import chronoMods.ui.deathScreen.*;
import chronoMods.ui.hud.*;
import chronoMods.ui.lobby.*;
import chronoMods.ui.mainMenu.*;
import chronoMods.utilities.*;

import java.util.*;

public class CoopMultiRoom {

	@SpirePatch(clz=MapRoomNode.class, method=SpirePatch.CLASS)
	public static class secondRoomField { public static SpireField<AbstractRoom> secondRoom = new SpireField<>(() -> null); }

    @SpirePatch(clz = MapRoomNode.class, method="setRoom")
    public static class patchInMultiRooms {
        public static void Postfix(MapRoomNode __instance, AbstractRoom room) {
        	if (TogetherManager.gameMode != TogetherManager.mode.Coop) { return; }
        	if (__instance.y == 0) { return; }

            int choice = 0;

            if (room instanceof MonsterRoomElite) {
            	if (AbstractDungeon.mapRng.random(0, 2) == 0) {
            		if (AbstractDungeon.mapRng.random(0, 2) == 0){
	            		CoopMultiRoom.secondRoomField.secondRoom.set(__instance, new RestRoom());
						__instance.room.setMapImg(TogetherManager.mapeliterest, TogetherManager.mapeliterestOutline); }
	            	else {
	            		CoopMultiRoom.secondRoomField.secondRoom.set(__instance, new TreasureRoom());
						__instance.room.setMapImg(TogetherManager.mapelitechest, TogetherManager.mapelitechestOutline); }

					//__instance.room.setMapSymbol("2");
            	}
            }

            else if (room instanceof MonsterRoom) {
                choice = AbstractDungeon.mapRng.random(0, 10);
            	if (choice <= 1) {
	            	CoopMultiRoom.secondRoomField.secondRoom.set(__instance, new ShopRoom());
					__instance.room.setMapImg(TogetherManager.mapmonstershop, TogetherManager.mapmonstershopOutline); 
					//__instance.room.setMapSymbol("2");
                } else if (choice == 2) {
                    __instance.setRoom(new CoopCourierRoom());
            	} else if (choice == 3) {
                    CoopMultiRoom.secondRoomField.secondRoom.set(__instance, new CoopCourierRoom());
                    __instance.room.setMapImg(TogetherManager.mapmonstercourier, TogetherManager.mapmonstercourierOutline); 
                    //__instance.room.setMapSymbol("2");
                } else if (choice <= 5) {
                    CoopMultiRoom.secondRoomField.secondRoom.set(__instance, new MonsterRoom());
                    __instance.room.setMapImg(TogetherManager.mapmonstermonster, TogetherManager.mapmonstermonsterOutline); 
                    //__instance.room.setMapSymbol("2");
                }
            }
        }
    }
}