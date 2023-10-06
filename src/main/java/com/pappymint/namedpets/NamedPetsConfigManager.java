package com.pappymint.namedpets;

import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;

import javax.inject.Inject;
import java.awt.*;
import java.util.List;

public class NamedPetsConfigManager {
    private final ConfigManager configManager;
    private final NamedPetsPlugin plugin;

    public static final String CONFIG_GROUP = "namedPets";
    public static final String CONFIG_NAME = "name";
    public static final String CONFIG_COLOR = "color";

    @Inject
    NamedPetsConfigManager(NamedPetsPlugin plugin, ConfigManager configManager) {
        this.configManager = configManager;
        this.plugin = plugin;
    }

    private String nameConfigKey(int petId) {
        return CONFIG_NAME + "-" + petId;
    }
    private String colorConfigKey(int petId) {
        return CONFIG_COLOR + "-" + petId;
    }

    // *** Pet Name Getters/Setters ***
    public String getSavedPetName(int petId) {
        return configManager.getConfiguration(CONFIG_GROUP, nameConfigKey(petId));
    }
    public void setPetName(int petId, String petName) {
        configManager.setConfiguration(CONFIG_GROUP, nameConfigKey(petId), petName);
    }
    public void unsetPetName(int petId) {
        configManager.unsetConfiguration(CONFIG_GROUP, nameConfigKey(petId));
    }

    // *** Pet Color Getters/Setters ***
    public String getSavedPetColor(int petId) {
        return configManager.getConfiguration(CONFIG_GROUP, colorConfigKey(petId));
    }
    public void setPetColor(int petId, Color petNameColor) {
        configManager.setConfiguration(CONFIG_GROUP, colorConfigKey(petId), petNameColor);
    }

    // *** Pet List ***
    public List<String> getAllPetConfig() {
        return configManager.getConfigurationKeys(CONFIG_GROUP + '.' + CONFIG_NAME);
    }
}
