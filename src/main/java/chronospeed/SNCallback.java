package chronospeed;

import com.codedisaster.steamworks.SteamAuth.AuthSessionResponse;
import com.codedisaster.steamworks.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import chronospeed.*;

public class SNCallback
  implements SteamNetworkingCallback
{

  private static final Logger logger = LogManager.getLogger(SMCallback.class.getName());

  public SNCallback() {}


  public void onP2PSessionConnectFail(SteamID paramSteamID, SteamNetworking.P2PSessionError paramP2PSessionError) {
    logger.info("onP2PSessionConnectFail");
  }
  
  public void onP2PSessionRequest(SteamID paramSteamID) {
    logger.info("onP2PSessionRequest");
    NetworkHelper.net.acceptP2PSessionWithUser(paramSteamID);
  }
}
