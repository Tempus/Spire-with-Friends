package chronoMods.chat;

import java.util.ArrayList;
import java.util.Scanner;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.*;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.helpers.*;
import com.megacrit.cardcrawl.helpers.input.*;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.helpers.Hitbox;

import chronoMods.chat.TextEffects.NullEffect;
import chronoMods.chat.TextEffects.TextEffect;
import basemod.BaseMod;
import basemod.interfaces.RenderSubscriber;
import basemod.interfaces.PostUpdateSubscriber;
import chronoMods.network.NetworkHelper;
import chronoMods.ui.lobby.*;
import chronoMods.*;

public class ChatScreen implements PostUpdateSubscriber, RenderSubscriber {

    public boolean isOpen = false;
    public boolean isHidden = false;

    public ArrayList<String> Messages;
    public ArrayList<ArrayList<ChatText>> Texts;
    public int ShowIndex = 0;
    public String TypingMsg = "";
    public int TypingCursor = 0;
    public float showTimer = 0.0F;
    public Color CloseColor = new Color(0.0F, 0.0F, 0.0F, 0.0F);

    public GlyphLayout GL;

    public static int MAX_MSG_CAP = 200;

    public static final int OpenKey = Keys.TAB;
    public static final int SendKey = Keys.ENTER;
    public static final int RemoveKey = Keys.BACKSPACE;

    public static final Color BG_OPEN_COLOR = new Color(0.0F, 0.0F, 0.0F, 0.75F);
    public static final Color FADED_FONT_COLOR = new Color(1.0F, 1.0F, 1.0F, 0.25F);

    public static final BitmapFont FONT = FontHelper.turnNumFont;

    public static final int MAX_MSG_SIZE = 8;

    public static final float LINE_HEIGHT = 30.0F * Settings.scale;
    public static final float CHAT_W = 800.0F * Settings.scale;
    public static final float CHAT_H = MAX_MSG_SIZE * LINE_HEIGHT + 10.0F * Settings.scale;
    public static final float CHAT_X = Settings.WIDTH / 2f - CHAT_W / 2f;
    public static final float CHAT_Y = Settings.HEIGHT - 192.0F * Settings.scale;

    public static final float CHAT_W_LOBBY = 600.0F * Settings.scale;
    public static final float CHAT_X_LOBBY = 32.0F * Settings.scale;
    public static final float CHAT_Y_LOBBY = Settings.HEIGHT - 32.0F * Settings.scale - + (float) Math.floor(LINE_HEIGHT);

    public static String[] CHAT_STRINGS = CardCrawlGame.languagePack.getUIString("Chat").TEXT;

    InputProcessor vanillaInput;
    InputProcessor chatInput;

    public Hitbox hb;
    ArrayList<PowerTip> tips = new ArrayList();

    public ChatScreen() {
        vanillaInput = Gdx.input.getInputProcessor();
        chatInput = new ChatTextProcessor(this);

        BaseMod.subscribe(this);
        this.Messages = new ArrayList<>();
        this.Texts = new ArrayList<>();
        GL = new GlyphLayout();

        this.hb = new Hitbox(24f*Settings.scale,24f*Settings.scale);
        makeTips();
    }

    public void makeTips() {
        tips.add(new PowerTip(CHAT_STRINGS[0], CHAT_STRINGS[1]));
        tips.add(new PowerTip(CHAT_STRINGS[2], CHAT_STRINGS[3]));
        tips.add(new PowerTip(CHAT_STRINGS[4], CHAT_STRINGS[5]));
    }

    public void addMsg(String msg, Color color) {
        String str = String.format("[#%s]%s[]", color.toString(), msg);
        Messages.add(0, str);
        makeText(str);
        if (Messages.size() > MAX_MSG_CAP) {
            Messages.remove(Messages.size() - 1);
            ArrayList<ChatText> text = Texts.remove(Texts.size() - 1);
            for (ChatText t : text) {
                t.dispose();
            }
        }
        this.showTimer = 5.0F;
    }

    public void addMsg(String user, String msg, Color color) {
        String str = String.format("[#%s]%s[]: %s", color.toString(), user, msg);
        TogetherManager.log(str);
        Messages.add(0, str);
        makeText(str);
        if (Messages.size() > MAX_MSG_CAP) {
            Messages.remove(Messages.size() - 1);
            ArrayList<ChatText> text = Texts.remove(Texts.size() - 1);
            for (ChatText t : text) {
                t.dispose();
            }
        }
        this.showTimer = 5.0F;
    }

    public void makeText(String msg) {
        try {
            ArrayList<ChatText> texts = new ArrayList<>();
            Scanner s = new Scanner(msg);
            float x = 0.0F;
            while (s.hasNext()) {
                ArrayList<TextEffect> es = new ArrayList<>();
                String w = s.next();

                // Decide on the colour
                Color c = ChatText.IdentifyWordColor(w);

                // Remove any #y/#r etc colour tags
                if (c != null && w.startsWith("#")) { w = w.substring(2); }

                // Remove any [] colour tags
                if (c != null && w.startsWith("[")) { w = w.replaceAll("\\[.*?\\]", ""); }

                // Word Effect Tags @, ~, ()
                TextEffect e = ChatText.IdentifyWordEffect(w);
                if (!(e instanceof NullEffect)) {
                    w = w.substring(1, w.length() - 1);
                    es.add(e);
                }

                // Set the GL Text
                GL.setText(FONT, w);

                // Add the word to the render list
                texts.add(new ChatText(FONT, w, es, c, x));
                x += GL.width;
                if (!Settings.lineBreakViaCharacter) {
                    x += 8.0F * Settings.scale;
                }
            }
            s.close();
            Texts.add(0, texts);
        } catch (Exception ex) {
            ex.printStackTrace();
            ArrayList<ChatText> texts = new ArrayList<>();
            texts.add(new ChatText(FONT, msg, new ArrayList<>(), Color.WHITE.cpy(), 0.0F));
            Texts.add(0, texts);
        }

    }

    public void clear() {
        this.Messages.clear();
        this.Texts.clear();
        this.ShowIndex = 0;
    }

    public boolean isMouseInScreen() {
        float x = InputHelper.mX;
        float y = InputHelper.mY;

        return x > CHAT_X && x < CHAT_X + CHAT_W && y > CHAT_Y && y < CHAT_Y + CHAT_H;
    }

    public boolean shouldExist () {
        if (CardCrawlGame.isInARun()) { return true; }

        if (CardCrawlGame.mainMenuScreen == null) { return false; }
        if (CardCrawlGame.mainMenuScreen.screen == NewGameScreen.Enum.CREATEMULTIPLAYERGAME) { return true; }

        return false;
    }

    public boolean shouldHide () {
        if (isHidden) { return true; }
        if (isOpen || showTimer > 0.0F) { return false; }
        if (CardCrawlGame.isInARun() && AbstractDungeon.screen == AbstractDungeon.CurrentScreen.NONE) { return false; }
        if (!CardCrawlGame.isInARun()) { return false; }

        return true;
    }

    @Override
    public void receiveRender(SpriteBatch sb) {
        if (!shouldExist()) { return; }

        int size;
        if (Messages.size() < MAX_MSG_SIZE) {
            size = Messages.size() + 1;
        } else {
            size = MAX_MSG_SIZE;
        }

        // Calculate position for in Dungeon
        float x = CHAT_X + 10.0F * Settings.scale;
        float y = CHAT_Y + (float) Math.floor(LINE_HEIGHT) - 5.0F * Settings.scale;
        
        // Position for in Lobby
        if (!CardCrawlGame.isInARun()) {
            x = CHAT_X_LOBBY + 10.0F * Settings.scale;
            y = CHAT_Y_LOBBY + (float) Math.floor(LINE_HEIGHT) - 5.0F * Settings.scale;
        }  

        // Box drawing
        sb.setColor(BG_OPEN_COLOR);
        if (!shouldHide()) {
            if (CardCrawlGame.isInARun()) {
                sb.draw(ImageMaster.WHITE_SQUARE_IMG, CHAT_X, CHAT_Y - 5.0F * Settings.scale, CHAT_W, LINE_HEIGHT + 10.0F * Settings.scale);
                sb.setColor(isOpen ? BG_OPEN_COLOR : CloseColor);
                sb.draw(ImageMaster.WHITE_SQUARE_IMG, CHAT_X, CHAT_Y-CHAT_H, CHAT_W, CHAT_H);
            } else {
                sb.draw(ImageMaster.WHITE_SQUARE_IMG, CHAT_X_LOBBY, CHAT_Y_LOBBY - 5.0F * Settings.scale, CHAT_W_LOBBY, LINE_HEIGHT + 10.0F * Settings.scale);
                sb.setColor(isOpen ? BG_OPEN_COLOR : CloseColor);
                sb.draw(ImageMaster.WHITE_SQUARE_IMG, CHAT_X_LOBBY, CHAT_Y_LOBBY-CHAT_H, CHAT_W_LOBBY, CHAT_H);            
            }
        }

        if (isOpen) {
            FONT.draw(sb, ">: " + TypingMsg, x, y);
            float w;
            if (TypingCursor > 0) {
                GL.setText(FONT, ">: " + TypingMsg.substring(0, TypingCursor));
            } else {
                GL.setText(FONT, ">: ");
            }
            w = GL.width - 10.0F * Settings.scale;
            if (!Settings.lineBreakViaCharacter) {
                w += 2.0F * Settings.scale;
            }
            FONT.draw(sb, " |", x + w, y);
        } else if (!shouldHide()) {
            FONT.setColor(FADED_FONT_COLOR);
            FONT.draw(sb, CHAT_STRINGS[6] + TypingMsg, x, y);
            FONT.setColor(Color.WHITE.cpy());            
        }

        // Help Tooltip
        if (!shouldHide()) {
            FONT.setColor(FADED_FONT_COLOR);
            if (CardCrawlGame.isInARun())
                FONT.draw(sb, "?", CHAT_X + CHAT_W, CHAT_Y+ (float) Math.floor(LINE_HEIGHT));
            else
                FONT.draw(sb, "?", CHAT_X_LOBBY + CHAT_W_LOBBY, CHAT_Y_LOBBY+ (float) Math.floor(LINE_HEIGHT));
            this.hb.render(sb);
            FONT.setColor(Color.WHITE.cpy());            
        }

        if (size == 1 || (!isOpen && showTimer <= 0.0F) || shouldHide()) {
            return;
        }

        y -= 5f * Settings.scale;
        for (int i = ShowIndex; i < ShowIndex + size - 1; i++) {
            y -= (float) Math.floor(LINE_HEIGHT);
            // FONT.draw(sb, Messages.get(i), x, y);
            ArrayList<ChatText> texts = Texts.get(i);
            for (ChatText text : texts) {
                text.targetColor.a = CloseColor.a*2;
                text.color.a = CloseColor.a*2;
                text.render(sb, x, y);
            }
        }
    }

    @Override
    public void receivePostUpdate() {
        if (!shouldExist()) { return; }

        if (Gdx.input.isKeyJustPressed(OpenKey)) {
            isOpen = !isOpen;
            if (isOpen) {
                Gdx.input.setInputProcessor(chatInput);
                CloseColor.a = 0.5f;
                this.showTimer = 0f;
            }
            else {
                Gdx.input.setInputProcessor(vanillaInput);
                CloseColor.a = 0f;
            }
        }

        // Help Tooltip
        if (CardCrawlGame.isInARun())
            this.hb.update(CHAT_X + CHAT_W, CHAT_Y);
        else
            this.hb.update(CHAT_X_LOBBY + CHAT_W_LOBBY, CHAT_Y_LOBBY);

        if (this.hb.hovered && !shouldHide()) 
            TipHelper.queuePowerTips(this.hb.x * 1.03f, this.hb.y + 50.0F * Settings.scale, tips);


        if (this.showTimer > 0.0F && !shouldHide()) {
            this.showTimer -= Gdx.graphics.getDeltaTime();
            if (this.showTimer < 0.0F) {
                this.showTimer = 0.0F;
            }
            if (this.showTimer > 1f) {
                CloseColor.a = 0.5f;
            } else {
                CloseColor.a = this.showTimer * .5f;
            }
        }

        if (isOpen) {
            if (TypingMsg != "") {
                if (Gdx.input.isKeyJustPressed(SendKey)) {
                    NetworkHelper.sendData(NetworkHelper.dataType.SendMessage);
                    TypingMsg = "";
                    TypingCursor = 0;
                    isOpen = false;
                    Gdx.input.setInputProcessor(vanillaInput);
                }
                if (Gdx.input.isKeyJustPressed(RemoveKey)) {
                    if (TypingCursor > 0) {
                        if (TypingCursor == 1) {
                            TypingMsg = TypingMsg.substring(1, TypingMsg.length());
                        } else if (TypingCursor == TypingMsg.length()) {
                            TypingMsg = TypingMsg.substring(0, TypingMsg.length() - 1);
                        } else {
                            String pre = TypingMsg.substring(0, TypingCursor - 1);
                            String post = TypingMsg.substring(TypingCursor, TypingMsg.length());
                            TypingMsg = pre + post;
                        }
                        TypingCursor--;
                    }
                }
                if (Gdx.input.isKeyJustPressed(Keys.LEFT)) {
                    TypingCursor -= 1;
                    if (TypingCursor < 0)
                        TypingCursor = 0;
                }
                if (Gdx.input.isKeyJustPressed(Keys.RIGHT)) {
                    TypingCursor += 1;
                    if (TypingCursor > TypingMsg.length())
                        TypingCursor = TypingMsg.length();
                }
            }

        }

        int size;
        if (Messages.size() < MAX_MSG_SIZE) {
            size = Messages.size() + 1;
        } else {
            size = MAX_MSG_SIZE;
        }

        if (size == 1 || (!isOpen && showTimer <= 0.0F) || shouldHide())
            return;
        for (int i = ShowIndex; i < ShowIndex + size - 1; i++) {
            // FONT.draw(sb, Messages.get(i), x, y);
            ArrayList<ChatText> texts = Texts.get(i);
            for (ChatText text : texts) {
                text.update();
            }
        }
    }

    public class ChatTextProcessor implements InputProcessor {
        public ChatScreen parent;

        public ChatTextProcessor(ChatScreen parent) {
            this.parent = parent;
        }

        public boolean keyDown(int keycode) {
            return false;
        }

        public boolean keyUp(int keycode) {
            return false;
        }

        public boolean keyTyped(char character) {
            if (!parent.isOpen)
                return false;
            String charStr = String.valueOf(character);
            if (Character.isLetterOrDigit(character) || ChatText.WHITE_LIST.contains(charStr)) {
                if (parent.TypingMsg == "") {
                    parent.TypingMsg = charStr;
                } else {
                    StringBuilder str = new StringBuilder(parent.TypingMsg);
                    str.insert(parent.TypingCursor, charStr);
                    parent.TypingMsg = str.toString();
                }

                // parent.TypingMsg += charStr;
                parent.TypingCursor += charStr.length();
                return true;
            }
            return true;
        }

        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            return false;
        }

        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            return false;
        }

        public boolean touchDragged(int screenX, int screenY, int pointer) {
            return false;
        }

        public boolean mouseMoved(int screenX, int screenY) {
            return false;
        }

        public boolean scrolled(int amount) {
            if (!parent.isOpen || !isMouseInScreen())
                return false;
            if (amount < 0) {
                ShowIndex++;
            } else if (amount > 0) {
                ShowIndex--;
            }
            if (Messages.size() <= MAX_MSG_SIZE) {
                ShowIndex = 0;
            } else {
                ShowIndex = MathUtils.clamp(ShowIndex, 0, Messages.size() - MAX_MSG_SIZE);
            }
            return false;
        }
    }

    // Disable key presses when the text input is up
    @SpirePatch(cls="com.megacrit.cardcrawl.helpers.input.InputAction", method="isJustPressed")
    public static class IsJustPressedFix {
        public static SpireReturn<Boolean> Prefix(InputAction __instance) {
            if (TogetherManager.chatScreen.isOpen)
                return SpireReturn.Return(false);

            return SpireReturn.Continue();
        }
    }

    @SpirePatch(cls="com.megacrit.cardcrawl.helpers.input.InputAction", method="isPressed")
    public static class IsPressedFix {
        public static SpireReturn<Boolean> Prefix(InputAction __instance) {
            if (TogetherManager.chatScreen.isOpen)
                return SpireReturn.Return(false);

            return SpireReturn.Continue();
        }        
    }
}
