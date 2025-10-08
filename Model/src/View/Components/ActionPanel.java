package View.Components;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

/**
 * ActionPanel Component - Single Responsibility
 * Only responsible for attack buttons and status display
 */
public class ActionPanel extends JPanel {

    private JLabel statusLabel;
    private JButton attackButton;
    private JButton crossBombButton;
    private JButton torpedoHButton;
    private JButton torpedoVButton;
    private JButton nukeButton;
    private JButton resetButton;

    // Attack type enum for callbacks
    public enum AttackType {
        BASIC, CROSS_BOMB, TORPEDO_H, TORPEDO_V, NUKE, RESET
    }

    private Consumer<AttackType> onAttackListener;

    public ActionPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(0, 131, 143));
        setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        createComponents();
    }

    private void createComponents() {
        // Status label at top
        statusLabel = new JLabel("Select a cell to attack", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        statusLabel.setForeground(Color.WHITE);

        // Buttons grid
        JPanel buttonsPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        buttonsPanel.setOpaque(false);

        // Create all buttons
        attackButton = createButton("ATTACK", new Color(255, 87, 34),
                () -> notifyAttack(AttackType.BASIC));

        crossBombButton = createButton("CROSS", new Color(156, 39, 176),
                () -> notifyAttack(AttackType.CROSS_BOMB));

        nukeButton = createButton("NUKE", new Color(244, 67, 54),
                () -> notifyAttack(AttackType.NUKE));

        torpedoHButton = createButton("TORPEDO →", new Color(33, 150, 243),
                () -> notifyAttack(AttackType.TORPEDO_H));

        torpedoVButton = createButton("TORPEDO ↓", new Color(3, 169, 244),
                () -> notifyAttack(AttackType.TORPEDO_V));

        resetButton = createButton("NEW GAME", new Color(76, 175, 80),
                () -> notifyAttack(AttackType.RESET));

        // Add buttons to grid
        buttonsPanel.add(attackButton);
        buttonsPanel.add(crossBombButton);
        buttonsPanel.add(nukeButton);
        buttonsPanel.add(torpedoHButton);
        buttonsPanel.add(torpedoVButton);
        buttonsPanel.add(resetButton);

        // Add to panel
        add(statusLabel, BorderLayout.NORTH);
        add(buttonsPanel, BorderLayout.CENTER);
    }

    private JButton createButton(String text, Color bgColor, Runnable action) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setPreferredSize(new Dimension(110, 35));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setEnabled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(e -> action.run());
        return button;
    }

    private void notifyAttack(AttackType type) {
        if (onAttackListener != null) {
            onAttackListener.accept(type);
        }
    }

    /**
     * Set callback for attack actions
     */
    public void setOnAttackListener(Consumer<AttackType> listener) {
        this.onAttackListener = listener;
    }

    /**
     * Update status message
     */
    public void setStatus(String message) {
        statusLabel.setText(message);
    }

    /**
     * Enable/disable all attack buttons
     */
    public void setAttackButtonsEnabled(boolean basic, boolean cross,
                                        boolean torpedoH, boolean torpedoV,
                                        boolean nuke) {
        attackButton.setEnabled(basic);
        crossBombButton.setEnabled(cross);
        torpedoHButton.setEnabled(torpedoH);
        torpedoVButton.setEnabled(torpedoV);
        nukeButton.setEnabled(nuke);
    }

    /**
     * Disable all attack buttons
     */
    public void disableAllAttackButtons() {
        attackButton.setEnabled(false);
        crossBombButton.setEnabled(false);
        torpedoHButton.setEnabled(false);
        torpedoVButton.setEnabled(false);
        nukeButton.setEnabled(false);
    }

    /**
     * Enable reset button always
     */
    public void enableResetButton() {
        resetButton.setEnabled(true);
    }
}
