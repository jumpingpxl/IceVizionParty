package de.icevizion.partysystem.cloud.commands.subcommands;

import de.icevizion.partysystem.cloud.PartyCloudPlugin;
import de.icevizion.partysystem.cloud.util.Party;
import de.icevizion.partysystem.cloud.util.PartySubCommand;
import net.titan.player.CloudPlayer;

import java.util.Optional;

/**
 * @author Nico (JumpingPxl) Middendorf
 */

public class PartyDeleteCommand extends PartySubCommand {

	private final PartyCloudPlugin partyPlugin;

	public PartyDeleteCommand(PartyCloudPlugin partyPlugin) {
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
		if (!party.isLeader(cloudPlayer)) {
			partyPlugin.getLocales().sendMessage(cloudPlayer, "notLeader");
			return;
		}

		if (args.length == 0 || !args[0].equalsIgnoreCase("confirm")) {
			partyPlugin.getLocales().sendMessage(cloudPlayer, "partyDeleteConfirm");
			return;
		}

		party.sendMessage(partyPlugin.getLocales(), "partyDeleteSuccess");
		partyPlugin.deleteParty(party);
	}
}
