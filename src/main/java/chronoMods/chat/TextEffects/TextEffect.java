package chronoMods.chat.TextEffects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import chronoMods.chat.ChatText;

public abstract class TextEffect {
    public ChatText Text;

    public TextEffect() {
    }

    public void onApply(ChatText text) {
        this.Text = text;
    }

    public abstract boolean update(float dt);

    public void render(SpriteBatch sb, float x, float y) {
    }

    public abstract TextEffect makeCopy();

    public void dispose() {
    }
}
