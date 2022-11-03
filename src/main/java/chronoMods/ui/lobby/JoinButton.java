package chronoMods.ui.lobby;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;

public class JoinButton {
	// Rendering variables
	private static final int W = 512, H = 256;
	private static final Color HOVER_BLEND_COLOR = new Color(1f, 1f, 1f, 0.3f);
	private static final Color TEXT_DISABLED_COLOR = new Color(0.6f, 0.6f, 0.6f, 1f);

	private static final Color BUTTON_SHADOW_COLOR = new Color(0f, 0f, 0f, 0.2f);

	private static final float SHOW_X = Settings.WIDTH - 256f * Settings.scale, DRAW_Y = 128f * Settings.scale;
	private static final float HIDE_X = SHOW_X + 400f * Settings.scale;
	private float x;
	private float y;
	private float controller_offset_x = 0f;
	private boolean isHidden = true;
	public boolean isDisabled = true, isHovered = false;
	private float glowAlpha = 0f;
	private Color glowColor = Color.WHITE.cpy();

	// Text
	public String buttonText = CardCrawlGame.languagePack.getUIString("Lobby").TEXT[0];

	// Hitbox
	public Hitbox hb = new Hitbox(0f, 0f, 320f * Settings.scale, 100f * Settings.scale);

	public JoinButton(String label) {
		updateText(label);
	}

	public void updateText(String label) {
		buttonText = label;
		try {
			controller_offset_x = FontHelper.getSmartWidth(FontHelper.buttonLabelFont, label, 99999f, 0f) / 2f;
		} catch (Exception e) {}
	}

	public void update() {
		if (!isHidden && !isDisabled) {
			updateGlow();
			hb.update();

			if (InputHelper.justClickedLeft && hb.hovered && !isDisabled) {
				hb.clickStarted = true;
				CardCrawlGame.sound.play("UI_CLICK_1");
			}
			if (hb.justHovered && !isDisabled) {
				CardCrawlGame.sound.play("UI_HOVER");
			}
			isHovered = hb.hovered;

			if (CInputActionSet.select.isJustPressed()) {
				CInputActionSet.select.unpress();
				hb.clicked = true;
			}
		}
	}

	private void updateGlow() {
		glowAlpha += Gdx.graphics.getDeltaTime() * 3f;
		if (glowAlpha < 0) {
			glowAlpha *= -1f;
		}
		float tmp = MathUtils.cos(glowAlpha);
		if (tmp < 0f) {
			glowColor.a = -tmp / 2f + 0.3f;
		} else {
			glowColor.a = tmp / 2f + 0.3f;
		}
	}

	public void hideInstantly() {
		isHidden = true;
	}

	public void hide() {
		if (!isHidden) {
			isHidden = true;
		}
	}

	public void show() {
		if (isHidden) {
			glowAlpha = 0f;
			isHidden = false;
			isDisabled = false;
		}
	}

	public void move(float x,float y) {
		this.x = x;
		this.y = y;
		hb.move(this.x, this.y);
	}

	public void render(SpriteBatch sb) {
		sb.setColor(Color.WHITE);
		renderShadow(sb);
		renderButton(sb);

		if (hb.hovered && !isDisabled && !hb.clickStarted) {
			sb.setBlendFunction(GL30.GL_SRC_ALPHA, GL30.GL_ONE);
			sb.setColor(HOVER_BLEND_COLOR);
			renderButton(sb);
			sb.setBlendFunction(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
		}

		// Render the text
		if (isDisabled) {
			renderDisabled(sb);
			FontHelper.renderFontCentered(
				sb,
				FontHelper.buttonLabelFont,
				buttonText,
				this.x,
				this.y,
				TEXT_DISABLED_COLOR);
		} else if (hb.clickStarted) {
			FontHelper.renderFontCentered(
				sb,
				FontHelper.buttonLabelFont,
				buttonText,
				this.x,
				this.y,
				Color.LIGHT_GRAY);
		} else if (hb.hovered) {
			FontHelper.renderFontCentered(
				sb,
				FontHelper.buttonLabelFont,
				buttonText,
				this.x,
				this.y,
				Settings.LIGHT_YELLOW_COLOR);
		} else {
			FontHelper.renderFontCentered(
				sb,
				FontHelper.buttonLabelFont,
				buttonText,
				this.x,
				this.y,
				Settings.LIGHT_YELLOW_COLOR);
		}

		renderControllerUi(sb);

		if (!isHidden) {
			hb.render(sb);
		}
	}

	private void renderShadow(SpriteBatch sb) {

		sb.setColor(BUTTON_SHADOW_COLOR);
		sb.draw(
			ImageMaster.REWARD_SCREEN_TAKE_USED_BUTTON,
			this.x - W / 2f,
			this.y - H / 2f,
			W / 2f,
			H / 2f,
			W,
			H,
			Settings.scale,
			Settings.scale,
			0f,
			0,
			0,
			W,
			H,
			false,
			false);
		sb.setColor(Color.WHITE);
	}

	private void renderDisabled(SpriteBatch sb) {
		sb.draw(
			ImageMaster.REWARD_SCREEN_TAKE_USED_BUTTON,
			this.x - W / 2f,
			this.y - H / 2f,
			W / 2f,
			H / 2f,
			W,
			H,
			Settings.scale,
			Settings.scale,
			0f,
			0,
			0,
			W,
			H,
			false,
			false);
	}

	private void renderButton(SpriteBatch sb) {
		sb.draw(
			ImageMaster.REWARD_SCREEN_TAKE_BUTTON,
			this.x - W / 2f,
			this.y - H / 2f,
			W / 2f,
			H / 2f,
			W,
			H,
			Settings.scale,
			Settings.scale,
			0f,
			0,
			0,
			W,
			H,
			false,
			false);
	}

	private void renderControllerUi(SpriteBatch sb) {
		if (Settings.isControllerMode) {
			sb.setColor(Color.WHITE);
			sb.draw(
				CInputActionSet.select.getKeyImg(),
				this.x - 32f - controller_offset_x + 96f * Settings.scale,
				this.y - 32f + 57f * Settings.scale,
				32f,
				32f,
				64,
				64,
				Settings.scale,
				Settings.scale,
				0f,
				0,
				0,
				64,
				64,
				false,
				false);
		}
	}
}
