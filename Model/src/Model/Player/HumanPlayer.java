package Model.Player;

import Attacks.Attack;
import Attacks.BasicAttack;
import Model.Board.Board;

public class HumanPlayer extends Player{
    private int nextAttackRow = -1;
    private int nextAttackColumn = -1;
    public HumanPlayer(String username, String password) {
        super(username, password);
    }

    @Override
    public Attack makeattack(Board enemyBoard) {
        return null;
    }
}

