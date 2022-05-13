package chronoMods.coop;

import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.input.*;
import com.megacrit.cardcrawl.localization.*;
import com.megacrit.cardcrawl.relics.*;
import com.megacrit.cardcrawl.ui.*;
import com.megacrit.cardcrawl.ui.buttons.*;
import com.megacrit.cardcrawl.vfx.*;
import com.megacrit.cardcrawl.vfx.cardManip.*;
import com.megacrit.cardcrawl.core.*;
import com.megacrit.cardcrawl.integrations.steam.*;
import com.megacrit.cardcrawl.helpers.*;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.map.*;
import com.megacrit.cardcrawl.potions.*;
import com.megacrit.cardcrawl.relics.*;
import com.megacrit.cardcrawl.rewards.*;
import com.codedisaster.steamworks.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;

import java.util.*;
import java.nio.*;

import chronoMods.*;
import chronoMods.network.steam.*;
import chronoMods.network.*;
import chronoMods.ui.deathScreen.*;
import chronoMods.ui.hud.*;
import chronoMods.ui.lobby.*;
import chronoMods.ui.mainMenu.*;
import chronoMods.utilities.*;
import chronoMods.coop.drawable.*;
import chronoMods.coop.infusions.*;

import com.evacipated.cardcrawl.modthespire.lib.*;
import basemod.interfaces.*;

public class CoopCourierScreen {

	public int COMMON_CARD_COST = 		20;
	public int UNCOMMON_CARD_COST = 	20;
	public int RARE_CARD_COST = 		20;
	public int CURSE_CARD_COST = 		1;
	public int BASIC_CARD_COST = 		1;

	public int COMMON_RELIC_COST = 		20;
	public int UNCOMMON_RELIC_COST = 	20;
	public int RARE_RELIC_COST = 		20;

	public int COMMON_POTION_COST = 	20;
	public int UNCOMMON_POTION_COST = 	20;
	public int RARE_POTION_COST = 		20;


	public static final Logger logger = LogManager.getLogger("CoopCourierScreen");
	

	public static final TutorialStrings tutorialStrings = CardCrawlGame.languagePack.getTutorialString("Shop Tip");
	public static final String[] MSG = tutorialStrings.TEXT;
	public static final String[] LABEL = tutorialStrings.LABEL;

	public static final CharacterStrings characterStrings = CardCrawlGame.languagePack.getCharacterString("Shop Screen");
	public static final String[] NAMES = characterStrings.NAMES;
	public static final String[] TEXT = characterStrings.TEXT;
	
    public static final String[] TALK = CardCrawlGame.languagePack.getUIString("Courier").TEXT;

	public boolean isActive = true;
	
	public static Texture rugImg = null;
	public static Texture removeServiceImg = null;
	public static Texture handImg = null;
	
	public float rugY = Settings.HEIGHT / 2.0F + 540.0F * Settings.yScale;
	public static final float RUG_SPEED = 5.0F;
	public static final float DRAW_START_X = Settings.WIDTH * 0.22F + AbstractCard.IMG_WIDTH_S / 2;
	public static final float DRAW_PAD_X = Settings.WIDTH * 0.133F;
	
	public static final float TOP_ROW_Y = 760.0F * Settings.yScale;
	public static final float BOTTOM_ROW_Y = 500.0F * Settings.yScale;
	
	public float speechTimer = 0.0F;
	public static final float MIN_IDLE_MSG_TIME = 40.0F; 
	public static final float MAX_IDLE_MSG_TIME = 60.0F;
	public static final float SPEECH_DURATION = 4.0F;
	public static final float SPEECH_TEXT_R_X = 164.0F * Settings.scale;
	public static final float SPEECH_TEXT_L_X = -166.0F * Settings.scale;
	public static final float SPEECH_TEXT_Y = 126.0F * Settings.scale;
	
	public ShopSpeechBubble speechBubble = null;
	public SpeechTextEffect dialogTextEffect = null;
	public static final String WELCOME_MSG = NAMES[0];
	public ArrayList<String> idleMessages = new ArrayList<>();
	public boolean saidWelcome = false;
	public boolean somethingHovered = false;
				
	public FloatyEffect f_effect = new FloatyEffect(20.0F, 0.1F);
	public float handTimer = 1.0F;
	public float handX = Settings.WIDTH / 2.0F;
	public float handY = Settings.HEIGHT;
	public float handTargetX = 0.0F;
	public float handTargetY = Settings.HEIGHT;
	public static final float HAND_SPEED = 6.0F;
	public static float HAND_W;
	public static float HAND_H;
	
	public float notHoveredTimer = 0.0F;
	
	public static final float GOLD_IMG_WIDTH = ImageMaster.UI_GOLD.getWidth() * Settings.scale;	
	
	public static final float GOLD_IMG_OFFSET_X = -50.0F * Settings.scale;
	public static final float GOLD_IMG_OFFSET_Y = -215.0F * Settings.scale;
	public static final float GOLD_IMG_BOOSTER_OFFSET_Y = -65.0F * Settings.scale;
	
	public static final float PRICE_TEXT_OFFSET_X = 16.0F * Settings.scale;
	public static final float PRICE_TEXT_OFFSET_Y = -180.0F * Settings.scale;
	public static final float PRICE_TEXT_BOOSTER_OFFSET_Y = -30.0F * Settings.scale;
		
	
	public float MAILBOX_X = 1606f * Settings.scale;

	public Hitbox rewardButtonBox = new Hitbox(MAILBOX_X,0f,470.0f * Settings.scale, 300.0f * Settings.scale);
	public static Texture rewardButtonImg = null;
	public float rewardscale = 1.0f;
	public float rewardx = 5.0f;
	public float rewardy = -44.0f * Settings.scale;

	public RemotePlayer recipient;

	public AbstractCard[] cards = new AbstractCard[3];
	public Hitbox[] boosterHBs = new Hitbox[3];
	public float[] boosterScale = new float[3];
	public boolean[] boosterActive = new boolean[3];
	public Texture[] boosterTex = new Texture[3];
	public static final float HB_W = 300.0F * Settings.scale;
	public static final float HB_H = 420.0F * Settings.scale;


	public static final float CARD_PRICE_JITTER = 0.1F;
	public AbstractCard transferCard;
	public int transferRarity;

	public ArrayList<String> bannedCardsCom = new ArrayList<>();
	public ArrayList<String> bannedCardsUnc = new ArrayList<>();
	public ArrayList<String> bannedCardsRar = new ArrayList<>();
	public ArrayList<String> bannedCardsCur = new ArrayList<>();

	public boolean triedBefore = false;

	public ArrayList<CoopCourierRelic> relics = new ArrayList<>();
	public static final float RELIC_PRICE_JITTER = 0.05F;
	public AbstractRelic transferRelic;
	public ArrayList<String> bannedRelics = new ArrayList<>();
	
	public ArrayList<CoopCourierPotion> potions = new ArrayList<>();
	public static final float POTION_PRICE_JITTER = 0.05F;
	public AbstractPotion transferPotion;

	public ArrayList<CourierInfusionBox> infusions = new ArrayList<>();

	public ArrayList<CoopCourierRecipient> players = new ArrayList<>();
	public float players_x = MAILBOX_X;
	public float players_y = -1000.0F;
	public float players_margin = 64f * Settings.yScale;

	public chronoMods.coop.drawable.Button rerollButton = new chronoMods.coop.drawable.Button(Settings.WIDTH*0.525f, Settings.HEIGHT*0.22f, "", ImageMaster.loadImage("chrono/images/rerollButton.png"));


    public static class Enum
    {
        @SpireEnum
        public static AbstractDungeon.CurrentScreen COURIER;
    }

    @SpirePatch(clz=AbstractDungeon.class, method="update")
    public static class Update
    {
        public static void Postfix(AbstractDungeon __instance)
        {
            if (__instance.screen == CoopCourierScreen.Enum.COURIER) {
                TogetherManager.courierScreen.update();
            }
        }
    }

    @SpirePatch(clz=AbstractDungeon.class, method="render")
    public static class Render
    {
    	@SpireInsertPatch(rloc=2773-2658,localvars={})
        public static void Insert(AbstractDungeon __instance, SpriteBatch sb)
        {
            if (__instance.screen == CoopCourierScreen.Enum.COURIER) {
                TogetherManager.courierScreen.render(sb);
            }
        }
    }

    @SpirePatch(clz=AbstractDungeon.class, method="openPreviousScreen")
    public static class Reopen
    {
        public static void Postfix(AbstractDungeon.CurrentScreen s)
        {
            if (s == CoopCourierScreen.Enum.COURIER) {
                TogetherManager.courierScreen.open();
            }
        }
    }

    @SpirePatch(clz=AbstractDungeon.class, method="closeCurrentScreen")
    public static class closeCurrentScreen
    {
    	@SpireInsertPatch(rloc=2961-2858,localvars={})
        public static void Insert()
        {
            if (AbstractDungeon.screen == CoopCourierScreen.Enum.COURIER) {
		        CardCrawlGame.sound.play("SHOP_CLOSE");

			    if (AbstractDungeon.previousScreen == null)
			      if (AbstractDungeon.player.isDead) {
			        AbstractDungeon.previousScreen = AbstractDungeon.CurrentScreen.DEATH;
			      } else {
			        AbstractDungeon.isScreenUp = false;
			        AbstractDungeon.overlayMenu.hideBlackScreen();
			      }  

  		        AbstractDungeon.overlayMenu.cancelButton.hide();
            }
        }
    }

	public void init() {
		this.idleMessages.clear();
	    if (AbstractDungeon.id.equals("TheEnding")) {
	      this.idleMessages.add(TALK[0]);
	      this.idleMessages.add(TALK[1]);
	      this.idleMessages.add(TALK[2]);
	    } else {
	      this.idleMessages.add(TALK[3]);
	      this.idleMessages.add(TALK[4]);
	      this.idleMessages.add(TALK[5]);
	      this.idleMessages.add(TALK[6]);
	      this.idleMessages.add(TALK[7]);
	      this.idleMessages.add(TALK[8]);
	      this.idleMessages.add(TALK[9]);
	    } 
		    
		if (rugImg == null) {
			rugImg = ImageMaster.loadImage("chrono/images/CourierScreenBack.png");
			handImg = ImageMaster.loadImage("chrono/images/merchantHand.png");
			rewardButtonImg = ImageMaster.loadImage("chrono/images/CourierDrawer.png");
		} 

		// Create the booster pack hitboxes
		int tmp = (int)(Settings.WIDTH - DRAW_START_X * 2.0F - AbstractCard.IMG_WIDTH_S * 5.0F) / 4;
		float padX = (int)(tmp + AbstractCard.IMG_WIDTH_S) + 10.0F * Settings.scale;

		// 110/150...?
		boosterHBs[0] = new Hitbox(DRAW_START_X + DRAW_PAD_X * -1,this.rugY + TOP_ROW_Y,HB_W, HB_H);
		boosterHBs[1] = new Hitbox(DRAW_START_X + DRAW_PAD_X * 0,this.rugY + TOP_ROW_Y,HB_W, HB_H);
		boosterHBs[2] = new Hitbox(DRAW_START_X + DRAW_PAD_X * 1,this.rugY + TOP_ROW_Y,HB_W, HB_H);

		boosterScale[0] = 0.7f;
		boosterScale[1] = 0.7f;
		boosterScale[2] = 0.7f;

		switch (AbstractDungeon.player.chosenClass) {
			case IRONCLAD:
				boosterTex[0] = ImageMaster.loadImage("chrono/images/boosters/ironcladCommon.png");
				boosterTex[1] = ImageMaster.loadImage("chrono/images/boosters/ironcladUncommon.png");
				boosterTex[2] = ImageMaster.loadImage("chrono/images/boosters/ironcladRare.png");
				break;
			case THE_SILENT:
				boosterTex[0] = ImageMaster.loadImage("chrono/images/boosters/silentCommon.png");
				boosterTex[1] = ImageMaster.loadImage("chrono/images/boosters/silentUncommon.png");
				boosterTex[2] = ImageMaster.loadImage("chrono/images/boosters/silentRare.png");
				break;
			case DEFECT:
				boosterTex[0] = ImageMaster.loadImage("chrono/images/boosters/defectCommon.png");
				boosterTex[1] = ImageMaster.loadImage("chrono/images/boosters/defectUncommon.png");
				boosterTex[2] = ImageMaster.loadImage("chrono/images/boosters/defectRare.png");
				break;
			case WATCHER:
				boosterTex[0] = ImageMaster.loadImage("chrono/images/boosters/watcherCommon.png");
				boosterTex[1] = ImageMaster.loadImage("chrono/images/boosters/watcherUncommon.png");
				boosterTex[2] = ImageMaster.loadImage("chrono/images/boosters/watcherRare.png");
				break;
			default:
				boosterTex[0] = ImageMaster.loadImage("chrono/images/boosters/modCommon.png");
				boosterTex[1] = ImageMaster.loadImage("chrono/images/boosters/modUncommon.png");
				boosterTex[2] = ImageMaster.loadImage("chrono/images/boosters/modRare.png");
				break;				
		}

		HAND_W = handImg.getWidth() * Settings.scale;
		HAND_H = handImg.getHeight() * Settings.scale;

		rerollButton.isDisabled = false;
		rollInventory();

		this.players.clear();
		int i = 0;
		for (RemotePlayer p : TogetherManager.players) {
			if (!p.isUser(TogetherManager.currentUser)) {
				players.add(new CoopCourierRecipient(p, players_x, players_y + i * players_margin, this));
			}
		}
	}
	
	public void rollInventory() {
		this.relics.clear();
		this.potions.clear();
		this.infusions.clear();

		triedBefore = false;
		initCards();
		initRelics();
		initPotions();
		initInfusions();

		boosterActive[0] = true;
		boosterActive[1] = true;
		boosterActive[2] = true;

		if (AbstractDungeon.ascensionLevel >= 16)
			applyDiscount(1.1F); 
		if (AbstractDungeon.player.hasRelic("The Courier"))
			applyDiscount(0.8F); 
		if (AbstractDungeon.player.hasRelic("Membership Card"))
			applyDiscount(0.5F);
		if (AbstractDungeon.player.hasRelic("Ectoplasm"))
			applyDiscount(0.0F);
	}

	public void initCards() {
		this.cards[0] = null;
		this.cards[1] = null;
		this.cards[2] = null;

		// Select Cards for the array
		CardGroup cg = AbstractDungeon.player.masterDeck.getPurgeableCards();
		
		// Remove any banned cards from the running
		for (String banned : this.bannedCardsCom)
			cg.removeCard(banned);

		for (String banned : this.bannedCardsUnc)
			cg.removeCard(banned);

		for (String banned : this.bannedCardsRar)
			cg.removeCard(banned);

		for (String banned : this.bannedCardsCur)
			cg.removeCard(banned);

		AbstractCard curse = cg.getRandomCard(true, AbstractCard.CardRarity.CURSE);
		AbstractCard speci = cg.getRandomCard(true, AbstractCard.CardRarity.SPECIAL);
		AbstractCard commo = cg.getRandomCard(true, AbstractCard.CardRarity.COMMON);
		AbstractCard uncom = cg.getRandomCard(true, AbstractCard.CardRarity.UNCOMMON);
		AbstractCard rare  = cg.getRandomCard(true, AbstractCard.CardRarity.RARE);


		// New card logic is as follows. 
		// There are three slots, common, uncommon, and rare.
		// It will choose a purgeable card that has prefentially not been seen in the courier before of the given rarity.
		// If there is no available card, it will give the option to sell a card draft of your colour to your friend, and it will reset the seen list for that rarity.
		// Curses and Status Cards will override any given slot at 30% chance, also with a ban list.

		// Add three cards, one of each rarity.
		this.cards[0] = commo;
		this.cards[1] = uncom;
		this.cards[2] = rare;

		// Replace with curses or status cards
		int randomCurseSlot = MathUtils.random(0, 3);
		if ((curse != null || speci != null) && randomCurseSlot < 3) {
			if (curse != null)
				this.cards[randomCurseSlot] = curse;
			else
				this.cards[randomCurseSlot] = speci;
			this.bannedCardsCur.add(this.cards[randomCurseSlot].cardID);
		} else {
			this.bannedCardsCom.clear();
		}

		// If the card slot is empty, reset the ban list. If not, place and price the card and add it to the ban list.
		if (this.cards[0] == null)
			this.bannedCardsCom.clear();
		else{
			this.bannedCardsCom.add(this.cards[0].cardID);
			priceCard(this.cards[0]);
			placeCard(this.cards[0],0);
		}

		if (this.cards[1] == null)
			this.bannedCardsUnc.clear();
		else{
			this.bannedCardsUnc.add(this.cards[1].cardID);
			priceCard(this.cards[1]);
			placeCard(this.cards[1],1);
		}

		if (this.cards[2] == null)
			this.bannedCardsRar.clear();
		else{
			this.bannedCardsRar.add(this.cards[2].cardID);
			priceCard(this.cards[2]);
			placeCard(this.cards[2],2);
		}
	}

	public void priceCard(AbstractCard c) {
		float tmpPrice = AbstractCard.getPrice(c.rarity) / 4;
		tmpPrice = tmpPrice > 1000 ? 100 : tmpPrice;
		c.price = c.rarity == AbstractCard.CardRarity.CURSE ? 100 : (int)tmpPrice;
		c.current_x = (Settings.WIDTH / 2);
	}

	public void placeCard(AbstractCard c, int i) {
		int tmp = (int)(Settings.WIDTH - DRAW_START_X * 2.0F - AbstractCard.IMG_WIDTH_S * 5.0F) / 4;
		float padX = (int)(tmp + AbstractCard.IMG_WIDTH_S) + 10.0F * Settings.scale;
		c.updateHoverLogic();
		c.targetDrawScale = 0.75F;
		c.current_x = DRAW_START_X + DRAW_PAD_X * i;
		c.target_x = DRAW_START_X + DRAW_PAD_X * i;
		c.target_y = 9999.0F * Settings.scale;
		c.current_y = 9999.0F * Settings.scale;
	}
					
	public void applyDiscount(float multiplier) {
		for (CoopCourierRelic r : this.relics)
			r.setPrice(MathUtils.round(r.price * multiplier)); 
		for (CoopCourierPotion p : this.potions)
			p.price = MathUtils.round(p.price * multiplier); 
		for (AbstractCard c : this.cards) {
			if (c != null)
				c.price = MathUtils.round(c.price * multiplier); 
		}
	}
	
	public void initRelics() {
		this.relics.clear();
		this.relics = new ArrayList<>();

		LinkedHashSet<AbstractRelic> shufflePicker = new LinkedHashSet<AbstractRelic>();
		ArrayList<AbstractRelic> shuffler = new ArrayList(AbstractDungeon.player.relics);
		Collections.shuffle(shuffler);
		shufflePicker.addAll(shuffler);

		try {	
			// Cauldron and Orrery are broken dumdums
			for (AbstractRelic r : shufflePicker) {
				if (r.relicId.equals("Orrery") || r.relicId.equals("Cauldron") || r.relicId.equals("NeowInfusion") ) {
                        TogetherManager.log("Has one: " + r.name);
			    	shufflePicker.remove(r);
			    	break;
				}
			}

			for (AbstractRelic r : shufflePicker) {
				if (r.relicId.equals("Orrery") || r.relicId.equals("Cauldron") || r.relicId.equals("NeowInfusion") ) {
			    	shufflePicker.remove(r);
			    	break;
				}
			}

			CoopCourierRelic c;
			// Grab three relics, one common, one uncommon, one rare, and if not enough available fill the slots
			c = chooseRelic(shufflePicker, AbstractRelic.RelicTier.COMMON);
			if (c != null)
				this.relics.add(c);

			c = chooseRelic(shufflePicker, AbstractRelic.RelicTier.UNCOMMON);
			if (c != null)
				this.relics.add(c);

			c = chooseRelic(shufflePicker, AbstractRelic.RelicTier.RARE);
			if (c != null)
				this.relics.add(c);

			// Dimensioneel relics
			if (AbstractDungeon.player.hasBlight("Dimensioneel")) {
				ArrayList<AbstractRelic> randomizer = new ArrayList();

				randomizer.addAll(RelicLibrary.commonList);
				randomizer.addAll(RelicLibrary.uncommonList);
				randomizer.addAll(RelicLibrary.rareList);
				randomizer.addAll(RelicLibrary.shopList);
				randomizer.addAll(RelicLibrary.redList);
				randomizer.addAll(RelicLibrary.greenList);
				randomizer.addAll(RelicLibrary.blueList);
				randomizer.addAll(RelicLibrary.whiteList);
				Collections.shuffle(randomizer);

                for (Iterator<AbstractRelic> i = randomizer.iterator(); i.hasNext(); ) {
                    AbstractRelic r = i.next();
					if (r.relicId.equals("Orrery") || r.relicId.equals("Cauldron") || r.relicId.equals("NeowInfusion") ) {
                        TogetherManager.log("Removing " + r.name);
                        i.remove(); 
                    }
                } 
				for (AbstractRelic r : randomizer) {
					if (r.relicId.equals("Orrery") || r.relicId.equals("Cauldron") || r.relicId.equals("NeowInfusion") ) {
                        TogetherManager.log("Still here: " + r.name);
				    	break;
					}
				}

				c = new CoopCourierRelic(randomizer.get(0).makeCopy(), 3, this);
				this.relics.add(c);

				c = new CoopCourierRelic(randomizer.get(1).makeCopy(), 4, this);
				this.relics.add(c);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public CoopCourierRelic chooseRelic(LinkedHashSet<AbstractRelic> shufflePicker, AbstractRelic.RelicTier tier) {
		CoopCourierRelic c;
		int slotMod = 1;
		if (AbstractDungeon.player.hasBlight("Dimensioneel"))
			slotMod = 0;

		// Pick a relic of the selected tier if unbanned
		for (AbstractRelic r : shufflePicker) {
			if (r.tier == tier && !bannedRelics.contains(r.relicId)) {
		    	bannedRelics.add(r.relicId);
		    	c = new CoopCourierRelic(r.makeCopy(), this.relics.size() + slotMod, this);
		    	shufflePicker.remove(r);
		    	return c;
			}
		}

		// If not possible pick any relic of the correct rarity
		for (AbstractRelic r : shufflePicker) {
			if (r.tier == tier) {
		    	bannedRelics.add(r.relicId);
		    	c = new CoopCourierRelic(r.makeCopy(), this.relics.size() + slotMod, this);
		    	shufflePicker.remove(r);
		    	return c;
			}
		}

		// If not possible pick any relic
		for (AbstractRelic r : shufflePicker) {
			if (r.tier == AbstractRelic.RelicTier.COMMON || r.tier == AbstractRelic.RelicTier.UNCOMMON || r.tier == AbstractRelic.RelicTier.RARE || r.tier == AbstractRelic.RelicTier.SHOP  || r.tier == AbstractRelic.RelicTier.SPECIAL) {
		    	// this.relics.add(new CoopCourierRelic(r.makeCopy(), this.relics.size(), this));
		    	bannedRelics.add(r.relicId);
		    	c = new CoopCourierRelic(r.makeCopy(), this.relics.size() + slotMod, this);
		    	shufflePicker.remove(r);
		    	return c;
			}
		}

		// Nope, nothing left
		return null;
	}
	
	public void initPotions() {
		if (AbstractDungeon.player.hasBlight("VaporFunnel")) { return; }
		
		this.potions.clear();

		for (AbstractPotion p : AbstractDungeon.player.potions) {
			if (!(p instanceof PotionSlot)) {
				AbstractPotion newPot = p.makeCopy();
				newPot.slot = p.slot;

				this.potions.add(new CoopCourierPotion(p.makeCopy(), this.potions.size(), p.slot, this));
				if (this.potions.size() == 3) { return; }
			}
		}
	}

	public void initInfusions() {
		if (!AbstractDungeon.player.hasBlight("TransfusionBag")) { return; }
		
		for (int i = 0; i < 2; i++) {
			infusions.add(new CourierInfusionBox(i, InfusionHelper.getInfusionSet(AbstractDungeon.player.chosenClass)));
		}
	}

	public static String getCantBuyMsg() {
		ArrayList<String> list = new ArrayList<>();
		list.add(TALK[10]);
		list.add(NAMES[2]);
		list.add(NAMES[3]);
		list.add(NAMES[4]);
		list.add(NAMES[5]);
		list.add(NAMES[6]);
		return list.get(MathUtils.random(list.size() - 1));
	}

	public static String getNoRecipientMsg() {
		ArrayList<String> list = new ArrayList<>();
		list.add(TALK[11]);
		list.add(TALK[12]);
		list.add(TALK[13]);
		list.add(TALK[14]);
		list.add(TALK[15]);
		list.add(TALK[16]);
		return list.get(MathUtils.random(list.size() - 1));
	}
	
	public static String getBuyMsg() {
		ArrayList<String> list = new ArrayList<>();
		list.add(TALK[17]);
		list.add(TALK[18]);
		list.add(TALK[19]);
		list.add(TALK[20]);
		list.add(TALK[21]);
		return list.get(MathUtils.random(list.size() - 1));
	}


	public void open() {
		CardCrawlGame.sound.play("SHOP_OPEN");
		if (this.cards[0] != null)
			placeCard(this.cards[0],0);

		if (this.cards[1] != null)
			placeCard(this.cards[1],1);

		if (this.cards[2] != null)
			placeCard(this.cards[2],2);

		AbstractDungeon.isScreenUp = true;
		AbstractDungeon.screen = CoopCourierScreen.Enum.COURIER;
		AbstractDungeon.overlayMenu.proceedButton.hide();
		AbstractDungeon.overlayMenu.cancelButton.show(NAMES[12]);
		for (CoopCourierRelic r : this.relics)
			r.hide(); 
		for (CoopCourierPotion p : this.potions)
			p.hide(); 
		this.rugY = Settings.HEIGHT;
		this.handX = Settings.WIDTH / 2.0F;
		this.handY = Settings.HEIGHT;
		this.handTargetX = this.handX;
		this.handTargetY = this.handY;
		this.handTimer = 1.0F;
		this.speechTimer = 1.5F;
		this.speechBubble = null;
		this.dialogTextEffect = null;
		AbstractDungeon.overlayMenu.showBlackScreen();
	}
		
	public void update() {
		if (this.handTimer != 0.0F) {
			this.handTimer -= Gdx.graphics.getDeltaTime();
			if (this.handTimer < 0.0F)
				this.handTimer = 0.0F; 
		} 
		this.f_effect.update();
		this.somethingHovered = false;

		// updateControllerInput();
		updateRelics();
		updatePotions();
		updateRug();
		updateSpeech();
		updateCards();
		updateHand();
		updateRewardButton();

		for (CourierInfusionBox cbox : infusions)
			cbox.update();

		// Reroll Button
		if (AbstractDungeon.player.hasBlight("PneumaticPost")) {
			rerollButton.move(Settings.WIDTH*0.525f, Settings.HEIGHT*0.22f + this.rugY);
		    rerollButton.update();
		    if (this.rerollButton.hb.clicked || CInputActionSet.proceed.isJustPressed()) {
		        this.rerollButton.hb.clicked = false;
		        if (!rerollButton.isDisabled)
			        rollInventory();
		        rerollButton.isDisabled = true;
		    }
	        if (rerollButton.hb.hovered) {
	            TipHelper.renderGenericTip(this.rerollButton.hb.cX - 320.0F * Settings.scale / 2f, this.rerollButton.hb.cY, TALK[23], TALK[24]); }
	    }

		players_y = this.rugY + BOTTOM_ROW_Y;
		int i = 0;
		for (CoopCourierRecipient p : players) {
			p.y = players_y - i * players_margin - ((Settings.scale*88f*0.75f) / 2);
			p.update();
			i++;
		}

		if (!this.somethingHovered) {
			this.notHoveredTimer += Gdx.graphics.getDeltaTime();
			if (this.notHoveredTimer > 1.0F)
				this.handTargetY = Settings.HEIGHT; 
		} else {
			this.notHoveredTimer = 0.0F;
		} 
	}
	
	public void deselect() {
		for (CoopCourierRecipient p : players) {
			p.selected = false;
		}
	}

	public RemotePlayer getRecipient() {
		for (CoopCourierRecipient p : players) {
			if (p.selected)
				return p.player;
		}
		return null;
	}

	public void purchaseCard(AbstractCard hoveredCard, int i) {
		if (AbstractDungeon.player.gold >= hoveredCard.price) {
		    if (getRecipient() != null) {
				AbstractDungeon.player.loseGold(hoveredCard.price);
				CardCrawlGame.sound.play("SHOP_PURCHASE", 0.1F);

				this.transferCard = hoveredCard;
	        	NetworkHelper.sendData(NetworkHelper.dataType.TransferCard);

		        AbstractDungeon.topLevelEffects.add(new PurgeCardEffect(hoveredCard, Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
		        AbstractDungeon.player.masterDeck.removeCard(hoveredCard);

				this.cards[i] = null;
				hoveredCard = null;
				InputHelper.justClickedLeft = false;
				this.notHoveredTimer = 1.0F;
				this.speechTimer = MathUtils.random(40.0F, 60.0F);
				playBuySfx();
				createSpeech(getBuyMsg());
		    } else {
		        this.speechTimer = MathUtils.random(40.0F, 60.0F);
		        playCantBuySfx();
		        createSpeech(CoopCourierScreen.getNoRecipientMsg());
		    }
		} else {
			this.speechTimer = MathUtils.random(40.0F, 60.0F);
			playCantBuySfx();
			createSpeech(getCantBuyMsg());
		} 
	}

	public void updateRewardButton() {
		// if (TogetherManager.currentUser.packages.size() > 0)
		rewardButtonBox.update();

		rewardscale = 1.0f;
		if (rewardButtonBox.hovered) {
			rewardscale = 0.8f;
			if (InputHelper.justClickedLeft)
                rewardButtonBox.clickStarted = true; 
		}	
	
		if (rewardButtonBox.clicked) {
			AbstractDungeon.getCurrRoom().rewards.clear();
			AbstractDungeon.getCurrRoom().rewards = new ArrayList(TogetherManager.getCurrentUser().packages);

		    // Open the Reward Screen
		    AbstractDungeon.combatRewardScreen.open(TALK[22]);
		    AbstractDungeon.combatRewardScreen.rewards.remove(AbstractDungeon.combatRewardScreen.rewards.size()-1);
		    (AbstractDungeon.getCurrRoom()).rewardPopOutTimer = 0.0F;

		    TogetherManager.getCurrentUser().packages.clear();
			rewardButtonBox.clicked = false;
		}

		rewardButtonBox.move(MAILBOX_X, this.rugY + TOP_ROW_Y + rewardy);
    }

	public void updateCards() {
		for (int i = 0; i < this.cards.length; i++) {
			AbstractCard c = this.cards[i];

			if (c != null) {
				c.update();
				c.updateHoverLogic();
				c.current_y = this.rugY + TOP_ROW_Y;
				c.target_y = c.current_y;

				if (c.hb.hovered) {
					this.somethingHovered = true;
					moveHand(c.current_x - AbstractCard.IMG_WIDTH / 2.0F, c.current_y);

					if (InputHelper.justClickedLeft)
						c.hb.clickStarted = true; 
					if (InputHelper.justClickedRight || CInputActionSet.proceed.isJustPressed()) {
						InputHelper.justClickedRight = false;
						CardCrawlGame.cardPopup.open(c);
					} 
					if (c.hb.clicked || CInputActionSet.select.isJustPressed()) {
						c.hb.clicked = false;
						purchaseCard(c, i);
					} 
				}
			} else if (boosterActive[i]) {
				boosterHBs[i].update(boosterHBs[i].cX, this.rugY + TOP_ROW_Y - boosterHBs[i].height / 2.0F);
				
				if (boosterHBs[i].hovered) {
					this.somethingHovered = true;
					moveHand(boosterHBs[i].cX - AbstractCard.IMG_WIDTH / 2.0F, boosterHBs[i].cY);
				}
				
				if (boosterHBs[i].hovered == true && InputHelper.justClickedLeft) {
					purchaseBooster(i);
				}
			}
		} 
	}
	
	public void purchaseBooster(int i) {
		int price = 15 * (i+1);
		if (i == 2)
			price = 75;

		if (AbstractDungeon.ascensionLevel >= 16)
			price *= 1.1F; 
		if (AbstractDungeon.player.hasRelic("The Courier"))
			price *= 0.8F; 
		if (AbstractDungeon.player.hasRelic("Membership Card"))
			price *= 0.5F;
		if (AbstractDungeon.player.hasRelic("Ectoplasm"))
			price *= 0.5F;

		if (AbstractDungeon.player.gold >= price) {
		    if (getRecipient() != null) {
				AbstractDungeon.player.loseGold(price);
				CardCrawlGame.sound.play("SHOP_PURCHASE", 0.1F);

				// Update this to send the booster pack
				this.transferRarity = i;
	        	NetworkHelper.sendData(NetworkHelper.dataType.TransferBooster);

	        	boosterActive[i] = false;

				this.speechTimer = MathUtils.random(40.0F, 60.0F);
				playBuySfx();
				createSpeech(getBuyMsg());
		    } else {
		        this.speechTimer = MathUtils.random(40.0F, 60.0F);
		        playCantBuySfx();
		        createSpeech(CoopCourierScreen.getNoRecipientMsg());
		    }
		} else {
			this.speechTimer = MathUtils.random(40.0F, 60.0F);
			playCantBuySfx();
			createSpeech(getCantBuyMsg());
		} 
	}

	public void setPrice(AbstractCard card) {
		float tmpPrice = AbstractCard.getPrice(card.rarity) * AbstractDungeon.merchantRng.random(0.9F, 1.1F);
		if (card.color == AbstractCard.CardColor.COLORLESS)
			tmpPrice *= 1.2F; 
		if (AbstractDungeon.player.hasRelic("The Courier"))
			tmpPrice *= 0.8F; 
		if (AbstractDungeon.player.hasRelic("Membership Card"))
			tmpPrice *= 0.5F; 
		card.price = (int)tmpPrice;
	}
	
	public void moveHand(float x, float y) {
		this.handTargetX = x - 50.0F * Settings.xScale;
		this.handTargetY = y + 90.0F * Settings.yScale;
	}
	
	public void updateRelics() {
		for (Iterator<CoopCourierRelic> i = this.relics.iterator(); i.hasNext(); ) {
			CoopCourierRelic r = i.next();
			if (Settings.isFourByThree) {
				r.update(this.rugY + 50.0F * Settings.yScale);
			} else {
				r.update(this.rugY);
			} 
			if (r.isPurchased) {
				i.remove();
				break;
			} 
		} 
	}
	
	public void updatePotions() {
		CoopCourierPotion removeMe = null;
		for (Iterator<CoopCourierPotion> i = this.potions.iterator(); i.hasNext(); ) {
			CoopCourierPotion p = i.next();
			if (Settings.isFourByThree) {
				p.update(this.rugY + 50.0F * Settings.scale);
			} else {
				p.update(this.rugY);
			} 
			if (p.isPurchased) {
				removeMe = p;
				break;
			} 
		}
		this.potions.remove(removeMe);
	}
	
	public void createSpeech(String msg) {
		boolean isRight = MathUtils.randomBoolean();
		float x = MathUtils.random(660.0F, 1260.0F) * Settings.scale;
		float y = Settings.HEIGHT - 380.0F * Settings.scale;
		this.speechBubble = new ShopSpeechBubble(x, y, 4.0F, msg, isRight);
		float offset_x = 0.0F;
		if (isRight) {
			offset_x = SPEECH_TEXT_R_X;
		} else {
			offset_x = SPEECH_TEXT_L_X;
		} 
		this.dialogTextEffect = new SpeechTextEffect(x + offset_x, y + SPEECH_TEXT_Y, 4.0F, msg, DialogWord.AppearEffect.BUMP_IN);
	}
	
	public void updateSpeech() {
		if (this.speechBubble != null) {
			this.speechBubble.update();
			if (this.speechBubble.hb.hovered && this.speechBubble.duration > 0.3F) {
				this.speechBubble.duration = 0.3F;
				this.dialogTextEffect.duration = 0.3F;
			} 
			if (this.speechBubble.isDone)
				this.speechBubble = null; 
		} 
		if (this.dialogTextEffect != null) {
			this.dialogTextEffect.update();
			if (this.dialogTextEffect.isDone)
				this.dialogTextEffect = null; 
		} 
		this.speechTimer -= Gdx.graphics.getDeltaTime();
		if (this.speechBubble == null && this.dialogTextEffect == null && this.speechTimer <= 0.0F) {
			this.speechTimer = MathUtils.random(40.0F, 60.0F);
			if (!this.saidWelcome) {
				createSpeech(WELCOME_MSG);
				this.saidWelcome = true;
				welcomeSfx();
			} else {
				playMiscSfx();
				createSpeech(getIdleMsg());
			} 
		} 
	}
	
	public void welcomeSfx() {
		int roll = MathUtils.random(2);
		if (roll == 0) {
			CardCrawlGame.sound.play("VO_MERCHANT_3A");
		} else if (roll == 1) {
			CardCrawlGame.sound.play("VO_MERCHANT_3B");
		} else {
			CardCrawlGame.sound.play("VO_MERCHANT_3C");
		} 
	}
	
	public void playMiscSfx() {
		int roll = MathUtils.random(5);
		if (roll == 0) {
			CardCrawlGame.sound.play("VO_MERCHANT_MA");
		} else if (roll == 1) {
			CardCrawlGame.sound.play("VO_MERCHANT_MB");
		} else if (roll == 2) {
			CardCrawlGame.sound.play("VO_MERCHANT_MC");
		} else if (roll == 3) {
			CardCrawlGame.sound.play("VO_MERCHANT_3A");
		} else if (roll == 4) {
			CardCrawlGame.sound.play("VO_MERCHANT_3B");
		} else {
			CardCrawlGame.sound.play("VO_MERCHANT_3C");
		} 
	}
	
	public void playBuySfx() {
		int roll = MathUtils.random(2);
		if (roll == 0) {
			CardCrawlGame.sound.play("VO_MERCHANT_KA");
		} else if (roll == 1) {
			CardCrawlGame.sound.play("VO_MERCHANT_KB");
		} else {
			CardCrawlGame.sound.play("VO_MERCHANT_KC");
		} 
	}
	
	public void playCantBuySfx() {
		int roll = MathUtils.random(2);
		if (roll == 0) {
			CardCrawlGame.sound.play("VO_MERCHANT_2A");
		} else if (roll == 1) {
			CardCrawlGame.sound.play("VO_MERCHANT_2B");
		} else {
			CardCrawlGame.sound.play("VO_MERCHANT_2C");
		} 
	}
	
	public String getIdleMsg() {
		return this.idleMessages.get(MathUtils.random(this.idleMessages.size() - 1));
	}
	
	public void updateRug() {
		if (this.rugY != 0.0F) {
			this.rugY = MathUtils.lerp(this.rugY, Settings.HEIGHT / 2.0F - 540.0F * Settings.yScale, Gdx.graphics
					
					.getDeltaTime() * 5.0F);
			if (Math.abs(this.rugY - 0.0F) < 0.5F)
				this.rugY = 0.0F; 
		} 
	}
	
	public void updateHand() {
		if (this.handTimer == 0.0F) {
			if (this.handX != this.handTargetX)
				this.handX = MathUtils.lerp(this.handX, this.handTargetX, Gdx.graphics.getDeltaTime() * 6.0F); 
			if (this.handY != this.handTargetY)
				if (this.handY > this.handTargetY) {
					this.handY = MathUtils.lerp(this.handY, this.handTargetY, Gdx.graphics.getDeltaTime() * 6.0F);
				} else {
					this.handY = MathUtils.lerp(this.handY, this.handTargetY, Gdx.graphics.getDeltaTime() * 6.0F / 4.0F);
				}  
		} 
	}
	
	public void render(SpriteBatch sb) {
		sb.setColor(Color.WHITE);

		for (CourierInfusionBox cbox : infusions)
			cbox.render(sb);

		sb.draw(rugImg, 0.0F, this.rugY, Settings.WIDTH, Settings.HEIGHT);
		renderCardsAndPrices(sb);
		renderRelics(sb);
		renderPotions(sb);
		renderCourierBag(sb);
		sb.draw(handImg, this.handX + this.f_effect.x, this.handY + this.f_effect.y, HAND_W, HAND_H);
		if (this.speechBubble != null)
			this.speechBubble.render(sb); 
		if (this.dialogTextEffect != null)
			this.dialogTextEffect.render(sb); 
		for (CoopCourierRecipient p : players) {
			p.render(sb);
		}
		if (AbstractDungeon.player.hasBlight("PneumaticPost"))
			rerollButton.render(sb);
	}
	
	public void renderCourierBag(SpriteBatch sb) {
		sb.setColor(Color.WHITE.cpy());
		sb.draw(rewardButtonImg, rewardButtonBox.x, rewardButtonBox.y, 473.0f * Settings.scale, 301.0f * Settings.scale * rewardscale);
		FontHelper.renderFontCentered(sb, FontHelper.largeCardFont, 
					Integer.toString(TogetherManager.getCurrentUser().packages.size()),
					rewardButtonBox.x + 473.0f * Settings.scale / 2, rewardButtonBox.y + 301.0f * Settings.scale * rewardscale / 2, Color.WHITE.cpy(), 1.0f);
		rewardButtonBox.render(sb);
	}

	public void renderRelics(SpriteBatch sb) {
		for (CoopCourierRelic r : this.relics)
			r.render(sb); 
	}
	
	public void renderPotions(SpriteBatch sb) {
		for (CoopCourierPotion p : this.potions)
			p.render(sb); 
	}
	
	public void renderCardsAndPrices(SpriteBatch sb) {
		for (int i = 0; i < this.cards.length; i++) {
			AbstractCard c = this.cards[i];

			if (c != null) {
				c.render(sb);
				sb.setColor(Color.WHITE);
				sb.draw(ImageMaster.UI_GOLD, c.current_x + GOLD_IMG_OFFSET_X, c.current_y + GOLD_IMG_OFFSET_Y - (c.drawScale - 0.75F) * 200.0F * Settings.scale, GOLD_IMG_WIDTH, GOLD_IMG_WIDTH);
				Color color = Color.WHITE.cpy();
				if (c.price > AbstractDungeon.player.gold) {
					color = Color.SALMON.cpy();
				}
				FontHelper.renderFontLeftTopAligned(sb, FontHelper.tipHeaderFont, 
					Integer.toString(c.price), c.current_x + PRICE_TEXT_OFFSET_X, c.current_y + PRICE_TEXT_OFFSET_Y - (c.drawScale - 0.75F) * 200.0F * Settings.scale, color);

				c.renderCardTip(sb); 
			} else if (boosterActive[i]) {
			    boosterHBs[i].resize(HB_W * boosterScale[i], HB_H * boosterScale[i]);
			    if (boosterHBs[i].hovered) {
			    	boosterScale[i] = MathHelper.cardScaleLerpSnap(boosterScale[i], 0.85f); 
			    	boosterScale[i] = MathHelper.cardScaleLerpSnap(boosterScale[i], 0.85f); 
			    }
			    else {
			    	boosterScale[i] = MathHelper.cardScaleLerpSnap(boosterScale[i], 0.7f); 
			    	boosterScale[i] = MathHelper.cardScaleLerpSnap(boosterScale[i], 0.7f); 
			    }

				int price = 15 * (i+1);
				if (i == 2)
					price = 75;

				if (AbstractDungeon.ascensionLevel >= 16)
					price *= 1.1F; 
				if (AbstractDungeon.player.hasRelic("The Courier"))
					price *= 0.8F; 
				if (AbstractDungeon.player.hasRelic("Membership Card"))
					price *= 0.5F;
				if (AbstractDungeon.player.hasRelic("Ectoplasm"))
					price *= 0.5F;

				sb.setColor(Color.WHITE);
				// Draw the booster pack
  				sb.draw(boosterTex[i], boosterHBs[i].x - boosterTex[i].getWidth() / 2.0F + boosterHBs[i].width / 2.0f, boosterHBs[i].y - boosterTex[i].getHeight() / 2.0F + boosterHBs[i].height / 2.0f, boosterTex[i].getWidth() / 2.0F, boosterTex[i].getHeight() / 2.0F, boosterTex[i].getWidth(), boosterTex[i].getHeight(), Settings.scale * boosterScale[i], Settings.scale * boosterScale[i], 0.0F, 0, 0, boosterTex[i].getHeight(), boosterTex[i].getWidth(), false, false);

				// Draw the gold text
				sb.draw(ImageMaster.UI_GOLD, boosterHBs[i].cX + (HB_W/2f)*0.7f + GOLD_IMG_OFFSET_X, boosterHBs[i].y + GOLD_IMG_BOOSTER_OFFSET_Y - (boosterScale[i] - 0.7F) * 200.0F * Settings.scale, GOLD_IMG_WIDTH, GOLD_IMG_WIDTH);
				Color color = Color.WHITE.cpy();
				if (price > AbstractDungeon.player.gold) {
					color = Color.SALMON.cpy();
				}
				FontHelper.renderFontLeftTopAligned(sb, FontHelper.tipHeaderFont, 
					Integer.toString(price), boosterHBs[i].cX + (HB_W/2f)*0.7f + PRICE_TEXT_OFFSET_X, boosterHBs[i].y + PRICE_TEXT_BOOSTER_OFFSET_Y - (boosterScale[i] - 0.7F) * 200.0F * Settings.scale, color);
			
				boosterHBs[i].render(sb);
			}
		} 
	}
}
