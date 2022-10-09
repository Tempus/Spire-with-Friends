package chronoMods.utilities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.screens.mainMenu.MenuButton;

public class RainbowPatchNotes
{
    public static float low = 0.75f;
    public static Color RAINBOW = new Color(1f,low,low,1f);

    @SpirePatch(clz = MenuButton.class, method="render")
    public static class Rainbow {
        @SpireInsertPatch(rloc=254-202, localvars="sliderX")
        public static SpireReturn Insert(MenuButton __instance, SpriteBatch sb, String ___label, float ___x, float sliderX) {

          if (__instance.result == MenuButton.ClickResult.PATCH_NOTES) {
            // Red goes up
            if (RAINBOW.r < 1f && RAINBOW.g <= low && RAINBOW.b >= 1f)
              RAINBOW.r += Gdx.graphics.getDeltaTime();

            // Red goes down
            else if (RAINBOW.r > low && RAINBOW.g >= 1f && RAINBOW.b <= low)
              RAINBOW.r -= Gdx.graphics.getDeltaTime();

            // Green goes up
            else if (RAINBOW.r >= 1f && RAINBOW.g < 1f && RAINBOW.b <= low)
              RAINBOW.g += Gdx.graphics.getDeltaTime();

            // Green goes down
            else if (RAINBOW.r <= low && RAINBOW.g > low && RAINBOW.b >= 1f)
              RAINBOW.g -= Gdx.graphics.getDeltaTime();

            // Blue goes up
            else if (RAINBOW.r <= low && RAINBOW.g >= 1f && RAINBOW.b < 1f)
              RAINBOW.b += Gdx.graphics.getDeltaTime();

            // Blue goes down
            else if (RAINBOW.r >= 1f && RAINBOW.g <= low && RAINBOW.b > low)
              RAINBOW.b -= Gdx.graphics.getDeltaTime();

            RAINBOW.clamp();
            FontHelper.renderSmartText(sb, FontHelper.buttonLabelFont, ___label, ___x + __instance.FONT_X + sliderX, __instance.hb.cY + __instance.FONT_OFFSET_Y, 9999.0F, 1.0F, RAINBOW);

            return SpireReturn.Return(null);
          }
          return SpireReturn.Continue();
        }
    }
}
