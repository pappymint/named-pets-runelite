package com.pappymint.namedpets;

import net.runelite.client.config.ConfigManager;

import javax.inject.Inject;
import java.awt.*;
import java.util.List;

public class NamedPetsConfigManager {
    private final ConfigManager configManager;
    private final NamedPetsPlugin plugin;

    public static final String CONFIG_GROUP = "namedPets";
    public static final String CONFIG_NAME = "name";
    public static final String CONFIG_COLOR = "color";
    public static final String CONFIG_NPC_NAME = "npcName";

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
    private String npcNameConfigKey(int petId) {
        return CONFIG_NPC_NAME + "-" + petId;
    }

    // *** Pet Name Getters/Setters ***
    public String getSavedPetName(int petId) {
        return configManager.getRSProfileConfiguration(CONFIG_GROUP, nameConfigKey(petId));
    }
    public void setPetName(int petId, String petName) {
        configManager.setRSProfileConfiguration(CONFIG_GROUP, nameConfigKey(petId), petName);
    }
    public void unsetPetName(int petId) {
        configManager.unsetRSProfileConfiguration(CONFIG_GROUP, nameConfigKey(petId));
    }

    // *** Pet Color Getters/Setters ***
    public String getSavedPetColor(int petId) {
        return configManager.getRSProfileConfiguration(CONFIG_GROUP, colorConfigKey(petId));
    }
    public void setPetColor(int petId, Color petNameColor) {
        configManager.setRSProfileConfiguration(CONFIG_GROUP, colorConfigKey(petId), petNameColor);
    }

    // *** Pet NPC Name Getters/Setters ***
    public String getPetNPCName(int petId) {
        return configManager.getRSProfileConfiguration(CONFIG_GROUP, npcNameConfigKey(petId));
    }
    public void setPetNPCName(int petId, String npcName) {
        configManager.setRSProfileConfiguration(CONFIG_GROUP, npcNameConfigKey(petId), npcName);
    }
}
