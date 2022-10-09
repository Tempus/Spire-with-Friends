package chronoMods.coop;

import chronoMods.TogetherManager;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;

import java.util.ArrayList;
import java.util.HashMap;

public class CardPoolThemes {

	public static void CalculateClassThemes(AbstractPlayer player) {

		HashMap<String, ArrayList<AbstractCard>> themeMap = new HashMap();

		ArrayList<AbstractCard> pool = new ArrayList();
		pool = player.getCardPool(pool);

		for (AbstractCard c : pool) {
			for (String keyword : c.keywords) {
				if (themeMap.containsKey(keyword)) {
					themeMap.get(keyword).add(c);
				}
				else {
					themeMap.put(keyword, new ArrayList());
					themeMap.get(keyword).add(c);
				}
			}
		}

		TogetherManager.log(themeMap.toString());
	}

}

// Things it obviously doesn't cover well: Discard, Draw, Block, Shivs and other Temp Cards



//  [E]=[Offering, Blood for Blood, Berserk, Seeing Red, Sentinel, Bloodletting, Dropkick]
//  vulnerable=[Thunderclap, Berserk, Shockwave, Dropkick, Uppercut]
//  strength=[Spot Weakness, Inflame, Flex, Disarm, Limit Break, Heavy Blade, Rupture, Demon Form]
//  upgrade=[Armaments, Searing Blow]
//  exhaust=[Warcry, Offering, Exhume, Reaper, Infernal Blade, Intimidate, True Grit, Impervious, Pummel, Burning Pact, Shockwave, Sever Soul, Dark Embrace, Seeing Red, Disarm, Fiend Fire, Havoc, Feel No Pain, Corruption, Limit Break, Sentinel, Second Wind, Feed]
// // curse=[Fire Breathing]
//  block=[Power Through, Iron Wave, Juggernaut, Body Slam, True Grit, Impervious, Shrug It Off, Flame Barrier, Metallicize, Barricade, Armaments, Feel No Pain, Rage, Entrench, Sentinel, Second Wind, Ghostly Armor]
// // ethereal=[Carnage, Ghostly Armor]
//  weak=[Intimidate, Clothesline, Shockwave, Uppercut]
// // fatal=[Feed]
//  status=[Fire Breathing, Evolve]


// // intangible=[Wraith Form]
// // dexterity=[Wraith Form, Footwork]
//  [E]=[Sneaky Strike, Tactician, Doppelganger, Adrenaline, Heel Hook, Eviscerate, Outmaneuver, Concentrate, Masterful Stab, Flying Knee]
// // vulnerable=[Terror]
//  poison=[Crippling Cloud, Deadly Poison, Catalyst, Bane, Poisoned Stab, Envenom, Noxious Fumes, Corpse Explosion, Bouncing Flask]
//  strength=[Malaise, Piercing Wail]
// // innate=[Backstab]
//  exhaust=[Crippling Cloud, Catalyst, Nightmare, Endless Agony, Doppelganger, Adrenaline, Calculated Gamble, Die Die Die, Terror, Malaise, Backstab, Alchemize, Piercing Wail, Distraction]
// // retain=[Well-Laid Plans]
//  block=[Cloak and Dagger, Leg Sweep, After Image, Deflect, Blur, Escape Plan, Dash, Backflip, Dodge and Roll]
// // unplayable=[Tactician, Reflex]
//  weak=[Crippling Cloud, Leg Sweep, Heel Hook, Malaise, Sucker Punch]


//  lightning=[Storm, Ball Lightning, Rainbow, Static Discharge, Tempest, Electrodynamics, Thunder Strike]
// // strength=[Reprogram]
// // innate=[Boot Sequence]
//  channel=[Storm, Ball Lightning, Glacier, Recursion, Fusion, Blizzard, Chaos, Barrage, Meteor Strike, Rainbow, Chill, Coolheaded, Static Discharge, Tempest, Electrodynamics, Darkness, Cold Snap, Thunder Strike, Doom and Gloom]
//  exhaust=[Genetic Algorithm, Core Surge, Fission, Reboot, Seek, Rainbow, Chill, Boot Sequence, Tempest, Double Energy, Hologram, Recycle, White Noise]
// // retain=[Equilibrium]
//  focus=[Consume, Biased Cognition, Reprogram, Hyperbeam, Defragment]
// // ethereal=[Echo Form]
// // lock-on=[Bullseye]
// // weak=[Go for the Eyes]
// // artifact=[Core Surge]
// // dexterity=[Reprogram]
//  [E]=[Fission, Sunder, Aggregate, TURBO, Force Field, Charge Battery, Recycle]
// // vulnerable=[Beam Cell]
//  frost=[Glacier, Blizzard, Rainbow, Chill, Coolheaded, Cold Snap]
//  dark=[Rainbow, Darkness, Doom and Gloom]
//  block=[Genetic Algorithm, Glacier, Stack, Melter, Boot Sequence, Equilibrium, Force Field, Reinforced Body, Charge Battery, Auto-Shields, Hologram, Leap, Steam Barrier]
//  plasma=[Fusion, Meteor Strike]
//  evoke=[Recursion, Multi-Cast]


//  [E]=[Deva Form, Fasting, Follow-Up]
//  scry=[Foresight, Just Lucky, Third Eye, Cut Through Fate, Nirvana, Weave]
//  divinity=[Blasphemy, Devotion, Worship, Prostrate, Pray]
//  mantra=[Devotion, Worship, Brilliance, Prostrate, Pray]
//  strength=[Fasting, Wish]
//  upgrade=[Lesson Learned, Master Reality]
//  exhaust=[Deus Ex Machina, Talk to the Hand, Foreign Influence, Crescendo, Tranquility, Collect, Omniscience, Wish, Alpha, Vault, Scrawl, Lesson Learned, Blasphemy, Conjure Blade]
//  retain=[Flying Sleeves, Protect, Windmill Strike, Crescendo, Tranquility, Establishment, Sands of Time, Meditate, Perseverance]
//  ethereal=[Deva Form]
//  weak=[Sash Whip, Wave of the Hand]
//  fatal=[Lesson Learned]
//  dexterity=[Fasting]
//  calm=[Like Water, Tranquility, Inner Peace, Fear No Evil, Meditate]
//  vulnerable=[Indignation, Crush Joints]
//  wrath=[Simmering Fury, Halt, Indignation, Crescendo, Rushdown, Tantrum]
//  block=[Sanctity, Halt, Just Lucky, Talk to the Hand, Protect, Third Eye, Like Water, Spirit Shield, Empty Body, Wallop, Deceive Reality, Mental Fortress, Wave of the Hand, Perseverance, Swivel, Nirvana, Evaluate, Prostrate]
//  stance=[Flurry of Blows, Empty Body, Mental Fortress, Empty Mind, Empty Fist]
//  unplayable=[Deus Ex Machina]