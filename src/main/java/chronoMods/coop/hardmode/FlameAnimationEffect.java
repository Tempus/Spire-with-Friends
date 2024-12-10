package chronoMods.coop.hardmode;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

public class FlameAnimationEffect extends AbstractGameEffect {
  public Texture img = null;
  
  private static final int W = 256;
  
  private static final float DUR = 0.5F;
  
  private static boolean alternator = true;
  
  private boolean flipped = false;
  
  private Hitbox nodeHb;
  
  private float offsetX;
  
  private float offsetY;
  
  public FlameAnimationEffect(Hitbox hb) {
    this.nodeHb = hb;
    this.startingDuration = 0.5F;
    this.duration = 0.5F;
    this.scale = MathUtils.random(3.4F, 3.6F) * Settings.scale;
    this.rotation = MathUtils.random(-30.0F, 30.0F);
    this.offsetX = MathUtils.random(0.0F, 8.0F) * Settings.scale;
    this.offsetY = MathUtils.random(-3.0F, 14.0F) * Settings.scale;
    alternator = !alternator;
    this.flipped = alternator;
    if (!alternator)
      this.offsetX = -this.offsetX; 
    this.color = new Color(0.34F, 0.34F, 0.34F, 1.0F);
    this.color = this.color.cpy();
    this.img = ImageMaster.FLAME_ANIM_1;
  }
  
  public void update() {
    this.color.a = this.duration / 0.5F;
    if (this.duration < 0.1F) {
      this.img = null;
    } else if (this.duration < 0.0F) {
      this.img = ImageMaster.FLAME_ANIM_6;
    } else if (this.duration < 0.1F) {
      this.img = ImageMaster.FLAME_ANIM_5;
    } else if (this.duration < 0.2F) {
      this.img = ImageMaster.FLAME_ANIM_4;
    } else if (this.duration < 0.3F) {
      this.img = ImageMaster.FLAME_ANIM_3;
    } else if (this.duration < 0.4F) {
      this.img = ImageMaster.FLAME_ANIM_2;
    } 
    this.duration -= Gdx.graphics.getDeltaTime();
    if (this.duration < 0.0F)
      this.isDone = true; 
  }
  
  public void render(SpriteBatch sb) {
    sb.setColor(this.color);
    if (this.img != null)
      sb.draw(this.img, this.nodeHb.cX - 128.0F + this.offsetX, this.nodeHb.cY - 128.0F + this.offsetY, 128.0F, 128.0F, 256.0F, 256.0F, this.scale, this.scale, this.rotation, 0, 0, 256, 256, this.flipped, false); 
  }
    
  public void dispose() {}
}
