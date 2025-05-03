package com.library.views.panels;

import javax.swing.*;
import java.awt.*;

public class ModernSearchPanel extends JPanel {
    public ModernSearchPanel(JComponent... components) {
        setLayout(new FlowLayout(FlowLayout.LEFT, 18, 16));
        setOpaque(false);
        JPanel card = new JPanel(new FlowLayout(FlowLayout.LEFT, 18, 0)) {
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
        for (JComponent comp : components) {
            comp.setFont(new Font("Arial Rounded MT Bold", Font.PLAIN, 16));
            if (comp instanceof JButton) {
                JButton btn = (JButton) comp;
                btn.setBackground(new Color(247, 202, 220));
                btn.setForeground(new Color(60, 60, 60));
                btn.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
                btn.setFocusPainted(false);
                btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            } else if (comp instanceof JTextField) {
                comp.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(220, 200, 220), 1),
                        BorderFactory.createEmptyBorder(6, 12, 6, 12)));
                comp.setBackground(new Color(255, 255, 255, 220));
            } else if (comp instanceof JComboBox) {
                comp.setBorder(BorderFactory.createLineBorder(new Color(220, 200, 220), 1));
                comp.setBackground(new Color(255, 255, 255, 220));
            }
            card.add(comp);
        }
        add(card);
    }
}