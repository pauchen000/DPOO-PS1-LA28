package business;

/**
 * lo que ha hecho un atleta en una competicion simulada: su suerte y su tiempo
 */
public class Performance {

    private Athlete athlete;
    private double luck;
    private double time;

    /**
     * guarda la actuacion de un atleta
     * @param athlete el atleta
     * @param luck la suerte que le ha tocado (0 a 1)
     * @param time el tiempo que ha hecho
     */
    public Performance(Athlete athlete, double luck, double time) {
        this.athlete = athlete;
        this.luck = luck;
        this.time = time;
    }

    /**
     * devuelve el atleta
     * @return el atleta
     */
    public Athlete getAthlete() {
        return athlete;
    }

    /**
     * devuelve la suerte que le ha tocado
     * @return la suerte
     */
    public double getLuck() {
        return luck;
    }

    /**
     * devuelve el tiempo que ha hecho
     * @return el tiempo
     */
    public double getTime() {
        return time;
    }
}
