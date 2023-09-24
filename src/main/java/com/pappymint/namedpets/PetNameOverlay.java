package com.pappymint.namedpets;

import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.Point;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.ui.overlay.Overlay;
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

    @Override
    public Dimension render(Graphics2D graphics) {
        renderFollowingPetName(graphics);
        return null;
    }

    private void renderFollowingPetName(Graphics2D graphics) {
        NPC follower = client.getFollower();
        if (follower == null) {
            return;
        }

        String followingPetName = configManager.getConfiguration(CONFIG_GROUP, String.valueOf(follower.getId()));
        Point petNameLocation = follower.getCanvasTextLocation(graphics, followingPetName, follower.getLogicalHeight() + 40);
        if (petNameLocation != null) {
            OverlayUtil.renderTextLocation(graphics, petNameLocation, followingPetName, Color.getHSBColor(121, 100, 49));
        }
    }
}
