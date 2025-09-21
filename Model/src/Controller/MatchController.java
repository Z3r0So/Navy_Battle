package Controller;

import Model.Boat.*;
import Model.Match.Match;
import Model.Player.HumanPlayer;
import Model.Player.Machine;
import Model.Player.Player;

import java.util.Random;

public class MatchController {
        private Match currentMatch;
        private Random random;

        public MatchController() {
            this.random = new Random();
        }
        public boolean startNewMatch(String playerName, String password) {
            try {
                Player humanPlayer = new HumanPlayer(playerName, password);
                Player machinePlayer = new Machine();

                currentMatch = new Match(humanPlayer, machinePlayer);
            } catch (Exception e) {
                System.out.println("Error starting new match: " + e.getMessage());
                return false;
            }
        }
        public void setPlayerShips(Player player) {
            Boat[] boats = {
                    new Aircrafter(),     // 6 espacios
                    new Cruise(),         // 4 espacios
                    new Cruise(),         // 4 espacios
                    new Destructor(),     // 3 espacios
                    new Destructor(),     // 3 espacios
                    new Submarine(),      // 2 espacios
                    new Submarine(),      // 2 espacios
                    new Submarine()       // 2 espacios
            };
            for (Boat boat : boats) {
                boolean placed = false;
                while (!placed) {
                    int x = random.nextInt(10);
                    int y = random.nextInt(10);
                    boolean horizontal = random.nextBoolean();
                    placed = player.getOwnBoard().placeShip(boat, x, y, horizontal);
                }
            }
        }
}
