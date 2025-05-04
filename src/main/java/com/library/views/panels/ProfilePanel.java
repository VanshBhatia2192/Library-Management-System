package com.library.views.panels;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import com.library.utils.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class ProfilePanel extends JPanel {
    private final String currentUser;
    private final String userRole;

    public ProfilePanel(String currentUser, String userRole) {
        this.currentUser = currentUser;
        this.userRole = userRole;
        setLayout(new BorderLayout());
        setOpaque(false);
        add(createTabbedPane(), BorderLayout.CENTER);
    }

    private JTabbedPane createTabbedPane() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Arial", Font.PLAIN, 15));
        tabs.addTab("Edit Details", createEditDetailsPanel());
        tabs.addTab("Change Password", createChangePasswordPanel());
        tabs.addTab("Backup & Restore", createBackupRestorePanel());
        return tabs;
    }

    private JPanel createEditDetailsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField(20);
        JLabel emailLabel = new JLabel("Email:");
        JTextField emailField = new JTextField(20);
        JLabel fullNameLabel = new JLabel("Full Name:");
        JTextField fullNameField = new JTextField(20);
        JButton saveBtn = new JButton("Save Changes");
        com.library.views.panels.ModernButtonStyler.style(saveBtn);

        panel.add(usernameLabel, gbc);
        gbc.gridx = 1;
        panel.add(usernameField, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(emailLabel, gbc);
        gbc.gridx = 1;
        panel.add(emailField, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(fullNameLabel, gbc);
        gbc.gridx = 1;
        panel.add(fullNameField, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        panel.add(saveBtn, gbc);

        // Load current user details from DB and fill fields
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(
                        "SELECT username, email, full_name FROM users WHERE username = ? AND role = ?")) {
            stmt.setString(1, currentUser);
            stmt.setString(2, userRole);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    usernameField.setText(rs.getString("username"));
                    emailField.setText(rs.getString("email"));
                    fullNameField.setText(rs.getString("full_name"));
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load user details: " + e.getMessage());
        }

        saveBtn.addActionListener(e -> {
            String newUsername = usernameField.getText().trim();
            String newEmail = emailField.getText().trim();
            String newFullName = fullNameField.getText().trim();
            if (newUsername.isEmpty() || newEmail.isEmpty() || newFullName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required.");
                return;
            }
            try (Connection conn = DatabaseConnection.getInstance().getConnection();
                    PreparedStatement stmt = conn.prepareStatement(
                            "UPDATE users SET username=?, email=?, full_name=? WHERE username=? AND role=?")) {
                stmt.setString(1, newUsername);
                stmt.setString(2, newEmail);
                stmt.setString(3, newFullName);
                stmt.setString(4, currentUser);
                stmt.setString(5, userRole);
                int updated = stmt.executeUpdate();
                if (updated > 0) {
                    JOptionPane.showMessageDialog(this, "Profile updated successfully.");
                } else {
                    JOptionPane.showMessageDialog(this, "No changes made or user not found.");
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Failed to update profile: " + ex.getMessage());
            }
        });

        return panel;
    }

    private JPanel createChangePasswordPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        JLabel oldPassLabel = new JLabel("Old Password:");
        JPasswordField oldPassField = new JPasswordField(20);
        JLabel newPassLabel = new JLabel("New Password:");
        JPasswordField newPassField = new JPasswordField(20);
        JLabel confirmPassLabel = new JLabel("Confirm New Password:");
        JPasswordField confirmPassField = new JPasswordField(20);
        JButton changeBtn = new JButton("Change Password");
        com.library.views.panels.ModernButtonStyler.style(changeBtn);

        panel.add(oldPassLabel, gbc);
        gbc.gridx = 1;
        panel.add(oldPassField, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(newPassLabel, gbc);
        gbc.gridx = 1;
        panel.add(newPassField, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(confirmPassLabel, gbc);
        gbc.gridx = 1;
        panel.add(confirmPassField, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        panel.add(changeBtn, gbc);

        changeBtn.addActionListener(e -> {
            String oldPass = new String(oldPassField.getPassword());
            String newPass = new String(newPassField.getPassword());
            String confirmPass = new String(confirmPassField.getPassword());
            if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required.");
                return;
            }
            if (!newPass.equals(confirmPass)) {
                JOptionPane.showMessageDialog(this, "New passwords do not match.");
                return;
            }
            // Get the latest username from the Edit Details tab if available
            String latestUsername = null;
            Component parent = this.getParent();
            while (parent != null && !(parent instanceof JTabbedPane))
                parent = parent.getParent();
            if (parent instanceof JTabbedPane) {
                JTabbedPane tabs = (JTabbedPane) parent;
                Component editTab = tabs.getComponentAt(0);
                JTextField usernameField = null;
                for (Component c : ((JPanel) editTab).getComponents()) {
                    if (c instanceof JTextField
                            && ((JLabel) ((JPanel) editTab).getComponent(0)).getText().contains("Username")) {
                        usernameField = (JTextField) c;
                        break;
                    }
                }
                if (usernameField != null) {
                    latestUsername = usernameField.getText().trim();
                }
            }
            if (latestUsername == null || latestUsername.isEmpty()) {
                latestUsername = currentUser;
            }
            try {
                String storedPassword = null;
                try (Connection conn = DatabaseConnection.getInstance().getConnection();
                        PreparedStatement stmt = conn
                                .prepareStatement("SELECT password FROM users WHERE username=? AND role=?")) {
                    stmt.setString(1, latestUsername);
                    stmt.setString(2, userRole);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            storedPassword = rs.getString("password");
                        } else {
                            JOptionPane.showMessageDialog(this, "User not found.");
                            return;
                        }
                    }
                }
                // Use PasswordUtils to verify old password
                if (!com.library.utils.PasswordUtils.verifySecurePassword(oldPass, storedPassword)) {
                    JOptionPane.showMessageDialog(this, "Old password is incorrect.");
                    return;
                }
                // Hash the new password
                String newSecurePassword = com.library.utils.PasswordUtils.generateSecurePassword(newPass);
                // Update password
                try (Connection conn = DatabaseConnection.getInstance().getConnection();
                        PreparedStatement stmt = conn
                                .prepareStatement("UPDATE users SET password=? WHERE username=? AND role=?")) {
                    stmt.setString(1, newSecurePassword);
                    stmt.setString(2, latestUsername);
                    stmt.setString(3, userRole);
                    int updated = stmt.executeUpdate();
                    if (updated > 0) {
                        JOptionPane.showMessageDialog(this, "Password changed successfully.");
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to change password.");
                    }
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Failed to change password: " + ex.getMessage());
            }
        });

        return panel;
    }

    private JPanel createBackupRestorePanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 30));
        panel.setOpaque(false);
        JButton backupBtn = new JButton("Backup Database");
        JButton restoreBtn = new JButton("Restore Database");
        com.library.views.panels.ModernButtonStyler.style(backupBtn);
        com.library.views.panels.ModernButtonStyler.style(restoreBtn);
        panel.add(backupBtn);
        panel.add(restoreBtn);

        backupBtn.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save Database Backup");
            int userSelection = fileChooser.showSaveDialog(this);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File destFile = fileChooser.getSelectedFile();
                try {
                    File dbFile = new File("library.db");
                    Files.copy(dbFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    JOptionPane.showMessageDialog(this, "Backup successful.");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Backup failed: " + ex.getMessage());
                }
            }
        });

        restoreBtn.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Restore Database from Backup");
            int userSelection = fileChooser.showOpenDialog(this);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File srcFile = fileChooser.getSelectedFile();
                try {
                    File dbFile = new File("library.db");
                    Files.copy(srcFile.toPath(), dbFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    JOptionPane.showMessageDialog(this, "Restore successful. Please restart the application.");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Restore failed: " + ex.getMessage());
                }
            }
        });

        return panel;
    }
}