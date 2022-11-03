package chronoMods.coop.infusions;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;

public class InfusionVFXEmber extends InfusionVFXBase {
  
  public InfusionVFXEmber(AbstractCard card) {
    super(card);
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

    frequency = 0.02f;

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
  
  public TextureAtlas.AtlasRegion setImg() {
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
}
