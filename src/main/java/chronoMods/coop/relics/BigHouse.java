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

	RemotePlayer lowestGold, lowestPots, lowestCards, mostCards, lowestRelics, lowestUpgrades, lowestHP;


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

		if (!this.isObtained) {
			calculateEarners();
			this.description += String.format(this.DESCRIPTIONS[2], getGoldNames(), getPotionNames(), getFewestCardsNames(), getMostCardsNames(), getRelicsNames(), getUpgradeNames(), getMaxHPNames());
		}
	}

    public void renderTip(SpriteBatch sb) {
        updateDescription();
        this.tips.clear();
        this.tips.add(new PowerTip(name, description));

        super.renderTip(sb);
    }

	public void calculateEarners() {
		// Lowest Gold
		lowestGold = TogetherManager.players.stream()
				.min((x, y) -> x.gold - y.gold).get();
     
		// Lowest Potions
		lowestPots = TogetherManager.players.stream()
				.min((x, y) -> x.potions.size() - y.potions.size()).get();
     
		// Fewest Cards
		lowestCards = TogetherManager.players.stream()
				.min((x, y) -> x.cards - y.cards).get();
     
		// Most Cards
		mostCards = TogetherManager.players.stream()
				.max((x, y) -> x.cards - y.cards).get();
     
		// Fewest relics
		lowestRelics = TogetherManager.players.stream()
				.min((x, y) -> x.relics - y.relics).get();
     
		// Lowest Upgrades
		lowestUpgrades = TogetherManager.players.stream()
				.min((x, y) -> x.upgrades - y.upgrades).get();

		// Lowest MaxHp
		lowestHP = TogetherManager.players.stream()
				.min((x, y) -> x.maxHp - y.maxHp).get();
	}

	public String getGoldNames() {
		String names = "";
		for (RemotePlayer p : TogetherManager.players)
			if (p.gold == lowestGold.gold)
				names += p.userName + ", ";
		
		return names.substring(0, names.length() - 2);
	}

	public String getPotionNames() {
		String names = "";
		for (RemotePlayer p : TogetherManager.players)
			if (p.potions.size()  == lowestPots.potions.size() )
				names += p.userName + ", ";
		
		return names.substring(0, names.length() - 2);
	}

	public String getFewestCardsNames() {
		String names = "";
		for (RemotePlayer p : TogetherManager.players)
			if (p.cards  == lowestCards.cards)
				names += p.userName + ", ";
		
		return names.substring(0, names.length() - 2);
	}

	public String getMostCardsNames() {
		String names = "";
		for (RemotePlayer p : TogetherManager.players)
			if (p.cards  == mostCards.cards)
				names += p.userName + ", ";
		
		return names.substring(0, names.length() - 2);
	}

	public String getRelicsNames() {
		String names = "";
		for (RemotePlayer p : TogetherManager.players)
			if (p.relics  == lowestRelics.relics)
				names += p.userName + ", ";
		
		return names.substring(0, names.length() - 2);
	}

	public String getUpgradeNames() {
		String names = "";
		for (RemotePlayer p : TogetherManager.players)
			if (p.upgrades  == lowestUpgrades.upgrades)
				names += p.userName + ", ";
		
		return names.substring(0, names.length() - 2);
	}

	public String getMaxHPNames() {
		String names = "";
		for (RemotePlayer p : TogetherManager.players)
			if (p.maxHp  == lowestHP.maxHp)
				names += p.userName + ", ";
		
		return names.substring(0, names.length() - 2);
	}

	@Override
	public void onEquip() {
		calculateEarners();

		// Lowest Gold     
     	if (AbstractDungeon.player.gold == lowestGold.gold)
     		AbstractDungeon.getCurrRoom().addGoldToRewards(50);

		// Lowest Potions
     	if (TogetherManager.getCurrentUser().potions.size() == lowestGold.potions.size())
			AbstractDungeon.getCurrRoom().addPotionToRewards(PotionHelper.getRandomPotion(AbstractDungeon.miscRng));

		// Fewest Cards
     	if (AbstractDungeon.player.masterDeck.size() == lowestCards.cards)
     		AbstractDungeon.getCurrRoom().addCardToRewards();

		// Most Cards
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
     	if (AbstractDungeon.player.relics.size() == lowestRelics.relics)
     		AbstractDungeon.getCurrRoom().addRelicToRewards(AbstractRelic.RelicTier.UNCOMMON);

		// Lowest Upgrades
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
     	if (AbstractDungeon.player.maxHealth == lowestHP.maxHp)
			AbstractDungeon.player.increaseMaxHp(5, true);

		// Conclude
		AbstractDungeon.combatRewardScreen.open(this.DESCRIPTIONS[1]); // Big House!
		(AbstractDungeon.getCurrRoom()).rewardPopOutTimer = 0.0F;
	}
}