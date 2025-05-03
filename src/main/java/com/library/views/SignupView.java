package com.library.views;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import com.library.utils.PasswordUtils;
import com.library.utils.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SignupView extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField emailField;
    private JComboBox<String> roleComboBox;
    private JButton signupButton;
    private JButton backToLoginButton;

    public SignupView() {
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Library Management System - Sign Up");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 500);
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
        JLabel titleLabel = new JLabel("Create an Account");
        titleLabel.setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 24));
        titleLabel.setForeground(new Color(60, 60, 60));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 20, 0);
        formPanel.add(titleLabel, gbc);

        // Subtitle
        JLabel subtitleLabel = new JLabel("Join Smart Library today");
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

        // Email field
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0.0;
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Arial", Font.BOLD, 14));
        emailLabel.setForeground(new Color(60, 60, 60));
        formPanel.add(emailLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.weightx = 1.0;
        emailField = new JTextField(20);
        emailField.setFont(new Font("Arial", Font.PLAIN, 14));
        emailField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        formPanel.add(emailField, gbc);

        // Role selection
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.weightx = 0.0;
        JLabel roleLabel = new JLabel("Role:");
        roleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        roleLabel.setForeground(new Color(60, 60, 60));
        formPanel.add(roleLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.weightx = 1.0;
        String[] roles = { "STUDENT", "LIBRARIAN" };
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
        signupButton = new JButton("Sign Up");
        backToLoginButton = new JButton("Back to Login");

        // Style buttons
        com.library.views.panels.ModernButtonStyler.style(signupButton);
        com.library.views.panels.ModernButtonStyler.style(backToLoginButton);

        buttonPanel.add(signupButton);
        buttonPanel.add(backToLoginButton);

        // Add panels to main panel
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add main panel to frame
        add(mainPanel);

        // Add action listeners
        signupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleSignup();
            }
        });

        backToLoginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleBackToLogin();
            }
        });
    }

    private void handleSignup() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String email = emailField.getText();
        String role = (String) roleComboBox.getSelectedItem();

        // Validate input
        if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please fill in all fields",
                    "Signup Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Hash password
        String securePassword = PasswordUtils.generateSecurePassword(password);

        try {
            // Save user to database
            if (saveUser(username, securePassword, email, role)) {
                JOptionPane.showMessageDialog(this,
                        "Signup successful! Please login.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                handleBackToLogin();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error during signup: " + ex.getMessage(),
                    "Signup Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean saveUser(String username, String password, String email, String role) throws SQLException {
        String sql = "INSERT INTO users (username, password, email, full_name, role) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, email);
            pstmt.setString(4, username); // Use username as full_name
            pstmt.setString(5, role);

            return pstmt.executeUpdate() > 0;
        }
    }

    private void handleBackToLogin() {
        this.dispose();
        new LoginView().setVisible(true);
    }
}