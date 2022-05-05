package chronoMods.bingo;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.random.*;
import com.megacrit.cardcrawl.vfx.*;
import com.badlogic.gdx.graphics.Texture;

import chronoMods.*;
import chronoMods.ui.hud.*;
import chronoMods.network.*;
import chronoMods.ui.lobby.*;
import chronoMods.ui.mainMenu.*;

import java.util.*;

public class Caller
{
    public static final String[] EASY = CardCrawlGame.languagePack.getUIString("EasyBingo").TEXT;
    public static final String[] MED = CardCrawlGame.languagePack.getUIString("MedBingo").TEXT;
    public static final String[] HARD = CardCrawlGame.languagePack.getUIString("HardBingo").TEXT;

    public static long bingoSeed = 0;
	// In order to allow for different 'difficulties', we group bingos into Easy, Medium, and Hard.
	//		We can then make sliding scales for how many goals of each type are on a bingo card.

    public static ArrayList<AbstractGameEffect> notifications = new ArrayList();

    public static void bingoNotificationQueue() {
    	if (notifications.size() > 0) {
    		if (AbstractDungeon.topLevelEffects.size() == 0) {
    			AbstractDungeon.topLevelEffectsQueue.add(notifications.get(0));
    			notifications.remove(0);
    		}
    	}
    }

    // Accepts the number of Easy, Medium, and Hard in any given column. Should add up to five.
    public static int[][] makeBingoCard(int easy, int med, int hard) {

    	if (easy+med+hard != 5) { TogetherManager.log("Bingo Card not correct size."); }

    	int[][] card = new int[5][5];

    	com.megacrit.cardcrawl.random.Random rng;
    	if (NewMenuButtons.newGameScreen.uniqueBoardToggle.isTicked())
			rng = new com.megacrit.cardcrawl.random.Random(Caller.bingoSeed, TogetherManager.getCurrentUser().team);
		else
			rng = new com.megacrit.cardcrawl.random.Random(Caller.bingoSeed);


    	// Persistent Pools
    	ArrayList<Integer> easyPool = makeSequence(0,24);
		ArrayList<Integer> medPool	= makeSequence(25,49);
		ArrayList<Integer> hardPool	= makeSequence(50,74);

    	for (int x = 0; x < 5; x++) {
    		int y = 0;
    		for (Integer column : makeBingoColumn(rng, easy, med, hard, easyPool, medPool, hardPool)) {
    			card[x][y] = column;
    			y++;
    		}
    	}

    	return card;
    }

	public static ArrayList<Integer> makeSequence(int begin, int end) {
	  ArrayList<Integer> ret = new ArrayList<>(end - begin + 1);
	  for (int i=begin; i<=end; i++) {
	    ret.add(i);
	  }
	  return ret;  
	}

    public static ArrayList<Integer> makeBingoColumn(com.megacrit.cardcrawl.random.Random rng, int easy, int med, int hard, ArrayList<Integer> easyPool, ArrayList<Integer> medPool, ArrayList<Integer> hardPool) {
    	ArrayList<Integer> column = new ArrayList<Integer>();

       	int rn = 0;
    	
       	// Make the board. I hate that java has no 'remove and return the removed value'
    	for (int x = 0; x < easy; x++) {
    		rn = rng.random(easyPool.size()-1);
    		column.add(easyPool.get(rn));
    		easyPool.remove(rn);
    	}

    	for (int x = 0; x < med; x++) {
    		rn = rng.random(medPool.size()-1);
    		column.add(medPool.get(rn));
    		medPool.remove(rn);
    	}

    	for (int x = 0; x < hard; x++) {
    		rn = rng.random(hardPool.size()-1);
    		column.add(hardPool.get(rn));
    		hardPool.remove(rn);
    	}
  
  		Collections.shuffle(column, new java.util.Random(rng.random(1000000)));
    	return column;
    }

	public static int isWin(Texture[][] bingoCard) {

		// Horizontal lines
	    for (int x = 0; x < bingoCard.length; ++x) {
    		for(int y = 0; y < bingoCard[x].length; ++y) {
    			if (bingoCard[x][y] == null) { break; } // If one in line is not hit, move to the next line
    			if (y == 4) { return x+1; } // If you reach the end and they're all true, we've got a bingo. Horiz bingos are 1-5
    		}
    	}

		// Vertical lines
	    for (int y = 0; y < bingoCard[0].length; ++y) {
    		for(int x = 0; x < bingoCard.length; ++x) {
    			if (bingoCard[x][y] == null) { break; } // If one in line is not hit, move to the next line
    			if (x == 4) { return y+6; } // If you reach the end and they're all true, we've got a bingo. Vert bingos are 6-10
    		}
    	}

    	// Diagonal lines
    	for (int x = 0; x < bingoCard.length; ++x) {
    		if (bingoCard[x][x] == null) { break; } // If one in line is not hit, move to the next line
			if (x == 4) { return 11; } // If you reach the end and they're all true, we've got a bingo. Diag are 11-12
    	}

    	for (int x = 0; x < bingoCard.length; ++x) {
    		if (bingoCard[x][bingoCard.length-x-1] == null) { break; } // If one in line is not hit, move to the next line
			if (x == 4) { return 12; } // If you reach the end and they're all true, we've got a bingo. Diag are 11-12
    	}

    	// No Bingos
		return 0;
	}

	public static int countMarks(Texture[][] card) {
		int markCount = 0;
		for (Texture[] row : card)
			for (Texture mark : row)
				if (mark != null)
					markCount++;

		return markCount;
	}

	public static boolean markCard(RemotePlayer player, int rule) {
		Texture m = TogetherManager.bingoMark;
		if (player.bingoMark != null)
			m = player.bingoMark;

	    for (int x = 0; x < player.bingoCardIndices.length; ++x) {
    		for(int y = 0; y < player.bingoCardIndices[x].length; ++y) {
    			if (player.bingoCardIndices[x][y] == rule) {
    				if (player.bingoCard[x][y] == null) {
    					for (RemotePlayer p : ((BingoPlayerWidget)player.widget).teamPlayers)
		    				p.bingoCard[x][y] = m; 
	    				return true;
	    			}
    			}
    		}
    	}
    	return false;
	}

	public static boolean isMarked(int rule) {
		RemotePlayer player = TogetherManager.getCurrentUser();
	    for (int x = 0; x < player.bingoCardIndices.length; ++x) {
    		for(int y = 0; y < player.bingoCardIndices[x].length; ++y) {
    			if (player.bingoCardIndices[x][y] == rule) {
    				return player.bingoCard[x][y] != null;
    			}
    		}
    	}
    	return true;
	}
}