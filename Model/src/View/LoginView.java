package View;

import Database.PlayerDAO;

import javax.swing.*;
import java.awt.*;

/**
 * Vista de inicio de sesión integrada con el juego
 * Permite ingresar el nombre del jugador, comenzar el juego y ver estadísticas
 */
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
        setTitle("Battleship - Welcome");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 350);
        setLocationRelativeTo(null);
        setResizable(false);

        // Panel principal
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        mainPanel.setBackground(new Color(240, 248, 255));

        // Título
        JLabel titleLabel = new JLabel("⚓ BATTLESHIP");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setForeground(new Color(0, 51, 102));

        // Subtítulo
        JLabel subtitleLabel = new JLabel("Naval Combat Game");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitleLabel.setForeground(new Color(100, 100, 100));

        // Espacio
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(subtitleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 40)));

        // Panel para el campo de nombre
        JPanel namePanel = new JPanel();
        namePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        namePanel.setBackground(new Color(240, 248, 255));

        JLabel nameLabel = new JLabel("Player Name:");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));

        nameField = new JTextField(15);
        nameField.setFont(new Font("Arial", Font.PLAIN, 14));

        namePanel.add(nameLabel);
        namePanel.add(nameField);

        mainPanel.add(namePanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Panel de botones
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(new Color(240, 248, 255));

        // Botón de estadísticas
        statsButton = new JButton("📊 Statistics");
        statsButton.setFont(new Font("Arial", Font.BOLD, 14));
        statsButton.setPreferredSize(new Dimension(140, 40));
        statsButton.setBackground(new Color(108, 117, 125));
        statsButton.setForeground(Color.WHITE);
        statsButton.setFocusPainted(false);
        statsButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        statsButton.addActionListener(e -> showStatistics());

        // Botón de inicio
        startButton = new JButton("🎮 Start Game");
        startButton.setFont(new Font("Arial", Font.BOLD, 14));
        startButton.setPreferredSize(new Dimension(140, 40));
        startButton.setBackground(new Color(0, 123, 255));
        startButton.setForeground(Color.WHITE);
        startButton.setFocusPainted(false);
        startButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        startButton.addActionListener(e -> startGame());

        buttonPanel.add(statsButton);
        buttonPanel.add(startButton);

        // También permitir Enter en el campo de texto
        nameField.addActionListener(e -> startGame());

        mainPanel.add(buttonPanel);

        add(mainPanel);
    }

    /**
     * Muestra la ventana de estadísticas
     */
    private void showStatistics() {
        StatsView statsView = new StatsView();
        statsView.setVisible(true);
    }

    /**
     * Inicia el juego
     */
    private void startGame() {
        String playerName = nameField.getText().trim();

        // Validar que el nombre no esté vacío
        if (playerName.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter a player name!",
                    "Name Required",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // Validar longitud y caracteres
        if (playerName.length() < 3 || playerName.length() > 20) {
            JOptionPane.showMessageDialog(
                    this,
                    "Name must be between 3 and 20 characters!",
                    "Invalid Name",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (!playerName.matches("[a-zA-Z0-9]+")) {
            JOptionPane.showMessageDialog(
                    this,
                    "Name can only contain letters and numbers!",
                    "Invalid Name",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // Obtener o crear jugador en la base de datos
        int wins = playerDAO.getOrCreatePlayer(playerName);

        // Mensaje de bienvenida
        if (wins >= 0) {
            String message = wins == 0 ?
                    "Welcome new player! Good luck!" :
                    "Welcome back! You have " + wins + " victories!";

            JOptionPane.showMessageDialog(
                    this,
                    message,
                    "Welcome",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }

        // Crear y mostrar la ventana del juego con el nombre del jugador
        SwingUtilities.invokeLater(() -> {
            GameView gameView = GameViewFactory.createGameView(playerName);
            gameView.setVisible(true);
            dispose(); // Cerrar la ventana de login
        });
    }

    /**
     * Método main para ejecutar la aplicación
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            LoginView loginView = new LoginView();
            loginView.setVisible(true);
        });
    }
}