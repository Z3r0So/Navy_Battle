package Model.Player.Interfaces;

import Model.Player.Player;

public interface IPlayerCreator {
    Player createHumanPlayer(String name, String password);
    Player createMachinePlayer();
}
