package chronoMods.ui.hud;

import chronoMods.TogetherManager;
import chronoMods.network.RemotePlayer;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.GameCursor;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.MathHelper;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.screens.MasterDeckSortHeader;
import com.megacrit.cardcrawl.screens.MasterDeckViewScreen;
import com.megacrit.cardcrawl.screens.mainMenu.ScrollBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

// The fact that the easiest way to implement this shit is to make a FIFTH copy in this game is a testament to spaghetti

public class PlayerDeckViewScreen extends MasterDeckViewScreen {
  private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString("MasterDeckViewScreen");
  
  public static final String[] TEXT = uiStrings.TEXT;
  
  private static float drawStartX;
  
  private static float drawStartY = Settings.HEIGHT * 0.66F;
  
  private static float padX;
  
  private static float padY;
  
  private static final float SCROLL_BAR_THRESHOLD = 500.0F * Settings.scale;
  
  private static final int CARDS_PER_LINE = 5;
  
  private boolean grabbedScreen = false;
  
  private float grabStartY = 0.0F, currentDiffY = 0.0F;
  
  private float scrollLowerBound = -Settings.DEFAULT_SCROLL_LIMIT;
  
  private float scrollUpperBound = Settings.DEFAULT_SCROLL_LIMIT;
  
  private static final String HEADER_INFO = TEXT[0];
  
  private AbstractCard hoveredCard = null;
  
  private AbstractCard clickStartedCard = null;
  
  private int prevDeckSize = 0;
  
  private ScrollBar scrollBar;
  
  private AbstractCard controllerCard = null;
  
  private MasterDeckSortHeader sortHeader;
  
  private int headerIndex = -1;
  
  private Comparator<AbstractCard> sortOrder = null;
  
  private ArrayList<AbstractCard> tmpSortedDeck = null;
  
  private float tmpHeaderPosition = Float.NEGATIVE_INFINITY;
  
  private int headerScrollLockRemainingFrames = 0;
  
  private boolean justSorted = false;

  RemotePlayer p;
  

  // Why doesn't basemods have ways to register custom screens?
	public static class Enum
	{
		@SpireEnum
		public static AbstractDungeon.CurrentScreen PLAYERDECK;
	}

	@SpirePatch(clz=AbstractDungeon.class, method="update")
	public static class Update
	{
		public static void Postfix(AbstractDungeon __instance)
		{
			if (__instance.screen == PlayerDeckViewScreen.Enum.PLAYERDECK) {
				TogetherManager.playerDeckViewScreen.update();
			}
		}
	}

	@SpirePatch(clz=AbstractDungeon.class, method="render")
	public static class Render
	{
		@SpireInsertPatch(rloc=2773-2658,localvars={})
		public static void Insert(AbstractDungeon __instance, SpriteBatch sb)
		{
			if (__instance.screen == PlayerDeckViewScreen.Enum.PLAYERDECK) {
				TogetherManager.playerDeckViewScreen.render(sb);
			}
		}
	}

	@SpirePatch(clz=AbstractDungeon.class, method="closeCurrentScreen")
	public static class Close
	{
	    public static void Prefix()
	    {
	        if (AbstractDungeon.screen == PlayerDeckViewScreen.Enum.PLAYERDECK) {
		        AbstractDungeon.overlayMenu.cancelButton.hide();
		        AbstractDungeon.overlayMenu.hideBlackScreen();
	        }
					AbstractDungeon.isScreenUp = false;

			    if ((AbstractDungeon.getCurrRoom()).phase == AbstractRoom.RoomPhase.COMBAT && !AbstractDungeon.player.isDead)
			      AbstractDungeon.overlayMenu.showCombatPanels(); 
			}
	}




  public PlayerDeckViewScreen() {
    drawStartX = Settings.WIDTH;
    drawStartX -= 5.0F * AbstractCard.IMG_WIDTH * 0.75F;
    drawStartX -= 4.0F * Settings.CARD_VIEW_PAD_X;
    drawStartX /= 2.0F;
    drawStartX += AbstractCard.IMG_WIDTH * 0.75F / 2.0F;
    padX = AbstractCard.IMG_WIDTH * 0.75F + Settings.CARD_VIEW_PAD_X;
    padY = AbstractCard.IMG_HEIGHT * 0.75F + Settings.CARD_VIEW_PAD_Y;
    this.scrollBar = new ScrollBar(this);
    this.scrollBar.move(0.0F, -30.0F * Settings.scale);
    this.sortHeader = new MasterDeckSortHeader(this);
  }
  
  public void update() {
    updateControllerInput();
    if (Settings.isControllerMode && this.controllerCard != null && !CardCrawlGame.isPopupOpen && !AbstractDungeon.topPanel.selectPotionMode)
      if (Gdx.input.getY() > Settings.HEIGHT * 0.7F) {
        this.currentDiffY += Settings.SCROLL_SPEED;
      } else if (Gdx.input.getY() < Settings.HEIGHT * 0.3F) {
        this.currentDiffY -= Settings.SCROLL_SPEED;
      }  
    boolean isDraggingScrollBar = false;
    if (shouldShowScrollBar())
      isDraggingScrollBar = this.scrollBar.update(); 
    if (!isDraggingScrollBar)
      updateScrolling(); 
    updatePositions();
    this.sortHeader.update();
    updateClicking();
    if (Settings.isControllerMode && this.controllerCard != null)
      Gdx.input.setCursorPosition((int)this.controllerCard.hb.cX, (int)(Settings.HEIGHT - this.controllerCard.hb.cY)); 
  }
  
  private void updateControllerInput() {
    if (!Settings.isControllerMode || AbstractDungeon.topPanel.selectPotionMode)
      return; 
    CardGroup deck = p.deck;
    boolean anyHovered = false;
    int index = 0;
    if (this.tmpSortedDeck == null)
      this.tmpSortedDeck = deck.group; 
    for (AbstractCard c : this.tmpSortedDeck) {
      if (c.hb.hovered) {
        anyHovered = true;
        break;
      } 
      index++;
    } 
    anyHovered = (anyHovered || this.headerIndex >= 0);
    if (!anyHovered) {
      if (this.tmpSortedDeck.size() > 0) {
        Gdx.input.setCursorPosition((int)((AbstractCard)this.tmpSortedDeck.get(0)).hb.cX, (int)((AbstractCard)this.tmpSortedDeck.get(0)).hb.cY);
        this.controllerCard = this.tmpSortedDeck.get(0);
      } 
    } else if (this.headerIndex >= 0) {
      if (CInputActionSet.down.isJustPressed() || CInputActionSet.altDown.isJustPressed()) {
        this.sortHeader.selectionIndex = this.headerIndex = -1;
        this.controllerCard = this.tmpSortedDeck.get(0);
        Gdx.input.setCursorPosition(
            (int)((AbstractCard)this.tmpSortedDeck.get(0)).hb.cX, Settings.HEIGHT - 
            (int)((AbstractCard)this.tmpSortedDeck.get(0)).hb.cY);
      } else if (CInputActionSet.left.isJustPressed() || CInputActionSet.altLeft.isJustPressed()) {
        if (this.headerIndex > 0)
          selectSortButton(--this.headerIndex); 
      } else if ((CInputActionSet.right.isJustPressed() || CInputActionSet.altRight.isJustPressed()) && 
        this.headerIndex < this.sortHeader.buttons.length - 1) {
        selectSortButton(++this.headerIndex);
      } 
    } else if ((CInputActionSet.up.isJustPressed() || CInputActionSet.altUp.isJustPressed()) && deck
      .size() > 5) {
      index -= 5;
      if (index < 0) {
        selectSortButton(this.headerIndex = 0);
        return;
      } 
      Gdx.input.setCursorPosition(
          (int)((AbstractCard)this.tmpSortedDeck.get(index)).hb.cX, Settings.HEIGHT - 
          (int)((AbstractCard)this.tmpSortedDeck.get(index)).hb.cY);
      this.controllerCard = this.tmpSortedDeck.get(index);
    } else if ((CInputActionSet.down.isJustPressed() || CInputActionSet.altDown.isJustPressed()) && deck
      .size() > 5) {
      if (index < deck.size() - 5) {
        index += 5;
      } else {
        index %= 5;
      } 
      Gdx.input.setCursorPosition(
          (int)((AbstractCard)this.tmpSortedDeck.get(index)).hb.cX, Settings.HEIGHT - 
          (int)((AbstractCard)this.tmpSortedDeck.get(index)).hb.cY);
      this.controllerCard = this.tmpSortedDeck.get(index);
    } else if (CInputActionSet.left.isJustPressed() || CInputActionSet.altLeft.isJustPressed()) {
      if (index % 5 > 0) {
        index--;
      } else {
        index += 4;
        if (index > deck.size() - 1)
          index = deck.size() - 1; 
      } 
      Gdx.input.setCursorPosition(
          (int)((AbstractCard)this.tmpSortedDeck.get(index)).hb.cX, Settings.HEIGHT - 
          (int)((AbstractCard)this.tmpSortedDeck.get(index)).hb.cY);
      this.controllerCard = this.tmpSortedDeck.get(index);
    } else if (CInputActionSet.right.isJustPressed() || CInputActionSet.altRight.isJustPressed()) {
      if (index % 5 < 4) {
        index++;
        if (index > deck.size() - 1)
          index -= deck.size() % 5; 
      } else {
        index -= 4;
        if (index < 0)
          index = 0; 
      } 
      Gdx.input.setCursorPosition(
          (int)((AbstractCard)this.tmpSortedDeck.get(index)).hb.cX, Settings.HEIGHT - 
          (int)((AbstractCard)this.tmpSortedDeck.get(index)).hb.cY);
      this.controllerCard = this.tmpSortedDeck.get(index);
    } 
  }
  
  public void open(RemotePlayer p) {
  	this.p = p;

    if (Settings.isControllerMode) {
      Gdx.input.setCursorPosition(10, Settings.HEIGHT / 2);
      this.controllerCard = null;
    } 
    AbstractDungeon.player.releaseCard();
    CardCrawlGame.sound.play("DECK_OPEN");
    this.currentDiffY = this.scrollLowerBound;
    this.grabStartY = this.scrollLowerBound;
    this.grabbedScreen = false;
    hideCards();
    AbstractDungeon.dynamicBanner.hide();
    AbstractDungeon.isScreenUp = true;
    AbstractDungeon.screen = PlayerDeckViewScreen.Enum.PLAYERDECK;
    AbstractDungeon.overlayMenu.proceedButton.hide();
    AbstractDungeon.overlayMenu.hideCombatPanels();
    AbstractDungeon.overlayMenu.showBlackScreen();
    AbstractDungeon.overlayMenu.cancelButton.show(TEXT[1]);
    calculateScrollBounds();
  }
  
  private void updatePositions() {
    this.hoveredCard = null;
    int lineNum = 0;
    ArrayList<AbstractCard> cards = p.deck.group;
    if (this.sortOrder != null) {
      cards = new ArrayList<>(cards);
      Collections.sort(cards, this.sortOrder);
      this.tmpSortedDeck = cards;
    } else {
      this.tmpSortedDeck = null;
    } 
    if (this.justSorted && this.headerScrollLockRemainingFrames <= 0) {
      AbstractCard abstractCard = highestYPosition(cards);
      if (abstractCard != null)
        this.tmpHeaderPosition = abstractCard.current_y; 
    } 
    for (int i = 0; i < cards.size(); i++) {
      int mod = i % 5;
      if (mod == 0 && i != 0)
        lineNum++; 
      ((AbstractCard)cards.get(i)).target_x = drawStartX + mod * padX;
      ((AbstractCard)cards.get(i)).target_y = drawStartY + this.currentDiffY - lineNum * padY;
      ((AbstractCard)cards.get(i)).update();
      ((AbstractCard)cards.get(i)).updateHoverLogic();
      if (((AbstractCard)cards.get(i)).hb.hovered)
        this.hoveredCard = cards.get(i); 
    } 
    AbstractCard c = highestYPosition(cards);
    if (this.justSorted && c != null) {
      int lerps = 0;
      float lerpY = c.current_y, lerpTarget = c.target_y;
      while (lerpY != lerpTarget) {
        lerpY = MathHelper.cardLerpSnap(lerpY, lerpTarget);
        lerps++;
      } 
      this.headerScrollLockRemainingFrames = lerps;
    } 
    this.headerScrollLockRemainingFrames -= Settings.FAST_MODE ? 2 : 1;
    if (cards.size() > 0 && this.sortHeader != null && c != null) {
      this.sortHeader.updateScrollPosition((this.headerScrollLockRemainingFrames <= 0) ? c.current_y : this.tmpHeaderPosition);
      this.justSorted = false;
    } 
  }
  
  private AbstractCard highestYPosition(List<AbstractCard> cards) {
    if (cards == null)
      return null; 
    float highestY = 0.0F;
    AbstractCard retVal = null;
    for (AbstractCard card : cards) {
      if (card.current_y > highestY) {
        highestY = card.current_y;
        retVal = card;
      } 
    } 
    return retVal;
  }
  
  private void updateScrolling() {
    int y = InputHelper.mY;
    if (!this.grabbedScreen) {
      if (InputHelper.scrolledDown) {
        this.currentDiffY += Settings.SCROLL_SPEED;
      } else if (InputHelper.scrolledUp) {
        this.currentDiffY -= Settings.SCROLL_SPEED;
      } 
      if (InputHelper.justClickedLeft) {
        this.grabbedScreen = true;
        this.grabStartY = y - this.currentDiffY;
      } 
    } else if (InputHelper.isMouseDown) {
      this.currentDiffY = y - this.grabStartY;
    } else {
      this.grabbedScreen = false;
    } 
    if (this.prevDeckSize != p.deck.size())
      calculateScrollBounds(); 
    resetScrolling();
    updateBarPosition();
  }
  
  private void updateClicking() {
    if (this.hoveredCard != null) {
      CardCrawlGame.cursor.changeType(GameCursor.CursorType.INSPECT);
      if (InputHelper.justClickedLeft)
        this.clickStartedCard = this.hoveredCard; 
      if (((InputHelper.justReleasedClickLeft && this.hoveredCard == this.clickStartedCard) || CInputActionSet.select
        .isJustPressed()) && 
        this.headerIndex < 0) {
        InputHelper.justReleasedClickLeft = false;
        CardCrawlGame.cardPopup.open(this.hoveredCard, p.deck);
        this.clickStartedCard = null;
      } 
    } else {
      this.clickStartedCard = null;
    } 
  }
  
  private void calculateScrollBounds() {
    if (p.deck.size() > 10) {
      int scrollTmp = p.deck.size() / 5 - 2;
      if (p.deck.size() % 5 != 0)
        scrollTmp++; 
      this.scrollUpperBound = Settings.DEFAULT_SCROLL_LIMIT + scrollTmp * padY;
    } else {
      this.scrollUpperBound = Settings.DEFAULT_SCROLL_LIMIT;
    } 
    this.prevDeckSize = p.deck.size();
  }
  
  private void resetScrolling() {
    if (this.currentDiffY < this.scrollLowerBound) {
      this.currentDiffY = MathHelper.scrollSnapLerpSpeed(this.currentDiffY, this.scrollLowerBound);
    } else if (this.currentDiffY > this.scrollUpperBound) {
      this.currentDiffY = MathHelper.scrollSnapLerpSpeed(this.currentDiffY, this.scrollUpperBound);
    } 
  }
  
  private void hideCards() {
    int lineNum = 0;
    ArrayList<AbstractCard> cards = p.deck.group;
    for (int i = 0; i < cards.size(); i++) {
      int mod = i % 5;
      if (mod == 0 && i != 0)
        lineNum++; 
      ((AbstractCard)cards.get(i)).current_x = drawStartX + mod * padX;
      ((AbstractCard)cards.get(i)).current_y = drawStartY + this.currentDiffY - lineNum * padY - MathUtils.random(100.0F * Settings.scale, 200.0F * Settings.scale);
      ((AbstractCard)cards.get(i)).targetDrawScale = 0.75F;
      ((AbstractCard)cards.get(i)).drawScale = 0.75F;
      ((AbstractCard)cards.get(i)).setAngle(0.0F, true);
    } 
  }
  
  public void render(SpriteBatch sb) {
    if (shouldShowScrollBar())
      this.scrollBar.render(sb); 
    if (this.hoveredCard == null) {
      p.deck.renderMasterDeck(sb);
    } else {
      p.deck.renderMasterDeckExceptOneCard(sb, this.hoveredCard);
      this.hoveredCard.renderHoverShadow(sb);
      this.hoveredCard.render(sb);
      if (this.hoveredCard.inBottleFlame) {
        AbstractRelic tmp = RelicLibrary.getRelic("Bottled Flame");
        float prevX = tmp.currentX;
        float prevY = tmp.currentY;
        tmp.currentX = this.hoveredCard.current_x + 130.0F * Settings.scale;
        tmp.currentY = this.hoveredCard.current_y + 182.0F * Settings.scale;
        tmp.scale = this.hoveredCard.drawScale * Settings.scale * 1.5F;
        tmp.render(sb);
        tmp.currentX = prevX;
        tmp.currentY = prevY;
        tmp = null;
      } else if (this.hoveredCard.inBottleLightning) {
        AbstractRelic tmp = RelicLibrary.getRelic("Bottled Lightning");
        float prevX = tmp.currentX;
        float prevY = tmp.currentY;
        tmp.currentX = this.hoveredCard.current_x + 130.0F * Settings.scale;
        tmp.currentY = this.hoveredCard.current_y + 182.0F * Settings.scale;
        tmp.scale = this.hoveredCard.drawScale * Settings.scale * 1.5F;
        tmp.render(sb);
        tmp.currentX = prevX;
        tmp.currentY = prevY;
        tmp = null;
      } else if (this.hoveredCard.inBottleTornado) {
        AbstractRelic tmp = RelicLibrary.getRelic("Bottled Tornado");
        float prevX = tmp.currentX;
        float prevY = tmp.currentY;
        tmp.currentX = this.hoveredCard.current_x + 130.0F * Settings.scale;
        tmp.currentY = this.hoveredCard.current_y + 182.0F * Settings.scale;
        tmp.scale = this.hoveredCard.drawScale * Settings.scale * 1.5F;
        tmp.render(sb);
        tmp.currentX = prevX;
        tmp.currentY = prevY;
        tmp = null;
      } 
    } 
    p.deck.renderTip(sb);
    FontHelper.renderDeckViewTip(sb, HEADER_INFO, 96.0F * Settings.scale, Settings.CREAM_COLOR);
    this.sortHeader.render(sb);
  }
  
  public void scrolledUsingBar(float newPercent) {
    this.currentDiffY = MathHelper.valueFromPercentBetween(this.scrollLowerBound, this.scrollUpperBound, newPercent);
    updateBarPosition();
  }
  
  private void updateBarPosition() {
    float percent = MathHelper.percentFromValueBetween(this.scrollLowerBound, this.scrollUpperBound, this.currentDiffY);
    this.scrollBar.parentScrolledToPercent(percent);
  }
  
  private boolean shouldShowScrollBar() {
    return (this.scrollUpperBound > SCROLL_BAR_THRESHOLD);
  }
  
  public void setSortOrder(Comparator<AbstractCard> sortOrder) {
    if (this.sortOrder != sortOrder)
      this.justSorted = true; 
    this.sortOrder = sortOrder;
  }
  
  private void selectSortButton(int index) {
    Hitbox hb = (this.sortHeader.buttons[this.headerIndex]).hb;
    Gdx.input.setCursorPosition((int)hb.cX, Settings.HEIGHT - (int)hb.cY);
    this.controllerCard = null;
    this.sortHeader.selectionIndex = this.headerIndex;
  }
}
