package chronoMods.bingo;

import com.evacipated.cardcrawl.modthespire.lib.*;
import basemod.interfaces.*;
import basemod.*;

import com.megacrit.cardcrawl.actions.*;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.unique.*;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.*;
import com.megacrit.cardcrawl.potions.*;
import com.megacrit.cardcrawl.map.*;
import com.megacrit.cardcrawl.neow.*;
import com.megacrit.cardcrawl.rewards.*;
import com.megacrit.cardcrawl.relics.*;
import com.megacrit.cardcrawl.rooms.*;
import com.megacrit.cardcrawl.screens.select.*;
import com.megacrit.cardcrawl.screens.*;
import com.megacrit.cardcrawl.helpers.*;
import com.megacrit.cardcrawl.shop.*;
import com.megacrit.cardcrawl.unlock.*;
import com.megacrit.cardcrawl.monsters.beyond.*;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.ui.campfire.*;
import com.megacrit.cardcrawl.ui.panels.*;
import com.megacrit.cardcrawl.vfx.*;
import com.megacrit.cardcrawl.vfx.cardManip.*;
import com.megacrit.cardcrawl.screens.stats.*;

import chronoMods.*;
import chronoMods.network.*;
import chronoMods.network.steam.*;

import java.util.*;
import java.util.stream.*;

public class SendBingoPatches implements StartActSubscriber {

    static public int lastBingo;

    public static final String[] EasyBingo = CardCrawlGame.languagePack.getUIString("EasyBingo").TEXT;
    public static final String[] MedBingo = CardCrawlGame.languagePack.getUIString("MedBingo").TEXT;
    public static final String[] HardBingo = CardCrawlGame.languagePack.getUIString("HardBingo").TEXT;

    public static void Bingo(int bingo) {
        List<String> allBingo = Stream.of(EasyBingo, MedBingo, HardBingo).flatMap(Stream::of).collect(Collectors.toList());
        TogetherManager.log("Triggered Bingo goal: " + allBingo.get(bingo));

        if (!Caller.isMarked(bingo)) {
            lastBingo = bingo;
            NetworkHelper.sendData(NetworkHelper.dataType.Bingo);
        }
    }

    public void receiveStartAct() {
        if (TogetherManager.gameMode != TogetherManager.mode.Bingo) { return; }

        // Need to receive Bingo Rules from Host
        Caller.makeBingoCard(1,3,1);

        NetworkHelper.sendData(NetworkHelper.dataType.Bingo);
    }


    // Achievement Unlock Bingos
    @SpirePatch(clz = VictoryScreen.class, method=SpirePatch.CONSTRUCTOR)
    public static class bingoBeatHeart {
        public static void Postfix(VictoryScreen __instance) {
            if (TogetherManager.gameMode != TogetherManager.mode.Bingo) { return; }

            Bingo(29);

            switch (AbstractDungeon.player.chosenClass) {
              case IRONCLAD:
                Bingo(50);
                break;
              case THE_SILENT:
                Bingo(51);
                break;
              case DEFECT:
                Bingo(52);
                break;
              case WATCHER:
                Bingo(53);
                break;
            } 

            if (AbstractDungeon.player.masterDeck.pauperCheck())
                Bingo(54);
        
            if (AbstractDungeon.player.maxHealth == AbstractDungeon.player.currentHealth)
                Bingo(74);

            // These happen if you beat the heart OR Act 3, as long as you win
            if (CardCrawlGame.playtime <= 1800.0F) 
                Bingo(65);
            if (AbstractDungeon.player.masterDeck.size() <= 5)
                Bingo(48); 
            if (AbstractDungeon.player.masterDeck.size() >= 35)
                Bingo(49); 
            if (AbstractDungeon.player.masterDeck.size() >= 50)
                Bingo(66); 
            if (AbstractDungeon.player.relics.size() == 1)
                Bingo(64);
            if (AbstractDungeon.player.masterDeck.cursedCheck())
                Bingo(68);
            if (CardCrawlGame.metricData.campfire_rested == 0)
                Bingo(55);
            if (CardCrawlGame.metricData.campfire_upgraded == 0)
                Bingo(71);
            if (Collections.frequency(CardCrawlGame.metricData.path_taken, "E") <= 0)
                Bingo(72);
        }
    }

    @SpirePatch(clz = AbstractMonster.class, method="onFinalBossVictoryLogic")
    public static class bingoBeatAct3 {
        public static void Postfix(AbstractMonster __instance) {
            if (TogetherManager.gameMode != TogetherManager.mode.Bingo) { return; }

            if (AbstractDungeon.ascensionLevel < 20 || AbstractDungeon.bossList.size() != 2) {

                Bingo(0);

                switch (AbstractDungeon.player.chosenClass) {
                  case IRONCLAD:
                    Bingo(25);
                    break;
                  case THE_SILENT:
                    Bingo(26);
                    break;
                  case DEFECT:
                    Bingo(27);
                    break;
                  case WATCHER:
                    Bingo(28);
                    break;
                } 

                // These happen if you beat the heart OR Act 3, as long as you win
                if (CardCrawlGame.playtime <= 1800.0F) 
                    Bingo(65);
                if (AbstractDungeon.player.masterDeck.size() <= 5)
                    Bingo(48); 
                if (AbstractDungeon.player.masterDeck.size() >= 35)
                    Bingo(49); 
                if (AbstractDungeon.player.masterDeck.size() >= 50)
                    Bingo(66); 
                if (AbstractDungeon.player.relics.size() == 1)
                    Bingo(64);
                if (AbstractDungeon.player.masterDeck.cursedCheck())
                    Bingo(68);
                if (CardCrawlGame.metricData.campfire_rested == 0)
                    Bingo(55);
                if (CardCrawlGame.metricData.campfire_upgraded == 0)
                    Bingo(71);
                if (Collections.frequency(CardCrawlGame.metricData.path_taken, "E") <= 0)
                    Bingo(72);

                boolean noDamage = true;
                for (HashMap<String, Integer> combat : CardCrawlGame.metricData.damage_taken) {
                    if (combat.get("damage") > 0 && combat.get("floor") > (AbstractDungeon.actNum -1) * 16 && AbstractDungeon.actNum != 4)
                        noDamage = false;
                }
                if (noDamage)
                    Bingo(73);
            }
        }
    }

    @SpirePatch(clz = ObtainKeyEffect.class, method=SpirePatch.CONSTRUCTOR)
    public static class bingoKeys {
        public static void Postfix(ObtainKeyEffect __instance, ObtainKeyEffect.KeyColor keyColor) {
            if (TogetherManager.gameMode != TogetherManager.mode.Bingo) { return; }

            switch (keyColor) {
              case RED:
                Bingo(1);
                break;
              case GREEN:
                Bingo(3);
                break;
              case BLUE:
                Bingo(2);
                break;
            }

        }
    }

    @SpirePatch(clz = Transient.class, method="die")
    public static class bingoTransient {
        public static void Postfix(Transient __instance) {
            if (TogetherManager.gameMode != TogetherManager.mode.Bingo) { return; }

            Bingo(47);
        }
    }

    @SpirePatch(clz = FocusPower.class, method="stackPower")
    public static class bingoFocus {
        public static void Postfix(FocusPower __instance, int stackAmount) {
            if (TogetherManager.gameMode != TogetherManager.mode.Bingo) { return; }

            if (__instance.amount >= 25)
                Bingo(46);
        }
    }

    @SpirePatch(clz = GameActionManager.class, method="getNextAction")
    public static class bingoCardPlays {
        public static void Postfix(GameActionManager __instance) {
            if (TogetherManager.gameMode != TogetherManager.mode.Bingo) { return; }

            if (__instance.cardsPlayedThisTurn.size() == 25)
                Bingo(44);

            if (__instance.cardsPlayedThisTurn.size() == 12)
                Bingo(21);

            int shivCount = 0;
            for (AbstractCard i : __instance.cardsPlayedThisTurn) {
                if (i instanceof com.megacrit.cardcrawl.cards.tempCards.Shiv) {
                    shivCount++;
                    if (shivCount == 10) {
                        Bingo(42);
                        break;
                    } 
                }
            } 
        }
    }

    @SpirePatch(clz = AbstractRoom.class, method="endBattle")
    public static class bingoEndBattle {
        public static void Prefix(AbstractRoom __instance) {
            if (TogetherManager.gameMode != TogetherManager.mode.Bingo) { return; }

            if (AbstractDungeon.player.currentHealth == 1)
                Bingo(31);

            int attackCount = 0;
            int skillCount = 0;
            for (AbstractCard c : AbstractDungeon.actionManager.cardsPlayedThisCombat) {
              if (c.type == AbstractCard.CardType.ATTACK) {
                attackCount++;
                break;
              } 
              if (c.type == AbstractCard.CardType.SKILL)
                skillCount++; 
            } 

            if (attackCount == 0)
                Bingo(30);

            if (skillCount == 0)
                Bingo(5);
        }
    }

    @SpirePatch(clz = AbstractMonster.class, method="onBossVictoryLogic")
    public static class bingoBossVictory {
        public static void Postfix(AbstractMonster __instance) {
            if (TogetherManager.gameMode != TogetherManager.mode.Bingo) { return; }

            if (GameActionManager.turn <= 1)
                Bingo(45);
            if (GameActionManager.damageReceivedThisCombat - GameActionManager.hpLossThisCombat <= 0) 
                Bingo(22);

            boolean noDamage = true;
            for (HashMap<String, Integer> combat : CardCrawlGame.metricData.damage_taken) {
                if (combat.get("damage") > 0 && combat.get("floor") > (AbstractDungeon.actNum -1) * 16 && AbstractDungeon.actNum != 4)
                    noDamage = false;
            }
            if (noDamage)
                Bingo(73);

            for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
                if (c.rarity != AbstractCard.CardRarity.BASIC)
                    if (c.color != AbstractCard.CardColor.COLORLESS && c.color != AbstractCard.CardColor.CURSE)
                        return;
            }
            Bingo(70);
        }
    }

    @SpirePatch(clz = PoisonLoseHpAction.class, method="update")
    public static class bingoPoisonKill {
        public static void Postfix(PoisonLoseHpAction __instance) {
            if (TogetherManager.gameMode != TogetherManager.mode.Bingo) { return; }

            if (AbstractPlayer.poisonKillCount == 3)
                Bingo(20);
        }
    }

    @SpirePatch(clz = EnergyPanel.class, method="addEnergy")
    public static class bingoEnergy {
        public static void Postfix(int e) {
            if (TogetherManager.gameMode != TogetherManager.mode.Bingo) { return; }

            if (EnergyPanel.totalCount >= 9)
                Bingo(38);
        }
    }

    @SpirePatch(clz = AbstractCreature.class, method="addBlock")
    public static class bingoBlock {
        public static void Postfix(AbstractCreature __instance, int blockAmount) {
            if (TogetherManager.gameMode != TogetherManager.mode.Bingo) { return; }

            if (__instance.currentBlock >= 99 && __instance.isPlayer)
                Bingo(40); 
            if (__instance.currentBlock == 999)
                Bingo(63);
        }
    }

    @SpirePatch(clz = PoisonPower.class, method="stackPower")
    public static class bingoPoisonStack {
        public static void Postfix(PoisonPower __instance, int stackAmount) {
            if (TogetherManager.gameMode != TogetherManager.mode.Bingo) { return; }

            if (__instance.amount >= 99)
                Bingo(41); 
        }
    }

    @SpirePatch(clz = StrengthPower.class, method="stackPower")
    public static class bingoStrengthStack {
        public static void Postfix(StrengthPower __instance, int stackAmount) {
            if (TogetherManager.gameMode != TogetherManager.mode.Bingo) { return; }

            if (__instance.amount >= 50)
                Bingo(39); 
        }
    }

    @SpirePatch(clz = AbstractDungeon.class, method="checkForPactAchievement")
    public static class bingoExhaust {
        public static void Postfix(AbstractDungeon __instance) {
            if (TogetherManager.gameMode != TogetherManager.mode.Bingo) { return; }

            if (AbstractDungeon.player != null)
              if (AbstractDungeon.player.exhaustPile.size() >= 20)
                Bingo(62); 
        }
    }

    @SpirePatch(clz = CardGroup.class, method="refreshHandLayout")
    public static class bingoSmolHand {
        public static void Postfix(CardGroup __instance) {
            if (TogetherManager.gameMode != TogetherManager.mode.Bingo) { return; }

            if (AbstractDungeon.player.hand.size() + AbstractDungeon.player.drawPile.size() + AbstractDungeon.player.discardPile.size() <= 3 && 
                (AbstractDungeon.getCurrRoom()).phase == AbstractRoom.RoomPhase.COMBAT && (AbstractDungeon.getCurrRoom()).monsters != null && 
               !(AbstractDungeon.getCurrRoom()).monsters.areMonstersBasicallyDead() && AbstractDungeon.floorNum > 3)
                    Bingo(61); 

            if (AbstractDungeon.player.hand.size() == 10)
                Bingo(15);
        }
    }

    @SpirePatch(clz = UpgradeShineEffect.class, method="clank")
    public static class bingoUpgrade {
        public static void Postfix(UpgradeShineEffect __instance, float x, float y) {
            if (TogetherManager.gameMode != TogetherManager.mode.Bingo) { return; }

            if (!AbstractDungeon.player.masterDeck.hasUpgradableCards())
                Bingo(43); 
        }
    }

    @SpirePatch(clz = RestOption.class, method="useOption")
    public static class bingoRest {
        public static void Postfix(RestOption __instance) {
            if (TogetherManager.gameMode != TogetherManager.mode.Bingo) { return; }

            if (AbstractDungeon.player.currentHealth == AbstractDungeon.player.maxHealth)
                Bingo(4); 
        }
    }

    @SpirePatch(clz = CardGroup.class, method="removeCard", paramtypez = {AbstractCard.class})
    public static class bingoRemoval {
        public static void Postfix(CardGroup __instance, AbstractCard c) {
            if (TogetherManager.gameMode != TogetherManager.mode.Bingo) { return; }

            if (__instance.type != CardGroup.CardGroupType.MASTER_DECK) { return; }

            if (c.rarity == AbstractCard.CardRarity.RARE)
                Bingo(11); 

            boolean nostrikes = true;
            boolean nodefends = true;
            boolean nostarter = true;

            for (AbstractCard card : AbstractDungeon.player.masterDeck.group) {
                if (card.tags.contains(AbstractCard.CardTags.STARTER_STRIKE))
                    nostrikes = false;

                if (card.tags.contains(AbstractCard.CardTags.STARTER_DEFEND))
                    nodefends = false;

                if (card.rarity == AbstractCard.CardRarity.BASIC)
                    nostarter = false;
            }

            if (nostrikes)
                Bingo(13); 

            if (nostrikes && nodefends)
                Bingo(35); 

            if (nostarter)
                Bingo(57); 
        }
    }

    @SpirePatch(clz = PandorasBox.class, method="onEquip")
    public static class bingoPandorasBox {
        public static void Postfix(PandorasBox __instance) {
            if (TogetherManager.gameMode != TogetherManager.mode.Bingo) { return; }

            boolean nostrikes = true;
            boolean nodefends = true;
            boolean nostarter = true;

            for (AbstractCard card : AbstractDungeon.player.masterDeck.group) {
                if (card.tags.contains(AbstractCard.CardTags.STARTER_STRIKE))
                    nostrikes = false;

                if (card.tags.contains(AbstractCard.CardTags.STARTER_DEFEND))
                    nodefends = false;

                if (card.rarity == AbstractCard.CardRarity.BASIC)
                    nostarter = false;
            }

            if (nostrikes)
                Bingo(13); 

            if (nostrikes && nodefends)
                Bingo(35); 

            if (nostarter)
                Bingo(57); 
        }
    }

    @SpirePatch(clz = AbstractPlayer.class, method="gainGold")
    public static class bingoGold {
        public static void Postfix(AbstractPlayer __instance, int amount) {
            if (TogetherManager.gameMode != TogetherManager.mode.Bingo) { return; }

            if (AbstractDungeon.player.gold >= 300)
                Bingo(14); 

            if (AbstractDungeon.player.gold >= 1200)
                Bingo(36); 

            if (AbstractDungeon.player.gold >= 1500)
                Bingo(58); 
        }
    }

    @SpirePatch(clz = AbstractRelic.class, method="obtain")
    public static class bingoGetRelic {
        public static void Postfix(AbstractRelic __instance) {
            if (TogetherManager.gameMode != TogetherManager.mode.Bingo) { return; }

            if (AbstractDungeon.player.relics.size() >= 25)
                Bingo(24); 

            if (__instance.relicId.equals("Spirit Poop"))
                Bingo(69); 
        }
    }

    @SpirePatch(clz = ShopScreen.class, method="update")
    public static class bingoShop {
        public static void Postfix(ShopScreen __instance) {
            if (TogetherManager.gameMode != TogetherManager.mode.Bingo) { return; }

            if (((ArrayList<StoreRelic>)ReflectionHacks.getPrivate(__instance, ShopScreen.class, "relics")).size() == 0)
                if (!Caller.isMarked(34))
                    Bingo(34);

            if (((ArrayList<StorePotion>)ReflectionHacks.getPrivate(__instance, ShopScreen.class, "potions")).size() == 0)
                if (!Caller.isMarked(12))
                    Bingo(12); 

            if (__instance.coloredCards.size() == 0 && __instance.colorlessCards.size() == 0)
                if (!Caller.isMarked(56))
                    Bingo(56); 
        }
    }

    @SpirePatch(clz = BossRelicSelectScreen.class, method="noPick")
    public static class bingoBossRelicSkip {
        public static void Postfix(BossRelicSelectScreen __instance) {
            if (TogetherManager.gameMode != TogetherManager.mode.Bingo) { return; }

            Bingo(32); 
        }
    }

    @SpirePatch(clz = MonsterRoomBoss.class, method="onPlayerEntry")
    public static class bingoEnterBoss {
        public static void Postfix(MonsterRoomBoss __instance) {
            if (TogetherManager.gameMode != TogetherManager.mode.Bingo) { return; }


            boolean lefty = true;
            boolean righty = true;

            int Elites = 0;
            int Events = 0;

            for (ArrayList<MapRoomNode> row : AbstractDungeon.map) {
                boolean leftmost = true;
                int lastIndex = 6;
                for (int r = 0; r < row.size(); r++) {
                    if (row.get(r).hasEdges())
                        lastIndex = r;
                }

                for (MapRoomNode node : row) {

                    if (node.hasEdges()) {

                        if (node.taken) {
                            if (node.room.getMapSymbol().equals("E"))
                                Elites++;
                            if (node.room.getMapSymbol().equals("?"))
                                Events++;
                            if (!leftmost)
                                lefty = false;
                            if (row.indexOf(node) != lastIndex)
                                righty = false;
                        }

                        leftmost = false;
                    }
                }
            }

            if (lefty)
                Bingo(8);
            if (righty)
                Bingo(9);
            if (Elites == 0)
                Bingo(18);
            if (Elites >= 5)
                Bingo(60);
            if (Events == 0)
                Bingo(10);
        }
    }

    @SpirePatch(clz = AbstractDungeon.class, method="nextRoomTransition", paramtypez = {SaveFile.class})
    public static class bingoLeaveRoom {
        public static void Prefix(AbstractDungeon __instance, SaveFile saveFile) {
            if (TogetherManager.gameMode != TogetherManager.mode.Bingo) { return; }

            if (AbstractDungeon.currMapNode.room instanceof MonsterRoomElite) {
                TogetherManager.log("Left an Elite Room");

                for (RewardItem r : AbstractDungeon.currMapNode.room.rewards) {
                    TogetherManager.log("There's still a reward " + r.type);
                    if (r.type == RewardItem.RewardType.RELIC && !r.isDone) {
                        TogetherManager.log("Found a relic");
                        Bingo(6);
                    }
                }
            }
        }
    }

    @SpirePatch(clz = DungeonTransitionScreen.class, method="setAreaName")
    public static class bingoNewAct {
        public static void Prefix(DungeonTransitionScreen __instance, String key) {
            if (TogetherManager.gameMode != TogetherManager.mode.Bingo || !CardCrawlGame.isInARun()) { return; }

            if (AbstractDungeon.player.masterDeck.pauperCheck() && key.equals("TheCity"))
                Bingo(7);
            if (AbstractDungeon.player.masterDeck.pauperCheck() && key.equals("TheBeyond"))
                Bingo(33);
        }
    }

    @SpirePatch(clz = CardHelper.class, method="obtain")
    public static class bingoGetCard {
        public static void Postfix(String key, AbstractCard.CardRarity rarity, AbstractCard.CardColor color) {
            if (TogetherManager.gameMode != TogetherManager.mode.Bingo) { return; }

            if (AbstractDungeon.player.masterDeck.fullSetCheck() >= 1)
                Bingo(23);

            // First cards you obtain are attacks, powers, skills
            CardGroup noBasics = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
            for (AbstractCard card : AbstractDungeon.player.masterDeck.group) {
              if (card.rarity != AbstractCard.CardRarity.BASIC)
                noBasics.addToBottom(card); 
            } 

            int attacks = noBasics.getAttacks().size();
            int skills = noBasics.getSkills().size();
            int powers = noBasics.getPowers().size();

            if (attacks >= 4 && skills == 0 && powers == 0)
                Bingo(16); 

            if (attacks == 0 && skills >= 3 && powers == 0)
                Bingo(37); 

            if (attacks == 0 && skills == 0 && powers >= 2)
                Bingo(59); 
        }
    }

    @SpirePatch(clz = EventRoom.class, method="onPlayerEntry")
    public static class bingoEventEnter {
        public static void Prefix(EventRoom __instance) {
            if (TogetherManager.gameMode != TogetherManager.mode.Bingo) { return; }

            if (AbstractDungeon.eventRng.counter > 14)
                Bingo(67);
        }
    }

    @SpirePatch(clz = AbstractCreature.class, method="addPower")
    public static class bingoAddPower {
        public static void Postfix(AbstractCreature _instance, AbstractPower powerToApply) {
            if (TogetherManager.gameMode != TogetherManager.mode.Bingo) { return; }

            if (_instance.isPlayer) {
                int buffCount = 0;
                for (AbstractPower p : _instance.powers) {
                  if (p.type == AbstractPower.PowerType.BUFF)
                    buffCount++; 
                } 
                if (buffCount >= 10)
                  Bingo(19);
            } 
        }
    }

    @SpirePatch(clz = ApplyPowerAction.class, method="update")
    public static class bingoAddPowerTwo {
        public static void Postfix(ApplyPowerAction _instance) {
            if (TogetherManager.gameMode != TogetherManager.mode.Bingo) { return; }

            if (_instance.target != null && _instance.target.isPlayer && _instance.isDone) {
                int buffCount = 0;
                for (AbstractPower p : _instance.target.powers) {
                  if (p.type == AbstractPower.PowerType.BUFF)
                    buffCount++; 
                } 
                if (buffCount >= 10)
                  Bingo(19);
            } 
        }
    }

    @SpirePatch(clz = TopPanel.class, method="destroyPotion")
    public static class bingoPotions {
        public static void Postfix(TopPanel __instance, int slot) {
            if (TogetherManager.gameMode != TogetherManager.mode.Bingo) { return; }

            if (Collections.frequency(CardCrawlGame.metricData.potions_floor_usage, Integer.valueOf(AbstractDungeon.floorNum)) >= AbstractDungeon.player.potionSlots)
                Bingo(17);
        }
    }
}