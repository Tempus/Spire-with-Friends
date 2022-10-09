package chronoMods.utilities;

import chronoMods.network.NetworkHelper;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;

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