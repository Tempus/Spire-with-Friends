package chronoMods.coop.relics;

import chronoMods.TogetherManager;
import chronoMods.network.NetworkHelper;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.actions.unique.LoseEnergyAction;
import com.megacrit.cardcrawl.blights.AbstractBlight;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.BlightStrings;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

public class BrainFreeze extends AbstractBlight {
    public static final String ID = "BrainFreeze";
    private static final BlightStrings blightStrings = CardCrawlGame.languagePack.getBlightString(ID);
    public static final String NAME = blightStrings.NAME;
    public static final String[] DESCRIPTIONS = blightStrings.DESCRIPTION;

    public static int modEnergy = 0;

    public BrainFreeze() {
        super(ID, NAME, "", "spear.png", true);
        this.blightID = ID;
        this.name = NAME;
        updateDescription();
        this.unique = true;
        this.img = ImageMaster.loadImage("chrono/images/blights/" + ID + ".png");
        this.outlineImg = ImageMaster.loadImage("chrono/images/blights/outline/" + ID + ".png");
        this.increment = 0;
        this.counter = 0;
        this.tips.clear();
        this.tips.add(new PowerTip(name, description));
    }

    @Override
    public void updateDescription() {
        this.description = this.DESCRIPTIONS[0];
    }

    @Override
    public void atBattleStart() {
        AbstractDungeon.actionManager.addToBottom(new LoseEnergyAction(1));
        modEnergy = 1;
        NetworkHelper.sendData(NetworkHelper.dataType.ModifyBrainFreeze);

        flash();
    }

    // On Click
    @Override
    public void update() {
        super.update();
        

        if (InputHelper.justReleasedClickLeft && this.hb.hovered) {
            TogetherManager.log("Clicking Brain Freeze");

            if (AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT && this.counter > 0) {
                modEnergy = -1;
                NetworkHelper.sendData(NetworkHelper.dataType.ModifyBrainFreeze);
                
                flash();
                
                AbstractDungeon.actionManager.addToBottom(new GainEnergyAction(1));
                this.hb.clicked = false;
            }
        }
    }
}