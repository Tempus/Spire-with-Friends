package chronoMods.utilities;

import com.evacipated.cardcrawl.modthespire.lib.*;

import basemod.*;
import basemod.abstracts.*;
import basemod.interfaces.*;

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

public class LeaveLobbyPatching
{
    @SpirePatch(clz = CardCrawlGame.class, method="startOver")
    public static class LeaveLobbyOnRestart {
        public static void Postfix() {
            NetworkHelper.leaveLobby();
        }
    }

    @SpirePatch(clz = CardCrawlGame.class, method="startOverButShowCredits")
    public static class LeaveLobbyOnVictory {
        public static void Postfix() {
            NetworkHelper.leaveLobby();
        }
    }
}