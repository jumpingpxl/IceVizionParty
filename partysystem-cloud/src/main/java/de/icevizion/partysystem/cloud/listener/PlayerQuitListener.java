package de.icevizion.partysystem.cloud.listener;

import de.icevizion.partysystem.cloud.PartyCloudPlugin;
import de.icevizion.partysystem.cloud.util.Party;
import net.titan.event.EventHandler;
import net.titan.event.Listener;
import net.titan.event.events.PlayerQuitEvent;
import net.titan.player.CloudPlayer;

import java.util.List;
import java.util.Optional;

/**
 * @author Nico (JumpingPxl) Middendorf
 */

public class PlayerQuitListener implements Listener {

	private PartyCloudPlugin partyPlugin;

	public PlayerQuitListener(PartyCloudPlugin partyPlugin) {
		this.partyPlugin = partyPlugin;
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Optional<Party> optionalParty = partyPlugin.getPartyByPlayer(event.getCloudPlayer());
		if (optionalParty.isPresent()) {
			return;
		}

		Party party = optionalParty.get();
		List<String> memberUuids = party.getMemberUuids();
		if (party.getLeaderUuid().equals(event.getCloudPlayer().getUuid())) {
			if (memberUuids.isEmpty()) {
				partyPlugin.deleteParty(party);
				return;
			}

			CloudPlayer targetPlayer = partyPlugin.getCloud().getPlayer(memberUuids.get(0));
			if (memberUuids.size() == 1) {
				partyPlugin.getLocales().sendMessage(targetPlayer, "partyDeleted");
				partyPlugin.deleteParty(party);
				return;
			}

			party.removeMember(targetPlayer);
			party.setLeader(targetPlayer);
			party.sendMessage(partyPlugin.getLocales(), "partyLeavePromoted", targetPlayer.getFullDisplayName(),
					event.getCloudPlayer().getFullDisplayName());
			return;
		}

		party.removeMember(event.getCloudPlayer());
		party.sendMessage(partyPlugin.getLocales(), "partyLeaveLeft", event.getCloudPlayer().getFullDisplayName());

		if (memberUuids.isEmpty()) {
			partyPlugin.getLocales().sendMessage(party.getLeader(), "partyDeleted");
			partyPlugin.deleteParty(party);
		}
	}
}
