package chronoMods.coop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.cards.*;

public class InfusionEmber extends AbstractGameEffect {
  private float x;
  private float y;
  
  private float vX;
  private float vY;
  
  private float startDur;
  private float targetScale;
  
  private TextureAtlas.AtlasRegion img;
  
  private float rotateSpeed = 0.0F;
  AbstractCard card;
  
  public InfusionEmber(AbstractCard card) {
    this.card = card;
    switch (MathUtils.random(4)) {
      case 0:
        this.color = Color.CORAL.cpy();
        break;
      case 1:
        this.color = Color.ORANGE.cpy();
        break;
      case 2:
        this.color = Color.SCARLET.cpy();
        break;
      case 3:
        this.color = Color.BLACK.cpy();
        break;
      case 4:
        this.color = Color.DARK_GRAY.cpy();
        break;
    } 
    this.duration = MathUtils.random(0.6F, 1.4F);
    this.duration *= this.duration;
    this.targetScale = MathUtils.random(0.2F, 0.4F);
    this.startDur = this.duration;
    this.vX = MathUtils.random(-30.0F * Settings.scale, 30.0F * Settings.scale);
    this.vY = MathUtils.random(20.0F * Settings.scale, 90.0F * Settings.scale);
    this.x = x + MathUtils.random(-130.0F * Settings.scale, 130.0F * Settings.scale);
    // this.y = y + MathUtils.random(-220.0F * Settings.scale, 150.0F * Settings.scale);
    this.y = -208f * Settings.scale;
    this.scale = 0.01F;
    this.img = setImg();
    this.rotateSpeed = MathUtils.random(-700.0F, 700.0F);
  }
  
  private TextureAtlas.AtlasRegion setImg() {
    switch (MathUtils.random(0, 5)) {
      case 0:
        return ImageMaster.DUST_1;
      case 1:
        return ImageMaster.DUST_2;
      case 2:
        return ImageMaster.DUST_3;
      case 3:
        return ImageMaster.DUST_4;
      case 4:
        return ImageMaster.DUST_5;
    } 
    return ImageMaster.DUST_6;
  }
  
  public void update() {
    this.duration -= Gdx.graphics.getDeltaTime();
    if (this.duration < 0.0F)
      this.isDone = true; 
    this.x += this.vX * Gdx.graphics.getDeltaTime();
    this.y += this.vY * Gdx.graphics.getDeltaTime();
    this.rotation += this.rotateSpeed * Gdx.graphics.getDeltaTime();
    this.scale = Interpolation.swing.apply(0.01F, this.targetScale, 1.0F - this.duration / this.startDur) * this.card.drawScale;
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
