package Lyraedan.networking.packets;

import java.nio.Buffer;
import java.nio.ByteBuffer;

import chronoMods.TogetherManager;
import chronoMods.coop.infusions.InfusionHelper;
import chronoMods.coop.infusions.InfusionReward;
import chronoMods.coop.infusions.InfusionSet;
import chronoMods.coop.relics.TransfusionBag;
import chronoMods.network.RemotePlayer;

public class InfusionPacket extends SpirePacket {

	// Coop specific
	
	@Override
	public void onDataReceived(ByteBuffer data, RemotePlayer playerInfo) {
		// Find the correct recipient
		long steamIDInfuse = data.getLong(4);
		if (!TogetherManager.currentUser.isUser(steamIDInfuse)) { return; }

		// Extract the string
		((Buffer)data).position(12);
		byte[] bytesInfuse = new byte[data.remaining()];
		data.get(bytesInfuse);
		String stringOutInfuse = new String(bytesInfuse);

		TogetherManager.log("Infusion Set: " + stringOutInfuse);

		// Get the set and add 3 packages
		InfusionSet infSet = InfusionHelper.getSetByID(stringOutInfuse);
		
		for (int i = 0; i < 3; i++)
        	TogetherManager.getCurrentUser().packages.add(new InfusionReward(infSet.getRandomInfusion()));
	}

	@Override
	public ByteBuffer generatePacketData() {
		ByteBuffer data = ByteBuffer.allocateDirect(12 + TransfusionBag.set.setID.getBytes().length);
		data.putLong(4, TogetherManager.courierScreen.getRecipient().getAccountID()); // Selected recipient
		
		((Buffer)data).position(12);
		data.put(TransfusionBag.set.setID.getBytes());
		((Buffer)data).rewind();

		TransfusionBag.set = null;
		return data;
	}

}
