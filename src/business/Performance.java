package business;

public class Performance {

    private Athlete athlete;
    private double luck;
    private double time;

    public Performance(Athlete athlete, double luck, double time) {
        this.athlete = athlete;
        this.luck = luck;
        this.time = time;
    }

    public Athlete getAthlete() {
        return athlete;
    }

    public double getLuck() {
        return luck;
    }

    public double getTime() {
        return time;
    }
}
