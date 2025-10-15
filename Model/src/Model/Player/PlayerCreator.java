package Model.Player;

import Model.Player.Interfaces.IPlayerCreator;

public class PlayerCreator implements IPlayerCreator{
    @Override
    public Player createHumanPlayer(String name, String password) {
        return new HumanPlayer(name, password);
    }
    @Override
    public Player createMachinePlayer() {
        return new Machine();
    }

}
