package Model.Match;

import Model.Board.Board;
import Model.Player.Player;

public class Match {
    private Player player;
    private Player machine;
    private boolean playerTurn;

    public Match(Player player, Player machine) {
        this.player = player;
        this.machine = machine;
        this.playerTurn = true;
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
}

