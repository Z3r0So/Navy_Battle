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
    /*Method to make an attack on the enemy board
     * @param enemyBoard the board of the enemy player
     * @return the attack made
     * @throws IllegalStateException if no attack coordinates are set
     * @throws IllegalArgumentException if the attack coordinates are out of bounds or already attacked
    * */
    @Override
    public Attack makeAttack(Board enemyBoard) {
        // Validate the next attack coordinates
        if (nextAttackRow == -1 || nextAttackColumn == -1) {
            throw new IllegalStateException(
                    "No attack coordinates set! Call setNextAttack() first."
            );
        }

        // Verify if the coordinates are within bounds
        if (nextAttackRow < 0 || nextAttackRow >= enemyBoard.getRows() ||
                nextAttackColumn < 0 || nextAttackColumn >= enemyBoard.getColumns()) {
            // Limpiar coordenadas inválidas
            int invalidRow = nextAttackRow;
            int invalidCol = nextAttackColumn;
            nextAttackRow = -1;
            nextAttackColumn = -1;

            throw new IllegalArgumentException(
                    "Attack coordinates (" + invalidRow + "," + invalidCol + ") out of bounds! " +
                            "Valid range: 0-" + (enemyBoard.getRows()-1) + ", 0-" + (enemyBoard.getColumns()-1)
            );
        }

        // Verify if the position has already been attacked
        if (!enemyBoard.validShoot(nextAttackRow, nextAttackColumn)) {
            // Limpiar coordenadas inválidas
            int invalidRow = nextAttackRow;
            int invalidCol = nextAttackColumn;
            nextAttackRow = -1;
            nextAttackColumn = -1;

            throw new IllegalArgumentException(
                    "Position (" + invalidRow + "," + invalidCol + ") has already been attacked!"
            );
        }
        Attack attack = new BasicAttack(nextAttackRow, nextAttackColumn);
        // Clear the next attack coordinates after use
        nextAttackRow = -1;
        nextAttackColumn = -1;
        return attack;
    }
    /*Method to clear the next attack coordinates
    * */
    public void clearNextAttack() {
        nextAttackRow = -1;
        nextAttackColumn = -1;
    }
    /*Method to set the next attack coordinates for the human player
     * @param row the row of the next attack
     * @param column the column of the next attack
    * */
    public void setNextAttack(int row, int column) {
        this.nextAttackRow = row;
        this.nextAttackColumn = column;
    }


}

