package chronoMods.steam;

import com.evacipated.cardcrawl.modthespire.lib.*;

import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import com.megacrit.cardcrawl.core.Settings;

import chronoMods.*;
import chronoMods.steam.*;
import chronoMods.ui.deathScreen.*;
import chronoMods.ui.hud.*;
import chronoMods.ui.lobby.*;
import chronoMods.ui.mainMenu.*;
import chronoMods.utilities.*;

public class CoopDeathRevival {
    // When one player dies, they are 'sustained' by their ally's health.
    //  If either player takes damage during this time, both their health drops.
    //  Extra 'overkill' damage will also be applied to both players
    //  If both players have 0 hp, they die

    public static boolean noHP = false;

    @SpirePatch(clz = AbstractPlayer.class, method="damage")
    public static class sendGainGold {
        @SpireInsertPatch(rloc=1812-1708, localvars={"damageAmount"})
        public static SpireReturn Insert(AbstractPlayer player, DamageInfo amount, int damageAmount) {

            // We are currently 'dead', so we should take HP from our allies instead
            if (player.currentHealth < 1) {

                // Check to see if we should die or not
                boolean shouldDie = true;
                for (RemotePlayer teamMember : TogetherManager.players) {
                    if (teamMember.hp > -player.currentHealth) {
                        shouldDie = false;
                    }
                }

                if (shouldDie) {
                    return SpireReturn.Continue();
                } else {

                    CoopDeathRevival.noHP = true;

                    // Send out the shared HP
                    NetworkHelper.sendData(NetworkHelper.dataType.Hp);

                    // Give Feedback here on what's happening

                    // Make sure we don't die unless we should!
                    player.isDead = false;
                    player.currentHealth = 0;

                }

                return SpireReturn.Return(null);

            }

            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = AbstractPlayer.class, method="heal")
    public static class sendHeal {
        public static void Prefix(AbstractPlayer player, int amount) {
            // Reset the fact that we have health
            CoopDeathRevival.noHP = false;
        }
    }

}
