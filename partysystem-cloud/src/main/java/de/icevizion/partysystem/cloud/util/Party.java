package de.icevizion.partysystem.cloud.util;

import de.icevizion.partysystem.cloud.PartyCloudPlugin;
import de.icevizion.partysystem.core.IParty;
import net.titan.cloudcore.database.Id;
import net.titan.cloudcore.database.MemoryObject;
import net.titan.cloudcore.i18n.Translator;
import net.titan.player.CloudPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Party extends MemoryObject implements IParty {

	private final long millisecondsUntilInviteExpires = 1000L * 60L * 5L;

	@Id
	protected final String identifier;
	private final PartyCloudPlugin partyPlugin;
	private final ScheduledTask taskAfterIdle;

	public Party(PartyCloudPlugin partyPlugin, String identifier) {
		super("Party", partyPlugin.getCloud().getCloudRedis());
		this.identifier = identifier;
		this.partyPlugin = partyPlugin;
		taskAfterIdle = new ScheduledTask(() -> partyPlugin.getLocales().sendMessage(getLeader(), "partyDeleted"));
	}

	public Party(PartyCloudPlugin partyPlugin, String identifier, CloudPlayer leader) {
		this(partyPlugin, identifier);
		setLeader(leader.getUuid());
	}

	public String getIdentifier() {
		return identifier;
	}

	public String getLeaderUuid() {
		return String.valueOf(getData("leader"));
	}

	public CloudPlayer getLeader() {
		return partyPlugin.getCloud().getPlayer(getLeaderUuid());
	}

	public long getMillisecondsUntilInviteExpires() {
		return millisecondsUntilInviteExpires;
	}

	public List<String> getMemberUuids() {
		return getData("members", new ArrayList<>());
	}

	public List<CloudPlayer> getMembers() {
		return getMemberUuids().stream().map(uuid -> partyPlugin.getCloud().getPlayer(uuid)).collect(Collectors.toList());
	}

	public Map<String, Long> getInvites() {
		return getData("invites", new HashMap<>());
	}

	public List<CloudPlayer> getInvitedPlayers() {
		return getInvites().keySet().stream().map(uuid -> partyPlugin.getCloud().getPlayer(uuid)).collect(
				Collectors.toList());
	}

	public boolean isEmpty() {
		return getMemberUuids().isEmpty();
	}

	public boolean isActive() {
		return getData("active", false);
	}

	public boolean isLeader(CloudPlayer cloudPlayer) {
		return getLeaderUuid().equals(cloudPlayer.getUuid());
	}

	public boolean isPlayerInvited(String playerUuid) {
		return getInvites().containsKey(playerUuid);
	}

	public boolean isPlayerInvited(CloudPlayer cloudPlayer) {
		return isPlayerInvited(cloudPlayer.getUuid());
	}

	public boolean isInviteValid(CloudPlayer cloudPlayer) {
		Map<String, Long> invites = getInvites();
		return invites.containsKey(cloudPlayer.getUuid()) && invites.get(cloudPlayer.getUuid())
				> System.currentTimeMillis();
	}

	public void setLeader(String playerUuid) {
		setData("leader", playerUuid);
	}

	public void setLeader(CloudPlayer cloudPlayer) {
		setLeader(cloudPlayer.getUuid());
	}

	public void setActive(boolean active) {
		setData("active", active);
		if (active) {
			clearExpire();
			taskAfterIdle.cancel();
		} else {
			expire(millisecondsUntilInviteExpires, TimeUnit.MILLISECONDS);
			taskAfterIdle.cancel();
			taskAfterIdle.delay(millisecondsUntilInviteExpires, TimeUnit.MILLISECONDS);
		}
	}

	public void addMember(String playerUuid) {
		List<String> memberUuids = getMemberUuids();
		memberUuids.add(playerUuid);
		setData("members", memberUuids);
	}

	public void addMember(CloudPlayer cloudPlayer) {
		addMember(cloudPlayer.getUuid());
	}

	public void addInvite(String playerUuid) {
		Map<String, Long> invites = getInvites();
		invites.put(playerUuid, System.currentTimeMillis() + millisecondsUntilInviteExpires);
		setData("invites", invites);
	}

	public void addInvite(CloudPlayer cloudPlayer) {
		addInvite(cloudPlayer.getUuid());
	}

	public void removeMember(String playerUuid) {
		List<String> memberUuids = getMemberUuids();
		memberUuids.remove(playerUuid);
		setData("members", memberUuids);
	}

	public void removeMember(CloudPlayer cloudPlayer) {
		removeMember(cloudPlayer.getUuid());
	}

	public void removeInvite(String playerUuid) {
		Map<String, Long> invites = getInvites();
		invites.remove(playerUuid);
		setData("invites", invites);
	}

	public void removeInvite(CloudPlayer cloudPlayer) {
		removeInvite(cloudPlayer.getUuid());
	}

	public void sendMessage(Translator translator, String key, Object... arguments) {
		translator.sendMessage(getLeader(), key, arguments);
		getMembers().forEach(member -> translator.sendMessage(member, key, arguments));
	}
}
