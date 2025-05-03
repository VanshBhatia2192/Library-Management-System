package com.library.views.panels;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ThemedPanel extends JPanel {
    public ThemedPanel(String userRole, String[] navItems, String activePage, boolean showGoBack, JPanel content) {
        setLayout(new BorderLayout());
        setBackground(new Color(250, 247, 240)); // Soft cream background
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Top navigation bar
        JPanel navBar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(247, 202, 220)); // Pastel pink
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);
            }
        };
        navBar.setOpaque(false);
        navBar.setPreferredSize(new Dimension(0, 60));
        navBar.setLayout(new BoxLayout(navBar, BoxLayout.X_AXIS));
        navBar.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));

        if (showGoBack) {
            JButton goBackBtn = new JButton("← Go Back");
            com.library.views.panels.ModernButtonStyler.style(goBackBtn);
            goBackBtn.addActionListener(e -> firePropertyChange("navigate", null, "GoBack"));
            navBar.add(goBackBtn);
            navBar.add(Box.createHorizontalStrut(10));
        }

        JLabel title = new JLabel("Smart Library");
        title.setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 22));
        title.setForeground(new Color(60, 60, 60));
        navBar.add(title);
        navBar.add(Box.createHorizontalGlue());

        for (String nav : navItems) {
            JButton btn = new JButton(nav);
            boolean isActive = nav.equals(activePage);
            Color highlight = isActive ? new Color(220, 200, 255, 120) : null; // pastel purple highlight
            com.library.views.panels.ModernButtonStyler.style(btn, isActive, highlight);
            btn.setForeground(isActive ? new Color(0, 0, 0) : new Color(60, 60, 60));
            btn.addActionListener(e -> firePropertyChange("navigate", null, nav));
            navBar.add(btn);
            navBar.add(Box.createHorizontalStrut(10));
        }

        navBar.add(Box.createHorizontalGlue());
        navBar.add(new JLabel("\u25A0")); // Placeholder icon
        navBar.add(Box.createHorizontalStrut(10));
        navBar.add(new JLabel("\u25A0")); // Placeholder icon

        // Add Logout button at the end
        JButton logoutBtn = new JButton("Logout");
        com.library.views.panels.ModernButtonStyler.style(logoutBtn);
        logoutBtn.addActionListener(e -> firePropertyChange("navigate", null, "Logout"));
        navBar.add(Box.createHorizontalStrut(10));
        navBar.add(logoutBtn);

        add(navBar, BorderLayout.NORTH);
        add(content, BorderLayout.CENTER);
    }
}