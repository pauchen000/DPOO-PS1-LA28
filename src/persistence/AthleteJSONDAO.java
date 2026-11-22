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
     * @throws PersistenceException si el fichero existe pero no se puede leer
     */
    public List<Athlete> cargarAtletas() throws PersistenceException {
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
        } catch (IOException | JsonParseException | IllegalStateException e) {
            throw new PersistenceException("The athletes.json file can't be read.");
        }
        return athletes;
    }

    /**
     * anade un atleta al fichero, pero si el fichero no existe lo crea
     * @param atleta el atleta que se guarda
     * @throws PersistenceException si el fichero no se puede leer o escribir
     */
    public void escribirAtleta(Athlete atleta) throws PersistenceException {
        //cargamos todos, anadimos el nuevo y reescribimos el fichero entero
        List<Athlete> athletes = cargarAtletas();
        athletes.add(atleta);
        writeAll(athletes);
    }

    /**
     * quita del fichero al atleta que tenga ese id
     * @param idAtleta id del atleta a borrar
     * @throws PersistenceException si el fichero no se puede leer o escribir
     */
    public void eliminarAtleta(String idAtleta) throws PersistenceException {
        List<Athlete> athletes = cargarAtletas();
        List<Athlete> remaining = new ArrayList<>();
        for (Athlete athlete : athletes) {
            if (!athlete.getId().equals(idAtleta)) {
                remaining.add(athlete);
            }
        }
        writeAll(remaining);
    }

    /**
     * busca el atleta que tenga ese id
     * @param id el id que se busca
     * @return el atleta, o null si no esta
     * @throws PersistenceException si el fichero no se puede leer
     */
    public Athlete buscarPorId(String id) throws PersistenceException {
        for (Athlete athlete : cargarAtletas()) {
            if (athlete.getId().equals(id)) {
                return athlete;
            }
        }
        return null;
    }

    /**
     * da los atletas cuyo nombre contenga el texto, sin mirar mayusculas
     * @param texto lo que se busca; si esta vacio salen todos
     * @return los atletas que coinciden
     * @throws PersistenceException si el fichero no se puede leer
     */
    public List<Athlete> buscarPorTexto(String texto) throws PersistenceException {
        List<Athlete> found = new ArrayList<>();
        for (Athlete athlete : cargarAtletas()) {
            if (athlete.getName().toLowerCase().contains(texto.toLowerCase())) {
                found.add(athlete);
            }
        }
        return found;
    }

    /**
     * da los atletas de una delegacion
     * @param codigo codigo de la delegacion
     * @return los atletas de esa delegacion
     * @throws PersistenceException si el fichero no se puede leer
     */
    public List<Athlete> buscarPorDelegacion(String codigo) throws PersistenceException {
        List<Athlete> found = new ArrayList<>();
        for (Athlete athlete : cargarAtletas()) {
            if (athlete.getDelegationCode().equals(codigo)) {
                found.add(athlete);
            }
        }
        return found;
    }

    /**
     * da los atletas de una lista de ids, en el mismo orden que los ids.
     * si algun id no existe, simplemente no sale
     * @param ids los ids que se buscan
     * @return los atletas encontrados
     * @throws PersistenceException si el fichero no se puede leer
     */
    public List<Athlete> buscarPorIds(List<String> ids) throws PersistenceException {
        List<Athlete> athletes = cargarAtletas();
        List<Athlete> found = new ArrayList<>();
        for (String id : ids) {
            for (Athlete athlete : athletes) {
                if (athlete.getId().equals(id)) {
                    found.add(athlete);
                }
            }
        }
        return found;
    }

    /**
     * mira si ya hay un atleta con ese nombre en esa delegacion (sin mirar mayusculas)
     * @param nombre nombre del atleta
     * @param codigoDelegacion codigo de la delegacion
     * @return true si ya existe
     * @throws PersistenceException si el fichero no se puede leer
     */
    public boolean existeAtleta(String nombre, String codigoDelegacion) throws PersistenceException {
        for (Athlete athlete : cargarAtletas()) {
            if (athlete.getDelegationCode().equals(codigoDelegacion)
                    && athlete.getName().equalsIgnoreCase(nombre)) {
                return true;
            }
        }
        return false;
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

    private void writeAll(List<Athlete> athletes) throws PersistenceException {
        JsonArray array = new JsonArray();
        for (Athlete athlete : athletes) {
            array.add(toJson(athlete));
        }
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(ATHLETES_FILE)) {
            gson.toJson(array, writer);
        } catch (IOException e) {
            throw new PersistenceException("The athletes.json file can't be written.");
        }
    }
}
