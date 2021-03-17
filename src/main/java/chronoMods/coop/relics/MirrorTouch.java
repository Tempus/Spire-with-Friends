package chronoMods.coop.relics;

import com.evacipated.cardcrawl.modthespire.lib.*;

import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;

import com.megacrit.cardcrawl.localization.*;
import com.megacrit.cardcrawl.core.*;
import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.blights.*;
import com.megacrit.cardcrawl.helpers.*;
import com.megacrit.cardcrawl.dungeons.*;

import basemod.*;
import basemod.abstracts.*;
import basemod.interfaces.*;

import java.util.*;

import chronoMods.*;
import chronoMods.steam.*;
import chronoMods.coop.*;
import chronoMods.ui.deathScreen.*;
import chronoMods.ui.hud.*;
import chronoMods.ui.lobby.*;
import chronoMods.ui.mainMenu.*;

public class MirrorTouch extends AbstractBlight {
    public static final String ID = "MirrorTouch";
  private static final BlightStrings blightStrings = CardCrawlGame.languagePack.getBlightString(ID);
  public static final String NAME = blightStrings.NAME;
  public static final String[] DESCRIPTIONS = blightStrings.DESCRIPTION;

    public MirrorTouch() {
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
        this.description = this.DESCRIPTIONS[0] + (int)(getCombinedHealth() * 1.5f) + this.DESCRIPTIONS[1];
    }
        
    @Override
    public void onEquip() {
        AbstractDungeon.player.maxHealth = (int)(getCombinedHealth() * 1.5f);
        AbstractDungeon.player.currentHealth = AbstractDungeon.player.maxHealth;
    }

    public int getCombinedHealth() {
        int i = 0;
        for (RemotePlayer r : TogetherManager.players) {
            i += r.maxHp;
        }

        return i / TogetherManager.players.size();
    }
}