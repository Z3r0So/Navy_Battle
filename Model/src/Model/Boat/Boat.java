package Model.Boat;

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




