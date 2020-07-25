package de.icevizion.partysystem.spigot.util;

import de.icevizion.partysystem.core.IParty;
import de.icevizion.partysystem.spigot.PartySpigotPlugin;
import net.titan.lib.database.RedisObject;
import net.titan.spigot.player.CloudPlayer;
import org.redisson.api.RMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Party extends RedisObject implements IParty {

	private final PartySpigotPlugin partyPlugin;
	private final String identifier;

	public Party(PartySpigotPlugin partyPlugin, String identifier) {
		this.partyPlugin = partyPlugin;
		this.identifier = identifier;
	}

	@Override
	public String getIdentifier() {
		return identifier;
	}

	@Override
	public String getLeaderUuid() {
		return getStringFromRedis("leader");
	}

	public CloudPlayer getLeader() {
		return partyPlugin.getCloud().getPlayer(getLeaderUuid());
	}

	@Override
	public List<String> getMemberUuids() {
		return getFromRedis("members", new ArrayList<>());
	}

	public List<CloudPlayer> getMembers() {
		return getMemberUuids().stream().map(uuid -> partyPlugin.getCloud().getPlayer(uuid)).collect(Collectors.toList());
	}

	@Override
	public Map<String, Long> getInvites() {
		return getFromRedis("invited", new HashMap<>());
	}

	@Override
	public boolean isEmpty() {
		return getMemberUuids().isEmpty();
	}

	@Override
	public boolean isActive() {
		return getBooleanFromRedis("active", true);
	}

	@Override
	public boolean isPlayerInvited(String playerUuid) {
		return getInvites().containsKey(playerUuid);
	}

	@Override
	protected RMap<String, Object> getRedisMap() {
		return partyPlugin.getCloud().getCloudRedis().getMap(PartySpigotPlugin.IDENTIFIER + identifier);
	}
}
