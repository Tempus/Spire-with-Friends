package chronoMods.ui.mainMenu;

import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen;
import com.megacrit.cardcrawl.screens.mainMenu.MenuButton;

import chronoMods.*;
import chronoMods.network.steam.*;
import chronoMods.network.*;
import chronoMods.ui.deathScreen.*;
import chronoMods.ui.hud.*;
import chronoMods.ui.lobby.*;
import chronoMods.ui.mainMenu.*;
import chronoMods.utilities.*;
import com.evacipated.cardcrawl.modthespire.lib.*;


import java.util.Arrays;

@SpirePatch(
    clz=MainMenuScreen.class,
    method="setMainMenuButtons"
)
public class MainMenuItems
{
    @SpireInsertPatch(
        rloc=16,
        localvars={"index"}
    )
    public static SpireReturn Insert(Object __obj_instance, @ByRef int[] index)
    {
        MainMenuScreen __instance = (MainMenuScreen)__obj_instance;
        __instance.buttons.add(new MenuButton(NewMenuButtons.BINGO, index[0]++));
        __instance.buttons.add(new MenuButton(NewMenuButtons.VERSUS, index[0]++));
        __instance.buttons.add(new MenuButton(NewMenuButtons.COOP, index[0]++));

        return SpireReturn.Return(null);
    }
}