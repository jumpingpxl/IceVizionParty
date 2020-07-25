package de.icevizion.partysystem.core;

import net.titan.protocol.NettyPacket;

public class PartyInfoPacket extends NettyPacket {

	private int partyId;
	private PartyAction partyAction;

	public PartyInfoPacket(int partyId, PartyAction partyAction) {
		this.partyId = partyId;
		this.partyAction = partyAction;
	}

	@Override
	public void write() {
		writeInt(partyId);
		writeString(partyAction.name());
	}

	@Override
	public void read() {
		partyId = readInt();
		String name = readString();
		if (name != null) {
			partyAction = PartyAction.valueOf(name);
		}
	}

	public int getPartyId() {
		return partyId;
	}

	public PartyAction getPartyAction() {
		return partyAction;
	}
}