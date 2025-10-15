package View;

import Controller.FleetManager;
import Controller.GameController;
import Controller.Interfaces.*;
import Model.Player.Interfaces.IPlayerCreator;
import Model.Player.PlayerCreator;
import Services.*;
import Services.Interfaces.IAttackExecutor;
import Services.Interfaces.IAttackService;
import Services.Interfaces.IShipPlacementService;

/**
 * GameViewFactory - Factory for creating GameView with all dependencies
 * ACTUALIZADO para recibir el nombre del jugador desde el login
 *
 * Follows SRP: Only responsible for object creation and wiring
 * Follows DIP: Creates dependencies based on interfaces
 */
public class GameViewFactory {

    /**
     * Creates a fully configured GameView with all dependencies
     * CON NOMBRE DE JUGADOR (para integración con login)
     *
     * @param playerName Nombre del jugador desde el login
     * @return Configured GameView ready to display
     */
    public static GameView createGameView(String playerName) {
        // Create all dependencies from bottom up

        // Services layer
        IShipPlacementService placementService = new ShipPlacementService();
        IAttackService attackService = new AttackService();
        IAttackExecutor attackExecutor = new AttackExecutor(attackService);

        // Player creation
        IPlayerCreator playerCreator = new PlayerCreator();

        // Fleet management
        IFleetManager fleetManager = new FleetManager(placementService);

        // Main game controller with all dependencies
        GameController gameController = new GameController(
                playerCreator,
                fleetManager,
                attackExecutor,
                attackService
        );

        // Create view with controller interfaces (DIP) and player name
        return new GameView(
                playerName,      // NUEVO: nombre del jugador
                gameController,  // IAttackController
                gameController,  // ITurnController
                gameController,  // IGameLifecycle
                gameController   // IBoardController
        );
    }

    /**
     * Creates a fully configured GameView with default player name
     * MANTENER PARA COMPATIBILIDAD CON CÓDIGO ANTIGUO
     *
     * @return Configured GameView ready to display
     */
    public static GameView createGameView() {
        return createGameView("Player"); // Nombre por defecto
    }

    /**
     * Creates GameView with custom dependencies (for testing or customization)
     *
     * @param playerName Nombre del jugador
     * @param attackController Controller for attacks
     * @param turnController Controller for turns
     * @param gameLifecycle Controller for game lifecycle
     * @param boardController Controller for board operations
     * @return Configured GameView
     */
    public static GameView createGameView(
            String playerName,
            IAttackController attackController,
            ITurnController turnController,
            IGameLifecycle gameLifecycle,
            IBoardController boardController
    ) {
        return new GameView(
                playerName,
                attackController,
                turnController,
                gameLifecycle,
                boardController
        );
    }
}