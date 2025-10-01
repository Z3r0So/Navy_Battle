package View;

import Controller.GameController;
import Model.Boat.Boat;
import Model.Player.HumanPlayer;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;

public class GameView extends JFrame {

    private GameController controller;

    private static final int CELL_SIZE = 45;
    private static final int GRID_SIZE = 10;
    private static final Color WATER_COLOR = new Color(174, 190, 239);
    private static final Color GRID_LINE_COLOR = new Color(100, 177, 210);
    private static final Color SHIP_HIT_COLOR = new Color(230, 80, 80);
    private static final Color MISS_COLOR = new Color(110, 195, 227, 255);
    private static final Color SELECTED_COLOR = new Color(255, 234, 7, 255);

    private BoardPanel playerBoard;
    private BoardPanel enemyBoard;
    private JLabel statusLabel;
    private JLabel playerShipsLabel;
    private JLabel enemyShipsLabel;
    private JLabel turnLabel;
    private JButton attackButton;
    private JButton crossBombButton;
    private JTextArea logArea;
    private JLabel powerUpsLabel;

    private int selectedRow = -1;
    private int selectedCol = -1;

    public GameView() {
        controller = new GameController();

        // Start a new game with default player
        boolean gameStarted = controller.startNewGame("Player", "password");
        if (!gameStarted) {
            JOptionPane.showMessageDialog(this, "Couldn't start a new game",
                    "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        initComponents();
        updateBoards();
        updateGameInfo();
        updatePowerUpsDisplay();
    }

    /**
     * Get the color for a specific boat type
     * @param boatType the type of the boat
     * @return the Color object for that boat type
     */
    private Color getBoatColor(String boatType) {
        switch (boatType) {
            case "Portaaviones": // Aircrafter
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

    private void initComponents() {
        setTitle("Navy Battle");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        setResizable(false);
        getContentPane().setBackground(new Color(224, 247, 250));

        // HEADER
        add(createHeader(), BorderLayout.NORTH);

        // CENTER
        add(createBoardsPanel(), BorderLayout.CENTER);

        // RIGHT
        add(createInfoPanel(), BorderLayout.EAST);

        // BOTTOM
        add(createActionPanel(), BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(new Color(0, 151, 167));
        header.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Navy Battle");
        title.setFont(new Font("Arial", Font.BOLD, 32));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        turnLabel = new JLabel("Your turn - Select a cell to attack");
        turnLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        turnLabel.setForeground(new Color(200, 255, 255));
        turnLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        header.add(title);
        header.add(Box.createVerticalStrut(10));
        header.add(turnLabel);

        return header;
    }

    private JPanel createBoardsPanel() {
        JPanel boardsPanel = new JPanel();
        boardsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 40, 30));
        boardsPanel.setBackground(new Color(224, 247, 250));

        JPanel playerSection = createBoardSection("Your Crew", true);
        JPanel enemySection = createBoardSection("Enemy Board", false);

        boardsPanel.add(playerSection);
        boardsPanel.add(enemySection);

        return boardsPanel;
    }

    private JPanel createBoardSection(String title, boolean isPlayerBoard) {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBackground(new Color(224, 247, 250));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(0, 96, 100));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel boardContainer = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            }
        };
        boardContainer.setOpaque(false);
        boardContainer.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Board Creation
        BoardPanel board = new BoardPanel(isPlayerBoard);
        if (isPlayerBoard) {
            playerBoard = board;
        } else {
            enemyBoard = board;
        }
        boardContainer.add(board);

        JLabel infoLabel;
        if (isPlayerBoard) {
            playerShipsLabel = new JLabel("Ships: 8");
            infoLabel = playerShipsLabel;
        } else {
            enemyShipsLabel = new JLabel("Ships: 8");
            infoLabel = enemyShipsLabel;
        }
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        infoLabel.setForeground(new Color(0, 96, 100));
        infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        section.add(titleLabel);
        section.add(Box.createVerticalStrut(15));
        section.add(boardContainer);
        section.add(Box.createVerticalStrut(10));
        section.add(infoLabel);

        return section;
    }

    /**
     * Method to create the information panel on the right side
     * Contains the game log and power-ups info
     */
    private JPanel createInfoPanel() {
        JPanel infoPanel = new JPanel(new BorderLayout(10, 10));
        infoPanel.setPreferredSize(new Dimension(250, 0));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Power-ups section
        JPanel powerUpsPanel = new JPanel();
        powerUpsPanel.setLayout(new BoxLayout(powerUpsPanel, BoxLayout.Y_AXIS));
        powerUpsPanel.setBackground(Color.WHITE);
        powerUpsPanel.setBorder(BorderFactory.createTitledBorder("POWER-UPS"));

        powerUpsLabel = new JLabel("Cross Bombs: 2");
        powerUpsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        powerUpsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        powerUpsPanel.add(powerUpsLabel);

        // Log section
        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setBackground(Color.WHITE);

        JLabel logTitle = new JLabel("GAME LOG");
        logTitle.setFont(new Font("Arial", Font.BOLD, 16));

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Courier New", Font.PLAIN, 12));
        logArea.setBackground(new Color(245, 245, 245));
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setPreferredSize(new Dimension(230, 350));

        logPanel.add(logTitle, BorderLayout.NORTH);
        logPanel.add(scrollPane, BorderLayout.CENTER);

        infoPanel.add(powerUpsPanel, BorderLayout.NORTH);
        infoPanel.add(logPanel, BorderLayout.CENTER);

        return infoPanel;
    }

    /**
     * Method to create the action panel at the bottom
     * Contains the status label, attack buttons and new game button
     */
    private JPanel createActionPanel() {
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        actionPanel.setBackground(new Color(0, 131, 143));

        statusLabel = new JLabel("Select a cell to attack");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        statusLabel.setForeground(Color.WHITE);

        attackButton = new JButton("ATTACK");
        attackButton.setFont(new Font("Arial", Font.BOLD, 16));
        attackButton.setPreferredSize(new Dimension(130, 40));
        attackButton.setBackground(new Color(255, 87, 34));
        attackButton.setForeground(Color.WHITE);
        attackButton.setFocusPainted(false);
        attackButton.setBorderPainted(false);
        attackButton.setEnabled(false);
        attackButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        attackButton.addActionListener(e -> executeAttack());

        crossBombButton = new JButton("CROSS BOMB");
        crossBombButton.setFont(new Font("Arial", Font.BOLD, 14));
        crossBombButton.setPreferredSize(new Dimension(130, 40));
        crossBombButton.setBackground(new Color(156, 39, 176));
        crossBombButton.setForeground(Color.WHITE);
        crossBombButton.setFocusPainted(false);
        crossBombButton.setBorderPainted(false);
        crossBombButton.setEnabled(false);
        crossBombButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        crossBombButton.addActionListener(e -> executeCrossBombAttack());

        JButton resetButton = new JButton("NEW GAME");
        resetButton.setFont(new Font("Arial", Font.PLAIN, 14));
        resetButton.setPreferredSize(new Dimension(130, 40));
        resetButton.setBackground(new Color(76, 175, 80));
        resetButton.setForeground(Color.WHITE);
        resetButton.setFocusPainted(false);
        resetButton.setBorderPainted(false);
        resetButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        resetButton.addActionListener(e -> resetGame());

        actionPanel.add(statusLabel);
        actionPanel.add(Box.createHorizontalStrut(30));
        actionPanel.add(attackButton);
        actionPanel.add(crossBombButton);
        actionPanel.add(resetButton);

        return actionPanel;
    }

    /**
     * Custom JPanel to represent a game board
     * Handles rendering, clicks, and hover effects
     */
    public class BoardPanel extends JPanel {
        private boolean isPlayerBoard;
        private int[][] boardState;
        private Point hoveredCell = null;

        public BoardPanel(boolean isPlayerBoard) {
            this.isPlayerBoard = isPlayerBoard;
            this.boardState = new int[GRID_SIZE][GRID_SIZE];

            setPreferredSize(new Dimension(
                    CELL_SIZE * GRID_SIZE + 2,
                    CELL_SIZE * GRID_SIZE + 2
            ));
            setBackground(WATER_COLOR);

            // Set up mouse listeners only for enemy board - player can only interact here
            if (!isPlayerBoard) {
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        handleClick(e.getX(), e.getY());
                    }
                });

                addMouseMotionListener(new MouseMotionAdapter() {
                    @Override
                    public void mouseMoved(MouseEvent e) {
                        handleHover(e.getX(), e.getY());
                    }
                });
            }
        }

        private void handleClick(int x, int y) {
            if (controller.isGameFinished() || !controller.isPlayerTurn()) {
                return;
            }

            int col = x / CELL_SIZE;
            int row = y / CELL_SIZE;

            if (row >= 0 && row < GRID_SIZE && col >= 0 && col < GRID_SIZE) {
                selectedRow = row;
                selectedCol = col;
                statusLabel.setText("Cell (" + row + "," + col + ") Selected - press ATTACK");
                attackButton.setEnabled(true);
                updateCrossBombButton();
                // Force immediate repaint
                SwingUtilities.invokeLater(() -> {
                    repaint();
                    paintImmediately(getBounds());
                });
            }
        }

        private void handleHover(int x, int y) {
            if (controller.isGameFinished() || !controller.isPlayerTurn()) {
                return;
            }

            int col = x / CELL_SIZE;
            int row = y / CELL_SIZE;

            if (row >= 0 && row < GRID_SIZE && col >= 0 && col < GRID_SIZE) {
                hoveredCell = new Point(col, row);
                setCursor(new Cursor(Cursor.HAND_CURSOR));
            } else {
                hoveredCell = null;
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
            repaint();
        }

        public void updateBoard(int[][] newState) {
            // Copy state
            for (int i = 0; i < GRID_SIZE; i++) {
                for (int j = 0; j < GRID_SIZE; j++) {
                    this.boardState[i][j] = newState[i][j];
                }
            }
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            for (int row = 0; row < GRID_SIZE; row++) {
                for (int col = 0; col < GRID_SIZE; col++) {
                    int x = col * CELL_SIZE;
                    int y = row * CELL_SIZE;

                    Color cellColor = WATER_COLOR;
                    if (!isPlayerBoard && hoveredCell != null &&
                            hoveredCell.x == col && hoveredCell.y == row) {
                        cellColor = WATER_COLOR.brighter();
                    }

                    g2d.setColor(cellColor);
                    g2d.fill(new RoundRectangle2D.Double(x, y, CELL_SIZE - 2, CELL_SIZE - 2, 5, 5));

                    // Border
                    g2d.setColor(GRID_LINE_COLOR);
                    g2d.setStroke(new BasicStroke(1));
                    g2d.draw(new RoundRectangle2D.Double(x, y, CELL_SIZE - 2, CELL_SIZE - 2, 5, 5));

                    // Draw cell content with ship colors
                    int state = boardState[row][col];
                    drawCellContent(g2d, x, y, row, col, state);

                    // Highlight selected cell
                    if (!isPlayerBoard && selectedRow == row && selectedCol == col) {
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
                }
            }
        }

        /**
         * Method to draw the content of each cell based on its state
         * @param g2d the Graphics2D object for drawing
         * @param x the x coordinate of the cell
         * @param y the y coordinate of the cell
         * @param row the row position in the grid
         * @param col the column position in the grid
         * @param state the state of the cell (0: empty, 1: ship, 2: miss, 3: hit)
         */
        private void drawCellContent(Graphics2D g2d, int x, int y, int row, int col, int state) {
            int centerX = x + CELL_SIZE / 2;
            int centerY = y + CELL_SIZE / 2;
            int radius = CELL_SIZE / 2 - 5;

            switch (state) {
                case 1: // Ship
                    if (isPlayerBoard) {
                        // Get the boat at this position to use its color
                        Boat boat = controller.getCurrentMatch().getPlayer()
                                .getOwnBoard().getBoatAt(row, col);

                        Color shipColor;
                        if (boat != null) {
                            shipColor = getBoatColor(boat.getType());
                        } else {
                            shipColor = new Color(100, 126, 230); // Default color
                        }

                        g2d.setColor(shipColor);
                        g2d.fill(new Ellipse2D.Double(
                                centerX - radius, centerY - radius,
                                radius * 2, radius * 2
                        ));

                        // Add a border to delimit the ship
                        g2d.setColor(Color.BLACK);
                        g2d.setStroke(new BasicStroke(2));
                        g2d.draw(new Ellipse2D.Double(
                                centerX - radius, centerY - radius,
                                radius * 2, radius * 2
                        ));
                    }
                    break;

                case 2: // Miss
                    g2d.setColor(MISS_COLOR);
                    int missRadius = CELL_SIZE / 4;
                    g2d.fill(new Ellipse2D.Double(
                            centerX - missRadius, centerY - missRadius,
                            missRadius * 2, missRadius * 2
                    ));
                    break;

                case 3: // Hit
                    g2d.setColor(SHIP_HIT_COLOR);
                    g2d.fill(new Ellipse2D.Double(
                            centerX - radius, centerY - radius,
                            radius * 2, radius * 2
                    ));

                    // Draw X mark for hit
                    g2d.setColor(Color.WHITE);
                    g2d.setStroke(new BasicStroke(3));
                    int markSize = radius / 2;
                    g2d.drawLine(centerX - markSize, centerY - markSize,
                            centerX + markSize, centerY + markSize);
                    g2d.drawLine(centerX + markSize, centerY - markSize,
                            centerX - markSize, centerY + markSize);
                    break;
            }
        }
    }

    /**
     * Update the Cross Bomb button state based on available power-ups
     */
    private void updateCrossBombButton() {
        if (controller.getCurrentMatch() != null) {
            HumanPlayer human = (HumanPlayer) controller.getCurrentMatch().getPlayer();
            boolean hasBombs = human.getPowerUps().hasCrossBombs();
            crossBombButton.setEnabled(hasBombs && selectedRow != -1 && selectedCol != -1);
        }
    }

    /**
     * Update power-ups display
     */
    private void updatePowerUpsDisplay() {
        if (controller.getCurrentMatch() != null) {
            HumanPlayer human = (HumanPlayer) controller.getCurrentMatch().getPlayer();
            int bombs = human.getPowerUps().getCrossBombs();
            powerUpsLabel.setText("Cross Bombs: " + bombs);
        }
    }

    /**
     * Executes the Cross Bomb attack
     */
    private void executeCrossBombAttack() {
        if (selectedRow == -1 || selectedCol == -1) {
            return;
        }

        int attackRow = selectedRow;
        int attackCol = selectedCol;

        // Debugging info
        System.out.println("\n=== Cross Bomb Attack ===");
        System.out.println("Attacking: (" + attackRow + "," + attackCol + ")");

        // Execute the cross bomb attack
        String result = controller.playerCrossBombAttack(attackRow, attackCol);
        System.out.println("Result: " + result);
        addLog("CROSS BOMB at (" + attackRow + "," + attackCol + "): " + result);

        // Manually mark all cells in the cross pattern on the attack board
        int[][] crossPattern = {
                {attackRow, attackCol},           // Center
                {attackRow - 1, attackCol},       // Up
                {attackRow + 1, attackCol},       // Down
                {attackRow, attackCol - 1},       // Left
                {attackRow, attackCol + 1}        // Right
        };

        // Mark each cell in the cross pattern
        for (int[] pos : crossPattern) {
            int r = pos[0];
            int c = pos[1];

            // Check if coordinates are within bounds
            if (r >= 0 && r < 10 && c >= 0 && c < 10) {
                // Check if the enemy board has a ship at this position
                int[][] enemyBoardState = controller.getCurrentMatch().getMachine()
                        .getOwnBoard().getBoardState();

                boolean wasHit = (enemyBoardState[r][c] == 3); // 3 means hit

                // Mark the attack on player's attack board
                controller.getCurrentMatch().getPlayer().getAttackBoard()
                        .markAttack(r, c, wasHit);

                System.out.println("Marked cell (" + r + "," + c + ") as " + (wasHit ? "HIT" : "MISS"));
            }
        }

        // Clean the selection
        selectedRow = -1;
        selectedCol = -1;
        attackButton.setEnabled(false);
        crossBombButton.setEnabled(false);

        // Force board updates
        updateBoards();
        updateGameInfo();
        updatePowerUpsDisplay();

        // Force repaint of both boards
        SwingUtilities.invokeLater(() -> {
            playerBoard.repaint();
            enemyBoard.repaint();
            playerBoard.paintImmediately(playerBoard.getBounds());
            enemyBoard.paintImmediately(enemyBoard.getBounds());
        });

        if (controller.isGameFinished()) {
            showVictory();
            return;
        }

        if (controller.isPlayerTurn()) {
            statusLabel.setText("IMPACT! You can hit again");
        } else {
            executeMachineTurn();
        }
    }

    /**
     * Executes the player attack
     */
    private void executeAttack() {
        if (selectedRow == -1 || selectedCol == -1) {
            return;
        }

        int attackRow = selectedRow;
        int attackCol = selectedCol;

        // Debugging info
        System.out.println("\n=== Player attack ===");
        System.out.println("Attacking: (" + attackRow + "," + attackCol + ")");

        // Execute the attack
        String result = controller.playerAttack(attackRow, attackCol);
        System.out.println("Result: " + result);
        addLog("You attacked (" + attackRow + "," + attackCol + "): " + result);

        // Clean the selection
        selectedRow = -1;
        selectedCol = -1;
        attackButton.setEnabled(false);
        crossBombButton.setEnabled(false);

        // Update status of the boards
        int[][] attackBoardState = controller.getPlayerAttackBoardState();
        System.out.println("State attackBoard[" + attackRow + "][" + attackCol + "] = " + attackBoardState[attackRow][attackCol]);

        // Mark attack if not already marked
        if (attackBoardState[attackRow][attackCol] == 0 || attackBoardState[attackRow][attackCol] == 1) {
            System.err.println("ERROR: The attack was not marked!");
            // Manually mark the attack to keep consistency
            boolean wasHit = result.contains("Hit") || result.contains("Sunk");
            controller.getCurrentMatch().getPlayer().getAttackBoard().markAttack(attackRow, attackCol, wasHit);
            System.out.println("Manual marking applied");
        }

        // Force board updates
        updateBoards();
        updateGameInfo();
        updatePowerUpsDisplay();

        // Force repaint of both boards
        SwingUtilities.invokeLater(() -> {
            playerBoard.repaint();
            enemyBoard.repaint();
            playerBoard.paintImmediately(playerBoard.getBounds());
            enemyBoard.paintImmediately(enemyBoard.getBounds());
        });

        if (controller.isGameFinished()) {
            showVictory();
            return;
        }

        if (controller.isPlayerTurn()) {
            statusLabel.setText("IMPACT! You can hit again");
        } else {
            executeMachineTurn();
        }
    }

    /**
     * Executes the machine turn
     */
    private void executeMachineTurn() {
        turnLabel.setText("Machine's turn...");
        statusLabel.setText("The machine is thinking...");
        attackButton.setEnabled(false);
        crossBombButton.setEnabled(false);

        Timer timer = new Timer(800, null);
        timer.addActionListener(e -> {
            String result = controller.machineAttack();
            addLog("Machine attacked: " + result);

            updateBoards();
            updateGameInfo();

            timer.stop();

            if (controller.isGameFinished()) {
                showVictory();
            } else if (!controller.isPlayerTurn()) {
                executeMachineTurn();
            } else {
                turnLabel.setText("Your turn - Select a cell to attack");
                statusLabel.setText("Is your turn");
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    /**
     * Method to update the boards in the UI
     * Updates both player and enemy boards
     */
    private void updateBoards() {
        // Shows player's own ships and hits/misses in the enemy board
        playerBoard.updateBoard(controller.getPlayerOwnBoardState());

        // Shows only hits and misses in the enemy board made by the player
        enemyBoard.updateBoard(controller.getPlayerAttackBoardState());
    }

    /**
     * Method to update game info labels
     * Updates the number of remaining ships for both players
     */
    private void updateGameInfo() {
        if (controller.getCurrentMatch() != null) {
            int playerShips = controller.getCurrentMatch().getPlayer()
                    .getOwnBoard().getRemainingBoats();
            int enemyShips = controller.getCurrentMatch().getMachine()
                    .getOwnBoard().getRemainingBoats();

            playerShipsLabel.setText("Ships: " + playerShips);
            enemyShipsLabel.setText("Ships: " + enemyShips);
        }
    }

    private void addLog(String message) {
        logArea.append(message + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    private void showVictory() {
        String winner = controller.getWinner().getUsername();
        boolean playerWon = winner.equals("Player");

        int option = JOptionPane.showConfirmDialog(
                this,
                playerWon ? "You Won!\nPlay again?"
                        : "The Machine won\nPlay again?",
                playerWon ? "VICTORY!" : "YOU LOSE",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE
        );

        if (option == JOptionPane.YES_OPTION) {
            resetGame();
        } else {
            System.exit(0);
        }
    }

    private void resetGame() {
        controller.resetGame();
        controller.startNewGame("Player", "password");

        selectedRow = -1;
        selectedCol = -1;
        attackButton.setEnabled(false);
        crossBombButton.setEnabled(false);
        logArea.setText("");

        updateBoards();
        updateGameInfo();
        updatePowerUpsDisplay();

        turnLabel.setText("Your Turn - Select a cell to attack");
        statusLabel.setText("New game started");
        addLog("New game started");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            GameView game = new GameView();
            game.setVisible(true);
        });
    }
}