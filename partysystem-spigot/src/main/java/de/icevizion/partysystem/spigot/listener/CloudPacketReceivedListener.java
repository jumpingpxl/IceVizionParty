package de.icevizion.partysystem.spigot.listener;

import de.icevizion.partysystem.core.PartyInfoPacket;
import de.icevizion.partysystem.spigot.PartySpigotPlugin;
import de.icevizion.partysystem.spigot.events.PartyCreateEvent;
import de.icevizion.partysystem.spigot.events.PartyDeleteEvent;
import de.icevizion.partysystem.spigot.events.PartyUpdateEvent;
import de.icevizion.partysystem.spigot.util.Party;
import net.titan.spigot.event.CloudPacketReceivedEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class CloudPacketReceivedListener implements Listener {

	private PartySpigotPlugin partyPlugin;

	public CloudPacketReceivedListener(PartySpigotPlugin partyPlugin) {
		this.partyPlugin = partyPlugin;
	}

	@EventHandler
	public void onCloudPacketReceived(CloudPacketReceivedEvent event) {
		if (!(event.getNettyPacket() instanceof PartyInfoPacket)) {
			return;
		}

		//TODO -> Integrate Into System to Visualize the Party on the Server
		PartyInfoPacket partyInfoPacket = (PartyInfoPacket) event.getNettyPacket();
		switch (partyInfoPacket.getPartyAction()) {
			case CREATE:
				Party createdParty = new Party(partyPlugin, String.valueOf(partyInfoPacket.getPartyId()));
				partyPlugin.getCachedParties().put(createdParty.getIdentifier(), createdParty);
				partyPlugin.getServer().getPluginManager().callEvent(
						new PartyCreateEvent(new Party(partyPlugin, String.valueOf(partyInfoPacket.getPartyId()))));
				break;
			case UPDATE:
				partyPlugin.getServer().getPluginManager().callEvent(
						new PartyUpdateEvent(new Party(partyPlugin, String.valueOf(partyInfoPacket.getPartyId()))));
				break;
			case DELETE:
				Party deletedParty = new Party(partyPlugin, String.valueOf(partyInfoPacket.getPartyId()));
				partyPlugin.getCachedParties().put(deletedParty.getIdentifier(), deletedParty);
				partyPlugin.getServer().getPluginManager().callEvent(
						new PartyDeleteEvent(new Party(partyPlugin, String.valueOf(partyInfoPacket.getPartyId()))));
				break;
		}
	}
}
