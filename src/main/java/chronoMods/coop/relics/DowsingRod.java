package chronoMods.coop.relics;

import com.evacipated.cardcrawl.modthespire.lib.*;

import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;

import com.megacrit.cardcrawl.localization.*;
import com.megacrit.cardcrawl.core.*;
import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.blights.*;
import com.megacrit.cardcrawl.rooms.*;
import com.megacrit.cardcrawl.helpers.*;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.saveAndContinue.*;
import com.megacrit.cardcrawl.map.*;

import basemod.*;
import basemod.abstracts.*;
import basemod.interfaces.*;

import java.util.*;

import chronoMods.*;
import chronoMods.network.steam.*;
import chronoMods.network.*;
import chronoMods.coop.*;
import chronoMods.ui.deathScreen.*;
import chronoMods.ui.hud.*;
import chronoMods.ui.lobby.*;
import chronoMods.ui.mainMenu.*;

public class DowsingRod extends AbstractBlight {
    public static final String ID = "DowsingRod";
    private static final BlightStrings blightStrings = CardCrawlGame.languagePack.getBlightString(ID);
    public static final String NAME = blightStrings.NAME;
    public static final String[] DESCRIPTIONS = blightStrings.DESCRIPTION;

    public DowsingRod() {
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
        this.description = this.DESCRIPTIONS[0];
    }


    public static void multiStackRooms(MapRoomNode node, AbstractRoom room) {
        int pathCount = node.getEdges().size() + CoopMultiRoom.getParentNodeCount(node);
        TogetherManager.log ("Paths from: " + node.x + ", " + node.y + " - " + pathCount);

        if (room instanceof MonsterRoomElite) {
            if (pathCount == 3) {
                CoopMultiRoom.secondRoomField.secondRoom.set(node, new TreasureRoom());   
            } else if (pathCount == 4) {
                CoopMultiRoom.secondRoomField.secondRoom.set(node, new TreasureRoom());   
            } else if (pathCount == 5) {
                CoopMultiRoom.secondRoomField.secondRoom.set(node, new TreasureRoom());   
                CoopMultiRoom.thirdRoomField.thirdRoom.set(node, new TreasureRoom());            
            } else if (pathCount == 6) {
                CoopMultiRoom.secondRoomField.secondRoom.set(node, new TreasureRoom());   
                CoopMultiRoom.thirdRoomField.thirdRoom.set(node, new TreasureRoom());            
            }
        }

        else if (room instanceof MonsterRoom) {
            if (AbstractDungeon.mapRng.random(0, 8) == 0 && CoopMultiRoom.IsNotAdjacentToCourier(node)) {
                node.setRoom(new CoopCourierRoom());
                if (pathCount == 2) {
                    CoopMultiRoom.secondRoomField.secondRoom.set(node, new ShopRoom());   
                } else if  (pathCount == 3) {
                    CoopMultiRoom.secondRoomField.secondRoom.set(node, new ShopRoom());   
                } else if (pathCount == 4) {
                    CoopMultiRoom.secondRoomField.secondRoom.set(node, new TreasureRoom());   
                } else if (pathCount == 5) {
                    CoopMultiRoom.secondRoomField.secondRoom.set(node, new ShopRoom());   
                    CoopMultiRoom.thirdRoomField.thirdRoom.set(node, new TreasureRoom());            
                } else if (pathCount == 6) {
                    CoopMultiRoom.secondRoomField.secondRoom.set(node, new TreasureRoom());   
                    CoopMultiRoom.thirdRoomField.thirdRoom.set(node, new TreasureRoom());            
                }
            } else {
                if (pathCount == 3) {
                    CoopMultiRoom.secondRoomField.secondRoom.set(node, new EventRoom());   
                } else if (pathCount == 4) {
                    CoopMultiRoom.secondRoomField.secondRoom.set(node, new EventRoom());   
                    CoopMultiRoom.thirdRoomField.thirdRoom.set(node, new RestRoom());   
                } else if (pathCount == 5) {
                    CoopMultiRoom.secondRoomField.secondRoom.set(node, new RestRoom());   
                    CoopMultiRoom.thirdRoomField.thirdRoom.set(node, new MonsterRoomElite());            
                } else if (pathCount == 6) {
                    CoopMultiRoom.secondRoomField.secondRoom.set(node, new MonsterRoomElite());   
                    CoopMultiRoom.thirdRoomField.thirdRoom.set(node, new MonsterRoomElite());            
                }
            }
        }

        else if (room instanceof RestRoom) {
            if (pathCount == 2) {
                CoopMultiRoom.secondRoomField.secondRoom.set(node, new RestRoom());   
            } else if  (pathCount == 3) {
                CoopMultiRoom.secondRoomField.secondRoom.set(node, new RestRoom());   
                CoopMultiRoom.thirdRoomField.thirdRoom.set(node, new RestRoom());            
            } else if (pathCount == 4) {
                CoopMultiRoom.secondRoomField.secondRoom.set(node, new RestRoom());   
                CoopMultiRoom.thirdRoomField.thirdRoom.set(node, new RestRoom());            
            } else if (pathCount == 5) {
                CoopMultiRoom.secondRoomField.secondRoom.set(node, new RestRoom());   
                CoopMultiRoom.thirdRoomField.thirdRoom.set(node, new RestRoom());            
            } else if (pathCount == 6) {
                CoopMultiRoom.secondRoomField.secondRoom.set(node, new RestRoom());   
                CoopMultiRoom.thirdRoomField.thirdRoom.set(node, new RestRoom());   
            }
        }

        else if (room instanceof TreasureRoom) {
            if (pathCount == 2) {
                CoopMultiRoom.secondRoomField.secondRoom.set(node, new TreasureRoom());   
            } else if  (pathCount == 3) {
                CoopMultiRoom.secondRoomField.secondRoom.set(node, new TreasureRoom());   
            } else if (pathCount == 4) {
                CoopMultiRoom.secondRoomField.secondRoom.set(node, new TreasureRoom());   
                CoopMultiRoom.thirdRoomField.thirdRoom.set(node, new TreasureRoom());            
            } else if (pathCount == 5) {
                CoopMultiRoom.secondRoomField.secondRoom.set(node, new TreasureRoom());   
                CoopMultiRoom.thirdRoomField.thirdRoom.set(node, new TreasureRoom());            
            } else if (pathCount == 6) {
                CoopMultiRoom.secondRoomField.secondRoom.set(node, new TreasureRoom());   
                CoopMultiRoom.thirdRoomField.thirdRoom.set(node, new TreasureRoom());            
            }
        }

        else if (room instanceof ShopRoom) {
            if (pathCount == 2) {
                CoopMultiRoom.secondRoomField.secondRoom.set(node, new EventRoom());   
            } else if  (pathCount == 3) {
                CoopMultiRoom.secondRoomField.secondRoom.set(node, new CoopCourierRoom());   
            } else if (pathCount == 4) {
                CoopMultiRoom.secondRoomField.secondRoom.set(node, new TreasureRoom());   
            } else if (pathCount == 5) {
                CoopMultiRoom.secondRoomField.secondRoom.set(node, new CoopCourierRoom());   
                CoopMultiRoom.thirdRoomField.thirdRoom.set(node, new TreasureRoom());            
            } else if (pathCount == 6) {
                CoopMultiRoom.secondRoomField.secondRoom.set(node, new TreasureRoom());   
                CoopMultiRoom.thirdRoomField.thirdRoom.set(node, new TreasureRoom());            
            }
        }

        else if (room instanceof EventRoom) {
            if (pathCount == 2) {
                CoopMultiRoom.secondRoomField.secondRoom.set(node, new EventRoom());   
            } else if  (pathCount == 3) {
                CoopMultiRoom.secondRoomField.secondRoom.set(node, new EventRoom());   
            } else if (pathCount == 4) {
                CoopMultiRoom.secondRoomField.secondRoom.set(node, new EventRoom());   
                CoopMultiRoom.thirdRoomField.thirdRoom.set(node, new RestRoom());   
            } else if (pathCount == 5) {
                CoopMultiRoom.secondRoomField.secondRoom.set(node, new RestRoom());   
                CoopMultiRoom.thirdRoomField.thirdRoom.set(node, new ShopRoom());            
            } else if (pathCount == 6) {
                CoopMultiRoom.secondRoomField.secondRoom.set(node, new RestRoom());   
                CoopMultiRoom.thirdRoomField.thirdRoom.set(node, new TreasureRoom());            
            }
        }
    }
}