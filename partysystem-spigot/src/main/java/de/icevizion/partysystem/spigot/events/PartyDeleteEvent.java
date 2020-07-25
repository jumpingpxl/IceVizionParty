package de.icevizion.partysystem.spigot.events;

import de.icevizion.partysystem.spigot.util.Party;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PartyDeleteEvent extends Event {

	private final Party party;
	private final HandlerList handlers;

	public PartyDeleteEvent(Party party) {
		this.party = party;
		this.handlers = new HandlerList();
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public Party getParty() {
		return party;
	}
}
