package de.icevizion.partysystem.cloud.commands.subcommands;

import de.icevizion.partysystem.cloud.PartyCloudPlugin;
import de.icevizion.partysystem.cloud.util.Party;
import de.icevizion.partysystem.cloud.util.PartySubCommand;
import net.titan.player.CloudPlayer;

import java.util.Optional;

/**
 * @author Nico (JumpingPxl) Middendorf
 */

public class PartyAcceptCommand extends PartySubCommand {

	private final PartyCloudPlugin partyPlugin;

	public PartyAcceptCommand(PartyCloudPlugin partyPlugin) {
		this.partyPlugin = partyPlugin;
	}

	@Override
	public void execute(CloudPlayer cloudPlayer, String label, String[] args) {
		if (args.length == 0) {
			partyPlugin.getLocales().sendMessage(cloudPlayer, "partyAcceptArgs");
			return;
		}

		Optional<Party> optionalParty = partyPlugin.getPartyByPlayer(cloudPlayer);
		if (optionalParty.isPresent()) {
			partyPlugin.getLocales().sendMessage(cloudPlayer, "partyAcceptInOtherParty");
			return;
		}

		if (args.length == 2 && args[0].equalsIgnoreCase("direct")) {
			Optional<Party> optionalDirectParty = partyPlugin.getPartyByIdentifier(args[1]);
			acceptInvite(optionalDirectParty, cloudPlayer);
			return;
		}

		CloudPlayer targetPlayer = partyPlugin.getCloud().getPlayer(args[0]);
		if (!isPlayerAvailable(partyPlugin.getLocales(), cloudPlayer, targetPlayer)) {
			return;
		}

		if (targetPlayer.getUuid().equals(cloudPlayer.getUuid())) {
			partyPlugin.getLocales().sendMessage(cloudPlayer, "partyAcceptSelf");
			return;
		}

		Optional<Party> optionalTargetParty = partyPlugin.getPartyByPlayer(targetPlayer);
		acceptInvite(optionalTargetParty, cloudPlayer);
	}

	private void acceptInvite(Optional<Party> optionalParty, CloudPlayer cloudPlayer) {
		if (!optionalParty.isPresent() || !optionalParty.get().isPlayerInvited(cloudPlayer)) {
			partyPlugin.getLocales().sendMessage(cloudPlayer, "partyAcceptNoInvite");
			return;
		}

		Party targetParty = optionalParty.get();
		if (!targetParty.isInviteValid(cloudPlayer)) {
			partyPlugin.getLocales().sendMessage(cloudPlayer, "partyAcceptInviteExpired");
			return;
		}

		targetParty.addMember(cloudPlayer.getUuid());
		targetParty.sendMessage(partyPlugin.getLocales(), "partyAcceptAccepted", cloudPlayer.getFullDisplayName());
	}
}
