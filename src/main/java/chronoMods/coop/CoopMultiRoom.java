package chronoMods.coop;

import chronoMods.TogetherManager;
import chronoMods.coop.courier.CoopCourierRoom;
import chronoMods.coop.relics.DowsingRod;
import chronoMods.network.RemotePlayer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.map.MapEdge;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.rooms.*;
import com.megacrit.cardcrawl.screens.DungeonMapScreen;

import java.util.ArrayList;

public class CoopMultiRoom {

	@SpirePatch(clz=MapRoomNode.class, method=SpirePatch.CLASS)
	public static class secondRoomField { public static SpireField<AbstractRoom> secondRoom = new SpireField<>(() -> null); }

    @SpirePatch(clz=MapRoomNode.class, method=SpirePatch.CLASS)
    public static class thirdRoomField  { public static SpireField<AbstractRoom> thirdRoom  = new SpireField<>(() -> null); }

    public static int getParentNodeCount(MapRoomNode node) {
        if (node.y-1 < 0) { return 0; }
        
        int count = 0;
        for (MapRoomNode parentNode : AbstractDungeon.map.get(node.y-1)) {
            for (MapEdge edge : parentNode.getEdges()) {
                if (node.x == edge.dstX && node.y == edge.dstY)
                    count++;
            } 
        }
        return count;
    }

    @SpirePatch(clz = MapRoomNode.class, method="setRoom")
    public static class patchInMultiRooms {
        public static void Postfix(MapRoomNode __instance, AbstractRoom room) {
            // Only add coop rooms in Versus
        	if (TogetherManager.gameMode != TogetherManager.mode.Coop) { return; }
        	if (__instance.y == 0) { return; }

            if (AbstractDungeon.player.hasBlight("DowsingRod")) {
                DowsingRod.multiStackRooms(__instance, room);
                return;
            }

            int pathCount = __instance.getEdges().size() + CoopMultiRoom.getParentNodeCount(__instance);
            TogetherManager.log ("Paths from: " + __instance.x + ", " + __instance.y + " - " + pathCount);

            if (room instanceof MonsterRoomElite) {
                if (pathCount == 3) {
                    CoopMultiRoom.secondRoomField.secondRoom.set(__instance, new RestRoom());   
                } else if (pathCount == 4) {
                    CoopMultiRoom.secondRoomField.secondRoom.set(__instance, new TreasureRoom());   
                } else if (pathCount == 5) {
                    CoopMultiRoom.secondRoomField.secondRoom.set(__instance, new RestRoom());   
                    CoopMultiRoom.thirdRoomField.thirdRoom.set(__instance, new TreasureRoom());            
                } else if (pathCount == 6) {
                    CoopMultiRoom.secondRoomField.secondRoom.set(__instance, new TreasureRoom());   
                    CoopMultiRoom.thirdRoomField.thirdRoom.set(__instance, new TreasureRoom());            
                }
            }

            else if (room instanceof MonsterRoom) {
                if (AbstractDungeon.mapRng.random(0, 6) == 0 && IsNotAdjacentToCourier(__instance)) {
                    __instance.setRoom(new CoopCourierRoom());
                    if (pathCount == 3) {
                    } else if (pathCount == 4) {
                        CoopMultiRoom.secondRoomField.secondRoom.set(__instance, new ShopRoom());   
                    } else if (pathCount == 5) {
                        CoopMultiRoom.secondRoomField.secondRoom.set(__instance, new ShopRoom());   
                        CoopMultiRoom.thirdRoomField.thirdRoom.set(__instance, new TreasureRoom());            
                    } else if (pathCount == 6) {
                        CoopMultiRoom.secondRoomField.secondRoom.set(__instance, new TreasureRoom());   
                        CoopMultiRoom.thirdRoomField.thirdRoom.set(__instance, new TreasureRoom());            
                    }
                } else {
                    if (pathCount == 3) {
                        CoopMultiRoom.secondRoomField.secondRoom.set(__instance, new MonsterRoom());
                        if (AbstractDungeon.player.hasBlight("StrangeFlame"))
                            __instance.hasEmeraldKey = true;  
                    } else if (pathCount == 4) {
                        CoopMultiRoom.secondRoomField.secondRoom.set(__instance, new EventRoom());   
                    } else if (pathCount == 5) {
                        CoopMultiRoom.secondRoomField.secondRoom.set(__instance, new MonsterRoom());   
                        CoopMultiRoom.thirdRoomField.thirdRoom.set(__instance, new MonsterRoom());
                        if (AbstractDungeon.player.hasBlight("StrangeFlame"))
                            __instance.hasEmeraldKey = true;      
                    } else if (pathCount == 6) {
                        CoopMultiRoom.secondRoomField.secondRoom.set(__instance, new MonsterRoomElite());   
                        CoopMultiRoom.thirdRoomField.thirdRoom.set(__instance, new MonsterRoomElite());            
                    }
                }
            }

            else if (room instanceof RestRoom) {
                if (pathCount == 3) {
                    CoopMultiRoom.secondRoomField.secondRoom.set(__instance, new RestRoom());   
                } else if (pathCount == 4) {
                    CoopMultiRoom.secondRoomField.secondRoom.set(__instance, new RestRoom());   
                } else if (pathCount == 5) {
                    CoopMultiRoom.secondRoomField.secondRoom.set(__instance, new RestRoom());   
                    CoopMultiRoom.thirdRoomField.thirdRoom.set(__instance, new RestRoom());            
                } else if (pathCount == 6) {
                    CoopMultiRoom.secondRoomField.secondRoom.set(__instance, new RestRoom());   
                    CoopMultiRoom.thirdRoomField.thirdRoom.set(__instance, new RestRoom());   
                }
            }

            else if (room instanceof TreasureRoom) {
                if (pathCount == 3) {
                    CoopMultiRoom.secondRoomField.secondRoom.set(__instance, new TreasureRoom());   
                } else if (pathCount == 4) {
                    CoopMultiRoom.secondRoomField.secondRoom.set(__instance, new TreasureRoom());   
                } else if (pathCount == 5) {
                    CoopMultiRoom.secondRoomField.secondRoom.set(__instance, new TreasureRoom());   
                    CoopMultiRoom.thirdRoomField.thirdRoom.set(__instance, new TreasureRoom());            
                } else if (pathCount == 6) {
                    CoopMultiRoom.secondRoomField.secondRoom.set(__instance, new TreasureRoom());   
                    CoopMultiRoom.thirdRoomField.thirdRoom.set(__instance, new TreasureRoom());            
                }
            }

            else if (room instanceof ShopRoom) {
                if (pathCount == 3) {
                } else if (pathCount == 4) {
                    CoopMultiRoom.secondRoomField.secondRoom.set(__instance, new CoopCourierRoom());
                } else if (pathCount == 5) {
                    CoopMultiRoom.secondRoomField.secondRoom.set(__instance, new CoopCourierRoom());   
                    CoopMultiRoom.thirdRoomField.thirdRoom.set(__instance, new TreasureRoom());            
                } else if (pathCount == 6) {
                    CoopMultiRoom.secondRoomField.secondRoom.set(__instance, new TreasureRoom());   
                    CoopMultiRoom.thirdRoomField.thirdRoom.set(__instance, new TreasureRoom());            
                }
            }

            else if (room instanceof EventRoom) {
                if (pathCount == 3) {
                    CoopMultiRoom.secondRoomField.secondRoom.set(__instance, new MonsterRoom());   
                } else if (pathCount == 4) {
                    CoopMultiRoom.secondRoomField.secondRoom.set(__instance, new EventRoom());   
                } else if (pathCount == 5) {
                    CoopMultiRoom.secondRoomField.secondRoom.set(__instance, new EventRoom());   
                    CoopMultiRoom.thirdRoomField.thirdRoom.set(__instance, new EventRoom());            
                } else if (pathCount == 6) {
                    CoopMultiRoom.secondRoomField.secondRoom.set(__instance, new MonsterRoom());   
                    CoopMultiRoom.thirdRoomField.thirdRoom.set(__instance, new MonsterRoomElite());            
                }
            }
        }
    }

    public static boolean IsNotAdjacentToCourier(MapRoomNode n) {
        // Floor 16 is the boss, 15 is the fire after a courier, 14 is the fixed courier floor, 13 is always adjacent to a fixed courier
        if (n.y > 12) { return false; }

        // Rooms are assigned bottom up, so we only need to check if the parents have couriers beside them
        ArrayList<MapRoomNode> parents = n.getParents();
        for (MapRoomNode parent : parents) {
            if (parent.getRoom() instanceof CoopCourierRoom) {
                return false;
            }
        }

        return true;
    }

    @SpirePatch(clz = MapRoomNode.class, method="render")
    public static class HoverMultiRoom {
        public static final float OFFSET_X = Settings.isMobile ? (496.0F * Settings.xScale) : (560.0F * Settings.xScale);
        private static final float OFFSET_Y = 180.0F * Settings.scale;
        private static final float SPACING_X = Settings.isMobile ? ((Settings.xScale * 64.0F) * 2.2F) : ((Settings.xScale * 64.0F) * 2.0F);
        public static final Color AVAILABLE_COLOR = new Color(0.09F, 0.13F, 0.17F, 1.0F);

        @SpireInsertPatch(rloc=464-446, localvars={"legendHovered"})
        public static SpireReturn Insert(MapRoomNode __instance, SpriteBatch sb, float ___scale, float ___angle, boolean legendHovered) {
            if (TogetherManager.gameMode == TogetherManager.mode.Coop) {
            
                AbstractRoom secondRoom = CoopMultiRoom.secondRoomField.secondRoom.get(__instance);
                AbstractRoom thirdRoom = CoopMultiRoom.thirdRoomField.thirdRoom.get(__instance);

                float iconX = __instance.x * SPACING_X + OFFSET_X - 64.0F + __instance.offsetX;
                float iconY = __instance.y * Settings.MAP_DST_Y + OFFSET_Y + DungeonMapScreen.offsetY - 64.0F + __instance.offsetY;

                float tiny = legendHovered ? 0.68f : 0.5f;
                float primary = 0.9F;
                float secondary = 0.7f;

                // Manual fix
                if (__instance.room == null || __instance.room.getMapImgOutline() == null) { return SpireReturn.Return(null); }

                // Draw Outlines
                if (secondRoom == null && thirdRoom == null) {
                    if (TogetherManager.foundmod_colormap) { setIconOutlineColor(__instance.room,sb); }
                    sb.draw(__instance.room.getMapImgOutline(), iconX, iconY, 64.0F, 64.0F, 128.0F, 128.0F,         ___scale * Settings.scale, ___scale * Settings.scale, 0.0F, 0, 0, 128, 128, false, false);
                } else if (thirdRoom == null) {
                    if (TogetherManager.foundmod_colormap) { setIconOutlineColor(secondRoom,sb); }
                    sb.draw(secondRoom.getMapImgOutline(),      iconX+20f*___scale, iconY-20f*___scale, 64.0F, 64.0F, 128.0F, 128.0F, tiny * Settings.scale * secondary, tiny * Settings.scale * secondary, 0.0F, 0, 0, 128, 128, false, false);
                    if (TogetherManager.foundmod_colormap) { setIconOutlineColor(__instance.room,sb); }
                    sb.draw(__instance.room.getMapImgOutline(), iconX-10f*tiny, iconY+10f*tiny, 64.0F, 64.0F, 128.0F, 128.0F, ___scale * Settings.scale * primary, ___scale * Settings.scale * primary, 0.0F, 0, 0, 128, 128, false, false);
                } else {
                    if (TogetherManager.foundmod_colormap) { setIconOutlineColor(thirdRoom,sb); }
                    sb.draw(thirdRoom.getMapImgOutline(),       iconX+20f*tiny, iconY-20f*tiny, 64.0F, 64.0F, 128.0F, 128.0F, tiny * Settings.scale * secondary, tiny * Settings.scale * secondary, 0.0F, 0, 0, 128, 128, false, false);
                    if (TogetherManager.foundmod_colormap) { setIconOutlineColor(secondRoom,sb); }
                    sb.draw(secondRoom.getMapImgOutline(),      iconX-20f*tiny, iconY-20f*tiny, 64.0F, 64.0F, 128.0F, 128.0F, tiny * Settings.scale * secondary, tiny * Settings.scale * secondary, 0.0F, 0, 0, 128, 128, false, false);
                    if (TogetherManager.foundmod_colormap) { setIconOutlineColor(__instance.room,sb); }
                    sb.draw(__instance.room.getMapImgOutline(), iconX, iconY+15f*___scale, 64.0F, 64.0F, 128.0F, 128.0F,     ___scale * Settings.scale * primary, ___scale * Settings.scale * primary, 0.0F, 0, 0, 128, 128, false, false);
                }

                // Set Colour
                if (__instance.taken) {
                    sb.setColor(AVAILABLE_COLOR);
                } else {
                    sb.setColor(__instance.color);
                } 
                if (legendHovered)
                  sb.setColor(AVAILABLE_COLOR); 

                Color c = sb.getColor();
                // Color cf = c.cpy();
                // cf.a = 0.5f;

                // Draw Room Icon
                if (secondRoom == null && thirdRoom == null) {
                    if (TogetherManager.foundmod_colormap) { setIconColor(__instance.room,sb,1f); }
                    sb.draw(__instance.room.getMapImg(), iconX, iconY, 64.0F, 64.0F, 128.0F, 128.0F,         ___scale * Settings.scale, ___scale * Settings.scale, 0.0F, 0, 0, 128, 128, false, false);
                } else if (thirdRoom == null) {
                    c.a = 0.5f;
                    sb.setColor(c);
                    if (TogetherManager.foundmod_colormap) { setIconColor(secondRoom,sb,c.a); }
                    sb.draw(secondRoom.getMapImg(),      iconX+20f*tiny, iconY-20f*tiny, 64.0F, 64.0F, 128.0F, 128.0F, tiny * Settings.scale * secondary, tiny * Settings.scale * secondary, 0.0F, 0, 0, 128, 128, false, false);
                    c.a = 1.0f;
                    sb.setColor(c);
                    if (TogetherManager.foundmod_colormap) { setIconColor(__instance.room,sb,1f); }
                    sb.draw(__instance.room.getMapImg(), iconX-10f*___scale, iconY+10f*___scale, 64.0F, 64.0F, 128.0F, 128.0F, ___scale * Settings.scale * primary, ___scale * Settings.scale * primary, 0.0F, 0, 0, 128, 128, false, false);
                } else {
                    c.a = 0.5f;
                    sb.setColor(c);
                    if (TogetherManager.foundmod_colormap) { setIconColor(thirdRoom,sb,c.a); }
                    sb.draw(thirdRoom.getMapImg(),       iconX+20f*tiny, iconY-20f*tiny, 64.0F, 64.0F, 128.0F, 128.0F, tiny * Settings.scale * secondary, tiny * Settings.scale * secondary, 0.0F, 0, 0, 128, 128, false, false);
                    if (TogetherManager.foundmod_colormap) { setIconColor(secondRoom,sb,c.a); }
                    sb.draw(secondRoom.getMapImg(),      iconX-20f*tiny, iconY-20f*tiny, 64.0F, 64.0F, 128.0F, 128.0F, tiny * Settings.scale * secondary, tiny * Settings.scale * secondary, 0.0F, 0, 0, 128, 128, false, false);
                    c.a = 1.0f;
                    sb.setColor(c);
                    if (TogetherManager.foundmod_colormap) { setIconColor(__instance.room,sb,1f); }
                    sb.draw(__instance.room.getMapImg(), iconX, iconY+15f*___scale, 64.0F, 64.0F, 128.0F, 128.0F,     ___scale * Settings.scale * primary, ___scale * Settings.scale * primary, 0.0F, 0, 0, 128, 128, false, false);
                }
                

                // Draw Been here circles
                if (__instance.taken || (AbstractDungeon.firstRoomChosen && __instance.equals(AbstractDungeon.getCurrMapNode()))) {
                    sb.setColor(AVAILABLE_COLOR);
                    sb.draw(ImageMaster.MAP_CIRCLE_5, __instance.x * SPACING_X + OFFSET_X - 96.0F + __instance.offsetX, __instance.y * Settings.MAP_DST_Y + OFFSET_Y + DungeonMapScreen.offsetY - 96.0F + __instance.offsetY, 96.0F, 96.0F, 192.0F, 192.0F, (___scale * 0.95F + 0.2F) * Settings.scale, (___scale * 0.95F + 0.2F) * Settings.scale, ___angle, 0, 0, 192, 192, false, false);
                } 
                if (__instance.hb != null)
                    __instance.hb.render(sb); 

                return SpireReturn.Return(null);
            }

            return SpireReturn.Continue();
        }
    }

    public static int otherPlayersOnNodeCount(int x, int y, int actNum) {
        int count = 0;
        for (RemotePlayer p : TogetherManager.players)
            if (p.x == x && p.y == y && p.act == actNum)
                count++;
        return count;
    }

    public static void setIconOutlineColor(AbstractRoom room, SpriteBatch sb) {
        String symbol = room.getMapSymbol();
        if(symbol != null)
            sb.setColor(new Color(
                TogetherManager.colormapPrefs.getFloat(symbol + "_red_outline", 0.0f),
                TogetherManager.colormapPrefs.getFloat(symbol + "_green_outline", 0.0f),
                TogetherManager.colormapPrefs.getFloat(symbol + "_blue_outline", 0.0f),
                TogetherManager.colormapPrefs.getFloat(symbol + "_alpha_outline", 0.0f)));
    }

    public static void setIconColor(AbstractRoom room, SpriteBatch sb, float alpha) {
        String symbol = room.getMapSymbol();
        if(symbol != null)
            sb.setColor(new Color(
                TogetherManager.colormapPrefs.getFloat(symbol + "_red_icon", 0.0f),
                TogetherManager.colormapPrefs.getFloat(symbol + "_green_icon", 0.0f),
                TogetherManager.colormapPrefs.getFloat(symbol + "_blue_icon", 0.0f), 
                alpha));
    }
}