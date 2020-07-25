package de.icevizion.partysystem.cloud.commands.subcommands;

import de.icevizion.partysystem.cloud.PartyCloudPlugin;
import de.icevizion.partysystem.cloud.util.Party;
import de.icevizion.partysystem.cloud.util.PartySubCommand;
import net.titan.player.CloudPlayer;

import java.util.Optional;

/**
 * @author Nico (JumpingPxl) Middendorf
 */

public class PartyForceInviteCommand extends PartySubCommand {

	private final PartyCloudPlugin partyPlugin;

	public PartyForceInviteCommand(PartyCloudPlugin partyPlugin) {
		this.partyPlugin = partyPlugin;
	}

	@Override
	public boolean hasPermission(CloudPlayer cloudPlayer) {
		return cloudPlayer.hasPermission("party.admin");
	}

	@Override
	public void execute(CloudPlayer cloudPlayer, String label, String[] args) {
		if (args.length == 0) {
			partyPlugin.getLocales().sendMessage(cloudPlayer, "partyForceInviteArgs");
			return;
		}

		for (String target : args) {
			CloudPlayer targetPlayer = partyPlugin.getCloud().getPlayer(args[1]);
			if (!isPlayerAvailable(partyPlugin.getLocales(), cloudPlayer, targetPlayer)) {
				return;
			}

			if (targetPlayer.getUuid().equals(cloudPlayer.getUuid())) {
				partyPlugin.getLocales().sendMessage(cloudPlayer, "partyForceInviteSelf");
				return;
			}

			Optional<Party> optionalParty = partyPlugin.getPartyByPlayer(cloudPlayer);
			if (!optionalParty.isPresent()) {
				partyPlugin.getLocales().sendMessage(cloudPlayer, "notInParty");
				return;
			}

			Party party = optionalParty.get();
			if (party.getMemberUuids().contains(targetPlayer.getUuid())) {
				partyPlugin.getLocales().sendMessage(cloudPlayer, "partyForceInviteAlreadyInParty",
						targetPlayer.getFullDisplayName());
				return;
			}

			if (party.getInvites().containsKey(cloudPlayer.getUuid())) {
				partyPlugin.getLocales().sendMessage(cloudPlayer, "partyForceInviteAlreadyInvited",
						targetPlayer.getFullDisplayName());
				return;
			}

			Optional<Party> optionalTargetParty = partyPlugin.getPartyByPlayer(targetPlayer);
			if (optionalTargetParty.isPresent()) {
				partyPlugin.getLocales().sendMessage(cloudPlayer, "partyForceInviteInOtherParty",
						targetPlayer.getFullDisplayName());
				return;
			}

			party.addInvite(targetPlayer.getUuid());
			party.sendMessage(partyPlugin.getLocales(), "partyForceInviteSuccess", targetPlayer.getFullDisplayName(),
					cloudPlayer.getFullDisplayName());
			partyPlugin.getLocales().sendMessage(targetPlayer, "invited", cloudPlayer.getFullDisplayName());
			partyPlugin.getLocales().sendChatComponent(targetPlayer, "invitedAcceptDeny", party.getIdentifier());
		}
	}
}
