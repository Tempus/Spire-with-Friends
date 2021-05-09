package chronoMods.network.discord;

import de.jcm.discordgamesdk.DiscordEventAdapter;

public class EventAdapter extends DiscordEventAdapter {
  public DiscordIntegration integration;
  public EventAdapter(DiscordIntegration integration) {
    this.integration = integration;
  }
}
