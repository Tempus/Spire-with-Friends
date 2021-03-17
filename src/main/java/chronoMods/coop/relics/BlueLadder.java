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
import com.megacrit.cardcrawl.dungeons.*;

import basemod.*;
import basemod.abstracts.*;

import java.util.*;

import chronoMods.*;
import chronoMods.steam.*;
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

    @SpirePatch(clz = MapRoomNode.class, method="wingedIsConnectedTo")
    public static class BlueLadderWings {
        public static boolean Postfix(boolean __result, MapRoomNode __instance, MapRoomNode node) {
            
            if (CoopEmptyRoom.LockedRoomField.locked.get(node.getRoom())) {
                return false;
            }

            if (AbstractDungeon.player.hasBlight("BlueLadder")) {
            
                // Any nodes on the same plane, that we haven't visited
                if (__instance.y == node.y && !TogetherManager.getCurrentUser().hasNode(AbstractDungeon.actNum, node)) {
                    // but someone else has visited from our team
                    for (RemotePlayer p : TogetherManager.players) {
                        if (p.hasNode(AbstractDungeon.actNum, node))
                            return true;
                    }
                }
            }

            return __result;
        }
    }

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
        this.description = this.DESCRIPTIONS[0];
    }
}