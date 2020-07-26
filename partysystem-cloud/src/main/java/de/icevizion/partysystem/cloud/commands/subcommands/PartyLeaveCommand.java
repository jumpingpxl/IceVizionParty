package de.icevizion.partysystem.cloud.commands.subcommands;

import de.icevizion.partysystem.cloud.PartyCloudPlugin;
import de.icevizion.partysystem.cloud.util.Party;
import de.icevizion.partysystem.cloud.util.PartySubCommand;
import net.titan.player.CloudPlayer;

import java.util.List;
import java.util.Optional;

/**
 * @author Nico (JumpingPxl) Middendorf
 */

public class PartyLeaveCommand extends PartySubCommand {

	private final PartyCloudPlugin partyPlugin;

	public PartyLeaveCommand(PartyCloudPlugin partyPlugin) {
		this.partyPlugin = partyPlugin;
	}

	@Override
	public void execute(CloudPlayer cloudPlayer, String label, String[] args) {
		Optional<Party> optionalParty = partyPlugin.getPartyByPlayer(cloudPlayer);
		if (!optionalParty.isPresent()) {
			partyPlugin.getLocales().sendMessage(cloudPlayer, "notInParty");
			return;
		}

		Party party = optionalParty.get();
		if (party.getLeaderUuid().equals(cloudPlayer.getUuid())) {
			List<String> memberUuids = party.getMemberUuids();
			if (memberUuids.isEmpty()) {
				partyPlugin.deleteParty(party);
				return;
			}

			CloudPlayer targetPlayer = partyPlugin.getCloud().getPlayer(memberUuids.get(0));
			if (memberUuids.size() == 1) {
				party.sendMessage(partyPlugin.getLocales(), "partyLeaveLeft", cloudPlayer.getFullDisplayName());
				partyPlugin.deleteParty(party);
				partyPlugin.getLocales().sendMessage(targetPlayer, "partyDeleted");
				partyPlugin.getLocales().sendMessage(cloudPlayer, "partyLeaveSuccess");
				return;
			}

			party.removeMember(targetPlayer);
			party.setLeader(targetPlayer);
			party.sendMessage(partyPlugin.getLocales(), "partyLeaveLeft", cloudPlayer.getFullDisplayName());
			party.sendMessage(partyPlugin.getLocales(), "partyLeavePromoted", targetPlayer.getFullDisplayName(),
					cloudPlayer.getFullDisplayName());
			partyPlugin.getLocales().sendMessage(cloudPlayer, "partyLeaveSuccess");
			return;
		}

		party.removeMember(cloudPlayer);
		party.sendMessage(partyPlugin.getLocales(), "partyLeaveLeft", cloudPlayer.getFullDisplayName());
		partyPlugin.getLocales().sendMessage(cloudPlayer, "partyLeaveSuccess");

		if (party.getMemberUuids().isEmpty()) {
			partyPlugin.getLocales().sendMessage(party.getLeader(), "partyDeleted");
			partyPlugin.deleteParty(party);
		}
	}
}
