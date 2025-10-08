package Services;
import Attacks.Attack;
import Model.Board.Board;

public interface IAttackService {
    String executeAttack(Attack attack, Board targetBoard);
    void markAttackOnBoard(Board attackBoard, int row, int column, boolean wasHit);
    void markCrossAttackPattern(Board attackBoard, Board enemyBoard, int centerRow, int centerCol);
    void markTorpedoAttackPattern(Board attackBoard, Board enemyBoard, int row, int column, boolean isHorizontal);
    void markNukeAttackPattern(Board attackBoard, Board enemyBoard, int centerRow, int centerCol);
}
