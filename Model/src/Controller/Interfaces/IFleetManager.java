package Controller.Interfaces;

import Model.Board.Board;
import Model.Boat.Boat;

public interface IFleetManager {
    /**Creates a standard fleet of boats
     * @return Array of boats representing the standard fleet
     */
    Boat[] createStandardFleet();

    /**Deploys an entire fleet on a board automatically
     * @param board The board where ships will be placed
     * @param fleet The fleet of boats to deploy
     * @return true if all ships were placed successfully, false otherwise
     */
    boolean deployFleet(Board board, Boat[] fleet);

    /**Creates a custom fleet with specified boat counts
     *
     * @param aircrafters Number of aircraft carriers
     * @param cruises Number of cruises
     * @param destructors Number of destructors
     * @param submarines Number of submarines
     * @return Array of boats representing the custom fleet
     */
    Boat[] createCustomFleet(int aircrafters, int cruises, int destructors, int submarines);
}
