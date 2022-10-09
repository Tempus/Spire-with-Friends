package chronoMods.coop.drawable;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.MathHelper;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;

import java.text.DecimalFormat;

public class Slider {
	private int BG_W = 250;
	private int BG_H = 24;
	private int S_W = 44;
	private float SLIDE_W = 230.0F * Settings.xScale;
	private float BG_X = 1350.0F * Settings.xScale;
	private float L_X = 1235.0F * Settings.xScale;
	public float leftbound, sliderx;
    private Color WHITE = new Color(1.0F, 1.0F, 1.0F, 1.0F);

	private float x;
	private float y;
	
	private float value;
	
	public Hitbox hb;
	public Hitbox bgHb;

	public float lowRange = 0;
	public float highRange = 100;
	public float alpha = 1f;
	
	private boolean sliderGrabbed = false;
	
	private static DecimalFormat df = new DecimalFormat("#");
	public static final String[] TEXT = CardCrawlGame.languagePack.getUIString("Canvas").TEXT;

	public Slider(float x, float y, float value) {
		this.y = y;

		L_X = x;
		BG_X = L_X + SLIDE_W / 2f;

		this.value = value;
		this.hb = new Hitbox(42.0F * Settings.scale, 38.0F * Settings.scale);
		this.bgHb = new Hitbox(256.0F * Settings.scale, 38.0F * Settings.scale);

	    this.bgHb.move(BG_X, y);
	    this.x = L_X + SLIDE_W * value;
   	}
	
	public void update() {
		this.hb.update();
		this.bgHb.update();
	    this.hb.move(this.x, this.y);
	    if (this.sliderGrabbed) {
	      if (InputHelper.isMouseDown) {
	        this.x = MathHelper.fadeLerpSnap(this.x, InputHelper.mX);
	        if (this.x < L_X) {
	          this.x = L_X;
	        } else if (this.x > L_X + SLIDE_W) {
	          this.x = L_X + SLIDE_W;
	        } 
	        this.value = (this.x - L_X) / SLIDE_W;
   			} else {
				CardCrawlGame.sound.play("UI_CLICK_1");
				this.sliderGrabbed = false;
			} 
	    } else if (InputHelper.justClickedLeft) {
	      if (this.hb.hovered) {
	        this.sliderGrabbed = true;
	      } else if (this.bgHb.hovered) {
	        this.sliderGrabbed = true;
	      } 
	    } 
	    if (Settings.isControllerMode && 
	      this.bgHb.hovered)
	      if (CInputActionSet.inspectLeft.isJustPressed()) {
	        this.x -= 5.0F * Settings.scale;
	        if (this.x < L_X)
	          this.x = L_X; 
	        this.value = (this.x - L_X) / SLIDE_W;
	      } else if (CInputActionSet.inspectRight.isJustPressed()) {
	        this.x += 5.0F * Settings.scale;
	        if (this.x > L_X + SLIDE_W)
	          this.x = L_X + SLIDE_W; 
	        this.value = (this.x - L_X) / SLIDE_W;
	      }  
	}
	
	public void setRange(float low, float high) {
		this.lowRange = low;
		this.highRange = high;
	}

	public float getValue() {
		return this.value * (highRange-lowRange) + lowRange;
	}
	
	public void render(SpriteBatch sb) {
	    WHITE.a = alpha;
	    sb.setColor(WHITE);

	    if (Settings.isControllerMode && this.bgHb.hovered)
	      sb.draw(ImageMaster.CONTROLLER_RS, this.bgHb.cX + 195.0F * Settings.scale, this.bgHb.cY - 46.0F * Settings.scale, 32.0F, 32.0F, 64.0F, 64.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 64, 64, false, false); 
	    sb.draw(ImageMaster.OPTION_SLIDER_BG, BG_X - 125.0F, this.y - 12.0F, 125.0F, 12.0F, 250.0F, 24.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 250, 24, false, false);
	  //   if (this.sliderGrabbed) {
			// FontHelper.renderFontCentered(sb, FontHelper.tipBodyFont, df.format((this.value * (highRange-lowRange) + lowRange)), BG_X + 170.0F * Settings.scale, this.y, Settings.GREEN_TEXT_COLOR);
	  //   } else {
			// FontHelper.renderFontCentered(sb, FontHelper.tipBodyFont, df.format((this.value * (highRange-lowRange) + lowRange)), BG_X + 170.0F * Settings.scale, this.y, Settings.BLUE_TEXT_COLOR);
	  //   } 
	    sb.draw(ImageMaster.OPTION_SLIDER, this.x - 22.0F, this.y - 22.0F, 22.0F, 22.0F, 44.0F, 44.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 44, 44, false, false);
	    this.hb.render(sb);
	    this.bgHb.render(sb);	    

	    sb.setColor(Color.WHITE);
	}
}
