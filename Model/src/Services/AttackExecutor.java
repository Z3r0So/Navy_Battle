package Services;

import Attacks.Attack;
import Model.Match.Match;
import Model.Player.Machine;
import Model.Player.Player;
import Services.Interfaces.IAttackExecutor;
import Services.Interfaces.IAttackService;

public class AttackExecutor implements IAttackExecutor {
    private final IAttackService attackService;

    /**Constructor with dependency injection
     *
     * @param attackService Service for marking attacks on boards
     */
    public AttackExecutor(IAttackService attackService) {
        this.attackService = attackService;
    }

    /**Executes a player's attack on the enemy
     * Process:
     * 1. Apply attack to target board
     * 2. Mark result on attacker's attack board
     * 3. Return result with hit information
     *
     * @param match Current match context
     * @param attack The attack to execute
     * @param attacker The attacking player
     * @param target The target player
     * @return Attack result with outcome
     */
    @Override
    public AttackResult executeAttack(Match match, Attack attack, Player attacker, Player target) {
        // Execute attack through match (handles turn logic)
        String result = match.executeAttack(attack, attacker, target);

        // Determine if it was a hit
        boolean wasHit = result.contains("Hit") || result.contains("Sunk");

        // Mark attack on attacker's tracking board
        attackService.markAttackOnBoard(
                attacker.getAttackBoard(),
                attack.getRow(),
                attack.getColumn(),
                wasHit
        );

        return new AttackResult(result, wasHit, attack.getRow(), attack.getColumn());
    }

    /**Executes the machine's attack automatically
     * Process:
     * 1. Machine chooses target
     * 2. Execute attack
     * 3. Notify machine of result (for AI learning)
     * 4. Return result
     *
     * @param match Current match
     * @return Attack result
     */
    @Override
    public AttackResult executeMachineAttack(Match match) {
        Machine machine = (Machine) match.getMachine();
        Player player = match.getPlayer();

        // Machine chooses its attack
        Attack attack = machine.makeAttack(player.getOwnBoard());

        // Execute attack
        String result = match.executeAttack(attack, machine, player);

        // Notify machine for AI learning
        machine.notifyAttackResult(
                attack.getRow(),
                attack.getColumn(),
                result,
                player.getOwnBoard()
        );

        boolean wasHit = result.contains("Hit") || result.contains("Sunk");

        return new AttackResult(result, wasHit, attack.getRow(), attack.getColumn());
    }

}
