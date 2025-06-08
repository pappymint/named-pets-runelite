package com.pappymint.namedpets;

import net.runelite.client.config.*;

import java.awt.Color;

@ConfigGroup("namedpets")
public interface NamedPetsConfig extends Config
{
	@Range(
			min = 0,
			max = 100
	)
	@ConfigItem(
		keyName = "position",
		name = "Adjust Name Position",
		description = "Adjust position of name above the pet."
	)
	default int getCustomPosition()
	{
		return 0;
	}

	@ConfigItem(
			keyName = "defaultNameColor",
			name = "Default Pet Name Color",
			description = "A default color for pet names. If you individually set a color for a pet name, that will be set first."
	)
	default Color getDefaultPetNameColor() { return new Color(255, 255, 255); }

	@Range(
			min = 10,
			max = 40
	)
	@ConfigItem(
			keyName = "petNameFontSize",
			name = "Font Size",
			description = "Font size for pet name display."
	)
	default int petNameFontSize() { return 16; }

	@ConfigItem(
			keyName = "enablePOHPetNames",
			name = "Name POH pets",
			description = "View name above pets in your player owned home."
	)
	default boolean petNamesPOHEnabled () { return true; }

	@ConfigItem(
			keyName = "replaceMenuPetname",
			name = "Replace Menu Option Pet Name",
			description = "Replace the right-click menu option NPC name with the pet name."
	)
	default boolean replaceMenuPetName () { return false; }
}
