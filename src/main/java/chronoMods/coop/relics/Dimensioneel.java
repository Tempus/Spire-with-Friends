package chronoMods.coop.relics;

import com.evacipated.cardcrawl.modthespire.lib.*;

import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.*;

import com.megacrit.cardcrawl.localization.*;
import com.megacrit.cardcrawl.core.*;
import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.blights.*;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.helpers.*;
import com.megacrit.cardcrawl.rooms.*;
import com.megacrit.cardcrawl.relics.*;

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

public class Dimensioneel extends AbstractBlight {
    public static final String ID = "Dimensioneel";
    private static final BlightStrings blightStrings = CardCrawlGame.languagePack.getBlightString(ID);
    public static final String NAME = blightStrings.NAME;
    public static final String[] DESCRIPTIONS = blightStrings.DESCRIPTION;

    public static String relicID;
    public static RemotePlayer sendPlayer;

    private boolean relicSelected = true;
    private RelicSelectScreen relicSelectScreen;
    private boolean screenOpen = false;

    public Dimensioneel() {
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

    // @Override
    // public void onEquip() {
    //     ArrayList<String> ids = new ArrayList();
    //     for (AbstractRelic r : AbstractDungeon.player.relics)
    //         if (r.tier != AbstractRelic.RelicTier.BOSS && r.tier != AbstractRelic.RelicTier.STARTER)
    //             ids.add(r.relicId);

    //     for (String rid : ids) {
    //         relicID = rid;
    //         sendPlayer = TogetherManager.players.get(AbstractDungeon.miscRng.random(0,TogetherManager.players.size()-1));
    //         NetworkHelper.sendData(NetworkHelper.dataType.SendRelic);
    //     }
    // }

    @Override
    public void updateDescription() {
        this.description = this.DESCRIPTIONS[0];
    }
}