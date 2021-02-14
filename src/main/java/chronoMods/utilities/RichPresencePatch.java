package chronoMods.utilities;

import com.evacipated.cardcrawl.modthespire.lib.*;

import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;

import com.megacrit.cardcrawl.localization.*;
import com.megacrit.cardcrawl.core.*;
import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.rooms.*;
import com.megacrit.cardcrawl.helpers.*;
import com.megacrit.cardcrawl.integrations.steam.*;
import com.codedisaster.steamworks.*;

import basemod.*;
import basemod.abstracts.*;
import basemod.interfaces.*;

import java.util.*;

import chronoMods.*;
import chronoMods.steam.*;
import chronoMods.coop.*;
import chronoMods.ui.deathScreen.*;
import chronoMods.ui.hud.*;
import chronoMods.ui.lobby.*;
import chronoMods.ui.mainMenu.*;

public class RichPresencePatch {

    @SpirePatch(clz = SteamIntegration.class, method="setRichPresenceDisplayPlaying", paramtypez={int.class, int.class, String.class})
    public static class PerFloorRichPresenceAscension {
        public static void Replace(SteamIntegration __instance, int floor, int ascension, String character) {
        	if (TogetherManager.gameMode == TogetherManager.mode.Coop)
	        	RichPresencePatch.setRP(character + " Coop with " + TogetherManager.players.size() + " on " + floor + "F, A" + ascension);
	        else
	        	RichPresencePatch.setRP(RichPresencePatch.ordinal(TogetherManager.getCurrentUser().ranking+1) + " in " + character + " Versus on " + floor + "F, A" + ascension);
        }
    }

    @SpirePatch(clz = SteamIntegration.class, method="setRichPresenceDisplayPlaying", paramtypez={int.class, String.class})
    public static class PerFloorRichPresence {
        public static void Replace(SteamIntegration __instance, int floor, String character) {
        	if (TogetherManager.gameMode == TogetherManager.mode.Coop)
	        	RichPresencePatch.setRP(character + " Coop with " + TogetherManager.players.size() + " on " + floor + "F");
	        else
	        	RichPresencePatch.setRP(RichPresencePatch.ordinal(TogetherManager.getCurrentUser().ranking+1) + " in " + character + " Versus on " + floor + "F");
        }
    }

    @SpirePatch(clz = SteamIntegration.class, method="setRichPresenceDisplayInMenu")
    public static class MainMenuRichPresence {
        public static void Replace(SteamIntegration __instance) {
        	RichPresencePatch.setRP("Spire with Friends by Chronometrics");
        }
    }

    public static void setRP(String value) {
    	SteamFriends sf = (SteamFriends)ReflectionHacks.getPrivateStatic(SteamIntegration.class, "steamFriends");

	    if (sf != null) {
	    	sf.setRichPresence("status", value);
	    	sf.setRichPresence("steam_display", "#Status");
	    }
    }

	public static String ordinal(int i) {
	    String[] suffixes = new String[] { "th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th" };
	    switch (i % 100) {
	    case 11:
	    case 12:
	    case 13:
	        return i + "th";
	    default:
	        return i + suffixes[i % 10];

	    }
	}    
}