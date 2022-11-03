package chronoMods.chat.TextEffects;

import chronoMods.chat.ChatText;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.Settings;

public class WaveEffect extends TextEffect {

    public WaveEffect() {
        super();
    }

    @Override
    public void onApply(ChatText text) {
        super.onApply(text);
        Text.timer = MathUtils.random(1.5707964F);
    }

    @Override
    public boolean update(float dt) {
        Text.timer += dt * 6.0F;
        Text.offset_y = (float) Math.cos(Text.timer) * Settings.scale * ChatText.WAVY_DIST;
        return false;
    }

    @Override
    public TextEffect makeCopy() {
        return new WaveEffect();
    }

}
