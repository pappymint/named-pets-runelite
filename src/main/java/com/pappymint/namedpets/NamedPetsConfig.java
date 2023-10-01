package com.pappymint.namedpets;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

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
}
