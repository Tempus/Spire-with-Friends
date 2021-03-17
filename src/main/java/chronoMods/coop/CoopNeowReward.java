package chronoMods.coop;

import com.evacipated.cardcrawl.modthespire.lib.*;
import basemod.interfaces.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.rooms.*;
import com.megacrit.cardcrawl.neow.*;
import com.megacrit.cardcrawl.map.*;
import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.cards.red.*;
import com.megacrit.cardcrawl.cards.blue.*;
import com.megacrit.cardcrawl.cards.green.*;
import com.megacrit.cardcrawl.cards.purple.*;
import com.megacrit.cardcrawl.cards.status.*;
import com.megacrit.cardcrawl.cards.tempCards.*;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.shop.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.*;
import com.megacrit.cardcrawl.potions.*;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.rewards.chests.AbstractChest;
import com.megacrit.cardcrawl.vfx.ChestShineEffect;
import com.megacrit.cardcrawl.vfx.scene.SpookyChestEffect;

import chronoMods.*;
import chronoMods.coop.*;
import chronoMods.steam.*;
import chronoMods.ui.deathScreen.*;
import chronoMods.ui.hud.*;
import chronoMods.ui.lobby.*;
import chronoMods.ui.mainMenu.*;
import chronoMods.utilities.*;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.localization.CharacterStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.NeowsLament;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import com.megacrit.cardcrawl.vfx.UpgradeShineEffect;
import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;
import java.util.ArrayList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CoopNeowReward {
	public static class NeowRewardDef {
		public CoopNeowReward.NeowRewardType type;
		
		public String desc;
		
		public NeowRewardDef(CoopNeowReward.NeowRewardType type, String desc) {
			this.type = type;
			this.desc = desc;
		}
	}
			
	private static final Logger logger = LogManager.getLogger(NeowReward.class.getName());
	private static final CharacterStrings characterStrings = CardCrawlGame.languagePack.getCharacterString("Neow Reward");
	public static final String[] NAMES = characterStrings.NAMES;
	public static final String[] TEXT = characterStrings.TEXT;
	public static final String[] UNIQUE_REWARDS = characterStrings.UNIQUE_REWARDS;	

    public static final String[] REWARD = CardCrawlGame.languagePack.getUIString("NeowRewards").TEXT;

	public String optionLabel;
	public NeowRewardType type;
	public String chosenBy = "";

	private boolean activated;
	
	private int hp_bonus;
	private boolean cursed;
	
	private static final int GOLD_BONUS = 100;	
	private static final int LARGE_GOLD_BONUS = 250;
		
	public enum NeowRewardType {
		NONE, TEN_PERCENT_HP_LOSS, NO_GOLD, CURSE, PERCENT_DAMAGE,      LOSE_POTION_SLOT, LOSE_CLASS_BASIC, ADD_STRIKE_DEFEND, TRANSFORM_BANE, LOWER_MAX_HAND, FIRST_TREASURE_EMPTY, MORE_MONSTER_NODES, TWO_DAZES, TWO_SLIMED, THREE_SHIVS,
		THREE_CARDS, ONE_RANDOM_RARE_CARD, REMOVE_CARD, UPGRADE_CARD, RANDOM_COLORLESS, TRANSFORM_CARD, THREE_SMALL_POTIONS, RANDOM_COMMON_RELIC, TEN_PERCENT_HP_BONUS, HUNDRED_GOLD,   REMOVE_ASCENDERS_BANE, 
		RANDOM_COLORLESS_2, REMOVE_TWO, TRANSFORM_TWO_CARDS, ONE_RARE_RELIC, THREE_RARE_CARDS, TWO_FIFTY_GOLD, TWENTY_PERCENT_HP_BONUS, THREE_ENEMY_KILL,     UPGRADE_3_RANDOM, POTIONS_AND_SLOT, UPGRADE_CLASS_RELIC, RANDOM_SHOP_RELIC, RANDOM_CLASS_RELIC, TWO_RANDOM_UPGRADED_CARDS,
		BOSS_RELIC;
	}

	public CoopNeowReward(NeowRewardDef reward) {
		this.optionLabel = "";
		this.activated = false;
		this.cursed = false;
		this.optionLabel += reward.desc;
		this.type = reward.type;
	}
	
	public static CoopNeowReward getWeakReward() {
		ArrayList<NeowRewardDef> possibleRewards = new ArrayList<>();

		possibleRewards.add(new NeowRewardDef(NeowRewardType.THREE_CARDS, TEXT[0]));
		possibleRewards.add(new NeowRewardDef(NeowRewardType.ONE_RANDOM_RARE_CARD, TEXT[1]));
		possibleRewards.add(new NeowRewardDef(NeowRewardType.REMOVE_CARD, TEXT[2]));
		possibleRewards.add(new NeowRewardDef(NeowRewardType.UPGRADE_CARD, TEXT[3]));
		possibleRewards.add(new NeowRewardDef(NeowRewardType.TRANSFORM_CARD, TEXT[4]));
		possibleRewards.add(new NeowRewardDef(NeowRewardType.RANDOM_COLORLESS, TEXT[30]));
		possibleRewards.add(new NeowRewardDef(NeowRewardType.THREE_SMALL_POTIONS, TEXT[5]));
		possibleRewards.add(new NeowRewardDef(NeowRewardType.RANDOM_COMMON_RELIC, TEXT[6]));
		possibleRewards.add(new NeowRewardDef(NeowRewardType.TEN_PERCENT_HP_BONUS, TEXT[7] + (int)(AbstractDungeon.player.maxHealth * 0.1F) + " ]"));
		possibleRewards.add(new NeowRewardDef(NeowRewardType.HUNDRED_GOLD, TEXT[8] + "100" + TEXT[9]));
		if (AbstractDungeon.ascensionLevel >= 10)
			possibleRewards.add(new NeowRewardDef(NeowRewardType.REMOVE_ASCENDERS_BANE, REWARD[0]));

		NeowRewardDef reward = possibleRewards.get(NeowEvent.rng.random(0, possibleRewards.size() - 1));

		CoopNeowReward c = new CoopNeowReward(reward);
		return c;
	}

	public static ArrayList<CoopNeowReward> getRewards(int amount) {

		ArrayList<NeowRewardDef> possibleRewards = new ArrayList<>();

		possibleRewards.add(new NeowRewardDef(NeowRewardType.RANDOM_COLORLESS_2, "[ " + TEXT[31]));
		possibleRewards.add(new NeowRewardDef(NeowRewardType.ONE_RARE_RELIC, "[ " + TEXT[11]));
		possibleRewards.add(new NeowRewardDef(NeowRewardType.THREE_RARE_CARDS, "[ " + TEXT[12]));
		possibleRewards.add(new NeowRewardDef(NeowRewardType.TWO_FIFTY_GOLD, "[ " + TEXT[13] + "250" + TEXT[14])); 
		possibleRewards.add(new NeowRewardDef(NeowRewardType.TRANSFORM_TWO_CARDS, "[ " + TEXT[15]));
		possibleRewards.add(new NeowRewardDef(NeowRewardType.TWENTY_PERCENT_HP_BONUS, "[ " + TEXT[16] + (int)(AbstractDungeon.player.maxHealth * 0.2F) + " ]")); 
		possibleRewards.add(new NeowRewardDef(NeowRewardType.THREE_ENEMY_KILL, TEXT[28]));
		possibleRewards.add(new NeowRewardDef(NeowRewardType.REMOVE_TWO, REWARD[1]));

		possibleRewards.add(new NeowRewardDef(NeowRewardType.UPGRADE_3_RANDOM, REWARD[2]));
		possibleRewards.add(new NeowRewardDef(NeowRewardType.POTIONS_AND_SLOT, REWARD[3]));
		possibleRewards.add(new NeowRewardDef(NeowRewardType.UPGRADE_CLASS_RELIC, REWARD[4]));
		possibleRewards.add(new NeowRewardDef(NeowRewardType.RANDOM_SHOP_RELIC, REWARD[5]));
		possibleRewards.add(new NeowRewardDef(NeowRewardType.RANDOM_CLASS_RELIC, REWARD[6]));
		possibleRewards.add(new NeowRewardDef(NeowRewardType.TWO_RANDOM_UPGRADED_CARDS, REWARD[7]));

		int choice;
		ArrayList<CoopNeowReward> chosenRewards = new ArrayList<>();
		for (int i = 0; i < amount; i++) {
			choice = NeowEvent.rng.random(0, possibleRewards.size() - 1);
			NeowRewardDef reward = possibleRewards.get(choice);
			possibleRewards.remove(choice);

			chosenRewards.add(new CoopNeowReward(reward));
		}

		return chosenRewards;
	}

	public static CoopNeowReward getBossSwap() {
		return new CoopNeowReward(new NeowRewardDef(NeowRewardType.BOSS_RELIC, UNIQUE_REWARDS[0]));
	}

	public static ArrayList<CoopNeowReward> getPenalties(int amount) {
		ArrayList<NeowRewardDef> possibleRewards = new ArrayList<>();

		possibleRewards.add(new NeowRewardDef(NeowRewardType.TEN_PERCENT_HP_LOSS, TEXT[17] + (int)(AbstractDungeon.player.maxHealth * 0.1F) + TEXT[18] + " ]"));
		possibleRewards.add(new NeowRewardDef(NeowRewardType.NO_GOLD, TEXT[19] + " ]"));
		possibleRewards.add(new NeowRewardDef(NeowRewardType.CURSE, TEXT[20] + " ]"));
		possibleRewards.add(new NeowRewardDef(NeowRewardType.PERCENT_DAMAGE, TEXT[21] + (AbstractDungeon.player.currentHealth / 10 * 3) + TEXT[29] + " ]"));

		possibleRewards.add(new NeowRewardDef(NeowRewardType.LOSE_POTION_SLOT, REWARD[8]));
		possibleRewards.add(new NeowRewardDef(NeowRewardType.LOSE_CLASS_BASIC, REWARD[9]));
		possibleRewards.add(new NeowRewardDef(NeowRewardType.ADD_STRIKE_DEFEND, REWARD[10]));
		possibleRewards.add(new NeowRewardDef(NeowRewardType.LOWER_MAX_HAND, REWARD[11]));
		possibleRewards.add(new NeowRewardDef(NeowRewardType.FIRST_TREASURE_EMPTY, REWARD[12]));
		possibleRewards.add(new NeowRewardDef(NeowRewardType.MORE_MONSTER_NODES, REWARD[13]));
		possibleRewards.add(new NeowRewardDef(NeowRewardType.TWO_DAZES, REWARD[14]));
		possibleRewards.add(new NeowRewardDef(NeowRewardType.TWO_SLIMED, REWARD[15]));
		possibleRewards.add(new NeowRewardDef(NeowRewardType.THREE_SHIVS, REWARD[16]));
		if (AbstractDungeon.ascensionLevel >= 10)
			possibleRewards.add(new NeowRewardDef(NeowRewardType.TRANSFORM_BANE, REWARD[17]));

		// First X enemies have 6 more HP?
		
		int choice;
		ArrayList<CoopNeowReward> chosenRewards = new ArrayList<>();
		for (int i = 0; i < amount; i++) {
			choice = NeowEvent.rng.random(0, possibleRewards.size() - 1);
			NeowRewardDef reward = possibleRewards.get(choice);
			possibleRewards.remove(choice);

			chosenRewards.add(new CoopNeowReward(reward));
		}

		return chosenRewards;
	}

	public static CoopNeowReward getNoPenalty() {
		return new CoopNeowReward(new NeowRewardDef(NeowRewardType.NONE, REWARD[18]));
	}
		
	public void update() {
		if (this.activated) {
			if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
				AbstractCard c, c2, c3, t1, t2;
				switch (this.type) {
					case UPGRADE_CARD:
						c = AbstractDungeon.gridSelectScreen.selectedCards.get(0);
						c.upgrade();
						AbstractDungeon.topLevelEffects.add(new ShowCardBrieflyEffect(c.makeStatEquivalentCopy()));
						AbstractDungeon.topLevelEffects.add(new UpgradeShineEffect(Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
						break;
					case REMOVE_CARD:
						CardCrawlGame.sound.play("CARD_EXHAUST");
						AbstractDungeon.topLevelEffects.add(new PurgeCardEffect(AbstractDungeon.gridSelectScreen.selectedCards
									
									.get(0), (Settings.WIDTH / 2), (Settings.HEIGHT / 2)));
						AbstractDungeon.player.masterDeck.removeCard(AbstractDungeon.gridSelectScreen.selectedCards
								.get(0));
						break;
					case REMOVE_TWO:
						CardCrawlGame.sound.play("CARD_EXHAUST");
						c2 = AbstractDungeon.gridSelectScreen.selectedCards.get(0);
						c3 = AbstractDungeon.gridSelectScreen.selectedCards.get(1);
						AbstractDungeon.topLevelEffects.add(new PurgeCardEffect(c2, Settings.WIDTH / 2.0F - AbstractCard.IMG_WIDTH / 2.0F - 30.0F * Settings.scale, (Settings.HEIGHT / 2)));
						AbstractDungeon.topLevelEffects.add(new PurgeCardEffect(c3, Settings.WIDTH / 2.0F + AbstractCard.IMG_WIDTH / 2.0F + 30.0F * Settings.scale, Settings.HEIGHT / 2.0F));
						AbstractDungeon.player.masterDeck.removeCard(c2);
						AbstractDungeon.player.masterDeck.removeCard(c3);
						break;
					case TRANSFORM_CARD:
						AbstractDungeon.transformCard(AbstractDungeon.gridSelectScreen.selectedCards
								.get(0), false, NeowEvent.rng);
						AbstractDungeon.player.masterDeck.removeCard(AbstractDungeon.gridSelectScreen.selectedCards
								.get(0));
						AbstractDungeon.topLevelEffects.add(new ShowCardAndObtainEffect(
									
									AbstractDungeon.getTransformedCard(), Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
						break;
					case TRANSFORM_TWO_CARDS:
						t1 = AbstractDungeon.gridSelectScreen.selectedCards.get(0);
						t2 = AbstractDungeon.gridSelectScreen.selectedCards.get(1);
						AbstractDungeon.player.masterDeck.removeCard(t1);
						AbstractDungeon.player.masterDeck.removeCard(t2);
						AbstractDungeon.transformCard(t1, false, NeowEvent.rng);
						AbstractDungeon.topLevelEffects.add(new ShowCardAndObtainEffect(
									
									AbstractDungeon.getTransformedCard(), Settings.WIDTH / 2.0F - AbstractCard.IMG_WIDTH / 2.0F - 30.0F * Settings.scale, Settings.HEIGHT / 2.0F));
						AbstractDungeon.transformCard(t2, false, NeowEvent.rng);
						AbstractDungeon.topLevelEffects.add(new ShowCardAndObtainEffect(
									
									AbstractDungeon.getTransformedCard(), Settings.WIDTH / 2.0F + AbstractCard.IMG_WIDTH / 2.0F + 30.0F * Settings.scale, Settings.HEIGHT / 2.0F));
						break;
					default:
						TogetherManager.log("[ERROR] Missing Neow Reward Type: " + this.type.name());
						break;
				} 
				AbstractDungeon.gridSelectScreen.selectedCards.clear();
				AbstractDungeon.overlayMenu.cancelButton.hide();
				SaveHelper.saveIfAppropriate(SaveFile.SaveType.POST_NEOW);
				this.activated = false;
			} 
		} 
	}
	
	public void activate() {
		int i, remove, j;
		AbstractCard c;
		this.activated = true;
		switch (this.type) {
			// Bad things
			case CURSE:
				AbstractDungeon.topLevelEffects.add(new ShowCardAndObtainEffect(
					AbstractDungeon.getCardWithoutRng(AbstractCard.CardRarity.CURSE), Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
				break;
			case NO_GOLD:
				AbstractDungeon.player.loseGold(AbstractDungeon.player.gold);
				break;
			case TEN_PERCENT_HP_LOSS:
				AbstractDungeon.player.decreaseMaxHealth((int)(AbstractDungeon.player.maxHealth * 0.1F));
				break;
			case PERCENT_DAMAGE:
				AbstractDungeon.player.damage(new DamageInfo(null, AbstractDungeon.player.currentHealth / 10 * 3, DamageInfo.DamageType.HP_LOSS));
				break;
			case LOSE_POTION_SLOT:
				CardCrawlGame.sound.play("POTION_DROP_1");
				AbstractDungeon.player.potionSlots--;
				AbstractDungeon.player.potions.remove(AbstractDungeon.player.potions.size()-1);
				break;
			case LOSE_CLASS_BASIC:
				CardCrawlGame.sound.play("CARD_EXHAUST");
				c = AbstractDungeon.player.masterDeck.findCardById(AbstractDungeon.player.getStartCardForEvent().cardID);

				if (c != null) {
					AbstractDungeon.topLevelEffects.add(new PurgeCardEffect(c, (Settings.WIDTH / 2), (Settings.HEIGHT / 2)));
					AbstractDungeon.player.masterDeck.removeCard(c);
				}
				break;
			case ADD_STRIKE_DEFEND:
			    switch (AbstractDungeon.player.chosenClass) {
			      case THE_SILENT:
					AbstractDungeon.topLevelEffects.add(new ShowCardAndObtainEffect(
						new Strike_Green(), Settings.WIDTH / 2.0F - AbstractCard.IMG_WIDTH / 2.0F - 30.0F * Settings.scale, Settings.HEIGHT / 2.0F));
					AbstractDungeon.topLevelEffects.add(new ShowCardAndObtainEffect(
						new Defend_Green(), Settings.WIDTH / 2.0F + AbstractCard.IMG_WIDTH / 2.0F + 30.0F * Settings.scale, Settings.HEIGHT / 2.0F));
			        break;
			      case DEFECT:
					AbstractDungeon.topLevelEffects.add(new ShowCardAndObtainEffect(
						new Strike_Blue(), Settings.WIDTH / 2.0F - AbstractCard.IMG_WIDTH / 2.0F - 30.0F * Settings.scale, Settings.HEIGHT / 2.0F));
					AbstractDungeon.topLevelEffects.add(new ShowCardAndObtainEffect(
						new Defend_Blue(), Settings.WIDTH / 2.0F + AbstractCard.IMG_WIDTH / 2.0F + 30.0F * Settings.scale, Settings.HEIGHT / 2.0F));
			        break;
			      case WATCHER:
					AbstractDungeon.topLevelEffects.add(new ShowCardAndObtainEffect(
						new Strike_Purple(), Settings.WIDTH / 2.0F - AbstractCard.IMG_WIDTH / 2.0F - 30.0F * Settings.scale, Settings.HEIGHT / 2.0F));
					AbstractDungeon.topLevelEffects.add(new ShowCardAndObtainEffect(
						new Defend_Watcher(), Settings.WIDTH / 2.0F + AbstractCard.IMG_WIDTH / 2.0F + 30.0F * Settings.scale, Settings.HEIGHT / 2.0F));
			        break;
			      default:
					AbstractDungeon.topLevelEffects.add(new ShowCardAndObtainEffect(
						new Strike_Red(), Settings.WIDTH / 2.0F - AbstractCard.IMG_WIDTH / 2.0F - 30.0F * Settings.scale, Settings.HEIGHT / 2.0F));
					AbstractDungeon.topLevelEffects.add(new ShowCardAndObtainEffect(
						new Defend_Red(), Settings.WIDTH / 2.0F + AbstractCard.IMG_WIDTH / 2.0F + 30.0F * Settings.scale, Settings.HEIGHT / 2.0F));
			        break;
			    } 
				break;
			case TRANSFORM_BANE:
				c = AbstractDungeon.player.masterDeck.findCardById("AscendersBane");
				AbstractDungeon.transformCard(c, false, NeowEvent.rng);
				AbstractDungeon.player.masterDeck.removeCard(c);
				AbstractDungeon.topLevelEffects.add(new ShowCardAndObtainEffect(
						AbstractDungeon.getTransformedCard(), Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
				break;
			case LOWER_MAX_HAND:
				basemod.BaseMod.MAX_HAND_SIZE = 9;
				break;
			case FIRST_TREASURE_EMPTY:
		        for (MapRoomNode m : AbstractDungeon.map.get(8)) {
		            m.setRoom(new CoopEmptyRoom());
					CoopMultiRoom.secondRoomField.secondRoom.set(m, null);
					CoopMultiRoom.thirdRoomField.thirdRoom.set(m, null);
		        }
				break;
			case MORE_MONSTER_NODES:
				for (ArrayList<MapRoomNode> row : AbstractDungeon.map) {
					for (MapRoomNode node : row) {
						if ((node.room instanceof EventRoom))
							node.setRoom(new MonsterRoom());

						AbstractRoom secondRoom = CoopMultiRoom.secondRoomField.secondRoom.get(node);
						if ((secondRoom != null && secondRoom instanceof EventRoom))
							CoopMultiRoom.secondRoomField.secondRoom.set(node, new MonsterRoom());

						AbstractRoom thirdRoom = CoopMultiRoom.thirdRoomField.thirdRoom.get(node);
						if ((thirdRoom != null && thirdRoom instanceof EventRoom))
							CoopMultiRoom.thirdRoomField.thirdRoom.set(node, new MonsterRoom());
					}
				}
				break;
			case TWO_DAZES:
				AbstractDungeon.topLevelEffects.add(new ShowCardAndObtainEffect(
					new Dazed(), Settings.WIDTH / 2.0F - AbstractCard.IMG_WIDTH / 2.0F - 30.0F * Settings.scale, Settings.HEIGHT / 2.0F));
				AbstractDungeon.topLevelEffects.add(new ShowCardAndObtainEffect(
					new Dazed(), Settings.WIDTH / 2.0F + AbstractCard.IMG_WIDTH / 2.0F + 30.0F * Settings.scale, Settings.HEIGHT / 2.0F));
				break;
			case TWO_SLIMED:
				AbstractDungeon.topLevelEffects.add(new ShowCardAndObtainEffect(
					new Slimed(), Settings.WIDTH / 2.0F - AbstractCard.IMG_WIDTH / 2.0F - 30.0F * Settings.scale, Settings.HEIGHT / 2.0F));
				AbstractDungeon.topLevelEffects.add(new ShowCardAndObtainEffect(
					new Slimed(), Settings.WIDTH / 2.0F + AbstractCard.IMG_WIDTH / 2.0F + 30.0F * Settings.scale, Settings.HEIGHT / 2.0F));
				break;
			case THREE_SHIVS:
				AbstractDungeon.topLevelEffects.add(new ShowCardAndObtainEffect(
					new Shiv(), Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
				AbstractDungeon.topLevelEffects.add(new ShowCardAndObtainEffect(
					new Shiv(), Settings.WIDTH / 2.0F - AbstractCard.IMG_WIDTH - 30.0F * Settings.scale, Settings.HEIGHT / 2.0F));
				AbstractDungeon.topLevelEffects.add(new ShowCardAndObtainEffect(
					new Shiv(), Settings.WIDTH / 2.0F + AbstractCard.IMG_WIDTH + 30.0F * Settings.scale, Settings.HEIGHT / 2.0F));
				break;


			// Good Things
			case RANDOM_COLORLESS_2:
				AbstractDungeon.cardRewardScreen.open(
						getColorlessRewardCards(true), null, 
						
						(CardCrawlGame.languagePack.getUIString("CardRewardScreen")).TEXT[1]);
				break;
			case RANDOM_COLORLESS:
				AbstractDungeon.cardRewardScreen.open(
						getColorlessRewardCards(false), null, 
						
						(CardCrawlGame.languagePack.getUIString("CardRewardScreen")).TEXT[1]);
				break;

			case THREE_RARE_CARDS:
				AbstractDungeon.cardRewardScreen.open(getRewardCards(true), null, TEXT[22]);
				break;
			case THREE_CARDS:
				AbstractDungeon.cardRewardScreen.open(
						getRewardCards(false), null, 
						
						(CardCrawlGame.languagePack.getUIString("CardRewardScreen")).TEXT[1]);
				break;

			case HUNDRED_GOLD:
				CardCrawlGame.sound.play("GOLD_JINGLE");
				AbstractDungeon.player.gainGold(100);
				break;
			case TWO_FIFTY_GOLD:
				CardCrawlGame.sound.play("GOLD_JINGLE");
				AbstractDungeon.player.gainGold(250);
				break;

			case ONE_RANDOM_RARE_CARD:
				AbstractDungeon.topLevelEffects.add(new ShowCardAndObtainEffect(
							
							AbstractDungeon.getCard(AbstractCard.CardRarity.RARE, NeowEvent.rng).makeCopy(), Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
				break;
			case RANDOM_SHOP_RELIC:
				AbstractDungeon.getCurrRoom().spawnRelicAndObtain((Settings.WIDTH / 2), (Settings.HEIGHT / 2), 
						
						AbstractDungeon.returnRandomRelic(AbstractRelic.RelicTier.SHOP));
				break;
			case RANDOM_COMMON_RELIC:
				AbstractDungeon.getCurrRoom().spawnRelicAndObtain((Settings.WIDTH / 2), (Settings.HEIGHT / 2), 
						
						AbstractDungeon.returnRandomRelic(AbstractRelic.RelicTier.COMMON));
				break;
			case ONE_RARE_RELIC:
				AbstractDungeon.getCurrRoom().spawnRelicAndObtain((Settings.WIDTH / 2), (Settings.HEIGHT / 2), 
						
						AbstractDungeon.returnRandomRelic(AbstractRelic.RelicTier.RARE));
				break;

			case BOSS_RELIC:
				AbstractDungeon.player.loseRelic(((AbstractRelic)AbstractDungeon.player.relics.get(0)).relicId);
				AbstractDungeon.getCurrRoom().spawnRelicAndObtain((Settings.WIDTH / 2), (Settings.HEIGHT / 2), 
						AbstractDungeon.returnRandomRelic(AbstractRelic.RelicTier.BOSS));
				break;

			case THREE_ENEMY_KILL:
				AbstractDungeon.getCurrRoom().spawnRelicAndObtain((Settings.WIDTH / 2), (Settings.HEIGHT / 2), (AbstractRelic)new NeowsLament());
				break;

			case REMOVE_CARD:
				AbstractDungeon.gridSelectScreen.open(AbstractDungeon.player.masterDeck
						.getPurgeableCards(), 1, TEXT[23], false, false, false, true);
				break;
			case REMOVE_TWO:
				AbstractDungeon.gridSelectScreen.open(AbstractDungeon.player.masterDeck
						.getPurgeableCards(), 2, TEXT[24], false, false, false, false);
				break;

			case TEN_PERCENT_HP_BONUS:
				AbstractDungeon.player.increaseMaxHp((int)(AbstractDungeon.player.maxHealth * 0.1F), true);
				break;
			case TWENTY_PERCENT_HP_BONUS:
				AbstractDungeon.player.increaseMaxHp((int)(AbstractDungeon.player.maxHealth * 0.2F), true);
				break;

			case THREE_SMALL_POTIONS:
				AbstractDungeon.combatRewardScreen.rewards.clear();
				CardCrawlGame.sound.play("POTION_1");
				for (i = 0; i < 3; i++)
					AbstractDungeon.getCurrRoom().addPotionToRewards(PotionHelper.getRandomPotion(NeowEvent.rng)); 
				AbstractDungeon.combatRewardScreen.open(REWARD[19]);
				(AbstractDungeon.getCurrRoom()).rewardPopOutTimer = 0.0F;
				remove = -1;
				for (j = 0; j < AbstractDungeon.combatRewardScreen.rewards.size(); j++) {
					if (((RewardItem)AbstractDungeon.combatRewardScreen.rewards.get(j)).type == RewardItem.RewardType.CARD) {
						remove = j;
						break;
					} 
				} 
				if (remove != -1)
					AbstractDungeon.combatRewardScreen.rewards.remove(remove); 

				AbstractDungeon.overlayMenu.proceedButton.hide();
				break;
			case TRANSFORM_CARD:
				AbstractDungeon.gridSelectScreen.open(AbstractDungeon.player.masterDeck
						.getPurgeableCards(), 1, TEXT[25], false, true, false, false);
				break;
			case TRANSFORM_TWO_CARDS:
				AbstractDungeon.gridSelectScreen.open(AbstractDungeon.player.masterDeck
						.getPurgeableCards(), 2, TEXT[26], false, false, false, false);
				break;
			case UPGRADE_CARD:
				AbstractDungeon.gridSelectScreen.open(AbstractDungeon.player.masterDeck
						.getUpgradableCards(), 1, TEXT[27], true, false, false, false);
				break;
			case REMOVE_ASCENDERS_BANE:
				CardCrawlGame.sound.play("CARD_EXHAUST");
				c = AbstractDungeon.player.masterDeck.findCardById("AscendersBane");

				if (c != null) {
					AbstractDungeon.topLevelEffects.add(new PurgeCardEffect(c, (Settings.WIDTH / 2), (Settings.HEIGHT / 2)));
					AbstractDungeon.player.masterDeck.removeCard(c);
				}
				break;

			case UPGRADE_3_RANDOM:
				CardGroup targets;

				// Center card
				targets = AbstractDungeon.player.masterDeck.getUpgradableCards();
				c = targets.getRandomCard(NeowEvent.rng);
				c.upgrade();
				AbstractDungeon.topLevelEffects.add(new ShowCardBrieflyEffect(c.makeStatEquivalentCopy(), Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
				AbstractDungeon.topLevelEffects.add(new UpgradeShineEffect(Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));

				// Left card
				targets = AbstractDungeon.player.masterDeck.getUpgradableCards();
				c = targets.getRandomCard(NeowEvent.rng);
				c.upgrade();
				AbstractDungeon.topLevelEffects.add(new ShowCardBrieflyEffect(c.makeStatEquivalentCopy(), Settings.WIDTH / 2.0F - AbstractCard.IMG_WIDTH - 30.0F * Settings.scale, Settings.HEIGHT / 2.0F));
				AbstractDungeon.topLevelEffects.add(new UpgradeShineEffect(Settings.WIDTH / 2.0F - AbstractCard.IMG_WIDTH - 30.0F * Settings.scale, Settings.HEIGHT / 2.0F));

				// Right card
				targets = AbstractDungeon.player.masterDeck.getUpgradableCards();
				c = targets.getRandomCard(NeowEvent.rng);
				c.upgrade();
				AbstractDungeon.topLevelEffects.add(new ShowCardBrieflyEffect(c.makeStatEquivalentCopy(), Settings.WIDTH / 2.0F + AbstractCard.IMG_WIDTH + 30.0F * Settings.scale, Settings.HEIGHT / 2.0F));
				AbstractDungeon.topLevelEffects.add(new UpgradeShineEffect(Settings.WIDTH / 2.0F + AbstractCard.IMG_WIDTH + 30.0F * Settings.scale, Settings.HEIGHT / 2.0F));

				break;
			case POTIONS_AND_SLOT:
				CardCrawlGame.sound.play("POTION_1");
				AbstractDungeon.combatRewardScreen.rewards.clear();

				AbstractDungeon.player.potionSlots++;
				AbstractDungeon.player.potions.add(new PotionSlot(AbstractDungeon.player.potionSlots - 1));

				AbstractPotion p;
				while (AbstractDungeon.getCurrRoom().rewards.size() < 3) {
					p = PotionHelper.getRandomPotion(NeowEvent.rng);
					if (p.rarity != AbstractPotion.PotionRarity.COMMON)
						AbstractDungeon.getCurrRoom().addPotionToRewards(p); 
				}

				AbstractDungeon.combatRewardScreen.open(REWARD[19]);
				(AbstractDungeon.getCurrRoom()).rewardPopOutTimer = 0.0F;
				remove = -1;
				for (j = 0; j < AbstractDungeon.combatRewardScreen.rewards.size(); j++) {
					if (((RewardItem)AbstractDungeon.combatRewardScreen.rewards.get(j)).type == RewardItem.RewardType.CARD) {
						remove = j;
						break;
					} 
				} 
				if (remove != -1)
					AbstractDungeon.combatRewardScreen.rewards.remove(remove); 
   				break;
			case UPGRADE_CLASS_RELIC:
				AbstractDungeon.player.loseRelic(((AbstractRelic)AbstractDungeon.player.relics.get(0)).relicId);
				if (AbstractDungeon.player.getStartingRelics().get(0).equals("Burning Blood")) {
					AbstractDungeon.getCurrRoom().spawnRelicAndObtain((Settings.WIDTH / 2), (Settings.HEIGHT / 2), RelicLibrary.getRelic("Black Blood").makeCopy());
					AbstractDungeon.bossRelicPool.remove("Black Blood");
				}
				if (AbstractDungeon.player.getStartingRelics().get(0).equals("Ring of the Snake")) {
					AbstractDungeon.getCurrRoom().spawnRelicAndObtain((Settings.WIDTH / 2), (Settings.HEIGHT / 2), RelicLibrary.getRelic("Ring of the Serpent").makeCopy());
					AbstractDungeon.bossRelicPool.remove("Ring of the Serpent");
				}
				if (AbstractDungeon.player.getStartingRelics().get(0).equals("Cracked Core")) {
					AbstractDungeon.getCurrRoom().spawnRelicAndObtain((Settings.WIDTH / 2), (Settings.HEIGHT / 2), RelicLibrary.getRelic("FrozenCore").makeCopy());
					AbstractDungeon.bossRelicPool.remove("FrozenCore");
				}
				if (AbstractDungeon.player.getStartingRelics().get(0).equals("PureWater")) {
					AbstractDungeon.getCurrRoom().spawnRelicAndObtain((Settings.WIDTH / 2), (Settings.HEIGHT / 2), RelicLibrary.getRelic("HolyWater").makeCopy());
					AbstractDungeon.bossRelicPool.remove("HolyWater");
				}
				break;
			case RANDOM_CLASS_RELIC:
				AbstractRelic r = null;
			    switch (AbstractDungeon.player.chosenClass) {
			      case IRONCLAD:
			      	do {
			      		r = RelicLibrary.redList.get(NeowEvent.rng.random(RelicLibrary.redList.size()-1));
			      	} while (r.tier == AbstractRelic.RelicTier.BOSS || r.tier == AbstractRelic.RelicTier.STARTER);
			        break;
			      case THE_SILENT:
			      	do {
			     	   r = RelicLibrary.greenList.get(NeowEvent.rng.random(RelicLibrary.greenList.size()-1));
			      	} while (r.tier == AbstractRelic.RelicTier.BOSS || r.tier == AbstractRelic.RelicTier.STARTER);
			        break;
			      case DEFECT:
			      	do {
			        	r = RelicLibrary.blueList.get(NeowEvent.rng.random(RelicLibrary.blueList.size()-1));
			      	} while (r.tier == AbstractRelic.RelicTier.BOSS || r.tier == AbstractRelic.RelicTier.STARTER);
			        break;
			      case WATCHER:
			      	do {
			        	r = RelicLibrary.whiteList.get(NeowEvent.rng.random(RelicLibrary.whiteList.size()-1));
			      	} while (r.tier == AbstractRelic.RelicTier.BOSS || r.tier == AbstractRelic.RelicTier.STARTER);
			        break;
			    } 
			    if (r != null) {
					AbstractDungeon.getCurrRoom().spawnRelicAndObtain((Settings.WIDTH / 2), (Settings.HEIGHT / 2), r.makeCopy());
					AbstractDungeon.commonRelicPool.remove(r.relicId);
					AbstractDungeon.uncommonRelicPool.remove(r.relicId);
					AbstractDungeon.rareRelicPool.remove(r.relicId);
					AbstractDungeon.shopRelicPool.remove(r.relicId);
			    }
				break;
			case TWO_RANDOM_UPGRADED_CARDS:
				c = getCard(rollRarity()).makeCopy();
				c.upgrade();
				AbstractDungeon.topLevelEffects.add(
					new ShowCardAndObtainEffect(c.makeStatEquivalentCopy(), Settings.WIDTH / 2.0F - AbstractCard.IMG_WIDTH / 2.0F - 30.0F * Settings.scale, Settings.HEIGHT / 2.0F));

				c = getCard(rollRarity()).makeCopy();
				c.upgrade();
				AbstractDungeon.topLevelEffects.add(
					new ShowCardAndObtainEffect(c.makeStatEquivalentCopy(), Settings.WIDTH / 2.0F + AbstractCard.IMG_WIDTH / 2.0F + 30.0F * Settings.scale, Settings.HEIGHT / 2.0F));

				break;
		} 
	}
	
	public ArrayList<AbstractCard> getColorlessRewardCards(boolean rareOnly) {
		ArrayList<AbstractCard> retVal = new ArrayList<>();
		for (int numCards = 3, i = 0; i < numCards; i++) {
			AbstractCard.CardRarity rarity = rollRarity();
			if (rareOnly) {
				rarity = AbstractCard.CardRarity.RARE;
			} else if (rarity == AbstractCard.CardRarity.COMMON) {
				rarity = AbstractCard.CardRarity.UNCOMMON;
			} 
			AbstractCard card = AbstractDungeon.getColorlessCardFromPool(rarity);
			while (retVal.contains(card))
				card = AbstractDungeon.getColorlessCardFromPool(rarity); 
			retVal.add(card);
		} 
		ArrayList<AbstractCard> retVal2 = new ArrayList<>();
		for (AbstractCard c : retVal)
			retVal2.add(c.makeCopy()); 
		return retVal2;
	}
	
	public ArrayList<AbstractCard> getRewardCards(boolean rareOnly) {
		ArrayList<AbstractCard> retVal = new ArrayList<>();
		for (int numCards = 3, i = 0; i < numCards; i++) {
			AbstractCard.CardRarity rarity = rollRarity();
			if (rareOnly)
				rarity = AbstractCard.CardRarity.RARE; 
			AbstractCard card = null;
			switch (rarity) {
				case RARE:
					card = getCard(rarity);
					break;
				case UNCOMMON:
					card = getCard(rarity);
					break;
				case COMMON:
					card = getCard(rarity);
					break;
				default:
					TogetherManager.log("WTF?");
					break;
			} 
			while (retVal.contains(card))
				card = getCard(rarity); 
			retVal.add(card);
		} 
		ArrayList<AbstractCard> retVal2 = new ArrayList<>();
		for (AbstractCard c : retVal)
			retVal2.add(c.makeCopy()); 
		return retVal2;
	}
	
	public AbstractCard.CardRarity rollRarity() {
		if (NeowEvent.rng.randomBoolean(0.33F))
			return AbstractCard.CardRarity.UNCOMMON; 
		return AbstractCard.CardRarity.COMMON;
	}
	
	public AbstractCard getCard(AbstractCard.CardRarity rarity) {
		switch (rarity) {
			case RARE:
				return AbstractDungeon.rareCardPool.getRandomCard(NeowEvent.rng);
			case UNCOMMON:
				return AbstractDungeon.uncommonCardPool.getRandomCard(NeowEvent.rng);
			case COMMON:
				return AbstractDungeon.commonCardPool.getRandomCard(NeowEvent.rng);
		} 
		TogetherManager.log("Error in getCard in Neow Reward");
		return null;
	}
}
