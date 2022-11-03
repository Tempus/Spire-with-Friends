package chronoMods.coop.drawable;

import chronoMods.network.RemotePlayer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;

public class HideButton {
  private int W = 512;
  
  private int H = 256;
  
  private Color HOVER_BLEND_COLOR = new Color(1.0F, 1.0F, 1.0F, 0.4F);
      private Color WHITE = new Color(1.0F, 1.0F, 1.0F, 1.0F);
      private Color GREY = new Color(0.5F, 0.5F, 0.5F, 1.0F);

  private Color TEXT_DISABLED_COLOR = new Color(0.6F, 0.6F, 0.6F, 1.0F);
  
  public float SHOW_X = Settings.WIDTH - 256.0F * Settings.scale, DRAW_Y = 128.0F * Settings.scale;
  
  private float HIDE_X = SHOW_X + 400.0F * Settings.scale;
  
  private float current_x = HIDE_X;
  
  private float target_x = this.current_x;
      
  public boolean isDisabled = false;
  
  public boolean isHovered = false;
      
  private float TEXT_OFFSET_X = 0.0F * Settings.scale;
  
  private float TEXT_OFFSET_Y = 0.0F * Settings.scale;
    
  public Hitbox hb;
  
  public float alpha = 1f;

  public Texture image;

  public RemotePlayer player;

  public float buttonSize = 36f;

  public HideButton(float x, float y, RemotePlayer p) {
    SHOW_X = x;
    current_x = SHOW_X;
    target_x = this.current_x;

    DRAW_Y = y;

    this.player = p;

    this.hb = new Hitbox(0.0F, 0.0F, buttonSize, buttonSize);
    this.hb.move(SHOW_X, DRAW_Y);
  }
    
  public void update() {
      this.hb.update();
      if (InputHelper.justClickedLeft && this.hb.hovered) {
        isDisabled = !isDisabled;
        player.drawable[AbstractDungeon.actNum-1].hidden = isDisabled;

        this.hb.clickStarted = true;
        CardCrawlGame.sound.play("UI_CLICK_1");
      } 
      if (this.hb.justHovered && !this.isDisabled)
        CardCrawlGame.sound.play("UI_HOVER"); 
      this.isHovered = this.hb.hovered;
      if (CInputActionSet.proceed.isJustPressed()) {
        CInputActionSet.proceed.unpress();
        this.hb.clicked = true;
      } 
  }
      
  public void render(SpriteBatch sb) {

    WHITE.a = alpha;
    Color c = WHITE;
    if (isDisabled) {
      GREY.a = alpha;
      c = GREY;
    }

    renderButton(sb, c);
    if (this.hb.hovered && !this.isDisabled && !this.hb.clickStarted) {
      sb.setBlendFunction(770, 1);
      renderButton(sb, HOVER_BLEND_COLOR);
      sb.setBlendFunction(770, 771);
    } 
    this.hb.render(sb); 
    sb.setColor(Color.WHITE);
  }
    
  private void renderButton(SpriteBatch sb, Color c) {
    if (player.getPortrait() != null) {
      sb.setColor(Color.BLACK);
      sb.draw(player.getPortrait(), this.current_x - buttonSize/2f -4f * Settings.xScale, DRAW_Y - buttonSize/2f -4f * Settings.yScale, 0, 0, buttonSize + 8f, buttonSize + 8f, Settings.scale, Settings.scale, 0.0F, 0, 0, player.getPortrait().getWidth(), player.getPortrait().getHeight(), false, false);
      sb.setColor(c);
      sb.draw(player.getPortrait(), this.current_x - buttonSize/2f, DRAW_Y - buttonSize/2f, 0, 0, buttonSize, buttonSize, Settings.scale, Settings.scale, 0.0F, 0, 0, player.getPortrait().getWidth(), player.getPortrait().getHeight(), false, false);
    }
  }
}
