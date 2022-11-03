package chronoMods.utilities;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.GameTips;

import java.util.ArrayList;
import java.util.Collections;

public class CustomTips
{
    @SpirePatch(clz = GameTips.class, method="initialize")
    public static class CustomHintAndTips {
        public static void Postfix(GameTips __instance) {
            ArrayList<String> tips = new ArrayList();
            Collections.addAll(tips, CardCrawlGame.languagePack.getUIString("Tips").TEXT); 
            ReflectionHacks.setPrivate(__instance, GameTips.class, "tips", tips);
        }
    }
}