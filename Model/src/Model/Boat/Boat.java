package Model.Boat;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/*This class is going to be in charge of all the boat changes (sunkings, impats, type ) -07/09/2025
 * */
public class Boat {
    private String type;
    private int length;
    private int life;
    private boolean isSunk;
    private List<int[]> positions;
//Creation of standard constructor for the creation of a Boat
    public Boat(String type, int length) {
        this.type = type;
        this.length = length;
        this.life = length;
        this.isSunk = false;
        this.positions = new ArrayList<>();
    }
    /*Creation of method for the position of the boat in the board
    *@int startRow is the row where the boat is going to start
    * @int startCol is the column where the boat is going to start
    * @boolean horizontal is true if the boat is going to be placed horizontally, false if
    * it is going to be placed vertically
    * @return void
    * */
    public void setPositions(int startRow, int startCol, boolean horizontal) {
        positions.clear(); // Cleaning of previous positions

        // Get all the positions that a boat is going to occupy
        for (int i = 0; i < length; i++) {
            if (horizontal) {
                positions.add(new int[]{startRow, startCol + i});
            } else {
                positions.add(new int[]{startRow + i, startCol});
            }
        }
    }
    /*Method to check if the boat occupies a specific position
    *@int row is the row where we want to check if the boat is occupying
    * @int col is the column where we want to check if the boat is occupying
    * @return boolean true if the boat occupies the position, false otherwise
    * */
    public boolean occupiesPosition(int row, int col) {
        for (int[] pos : positions) {
            if (pos[0] == row && pos[1] == col) {
                return true;
            }
        }
        return false;
    }
    /*Creation of method for the damage to the boat
    * */
    public void impacted() {
        if(!isSunk && life > 0) {
            life--;
            if (life == 0) {
                isSunk = true;
            }
        }
    }
    /**Method to get the color associated with the boat type
     * @return Color object representing the boat's color
    * */
    public Color getColor() {
        switch (type) {
            case "Portaaviones": // Aircrafter
                return new Color(128, 128, 128); // Gray
            case "Cruise":
                return new Color(0, 100, 150); // Dark Blue
            case "Destructor":
                return new Color(200, 100, 0); // Orange
            case "Submarine":
                return new Color(0, 150, 0); // Green
            default:
                return new Color(100, 126, 230); // Default blue
        }
    }
    // Method for verification of Sunking
    public boolean isSunk() {
        return isSunk;
    }

    public void setSunk(boolean sunk) {
        isSunk = sunk;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getLife() {
        return life;
    }

    public void setLife(int life) {
        this.life = life;
    }
    // Obtaining all the positions of the boat
    public List<int[]> getPositions() {
        return new ArrayList<>(positions);
    }
}




