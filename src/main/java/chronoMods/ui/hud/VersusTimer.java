package chronoMods.ui.hud;

import com.evacipated.cardcrawl.modthespire.lib.*;

import com.megacrit.cardcrawl.ui.panels.TopPanel;
import com.megacrit.cardcrawl.core.Settings;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.FontHelper;

import chronoMods.*;
import chronoMods.steam.*;
import chronoMods.ui.deathScreen.*;
import chronoMods.ui.hud.*;
import chronoMods.ui.lobby.*;
import chronoMods.ui.mainMenu.*;
import chronoMods.utilities.*;

public class VersusTimer {

    public VersusTimer() {}

    @SpirePatch(clz = TopPanel.class, method="render")
    public static class renderAdditionalTimers {
        public static void Postfix(TopPanel __instance, SpriteBatch sb) {
            if (TogetherManager.gameMode == TogetherManager.mode.Versus) {
                sb.draw(ImageMaster.TIMER_ICON, Settings.WIDTH - 380f * Settings.scale, Settings.HEIGHT - ((64f) * Settings.scale), 64f * Settings.scale, 64f * Settings.scale);
                
                FontHelper.renderFontLeftTopAligned(
                    sb,
                    FontHelper.tipBodyFont,
                    VersusTimer.returnTimeString(CardCrawlGame.playtime),
                    Settings.WIDTH - 320f * Settings.scale,
                    Settings.HEIGHT - (28f) * Settings.scale,
                    Settings.GOLD_COLOR);
            }
        }
    }

    public static String returnTimeString(float duration) {

        String res = "";
        int seconds = (int)(duration % 60L);
        int milliseconds = (int)((duration % 1) * 100);
        duration /= 60L;
        int minutes = (int)(duration % 60L);
        int hours = (int)CardCrawlGame.playtime / 3600;
        if (hours > 0) {
          res = String.format("%02d:%02d:%02d:%03d", new Object[] { Integer.valueOf(hours), Integer.valueOf(minutes), Integer.valueOf(seconds), Integer.valueOf(milliseconds) });
        } else {
          res = String.format("%02d:%02d:%03d", new Object[] { Integer.valueOf(minutes), Integer.valueOf(seconds), Integer.valueOf(milliseconds) });
        }

        return res;
    }
}
