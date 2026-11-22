package business;

/**
 * los pesos de una competicion: cuanto cuenta cada atributo del atleta (suman 1)
 */
public class Weights {

    private double speed;
    private double endurance;
    private double strength;
    private double technique;
    private double precision;

    /**
     * crea los pesos de una competicion
     * @param speed peso de la velocidad
     * @param endurance peso de la resistencia
     * @param strength peso de la fuerza
     * @param technique peso de la tecnica
     * @param precision peso de la precision
     */
    public Weights(double speed, double endurance, double strength, double technique, double precision) {
        this.speed = speed;
        this.endurance = endurance;
        this.strength = strength;
        this.technique = technique;
        this.precision = precision;
    }

    /**
     * devuelve el peso de la velocidad
     * @return el peso
     */
    public double getSpeed() {
        return speed;
    }

    /**
     * devuelve el peso de la resistencia
     * @return el peso
     */
    public double getEndurance() {
        return endurance;
    }

    /**
     * devuelve el peso de la fuerza
     * @return el peso
     */
    public double getStrength() {
        return strength;
    }

    /**
     * devuelve el peso de la tecnica
     * @return el peso
     */
    public double getTechnique() {
        return technique;
    }

    /**
     * devuelve el peso de la precision
     * @return el peso
     */
    public double getPrecision() {
        return precision;
    }
}
