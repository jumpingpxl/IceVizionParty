package de.icevizion.partysystem.cloud;

import de.icevizion.partysystem.cloud.commands.PartyCommand;
import de.icevizion.partysystem.cloud.commands.subcommands.PartyMessageCommand;
import de.icevizion.partysystem.cloud.util.Locales;
import de.icevizion.partysystem.cloud.util.Party;
import net.titan.Cloud;
import net.titan.player.CloudPlayer;
import net.titan.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class PartyCloudPlugin extends Plugin {

	private final String redisIdentifier = "Party:";
	private Cloud cloud;
	private Locales locales;

	@Override
	public void onLoad() {
		//TODO -> Remove Singleton Pattern.
		cloud = Cloud.getInstance();

		locales = new Locales(this);

		cloud.registerCommand("party", new PartyCommand(this));
		cloud.registerCommand("p", new PartyMessageCommand(this));
	}

	@Override
	public void onUnload() {

	}

	@Override
	public String getPluginName() {
		return "PartySystem";
	}

	public Cloud getCloud() {
		return cloud;
	}

	public Locales getLocales() {
		return locales;
	}

	public List<Party> getParties() {
		return cloud.getCloudRedis().getKeys().findKeysByPattern(redisIdentifier + "*").stream().map(
				id -> new Party(this, id.replace(redisIdentifier, ""))).collect(Collectors.toList());
	}

	public Optional<Party> getPartyByPlayer(CloudPlayer player) {
		for (Party parties : getParties()) {
			if (parties.isLeader(player) || parties.getMemberUuids().contains(player.getUuid())) {
				return Optional.of(parties);
			}
		}

		return Optional.empty();
	}

	public Optional<Party> getPartyByIdentifier(Object identifier) {
		for (Party parties : getParties()) {
			if (parties.getIdentifier().equals(identifier)) {
				return Optional.of(parties);
			}
		}

		return Optional.empty();
	}

	public List<Party> getPartyInvites(CloudPlayer cloudPlayer) {
		List<Party> invites = new ArrayList<>();
		getParties().stream().filter(party -> party.getInvites().containsKey(cloudPlayer.getUuid())).forEach(invites::add);
		return invites;
	}

	public Party createParty(CloudPlayer leader) {
		int identifier;
		do {
			identifier = (int) (Math.random() * 10000);
		} while (cloud.getCloudRedis().getKeys().countExists(redisIdentifier + identifier) > 0);

		Party party = new Party(this, String.valueOf(identifier), leader);
		cloud.getCloudRedis().getKeys().expire(redisIdentifier + identifier, party.getMillisecondsUntilInviteExpires(),
				TimeUnit.MILLISECONDS);
		return party;
	}

	public void deleteParty(Party party) {
		cloud.getCloudRedis().getKeys().delete(redisIdentifier + party.getIdentifier());
	}
}
