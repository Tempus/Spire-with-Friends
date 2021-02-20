package chronoMods.coop;

import com.evacipated.cardcrawl.modthespire.lib.*;

import basemod.*;
import basemod.abstracts.*;
import basemod.interfaces.*;

import org.apache.logging.log4j.*;

import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.helpers.*;
import com.megacrit.cardcrawl.helpers.input.*;
import com.megacrit.cardcrawl.helpers.controller.*;
import com.megacrit.cardcrawl.rooms.*;
import com.megacrit.cardcrawl.map.*;
import com.megacrit.cardcrawl.saveAndContinue.*;
import com.megacrit.cardcrawl.rewards.*;
import com.megacrit.cardcrawl.ui.panels.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.characters.AbstractPlayer;

import chronoMods.*;
import chronoMods.coop.*;
import chronoMods.steam.*;
import chronoMods.ui.deathScreen.*;
import chronoMods.ui.hud.*;
import chronoMods.ui.lobby.*;
import chronoMods.ui.mainMenu.*;
import chronoMods.utilities.*;

import java.util.*;
import java.lang.*;
import java.nio.*;

import com.codedisaster.steamworks.*;
import com.megacrit.cardcrawl.integrations.steam.*;

public class MapDensityPatch
{
    @SpirePatch(clz = MapGenerator.class, method="generateDungeon")
    public static class changeDungeonDensity {
        public static void Prefix(int height, int width, @ByRef int[] pathDensity, com.megacrit.cardcrawl.random.Random rng) {
            if (TogetherManager.gameMode == TogetherManager.mode.Coop) {
                pathDensity[0] = 2 + TogetherManager.players.size() * 2; // 4, 6, 8, 10, 12, 14
            }
        }
    }
}