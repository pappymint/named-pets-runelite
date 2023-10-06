package com.pappymint.namedpets;

import net.runelite.api.NPC;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.ui.PluginPanel;
import net.runelite.http.api.config.Profile;

import javax.inject.Inject;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class NamedPetsPanel extends PluginPanel {
    private final NamedPetsConfigManager configManager;
    private final NamedPetsConfig config;
    private final NamedPetsPlugin plugin;
    private final JPanel mainPanel = new JPanel();

    @Inject
    NamedPetsPanel(NamedPetsPlugin plugin, NamedPetsConfig config, NamedPetsConfigManager manager) {
        this.configManager = manager;
        this.config = config;
        this.plugin = plugin;


        // Initialise the main panel
        this.setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);

        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        renderPetsList();
    }

    public void renderPetsList() {
        mainPanel.removeAll();

        JLabel title = new JLabel("Named Pets");
        mainPanel.add(title);

        List<String> configKeys = configManager.getAllPetConfig();
        JLabel numOfNames = new JLabel(configKeys.size() + " named pets");
        mainPanel.add(numOfNames);

        // TODO: Refactor how we can manage config so this isn't so hacky
        for (String key : configKeys)
        {
            String nameKey = key.split("\\.")[1];
            if (nameKey != null) {
                int npcID = Integer.parseInt(nameKey.split("-")[1]);
                String petName = configManager.getSavedPetName(npcID);
                Color petColor = Color.decode(configManager.getSavedPetColor(npcID));

                JLabel newPanel = new JLabel(petName + ": ID " + npcID + ", " + petColor);
                mainPanel.add(newPanel);

                /**
                 * To get the NPC inventory item sprite
                 * We may need to create a manual map between npc ID to item ID
                 * then get the item sprite by ID
                 */


                // Need:
                // Name of NPC
                // Name given
                // Color given
            }
        }

        revalidate();
        repaint();
    }
}
