package chronoMods.coop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.rooms.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
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

public class CoopEmptyRoom extends AbstractRoom {
	private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString("TreasureRoom");
	public static final String[] TEXT = uiStrings.TEXT;

	// Treasure
	public AbstractChest chest;
	private float shinyTimer = 0f;
	private static final float SHINY_INTERVAL = 0.2f;

	// Default Constructor
	public CoopEmptyRoom() {
		phase = RoomPhase.COMPLETE;
		mapSymbol = "C";
		mapImg = TogetherManager.mapEmpty;
		mapImgOutline = TogetherManager.mapEmptyOutline;
	}

	@Override
	public void onPlayerEntry() {
		playBGM(null);
		// chest = AbstractDungeon.getRandomChest();
		AbstractDungeon.overlayMenu.proceedButton.setLabel(TEXT[0]);

		if (rewards.size() > 0) {
			AbstractDungeon.combatRewardScreen.open();
		}
	}

	// Update the treasure room
	@Override
	public void update() {
		super.update();
		// if (chest != null) {
		// 	chest.update();
		// }
		// updateShiny();
	}

	private void updateShiny() {
		// if (!chest.isOpen) {
		// 	shinyTimer -= Gdx.graphics.getDeltaTime();
		// 	if (shinyTimer < 0f && !Settings.DISABLE_EFFECTS) {
		// 		shinyTimer = SHINY_INTERVAL;
		// 		AbstractDungeon.topLevelEffects.add(new ChestShineEffect());
		// 		AbstractDungeon.effectList.add(new SpookyChestEffect());
		// 		AbstractDungeon.effectList.add(new SpookyChestEffect());
		// 	}
		// }
	}

	@Override
	public void renderAboveTopPanel(SpriteBatch sb) {
		super.renderAboveTopPanel(sb);
	}

	// Render the contents of the room
	@Override
	public void render(SpriteBatch sb) {
		// if (chest != null) {
		// 	chest.render(sb);
		// }
		super.render(sb);
	}
}
