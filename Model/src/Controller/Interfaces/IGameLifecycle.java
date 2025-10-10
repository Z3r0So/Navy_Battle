package Controller.Interfaces;

import Model.Match.Match;
import Model.Player.Player;

public interface IGameLifecycle {
    boolean startNewGame(String playerName, String password);
    void resetGame();
    boolean isGameFinished();
    Player getWinner();
    Match getCurrentMatch();
}
