package chronoMods.chat.TextEffects;

import chronoMods.chat.ChatText;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.ImageMaster;

import java.util.HashMap;

public class InspectEffect extends TextEffect {

    public InspectType type = InspectType.NONE;
    public AbstractCard card;

    public static final HashMap<String, AbstractCard> CARD_MAP = new HashMap<>();

    public InspectEffect() {
        super();
    }

    @Override
    public void onApply(ChatText text) {
        super.onApply(text);
        text.word = text.word.replace("_", " ");
        String[] group = text.word.split(":");
        if (group.length != 2) {
            group = text.word.split("：");
        }
        // if (group.length != 2)
        // return;
        // if (group[0].equals(TEXT[0])) {
        // type = InspectType.RELIC;
        // } else if (group[0].equals(TEXT[1])) {
        type = InspectType.CARD;
        // } else if (group[0].equals(TEXT[2])) {
        // type = InspectType.POTION;
        // }

        switch (type) {
            case CARD:
                // String name = group[1];
                String name = text.word;
                boolean upgraded = false;
                if (name.endsWith("+")) {
                    name = name.substring(0, name.length() - 1);
                    upgraded = true;
                }

                if (CARD_MAP.containsKey(name)) {
                    this.card = CARD_MAP.get(name).makeCopy();
                    if (upgraded && this.card.canUpgrade())
                        this.card.upgrade();
                    return;
                }
                for (AbstractCard c : CardLibrary.cards.values()) {
                    if (c.name.equals(name)) {
                        this.card = c.makeCopy();
                        if (upgraded && this.card.canUpgrade())
                            this.card.upgrade();
                        CARD_MAP.put(name, c.makeCopy());
                        break;
                    }
                }

                if (this.card == null) {
                    this.type = InspectType.NONE;
                }
                break;
            default:
                break;
        }
        if (this.type != InspectType.NONE) {
            this.Text.color = Settings.BLUE_RELIC_COLOR.cpy();
            this.Text.targetColor = Settings.BLUE_RELIC_COLOR.cpy();
        }
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
            switch (this.type) {
                case CARD:
                    if (this.card != null) {
                        this.card.current_x = x + 300.0F * Settings.scale;
                        this.card.current_y = y + 30.0F * Settings.scale;
                        this.card.render(sb);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public TextEffect makeCopy() {
        return new InspectEffect();
    }

    @Override
    public void dispose() {
        super.dispose();
        this.card = null;
    }

    public enum InspectType {
        NONE, RELIC, CARD, POTION
    }

}
