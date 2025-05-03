package com.library.views.panels;

import javax.swing.*;
import java.awt.*;

public class ModernButtonStyler {
    public static void style(JButton button) {
        style(button, false, null);
    }

    public static void style(JButton button, boolean active, Color highlight) {
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(new Color(60, 60, 60));
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                AbstractButton b = (AbstractButton) c;
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int arc = 32;
                // Subtle drop shadow
                g2.setColor(new Color(220, 200, 220, 30));
                g2.fillRoundRect(4, 4, b.getWidth() - 8, b.getHeight() - 8, arc, arc);
                // Highlight for active
                if (active && highlight != null) {
                    g2.setColor(highlight);
                    g2.fillRoundRect(0, 0, b.getWidth(), b.getHeight(), arc, arc);
                } else {
                    // Translucent white fill
                    g2.setColor(new Color(255, 255, 255, 230));
                    g2.fillRoundRect(0, 0, b.getWidth(), b.getHeight(), arc, arc);
                }
                // Thin, light border
                g2.setColor(new Color(220, 200, 220, 60));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(1, 1, b.getWidth() - 3, b.getHeight() - 3, arc, arc);
                g2.dispose();
                super.paint(g, c);
            }
        });
    }
}