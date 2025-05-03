package com.library.views.panels;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class HomePanel extends JPanel {
    public HomePanel(String userRole) {
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

        // Go Back button
        JButton goBackBtn = new JButton("← Go Back");
        goBackBtn.setFocusPainted(false);
        goBackBtn.setFont(new Font("Arial", Font.BOLD, 14));
        goBackBtn.setBackground(new Color(247, 202, 220));
        goBackBtn.setForeground(new Color(60, 60, 60));
        goBackBtn.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        goBackBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        goBackBtn.addActionListener(e -> firePropertyChange("navigate", null, "GoBack"));
        navBar.add(goBackBtn, 0);
        navBar.add(Box.createHorizontalStrut(10), 1);

        // App title
        JLabel title = new JLabel("Smart Library");
        title.setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 22));
        title.setForeground(new Color(60, 60, 60));
        navBar.add(title);
        navBar.add(Box.createHorizontalGlue());

        // Navigation buttons (customize per role)
        String[] studentNav = { "Borrow", "Return", "Request", "Status", "Notifications", "Profile" };
        String[] librarianNav = { "Manage Books", "Issued Books", "Student Records", "Overdue Notices", "Profile" };
        String[] adminNav = { "Manage Librarians", "Reports", "Fine Mgmt", "User Mgmt", "Profile" };
        String[] navItems = userRole.equals("ADMIN") ? adminNav
                : userRole.equals("LIBRARIAN") ? librarianNav : studentNav;
        for (String nav : navItems) {
            JButton btn = new JButton(nav);
            btn.setFocusPainted(false);
            btn.setFont(new Font("Arial", Font.BOLD, 14));
            btn.setBackground(new Color(247, 202, 220));
            btn.setForeground(new Color(60, 60, 60));
            btn.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.addActionListener(e -> firePropertyChange("navigate", null, nav));
            navBar.add(btn);
            navBar.add(Box.createHorizontalStrut(10));
        }

        // Placeholder for social icons (right side)
        navBar.add(Box.createHorizontalGlue());
        navBar.add(new JLabel("\u25A0")); // Placeholder icon
        navBar.add(Box.createHorizontalStrut(10));
        navBar.add(new JLabel("\u25A0")); // Placeholder icon

        add(navBar, BorderLayout.NORTH);

        // Center content
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel welcome = new JLabel("Welcome to the Smart Library!");
        welcome.setFont(new Font("Comic Sans MS", Font.BOLD, 36));
        welcome.setForeground(new Color(40, 40, 40));
        centerPanel.add(welcome, gbc);

        gbc.gridy++;
        JLabel subtitle = new JLabel("Manage your books, requests, and more with ease.");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 18));
        subtitle.setForeground(new Color(80, 80, 80));
        centerPanel.add(subtitle, gbc);

        gbc.gridy++;
        JButton cta = new JButton("Get Started");
        cta.setFont(new Font("Arial", Font.BOLD, 18));
        cta.setBackground(new Color(247, 202, 220));
        cta.setForeground(new Color(60, 60, 60));
        cta.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        cta.setFocusPainted(false);
        centerPanel.add(cta, gbc);

        // Placeholders for character images
        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        JLabel leftChar = new JLabel();
        leftChar.setPreferredSize(new Dimension(160, 220));
        leftChar.setIcon(new ImageIcon(new BufferedImage(160, 220, BufferedImage.TYPE_INT_ARGB)));
        centerPanel.add(leftChar, gbc);

        gbc.gridx = 2;
        JLabel rightChar = new JLabel();
        rightChar.setPreferredSize(new Dimension(160, 220));
        rightChar.setIcon(new ImageIcon(new BufferedImage(160, 220, BufferedImage.TYPE_INT_ARGB)));
        centerPanel.add(rightChar, gbc);

        add(centerPanel, BorderLayout.CENTER);
    }
}