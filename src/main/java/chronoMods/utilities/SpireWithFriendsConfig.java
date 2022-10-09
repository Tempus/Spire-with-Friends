package chronoMods.utilities;

import basemod.ModLabeledButton;
import basemod.ModPanel;
import chronoMods.TogetherManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;

import java.awt.*;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

public class SpireWithFriendsConfig extends ModPanel {

    Color textColor = Settings.CREAM_COLOR.cpy();
    public static final String[] Text = CardCrawlGame.languagePack.getUIString("ConfigScreen").TEXT;

    public Texture mark = TogetherManager.bingoMark;
     
    public SpireWithFriendsConfig() {
    	super();

        if (TogetherManager.customMark != null)
            mark = TogetherManager.customMark;

        addUIElement(new ModLabeledButton(Text[3], 400f+120f+20f, 550f+60f-35f, this, (me) -> { clickLoadImageButton(); }));
    }
     
    public void clickLoadImageButton() {
        FileDialog fileDialog = new FileDialog((Frame) null, "Load an image");

        fileDialog.setFilenameFilter(new ImageFileFilter());
        fileDialog.setVisible(true);

        File[] files = fileDialog.getFiles();

        if (files == null || files.length == 0) {
            TogetherManager.logger.info("No file");
        } else {
            // Import and Resize the image
            Pixmap importMark = new Pixmap(new FileHandle(files[0]));
            Pixmap sizedMark = new Pixmap(120, 120, importMark.getFormat());
            importMark.setFilter(Pixmap.Filter.BiLinear);
            sizedMark.setFilter(Pixmap.Filter.BiLinear);
            sizedMark.drawPixmap(importMark,
                    0, 0, importMark.getWidth(), importMark.getHeight(),
                    0, 0, sizedMark.getWidth(), sizedMark.getHeight()
            );
            mark = new Texture(sizedMark);

            // Save the resized image to a file, and store the value in config
            String destpath = SpireConfig.makeFilePath(TogetherManager.MODNAME, "mark", "png");

            PixmapIO.writePNG(new FileHandle(destpath), sizedMark);
            TogetherManager.config.setString("mark", destpath);
            try {
                TogetherManager.config.save();
            } catch (IOException e) {}
            

            // Cleanup
            importMark.dispose();
            sizedMark.dispose();
        }
    }

    class ImageFileFilter implements FilenameFilter {
        private final String[] okFileExtensions = new String[] { "jpg", "jpeg", "png" };

        public boolean accept(File file, String name) {
            for (String extension : okFileExtensions) {
                if (name.toLowerCase().endsWith(extension)) {
                    return true;
                }
            }
            return false;
        }
    }

    public void render(SpriteBatch sb) {
        super.render(sb);

        float s = Settings.scale;

        FontHelper.renderFontLeftDownAligned(sb, FontHelper.cardTitleFont,  Text[0], 400f * s, 715f * s, textColor);
        FontHelper.renderFontLeftDownAligned(sb, FontHelper.cardDescFont_N, Text[1], 400f * s, 690f * s, textColor);

        sb.draw(mark, 400f * s, 550f * s);

        FontHelper.renderFontCentered(sb,        FontHelper.cardTypeFont,   Text[2], 460f * s, 530f * s, textColor);
    }
}