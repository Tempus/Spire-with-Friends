package chronoMods.coop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.GameCursor;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.screens.VictoryScreen;
import com.megacrit.cardcrawl.rooms.*;
import com.megacrit.cardcrawl.cutscenes.*;
import java.util.ArrayList;
import java.awt.Desktop;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;

import chronoMods.*;
import chronoMods.coop.drawable.*;
import chronoMods.ui.hud.*;
import chronoMods.ui.lobby.*;
import chronoMods.network.*;

public class CoopCutscene extends Cutscene implements Disposable {

    // Injections
    @SpirePatch(clz=TrueVictoryRoom.class, method=SpirePatch.CONSTRUCTOR)
    public static class AddEndingCutscene
    {
        public static void Postfix(TrueVictoryRoom d)
        {
            if (TogetherManager.gameMode != TogetherManager.mode.Coop) { return; }

            d.cutscene = TogetherManager.cutscene;
        }
    }
    @SpirePatch(clz=TrueVictoryRoom.class, method="onPlayerEntry")
    public static class TriggerVictory
    {
        public static void Postfix(TrueVictoryRoom d)
        {
            if (TogetherManager.gameMode != TogetherManager.mode.Coop) { return; }

            NetworkHelper.sendData(NetworkHelper.dataType.Victory);
        }
    }

  static public Button patreonButton = new Button(Settings.WIDTH/2f, 36f * Settings.yScale, "", ImageMaster.loadImage("chrono/images/patreon.png"));

  public int currentScene = 0;
  
  public float darkenTimer = 2.0F;
  public float fadeTimer = 1.0F;
  public float endingTimer = 0.0F;
  
  public Color screenColor;
  public Color bgColor;
  
  public ArrayList<CutscenePanel> panels = new ArrayList<>();
  
  public Texture bgImg;

  public boolean lastSection = false;
  public boolean isDone = false;
  public static boolean shouldRenderPlayers = true;
  
  public CoopCutscene() {
    super(AbstractPlayer.PlayerClass.IRONCLAD);
    this.bgImg = ImageMaster.loadImage("chrono/images/cutscenes/CutsceneBG.jpg");

    this.bgColor = Color.WHITE.cpy();
    this.screenColor = new Color(0.0F, 0.0F, 0.0F, 0.0F);
  }
  
  public void update() {

    patreonButton.update();

    if (patreonButton.hb.clicked == true || (patreonButton.hb.hovered && InputHelper.justClickedLeft)) {
        NewScreenUpdateRender.openWebpage("https://www.patreon.com/chronometrics");
        patreonButton.hb.clicked = false;
    }

    updateFadeOut();
    updateFadeIn();
    for (CutscenePanel p : this.panels)
      p.update(); 
    updateSceneChange();
  }
  
  public void whenDone() {
    dispose();
    this.bgColor.a = 0.0F;
    this.screenColor.a = 0.0F;
    openVictoryScreen();
  }
  
  public void playerWins(RemotePlayer p) {
    String character = "mod";

    switch (p.character.chosenClass) {
      case IRONCLAD:
        character = "ironclad";
        break;
      case THE_SILENT:
        character = "silent";
        break;
      case DEFECT:
        character = "defect";
        break;
      case WATCHER:
        character = "watcher";
        break;
    }

    String panelPath = "chrono/images/cutscenes/" + TogetherManager.players.size() + "/" + (TogetherManager.players.indexOf(p)+1) + "/" + character + ".png";
    TogetherManager.log("Displaying panel at: " + panelPath);
    CutscenePanel panel = new CutscenePanel(panelPath, "ATTACK_HEAVY");
    this.panels.add(panel);
    panel.activate();

    // Check to see if everyone is done
    for (RemotePlayer r: TogetherManager.players) 
      if (!r.victory)
        return;

    // If we're all done, add the last panel as well.
    TogetherManager.log("All panels acquired");
    this.endingTimer = 8.0F;
    panel = new CutscenePanel("chrono/images/cutscenes/AllTogether.png");
    this.panels.add(panel);
    panel.activate();
  }

  public void updateSceneChange() {
    if (endingTimer > 0f) {
      this.endingTimer -= Gdx.graphics.getDeltaTime();
      if (this.endingTimer < 0.0F) {
        TogetherManager.log("Let's skedaddle.");
        for (CutscenePanel p : this.panels)
          p.fadeOut(); 
        this.isDone = true;
      }
    }
  }
  
  public void openVictoryScreen() {
    GameCursor.hidden = false;
    AbstractDungeon.victoryScreen = new VictoryScreen(null);
  }
  
  public void updateFadeIn() {
    if (this.darkenTimer == 0.0F && !isDone) {
      this.fadeTimer -= Gdx.graphics.getDeltaTime();
      if (this.fadeTimer < 0.0F)
        this.fadeTimer = 0.0F; 
      this.screenColor.a = this.fadeTimer;
    } 
  }
  
  public void updateFadeOut() {
    if (isDone && this.darkenTimer > 0.0F) {
      this.darkenTimer -= Gdx.graphics.getDeltaTime();
      this.screenColor.a = 1.0F - this.darkenTimer;
      if (this.darkenTimer <= 0.0F)
        whenDone();
    }
  }
  
  public void render(SpriteBatch sb) {
    if (this.currentScene <= 1) {
      sb.setColor(Color.BLACK);
      sb.draw(ImageMaster.WHITE_SQUARE_IMG, 0.0F, 0.0F, Settings.WIDTH, Settings.HEIGHT);
    } 
  }
  
  public void renderAbove(SpriteBatch sb) {
    if (this.bgImg != null) {
      sb.setColor(this.bgColor);
      renderImg(sb, this.bgImg);
    } 
    renderPanels(sb);

    if (endingTimer <= 0f && !isDone)
      renderPlayerList(sb);

    sb.setColor(this.screenColor);
    sb.draw(ImageMaster.WHITE_SQUARE_IMG, 0.0F, 0.0F, Settings.WIDTH, Settings.HEIGHT);

    patreonButton.render(sb);
    if (patreonButton.hb.clicked == true || (patreonButton.hb.hovered && InputHelper.justClickedLeft)) {
        NewScreenUpdateRender.openWebpage("https://www.patreon.com/chronometrics");
        patreonButton.hb.clicked = false;
    }
  }
  
  public void renderPanels(SpriteBatch sb) {
    for (CutscenePanel p : this.panels)
      p.render(sb); 
  }
  
  public void renderPlayerList(SpriteBatch sb) {
    sb.setColor(Color.WHITE);
    shouldRenderPlayers = false;

    for (RemotePlayerWidget widget : TopPanelPlayerPanels.playerWidgets) {
        widget.xoffset = 780f * Settings.scale;
        widget.yoffset = -(60f * Settings.scale);
        widget.render(sb);
    }

    sb.setColor(Color.WHITE);
  }

  public void renderImg(SpriteBatch sb, Texture img) {
    if (Settings.isSixteenByTen) {
      sb.draw(img, 0.0F, 0.0F, Settings.WIDTH, Settings.HEIGHT);
    } else {
      sb.draw(img, 0.0F, -50.0F * Settings.scale, Settings.WIDTH, Settings.HEIGHT + 110.0F * Settings.scale);
    } 
  }
  
  public void dispose() {
    if (this.bgImg != null) {
      this.bgImg.dispose();
      this.bgImg = null;
    } 
    for (CutscenePanel p : this.panels)
      p.dispose(); 
  }
}
