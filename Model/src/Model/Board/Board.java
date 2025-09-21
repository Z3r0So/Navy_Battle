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
                boatGrid = null;
            }
            boatList.clear();
        }
    }

    //Logic for the placement of the ship
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

    //Method for the shooting of the enemy boat
    public String shootEnemyBoat(int row, int column) {
        if (board[row][column] == 1) {
            board[row][column] = 3; // Mark as hit (3)
            Boat hitBoat = boatGrid[row][column];
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
     * It will return true if the shoot is valid, false otherwise
     * */
    public boolean validShoot(int row, int column) {
        return (row >= 0 && row < rows &&
                column >= 0 && column < columns && (board[row][column] != 2 && board[row][column] != 3));

    }
    public boolean allBoatsSunk() {
        for (Boat boat : boatList) {
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
}
