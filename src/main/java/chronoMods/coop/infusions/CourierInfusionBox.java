package chronoMods.coop.infusions;

import chronoMods.TogetherManager;
import chronoMods.coop.courier.CoopCourierScreen;
import chronoMods.coop.relics.TransfusionBag;
import chronoMods.network.NetworkHelper;
import chronoMods.network.RemotePlayer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.vfx.NecronomicurseEffect;

public class CourierInfusionBox {
	private static final float TEXT_DURATION = 1.8F;
	private static final float DRAW_X = Settings.WIDTH / 2.0F, DRAW_Y = Settings.HEIGHT * 0.6F;	
	private static final float STARTING_OFFSET_Y = 0.0F * Settings.scale;
	private static final float TARGET_OFFSET_Y = 120.0F * Settings.scale;
	private static final float LERP_RATE = 5.0F;

    public static String[] TEXT = CardCrawlGame.languagePack.getUIString("CardInfusions").TEXT;

	private float X, Y;
	public float scaleMod = 1.0f;
	private String msg;

	public Color textColour;

	public InfusionSet infusionSet;
	public Hitbox hb;

	boolean taken;

	public CourierInfusionBox(int i, InfusionSet set) {
		infusionSet = set;
		this.textColour = Settings.CREAM_COLOR.cpy();
	
		X = screenPos(150f);
		Y = Settings.HEIGHT-screenPosY(425f) - i*screenPosY(180f);

		hb = new Hitbox(160, 160);
	}
	
	protected float screenPos(float val)  { return val * Settings.scale;  }
	protected float screenPosX(float val) { return val * Settings.xScale; }
	protected float screenPosY(float val) { return val * Settings.yScale; }


	public void update() {
		this.hb.move(X+screenPos(80f), Y);
		this.hb.update();

		if (this.hb.hovered) {
			TipHelper.renderGenericTip(X + 180f*Settings.scale, Y - 32.0F - 2.0F * Settings.scale, infusionSet.name, TEXT[6] + infusionSet.description + " NL NL " + TEXT[5]);
            if (InputHelper.justClickedLeft)
                hb.clickStarted = true; 
		}

		if (this.hb.clicked && !taken) {
			if (TogetherManager.courierScreen.getRecipient() == null) {
				TogetherManager.courierScreen.speechTimer = MathUtils.random(40.0F, 60.0F);
		        TogetherManager.courierScreen.playCantBuySfx();
		        TogetherManager.courierScreen.createSpeech(CoopCourierScreen.getNoRecipientMsg());

	            hb.clicked = false;
	
				return;
			}

			RemotePlayer rp = TogetherManager.courierScreen.getRecipient();
			AbstractDungeon.effectsQueue.add(new NecronomicurseEffect(new Conduit(rp.userName, infusionSet, rp.getPortrait()), Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));

			TransfusionBag.set = infusionSet;
			CardCrawlGame.sound.play("UI_CLICK_1");
			CardCrawlGame.sound.play("SHOP_PURCHASE", 0.1F);

			NetworkHelper.sendData(NetworkHelper.dataType.Infusion);
			taken = true;
            hb.clicked = false;
		} // Send the infusion via courier, the recipient gets 3 infusions from this set.
	}
	
	public void render(SpriteBatch sb) {
		scaleMod = 1f;

		// Hover feedback
		if (taken)
	 		sb.setColor(Color.DARK_GRAY);
		else if (this.hb.clickStarted && this.hb.hovered) {
			sb.setColor(Color.GRAY);
			scaleMod = 0.95F;
		} else if (this.hb.hovered) {
			sb.setColor(Color.LIGHT_GRAY);
			scaleMod = 1.15F;
		} else
			sb.setColor(Color.WHITE);

		// Panel BG and Text
		sb.draw(TogetherManager.bingoCompletePanel, X, Y, 
			80f, 80f, 160f, 160f, Settings.scale * scaleMod, Settings.scale * scaleMod, 0.0F, 0, 0, 160, 160, false, false);
		FontHelper.renderWrappedText(sb, FontHelper.cardTypeFont, infusionSet.longname, X + screenPos(160f/2f), Y + 160f/2f, 100*Settings.scale, textColour, scaleMod);

		sb.setColor(Color.WHITE);
	}
	
	public void dispose() {}
}
