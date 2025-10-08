package View;

import Controller.IGameController;
import Controller.GameController;
import Model.Boat.Boat;
import Model.Player.HumanPlayer;
import View.Components.*;
import javax.swing.*;
import java.awt.*;

/**
 * GameView - Main Frame
 */
public class GameView extends JFrame {

    //Controller
    private final IGameController controller;

    //Components
    private HeaderPanel headerPanel;
    private BoardPanel playerBoard;
    private BoardPanel enemyBoard;
    private InfoPanel infoPanel;
    private ActionPanel actionPanel;

    private JLabel playerShipsLabel;
    private JLabel enemyShipsLabel;

    //State
    private int selectedRow = -1;
    private int selectedCol = -1;

    public GameView() {
        this.controller = new GameController();

        if (!controller.startNewGame("Player", "password")) {
            JOptionPane.showMessageDialog(this, "Couldn't start a new game",
                    "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        initComponents();
        setupListeners();
        updateAllComponents();
    }

    private void initComponents() {
        setTitle("Navy Battle - SOLID Edition");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        setResizable(false);
        getContentPane().setBackground(new Color(224, 247, 250));

        // Create components
        headerPanel = new HeaderPanel();
        infoPanel = new InfoPanel();
        actionPanel = new ActionPanel();

        // Create board sections
        JPanel boardsPanel = createBoardsPanel();

        // Add components to frame
        add(headerPanel, BorderLayout.NORTH);
        add(boardsPanel, BorderLayout.CENTER);
        add(infoPanel, BorderLayout.EAST);
        add(actionPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }

    private JPanel createBoardsPanel() {
        JPanel boardsPanel = new JPanel();
        boardsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 40, 30));
        boardsPanel.setBackground(new Color(224, 247, 250));

        // Player section
        JPanel playerSection = createBoardSection("Your Crew", true);

        // Enemy section
        JPanel enemySection = createBoardSection("Enemy Board", false);

        boardsPanel.add(playerSection);
        boardsPanel.add(enemySection);

        return boardsPanel;
    }

    private JPanel createBoardSection(String title, boolean isPlayerBoard) {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBackground(new Color(224, 247, 250));

        // Title
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(0, 96, 100));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Board container with rounded background
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

        // Create board panel
        BoardPanel board = new BoardPanel(isPlayerBoard);
        if (isPlayerBoard) {
            playerBoard = board;
        } else {
            enemyBoard = board;
        }
        boardContainer.add(board);

        // Ships counter
        JLabel shipsLabel = new JLabel("Ships: 8");
        shipsLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        shipsLabel.setForeground(new Color(0, 96, 100));
        shipsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        if (isPlayerBoard) {
            playerShipsLabel = shipsLabel;
        } else {
            enemyShipsLabel = shipsLabel;
        }

        // Add components to section
        section.add(titleLabel);
        section.add(Box.createVerticalStrut(15));
        section.add(boardContainer);
        section.add(Box.createVerticalStrut(10));
        section.add(shipsLabel);

        return section;
    }

    private void setupListeners() {
        // Enemy board click listener
        enemyBoard.setOnCellClickListener((row, col) -> {
            if (controller.isGameFinished() || !controller.isPlayerTurn()) {
                return;
            }

            selectedRow = row;
            selectedCol = col;
            actionPanel.setStatus("Cell (" + row + "," + col + ") Selected");
            updateAttackButtons();
        });

        // Action panel listeners
        actionPanel.setOnAttackListener(attackType -> {
            switch (attackType) {
                case BASIC:
                    executeBasicAttack();
                    break;
                case CROSS_BOMB:
                    executeCrossBombAttack();
                    break;
                case TORPEDO_H:
                    executeTorpedoAttack(true);
                    break;
                case TORPEDO_V:
                    executeTorpedoAttack(false);
                    break;
                case NUKE:
                    executeNukeAttack();
                    break;
                case RESET:
                    resetGame();
                    break;
            }
        });

        // Enable reset button always
        actionPanel.enableResetButton();
    }

    private void executeBasicAttack() {
        if (!isValidAttack()) return;

        String result = controller.playerAttack(selectedRow, selectedCol);
        infoPanel.addLog("ATTACK (" + selectedRow + "," + selectedCol + "): " + result);

        finishAttack();
    }

    private void executeCrossBombAttack() {
        if (!isValidAttack()) return;

        String result = controller.playerCrossBombAttack(selectedRow, selectedCol);
        infoPanel.addLog("CROSS BOMB (" + selectedRow + "," + selectedCol + "): " + result);

        finishAttack();
    }

    private void executeTorpedoAttack(boolean horizontal) {
        if (!isValidAttack()) return;

        String result = controller.playerTorpedoAttack(selectedRow, selectedCol, horizontal);
        String direction = horizontal ? "→" : "↓";
        infoPanel.addLog("TORPEDO " + direction + " (" + selectedRow + "," + selectedCol + "): " + result);

        finishAttack();
    }

    private void executeNukeAttack() {
        if (!isValidAttack()) return;

        String result = controller.playerNukeAttack(selectedRow, selectedCol);
        infoPanel.addLog("NUKE (" + selectedRow + "," + selectedCol + "): " + result);

        finishAttack();
    }

    private boolean isValidAttack() {
        return selectedRow != -1 && selectedCol != -1;
    }

    private void finishAttack() {
        // Clear selection
        selectedRow = -1;
        selectedCol = -1;
        enemyBoard.clearSelection();
        actionPanel.disableAllAttackButtons();

        // Update all components
        updateAllComponents();

        // Check game state
        if (controller.isGameFinished()) {
            showVictory();
            return;
        }

        if (controller.isPlayerTurn()) {
            actionPanel.setStatus("IMPACT! You can attack again");
            headerPanel.setTurnMessage("Your turn - Impact! Attack again");
        } else {
            executeMachineTurn();
        }
    }

    private void executeMachineTurn() {
        headerPanel.setTurnMessage("Machine's turn...");
        actionPanel.setStatus("The machine is thinking...");
        actionPanel.disableAllAttackButtons();
        enemyBoard.setInteractive(false);

        Timer timer = new Timer(800, e -> {
            String result = controller.machineAttack();
            infoPanel.addLog("Machine: " + result);

            updateAllComponents();

            if (controller.isGameFinished()) {
                showVictory();
            } else if (!controller.isPlayerTurn()) {
                // Machine got a hit, continues
                executeMachineTurn();
            } else {
                // Player's turn
                headerPanel.setTurnMessage("Your turn - Select a cell to attack");
                actionPanel.setStatus("Your turn");
                enemyBoard.setInteractive(true);
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    private void updateAllComponents() {
        updateBoards();
        updateGameInfo();
        updatePowerUps();
        updateAttackButtons();
    }

    private void updateBoards() {
        // Get board states
        int[][] playerBoardState = controller.getPlayerOwnBoardState();
        int[][] enemyBoardState = controller.getPlayerAttackBoardState();

        // Get boat grids for coloring
        Boat[][] playerBoatGrid = getBoatGrid(controller.getCurrentMatch().getPlayer().getOwnBoard());

        // Update both boards
        playerBoard.updateBoard(playerBoardState, playerBoatGrid);
        enemyBoard.updateBoard(enemyBoardState, null);
    }

    private Boat[][] getBoatGrid(Model.Board.Board board) {
        Boat[][] grid = new Boat[10][10];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                grid[i][j] = board.getBoatAt(i, j);
            }
        }
        return grid;
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

    private void updatePowerUps() {
        if (controller.getCurrentMatch() != null) {
            HumanPlayer human = (HumanPlayer) controller.getCurrentMatch().getPlayer();
            infoPanel.updatePowerUps(
                    human.getPowerUps().getCrossBombs(),
                    human.getPowerUps().getTorpedoes(),
                    human.getPowerUps().getNukes()
            );
        }
    }

    private void updateAttackButtons() {
        boolean cellSelected = (selectedRow != -1 && selectedCol != -1);

        if (cellSelected && controller.getCurrentMatch() != null) {
            HumanPlayer human = (HumanPlayer) controller.getCurrentMatch().getPlayer();

            actionPanel.setAttackButtonsEnabled(
                    true, // basic attack always available
                    human.getPowerUps().hasCrossBombs(),
                    human.getPowerUps().hasTorpedoes(),
                    human.getPowerUps().hasTorpedoes(),
                    human.getPowerUps().hasNukes()
            );
        } else {
            actionPanel.disableAllAttackButtons();
        }
    }

    private void showVictory() {
        String winner = controller.getWinner().getUsername();
        boolean playerWon = winner.equals("Player");

        int option = JOptionPane.showConfirmDialog(
                this,
                playerWon ? "You Won!\nPlay again?" : "Machine won\nPlay again?",
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

        // Reset all components
        enemyBoard.clearSelection();
        playerBoard.clearSelection();
        actionPanel.disableAllAttackButtons();
        actionPanel.setStatus("New game started");
        headerPanel.setTurnMessage("Your Turn - Select a cell to attack");
        infoPanel.clearLog();
        infoPanel.addLog("New game started");

        // Update everything
        updateAllComponents();

        // Enable interaction
        enemyBoard.setInteractive(true);
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