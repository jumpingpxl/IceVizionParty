package de.icevizion.partysystem.cloud.util;

import de.icevizion.partysystem.cloud.PartyCloudPlugin;
import net.titan.cloudcore.i18n.Translator;

import java.util.Locale;

/**
 * @author Nico (JumpingPxl) Middendorf
 */

public class Locales extends Translator {

	public Locales(PartyCloudPlugin partyPlugin) {
		super(partyPlugin.getClass().getClassLoader(), "party");
	}

	@Override
	public void loadLocales() {
		addResourceBundle(Locale.GERMAN);
		addResourceBundle(Locale.ENGLISH);
	}
}
