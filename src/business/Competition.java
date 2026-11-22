package business;

import java.time.LocalDateTime;
import java.util.List;

/**
 * una competicion de los juegos: sus datos, sus pesos, sus participantes y su podio si ya se ha simulado
 */
public class Competition {

    private String id;
    private Format format;
    private String name;
    private LocalDateTime dateTime;
    private Weights weights;
    private List<String> athleteIds;
    private Results results;

    /**
     * crea una competicion sin resultados
     * @param id identificador (CMP-XXXX)
     * @param format formato de la competicion
     * @param name nombre de la competicion
     * @param dateTime fecha y hora de inicio
     * @param weights los pesos de los atributos
     * @param athleteIds ids de los atletas que participan
     */
    public Competition(String id, Format format, String name, LocalDateTime dateTime, Weights weights,
                       List<String> athleteIds) {
        this.id = id;
        this.format = format;
        this.name = name;
        this.dateTime = dateTime;
        this.weights = weights;
        this.athleteIds = athleteIds;
    }

    /**
     * devuelve el id de la competicion
     * @return el id
     */
    public String getId() {
        return id;
    }

    /**
     * devuelve el formato (TIMED, SCORED o MEASURED)
     * @return el formato
     */
    public Format getFormat() {
        return format;
    }

    /**
     * devuelve el nombre de la competicion
     * @return el nombre
     */
    public String getName() {
        return name;
    }

    /**
     * devuelve cuando empieza
     * @return la fecha y hora
     */
    public LocalDateTime getDateTime() {
        return dateTime;
    }

    /**
     * devuelve los pesos de los atributos
     * @return los pesos
     */
    public Weights getWeights() {
        return weights;
    }

    /**
     * devuelve los ids de los participantes
     * @return la lista de ids
     */
    public List<String> getAthleteIds() {
        return athleteIds;
    }

    /**
     * devuelve el podio (null si no se ha simulado)
     * @return el podio
     */
    public Results getResults() {
        return results;
    }

    /**
     * cambia el podio (null para borrarlo)
     * @param results el podio nuevo
     */
    public void setResults(Results results) {
        this.results = results;
    }

    /**
     * dice si la competicion ya tiene resultados
     * @return true si ya se ha simulado
     */
    public boolean estaSimulada() {
        return results != null;
    }

    /**
     * quita a un atleta de la lista de participantes
     * @param idAtleta id del atleta que se quita
     */
    public void quitarAtleta(String idAtleta) {
        athleteIds.remove(idAtleta);
    }

    /**
     * calcula el tiempo de un atleta con la formula del enunciado:
     * time = 1 - luck/10 - suma(atributo * peso)
     * @param atleta el atleta que corre
     * @param suerte el factor suerte, entre 0 y 1
     * @return el tiempo (cuanto mas pequeno, mejor)
     */
    public double calcularTiempo(Athlete atleta, double suerte) {
        double sum = atleta.getSpeed() * weights.getSpeed()
                + atleta.getEndurance() * weights.getEndurance()
                + atleta.getStrength() * weights.getStrength()
                + atleta.getTechnique() * weights.getTechnique()
                + atleta.getPrecision() * weights.getPrecision();
        return 1 - suerte / 10 - sum;
    }
}
