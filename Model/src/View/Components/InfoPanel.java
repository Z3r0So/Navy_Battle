package View.Components;

import javax.swing.*;
import java.awt.*;

/**
 * InfoPanel Component - Single Responsibility
 * Only responsible for displaying power-ups and game log
 */
public class InfoPanel extends JPanel {

    private JLabel powerUpsLabel;
    private JTextArea logArea;

    public InfoPanel() {
        setLayout(new BorderLayout(10, 10));
        setPreferredSize(new Dimension(250, 0));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        createComponents();
    }

    private void createComponents() {
        // Power-ups section
        JPanel powerUpsPanel = new JPanel();
        powerUpsPanel.setLayout(new BoxLayout(powerUpsPanel, BoxLayout.Y_AXIS));
        powerUpsPanel.setBackground(Color.WHITE);
        powerUpsPanel.setBorder(BorderFactory.createTitledBorder("POWER-UPS"));

        powerUpsLabel = new JLabel("<html>Cross: 2<br>Torpedoes: 2<br>Nukes: 1</html>");
        powerUpsLabel.setFont(new Font("Arial", Font.BOLD, 12));
        powerUpsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        powerUpsPanel.add(powerUpsLabel);

        // Log section
        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setBackground(Color.WHITE);

        JLabel logTitle = new JLabel("GAME LOG");
        logTitle.setFont(new Font("Arial", Font.BOLD, 16));

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Courier New", Font.PLAIN, 11));
        logArea.setBackground(new Color(245, 245, 245));
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setPreferredSize(new Dimension(230, 320));

        logPanel.add(logTitle, BorderLayout.NORTH);
        logPanel.add(scrollPane, BorderLayout.CENTER);

        // Add sections to main panel
        add(powerUpsPanel, BorderLayout.NORTH);
        add(logPanel, BorderLayout.CENTER);
    }

    /**
     * Update power-ups display
     */
    public void updatePowerUps(int crossBombs, int torpedoes, int nukes) {
        powerUpsLabel.setText(String.format(
                "<html>Cross: %d<br>Torpedoes: %d<br>Nukes: %d</html>",
                crossBombs, torpedoes, nukes
        ));
    }

    /**
     * Add message to log
     */
    public void addLog(String message) {
        logArea.append(message + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    /**
     * Clear log
     */
    public void clearLog() {
        logArea.setText("");
    }
}