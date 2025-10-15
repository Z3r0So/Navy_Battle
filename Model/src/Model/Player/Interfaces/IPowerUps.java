package Model.Player.Interfaces;

public interface IPowerUps {
    boolean hasCrossBombs();
    boolean hasNukes();
    boolean hasTorpedoes();
    boolean useCrossBomb();
    boolean useNuke();
    boolean useTorpedo();
    int getCrossBombs();
    int getNukes();
    int getTorpedoes();
    void reset();
}
