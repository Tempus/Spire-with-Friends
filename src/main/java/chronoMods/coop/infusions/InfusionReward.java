package chronoMods.coop.infusions;

import com.evacipated.cardcrawl.modthespire.lib.*;
import basemod.abstracts.*;

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
import com.megacrit.cardcrawl.rewards.*;
import com.megacrit.cardcrawl.shop.*;
import com.megacrit.cardcrawl.neow.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.*;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.rewards.chests.AbstractChest;
import com.megacrit.cardcrawl.vfx.ChestShineEffect;
import com.megacrit.cardcrawl.vfx.scene.SpookyChestEffect;
import com.megacrit.cardcrawl.random.Random;

import chronoMods.*;
import chronoMods.coop.*;
import chronoMods.network.steam.*;
import chronoMods.network.*;
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
import com.megacrit.cardcrawl.vfx.cardManip.*;
import com.megacrit.cardcrawl.vfx.*;
import java.util.ArrayList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class InfusionReward extends CustomReward {

	public Infusion infusion;
	public static Texture bgIcon = new Texture("chrono/images/infusions/NeowInfusion.png");
	public static final String[] INFUSE = CardCrawlGame.languagePack.getUIString("CardInfusions").TEXT;

	public boolean gridOpened;
	public boolean rewardTaken;

	public InfusionReward(Infusion infusion)
	{
		super(AbstractDungeon.player.relics.get(0).img, infusion.description, RewardTypePatch.INFUSION);

		this.infusion = infusion;
 	}

 	public void update() {
		if (!isDone && gridOpened) {
			if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
				AbstractCard c = AbstractDungeon.gridSelectScreen.selectedCards.get(0);

				infusion.ApplyInfusion(c);
				AbstractDungeon.topLevelEffects.add(new ShowCardBrieflyEffect(c.makeStatEquivalentCopy()));

				AbstractDungeon.gridSelectScreen.selectedCards.clear();
				AbstractDungeon.overlayMenu.cancelButton.hide();
				rewardTaken = true;

				isDone = true;
 			}
		}

		super.update();
 	}

	public boolean claimReward() {
		if (rewardTaken) { return true; }

		for (AbstractCard c : AbstractDungeon.player.masterDeck.group)
			c.stopGlowing();
		
		AbstractDungeon.dynamicBanner.hide();

		AbstractDungeon.gridSelectScreen.open(infusion.getInfuseable(AbstractDungeon.player.masterDeck), 1, true, INFUSE[5]);
		gridOpened = true;

  	return false;
	}
  
	@Override
	public void render(SpriteBatch sb)
	{
		if (this.hb.hovered) {
			sb.setColor(new Color(0.4f, 0.6f, 0.6f, 1.0f));
		} else {
			sb.setColor(new Color(0.5f, 0.6f, 0.6f, 0.8f));
		}

		if (this.hb.clickStarted) {
			sb.draw(ImageMaster.REWARD_SCREEN_ITEM, Settings.WIDTH / 2.0f - 232.0f, this.y - 49.0f, 232.0f, 49.0f, 464.0f, 98.0f, Settings.scale * 0.98f, Settings.scale * 0.98f, 0.0f, 0, 0, 464, 98, false, false);
		} else {
			sb.draw(ImageMaster.REWARD_SCREEN_ITEM, Settings.WIDTH / 2.0f - 232.0f, this.y - 49.0f, 232.0f, 49.0f, 464.0f, 98.0f, Settings.scale, Settings.scale, 0.0f, 0, 0, 464, 98, false, false);
		}

		if (this.flashTimer != 0.0f) {
			sb.setColor(0.6f, 1.0f, 1.0f, this.flashTimer * 1.5f);
			sb.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
			sb.draw(ImageMaster.REWARD_SCREEN_ITEM, Settings.WIDTH / 2.0f - 232.0f, this.y - 49.0f, 232.0f, 49.0f, 464.0f, 98.0f, Settings.scale * 1.03f, Settings.scale * 1.15f, 0.0f, 0, 0, 464, 98, false, false);
			sb.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		}

		sb.setColor(Color.WHITE.cpy());

		// Put my Actual code here
		sb.draw(bgIcon, RewardItem.REWARD_ITEM_X - 64.0F, this.y - 64.0F - 2.0F * Settings.scale, 64.0F, 64.0F, 128.0F, 128.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 128, 128, false, false);
		sb.draw(infusion.icon, RewardItem.REWARD_ITEM_X - 21.0F, this.y - 21.0F - 10.0F * Settings.scale, 21.0F, 21.0F, 42.0F, 42.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 96, 96, false, false);
    	// sb.draw(infusionSet.icon, this.currentX - 48.0F + (float)ReflectionHacks.getPrivate(this, AbstractRelic.class, "offsetX"), this.currentY - 48.0F - 10f*Settings.scale, 48.0F, 48.0F, 96.0F, 96.0F, this.scale * 0.35f, this.scale * 0.35f, (float)ReflectionHacks.getPrivate(this, AbstractRelic.class, "rotation"), 0, 0, 96, 96, false, false);

    if (this.hb.hovered)
			TipHelper.renderGenericTip(RewardItem.REWARD_ITEM_X - 32.0F + 64f*Settings.scale, this.y - 32.0F - 2.0F * Settings.scale, INFUSE[4], infusion.description);

    // And we're done.


		Color c = Settings.CREAM_COLOR.cpy();
		if (this.hb.hovered) {
			c = Settings.GOLD_COLOR.cpy();
		}

		FontHelper.renderSmartText(sb, FontHelper.cardDescFont_N, this.text, 833.0f * Settings.scale, this.y + 5.0f * Settings.scale, 1000.0f * Settings.scale, 0.0f, c);

		if (!this.hb.hovered) {
			for (AbstractGameEffect e : this.effects) {
				e.render(sb);
			}
		}

		this.hb.render(sb);
	}
}