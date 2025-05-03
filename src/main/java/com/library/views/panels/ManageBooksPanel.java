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
        // Initialize components first
        searchField = new JTextField(20);
        searchButton = new JButton("Search");
        categoryFilter = new JComboBox<>(
                new String[] { "All Categories", "Fiction", "Non-Fiction", "Science", "History", "Biography",
                        "Children", "Comics", "Mystery", "Romance", "Fantasy", "Technology", "Self-Help", "Education",
                        "Poetry", "Art", "Travel", "Health", "Religion", "Business" });
        addButton = new JButton("Add Book");
        editButton = new JButton("Edit Book");
        deleteButton = new JButton("Delete Book");
        // Style toolbar buttons
        com.library.views.panels.ModernButtonStyler.style(searchButton);
        com.library.views.panels.ModernButtonStyler.style(addButton);
        com.library.views.panels.ModernButtonStyler.style(editButton);
        com.library.views.panels.ModernButtonStyler.style(deleteButton);
        // Modern search/action bar
        JPanel toolbarPanel = new com.library.views.panels.ModernSearchPanel(
                new JLabel("Search:"), searchField, searchButton,
                new JLabel("Category:"), categoryFilter,
                addButton, editButton, deleteButton);

        // Create table
        String[] columns = { "ID", "Title", "Author", "ISBN", "Category", "Available Copies", "Total Copies" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        booksTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(booksTable);
        scrollPane.setPreferredSize(new Dimension(800, 400)); // Set preferred size for the scroll pane

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
        JComboBox<String> categoryCombo = new JComboBox<>(
                new String[] { "Fiction", "Non-Fiction", "Science", "History", "Biography", "Children", "Comics",
                        "Mystery", "Romance", "Fantasy", "Technology", "Self-Help", "Education", "Poetry", "Art",
                        "Travel", "Health", "Religion", "Business" });

        // Add components to dialog
        gbc.gridx = 0;
        gbc.gridy = 0;
        dialog.add(new JLabel("Title:"), gbc);
        gbc.gridx = 1;
        dialog.add(titleField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        dialog.add(new JLabel("Author:"), gbc);
        gbc.gridx = 1;
        dialog.add(authorField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        dialog.add(new JLabel("ISBN:"), gbc);
        gbc.gridx = 1;
        dialog.add(isbnField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        dialog.add(new JLabel("Publisher:"), gbc);
        gbc.gridx = 1;
        dialog.add(publisherField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        dialog.add(new JLabel("Quantity:"), gbc);
        gbc.gridx = 1;
        dialog.add(quantityField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        dialog.add(new JLabel("Category:"), gbc);
        gbc.gridx = 1;
        dialog.add(categoryCombo, gbc);

        // Add buttons
        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        com.library.views.panels.ModernButtonStyler.style(saveButton);
        com.library.views.panels.ModernButtonStyler.style(cancelButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        // Save button logic
        saveButton.addActionListener(e -> {
            String title = titleField.getText().trim();
            String author = authorField.getText().trim();
            String isbn = isbnField.getText().trim();
            String publisher = publisherField.getText().trim();
            String quantityStr = quantityField.getText().trim();
            String category = (String) categoryCombo.getSelectedItem();
            if (title.isEmpty() || author.isEmpty() || isbn.isEmpty() || quantityStr.isEmpty() || category == null) {
                JOptionPane.showMessageDialog(dialog, "Please fill in all required fields.", "Input Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            int quantity;
            try {
                quantity = Integer.parseInt(quantityStr);
                if (quantity < 1)
                    throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Quantity must be a positive integer.", "Input Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            try (Connection conn = com.library.utils.DatabaseConnection.getInstance().getConnection();
                    PreparedStatement pstmt = conn.prepareStatement(
                            "INSERT INTO books (title, author, isbn, publisher, quantity, available_quantity, category) VALUES (?, ?, ?, ?, ?, ?, ?)")) {
                pstmt.setString(1, title);
                pstmt.setString(2, author);
                pstmt.setString(3, isbn);
                pstmt.setString(4, publisher);
                pstmt.setInt(5, quantity);
                pstmt.setInt(6, quantity);
                pstmt.setString(7, category);
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(dialog, "Book added successfully!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                loadBooks();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Error adding book: " + ex.getMessage(), "Database Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
        // Cancel button logic
        cancelButton.addActionListener(e -> dialog.dispose());

        gbc.gridx = 0;
        gbc.gridy = 6;
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
        JTextField availableField = new JTextField(String.valueOf(availableCopies), 20);
        JComboBox<String> categoryCombo = new JComboBox<>(
                new String[] { "Fiction", "Non-Fiction", "Science", "History", "Biography", "Children", "Comics",
                        "Mystery", "Romance", "Fantasy", "Technology", "Self-Help", "Education", "Poetry", "Art",
                        "Travel", "Health", "Religion", "Business" });
        categoryCombo.setSelectedItem(category);

        // Add components to dialog
        gbc.gridx = 0;
        gbc.gridy = 0;
        dialog.add(new JLabel("Title:"), gbc);
        gbc.gridx = 1;
        dialog.add(titleField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        dialog.add(new JLabel("Author:"), gbc);
        gbc.gridx = 1;
        dialog.add(authorField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        dialog.add(new JLabel("ISBN:"), gbc);
        gbc.gridx = 1;
        dialog.add(isbnField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        dialog.add(new JLabel("Total Copies:"), gbc);
        gbc.gridx = 1;
        dialog.add(quantityField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        dialog.add(new JLabel("Available Copies:"), gbc);
        gbc.gridx = 1;
        dialog.add(availableField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        dialog.add(new JLabel("Category:"), gbc);
        gbc.gridx = 1;
        dialog.add(categoryCombo, gbc);

        // Add buttons
        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        com.library.views.panels.ModernButtonStyler.style(saveButton);
        com.library.views.panels.ModernButtonStyler.style(cancelButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        // Save button logic
        saveButton.addActionListener(e -> {
            String newTitle = titleField.getText().trim();
            String newAuthor = authorField.getText().trim();
            String newIsbn = isbnField.getText().trim();
            String newQuantityStr = quantityField.getText().trim();
            String newAvailableStr = availableField.getText().trim();
            String newCategory = (String) categoryCombo.getSelectedItem();
            if (newTitle.isEmpty() || newAuthor.isEmpty() || newIsbn.isEmpty() || newQuantityStr.isEmpty()
                    || newAvailableStr.isEmpty() || newCategory == null) {
                JOptionPane.showMessageDialog(dialog, "Please fill in all required fields.", "Input Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            int newQuantity, newAvailable;
            try {
                newQuantity = Integer.parseInt(newQuantityStr);
                newAvailable = Integer.parseInt(newAvailableStr);
                if (newQuantity < 1 || newAvailable < 0 || newAvailable > newQuantity)
                    throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Total Copies must be a positive integer and Available Copies must be between 0 and Total Copies.",
                        "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try (Connection conn = com.library.utils.DatabaseConnection.getInstance().getConnection();
                    PreparedStatement pstmt = conn.prepareStatement(
                            "UPDATE books SET title=?, author=?, isbn=?, quantity=?, available_quantity=?, category=? WHERE id=?")) {
                pstmt.setString(1, newTitle);
                pstmt.setString(2, newAuthor);
                pstmt.setString(3, newIsbn);
                pstmt.setInt(4, newQuantity);
                pstmt.setInt(5, newAvailable);
                pstmt.setString(6, newCategory);
                pstmt.setInt(7, bookId);
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(dialog, "Book updated successfully!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                loadBooks();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Error updating book: " + ex.getMessage(), "Database Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
        // Cancel button logic
        cancelButton.addActionListener(e -> dialog.dispose());

        gbc.gridx = 0;
        gbc.gridy = 6;
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