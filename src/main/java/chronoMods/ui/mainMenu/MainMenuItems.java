package chronoMods.ui.mainMenu;

import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen;
import com.megacrit.cardcrawl.screens.mainMenu.MenuButton;

import chronoMods.*;
import chronoMods.steam.*;
import chronoMods.ui.deathScreen.*;
import chronoMods.ui.hud.*;
import chronoMods.ui.lobby.*;
import chronoMods.ui.mainMenu.*;
import chronoMods.utilities.*;


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
    public static void Insert(Object __obj_instance, @ByRef int[] index)
    {
        MainMenuScreen __instance = (MainMenuScreen)__obj_instance;
        __instance.buttons.add(new MenuButton(NewMenuButtons.VERSUS, index[0]++));
        __instance.buttons.add(new MenuButton(NewMenuButtons.COOP, index[0]++));
    }
}