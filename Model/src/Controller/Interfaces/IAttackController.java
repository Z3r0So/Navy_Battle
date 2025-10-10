package Controller.Interfaces;

public interface IAttackController {
    String playerAttack(int row, int column);
    String playerCrossBombAttack(int row, int column);
    String playerTorpedoAttack(int row, int column, boolean isHorizontal);
    String playerNukeAttack(int row, int column);
    String machineAttack();
}