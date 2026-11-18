package business;

public class Weights {

    private double speed;
    private double endurance;
    private double strength;
    private double technique;
    private double precision;

    public Weights(double speed, double endurance, double strength, double technique, double precision) {
        this.speed = speed;
        this.endurance = endurance;
        this.strength = strength;
        this.technique = technique;
        this.precision = precision;
    }

    public double getSpeed() {
        return speed;
    }

    public double getEndurance() {
        return endurance;
    }

    public double getStrength() {
        return strength;
    }

    public double getTechnique() {
        return technique;
    }

    public double getPrecision() {
        return precision;
    }
}
