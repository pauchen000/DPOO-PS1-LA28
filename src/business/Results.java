package business;

import java.util.HashMap;
import java.util.Map;

/**
 * el podio de una competicion: el id del atleta que gana cada medalla
 */
public class Results {

    private String gold;
    private String silver;
    private String bronze;

    /**
     * crea el podio
     * @param gold id del atleta con el oro
     * @param silver id del atleta con la plata
     * @param bronze id del atleta con el bronce
     */
    public Results(String gold, String silver, String bronze) {
        this.gold = gold;
        this.silver = silver;
        this.bronze = bronze;
    }

    /**
     * devuelve quien tiene el oro
     * @return el id del atleta
     */
    public String getGold() {
        return gold;
    }

    /**
     * devuelve quien tiene la plata
     * @return el id del atleta
     */
    public String getSilver() {
        return silver;
    }

    /**
     * devuelve quien tiene el bronce
     * @return el id del atleta
     */
    public String getBronze() {
        return bronze;
    }

    /**
     * devuelve el podio como medalla -> id del atleta que la gana
     * @return el podio
     */
    public Map<Medal, String> toPodio() {
        Map<Medal, String> podium = new HashMap<>();
        podium.put(Medal.GOLD, gold);
        podium.put(Medal.SILVER, silver);
        podium.put(Medal.BRONZE, bronze);
        return podium;
    }
}
