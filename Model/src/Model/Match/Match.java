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
    public String executeAttack(Attack attack, Player attacker, Player target) {
        Board targetBoard;
        if (target == player) {
            targetBoard = player.getOwnBoard();
        } else {
            targetBoard = machine.getOwnBoard();
        }

        // Aplicar el ataque al tablero
        String result = attack.apply(targetBoard);

        // Verificar si el juego terminó después del ataque
        checkGameEnd();

        // Si el ataque no fue un impacto ni hundió un barco, cambiar el turno
        boolean isHit = result.contains("Hit");
        boolean isSunk = result.contains("Sunk");
        if (!isHit && !isSunk) {
            switchTurn();
        }

        return result;
    }
    //
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

