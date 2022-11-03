package chronoMods.chat.TextEffects;

import chronoMods.chat.ChatText;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ScreenUtils;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;

public class ScreenshotEffect extends TextEffect {
    public TextureRegion texture;

    public ScreenshotEffect() {
        super();
    }

    @Override
    public void onApply(ChatText text) {
        super.onApply(text);
        this.texture = ScreenUtils.getFrameBufferTexture();
    }

    @Override
    public boolean update(float dt) {
        return false;
    }

    @Override
    public void render(SpriteBatch sb, float x, float y) {
        super.render(sb, x, y);
        sb.setColor(Settings.BLUE_RELIC_COLOR);
        sb.draw(ImageMaster.WHITE_SQUARE_IMG, x, y - 30.0F * Settings.scale, this.Text.hb.width, 2.0F);
        if (this.Text.hb.hovered) {
            sb.setColor(Color.WHITE.cpy());
            sb.draw(texture, Settings.WIDTH * 0.15F, Settings.HEIGHT * 0.15F, Settings.WIDTH * 0.7F,
                    Settings.HEIGHT * 0.7F);
        }
    }

    @Override
    public TextEffect makeCopy() {
        return new ScreenshotEffect();
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    public enum InspectType {
        NONE, RELIC, CARD, POTION
    }

}
