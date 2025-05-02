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
        setSize(400, 300);
        setLocationRelativeTo(null);
        setResizable(false);

        // Create main panel with padding
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create form panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Username field
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        usernameField = new JTextField(20);
        formPanel.add(usernameField, gbc);

        // Password field
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        passwordField = new JPasswordField(20);
        formPanel.add(passwordField, gbc);

        // Email field
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Email:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        emailField = new JTextField(20);
        formPanel.add(emailField, gbc);

        // Role selection
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Role:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        String[] roles = {"STUDENT", "LIBRARIAN"};
        roleComboBox = new JComboBox<>(roles);
        formPanel.add(roleComboBox, gbc);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        signupButton = new JButton("Sign Up");
        backToLoginButton = new JButton("Back to Login");

        // Style buttons
        Color buttonColor = new Color(70, 70, 70);
        Color textColor = Color.WHITE;
        Font buttonFont = new Font("Arial", Font.PLAIN, 14);

        for (JButton button : new JButton[]{signupButton, backToLoginButton}) {
            button.setBackground(buttonColor);
            button.setForeground(textColor);
            button.setFont(buttonFont);
            button.setBorderPainted(false);
            button.setFocusPainted(false);
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

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