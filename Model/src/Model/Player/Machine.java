package Model.Player;

import Attacks.Attack;
import Model.Board.Board;

import java.util.Random;

public class Machine extends Player {
    private Random random;
    public Machine() {
        super("Machine", "none");
        this.random = new Random();
    }

    @Override
    public Attack makeattack(Board enemyBoard) {
        return null;
    }
}
