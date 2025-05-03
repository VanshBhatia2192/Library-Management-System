package com.library.views.panels;

import javax.swing.*;
import java.awt.*;

public class ModernSearchPanel extends JPanel {
    public ModernSearchPanel(JComponent... components) {
        setLayout(new BorderLayout());
        setOpaque(false);
        JPanel card = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(247, 202, 220, 180)); // Pastel pink, semi-transparent
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(10, 24, 10, 24));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 8, 0, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        for (int i = 0; i < components.length; i++) {
            JComponent comp = components[i];
            comp.setFont(new Font("Arial Rounded MT Bold", Font.PLAIN, 16));
            if (comp instanceof JButton) {
                com.library.views.panels.ModernButtonStyler.style((JButton) comp);
            } else if (comp instanceof JTextField) {
                comp.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(220, 200, 220), 1),
                        BorderFactory.createEmptyBorder(6, 12, 6, 12)));
                comp.setBackground(new Color(255, 255, 255, 220));
                comp.setPreferredSize(new Dimension(220, 32));
                comp.setMinimumSize(new Dimension(80, 28));
            } else if (comp instanceof JComboBox) {
                comp.setBorder(BorderFactory.createLineBorder(new Color(220, 200, 220), 1));
                comp.setBackground(new Color(255, 255, 255, 220));
                comp.setPreferredSize(new Dimension(220, 36));
                comp.setMinimumSize(new Dimension(120, 32));
            }
            gbc.gridx = i;
            gbc.weightx = (comp instanceof JTextField) ? 1.0 : 0.0;
            card.add(comp, gbc);
        }
        gbc.gridx = components.length;
        gbc.weightx = 1.0;
        card.add(Box.createHorizontalGlue(), gbc); // Fills remaining space
        add(card, BorderLayout.CENTER);
    }
}