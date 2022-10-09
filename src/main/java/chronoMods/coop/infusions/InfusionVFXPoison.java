package chronoMods.coop.infusions;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;

public class InfusionVFXPoison extends InfusionVFXBase {

  public static TextureAtlas.AtlasRegion p1;
  public static TextureAtlas.AtlasRegion p2;
  public static TextureAtlas.AtlasRegion p3;
  public static TextureAtlas.AtlasRegion p4;
  
  public InfusionVFXPoison(AbstractCard card) {
    super(card);

    frequency = 0.1f;

    if (p1 == null) {
      Texture t = ImageMaster.loadImage("chrono/images/particles/poison1.png");
      p1 = new TextureAtlas.AtlasRegion(t, 0, 0, t.getWidth(), t.getHeight());
    }
    if (p2 == null) {
      Texture t = ImageMaster.loadImage("chrono/images/particles/poison2.png");
      p2 = new TextureAtlas.AtlasRegion(t, 0, 0, t.getWidth(), t.getHeight());
    }
    if (p3 == null) {
      Texture t = ImageMaster.loadImage("chrono/images/particles/poison3.png");
      p3 = new TextureAtlas.AtlasRegion(t, 0, 0, t.getWidth(), t.getHeight());
    }
    if (p4 == null) {
      Texture t = ImageMaster.loadImage("chrono/images/particles/poison4.png");
      p4 = new TextureAtlas.AtlasRegion(t, 0, 0, t.getWidth(), t.getHeight());
    }

    this.card = card;
    switch (MathUtils.random(3)) {
      case 0:
        this.color = Color.WHITE.cpy();
        break;
      case 1:
        this.color = Color.WHITE.cpy();
        break;
      case 2:
        this.color = Color.GRAY.cpy();
        break;
      case 3:
        this.color = Color.DARK_GRAY.cpy();
        break;
    } 

    this.duration = MathUtils.random(0.9F, 2.4F);
    this.duration *= this.duration;
    this.startDur = this.duration;
    
    this.startScale = MathUtils.random(0.4F, 0.8F);
    this.targetScale = 0.01f;
    this.scale = this.startScale;

    this.vX = MathUtils.random(-15.0F * Settings.scale, 15.0F * Settings.scale);
    this.vY = MathUtils.random(10.0F * Settings.scale, 40.0F * Settings.scale);

    this.x = MathUtils.random(-130.0F * Settings.scale, 130.0F * Settings.scale);
    this.y = -208f * Settings.scale;

    this.rotateSpeed = MathUtils.random(-200.0F, 200.0F);

    this.img = setImg();
  }
  
  public TextureAtlas.AtlasRegion setImg() {
    switch (MathUtils.random(0, 5)) {
      case 0:
        return p1;
      case 1:
        return p2;
      case 2:
        return p3;
      case 3:
        return p4;
      case 4:
        return p4;
    } 
    return p4;
  }
}
