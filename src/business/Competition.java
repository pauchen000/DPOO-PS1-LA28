package business;

import java.time.LocalDateTime;
import java.util.List;

public class Competition {

    private String id;
    private Format format;
    private String name;
    private LocalDateTime dateTime;
    private Weights weights;
    private List<String> athleteIds;
    private Results results;

    public Competition(String id, Format format, String name, LocalDateTime dateTime, Weights weights,
                       List<String> athleteIds) {
        this.id = id;
        this.format = format;
        this.name = name;
        this.dateTime = dateTime;
        this.weights = weights;
        this.athleteIds = athleteIds;
    }

    public String getId() {
        return id;
    }

    public Format getFormat() {
        return format;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public Weights getWeights() {
        return weights;
    }

    public List<String> getAthleteIds() {
        return athleteIds;
    }

    public Results getResults() {
        return results;
    }

    public void setResults(Results results) {
        this.results = results;
    }

    public boolean estaSimulada() {
        // TODO
        return false;
    }

    public void quitarAtleta(String idAtleta) {
        // TODO
    }

    public double calcularTiempo(Athlete atleta, double suerte) {
        // TODO
        return 0;
    }
}
