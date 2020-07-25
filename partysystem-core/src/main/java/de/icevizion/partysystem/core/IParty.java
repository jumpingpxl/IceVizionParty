package de.icevizion.partysystem.core;

import java.util.List;
import java.util.Map;

public interface IParty {

	String getIdentifier();

	String getLeaderUuid();

	List<String> getMemberUuids();

	Map<String, Long> getInvites();

	boolean isEmpty();

	boolean isActive();

	boolean isPlayerInvited(String playerUuid);
}
