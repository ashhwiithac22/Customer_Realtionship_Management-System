import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.Files;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.List;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.concurrent.atomic.AtomicReference;
import com.toedter.calendar.JDateChooser;

public class CRMDashboard extends JFrame {
    private JTabbedPane tabbedPane;
    private DefaultTableModel salesmanTableModel, customerTableModel, orderTableModel, transactionTableModel, loginHistoryTableModel;
    private JLabel userInfoLabel;
    private String currentUser = "Admin";

    // Professional color scheme
    private Color primaryColor = new Color(25, 42, 86);       // Dark blue-gray
    private Color secondaryColor = new Color(64, 92, 144);     // Medium blue-gray
    private Color accentColor = new Color(20, 224, 243);       // Teal accent
    private Color backgroundColor = new Color(30, 38, 59); // Light gray background
    private Color textColor = new Color(224, 224, 224);          // Dark text
    private Color lightTextColor = new Color(170, 179, 193);  // Light text

    private File photoFile;
    private boolean documentFile;
    private JTable loginHistoryTable;
    private static final int PASSPORT_WIDTH = 200;
    private static final int PASSPORT_HEIGHT = 150;

    // Database connection
    private Connection connection;

    public CRMDashboard() {
        // Initialize database connection
        initializeDatabase();

        setTitle("Professional CRM Dashboard");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Record login
        recordLogin("login");

        // Main panel with improved layout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(primaryColor);

        // Header panel
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Tabbed pane with working tabs
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabbedPane.setBackground(secondaryColor);
        tabbedPane.setForeground(lightTextColor);

        // Add all tabs with working input fields
        addSalesmanTab();
        addCustomerTab();
        addOrderTab();
        addTransactionTab();
        addLoginHistoryTab();

        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        add(mainPanel);

        // Load initial data
        refreshData();
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(primaryColor);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        headerPanel.setPreferredSize(new Dimension(getWidth(), 80));

        // Title with logo
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        titlePanel.setOpaque(false);

        // Logo (can be replaced with actual image)
        JLabel logoLabel = new JLabel("CRM");
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        logoLabel.setForeground(accentColor);
        titlePanel.add(logoLabel);

        JLabel titleLabel = new JLabel("DASHBOARD");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(lightTextColor);
        titlePanel.add(titleLabel);

        headerPanel.add(titlePanel, BorderLayout.WEST);

        // User info
        userInfoLabel = new JLabel("Welcome, " + currentUser);
        userInfoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        userInfoLabel.setForeground(lightTextColor);
        headerPanel.add(userInfoLabel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);

        JButton refreshButton = new JButton("Refresh");
        styleButton(refreshButton, accentColor);
        refreshButton.addActionListener(e -> refreshData());
        buttonPanel.add(refreshButton);

        JButton logoutButton = new JButton("Logout");
        styleButton(logoutButton, new Color(210, 50, 45));
        logoutButton.addActionListener(e -> {
            recordLogin("logout");
            System.exit(0);
        });
        buttonPanel.add(logoutButton);

        headerPanel.add(buttonPanel, BorderLayout.EAST);
        return headerPanel;
    }

    private void initializeDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/crm",
                    "root",
                    "AshwithaChandru*1" // Replace with your actual password
            );
        } catch (Exception e) {
            showErrorMessage("Database Connection Error", "Failed to connect to database: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void refreshData() {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                loadSalesmen();
                loadCustomers();
                loadOrders();
                loadTransactions();
                loadLoginHistory();
                return null;
            }

            @Override
            protected void done() {
                showSuccessMessage("Data refreshed successfully!");
            }
        };
        worker.execute();
    }

    private void loadSalesmen() {
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM salesmen")) {

            salesmanTableModel.setRowCount(0);

            while (rs.next()) {
                byte[] photoData = rs.getBytes("photo");
                Icon photoIcon = null;

                if (photoData != null) {
                    photoIcon = new ImageIcon(photoData);
                } else {
                    // Default icon if no photo
                    photoIcon = new ImageIcon();
                }

                String docInfo = rs.getBytes("document") != null ?
                        "View Document (" + rs.getString("document_name") + ")" : "No Document";

                salesmanTableModel.addRow(new Object[]{
                        rs.getInt("id"),
                        photoIcon,
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        docInfo
                });
            }
        } catch (SQLException e) {
            showErrorMessage("Database Error", "Failed to load salesmen: " + e.getMessage());
        }
    }

    private void loadCustomers() {
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM customers")) {

            customerTableModel.setRowCount(0);

            while (rs.next()) {
                byte[] photoData = rs.getBytes("photo");
                Icon photoIcon = null;

                if (photoData != null) {
                    photoIcon = new ImageIcon(photoData);
                } else {
                    // Default icon if no photo
                    photoIcon = new ImageIcon();
                }

                String docInfo = rs.getBytes("document") != null ?
                        "View Document (" + rs.getString("document_name") + ")" : "No Document";

                customerTableModel.addRow(new Object[]{
                        rs.getInt("id"),
                        photoIcon,
                        rs.getString("name"),
                        rs.getString("company"),
                        rs.getString("email"),
                        docInfo
                });
            }
        } catch (SQLException e) {
            showErrorMessage("Database Error", "Failed to load customers: " + e.getMessage());
        }
    }

    private void loadOrders() {
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT o.*, c.name AS customer_name FROM orders o JOIN customers c ON o.customer_id = c.id")) {

            orderTableModel.setRowCount(0);

            while (rs.next()) {
                orderTableModel.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("customer_name"),
                        rs.getDouble("amount"),
                        rs.getDate("order_date"),
                        rs.getString("status")
                });
            }
        } catch (SQLException e) {
            showErrorMessage("Database Error", "Failed to load orders: " + e.getMessage());
        }
    }

    private void loadTransactions() {
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT t.*, o.id AS order_id FROM transactions t JOIN orders o ON t.order_id = o.id")) {

            transactionTableModel.setRowCount(0);

            while (rs.next()) {
                byte[] receiptData = rs.getBytes("receipt");
                Icon receiptIcon = null;

                if (receiptData != null) {
                    receiptIcon = new ImageIcon(receiptData);
                } else {
                    // Default icon if no receipt
                    receiptIcon = new ImageIcon();
                }

                transactionTableModel.addRow(new Object[]{
                        rs.getInt("id"),
                        receiptIcon,
                        rs.getInt("order_id"),
                        rs.getDouble("amount"),
                        rs.getDate("transaction_date"),
                        rs.getString("payment_method")
                });
            }
        } catch (SQLException e) {
            showErrorMessage("Database Error", "Failed to load transactions: " + e.getMessage());
        }
    }

    private void loadLoginHistory() {
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM vw_login_history ORDER BY timestamp DESC LIMIT 50")) {

            loginHistoryTableModel.setRowCount(0);

            while (rs.next()) {
                loginHistoryTableModel.addRow(new Object[]{
                        rs.getString("username"),
                        rs.getString("full_name"),
                        rs.getString("action"),
                        rs.getTimestamp("timestamp"),
                        rs.getString("ip_address")
                });
            }
        } catch (SQLException e) {
            showErrorMessage("Database Error", "Failed to load login history: " + e.getMessage());
        }
    }

    private void recordLogin(String action) {
        try (PreparedStatement pstmt = connection.prepareStatement(
                "INSERT INTO login_history (user_id, action, ip_address) VALUES (?, ?, ?)")) {

            // For demo, we're using user_id 1 (admin)
            pstmt.setInt(1, 1);
            pstmt.setString(2, action);
            pstmt.setString(3, "127.0.0.1"); // In real app, get actual IP

            pstmt.executeUpdate();

            // Update user's last login/logout
            String updateSql = action.equals("login") ?
                    "UPDATE users SET last_login = NOW() WHERE id = 1" :
                    "UPDATE users SET last_logout = NOW() WHERE id = 1";

            try (Statement stmt = connection.createStatement()) {
                stmt.executeUpdate(updateSql);
            }

        } catch (SQLException e) {
            showErrorMessage("Database Error", "Failed to record login: " + e.getMessage());
        }
    }

    private void styleButton(JButton button, Color bgColor) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bgColor.darker(), 1),
                BorderFactory.createEmptyBorder(8, 20, 8, 20)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.brighter());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
    }

    private void addSalesmanTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setBackground(backgroundColor);

        // Toolbar with working buttons
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        toolbar.setBackground(backgroundColor);

        JButton addButton = new JButton("Add Salesman");
        styleButton(addButton, accentColor);
        addButton.addActionListener(e -> showSalesmanDialog(null));
        toolbar.add(addButton);

        JButton editButton = new JButton("Edit Salesman");
        styleButton(editButton, new Color(243, 156, 18));
        editButton.addActionListener(e -> editSalesman());
        toolbar.add(editButton);

        JButton deleteButton = new JButton("Delete Salesman");
        styleButton(deleteButton, new Color(231, 76, 60));
        deleteButton.addActionListener(e -> deleteSalesman());
        toolbar.add(deleteButton);

        panel.add(toolbar, BorderLayout.NORTH);

        // Table with sample data
        String[] columns = {"ID", "Photo", "Name", "Email", "Phone", "Documents"};
        salesmanTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                return column == 1 ? Icon.class : Object.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(salesmanTableModel);
        styleTable(table);
        table.setRowHeight(PASSPORT_HEIGHT + 20);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        tabbedPane.addTab("Salesmen", new ImageIcon(), panel);
    }

    private void addCustomerTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setBackground(backgroundColor);

        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        toolbar.setBackground(backgroundColor);

        JButton addButton = new JButton("Add Customer");
        styleButton(addButton, accentColor);
        addButton.addActionListener(e -> showCustomerDialog(null));
        toolbar.add(addButton);

        JButton editButton = new JButton("Edit Customer");
        styleButton(editButton, new Color(243, 156, 18));
        editButton.addActionListener(e -> editCustomer());
        toolbar.add(editButton);

        JButton deleteButton = new JButton("Delete Customer");
        styleButton(deleteButton, new Color(231, 76, 60));
        deleteButton.addActionListener(e -> deleteCustomer());
        toolbar.add(deleteButton);

        panel.add(toolbar, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Photo", "Name", "Company", "Email", "Documents"};
        customerTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                return column == 1 ? Icon.class : Object.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(customerTableModel);
        styleTable(table);
        table.setRowHeight(PASSPORT_HEIGHT + 20);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        tabbedPane.addTab("Customers", new ImageIcon(), panel);
    }

    private void addOrderTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setBackground(backgroundColor);

        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        toolbar.setBackground(backgroundColor);

        JButton addButton = new JButton("Add Order");
        styleButton(addButton, accentColor);
        addButton.addActionListener(e -> showOrderDialog(null));
        toolbar.add(addButton);

        JButton editButton = new JButton("Edit Order");
        styleButton(editButton, new Color(243, 156, 18));
        editButton.addActionListener(e -> editOrder());
        toolbar.add(editButton);

        JButton deleteButton = new JButton("Delete Order");
        styleButton(deleteButton, new Color(231, 76, 60));
        deleteButton.addActionListener(e -> deleteOrder());
        toolbar.add(deleteButton);

        panel.add(toolbar, BorderLayout.NORTH);

        // Table
        String[] columns = {"Order ID", "Customer", "Amount", "Date", "Status"};
        orderTableModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(orderTableModel);
        styleTable(table);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        tabbedPane.addTab("Orders", new ImageIcon(), panel);
    }

    private void addTransactionTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setBackground(backgroundColor);

        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        toolbar.setBackground(backgroundColor);

        JButton addButton = new JButton("Add Transaction");
        styleButton(addButton, accentColor);
        addButton.addActionListener(e -> showTransactionDialog(null));
        toolbar.add(addButton);

        JButton editButton = new JButton("Edit Transaction");
        styleButton(editButton, new Color(243, 156, 18));
        editButton.addActionListener(e -> editTransaction());
        toolbar.add(editButton);

        JButton deleteButton = new JButton("Delete Transaction");
        styleButton(deleteButton, new Color(231, 76, 60));
        deleteButton.addActionListener(e -> deleteTransaction());
        toolbar.add(deleteButton);

        panel.add(toolbar, BorderLayout.NORTH);

        // Table
        String[] columns = {"Transaction ID", "Receipt", "Order ID", "Amount", "Date", "Payment Method"};
        transactionTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                return column == 1 ? Icon.class : Object.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(transactionTableModel);
        styleTable(table);
        table.setRowHeight(PASSPORT_HEIGHT + 20);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        tabbedPane.addTab("Transactions", new ImageIcon(), panel);
    }

    private void addLoginHistoryTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setBackground(backgroundColor);

        // Table with additional columns
        String[] columns = {"Username", "Full Name", "Action", "Timestamp", "IP Address"};
        loginHistoryTableModel = new DefaultTableModel(columns, 0);

        loginHistoryTable = new JTable(loginHistoryTableModel);
        styleTable(loginHistoryTable);
        panel.add(new JScrollPane(loginHistoryTable), BorderLayout.CENTER);

        tabbedPane.addTab("Login History", new ImageIcon(), panel);
    }

    private BufferedImage resizeImageToPassportSize(BufferedImage originalImage) {
        BufferedImage resizedImage = new BufferedImage(PASSPORT_WIDTH, PASSPORT_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, PASSPORT_WIDTH, PASSPORT_HEIGHT, null);
        g.dispose();
        return resizedImage;
    }

    private void showSalesmanDialog(Map<String, Object> existingData) {
        JDialog dialog = new JDialog(this, existingData == null ? "Add Salesman" : "Edit Salesman", true);
        dialog.setSize(600, 550);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(backgroundColor);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(backgroundColor);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Photo upload
        JLabel photoLabel = new JLabel("Photo (Passport Size 200x150):");
        photoLabel.setForeground(textColor);
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(photoLabel, gbc);

        JPanel photoPanel = new JPanel(new BorderLayout());
        photoPanel.setBackground(backgroundColor);

        JLabel imagePreview = new JLabel();
        imagePreview.setPreferredSize(new Dimension(PASSPORT_WIDTH, PASSPORT_HEIGHT));
        imagePreview.setHorizontalAlignment(JLabel.CENTER);
        imagePreview.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        JButton browseButton = new JButton("Browse...");
        styleButton(browseButton, accentColor);
        browseButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setAcceptAllFileFilterUsed(false);
            fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Image Files", "jpg", "jpeg", "png", "gif"));

            if (fileChooser.showOpenDialog(dialog) == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                try {
                    BufferedImage originalImage = ImageIO.read(selectedFile);
                    BufferedImage resizedImage = resizeImageToPassportSize(originalImage);
                    ImageIcon icon = new ImageIcon(resizedImage);
                    imagePreview.setIcon(icon);
                    photoFile = selectedFile;
                } catch (IOException ex) {
                    showErrorMessage("Image Error", "Error loading image: " + ex.getMessage());
                }
            }
        });

        photoPanel.add(imagePreview, BorderLayout.CENTER);
        photoPanel.add(browseButton, BorderLayout.SOUTH);

        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(photoPanel, gbc);

        // Name field
        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setForeground(textColor);
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(nameLabel, gbc);

        JTextField nameField = new JTextField(30);
        styleTextField(nameField);
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(nameField, gbc);

        // Email field
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setForeground(textColor);
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(emailLabel, gbc);

        JTextField emailField = new JTextField(30);
        styleTextField(emailField);
        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(emailField, gbc);

        // Phone field
        JLabel phoneLabel = new JLabel("Phone:");
        phoneLabel.setForeground(textColor);
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(phoneLabel, gbc);

        JTextField phoneField = new JTextField(30);
        styleTextField(phoneField);
        gbc.gridx = 1;
        gbc.gridy = 3;
        panel.add(phoneField, gbc);

        // Document upload
        JLabel docLabelTitle = new JLabel("Documents (Optional):");
        docLabelTitle.setForeground(textColor);
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(docLabelTitle, gbc);

        JButton docButton = new JButton("Upload Document");
        styleButton(docButton, accentColor);

        JLabel docLabel = new JLabel("No document selected");
        docLabel.setForeground(textColor);

        JPanel docPanel = new JPanel(new BorderLayout(5, 0));
        docPanel.setBackground(backgroundColor);
        docPanel.add(docButton, BorderLayout.WEST);
        docPanel.add(docLabel, BorderLayout.CENTER);

        // Document preview button
        JButton previewDocButton = new JButton("Preview");
        styleButton(previewDocButton, new Color(46, 125, 50));
        previewDocButton.setEnabled(false);
        docPanel.add(previewDocButton, BorderLayout.EAST);

        gbc.gridx = 1;
        gbc.gridy = 4;
        panel.add(docPanel, gbc);

        // Variables to hold the files
        AtomicReference<File> documentFileRef = new AtomicReference<>();

        docButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setAcceptAllFileFilterUsed(false);
            fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Documents", "pdf", "doc", "docx", "txt"));

            if (fileChooser.showOpenDialog(dialog) == JFileChooser.APPROVE_OPTION) {
                documentFileRef.set(fileChooser.getSelectedFile());
                docLabel.setText(documentFileRef.get().getName());
                previewDocButton.setEnabled(true);
            }
        });

        previewDocButton.addActionListener(e -> {
            if (documentFileRef.get() != null) {
                try {
                    Desktop.getDesktop().open(documentFileRef.get());
                } catch (IOException ex) {
                    showErrorMessage("Document Error", "Error opening document: " + ex.getMessage());
                }
            }
        });

        // If editing, populate fields
        if (existingData != null) {
            nameField.setText((String) existingData.get("name"));
            emailField.setText((String) existingData.get("email"));
            phoneField.setText((String) existingData.get("phone"));

            byte[] photoData = (byte[]) existingData.get("photo");
            if (photoData != null) {
                ImageIcon icon = new ImageIcon(photoData);
                imagePreview.setIcon(new ImageIcon(icon.getImage().getScaledInstance(PASSPORT_WIDTH, PASSPORT_HEIGHT, Image.SCALE_SMOOTH)));
            }

            byte[] docData = (byte[]) existingData.get("document");
            if (docData != null) {
                docLabel.setText((String) existingData.get("document_name"));
                previewDocButton.setEnabled(true);
            }
        }

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(backgroundColor);

        JButton saveButton = new JButton("Save");
        styleButton(saveButton, accentColor);
        saveButton.addActionListener(e -> {
            // Validate inputs
            if (!validateName(nameField.getText())) {
                showErrorMessage("Validation Error", "Name must contain only alphabets and spaces");
                return;
            }

            if (!validateEmail(emailField.getText())) {
                showErrorMessage("Validation Error", "Please enter a valid email address");
                return;
            }

            if (!validatePhone(phoneField.getText())) {
                showErrorMessage("Validation Error", "Phone number must be 10 digits");
                return;
            }

            try {
                // Prepare the SQL
                String sql;
                if (existingData == null) {
                    sql = "INSERT INTO salesmen (name, email, phone, photo, document, document_name) VALUES (?, ?, ?, ?, ?, ?)";
                } else {
                    sql = "UPDATE salesmen SET name = ?, email = ?, phone = ?, photo = ?, document = ?, document_name = ? WHERE id = ?";
                }

                try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    pstmt.setString(1, nameField.getText());
                    pstmt.setString(2, emailField.getText());
                    pstmt.setString(3, phoneField.getText());

                    // Handle photo
                    if (imagePreview.getIcon() != null && photoFile != null) {
                        BufferedImage bImage = ImageIO.read(photoFile);
                        BufferedImage resizedImage = resizeImageToPassportSize(bImage);
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        ImageIO.write(resizedImage, "jpg", bos);
                        pstmt.setBytes(4, bos.toByteArray());
                    } else if (existingData != null && existingData.get("photo") != null) {
                        pstmt.setBytes(4, (byte[]) existingData.get("photo"));
                    } else {
                        pstmt.setNull(4, Types.BLOB);
                    }

                    // Handle document
                    if (documentFileRef.get() != null) {
                        pstmt.setBytes(5, Files.readAllBytes(documentFileRef.get().toPath()));
                        pstmt.setString(6, documentFileRef.get().getName());
                    } else if (existingData != null && existingData.get("document") != null) {
                        pstmt.setBytes(5, (byte[]) existingData.get("document"));
                        pstmt.setString(6, (String) existingData.get("document_name"));
                    } else {
                        pstmt.setNull(5, Types.BLOB);
                        pstmt.setNull(6, Types.VARCHAR);
                    }

                    if (existingData != null) {
                        pstmt.setInt(7, (Integer) existingData.get("id"));
                    }

                    pstmt.executeUpdate();

                    if (existingData == null) {
                        showSuccessMessage("Salesman added successfully!");
                    } else {
                        showSuccessMessage("Salesman updated successfully!");
                    }

                    refreshData();
                    dialog.dispose();
                }
            } catch (IOException | SQLException ex) {
                showErrorMessage("Save Error", "Error saving salesman: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        JButton cancelButton = new JButton("Cancel");
        styleButton(cancelButton, new Color(231, 76, 60));
        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);

        // Set focus to first field
        nameField.requestFocusInWindow();
    }

    private void showCustomerDialog(Map<String, Object> existingData) {
        JDialog dialog = new JDialog(this, existingData == null ? "Add Customer" : "Edit Customer", true);
        dialog.setSize(600, 550);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(backgroundColor);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(backgroundColor);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Photo upload
        JLabel photoLabel = new JLabel("Photo (Passport Size 200x150):");
        photoLabel.setForeground(textColor);
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(photoLabel, gbc);

        JPanel photoPanel = new JPanel(new BorderLayout());
        photoPanel.setBackground(backgroundColor);

        JLabel imagePreview = new JLabel();
        imagePreview.setPreferredSize(new Dimension(PASSPORT_WIDTH, PASSPORT_HEIGHT));
        imagePreview.setHorizontalAlignment(JLabel.CENTER);
        imagePreview.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        JButton browseButton = new JButton("Browse...");
        styleButton(browseButton, accentColor);
        browseButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setAcceptAllFileFilterUsed(false);
            fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Image Files", "jpg", "jpeg", "png", "gif"));

            if (fileChooser.showOpenDialog(dialog) == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                try {
                    BufferedImage originalImage = ImageIO.read(selectedFile);
                    BufferedImage resizedImage = resizeImageToPassportSize(originalImage);
                    ImageIcon icon = new ImageIcon(resizedImage);
                    imagePreview.setIcon(icon);
                    photoFile = selectedFile;
                } catch (IOException ex) {
                    showErrorMessage("Image Error", "Error loading image: " + ex.getMessage());
                }
            }
        });

        photoPanel.add(imagePreview, BorderLayout.CENTER);
        photoPanel.add(browseButton, BorderLayout.SOUTH);

        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(photoPanel, gbc);

        // Name field
        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setForeground(textColor);
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(nameLabel, gbc);

        JTextField nameField = new JTextField(30);
        styleTextField(nameField);
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(nameField, gbc);

        // Company field
        JLabel companyLabel = new JLabel("Company:");
        companyLabel.setForeground(textColor);
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(companyLabel, gbc);

        JTextField companyField = new JTextField(30);
        styleTextField(companyField);
        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(companyField, gbc);

        // Email field
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setForeground(textColor);
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(emailLabel, gbc);

        JTextField emailField = new JTextField(30);
        styleTextField(emailField);
        gbc.gridx = 1;
        gbc.gridy = 3;
        panel.add(emailField, gbc);

        // Phone field
        JLabel phoneLabel = new JLabel("Phone:");
        phoneLabel.setForeground(textColor);
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(phoneLabel, gbc);

        JTextField phoneField = new JTextField(30);
        styleTextField(phoneField);
        gbc.gridx = 1;
        gbc.gridy = 4;
        panel.add(phoneField, gbc);

        // Document upload
        JLabel docLabelTitle = new JLabel("Documents (Optional):");
        docLabelTitle.setForeground(textColor);
        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(docLabelTitle, gbc);

        JButton docButton = new JButton("Upload Document");
        styleButton(docButton, accentColor);

        JLabel docLabel = new JLabel("No document selected");
        docLabel.setForeground(textColor);

        JPanel docPanel = new JPanel(new BorderLayout(5, 0));
        docPanel.setBackground(backgroundColor);
        docPanel.add(docButton, BorderLayout.WEST);
        docPanel.add(docLabel, BorderLayout.CENTER);

        // Document preview button
        JButton previewDocButton = new JButton("Preview");
        styleButton(previewDocButton, new Color(46, 125, 50));
        previewDocButton.setEnabled(false);
        docPanel.add(previewDocButton, BorderLayout.EAST);

        gbc.gridx = 1;
        gbc.gridy = 5;
        panel.add(docPanel, gbc);

        // Variables to hold the files
        AtomicReference<File> documentFileRef = new AtomicReference<>();

        docButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setAcceptAllFileFilterUsed(false);
            fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Documents", "pdf", "doc", "docx", "txt"));

            if (fileChooser.showOpenDialog(dialog) == JFileChooser.APPROVE_OPTION) {
                documentFileRef.set(fileChooser.getSelectedFile());
                docLabel.setText(documentFileRef.get().getName());
                previewDocButton.setEnabled(true);
            }
        });

        previewDocButton.addActionListener(e -> {
            if (documentFileRef.get() != null) {
                try {
                    Desktop.getDesktop().open(documentFileRef.get());
                } catch (IOException ex) {
                    showErrorMessage("Document Error", "Error opening document: " + ex.getMessage());
                }
            }
        });

        // If editing, populate fields
        if (existingData != null) {
            nameField.setText((String) existingData.get("name"));
            companyField.setText((String) existingData.get("company"));
            emailField.setText((String) existingData.get("email"));
            phoneField.setText((String) existingData.get("phone"));

            byte[] photoData = (byte[]) existingData.get("photo");
            if (photoData != null) {
                ImageIcon icon = new ImageIcon(photoData);
                imagePreview.setIcon(new ImageIcon(icon.getImage().getScaledInstance(PASSPORT_WIDTH, PASSPORT_HEIGHT, Image.SCALE_SMOOTH)));
            }

            byte[] docData = (byte[]) existingData.get("document");
            if (docData != null) {
                docLabel.setText((String) existingData.get("document_name"));
                previewDocButton.setEnabled(true);
            }
        }

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(backgroundColor);

        JButton saveButton = new JButton("Save");
        styleButton(saveButton, accentColor);
        saveButton.addActionListener(e -> {
            // Validate inputs
            if (!validateName(nameField.getText())) {
                showErrorMessage("Validation Error", "Name must contain only alphabets and spaces");
                return;
            }

            if (!validateEmail(emailField.getText())) {
                showErrorMessage("Validation Error", "Please enter a valid email address");
                return;
            }

            if (!validatePhone(phoneField.getText())) {
                showErrorMessage("Validation Error", "Phone number must be 10 digits");
                return;
            }

            try {
                // Prepare the SQL
                String sql;
                if (existingData == null) {
                    sql = "INSERT INTO customers (name, company, email, phone, photo, document, document_name) VALUES (?, ?, ?, ?, ?, ?, ?)";
                } else {
                    sql = "UPDATE customers SET name = ?, company = ?, email = ?, phone = ?, photo = ?, document = ?, document_name = ? WHERE id = ?";
                }

                try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    pstmt.setString(1, nameField.getText());
                    pstmt.setString(2, companyField.getText());
                    pstmt.setString(3, emailField.getText());
                    pstmt.setString(4, phoneField.getText());

                    // Handle photo
                    if (imagePreview.getIcon() != null && photoFile != null) {
                        BufferedImage bImage = ImageIO.read(photoFile);
                        BufferedImage resizedImage = resizeImageToPassportSize(bImage);
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        ImageIO.write(resizedImage, "jpg", bos);
                        pstmt.setBytes(5, bos.toByteArray());
                    } else if (existingData != null && existingData.get("photo") != null) {
                        pstmt.setBytes(5, (byte[]) existingData.get("photo"));
                    } else {
                        pstmt.setNull(5, Types.BLOB);
                    }

                    // Handle document
                    if (documentFileRef.get() != null) {
                        pstmt.setBytes(6, Files.readAllBytes(documentFileRef.get().toPath()));
                        pstmt.setString(7, documentFileRef.get().getName());
                    } else if (existingData != null && existingData.get("document") != null) {
                        pstmt.setBytes(6, (byte[]) existingData.get("document"));
                        pstmt.setString(7, (String) existingData.get("document_name"));
                    } else {
                        pstmt.setNull(6, Types.BLOB);
                        pstmt.setNull(7, Types.VARCHAR);
                    }

                    if (existingData != null) {
                        pstmt.setInt(8, (Integer) existingData.get("id"));
                    }

                    pstmt.executeUpdate();

                    if (existingData == null) {
                        showSuccessMessage("Customer added successfully!");
                    } else {
                        showSuccessMessage("Customer updated successfully!");
                    }

                    refreshData();
                    dialog.dispose();
                }
            } catch (IOException | SQLException ex) {
                showErrorMessage("Save Error", "Error saving customer: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        JButton cancelButton = new JButton("Cancel");
        styleButton(cancelButton, new Color(231, 76, 60));
        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);

        // Set focus to first field
        nameField.requestFocusInWindow();
    }

    private void showOrderDialog(Map<String, Object> existingData) {
        JDialog dialog = new JDialog(this, existingData == null ? "Add Order" : "Edit Order", true);
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(backgroundColor);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(backgroundColor);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Customer field
        JLabel customerLabel = new JLabel("Customer:");
        customerLabel.setForeground(textColor);
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(customerLabel, gbc);

        JComboBox<String> customerCombo = new JComboBox<>();
        styleComboBox(customerCombo);
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, name FROM customers ORDER BY name")) {
            while (rs.next()) {
                customerCombo.addItem(rs.getString("name") + " (ID: " + rs.getInt("id") + ")");
            }
        } catch (SQLException e) {
            showErrorMessage("Database Error", "Failed to load customers: " + e.getMessage());
        }
        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(customerCombo, gbc);

        // Salesman field
        JLabel salesmanLabel = new JLabel("Salesman:");
        salesmanLabel.setForeground(textColor);
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(salesmanLabel, gbc);

        JComboBox<String> salesmanCombo = new JComboBox<>();
        styleComboBox(salesmanCombo);
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, name FROM salesmen ORDER BY name")) {
            while (rs.next()) {
                salesmanCombo.addItem(rs.getString("name") + " (ID: " + rs.getInt("id") + ")");
            }
        } catch (SQLException e) {
            showErrorMessage("Database Error", "Failed to load salesmen: " + e.getMessage());
        }
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(salesmanCombo, gbc);

        // Amount field
        JLabel amountLabel = new JLabel("Amount:");
        amountLabel.setForeground(textColor);
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(amountLabel, gbc);

        JTextField amountField = new JTextField(30);
        styleTextField(amountField);
        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(amountField, gbc);

        // Date field with calendar
        JLabel dateLabel = new JLabel("Order Date:");
        dateLabel.setForeground(textColor);
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(dateLabel, gbc);

        JDateChooser dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("yyyy-MM-dd");
        dateChooser.setDate(new Date());
        JPanel datePanel = new JPanel(new BorderLayout());
        datePanel.setBackground(backgroundColor);
        datePanel.add(dateChooser, BorderLayout.CENTER);
        gbc.gridx = 1;
        gbc.gridy = 3;
        panel.add(datePanel, gbc);

        // Status field
        JLabel statusLabel = new JLabel("Status:");
        statusLabel.setForeground(textColor);
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(statusLabel, gbc);

        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"pending", "processing", "completed", "cancelled"});
        styleComboBox(statusCombo);
        gbc.gridx = 1;
        gbc.gridy = 4;
        panel.add(statusCombo, gbc);

        // If editing, populate fields
        if (existingData != null) {
            // Set customer
            for (int i = 0; i < customerCombo.getItemCount(); i++) {
                if (customerCombo.getItemAt(i).contains("(ID: " + existingData.get("customer_id") + ")")) {
                    customerCombo.setSelectedIndex(i);
                    break;
                }
            }

            // Set salesman
            for (int i = 0; i < salesmanCombo.getItemCount(); i++) {
                if (salesmanCombo.getItemAt(i).contains("(ID: " + existingData.get("salesman_id") + ")")) {
                    salesmanCombo.setSelectedIndex(i);
                    break;
                }
            }

            amountField.setText(existingData.get("amount").toString());
            dateChooser.setDate((Date) existingData.get("order_date"));
            statusCombo.setSelectedItem(existingData.get("status"));
        }

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(backgroundColor);

        JButton saveButton = new JButton("Save");
        styleButton(saveButton, accentColor);
        saveButton.addActionListener(e -> {
            // Validate inputs
            if (amountField.getText().isEmpty() || !amountField.getText().matches("^\\d+(\\.\\d{1,2})?$")) {
                showErrorMessage("Validation Error", "Please enter a valid amount (numbers only)");
                return;
            }

            try {
                // Extract customer ID from combo box
                String customerStr = (String) customerCombo.getSelectedItem();
                int customerId = Integer.parseInt(customerStr.substring(customerStr.lastIndexOf("ID: ") + 4, customerStr.length() - 1));

                // Extract salesman ID from combo box
                String salesmanStr = (String) salesmanCombo.getSelectedItem();
                int salesmanId = Integer.parseInt(salesmanStr.substring(salesmanStr.lastIndexOf("ID: ") + 4, salesmanStr.length() - 1));

                // Prepare the SQL
                String sql;
                if (existingData == null) {
                    sql = "INSERT INTO orders (customer_id, salesman_id, amount, order_date, status) VALUES (?, ?, ?, ?, ?)";
                } else {
                    sql = "UPDATE orders SET customer_id = ?, salesman_id = ?, amount = ?, order_date = ?, status = ? WHERE id = ?";
                }

                try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    pstmt.setInt(1, customerId);
                    pstmt.setInt(2, salesmanId);
                    pstmt.setDouble(3, Double.parseDouble(amountField.getText()));
                    pstmt.setDate(4, new java.sql.Date(dateChooser.getDate().getTime()));
                    pstmt.setString(5, (String) statusCombo.getSelectedItem());

                    if (existingData != null) {
                        pstmt.setInt(6, (Integer) existingData.get("id"));
                    }

                    pstmt.executeUpdate();

                    if (existingData == null) {
                        showSuccessMessage("Order added successfully!");
                    } else {
                        showSuccessMessage("Order updated successfully!");
                    }

                    refreshData();
                    dialog.dispose();
                }
            } catch (NumberFormatException | SQLException ex) {
                showErrorMessage("Save Error", "Error saving order: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        JButton cancelButton = new JButton("Cancel");
        styleButton(cancelButton, new Color(231, 76, 60));
        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);

        // Set focus to first field
        customerCombo.requestFocusInWindow();
    }

    private void showTransactionDialog(Map<String, Object> existingData) {
        JDialog dialog = new JDialog(this, existingData == null ? "Add Transaction" : "Edit Transaction", true);
        dialog.setSize(600, 550);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(backgroundColor);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(backgroundColor);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Order ID field
        JLabel orderIdLabel = new JLabel("Order ID:");
        orderIdLabel.setForeground(textColor);
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(orderIdLabel, gbc);

        JComboBox<String> orderCombo = new JComboBox<>();
        styleComboBox(orderCombo);
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id FROM orders ORDER BY id")) {
            while (rs.next()) {
                orderCombo.addItem("Order #" + rs.getInt("id"));
            }
        } catch (SQLException e) {
            showErrorMessage("Database Error", "Failed to load orders: " + e.getMessage());
        }
        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(orderCombo, gbc);

        // Amount field
        JLabel amountLabel = new JLabel("Amount:");
        amountLabel.setForeground(textColor);
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(amountLabel, gbc);

        JTextField amountField = new JTextField(30);
        styleTextField(amountField);
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(amountField, gbc);

        // Date field with calendar
        JLabel dateLabel = new JLabel("Transaction Date:");
        dateLabel.setForeground(textColor);
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(dateLabel, gbc);

        JDateChooser dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("yyyy-MM-dd");
        dateChooser.setDate(new Date());
        JPanel datePanel = new JPanel(new BorderLayout());
        datePanel.setBackground(backgroundColor);
        datePanel.add(dateChooser, BorderLayout.CENTER);
        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(datePanel, gbc);

        // Payment Method field
        JLabel paymentLabel = new JLabel("Payment Method:");
        paymentLabel.setForeground(textColor);
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(paymentLabel, gbc);

        JComboBox<String> paymentCombo = new JComboBox<>(new String[]{"cash", "credit_card", "bank_transfer", "check", "other"});
        styleComboBox(paymentCombo);
        gbc.gridx = 1;
        gbc.gridy = 3;
        panel.add(paymentCombo, gbc);

        // Receipt upload
        JLabel receiptLabel = new JLabel("Receipt (Passport Size 200x150):");
        receiptLabel.setForeground(textColor);
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(receiptLabel, gbc);

        JPanel receiptPanel = new JPanel(new BorderLayout());
        receiptPanel.setBackground(backgroundColor);

        JLabel imagePreview = new JLabel();
        imagePreview.setPreferredSize(new Dimension(PASSPORT_WIDTH, PASSPORT_HEIGHT));
        imagePreview.setHorizontalAlignment(JLabel.CENTER);
        imagePreview.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        JButton browseButton = new JButton("Browse...");
        styleButton(browseButton, accentColor);
        browseButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setAcceptAllFileFilterUsed(false);
            fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Image Files", "jpg", "jpeg", "png", "gif"));

            if (fileChooser.showOpenDialog(dialog) == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                try {
                    BufferedImage originalImage = ImageIO.read(selectedFile);
                    BufferedImage resizedImage = resizeImageToPassportSize(originalImage);
                    ImageIcon icon = new ImageIcon(resizedImage);
                    imagePreview.setIcon(icon);
                    photoFile = selectedFile;
                } catch (IOException ex) {
                    showErrorMessage("Image Error", "Error loading image: " + ex.getMessage());
                }
            }
        });

        receiptPanel.add(imagePreview, BorderLayout.CENTER);
        receiptPanel.add(browseButton, BorderLayout.SOUTH);

        gbc.gridx = 1;
        gbc.gridy = 4;
        panel.add(receiptPanel, gbc);

        // If editing, populate fields
        if (existingData != null) {
            // Set order
            for (int i = 0; i < orderCombo.getItemCount(); i++) {
                if (orderCombo.getItemAt(i).contains("#" + existingData.get("order_id"))) {
                    orderCombo.setSelectedIndex(i);
                    break;
                }
            }

            amountField.setText(existingData.get("amount").toString());
            dateChooser.setDate((Date) existingData.get("transaction_date"));
            paymentCombo.setSelectedItem(existingData.get("payment_method"));

            byte[] receiptData = (byte[]) existingData.get("receipt");
            if (receiptData != null) {
                ImageIcon icon = new ImageIcon(receiptData);
                imagePreview.setIcon(new ImageIcon(icon.getImage().getScaledInstance(PASSPORT_WIDTH, PASSPORT_HEIGHT, Image.SCALE_SMOOTH)));
            }
        }

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(backgroundColor);

        JButton saveButton = new JButton("Save");
        styleButton(saveButton, accentColor);
        saveButton.addActionListener(e -> {
            // Validate inputs
            if (amountField.getText().isEmpty() || !amountField.getText().matches("^\\d+(\\.\\d{1,2})?$")) {
                showErrorMessage("Validation Error", "Please enter a valid amount (numbers only)");
                return;
            }

            try {
                // Extract order ID from combo box
                String orderStr = (String) orderCombo.getSelectedItem();
                int orderId = Integer.parseInt(orderStr.substring(orderStr.lastIndexOf("#") + 1));

                // Prepare the SQL
                String sql;
                if (existingData == null) {
                    sql = "INSERT INTO transactions (order_id, amount, transaction_date, payment_method, receipt) VALUES (?, ?, ?, ?, ?)";
                } else {
                    sql = "UPDATE transactions SET order_id = ?, amount = ?, transaction_date = ?, payment_method = ?, receipt = ? WHERE id = ?";
                }

                try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    pstmt.setInt(1, orderId);
                    pstmt.setDouble(2, Double.parseDouble(amountField.getText()));
                    pstmt.setDate(3, new java.sql.Date(dateChooser.getDate().getTime()));
                    pstmt.setString(4, (String) paymentCombo.getSelectedItem());

                    // Handle receipt
                    if (imagePreview.getIcon() != null && photoFile != null) {
                        BufferedImage bImage = ImageIO.read(photoFile);
                        BufferedImage resizedImage = resizeImageToPassportSize(bImage);
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        ImageIO.write(resizedImage, "jpg", bos);
                        pstmt.setBytes(5, bos.toByteArray());
                    } else if (existingData != null && existingData.get("receipt") != null) {
                        pstmt.setBytes(5, (byte[]) existingData.get("receipt"));
                    } else {
                        pstmt.setNull(5, Types.BLOB);
                    }

                    if (existingData != null) {
                        pstmt.setInt(6, (Integer) existingData.get("id"));
                    }

                    pstmt.executeUpdate();

                    if (existingData == null) {
                        showSuccessMessage("Transaction added successfully!");
                    } else {
                        showSuccessMessage("Transaction updated successfully!");
                    }

                    refreshData();
                    dialog.dispose();
                }
            } catch (NumberFormatException | IOException | SQLException ex) {
                showErrorMessage("Save Error", "Error saving transaction: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        JButton cancelButton = new JButton("Cancel");
        styleButton(cancelButton, new Color(231, 76, 60));
        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);

        // Set focus to first field
        orderCombo.requestFocusInWindow();
    }

    private void editSalesman() {
        JTable table = getCurrentTable();
        if (table == null) return;

        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            showErrorMessage("Selection Error", "Please select a salesman to edit");
            return;
        }

        int modelRow = table.convertRowIndexToModel(selectedRow);
        int salesmanId = (Integer) salesmanTableModel.getValueAt(modelRow, 0);

        try (PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM salesmen WHERE id = ?")) {
            pstmt.setInt(1, salesmanId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Map<String, Object> salesmanData = new HashMap<>();
                salesmanData.put("id", rs.getInt("id"));
                salesmanData.put("name", rs.getString("name"));
                salesmanData.put("email", rs.getString("email"));
                salesmanData.put("phone", rs.getString("phone"));
                salesmanData.put("photo", rs.getBytes("photo"));
                salesmanData.put("document", rs.getBytes("document"));
                salesmanData.put("document_name", rs.getString("document_name"));

                showSalesmanDialog(salesmanData);
            }
        } catch (SQLException e) {
            showErrorMessage("Database Error", "Failed to load salesman: " + e.getMessage());
        }
    }

    private void deleteSalesman() {
        JTable table = getCurrentTable();
        if (table == null) return;

        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            showErrorMessage("Selection Error", "Please select a salesman to delete");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "<html><b>Are you sure you want to delete this salesman?</b><br>This action cannot be undone.</html>",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            int modelRow = table.convertRowIndexToModel(selectedRow);
            int salesmanId = (Integer) salesmanTableModel.getValueAt(modelRow, 0);

            try (PreparedStatement pstmt = connection.prepareStatement("DELETE FROM salesmen WHERE id = ?")) {
                pstmt.setInt(1, salesmanId);
                pstmt.executeUpdate();
                showSuccessMessage("Salesman deleted successfully!");
                refreshData();
            } catch (SQLException e) {
                showErrorMessage("Delete Error", "Failed to delete salesman: " + e.getMessage());
            }
        }
    }

    private void editCustomer() {
        JTable table = getCurrentTable();
        if (table == null) return;

        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            showErrorMessage("Selection Error", "Please select a customer to edit");
            return;
        }

        int modelRow = table.convertRowIndexToModel(selectedRow);
        int customerId = (Integer) customerTableModel.getValueAt(modelRow, 0);

        try (PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM customers WHERE id = ?")) {
            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Map<String, Object> customerData = new HashMap<>();
                customerData.put("id", rs.getInt("id"));
                customerData.put("name", rs.getString("name"));
                customerData.put("company", rs.getString("company"));
                customerData.put("email", rs.getString("email"));
                customerData.put("phone", rs.getString("phone"));
                customerData.put("photo", rs.getBytes("photo"));
                customerData.put("document", rs.getBytes("document"));
                customerData.put("document_name", rs.getString("document_name"));

                showCustomerDialog(customerData);
            }
        } catch (SQLException e) {
            showErrorMessage("Database Error", "Failed to load customer: " + e.getMessage());
        }
    }

    private void deleteCustomer() {
        JTable table = getCurrentTable();
        if (table == null) return;

        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            showErrorMessage("Selection Error", "Please select a customer to delete");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "<html><b>Are you sure you want to delete this customer?</b><br>This action cannot be undone.</html>",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            int modelRow = table.convertRowIndexToModel(selectedRow);
            int customerId = (Integer) customerTableModel.getValueAt(modelRow, 0);

            try (PreparedStatement pstmt = connection.prepareStatement("DELETE FROM customers WHERE id = ?")) {
                pstmt.setInt(1, customerId);
                pstmt.executeUpdate();
                showSuccessMessage("Customer deleted successfully!");
                refreshData();
            } catch (SQLException e) {
                showErrorMessage("Delete Error", "Failed to delete customer: " + e.getMessage());
            }
        }
    }

    private void editOrder() {
        JTable table = getCurrentTable();
        if (table == null) return;

        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            showErrorMessage("Selection Error", "Please select an order to edit");
            return;
        }

        int modelRow = table.convertRowIndexToModel(selectedRow);
        int orderId = (Integer) orderTableModel.getValueAt(modelRow, 0);

        try (PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM orders WHERE id = ?")) {
            pstmt.setInt(1, orderId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Map<String, Object> orderData = new HashMap<>();
                orderData.put("id", rs.getInt("id"));
                orderData.put("customer_id", rs.getInt("customer_id"));
                orderData.put("salesman_id", rs.getInt("salesman_id"));
                orderData.put("amount", rs.getDouble("amount"));
                orderData.put("order_date", rs.getDate("order_date"));
                orderData.put("status", rs.getString("status"));

                showOrderDialog(orderData);
            }
        } catch (SQLException e) {
            showErrorMessage("Database Error", "Failed to load order: " + e.getMessage());
        }
    }

    private void deleteOrder() {
        JTable table = getCurrentTable();
        if (table == null) return;

        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            showErrorMessage("Selection Error", "Please select an order to delete");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "<html><b>Are you sure you want to delete this order?</b><br>This action cannot be undone.</html>",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            int modelRow = table.convertRowIndexToModel(selectedRow);
            int orderId = (Integer) orderTableModel.getValueAt(modelRow, 0);

            try (PreparedStatement pstmt = connection.prepareStatement("DELETE FROM orders WHERE id = ?")) {
                pstmt.setInt(1, orderId);
                pstmt.executeUpdate();
                showSuccessMessage("Order deleted successfully!");
                refreshData();
            } catch (SQLException e) {
                showErrorMessage("Delete Error", "Failed to delete order: " + e.getMessage());
            }
        }
    }

    private void editTransaction() {
        JTable table = getCurrentTable();
        if (table == null) return;

        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            showErrorMessage("Selection Error", "Please select a transaction to edit");
            return;
        }

        int modelRow = table.convertRowIndexToModel(selectedRow);
        int transactionId = (Integer) transactionTableModel.getValueAt(modelRow, 0);

        try (PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM transactions WHERE id = ?")) {
            pstmt.setInt(1, transactionId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Map<String, Object> transactionData = new HashMap<>();
                transactionData.put("id", rs.getInt("id"));
                transactionData.put("order_id", rs.getInt("order_id"));
                transactionData.put("amount", rs.getDouble("amount"));
                transactionData.put("transaction_date", rs.getDate("transaction_date"));
                transactionData.put("payment_method", rs.getString("payment_method"));
                transactionData.put("receipt", rs.getBytes("receipt"));

                showTransactionDialog(transactionData);
            }
        } catch (SQLException e) {
            showErrorMessage("Database Error", "Failed to load transaction: " + e.getMessage());
        }
    }

    private void deleteTransaction() {
        JTable table = getCurrentTable();
        if (table == null) return;

        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            showErrorMessage("Selection Error", "Please select a transaction to delete");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "<html><b>Are you sure you want to delete this transaction?</b><br>This action cannot be undone.</html>",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            int modelRow = table.convertRowIndexToModel(selectedRow);
            int transactionId = (Integer) transactionTableModel.getValueAt(modelRow, 0);

            try (PreparedStatement pstmt = connection.prepareStatement("DELETE FROM transactions WHERE id = ?")) {
                pstmt.setInt(1, transactionId);
                pstmt.executeUpdate();
                showSuccessMessage("Transaction deleted successfully!");
                refreshData();
            } catch (SQLException e) {
                showErrorMessage("Delete Error", "Failed to delete transaction: " + e.getMessage());
            }
        }
    }

    private JTable getCurrentTable() {
        Component currentTab = tabbedPane.getSelectedComponent();
        if (currentTab instanceof JPanel) {
            Component[] components = ((JPanel) currentTab).getComponents();
            for (Component comp : components) {
                if (comp instanceof JScrollPane) {
                    return (JTable) ((JScrollPane) comp).getViewport().getView();
                }
            }
        }
        return null;
    }

    private void styleTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(primaryColor);
        table.getTableHeader().setForeground(lightTextColor);
        table.setSelectionBackground(accentColor);
        table.setSelectionForeground(Color.WHITE);
        table.setGridColor(new Color(220, 220, 220));
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(1, 1));
        table.setBackground(Color.WHITE);
        table.setForeground(textColor);

        // Add mouse listener for document viewing
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int column = table.columnAtPoint(e.getPoint());
                int row = table.rowAtPoint(e.getPoint());

                if (row >= 0 && column >= 0) {
                    String columnName = table.getColumnName(column);
                    if (columnName.equals("Documents") || columnName.equals("Receipt")) {
                        Object value = table.getValueAt(row, column);
                        if (value instanceof String && ((String) value).startsWith("View Document")) {
                            int modelRow = table.convertRowIndexToModel(row);
                            int id = (Integer) table.getModel().getValueAt(modelRow, 0);

                            try {
                                String tableName = "";
                                String columnToSelect = "";

                                switch (tabbedPane.getSelectedIndex()) {
                                    case 0: // Salesman
                                        tableName = "salesmen";
                                        columnToSelect = "document";
                                        break;
                                    case 1: // Customer
                                        tableName = "customers";
                                        columnToSelect = "document";
                                        break;
                                    case 3: // Transaction
                                        tableName = "transactions";
                                        columnToSelect = "receipt";
                                        break;
                                }

                                if (!tableName.isEmpty()) {
                                    try (PreparedStatement pstmt = connection.prepareStatement(
                                            "SELECT " + columnToSelect + " FROM " + tableName + " WHERE id = ?")) {
                                        pstmt.setInt(1, id);
                                        ResultSet rs = pstmt.executeQuery();

                                        if (rs.next()) {
                                            byte[] fileData = rs.getBytes(1);
                                            if (fileData != null) {
                                                File tempFile = File.createTempFile("document", ".tmp");
                                                Files.write(tempFile.toPath(), fileData);
                                                Desktop.getDesktop().open(tempFile);
                                            }
                                        }
                                    }
                                }
                            } catch (IOException | SQLException ex) {
                                showErrorMessage("Document Error", "Error opening document: " + ex.getMessage());
                            }
                        }
                    }
                }
            }
        });
    }

    private void styleTextField(JTextField textField) {
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setBackground(Color.WHITE);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(secondaryColor, 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
    }

    private void styleComboBox(JComboBox<String> comboBox) {
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboBox.setBackground(Color.WHITE);
        comboBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(secondaryColor, 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
    }

    private boolean validateName(String name) {
        return name.matches("^[a-zA-Z\\s]+$");
    }

    private boolean validateEmail(String email) {
        return email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    }

    private boolean validatePhone(String phone) {
        return phone.matches("^\\d{10}$");
    }

    private void showSuccessMessage(String message) {
        JOptionPane.showMessageDialog(this,
                "<html><div style='color:green;font-weight:bold;'>" + message + "</div></html>",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void showErrorMessage(String title, String message) {
        JOptionPane.showMessageDialog(this,
                "<html><div style='color:red;font-weight:bold;'>" + message + "</div></html>",
                title,
                JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                CRMDashboard dashboard = new CRMDashboard();
                dashboard.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}