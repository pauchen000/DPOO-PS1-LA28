package business;

public class Delegation {

    private String code;
    private String name;
    private int goldMedals;
    private int silverMedals;
    private int bronzeMedals;

    public Delegation(String code, String name, int goldMedals, int silverMedals, int bronzeMedals) {
        this.code = code;
        this.name = name;
        this.goldMedals = goldMedals;
        this.silverMedals = silverMedals;
        this.bronzeMedals = bronzeMedals;
    }

    /**
     * Devuelve el código de la delegación (formato DLG-XXX).
     * @return el código de la delegación
     */
    public String getCode() {
        return code;
    }

    /**
     * Devuelve el nombre completo de la delegación.
     * @return el nombre de la delegación
     */
    public String getName() {
        return name;
    }

    /**
     * Devuelve el número de medallas de oro.
     * @return medallas de oro
     */
    public int getGoldMedals() {
        return goldMedals;
    }

    /**
     * Devuelve el número de medallas de plata.
     * @return medallas de plata
     */
    public int getSilverMedals() {
        return silverMedals;
    }

    /**
     * Devuelve el número de medallas de bronce.
     * @return medallas de bronce
     */
    public int getBronzeMedals() {
        return bronzeMedals;
    }

    /**
     * Suma una medalla del tipo indicado.
     * @param medal tipo de medalla a sumar
     */
    public void addMedal(Medal medal) {
        switch (medal) {
            case GOLD -> goldMedals++;
            case SILVER -> silverMedals++;
            case BRONZE -> bronzeMedals++;
        }
    }

    /**
     * Resta una medalla del tipo indicado. Si ya no quedan medallas de ese tipo, no hace nada.
     * @param medal tipo de medalla a restar
     */
    public void removeMedal(Medal medal) {
        switch (medal) {
            case GOLD -> {
                if (goldMedals > 0) {
                    goldMedals--;
                }
            }
            case SILVER -> {
                if (silverMedals > 0) {
                    silverMedals--;
                }
            }
            case BRONZE -> {
                if (bronzeMedals > 0) {
                    bronzeMedals--;
                }
            }
        }
    }

    /**
     * Pone a cero todas las medallas de la delegación.
     */
    public void resetMedals() {
        goldMedals = 0;
        silverMedals = 0;
        bronzeMedals = 0;
    }
}
