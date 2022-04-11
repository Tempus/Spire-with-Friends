package chronoMods.coop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Interpolation;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

import chronoMods.*;

public class InfusionVFX extends AbstractGameEffect {
  private AbstractCard card;
  
  private Texture img;
  private float scale;

  public static float startScale = 0.11f;
  public static float endScale   = 0.03f;
  
  public InfusionVFX(AbstractCard card, Color gColor) {
    this.card = card;
    this.img = TogetherManager.infusionGlow;

    this.duration = 1.2F;
    if ((AbstractDungeon.getCurrRoom()).phase == AbstractRoom.RoomPhase.COMBAT) {
      this.color = gColor.cpy();
    } else {
      this.color = Color.GOLD.cpy();
    } 
  }
  
  public void update() {
    this.scale = (1.0F - Interpolation.pow2Out.apply(0.03F, startScale, 1.0F - this.duration)) * this.card.drawScale * Settings.scale;
    this.color.a = this.duration / 2.0F * this.card.transparency;
    this.duration -= Gdx.graphics.getDeltaTime();
    if (this.duration < 0.0F) {
      this.isDone = true;
      this.duration = 0.0F;
    } 
  }
  
  public void render(SpriteBatch sb) {
    sb.setColor(this.color);
    sb.draw(this.img, this.card.current_x - this.img.getWidth() / 2.0F, this.card.current_y - this.img.getHeight() / 2.0F - 86f * this.card.drawScale,
            this.img.getWidth() / 2.0F, this.img.getHeight() / 2.0F, this.img.getWidth(), this.img.getHeight(), this.scale, this.scale, this.card.angle,
            0, 0, this.img.getWidth(), this.img.getHeight(), false, false);
  }
  
  public void dispose() {}
}