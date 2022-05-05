package chronoMods.coop.hardmode;

import com.evacipated.cardcrawl.modthespire.lib.*;

import com.badlogic.gdx.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;

import com.megacrit.cardcrawl.localization.*;
import com.megacrit.cardcrawl.core.*;
import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.cards.curses.*;
import com.megacrit.cardcrawl.cards.status.*;
import com.megacrit.cardcrawl.blights.*;
import com.megacrit.cardcrawl.helpers.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.city.*;
import com.megacrit.cardcrawl.events.shrines.*;
import com.megacrit.cardcrawl.rewards.*;
import com.megacrit.cardcrawl.rooms.*;
import com.megacrit.cardcrawl.map.*;
import com.megacrit.cardcrawl.shop.*;
import com.megacrit.cardcrawl.powers.*;
import com.megacrit.cardcrawl.actions.*;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.monsters.*;
import com.megacrit.cardcrawl.monsters.beyond.*;
import com.megacrit.cardcrawl.monsters.city.*;
import com.megacrit.cardcrawl.monsters.exordium.*;
import com.megacrit.cardcrawl.monsters.ending.*;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.scene.*;
import com.megacrit.cardcrawl.vfx.campfire.*;
import com.megacrit.cardcrawl.screens.*;

import basemod.*;
import basemod.abstracts.*;
import basemod.interfaces.*;

import java.util.*;

import chronoMods.*;
import chronoMods.network.steam.*;
import chronoMods.network.*;
import chronoMods.coop.*;
import chronoMods.ui.deathScreen.*;
import chronoMods.ui.hud.*;
import chronoMods.ui.lobby.*;
import chronoMods.ui.mainMenu.*;


import com.megacrit.cardcrawl.actions.utility.*;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.unique.*;
import com.megacrit.cardcrawl.vfx.combat.*;
import com.megacrit.cardcrawl.vfx.cardManip.*;
import com.megacrit.cardcrawl.vfx.*;

public class StrangeFlame extends AbstractBlight {
    public static final String ID = "StrangeFlame";
    private static final BlightStrings blightStrings = CardCrawlGame.languagePack.getBlightString(ID);
    public static final String NAME = blightStrings.NAME;
    public static final String[] DESCRIPTIONS = blightStrings.DESCRIPTION;

    public ArrayList<Integer> bossList = new ArrayList();

    public StrangeFlame() {
        super(ID, NAME, "", "spear.png", true);
        this.blightID = ID;
        this.name = NAME;
        updateDescription();
        this.unique = true;
        this.img = ImageMaster.loadImage("chrono/images/blights/" + ID + ".png");
        this.outlineImg = ImageMaster.loadImage("chrono/images/blights/outline/" + ID + ".png");
        this.increment = 0;
        this.tips.clear();
        this.tips.add(new PowerTip(name, description));
    }

    @Override
    public void updateDescription() {
        this.description = this.DESCRIPTIONS[0] + getBossDescription(AbstractDungeon.bossKey);
        initializeTips();
    }

    @Override
    public void renderTip(SpriteBatch sb) {
        updateDescription();
        this.tips.clear();
        this.tips.add(new PowerTip(name, description));

        super.renderTip(sb);
    }

	public float strangeFlameVfxTimer = 0.3f;
	public ArrayList<AbstractGameEffect> myEffects = new ArrayList<>();

	public void renderInTopPanel(SpriteBatch sb) {
		if (Settings.hideRelics)
			return;

		sb.setColor(Color.WHITE);

		// Flame Effect
		strangeFlameVfxTimer -= Gdx.graphics.getDeltaTime();
		if (strangeFlameVfxTimer < 0.0F) {
			strangeFlameVfxTimer = MathUtils.random(0.2F, 0.4F);
			myEffects.add(new chronoMods.coop.StrangeFlameAnimationEffect(hb));
		} 

		// Update Particles
		Iterator<AbstractGameEffect> i;
		for (i = myEffects.iterator(); i.hasNext(); ) {
			AbstractGameEffect e = i.next();
			if (e.isDone) {
			  e.dispose();
			  i.remove();
			} 
		} 
		for (i = myEffects.iterator(); i.hasNext(); ) {
			AbstractGameEffect e = i.next();
			e.update();
		} 

		for (AbstractGameEffect e : myEffects)
		    e.render(sb);  

		super.render(sb);
	}

	// SLIMEBOSS, GUARDIAN, HEXAGHOST, CHAMP, COLLECTOR, AUTOMATON, TIMEEATER, AWAKENED, DONUDECA;
	private String getBossDescription(String key) {

		if (key.equals("The Guardian")) {
			addBoss(2);
			return this.DESCRIPTIONS[2];
		} else if (key.equals("Hexaghost")) {
			addBoss(3);
			return this.DESCRIPTIONS[3];
		} else if (key.equals("Slime Boss")) {
			addBoss(1);
			return this.DESCRIPTIONS[1];
		} else if (key.equals("Collector")) {
			addBoss(5);
			return this.DESCRIPTIONS[6];
		} else if (key.equals("Automaton")) {
			addBoss(6);
			return this.DESCRIPTIONS[4];
		} else if (key.equals("Champ")) {
			addBoss(4);
			return this.DESCRIPTIONS[5];
		} else if (key.equals("Awakened One")) {
			addBoss(8);	
			return this.DESCRIPTIONS[7];
		} else if (key.equals("Time Eater")) {
			addBoss(7);	
			return this.DESCRIPTIONS[8];
		} else if (key.equals("Donu and Deca")) {
			addBoss(9);	
			return this.DESCRIPTIONS[9];
		} else if (key.equals("The Heart")) {
			return this.DESCRIPTIONS[10];
		} 
		return "";
	}

	public void addBoss(int keyNum) {
		if (!bossList.contains(keyNum))
			bossList.add(keyNum);
	}
	

	// Enhanced Enemies
	////////////////////
    @SpirePatch(clz = AbstractRoom.class, method="applyEmeraldEliteBuff")
    public static class emeraldEnemyB {
        public static void Replace(AbstractRoom __instance) {
            if (TogetherManager.gameMode != TogetherManager.mode.Coop) { return; }

            if (AbstractDungeon.player.hasBlight("StrangeFlame")) {
			    if ((AbstractDungeon.getCurrMapNode()).hasEmeraldKey)
			      switch (AbstractDungeon.mapRng.random(0, 3)) {
			        case 0:
			          for (AbstractMonster m : __instance.monsters.monsters)
			            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, m, new StrengthPower(m, AbstractDungeon.actNum + 1), AbstractDungeon.actNum + 1)); 
			          break;
			        case 1:
			          for (AbstractMonster m : __instance.monsters.monsters)
			            AbstractDungeon.actionManager.addToBottom(new IncreaseMaxHpAction(m, 0.25F, true)); 
			          break;
			        case 2:
			          for (AbstractMonster m : __instance.monsters.monsters)
			            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, m, new MetallicizePower(m, AbstractDungeon.actNum * 2 + 2), AbstractDungeon.actNum * 2 + 2)); 
			          break;
			        case 3:
			          for (AbstractMonster m : __instance.monsters.monsters)
			            AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, m, new RegenerateMonsterPower(m, 1 + AbstractDungeon.actNum * 2), 1 + AbstractDungeon.actNum * 2)); 
			          break;
			      }  
            }
        }
    }

    // Enhanced Bosses
    ////////////////////
    public static int fightingBoss = -1;

    // Are you the first player here?
    public static boolean isFirst() {
    	for (RemotePlayer p : TogetherManager.players)
    		if (p.lastBoss == AbstractDungeon.actNum)
    			return false;

    	return true;
    }

	public static float flameTimer = 0.02f;
	public static float flameVfxTimer = 0.3f;
	public static ArrayList<AbstractGameEffect> fEffects = new ArrayList<>();

    // Big flame effects on the boss node
    @SpirePatch(clz = DungeonMap.class, method="renderBossIcon")
    public static class emeraldDungeonMap {
        public static void Prefix(DungeonMap __instance, SpriteBatch sb) {
            if (AbstractDungeon.player.hasBlight("StrangeFlame") && StrangeFlame.isFirst()) {
				AbstractDungeon.player.getBlight("StrangeFlame").updateDescription();

            	if (AbstractDungeon.screen != AbstractDungeon.CurrentScreen.MAP) { return; }
				
				// Create particles
				StrangeFlame.updateBossFrontParticle((float)ReflectionHacks.getPrivate(__instance, DungeonMap.class, "mapOffsetY"));
				StrangeFlame.updateBossBackFlame(__instance.bossHb);

				// Update Particles
				Iterator<AbstractGameEffect> i;
				for (i = StrangeFlame.fEffects.iterator(); i.hasNext(); ) {
					AbstractGameEffect e = i.next();
					if (e.isDone) {
					  e.dispose();
					  i.remove();
					} 
				} 
				for (i = StrangeFlame.fEffects.iterator(); i.hasNext(); ) {
					AbstractGameEffect e = i.next();
					e.update();
				} 

				// Render
				renderFlameVfx(sb);
			}
		}
	}

	private static void updateBossFrontParticle(float mY) {
		StrangeFlame.flameTimer -= Gdx.graphics.getDeltaTime();
		if (StrangeFlame.flameTimer < 0.0F && !Settings.DISABLE_EFFECTS) {
			StrangeFlame.flameTimer = 0.02F;

			AbstractDungeon.topLevelEffectsQueue.add(new NemesisFireParticle( 
				MathUtils.random(Settings.WIDTH / 2.0F - (256.0F * Settings.scale) / 2.0F, Settings.WIDTH / 2.0F + (256.0F * Settings.scale) / 2.0F), 
				MathUtils.random(DungeonMapScreen.offsetY + mY + 1416.0F * Settings.scale + 256.0F * Settings.scale, DungeonMapScreen.offsetY + mY + 1416.0F * Settings.scale + 512.0F * Settings.scale)));

			AbstractDungeon.topLevelEffectsQueue.add(new ExhaustEmberEffect( 
				MathUtils.random(Settings.WIDTH / 2.0F - (256.0F * Settings.scale) / 2.0F, Settings.WIDTH / 2.0F + (256.0F * Settings.scale) / 2.0F), 
				MathUtils.random(DungeonMapScreen.offsetY + mY + 1416.0F * Settings.scale + 256.0F * Settings.scale, DungeonMapScreen.offsetY + mY + 1416.0F * Settings.scale + 512.0F * Settings.scale)));
		} 
	}

	private static void updateBossBackFlame(Hitbox hb) {
		StrangeFlame.flameVfxTimer -= Gdx.graphics.getDeltaTime();
		if (StrangeFlame.flameVfxTimer < 0.0F) {
			StrangeFlame.flameVfxTimer = MathUtils.random(0.2F, 0.4F);
			StrangeFlame.fEffects.add(new chronoMods.coop.FlameAnimationEffect(hb));
		} 
	}

	private static void renderFlameVfx(SpriteBatch sb) {
		// if (Settings.isFinalActAvailable && this.hasEmeraldKey)
		  for (AbstractGameEffect e : StrangeFlame.fEffects)
		    e.render(sb);  
	}


    // Flame effects inside the fight room
    @SpirePatch(clz = MonsterRoomBoss.class, method="onPlayerEntry")
    public static class emeraldBossRoomEntry {
        public static void Postfix(MonsterRoomBoss __instance) {
            if (AbstractDungeon.player.hasBlight("StrangeFlame") && StrangeFlame.isFirst()) {
				NetworkHelper.sendData(NetworkHelper.dataType.LastBoss);
	    		AbstractDungeon.topLevelEffectsQueue.add(new ScreenOnFireEffect());
			}
		}
	}
				
    // Act 1
    @SpirePatch(clz = SlimeBoss.class, method=SpirePatch.CONSTRUCTOR)
    public static class emeraldSlimeBoss {
        public static void Postfix(SlimeBoss __instance) {
            if (AbstractDungeon.player.hasBlight("StrangeFlame") && StrangeFlame.isFirst()) {
	    		__instance.maxHealth = 240;
	    		__instance.currentHealth = 240;
			}
		}
	}

    @SpirePatch(clz = TheGuardian.class, method=SpirePatch.CONSTRUCTOR)
    public static class emeraldTheGuardian {
        public static void Postfix(TheGuardian __instance) {
            if (AbstractDungeon.player.hasBlight("StrangeFlame") && StrangeFlame.isFirst()) {
				ReflectionHacks.setPrivate(__instance, TheGuardian.class, "thornsDamage", 9);
 			}
		}
	}
    @SpirePatch(clz = TheGuardian.class, method="useCloseUp")
    public static class emeraldTheGuardianCharge {
        public static void Postfix(TheGuardian __instance) {
            if (AbstractDungeon.player.hasBlight("StrangeFlame") && fightingBoss == AbstractDungeon.actNum) {
            	AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(__instance, __instance, new BufferPower(__instance, 3)));
            	AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(__instance, __instance, new ArtifactPower(__instance, 3)));
 			}
		}
	}

    @SpirePatch(clz = Hexaghost.class, method=SpirePatch.CONSTRUCTOR)
    public static class emeraldHexaghost {
        public static void Postfix(Hexaghost __instance) {
            if (AbstractDungeon.player.hasBlight("StrangeFlame") && StrangeFlame.isFirst()) {
        		ReflectionHacks.setPrivate(__instance, Hexaghost.class, "searBurnCount", 3);
        		ReflectionHacks.setPrivate(__instance, Hexaghost.class, "burnUpgraded", true);
        	}
		}
	}

	// Act 2
    @SpirePatch(clz = BronzeOrb.class, method=SpirePatch.CONSTRUCTOR)
    public static class emeraldBronzeAutomaton {
        public static void Postfix(BronzeOrb __instance) {
            if (AbstractDungeon.player.hasBlight("StrangeFlame") && fightingBoss == AbstractDungeon.actNum) {
	    		__instance.maxHealth = 98;
	    		__instance.currentHealth = 98;
	    		__instance.damage.get(0).base = 12;
	    		__instance.damage.get(0).output = 12;
            }
		}
	}
    @SpirePatch(clz = BronzeOrb.class, method="getMove")
    public static class emeraldBronzeAutomatonVisual {
    	@SpireInsertPatch(rloc=105-94)
        public static void Insert(BronzeOrb __instance) {
            if (AbstractDungeon.player.hasBlight("StrangeFlame") && fightingBoss == AbstractDungeon.actNum) {
	    		__instance.setMove((byte)1, AbstractMonster.Intent.ATTACK, 12);
            }
		}
	}

    @SpirePatch(clz = Champ.class, method="usePreBattleAction")
    public static class emeraldChamp {
        public static void Prefix(Champ __instance) {
            if (AbstractDungeon.player.hasBlight("StrangeFlame") && StrangeFlame.isFirst()) {
            	AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(__instance, __instance, new BulkUpPower(__instance)));
            }
		}
	}

    @SpirePatch(clz = TorchHead.class, method=SpirePatch.CONSTRUCTOR)
    public static class emeraldCollector {
        public static void Postfix(TorchHead __instance) {
            if (AbstractDungeon.player.hasBlight("StrangeFlame") && fightingBoss == AbstractDungeon.actNum) {
            	for (AbstractMonster m : AbstractDungeon.getMonsters().monsters)
            		AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, m, new FlameBarrierPower(m, 4)));
            }
		}
	}

	// Act 3
    @SpirePatch(clz = AwakenedOne.class, method="changeState")
    public static class emeraldAwakenedOne {
        public static void Postfix(AwakenedOne __instance, String key) {
            if (AbstractDungeon.player.hasBlight("StrangeFlame") && fightingBoss == AbstractDungeon.actNum) {
            	if (key.equals("REBIRTH")) {
		            	AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, __instance, new HexPower(AbstractDungeon.player, 1)));
            	}
            }
		}
	}

 //    @SpirePatch(clz = Donu.class, method="usePreBattleAction")
 //    public static class emeraldDonuStart {
 //        public static void Postfix(Donu __instance) {
 //            if (AbstractDungeon.player.hasBlight("StrangeFlame") && fightingBoss == AbstractDungeon.actNum) {
	// 		    AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(__instance, __instance, new IntangiblePower(__instance, 1))); 
 //            }
	// 	}
	// }

    @SpirePatch(clz = Donu.class, method="takeTurn")
    public static class emeraldDonu {
        public static void Postfix(Donu __instance) {
            if (AbstractDungeon.player.hasBlight("StrangeFlame") && fightingBoss == AbstractDungeon.actNum) {
			    if (AbstractDungeon.actionManager.turn % 2 == 0)
			    	AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(__instance, __instance, new IntangiblePower(__instance, 1))); 
            }
		}
	}

    @SpirePatch(clz = Deca.class, method="takeTurn")
    public static class emeraldDeca {
        public static void Postfix(Deca __instance) {
            if (AbstractDungeon.player.hasBlight("StrangeFlame") && fightingBoss == AbstractDungeon.actNum) {
			    if (AbstractDungeon.actionManager.turn % 2 == 1)
			    	AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(__instance, __instance, new IntangiblePower(__instance, 1))); 
            }
		}
	}

    @SpirePatch(clz = Donu.class, method="damage")
    public static class emeraldDonuIntangidamage {
        public static void Prefix(Donu __instance, @ByRef DamageInfo[] info) {
		    if (info[0].output > 0 && __instance.hasPower("Intangible"))
		    	info[0].output = 1; 
   		}
	}

    @SpirePatch(clz = Deca.class, method="damage")
    public static class emeraldDecaIntangidamage {
        public static void Prefix(Deca __instance, @ByRef DamageInfo[] info) {
		    if (info[0].output > 0 && __instance.hasPower("Intangible"))
		    	info[0].output = 1; 
		}
	}

    @SpirePatch(clz = TimeEater.class, method="usePreBattleAction")
    public static class emeraldTimeEater {
        public static void Postfix(TimeEater __instance) {
            if (AbstractDungeon.player.hasBlight("StrangeFlame") && fightingBoss == AbstractDungeon.actNum) {
		    	AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new DrawPower(AbstractDungeon.player, -1))); 
            }
		}
	}

	// // Heart
 //    @SpirePatch(clz = CorruptHeart.class, method="takeTurn")
 //    public static class emeraldCorruptHeart {
 //    	@SpireInsertPatch(rloc=121-100)
 //        public static SpireReturn<Boolean> Insert(CorruptHeart __instance) {
 //            if (AbstractDungeon.player.hasBlight("StrangeFlame") && fightingBoss == AbstractDungeon.actNum) {
	// 	        AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDrawPileAction((AbstractCard)new Injury(), 1, true, false, false, Settings.WIDTH * 0.2F, Settings.HEIGHT / 2.0F));
	// 	        AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDrawPileAction((AbstractCard)new Shame(), 1, true, false, false, Settings.WIDTH * 0.35F, Settings.HEIGHT / 2.0F));
	// 	        AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDrawPileAction((AbstractCard)new Doubt(), 1, true, false, false, Settings.WIDTH * 0.5F, Settings.HEIGHT / 2.0F));
	// 	        AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDrawPileAction((AbstractCard)new Pain(), 1, true, false, false, Settings.WIDTH * 0.65F, Settings.HEIGHT / 2.0F));
	// 	        AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDrawPileAction((AbstractCard)new Regret(), 1, true, false, false, Settings.WIDTH * 0.8F, Settings.HEIGHT / 2.0F));
	// 	        AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDrawPileAction((AbstractCard)new Writhe(), 1, true, false, false, Settings.WIDTH * 0.8F, Settings.HEIGHT / 2.0F));
	// 	        AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDrawPileAction((AbstractCard)new Normality(), 1, true, false, false, Settings.WIDTH * 0.8F, Settings.HEIGHT / 2.0F));
	// 	        AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDrawPileAction((AbstractCard)new Decay(), 1, true, false, false, Settings.WIDTH * 0.8F, Settings.HEIGHT / 2.0F));
	// 	        AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDrawPileAction((AbstractCard)new Clumsy(), 1, true, false, false, Settings.WIDTH * 0.8F, Settings.HEIGHT / 2.0F));
	// 	        AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDrawPileAction((AbstractCard)new Parasite(), 1, true, false, false, Settings.WIDTH * 0.8F, Settings.HEIGHT / 2.0F));
 //            	AbstractDungeon.actionManager.addToBottom(new RollMoveAction(__instance));
 //            	return SpireReturn.Return(null);
 //            }
 //            return SpireReturn.Continue();
	// 	}
	// }
}