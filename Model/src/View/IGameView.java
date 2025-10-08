package View;

public interface IGameView {
    void updateBoards();
    void updateGameInfo();
    void updatePowerUpsDisplay();
    void showMessage(String message);
    void showVictory(String winner);
    void enableAttackButtons(boolean enable);
    void resetView();
}
