package chronoMods.ui.lobby;

import chronoMods.TogetherManager;
import chronoMods.network.NetworkHelper;
import chronoMods.network.RemotePlayer;
import chronoMods.ui.mainMenu.NewMenuButtons;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;

public class PlayerListWidgetItem
{
    public RemotePlayer player;
    public Texture ownerCrown;
    public Texture kickBoot;

    // Position
    public float x;
    public float y;
    public float scroll;

    public Hitbox kickbox = new Hitbox(36f * Settings.xScale, 24f * Settings.yScale);
    public float ks;

    public Hitbox versionbox = new Hitbox(36f * Settings.xScale, 24f * Settings.yScale);
    public static final String[] TIPS = CardCrawlGame.languagePack.getUIString("PlayerListWidget").TEXT;

    public Hitbox connectbox = new Hitbox(32f * Settings.xScale, 64f * Settings.yScale);
    public float hoverScale = 1.0f;

    public boolean fallbackChecked = false;
    public boolean justHoveredVersion = false;


    public PlayerListWidgetItem(RemotePlayer player) {
        this.player = player;
        ownerCrown = ImageMaster.getRelicImg("Busted Crown");
        kickBoot = ImageMaster.getRelicImg("Boot");

        // Check for Fallback Font usage
        if (player != null)
            fallbackChecked = checkForFallbackFont();
    }

    public boolean checkForFallbackFont() {
        try {
            if (!player.userName.matches("\\A\\p{ASCII}*\\z")) {
                player.useFallbackFont = true;
                TogetherManager.logger.info("Using fallback font for player " + player.userName);
                return true;
            }
        } catch (Exception e) {
            TogetherManager.logger.info("Fallback Font Detection has caused an error on " + player.userName);
            player.useFallbackFont = true;
            return true;
        }

        return true;
    }

    public void move(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void scroll(float y) {
        this.scroll = y;
    }

    public void update(int i) {
        // if (TogetherManager.currentLobby != null && player != null && !(player.isUser(TogetherManager.currentLobby.ownerID)) && TogetherManager.getCurrentUser().isUser(TogetherManager.currentLobby.ownerID)) {
        if (player != null && !fallbackChecked)
            fallbackChecked = checkForFallbackFont();

        if (TogetherManager.currentLobby != null && player != null) {
            
            connectbox.update();
            connectbox.move(this.x - (464 / 2f) * Settings.scale + 32f / 2f, this.y + this.scroll - (i * 75f * Settings.yScale));
            if (connectbox.hovered){
                hoverScale = 1.1f;
                if (InputHelper.justClickedLeft && NewMenuButtons.newGameScreen.teamsToggle.isTicked() && (TogetherManager.currentLobby.isOwner() || TogetherManager.getCurrentUser().isUser(player))) {
                    player.team++;
                    if (player.team >= (TogetherManager.players.size() / 2) + 1 || player.team >= RemotePlayer.colourChoices.length)
                        player.team = 0;

                    TogetherManager.log("Player team is: " + player.team);

                    NetworkHelper.sendData(NetworkHelper.dataType.TeamChange);
                    CardCrawlGame.sound.play("UI_CLICK_1");
                }
            } else {
                hoverScale = 1.0f;
            }


            // Allow the owner to kick players
            if (TogetherManager.currentLobby.isOwner() && !TogetherManager.getCurrentUser().isUser(player)) {
                kickbox.move(this.x - (464 / 2f) * Settings.xScale + 36f * Settings.xScale, this.y + this.scroll - (i * 75f * Settings.yScale) - 24f * Settings.yScale);

                kickbox.update();
                this.ks = 1.0f;
                if (kickbox.hovered) {
                    this.ks = 1.2f;
                    TipHelper.renderGenericTip(kickbox.cX * 1.1f, kickbox.cY + 48f, TIPS[0], TIPS[1]); 
                }
                if (kickbox.hovered && InputHelper.justClickedLeft) {
                  kickbox.clickStarted = true;
                  NewGameScreen.kick = player;
                  CardCrawlGame.sound.play("BLUNT_HEAVY");
                  NetworkHelper.sendData(NetworkHelper.dataType.Kick);
                }
            }

            // Provide information if there's a version mismatch

            versionbox.move(this.x - 64 / 2f + (464 / 2f) * Settings.xScale + 8f * Settings.xScale,
                this.y + this.scroll - (i * 75f * Settings.yScale) - 64 / 2f - 2f * Settings.yScale + 24f * Settings.yScale);

            versionbox.update();          
            if (versionbox.hovered) {


                String versionTip = "";
                if (player.version == 0) {
                    versionTip = TIPS[5];
                    if (justHoveredVersion)
                        NetworkHelper.sendData(NetworkHelper.dataType.RequestVersion);
                } else if (player.version != TogetherManager.VERSION) {
                    versionTip = String.format(TIPS[3], TogetherManager.VERSION, player.version);
                } else if ((!player.safeMods && player.modHash != TogetherManager.getCurrentUser().modHash)) {
                    versionTip = TIPS[6];
                } else if (player.modHash != TogetherManager.getCurrentUser().modHash) {
                    versionTip = TIPS[4];
                } else {
                    justHoveredVersion = false;
                    return;
                }

                TipHelper.renderGenericTip(versionbox.cX * 0.85f, versionbox.cY + 48f, TIPS[2], versionTip); 
                justHoveredVersion = false;

            } else {
                justHoveredVersion = true;
            }
        }
    }

    public void clear() {
        player = null;
    }

    private static final Color EMPTY_PLAYER_SLOT = new Color(1f, 1f, 1f, 0.3f);

    public void render(SpriteBatch sb, int i) {
        // BG Panel        
        sb.setColor(Color.WHITE.cpy());

        if (player == null) {
            sb.setColor(EMPTY_PLAYER_SLOT);
            // Background
            sb.draw(
                ImageMaster.REWARD_SCREEN_ITEM,
                this.x - 464 / 2f,
                this.y + this.scroll - (i * 75f * Settings.scale) - 98 / 2f,
                464 / 2f, 98 / 2f,
                464, 98,
                Settings.scale,Settings.scale*0.75f,
                0f,
                0, 0, 464, 98,
                false, false);
            sb.setColor(Color.WHITE);
        } else {

            sb.draw(
                ImageMaster.REWARD_SCREEN_ITEM,
                this.x - 464 / 2f,
                this.y + this.scroll - (i * 75f * Settings.scale) - 98 / 2f,
                464 / 2f, 98 / 2f,
                464, 98,
                Settings.scale,Settings.scale*0.75f,
                0f,
                0, 0, 464, 98,
                false, false);

            // Player Portrait
            if (player.getPortrait() != null) {
                sb.draw(
                    player.getPortrait(),
                    this.x - 56 / 2f - 164f * Settings.scale,
                    this.y + this.scroll - (i * 75f * Settings.scale) - 56 / 2f - 2f * Settings.scale,
                    56 / 2f, 56 / 2f,
                    56, 56,
                    Settings.scale, Settings.scale,
                    0f, 0, 0,
                    player.getPortrait().getWidth(), player.getPortrait().getHeight(),
                    false, false); }

            // Portrait Frame
            sb.draw(TogetherManager.portraitFrames.get(0), 
                this.x - (64 / 2f) * Settings.scale - 164f * Settings.scale    - 184.0F * Settings.scale, 
                this.y + this.scroll - (i * 75f * Settings.scale) - (64 / 2f) * Settings.scale - 2f * Settings.scale    - 104.0F * Settings.scale, 
                0.0F, 0.0F, 432.0F, 243.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 1920, 1080, false, false);

            // Owner Crown
            if (TogetherManager.currentLobby != null && player.isUser(TogetherManager.currentLobby.getOwner())) {
                sb.draw(
                    ownerCrown,
                    this.x - 164f * Settings.scale - 12f,
                    this.y + this.scroll - (i * 75f * Settings.yScale) - 12f,
                    64 / 2f, 64 / 2f,
                    64, 64,
                    Settings.scale, Settings.scale,
                    -60f, 0, 0,
                    ownerCrown.getWidth(), ownerCrown.getHeight(),
                    false, false); }
            
            // Kick Icon
            else if (TogetherManager.currentLobby.isOwner()) {
                sb.draw(
                    kickBoot,
                    kickbox.x - 12f,
                    kickbox.y - 12f,
                    48 / 2f, 48 / 2f,
                    48, 48,
                    Settings.scale * this.ks, Settings.scale * this.ks,
                    0f, 0, 0,
                    kickBoot.getWidth(), kickBoot.getHeight(),
                    false, false);

                kickbox.render(sb);
            }


            // Player Name
            Color color = Settings.CREAM_COLOR;

            if (TogetherManager.gameMode != TogetherManager.mode.Coop) {
                FontHelper.renderSmartText(
                    sb,
                    player.useFallbackFont ? TogetherManager.fallbackFont : FontHelper.topPanelInfoFont,
                    player.userName,
                    this.x - 112f * Settings.scale,
                    this.y + this.scroll - (i * 75f * Settings.scale) + 5f * Settings.scale,
                    1000f * Settings.scale,
                    0f,
                    color,
                    hoverScale);                
            } else {
                FontHelper.renderSmartText(
                    sb,
                    player.useFallbackFont ? TogetherManager.fallbackFont : FontHelper.topPanelInfoFont,
                    player.userName,
                    this.x - 112f * Settings.scale,
                    this.y + this.scroll - (i * 75f * Settings.scale) + 16f * Settings.scale,
                    1000f * Settings.scale,
                    0f,
                    color,
                    hoverScale);
                if (player.character != null)
                    FontHelper.renderSmartText(
                        sb,
                        FontHelper.cardTypeFont,
                        player.character.getLocalizedCharacterName(),
                        this.x - 100f * Settings.scale,
                        this.y + this.scroll - (i * 75f * Settings.scale) - 10f * Settings.scale,
                        1000f * Settings.scale,
                        0f,
                        Color.DARK_GRAY,
                        1.0f);
                }

            // Ready Tick
            if (player.ready) {
                sb.draw(
                    ImageMaster.TICK,
                    this.x - 64 / 2f + 164f * Settings.scale,
                    this.y + this.scroll - (i * 75f * Settings.scale) - 64 / 2f - 2f * Settings.scale,
                    64 / 2f, 64 / 2f,
                    64, 64,
                    Settings.scale, Settings.scale,
                    0f, 0, 0, 64, 64,
                    false, false);
            }
 
            // Team Box
            if (NewMenuButtons.newGameScreen.teamsToggle.isTicked()) {
                sb.setColor(RemotePlayer.colourChoices[player.team]);
                sb.draw(TogetherManager.colourIndicatorImg, 
                    this.x - (464 / 2f) * Settings.scale, 
                    this.y + this.scroll - (i * 75f * Settings.scale) - (75f / 2f) * Settings.scale, 
                    TogetherManager.colourIndicatorImg.getWidth() * Settings.scale * 2f, TogetherManager.colourIndicatorImg.getHeight() * Settings.scale * 0.9f);
                sb.setColor(Color.WHITE.cpy());
            }

            // Version warning  
            Color versionColor = Color.WHITE;
            String warningString = "?";
            Boolean draw = false;

            if (player.version == 0) {
                draw = true;
            } else if (player.version != TogetherManager.VERSION || (!player.safeMods && player.modHash != TogetherManager.getCurrentUser().modHash)) {
                versionColor = Color.RED;
                warningString = "!";
                draw = true;
            } else if (player.modHash != TogetherManager.getCurrentUser().modHash) {
                versionColor = Color.GOLD;
                draw = true;
            }

            if (draw) {
                FontHelper.renderSmartText(
                    sb,
                    FontHelper.topPanelInfoFont,
                    warningString,
                    this.x - 64 / 2f + (464 / 2f) * Settings.scale + 8f * Settings.scale,
                    this.y + this.scroll - (i * 75f * Settings.scale) - 64 / 2f - 2f * Settings.scale + 24f * Settings.scale,
                    1000f * Settings.scale,
                    0f,
                    versionColor,
                    1.0f);
            }

            kickbox.render(sb);
            versionbox.render(sb);
            connectbox.render(sb);

        }
    }
}