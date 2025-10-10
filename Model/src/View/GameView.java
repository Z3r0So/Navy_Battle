package View;

import Controller.GameController;
import Controller.Interfaces.IAttackController;
import Controller.Interfaces.IGameLifecycle;
import Controller.Interfaces.ITurnController;
import Controller.Interfaces.IBoardController;
import Model.Boat.Boat;
import Model.Player.HumanPlayer;
import View.Components.*;

import javax.swing.*;
import java.awt.*;

/**
 * GameView - Main Frame (actualizado con ISP)
 */
public class GameView extends JFrame {


    private final IAttackController attackController;
    private final ITurnController turnController;
    private final IGameLifecycle gameController;
    private final IBoardController boardController;

    private HeaderPanel headerPanel;
    private BoardPanel playerBoard;
    private BoardPanel enemyBoard;
    private InfoPanel infoPanel;
    private ActionPanel actionPanel;

    private JLabel playerShipsLabel;
    private JLabel enemyShipsLabel;


    private int selectedRow = -1;
    private int selectedCol = -1;

    public GameView() {

        GameController gc = new GameController();
        this.attackController = gc;
        this.turnController = gc;
        this.gameController = gc;
        this.boardController = gc;

        if (!gameController.startNewGame("Player", "password")) {
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


        headerPanel = new HeaderPanel();
        infoPanel = new InfoPanel();
        actionPanel = new ActionPanel();


        JPanel boardsPanel = createBoardsPanel();


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
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            }
        };
        boardContainer.setOpaque(false);
        boardContainer.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        BoardPanel board = new BoardPanel(isPlayerBoard);
        if (isPlayerBoard) {
            playerBoard = board;
        } else {
            enemyBoard = board;
        }
        boardContainer.add(board);

        JLabel shipsLabel = new JLabel("Ships: 10");
        shipsLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        shipsLabel.setForeground(new Color(0, 96, 100));
        shipsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        if (isPlayerBoard) {
            playerShipsLabel = shipsLabel;
        } else {
            enemyShipsLabel = shipsLabel;
        }

        section.add(titleLabel);
        section.add(Box.createVerticalStrut(15));
        section.add(boardContainer);
        section.add(Box.createVerticalStrut(10));
        section.add(shipsLabel);

        return section;
    }

    private void setupListeners() {

        enemyBoard.setOnCellClickListener((row, col) -> {
            if (gameController.isGameFinished() || !turnController.isPlayerTurn()) {
                return;
            }

            selectedRow = row;
            selectedCol = col;
            actionPanel.setStatus("Cell (" + row + "," + col + ") Selected");
            updateAttackButtons();
        });


        actionPanel.setOnAttackListener(attackType -> {
            switch (attackType) {
                case BASIC -> executeBasicAttack();
                case CROSS_BOMB -> executeCrossBombAttack();
                case TORPEDO_H -> executeTorpedoAttack(true);
                case TORPEDO_V -> executeTorpedoAttack(false);
                case NUKE -> executeNukeAttack();
                case RESET -> resetGame();
            }
        });

        actionPanel.enableResetButton();
    }

    private void executeBasicAttack() {
        if (!isValidAttack()) return;
        String result = attackController.playerAttack(selectedRow, selectedCol);
        infoPanel.addLog("ATTACK (" + selectedRow + "," + selectedCol + "): " + result);
        finishAttack();
    }

    private void executeCrossBombAttack() {
        if (!isValidAttack()) return;
        String result = attackController.playerCrossBombAttack(selectedRow, selectedCol);
        infoPanel.addLog("CROSS BOMB (" + selectedRow + "," + selectedCol + "): " + result);
        finishAttack();
    }

    private void executeTorpedoAttack(boolean horizontal) {
        if (!isValidAttack()) return;
        String result = attackController.playerTorpedoAttack(selectedRow, selectedCol, horizontal);
        String dir = horizontal ? "→" : "↓";
        infoPanel.addLog("TORPEDO " + dir + " (" + selectedRow + "," + selectedCol + "): " + result);
        finishAttack();
    }

    private void executeNukeAttack() {
        if (!isValidAttack()) return;
        String result = attackController.playerNukeAttack(selectedRow, selectedCol);
        infoPanel.addLog("NUKE (" + selectedRow + "," + selectedCol + "): " + result);
        finishAttack();
    }

    private boolean isValidAttack() {
        return selectedRow != -1 && selectedCol != -1;
    }

    private void finishAttack() {
        selectedRow = -1;
        selectedCol = -1;
        enemyBoard.clearSelection();
        actionPanel.disableAllAttackButtons();

        updateAllComponents();

        if (gameController.isGameFinished()) {
            showVictory();
            return;
        }

        if (turnController.isPlayerTurn()) {

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
            String result = attackController.machineAttack();
            infoPanel.addLog("Machine: " + result);
            updateAllComponents();

            if (gameController.isGameFinished()) {
                showVictory();
            } else if (!turnController.isPlayerTurn()) {
                executeMachineTurn();
            } else {
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
        int[][] playerBoardState = boardController.getPlayerOwnBoardState();
        int[][] enemyBoardState = boardController.getPlayerAttackBoardState();

        Boat[][] playerBoatGrid = getBoatGrid(gameController.getCurrentMatch().getPlayer().getOwnBoard());

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
        if (gameController.getCurrentMatch() != null) {
            int playerShips = gameController.getCurrentMatch().getPlayer()
                    .getOwnBoard().getRemainingBoats();
            int enemyShips = gameController.getCurrentMatch().getMachine()
                    .getOwnBoard().getRemainingBoats();

            playerShipsLabel.setText("Ships: " + playerShips);
            enemyShipsLabel.setText("Ships: " + enemyShips);
        }
    }

    private void updatePowerUps() {
        if (gameController.getCurrentMatch() != null) {
            HumanPlayer human = (HumanPlayer) gameController.getCurrentMatch().getPlayer();
            infoPanel.updatePowerUps(
                    human.getPowerUps().getCrossBombs(),
                    human.getPowerUps().getTorpedoes(),
                    human.getPowerUps().getNukes()
            );
        }
    }

    private void updateAttackButtons() {
        boolean cellSelected = (selectedRow != -1 && selectedCol != -1);

        if (cellSelected && gameController.getCurrentMatch() != null) {
            HumanPlayer human = (HumanPlayer) gameController.getCurrentMatch().getPlayer();

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

    private void showVictory() {
        String winner = gameController.getWinner().getUsername();
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
        gameController.resetGame();
        gameController.startNewGame("Player", "password");

        selectedRow = -1;
        selectedCol = -1;

        enemyBoard.clearSelection();
        playerBoard.clearSelection();
        actionPanel.disableAllAttackButtons();
        actionPanel.setStatus("New game started");
        headerPanel.setTurnMessage("Your Turn - Select a cell to attack");
        infoPanel.clearLog();
        infoPanel.addLog("New game started");

        updateAllComponents();
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
