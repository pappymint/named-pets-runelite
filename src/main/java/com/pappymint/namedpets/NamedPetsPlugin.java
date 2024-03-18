package com.pappymint.namedpets;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.MenuOpened;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.game.chatbox.ChatboxPanelManager;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.components.colorpicker.ColorPickerManager;
import net.runelite.client.ui.components.colorpicker.RuneliteColorPicker;
import net.runelite.client.ui.overlay.OverlayManager;
import javax.inject.Inject;
import javax.swing.*;
import java.applet.Applet;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

@Slf4j
@PluginDescriptor(
	name = "Named Pets",
	description = "Right click & give your pets a name!",
	tags = {"name", "pet"}
)
public class NamedPetsPlugin extends Plugin
{
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
	private NamedPetsOverlay petNameOverlay;
	@Inject
	private ClientToolbar clientToolbar;
	@Inject
	private ColorPickerManager colorPickerManager;

	@Provides
	NamedPetsConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(NamedPetsConfig.class);
	}

	private ArrayList<NPC> pohPets = new ArrayList<>();

	@Override
	protected void startUp() throws Exception {
		pluginConfigManager = new NamedPetsConfigManager(this, configManager);
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

	@Subscribe
	public void onNpcSpawned(NpcSpawned event)
	{
		if (config.petNamesPOHEnabled()) {
			NPC spawnedNpc = event.getNpc();
			NPCComposition npcComposition = spawnedNpc.getComposition();
			String[] actions = npcComposition.getActions();

			// Has menu option 'Pick-up' and is not following the player
			if (Arrays.asList(actions).contains("Pick-up") && !npcComposition.isFollower()) {
                addNewPOHPetToRenderList(spawnedNpc);
			}
		}
	}

	@Subscribe
	public void onNpcDespawned(NpcDespawned event)
	{
		// Unsubscribe pet from render list
		NPC npcDespawned = event.getNpc();
		removePOHPetFromRenderList(npcDespawned);
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
				addNamePetMenuOption(targetedNpc, entryIndex, entry, menuEntries);
				addColorNameMenuOption(targetedNpc, entryIndex, entry);
				break;
			}
		}
	}

	private void addNamePetMenuOption(NPC pet, int index, MenuEntry menuEntry, MenuEntry[] menuEntries) {
		if (config.replaceMenuPetName()) {
			int petId = pet.getId();
			String petName = getExistingPetName(petId);
			if (pet.getName() != null) {
				for (MenuEntry menuEntry1 : menuEntries) {
					if (menuEntry1.getTarget() != null && menuEntry1.getTarget().contains(pet.getName())) {
						menuEntry1.setTarget(menuEntry1.getTarget().replace(pet.getName(), petName));
					}
				}
			}
		}
		client.createMenuEntry(index)
			.setOption("Name")
			.setTarget(menuEntry.getTarget())
			.setType(MenuAction.RUNELITE)
			.onClick(e -> onNameMenuEntryOptionClicked(pet));
	}

	private void addColorNameMenuOption(NPC pet, int index, MenuEntry menuEntry) {
		int petId = pet.getId();
		if (!getExistingPetName(petId).isEmpty()) {
			// Add remove color option if color is set

			client.createMenuEntry(index)
				.setOption("Color Name")
				.setTarget(menuEntry.getTarget())
				.onClick(e ->
				{
					Color existingColor = getExistingPetNameColor(petId);

					SwingUtilities.invokeLater(() ->
					{
						RuneliteColorPicker colorPicker = colorPickerManager.create(SwingUtilities.windowForComponent((Applet) client),
								existingColor, "Pet Name Color", false);
						colorPicker.setOnClose(color -> saveNameColor(petId, color));
						colorPicker.setVisible(true);
					});
				});


			if (
				pluginConfigManager.getSavedPetColor(petId) != null &&
				!pluginConfigManager.getSavedPetColor(petId).isEmpty()
			) {
				client.createMenuEntry(index + 1)
						.setOption("Remove Name Color")
						.setTarget(menuEntry.getTarget())
						.onClick(e ->
						{
							pluginConfigManager.unsetPetColor(petId);
						});
			}
		}
	}

	private void onNameMenuEntryOptionClicked(NPC pet) {
		chatboxPanelManager.openTextInput("Name your " + pet.getName())
			.value(getExistingPetName(pet.getId()))
			.onDone((input) ->
			{
				savePetName(pet, input);
			})
			.build();
	}

	/**
	 * Save a pet name into config manager
	 * @param petNpc Pet NPC
	 * @param petName Pet name to save
	 */
	private void savePetName(NPC petNpc, String petName)
	{
		if (Objects.equals(petName, "") || petName == null) {
			pluginConfigManager.unsetPetName(petNpc.getId());
		} else {
			pluginConfigManager.setPetName(petNpc.getId(), petName);
			pluginConfigManager.setPetNPCName(petNpc.getId(), petNpc.getName());
		}
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
	}

	private Color getExistingPetNameColor(int petNpcId)
	{
		String savedColor = pluginConfigManager.getSavedPetColor(petNpcId);
		if (savedColor != null) {
			return Color.decode(savedColor);
		}
		return Color.white;
	}

	public ArrayList<NPC> getPOHPetRenderList() {
		return pohPets;
	}

	public void addNewPOHPetToRenderList(NPC newPet)
	{
		if (!isPetInPOHRenderList(newPet)) {
			pohPets.add(newPet);
		}
	}

	public void removePOHPetFromRenderList(NPC petToRemove)
	{
		if (isPetInPOHRenderList(petToRemove)) {
			pohPets.remove(petToRemove);
		}
	}

	public boolean isPetInPOHRenderList(NPC pet)
	{
		return pohPets.contains(pet);
	}
}