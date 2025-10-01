package Model.Player;

public class PowerUpsManager {
        private int crossBombs;
        private int nukes;
        private int torpedoes;


        public PowerUpsManager() {
            this.crossBombs = 2;
            this.nukes = 1;
            this.torpedoes = 2;
        }

        /**Constructor with custom ammunition counts
         */
        public PowerUpsManager(int crossBombs, int nukes, int torpedoes) {
            this.crossBombs = crossBombs;
            this.nukes = nukes;
            this.torpedoes = torpedoes;
        }

        public boolean hasCrossBombs() {
            return crossBombs > 0;
        }

        public boolean hasNukes() {
            return nukes > 0;
        }

        public boolean hasTorpedoes() {
            return torpedoes > 0;
        }

        public boolean useCrossBomb() {
            if (crossBombs > 0) {
                crossBombs--;
                return true;
            }
            return false;
        }

        public boolean useNuke() {
            if (nukes > 0) {
                nukes--;
                return true;
            }
            return false;
        }

        public boolean useTorpedo() {
            if (torpedoes > 0) {
                torpedoes--;
                return true;
            }
            return false;
        }

        public int getCrossBombs() {
            return crossBombs;
        }

        public int getNukes() {
            return nukes;
        }

        public int getTorpedoes() {
            return torpedoes;
        }

        public void reset() {
            this.crossBombs = 2;
            this.nukes = 1;
            this.torpedoes = 2;
        }

        @Override
        public String toString() {
            return String.format("PowerUps: Cruces=%d, Nukes=%d, Torpedos=%d",
                    crossBombs, nukes, torpedoes);
        }
    }
