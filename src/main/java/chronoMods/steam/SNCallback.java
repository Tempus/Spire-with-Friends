package chronoMods.steam;

import com.codedisaster.steamworks.SteamAuth.AuthSessionResponse;
import com.codedisaster.steamworks.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import chronoMods.*;
import chronoMods.steam.*;
import chronoMods.ui.deathScreen.*;
import chronoMods.ui.hud.*;
import chronoMods.ui.lobby.*;
import chronoMods.ui.mainMenu.*;
import chronoMods.utilities.*;

public class SNCallback
  implements SteamNetworkingCallback
{

  private static final Logger logger = LogManager.getLogger(SMCallback.class.getName());

  public SNCallback() {}


  public void onP2PSessionConnectFail(SteamID paramSteamID, SteamNetworking.P2PSessionError paramP2PSessionError) {
    TogetherManager.log("onP2PSessionConnectFail");
  }
  
  public void onP2PSessionRequest(SteamID paramSteamID) {
    TogetherManager.log("onP2PSessionRequest");
    NetworkHelper.net.acceptP2PSessionWithUser(paramSteamID);
  }
}
