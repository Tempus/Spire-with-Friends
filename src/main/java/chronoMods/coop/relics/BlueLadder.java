package chronoMods.coop.relics;

import com.evacipated.cardcrawl.modthespire.lib.*;

import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;

import com.megacrit.cardcrawl.localization.*;
import com.megacrit.cardcrawl.core.*;
import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.blights.*;
import com.megacrit.cardcrawl.map.*;
import com.megacrit.cardcrawl.helpers.*;
import com.megacrit.cardcrawl.relics.*;
import com.megacrit.cardcrawl.dungeons.*;

import basemod.*;
import basemod.abstracts.*;

import java.util.*;

import chronoMods.*;
import chronoMods.network.steam.*;
import chronoMods.network.*;
import chronoMods.coop.*;
import chronoMods.ui.deathScreen.*;
import chronoMods.ui.hud.*;
import chronoMods.ui.lobby.*;
import chronoMods.ui.mainMenu.*;

public class BlueLadder extends AbstractBlight {
    public static final String ID = "BlueLadder";
  private static final BlightStrings blightStrings = CardCrawlGame.languagePack.getBlightString(ID);
  public static final String NAME = blightStrings.NAME;
  public static final String[] DESCRIPTIONS = blightStrings.DESCRIPTION;

    // Old Blue Ladder, winged connections to nodes
    @SpirePatch(clz = MapRoomNode.class, method="wingedIsConnectedTo")
    public static class BlueLadderWings {
        public static boolean Postfix(boolean __result, MapRoomNode __instance, MapRoomNode node) {
            
            if (CoopEmptyRoom.LockedRoomField.locked.get(node.getRoom())) {
                return false;
            }

            if (AbstractDungeon.player.hasBlight("BlueLadder") && AbstractDungeon.player.getBlight("BlueLadder").counter > 0) {
            
                // Any nodes on the same plane, that we haven't visited
                if (__instance.y == node.y && !TogetherManager.getCurrentUser().hasNode(AbstractDungeon.actNum, node)) {
                    // but someone else has visited from our team
                    for (RemotePlayer p : TogetherManager.players) {
                        if (p.hasNode(AbstractDungeon.actNum, node))
                            return true;
                    }
                }
            }

            // Wing Boot Stacking goes here because too many Postfix spoils the pot
            for (MapEdge edge : __instance.getEdges())
                if (node.y == edge.dstY)
                    for (AbstractRelic r : AbstractDungeon.player.relics)
                        if (r.relicId.equals("WingedGreaves") && r.counter > 0)
                            return true; 

            return __result;
        }
    }

    // Wing Boots don't trigger on Blue Ladder proc
    @SpirePatch(clz = MapRoomNode.class, method="update")
    public static class BlueLadderWingBootAdjustments {
        @SpireInsertPatch(rloc = 293-219)
        public static void Insert(MapRoomNode __instance) {
            
            if (AbstractDungeon.player.hasBlight("BlueLadder") && AbstractDungeon.getCurrMapNode().y == __instance.y)
                if (AbstractDungeon.player.getBlight("BlueLadder").counter > 0)
                    (AbstractDungeon.player.getRelic("WingedGreaves")).counter++;
        }
    }


    // Blue Ladder Countdown
    // @SpirePatch(clz = MapRoomNode.class, method="update")
    // public static class BlueLadderCountdown {
    //     @SpireInsert(rloc = 301-219)
    //     public static void Insert(MapRoomNode __instance) {
            
    //         if (AbstractDungeon.player.hasBlight("BlueLadder") && AbstractDungeon.getCurrMapNode().y == __instance.y)
    //             if (AbstractDungeon.player.getBlight("BlueLadder").counter > 0)
    //                 (AbstractDungeon.player.getBlight("BlueLadder")).counter--;
    //     }
    // }


    // New Blue Ladder, Buried Paths as well as nodes. You can never revisit a node.

    // @SpirePatch(clz = MapRoomNode.class, method="isConnectedTo")
    // public static class NoRevisitNodes {
    //     public static boolean Postfix(boolean __result, MapRoomNode __instance, MapRoomNode node) {
            
    //         if (TogetherManager.getCurrentUser().hasNode(AbstractDungeon.actNum, node))
    //             return false;

    //         return __result;
    //     }
    // }


    public BlueLadder() {
        super(ID, NAME, "", "spear.png", true);
        this.blightID = ID;
        this.name = NAME;
        updateDescription();
        this.unique = true;
        this.img = ImageMaster.loadImage("chrono/images/blights/" + ID + ".png");
        this.outlineImg = ImageMaster.loadImage("chrono/images/blights/outline/" + ID + ".png");
        this.increment = 0;
        this.tips.clear();
        this.tips.add(new PowerTip(name, description));
    }

    @Override
    public void updateDescription() {
        this.description = this.DESCRIPTIONS[0] + (TogetherManager.players.size() * 2) + this.DESCRIPTIONS[1];
    }

    @Override
    public void onEquip() {
        if (isObtained) { return; }

        counter = TogetherManager.players.size() * 2;
        updateDescription();
    }

    // public static void addLadderEdges(MapRoomNode currentNode, int srcX, int srcY) {
    //     if (AbstractDungeon.player.hasBlight("BlueLadder")) {
        
    //         TogetherManager.log("Adding Paths for " + srcX + ", " + srcY);
    //         MapRoomNode leftNode = null, rightNode = null;
    //         if (srcX > 0)
    //             leftNode = AbstractDungeon.map.get(srcY).get(srcX-1);

    //         if (srcX < 6)
    //             rightNode = AbstractDungeon.map.get(srcY).get(srcX+1);

    //         // If there is a node directly left, we can jump there, else, no path
    //         if (leftNode != null && leftNode.getRoom() != null) {
    //             TogetherManager.log("New Path left to " + (srcX-1 )+ ", " + srcY);
    //             //Check if an edge is already present
    //             if (currentNode.getEdgeConnectedTo(leftNode) == null) {
    //                 TogetherManager.log("No pre-existing edge");
    //                 currentNode.addEdge(new MapEdge(srcX, srcY, currentNode.offsetX, currentNode.offsetY, srcX-1, srcY, leftNode.offsetX, leftNode.offsetY, false));
    //                 currentNode.addEdge(new MapEdge(srcX-1, srcY, leftNode.offsetX, leftNode.offsetY, srcX, srcY, currentNode.offsetX, currentNode.offsetY, false));
    //             } else {
    //                 TogetherManager.log("Pre-existing edge found");
    //             }
    //         }
            
    //         // If there is a node directly right, we can jump there, else, no path
    //         if (rightNode != null && rightNode.getRoom() != null) {
    //             TogetherManager.log("New Path right to " + (srcX+1) + ", " + srcY);
    //             //Check if an edge is already present
    //             if (currentNode.getEdgeConnectedTo(rightNode) == null) {
    //                 TogetherManager.log("No pre-existing edge");
    //                 currentNode.addEdge(new MapEdge(srcX, srcY, currentNode.offsetX, currentNode.offsetY, srcX+1, srcY, rightNode.offsetX, rightNode.offsetY, false));
    //                 currentNode.addEdge(new MapEdge(srcX+1, srcY, rightNode.offsetX, rightNode.offsetY, srcX, srcY, currentNode.offsetX, currentNode.offsetY, false));
    //             } else {
    //                 TogetherManager.log("Pre-existing edge found");
    //             }
    //         }

    //     }
    // }
}