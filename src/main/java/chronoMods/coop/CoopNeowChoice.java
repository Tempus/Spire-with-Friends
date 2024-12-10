package chronoMods.coop;

import chronoMods.TogetherManager;
import chronoMods.network.RemotePlayer;
import com.badlogic.gdx.Gdx;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.RoomEventDialog;
import com.megacrit.cardcrawl.ui.buttons.LargeDialogOptionButton;

public class CoopNeowChoice {
	public int choice = -1;
	public RemotePlayer playerInfo;

	public float timer = 0.75f;
	public boolean complete = false;

	public CoopNeowChoice(int choice, RemotePlayer playerInfo) {
		this.choice = choice;
		this.playerInfo = playerInfo;
		timer = 0.75f;
	}

	public void update() {
		if (timer > 0) {
			timer -= Gdx.graphics.getDeltaTime();
		} else if (!complete) {
			complete = true;
			
			registerButtonSelection();			
			activateChosenNeowOption();

			if (playerInfo.isUser(TogetherManager.currentUser)) 
				disableNeowButtons();

	       	// Is choosing done?
			/*
	       	if (hasEveryoneChosenNeow()) {
	       		CoopNeowEvent.advanceScreen();
	       	}*/
		}
	}

    public void registerButtonSelection() {
    	String neowMsg = String.format(CardCrawlGame.languagePack.getUIString("Neow").TEXT[0], playerInfo.userName.replaceAll(" ", " #p"), AbstractDungeon.getCurrRoom().event.roomEventText.optionList.get(choice).msg);

		RoomEventDialog.optionList.get(choice).msg = neowMsg;
		RoomEventDialog.optionList.get(choice).isDisabled = true;

		if (CoopNeowEvent.screenNum == 1)
			CoopNeowEvent.rewards.get(choice).chosenBy = playerInfo;
		else 
			CoopNeowEvent.penalties.get(choice).chosenBy = playerInfo;
	
    }

    public void disableNeowButtons() {
		for (LargeDialogOptionButton choiceButton : RoomEventDialog.optionList)
			choiceButton.isDisabled = true;    	
    }

    public void activateChosenNeowOption() {
		if (playerInfo.isUser(TogetherManager.currentUser)) { 
			if (CoopNeowEvent.screenNum == 1) {	// Benign bonuses
		        if (CoopNeowEvent.rewards.get(choice).link == null) // ACtivate non-linked bonuses
		        	CoopNeowEvent.rewards.get(choice).activate();
		    } else {
		        CoopNeowEvent.penalties.get(choice).activate();
		    }
		}
		
		// Special Linked Choosing
		if (CoopNeowEvent.screenNum == 1) {	// Benign bonuses
			if (CoopNeowEvent.rewards.get(choice).link != null) {	// This is a linked choice
				if (CoopNeowEvent.rewards.get(choice).link.chosenBy != null) {	// The other choice is already chosen
					// Check both options, if either of them is us, activate
					CoopNeowReward neowReward = CoopNeowEvent.rewards.get(choice);

					if (neowReward.chosenBy.isUser(TogetherManager.currentUser)) // I am the one who just clicked it
						neowReward.linkedActivate(neowReward.link.chosenBy);
					if (neowReward.link.chosenBy.isUser(TogetherManager.currentUser)) //  The other person just clicked it
						neowReward.link.linkedActivate(playerInfo);
				}
			}
		}
    }

    public static boolean hasEveryoneChosenNeow() {
		int chosenPlayerCount = 0;

		boolean allPlayersChosen = false;
		boolean penalties = false;
		// Rewards
		if (CoopNeowEvent.screenNum == 1) {
			// Stop here if not everyone has chosen
			for (CoopNeowReward r : CoopNeowEvent.rewards) {
				if (r.chosenBy != null) { 
					chosenPlayerCount++;
					if (chosenPlayerCount >= TogetherManager.players.size()) {
						allPlayersChosen = true;
						break;
					}
				}
			}
		} else {
			// Penalties
			penalties = true;
			for (CoopNeowReward r : CoopNeowEvent.penalties) {
				if (r.chosenBy != null) { 
					chosenPlayerCount++;
					if (chosenPlayerCount >= TogetherManager.players.size()) {
						allPlayersChosen = true;
						break;
					}
				}
			}
		}
		
		return allPlayersChosen && IsEveryonesNeowReady();
    }
    
    public static boolean IsEveryonesNeowReady() {
    	int neowReadyCount = 0;
    	for(RemotePlayer player : TogetherManager.players) {
    		if(player.neowReady) {
    			neowReadyCount++;
    		}
    	}
    	return neowReadyCount >= TogetherManager.players.size();
    }
}
