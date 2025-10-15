package View;

import Controller.Interfaces.*;
import Database.PlayerDAO;
import Model.Boat.Boat;
import Model.Player.HumanPlayer;
import View.Components.*;

import javax.swing.*;
import java.awt.*;

/**
 * GameView - Main Frame (ACTUALIZADO con integración de base de datos)
 *
 * Responsibilities:
 * - Coordinate visual components
 * - Delegate actions to appropriate controllers
 * - Update UI based on game state
 * - Update player wins in database
 */
public class GameView extends JFrame implements IGameView {

    // NUEVO: Información del jugador y DAO
    private final String playerName;
    private final PlayerDAO playerDAO;

    // Controller dependencies (DIP - depend on abstractions)
    private final IAttackController attackController;
    private final ITurnController turnController;
    private final IGameLifecycle gameLifecycle;
    private final IBoardController boardController;

    // UI Components (SRP - each has single responsibility)
    private HeaderPanel headerPanel;
    private BoardPanel playerBoard;
    private BoardPanel enemyBoard;
    private InfoPanel infoPanel;
    private ActionPanel actionPanel;
    private JLabel playerShipsLabel;
    private JLabel enemyShipsLabel;

    // Selection state (managed by view coordinator)
    private int selectedRow = -1;
    private int selectedCol = -1;

    /**
     * Constructor with dependency injection Y NOMBRE DE JUGADOR
     *
     * @param playerName Nombre del jugador (desde login)
     * @param attackController Controller for attack operations
     * @param turnController Controller for turn management
     * @param gameLifecycle Controller for game lifecycle
     * @param boardController Controller for board operations
     */
    public GameView(
            String playerName,
            IAttackController attackController,
            ITurnController turnController,
            IGameLifecycle gameLifecycle,
            IBoardController boardController
    ) {
        this.playerName = playerName;
        this.playerDAO = new PlayerDAO();
        this.attackController = attackController;
        this.turnController = turnController;
        this.gameLifecycle = gameLifecycle;
        this.boardController = boardController;

        initializeGame();
        initComponents();
        setupListeners();
        updateAllComponents();
    }

    /**
     * Initialize game state through lifecycle controller
     * AHORA USA EL NOMBRE DEL JUGADOR REAL
     */
    private void initializeGame() {
        if (!gameLifecycle.startNewGame(playerName, "default")) {
            showErrorAndExit("Couldn't start a new game");
        }
    }

    /**
     * Initialize all UI components
     */
    private void initComponents() {
        setTitle("Navy Battle - " + playerName); // ACTUALIZADO con nombre del jugador
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        setResizable(false);
        getContentPane().setBackground(new Color(224, 247, 250));

        // Create all panels
        headerPanel = new HeaderPanel();
        headerPanel.setTitle("Navy Battle - " + playerName); // ACTUALIZADO
        infoPanel = new InfoPanel();
        actionPanel = new ActionPanel();
        JPanel boardsPanel = createBoardsPanel();

        // Add to frame
        add(headerPanel, BorderLayout.NORTH);
        add(boardsPanel, BorderLayout.CENTER);
        add(infoPanel, BorderLayout.EAST);
        add(actionPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }

    /**
     * Create the boards panel with both player and enemy boards
     */
    private JPanel createBoardsPanel() {
        JPanel boardsPanel = new JPanel();
        boardsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 40, 30));
        boardsPanel.setBackground(new Color(224, 247, 250));

        JPanel playerSection = createBoardSection("Your Fleet", true);
        JPanel enemySection = createBoardSection("Enemy Waters", false);

        boardsPanel.add(playerSection);
        boardsPanel.add(enemySection);

        return boardsPanel;
    }

    /**
     * Create a single board section with title and ship counter
     */
    private JPanel createBoardSection(String title, boolean isPlayerBoard) {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBackground(new Color(224, 247, 250));

        // Title
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(0, 96, 100));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Board container with rounded border
        JPanel boardContainer = createRoundedPanel();
        BoardPanel board = new BoardPanel(isPlayerBoard);

        if (isPlayerBoard) {
            playerBoard = board;
        } else {
            enemyBoard = board;
        }
        boardContainer.add(board);

        // Ships counter
        JLabel shipsLabel = new JLabel("Ships: 10");
        shipsLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        shipsLabel.setForeground(new Color(0, 96, 100));
        shipsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        if (isPlayerBoard) {
            playerShipsLabel = shipsLabel;
        } else {
            enemyShipsLabel = shipsLabel;
        }

        // Assemble section
        section.add(titleLabel);
        section.add(Box.createVerticalStrut(15));
        section.add(boardContainer);
        section.add(Box.createVerticalStrut(10));
        section.add(shipsLabel);

        return section;
    }

    /**
     * Create a rounded panel for board container
     */
    private JPanel createRoundedPanel() {
        return new JPanel() {
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
    }

    /**
     * Setup all event listeners
     */
    private void setupListeners() {
        // Enemy board cell selection
        enemyBoard.setOnCellClickListener(this::handleCellSelection);

        // Attack action buttons
        actionPanel.setOnAttackListener(this::handleAttackAction);

        // Enable reset button always
        actionPanel.enableResetButton();
    }

    /**
     * Handle cell selection on enemy board
     */
    private void handleCellSelection(int row, int col) {
        if (gameLifecycle.isGameFinished() || !turnController.isPlayerTurn()) {
            return;
        }

        selectedRow = row;
        selectedCol = col;
        actionPanel.setStatus("Cell (" + row + "," + col + ") selected");
        updateAttackButtons();
    }

    /**
     * Handle attack action from action panel
     */
    private void handleAttackAction(ActionPanel.AttackType attackType) {
        switch (attackType) {
            case BASIC -> executeBasicAttack();
            case CROSS_BOMB -> executeCrossBombAttack();
            case TORPEDO_H -> executeTorpedoAttack(true);
            case TORPEDO_V -> executeTorpedoAttack(false);
            case NUKE -> executeNukeAttack();
            case RESET -> resetView();
        }
    }

    // ==================== Attack Execution Methods ====================

    private void executeBasicAttack() {
        if (!isValidSelection()) return;

        String result = attackController.playerAttack(selectedRow, selectedCol);
        infoPanel.addLog("⚔️ ATTACK (" + selectedRow + "," + selectedCol + "): " + result);
        finishAttackSequence();
    }

    private void executeCrossBombAttack() {
        if (!isValidSelection()) return;

        String result = attackController.playerCrossBombAttack(selectedRow, selectedCol);
        infoPanel.addLog("💣 CROSS BOMB (" + selectedRow + "," + selectedCol + "): " + result);
        finishAttackSequence();
    }

    private void executeTorpedoAttack(boolean horizontal) {
        if (!isValidSelection()) return;

        String result = attackController.playerTorpedoAttack(
                selectedRow, selectedCol, horizontal
        );
        String direction = horizontal ? "→" : "↓";
        infoPanel.addLog("🚀 TORPEDO " + direction + " (" + selectedRow + "," +
                selectedCol + "): " + result);
        finishAttackSequence();
    }

    private void executeNukeAttack() {
        if (!isValidSelection()) return;

        String result = attackController.playerNukeAttack(selectedRow, selectedCol);
        infoPanel.addLog("☢️ NUKE (" + selectedRow + "," + selectedCol + "): " + result);
        finishAttackSequence();
    }

    /**
     * Validates that a cell is selected
     */
    private boolean isValidSelection() {
        return selectedRow != -1 && selectedCol != -1;
    }

    /**
     * Complete attack sequence and check game state
     */
    private void finishAttackSequence() {
        clearSelection();
        updateAllComponents();

        if (gameLifecycle.isGameFinished()) {
            handleGameEnd();
            return;
        }

        if (turnController.isPlayerTurn()) {
            handleContinuousPlayerTurn();
        } else {
            executeMachineTurn();
        }
    }

    /**
     * Handle continuous player turn after hit
     */
    private void handleContinuousPlayerTurn() {
        actionPanel.setStatus("💥 HIT! Attack again");
        headerPanel.setTurnMessage("Your turn - Hit! Attack again");
    }

    /**
     * Execute machine's turn with animation
     */
    private void executeMachineTurn() {
        headerPanel.setTurnMessage("🤖 Machine's turn...");
        actionPanel.setStatus("Machine is thinking...");
        actionPanel.disableAllAttackButtons();
        enemyBoard.setInteractive(false);

        // Delay for visual effect
        Timer timer = new Timer(800, e -> {
            String result = attackController.machineAttack();
            infoPanel.addLog("🤖 Machine: " + result);
            updateAllComponents();

            if (gameLifecycle.isGameFinished()) {
                handleGameEnd();
            } else if (!turnController.isPlayerTurn()) {
                // Machine hit - attacks again
                executeMachineTurn();
            } else {
                // Player's turn now
                enablePlayerTurn();
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    /**
     * Enable player turn after machine finishes
     */
    private void enablePlayerTurn() {
        headerPanel.setTurnMessage("Your turn - Select a cell to attack");
        actionPanel.setStatus("Your turn");
        enemyBoard.setInteractive(true);
    }

    /**
     * Clear cell selection
     */
    private void clearSelection() {
        selectedRow = -1;
        selectedCol = -1;
        enemyBoard.clearSelection();
        actionPanel.disableAllAttackButtons();
    }

    // ==================== IGameView Implementation ====================

    @Override
    public void updateBoards() {
        int[][] playerBoardState = boardController.getPlayerOwnBoardState();
        int[][] enemyBoardState = boardController.getPlayerAttackBoardState();

        Boat[][] playerBoatGrid = extractBoatGrid(
                gameLifecycle.getCurrentMatch().getPlayer().getOwnBoard()
        );

        playerBoard.updateBoard(playerBoardState, playerBoatGrid);
        enemyBoard.updateBoard(enemyBoardState, null);
    }

    @Override
    public void updateGameInfo() {
        if (gameLifecycle.getCurrentMatch() != null) {
            int playerShips = gameLifecycle.getCurrentMatch()
                    .getPlayer().getOwnBoard().getRemainingBoats();
            int enemyShips = gameLifecycle.getCurrentMatch()
                    .getMachine().getOwnBoard().getRemainingBoats();

            playerShipsLabel.setText("Ships: " + playerShips);
            enemyShipsLabel.setText("Ships: " + enemyShips);
        }
    }

    @Override
    public void updatePowerUpsDisplay() {
        if (gameLifecycle.getCurrentMatch() != null) {
            HumanPlayer human = (HumanPlayer) gameLifecycle.getCurrentMatch().getPlayer();

            infoPanel.updatePowerUps(
                    human.getPowerUps().getCrossBombs(),
                    human.getPowerUps().getTorpedoes(),
                    human.getPowerUps().getNukes()
            );
        }
    }

    @Override
    public void showMessage(String message) {
        actionPanel.setStatus(message);
        infoPanel.addLog(message);
    }

    @Override
    public void showVictory(String winner) {
        boolean playerWon = winner.equals(playerName); // ACTUALIZADO para usar nombre real

        // NUEVO: Si el jugador ganó, actualizar la base de datos
        if (playerWon) {
            updatePlayerWinsInDatabase();
        }

        int option = JOptionPane.showConfirmDialog(
                this,
                playerWon ? "🎉 You Won!\nPlay again?" : "💀 Machine Won\nPlay again?",
                playerWon ? "VICTORY!" : "DEFEAT",
                JOptionPane.YES_NO_OPTION,
                playerWon ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE
        );

        if (option == JOptionPane.YES_OPTION) {
            resetView();
        } else {
            backToLogin();
        }
    }

    /**
     * NUEVO: Actualiza las victorias del jugador en la base de datos
     */
    private void updatePlayerWinsInDatabase() {
        boolean success = playerDAO.incrementWins(playerName);

        if (success) {
            int totalWins = playerDAO.getPlayerWins(playerName);
            infoPanel.addLog("🏆 Victory recorded! Total wins: " + totalWins);
        } else {
            System.err.println("Error updating player wins in database");
        }
    }

    /**
     * NUEVO: Vuelve a la pantalla de login
     */
    private void backToLogin() {
        dispose();
        SwingUtilities.invokeLater(() -> {
            LoginView loginView = new LoginView();
            loginView.setVisible(true);
        });
    }

    @Override
    public void enableAttackButtons(boolean enable) {
        if (enable) {
            updateAttackButtons();
        } else {
            actionPanel.disableAllAttackButtons();
        }
    }

    @Override
    public void resetView() {
        gameLifecycle.resetGame();
        gameLifecycle.startNewGame(playerName, "default"); // USA NOMBRE REAL

        clearSelection();
        playerBoard.clearSelection();

        actionPanel.setStatus("⚔️ New game started");
        headerPanel.setTurnMessage("Your turn - Select a cell to attack");

        infoPanel.clearLog();
        infoPanel.addLog("⚔️ New game started for " + playerName + "!");

        updateAllComponents();
        enemyBoard.setInteractive(true);
    }

    // ==================== Helper Methods ====================

    /**
     * Update all UI components at once
     */
    private void updateAllComponents() {
        updateBoards();
        updateGameInfo();
        updatePowerUpsDisplay();
        updateAttackButtons();
    }

    /**
     * Update attack buttons based on selection and power-ups
     */
    private void updateAttackButtons() {
        boolean cellSelected = isValidSelection();

        if (cellSelected && gameLifecycle.getCurrentMatch() != null) {
            HumanPlayer human = (HumanPlayer) gameLifecycle.getCurrentMatch().getPlayer();

            actionPanel.setAttackButtonsEnabled(
                    true,
                    human.getPowerUps().hasCrossBombs(),
                    human.getPowerUps().hasTorpedoes(),
                    human.getPowerUps().hasTorpedoes(),
                    human.getPowerUps().hasNukes()
            );
        } else {
            actionPanel.disableAllAttackButtons();
        }
    }

    /**
     * Extract boat grid from board for rendering
     */
    private Boat[][] extractBoatGrid(Model.Board.Board board) {
        Boat[][] grid = new Boat[10][10];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                grid[i][j] = board.getBoatAt(i, j);
            }
        }
        return grid;
    }

    /**
     * Handle game end
     */
    private void handleGameEnd() {
        String winner = gameLifecycle.getWinner().getUsername();
        showVictory(winner);
    }

    /**
     * Show error and exit application
     */
    private void showErrorAndExit(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
        System.exit(1);
    }
}