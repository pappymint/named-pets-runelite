package com.pappymint.namedpets;

import net.runelite.api.NPC;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.DynamicGridLayout;
import net.runelite.client.ui.PluginPanel;
import net.runelite.http.api.config.Profile;

import javax.inject.Inject;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class NamedPetsPanel extends PluginPanel {
    private final NamedPetsConfigManager configManager;
    private final NamedPetsConfig config;
    private final NamedPetsPlugin plugin;
    private final JPanel mainPanel = new JPanel();

    private final int GUTTER = 10;

    @Inject
    NamedPetsPanel(NamedPetsPlugin plugin, NamedPetsConfig config, NamedPetsConfigManager manager) {
        this.configManager = manager;
        this.config = config;
        this.plugin = plugin;


        // Initialise the main panel
        this.setLayout(new BorderLayout());
        this.setBorder(new EmptyBorder(GUTTER, GUTTER, GUTTER, GUTTER));
        add(mainPanel, BorderLayout.CENTER);

        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        renderPetsList();
    }

//    public JPanel buildPetsListPanel() {
//        final JPanel petsListPanel = new JPanel();
//
//        petsListPanel.setBorder(new EmptyBorder(GUTTER, GUTTER, 0, GUTTER));
//        petsListPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
//
//        return petsListPanel;
//    }

    public void renderPetsList() {
        JPanel petsListPanel = new JPanel();
        petsListPanel.setLayout(new DynamicGridLayout(0, 1, 0, GUTTER));
        List<String> configKeys = configManager.getAllPetConfig();

        // TODO: Refactor how we can manage config so this isn't so hacky
        for (String key : configKeys)
        {
            String nameKey = key.split("\\.")[1];
            if (nameKey != null) {
                int npcID = Integer.parseInt(nameKey.split("-")[1]);
                String petName = configManager.getSavedPetName(npcID);
                Color petColor = Color.decode(configManager.getSavedPetColor(npcID));

                JPanel petInfo = new JPanel();
                petInfo.setBackground(ColorScheme.DARKER_GRAY_COLOR);

                petInfo.add(new JLabel(petName));
                petInfo.add(new JLabel(Integer.toString(npcID)));

                petsListPanel.add(petInfo);

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

        mainPanel.add(petsListPanel);

        revalidate();
        repaint();
    }
}
