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
import com.megacrit.cardcrawl.relics.*;
import com.megacrit.cardcrawl.vfx.UpgradeShineEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;
import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;

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

public class BigHouse extends AbstractBlight {
	public static final String ID = "BigHouse";
	private static final BlightStrings blightStrings = CardCrawlGame.languagePack.getBlightString(ID);
	public static final String NAME = blightStrings.NAME;
	public static final String[] DESCRIPTIONS = blightStrings.DESCRIPTION;

	public BigHouse() {
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

	@Override
	public void onEquip() {

		// Lowest Gold
		RemotePlayer lowestGold = TogetherManager.players
				.stream()
				.min((x, y) -> x.gold - y.gold).get();
     
     	if (AbstractDungeon.player.gold == lowestGold.gold)
     		AbstractDungeon.getCurrRoom().addGoldToRewards(50);

		// Lowest Potions
		RemotePlayer lowestPots = TogetherManager.players
				.stream()
				.min((x, y) -> x.potions.size() - y.potions.size()).get();
     
     	if (TogetherManager.getCurrentUser().potions.size() == lowestGold.potions.size())
			AbstractDungeon.getCurrRoom().addPotionToRewards(PotionHelper.getRandomPotion(AbstractDungeon.miscRng));

		// Fewest Cards
		RemotePlayer lowestCards= TogetherManager.players
				.stream()
				.min((x, y) -> x.cards - y.cards).get();
     
     	if (AbstractDungeon.player.masterDeck.size() == lowestCards.cards)
     		AbstractDungeon.getCurrRoom().addCardToRewards();

		// Most Cards
		RemotePlayer mostCards= TogetherManager.players
				.stream()
				.min((x, y) -> y.cards - x.cards).get();
     
     	if (AbstractDungeon.player.masterDeck.size() == mostCards.cards) {
     		CardGroup purgies = AbstractDungeon.player.masterDeck.getPurgeableCards();
     		AbstractCard card;

     		card = purgies.getRandomCard(AbstractCard.CardType.CURSE, true);
     		if (card == null)
     			card = purgies.getRandomCard(AbstractCard.CardType.CURSE, true);
     		if (card == null)
     			card = purgies.getRandomCard(true, AbstractCard.CardRarity.BASIC);
     		if (card == null)
     			card = purgies.getRandomCard(true, AbstractCard.CardRarity.COMMON);
     		if (card == null)
     			card = purgies.getRandomCard(true, AbstractCard.CardRarity.UNCOMMON);
     		if (card == null)
     			card = purgies.getRandomCard(true, AbstractCard.CardRarity.RARE);

     		if (card != null) {
				AbstractDungeon.topLevelEffects.add(new PurgeCardEffect(card, Settings.WIDTH / 2f, Settings.HEIGHT / 2.0F));
				AbstractDungeon.player.masterDeck.removeCard(card);
			}
     	}

		// Fewest relics
		RemotePlayer lowestRelics = TogetherManager.players
				.stream()
				.min((x, y) -> x.relics - y.relics).get();
     
     	if (AbstractDungeon.player.relics.size() == lowestRelics.relics)
     		AbstractDungeon.getCurrRoom().addRelicToRewards(AbstractRelic.RelicTier.UNCOMMON);

		// Lowest Upgrades
		RemotePlayer lowestUpgrades = TogetherManager.players
				.stream()
				.min((x, y) -> x.upgrades - y.upgrades).get();

     	if (TogetherManager.getCurrentUser().upgrades == lowestUpgrades.upgrades) {
			ArrayList<AbstractCard> upgradableCards = new ArrayList<>();
			for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
				if (c.canUpgrade())
					upgradableCards.add(c); 
			}

			Collections.shuffle(upgradableCards, new Random(AbstractDungeon.miscRng.randomLong()));
			
			if (!upgradableCards.isEmpty())
				if (upgradableCards.size() == 1) {
					((AbstractCard)upgradableCards.get(0)).upgrade();
					AbstractDungeon.player.bottledCardUpgradeCheck(upgradableCards.get(0));
					AbstractDungeon.topLevelEffects.add(new ShowCardBrieflyEffect(((AbstractCard)upgradableCards
					.get(0)).makeStatEquivalentCopy()));
					AbstractDungeon.topLevelEffects.add(new UpgradeShineEffect(Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
				} else {
					((AbstractCard)upgradableCards.get(0)).upgrade();
					AbstractDungeon.player.bottledCardUpgradeCheck(upgradableCards.get(0));
					AbstractDungeon.player.bottledCardUpgradeCheck(upgradableCards.get(1));
					AbstractDungeon.topLevelEffects.add(new ShowCardBrieflyEffect(((AbstractCard)upgradableCards

					.get(0)).makeStatEquivalentCopy(), Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
					AbstractDungeon.topLevelEffects.add(new UpgradeShineEffect(Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
				}  
		}

		// Lowest MaxHp
		RemotePlayer lowestHP = TogetherManager.players
				.stream()
				.min((x, y) -> x.maxHp - y.maxHp).get();
		
     	if (AbstractDungeon.player.maxHealth == lowestHP.maxHp)
			AbstractDungeon.player.increaseMaxHp(5, true);

		// Conclude
		AbstractDungeon.combatRewardScreen.open(this.DESCRIPTIONS[1]); // Big House!
		(AbstractDungeon.getCurrRoom()).rewardPopOutTimer = 0.0F;
	}
}