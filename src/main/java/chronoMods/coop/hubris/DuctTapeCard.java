package chronoMods.coop.hubris;

// Used with Permission from Kiooeht
//  https://github.com/kiooeht/Hubris/blob/0ec283a74ea0c3131e0cf790e92e9fa857cbdac7/src/main/java/com/evacipated/cardcrawl/mod/hubris/cards/DuctTapeCard.java

import basemod.BaseMod;
import basemod.abstracts.CustomCard;
import basemod.abstracts.DynamicVariable;
import basemod.helpers.TooltipInfo;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import chronoMods.coop.hubris.DuctTapeUseNextAction;
import com.evacipated.cardcrawl.modthespire.lib.SpireOverride;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.unique.*;
import com.megacrit.cardcrawl.actions.defect.*;
import com.megacrit.cardcrawl.actions.utility.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardSave;
import com.megacrit.cardcrawl.cards.DescriptionLine;
import com.megacrit.cardcrawl.cards.green.*;
import com.megacrit.cardcrawl.cards.red.*;
import com.megacrit.cardcrawl.cards.blue.*;
import com.megacrit.cardcrawl.cards.purple.*;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.GameDictionary;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import com.megacrit.cardcrawl.stances.AbstractStance;

import chronoMods.*;

import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DuctTapeCard extends CustomCard
{
    public static final String ID = "MergeCard";
    private static final List<String> keywordBlacklist = Arrays.asList(
            "strike"
    );
    private static final Map<CardColor, Map<CardType, TextureAtlas.AtlasRegion>> cardBgMap;
    private static final Map<CardColor, Map<CardType, TextureAtlas.AtlasRegion>> cardLargeBgMap;
    private static final Map<CardRarity, Map<CardType, TextureAtlas.AtlasRegion>> cardFrameMap;
    private static final Map<CardRarity, Map<CardType, TextureAtlas.AtlasRegion>> cardLargeFrameMap;

    public List<AbstractCard> cards;
    private List<TextureAtlas.AtlasRegion> cardBgs = new ArrayList<>();
    private List<TextureAtlas.AtlasRegion> cardLargeBgs = new ArrayList<>();
    private List<TextureAtlas.AtlasRegion> cardFrames = new ArrayList<>();
    private List<TextureAtlas.AtlasRegion> cardLargeFrames = new ArrayList<>();
    private List<String> savedKeywords = new ArrayList<>();

    static
    {
        // Base Game card backgrounds
        cardBgMap = new HashMap<>();
        Map<CardType, TextureAtlas.AtlasRegion> red = new HashMap<>();
        cardBgMap.put(CardColor.RED, red);
        Map<CardType, TextureAtlas.AtlasRegion> green = new HashMap<>();
        cardBgMap.put(CardColor.GREEN, green);
        Map<CardType, TextureAtlas.AtlasRegion> blue = new HashMap<>();
        cardBgMap.put(CardColor.BLUE, blue);
        Map<CardType, TextureAtlas.AtlasRegion> purple = new HashMap<>();
        cardBgMap.put(CardColor.PURPLE, purple);
        Map<CardType, TextureAtlas.AtlasRegion> colorless = new HashMap<>();
        cardBgMap.put(CardColor.COLORLESS, colorless);
        Map<CardType, TextureAtlas.AtlasRegion> curse = new HashMap<>();
        cardBgMap.put(CardColor.CURSE, curse);
        
        red.put(CardType.ATTACK, ImageMaster.CARD_ATTACK_BG_RED);
        red.put(CardType.SKILL, ImageMaster.CARD_SKILL_BG_RED);
        red.put(CardType.POWER, ImageMaster.CARD_POWER_BG_RED);

        green.put(CardType.ATTACK, ImageMaster.CARD_ATTACK_BG_GREEN);
        green.put(CardType.SKILL, ImageMaster.CARD_SKILL_BG_GREEN);
        green.put(CardType.POWER, ImageMaster.CARD_POWER_BG_GREEN);

        blue.put(CardType.ATTACK, ImageMaster.CARD_ATTACK_BG_BLUE);
        blue.put(CardType.SKILL, ImageMaster.CARD_SKILL_BG_BLUE);
        blue.put(CardType.POWER, ImageMaster.CARD_POWER_BG_BLUE);

        purple.put(CardType.ATTACK, ImageMaster.CARD_ATTACK_BG_PURPLE);
        purple.put(CardType.SKILL, ImageMaster.CARD_SKILL_BG_PURPLE);
        purple.put(CardType.POWER, ImageMaster.CARD_POWER_BG_PURPLE);

        colorless.put(CardType.ATTACK, ImageMaster.CARD_ATTACK_BG_GRAY);
        colorless.put(CardType.SKILL, ImageMaster.CARD_SKILL_BG_GRAY);
        colorless.put(CardType.POWER, ImageMaster.CARD_POWER_BG_GRAY);

        curse.put(CardType.ATTACK, ImageMaster.CARD_SKILL_BG_BLACK);
        curse.put(CardType.SKILL, ImageMaster.CARD_SKILL_BG_BLACK);
        curse.put(CardType.POWER, ImageMaster.CARD_SKILL_BG_BLACK);
        
        cardLargeBgMap = new HashMap<>();
        red = new HashMap<>();
        cardLargeBgMap.put(CardColor.RED, red);
        green = new HashMap<>();
        cardLargeBgMap.put(CardColor.GREEN, green);
        blue = new HashMap<>();
        cardLargeBgMap.put(CardColor.BLUE, blue);
        purple = new HashMap<>();
        cardLargeBgMap.put(CardColor.PURPLE, purple);
        colorless = new HashMap<>();
        cardLargeBgMap.put(CardColor.COLORLESS, colorless);
        curse = new HashMap<>();
        cardLargeBgMap.put(CardColor.CURSE, curse);

        red.put(CardType.ATTACK, ImageMaster.CARD_ATTACK_BG_RED_L);
        red.put(CardType.SKILL, ImageMaster.CARD_SKILL_BG_RED_L);
        red.put(CardType.POWER, ImageMaster.CARD_POWER_BG_RED_L);

        green.put(CardType.ATTACK, ImageMaster.CARD_ATTACK_BG_GREEN_L);
        green.put(CardType.SKILL, ImageMaster.CARD_SKILL_BG_GREEN_L);
        green.put(CardType.POWER, ImageMaster.CARD_POWER_BG_GREEN_L);

        blue.put(CardType.ATTACK, ImageMaster.CARD_ATTACK_BG_BLUE_L);
        blue.put(CardType.SKILL, ImageMaster.CARD_SKILL_BG_BLUE_L);
        blue.put(CardType.POWER, ImageMaster.CARD_POWER_BG_BLUE_L);

        purple.put(CardType.ATTACK, ImageMaster.CARD_ATTACK_BG_PURPLE_L);
        purple.put(CardType.SKILL, ImageMaster.CARD_SKILL_BG_PURPLE_L);
        purple.put(CardType.POWER, ImageMaster.CARD_POWER_BG_PURPLE_L);

        colorless.put(CardType.ATTACK, ImageMaster.CARD_ATTACK_BG_GRAY_L);
        colorless.put(CardType.SKILL, ImageMaster.CARD_SKILL_BG_GRAY_L);
        colorless.put(CardType.POWER, ImageMaster.CARD_POWER_BG_GRAY_L);

        curse.put(CardType.ATTACK, ImageMaster.CARD_SKILL_BG_BLACK_L);
        curse.put(CardType.SKILL, ImageMaster.CARD_SKILL_BG_BLACK_L);
        curse.put(CardType.POWER, ImageMaster.CARD_SKILL_BG_BLACK_L);
        
        // Base game card frames
        cardFrameMap = new HashMap<>();
        Map<CardType, TextureAtlas.AtlasRegion> common = new HashMap<>();
        cardFrameMap.put(CardRarity.COMMON, common);
        cardFrameMap.put(CardRarity.BASIC, common);
        cardFrameMap.put(CardRarity.CURSE, common);
        Map<CardType, TextureAtlas.AtlasRegion> uncommon = new HashMap<>();
        cardFrameMap.put(CardRarity.UNCOMMON, uncommon);
        Map<CardType, TextureAtlas.AtlasRegion> rare = new HashMap<>();
        cardFrameMap.put(CardRarity.RARE, rare);

        common.put(CardType.ATTACK, ImageMaster.CARD_FRAME_ATTACK_COMMON);
        common.put(CardType.SKILL, ImageMaster.CARD_FRAME_SKILL_COMMON);
        common.put(CardType.POWER, ImageMaster.CARD_FRAME_POWER_COMMON);

        uncommon.put(CardType.ATTACK, ImageMaster.CARD_FRAME_ATTACK_UNCOMMON);
        uncommon.put(CardType.SKILL, ImageMaster.CARD_FRAME_SKILL_UNCOMMON);
        uncommon.put(CardType.POWER, ImageMaster.CARD_FRAME_POWER_UNCOMMON);

        rare.put(CardType.ATTACK, ImageMaster.CARD_FRAME_ATTACK_RARE);
        rare.put(CardType.SKILL, ImageMaster.CARD_FRAME_SKILL_RARE);
        rare.put(CardType.POWER, ImageMaster.CARD_FRAME_POWER_RARE);

        cardLargeFrameMap = new HashMap<>();
        common = new HashMap<>();
        cardLargeFrameMap.put(CardRarity.COMMON, common);
        cardLargeFrameMap.put(CardRarity.BASIC, common);
        cardLargeFrameMap.put(CardRarity.CURSE, common);
        uncommon = new HashMap<>();
        cardLargeFrameMap.put(CardRarity.UNCOMMON, uncommon);
        rare = new HashMap<>();
        cardLargeFrameMap.put(CardRarity.RARE, rare);

        common.put(CardType.ATTACK, ImageMaster.CARD_FRAME_ATTACK_COMMON_L);
        common.put(CardType.SKILL, ImageMaster.CARD_FRAME_SKILL_COMMON_L);
        common.put(CardType.POWER, ImageMaster.CARD_FRAME_POWER_COMMON_L);

        uncommon.put(CardType.ATTACK, ImageMaster.CARD_FRAME_ATTACK_UNCOMMON_L);
        uncommon.put(CardType.SKILL, ImageMaster.CARD_FRAME_SKILL_UNCOMMON_L);
        uncommon.put(CardType.POWER, ImageMaster.CARD_FRAME_POWER_UNCOMMON_L);

        rare.put(CardType.ATTACK, ImageMaster.CARD_FRAME_ATTACK_RARE_L);
        rare.put(CardType.SKILL, ImageMaster.CARD_FRAME_SKILL_RARE_L);
        rare.put(CardType.POWER, ImageMaster.CARD_FRAME_POWER_RARE_L);
    }

    public DuctTapeCard(List<AbstractCard> pCards)
    {
        super(ID, "Duct Tape", (String) null, -2, "", CardType.STATUS, CardColor.COLORLESS, CardRarity.SPECIAL, CardTarget.NONE);

        assert pCards != null;
        assert pCards.size() > 0;

        cards = new ArrayList<>(pCards.size());
        for (AbstractCard c : pCards) {
            cards.add(c.makeStatEquivalentCopy());
        }

        calculateCard();
    }

    private void calculateCard()
    {
        calculateCardTags();

        calculateBgs();

        calculateCost();
        calculateTarget();

        // Rarity
        for (AbstractCard c : cards) {
            if (c.rarity.ordinal() > rarity.ordinal()) {
                rarity = c.rarity;
            }
        }

        // Type
        for (AbstractCard c : cards) {
            switch (c.type) {
                case POWER:
                    if (type != CardType.CURSE) {
                        type = CardType.POWER;
                    }
                    break;
                case ATTACK:
                    if (type != CardType.CURSE && type != CardType.POWER) {
                        type = CardType.ATTACK;
                    }
                    break;
                case SKILL:
                    if (type != CardType.CURSE && type != CardType.POWER && type != CardType.ATTACK) {
                        type = CardType.SKILL;
                    }
                    break;
            }
        }

        initializeAmalgamTitle();

        calculateFrames();

        calculateKeywords();

        calculateDescription();
    }

    private void initializeAmalgamTitle() {

        // Amalgam Card name. We'll limit it to the first two cards because tests show three or more words tend to overrun the available space.
        int wordIndex = 0;

        name = "";
        name += cards.get(0).originalName.split(" ", 2)[0];

        String[] addOn = cards.get(1).originalName.split(" ", 2);
        if (addOn.length > 1)
            name += " " + addOn[1];
        else
            name += " " + addOn[0];

        // Upgrades
        for (AbstractCard c : cards) {
            if (c.upgraded) {
                name += "+";
                if (c.timesUpgraded > 1) {
                    name += c.timesUpgraded;
                }
            }
        }

        initializeTitle();
    }

    public String generateTransferID() {
        return cards.stream()
                        .map(x -> x.cardID)
                        .collect(Collectors.joining(";"));

    }


    private void calculateCardTags()
    {
        Set<CardTags> tags = new HashSet<>();
        for (AbstractCard c : cards) {
            tags.addAll(c.tags);
        }
        tags.remove(CardTags.STARTER_STRIKE);
        tags.remove(CardTags.STARTER_DEFEND);
        this.tags.clear();
        this.tags.addAll(tags);
    }

    public Texture calculateLargePortrait()
    {
        try {
            FrameBuffer fbo = new FrameBuffer(Pixmap.Format.RGBA8888, 500, 380, false);
            TextureRegion region = new TextureRegion(fbo.getColorBufferTexture());

            Texture portraitTexture;
            if (cards.get(0) instanceof CustomCard) {
                portraitTexture = CustomCard.getPortraitImage((CustomCard) cards.get(0));
            } else {
                portraitTexture = ImageMaster.loadImage("images/1024Portraits/" + cards.get(0).assetUrl + ".png");
            }
            TextureRegion portrait0 = new TextureRegion(portraitTexture);
            portrait0.setRegion(
                    portrait0.getRegionX(),
                    portrait0.getRegionY(),
                    portrait0.getRegionWidth() / 2,
                    portrait0.getRegionHeight()
            );
            portrait0.flip(false, true);

            if (cards.get(1) instanceof CustomCard) {
                portraitTexture = CustomCard.getPortraitImage((CustomCard) cards.get(1));
            } else {
                portraitTexture = ImageMaster.loadImage("images/1024Portraits/" + cards.get(1).assetUrl + ".png");
            }
            TextureRegion portrait1 = new TextureRegion(portraitTexture);
            portrait1.setRegion(
                    portrait1.getRegionX() + portrait1.getRegionWidth() / 2,
                    portrait1.getRegionY(),
                    portrait1.getRegionWidth() / 2,
                    portrait1.getRegionHeight()
            );
            portrait1.flip(false, true);

            fbo.begin();
            SpriteBatch sb = new SpriteBatch();
            sb.begin();

            sb.draw(portrait0, 0.0f, 0.0f, Gdx.graphics.getWidth() / 2.0f, Gdx.graphics.getHeight());

            sb.draw(portrait1, Gdx.graphics.getWidth() / 2.0f, 0.0f, Gdx.graphics.getWidth() / 2.0f, Gdx.graphics.getHeight());

            sb.end();
            fbo.end();

            region.flip(false, true);
            return region.getTexture();
        } catch (Exception e) {
            TogetherManager.logger.error(e);
            return null;
        }
    }

    private void calculateBgs()
    {
        cardBgs.clear();
        cardLargeBgs.clear();
        for (AbstractCard c : cards) {
            if (cardBgMap.containsKey(c.color)) {
                Map<CardType, TextureAtlas.AtlasRegion> tmp = cardBgMap.get(c.color);
                if (tmp.containsKey(c.type)) {
                    cardBgs.add(tmp.get(c.type));
                    cardLargeBgs.add(cardLargeBgMap.get(c.color).get(c.type));
                } else {
                    cardBgs.add(ImageMaster.CARD_SKILL_BG_BLACK);
                    cardLargeBgs.add(ImageMaster.CARD_SKILL_BG_BLACK_L);
                }
            } else {
                Texture tex;
                TextureAtlas.AtlasRegion texture;
                TextureAtlas.AtlasRegion largeTexture;
                switch (c.type) {
                    case POWER:
                        if (BaseMod.getPowerBgTexture(c.color) == null) {
                            BaseMod.savePowerBgTexture(c.color, ImageMaster.loadImage(BaseMod.getPowerBg(c.color)));
                        }
                        tex = BaseMod.getPowerBgTexture(c.color);
                        texture = new TextureAtlas.AtlasRegion(tex, 0, 0, tex.getWidth(), tex.getHeight());
                        if (BaseMod.getPowerBgPortraitTexture(c.color) == null) {
                            BaseMod.savePowerBgPortraitTexture(c.color, ImageMaster.loadImage(BaseMod.getPowerBgPortrait(c.color)));
                        }
                        tex = BaseMod.getPowerBgPortraitTexture(c.color);
                        largeTexture = new TextureAtlas.AtlasRegion(tex, 0, 0, tex.getWidth(), tex.getHeight());
                        break;
                    case ATTACK:
                        if (BaseMod.getAttackBgTexture(c.color) == null) {
                            BaseMod.saveAttackBgTexture(c.color, ImageMaster.loadImage(BaseMod.getAttackBg(c.color)));
                        }
                        tex = BaseMod.getAttackBgTexture(c.color);
                        texture = new TextureAtlas.AtlasRegion(tex, 0, 0, tex.getWidth(), tex.getHeight());
                        if (BaseMod.getAttackBgPortraitTexture(c.color) == null) {
                            BaseMod.saveAttackBgPortraitTexture(c.color, ImageMaster.loadImage(BaseMod.getAttackBgPortrait(c.color)));
                        }
                        tex = BaseMod.getAttackBgPortraitTexture(c.color);
                        largeTexture = new TextureAtlas.AtlasRegion(tex, 0, 0, tex.getWidth(), tex.getHeight());
                        break;
                    case SKILL:
                        if (BaseMod.getSkillBgTexture(c.color) == null) {
                            BaseMod.saveSkillBgTexture(c.color, ImageMaster.loadImage(BaseMod.getSkillBg(c.color)));
                        }
                        tex = BaseMod.getSkillBgTexture(c.color);
                        texture = new TextureAtlas.AtlasRegion(tex, 0, 0, tex.getWidth(), tex.getHeight());
                        if (BaseMod.getSkillBgPortraitTexture(c.color) == null) {
                            BaseMod.saveSkillBgPortraitTexture(c.color, ImageMaster.loadImage(BaseMod.getSkillBgPortrait(c.color)));
                        }
                        tex = BaseMod.getSkillBgPortraitTexture(c.color);
                        largeTexture = new TextureAtlas.AtlasRegion(tex, 0, 0, tex.getWidth(), tex.getHeight());
                        break;
                    default:
                        texture = ImageMaster.CARD_SKILL_BG_BLACK;
                        largeTexture = ImageMaster.CARD_SKILL_BG_BLACK_L;
                        break;
                }
                cardBgs.add(texture);
                cardLargeBgs.add(largeTexture);
            }
        }
    }

    private void calculateFrames()
    {
        cardFrames.clear();
        cardLargeFrames.clear();
        for (AbstractCard c : cards) {
            if (cardFrameMap.containsKey(c.rarity)) {
                Map<CardType, TextureAtlas.AtlasRegion> tmp = cardFrameMap.get(c.rarity);
                if (tmp.containsKey(c.type)) {
                    cardFrames.add(tmp.get(c.type));
                    cardLargeFrames.add(cardLargeFrameMap.get(c.rarity).get(c.type));
                } else {
                    cardFrames.add(ImageMaster.CARD_FRAME_SKILL_COMMON);
                    cardLargeFrames.add(ImageMaster.CARD_FRAME_SKILL_COMMON_L);
                }
            } else {
                cardFrames.add(ImageMaster.CARD_FRAME_SKILL_COMMON);
                cardLargeFrames.add(ImageMaster.CARD_FRAME_SKILL_COMMON_L);
            }
        }
    }

    public int costModifier;
    public int costForTurnSetter;
    public boolean turnCostChanged;

    public void calculateCost()
    {
        // Add up card costs
        cost = 0;
        costForTurn = 0;
        for (AbstractCard c : cards) {
            if (c.cost < 0) {
                cost = c.cost;
                break;
            }
            cost += c.cost;
            costForTurn += c.costForTurn;

            if (c.isCostModified)
                isCostModified = true;
            if (c.isCostModifiedForTurn)
                isCostModifiedForTurn = true;
        }

        // If one of the cards below is an X cost, curse, or unplayable card
        if (cost < 0) {
            for (AbstractCard c : cards) {
                c.costForTurn = c.cost = cost;
            }

            return;
        }

        // Deal with modifications here
        // Cost for turn
        if (turnCostChanged) {
          costForTurn = costForTurnSetter;
          if (costForTurn < 0)
            costForTurn = 0;
          if (costForTurn != this.cost)
            isCostModifiedForTurn = true; 
        } 

        // Cost for Combat changes
        if (costModifier != 0) {
            int preCost = cost;

            cost += costModifier;
            costForTurn += costModifier;
            
            if (cost < 0) { cost = 0; }
            if (costForTurn < 0) { costForTurn = 0; }

            if (preCost != cost)
                isCostModified = true;
        }
    }

    @Override
    public void updateCost(int amt) {
        costModifier += amt;
        calculateCost();
    }

    @Override
    public void setCostForTurn(int amt) {
        costForTurnSetter = amt;
        turnCostChanged = true;
        calculateCost();
    }

    @Override
    public void modifyCostForCombat(int amt) {
        costModifier += amt;
        calculateCost();
    }

    @Override
    public void resetAttributes() {
        super.resetAttributes();
        costForTurnSetter = this.cost;
        turnCostChanged = false;
    }


    private void calculateTarget()
    {
        // Figure out card target type
        boolean self = false;
        boolean enemy = false;
        boolean all_enemy = false;
        for (AbstractCard c : cards) {
            switch (c.target) {
                case SELF:
                    self = true;
                    break;
                case ENEMY:
                    enemy = true;
                    break;
                case ALL_ENEMY:
                    all_enemy = true;
                    break;
                case SELF_AND_ENEMY:
                    self = true;
                    enemy = true;
                    break;
                case ALL:
                    self = true;
                    all_enemy = true;
                    break;
            }
        }
        if (self && enemy) {
            target = CardTarget.SELF_AND_ENEMY;
        } else if (self && all_enemy) {
            target = CardTarget.ALL;
        } else if (self) {
            target = CardTarget.SELF;
        } else if (enemy) {
            target = CardTarget.ENEMY;
        } else if (all_enemy) {
            target = CardTarget.ALL_ENEMY;
        }
    }

    private void calculateKeywords()
    {
        // Exhaust
        exhaust = false;
        for (AbstractCard c : cards) {
            if (c.exhaust) {
                exhaust = true;
                break;
            }
        }
        // Ethereal
        isEthereal = false;
        for (AbstractCard c : cards) {
            if (c.isEthereal) {
                isEthereal = true;
                break;
            }
        }
        // Innate
        isInnate = false;
        for (AbstractCard c : cards) {
            if (c.isInnate) {
                isInnate = true;
                break;
            }
        }

        // Retain
        retain = false;
        for (AbstractCard c : cards) {
            if (c.retain) {
                retain = true;
                break;
            }
        }

        // Retain
        selfRetain = false;
        for (AbstractCard c : cards) {
            if (c.selfRetain) {
                selfRetain = true;
                break;
            }
        }

    }

    private void calculateDescription()
    {
        // Make description from card names
        rawDescription = cards.stream().map(c -> c.name).collect(Collectors.joining(" NL + NL "));
        if (exhaust) {
            rawDescription += " NL Exhaust.";
        }
        String prefix = "";
        if (isInnate) {
            prefix += " Innate.";
        }
        if (isEthereal) {
            prefix += " Ethereal.";
        }
        rawDescription = prefix.trim() + " NL " + rawDescription;
        initializeDescription();
    }

    @Override
    public void initializeDescription()
    {
        super.initializeDescription();

        for (DescriptionLine line : description) {
            String[] words;
            if (Settings.lineBreakViaCharacter) {
                words = line.getCachedTokenizedTextCN();
            } else {
                words = line.getCachedTokenizedText();
            }
            for (int i=0; i<words.length; ++i) {
                if (!words[i].startsWith("*")) {
                    words[i] = "*" + words[i];//.substring(1);
                }
            }
            //line.text = String.join(" *" , words);
            //line.text = "*" + line.text;
        }
    }

    @SpireOverride
    protected void renderPortrait(SpriteBatch sb)
    {
        float drawX;
        float drawY;

        TextureAtlas.AtlasRegion portrait0 = null;
        TextureAtlas.AtlasRegion portrait1 = null;
        try {
            Field f = AbstractCard.class.getDeclaredField("portrait");
            f.setAccessible(true);

            portrait0 = (TextureAtlas.AtlasRegion) f.get(cards.get(0));
            portrait1 = (TextureAtlas.AtlasRegion) f.get(cards.get(1));
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }

        if (portrait0 != null && portrait1 != null) {
            boolean sameCard = cards.get(0).cardID.equals(cards.get(1).cardID);

            portrait0 = new TextureAtlas.AtlasRegion(portrait0);
            if (!sameCard) {
                portrait0.setRegion(
                        portrait0.getRegionX(),
                        portrait0.getRegionY(),
                        portrait0.getRegionWidth() / 2,
                        portrait0.getRegionHeight()
                );
            }
            portrait1 = new TextureAtlas.AtlasRegion(portrait1);
            if (!sameCard) {
                portrait1.setRegion(
                        portrait1.getRegionX() + portrait1.getRegionWidth() / 2,
                        portrait1.getRegionY(),
                        portrait1.getRegionWidth() / 2,
                        portrait1.getRegionHeight()
                );
            }

            drawX = current_x - portrait0.packedWidth / 2f;
            drawY = current_y - portrait0.packedHeight / 2f;
            sb.draw(portrait0,
                    drawX, drawY + 72.0F,
                    portrait0.packedWidth / 2.0F, portrait0.packedHeight / 2.0F - 72.0F,
                    portrait0.packedWidth / 2.0f, portrait0.packedHeight,
                    drawScale * Settings.scale, drawScale * Settings.scale,
                    angle
            );
            drawX = current_x - portrait1.packedWidth / 2f;
            drawY = current_y - portrait1.packedHeight / 2f;
            sb.draw(portrait1,
                    drawX + (portrait1.packedWidth / 2.0f), drawY + 72.0F,
                    0, portrait1.packedHeight / 2.0F - 72.0F,
                    portrait1.packedWidth / 2.0f, portrait1.packedHeight,
                    drawScale * Settings.scale, drawScale * Settings.scale,
                    angle
            );
        }
    }

    @SpireOverride
    protected void renderCardBg(SpriteBatch sb, float x, float y)
    {
        sb.setColor(Color.WHITE);
        TextureAtlas.AtlasRegion img = new TextureAtlas.AtlasRegion(cardBgs.get(0));
        img.setRegionWidth(img.getRegionWidth() / 2 + 1);
        sb.draw(img,
                x + img.offsetX - img.originalWidth / 2f,
                y + img.offsetY - img.originalHeight / 2f,
                img.originalWidth / 2f - img.offsetX,
                img.originalHeight / 2f - img.offsetY,
                img.packedWidth / 2f + 1,
                img.packedHeight,
                drawScale * Settings.scale, drawScale * Settings.scale,
                angle
        );
        img = new TextureAtlas.AtlasRegion(cardBgs.get(1));
        img.setRegion(
                img.getRegionX() + img.getRegionWidth() / 2,
                img.getRegionY(),
                img.getRegionWidth() / 2,
                img.getRegionHeight()
        );
        sb.draw(img,
                x,
                y + img.offsetY - img.originalHeight / 2f,
                0,
                img.originalHeight / 2f - img.offsetY,
                img.packedWidth / 2f,
                img.packedHeight,
                drawScale * Settings.scale, drawScale * Settings.scale,
                angle
        );
    }

    @SuppressWarnings("unused")
    public void renderDuctTapeLargeCardBg(SpriteBatch sb)
    {
        sb.setColor(Color.WHITE);
        TextureAtlas.AtlasRegion img = new TextureAtlas.AtlasRegion(cardLargeBgs.get(0));
        img.setRegionWidth(img.getRegionWidth() / 2 + 1);
        sb.draw(img,
                Settings.WIDTH / 2f + img.offsetX - img.originalWidth / 2f,
                Settings.HEIGHT / 2f + img.offsetY - img.originalHeight / 2f,
                img.originalWidth / 2f - img.offsetX,
                img.originalHeight / 2f - img.offsetY,
                img.packedWidth / 2f + 1,
                img.packedHeight,
                Settings.scale, Settings.scale,
                angle
        );
        img = new TextureAtlas.AtlasRegion(cardLargeBgs.get(1));
        img.setRegion(
                img.getRegionX() + img.getRegionWidth() / 2,
                img.getRegionY(),
                img.getRegionWidth() / 2,
                img.getRegionHeight()
        );
        sb.draw(img,
                Settings.WIDTH / 2f,
                Settings.HEIGHT / 2f + img.offsetY - img.originalHeight / 2f,
                0,
                img.originalHeight / 2f - img.offsetY,
                img.packedWidth / 2f,
                img.packedHeight,
                Settings.scale, Settings.scale,
                angle
        );
    }

    @SpireOverride
    protected void renderPortraitFrame(SpriteBatch sb, float x, float y)
    {
        sb.setColor(Color.WHITE);
        TextureAtlas.AtlasRegion img = new TextureAtlas.AtlasRegion(cardFrames.get(0));
        img.setRegionWidth(img.getRegionWidth() / 2);
        sb.draw(img,
                x + img.offsetX - img.originalWidth / 2f,
                y + img.offsetY - img.originalHeight / 2f,
                img.originalWidth / 2f - img.offsetX,
                img.originalHeight / 2f - img.offsetY,
                img.packedWidth / 2f,
                img.packedHeight,
                drawScale * Settings.scale, drawScale * Settings.scale,
                angle
        );
        img = new TextureAtlas.AtlasRegion(cardFrames.get(1));
        img.setRegion(
                img.getRegionX() + img.getRegionWidth() / 2,
                img.getRegionY(),
                img.getRegionWidth() / 2,
                img.getRegionHeight()
        );
        sb.draw(img,
                x,
                y + img.offsetY - img.originalHeight / 2f,
                0,
                img.originalHeight / 2f - img.offsetY,
                img.packedWidth / 2f,
                img.packedHeight,
                drawScale * Settings.scale, drawScale * Settings.scale,
                angle
        );
    }

    @SuppressWarnings("unused")
    public void renderDuctTapeLargeFrame(SpriteBatch sb)
    {
        sb.setColor(Color.WHITE);
        TextureAtlas.AtlasRegion img = new TextureAtlas.AtlasRegion(cardLargeFrames.get(0));
        img.setRegionWidth(img.getRegionWidth() / 2);
        sb.draw(img,
                Settings.WIDTH / 2f + img.offsetX - img.originalWidth / 2f,
                Settings.HEIGHT / 2f + img.offsetY - img.originalHeight / 2f,
                img.originalWidth / 2f - img.offsetX,
                img.originalHeight / 2f - img.offsetY,
                img.packedWidth / 2f,
                img.packedHeight,
                Settings.scale, Settings.scale,
                angle
        );
        img = new TextureAtlas.AtlasRegion(cardLargeFrames.get(1));
        img.setRegion(
                img.getRegionX() + img.getRegionWidth() / 2,
                img.getRegionY(),
                img.getRegionWidth() / 2,
                img.getRegionHeight()
        );
        sb.draw(img,
                Settings.WIDTH / 2f,
                Settings.HEIGHT / 2f + img.offsetY - img.originalHeight / 2f,
                0,
                img.originalHeight / 2f - img.offsetY,
                img.packedWidth / 2f,
                img.packedHeight,
                Settings.scale, Settings.scale,
                angle
        );
    }


    int previewCard = 0;
    float previewTime = 3f;
    @Override
    public void renderCardTip(SpriteBatch sb)
    {
        this.cardsToPreview = cards.get(previewCard);
        previewTime -= Gdx.graphics.getDeltaTime();
        if (previewTime <= 0) {
            previewTime = 3f;
            previewCard++;
            if (previewCard == cards.size())
                previewCard = 0;
        }

        keywords.removeIf(keywordBlacklist::contains);
        savedKeywords.addAll(keywords);
        keywords.clear();
        super.renderCardTip(sb);
    }

    @Override
    public List<TooltipInfo> getCustomTooltips()
    {
        List<TooltipInfo> tooltips = new ArrayList<>();
        for (AbstractCard card : cards) {
            String description = "";
            boolean firstLine = true;
            for (DescriptionLine line : card.description) {
                if (!firstLine) {
                    description += " NL ";
                }
                firstLine = false;

                String[] tokens;
                if (Settings.lineBreakViaCharacter) {
                    tokens = line.getCachedTokenizedTextCN();
                } else {
                    tokens = line.getCachedTokenizedText();
                }
                for (String tmp : tokens) {
                    if (tmp.charAt(0) == '*') {
                        tmp = FontHelper.colorString(tmp.substring(1), "y");
                        tmp += " ";
                    } else if (tmp.charAt(0) == '!') {
                        Pattern pattern = Pattern.compile("!(.+)!(.*) ");
                        Matcher matcher = pattern.matcher(tmp);
                        if (matcher.find()) {
                            tmp = matcher.group(1);
                        }

                        // Main body of method
                        StringBuilder stringBuilder = new StringBuilder();
                        String color = null;
                        int num = 0;
                        DynamicVariable dv = BaseMod.cardDynamicVariableMap.get(tmp);
                        if (dv != null) {
                            if (dv.isModified(card)) {
                                num = dv.value(card);
                                if (num >= dv.baseValue(card)) {
                                    color = "g";
                                } else {
                                    color = "r";
                                }
                            } else {
                                num = dv.baseValue(card);
                            }
                        }
                        tmp = Integer.toString(num);
                        if (color != null) {
                            tmp = FontHelper.colorString(tmp, color);
                        }

                        tmp += " ";
                    }
                    description += tmp;
                }
            }
            description = description.replaceAll("\\]\\.", "] .");
            tooltips.add(new TooltipInfo(card.name, description));
        }

        // Saved keywords
        for (String keyword : savedKeywords) {
            if (GameDictionary.keywords.containsKey(keyword)) {
                tooltips.add(new TooltipInfo(TipHelper.capitalize(keyword), GameDictionary.keywords.get(keyword)));
            }
        }

        return tooltips;
    }

    private boolean subCardCanUse(AbstractCard card, AbstractPlayer p, AbstractMonster m)
    {
        if (card.canUse(p, m)) {
            return true;
        }
        if (card.cardPlayable(m)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean canUse(AbstractPlayer p, AbstractMonster m)
    {
        for (AbstractCard c : cards) {
            if (!subCardCanUse(c, p, m)) {
                return false;
            }
        }
        return super.canUse(p, m);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m)
    {
        for (AbstractCard c : cards)
            c.energyOnUse = energyOnUse;

        AbstractDungeon.actionManager.addToBottom(new DuctTapeUseNextAction(this, cards, 0, p, m));

        if (cost == -1)
            if (!this.freeToPlayOnce)
                p.energy.use(EnergyPanel.totalCount); 
    }

    public void calculateCardDamage(AbstractMonster m) {
        for (AbstractCard c : cards) {
            c.calculateCardDamage(m);
        }
    }

    @Override
    public void applyPowers()
    {
        for (AbstractCard c : cards) {
            c.applyPowers();
        }
    }

    @Override
    public void triggerWhenDrawn()
    {
        for (AbstractCard c : cards) {
            if (c instanceof EndlessAgony) {
                addToTop(new MakeTempCardInHandAction(makeStatEquivalentCopy()));
            }
            else if (c instanceof DeusExMachina) {
                c.triggerWhenDrawn();
                addToTop(new ExhaustSpecificCardAction(this, AbstractDungeon.player.hand));
            }
            else {
                c.triggerWhenDrawn();
            }
        }
        calculateCost();
    }

    @Override
    public void triggerOnExhaust()
    {
        for (AbstractCard c : cards) {
            c.triggerOnExhaust();
        }
    }

    @Override
    public void triggerOnManualDiscard()
    {
        for (AbstractCard c : cards) {
            c.triggerOnManualDiscard();
        }
    }
    
    @Override
    public void triggerOnOtherCardPlayed(AbstractCard cardPlayed)
    {
        for (AbstractCard c : cards) {
            c.triggerOnOtherCardPlayed(cardPlayed);
        }
    }

    @Override
    public void triggerOnCardPlayed(AbstractCard cardPlayed)
    {
        for (AbstractCard c : cards) {
            c.triggerOnCardPlayed(cardPlayed);
        }
    }

    @Override
    public void triggerOnScry()
    {
        for (AbstractCard c : cards) {
            if (c instanceof Weave) {
                addToBot(new DiscardToHandAction(this));
            }
            else {
                c.triggerOnScry();
            }
        }
    }

    @Override
    public void triggerExhaustedCardsOnStanceChange(AbstractStance newStance)
    {
        for (AbstractCard c : cards) {
            if (c instanceof FlurryOfBlows) {
                addToBot(new DiscardToHandAction(this));
            }
            else {
                c.triggerExhaustedCardsOnStanceChange(newStance);
            }
        }
    }

    @Override
    public void atTurnStart()
    {
        for (AbstractCard c : cards) {
            c.atTurnStart();
        }
        calculateCost();
    }

    @Override
    public void didDiscard()
    {
        for (AbstractCard c : cards) {
            c.didDiscard();
        }
        calculateCost();
    }

    @Override
    public void tookDamage()
    {
        for (AbstractCard c : cards) {
            c.tookDamage();
        }
        calculateCost();
    }

    @Override
    public void onRetained()
    {
        for (AbstractCard c : cards) {
            c.onRetained();
        }
        calculateCost();
    }

    @Override
    public boolean canUpgrade()
    {
        for (AbstractCard c : cards) {
            if (c.canUpgrade()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void upgrade()
    {
        timesUpgraded = 0;
        upgraded = true;
        for (int i=0; i<cards.size(); ++i) {
            AbstractCard c = cards.get(i);//.makeStatEquivalentCopy();
            if (c.canUpgrade()) {
                c.upgrade();
            }
            timesUpgraded += c.timesUpgraded;
            cards.set(i, c);
        }
        calculateCard();
    }

    @Override
    public AbstractCard makeCopy()
    {
        return new DuctTapeCard(cards);
    }

    @Override
    public AbstractCard makeStatEquivalentCopy()
    {
        DuctTapeCard card = (DuctTapeCard) super.makeStatEquivalentCopy();

        for (int i=0; i<cards.size(); ++i) {
            card.cards.set(i, cards.get(i).makeStatEquivalentCopy());
        }

        return card;
    }

    @Override
    public AbstractCard makeSameInstanceOf()
    {
        DuctTapeCard card = (DuctTapeCard) super.makeSameInstanceOf();

        for (int i=0; i<cards.size(); ++i) {
            card.cards.get(i).uuid = cards.get(i).uuid;
        }

        return card;
    }

    public List<CardSave> makeCardSaves()
    {
        ArrayList<CardSave> ret = new ArrayList<>();

        for (AbstractCard card : cards) {
            ret.add(new CardSave(card.cardID, card.timesUpgraded, card.misc));
        }

        return ret;
    }

    public void checkCardUUIDs(HashSet<AbstractCard> foundCards, UUID uuid)
    {
        for (AbstractCard card : cards) {
            if (card.uuid.equals(uuid)) {
                foundCards.add(card);
            }
        }
    }

    public boolean containsCard(String cardID)
    {
        for (AbstractCard card : cards) {
            if (card.cardID.equals(cardID)) {
                return true;
            }
        }
        return false;
    }
}