package com.pappymint.namedpets;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

import java.awt.Color;

@ConfigGroup("namedpets")
public interface NamedPetsConfig extends Config
{
	@ConfigItem(
		keyName = "position",
		name = "Adjust Name Position",
		description = "Adjust the position the name above the pet (0 - 100)"
	)
	default int getCustomPosition()
	{
		return 0;
	}

	@ConfigItem(
			keyName = "defaultNameColor",
			name = "Pet Name Color",
			description = "A default color for pet names. If you individually set a color for a pet name, that will be set first."
	)
	default Color getDefaultPetNameColor() { return new Color(255, 255, 255); }

	@ConfigItem(
			keyName = "enablePOHPetNames",
			name = "Show names over POH pets",
			description = "Be able to view and name pets in your player owned home"
	)
	default boolean petNamesPOHEnabled () { return true; }
}
