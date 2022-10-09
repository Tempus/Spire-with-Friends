package chronoMods.coop.infusions;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

public class InfusionVFXBase extends AbstractGameEffect {
  public float x;
  public float y;
  
  public float vX;
  public float vY;
  
  public float startDur;
  public float targetScale;
  public float startScale;
  
  public TextureAtlas.AtlasRegion img;
  
  public float rotateSpeed = 0.0F;
  AbstractCard card;

  public float frequency;
  
  public InfusionVFXBase(AbstractCard card) {
    this.card = card;

    this.color = Color.WHITE.cpy();

    this.duration = MathUtils.random(0.6F, 1.4F);
    this.duration *= this.duration;
    this.startDur = this.duration;
    
    this.targetScale = MathUtils.random(0.2F, 0.4F);
    this.startScale = 0.01f;
    this.scale = this.startScale;

    this.vX = MathUtils.random(-30.0F * Settings.scale, 30.0F * Settings.scale);
    this.vY = MathUtils.random(20.0F * Settings.scale, 90.0F * Settings.scale);

    this.x = MathUtils.random(-130.0F * Settings.scale, 130.0F * Settings.scale);
    this.y = -208f * Settings.scale;

    this.rotateSpeed = MathUtils.random(-700.0F, 700.0F);

    this.img = setImg();
  }
  
  public float getFrequency() { return frequency; }

  public TextureAtlas.AtlasRegion setImg() {
    return ImageMaster.DUST_1;
  }
  
  public void update() {
    this.duration -= Gdx.graphics.getDeltaTime();
    if (this.duration < 0.0F)
      this.isDone = true; 
    this.x += this.vX * Gdx.graphics.getDeltaTime();
    this.y += this.vY * Gdx.graphics.getDeltaTime();
    this.rotation += this.rotateSpeed * Gdx.graphics.getDeltaTime();
    this.scale = Interpolation.swing.apply(this.startScale, this.targetScale, 1.0F - this.duration / this.startDur) * this.card.drawScale;
    if (this.duration < 0.5F)
      this.color.a = this.duration * 2.0F * this.card.transparency; 
  }
  
  public void render(SpriteBatch sb) {
    sb.setColor(this.color);
    sb.draw((TextureRegion)this.img, this.card.current_x + this.x * this.card.drawScale, this.card.current_y + this.y * this.card.drawScale, this.img.offsetX, this.img.offsetY, this.img.packedWidth, this.img.packedHeight, this.scale, this.scale, this.rotation);
    sb.setColor(new Color(this.color.r, this.color.g, this.color.b, this.color.a / 3.0F));
    sb.setBlendFunction(770, 1);
    sb.draw((TextureRegion)this.img, this.card.current_x + this.x * this.card.drawScale, this.card.current_y + this.y * this.card.drawScale, this.img.offsetX, this.img.offsetY, this.img.packedWidth, this.img.packedHeight, this.scale, this.scale, this.rotation);
    sb.setBlendFunction(770, 771);
  }
  
  public void dispose() {}
}
