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
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);
                g2.setColor(new Color(220, 200, 220, 60));
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 40, 40);
                // Drop shadow
                g2.setColor(new Color(200, 180, 200, 60));
                g2.fillRoundRect(6, getHeight() - 12, getWidth() - 12, 12, 20, 20);
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(32, 32, 32, 32));

        // Apply consistent font styling to all components
        applyFontStyling(content);

        card.add(content, BorderLayout.CENTER);
        add(card, BorderLayout.CENTER);
    }

    private void applyFontStyling(JComponent component) {
        if (component instanceof JLabel) {
            JLabel label = (JLabel) component;
            if (label.getText().equals("Smart Library")) {
                label.setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 22));
            } else if (label.getText().toLowerCase().contains("welcome") ||
                    label.getText().toLowerCase().contains("profile") ||
                    label.getText().toLowerCase().contains("manage") ||
                    label.getText().toLowerCase().contains("view")) {
                label.setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 24));
            } else {
                label.setFont(new Font("Arial", Font.PLAIN, 14));
            }
            label.setForeground(new Color(60, 60, 60));
        } else if (component instanceof JButton) {
            JButton button = (JButton) component;
            button.setFont(new Font("Arial", Font.BOLD, 14));
            button.setForeground(new Color(60, 60, 60));
            button.setContentAreaFilled(false);
            button.setFocusPainted(false);
            button.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            // Custom paint for white rounded background
            button.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
                @Override
                public void paint(Graphics g, JComponent c) {
                    AbstractButton b = (AbstractButton) c;
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    int arc = 32;
                    g2.setColor(Color.WHITE);
                    g2.fillRoundRect(0, 0, b.getWidth(), b.getHeight(), arc, arc);
                    g2.setColor(new Color(220, 200, 220, 60));
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.drawRoundRect(1, 1, b.getWidth() - 3, b.getHeight() - 3, arc, arc);
                    g2.dispose();
                    super.paint(g, c);
                }
            });
        } else if (component instanceof JTextField) {
            JTextField field = (JTextField) component;
            field.setFont(new Font("Arial", Font.PLAIN, 14));
        } else if (component instanceof JComboBox) {
            JComboBox<?> combo = (JComboBox<?>) component;
            combo.setFont(new Font("Arial", Font.PLAIN, 14));
        } else if (component instanceof JTable) {
            JTable table = (JTable) component;
            table.setFont(new Font("Arial", Font.PLAIN, 14));
            table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        }

        // Recursively apply to child components
        if (component instanceof Container) {
            for (Component child : ((Container) component).getComponents()) {
                if (child instanceof JComponent) {
                    applyFontStyling((JComponent) child);
                }
            }
        }
    }
}