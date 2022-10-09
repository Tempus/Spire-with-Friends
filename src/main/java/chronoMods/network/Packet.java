package chronoMods.network;

import java.nio.ByteBuffer;

// Wrapper class for a data packet
public class Packet {
    private ByteBuffer data;
    private RemotePlayer player;
  
    public Packet(RemotePlayer player, ByteBuffer data) {
        this.data = data;
        this.player = player;
    }

    public Packet() {
        this.clear();
    }

    public void clear() {
        this.data = null;
        this.player = null;
    }
  
    public void set(RemotePlayer player, ByteBuffer data) {
        this.data = data;
        this.player = player;
    }

  	public boolean hasPacket() 
  	{ 
  		if (data == null || player == null)
  			return false;
  		return true;
  	}

  	public RemotePlayer player() { return player; }
  	public ByteBuffer data() { return data; }
}
