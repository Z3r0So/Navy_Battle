package View.Components;
import Model.Boat.Boat;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.util.function.BiConsumer;

/**
 * BoardPanel Component - Single Responsibility
 * Only responsible for displaying and interacting with a game board
 */
public class BoardPanel extends JPanel {

    private static final int CELL_SIZE = 45;
    private static final int GRID_SIZE = 10;
    private static final Color WATER_COLOR = new Color(174, 190, 239);
    private static final Color GRID_LINE_COLOR = new Color(100, 177, 210);
    private static final Color SHIP_HIT_COLOR = new Color(230, 80, 80);
    private static final Color MISS_COLOR = new Color(110, 195, 227, 255);
    private static final Color SELECTED_COLOR = new Color(255, 234, 7, 255);

    private boolean isPlayerBoard;
    private int[][] boardState;
    private Boat[][] boatGrid; // To get boat types for coloring
    private Point hoveredCell = null;
    private Point selectedCell = null;

    // Callback for when a cell is clicked
    private BiConsumer<Integer, Integer> onCellClickListener;

    public BoardPanel(boolean isPlayerBoard) {
        this.isPlayerBoard = isPlayerBoard;
        this.boardState = new int[GRID_SIZE][GRID_SIZE];
        this.boatGrid = new Boat[GRID_SIZE][GRID_SIZE];

        setPreferredSize(new Dimension(
                CELL_SIZE * GRID_SIZE + 2,
                CELL_SIZE * GRID_SIZE + 2
        ));
        setBackground(WATER_COLOR);

        // Only enemy board is interactive
        if (!isPlayerBoard) {
            setupMouseListeners();
        }
    }

    private void setupMouseListeners() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleClick(e.getX(), e.getY());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hoveredCell = null;
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                repaint();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                handleHover(e.getX(), e.getY());
            }
        });
    }

    private void handleClick(int x, int y) {
        int col = x / CELL_SIZE;
        int row = y / CELL_SIZE;

        if (isValidCell(row, col)) {
            selectedCell = new Point(col, row);

            // Notify the listener
            if (onCellClickListener != null) {
                onCellClickListener.accept(row, col);
            }

            repaint();
            revalidate();
        }
    }

    private void handleHover(int x, int y) {
        int col = x / CELL_SIZE;
        int row = y / CELL_SIZE;

        if (isValidCell(row, col)) {
            hoveredCell = new Point(col, row);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        } else {
            hoveredCell = null;
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
        repaint();
    }

    private boolean isValidCell(int row, int col) {
        return row >= 0 && row < GRID_SIZE && col >= 0 && col < GRID_SIZE;
    }

    /**
     * Update board state and force repaint
     */
    public void updateBoard(int[][] newState, Boat[][] newBoatGrid) {
        // Deep copy the state
        for (int i = 0; i < GRID_SIZE; i++) {
            System.arraycopy(newState[i], 0, this.boardState[i], 0, GRID_SIZE);
            if (newBoatGrid != null) {
                System.arraycopy(newBoatGrid[i], 0, this.boatGrid[i], 0, GRID_SIZE);
            }
        }

        // Force immediate visual update
        SwingUtilities.invokeLater(() -> {
            revalidate();
            repaint();
        });
    }

    /**
     * Clear cell selection
     */
    public void clearSelection() {
        selectedCell = null;
        repaint();
    }

    /**
     * Set selection programmatically
     */
    public void setSelectedCell(int row, int col) {
        if (isValidCell(row, col)) {
            selectedCell = new Point(col, row);
            repaint();
        }
    }

    /**
     * Set callback for cell clicks
     */
    public void setOnCellClickListener(BiConsumer<Integer, Integer> listener) {
        this.onCellClickListener = listener;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                drawCell(g2d, row, col);
            }
        }
    }

    private void drawCell(Graphics2D g2d, int row, int col) {
        int x = col * CELL_SIZE;
        int y = row * CELL_SIZE;

        // Cell background
        Color cellColor = getCellBackgroundColor(row, col);
        g2d.setColor(cellColor);
        g2d.fill(new RoundRectangle2D.Double(x, y, CELL_SIZE - 2, CELL_SIZE - 2, 5, 5));

        // Cell border
        g2d.setColor(GRID_LINE_COLOR);
        g2d.setStroke(new BasicStroke(1));
        g2d.draw(new RoundRectangle2D.Double(x, y, CELL_SIZE - 2, CELL_SIZE - 2, 5, 5));

        // Cell content (ship, hit, miss)
        drawCellContent(g2d, x, y, row, col);

        // Selection indicator
        if (!isPlayerBoard && selectedCell != null &&
                selectedCell.x == col && selectedCell.y == row) {
            drawSelectionIndicator(g2d, x, y);
        }
    }

    private Color getCellBackgroundColor(int row, int col) {
        if (!isPlayerBoard && hoveredCell != null &&
                hoveredCell.x == col && hoveredCell.y == row) {
            return WATER_COLOR.brighter();
        }
        return WATER_COLOR;
    }

    private void drawCellContent(Graphics2D g2d, int x, int y, int row, int col) {
        int centerX = x + CELL_SIZE / 2;
        int centerY = y + CELL_SIZE / 2;
        int radius = CELL_SIZE / 2 - 5;

        int state = boardState[row][col];

        switch (state) {
            case 0: // Empty - do nothing
                break;

            case 1: // Ship (only visible on player board)
                if (isPlayerBoard) {
                    drawShip(g2d, centerX, centerY, radius, row, col);
                }
                break;

            case 2: // Miss
                drawMiss(g2d, centerX, centerY);
                break;

            case 3: // Hit
                drawHit(g2d, centerX, centerY, radius);
                break;
        }
    }

    private void drawShip(Graphics2D g2d, int centerX, int centerY, int radius,
                          int row, int col) {
        Color shipColor = getShipColor(row, col);

        // Ship circle
        g2d.setColor(shipColor);
        g2d.fill(new Ellipse2D.Double(
                centerX - radius, centerY - radius,
                radius * 2, radius * 2
        ));

        // Ship border
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        g2d.draw(new Ellipse2D.Double(
                centerX - radius, centerY - radius,
                radius * 2, radius * 2
        ));
    }

    private void drawMiss(Graphics2D g2d, int centerX, int centerY) {
        g2d.setColor(MISS_COLOR);
        int missRadius = CELL_SIZE / 4;
        g2d.fill(new Ellipse2D.Double(
                centerX - missRadius, centerY - missRadius,
                missRadius * 2, missRadius * 2
        ));
    }

    private void drawHit(Graphics2D g2d, int centerX, int centerY, int radius) {
        // Red circle
        g2d.setColor(SHIP_HIT_COLOR);
        g2d.fill(new Ellipse2D.Double(
                centerX - radius, centerY - radius,
                radius * 2, radius * 2
        ));

        // White X mark
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(3));
        int markSize = radius / 2;
        g2d.drawLine(centerX - markSize, centerY - markSize,
                centerX + markSize, centerY + markSize);
        g2d.drawLine(centerX + markSize, centerY - markSize,
                centerX - markSize, centerY + markSize);
    }

    private void drawSelectionIndicator(Graphics2D g2d, int x, int y) {
        g2d.setColor(SELECTED_COLOR);
        g2d.setStroke(new BasicStroke(3));
        int centerX = x + CELL_SIZE / 2;
        int centerY = y + CELL_SIZE / 2;
        g2d.draw(new Ellipse2D.Double(
                centerX - CELL_SIZE / 3,
                centerY - CELL_SIZE / 3,
                CELL_SIZE * 2 / 3,
                CELL_SIZE * 2 / 3
        ));
    }

    private Color getShipColor(int row, int col) {
        Boat boat = boatGrid[row][col];
        if (boat == null) {
            return new Color(100, 126, 230); // Default blue
        }

        String type = boat.getType();
        switch (type) {
            case "Aircrafter":
                return new Color(128, 128, 128); // Gray
            case "Cruise":
                return new Color(0, 100, 150); // Dark Blue
            case "Destructor":
                return new Color(200, 100, 0); // Orange
            case "Submarine":
                return new Color(0, 150, 0); // Green
            default:
                return new Color(100, 126, 230); // Default blue
        }
    }

    /**
     * Enable or disable interaction
     */
    public void setInteractive(boolean interactive) {
        setEnabled(interactive);
        if (!interactive) {
            hoveredCell = null;
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
    }
}


