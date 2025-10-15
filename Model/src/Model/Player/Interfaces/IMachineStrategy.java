package Model.Player.Interfaces;

import Model.Board.Board;

import java.util.List;

public interface IMachineStrategy {
    /**
     * Chooses attack coordinates based on current game state
     *
     * @param enemyBoard The enemy's board
     * @param isTargetMode Whether AI is in target mode (focusing on a ship)
     * @param targetStack Stack of priority targets to attack
     * @param hitHistory History of successful hits
     * @return Array with [row, column] coordinates to attack
     */
    int[] chooseAttackCoordinates(
            Board enemyBoard,
            boolean isTargetMode,
            List<int[]> targetStack,
            List<int[]> hitHistory
    );
}
