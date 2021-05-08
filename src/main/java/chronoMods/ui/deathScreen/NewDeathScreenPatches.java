package chronoMods.ui.deathScreen;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;

import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon.CurrentScreen;
import com.megacrit.cardcrawl.cutscenes.Cutscene;
import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.screens.*;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.events.beyond.SpireHeart;

import chronoMods.*;
import chronoMods.network.steam.*;
import chronoMods.network.*;
import chronoMods.ui.deathScreen.*;
import chronoMods.ui.hud.*;
import chronoMods.ui.lobby.*;
import chronoMods.ui.mainMenu.*;
import chronoMods.utilities.*;

public class NewDeathScreenPatches
{

    static public RaceEndScreen raceEndScreen;
    static public boolean Ironman = false;

    public static class Enum
    {
        @SpireEnum
        public static AbstractDungeon.CurrentScreen RACEEND;
    }


    @SpirePatch(clz=DeathScreen.class, method=SpirePatch.CONSTRUCTOR)
    public static class ScreenOnDying
    {
        public static SpireReturn Prefix(DeathScreen d, MonsterGroup m)
        {
            if (TogetherManager.gameMode == TogetherManager.mode.Normal) { return SpireReturn.Continue(); }

            NewDeathScreenPatches.raceEndScreen = new RaceEndScreen(m);
            AbstractDungeon.screen = NewDeathScreenPatches.Enum.RACEEND;

            return SpireReturn.Return(null);
        }
    }

    @SpirePatch(clz=DeathScreen.class, method="reopen", paramtypez={boolean.class})
    public static class ReopenPlayerDeath
    {
        public static SpireReturn Prefix(DeathScreen d, boolean fromVictoryUnlock)
        {
            if (TogetherManager.gameMode == TogetherManager.mode.Normal) { return SpireReturn.Continue(); }

            NewDeathScreenPatches.raceEndScreen.reopen();
            //AbstractDungeon.screen = NewDeathScreenPatches.Enum.RACEEND;

            return SpireReturn.Return(null);
        }
    }

    @SpirePatch(clz=SpireHeart.class, method="buttonEffect")
    public static class ScreenOnActThreeWin
    {
        @SpireInsertPatch(rloc=68, localvars={}) // Maybe 69
        public static SpireReturn Insert(SpireHeart p, int buttonPressed)
        {
            if (TogetherManager.gameMode == TogetherManager.mode.Normal) { return SpireReturn.Continue(); }

            NewDeathScreenPatches.raceEndScreen = new RaceEndScreen(AbstractDungeon.getMonsters());
            AbstractDungeon.screen = NewDeathScreenPatches.Enum.RACEEND;

            return SpireReturn.Return(null);
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
            if (__instance.screen == AbstractDungeon.CurrentScreen.DEATH) {
                __instance.screen = NewDeathScreenPatches.Enum.RACEEND;
            }
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