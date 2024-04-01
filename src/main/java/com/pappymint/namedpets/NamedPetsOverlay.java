package com.pappymint.namedpets;

import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.Actor;
import net.runelite.api.Point;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

import java.awt.*;
import java.util.Objects;
import javax.inject.Inject;

public class NamedPetsOverlay extends Overlay
{
    private final NamedPetsConfig pluginConfig;
    private final NamedPetsPlugin plugin;
    private final NamedPetsConfigManager configManager;

    @Inject
    private Client client;

    @Inject
    private NamedPetsOverlay(NamedPetsPlugin plugin, NamedPetsConfig pluginConfig, NamedPetsConfigManager configManager)
    {
        this.plugin = plugin;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);

        this.pluginConfig = pluginConfig;
        this.configManager = configManager;
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        NPC follower = client.getFollower();
        if (follower != null) {
            renderPetName(graphics, follower, follower.getId());
        }

        if (pluginConfig.petNamesPOHEnabled()) {
            renderPOHPetNames(graphics);
        }

        return null;
    }

    private void renderPetName(Graphics2D graphics, Actor petActor, int petId) {
        // Gets stored pet name + name color from config manager
        String storedPetName = configManager.getSavedPetName(petId);
        if (storedPetName != null && !storedPetName.isEmpty()) {
            Color nameColor = getPetNameColor(petId);

            // Default config height to 0 if it isnt between 1 - 100
            int customHeightIncrease = pluginConfig.getCustomPosition() > 0 && pluginConfig.getCustomPosition() <= 100 ? pluginConfig.getCustomPosition() : 0;
            Point petNameLocation = petActor.getCanvasTextLocation(graphics, storedPetName, petActor.getModelHeight() + customHeightIncrease);

            if (petNameLocation != null) {
                OverlayUtil.renderTextLocation(graphics, petNameLocation, storedPetName, nameColor);
            }
        }
    }

    private Color getPetNameColor(int petId) {
        String customColorSetForPet = configManager.getSavedPetColor(petId);
        Color defaultConfigColor = pluginConfig.getDefaultPetNameColor();

        if (customColorSetForPet != null) {
            return Color.decode(customColorSetForPet);
        } else if (defaultConfigColor != null) {
            return defaultConfigColor;
        } else {
            return Color.white;
        }
    }

    /**
     * POH pet ids are different to follower NPC pet IDs. We must find a mapping
     * between the two to successfully find the appropriate set name for a POH pet.
     * @param graphics Graphics2D for rendering
     */
    private void renderPOHPetNames(Graphics2D graphics) {
        for (NPC pohPet : plugin.getPOHPetRenderList()) {
            // Only use the follower NPC id for naming, retrieving.

            for (int existingFollowerId : configManager.getAllSavedPetIds()) {
                if (Objects.equals(pohPet.getName(), configManager.getPetNPCName(existingFollowerId))) {
                    renderPetName(graphics, pohPet, existingFollowerId);
                }
                break;
            }
        }
    }
}
