package Controller;

import Model.Boat.Boat;
import Model.Match.Match;
import Model.Player.Player;

public interface IGameController {
    boolean startNewGame(String playerName, String password);
    String playerAttack(int row, int column);
    String playerCrossBombAttack(int row, int column);
    String playerTorpedoAttack(int row, int column, boolean isHorizontal);
    String playerNukeAttack(int row, int column);
    String machineAttack();
    boolean placePlayerShip(Boat boat, int row, int column, boolean horizontal);
    void resetPlayerBoard();
    boolean isPlayerTurn();
    boolean isGameFinished();
    Player getWinner();
    Match getCurrentMatch();
    void resetGame();
    int[][] getPlayerOwnBoardState();
    int[][] getPlayerAttackBoardState();
}
