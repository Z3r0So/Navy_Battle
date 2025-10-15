package Model.Match;

import Model.Board.Board;

public interface IGameEndValidator {
        /**Checks if the game has ended and determines the winner
         *
         * @param playerBoard The player's board
         * @param machineBoard The machine's board
         * @return GameEndResult containing end status and winner
         */
        GameEndResult checkGameEnd(Board playerBoard, Board machineBoard);

        /**Checks if a specific player has lost
         * @param board The player's board
         * @return true if all ships are sunk, false otherwise
         */
        boolean hasLost(Board board);
}
