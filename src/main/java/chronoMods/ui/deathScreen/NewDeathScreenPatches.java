package chronoMods.ui.deathScreen;

import chronoMods.TogetherManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.cutscenes.Cutscene;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon.CurrentScreen;
import com.megacrit.cardcrawl.events.beyond.SpireHeart;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.rooms.TrueVictoryRoom;
import com.megacrit.cardcrawl.rooms.VictoryRoom;
import com.megacrit.cardcrawl.screens.DeathScreen;

public class NewDeathScreenPatches
{

    static public EndScreenBase EndScreenBase;
    static public boolean Ironman = false;

    public static class Enum
    {
        @SpireEnum
        public static AbstractDungeon.CurrentScreen RACEEND;
    }

    public static void chooseEndScreen(MonsterGroup m) {
        // Did we win?
        boolean isVictory = (AbstractDungeon.getCurrRoom() instanceof VictoryRoom && !Settings.isFinalActAvailable) || AbstractDungeon.getCurrRoom() instanceof TrueVictoryRoom;

        // What do next?
        if (isVictory) {
            if (TogetherManager.gameMode == TogetherManager.mode.Coop)
                NewDeathScreenPatches.EndScreenBase = new EndScreenCoopVictory(m);
            else if (TogetherManager.gameMode == TogetherManager.mode.Versus)
                NewDeathScreenPatches.EndScreenBase = new EndScreenVersusVictory(m);
            else if (TogetherManager.gameMode == TogetherManager.mode.Bingo)
                NewDeathScreenPatches.EndScreenBase = new EndScreenBingoLoss(m); // Bingo only wins when the network says it wins
        } else {
            if (TogetherManager.gameMode == TogetherManager.mode.Coop)
                NewDeathScreenPatches.EndScreenBase = new EndScreenCoopLoss(m);
            else if (TogetherManager.gameMode == TogetherManager.mode.Versus)
                NewDeathScreenPatches.EndScreenBase = new EndScreenVersusLoss(m);
            else if (TogetherManager.gameMode == TogetherManager.mode.Bingo)
                NewDeathScreenPatches.EndScreenBase = new EndScreenBingoLoss(m);
        }

        // Don't forget to set!
        AbstractDungeon.screen = NewDeathScreenPatches.Enum.RACEEND;
    }

    @SpirePatch(clz=DeathScreen.class, method=SpirePatch.CONSTRUCTOR)
    public static class ScreenOnDying
    {
        public static SpireReturn Prefix(DeathScreen d, MonsterGroup m)
        {
            if (TogetherManager.gameMode == TogetherManager.mode.Normal) { return SpireReturn.Continue(); }
            chooseEndScreen(m);

            return SpireReturn.Return(null);
        }
    }

    @SpirePatch(clz=DeathScreen.class, method="reopen", paramtypez={boolean.class})
    public static class ReopenPlayerDeath
    {
        public static SpireReturn Prefix(DeathScreen d, boolean fromVictoryUnlock)
        {
            if (TogetherManager.gameMode == TogetherManager.mode.Normal) { return SpireReturn.Continue(); }

            NewDeathScreenPatches.EndScreenBase.reopen();
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

            chooseEndScreen(AbstractDungeon.getMonsters());

            return SpireReturn.Return(null);
        }
    }

    @SpirePatch(clz=Cutscene.class, method="openVictoryScreen")
    public static class ScreenOnHeartKill
    {
        public static void Postfix(Cutscene __instance)
        {
            chooseEndScreen(null);
        }
    }

    @SpirePatch(clz=AbstractDungeon.class, method="update")
    public static class PreUpdate
    {
        @SpireInsertPatch(rloc=2520-2506) // Maybe 69
        public static void Insert(AbstractDungeon __instance)
        {
            if (__instance.screen == AbstractDungeon.CurrentScreen.DEATH) {
                __instance.screen = NewDeathScreenPatches.Enum.RACEEND;
            }
        }
    }

    @SpirePatch(clz=AbstractDungeon.class, method="update")
    public static class Update
    {
        public static void Postfix(AbstractDungeon __instance)
        {
            if (__instance.screen == NewDeathScreenPatches.Enum.RACEEND) {
                NewDeathScreenPatches.EndScreenBase.update();
            }
        }
    }

    @SpirePatch(clz=AbstractDungeon.class, method="render")
    public static class Render
    {
        public static void Postfix(AbstractDungeon __instance, SpriteBatch sb)
        {
            if (__instance.screen == NewDeathScreenPatches.Enum.RACEEND) {
                NewDeathScreenPatches.EndScreenBase.render(sb);
            }
        }
    }

    @SpirePatch(clz=AbstractDungeon.class, method="openPreviousScreen")
    public static class Reopen
    {
        public static void Postfix(CurrentScreen s)
        {
            if (s == NewDeathScreenPatches.Enum.RACEEND) {
                NewDeathScreenPatches.EndScreenBase.reopen();
            }
        }
    }
}