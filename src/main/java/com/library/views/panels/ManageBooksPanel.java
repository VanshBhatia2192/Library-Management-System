package com.library.views.panels;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import com.library.utils.DatabaseConnection;

public class ManageBooksPanel extends JPanel {
    private JTable booksTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton addButton, editButton, deleteButton, searchButton;
    private JComboBox<String> categoryFilter;

    public ManageBooksPanel() {
        setLayout(new BorderLayout());
        initializeComponents();
        loadBooks();
    }

    private void initializeComponents() {
        // Create toolbar panel
        JPanel toolbarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        // Search components
        searchField = new JTextField(20);
        searchButton = new JButton("Search");
        categoryFilter = new JComboBox<>(new String[]{"All Categories", "Fiction", "Non-Fiction", "Science", "History", "Biography"});
        
        // Action buttons
        addButton = new JButton("Add Book");
        editButton = new JButton("Edit Book");
        deleteButton = new JButton("Delete Book");

        // Add components to toolbar
        toolbarPanel.add(new JLabel("Search:"));
        toolbarPanel.add(searchField);
        toolbarPanel.add(searchButton);
        toolbarPanel.add(new JLabel("Category:"));
        toolbarPanel.add(categoryFilter);
        toolbarPanel.add(addButton);
        toolbarPanel.add(editButton);
        toolbarPanel.add(deleteButton);

        // Create table
        String[] columns = {"ID", "Title", "Author", "ISBN", "Category", "Available Copies", "Total Copies"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        booksTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(booksTable);

        // Add components to panel
        add(toolbarPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Add action listeners
        addButton.addActionListener(e -> showAddBookDialog());
        editButton.addActionListener(e -> showEditBookDialog());
        deleteButton.addActionListener(e -> deleteSelectedBook());
        searchButton.addActionListener(e -> searchBooks());
        categoryFilter.addActionListener(e -> filterByCategory());
    }

    private void loadBooks() {
        tableModel.setRowCount(0);
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM books")) {
            
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getString("isbn"),
                    rs.getString("category"),
                    rs.getInt("available_quantity"),
                    rs.getInt("quantity")
                };
                tableModel.addRow(row);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Error loading books: " + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAddBookDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New Book", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Create form fields
        JTextField titleField = new JTextField(20);
        JTextField authorField = new JTextField(20);
        JTextField isbnField = new JTextField(20);
        JTextField publisherField = new JTextField(20);
        JTextField quantityField = new JTextField(20);
        JComboBox<String> categoryCombo = new JComboBox<>(new String[]{"Fiction", "Non-Fiction", "Science", "History", "Biography"});

        // Add components to dialog
        gbc.gridx = 0; gbc.gridy = 0;
        dialog.add(new JLabel("Title:"), gbc);
        gbc.gridx = 1;
        dialog.add(titleField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        dialog.add(new JLabel("Author:"), gbc);
        gbc.gridx = 1;
        dialog.add(authorField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        dialog.add(new JLabel("ISBN:"), gbc);
        gbc.gridx = 1;
        dialog.add(isbnField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        dialog.add(new JLabel("Publisher:"), gbc);
        gbc.gridx = 1;
        dialog.add(publisherField, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        dialog.add(new JLabel("Quantity:"), gbc);
        gbc.gridx = 1;
        dialog.add(quantityField, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        dialog.add(new JLabel("Category:"), gbc);
        gbc.gridx = 1;
        dialog.add(categoryCombo, gbc);

        // Add buttons
        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> {
            try {
                int quantity = Integer.parseInt(quantityField.getText());
                String sql = "INSERT INTO books (title, author, isbn, publisher, quantity, available_quantity, category) VALUES (?, ?, ?, ?, ?, ?, ?)";
                
                try (Connection conn = DatabaseConnection.getInstance().getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    
                    pstmt.setString(1, titleField.getText());
                    pstmt.setString(2, authorField.getText());
                    pstmt.setString(3, isbnField.getText());
                    pstmt.setString(4, publisherField.getText());
                    pstmt.setInt(5, quantity);
                    pstmt.setInt(6, quantity);
                    pstmt.setString(7, (String) categoryCombo.getSelectedItem());
                    
                    pstmt.executeUpdate();
                    JOptionPane.showMessageDialog(dialog, "Book added successfully!");
                    dialog.dispose();
                    loadBooks();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter a valid quantity!");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Error adding book: " + ex.getMessage());
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0; gbc.gridy = 6;
        gbc.gridwidth = 2;
        dialog.add(buttonPanel, gbc);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showEditBookDialog() {
        int selectedRow = booksTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a book to edit!");
            return;
        }

        // Get selected book details
        int bookId = (int) tableModel.getValueAt(selectedRow, 0);
        String title = (String) tableModel.getValueAt(selectedRow, 1);
        String author = (String) tableModel.getValueAt(selectedRow, 2);
        String isbn = (String) tableModel.getValueAt(selectedRow, 3);
        String category = (String) tableModel.getValueAt(selectedRow, 4);
        int availableCopies = (int) tableModel.getValueAt(selectedRow, 5);
        int totalCopies = (int) tableModel.getValueAt(selectedRow, 6);

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Book", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Create form fields
        JTextField titleField = new JTextField(title, 20);
        JTextField authorField = new JTextField(author, 20);
        JTextField isbnField = new JTextField(isbn, 20);
        JTextField quantityField = new JTextField(String.valueOf(totalCopies), 20);
        JComboBox<String> categoryCombo = new JComboBox<>(new String[]{"Fiction", "Non-Fiction", "Science", "History", "Biography"});
        categoryCombo.setSelectedItem(category);

        // Add components to dialog
        gbc.gridx = 0; gbc.gridy = 0;
        dialog.add(new JLabel("Title:"), gbc);
        gbc.gridx = 1;
        dialog.add(titleField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        dialog.add(new JLabel("Author:"), gbc);
        gbc.gridx = 1;
        dialog.add(authorField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        dialog.add(new JLabel("ISBN:"), gbc);
        gbc.gridx = 1;
        dialog.add(isbnField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        dialog.add(new JLabel("Total Copies:"), gbc);
        gbc.gridx = 1;
        dialog.add(quantityField, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        dialog.add(new JLabel("Category:"), gbc);
        gbc.gridx = 1;
        dialog.add(categoryCombo, gbc);

        // Add buttons
        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> {
            try {
                int newQuantity = Integer.parseInt(quantityField.getText());
                int difference = newQuantity - totalCopies;
                
                String sql = "UPDATE books SET title=?, author=?, isbn=?, quantity=?, available_quantity=available_quantity+?, category=? WHERE id=?";
                
                try (Connection conn = DatabaseConnection.getInstance().getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    
                    pstmt.setString(1, titleField.getText());
                    pstmt.setString(2, authorField.getText());
                    pstmt.setString(3, isbnField.getText());
                    pstmt.setInt(4, newQuantity);
                    pstmt.setInt(5, difference);
                    pstmt.setString(6, (String) categoryCombo.getSelectedItem());
                    pstmt.setInt(7, bookId);
                    
                    pstmt.executeUpdate();
                    JOptionPane.showMessageDialog(dialog, "Book updated successfully!");
                    dialog.dispose();
                    loadBooks();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter a valid quantity!");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Error updating book: " + ex.getMessage());
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0; gbc.gridy = 6;
        gbc.gridwidth = 2;
        dialog.add(buttonPanel, gbc);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void deleteSelectedBook() {
        int selectedRow = booksTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a book to delete!");
            return;
        }

        int bookId = (int) tableModel.getValueAt(selectedRow, 0);
        String title = (String) tableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete the book '" + title + "'?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DatabaseConnection.getInstance().getConnection();
                 PreparedStatement pstmt = conn.prepareStatement("DELETE FROM books WHERE id = ?")) {
                
                pstmt.setInt(1, bookId);
                pstmt.executeUpdate();
                
                JOptionPane.showMessageDialog(this, "Book deleted successfully!");
                loadBooks();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,
                    "Error deleting book: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void searchBooks() {
        String searchTerm = searchField.getText().trim();
        String selectedCategory = (String) categoryFilter.getSelectedItem();
        
        tableModel.setRowCount(0);
        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            StringBuilder sql = new StringBuilder("SELECT * FROM books WHERE 1=1");
            
            if (!searchTerm.isEmpty()) {
                sql.append(" AND (title LIKE ? OR author LIKE ? OR isbn LIKE ?)");
            }
            
            if (!selectedCategory.equals("All Categories")) {
                sql.append(" AND category = ?");
            }
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
                int paramIndex = 1;
                
                if (!searchTerm.isEmpty()) {
                    String searchPattern = "%" + searchTerm + "%";
                    pstmt.setString(paramIndex++, searchPattern);
                    pstmt.setString(paramIndex++, searchPattern);
                    pstmt.setString(paramIndex++, searchPattern);
                }
                
                if (!selectedCategory.equals("All Categories")) {
                    pstmt.setString(paramIndex, selectedCategory);
                }
                
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Object[] row = {
                            rs.getInt("id"),
                            rs.getString("title"),
                            rs.getString("author"),
                            rs.getString("isbn"),
                            rs.getString("category"),
                            rs.getInt("available_quantity"),
                            rs.getInt("quantity")
                        };
                        tableModel.addRow(row);
                    }
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Error searching books: " + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filterByCategory() {
        searchBooks();
    }
} 