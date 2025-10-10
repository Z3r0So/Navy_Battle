package Model.Match;

import Attacks.Attack;
import Model.Board.Board;
import Model.Player.Player;

public class Match {
    private Player player;
    private Player machine;
    private boolean playerTurn;
    private boolean gameFinished;
    private Player winner;

    public Match(Player player, Player machine) {
        this.player = player;
        this.machine = machine;
        this.playerTurn = true;
        this.gameFinished = false;
        this.winner = null;
    }
    //Method for switching turns
    public void switchTurn() {
        playerTurn = !playerTurn;
    }
    //Methods for getting the current player and the opponent
    public Player getCurrentPlayer() {
        if (playerTurn) {
            return player;
        } else {
            return machine;
        }
    }
    public Player getOpponent() {
        if (playerTurn) {
            return machine;
        } else {
            return player;
        }
    }
    /**Method for executing an attack.
     * This method applies the given attack to the target player's board,
     * checks if the game has ended, and switches turns if necessary.
     * @param attack The attack to be executed.
     * @param attacker The player executing the attack.
     * @param target The player being attacked.
     * @return A string indicating the result of the attack (e.g., "Hit", "Miss", "Sunk").
    * */
    public String executeAttack(Attack attack, Player attacker, Player target) {
        Board targetBoard = target.getOwnBoard();

        if (target == player) {
            targetBoard = player.getOwnBoard();
        } else {
            targetBoard = machine.getOwnBoard();
        }

        //Apply the attack to the target's board
        String result = attack.apply(targetBoard);

        //Verify if the game has ended after the attack
        checkGameEnd();

        // If the attack was a miss, switch turns
        boolean isHit = result.contains("Hit");
        boolean isSunk = result.contains("Sunk");
        if (!isHit && !isSunk) {
            switchTurn();
        }

        return result;
    }
    /**Method to check if the game has ended.
     * This method checks if either player has all their boats sunk.
     * If so, it sets the game as finished and declares the winner.
    * */
    private void checkGameEnd() {
        if (player.getOwnBoard().allBoatsSunk()) {
            gameFinished = true;
            winner = machine;
            machine.addWins();
        } else if (machine.getOwnBoard().allBoatsSunk()) {
            gameFinished = true;
            winner = player;
            player.addWins();
        }
    }

    public Player getPlayer() {
        return player;
    }

    public Player getMachine() {
        return machine;
    }

    public boolean isPlayerTurn() {
        return playerTurn;
    }

    public void setPlayerTurn(boolean turn) {
        this.playerTurn = turn;
    }
    public boolean isGameFinished() {
        return gameFinished;
    }
    public Player getWinner() {
        return winner;
    }

}

