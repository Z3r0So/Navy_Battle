package Model.Player;
import Attacks.Attack;
import Model.Board.Board;
public interface IPlayer {
    String getUsername();
    int getWins();
    void addWins();
    Board getOwnBoard();
    Board getAttackBoard();
    void resetBoards();
    Attack makeAttack(Board enemyBoard);
}
