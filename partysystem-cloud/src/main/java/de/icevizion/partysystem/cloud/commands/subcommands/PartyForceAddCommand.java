package de.icevizion.partysystem.cloud.commands.subcommands;

import de.icevizion.partysystem.cloud.PartyCloudPlugin;
import de.icevizion.partysystem.cloud.util.Party;
import de.icevizion.partysystem.cloud.util.PartySubCommand;
import net.titan.player.CloudPlayer;

import java.util.Optional;

/**
 * @author Nico (JumpingPxl) Middendorf
 */

public class PartyForceAddCommand extends PartySubCommand {

	private final PartyCloudPlugin partyPlugin;

	public PartyForceAddCommand(PartyCloudPlugin partyPlugin) {
		this.partyPlugin = partyPlugin;
	}

	@Override
	public boolean hasPermission(CloudPlayer cloudPlayer) {
		return cloudPlayer.hasPermission("party.admin");
	}

	@Override
	public void execute(CloudPlayer cloudPlayer, String label, String[] args) {
		if (args.length == 0) {
			partyPlugin.getLocales().sendMessage(cloudPlayer, "partyForceAddArgs");
			return;
		}

		Optional<Party> optionalParty = partyPlugin.getPartyByPlayer(cloudPlayer);
		if (!optionalParty.isPresent()) {
			partyPlugin.getLocales().sendMessage(cloudPlayer, "notInParty");
			return;
		}

		for (String target : args) {
			CloudPlayer targetPlayer = partyPlugin.getCloud().getPlayer(target);
			if (!isPlayerAvailable(partyPlugin.getLocales(), cloudPlayer, targetPlayer)) {
				return;
			}

			if (targetPlayer.getUuid().equals(cloudPlayer.getUuid())) {
				partyPlugin.getLocales().sendMessage(cloudPlayer, "partyForceAddSelf");
				return;
			}

			Party party = optionalParty.get();
			if (party.getMemberUuids().contains(targetPlayer.getUuid())) {
				partyPlugin.getLocales().sendMessage(cloudPlayer, "partyForceAddAlreadyInParty",
						targetPlayer.getFullDisplayName());
				return;
			}

			Optional<Party> optionalTargetParty = partyPlugin.getPartyByPlayer(targetPlayer);
			if (optionalTargetParty.isPresent()) {
				partyPlugin.getLocales().sendMessage(cloudPlayer, "partyForceAddInOtherParty",
						targetPlayer.getFullDisplayName());
				return;
			}

			party.sendMessage(partyPlugin.getLocales(), "partyForceAddSuccess", targetPlayer.getFullDisplayName());
			partyPlugin.getLocales().sendMessage(targetPlayer, "partyForceAddAdded", party.getLeader().getFullDisplayName(),
					cloudPlayer.getFullDisplayName());
			party.addMember(targetPlayer.getUuid());
			if (party.getInvites().containsKey(targetPlayer.getUuid().toLowerCase())) {
				party.removeInvite(targetPlayer.getUuid());
			}
		}
	}
}
