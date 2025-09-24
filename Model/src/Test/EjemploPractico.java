package Test;

import Model.Board.Board;
import Model.Boat.Boat;
import Model.Boat.Destructor;

public class EjemploPractico {
        public static void main(String[] args) {
            // 1. Crear tablero y barco
            Board board = new Board(10, 10);
            Boat destructor = new Destructor(); // Longitud 3

            System.out.println("Destructor creado:");
            System.out.println("- Tipo: " + destructor.getType());
            System.out.println("- Longitud: " + destructor.getLength());
            System.out.println("- Vida: " + destructor.getLife());
            System.out.println("- Posiciones: " + destructor.getPositions().size());

            // 2. Colocar barco en el tablero
            boolean colocado = board.placeShip(destructor, 2, 3, true); // Fila 2, Col 3, Horizontal

            if (colocado) {
                System.out.println("\n✅ Barco colocado exitosamente!");

                // Ver dónde quedó
                System.out.println("Posiciones ocupadas:");
                for (int[] pos : destructor.getPositions()) {
                    System.out.println("- [" + pos[0] + "," + pos[1] + "]");
                }

                // Ver tablero visual
                System.out.println("\nTablero visual:");
                printBoard(board);

            } else {
                System.out.println("❌ No se pudo colocar el barco");
            }

            // 3. Simular ataques
            System.out.println("\n🎯 Simulando ataques:");

            // Miss
            String result1 = board.shootEnemyBoat(1, 1);
            System.out.println("Ataque (1,1): " + result1);

            // Hit
            String result2 = board.shootEnemyBoat(2, 3);
            System.out.println("Ataque (2,3): " + result2);
            System.out.println("Vida del destructor: " + destructor.getLife());

            // Otro hit
            String result3 = board.shootEnemyBoat(2, 4);
            System.out.println("Ataque (2,4): " + result3);
            System.out.println("Vida del destructor: " + destructor.getLife());

            // Último hit - hundimiento
            String result4 = board.shootEnemyBoat(2, 5);
            System.out.println("Ataque (2,5): " + result4);
            System.out.println("¿Destructor hundido? " + destructor.isSunk());

            // Ver tablero final
            System.out.println("\nTablero después de ataques:");
            printBoard(board);
        }

        // Utilidad para mostrar el tablero
        private static void printBoard(Board board) {
            int[][] state = board.getBoardState();
            System.out.print("  ");
            for (int j = 0; j < state[0].length; j++) {
                System.out.print(j + " ");
            }
            System.out.println();

            for (int i = 0; i < state.length; i++) {
                System.out.print(i + " ");
                for (int j = 0; j < state[i].length; j++) {
                    char symbol;
                    switch (state[i][j]) {
                        case 0: symbol = '.'; break; // Vacío
                        case 1: symbol = 'B'; break; // Barco
                        case 2: symbol = 'O'; break; // Miss
                        case 3: symbol = 'X'; break; // Hit
                        default: symbol = '?'; break;
                    }
                    System.out.print(symbol + " ");
                }
                System.out.println();
            }
        }
    }
