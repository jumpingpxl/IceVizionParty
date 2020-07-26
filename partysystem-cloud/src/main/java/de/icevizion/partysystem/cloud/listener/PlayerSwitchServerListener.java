package de.icevizion.partysystem.cloud.listener;

import de.icevizion.partysystem.cloud.PartyCloudPlugin;
import de.icevizion.partysystem.cloud.util.Party;
import net.titan.event.EventHandler;
import net.titan.event.Listener;
import net.titan.event.events.PlayerSwitchServerEvent;
import net.titan.network.spigot.Spigot;
import net.titan.player.CloudPlayer;

import java.util.List;
import java.util.Optional;

/**
 * @author Nico (JumpingPxl) Middendorf
 */

public class PlayerSwitchServerListener implements Listener {

	private PartyCloudPlugin partyPlugin;

	public PlayerSwitchServerListener(PartyCloudPlugin partyPlugin) {
		this.partyPlugin = partyPlugin;
	}

	@EventHandler
	public void onPlayerSwitchServer(PlayerSwitchServerEvent event) {
		Spigot spigotServer = event.getNewServer();
		if (spigotServer.getServerType().toLowerCase().contains("lobby")) {
			return;
		}

		Optional<Party> optionalParty = partyPlugin.getPartyByPlayer(event.getCloudPlayer());
		if (!optionalParty.isPresent()) {
			return;
		}

		Party party = optionalParty.get();
		if (!party.getLeaderUuid().equals(event.getCloudPlayer().getUuid())) {
			return;
		}

		int requiredSlots = 0;
		if (!party.getLeader().hasPermission("network.ignoreplayerlimit")) {
			requiredSlots++;
		}

		List<CloudPlayer> members = party.getMembers();
		for (CloudPlayer cloudMember : members) {
			if (!cloudMember.hasPermission("network.ignoreplayerlimit")) {
				requiredSlots++;
			}
		}

		if ((spigotServer.getPlayerLimit() - spigotServer.getPlayerCount()) < requiredSlots) {
			party.sendMessage(partyPlugin.getLocales(), "switchServerCantSwitch", spigotServer.getDisplayName());
			event.setCancelled(true);
			return;
		}

		party.sendMessage(partyPlugin.getLocales(), "switchServerSwitched", spigotServer.getDisplayName());
		members.forEach(cloudMember -> cloudMember.sendToServer(spigotServer));
	}
}
