package chronoMods.coop;

import chronoMods.TogetherManager;
import chronoMods.network.NetworkHelper;
import chronoMods.network.RemotePlayer;
import chronoMods.ui.deathScreen.EndScreenCoopLoss;
import chronoMods.ui.deathScreen.NewDeathScreenPatches;
import chronoMods.ui.hud.RemotePlayerWidget;
import chronoMods.ui.hud.TopPanelPlayerPanels;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.blights.AbstractBlight;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.GameCursor;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.beyond.SpireHeart;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.MathHelper;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.screens.DoorUnlockScreen;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.DoorShineParticleEffect;

import java.util.ArrayList;
import java.util.Iterator;

public class CoopDoorUnlockScreen extends DoorUnlockScreen {

    public static boolean rk, bk, gk;

    @SpirePatch(clz=SpireHeart.class, method="buttonEffect")
    public static class FixCoopHeartNotEnoughKeys
    {
        public static void Prefix(SpireHeart d, int buttonPressed)
        {
            if (TogetherManager.gameMode != TogetherManager.mode.Coop) { return; }

            TogetherManager.log("Save Keys");
            if (Settings.isFinalActAvailable && (!Settings.hasRubyKey || !Settings.hasEmeraldKey || !Settings.hasSapphireKey)) {
              rk = Settings.hasRubyKey;
              gk = Settings.hasEmeraldKey;
              bk = Settings.hasSapphireKey;

              Settings.hasRubyKey = true;
              Settings.hasEmeraldKey = true;
              Settings.hasSapphireKey = true;
            }
        }
    }

    @SpirePatch(clz=SpireHeart.class, method="buttonEffect")
    public static class FixCoopHeartNotEnoughKeysReset
    {
        @SpireInsertPatch(rloc=189-149)
        public static void Insert(SpireHeart d, int buttonPressed)
        {
            if (TogetherManager.gameMode != TogetherManager.mode.Coop) { return; }

            TogetherManager.log("Reset Keys");
            if (Settings.isFinalActAvailable) {
              Settings.hasRubyKey = rk;
              Settings.hasEmeraldKey = gk;
              Settings.hasSapphireKey = bk;
            }
        }
    }


  private static Texture doorLeft;
  private static Texture doorRight;
  private static Texture circleLeft;
  private static Texture circleRight;
  
  private Color bgColor = new Color(337060863);
  private Color fadeColor = Color.BLACK.cpy();
  private Color fadeOutColor = new Color(0.0F, 0.0F, 0.0F, 0.0F);
 
  private float fadeTimer; 
  private float lightUpTimer;
  
  private boolean fadeOut = false;
  
  public ArrayList<CoopDoorLock> locks = new ArrayList();
  
  public ArrayList<AbstractGameEffect> effects = new ArrayList<>();
  
  private boolean animateCircle = false;
  private boolean rotatingCircle = true;
    
  private float circleAngle = -45.0F, doorOffset = 1.0F;
  private float circleTimer;
  private float circleTime;
  
  private float autoContinueTimer;
  private float doorTime;
  
  private float renderScale = (Settings.xScale > Settings.yScale) ? Settings.xScale : Settings.yScale;
  
  public void open(boolean eventVersion) {
    NetworkHelper.sendData(NetworkHelper.dataType.AtDoor);

    GameCursor.hidden = true;
    if (doorLeft == null) {
      doorLeft = ImageMaster.loadImage("images/ui/door/door_left.png");
      doorRight = ImageMaster.loadImage("images/ui/door/door_right.png");
      circleLeft = ImageMaster.loadImage("images/ui/door/circle_left.png");
      circleRight = ImageMaster.loadImage("images/ui/door/circle_right.png");
    } 
    
    locks.clear();

    // Arrange all the locks around the circle - Circle is 720px in diameter
    int playerCount = TogetherManager.players.size();
    int i = 0;

    if (playerCount == 1) {
      for (RemotePlayer p : TogetherManager.players)
        locks.add(new CoopDoorLock(p, 0, 0, 1.0f/playerCount));
    } else {
      // Radians
      float rads = 6.283f/playerCount;
      for (RemotePlayer p : TogetherManager.players) {
        // point on the edge of circle
        locks.add(new CoopDoorLock(p, 360f * MathUtils.cos(rads*i), 360f * MathUtils.sin(rads*i), 1.0f/playerCount));
        i++;
      }
    }

    if (Settings.FAST_MODE) {
      this.circleTimer = 1.0F;
      this.circleTime = 1.0F;
      this.lightUpTimer = 1.0F;
      this.autoContinueTimer = 0.01F;
      this.doorTime = 2.0F;
      this.fadeTimer = 1.0F;
    } else {
      this.circleTimer = 3.0F;
      this.circleTime = 3.0F;
      this.lightUpTimer = 3.0F;
      this.autoContinueTimer = 0.5F;
      this.doorTime = 5.0F;
      this.fadeTimer = 3.0F;
    } 
    this.circleAngle = -45.0F;
    this.doorOffset = 1.0F;
    this.rotatingCircle = true;
    this.fadeColor = Color.BLACK.cpy();
    CardCrawlGame.music.silenceBGM();
    this.fadeOut = false;
    GameCursor.hidden = true;
  }

  public void proceed() {
    // Check to see if any keys are missing
    int keysMissing = 0;
    for (RemotePlayer p : TogetherManager.players) {
      if (!p.emeraldKey)
        keysMissing++;
      if (!p.rubyKey)
        keysMissing++;
      if (!p.sapphireKey)
        keysMissing++;
    }

    // If keys are missing remove a life for each missing key
    if (keysMissing > 0) {
      AbstractBlight lives;
      if (AbstractDungeon.player.hasBlight("StringOfFate")) {
        lives = AbstractDungeon.player.getBlight("StringOfFate");
      } else if (AbstractDungeon.player.hasBlight("ChainsOfFate")) {
        lives = AbstractDungeon.player.getBlight("ChainsOfFate");
      } else {
        lives = AbstractDungeon.player.getBlight("BondsOfFate");
      }

      lives.counter = lives.counter-keysMissing;

      // If we don't have enough lives, it is time to die
      if (lives.counter < 0) {
        for (int i = 0; i < lives.counter; i++)
          NetworkHelper.sendData(NetworkHelper.dataType.LoseLife);
        
        AbstractDungeon.player.currentHealth = 0;
        AbstractDungeon.player.isDead = true;

        NewDeathScreenPatches.EndScreenBase = new EndScreenCoopLoss(AbstractDungeon.getCurrRoom().monsters);
        AbstractDungeon.screen = NewDeathScreenPatches.Enum.RACEEND;

        return;
      }
    }

    this.animateCircle = true;
    unlock();
  }
  
  private void unlock() {
    CardCrawlGame.sound.playA("ATTACK_HEAVY", 0.4F);
    CardCrawlGame.sound.playA("POWER_SHACKLE", 0.1F);
    CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.HIGH, ScreenShake.ShakeDur.SHORT, true);
    for (int i = 0; i < 50; i++)
      this.effects.add(new DoorShineParticleEffect(
            
            MathUtils.random(Settings.WIDTH * 0.45F, Settings.WIDTH * 0.55F), 
            MathUtils.random(Settings.HEIGHT * 0.45F, Settings.HEIGHT * 0.55F))); 
    for (CoopDoorLock l : locks)
      l.unlock();
  }

  public void update() {
    if (AbstractDungeon.player.isDead)
      return;

    checkForOpen();
    
    updateFade();
    updateCircle();
    for (CoopDoorLock l : locks)
      l.update();

    updateFadeInput();
    updateVfx();
  }
  
  public boolean open = false;
  public void checkForOpen() {
    if (open) { return; }
    
    for (RemotePlayer r: TogetherManager.players)
      if (!r.act4arrived)
        return;

    ((CoopDoorUnlockScreen)CardCrawlGame.mainMenuScreen.doorUnlockScreen).proceed();
    TogetherManager.log("Door opening, all players are here.");
    open = true;
  }

  private void updateFade() {
    if (this.fadeTimer != 0.0F) {
      this.fadeTimer -= Gdx.graphics.getDeltaTime();
      if (this.fadeTimer < 0.0F) {
        this.fadeTimer = 0.0F;
      } 
      this.fadeColor.a = Interpolation.fade.apply(0.0F, 1.0F, this.fadeTimer / 3.0F);
    } 
  }

  private void updateCircle() {
    if (this.animateCircle && this.fadeTimer == 0.0F)
      if (this.rotatingCircle) {
        this.circleTimer -= Gdx.graphics.getDeltaTime();
        this.circleAngle = Interpolation.fade.apply(0.0F, -45.0F, this.circleTimer / this.circleTime);
        if (this.circleTimer < 0.0F) {
          this.rotatingCircle = false;
          this.circleTimer = this.doorTime;
          this.circleAngle = 0.0F;
          CardCrawlGame.screenShake.mildRumble(this.doorTime - 0.25F);
          CardCrawlGame.sound.playA("RELIC_DROP_ROCKY", 0.3F);
          CardCrawlGame.sound.playA("RELIC_DROP_ROCKY", -0.6F);
          CardCrawlGame.sound.playA("EVENT_GOLDEN", -0.3F);
          CardCrawlGame.sound.playA("EVENT_WINDING", 0.5F);
        } 
      } else {
        this.circleTimer -= Gdx.graphics.getDeltaTime();
        if (this.circleTimer < 0.0F) {
          this.circleTimer = 0.0F;
          this.animateCircle = false;
          this.fadeTimer = 1.0f;
        } 
        this.bgColor.r = MathHelper.slowColorLerpSnap(this.bgColor.r, 0.0F);
        this.bgColor.g = MathHelper.slowColorLerpSnap(this.bgColor.g, 0.0F);
        this.bgColor.b = MathHelper.slowColorLerpSnap(this.bgColor.b, 0.0F);
        this.doorOffset = 1200.0F * Settings.scale * Interpolation.pow3.apply(1.0F, 0.0F, this.circleTimer / this.doorTime);
      }  
  }

  private void updateFadeInput() {
    if (!this.animateCircle && !this.rotatingCircle) {
      this.fadeTimer -= Gdx.graphics.getDeltaTime();
      this.fadeOutColor.a = 1.0F - this.fadeTimer;
      if (this.fadeTimer < 0.0F)
        exit(); 
      return;
    } 
  }

  private void updateVfx() {
    for (Iterator<AbstractGameEffect> i = this.effects.iterator(); i.hasNext(); ) {
      AbstractGameEffect e = i.next();
      e.update();
      if (e.isDone)
        i.remove(); 
    } 
  }
  
  private void exit() {
    for (CoopDoorLock l : locks)
      l.dispose();

    CardCrawlGame.mode = CardCrawlGame.GameMode.GAMEPLAY;
    CardCrawlGame.nextDungeon = "TheEnding";
    CardCrawlGame.music.fadeOutBGM();
    CardCrawlGame.music.fadeOutTempBGM();
    (AbstractDungeon.getCurrRoom()).phase = AbstractRoom.RoomPhase.COMPLETE;
    AbstractDungeon.fadeOut();
    AbstractDungeon.isDungeonBeaten = true;
  }
      
  public void render(SpriteBatch sb) {
    if (AbstractDungeon.player.isDead)
      return;

    sb.setColor(this.bgColor);
    sb.draw(ImageMaster.WHITE_SQUARE_IMG, 0.0F, 0.0F, Settings.WIDTH, Settings.HEIGHT);
    renderMainDoor(sb);
    renderCircleMechanism(sb);
    for (CoopDoorLock l : locks)
      l.render(sb);
    renderFade(sb);
    if (this.fadeOut) {
      sb.setColor(this.fadeOutColor);
      sb.draw(ImageMaster.WHITE_SQUARE_IMG, 0.0F, 0.0F, Settings.WIDTH, Settings.HEIGHT);
    } 
    for (AbstractGameEffect e : this.effects)
      e.render(sb); 

      renderPlayerList(sb);
  }
  
  private void renderFade(SpriteBatch sb) {
    sb.setColor(this.fadeColor);
    sb.draw(ImageMaster.WHITE_SQUARE_IMG, 0.0F, 0.0F, Settings.WIDTH, Settings.HEIGHT);
  }
  
  private void renderMainDoor(SpriteBatch sb) {
    sb.setColor(Color.WHITE);
    float yOffset = 0.0F;
    if (true)
      yOffset = -48.0F * Settings.scale; 
    sb.draw(doorLeft, Settings.WIDTH / 2.0F - 960.0F - this.doorOffset, Settings.HEIGHT / 2.0F - 600.0F + yOffset, 960.0F, 600.0F, 1920.0F, 1200.0F, this.renderScale, this.renderScale, 0.0F, 0, 0, 1920, 1200, false, false);
    sb.draw(doorRight, Settings.WIDTH / 2.0F - 960.0F + this.doorOffset, Settings.HEIGHT / 2.0F - 600.0F + yOffset, 960.0F, 600.0F, 1920.0F, 1200.0F, this.renderScale, this.renderScale, 0.0F, 0, 0, 1920, 1200, false, false);
  }
  
  private void renderCircleMechanism(SpriteBatch sb) {
    float yOffset = 0.0F;
    if (true)
      yOffset = -48.0F * Settings.scale; 
    sb.draw(circleRight, Settings.WIDTH / 2.0F - 960.0F + this.doorOffset, Settings.HEIGHT / 2.0F - 600.0F + yOffset, 960.0F, 600.0F, 1920.0F, 1200.0F, this.renderScale, this.renderScale, this.circleAngle, 2, 2, 1920, 1200, false, false);
    sb.draw(circleLeft, Settings.WIDTH / 2.0F - 960.0F - this.doorOffset, Settings.HEIGHT / 2.0F - 600.0F + yOffset, 960.0F, 600.0F, 1920.0F, 1200.0F, this.renderScale, this.renderScale, this.circleAngle, 2, 2, 1920, 1200, false, false);
  }

  private void renderPlayerList(SpriteBatch sb) {
    sb.setColor(Color.WHITE);

    for (RemotePlayerWidget widget : TopPanelPlayerPanels.playerWidgets) {
        widget.render(sb);
    }

    sb.setColor(Color.WHITE);
  }
}
