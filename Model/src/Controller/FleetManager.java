package Controller;

import Controller.Interfaces.IFleetManager;
import Model.Board.Board;
import Model.Boat.*;
import Services.Interfaces.IShipPlacementService;

public class FleetManager implements IFleetManager {
    private final IShipPlacementService placementService;
    /**Constructor with dependency injection
     *@param placementService Service to handle ship placement logic
     */
    public FleetManager(IShipPlacementService placementService) {
        this.placementService = placementService;
    }

    /**Creates the standard game fleet
      Standard configuration:
     *2 Aircraft Carriers (length 6)
     *2 Cruises (length 4)
     *3 Destructors (length 3)
     *3 Submarines (length 2)
     * @return Array of 10 boats
     */
    @Override
    public Boat[] createStandardFleet() {
        return new Boat[] {
                new Aircrafter(),
                new Aircrafter(),
                new Cruise(),
                new Cruise(),
                new Destructor(),
                new Destructor(),
                new Destructor(),
                new Submarine(),
                new Submarine(),
                new Submarine()
        };
    }

    /**Creates a custom fleet with specified quantities
     * @param aircrafters Number of aircraft carriers
     * @param cruises Number of cruises
     * @param destructors Number of destructors
     * @param submarines Number of submarines
     * @return Array of boats with custom configuration
     */
    @Override
    public Boat[] createCustomFleet(int aircrafters, int cruises, int destructors, int submarines) {
        int totalShips = aircrafters + cruises + destructors + submarines;
        Boat[] fleet = new Boat[totalShips];

        int index = 0;

        //Add aircraft carriers
        for (int i = 0; i < aircrafters; i++) {
            fleet[index++] = new Aircrafter();
        }

        //Add cruises
        for (int i = 0; i < cruises; i++) {
            fleet[index++] = new Cruise();
        }

        //Add destructors
        for (int i = 0; i < destructors; i++) {
            fleet[index++] = new Destructor();
        }

        //Add submarines
        for (int i = 0; i < submarines; i++) {
            fleet[index++] = new Submarine();
        }

        return fleet;
    }

    /**Deploys a fleet on a board using the placement service
     * Delegates actual placement to the injected service (SRP)
     *
     * @param board The board where ships will be placed
     * @param fleet The fleet to deploy
     * @return true if all ships placed successfully, false otherwise
     */
    @Override
    public boolean deployFleet(Board board, Boat[] fleet) {
        if (fleet == null || fleet.length == 0) {
            return false;
        }

        return placementService.placeFleetAutomatically(board, fleet);
    }
}
