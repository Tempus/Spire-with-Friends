package chronoMods.coop.relics;

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
    }

	private String getBossDescription(String key) {
		if (key.equals("The Guardian")) {
			return this.DESCRIPTIONS[2];
		} else if (key.equals("Hexaghost")) {
			return this.DESCRIPTIONS[3];
		} else if (key.equals("Slime Boss")) {
			return this.DESCRIPTIONS[1];
		} else if (key.equals("Collector")) {
			return this.DESCRIPTIONS[6];
		} else if (key.equals("Automaton")) {
			return this.DESCRIPTIONS[4];
		} else if (key.equals("Champ")) {
			return this.DESCRIPTIONS[5];
		} else if (key.equals("Awakened One")) {
			return this.DESCRIPTIONS[7];
		} else if (key.equals("Time Eater")) {
			return this.DESCRIPTIONS[8];
		} else if (key.equals("Donu and Deca")) {
			return this.DESCRIPTIONS[9];
		} else if (key.equals("The Heart")) {
			return this.DESCRIPTIONS[10];
		} 
		return "";
	}



    @SpirePatch(clz = AbstractRoom.class, method="applyEmeraldEliteBuff")
    public static class emeraldEnemyB {
        public static void Replace(AbstractRoom __instance) {
            if (TogetherManager.gameMode != TogetherManager.mode.Coop) { return; }

            if (AbstractDungeon.player.hasBlight("StrangeFlame")) {
			    if ((AbstractDungeon.getCurrMapNode()).hasEmeraldKey)
			      switch (AbstractDungeon.mapRng.random(0, 3)) {
			        case 0:
			          for (AbstractMonster m : __instance.monsters.monsters)
			            AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ApplyPowerAction((AbstractCreature)m, (AbstractCreature)m, (AbstractPower)new StrengthPower((AbstractCreature)m, AbstractDungeon.actNum + 1), AbstractDungeon.actNum + 1)); 
			          break;
			        case 1:
			          for (AbstractMonster m : __instance.monsters.monsters)
			            AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new IncreaseMaxHpAction(m, 0.25F, true)); 
			          break;
			        case 2:
			          for (AbstractMonster m : __instance.monsters.monsters)
			            AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ApplyPowerAction((AbstractCreature)m, (AbstractCreature)m, (AbstractPower)new MetallicizePower((AbstractCreature)m, AbstractDungeon.actNum * 2 + 2), AbstractDungeon.actNum * 2 + 2)); 
			          break;
			        case 3:
			          for (AbstractMonster m : __instance.monsters.monsters)
			            AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ApplyPowerAction((AbstractCreature)m, (AbstractCreature)m, (AbstractPower)new RegenerateMonsterPower(m, 1 + AbstractDungeon.actNum * 2), 1 + AbstractDungeon.actNum * 2)); 
			          break;
			      }  
            }
        }
    }

    // Enhanced Bosses
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
	    		__instance.maxHealth = 200;
	    		__instance.currentHealth = 200;
			}
		}
	}

    @SpirePatch(clz = TheGuardian.class, method=SpirePatch.CONSTRUCTOR)
    public static class emeraldTheGuardian {
        public static void Postfix(TheGuardian __instance) {
            if (AbstractDungeon.player.hasBlight("StrangeFlame") && StrangeFlame.isFirst()) {
				ReflectionHacks.setPrivate(__instance, TheGuardian.class, "thornsDamage", 10);
 				ReflectionHacks.setPrivate(__instance, TheGuardian.class, "VENT_DEBUFF", 4);
 			}
		}
	}

    @SpirePatch(clz = Hexaghost.class, method=SpirePatch.CONSTRUCTOR)
    public static class emeraldHexaghost {
        public static void Postfix(Hexaghost __instance) {
            if (AbstractDungeon.player.hasBlight("StrangeFlame") && StrangeFlame.isFirst()) {
        		ReflectionHacks.setPrivate(__instance, Hexaghost.class, "searBurnCount", 3);
        	}
		}
	}

	// Act 2
    @SpirePatch(clz = BronzeOrb.class, method=SpirePatch.CONSTRUCTOR)
    public static class emeraldBronzeAutomaton {
        public static void Postfix(BronzeOrb __instance) {
            if (AbstractDungeon.player.hasBlight("StrangeFlame") && StrangeFlame.isFirst()) {
	    		__instance.maxHealth = 98;
	    		__instance.currentHealth = 98;
	    		__instance.damage.get(0).base = 12;
	    		__instance.damage.get(0).output = 12;
            }
		}
	}

    @SpirePatch(clz = Champ.class, method=SpirePatch.CONSTRUCTOR)
    public static class emeraldChamp {
        public static void Postfix(Champ __instance) {
            if (AbstractDungeon.player.hasBlight("StrangeFlame") && StrangeFlame.isFirst()) {
        		ReflectionHacks.setPrivate(__instance, Champ.class, "strAmt", (int)ReflectionHacks.getPrivate(__instance, Champ.class, "strAmt") + 3);
        		ReflectionHacks.setPrivate(__instance, Champ.class, "forgeAmt", (int)ReflectionHacks.getPrivate(__instance, Champ.class, "forgeAmt") + 3);
            }
		}
	}

    @SpirePatch(clz = TorchHead.class, method="takeTurn")
    public static class emeraldCollector {
        public static void Postfix(TorchHead __instance) {
            if (AbstractDungeon.player.hasBlight("StrangeFlame") && fightingBoss == AbstractDungeon.actNum) {
            	AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ApplyPowerAction((AbstractCreature)__instance, (AbstractCreature)__instance, (AbstractPower)new AngryPower((AbstractCreature)__instance, 5)));
            }
		}
	}

	// Act 3
    @SpirePatch(clz = AwakenedOne.class, method="changeState")
    public static class emeraldAwakenedOne {
        public static void Postfix(AwakenedOne __instance, String key) {
            if (AbstractDungeon.player.hasBlight("StrangeFlame") && fightingBoss == AbstractDungeon.actNum) {
            	if (key.equals("REBIRTH")) {
		            	AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ApplyPowerAction((AbstractCreature)AbstractDungeon.player, (AbstractCreature)__instance, (AbstractPower)new HexPower((AbstractCreature)AbstractDungeon.player, 1)));
            	}
            }
		}
	}

    @SpirePatch(clz = Donu.class, method="usePreBattleAction")
    public static class emeraldDonuStart {
        public static void Postfix(Donu __instance) {
            if (AbstractDungeon.player.hasBlight("StrangeFlame") && fightingBoss == AbstractDungeon.actNum) {
			    AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ApplyPowerAction((AbstractCreature)__instance, (AbstractCreature)__instance, (AbstractPower)new IntangiblePower((AbstractCreature)__instance, 1))); 
            }
		}
	}

    @SpirePatch(clz = Donu.class, method="takeTurn")
    public static class emeraldDonu {
        public static void Postfix(Donu __instance) {
            if (AbstractDungeon.player.hasBlight("StrangeFlame") && fightingBoss == AbstractDungeon.actNum) {
			    if (!__instance.hasPower("Intangible"))
			    	AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ApplyPowerAction((AbstractCreature)__instance, (AbstractCreature)__instance, (AbstractPower)new IntangiblePower((AbstractCreature)__instance, 1))); 
            }
		}
	}

    @SpirePatch(clz = Deca.class, method="takeTurn")
    public static class emeraldDeca {
        public static void Postfix(Deca __instance) {
            if (AbstractDungeon.player.hasBlight("StrangeFlame") && fightingBoss == AbstractDungeon.actNum) {
			    if (!__instance.hasPower("Intangible"))
			    	AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new ApplyPowerAction((AbstractCreature)__instance, (AbstractCreature)__instance, (AbstractPower)new IntangiblePower((AbstractCreature)__instance, 1))); 
            }
		}
	}

    @SpirePatch(clz = TimeEater.class, method=SpirePatch.CONSTRUCTOR)
    public static class emeraldTimeEater {
        public static void Postfix(TimeEater __instance) {
            if (AbstractDungeon.player.hasBlight("StrangeFlame") && fightingBoss == AbstractDungeon.actNum) {
		        AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new MakeTempCardInDrawPileAction((AbstractCard)new Burn(), 1, true, false, false, Settings.WIDTH * 0.35F, Settings.HEIGHT / 2.0F));
		        AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new MakeTempCardInDrawPileAction((AbstractCard)new Burn(), 1, true, false, false, Settings.WIDTH * 0.5F, Settings.HEIGHT / 2.0F));
		        AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new MakeTempCardInDrawPileAction((AbstractCard)new Burn(), 1, true, false, false, Settings.WIDTH * 0.65F, Settings.HEIGHT / 2.0F));	
            }
		}
	}

	// Heart
    @SpirePatch(clz = CorruptHeart.class, method="takeTurn")
    public static class emeraldCorruptHeart {
    	@SpireInsertPatch(rloc=121-100)
        public static SpireReturn<Boolean> Insert(CorruptHeart __instance) {
            if (AbstractDungeon.player.hasBlight("StrangeFlame") && fightingBoss == AbstractDungeon.actNum) {
		        AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new MakeTempCardInDrawPileAction((AbstractCard)new Injury(), 1, true, false, false, Settings.WIDTH * 0.2F, Settings.HEIGHT / 2.0F));
		        AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new MakeTempCardInDrawPileAction((AbstractCard)new Shame(), 1, true, false, false, Settings.WIDTH * 0.35F, Settings.HEIGHT / 2.0F));
		        AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new MakeTempCardInDrawPileAction((AbstractCard)new Doubt(), 1, true, false, false, Settings.WIDTH * 0.5F, Settings.HEIGHT / 2.0F));
		        AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new MakeTempCardInDrawPileAction((AbstractCard)new Pain(), 1, true, false, false, Settings.WIDTH * 0.65F, Settings.HEIGHT / 2.0F));
		        AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new MakeTempCardInDrawPileAction((AbstractCard)new Regret(), 1, true, false, false, Settings.WIDTH * 0.8F, Settings.HEIGHT / 2.0F));
            	AbstractDungeon.actionManager.addToBottom((AbstractGameAction)new RollMoveAction(__instance));
            	return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
		}
	}
}