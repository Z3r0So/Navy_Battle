package View.Components;

import javax.swing.*;
import java.awt.*;

/**
 * HeaderPanel Component - Single Responsibility
 * Only responsible for displaying game title and turn information
 */
public class HeaderPanel extends JPanel {
    
    private JLabel titleLabel;
    private JLabel turnLabel;
    
    public HeaderPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(0, 151, 167));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        createComponents();
    }
    
    private void createComponents() {
        titleLabel = new JLabel("Navy Battle");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        turnLabel = new JLabel("Your turn - Select a cell to attack");
        turnLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        turnLabel.setForeground(new Color(200, 255, 255));
        turnLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        add(titleLabel);
        add(Box.createVerticalStrut(10));
        add(turnLabel);
    }
    
    /**
     * Update turn message
     */
    public void setTurnMessage(String message) {
        turnLabel.setText(message);
    }
    
    /**
     * Set title
     */
    public void setTitle(String title) {
        titleLabel.setText(title);
    }
}