package chronoMods.ui.mainMenu;

import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen;
import com.megacrit.cardcrawl.screens.mainMenu.MenuButton;

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