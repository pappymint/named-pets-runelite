package com.pappymint.namedpets;

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

        List<String> petNamesInConfig = configManager.getAllPetConfig();
        JLabel numOfNames = new JLabel(petNamesInConfig.size() + " named pets");
        mainPanel.add(numOfNames);

        for (String key : petNamesInConfig)
        {
            JLabel newPanel = new JLabel(key);
            JLabel hi = new JLabel("Hi");
            mainPanel.add(newPanel);
            mainPanel.add(hi);
        }

        revalidate();
        repaint();
    }
}
