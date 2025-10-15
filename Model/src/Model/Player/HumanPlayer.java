package Model.Player;

import Attacks.Attack;
import Attacks.BasicAttack;
import Model.Board.Board;
import Model.Player.Interfaces.IAttackCoordinatesValidator;

public class HumanPlayer extends Player{
    private int nextAttackRow = -1;
    private int nextAttackColumn = -1;
    private PowerUpsManager powerUps;
    private IAttackCoordinatesValidator coordinatesValidator;
    /**Method to create a new human player
     * @param username the username of the player
     * @param password the password of the player
     * @return a new instance of HumanPlayer
    * */
    public HumanPlayer(String username, String password, IAttackCoordinatesValidator validator) {
        super(username, password);
        this.powerUps = new PowerUpsManager();
        this.coordinatesValidator = validator;
    }
    /**Overloaded constructor for backward compatibility
     *Uses default validator implementation
     */
    public HumanPlayer(String username, String password) {
        this(username, password, new AttackCoordinatesValidator());
    }
    /**Method to make an attack on the enemy board
     * @param enemyBoard the board of the enemy player
     * @return the attack made
     * @throws IllegalStateException if no attack coordinates are set
     * @throws IllegalArgumentException if the attack coordinates are out of bounds or already attacked
    * */
    @Override
    public Attack makeAttack(Board enemyBoard) {
        //Delegate validation to injected validator (DIP)
        coordinatesValidator.validateAttackCoordinates(
                nextAttackRow,
                nextAttackColumn,
                enemyBoard
        );

        Attack attack = new BasicAttack(nextAttackRow, nextAttackColumn);

        clearNextAttack();

        return attack;
    }
    /**Method to clear the next attack coordinates
    * */
    public void clearNextAttack() {
        nextAttackRow = -1;
        nextAttackColumn = -1;
    }
    /**Method to set the next attack coordinates for the human player
     * @param row the row of the next attack
     * @param column the column of the next attack
    * */
    public void setNextAttack(int row, int column) {
        this.nextAttackRow = row;
        this.nextAttackColumn = column;
    }
    /**Gets the power-ups manager for this player (or the instance of the player)
     * @return The PowerUpsManager instance
     */
    public PowerUpsManager getPowerUps() {
        return powerUps;
    }


}

