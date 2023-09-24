package com.pappymint.namedpets;

import com.google.common.base.Strings;
import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.game.chatbox.ChatboxPanelManager;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.api.events.MenuOpened;

import java.util.Objects;

@Slf4j
@PluginDescriptor(
	name = "Named Pets",
	description = "Name your followed pets by right-clicking on them",
	tags = {"pet"}
)
public class NamedPetsPlugin extends Plugin
{
	private static final String CONFIG_GROUP = "namedPets";

	@Inject
	private Client client;

	@Inject
	private NamedPetsConfig config;

	@Inject
	private ChatboxPanelManager chatboxPanelManager;

	@Inject
	private ConfigManager configManager;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private PetNameOverlay petNameOverlay;

	@Override
	protected void startUp() throws Exception
	{
		overlayManager.add(petNameOverlay);
	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(petNameOverlay);
	}

	@Subscribe
	public void onMenuOpened(MenuOpened menuOpened)
	{
		checkIfMenuOptionsBelongToFollower(menuOpened.getMenuEntries());
	}

	private void checkIfMenuOptionsBelongToFollower(MenuEntry[] menuEntries)
	{
		NPC myFollowerNPC = client.getFollower();
		if (myFollowerNPC == null) {
			return;
		}

		for (int entryIndex = 0; entryIndex < menuEntries.length; entryIndex++) {
			MenuEntry entry = menuEntries[entryIndex];

			NPC targetedNpc = entry.getNpc();

			if (targetedNpc != null && targetedNpc.getId() == myFollowerNPC.getId()) {
				// Add menu entry at index below this option - e.g. "Name Overgrown Hellcat"
				addNamePetMenuOption(targetedNpc, entryIndex, entry);
				break;
			}
		}
	}

	private void addNamePetMenuOption(NPC pet, int index, MenuEntry menuEntry) {
		client.createMenuEntry(index)
				.setTarget(menuEntry.getTarget())
				.setOption("Name")
				.onClick(e -> onNameMenuEntryOptionClicked(pet));
	}

	private void onNameMenuEntryOptionClicked(NPC pet) {
		chatboxPanelManager.openTextInput("Name your " + pet.getName())
				.value("")
				.onDone((input) ->
				{
					savePetName(pet.getId(), input);
				})
				.build();
	}

	private void savePetName(int petNpcId, String petName)
	{
		log.info("Save pet: " + petName + " (ID: " + petNpcId + ")");

		if (Objects.equals(petName, "") || petName == null) {
			configManager.unsetConfiguration(CONFIG_GROUP, String.valueOf(petNpcId));
		} else {
			configManager.setConfiguration(CONFIG_GROUP, String.valueOf(petNpcId), petName);
		}
	}

	@Provides
	NamedPetsConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(NamedPetsConfig.class);
	}
}