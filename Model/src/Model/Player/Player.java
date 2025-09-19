package Model.Player;

import Model.Board.Board;

public abstract class Player {
    private String Username;
    private int wins;
    private String password;

    protected Board ownBoard;
    protected Board attackBoard;

    public Player(String username, String password) {
        this.Username = username;
        this.password = password;
        this.wins = 0;
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

    public void addWins(int wins) {
        wins++;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
