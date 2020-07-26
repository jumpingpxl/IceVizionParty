package de.icevizion.partysystem.cloud.commands.subcommands;

import de.icevizion.partysystem.cloud.PartyCloudPlugin;
import de.icevizion.partysystem.cloud.util.Party;
import de.icevizion.partysystem.cloud.util.PartySubCommand;
import net.titan.player.CloudPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Nico (JumpingPxl) Middendorf
 */

public class PartyInvitesCommand extends PartySubCommand {

	private final PartyCloudPlugin partyPlugin;

	public PartyInvitesCommand(PartyCloudPlugin partyPlugin) {
		this.partyPlugin = partyPlugin;
	}

	@Override
	public void execute(CloudPlayer cloudPlayer, String label, String[] args) {
		Optional<Party> optionalParty = partyPlugin.getPartyByPlayer(cloudPlayer);
		if (!optionalParty.isPresent()) {
			List<Party> incomingInvites = partyPlugin.getPartyInvites(cloudPlayer);
			if (incomingInvites.isEmpty()) {
				partyPlugin.getLocales().sendMessage(cloudPlayer, "partyInvitesNoIncoming");
				return;
			}

			partyPlugin.getLocales().sendMessage(cloudPlayer, "partyInvitesIncoming", incomingInvites.size());
			incomingInvites.forEach(party -> partyPlugin.getLocales()
					.sendChatComponent(cloudPlayer, "partyInvitesIncomingInvites", party.getLeader().getFullDisplayName(),
							party.getIdentifier()));
			return;
		}

		Party party = optionalParty.get();
		if (party.getInvites().isEmpty()) {
			partyPlugin.getLocales().sendMessage(cloudPlayer, "partyInvitesNoOutgoing");
			return;
		}

		List<String> invitedPlayers = new ArrayList<>();
		party.getInvitedPlayers().stream().filter(party::isInviteValid).forEach(
				player -> invitedPlayers.add(player.getFullUsername()));
		String listSeparator = partyPlugin.getLocales().getPattern(cloudPlayer, "listSeparator");
		String inviteList = String.join(listSeparator, invitedPlayers.toArray(new String[0]));
		partyPlugin.getLocales().sendMessage(cloudPlayer, "partyInvitesOutgoingInvites", invitedPlayers.size(),
				inviteList);
	}
}
