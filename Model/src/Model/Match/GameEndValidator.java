package Model.Match;

import Model.Board.Board;

public class GameEndValidator implements IGameEndValidator {
    @Override
    public GameEndResult checkGameEnd(Board playerBoard, Board machineBoard) {
        //Check if player lost (all ships sunk)
        if (hasLost(playerBoard)) {
            return GameEndResult.machineWon();
        }

        //Check if machine lost (all ships sunk)
        if (hasLost(machineBoard)) {
            return GameEndResult.playerWon();
        }

        //Game continues
        return GameEndResult.gameOngoing();
    }

    /**Checks if a player has lost by verifying if all ships are sunk
     *
     * @param board The board to check
     * @return true if all ships are sunk, false otherwise
     */
    @Override
    public boolean hasLost(Board board) {
        return board.allBoatsSunk();
    }
}
