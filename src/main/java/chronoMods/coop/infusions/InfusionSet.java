package chronoMods.coop.infusions;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;

import java.util.ArrayList;
import java.util.Collections;

public class InfusionSet {

    // To be effective, an Infusion set should have enough targeting options to hit most cards:
    //  ENEMY: ~25 per character 
    //  ALL_ENEMY: ~6 per character
    //  NONE: ~10 per character
    //  SELF: ~30 per character
    //  POWER: ~15 per character
    // Defect requires a non-targeting infusion as part of every set

    public ArrayList<Infusion> infusions = new ArrayList();
    public String setID;

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
        setID = stringID;
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
        i.indexID = infusions.size();
        i.setID = setID;
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
        ArrayList<Infusion> tmp = new ArrayList();
        tmp.addAll(infusions);

        Collections.shuffle(tmp);

        for (Infusion i : tmp)
            for (AbstractCard c : AbstractDungeon.player.masterDeck.group)
                if (i.canInfuse(c))
                    return i;

        return null;
    }
}
