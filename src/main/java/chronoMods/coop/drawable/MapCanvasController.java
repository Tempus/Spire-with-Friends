package chronoMods.coop.drawable;

import chronoMods.TogetherManager;
import chronoMods.network.NetworkHelper;
import chronoMods.network.RemotePlayer;
import chronoMods.ui.lobby.ToggleWidget;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;

import java.util.ArrayList;

public class MapCanvasController {

    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString("Canvas").TEXT;
    private static final float TOOLTIP_Y_OFFSET = 128.0F * Settings.scale;

	public Button clearButton;
    public ToggleWidget hideToggle;
    public Slider brushSlider;
	public ArrayList<HideButton> hideButtons = new ArrayList();

    boolean hidden;

    public float x = (1550f) * Settings.scale;


	public MapCanvasController() {
		// Button Setup
		clearButton = new Button(Settings.WIDTH / 2.0F - 268.0F * Settings.xScale, 24f * Settings.yScale, "", ImageMaster.loadImage("chrono/images/delete_button.png"));

        brushSlider = new Slider(Settings.WIDTH / 2.0F - 230.0F * Settings.xScale, 24f * Settings.yScale, (TogetherManager.getCurrentUser().drawable[0].brushSize - 2f) / 10f);
        brushSlider.setRange(2f, 16f);

        // hideToggle = new ToggleWidget(Settings.WIDTH / 2.0F + 156.0F, 24f, TEXT[2], false);

        int i = 0;
        for (RemotePlayer p : TogetherManager.players) {
			hideButtons.add(new HideButton(Settings.WIDTH / 2.0F + 42.0F * Settings.xScale + i * 42f * Settings.xScale, 24f, p));
			i++;
		}
	}

	/** Write the pixmap onto the texture if the pixmap has changed. */
	public void update() {

		if (AbstractDungeon.screen != AbstractDungeon.CurrentScreen.MAP) { return; }

		// Clear Button
	    clearButton.update();
	    if (this.clearButton.hb.clicked || CInputActionSet.proceed.isJustPressed()) {
	        this.clearButton.hb.clicked = false;
	        TogetherManager.getCurrentUser().drawable[AbstractDungeon.actNum-1].clear();
			NetworkHelper.sendData(NetworkHelper.dataType.ClearMap);
	    }
        if (clearButton.hb.hovered && !hidden) {
            TipHelper.renderGenericTip(this.clearButton.hb.cX - 320.0F * Settings.scale / 2f, brushSlider.hb.cY + TOOLTIP_Y_OFFSET, TEXT[0], TEXT[1]); }

        // Hide Toggle
        // if (hideToggle.update()) {
        // 	hidden = hideToggle.isTicked();
        // 	for (RemotePlayer p : TogetherManager.players)
        // 		for (MapCanvas m : p.drawable)
	       //  		m.hidden = hidden;
        // }
        // if (this.hideToggle.hb.hovered && !hidden) {
        //     TipHelper.renderGenericTip(this.hideToggle.hb.cX * 0.95f, this.hideToggle.hb.cY + TOOLTIP_Y_OFFSET, TEXT[2], TEXT[3]); }
	
        // Brush Size Slider
        brushSlider.update();
        if (brushSlider.bgHb.hovered && !hidden) {
            TipHelper.renderGenericTip(brushSlider.bgHb.cX - 320.0F * Settings.scale / 2f, brushSlider.bgHb.cY + TOOLTIP_Y_OFFSET + 26f * Settings.scale, TEXT[5], TEXT[6]); }

		for (MapCanvas m : TogetherManager.getCurrentUser().drawable) {
			m.brushSize = brushSlider.getValue();
		}

		// Hide Buttons
		for (HideButton b : hideButtons) {
			b.update();
	        if (b.hb.hovered && !hidden) {
	            TipHelper.renderGenericTip(b.hb.cX - 320.0F * Settings.scale / 2f, b.hb.cY + TOOLTIP_Y_OFFSET, TEXT[2], TEXT[3] + b.player.userName + TEXT[4]); }
		}
	}

	public void render(SpriteBatch sb, float a) {
		if (AbstractDungeon.screen != AbstractDungeon.CurrentScreen.MAP) { return; }

		sb.setColor(Color.WHITE);
		sb.draw(ImageMaster.VICTORY_BANNER, Settings.WIDTH / 2.0F - 556.0F, -128f, 556.0F, 119.0F, 1112.0F, 238.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 1112, 238, false, false);

		// hideToggle.alpha = a;
		clearButton.alpha = a;
		brushSlider.alpha = a;

		// hideToggle.render(sb);
		clearButton.render(sb);
        brushSlider.render(sb);

		for (HideButton b : hideButtons) {
			b.alpha = a;
			b.render(sb);
		}
	}
}