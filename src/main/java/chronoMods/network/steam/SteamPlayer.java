package chronoMods.network.steam;

import chronoMods.TogetherManager;
import chronoMods.network.NetworkHelper;
import chronoMods.network.RemotePlayer;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.codedisaster.steamworks.SteamID;

import java.nio.ByteBuffer;

public class SteamPlayer extends RemotePlayer
{
	public SteamID steamUser;
	public SteamIntegration service;

	public Pixmap pixmap;
	public boolean portraitImgLoaded;
	public int avatarID = -1;

	////////////////////////////////////////////
	// Highly Recommended you reimplement these:

	public SteamPlayer(SteamID steamuser) {		
		this.steamUser = steamuser;
		this.service  = ((SteamIntegration)NetworkHelper.service());

		this.userName = service.friends.getFriendPersonaName(this.steamUser).trim();

		getAvatar();
	}

	public void getAvatar() {
		// This service call will trigger a callback
		boolean known = service.friends.requestUserInformation(this.steamUser, false);
		// if (!known) { return; }

		int code = service.friends.getLargeFriendAvatar(this.steamUser);

		// if (code > 3) // -1 is "wait for callback", 0 is "No avatar set", "3 is Steam Default ?"
			updateAvatar(code);
	}

	public void updateAvatar(int imageID) {
		TogetherManager.log("~~~~~~~~~~~~~~~~~~~~~ Starting Steam Avatar ~~~~~~~~~~~~~~~~~~~~~");
		TogetherManager.log("ImageID: " + imageID);

		if (imageID == avatarID) { return; }

		int width = service.utils.getImageWidth(imageID);
		int height = service.utils.getImageHeight(imageID);

		TogetherManager.log("W: " + width + ", H: " + height);

		ByteBuffer imageBuffer = ByteBuffer.allocateDirect(width*height*4);
		try {
			boolean success = SteamworksAdapter.steamUtilsGetImageRGBA(service.utils, imageID, imageBuffer, width*height*4);
			TogetherManager.log("Image downloaded: " + success);
		}
		catch (Exception e) {
			TogetherManager.log(e.getMessage());
		}

		pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				pixmap.drawPixel(x, y, imageBuffer.getInt());
			}
		}

		SteamID su = this.steamUser;
		avatarID = imageID;
		portraitImg = null;

		// // Runnable needed to establish GL Context
		// Gdx.app.postRunnable(new Runnable() {
		// 	@Override
		// 	public void run() {
		// 		for (RemotePlayer player : TogetherManager.players) {
		// 			if (player.isUser(su)) {
		// 				player.getPortrait() = new Texture(pixmap);
		// 				player.getPortrait().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		// 				TogetherManager.log("Portrait is assigned");
		// 			}
		// 		}
		// 	}
		// });
		TogetherManager.log("We have completed creating the Steam image");
	}

	public Texture getPortrait() {
		if (pixmap == null && !portraitImgLoaded) {
			if (portraitImg == null) {
				portraitImg = new Texture(new Pixmap(182, 182, Pixmap.Format.RGBA8888));
			}
			return portraitImg;
		}

		if (portraitImg != null) {
			if (portraitImgLoaded) {
				return portraitImg;
			}
			portraitImg.dispose();
		}

		portraitImg = new Texture(pixmap);
		portraitImg.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		portraitImgLoaded = true;

		return portraitImg;
	}

	public boolean isUser(Object player) {
		if (player instanceof SteamID)
			return this.steamUser.getAccountID() == ((SteamID)player).getAccountID();
		return false;
	}

	public long getAccountID() { return steamUser.getAccountID(); }
}