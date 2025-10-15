package View;

import javax.swing.*;

/**
 * View.Main - Application Entry Point
 *
 * Responsibilities:
 * - Initialize Swing
 * - Create and display the main view
 *
 * Follows SRP: Only responsible for application startup
 */
public class Main {

    public static void main(String[] args) {
        // Ensure Swing components are created on EDT
        SwingUtilities.invokeLater(() -> {
            try {
                // Set system look and feel for native appearance
                UIManager.setLookAndFeel(
                        UIManager.getSystemLookAndFeelClassName()
                );
            } catch (Exception e) {
                System.err.println("Could not set look and feel: " + e.getMessage());
                // Continue with default look and feel
            }

            // Create and display the game view using factory
            GameView gameView = GameViewFactory.createGameView();
            gameView.setVisible(true);
        });
    }
}