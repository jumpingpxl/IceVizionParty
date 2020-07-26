package de.icevizion.partysystem.cloud.commands.subcommands;

import de.icevizion.partysystem.cloud.PartyCloudPlugin;
import de.icevizion.partysystem.cloud.util.Party;
import de.icevizion.partysystem.cloud.util.PartySubCommand;
import net.titan.network.spigot.Spigot;
import net.titan.player.CloudPlayer;

import java.util.Optional;

/**
 * @author Nico (JumpingPxl) Middendorf
 */

public class PartyJumpCommand extends PartySubCommand {

	private final PartyCloudPlugin partyPlugin;

	public PartyJumpCommand(PartyCloudPlugin partyPlugin) {
		this.partyPlugin = partyPlugin;
	}

	@Override
	public void execute(CloudPlayer cloudPlayer, String s, String[] strings) {
		Optional<Party> optionalParty = partyPlugin.getPartyByPlayer(cloudPlayer);
		if (!optionalParty.isPresent()) {
			partyPlugin.getLocales().sendMessage(cloudPlayer, "notInParty");
			return;
		}

		Party party = optionalParty.get();
		CloudPlayer targetPlayer = party.getLeader();
		if (cloudPlayer.getSpigot().getIdentifier().equals(targetPlayer.getSpigot().getIdentifier())) {
			partyPlugin.getLocales().sendMessage(cloudPlayer, "partyJumpAlreadyOnServer", targetPlayer.getFullDisplayName());
			return;
		}

		Spigot spigot = targetPlayer.getSpigot();
		partyPlugin.getLocales().sendMessage(cloudPlayer, "partyJumpJumped", spigot.getDisplayName(),
				targetPlayer.getFullDisplayName());
		cloudPlayer.sendToServer(spigot);
	}
}
