package Model.Board;

import Model.Boat.Boat;

public class Board {
    private static int rows;
    private static int columns;
    private int[][] board; // 0=vacio, 1=barco, 2=agua, 3=impacto
    private Boat[] ships;
    //Creation of constructor for the board
    public Board(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        this.board = new int[rows][columns];
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
        if (horizontal && row + boat.getLength() < rows) {
            return false;
        }
        //The second verifying is related to the vertical placement
        if (horizontal && column + boat.getLength() < columns) {
            return false;
        }
        // Placement of a ship
        for (int i = 0; i < boat.getLength(); i++) {
            //Fill of the board with the standard values (1 mean the space is occupied)
            board[row][column] = 1;
        }
        return true;
        // here we are missing the overlapping method
    }
    public String shootEnemy(int row, int columns){
        return "I'm currently fixing this!";
    }

}
