package chronoMods.coop.infusions;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;

public class InfusionVFXShock extends InfusionVFXBase {
  

  private boolean flipX;
  private boolean flipY;
  private float intervalDuration;
  private float intervalTimer;
  public int index = 0;

  public InfusionVFXShock(AbstractCard card) {
    super(card);
    this.card = card;

    this.color = Settings.LIGHT_YELLOW_COLOR.cpy();
    frequency = MathUtils.random(0.01F, 0.5F);

    this.duration = 2f;
    this.startDur = this.duration;
    
    this.targetScale = MathUtils.random(0.1F, 0.6F) * Settings.scale;
    this.startScale = targetScale;
    this.scale = this.startScale;

    this.vX = 0;
    this.vY = 0;

    this.x = MathUtils.random(-110.0F * Settings.scale, 110.0F * Settings.scale);
    this.y = -150.0F * Settings.scale;

    this.flipX = MathUtils.randomBoolean();
    this.flipY = MathUtils.randomBoolean();

    this.rotateSpeed = 0;
    this.rotation = MathUtils.random(0.0F, 360.0F);


    this.intervalDuration = MathUtils.random(0.03F, 0.06F);
    this.intervalTimer = this.intervalDuration;


    setImg();
  }
  
  public TextureAtlas.AtlasRegion setImg() {
      Texture tmp = ImageMaster.LIGHTNING_PASSIVE_VFX.get(this.index);
      this.img = new TextureAtlas.AtlasRegion(tmp, 0, 0, tmp.getWidth(), tmp.getHeight());
      this.img.flip(flipX, flipY);

      return this.img;
  }

  public void update() {
    super.update();

    this.intervalTimer -= Gdx.graphics.getDeltaTime();
    if (this.intervalTimer < 0.0F) {
      this.index++;
      if (this.index > ImageMaster.LIGHTNING_PASSIVE_VFX.size() - 1) {
        this.isDone = true;
        return;
      } 
      setImg();

      this.intervalTimer = this.intervalDuration;
    } 
  }

  public void render(SpriteBatch sb) {
    sb.setColor(this.color);
    // sb.draw((TextureRegion)this.img, this.card.current_x + this.x * this.card.drawScale, this.card.current_y + this.y * this.card.drawScale, this.img.offsetX, this.img.offsetY, this.img.packedWidth, this.img.packedHeight, this.scale, this.scale, this.rotation);
    // sb.setColor(new Color(this.color.r, this.color.g, this.color.b, this.color.a / 3.0F));
    sb.setBlendFunction(770, 1);
    sb.draw((TextureRegion)this.img, this.card.current_x + this.x * this.card.drawScale, this.card.current_y + this.y * this.card.drawScale, this.img.offsetX, this.img.offsetY, this.img.packedWidth, this.img.packedHeight, this.scale, this.scale, this.rotation);
    sb.setBlendFunction(770, 771);
  }

}
