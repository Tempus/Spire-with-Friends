package chronoMods.ui.hud;

import chronoMods.TogetherManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.screens.stats.CharStat;
import com.megacrit.cardcrawl.ui.panels.TopPanel;

public class VersusTimer {

    public static long startTime;
    public static float timer;

    public VersusTimer() {}

    @SpirePatch(clz = AbstractDungeon.class, method="update")
    public static class maintainOptimalTime {
        public static void Postfix(AbstractDungeon __instance) {
            if (TogetherManager.gameMode == TogetherManager.mode.Versus)
                if (!CardCrawlGame.stopClock)
                    VersusTimer.timer = (float)(System.currentTimeMillis() - startTime) / 1000f;
        }
    }

    @SpirePatch(clz = CharStat.class, method="formatHMSM", paramtypez={float.class})
    public static class changeTimerFormat {
        public static String Postfix(String __result, float t) {
            if (TogetherManager.gameMode == TogetherManager.mode.Versus)
                return returnTimeString(VersusTimer.timer);
            return __result;
        }
    }


    @SpirePatch(clz = TopPanel.class, method="render")
    public static class renderAdditionalTimers {
        public static void Postfix(TopPanel __instance, SpriteBatch sb) {
            if (TogetherManager.gameMode == TogetherManager.mode.Versus && AbstractDungeon.screen != AbstractDungeon.CurrentScreen.MAP) {
                float iconSize = 64f * Settings.scale;
                sb.draw(ImageMaster.TIMER_ICON, Settings.WIDTH - 480f * Settings.scale, Settings.HEIGHT - iconSize, iconSize, iconSize);
                
                FontHelper.renderFontLeftTopAligned(
                    sb,
                    FontHelper.tipBodyFont,
                    VersusTimer.returnTimeString(VersusTimer.timer),
                    Settings.WIDTH - 420f * Settings.scale,
                    Settings.HEIGHT - (28f) * Settings.scale,
                    Settings.GOLD_COLOR);

                // __instance.timerHb.update();
                // if (__instance.timerHb.hovered)
                //     TipHelper.renderGenericTip(1550.0F * Settings.scale, Settings.HEIGHT - 120.0F * Settings.scale, "Splits", 
                //         "Guess we can put splits here?"); 
                // __instance.timerHb.render(sb);
            }
        }
    }

    public static String returnTimeString(float duration) {

        String res = "";
        int seconds = (int)(duration % 60L);
        int milliseconds = (int)((duration % 1) * 1000);
        float reducedDur = duration / 60L;
        int minutes = (int)(reducedDur % 60L);
        int hours = (int)duration / 3600;
        if (hours > 0) {
          res = String.format("%02d:%02d:%02d:%03d", new Object[] { Integer.valueOf(hours), Integer.valueOf(minutes), Integer.valueOf(seconds), Integer.valueOf(milliseconds) });
        } else {
          res = String.format("%02d:%02d:%03d", new Object[] { Integer.valueOf(minutes), Integer.valueOf(seconds), Integer.valueOf(milliseconds) });
        }

        return res;
    }
}
