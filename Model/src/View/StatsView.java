package View;

import Database.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/** StatsView class to display player statistics from the database
 */
public class StatsView extends JFrame {
    private JTable statsTable;
    private DefaultTableModel tableModel;
    private JLabel totalPlayersLabel;
    /**The constructor initializes the StatsView UI and loads statistics from the database.
     * */
    public StatsView() {
        initializeUI();
        loadStatistics();
    }
    /**
     * Method to initialize the UI components and layout
     * It sets up the JFrame, JTable, buttons, and styles
     */
    private void initializeUI() {
        setTitle("Player Statistics");
        setSize(600, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("Player Statistics");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(50, 50, 50));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        totalPlayersLabel = new JLabel("Total Players: 0");
        totalPlayersLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        totalPlayersLabel.setForeground(new Color(67, 133, 205));
        totalPlayersLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(titleLabel);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        headerPanel.add(totalPlayersLabel);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Table
        String[] columnNames = {"Rank", "Player Name", "Victories", "Registered"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        statsTable = new JTable(tableModel);
        statsTable.setFont(new Font("Arial", Font.PLAIN, 13));
        statsTable.setRowHeight(30);
        statsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        statsTable.getTableHeader().setBackground(new Color(70, 130, 180));
        statsTable.getTableHeader().setForeground(Color.BLACK);
        statsTable.setSelectionBackground(new Color(0, 0, 0));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < statsTable.getColumnCount(); i++) {
            statsTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        statsTable.getColumnModel().getColumn(0).setPreferredWidth(60);
        statsTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        statsTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        statsTable.getColumnModel().getColumn(3).setPreferredWidth(140);

        JScrollPane scrollPane = new JScrollPane(statsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(Color.WHITE);

        JButton refreshButton = createButton("Refresh", new Color(70, 130, 180));
        refreshButton.addActionListener(e -> loadStatistics());

        JButton backButton = createButton("Back", new Color(100, 100, 100));
        backButton.addActionListener(e -> backToLogin());

        buttonPanel.add(refreshButton);
        buttonPanel.add(backButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JButton createButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 13));
        button.setPreferredSize(new Dimension(100, 35));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
    /** Method to navigate back to the login view
     * It disposes the current StatsView and opens a new LoginView
     * */
    private void backToLogin() {
        dispose();
        SwingUtilities.invokeLater(() -> {
            LoginView loginView = new LoginView();
            loginView.setVisible(true);
        });
    }
    /**Method to load player statistics from the database and populate the table
     * It also updates the total players label and handles errors
     * */
    private void loadStatistics() {
        tableModel.setRowCount(0);

        String query = "SELECT username, wins, created_at " +
                "FROM players " +
                "ORDER BY wins DESC, created_at ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            int totalPlayers = 0;

            while (rs.next()) {
                String username = rs.getString("username");
                int wins = rs.getInt("wins");
                String createdAt = rs.getTimestamp("created_at")
                        .toString().substring(0, 19);

                Object[] row = {
                        totalPlayers + 1,
                        username,
                        wins,
                        createdAt
                };

                tableModel.addRow(row);
                totalPlayers++;
            }

            totalPlayersLabel.setText("Total Players: " + totalPlayers);

            if (totalPlayers == 0) {
                JOptionPane.showMessageDialog(
                        this,
                        "No players found in the database.",
                        "No Data",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error loading statistics: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
            e.printStackTrace();
        }
    }
}