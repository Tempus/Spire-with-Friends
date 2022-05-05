package chronoMods.ui.hud;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.*;
import com.badlogic.gdx.math.*;

import com.megacrit.cardcrawl.core.*;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.integrations.steam.*;
import com.megacrit.cardcrawl.helpers.*;
import com.megacrit.cardcrawl.relics.*;
import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.screens.runHistory.*;
import com.codedisaster.steamworks.*;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.helpers.input.*;

import java.util.*;
import java.util.stream.*;
import java.nio.*;

import chronoMods.*;
import chronoMods.bingo.*;
import chronoMods.network.steam.*;
import chronoMods.network.*;
import chronoMods.ui.deathScreen.*;
import chronoMods.ui.hud.*;
import chronoMods.ui.lobby.*;
import chronoMods.ui.mainMenu.*;
import chronoMods.utilities.*;

public class BingoPlayerWidget extends RemotePlayerWidget
{
	public int team = 0;
	public String teamName = "Team " + team;
	public ArrayList<RemotePlayer> teamPlayers = new ArrayList();

	public static final String[] EasyBingo = CardCrawlGame.languagePack.getUIString("EasyBingo").TEXT;
	public static final String[] MedBingo = CardCrawlGame.languagePack.getUIString("MedBingo").TEXT;
	public static final String[] HardBingo = CardCrawlGame.languagePack.getUIString("HardBingo").TEXT;

	public static List<String> allBingo;

	public int winningLine = 0;

	public BingoPlayerWidget(RemotePlayer player) {
		super(player);
		teamPlayers.add(player);
		player.widget = this;

		// Merge Bingo?
		allBingo = Stream.of(EasyBingo, MedBingo, HardBingo).flatMap(Stream::of).collect(Collectors.toList());

		// Set the rank
		setRank(TopPanelPlayerPanels.playerWidgets.size());

		if (!player.teamName.equals(""))
			teamName = player.teamName;
	}

	public BingoPlayerWidget(ArrayList<RemotePlayer> teamPlayers) {
		super(teamPlayers.get(0));

		this.teamPlayers = teamPlayers;

		for (RemotePlayer user : teamPlayers) {
			user.widget = this;
		}

		// Merge Bingo?
		allBingo = Stream.of(EasyBingo, MedBingo, HardBingo).flatMap(Stream::of).collect(Collectors.toList());

		// Set the rank
		setRank(TopPanelPlayerPanels.playerWidgets.size());

		if (!player.teamName.equals(""))
			teamName = teamPlayers.get(0).teamName;
	}

	// For sorting, returns negative, 0, or positive for lower than, equal to, or higher than respectively
	@Override
	public int compareTo(Object compareToMe) {
		// These should be sorted in ascending order, first by finished time if available, then by floor
		RemotePlayerWidget c = (RemotePlayerWidget)compareToMe;

		// Well, except me or my team should always be on top.
		for (RemotePlayer user : teamPlayers)
			if (user.isUser(TogetherManager.currentUser))
				return 1;

		// Otherwise, compare the number of marks on the cards.
		return Caller.countMarks(player.bingoCard) - Caller.countMarks(c.player.bingoCard);
	}

	@Override
	public String toString() {
		return "Remote Player: " + player.userName + " @ Rank " + rank;
	}

	public void renderPortrait(SpriteBatch sb, float xn, float yn) {
		for (RemotePlayer user : teamPlayers) {
			if (user.isUser(TogetherManager.currentUser)) {
				if (user.getPortrait() != null) {
					sb.draw(user.getPortrait(), xn + 26.0F * Settings.scale, yn+12.0F * Settings.scale, 56f * Settings.scale, 56f * Settings.scale);
					sb.draw(TogetherManager.portraitFrames.get(0), xn - 160.0F * Settings.scale, yn - 96.0F * Settings.scale, 0.0F, 0.0F, 432.0F, 243.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 1920, 1080, false, false);
					return;
				}
			}
		}

		if (player.getPortrait() != null)
			sb.draw(player.getPortrait(), xn + 26.0F * Settings.scale, yn+12.0F * Settings.scale, 56f * Settings.scale, 56f * Settings.scale);

		// Render Portrait frame
		sb.draw(TogetherManager.portraitFrames.get(0), xn - 160.0F * Settings.scale, yn - 96.0F * Settings.scale, 0.0F, 0.0F, 432.0F, 243.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 1920, 1080, false, false);
	}

	public void renderUsername(SpriteBatch sb, float xn, float yn) {
		String displayName = player.userName;

		if (teamPlayers.size() > 1)
			displayName = teamName;

		Color textColour = Settings.CREAM_COLOR.cpy().sub(0f,0f,0f,1.0f-displayColour.a);
		FontHelper.renderSmartText(sb, player.useFallbackFont ? TogetherManager.fallbackFont : FontHelper.topPanelInfoFont, displayName, xn + 96.0F * Settings.scale, yn + 64.0F * Settings.scale, Settings.WIDTH, 0.0F, textColour, hoverScale);
	}

	public float marqueeDuration = 3.0f;
	public float marqueeTimer = marqueeDuration;
	public int marqueeIndice = 0;

	public void renderIcons(SpriteBatch sb, float xn, float yn) {
		Color textColour = Settings.CREAM_COLOR.cpy().sub(0f,0f,0f,1.0f-displayColour.a);

		// What do I need to put here if it's just one player?
		if (teamPlayers.size() == 1)
			return;

		// But we're a *team*
		sb.draw(TogetherManager.membersTexture, xn + 88.0F * Settings.scale, yn + 6.0F*Settings.scale, ICON_W, ICON_W);

		marqueeTimer -= Gdx.graphics.getDeltaTime();
		if (marqueeTimer < 0f) {
			// Fade Out
			textColour = Settings.CREAM_COLOR.cpy().sub(0f,0f,0f,1.0f-displayColour.a-marqueeTimer);
			
			// Fade Out over, switch to next person
			if (marqueeTimer < -1f) {
				marqueeIndice++;
				if (marqueeIndice >= teamPlayers.size())
					marqueeIndice = 0;

				// Set it to extra for the Fade In
				marqueeTimer = marqueeDuration + 1f;
			}
		}

		// Fade In
		if (marqueeTimer > 3f)
			textColour = Settings.CREAM_COLOR.cpy().sub(0f,0f,0f,1.0f-displayColour.a-((marqueeTimer-3f)*-1f));

		FontHelper.renderSmartText(sb, FontHelper.cardDescFont_N, teamPlayers.get(marqueeIndice).userName, xn + 124.0F * Settings.scale,  yn + 32.0F * Settings.scale, textColour);
	}

	public void renderBossRelics(SpriteBatch sb, float xn, float yn) {
		Color.WHITE.a = displayColour.a;

		float bx = xn + TogetherManager.panelImg.getWidth() * Settings.scale - 64f;
		float by = yn + screenPos(-24f);

		// sb.draw(TogetherManager.bingoTinyCard, xn + 26.0F * Settings.scale, yn+12.0F * Settings.scale, 128f * Settings.scale, 128f * Settings.scale);
		sb.draw(TogetherManager.bingoTinyCard, bx, yn-64f+screenPosY(40f), 64.0F, 64.0F, 128f, 128f, Settings.scale, Settings.scale, 0f, 0, 0, 128, 128, false, false);

		Texture[][] card = teamPlayers.get(0).bingoCard; 
		for (int x = 0; x < 5; x++) {
			for (int y = 0; y < 5; y++) {
				if (card[x][y] != null) {
					
					// Draw bingo
					if (x+1 == winningLine) { // Horiz
						sb.setColor(RemotePlayer.colourChoices[colour]);
					} else if (y+6 == winningLine) {
						sb.setColor(RemotePlayer.colourChoices[colour]);
					} else if (winningLine == 11 && x == y) {
						sb.setColor(RemotePlayer.colourChoices[colour]);
					} else if (winningLine == 12 && x == 4-y) {
						sb.setColor(RemotePlayer.colourChoices[colour]);
					}

					sb.draw(TogetherManager.bingoTinyMark, bx+42f+x*screenPos(10f), by+screenPos(82f-y*10f), 20f, 0f, 8f, 8f, Settings.scale, Settings.scale, 0f, 0, 0, 8, 8, false, false);
					sb.setColor(Color.WHITE);
				}
			}
		}

		updateFlash();
		renderFlash(sb, bx, yn-64f+screenPosY(40f));

		Color.WHITE.a = 1.0f;
	}

	private Color flashColor = new Color(1.0F, 1.0F, 1.0F, 0.0F);
	public float flashTimer = 0.0F;
	
	private void updateFlash() {
		if (this.flashTimer != 0.0F) {
			this.flashTimer -= Gdx.graphics.getDeltaTime();
			if (this.flashTimer < 0.0F)
				this.flashTimer = 0.0F;
		}
	}

	public void renderFlash(SpriteBatch sb, float bx, float by) {
		float tmp = Interpolation.exp10In.apply(0.0F, 4.0F, this.flashTimer / 2.0F);
		sb.setBlendFunction(770, 1);
		this.flashColor.a = this.flashTimer * 0.2F;
		sb.setColor(this.flashColor);
		// float tmpX = this.currentX - 64.0F;
		sb.draw(TogetherManager.bingoTinyCard, bx, by, 64.0F, 64.0F, 128.0F, 128.0F, 1f + tmp, 1f + tmp, 0f, 0, 0, 128, 128, false, false);
		sb.draw(TogetherManager.bingoTinyCard, bx, by, 64.0F, 64.0F, 128.0F, 128.0F, 1f + tmp * 0.66F, 1f + tmp * 0.66F, 0f, 0, 0, 128, 128, false, false);
		sb.draw(TogetherManager.bingoTinyCard, bx, by, 64.0F, 64.0F, 128.0F, 128.0F, 1f + tmp / 3.0F, 1f + tmp / 3.0F, 0f, 0, 0, 128, 128, false, false);
		sb.setBlendFunction(770, 771);
		sb.setColor(Color.WHITE);
	}

	public void flash() {
		this.flashTimer = 2.0F;
	}

	public float slideInDuration = 0f;
	public float slideInDurationOrigin = 1.5f;
	public float slideInPosition = -Settings.HEIGHT;

	public int colour = 0;

	public boolean reshowBanner;

	public void renderHoverPanel(SpriteBatch sb) {
		// Increment the slider
		if (slideInPosition < 0 && connectbox.hovered && slideInDuration < slideInDurationOrigin) {
			slideInDuration += Gdx.graphics.getDeltaTime();
			if (slideInDuration > slideInDurationOrigin)
				slideInDuration = slideInDurationOrigin;
		}
		else if (!connectbox.hovered && slideInDuration > 0f) {
			slideInDuration -= Gdx.graphics.getDeltaTime();
			if (slideInDuration < 0)
				slideInDuration = 0;
		}

		// Bounce it in and out differently
        if (connectbox.hovered) {
			slideInPosition = Interpolation.bounce.apply(-Settings.HEIGHT, 0f, this.slideInDuration*(1f/this.slideInDurationOrigin));
			TogetherManager.chatScreen.isHidden = true;
			if (AbstractDungeon.dynamicBanner.show) {
				AbstractDungeon.dynamicBanner.hide();
				reshowBanner = true;
			}
        }
        else {
			slideInPosition = Interpolation.bounceIn.apply(-Settings.HEIGHT, 0f, this.slideInDuration*(1f/this.slideInDurationOrigin));
			TogetherManager.chatScreen.isHidden = false;

			if (reshowBanner) {
				AbstractDungeon.dynamicBanner.appear();
				reshowBanner = false;
			}
        }

		// Only bother drawing it if we're onscreen
		if (slideInPosition > -Settings.HEIGHT+1) {
			Color textColour = Settings.CREAM_COLOR.cpy();

			// Teammate Names
			if (teamPlayers.size() > 1) {
				int i = 0;
				for (RemotePlayer user : teamPlayers) {
					sb.draw(TogetherManager.teamTags, screenPosX(1275f), Settings.HEIGHT-screenPos(890f+i*70f-teamPlayers.size()*70f)+slideInPosition, 329f/2f, 52f/2f, 329f, 52f, Settings.scale, Settings.scale, 0f, 0, 0, 329, 52, false, false);
					FontHelper.renderFont(sb, FontHelper.cardDescFont_N, user.userName, screenPosX(1320f), Settings.HEIGHT-screenPos(852f+i*70f-teamPlayers.size()*70f)+slideInPosition, textColour);
					i++;
				}
			}

			// Background
			sb.draw(TogetherManager.bingoCard, (Settings.WIDTH-1920f)/2f, 0+slideInPosition, 1920f/2f, 0, 1920f, 1080f, Settings.yScale, Settings.yScale, 0f, 0, 0, 1920, 1080, false, false);

			Texture[][] card = teamPlayers.get(0).bingoCard; 
			int[][] cardIndices = teamPlayers.get(0).bingoCardIndices;

			// Card Contents
			float distanceFromBGXToULSquare = 720f;
			float distanceFromBGYToULSquare = 725f;
			float squareSize = 120f;

			for (int x = 0; x < 5; x++) {
				for (int y = 0; y < 5; y++) {
					// Print bingo rule
					FontHelper.renderWrappedText(sb, FontHelper.cardTypeFont, allBingo.get(cardIndices[x][y]), 
						screenPosX(distanceFromBGXToULSquare+x*squareSize), screenPos(distanceFromBGYToULSquare-y*squareSize)+slideInPosition, 100f* Settings.scale, textColour, 1f);

					// Draw Mark if complete
					if (card[x][y] != null) 
						sb.draw(card[x][y], 
							screenPosX(distanceFromBGXToULSquare+x*squareSize)-60f, screenPos(distanceFromBGYToULSquare-y*squareSize)-60f+slideInPosition, 
							120f/2f, 120f/2f, 120f, 120f, Settings.scale, Settings.scale, 0f, 0, 0, 120, 120, false, false);
				}
			}

			// Winning Line
			sb.setColor(RemotePlayer.colourChoices[colour]);

			TextureAtlas.AtlasRegion img = ImageMaster.vfxAtlas.findRegion("combat/laserThin");
			float oX = img.packedWidth/2f;
			float oY = img.packedHeight/2f;

			if (winningLine > 0 && winningLine < 6) { // Horiz
				sb.draw(img, 
					screenPosX(distanceFromBGXToULSquare+(winningLine-1)*squareSize)-oX, screenPos(distanceFromBGYToULSquare-squareSize*2f)-oY+slideInPosition, 
					oX, oY, img.packedWidth, img.packedHeight, Settings.scale, Settings.scale, 90f);

			} else if (winningLine > 5 && winningLine < 11) {
				sb.draw(img, 
					screenPosX(distanceFromBGXToULSquare+squareSize*2f)-oX, screenPos(distanceFromBGYToULSquare-(winningLine-6)*squareSize)-oY+slideInPosition, 
					oX, oY, img.packedWidth, img.packedHeight, Settings.scale, Settings.scale, 0f);

			} else if (winningLine == 11) {
				sb.draw(img, 
					screenPosX(distanceFromBGXToULSquare+squareSize*2f)-oX, screenPos(distanceFromBGYToULSquare-squareSize*2f)-oY+slideInPosition, 
					oX, oY, img.packedWidth, img.packedHeight, Settings.scale, Settings.scale, 135f);

			} else if (winningLine == 12) {
				sb.draw(img, 
					screenPosX(distanceFromBGXToULSquare+squareSize*2f)-oX, screenPos(distanceFromBGYToULSquare-squareSize*2f)-oY+slideInPosition, 
					oX, oY, img.packedWidth, img.packedHeight, Settings.scale, Settings.scale, 45f);

			}
			sb.setColor(Color.WHITE);

			// Team Name - or just your name if you're not on a team
			if (teamPlayers.size() == 1) {
				FontHelper.renderFontCentered(sb, FontHelper.losePowerFont, player.userName, screenPosX(845f), screenPos(910f)+slideInPosition, textColour);
				return;
			}

			FontHelper.renderFontCentered(sb, FontHelper.losePowerFont, teamName, screenPosX(845f), screenPos(890f)+slideInPosition, textColour);

			sb.setColor(RemotePlayer.colourChoices[colour]);

			sb.setColor(Color.WHITE);


		}
	}
}