package Model.Player;

import Attacks.Attack;
import Model.Board.Board;

public abstract class Player {
    private String Username;
    private int wins;
    private String password;

    protected Board ownBoard;
    protected Board attackBoard;
    //Constructor for a player, creating instances of the boards
    public Player(String username, String password) {
        this.Username = username;
        this.password = password;
        this.wins = 0;
        this.ownBoard = new Board(10, 10);
        this.attackBoard = new Board(10, 10);
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public int getWins() {
        return wins;
    }
    //Specific method that add values to the wins attribute
    public void addWins() {
        wins++;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public Board getOwnBoard() {
        return ownBoard;
    }
    public Board getAttackBoard() {
        return attackBoard;
    }
    //Method that allows to reset the boards
    public void resetBoards() {
        ownBoard.initialize();
        attackBoard.initialize();
    }
    //Abstract method that will be implemented in the subclasses
    public abstract Attack makeattack(Board enemyBoard);
}
