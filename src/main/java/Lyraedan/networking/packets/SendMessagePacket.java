package Lyraedan.networking.packets;

import java.nio.Buffer;
import java.nio.ByteBuffer;

import chronoMods.TogetherManager;
import chronoMods.network.RemotePlayer;

public class SendMessagePacket extends SpirePacket {

	@Override
	public void onDataReceived(ByteBuffer data, RemotePlayer playerInfo) {
		try {
            byte[] sndmsgBytes = new byte[data.remaining()];
            data.get(sndmsgBytes);
            String sndmsg = new String(sndmsgBytes);
            TogetherManager.chatScreen.addMsg(playerInfo.userName, sndmsg, playerInfo.colour);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
	}

	@Override
	public ByteBuffer generatePacketData() {
		String sndmsg = TogetherManager.chatScreen.TypingMsg;
        ByteBuffer data = ByteBuffer.allocateDirect(4 + (sndmsg.getBytes()).length);
        ((Buffer) data).position(4);
        data.put(sndmsg.getBytes());
        ((Buffer) data).rewind();
            // Hpr.info(snddata.toString());
            // for (dataType i : dataType.values()) {
            // Hpr.info(String.valueOf(i));
            // }
            // SteamUser steamUser = (SteamUser)
            // ReflectionHacks.getPrivate(CardCrawlGame.publisherIntegration,
            // com.megacrit.cardcrawl.integrations.steam.SteamIntegration.class,
            // "steamUser");
            // NetworkHelper.parseData(data, new SteamPlayer(steamUser.getSteamID()));
        return data;
	}

}
