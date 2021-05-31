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


	private static final Logger logger = LogManager.getLogger("CoopCourierScreen");
	

	private static final TutorialStrings tutorialStrings = CardCrawlGame.languagePack.getTutorialString("Shop Tip");
	public static final String[] MSG = tutorialStrings.TEXT;
	public static final String[] LABEL = tutorialStrings.LABEL;

	private static final CharacterStrings characterStrings = CardCrawlGame.languagePack.getCharacterString("Shop Screen");
	public static final String[] NAMES = characterStrings.NAMES;
	public static final String[] TEXT = characterStrings.TEXT;
	
    public static final String[] TALK = CardCrawlGame.languagePack.getUIString("Courier").TEXT;

	public boolean isActive = true;
	
	private static Texture rugImg = null;
	private static Texture removeServiceImg = null;
	private static Texture handImg = null;
	
	private float rugY = Settings.HEIGHT / 2.0F + 540.0F * Settings.yScale;
	private static final float RUG_SPEED = 5.0F;
	private static final float DRAW_START_X = Settings.WIDTH * 0.22F + AbstractCard.IMG_WIDTH_S / 2 * Settings.scale;
	private static final float DRAW_PAD_X = Settings.WIDTH * 0.133F;
	
	private static final float TOP_ROW_Y = 760.0F * Settings.yScale;
	private static final float BOTTOM_ROW_Y = 500.0F * Settings.yScale;
	
	private float speechTimer = 0.0F;
	private static final float MIN_IDLE_MSG_TIME = 40.0F; 
	private static final float MAX_IDLE_MSG_TIME = 60.0F;
	private static final float SPEECH_DURATION = 4.0F;
	private static final float SPEECH_TEXT_R_X = 164.0F * Settings.scale;
	private static final float SPEECH_TEXT_L_X = -166.0F * Settings.scale;
	private static final float SPEECH_TEXT_Y = 126.0F * Settings.scale;
	
	private ShopSpeechBubble speechBubble = null;
	private SpeechTextEffect dialogTextEffect = null;
	private static final String WELCOME_MSG = NAMES[0];
	private ArrayList<String> idleMessages = new ArrayList<>();
	private boolean saidWelcome = false;
	private boolean somethingHovered = false;
				
	private FloatyEffect f_effect = new FloatyEffect(20.0F, 0.1F);
	private float handTimer = 1.0F;
	private float handX = Settings.WIDTH / 2.0F;
	private float handY = Settings.HEIGHT;
	private float handTargetX = 0.0F;
	private float handTargetY = Settings.HEIGHT;
	private static final float HAND_SPEED = 6.0F;
	private static float HAND_W;
	private static float HAND_H;
	
	private float notHoveredTimer = 0.0F;
	
	private static final float GOLD_IMG_WIDTH = ImageMaster.UI_GOLD.getWidth() * Settings.scale;	
	
	private static final float GOLD_IMG_OFFSET_X = -50.0F * Settings.scale;
	private static final float GOLD_IMG_OFFSET_Y = -215.0F * Settings.scale;
	
	private static final float PRICE_TEXT_OFFSET_X = 16.0F * Settings.scale;
	private static final float PRICE_TEXT_OFFSET_Y = -180.0F * Settings.scale;
		
	
	public float MAILBOX_X = 1606f * Settings.scale;

	public Hitbox rewardButtonBox = new Hitbox(MAILBOX_X,0f,470.0f * Settings.scale, 300.0f * Settings.scale);
	private static Texture rewardButtonImg = null;
	private float rewardscale = 1.0f;
	private float rewardx = 5.0f;
	private float rewardy = -44.0f * Settings.scale;

	public RemotePlayer recipient;

	public ArrayList<AbstractCard> cards = new ArrayList<>();
	private static final float CARD_PRICE_JITTER = 0.1F;
	public AbstractCard transferCard;
	public ArrayList<String> bannedCards = new ArrayList<>();
	public boolean triedBefore = false;

	public ArrayList<CoopCourierRelic> relics = new ArrayList<>();
	private static final float RELIC_PRICE_JITTER = 0.05F;
	public AbstractRelic transferRelic;
	public ArrayList<String> bannedRelics = new ArrayList<>();
	
	public ArrayList<CoopCourierPotion> potions = new ArrayList<>();
	private static final float POTION_PRICE_JITTER = 0.05F;
	public AbstractPotion transferPotion;


	public ArrayList<CoopCourierRecipient> players = new ArrayList<>();
	public float players_x = MAILBOX_X;
	public float players_y = -1000.0F;
	public float players_margin = 75f * Settings.yScale;

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
	
	private void rollInventory() {
		this.cards.clear();
		this.relics.clear();
		this.potions.clear();

		triedBefore = false;
		initCards();
		initRelics();
		initPotions();

		if (AbstractDungeon.ascensionLevel >= 16)
			applyDiscount(1.1F); 
		if (AbstractDungeon.player.hasRelic("The Courier"))
			applyDiscount(0.8F); 
		if (AbstractDungeon.player.hasRelic("Membership Card"))
			applyDiscount(0.5F);
		if (AbstractDungeon.player.hasBlight("PneumaticPost"))
			applyDiscount(0.5F);
		if (AbstractDungeon.player.hasRelic("Ectoplasm"))
			applyDiscount(0.0F);
	}

	private void initCards() {
		this.cards.clear();


		// Select Cards for the array
		CardGroup cg = AbstractDungeon.player.masterDeck.getPurgeableCards();
		
		// Clear banned cards if we just don't have that many cards left - we're assuming strikes and defends are still around
		if (cg.size() - this.bannedCards.size() < 7)
			this.bannedCards.clear();

		// Remove any banned cards from the running
		for (String banned : this.bannedCards) {
			cg.removeCard(banned);
		}

		AbstractCard curse = cg.getRandomCard(true, AbstractCard.CardRarity.CURSE);
		AbstractCard commo = cg.getRandomCard(true, AbstractCard.CardRarity.COMMON);
		AbstractCard start = cg.getRandomCard(true, AbstractCard.CardRarity.BASIC);
		AbstractCard uncom = cg.getRandomCard(true, AbstractCard.CardRarity.UNCOMMON);
		AbstractCard rare  = cg.getRandomCard(true, AbstractCard.CardRarity.RARE);

		// Add three cards when possible and no more, one rare, one uncommon, one curse, maybe one starter, maybe one common
		if (rare != null) 
			this.cards.add(0, rare);

		if (uncom != null) {
			this.cards.add(0, uncom);
			cg.removeCard(uncom);			
			uncom = cg.getRandomCard(true, AbstractCard.CardRarity.UNCOMMON);
		}

		if (curse != null) {
			curse.price = 10;
			this.cards.add(0, curse);
		}
		
		if (commo != null && this.cards.size() < 3) {
			this.cards.add(0, commo);
			cg.removeCard(commo);			
			commo = cg.getRandomCard(true, AbstractCard.CardRarity.COMMON);
		}

		if (uncom != null && this.cards.size() < 3) {
			this.cards.add(0, uncom);
			cg.removeCard(uncom);			
			uncom = cg.getRandomCard(true, AbstractCard.CardRarity.UNCOMMON);
		}

		if (commo != null && this.cards.size() < 3) {
			this.cards.add(0, commo);
			cg.removeCard(commo);			
		}

		if (uncom != null && this.cards.size() < 3) {
			this.cards.add(0, uncom);
			cg.removeCard(uncom);			
		}

		if (start != null && this.cards.size() < 3) {
			this.cards.add(0, start);
			cg.removeCard(start);			
			start = cg.getRandomCard(true, AbstractCard.CardRarity.BASIC);
		}

		if (start != null && this.cards.size() < 3) {
			this.cards.add(0, start);
			cg.removeCard(start);			
			start = cg.getRandomCard(true, AbstractCard.CardRarity.BASIC);
		}

		if (start != null && this.cards.size() < 3) {
			this.cards.add(0, start);
			cg.removeCard(start);			
			start = cg.getRandomCard(true, AbstractCard.CardRarity.BASIC);
		}

		for (AbstractCard banme : this.cards) {
			if (banme.type != AbstractCard.CardType.STATUS)
				bannedCards.add(banme.cardID);
		}

		if (this.cards.size() < 3 && !triedBefore) {
			bannedCards.clear();
			triedBefore = true;
			initCards();
			return;
		}

		// Place and Price the cards
		int tmp = (int)(Settings.WIDTH - DRAW_START_X * 2.0F - AbstractCard.IMG_WIDTH_S * 5.0F) / 4;
		float padX = (int)(tmp + AbstractCard.IMG_WIDTH_S);
		int i;
		for (i = 0; i < this.cards.size(); i++) {
			float tmpPrice = AbstractCard.getPrice(((AbstractCard)this.cards.get(i)).rarity) / 4;
			tmpPrice = tmpPrice > 1000 ? 75 : tmpPrice;
			AbstractCard c = this.cards.get(i);
			c.price = c.rarity == AbstractCard.CardRarity.CURSE ? 75 : (int)tmpPrice;
			c.current_x = (Settings.WIDTH / 2);
			c.target_x = DRAW_START_X + DRAW_PAD_X * i;
		} 

		setStartingCardPositions();
	}

	private void setStartingCardPositions() {
		int tmp = (int)(Settings.WIDTH - DRAW_START_X * 2.0F - AbstractCard.IMG_WIDTH_S * 5.0F) / 4;
		float padX = (int)(tmp + AbstractCard.IMG_WIDTH_S) + 10.0F * Settings.scale;
		int i;
		for (i = 0; i < this.cards.size(); i++) {
			((AbstractCard)this.cards.get(i)).updateHoverLogic();
			((AbstractCard)this.cards.get(i)).targetDrawScale = 0.75F;
			((AbstractCard)this.cards.get(i)).current_x = DRAW_START_X + DRAW_PAD_X * i;
			((AbstractCard)this.cards.get(i)).target_x = DRAW_START_X + DRAW_PAD_X * i;
			((AbstractCard)this.cards.get(i)).target_y = 9999.0F * Settings.scale;
			((AbstractCard)this.cards.get(i)).current_y = 9999.0F * Settings.scale;
		} 
	}
					
	public void applyDiscount(float multiplier) {
		for (CoopCourierRelic r : this.relics)
			r.price = MathUtils.round(r.price * multiplier); 
		for (CoopCourierPotion p : this.potions)
			p.price = MathUtils.round(p.price * multiplier); 
		for (AbstractCard c : this.cards)
			c.price = MathUtils.round(c.price * multiplier); 
	}
	
	private void initRelics() {
		this.relics.clear();
		this.relics = new ArrayList<>();

		LinkedHashSet<AbstractRelic> shufflePicker = new LinkedHashSet<AbstractRelic>();
		ArrayList<AbstractRelic> shuffler = new ArrayList(AbstractDungeon.player.relics);
		Collections.shuffle(shuffler);
		shufflePicker.addAll(shuffler);

		try {	
			// Cauldron and Orrery are broken dumdums
			for (AbstractRelic r : shufflePicker) {
				if (r.relicId == "Orrery" || r.relicId == "Cauldron") {
			    	shufflePicker.remove(r);
			    	break;
				}
			}

			for (AbstractRelic r : shufflePicker) {
				if (r.relicId == "Orrery" || r.relicId == "Cauldron") {
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

			// if (AbstractDungeon.player.hasBlight("PneumaticPost")) {
			// 	c = chooseRelic(shufflePicker, AbstractRelic.RelicTier.SHOP);
			// 	if (c != null)
			// 		this.relics.add(c);

			// 	c = chooseRelic(shufflePicker, AbstractRelic.RelicTier.SPECIAL);
			// 	if (c != null)
			// 		this.relics.add(c);
			// }

			//if (shufflePicker == null || shufflePicker.size() == 0) { return; }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public CoopCourierRelic chooseRelic(LinkedHashSet<AbstractRelic> shufflePicker, AbstractRelic.RelicTier tier) {
		CoopCourierRelic c;
		int slotMod = 1;
		if (AbstractDungeon.player.hasBlight("PneumaticPost"))
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
	
	private void initPotions() {
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
		setStartingCardPositions();
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

		AbstractCard hoveredCard = null;
		for (AbstractCard c : this.cards) {
			if (c.hb.hovered) {
				hoveredCard = c;
				this.somethingHovered = true;
				moveHand(c.current_x - AbstractCard.IMG_WIDTH / 2.0F, c.current_y);
				break;
			} 
		} 
		if (!this.somethingHovered) {
			this.notHoveredTimer += Gdx.graphics.getDeltaTime();
			if (this.notHoveredTimer > 1.0F)
				this.handTargetY = Settings.HEIGHT; 
		} else {
			this.notHoveredTimer = 0.0F;
		} 
		if (hoveredCard != null && InputHelper.justClickedLeft)
			hoveredCard.hb.clickStarted = true; 
		if (hoveredCard != null && (InputHelper.justClickedRight || CInputActionSet.proceed.isJustPressed())) {
			InputHelper.justClickedRight = false;
			CardCrawlGame.cardPopup.open(hoveredCard);
		} 
		if (hoveredCard != null && (hoveredCard.hb.clicked || CInputActionSet.select.isJustPressed())) {
			hoveredCard.hb.clicked = false;
			purchaseCard(hoveredCard);
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

	private void purchaseCard(AbstractCard hoveredCard) {
		if (AbstractDungeon.player.gold >= hoveredCard.price) {
		    if (getRecipient() != null) {
				AbstractDungeon.player.loseGold(hoveredCard.price);
				CardCrawlGame.sound.play("SHOP_PURCHASE", 0.1F);

				this.cards.remove(hoveredCard);

				this.transferCard = hoveredCard;
	        	NetworkHelper.sendData(NetworkHelper.dataType.TransferCard);

		        AbstractDungeon.topLevelEffects.add(new PurgeCardEffect(hoveredCard, Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
		        AbstractDungeon.player.masterDeck.removeCard(hoveredCard);

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

	private void updateRewardButton() {
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

	private void updateCards() {
		int i;
		for (i = 0; i < this.cards.size(); i++) {
			((AbstractCard)this.cards.get(i)).update();
			((AbstractCard)this.cards.get(i)).updateHoverLogic();
			((AbstractCard)this.cards.get(i)).current_y = this.rugY + TOP_ROW_Y;
			((AbstractCard)this.cards.get(i)).target_y = ((AbstractCard)this.cards.get(i)).current_y;
		} 
	}
	
	private void setPrice(AbstractCard card) {
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
	
	private void updateRelics() {
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
	
	private void updatePotions() {
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
	
	private void updateSpeech() {
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
	
	private void welcomeSfx() {
		int roll = MathUtils.random(2);
		if (roll == 0) {
			CardCrawlGame.sound.play("VO_MERCHANT_3A");
		} else if (roll == 1) {
			CardCrawlGame.sound.play("VO_MERCHANT_3B");
		} else {
			CardCrawlGame.sound.play("VO_MERCHANT_3C");
		} 
	}
	
	private void playMiscSfx() {
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
	
	private String getIdleMsg() {
		return this.idleMessages.get(MathUtils.random(this.idleMessages.size() - 1));
	}
	
	private void updateRug() {
		if (this.rugY != 0.0F) {
			this.rugY = MathUtils.lerp(this.rugY, Settings.HEIGHT / 2.0F - 540.0F * Settings.yScale, Gdx.graphics
					
					.getDeltaTime() * 5.0F);
			if (Math.abs(this.rugY - 0.0F) < 0.5F)
				this.rugY = 0.0F; 
		} 
	}
	
	private void updateHand() {
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
	
	private void renderCourierBag(SpriteBatch sb) {
		sb.setColor(Color.WHITE.cpy());
		sb.draw(rewardButtonImg, rewardButtonBox.x, rewardButtonBox.y, 473.0f * Settings.scale, 301.0f * Settings.scale * rewardscale);
		FontHelper.renderFontCentered(sb, FontHelper.largeCardFont, 
					Integer.toString(TogetherManager.getCurrentUser().packages.size()),
					rewardButtonBox.x + 473.0f * Settings.scale / 2, rewardButtonBox.y + 301.0f * Settings.scale * rewardscale / 2, Color.WHITE.cpy(), 1.0f);
		rewardButtonBox.render(sb);
	}

	private void renderRelics(SpriteBatch sb) {
		for (CoopCourierRelic r : this.relics)
			r.render(sb); 
	}
	
	private void renderPotions(SpriteBatch sb) {
		for (CoopCourierPotion p : this.potions)
			p.render(sb); 
	}
	
	private void renderCardsAndPrices(SpriteBatch sb) {
		for (AbstractCard c : this.cards) {
			c.render(sb);
			sb.setColor(Color.WHITE);
			sb.draw(ImageMaster.UI_GOLD, c.current_x + GOLD_IMG_OFFSET_X, c.current_y + GOLD_IMG_OFFSET_Y - (c.drawScale - 0.75F) * 200.0F * Settings.scale, GOLD_IMG_WIDTH, GOLD_IMG_WIDTH);
			Color color = Color.WHITE.cpy();
			if (c.price > AbstractDungeon.player.gold) {
				color = Color.SALMON.cpy();
			}
			FontHelper.renderFontLeftTopAligned(sb, FontHelper.tipHeaderFont, 
					
					Integer.toString(c.price), c.current_x + PRICE_TEXT_OFFSET_X, c.current_y + PRICE_TEXT_OFFSET_Y - (c.drawScale - 0.75F) * 200.0F * Settings.scale, color);
		}  
		for (AbstractCard c : this.cards)
			c.renderCardTip(sb); 
	}
}
