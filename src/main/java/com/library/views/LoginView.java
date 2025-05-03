package com.library.views;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import com.library.utils.PasswordUtils;
import com.library.utils.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginView extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleComboBox;
    private JButton loginButton;
    private JButton signupButton;

    public LoginView() {
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Library Management System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(null);
        setResizable(false);

        // Create main panel with padding
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(new Color(250, 247, 240)); // Soft cream background
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Create form panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Title
        JLabel titleLabel = new JLabel("Welcome to Smart Library");
        titleLabel.setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 24));
        titleLabel.setForeground(new Color(60, 60, 60));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 20, 0);
        formPanel.add(titleLabel, gbc);

        // Subtitle
        JLabel subtitleLabel = new JLabel("Please login to continue");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(80, 80, 80));
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 30, 0);
        formPanel.add(subtitleLabel, gbc);

        // Username field
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(5, 5, 5, 5);
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        usernameLabel.setForeground(new Color(60, 60, 60));
        formPanel.add(usernameLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        usernameField = new JTextField(20);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        formPanel.add(usernameField, gbc);

        // Password field
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.0;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 14));
        passwordLabel.setForeground(new Color(60, 60, 60));
        formPanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        formPanel.add(passwordField, gbc);

        // Role selection
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0.0;
        JLabel roleLabel = new JLabel("Role:");
        roleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        roleLabel.setForeground(new Color(60, 60, 60));
        formPanel.add(roleLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.weightx = 1.0;
        String[] roles = { "ADMIN", "LIBRARIAN", "STUDENT" };
        roleComboBox = new JComboBox<>(roles);
        roleComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        roleComboBox.setBackground(Color.WHITE);
        roleComboBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        formPanel.add(roleComboBox, gbc);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);
        loginButton = new JButton("Login");
        signupButton = new JButton("Sign Up");

        // Style buttons
        com.library.views.panels.ModernButtonStyler.style(loginButton);
        com.library.views.panels.ModernButtonStyler.style(signupButton);

        buttonPanel.add(loginButton);
        buttonPanel.add(signupButton);

        // Add panels to main panel
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add main panel to frame
        add(mainPanel);

        // Add action listeners
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });

        signupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleSignup();
            }
        });

        // Add enter key listener to password field
        passwordField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });
    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String role = (String) roleComboBox.getSelectedItem();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter both username and password",
                    "Login Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            if (authenticateUser(username, password, role)) {
                // Open appropriate dashboard based on role
                this.dispose();
                switch (role) {
                    case "ADMIN":
                        new AdminDashboard(username).setVisible(true);
                        break;
                    case "LIBRARIAN":
                        new LibrarianDashboard(username).setVisible(true);
                        break;
                    case "STUDENT":
                        new StudentDashboard(username).setVisible(true);
                        break;
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "Invalid username, password, or role",
                        "Login Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error during login: " + ex.getMessage(),
                    "Login Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean authenticateUser(String username, String password, String role) throws SQLException {
        String sql = "SELECT password FROM users WHERE username = ? AND role = ? AND is_active = true";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, role);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String storedPassword = rs.getString("password");
                    return PasswordUtils.verifySecurePassword(password, storedPassword);
                }
            }
        }
        return false;
    }

    private void handleSignup() {
        this.dispose();
        new SignupView().setVisible(true);
    }
}