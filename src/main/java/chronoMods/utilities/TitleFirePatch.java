package chronoMods.utilities;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.scene.LogoFlameEffect;

public class TitleFirePatch
{

    public static Color[] colourChoices = new Color[] {
      Color.RED.cpy(),
      Color.BLUE.cpy(),
      Color.GREEN.cpy(),
      Color.YELLOW.cpy(),
      Color.ORANGE.cpy(),
      Color.PINK.cpy(),
      Color.PURPLE.cpy()
    };

    @SpirePatch(clz = LogoFlameEffect.class, method="render", paramtypez = {SpriteBatch.class, float.class, float.class})
    public static class MoveFlamesOver {
        public static void Prefix(LogoFlameEffect __instance, SpriteBatch sb, @ByRef float x[], @ByRef float y[]) {
            x[0] = x[0] - 145.0F * Settings.scale;
            y[0] = y[0] - 32.0F * Settings.scale;

            Color color = colourChoices[MathUtils.random(0,6)];

            ReflectionHacks.setPrivate(__instance, AbstractGameEffect.class, "color", color);   
        }
    }
}
