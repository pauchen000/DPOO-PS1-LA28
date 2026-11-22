package business;

/**
 * una delegacion de un pais: su codigo, su nombre y las medallas que lleva
 */
public class Delegation {

    private String code;
    private String name;
    private int goldMedals;
    private int silverMedals;
    private int bronzeMedals;

    /**
     * crea una delegacion con los datos que le pasamos
     * @param code codigo de la delegacion (DLG-XXX)
     * @param name nombre completo del pais
     * @param goldMedals medallas de oro
     * @param silverMedals medallas de plata
     * @param bronzeMedals medallas de bronce
     */
    public Delegation(String code, String name, int goldMedals, int silverMedals, int bronzeMedals) {
        this.code = code;
        this.name = name;
        this.goldMedals = goldMedals;
        this.silverMedals = silverMedals;
        this.bronzeMedals = bronzeMedals;
    }

    /**
     * devuelve el codigo de la delegacion, con formato DLG-XXX
     * @return el codigo
     */
    public String getCode() {
        return code;
    }

    /**
     * saca el nombre completo del pais
     * @return el nombre
     */
    public String getName() {
        return name;
    }

    /**
     * cuantas medallas de oro lleva
     * @return medallas de oro
     */
    public int getGoldMedals() {
        return goldMedals;
    }

    /**
     * cuantas medallas de plata lleva
     * @return medallas de plata
     */
    public int getSilverMedals() {
        return silverMedals;
    }

    /**
     * cuantas medallas de bronce lleva
     * @return medallas de bronce
     */
    public int getBronzeMedals() {
        return bronzeMedals;
    }

    /**
     * suma uno al contador del tipo de medalla que le pasen
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
     * resta uno al contador del tipo de medalla que le pasen. ojo: aqui no se mira si quedan,
     * eso lo comprueba antes restarMedallas del manager
     * @param medal tipo de medalla a restar
     */
    public void removeMedal(Medal medal) {
        switch (medal) {
            case GOLD -> {
                    goldMedals--;

            }
            case SILVER -> {
                    silverMedals--;

            }
            case BRONZE -> {
                    bronzeMedals--;

            }
        }
    }

    /**
     * deja las tres medallas a cero
     */
    public void resetMedals() {
        goldMedals = 0;
        silverMedals = 0;
        bronzeMedals = 0;
    }
}
