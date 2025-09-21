package Model.Player;

import Attacks.Attack;
import Attacks.BasicAttack;
import Model.Board.Board;

public class HumanPlayer extends Player{
    private int nextAttackRow = -1;
    private int nextAttackColumn = -1;
    public HumanPlayer(String username, String password) {
        super(username, password);
    }

    @Override
    public Attack makeAttack(Board enemyBoard) {
        if (nextAttackRow == -1 || nextAttackColumn == -1) {
            throw new IllegalStateException("No attack coordinates set for human player!");
        }

        // Validar que las coordenadas estén en rango
        if (nextAttackRow < 0 || nextAttackRow >= enemyBoard.getRows() ||
                nextAttackColumn < 0 || nextAttackColumn >= enemyBoard.getColumns()) {
            throw new IllegalArgumentException("Attack coordinates out of bounds!");
        }

        // Validar que el ataque sea válido (no atacado previamente)
        if (!enemyBoard.validShoot(nextAttackRow, nextAttackColumn)) {
            throw new IllegalArgumentException("Invalid attack coordinates - already attacked or out of bounds!");
        }

        Attack attack = new BasicAttack(nextAttackRow, nextAttackColumn);

        // Limpiar las coordenadas después de crear el ataque
        nextAttackRow = -1;
        nextAttackColumn = -1;

        return attack;
    }

}
