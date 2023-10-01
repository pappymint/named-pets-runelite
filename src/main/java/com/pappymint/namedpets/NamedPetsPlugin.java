package com.pappymint.namedpets;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.MenuOpened;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.game.chatbox.ChatboxPanelManager;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.ImageUtil;
import javax.inject.Inject;
import java.awt.image.BufferedImage;
import java.util.Objects;

@Slf4j
@PluginDescriptor(
	name = "Named Pets",
	description = "Right click & name your fluffy friend!",
	tags = {"pet"}
)
public class NamedPetsPlugin extends Plugin
{
	private static final String CONFIG_GROUP = "namedPets";
	private static final BufferedImage SidePanelIcon = ImageUtil.loadImageResource(NamedPetsPlugin.class, "icon.png");
	private NavigationButton sideButton;

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
	@Inject
	private ClientToolbar clientToolbar;

	@Override
	protected void startUp() throws Exception
	{
		// Overlay manager renders the names above the pet
		overlayManager.add(petNameOverlay);

		// Panel is the menu item along the side to configure & view set pet names
		NamedPetsPanel panel = new NamedPetsPanel(this);
		sideButton = NavigationButton.builder()
				.tooltip("Named Pets")
				.icon(SidePanelIcon)
				.priority(9)
				.panel(panel)
				.build();
		clientToolbar.addNavigation(sideButton);
	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(petNameOverlay);
		clientToolbar.removeNavigation(sideButton);
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