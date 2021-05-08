package chronoMods.ui.hud;

import com.evacipated.cardcrawl.modthespire.lib.*;
import basemod.interfaces.*;

import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import com.megacrit.cardcrawl.core.Settings;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.potions.*;
import com.megacrit.cardcrawl.map.*;
import com.megacrit.cardcrawl.neow.*;
import com.megacrit.cardcrawl.relics.*;
import com.megacrit.cardcrawl.screens.select.*;
import com.megacrit.cardcrawl.screens.mainMenu.*;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.unlock.AbstractUnlock;

import java.util.*;

import chronoMods.*;
import chronoMods.coop.relics.*;
import chronoMods.network.steam.*;
import chronoMods.network.*;
import chronoMods.ui.deathScreen.*;
import chronoMods.ui.hud.*;
import chronoMods.ui.lobby.*;
import chronoMods.ui.mainMenu.*;
import chronoMods.utilities.*;

public class InfoPopupPatches {

    @SpirePatch(clz = CardCrawlGame.class, method="update")
    public static class infoDungeonUpdate {
        @SpireInsertPatch(rloc=760-733)
        public static void Insert(CardCrawlGame __instance) {
            TogetherManager.infoPopup.update();
        }
    }

    @SpirePatch(clz = CardCrawlGame.class, method="render")
    public static class infoRender {
        @SpireInsertPatch(rloc=458-408)
        public static void Insert(CardCrawlGame __instance, SpriteBatch ___sb) {
            TogetherManager.infoPopup.render(___sb);
        }
    }
}
