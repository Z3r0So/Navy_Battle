package View;


import Database.DatabaseConnection;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (DatabaseConnection.testConnection()) {
            DatabaseConnection.initialize();
        } else {
        }
        SwingUtilities.invokeLater(() -> {
            LoginView loginView = new LoginView();
            loginView.setVisible(true);
            System.out.println("Application started successfully");
        });
    }
}