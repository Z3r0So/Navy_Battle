package Services;

public class AttackResult {
    private final String message;
    private final boolean wasHit;
    private final int row;
    private final int column;

    /**Constructor for attack result
     * @param message Descriptive message of the result
     * @param wasHit Whether the attack hit a ship
     * @param row Row coordinate of the attack
     * @param column Column coordinate of the attack
     */
    public AttackResult(String message, boolean wasHit, int row, int column) {
        this.message = message;
        this.wasHit = wasHit;
        this.row = row;
        this.column = column;
    }

    /**
     * Simplified constructor when coordinates are not needed
     */
    public AttackResult(String message, boolean wasHit) {
        this(message, wasHit, -1, -1);
    }

    public String getMessage() {
        return message;
    }

    public boolean wasHit() {
        return wasHit;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public boolean isSunk() {
        return message.contains("Sunk");
    }

    @Override
    public String toString() {
        return String.format("AttackResult[message='%s', hit=%b, position=(%d,%d)]",
                message, wasHit, row, column);
    }
}
