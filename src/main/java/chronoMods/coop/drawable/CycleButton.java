package chronoMods.coop.drawable;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.input.InputHelper;

import java.util.ArrayList;

public class CycleButton {
  private int W = 512;
  
  private int H = 256;
  
  private Color HOVER_BLEND_COLOR = new Color(1.0F, 1.0F, 1.0F, 0.4F);
  
  private Color TEXT_DISABLED_COLOR = new Color(0.6F, 0.6F, 0.6F, 1.0F);
  
  public float SHOW_X = Settings.WIDTH - 256.0F * Settings.scale, DRAW_Y = 128.0F * Settings.scale;
  
  private float HIDE_X = SHOW_X + 400.0F * Settings.scale;
  
  private float current_x = HIDE_X;
  
  private float target_x = this.current_x;
      
  public boolean isDisabled = false;
  
  public boolean isHovered = false;
    
  private String buttonText = "NOT_SET";
  
  private float TEXT_OFFSET_X = 0.0F * Settings.scale;
  
  private float TEXT_OFFSET_Y = 0.0F * Settings.scale;
    
  public Hitbox hb;
  
  public float alpha = 1f;

  public ArrayList<Texture> images = new ArrayList();
  public int index;

  public CycleButton(float x, float y, String label) {
    updateText(label);
    SHOW_X = x;
    current_x = SHOW_X;
    target_x = this.current_x;

    DRAW_Y = y;
  }
  
  public void addOption(Texture image) {
    this.images.add(image);

    this.hb = new Hitbox(0.0F, 0.0F, image.getWidth(), image.getHeight());
    this.hb.move(SHOW_X, DRAW_Y);
  }

  public void move(float x, float y) {
    SHOW_X = x;
    DRAW_Y = y;
    this.hb.move(SHOW_X, DRAW_Y);
  }

  public void updateText(String label) {
    this.buttonText = label;
  }
  
  public void update() {
      this.hb.update();
      if (InputHelper.justClickedLeft && this.hb.hovered && !this.isDisabled) {
        this.hb.clickStarted = true;
        CardCrawlGame.sound.play("UI_CLICK_1");
        index++;
        if (index >= images.size())
          index = 0;
      } 
      if (this.hb.justHovered && !this.isDisabled)
        CardCrawlGame.sound.play("UI_HOVER"); 
      this.isHovered = this.hb.hovered;
  }
      
  public void render(SpriteBatch sb) {
    Color c = new Color(1f,1f,1f,alpha);

    sb.setColor(c);
    renderButton(sb);
    if (this.hb.hovered && !this.isDisabled && !this.hb.clickStarted) {
      sb.setBlendFunction(770, 1);
      sb.setColor(HOVER_BLEND_COLOR);
      renderButton(sb);
      sb.setBlendFunction(770, 771);
    } 
    if (this.isDisabled) {
      FontHelper.renderFontCentered(sb, FontHelper.buttonLabelFont, this.buttonText, SHOW_X + TEXT_OFFSET_X, DRAW_Y + TEXT_OFFSET_Y, TEXT_DISABLED_COLOR);
    } else if (this.hb.clickStarted) {
      FontHelper.renderFontCentered(sb, FontHelper.buttonLabelFont, this.buttonText, SHOW_X + TEXT_OFFSET_X, DRAW_Y + TEXT_OFFSET_Y, Color.LIGHT_GRAY);
    } else if (this.hb.hovered) {
      FontHelper.renderFontCentered(sb, FontHelper.buttonLabelFont, this.buttonText, SHOW_X + TEXT_OFFSET_X, DRAW_Y + TEXT_OFFSET_Y, Settings.LIGHT_YELLOW_COLOR);
    } else {
      FontHelper.renderFontCentered(sb, FontHelper.buttonLabelFont, this.buttonText, SHOW_X + TEXT_OFFSET_X, DRAW_Y + TEXT_OFFSET_Y, Settings.LIGHT_YELLOW_COLOR);
    } 
    this.hb.render(sb); 
  }
    
  private void renderButton(SpriteBatch sb) {
    Texture image = images.get(index);
    sb.draw(image, SHOW_X - image.getWidth()/2f, DRAW_Y - image.getHeight()/2f, image.getWidth()/2f, image.getHeight()/2f, image.getWidth(), image.getHeight(), Settings.scale, Settings.scale, 0.0F, 0, 0, image.getWidth(), image.getHeight(), false, false);
  }
}
