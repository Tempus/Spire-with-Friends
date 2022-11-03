package chronoMods.chat.TextEffects;

import chronoMods.chat.ChatText;
import com.badlogic.gdx.math.MathUtils;

public class ShakeEffect extends TextEffect {

    public ShakeEffect() {
        super();
    }

    @Override
    public boolean update(float dt) {
        Text.timer -= dt;
        if (Text.timer < 0.0F) {
            Text.offset_x = MathUtils.random(-ChatText.SHAKE_AMT, ChatText.SHAKE_AMT);
            Text.offset_y = MathUtils.random(-ChatText.SHAKE_AMT, ChatText.SHAKE_AMT);
            Text.timer = ChatText.SHAKE_INTERVAL;
        }
        return false;
    }

    @Override
    public TextEffect makeCopy() {
        return new ShakeEffect();
    }

}
