package de.icevizion.partysystem.spigot;

import de.icevizion.partysystem.spigot.listener.CloudPacketReceivedListener;
import de.icevizion.partysystem.spigot.util.Party;
import net.titan.spigot.Cloud;
import net.titan.spigot.player.CloudPlayer;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class PartySpigotPlugin extends JavaPlugin {

	public static String IDENTIFIER = "Party:";
	private final Map<String, Party> cachedParties = new HashMap<>();
	private Cloud cloud;

	@Override
	public void onEnable() {
		//TODO -> Remove Singleton Pattern.
		cloud = Cloud.getInstance();

		loadExistingParties();
		getServer().getPluginManager().registerEvents(new CloudPacketReceivedListener(this), this);
	}

	public Map<String, Party> getCachedParties() {
		return cachedParties;
	}

	public Cloud getCloud() {
		return cloud;
	}

	public Optional<Party> getPartyByMember(CloudPlayer cloudPlayer) {
		for (Party party : cachedParties.values()) {
			if (party.getLeaderUuid().equals(cloudPlayer.getUuid())) {
				return Optional.of(party);
			}

			for (String memberUuids : party.getMemberUuids()) {
				if (memberUuids.equals(cloudPlayer.getUuid())) {
					return Optional.of(party);
				}
			}
		}

		return Optional.empty();
	}

	public void loadExistingParties() {
		cloud.getCloudRedis().getKeys().findKeysByPattern(IDENTIFIER + "*").forEach(parties -> {
			Party party = new Party(this, parties.replace(IDENTIFIER, ""));
			cachedParties.put(party.getIdentifier(), party);
		});
	}
}
