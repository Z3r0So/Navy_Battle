package Model.Board;

import Model.Boat.Boat;

public class Board {
    private final int rows;
    private final int columns;
    private final int[][] board; // 0=vacio, 1=barco, 2=agua, 3=impacto

    //Creation of constructor for the board
    public Board(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        this.board = new int[rows][columns];
        initialize();
    }

    //Initialize the board by filling it with 0 (0 means that it is empty)
    public void initialize() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                board[i][j] = 0;
            }
        }
    }
    //Logic for the placement of the ship
    public boolean placeShip(Boat boat, int row, int column, boolean horizontal) {
        //First we are going to verify if the selected boat is able to place horizontal
        if (horizontal && row + boat.getLength() > row) {
            return false;
        }
        //The second verifying is related to the vertical placement
        if (!horizontal && column + boat.getLength() > column) {
            return false;
        }
        //Overlapping verify method
        for (int i = 0; i < boat.getLength(); i++) {
            if (horizontal) {
                if (board[row][column + i] == 1) {
                    return false;
                }
            } else {
                if (board[row + i][column] == 1) {
                    return false;
                }
            }
        }
        // Placement of a ship
        for (int i = 0; i < boat.getLength(); i++) {
            //Fill of the board with the standard values (1 mean the space is occupied)
            if (horizontal) {
                board[row][column + i] = 1;
            } else {
                board[row + i][column] = 1;
            }
        }
        return true;
    }

    //Method for the shooting of the enemy boat
    public String shootEnemyBoat(int row, int columns) {
        if (board[row][columns] == 1) {
            board[row][columns] = 3;
            return "Hit!";
        } else if (board[row][columns] == 0) {
            board[row][columns] = 2;
            return "Miss!";
        } else {
            return "Already Shot";
        }
    }
    /*
    * @row and @column are the coordinates of the shoot, not the size of the board
    * This method will verify if the shoot is valid or not
    * It will return true if the shoot is valid, false otherwise
    * */
    public boolean validShoot(int row, int column) {
        if (row > 10 || column > 10) {
            return false;
        }
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (board[i][j] == 0 || board[i][j] == 1) {
                    return false;
                }
            }
        }
        return true;
    }
}
