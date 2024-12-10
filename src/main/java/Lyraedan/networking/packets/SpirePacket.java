package Lyraedan.networking.packets;

import java.nio.ByteBuffer;

import chronoMods.network.RemotePlayer;

/**
 * The parent packet class that contains the 2 overrideable functions on how to handle the specific data packet
 * */
public abstract class SpirePacket {

	public SpirePacket() { }
	
	public void parseData(ByteBuffer data, RemotePlayer playerInfo) {
		// Log("Parsing Packet: " + getClass().getSimpleName());
		onDataReceived(data, playerInfo);
	}
	
	public ByteBuffer generateData() {
		// Log("Generating packet: " + getClass().getSimpleName());
		return generatePacketData();
	}
	
	/**
	 * What does this packet do when it is parsed?
	 * @param data - Incoming data
	 * @param playerInfo - Incoming player info
	 */
	public abstract void onDataReceived(ByteBuffer data, RemotePlayer playerInfo);
	
	/**
	 * What data are we sending to the server?
	 * @return the data byte buffer at a size of (bufferSize + 4) the + 4 is for the data type ordinal
	 */
	public abstract ByteBuffer generatePacketData();
	
	public void Log(String msg) {
		System.out.println("[SpirePacket] " + msg);
	}
}
