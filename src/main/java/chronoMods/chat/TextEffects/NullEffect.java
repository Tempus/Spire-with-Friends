package chronoMods.chat.TextEffects;

public class NullEffect extends TextEffect {

    public NullEffect() {
        super();
    }

    @Override
    public boolean update(float dt) {
        return false;
    }

    @Override
    public TextEffect makeCopy() {
        return new NullEffect();
    }

}
