package Model.Board;

import Model.Boat.Boat;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Board {
    private final int rows;
    private final int columns;
    private final int[][] board; // 0=vacio, 1=barco, 2=agua, 3=impacto
    private Boat[][] boatGrid; //Tracking of boat position
    private List<Boat> boatList; // All the boats that are going to be used in the game

    //Creation of constructor for the board
    public Board(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        this.board = new int[rows][columns];
        this.boatList = new ArrayList<>();
        this.boatGrid = new Boat[rows][columns];
        initialize();
    }

    //Initialize the board by filling it with 0 (0 means that it is empty)
    public void initialize() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                board[i][j] = 0;
                boatGrid[i][j] = null;
            }
        }
        boatList.clear();
    }

    /*Logic for the placement of the ship
    @Boat boat is the boat that we want to place
    @int row is the row where we want to place the boat
    @int column is the column where we want to place the boat
    @boolean horizontal is true if we want to place the boat horizontally, false if we want
    to place it vertically
    This method will return true if the boat was placed successfully, false otherwise
     */
    public boolean placeShip(Boat boat, int row, int column, boolean horizontal) {
        //First we are going to verify if the selected boat is able to place horizontal
        if (horizontal && column + boat.getLength() > columns) {
            return false;
        }
        //The second verifying is related to the vertical placement
        if (!horizontal && row + boat.getLength() > rows) {
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
                boatGrid[row][column + i] = boat;
            } else {
                board[row + i][column] = 1;
                boatGrid[row + i][column] = boat;
            }
        }
        boatList.add(boat);
        return true;
    }

    /*Method for the shooting of the enemy boat
    * @row and @column are the coordinates of the shoot, not the size of the board
    * This method will return a String with the result of the shoot
    * */
    public String shootEnemyBoat(int row, int column) {
        if (board[row][column] == 1) {
            board[row][column] = 3; // Mark as hit (3)
            Boat hitBoat = boatGrid[row][column]; //Object of type boat is assigned to the position of the boat that was hit in the board
            hitBoat.impacted();
            if (hitBoat.isSunk()) {
                return "Sunk";
            }
            return "Hit!";
        } else if (board[row][column] == 0) {
            board[row][column] = 2;
            return "Miss!";
        } else {
            return "Already Shot";
        }
    }

    /*
     * @row and @column are the coordinates of the shoot, not the size of the board
     * This method will verify if the shoot is valid or not
     * @return It will return true if the shoot is valid, false otherwise
     * */
    public boolean validShoot(int row, int column) {
        // We verify if the coordinates are within the board limits
        if (row < 0 || row >= rows || column < 0 || column >= columns) {
            return false;
        }
        // Only valid if the boat is not already hit
        return board[row][column] == 0 || board[row][column] == 1;
    }
    /*Method for the verification of the sinking of all the boats
    @return This method will return true if all the boats are sunk, false otherwise
    * */
    public boolean allBoatsSunk() {
        for (Boat boat : boatList) { // Iterate through all boats
            if (!boat.isSunk()) {
                return false;
            }
        }
        return true;
    }

    public int getRows() {
        return rows;
    }
    public int getColumns() {
        return columns;
    }
    public int[][] getBoard() {
        return board;
    }
    public List<Boat> getBoatList() {
        return boatList;
    }
    public Boat getBoatAt(int row, int column) { return boatGrid[row][column]; }
    public int[][] getBoardState() { return board.clone(); } // Copia para seguridad
}
