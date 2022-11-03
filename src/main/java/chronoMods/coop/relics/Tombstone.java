package chronoMods.coop.relics;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.BlightStrings;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;


public class Tombstone extends AbstractCard {
	public static final String ID = "CurseOfTheBell";
	private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("CurseOfTheBell");
	public static final String DESCRIPTION = cardStrings.DESCRIPTION;

	private static final BlightStrings blightStrings = CardCrawlGame.languagePack.getBlightString("Tombstone");
	public static final String NAME = blightStrings.NAME;
	public static final String[] EPITAPH = blightStrings.DESCRIPTION;

	private static final int COST = 1;

	public String playerName, killName;
	public Texture reserveportrait;

	public Tombstone(String playerName, String killName, Texture portrait) {
		super(ID, String.format(NAME, playerName), "status/beta", "status/beta", COST, 
			String.format(EPITAPH[0], playerName, killName == "" ? EPITAPH[1] : killName), 
			AbstractCard.CardType.CURSE, AbstractCard.CardColor.CURSE, AbstractCard.CardRarity.CURSE, AbstractCard.CardTarget.NONE);
		// this.isEthereal = true;

		this.portraitImg = portrait;
		this.portrait = null;

		this.playerName = playerName;
		this.killName = killName;
		this.reserveportrait = portrait;
		this.exhaust = true;
		this.selfRetain = true;
	}

	@Override
	public void use(AbstractPlayer p, AbstractMonster m) {}

	// public void triggerOnEndOfPlayerTurn() {
	// 	addToTop(new ExhaustSpecificCardAction(this, AbstractDungeon.player.hand));
	// }

	@Override
	public void upgrade() {}

	@Override
	public AbstractCard makeCopy() { return new Tombstone(playerName, killName, reserveportrait); }
}