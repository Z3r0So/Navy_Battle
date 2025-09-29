package View;

import Controller.GameController;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;

/**
 * Interfaz gráfica principal del juego de Batalla Naval usando Swing
 * Estilo visual moderno con barcos circulares y colores vibrantes
 */
public class GameView extends JFrame {

    private GameController controller;

    private static final int CELL_SIZE = 45;
    private static final int GRID_SIZE = 10;
    private static final Color WATER_COLOR = new Color(174, 190, 239);
    private static final Color GRID_LINE_COLOR = new Color(100, 177, 210);
    private static final Color SHIP_PLAYER_COLOR = new Color(100, 126, 230, 255);
    private static final Color SHIP_HIT_COLOR = new Color(230, 80, 80);
    private static final Color MISS_COLOR = new Color(110, 195, 227, 255);
    private static final Color SELECTED_COLOR = new Color(7, 255, 226, 255);

    private BoardPanel playerBoard;
    private BoardPanel enemyBoard;
    private JLabel statusLabel;
    private JLabel playerShipsLabel;
    private JLabel enemyShipsLabel;
    private JLabel turnLabel;
    private JButton attackButton;
    private JTextArea logArea;


    private int selectedRow = -1;
    private int selectedCol = -1;
    private JPanel panel1;

    public GameView() {
        controller = new GameController();

        //Start a new game with default player
        boolean gameStarted = controller.startNewGame("Player", "password");
        if (!gameStarted) {
            JOptionPane.showMessageDialog(this, "Couldn't start a new game",
                    "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        initComponents();
        updateBoards();
        updateGameInfo();
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

        //
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(0, 96, 100));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        //
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

        //Board Creation
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

    private JPanel createInfoPanel() {
        JPanel infoPanel = new JPanel(new BorderLayout(10, 10));
        infoPanel.setPreferredSize(new Dimension(250, 0));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel logTitle = new JLabel("GAME LOG");
        logTitle.setFont(new Font("Arial", Font.BOLD, 16));

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Courier New", Font.PLAIN, 12));
        logArea.setBackground(new Color(245, 245, 245));
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setPreferredSize(new Dimension(230, 400));

        infoPanel.add(logTitle, BorderLayout.NORTH);
        infoPanel.add(scrollPane, BorderLayout.CENTER);

        return infoPanel;
    }

    private JPanel createActionPanel() {
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        actionPanel.setBackground(new Color(0, 131, 143));

        statusLabel = new JLabel("Select a cell to attack");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        statusLabel.setForeground(Color.WHITE);

        attackButton = new JButton("ATTACK");
        attackButton.setFont(new Font("Arial", Font.BOLD, 16));
        attackButton.setPreferredSize(new Dimension(150, 40));
        attackButton.setBackground(new Color(255, 87, 34));
        attackButton.setForeground(Color.WHITE);
        attackButton.setFocusPainted(false);
        attackButton.setBorderPainted(false);
        attackButton.setEnabled(false);
        attackButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        attackButton.addActionListener(e -> executeAttack());

        JButton resetButton = new JButton("NEW GAME");
        resetButton.setFont(new Font("Arial", Font.PLAIN, 14));
        resetButton.setPreferredSize(new Dimension(150, 40));
        resetButton.setBackground(new Color(76, 175, 80));
        resetButton.setForeground(Color.WHITE);
        resetButton.setFocusPainted(false);
        resetButton.setBorderPainted(false);
        resetButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        resetButton.addActionListener(e -> resetGame());

        actionPanel.add(statusLabel);
        actionPanel.add(Box.createHorizontalStrut(50));
        actionPanel.add(attackButton);
        actionPanel.add(resetButton);

        return actionPanel;
    }

    private class BoardPanel extends JPanel {
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
                repaint();
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
            // Debug
            if (!isPlayerBoard) {
                int nonZeroCount = 0;
                for (int i = 0; i < GRID_SIZE; i++) {
                    for (int j = 0; j < GRID_SIZE; j++) {
                        if (newState[i][j] != 0) nonZeroCount++;
                    }
                }
                System.out.println("Updating enemy board. Cells with a state != 0: " + nonZeroCount);
            }

            //Copy state
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

                    // Borde
                    g2d.setColor(GRID_LINE_COLOR);
                    g2d.setStroke(new BasicStroke(1));
                    g2d.draw(new RoundRectangle2D.Double(x, y, CELL_SIZE - 2, CELL_SIZE - 2, 5, 5));

                    //
                    int state = boardState[row][col];
                    drawCellContent(g2d, x, y, state);

                    //
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

        private void drawCellContent(Graphics2D g2d, int x, int y, int state) {
            int centerX = x + CELL_SIZE / 2;
            int centerY = y + CELL_SIZE / 2;
            int radius = CELL_SIZE / 2 - 5;

            switch (state) {
                case 1: //
                    if (isPlayerBoard) {
                        g2d.setColor(SHIP_PLAYER_COLOR);
                        g2d.fill(new Ellipse2D.Double(
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
                    break;
            }
        }
    }

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

        //Clean the selection
        selectedRow = -1;
        selectedCol = -1;
        attackButton.setEnabled(false);

        // Actualizar tableros
        int[][] attackBoardState = controller.getPlayerAttackBoardState();
        System.out.println("Estado attackBoard[" + attackRow + "][" + attackCol + "] = " + attackBoardState[attackRow][attackCol]);

        // Verificar que se marcó correctamente
        if (attackBoardState[attackRow][attackCol] == 0 || attackBoardState[attackRow][attackCol] == 1) {
            System.err.println("ERROR: El ataque NO se marcó en attackBoard!");
            // Forzar marcado manual (workaround)
            boolean wasHit = result.contains("Hit") || result.contains("Sunk");
            controller.getCurrentMatch().getPlayer().getAttackBoard().markAttack(attackRow, attackCol, wasHit);
            System.out.println("Marcado manual aplicado");
        }

        updateBoards();
        updateGameInfo();

        // Forzar repaint
        enemyBoard.repaint();

        if (controller.isGameFinished()) {
            showVictory();
            return;
        }

        if (controller.isPlayerTurn()) {
            statusLabel.setText("¡Impacto! Puedes atacar de nuevo");
        } else {
            executeMachineTurn();
        }
    }

    /**
     * Ejecuta el turno de la máquina
     */
    private void executeMachineTurn() {
        turnLabel.setText("Turno de la máquina...");
        statusLabel.setText("La máquina está atacando...");
        attackButton.setEnabled(false);

        Timer timer = new Timer(800, null);
        timer.addActionListener(e -> {
            String result = controller.machineAttack();
            addLog("🤖 Máquina atacó: " + result);

            updateBoards();
            updateGameInfo();

            timer.stop();

            if (controller.isGameFinished()) {
                showVictory();
            } else if (!controller.isPlayerTurn()) {
                executeMachineTurn();
            } else {
                turnLabel.setText("Tu turno - Selecciona una casilla para atacar");
                statusLabel.setText("Es tu turno");
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    private void updateBoards() {
        // Shows player's own ships and hits/misses in the enemy board
        playerBoard.updateBoard(controller.getPlayerOwnBoardState());

        // Shows only hits and misses in the enemy board made by the player
        enemyBoard.updateBoard(controller.getPlayerAttackBoardState());
    }

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
        boolean playerWon = winner.equals("Jugador");

        int option = JOptionPane.showConfirmDialog(
                this,
                playerWon ? "¡You Won!\nPlay again?"
                        : "The Machine won\nPlay again?",
                playerWon ? "¡VICTORY!" : "YOU LOSE",
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
        logArea.setText("");

        updateBoards();
        updateGameInfo();

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