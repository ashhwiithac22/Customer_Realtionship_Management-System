import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

class CRMLogin extends JFrame {
    private JTextField username;
    private JPasswordField passwordField;
    private JButton loginButton;
    private Connection connection;

    public CRMLogin() {
        // Initialize database connection
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/crm",
                    "root", // your username
                    "AshwithaChandru*1" // your password
            );
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database connection failed", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        setTitle("CRM");
        setSize(450, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        JPanel background = new JPanel(new GridBagLayout());
        background.setBackground(new Color(25, 42, 86));

        JPanel loginPanel = new JPanel();
        loginPanel.setPreferredSize(new Dimension(350, 250));
        loginPanel.setBackground(new Color(40, 55, 90));
        loginPanel.setLayout(null);
        loginPanel.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));

        JLabel titleLabel = new JLabel("LOGIN");
        titleLabel.setBounds(135, 10, 100, 30);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        loginPanel.add(titleLabel);

        username = new JTextField();
        username.setBounds(50, 60, 250, 35);
        username.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        username.setBackground(new Color(236, 240, 241));
        username.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        username.setToolTipText("Enter username");
        loginPanel.add(username);

        passwordField = new JPasswordField();
        passwordField.setBounds(50, 110, 250, 35);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setBackground(new Color(236, 240, 241));
        passwordField.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        passwordField.setToolTipText("Enter your password");
        loginPanel.add(passwordField);

        loginButton = new JButton("SUBMIT");
        loginButton.setBounds(50, 170, 250, 35);
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginButton.setBackground(new Color(20, 224, 243));
        loginButton.setForeground(Color.WHITE);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.setFocusPainted(false);
        loginButton.setBorder(BorderFactory.createEmptyBorder());
        loginButton.addActionListener(this::handleLogin);
        loginPanel.add(loginButton);

        background.add(loginPanel);
        add(background);
    }

    private void handleLogin(ActionEvent e) {
        String userText = username.getText().trim();
        String password = new String(passwordField.getPassword());

        if (userText.isEmpty() || password.isEmpty()) {
            showCustomDialog("Please enter username and password", false);
            return;
        }

        try {
            String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, userText);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int userId = rs.getInt("id");
                recordLogin(userId); // Insert into login_history table
                showCustomDialog("Login successful!", true);
            } else {
                showCustomDialog("Invalid username or password", false);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            showCustomDialog("Database error during login", false);
        }
    }

    private void recordLogin(int userId) {
        try {
            String ip = "127.0.0.1"; // For demo; get actual IP if needed
            PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO login_history (user_id, action, ip_address) VALUES (?, 'login', ?)"
            );
            stmt.setInt(1, userId);
            stmt.setString(2, ip);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showCustomDialog(String message, boolean goToDashboard) {
        JDialog dialog = new JDialog(this, "Info", true);
        dialog.setSize(300, 150);
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(new Color(25, 42, 86));

        JLabel msgLabel = new JLabel(message, SwingConstants.CENTER);
        msgLabel.setForeground(Color.WHITE);
        msgLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dialog.add(msgLabel, BorderLayout.CENTER);

        JButton okButton = new JButton("OK");
        okButton.setFocusPainted(false);
        okButton.setForeground(Color.WHITE);
        okButton.setBackground(new Color(30, 132, 210));
        okButton.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));
        okButton.addActionListener(e -> {
            dialog.dispose();
            if (goToDashboard) {
                new CRMDashboard().setVisible(true);
                this.dispose();
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(30, 132, 210));
        buttonPanel.add(okButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CRMLogin().setVisible(true));
    }
}
