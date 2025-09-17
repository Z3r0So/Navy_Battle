package Model.Boat;

/*This class is going to be in charge of all the boat changes (sunkings, impats, type ) -07/09/2025
 * */
public class Boat {
    private String type;
    private int length;
    private int life;
    private boolean isSunk;
//Creation of standard constructor for the creation of a Model.Boat.Boat
    public Boat(String type, int length) {
        this.type = type;
        this.length = length;
        this.life = length;
        this.isSunk = false;
    }
    //Creation of method for the damage of the boat
    public void impacted() {
        if(!isSunk) {
            life--;
            if (life == 0) {
                isSunk = true;
            }
        }
    }
    // Method for verification of Sunking
    public boolean isSunked() {
        return isSunk;
    }
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
}




