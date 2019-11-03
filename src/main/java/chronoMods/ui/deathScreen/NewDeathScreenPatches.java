package chronoMods.ui.deathScreen;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;

import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon.CurrentScreen;
import com.megacrit.cardcrawl.cutscenes.Cutscene;
import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.events.beyond.SpireHeart;

import chronoMods.*;
import chronoMods.steam.*;
import chronoMods.ui.deathScreen.*;
import chronoMods.ui.hud.*;
import chronoMods.ui.lobby.*;
import chronoMods.ui.mainMenu.*;
import chronoMods.utilities.*;

public class NewDeathScreenPatches
{

    static public RaceEndScreen raceEndScreen;

    public static class Enum
    {
        @SpireEnum
        public static AbstractDungeon.CurrentScreen RACEEND;
    }


    @SpirePatch(clz=AbstractPlayer.class, method="damage"
    )
    public static class ScreenOnDying
    {
        @SpireInsertPatch(rloc=151, localvars={})
        public static void Insert(AbstractPlayer p, DamageInfo info)
        {
            NewDeathScreenPatches.raceEndScreen = new RaceEndScreen(AbstractDungeon.getMonsters());
            AbstractDungeon.screen = NewDeathScreenPatches.Enum.RACEEND;
        }
    }

    @SpirePatch(clz=SpireHeart.class, method="buttonEffect"
    )
    public static class ScreenOnActThreeWin
    {
        @SpireInsertPatch(rloc=68, localvars={}) // Maybe 69
        public static void Insert(SpireHeart p, int buttonPressed)
        {
            NewDeathScreenPatches.raceEndScreen = new RaceEndScreen(AbstractDungeon.getMonsters());
            AbstractDungeon.screen = NewDeathScreenPatches.Enum.RACEEND;
        }
    }

    @SpirePatch(clz=Cutscene.class, method="openVictoryScreen")
    public static class ScreenOnHeartKill
    {
        public static void Postfix(Cutscene __instance)
        {
            NewDeathScreenPatches.raceEndScreen = new RaceEndScreen(null);
            AbstractDungeon.screen = NewDeathScreenPatches.Enum.RACEEND;
        }
    }

    @SpirePatch(clz=AbstractDungeon.class, method="update")
    public static class Update
    {
        public static void Postfix(AbstractDungeon __instance)
        {
            if (__instance.screen == NewDeathScreenPatches.Enum.RACEEND) {
                NewDeathScreenPatches.raceEndScreen.update();
            }
        }
    }

    @SpirePatch(clz=AbstractDungeon.class, method="render")
    public static class Render
    {
        public static void Postfix(AbstractDungeon __instance, SpriteBatch sb)
        {
            if (__instance.screen == NewDeathScreenPatches.Enum.RACEEND) {
                NewDeathScreenPatches.raceEndScreen.render(sb);
            }
        }
    }

    @SpirePatch(clz=AbstractDungeon.class, method="openPreviousScreen")
    public static class Reopen
    {
        public static void Postfix(CurrentScreen s)
        {
            if (s == NewDeathScreenPatches.Enum.RACEEND) {
                NewDeathScreenPatches.raceEndScreen.reopen();
            }
        }
    }
}