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
import net.runelite.client.ui.components.colorpicker.ColorPickerManager;
import net.runelite.client.ui.components.colorpicker.RuneliteColorPicker;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.ImageUtil;
import javax.inject.Inject;
import javax.swing.*;
import java.applet.Applet;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.util.Objects;

@Slf4j
@PluginDescriptor(
	name = "Named Pets",
	description = "Right click & name your fluffy friend!",
	tags = {"pet"}
)
public class NamedPetsPlugin extends Plugin
{
	private static final BufferedImage SidePanelIcon = ImageUtil.loadImageResource(NamedPetsPlugin.class, "icon.png");
	private NavigationButton sideButton;
	private NamedPetsPanel panel;
	private NamedPetsConfigManager pluginConfigManager;

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
	@Inject
	private ColorPickerManager colorPickerManager;

	@Override
	protected void startUp() throws Exception {
		// Overlay manager renders the names above the pet
		pluginConfigManager = new NamedPetsConfigManager(this, configManager);

		// Panel is the menu item along the side to configure & view set pet names
		if (!config.hidePetPanel()) {
			panel = new NamedPetsPanel(this, config, pluginConfigManager);
			sideButton = NavigationButton.builder()
					.tooltip("Named Pets")
					.icon(SidePanelIcon)
					.priority(9)
					.panel(panel)
					.build();
			clientToolbar.addNavigation(sideButton);
		}

		overlayManager.add(petNameOverlay);
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
				addColorNameMenuOption(targetedNpc, entryIndex, entry);
				break;
			}
		}
	}

	private void addNamePetMenuOption(NPC pet, int index, MenuEntry menuEntry) {
		client.createMenuEntry(index)
				.setOption("Name")
				.setTarget(menuEntry.getTarget())
				.setType(MenuAction.RUNELITE)
				.onClick(e -> onNameMenuEntryOptionClicked(pet));
	}

	private void addColorNameMenuOption(NPC pet, int index, MenuEntry menuEntry) {
		if (!getExistingPetName(pet.getId()).isEmpty()) {
			MenuEntry nameColorEntry = client.createMenuEntry(index)
					.setOption("Color Name")
					.setTarget(menuEntry.getTarget())
					.setType(MenuAction.RUNELITE_SUBMENU);

			client.createMenuEntry(index)
					.setOption("Pick")
					.setParent(nameColorEntry)
					.setType(MenuAction.RUNELITE)
					.onClick(e ->
					{
						Color existingColor = getExistingPetNameColor(pet.getId());

						SwingUtilities.invokeLater(() ->
						{
							RuneliteColorPicker colorPicker = colorPickerManager.create(SwingUtilities.windowForComponent((Applet) client),
									existingColor, "Pet Name Color", false);
							colorPicker.setOnClose(color -> saveNameColor(pet.getId(), color));
							colorPicker.setVisible(true);
						});
					});
		}
	}

	private void onNameMenuEntryOptionClicked(NPC pet) {
		chatboxPanelManager.openTextInput("Name your " + pet.getName())
				.value(getExistingPetName(pet.getId()))
				.onDone((input) ->
				{
					savePetName(pet.getId(), input);
				})
				.build();
	}

	/**
	 * Save a pet name into config manager
	 * @param petNpcId Pet NPC ID
	 * @param petName Pet name to save
	 */
	private void savePetName(int petNpcId, String petName)
	{
		if (Objects.equals(petName, "") || petName == null) {
			pluginConfigManager.unsetPetName(petNpcId);
		} else {
			pluginConfigManager.setPetName(petNpcId, petName);
		}
		panel.renderPetsList();
	}

	private String getExistingPetName(int petNpcId)
	{
		String name = pluginConfigManager.getSavedPetName(petNpcId);
		if (name != null) {
			return name;
		}
		return "";
	}

	/**
	 * Saves a color to display for an individual pet
	 * @param petNpcId Pet NPC to set colored name for
	 * @param nameColor Color to save
	 */
	private void saveNameColor(int petNpcId, Color nameColor)
	{
		pluginConfigManager.setPetColor(petNpcId, nameColor);
		panel.renderPetsList();
	}

	private Color getExistingPetNameColor(int petNpcId)
	{
		String savedColor = pluginConfigManager.getSavedPetColor(petNpcId);
		if (savedColor != null) {
			return Color.decode(savedColor);
		}
		return Color.white;
	}

	@Provides
	NamedPetsConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(NamedPetsConfig.class);
	}
}