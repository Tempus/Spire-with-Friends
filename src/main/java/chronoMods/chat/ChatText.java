package chronoMods.chat;

import chronoMods.chat.TextEffects.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.MathHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class ChatText {
    public BitmapFont font;
    public ArrayList<TextEffect> effects;
    public Hitbox hb;
    public Color wColor;
    public String word;
    public float offset;
    public float x;
    public float y;
    public float target_x;
    public float timer = 0.0F;
    public float target_y;
    public float offset_x = 0.0F;
    public float offset_y = 0.0F;
    public Color color;
    public Color targetColor;
    public float scale = 1.0F;
    public float targetScale = 1.0F;
    public static GlyphLayout gl;
    public static final float COLOR_LERP_SPEED = 8.0F;
    public static final float SHAKE_AMT = 2.0F * Settings.scale;

    public static final float WAVY_DIST = 3.0F;
    public static final float SHAKE_INTERVAL = 0.02F;

    // All Colors
    public static final HashMap<String, Color> COLOR_MAP = new HashMap<>();
    // All text Effects
    public static final HashMap<String[], TextEffect> EFFECT_MAP = new HashMap<>();
    // All special characters that can be entered
    public static final ArrayList<String> WHITE_LIST = new ArrayList<>();

    static {
        COLOR_MAP.put("r", Settings.RED_TEXT_COLOR.cpy());
        COLOR_MAP.put("g", Settings.GREEN_TEXT_COLOR.cpy());
        COLOR_MAP.put("b", Settings.BLUE_TEXT_COLOR.cpy());
        COLOR_MAP.put("p", Settings.PURPLE_COLOR.cpy());
        COLOR_MAP.put("y", Settings.GOLD_COLOR.cpy());
        
        EFFECT_MAP.put(new String[] { "@", "@" }, new ShakeEffect());
        EFFECT_MAP.put(new String[] { "~", "~" }, new WaveEffect());
        EFFECT_MAP.put(new String[] { "(", ")" }, new InspectEffect());
        EFFECT_MAP.put(new String[] { "（", "）" }, new InspectEffect());

        // this effect doesn't work because I need to send the image to all players, I
        // don't know how to do it :(
        // EFFECT_MAP.put(new String[] { "<", ">" }, new ScreenshotEffect());

        String[] safeStrings = new String[] {
                "#", "!", "！", ",", "，", ".", "。", " ", "?", "？", ":", "：", "_", "\"", "”", "“",
                "\'", "‘", "’", "-"
        };
        for (String str : safeStrings) {
            WHITE_LIST.add(str);
        }
        for (String[] strs : EFFECT_MAP.keySet()) {
            for (String str : strs) {
                if (!WHITE_LIST.contains(str)) {
                    WHITE_LIST.add(str);
                }
            }
        }
    }

    public ChatText(BitmapFont font, String word, ArrayList<TextEffect> effect, Color wColor, float offset) {
        if (gl == null) {
            gl = new GlyphLayout();
        }
        this.font = font;
        this.effects = effect;
        this.wColor = wColor;
        this.word = word;
        this.offset = offset;
        this.x = 0.0F;
        this.y = 0.0F;
        this.target_x = x;
        this.target_y = y;
        if (wColor == null) {
            wColor = Color.WHITE.cpy();
        }
        this.targetColor = wColor;
        this.color = this.targetColor.cpy();
        gl.setText(font, word);
        this.hb = new Hitbox(this.x, this.y, gl.width, gl.height);
        for (TextEffect e : effect) {
            e.onApply(this);
        }
    }

    public void update() {
        this.hb.update();
        if (this.x != this.target_x) {
            this.x = MathUtils.lerp(this.x, this.target_x, Gdx.graphics.getDeltaTime() * 12.0F);
        }
        if (this.y != this.target_y) {
            this.y = MathUtils.lerp(this.y, this.target_y, Gdx.graphics.getDeltaTime() * 12.0F);
        }

        // this.color = this.color.lerp(this.targetColor, Gdx.graphics.getDeltaTime() * COLOR_LERP_SPEED);

        if (this.scale != this.targetScale) {
            this.scale = MathHelper.scaleLerpSnap(this.scale, this.targetScale);
        }

        applyEffects();
    }

    private void applyEffects() {
        float dt = Gdx.graphics.getDeltaTime();
        for (int i = effects.size() - 1; i > -1; i--) {
            TextEffect e = effects.get(i);
            if (e.update(dt)) {
                effects.remove(e);
            }
        }
    }

    public void render(SpriteBatch sb, float ox, float oy) {
        this.hb.move(ox + this.offset + hb.width / 2.0F, oy + hb.height / 2.0F - 30.0F * Settings.scale);
        this.font.setColor(this.color);
        this.font.getData().setScale(this.scale);

        this.font.draw((Batch) sb, this.word, ox + this.offset + this.x + this.offset_x, oy + this.y + this.offset_y);
        this.font.getData().setScale(1.0F);
        for (TextEffect e : effects) {
            e.render(sb, ox + this.offset, oy);
        }
        this.hb.render(sb);
    }

    public void dispose() {
        for (TextEffect e : effects) {
            e.dispose();
        }
        effects.clear();
    }

    public static Color IdentifyWordColor(String word) {
        // #r based colour keys
        if (word.length() > 2 && word.startsWith("#")) {
            String key = String.valueOf(word.charAt(1));
            if (COLOR_MAP.containsKey(key)) {
                return COLOR_MAP.get(key);
            }
        }

        // [] based colour keys
        if (word.length() > 10 && word.startsWith("[")) {
            return Color.valueOf(word.substring(1,10));
        }

        return null;
    }

    public static TextEffect IdentifyWordEffect(String word) {
        if (word.length() > 2) {
            char pre = word.charAt(0);
            char post = word.charAt(word.length() - 1);
            for (String[] strs : EFFECT_MAP.keySet()) {
                if (strs[0].equals(String.valueOf(pre)) && strs[1].equals(String.valueOf(post))) {
                    return EFFECT_MAP.get(strs).makeCopy();
                }
            }
        }
        return new NullEffect();
    }
}
