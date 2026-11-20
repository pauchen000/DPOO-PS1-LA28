package persistence;

import business.Athlete;
import business.Profile;
import com.google.gson.*;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * lee y escribe el fichero athletes.json
 */
public class AthleteJSONDAO {

    public static final String ATHLETES_FILE = "data/athletes.json";

    /**
     * lee todos los atletas del fichero. si todavia no existe, devuelve una lista vacia
     * @return la lista de atletas
     */
    public List<Athlete> cargarAtletas() {
        List<Athlete> athletes = new ArrayList<>();
        File file = new File(ATHLETES_FILE);
        if (!file.exists()) {
            return athletes;
        }
        try (FileReader reader = new FileReader(file)) {
            //leemos el json como un array y pasamos cada elemento a athlete
            JsonArray array = JsonParser.parseReader(reader).getAsJsonArray();
            for (JsonElement element : array) {
                athletes.add(toAthlete(element.getAsJsonObject()));
            }
        } catch (IOException e) {
            return new ArrayList<>();
        }
        return athletes;
    }

    /**
     * anade un atleta al ficherocpero si el fichero no existec lo crea
     * @param atleta el atleta que se guarda
     */
    public void escribirAtleta(Athlete atleta) {
        //cargamos todos, anadimos el nuevo y reescribimos el fichero entero
        List<Athlete> athletes = cargarAtletas();
        athletes.add(atleta);
        writeAll(athletes);
    }

    /**
     * quita del fichero al atleta que tenga ese id
     * @param idAtleta id del atleta a borrar
     */
    public void eliminarAtleta(String idAtleta) {
        List<Athlete> athletes = cargarAtletas();
        List<Athlete> remaining = new ArrayList<>();
        for (Athlete athlete : athletes) {
            if (!athlete.getId().equals(idAtleta)) {
                remaining.add(athlete);
            }
        }
        writeAll(remaining);
    }

    //cada campo se lee a mano
    private Athlete toAthlete(JsonObject object) {
        return new Athlete(
                object.get("id").getAsString(),
                object.get("name").getAsString(),
                LocalDate.parse(object.get("birthDate").getAsString()),
                object.get("delegationCode").getAsString(),
                Profile.valueOf(object.get("profile").getAsString()),
                object.get("speed").getAsInt(),
                object.get("endurance").getAsInt(),
                object.get("strength").getAsInt(),
                object.get("technique").getAsInt(),
                object.get("precision").getAsInt());
    }

    private JsonObject toJson(Athlete athlete) {
        JsonObject object = new JsonObject();
        object.addProperty("id", athlete.getId());
        object.addProperty("profile", athlete.getProfile().name());
        object.addProperty("name", athlete.getName());
        object.addProperty("birthDate", athlete.getBirthDate().toString());
        object.addProperty("delegationCode", athlete.getDelegationCode());
        object.addProperty("speed", athlete.getSpeed());
        object.addProperty("endurance", athlete.getEndurance());
        object.addProperty("strength", athlete.getStrength());
        object.addProperty("technique", athlete.getTechnique());
        object.addProperty("precision", athlete.getPrecision());
        return object;
    }

    private void writeAll(List<Athlete> athletes) {
        JsonArray array = new JsonArray();
        for (Athlete athlete : athletes) {
            array.add(toJson(athlete));
        }
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(ATHLETES_FILE)) {
            gson.toJson(array, writer);
        } catch (IOException e) {
        }
    }
}
