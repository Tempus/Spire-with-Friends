package chronoMods.coop;

import chronoMods.TogetherManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.rewards.chests.AbstractChest;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import com.megacrit.cardcrawl.vfx.ChestShineEffect;
import com.megacrit.cardcrawl.vfx.scene.SpookyChestEffect;

public class CoopEmptyRoom extends AbstractRoom {

	@SpirePatch(clz=AbstractRoom.class, method=SpirePatch.CLASS)
	public static class LockedRoomField { public static SpireField<Boolean> locked = new SpireField<>(() -> false); }

    @SpirePatch(clz = MapRoomNode.class, method="isConnectedTo")
    public static class lockedRoomNoGo {
        public static SpireReturn<Boolean> Prefix(MapRoomNode __instance, MapRoomNode node) {
        	if (node == null || node.getRoom() == null) {
	            return SpireReturn.Continue();
        	}

            if (CoopEmptyRoom.LockedRoomField.locked.get(node.getRoom())) {
            	return SpireReturn.Return(false);
            }

            if (node.getRoom() instanceof MonsterRoomBoss) {
            	return SpireReturn.Return(false);
            }

		    if (AbstractDungeon.getCurrMapNode()!=null) {
				if (AbstractDungeon.getCurrMapNode().getRoom() instanceof MonsterRoomBoss) {
		     	    return SpireReturn.Return(false);
	           	}
		    }

            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = MapRoomNode.class, method="wingedIsConnectedTo")
    public static class lockedRoomNoFlyZone {
        public static SpireReturn<Boolean> Prefix(MapRoomNode __instance, MapRoomNode node) {
            if (CoopEmptyRoom.LockedRoomField.locked.get(node.getRoom())) {
            	return SpireReturn.Return(false);
            }

            if (node.getRoom() instanceof MonsterRoomBoss) {
            	return SpireReturn.Return(false);
            }

            if (AbstractDungeon.getCurrMapNode().getRoom() instanceof MonsterRoomBoss) {
            	return SpireReturn.Return(false);
            }

            return SpireReturn.Continue();
        }
    }

    // First node locker
    @SpirePatch(clz = MapRoomNode.class, method="update")
    public static class firstRoomLockedRoomNoGo {
    	@SpireInsertPatch(rloc=337-219)
        public static SpireReturn Insert(MapRoomNode __instance) {
            if (CoopEmptyRoom.LockedRoomField.locked.get(__instance.getRoom())) {
            	return SpireReturn.Return(null);
            }

            return SpireReturn.Continue();
        }
    }

	// Treasure
	public AbstractChest chest;
	private float shinyTimer = 0f;
	private static final float SHINY_INTERVAL = 0.2f;

	// Default Constructor
	public CoopEmptyRoom() {
		phase = RoomPhase.COMPLETE;
		mapSymbol = "-";
		mapImg = TogetherManager.mapEmpty;
		mapImgOutline = TogetherManager.mapEmptyOutline;
        if (AbstractDungeon.player.hasBlight("DowsingRod")) {
            chest = new CoopBoxChest();
        }
        monsters = new MonsterGroup(new AbstractMonster[0]);
	}

	@Override
	public void onPlayerEntry() {
		playBGM(null);
		// chest = AbstractDungeon.getRandomChest();
		AbstractDungeon.overlayMenu.proceedButton.setLabel("Move on");

		rewards.clear();
	}

	// Update the treasure room
	@Override
	public void update() {
		super.update();
		if (chest != null) {
			chest.update();
			updateShiny();
		}
	}

	private void updateShiny() {
		if (!chest.isOpen) {
			shinyTimer -= Gdx.graphics.getDeltaTime();
			if (shinyTimer < 0f && !Settings.DISABLE_EFFECTS) {
				shinyTimer = SHINY_INTERVAL;
				AbstractDungeon.topLevelEffects.add(new ChestShineEffect());
				AbstractDungeon.effectList.add(new SpookyChestEffect());
				AbstractDungeon.effectList.add(new SpookyChestEffect());
			}
		}
	}

	// Render the contents of the room
	@Override
	public void render(SpriteBatch sb) {
		if (chest != null) {
			chest.render(sb);
		}
		super.render(sb);
	}
}
