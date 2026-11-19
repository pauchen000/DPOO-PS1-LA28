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

public class AthleteJSONDAO {

    public static final String ATHLETES_FILE = "data/athletes.json";

    /**
     * Lee todos los atletas del fichero.
     * @return la lista de atletas (vacía si el fichero no existe o no se puede leer)
     */
    public List<Athlete> cargarAtletas() {
        List<Athlete> athletes = new ArrayList<>();
        File file = new File(ATHLETES_FILE);
        if (!file.exists()) {
            return athletes;
        }
        try (FileReader reader = new FileReader(file)) {
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
     * Añade un atleta al fichero. Si el fichero no existe, lo crea.
     * @param atleta atleta a guardar
     */
    public void escribirAtleta(Athlete atleta) {
        List<Athlete> athletes = cargarAtletas();
        athletes.add(atleta);
        writeAll(athletes);
    }

    /**
     * Borra del fichero el atleta con el identificador indicado.
     * @param idAtleta identificador del atleta
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

    // Gson no sabe leer un LocalDate, así que se construye el Athlete campo a campo
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
            // La persistencia no puede mostrar mensajes: si falla, el fichero queda como estaba
        }
    }
}
