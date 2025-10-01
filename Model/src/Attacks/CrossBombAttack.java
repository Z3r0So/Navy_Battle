package Attacks;

import Model.Board.Board;

public class CrossBombAttack extends Attack {

    public CrossBombAttack(int row, int column) {
        super(row, column);
    }

    @Override
    public String apply(Board targetBoard) {
        int hits = 0;
        int misses = 0;
        StringBuilder result = new StringBuilder("Cross attack on (" + row + "," + column + "): ");

        // Coordinates for the cross pattern, starting from the center
        int[][] crossPattern = {
                {row, column}, //Center
                {row - 1, column},//Vertical displacement up
                {row + 1, column},//Vertical displacement down
                {row, column - 1},//Horizontal displacement left
                {row, column + 1} //Horizontal displacement right
        };
        //Realize the attack in the cross pattern
        for (int[] pos : crossPattern) {
            int r = pos[0]; //r is going to be defined as the first element (row)
            // of the crossPattern list
            int c = pos[1]; //c is going to be defined as the second element (column) of the crossPattern list

            // Verify if the coordinates are within the board limits
            if (r >= 0 && r < targetBoard.getRows() &&
                    c >= 0 && c < targetBoard.getColumns()) {

                String cellResult = targetBoard.shootEnemyBoat(r, c);

                if (cellResult.contains("Hit") || cellResult.contains("Sunk")) {
                    hits++;
                } else if (cellResult.contains("Miss")) {
                    misses++;
                }
            }
        }

        result.append(hits).append(" impacts, ").append(misses).append(" misses.");
        return result.toString();
    }

    @Override
    public String toString() {
        return "CrossBombAttack at (" + row + "," + column + ")";
    }
}