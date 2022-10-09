  package chronoMods.coop;

  import basemod.ReflectionHacks;
  import chronoMods.TogetherManager;
  import chronoMods.network.NetworkHelper;
  import chronoMods.network.RemotePlayer;
  import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
  import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
  import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
  import com.megacrit.cardcrawl.core.CardCrawlGame;
  import com.megacrit.cardcrawl.core.Settings;
  import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
  import com.megacrit.cardcrawl.map.MapRoomNode;
  import com.megacrit.cardcrawl.rewards.RewardItem;
  import com.megacrit.cardcrawl.rewards.chests.AbstractChest;
  import com.megacrit.cardcrawl.rooms.AbstractRoom;
  import com.megacrit.cardcrawl.rooms.CampfireUI;
  import com.megacrit.cardcrawl.rooms.MonsterRoomElite;
  import com.megacrit.cardcrawl.ui.campfire.AbstractCampfireOption;
  import com.megacrit.cardcrawl.ui.campfire.RecallOption;
  import com.megacrit.cardcrawl.vfx.ObtainKeyEffect;
  import com.megacrit.cardcrawl.vfx.campfire.CampfireRecallEffect;

  import java.util.ArrayList;

public class CoopKeySharing {

/* Things I need to do here

- SendData to a new Remote player field to record whether or not they have keys
- Emerald, Sapphire, and Ruby key should always be available if any player is missing a key
- Collecting a key when you already have it should send the key to the next player missing one
- There should ideally be feedback that you are giving another player the key
- Potentially we need the key icons on the player widgets
- If any player doesn't have three keys, the door won't open...?

*/

	public static RemotePlayer redKeyPlayer = null;
	public static RemotePlayer blueKeyPlayer = null;
	public static RemotePlayer greenKeyPlayer = null;

	// Key notify
    @SpirePatch(clz = ObtainKeyEffect.class, method="update")
    public static class notifyWeHaveAKey {
        public static void Postfix(ObtainKeyEffect __instance) {
		    if (__instance.isDone == true) {
			      switch ((ObtainKeyEffect.KeyColor)ReflectionHacks.getPrivate(__instance, ObtainKeyEffect.class, "keyColor")) {
			        case RED:
                      redKeyPlayer = TogetherManager.getCurrentUser();
			          NetworkHelper.sendData(NetworkHelper.dataType.GetRedKey);
			          break;
			        case BLUE:
                      blueKeyPlayer = TogetherManager.getCurrentUser();
			          NetworkHelper.sendData(NetworkHelper.dataType.GetBlueKey);
			          break;
			        case GREEN:
                      greenKeyPlayer = TogetherManager.getCurrentUser();
			          NetworkHelper.sendData(NetworkHelper.dataType.GetGreenKey);
			          break;
			      } 
		    } 
        }
    }



	// Acquiring the Red Key

	// Make sure red key option is available
    @SpirePatch(clz = CampfireUI.class, method="initializeButtons")
    public static class enableRedKeyRecall {
    	@SpireInsertPatch(rloc=117-92)
        public static void Insert(CampfireUI __instance) {
            if (TogetherManager.gameMode != TogetherManager.mode.Coop) { return; }

		    if (Settings.isFinalActAvailable && Settings.hasRubyKey && redKeyNeeded())
		      ((ArrayList<AbstractCampfireOption>)ReflectionHacks.getPrivate(__instance, CampfireUI.class, "buttons")).add(new RecallOption());
        }
    }

	// Pick up the red key for others
    @SpirePatch(clz = CampfireRecallEffect.class, method="update")
    public static class updateRedKeyRecall {
    	@SpireInsertPatch(rloc=32-31)
        public static void Insert(CampfireRecallEffect __instance) {
            if (TogetherManager.gameMode != TogetherManager.mode.Coop) { return; }

		    if (__instance.duration < 1.0F && Settings.hasRubyKey && CoopKeySharing.redKeyNeeded() && (boolean)ReflectionHacks.getPrivate(__instance, CampfireRecallEffect.class, "hasRecalled") == false) {
		    	ReflectionHacks.setPrivate(__instance, CampfireRecallEffect.class, "hasRecalled", true);
		        CardCrawlGame.sound.play("ATTACK_MAGIC_SLOW_2");
		        (AbstractDungeon.getCurrRoom()).rewards.clear();
		        (AbstractDungeon.getCurrRoom()).phase = AbstractRoom.RoomPhase.COMPLETE;
		        NetworkHelper.sendData(NetworkHelper.dataType.GetRedKey);
		    } 
        }
    }

    public static boolean redKeyNeeded() {
    	for (RemotePlayer player : TogetherManager.players) {
    		if (player.rubyKey == false) {
    			redKeyPlayer = player;
    			return true;
    		}
    	}
    	return false;
    }


	// Acquiring the Blue Key

	// Make sure blue key option is available
    @SpirePatch(clz = AbstractChest.class, method="open")
    public static class enableBlueKeyChest {
    	@SpireInsertPatch(rloc=123-79)
        public static void Insert(AbstractChest __instance, boolean bossChest) {
            if (TogetherManager.gameMode != TogetherManager.mode.Coop) { return; }

		    if (Settings.isFinalActAvailable && Settings.hasSapphireKey && blueKeyNeeded())
		        AbstractDungeon.getCurrRoom().addSapphireKey(
		            (AbstractDungeon.getCurrRoom()).rewards.get((AbstractDungeon.getCurrRoom()).rewards.size() - 1)); 
        }
    }

	// Pick up the blue key for others
    @SpirePatch(clz = RewardItem.class, method="claimReward")
    public static class updateBlueKeyReward {
    	@SpireInsertPatch(rloc=358-290)
        public static void Insert(RewardItem __instance) {
            if (TogetherManager.gameMode != TogetherManager.mode.Coop) { return; }

		    if (Settings.hasSapphireKey && CoopKeySharing.blueKeyNeeded() && !__instance.ignoreReward) {
		      __instance.ignoreReward = true;
		      NetworkHelper.sendData(NetworkHelper.dataType.GetBlueKey);
		    } 
        }
    }

    public static boolean blueKeyNeeded() {
    	for (RemotePlayer player : TogetherManager.players) {
    		if (player.sapphireKey == false) {
    			blueKeyPlayer = player;
    			return true;
    		}
    	}
    	return false;
    }

	// Acquiring the Green Key

	// Make sure Green key option is available
    @SpirePatch(clz = MonsterRoomElite.class, method="addEmeraldKey")
    public static class enableGreenKeyMonster {
        public static void Postfix(MonsterRoomElite __instance) {
            if (TogetherManager.gameMode != TogetherManager.mode.Coop) { return; }

		    if (Settings.isFinalActAvailable && Settings.hasEmeraldKey && greenKeyNeeded() && !__instance.rewards.isEmpty() && (AbstractDungeon.getCurrMapNode()).hasEmeraldKey)
		      __instance.rewards.add(new RewardItem(__instance.rewards.get(__instance.rewards.size() - 1), RewardItem.RewardType.EMERALD_KEY)); 
        }
    }

    @SpirePatch(clz = AbstractDungeon.class, method="setEmeraldElite")
    public static class enableGreenKeyMapNode {
        public static SpireReturn Prefix() {
            if (TogetherManager.gameMode != TogetherManager.mode.Coop) { return SpireReturn.Continue(); }

		    if (Settings.isFinalActAvailable && greenKeyNeeded()) {
		      ArrayList<MapRoomNode> eliteNodes = new ArrayList<>();
		      for (int i = 0; i < AbstractDungeon.map.size(); i++) {
		        for (int j = 0; j < ((ArrayList)AbstractDungeon.map.get(i)).size(); j++) {
		          if (((MapRoomNode)((ArrayList)AbstractDungeon.map.get(i)).get(j)).room instanceof MonsterRoomElite)
		            eliteNodes.add(((ArrayList<MapRoomNode>)AbstractDungeon.map.get(i)).get(j)); 
		        } 
		      } 

              int BurnersToAdd = Math.min(howManyGreensNeeded(), ((AbstractDungeon.actNum)*2)); // 2,4,6 max burning elites, but never more than players who need keys

              int failsafe = 0;
              for (int k = 0; k < BurnersToAdd; k++) {
                  MapRoomNode chosenNode = eliteNodes.get(AbstractDungeon.mapRng.random(0, eliteNodes.size() - 1));

                  if (chosenNode.hasEmeraldKey)
                    k--;

                  chosenNode.hasEmeraldKey = true;

                  failsafe++;
                  if (failsafe > 20) { return SpireReturn.Return(null); }
              }
		    } 

            return SpireReturn.Return(null);
        }
    }

	// Pick up the Green key for others
    @SpirePatch(clz = RewardItem.class, method="claimReward")
    public static class updateGreenKeyReward {
    	@SpireInsertPatch(rloc=369-290)
        public static SpireReturn<Boolean> Insert(RewardItem __instance) {
            if (TogetherManager.gameMode != TogetherManager.mode.Coop) { return SpireReturn.Continue(); }

		    if (Settings.hasEmeraldKey && CoopKeySharing.greenKeyNeeded()) {
		        NetworkHelper.sendData(NetworkHelper.dataType.GetGreenKey);
		        __instance.img.dispose();
		        __instance.outlineImg.dispose();
  		        return SpireReturn.Return(true);
		    } 

		    return SpireReturn.Continue();
        }
    }

    public static boolean greenKeyNeeded() {
    	for (RemotePlayer player : TogetherManager.players) {
    		if (player.emeraldKey == false) {
    			greenKeyPlayer = player;
    			return true;
    		}
    	}
    	return false;
    }

    public static int howManyGreensNeeded() {
        int gkey = 0;
        for (RemotePlayer player : TogetherManager.players) {
            if (player.emeraldKey == false) {
                gkey++;
            }
        }

        return gkey;
    }

}