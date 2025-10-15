package Services;

import Attacks.Attack;
import Model.Match.Match;
import Model.Player.Player;

public interface IAttackExecutor {
        /** Executes a player's attack and updates boards
         *
         * @param match The current match
         * @param attack The attack to execute
         * @param attacker The attacking player
         * @param target The target player
         * @return Result of the attack with hit information
         */
        AttackResult executeAttack(Match match, Attack attack, Player attacker, Player target);

        /**
         * Executes the machine's turn automatically
         *
         * @param match The current match
         * @return Result of the machine's attack
         */
        AttackResult executeMachineAttack(Match match);
}
