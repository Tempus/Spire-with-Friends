package chronoMods.coop;

import chronoMods.network.RemotePlayer;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.vfx.DoorFlashEffect;

public class CoopDoorLock {
  private Color glowColor = Color.WHITE.cpy();
  
  private Texture redlockImg = null;
  private Texture redglowImg = null;

  private Texture bluelockImg = null;
  private Texture blueglowImg = null;

  private Texture greenlockImg = null;
  private Texture greenglowImg = null;


  public boolean glowing = true;  
  private boolean unlockAnimation = false;
  private boolean usedFlash = false;
  
  private float x = 0.0F;
  private float y;
  public float scale = 1.0f;

  private float unlockTimer = 2.0F;
  
  private float startY;
  private float targetY;
  
  public RemotePlayer player;
  

  public CoopDoorLock(RemotePlayer p, float x, float y, float scale) {
    this.player = p;
    this.startY = 0.0F * Settings.scale;
    this.y = y;
    this.x = x;
    this.scale = scale;

    this.redlockImg = ImageMaster.loadImage("images/ui/door/lock_red.png");
    this.redglowImg = ImageMaster.loadImage("images/ui/door/glow_red.png");

    this.bluelockImg = ImageMaster.loadImage("images/ui/door/lock_green.png");
    this.blueglowImg = ImageMaster.loadImage("images/ui/door/glow_green.png");

    this.greenlockImg = ImageMaster.loadImage("images/ui/door/lock_blue.png");
    this.greenglowImg = ImageMaster.loadImage("images/ui/door/glow_blue.png");
  }
  
  public void update() {
    updateUnlockAnimation();
  }
  
  private void updateUnlockAnimation() {
    if (this.unlockAnimation) {
      this.unlockTimer -= Gdx.graphics.getDeltaTime();
      if (this.unlockTimer < 0.0F) {
        this.unlockTimer = 0.0F;
        this.unlockAnimation = false;
      } 
    } 
  }
  
  public void unlock() {
    this.unlockAnimation = true;
    this.unlockTimer = 2.0F;
  }
  
  public void render(SpriteBatch sb) {
    if (this.redlockImg == null || bluelockImg == null || greenlockImg == null)
      return; 
    renderLock(sb);
    renderGlow(sb);
  }
  
  private void renderLock(SpriteBatch sb) {
    sb.setColor(Color.WHITE);
    sb.draw(this.redlockImg, Settings.WIDTH / 2.0F - 960.0F + this.x, Settings.HEIGHT / 2.0F - 600.0F + this.y - 66 * Settings.yScale, 960.0F, 600.0F, 1920.0F, 1200.0F, Settings.scale * scale, Settings.scale * scale, 0.0F, 0, 0, 1920, 1200, false, false);
    sb.draw(this.bluelockImg, Settings.WIDTH / 2.0F - 960.0F + this.x, Settings.HEIGHT / 2.0F - 600.0F + this.y - 66 * Settings.yScale, 960.0F, 600.0F, 1920.0F, 1200.0F, Settings.scale * scale, Settings.scale * scale, 0.0F, 0, 0, 1920, 1200, false, false);
    sb.draw(this.greenlockImg, Settings.WIDTH / 2.0F - 960.0F + this.x, Settings.HEIGHT / 2.0F - 600.0F + this.y - 66 * Settings.yScale, 960.0F, 600.0F, 1920.0F, 1200.0F, Settings.scale * scale, Settings.scale * scale, 0.0F, 0, 0, 1920, 1200, false, false);
  }
  
  private void renderGlow(SpriteBatch sb) {
    if (this.glowing) {
      this.glowColor.a = (MathUtils.cosDeg((float)(System.currentTimeMillis() / 4L % 360L)) + 3.0F) / 4.0F;
      sb.setColor(this.glowColor);
      sb.setBlendFunction(770, 1);
      if (player.rubyKey)
        sb.draw(this.redglowImg, Settings.WIDTH / 2.0F - 960.0F + this.x, Settings.HEIGHT / 2.0F - 600.0F + this.y - 66 * Settings.yScale, 960.0F, 600.0F, 1920.0F, 1200.0F, Settings.scale * scale, Settings.scale * scale, 0.0F, 0, 0, 1920, 1200, false, false);
      if (player.sapphireKey)
        sb.draw(this.blueglowImg, Settings.WIDTH / 2.0F - 960.0F + this.x, Settings.HEIGHT / 2.0F - 600.0F + this.y - 66 * Settings.yScale, 960.0F, 600.0F, 1920.0F, 1200.0F, Settings.scale * scale, Settings.scale * scale, 0.0F, 0, 0, 1920, 1200, false, false);
      if (player.emeraldKey)
        sb.draw(this.greenglowImg, Settings.WIDTH / 2.0F - 960.0F + this.x, Settings.HEIGHT / 2.0F - 600.0F + this.y - 66 * Settings.yScale, 960.0F, 600.0F, 1920.0F, 1200.0F, Settings.scale * scale, Settings.scale * scale, 0.0F, 0, 0, 1920, 1200, false, false);
      sb.setBlendFunction(770, 771);
    } 
  }
  
  public void reset() {
    this.usedFlash = false;
    this.unlockAnimation = false;
    this.unlockTimer = 2.0F;
  }
  
  public void flash(boolean eventVersion) {
    if (!this.usedFlash) {
      CardCrawlGame.sound.playA("ATTACK_MAGIC_SLOW_2", 1.0F);
      this.usedFlash = true;
      CardCrawlGame.mainMenuScreen.doorUnlockScreen.effects.add(new DoorFlashEffect(this.redglowImg, false));
      CardCrawlGame.mainMenuScreen.doorUnlockScreen.effects.add(new DoorFlashEffect(this.blueglowImg, false));
      CardCrawlGame.mainMenuScreen.doorUnlockScreen.effects.add(new DoorFlashEffect(this.greenglowImg, false));
    } 
  }
  
  public void dispose() {
    this.redlockImg.dispose();
    this.redglowImg.dispose();

    this.bluelockImg.dispose();
    this.blueglowImg.dispose();

    this.greenlockImg.dispose();
    this.greenglowImg.dispose();
  }
}
