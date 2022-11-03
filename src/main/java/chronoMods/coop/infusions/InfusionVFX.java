package chronoMods.coop.infusions;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Iterator;

public class InfusionVFX {
    // Infusion Effect
    @SpirePatch(clz=AbstractCard.class, method=SpirePatch.CLASS)
    public static class infusionEffect { 
        public static SpireField<Float> infuseTimer = new SpireField<>(() -> 0f); 
        public static SpireField<ArrayList<InfusionVFXBase>> infuseList = new SpireField<>(() -> new ArrayList()); 
    }
            
    @SpirePatch(clz = AbstractCard.class, method="renderPortraitFrame")
    public static class InfusionEffectUpdateRender {
        public static void Postfix(AbstractCard __instance, SpriteBatch sb, float x, float y) {

            Infusion infusion = Infusion.infusionField.infusion.get(__instance);
            if (infusion != null) {

                InfusionVFX.infusionEffect.infuseTimer.set(__instance, InfusionVFX.infusionEffect.infuseTimer.get(__instance) - Gdx.graphics.getDeltaTime());

                float t = InfusionVFX.infusionEffect.infuseTimer.get(__instance);

                if (t < 0.0F) {
                    try {
                      Constructor con = infusion.particle.getConstructor(AbstractCard.class);

                      InfusionVFXBase vfx = (InfusionVFXBase)con.newInstance(__instance);

                      InfusionVFX.infusionEffect.infuseList.get(__instance).add(vfx);
                      InfusionVFX.infusionEffect.infuseTimer.set(__instance, vfx.getFrequency());
                    } catch (Exception e) {
                      e.printStackTrace();
                    }
                } 

                for (Iterator<InfusionVFXBase> i = InfusionVFX.infusionEffect.infuseList.get(__instance).iterator(); i.hasNext(); ) {
                    InfusionVFXBase e = i.next();
                    e.update();
                    if (e.isDone)
                        i.remove(); 
                } 

                sb.setBlendFunction(770, 1);
                for (InfusionVFXBase e : InfusionVFX.infusionEffect.infuseList.get(__instance))
                    e.render(sb); 
                sb.setBlendFunction(770, 771);

            }
        }
    }
}