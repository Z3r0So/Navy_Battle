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

/**
 * GUI for showing the player statistics
 */
public class StatsView extends JFrame {
    private JTable statsTable;
    private DefaultTableModel tableModel;
    private JLabel totalPlayersLabel;

    public StatsView() {
        initializeUI();
        loadStatistics();
    }

    private void initializeUI() {
        setTitle("Player Statistics");
        setSize(600, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(240, 248, 255));

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(240, 248, 255));
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("Player Statistics");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 51, 102));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        totalPlayersLabel = new JLabel("Total Players: 0");
        totalPlayersLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        totalPlayersLabel.setForeground(new Color(100, 100, 100));
        totalPlayersLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(titleLabel);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        headerPanel.add(totalPlayersLabel);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

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
        statsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        statsTable.getTableHeader().setBackground(new Color(0, 51, 102));
        statsTable.getTableHeader().setForeground(Color.WHITE);
        statsTable.setSelectionBackground(new Color(173, 216, 230));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < statsTable.getColumnCount(); i++) {
            statsTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        statsTable.getColumnModel().getColumn(0).setPreferredWidth(60);  // Rank
        statsTable.getColumnModel().getColumn(1).setPreferredWidth(200); // Player Name
        statsTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Victories
        statsTable.getColumnModel().getColumn(3).setPreferredWidth(140); // Registered

        JScrollPane scrollPane = new JScrollPane(statsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(0, 51, 102), 2));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        //Lower panel with buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(new Color(240, 248, 255));

        JButton refreshButton = new JButton("Refresh");
        refreshButton.setFont(new Font("Arial", Font.BOLD, 13));
        refreshButton.setPreferredSize(new Dimension(120, 35));
        refreshButton.setBackground(new Color(40, 167, 69));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshButton.addActionListener(e -> loadStatistics());

        JButton closeButton = new JButton("Close");
        closeButton.setFont(new Font("Arial", Font.BOLD, 13));
        closeButton.setPreferredSize(new Dimension(120, 35));
        closeButton.setBackground(new Color(108, 117, 125));
        closeButton.setForeground(Color.WHITE);
        closeButton.setFocusPainted(false);
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButton.addActionListener(e -> dispose());

        buttonPanel.add(refreshButton);
        buttonPanel.add(closeButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    /**Method to load statistics from the database
     * and display them in the table
     */
    private void loadStatistics() {
        tableModel.setRowCount(0);

        String query = "SELECT username, wins, created_at " +
                "FROM players " +
                "ORDER BY wins DESC, created_at ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            int rank = 1;
            int totalPlayers = 0;

            while (rs.next()) {
                String username = rs.getString("username");
                int wins = rs.getInt("wins");
                String createdAt = rs.getTimestamp("created_at").toString().substring(0, 19);

                Object[] row = {
                        username,
                        wins,
                        createdAt
                };

                tableModel.addRow(row);
                rank++;
                totalPlayers++;
            }

            totalPlayersLabel.setText("Total Players: " + totalPlayers);

            if (totalPlayers == 0) {
                JOptionPane.showMessageDialog(
                        this,
                        "No players found in the database.\nStart a game to create your first player!",
                        "No Data",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error loading statistics:\n" + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
            e.printStackTrace();
        }
    }
}