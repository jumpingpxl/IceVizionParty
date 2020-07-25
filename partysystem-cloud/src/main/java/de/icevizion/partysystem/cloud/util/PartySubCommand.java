package de.icevizion.partysystem.cloud.util;

import net.titan.cloudcore.i18n.Translator;
import net.titan.player.CloudPlayer;
import net.titan.player.command.Command;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author Nico (JumpingPxl) Middendorf
 */

public abstract class PartySubCommand extends Command {

	public List<String> onTabComplete(CloudPlayer cloudPlayer, String[] args) {
		return Collections.emptyList();
	}

	public boolean hasPermission(CloudPlayer cloudPlayer) {
		return true;
	}

	protected boolean isPlayerAvailable(Translator translator, CloudPlayer executor, CloudPlayer targetPlayer) {
		if (Objects.isNull(targetPlayer)) {
			translator.sendMessage(executor, "notExisting");
			return false;
		}

		if (!targetPlayer.isOnline()) {
			translator.sendMessage(executor, "notOnline");
			return false;
		}

		return true;
	}
}
