package Lyraedan.networking.packets;

import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Gdx2DPixmap;

import chronoMods.TogetherManager;
import chronoMods.network.RemotePlayer;

public class CustomMarkPacket extends SpirePacket {

	@Override
	public void onDataReceived(ByteBuffer data, RemotePlayer playerInfo) {
		((Buffer)data).position(4);
		byte[] bytesMark = new byte[data.remaining()];
		data.get(bytesMark);

        try {
			Gdx2DPixmap pix = new Gdx2DPixmap(bytesMark, 0, bytesMark.length, 0);
			Pixmap customMark = new Pixmap(pix);

			playerInfo.bingoMark = new Texture(customMark);
        	TogetherManager.log("I suppose we have it now.");
        } catch (IOException e) {
        	TogetherManager.log("Custom Mark image did not transfer.");
        }		
	}

	@Override
	public ByteBuffer generatePacketData() {
		byte[] bytes = new FileHandle(TogetherManager.config.getString("mark")).readBytes();

		ByteBuffer data = ByteBuffer.allocateDirect(4 + bytes.length);
		// data.putLong((long)ReflectionHacks.getPrivate(ppp, Gdx2DPixmap.class, "basePtr"));
		((Buffer)data).position(4);
		data.put(bytes);
		((Buffer)data).rewind();
		return data;
	}

}
