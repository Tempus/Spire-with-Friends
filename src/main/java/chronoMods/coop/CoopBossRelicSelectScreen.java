package chronoMods.coop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.blights.AbstractBlight;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.controller.CInputHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.TreasureRoomBoss;
import com.megacrit.cardcrawl.screens.mainMenu.MenuCancelButton;
import com.megacrit.cardcrawl.ui.buttons.ConfirmButton;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.BossChestShineEffect;
import com.megacrit.cardcrawl.vfx.FloatyEffect;
import de.robojumper.ststwitch.TwitchPanel;
import de.robojumper.ststwitch.TwitchVoteListener;
import de.robojumper.ststwitch.TwitchVoteOption;
import de.robojumper.ststwitch.TwitchVoter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Stream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import chronoMods.*;
import chronoMods.network.steam.*;
import chronoMods.network.*;
import chronoMods.ui.deathScreen.*;
import chronoMods.ui.hud.*;
import chronoMods.ui.lobby.*;
import chronoMods.ui.mainMenu.*;
import chronoMods.utilities.*;

import com.evacipated.cardcrawl.modthespire.lib.*;
import basemod.interfaces.*;
import basemod.*;

public class CoopBossRelicSelectScreen implements StartActSubscriber {
	private static final Logger logger = LogManager.getLogger(CoopBossRelicSelectScreen.class.getName());
	private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString("BossRelicSelectScreen");
	public static final String[] TEXT = uiStrings.TEXT;
	
	public boolean isDone = false;
		
	public ArrayList<AbstractBlight> blights = new ArrayList<>();
		
	private static final String SELECT_MSG = TEXT[2];
	
	private Texture smokeImg;
	private float shineTimer = 0.0F;
	private static final float SHINE_INTERAL = 0.1F;
	
	private static final float BANNER_Y = AbstractDungeon.floorY + 460.0F * Settings.scale;
	private static final float SLOT_1_X = Settings.WIDTH / 2.0F + 4.0F * Settings.scale, SLOT_1_Y = AbstractDungeon.floorY + 360.0F * Settings.scale;
	private static final float SLOT_2_X = Settings.WIDTH / 2.0F - 116.0F * Settings.scale, SLOT_2_Y = AbstractDungeon.floorY + 225.0F * Settings.scale;
	private static final float SLOT_3_X = Settings.WIDTH / 2.0F + 124.0F * Settings.scale;
	private final float B_SLOT_1_X = 844.0F * Settings.scale, B_SLOT_1_Y = AbstractDungeon.floorY + 310.0F * Settings.scale;
	private final float B_SLOT_2_X = 1084.0F * Settings.scale;
	
	public ArrayList<ArrayList<RemotePlayer>> selected = new ArrayList();
	public int selectedIndex = -1;
				

	public static class Enum
	{
		@SpireEnum
		public static AbstractDungeon.CurrentScreen TEAMRELIC;
	}

	@SpirePatch(clz=AbstractDungeon.class, method="update")
	public static class Update
	{
		public static void Postfix(AbstractDungeon __instance)
		{
			if (__instance.screen == CoopBossRelicSelectScreen.Enum.TEAMRELIC) {
				TogetherManager.teamRelicScreen.update();
			}
		}
	}

	@SpirePatch(clz=AbstractDungeon.class, method="render")
	public static class Render
	{
		@SpireInsertPatch(rloc=2773-2658,localvars={})
		public static void Insert(AbstractDungeon __instance, SpriteBatch sb)
		{
			if (__instance.screen == CoopBossRelicSelectScreen.Enum.TEAMRELIC) {
				TogetherManager.teamRelicScreen.render(sb);
			}
		}
	}

    @SpirePatch(clz=AbstractDungeon.class, method="openPreviousScreen")
    public static class Reopen
    {
        public static void Postfix(AbstractDungeon.CurrentScreen s)
        {
            if (s == CoopBossRelicSelectScreen.Enum.TEAMRELIC) {
                TogetherManager.teamRelicScreen.reopen();
            }
        }
    }

	// Hardcoded bullshit fix patch for clicky clikcy
	@SpirePatch(clz=AbstractBlight.class, method="update")
	public static class ClickyFixForAbstractBlightShit
	{
		@SpireInsertPatch(rloc=659-604,localvars={})
		public static void Insert(AbstractBlight __instance)
		{
	        if (__instance.hb.hovered && AbstractDungeon.screen == CoopBossRelicSelectScreen.Enum.TEAMRELIC) {
	          if (InputHelper.justClickedLeft && !__instance.isObtained) {
	            InputHelper.justClickedLeft = false;
	            __instance.hb.clickStarted = true;
	          } 
	          if ((__instance.hb.clicked || CInputActionSet.select.isJustPressed()) && !__instance.isObtained) {
	            CInputActionSet.select.unpress();
	            __instance.hb.clicked = false;
				__instance.bossObtainLogic();
	          } 
	        } 			
		}
	}

	// And this one fixes the bullshit rendering hardcoded shit!
	@SpirePatch(clz=AbstractBlight.class, method="render", paramtypez={SpriteBatch.class})
	public static class renderwhenBlightIsTouched
	{
		public static void Replace(AbstractBlight __instance, SpriteBatch sb)
		{
			float rotation = (float)ReflectionHacks.getPrivate(__instance, AbstractBlight.class, "rotation");
			FloatyEffect f_effect = (FloatyEffect)ReflectionHacks.getPrivate(__instance, AbstractBlight.class, "f_effect");

		    if (Settings.hideRelics)
		      return; 
		    if (__instance.isDone)
		      __instance.renderOutline(sb, false); 
		    if (!__instance.isObtained && (AbstractDungeon.screen == CoopBossRelicSelectScreen.Enum.TEAMRELIC)) {
		        if (__instance.hb.hovered) {
		          __instance.renderTip(sb); 
		          sb.setColor(new Color(0.0F, 0.0F, 0.0F, 0.33F));
		          sb.draw(__instance.outlineImg, __instance.currentX - 64.0F + f_effect.x, __instance.currentY - 64.0F + f_effect.y, 64.0F, 64.0F, 128.0F, 128.0F, __instance.scale, __instance.scale, rotation, 0, 0, 128, 128, false, false);
		        } else {
		          sb.setColor(new Color(0.0F, 0.0F, 0.0F, 0.33F));
		          sb.draw(__instance.outlineImg, __instance.currentX - 64.0F + f_effect.x, __instance.currentY - 64.0F + f_effect.y, 64.0F, 64.0F, 128.0F, 128.0F, __instance.scale, __instance.scale, rotation, 0, 0, 128, 128, false, false);
		        }  
		    } 
		    if (AbstractDungeon.screen == CoopBossRelicSelectScreen.Enum.TEAMRELIC) {
		      if (!__instance.isObtained) {
		        sb.setColor(Color.WHITE);
		        sb.draw(__instance.img, __instance.currentX - 64.0F + f_effect.x, __instance.currentY - 64.0F + f_effect.y, 64.0F, 64.0F, 128.0F, 128.0F, __instance.scale, __instance.scale, rotation, 0, 0, 128, 128, false, false);
		      } else {
		        sb.setColor(Color.WHITE);
		        sb.draw(__instance.img, __instance.currentX - 64.0F, __instance.currentY - 64.0F, 64.0F, 64.0F, 128.0F, 128.0F, __instance.scale, __instance.scale, rotation, 0, 0, 128, 128, false, false);
		        __instance.renderCounter(sb, false);
		      } 
		    } else {
		      sb.setColor(Color.WHITE);
		      sb.draw(__instance.img, __instance.currentX - 64.0F, __instance.currentY - 64.0F, 64.0F, 64.0F, 128.0F, 128.0F, __instance.scale, __instance.scale, rotation, 0, 0, 128, 128, false, false);
		      __instance.renderCounter(sb, false);
		    } 
		    if (__instance.isDone)
		      __instance.renderFlash(sb, false); 
		    __instance.hb.render(sb);
   		}
	}

	@SpirePatch(clz=AbstractBlight.class, method="bossObtainLogic")
	public static class whenBlightIsTouched
	{
		public static SpireReturn Prefix(AbstractBlight __instance)
		{
			if (TogetherManager.gameMode != TogetherManager.mode.Coop) { return SpireReturn.Continue(); }

			TogetherManager.log("bossObtainLogic clicked!"); 

			// Set your current choice
			CoopBossRelicSelectScreen t = TogetherManager.teamRelicScreen;

			for (int i = 0; i < t.blights.size(); i++) {
				if (t.blights.get(i).blightID.equals(__instance.blightID) && i != t.selectedIndex) {
					t.selected.get(i).add(TogetherManager.currentUser);

					if (t.selectedIndex != -1)
						t.selected.get(t.selectedIndex).remove(TogetherManager.currentUser);

					t.selectedIndex = i;
				}
			}

			// Share your choice with the world
			NetworkHelper.sendData(NetworkHelper.dataType.ChooseTeamRelic);

			// If everyone has chosen
			if (t.selected.get(t.selectedIndex).size() == TogetherManager.players.size()) {
				__instance.obtain();
				__instance.onEquip();
				__instance.isObtained = true;
				TogetherManager.teamRelicScreen.blightChoiceComplete();
			}


			return SpireReturn.Return(null);
		}
	}



	public void receiveStartAct() {
		// Creates the jank selection matrix
		for (ArrayList<RemotePlayer> a : this.selected) {
			a.clear();
		}

		refresh();
	}

	// Need to check if everyone has chosen every time we receive the data!

	public CoopBossRelicSelectScreen() {
		this.selected = new ArrayList();
		this.selected.add(new ArrayList());
		this.selected.add(new ArrayList());
	}


	public void update() {
		updateEffects();
		updateControllerInput();

		for (AbstractBlight b : this.blights) {
			b.update();
			if (b.isObtained)
				blightChoiceComplete();
		}

		if (isDone)
			this.blights.clear();
	}
	
	public void updateEffects() {
		this.shineTimer -= Gdx.graphics.getDeltaTime();
		if (this.shineTimer < 0.0F && !Settings.DISABLE_EFFECTS) {
			this.shineTimer = 0.1F;
			AbstractDungeon.topLevelEffects.add(new BossChestShineEffect());
			AbstractDungeon.topLevelEffects.add(new BossChestShineEffect(MathUtils.random(0.0F, Settings.WIDTH), MathUtils.random(0.0F, Settings.HEIGHT - 128.0F * Settings.scale)));
		} 
	}

	private void updateControllerInput() {
		if (!Settings.isControllerMode || AbstractDungeon.topPanel.selectPotionMode || !AbstractDungeon.topPanel.potionUi.isHidden || AbstractDungeon.player.viewingRelics)
			return; 
		boolean anyHovered = false;
		int index = 0;
		for (AbstractBlight b : this.blights) {
			if (b.hb.hovered) {
				anyHovered = true;
				break;
			} 
			index++;
		} 
		if (!anyHovered) {
				CInputHelper.setCursor(((AbstractBlight)this.blights.get(0)).hb);
		} else if (CInputActionSet.left.isJustPressed() || CInputActionSet.altLeft.isJustPressed() || CInputActionSet.right
			.isJustPressed() || CInputActionSet.altRight.isJustPressed()) {
			if (index == 0) {
				CInputHelper.setCursor(((AbstractBlight)this.blights.get(1)).hb);
			} else {
				CInputHelper.setCursor(((AbstractBlight)this.blights.get(0)).hb);
			} 
		} 
	}
	
	public void blightChoiceComplete() {
		TogetherManager.log("Blight has been obtained");

		TreasureRoomBoss curRoom = (TreasureRoomBoss)AbstractDungeon.getCurrRoom();
		curRoom.choseRelic = true;

		this.isDone = true;

		(AbstractDungeon.getCurrRoom()).phase = AbstractRoom.RoomPhase.COMPLETE;
		if (AbstractDungeon.screen != AbstractDungeon.CurrentScreen.COMBAT_REWARD && AbstractDungeon.screen != AbstractDungeon.CurrentScreen.GRID) {
			(AbstractDungeon.getCurrRoom()).rewardPopOutTimer = 99999.0F;
			AbstractDungeon.closeCurrentScreen();

	        AbstractDungeon.isScreenUp = false;
	        AbstractDungeon.overlayMenu.hideBlackScreen();
			AbstractDungeon.overlayMenu.proceedButton.show();
		}

		for (ArrayList<RemotePlayer> a : this.selected) {
			a.clear();
		}
	}
				
	public void render(SpriteBatch sb) {
		for (AbstractGameEffect e : AbstractDungeon.effectList)
			e.render(sb); 

		((TreasureRoomBoss)AbstractDungeon.getCurrRoom()).chest.render(sb);
		AbstractDungeon.player.render(sb);

		sb.setColor(Color.WHITE);
		sb.draw(this.smokeImg, Settings.WIDTH / 2.0F - 490.0F * Settings.scale, AbstractDungeon.floorY - 58.0F * Settings.scale, this.smokeImg				
				.getWidth() * Settings.scale, this.smokeImg
				.getHeight() * Settings.scale);

		for (int i = 0; i < blights.size(); i++) {
			if (i == selectedIndex)
				blights.get(i).renderOutline(Color.GOLD.cpy(), sb, false);

			blights.get(i).render(sb);

			int j = 0;
			for (RemotePlayer p : selected.get(i)) {
				FontHelper.renderFontLeftDownAligned(sb, FontHelper.topPanelInfoFont, p.userName, 
						blights.get(i).currentX + 48.0F * Settings.xScale, blights.get(i).currentY + (32.0F * Settings.xScale) - (j * 24f), 
						Color.WHITE);
				j++;
			}
		}

		TopPanelPlayerPanels.renderWidgets(sb);
	}
		
	public void reopen() {
		AbstractDungeon.dynamicBanner.appearInstantly(BANNER_Y, SELECT_MSG);
		AbstractDungeon.screen = CoopBossRelicSelectScreen.Enum.TEAMRELIC;
		AbstractDungeon.overlayMenu.cancelButton.hideInstantly();
		AbstractDungeon.overlayMenu.proceedButton.hideInstantly();
		AbstractDungeon.overlayMenu.showBlackScreen();
	}
	
	public void open(ArrayList<AbstractBlight> chosenBlights) {
		this.blights.clear();
		AbstractDungeon.dynamicBanner.appear(BANNER_Y, CardCrawlGame.languagePack.getUIString("TeamRelic").TEXT[0]);
		this.smokeImg = ImageMaster.BOSS_CHEST_SMOKE;
		AbstractDungeon.isScreenUp = true;
		AbstractDungeon.screen = CoopBossRelicSelectScreen.Enum.TEAMRELIC;
		AbstractDungeon.overlayMenu.cancelButton.hideInstantly();
		AbstractDungeon.overlayMenu.proceedButton.hideInstantly();
		AbstractDungeon.overlayMenu.showBlackScreen();

		// Spawn in the blights
		AbstractBlight r2 = chosenBlights.get(0);
		r2.spawn(this.B_SLOT_1_X, this.B_SLOT_1_Y);
		r2.hb.move(r2.currentX, r2.currentY);
		this.blights.add(r2);
		AbstractBlight r3 = chosenBlights.get(1);
		r3.spawn(this.B_SLOT_2_X, this.B_SLOT_1_Y);
		r3.hb.move(r3.currentX, r3.currentY);
		this.blights.add(r3);

		selectedIndex = -1;
	}
		
	public void refresh() {
		TogetherManager.log("Refreshing? " + isDone);
		for (ArrayList<RemotePlayer> a : this.selected) {
			a.clear();
		}
		this.isDone = false;
		this.shineTimer = 0.0F;
	}
	
	public void hide() {
		AbstractDungeon.dynamicBanner.hide();
		AbstractDungeon.overlayMenu.cancelButton.hide();
	}
}
