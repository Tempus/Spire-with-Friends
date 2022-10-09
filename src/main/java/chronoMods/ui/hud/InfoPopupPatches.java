package chronoMods.ui.hud;

import chronoMods.TogetherManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;

public class InfoPopupPatches {

    @SpirePatch(clz = CardCrawlGame.class, method="update")
    public static class infoDungeonUpdate {
        @SpireInsertPatch(rloc=760-733)
        public static void Insert(CardCrawlGame __instance) {
            TogetherManager.infoPopup.update();
        }
    }

    @SpirePatch(clz = CardCrawlGame.class, method="render")
    public static class infoRender {
        @SpireInsertPatch(rloc=458-408)
        public static void Insert(CardCrawlGame __instance, SpriteBatch ___sb) {
            TogetherManager.infoPopup.render(___sb);
        }
    }
}
