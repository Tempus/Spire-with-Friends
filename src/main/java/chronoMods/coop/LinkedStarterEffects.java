package chronoMods.coop;

import com.evacipated.cardcrawl.modthespire.lib.*;
import basemod.*;

import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.core.*;

import com.megacrit.cardcrawl.actions.*;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.unique.*;
import com.megacrit.cardcrawl.actions.defect.*;
import com.megacrit.cardcrawl.actions.watcher.*;
import com.megacrit.cardcrawl.actions.utility.*;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.powers.*;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.orbs.*;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;

import chronoMods.*;
import chronoMods.coop.*;

import java.util.*;

public class LinkedStarterEffects
{
    public static final String[] INFUSE = CardCrawlGame.languagePack.getUIString("CardInfusions").TEXT;

    public static void modifyCard(AbstractPlayer youPlayer, AbstractPlayer otherPlayer) {
        AbstractCard card;

        switch (youPlayer.chosenClass) {

            case IRONCLAD:
                switch (otherPlayer.chosenClass) {
                    case IRONCLAD:
                        LinkedCardUsePatch.modifyCard("Bash", () -> { 
                            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(LinkedCardUsePatch.lastMonster, AbstractDungeon.player, new VulnerablePower(LinkedCardUsePatch.lastMonster, 2, false), 2)); }, 
                            INFUSE[1]);
                        break;
                    case THE_SILENT:
                        LinkedCardUsePatch.modifyCard("Bash", () -> { 
                            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(LinkedCardUsePatch.lastMonster, AbstractDungeon.player, new WeakPower(LinkedCardUsePatch.lastMonster, 1, false), 1));
                            AbstractDungeon.actionManager.addToBottom(new GainBlockAction(AbstractDungeon.player, AbstractDungeon.player, 4));
                            AbstractDungeon.actionManager.addToBottom(new DiscardAction(AbstractDungeon.player, AbstractDungeon.player, 1, false)); }, 
                            INFUSE[4]+INFUSE[5]+INFUSE[6]);
                        break;
                    case DEFECT:
                        LinkedCardUsePatch.modifyCard("Bash", () -> { 
                            AbstractDungeon.actionManager.addToBottom(new ChannelAction(new Lightning()));
                            AbstractDungeon.actionManager.addToBottom(new AnimateOrbAction(1)); 
                            AbstractDungeon.actionManager.addToBottom(new EvokeOrbAction(1)); }, 
                            INFUSE[7]+INFUSE[9]);
                        break;
                    case WATCHER:
                        LinkedCardUsePatch.modifyCard("Bash", () -> { 
                            AbstractDungeon.actionManager.addToBottom(new ChangeStanceAction("Calm")); 
                            AbstractDungeon.actionManager.addToBottom(new ChangeStanceAction("Wrath")); }, 
                            INFUSE[10]+INFUSE[11]);
                        break;  
                }
                card = youPlayer.masterDeck.findCardById("Bash");
                LinkedCardUsePatch.UpdateDescriptions(card);
                AbstractDungeon.effectList.add(new ShowCardBrieflyEffect(card.makeStatEquivalentCopy(), Settings.WIDTH / 2f - 96.0F * Settings.scale, Settings.HEIGHT / 2f));
                break;

            case THE_SILENT:
                switch (otherPlayer.chosenClass) {
                    case IRONCLAD:
                        LinkedCardUsePatch.modifyCard("Survivor", () -> { 
                            AbstractDungeon.actionManager.addToBottom(new ArmamentsAction(false)); }, 
                            INFUSE[2]);
                        LinkedCardUsePatch.modifyCard("Neutralize", () -> { 
                            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(LinkedCardUsePatch.lastMonster, AbstractDungeon.player, new VulnerablePower(LinkedCardUsePatch.lastMonster, 1, false), 1)); }, 
                            INFUSE[0]);
                        break;
                    case THE_SILENT:
                        LinkedCardUsePatch.modifyCard("Survivor", () -> { 
                            AbstractDungeon.actionManager.addToBottom(new GainBlockAction(AbstractDungeon.player, AbstractDungeon.player, 4));
                            AbstractDungeon.actionManager.addToBottom(new DiscardAction(AbstractDungeon.player, AbstractDungeon.player, 1, false)); }, 
                            INFUSE[5]+INFUSE[6]);
                        LinkedCardUsePatch.modifyCard("Neutralize", () -> { 
                            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(LinkedCardUsePatch.lastMonster, AbstractDungeon.player, new WeakPower(LinkedCardUsePatch.lastMonster, 1, false), 1)); },
                            INFUSE[4]);
                        break;
                    case DEFECT:
                        LinkedCardUsePatch.modifyCard("Survivor", () -> { 
                            AbstractDungeon.actionManager.addToBottom(new AnimateOrbAction(1)); 
                            AbstractDungeon.actionManager.addToBottom(new EvokeOrbAction(1)); }, 
                            INFUSE[9]);
                        LinkedCardUsePatch.modifyCard("Neutralize", () -> { 
                            AbstractDungeon.actionManager.addToBottom(new ChannelAction(new Lightning())); },
                            INFUSE[7]);
                        break;
                    case WATCHER:
                        LinkedCardUsePatch.modifyCard("Survivor", () -> { 
                            AbstractDungeon.actionManager.addToBottom(new ChangeStanceAction("Calm")); }, 
                            INFUSE[11]);
                        LinkedCardUsePatch.modifyCard("Neutralize", () -> { 
                            AbstractDungeon.actionManager.addToBottom(new ChangeStanceAction("Wrath")); }, 
                            INFUSE[10]);
                        break;  
                }
                card = youPlayer.masterDeck.findCardById("Neutralize");
                LinkedCardUsePatch.UpdateDescriptions(card);
                 AbstractDungeon.effectList.add(new ShowCardBrieflyEffect(card.makeStatEquivalentCopy(),    Settings.WIDTH / 2f - 190.0F * Settings.scale, Settings.HEIGHT / 2f));
                card = youPlayer.masterDeck.findCardById("Survivor");
                LinkedCardUsePatch.UpdateDescriptions(card);
                 AbstractDungeon.effectList.add(new ShowCardBrieflyEffect(card.makeStatEquivalentCopy(),    Settings.WIDTH / 2f + 190.0F * Settings.scale, Settings.HEIGHT / 2f));
                break;

            case DEFECT:
                switch (otherPlayer.chosenClass) {
                    case IRONCLAD:
                        LinkedCardUsePatch.modifyCard("Dualcast", () -> { 
                            AbstractMonster m = AbstractDungeon.getMonsters().getRandomMonster(null, true, AbstractDungeon.cardRandomRng);
                            if (m != null)
                                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, AbstractDungeon.player, new VulnerablePower(LinkedCardUsePatch.lastMonster, 1, false), 1)); }, 
                            INFUSE[0]);
                        LinkedCardUsePatch.modifyCard("Zap", () -> { 
                            AbstractDungeon.actionManager.addToBottom(new ArmamentsAction(false)); }, 
                            INFUSE[2]);
                        break;
                    case THE_SILENT:
                        LinkedCardUsePatch.modifyCard("Dualcast", () -> { 
                            AbstractMonster m = AbstractDungeon.getMonsters().getRandomMonster(null, true, AbstractDungeon.cardRandomRng);
                            if (m != null)
                                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, AbstractDungeon.player, new WeakPower(LinkedCardUsePatch.lastMonster, 1, false), 1)); },
                            INFUSE[4]);
                        LinkedCardUsePatch.modifyCard("Zap", () -> { 
                            AbstractDungeon.actionManager.addToBottom(new GainBlockAction(AbstractDungeon.player, AbstractDungeon.player, 4));
                            AbstractDungeon.actionManager.addToBottom(new DiscardAction(AbstractDungeon.player, AbstractDungeon.player, 1, false)); }, 
                            INFUSE[5]+INFUSE[6]);
                        break;
                    case DEFECT:
                        LinkedCardUsePatch.modifyCard("Dualcast", () -> { 
                            AbstractDungeon.actionManager.addToBottom(new AnimateOrbAction(1)); 
                            AbstractDungeon.actionManager.addToBottom(new EvokeOrbAction(1)); }, 
                            INFUSE[9]);
                        LinkedCardUsePatch.modifyCard("Zap", () -> { 
                            AbstractDungeon.actionManager.addToBottom(new ChannelAction(new Lightning())); },
                            INFUSE[7]);
                        break;
                    case WATCHER:
                        LinkedCardUsePatch.modifyCard("Dualcast", () -> { 
                            AbstractDungeon.actionManager.addToBottom(new ChangeStanceAction("Calm")); }, 
                            INFUSE[11]);
                        LinkedCardUsePatch.modifyCard("Zap", () -> { 
                            AbstractDungeon.actionManager.addToBottom(new ChangeStanceAction("Wrath")); }, 
                            INFUSE[10]);
                        break;  
                }
                card = youPlayer.masterDeck.findCardById("Dualcast");
                LinkedCardUsePatch.UpdateDescriptions(card);
                 AbstractDungeon.effectList.add(new ShowCardBrieflyEffect(card.makeStatEquivalentCopy(),    Settings.WIDTH / 2f - 190.0F * Settings.scale, Settings.HEIGHT / 2f));
                card = youPlayer.masterDeck.findCardById("Zap");
                LinkedCardUsePatch.UpdateDescriptions(card);
                 AbstractDungeon.effectList.add(new ShowCardBrieflyEffect(card.makeStatEquivalentCopy(),    Settings.WIDTH / 2f + 190.0F * Settings.scale, Settings.HEIGHT / 2f));
                break;

            case WATCHER:
                switch (otherPlayer.chosenClass) {
                    case IRONCLAD:
                        LinkedCardUsePatch.modifyCard("Vigilance", () -> { 
                            AbstractDungeon.actionManager.addToBottom(new ArmamentsAction(false)); }, 
                            INFUSE[2]);
                        LinkedCardUsePatch.modifyCard("Eruption", () -> { 
                            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(LinkedCardUsePatch.lastMonster, AbstractDungeon.player, new VulnerablePower(LinkedCardUsePatch.lastMonster, 1, false), 1)); }, 
                            INFUSE[0]);
                        break;
                    case THE_SILENT:
                        LinkedCardUsePatch.modifyCard("Vigilance", () -> { 
                            AbstractDungeon.actionManager.addToBottom(new GainBlockAction(AbstractDungeon.player, AbstractDungeon.player, 4));
                            AbstractDungeon.actionManager.addToBottom(new DiscardAction(AbstractDungeon.player, AbstractDungeon.player, 1, false)); }, 
                            INFUSE[5]+INFUSE[6]);
                        LinkedCardUsePatch.modifyCard("Eruption", () -> { 
                            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(LinkedCardUsePatch.lastMonster, AbstractDungeon.player, new WeakPower(LinkedCardUsePatch.lastMonster, 1, false), 1)); },
                            INFUSE[4]);
                        break;
                    case DEFECT:
                        LinkedCardUsePatch.modifyCard("Vigilance", () -> { 
                            AbstractDungeon.actionManager.addToBottom(new ChannelAction(new Lightning())); },
                            INFUSE[7]);
                        LinkedCardUsePatch.modifyCard("Eruption", () -> { 
                            AbstractDungeon.actionManager.addToBottom(new AnimateOrbAction(1)); 
                            AbstractDungeon.actionManager.addToBottom(new EvokeOrbAction(1)); }, 
                            INFUSE[9]);
                        break;
                    case WATCHER:
                        LinkedCardUsePatch.modifyCard("Vigilance", () -> { 
                            AbstractDungeon.actionManager.addToBottom(new ChangeStanceAction("Wrath")); }, 
                            INFUSE[10]);
                        LinkedCardUsePatch.modifyCard("Eruption", () -> { 
                            AbstractDungeon.actionManager.addToBottom(new ChangeStanceAction("Calm")); }, 
                            INFUSE[11]);
                        break;  
                }
                card = youPlayer.masterDeck.findCardById("Vigilance");
                LinkedCardUsePatch.UpdateDescriptions(card);
                 AbstractDungeon.effectList.add(new ShowCardBrieflyEffect(card.makeStatEquivalentCopy(),    Settings.WIDTH / 2f - 190.0F * Settings.scale, Settings.HEIGHT / 2f));
                card = youPlayer.masterDeck.findCardById("Eruption");
                LinkedCardUsePatch.UpdateDescriptions(card);
                 AbstractDungeon.effectList.add(new ShowCardBrieflyEffect(card.makeStatEquivalentCopy(),    Settings.WIDTH / 2f + 190.0F * Settings.scale, Settings.HEIGHT / 2f));
               break;  
        }
    }
}