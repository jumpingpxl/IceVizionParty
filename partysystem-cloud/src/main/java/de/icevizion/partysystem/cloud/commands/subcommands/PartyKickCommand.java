package de.icevizion.partysystem.cloud.commands.subcommands;

import de.icevizion.partysystem.cloud.PartyCloudPlugin;
import de.icevizion.partysystem.cloud.util.Party;
import de.icevizion.partysystem.cloud.util.PartySubCommand;
import net.titan.player.CloudPlayer;

import java.util.Optional;

/**
 * @author Nico (JumpingPxl) Middendorf
 */

public class PartyKickCommand extends PartySubCommand {

	private final PartyCloudPlugin partyPlugin;

	public PartyKickCommand(PartyCloudPlugin partyPlugin) {
		this.partyPlugin = partyPlugin;
	}

	@Override
	public void execute(CloudPlayer cloudPlayer, String label, String[] args) {
		if (args.length == 0) {
			partyPlugin.getLocales().sendMessage(cloudPlayer, "partyKickArgs");
			return;
		}

		Optional<Party> optionalParty = partyPlugin.getPartyByPlayer(cloudPlayer);
		if (!optionalParty.isPresent()) {
			partyPlugin.getLocales().sendMessage(cloudPlayer, "notInParty");
			return;
		}

		Party party = optionalParty.get();
		if (!party.isLeader(cloudPlayer)) {
			partyPlugin.getLocales().sendMessage(cloudPlayer, "notLeader");
			return;
		}

		CloudPlayer targetPlayer = partyPlugin.getCloud().getPlayer(args[0]);
		if (!isPlayerAvailable(partyPlugin.getLocales(), cloudPlayer, targetPlayer)) {
			return;
		}

		if (targetPlayer.getUuid().equals(cloudPlayer.getUuid())) {
			partyPlugin.getLocales().sendMessage(cloudPlayer, "partyKickSelf");
			return;
		}

		if (!party.getMemberUuids().contains(targetPlayer.getUuid())) {
			partyPlugin.getLocales().sendMessage(cloudPlayer, "partyKickNotInParty", targetPlayer.getFullDisplayName());
			return;
		}

		party.removeMember(targetPlayer);
		party.sendMessage(partyPlugin.getLocales(), "partyKickSuccess", targetPlayer.getFullDisplayName());
		partyPlugin.getLocales().sendMessage(targetPlayer, "partyKickKicked");
	}
}
