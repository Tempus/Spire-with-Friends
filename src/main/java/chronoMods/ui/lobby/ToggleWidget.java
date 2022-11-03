package chronoMods.ui.lobby;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;

public class ToggleWidget {
  
  public boolean ticked = false;
  private String label;
  
  public Hitbox hb = new Hitbox(320.0F * Settings.scale, 72.0F * Settings.scale);
  
  private float x;
  private float y;
  
  private float scale = Settings.scale;
  public float alpha = 1.0f;
  
  public ToggleWidget(float x, float y, String label) {
    this.x = x * Settings.scale;
    this.y = y * Settings.scale;
    this.label = label;
    this.ticked = false;
    this.hb.move(x + 110.0F * Settings.scale, y * Settings.scale);
  }

  public ToggleWidget(float x, float y, String label, boolean initialState) {
    this.x = x;
    this.y = y;
    this.label = label;
    this.ticked = initialState;
    this.hb.move(x + 110.0F * Settings.scale, y);
  }
  
  public int getTicked() {
    return this.ticked ? 1 : 0;
  }

  public boolean isTicked() {
    return this.ticked;
  }

  public void setTicked(int i) {
    this.ticked = (i>0) ? true : false;
  }

  public void setTicked(boolean b) {
    this.ticked = b;
  }

  public boolean update() {
    this.hb.update();

    if (this.hb.hovered) {
      this.scale = Settings.scale * 1.125F;
    } else {
      this.scale = Settings.scale;
    } 

    if (this.hb.justHovered)
      CardCrawlGame.sound.play("UI_HOVER"); 
    if (this.hb.hovered && InputHelper.justClickedLeft) {
      this.hb.clickStarted = true;
      CardCrawlGame.sound.play("UI_CLICK_1");
    } else if (this.hb.clicked || (this.hb.hovered && CInputActionSet.select.isJustPressed())) {
      CInputActionSet.select.unpress();
      this.hb.clicked = false;
      this.ticked = !this.ticked;
      return true;
    } 
    return false;
  }
  
  public void render(SpriteBatch sb) {
    Color c = new Color(1f,1f,1f,alpha);
    sb.setColor(c);
    sb.draw(ImageMaster.CHECKBOX, this.x - 32.0F, this.y - 32.0F, 32.0F, 32.0F, 64.0F, 64.0F, this.scale, this.scale, 0.0F, 0, 0, 64, 64, false, false);
    if (this.ticked)
      sb.draw(ImageMaster.TICK, this.x - 32.0F, this.y - 32.0F, 32.0F, 32.0F, 64.0F, 64.0F, this.scale, this.scale, 0.0F, 0, 0, 64, 64, false, false); 
    if (this.hb.hovered) {
      FontHelper.renderFontLeft(sb, FontHelper.panelEndTurnFont, this.label, this.x + 40.0F * Settings.scale, this.y, Settings.GREEN_TEXT_COLOR);
    } else {
      FontHelper.renderFontLeft(sb, FontHelper.panelEndTurnFont, this.label, this.x + 40.0F * Settings.scale, this.y, Settings.CREAM_COLOR);
    } 
    this.hb.render(sb);
    sb.setColor(Color.WHITE);
  }
}
