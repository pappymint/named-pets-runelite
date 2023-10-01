package com.pappymint.namedpets;

import net.runelite.client.ui.PluginPanel;

import javax.inject.Inject;
import javax.swing.*;
import java.awt.*;

public class NamedPetsPanel extends PluginPanel {
    private final NamedPetsPlugin plugin;
    private final JPanel mainPanel = new JPanel();

    @Inject
    NamedPetsPanel(NamedPetsPlugin plugin) {
        this.plugin = plugin;
        this.setLayout(new BorderLayout());

        JLabel title = new JLabel("Named Pets");

        mainPanel.add(title);
    }
}
