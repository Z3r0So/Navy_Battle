package View;

import Database.PlayerDAO;

import javax.swing.*;
import java.awt.*;

public class LoginView extends JFrame {
    private JTextField nameField;
    private JButton startButton;
    private JButton statsButton;
    private PlayerDAO playerDAO;

    public LoginView() {
        this.playerDAO = new PlayerDAO();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Navy Battle");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        mainPanel.setBackground(Color.WHITE);

        //Creation of the title
        JLabel titleLabel = new JLabel("Navy Battle");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setForeground(new Color(50, 50, 50));

        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 40)));

        //Label to enter of the name
        JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        namePanel.setBackground(Color.WHITE);

        JLabel nameLabel = new JLabel("Player Name:");
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        nameField = new JTextField(15);
        nameField.setFont(new Font("Arial", Font.PLAIN, 14));
        nameField.addActionListener(e -> startGame());

        namePanel.add(nameLabel);
        namePanel.add(nameField);

        mainPanel.add(namePanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        //Creation of the buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(Color.WHITE);

        startButton = createButton("Start Game", new Color(70, 130, 180));
        startButton.addActionListener(e -> startGame());

        statsButton = createButton("Statistics", new Color(100, 100, 100));
        statsButton.addActionListener(e -> showStatistics());

        buttonPanel.add(startButton);
        buttonPanel.add(statsButton);

        mainPanel.add(buttonPanel);

        add(mainPanel);
    }

    private JButton createButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setPreferredSize(new Dimension(130, 35));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void showStatistics() {
        StatsView statsView = new StatsView();
        statsView.setVisible(true);
    }

    private void startGame() {
        String playerName = nameField.getText().trim();

        if (playerName.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter a player name",
                    "Name Required",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (playerName.length() < 3 || playerName.length() > 20) {
            JOptionPane.showMessageDialog(
                    this,
                    "Name must be between 3 and 20 characters",
                    "Invalid Name",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (!playerName.matches("[a-zA-Z0-9]+")) {
            JOptionPane.showMessageDialog(
                    this,
                    "Name can only contain letters and numbers",
                    "Invalid Name",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int wins = playerDAO.getOrCreatePlayer(playerName);

        if (wins >= 0) {
            String message = wins == 0 ?
                    "Welcome! Good luck in your first game." :
                    "Welcome back! You have " + wins + " victories.";

            JOptionPane.showMessageDialog(
                    this,
                    message,
                    "Welcome",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }

        SwingUtilities.invokeLater(() -> {
            GameView gameView = GameViewFactory.createGameView(playerName);
            gameView.setVisible(true);
            dispose();
        });
    }
}