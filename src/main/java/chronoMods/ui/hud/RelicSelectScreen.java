package chronoMods.ui.hud;

import chronoMods.TogetherManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import java.util.ArrayList;

public class RelicSelectScreen
{
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString("RelicSelectScreen");
    public static final String[] TEXT = uiStrings.TEXT;
    private static final float SPACE = 80.0F * Settings.scale;
    private static final float START_X = 600.0F * Settings.scale;
    private static final float START_Y = Settings.HEIGHT - 480.0F * Settings.scale;
    private int row = 0;
    private int col = 0;
    private static final Color RED_OUTLINE_COLOR = new Color(-10132568);
    private static final Color GREEN_OUTLINE_COLOR = new Color(2147418280);
    private static final Color BLUE_OUTLINE_COLOR = new Color(-2016482392);
    private static final Color BLACK_OUTLINE_COLOR = new Color(168);
    private AbstractRelic hoveredRelic = null;
    private AbstractRelic clickStartedRelic = null;
    private Hitbox controllerRelicHb = null;

    private ArrayList<AbstractRelic> relics;
    private boolean show = false;
    public int selectCount = 1;
    private ArrayList<AbstractRelic> selectedRelics = new ArrayList<>();

    public boolean doneSelecting()
    {
        return selectedRelics.size() >= selectCount;
    }

    public ArrayList<AbstractRelic> getSelectedRelics()
    {
        ArrayList<AbstractRelic> ret = new ArrayList<>(selectedRelics);
        selectedRelics.clear();
        return ret;
    }

    public RelicSelectScreen()
    {
    }

    public void open(ArrayList<AbstractRelic> relics)
    {
        AbstractDungeon.isScreenUp = true;
        AbstractDungeon.overlayMenu.showBlackScreen(0.5f);
        AbstractDungeon.overlayMenu.proceedButton.hide();
        show = true;

        controllerRelicHb = null;
        this.relics = relics;

        selectedRelics.clear();
    }

    public void close()
    {
        AbstractDungeon.screen = AbstractDungeon.CurrentScreen.GRID;
        AbstractDungeon.closeCurrentScreen();
        show = false;
    }

    public boolean isOpen()
    {
        return show;
    }

    public void update()
    {
        if (!isOpen()) {
            return;
        }

        if (hoveredRelic != null) {
            if (InputHelper.justClickedLeft || CInputActionSet.select.isJustPressed()) {
                clickStartedRelic = hoveredRelic;
            }
            if (InputHelper.justReleasedClickLeft || CInputActionSet.select.isJustPressed())
            {
                CInputActionSet.select.unpress();
                if (hoveredRelic == clickStartedRelic)
                {
                    if (selectedRelics.contains(hoveredRelic)) {
                        selectedRelics.remove(hoveredRelic);
                    } else {
                        selectedRelics.add(hoveredRelic);
                    }
    
                    clickStartedRelic = null;

                    if (doneSelecting()) {
                        close();
                    }
                }
            }

            if (InputHelper.justClickedRight || CInputActionSet.select.isJustPressed()) {
                clickStartedRelic = hoveredRelic;
            }
            if (InputHelper.justReleasedClickRight || CInputActionSet.select.isJustPressed())
            {
                CInputActionSet.select.unpress();
                if (hoveredRelic == clickStartedRelic)
                {
                    CardCrawlGame.relicPopup.open(hoveredRelic, relics);
                    clickStartedRelic = null;
                }
            }
        } else {
            clickStartedRelic = null;
        }
        InputHelper.justClickedLeft = false;
        InputHelper.justClickedRight = false;

        hoveredRelic = null;
        updateList(relics);
        if (Settings.isControllerMode && controllerRelicHb != null) {
            Gdx.input.setCursorPosition((int)controllerRelicHb.cX, (int)(Settings.HEIGHT - controllerRelicHb.cY));
        }
    }

    private void updateList(ArrayList<AbstractRelic> list)
    {
        for (AbstractRelic r : list)
        {
            r.hb.move(r.currentX, r.currentY);
            r.update();
            if (r.hb.hovered)
            {
                hoveredRelic = r;
            }
        }
    }

    public void render(SpriteBatch sb)
    {
        if (!isOpen()) {
            return;
        }

        FontHelper.renderFontCentered(sb, FontHelper.smallDialogOptionFont, 
                    TEXT[0] + Integer.toString(selectCount) + TEXT[1],
                    Settings.WIDTH / 2f, START_Y + 64.0f * Settings.scale, Color.WHITE.cpy(), 1.0f);

        row = -1;
        col = 0;
        renderList(sb, relics);
    }

    private void renderList(SpriteBatch sb, ArrayList<AbstractRelic> list)
    {
        TogetherManager.log("RenderList: " + list.size());
        row += 1;
        col = 0;
        for (AbstractRelic r : list) {
            if (col == 10) {
                col = 0;
                row += 1;
            }
            r.currentX = (START_X + SPACE * col);
            r.currentY = (START_Y - SPACE * row);

            if (selectedRelics.contains(r))
                r.render(sb, false, Color.GOLD.cpy());
            else
                r.render(sb, false, BLACK_OUTLINE_COLOR);

            col += 1;
        }
    }
}