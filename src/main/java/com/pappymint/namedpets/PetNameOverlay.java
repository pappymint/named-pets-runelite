package com.pappymint.namedpets;

import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.Actor;
import net.runelite.api.Point;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

import java.awt.*;
import javax.inject.Inject;

public class PetNameOverlay extends Overlay
{
    private static final String CONFIG_GROUP = "namedPets";
    @Inject
    private ConfigManager configManager;

    @Inject
    private Client client;

    @Inject
    private PetNameOverlay()
    {
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        NPC follower = client.getFollower();
        if (follower == null) {
            return null;
        }

        renderFollowingPetName(graphics, follower, follower.getId());
        return null;
    }

    private void renderFollowingPetName(Graphics2D graphics, Actor petActor, int petId) {
        // Gets stored pet name from config manager
        String followingPetName = configManager.getConfiguration(CONFIG_GROUP, String.valueOf(petId));
        Point petNameLocation = petActor.getCanvasTextLocation(graphics, followingPetName, petActor.getModelHeight() + 10);

        if (petNameLocation != null) {
            OverlayUtil.renderTextLocation(graphics, petNameLocation, followingPetName, Color.PINK);
        }
    }
}
