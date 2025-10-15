package Controller.Interfaces;

public interface IAttackController {
    /**Executes a basic single-cell attack
     * @param row Row to attack
     * @param column Column to attack
     * @return Result message of the attack
     */
    String playerAttack(int row, int column);

    /**Executes a cross bomb attack (5 cells in cross pattern)
     * @param row Center row of the cross
     * @param column Center column of the cross
     * @return Result message of the attack
     */
    String playerCrossBombAttack(int row, int column);

    /**Executes a torpedo attack (entire row or column)
     * @param row Row coordinate
     * @param column Column coordinate
     * @param isHorizontal true for row attack, false for column attack
     * @return Result message of the attack
     */
    String playerTorpedoAttack(int row, int column, boolean isHorizontal);

    /**Executes a nuke attack (3x3 area)
     * @param row Center row of the nuke
     * @param column Center column of the nuke
     * @return Result message of the attack
     */
    String playerNukeAttack(int row, int column);

    /**Executes the machine's attack automatically
     * @return Result message of the attack
     */
    String machineAttack();
}