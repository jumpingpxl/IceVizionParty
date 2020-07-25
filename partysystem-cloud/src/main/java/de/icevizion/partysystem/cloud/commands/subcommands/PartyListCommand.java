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

public class PartyListCommand extends PartySubCommand {

	private final PartyCloudPlugin partyPlugin;

	public PartyListCommand(PartyCloudPlugin partyPlugin) {
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
		List<String> partyMembers = new ArrayList<>();
		party.getMembers().forEach(player -> partyMembers.add(player.getFullUsername()));
		String listSeparator = partyPlugin.getLocales().getPattern(cloudPlayer, "listSeparator");
		String partyList = String.join(listSeparator, partyMembers.toArray(new String[0]));
		partyPlugin.getLocales().sendMessage(cloudPlayer, "partyListList", party.getLeader().getFullDisplayName(),
				partyMembers.size(), partyList);
	}
}
