package chronoMods.coop.infusions;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.localization.*;
import com.megacrit.cardcrawl.blights.*;
import com.megacrit.cardcrawl.vfx.NecronomicurseEffect;

import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;

import basemod.abstracts.*;


public class Conduit extends AbstractCard {
	private static final String[] TEXT = CardCrawlGame.languagePack.getBlightString("TransfusionBag").DESCRIPTION;

	private static final int COST = -2;

	public String playerName;
	public Texture reserveportrait;
	InfusionSet set;

	public Conduit(String playerName, InfusionSet set, Texture portrait) {
		super("CurseOfTheBell", String.format(TEXT[1], playerName), "status/beta", "status/beta", COST, 
			String.format(TEXT[2], playerName, set.name), 
			AbstractCard.CardType.STATUS, AbstractCard.CardColor.COLORLESS, AbstractCard.CardRarity.SPECIAL, AbstractCard.CardTarget.NONE);

		this.portraitImg = portrait;
		this.portrait = null;

		this.playerName = playerName;
		this.reserveportrait = portrait;
		this.set = set;
	}

	@Override
	public void use(AbstractPlayer p, AbstractMonster m) {}

	public void onRemoveFromMasterDeck() {
		AbstractDungeon.effectsQueue.add(new NecronomicurseEffect(new Conduit(playerName, set, reserveportrait), Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
	}

	@Override
	public void upgrade() {}

	@Override
	public AbstractCard makeCopy() { return new Conduit(playerName, set, reserveportrait); }
}