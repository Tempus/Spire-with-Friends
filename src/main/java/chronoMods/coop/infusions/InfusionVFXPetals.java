package chronoMods.coop.infusions;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;

public class InfusionVFXPetals extends InfusionVFXBase {
  
  private int frame = 0;
  private float animTimer = 0.18F;

  public InfusionVFXPetals(AbstractCard card) {
    super(card);
    this.card = card;

    this.color = new Color(1.0F, MathUtils.random(0.7F, 0.9F), MathUtils.random(0.7F, 0.9F), 1.0F);

    frequency = 0.6f;

    this.duration = MathUtils.random(0.6F, 1.4F);
    this.duration *= this.duration;
    this.startDur = this.duration;
    
    this.targetScale = MathUtils.random(0.15F, 0.35F);
    this.startScale = targetScale+0.25f;
    this.scale = this.startScale;

    this.vX = MathUtils.random(-30.0F * Settings.scale, 30.0F * Settings.scale);
    this.vY = MathUtils.random(20.0F * Settings.scale, 90.0F * Settings.scale);

    this.x = MathUtils.random(-130.0F * Settings.scale, 130.0F * Settings.scale);
    this.y = -208f * Settings.scale;

    this.rotateSpeed = MathUtils.random(-100.0F, 100.0F);
  }
  
  public TextureAtlas.AtlasRegion setImg() { return null; }


  public void render(SpriteBatch sb) {

    this.animTimer -= Gdx.graphics.getDeltaTime() / this.scale;
    if (this.animTimer < 0.0F) {
      this.animTimer += 0.18F;
      this.frame++;
      if (this.frame > 11)
        this.frame = 0; 
    } 

    switch (this.frame) {
      case 0:
        renderImg(sb, ImageMaster.PETAL_VFX[0], false, false);
        break;
      case 1:
        renderImg(sb, ImageMaster.PETAL_VFX[1], false, false);
        break;
      case 2:
        renderImg(sb, ImageMaster.PETAL_VFX[2], false, false);
        break;
      case 3:
        renderImg(sb, ImageMaster.PETAL_VFX[3], false, false);
        break;
      case 4:
        renderImg(sb, ImageMaster.PETAL_VFX[2], true, true);
        break;
      case 5:
        renderImg(sb, ImageMaster.PETAL_VFX[1], true, true);
        break;
      case 6:
        renderImg(sb, ImageMaster.PETAL_VFX[0], true, true);
        break;
      case 7:
        renderImg(sb, ImageMaster.PETAL_VFX[1], true, true);
        break;
      case 8:
        renderImg(sb, ImageMaster.PETAL_VFX[2], true, true);
        break;
      case 9:
        renderImg(sb, ImageMaster.PETAL_VFX[3], true, true);
        break;
      case 10:
        renderImg(sb, ImageMaster.PETAL_VFX[2], false, false);
        break;
      case 11:
        renderImg(sb, ImageMaster.PETAL_VFX[1], false, false);
        break;
    }
  }

  private void renderImg(SpriteBatch sb, Texture tmp, boolean flipH, boolean flipV) {
    sb.setBlendFunction(770, 1);
    this.img = new TextureAtlas.AtlasRegion(tmp, 0, 0, tmp.getWidth(), tmp.getHeight());
    this.img.flip(flipH, flipV);

    sb.draw((TextureRegion)this.img, this.card.current_x + this.x * this.card.drawScale, this.card.current_y + this.y * this.card.drawScale, this.img.offsetX, this.img.offsetY, this.img.packedWidth, this.img.packedHeight, this.scale, this.scale, this.rotation);
    sb.setBlendFunction(770, 771);
  }
}
