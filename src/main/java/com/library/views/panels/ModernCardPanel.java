package com.library.views.panels;

import javax.swing.*;
import java.awt.*;

public class ModernCardPanel extends JPanel {
    public ModernCardPanel(JComponent content) {
        setLayout(new BorderLayout());
        setOpaque(false);
        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 240));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 32, 32);
                g2.setColor(new Color(220, 200, 220, 60));
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 32, 32);
                // Drop shadow
                g2.setColor(new Color(200, 180, 200, 60));
                g2.fillRoundRect(6, getHeight() - 12, getWidth() - 12, 12, 16, 16);
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(32, 32, 32, 32));
        card.add(content, BorderLayout.CENTER);
        add(card, BorderLayout.CENTER);
    }
}