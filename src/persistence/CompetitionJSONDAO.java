package persistence;

import business.Competition;
import business.Format;
import business.Results;
import business.Weights;
import com.google.gson.*;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * lee y escribe el fichero competitions.json
 */
public class CompetitionJSONDAO {

    public static final String COMPETITIONS_FILE = "data/competitions.json";

    //asi esta guardada la fecha en el json: 2028-07-15 10:30
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /**
     * lee todas las competiciones del fichero. si todavia no existe, devuelve una lista vacia
     * @return la lista de competiciones
     * @throws PersistenceException si el fichero existe pero no se puede leer
     */
    public List<Competition> cargarCompeticiones() throws PersistenceException {
        List<Competition> competitions = new ArrayList<>();
        File file = new File(COMPETITIONS_FILE);
        if (!file.exists()) {
            return competitions;
        }
        try (FileReader reader = new FileReader(file)) {
            JsonArray array = JsonParser.parseReader(reader).getAsJsonArray();
            for (JsonElement element : array) {
                competitions.add(toCompetition(element.getAsJsonObject()));
            }
        } catch (IOException | JsonParseException | IllegalStateException e) {
            throw new PersistenceException("The competitions.json file can't be read.");
        }
        return competitions;
    }

    /**
     * escribe la lista entera en el fichero (si no existe, lo crea)
     * @param competiciones la lista que se guarda
     * @throws PersistenceException si el fichero no se puede escribir
     */
    public void guardarCompeticiones(List<Competition> competiciones) throws PersistenceException {
        JsonArray array = new JsonArray();
        for (Competition competition : competiciones) {
            array.add(toJson(competition));
        }
        //serializeNulls hace que "results": null se escriba igualmente
        Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
        try (FileWriter writer = new FileWriter(COMPETITIONS_FILE)) {
            gson.toJson(array, writer);
        } catch (IOException e) {
            throw new PersistenceException("The competitions.json file can't be written.");
        }
    }

    /**
     * busca la competicion que tenga ese id
     * @param id el id que se busca
     * @return la competicion, o null si no esta
     * @throws PersistenceException si el fichero no se puede leer
     */
    public Competition buscarPorId(String id) throws PersistenceException {
        for (Competition competition : cargarCompeticiones()) {
            if (competition.getId().equals(id)) {
                return competition;
            }
        }
        return null;
    }

    /**
     * da las competiciones en las que participa un atleta
     * @param idAtleta id del atleta
     * @return las competiciones donde sale ese atleta
     * @throws PersistenceException si el fichero no se puede leer
     */
    public List<Competition> buscarPorAtleta(String idAtleta) throws PersistenceException {
        List<Competition> found = new ArrayList<>();
        for (Competition competition : cargarCompeticiones()) {
            if (competition.getAthleteIds().contains(idAtleta)) {
                found.add(competition);
            }
        }
        return found;
    }

    /**
     * guarda una competicion: si ya hay una con ese id la cambia, y si no la anade al final
     * @param competicion la competicion que se guarda
     * @throws PersistenceException si el fichero no se puede leer o escribir
     */
    public void guardarCompeticion(Competition competicion) throws PersistenceException {
        List<Competition> competitions = cargarCompeticiones();
        boolean replaced = false;
        for (int i = 0; i < competitions.size(); i++) {
            if (competitions.get(i).getId().equals(competicion.getId())) {
                competitions.set(i, competicion);
                replaced = true;
            }
        }
        if (!replaced) {
            competitions.add(competicion);
        }
        guardarCompeticiones(competitions);
    }

    /**
     * quita del fichero la competicion que tenga ese id
     * @param id id de la competicion a borrar
     * @throws PersistenceException si el fichero no se puede leer o escribir
     */
    public void eliminarCompeticion(String id) throws PersistenceException {
        List<Competition> remaining = new ArrayList<>();
        for (Competition competition : cargarCompeticiones()) {
            if (!competition.getId().equals(id)) {
                remaining.add(competition);
            }
        }
        guardarCompeticiones(remaining);
    }

    //pasa un objeto del json a competicion, campo a campo
    private Competition toCompetition(JsonObject object) {
        JsonObject w = object.getAsJsonObject("weights");
        Weights weights = new Weights(w.get("speed").getAsDouble(), w.get("endurance").getAsDouble(),
                w.get("strength").getAsDouble(), w.get("technique").getAsDouble(), w.get("precision").getAsDouble());

        List<String> athleteIds = new ArrayList<>();
        for (JsonElement id : object.getAsJsonArray("athleteIds")) {
            athleteIds.add(id.getAsString());
        }

        Competition competition = new Competition(
                object.get("id").getAsString(),
                Format.valueOf(object.get("format").getAsString()),
                object.get("name").getAsString(),
                LocalDateTime.parse(object.get("dateTime").getAsString(), DATE_FORMAT),
                weights,
                athleteIds);

        //si la competicion no se ha simulado, results es null en el json
        JsonElement results = object.get("results");
        if (results != null && !results.isJsonNull()) {
            JsonObject r = results.getAsJsonObject();
            competition.setResults(new Results(r.get("gold").getAsString(),
                    r.get("silver").getAsString(), r.get("bronze").getAsString()));
        }
        return competition;
    }

    //pasa una competicion a un objeto json, en el mismo orden que el enunciado
    private JsonObject toJson(Competition competition) {
        JsonObject object = new JsonObject();
        object.addProperty("id", competition.getId());
        object.addProperty("format", competition.getFormat().name());
        object.addProperty("name", competition.getName());
        object.addProperty("dateTime", competition.getDateTime().format(DATE_FORMAT));

        JsonObject weights = new JsonObject();
        weights.addProperty("speed", competition.getWeights().getSpeed());
        weights.addProperty("endurance", competition.getWeights().getEndurance());
        weights.addProperty("strength", competition.getWeights().getStrength());
        weights.addProperty("technique", competition.getWeights().getTechnique());
        weights.addProperty("precision", competition.getWeights().getPrecision());
        object.add("weights", weights);

        JsonArray ids = new JsonArray();
        for (String id : competition.getAthleteIds()) {
            ids.add(id);
        }
        object.add("athleteIds", ids);

        if (competition.estaSimulada()) {
            JsonObject results = new JsonObject();
            results.addProperty("gold", competition.getResults().getGold());
            results.addProperty("silver", competition.getResults().getSilver());
            results.addProperty("bronze", competition.getResults().getBronze());
            object.add("results", results);
        } else {
            object.add("results", JsonNull.INSTANCE);
        }
        return object;
    }
}
