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

// Events
import net.runelite.client.eventbus.Subscribe;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.MenuOpened;

@Slf4j
@PluginDescriptor(
	name = "Named Pets",
	description = "Name your followed pets by right-clicking on them",
	tags = {"pet"}
)
public class NamedPetsPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private NamedPetsConfig config;

	@Inject
	private ChatboxPanelManager chatboxPanelManager;

	@Inject
	private ConfigManager configManager;

	@Override
	protected void startUp() throws Exception
	{
		log.info("Named Pets started!");
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("Named Pets stopped!");
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() == GameState.LOGGED_IN)
		{
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Named Pets says " + config.greeting(), null);
		}
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
		log.info("Name your pet" + pet.getName());

		chatboxPanelManager.openTextInput("Name your " + pet.getName())
				.value("")
				.onDone((input) ->
				{
					input = Strings.emptyToNull(input);
					log.info(input);
				})
				.build();

//		if (menuEntry.getOption().startsWith("Name") && menuEntry) {
//
//		}
		// Chat message successful
	}

	@Provides
	NamedPetsConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(NamedPetsConfig.class);
	}
}


/**
 * MenuOpened(menuEntries=[MenuEntryImpl(getOption=Cancel, getTarget=, getIdentifier=0, getType=CANCEL, getParam0=0, getParam1=0, getItemId=0, isForceLeftClick=false, isDeprioritized=false), MenuEntryImpl(getOption=Examine, getTarget=<col=ffff00>Overgrown hellcat, getIdentifier=55783, getType=EXAMINE_NPC, getParam0=0, getParam1=0, getItemId=-1, isForceLeftClick=false, isDeprioritized=false), MenuEntryImpl(getOption=Interact, getTarget=<col=ffff00>Overgrown hellcat, getIdentifier=55783, getType=NPC_FIFTH_OPTION, getParam0=0, getParam1=0, getItemId=-1, isForceLeftClick=false, isDeprioritized=true), MenuEntryImpl(getOption=Pick-up, getTarget=<col=ffff00>Overgrown hellcat, getIdentifier=55783, getType=NPC_FIRST_OPTION, getParam0=0, getParam1=0, getItemId=-1, isForceLeftClick=false, isDeprioritized=true), MenuEntryImpl(getOption=Talk-to, getTarget=<col=ffff00>Overgrown hellcat, getIdentifier=55783, getType=NPC_THIRD_OPTION, getParam0=0, getParam1=0, getItemId=-1, isForceLeftClick=false, isDeprioritized=true), MenuEntryImpl(getOption=Chase, getTarget=<col=ffff00>Overgrown hellcat, getIdentifier=55783, getType=NPC_FOURTH_OPTION, getParam0=0, getParam1=0, getItemId=-1, isForceLeftClick=false, isDeprioritized=true), MenuEntryImpl(getOption=Walk here, getTarget=, getIdentifier=0, getType=WALK, getParam0=528, getParam1=270, getItemId=-1, isForceLeftClick=false, isDeprioritized=false)])
 */