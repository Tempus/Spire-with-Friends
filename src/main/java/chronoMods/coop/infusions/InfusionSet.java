package chronoMods.coop.infusions;

import com.evacipated.cardcrawl.modthespire.lib.*;
import basemod.*;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;

import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.core.*;

import com.megacrit.cardcrawl.actions.*;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.unique.*;
import com.megacrit.cardcrawl.actions.defect.*;
import com.megacrit.cardcrawl.actions.watcher.*;
import com.megacrit.cardcrawl.actions.utility.*;
import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.cards.tempCards.*;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.powers.*;
import com.megacrit.cardcrawl.powers.watcher.*;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.orbs.*;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;
import com.megacrit.cardcrawl.vfx.*;
import com.megacrit.cardcrawl.helpers.*;

import chronoMods.*;
import chronoMods.coop.*;
import chronoMods.coop.infusions.*;

import java.util.*;

public class InfusionSet {

    // To be effective, an Infusion set should have enough targeting options to hit most cards:
    //  ENEMY: ~25 per character 
    //  ALL_ENEMY: ~6 per character
    //  NONE: ~10 per character
    //  SELF: ~30 per character
    //  POWER: ~15 per character
    // Defect requires a non-targeting infusion as part of every set

    public ArrayList<Infusion> infusions = new ArrayList();
    public String infID;

    public String name;
    public String longname;
    public String description;
    public Texture icon;

    public String[] actText;

    public Class particle;

    public InfusionSet(String stringID) {
        this(stringID, InfusionVFXBase.class);
    }

    public InfusionSet(String stringID, Class particle) {
        infID = stringID;
        this.particle = particle;

        String[] info = CardCrawlGame.languagePack.getUIString("Inf:" + stringID).TEXT;
        actText = CardCrawlGame.languagePack.getUIString("Inf:" + stringID).EXTRA_TEXT;

        this.name = info[0];
        this.longname = name+ CardCrawlGame.languagePack.getUIString("CardInfusions").TEXT[1];
        this.description = info[1];
        icon = ImageMaster.loadImage("chrono/images/infusions/" + stringID + ".png");
    }

    public void add(Infusion i) {
        i.icon = this.icon;
        i.particle = this.particle;
        infusions.add(i);
    }

    public Infusion getValidInfusion(AbstractCard c) {
        ArrayList<Infusion> tmp = new ArrayList();
        tmp.addAll(infusions);

        Collections.shuffle(tmp);

        for (Infusion i : tmp)
            if (i.canInfuse(c))
                return i;

        return null;
    }

    public Infusion getUnshuffledValidInfusion(AbstractCard c) {
        for (Infusion i : infusions) {
            if (i.canInfuse(c)) {
                return i;
            }
        }

        return null;
    }

    public Infusion getRandomInfusion() {
        return infusions.get(MathUtils.random(infusions.size()-1));
    }
}
