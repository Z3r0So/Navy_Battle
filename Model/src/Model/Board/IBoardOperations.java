package Model.Board;
import Model.Boat.Boat;
import java.util.List;
public interface IBoardOperations {
    void initialize();
    boolean placeShip(Boat boat, int row, int column, boolean horizontal);
    boolean removeShip(Boat boat);
    String shootEnemyBoat(int row, int column);
    boolean validShoot(int row, int column);
    boolean allBoatsSunk();
    int getRemainingBoats();
    int getRows();
    int getColumns();
    Boat getBoatAt(int row, int column);
    int[][] getBoardState();
    List<Boat> getBoatList();
}
