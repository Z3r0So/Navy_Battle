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
 *
 * Follows SRP: Only responsible for object creation and wiring
 * Follows DIP: Creates dependencies based on interfaces
 *
 * This is the composition root where all dependencies are wired together
 */
public class GameViewFactory {

    /**
     * Creates a fully configured GameView with all dependencies
     * This is the main entry point for the application
     *
     * @return Configured GameView ready to display
     */
    public static GameView createGameView() {
        // Create all dependencies from bottom up

        // Services layer
        IShipPlacementService placementService = new ShipPlacementService();
        IAttackService attackService = new AttackService();
        IAttackExecutor attackExecutor = new AttackExecutor(attackService);

        // Player creation
        IPlayerCreator playerCreator = new PlayerCreator();

        // Fleet management
        IFleetManager fleetManager = new FleetManager(placementService);

        // View.Main game controller with all dependencies
        GameController gameController = new GameController(
                playerCreator,
                fleetManager,
                attackExecutor,
                attackService
        );

        // Create view with controller interfaces (DIP)
        return new GameView(
                gameController,  // IAttackController
                gameController,  // ITurnController
                gameController,  // IGameLifecycle
                gameController   // IBoardController
        );
    }

    /**
     * Creates GameView with custom dependencies (for testing or customization)
     *
     * @param attackController Controller for attacks
     * @param turnController Controller for turns
     * @param gameLifecycle Controller for game lifecycle
     * @param boardController Controller for board operations
     * @return Configured GameView
     */
    public static GameView createGameView(
            IAttackController attackController,
            ITurnController turnController,
            IGameLifecycle gameLifecycle,
            IBoardController boardController
    ) {
        return new GameView(
                attackController,
                turnController,
                gameLifecycle,
                boardController
        );
    }
}