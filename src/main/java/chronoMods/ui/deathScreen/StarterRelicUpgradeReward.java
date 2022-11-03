package chronoMods.ui.deathScreen;

import basemod.abstracts.CustomReward;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

public class StarterRelicUpgradeReward extends CustomReward {

	public StarterRelicUpgradeReward()
	{
		super(AbstractDungeon.player.relics.get(0).img, CardCrawlGame.languagePack.getUIString("RaceEnd").TEXT[7], RewardTypePatch.STARTERUP);
	    this.relic = relic;

		if (AbstractDungeon.player.getStartingRelics().get(0).equals("Burning Blood")) {
			this.relic = RelicLibrary.getRelic("Black Blood").makeCopy();
			AbstractDungeon.bossRelicPool.remove("Black Blood");
		}
		else if (AbstractDungeon.player.getStartingRelics().get(0).equals("Ring of the Snake")) {
			this.relic = RelicLibrary.getRelic("Ring of the Serpent").makeCopy();
			AbstractDungeon.bossRelicPool.remove("Ring of the Serpent");
		}
		else if (AbstractDungeon.player.getStartingRelics().get(0).equals("Cracked Core")) {
			this.relic = RelicLibrary.getRelic("FrozenCore").makeCopy();
			AbstractDungeon.bossRelicPool.remove("FrozenCore");
		}
		else if (AbstractDungeon.player.getStartingRelics().get(0).equals("PureWater")) {
			this.relic = RelicLibrary.getRelic("HolyWater").makeCopy();
			AbstractDungeon.bossRelicPool.remove("HolyWater");
		}
		else {
			this.relic = RelicLibrary.getRelic("Pocketwatch").makeCopy();
		}

	    this.relic.hb = new Hitbox(80.0F * Settings.scale, 80.0F * Settings.scale);
	    this.relic.hb.move(-1000.0F, -1000.0F);
   	}

	public boolean claimReward() {
		AbstractDungeon.player.loseRelic(((AbstractRelic)AbstractDungeon.player.relics.get(0)).relicId);
    	AbstractDungeon.getCurrRoom().spawnRelicAndObtain((Settings.WIDTH / 2), (Settings.HEIGHT / 2), this.relic);
    	AbstractDungeon.getCurrRoom().addRelicToRewards(RelicLibrary.getRelic("WingedGreaves").makeCopy());
    	return true;
	}

	@Override
	public void move(float y) {
		this.y = y;
		this.hb.move(Settings.WIDTH / 2.0F, y);
		if (this.relic != null) {
			this.relic.currentX = REWARD_ITEM_X;
			this.relic.currentY = y;
			this.relic.targetX = REWARD_ITEM_X;
			this.relic.targetY = y;
		}
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

        this.relic.renderWithoutAmount(sb, new Color(0.0F, 0.0F, 0.0F, 0.25F));
        if (this.hb.hovered)
          this.relic.renderTip(sb);

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