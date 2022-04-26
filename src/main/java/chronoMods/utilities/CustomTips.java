package chronoMods.utilities;

import com.evacipated.cardcrawl.modthespire.lib.*;
import basemod.*;

import com.badlogic.gdx.Gdx;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.helpers.*;
import com.megacrit.cardcrawl.core.*;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.*;

import chronoMods.*;
import chronoMods.network.steam.*;
import chronoMods.network.*;
import chronoMods.ui.deathScreen.*;
import chronoMods.ui.hud.*;
import chronoMods.ui.lobby.*;
import chronoMods.ui.mainMenu.*;
import chronoMods.utilities.*;

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