package de.icevizion.partysystem.cloud.commands;

import de.icevizion.partysystem.cloud.PartyCloudPlugin;
import de.icevizion.partysystem.cloud.commands.subcommands.*;
import de.icevizion.partysystem.cloud.util.PartySubCommand;
import net.titan.player.CloudPlayer;
import net.titan.player.command.Command;

import java.util.*;

/**
 * @author Nico (JumpingPxl) Middendorf
 */

public class PartyCommand extends Command {

	private final PartyCloudPlugin partyPlugin;
	private Map<String, PartySubCommand> subCommands;

	public PartyCommand(PartyCloudPlugin partyPlugin) {
		this.partyPlugin = partyPlugin;
		loadSubCommands();
	}

	@Override
	public void execute(CloudPlayer cloudPlayer, String label, String[] args) {
		if (args.length == 0) {
			partyPlugin.getLocales().sendMessage(cloudPlayer, "partyArgs");
			return;
		}

		PartySubCommand subCommand = subCommands.get(args[0].toLowerCase());
		if (Objects.isNull(subCommand) || !subCommand.hasPermission(cloudPlayer)) {
			partyPlugin.getLocales().sendMessage(cloudPlayer, "partyArgs");
			return;
		}

		String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
		subCommand.execute(cloudPlayer, args[0], subArgs);
	}

	@Override
	public List<String> tabComplete(CloudPlayer cloudPlayer, String[] args) {
		List<String> tabComplete = new ArrayList<>();
		if (args.length == 1) {
			subCommands.entrySet().stream().filter(entry -> entry.getValue().hasPermission(cloudPlayer)).forEach(
					entry -> tabComplete.add(entry.getKey()));
		} else if (args.length != 0) {
			PartySubCommand subCommand = subCommands.get(args[0].toLowerCase());
			if (Objects.nonNull(subCommand) && subCommand.hasPermission(cloudPlayer)) {
				return subCommand.onTabComplete(cloudPlayer, args);
			}
		} else {
			tabComplete.add("party");
		}

		return tabComplete;
	}

	private void loadSubCommands() {
		subCommands = new HashMap<>();
		subCommands.put("accept", new PartyAcceptCommand(partyPlugin));
		subCommands.put("delete", new PartyDeleteCommand(partyPlugin));
		subCommands.put("deny", new PartyDenyCommand(partyPlugin));
		subCommands.put("forceadd", new PartyForceAddCommand(partyPlugin));
		subCommands.put("forceinvite", new PartyForceInviteCommand(partyPlugin));
		subCommands.put("invites", new PartyInvitesCommand(partyPlugin));
		subCommands.put("invite", new PartyInviteCommand(partyPlugin));
		subCommands.put("jump", new PartyJumpCommand(partyPlugin));
		subCommands.put("kick", new PartyKickCommand(partyPlugin));
		subCommands.put("leave", new PartyLeaveCommand(partyPlugin));
		subCommands.put("list", new PartyListCommand(partyPlugin));
		subCommands.put("message", new PartyMessageCommand(partyPlugin));
		subCommands.put("msg", new PartyMessageCommand(partyPlugin));
		subCommands.put("promote", new PartyPromoteCommand(partyPlugin));
	}
}
