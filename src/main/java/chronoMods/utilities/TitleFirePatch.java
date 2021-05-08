package chronoMods.utilities;

import com.evacipated.cardcrawl.modthespire.lib.*;

import basemod.*;
import basemod.abstracts.*;
import basemod.interfaces.*;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.vfx.scene.LogoFlameEffect;
import com.megacrit.cardcrawl.vfx.*;
import com.badlogic.gdx.math.MathUtils;
import org.apache.logging.log4j.*;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.helpers.*;
import com.megacrit.cardcrawl.rooms.*;
import com.megacrit.cardcrawl.map.*;
import com.megacrit.cardcrawl.saveAndContinue.*;
import com.megacrit.cardcrawl.rewards.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.characters.AbstractPlayer;

import chronoMods.*;
import chronoMods.coop.*;
import chronoMods.network.steam.*;
import chronoMods.network.*;
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

public class TitleFirePatch
{

    public static Color[] colourChoices = new Color[] {
      Color.RED.cpy(),
      Color.BLUE.cpy(),
      Color.GREEN.cpy(),
      Color.YELLOW.cpy(),
      Color.ORANGE.cpy(),
      Color.PINK.cpy(),
      Color.PURPLE.cpy()
    };

    @SpirePatch(clz = LogoFlameEffect.class, method="render", paramtypez = {SpriteBatch.class, float.class, float.class})
    public static class MoveFlamesOver {
        public static void Prefix(LogoFlameEffect __instance, SpriteBatch sb, @ByRef float x[], @ByRef float y[]) {
            x[0] = x[0] - 145.0F * Settings.scale;
            y[0] = y[0] - 32.0F * Settings.scale;

            Color color = colourChoices[MathUtils.random(0,6)];

            ReflectionHacks.setPrivate(__instance, AbstractGameEffect.class, "color", color);   
        }
    }
}
