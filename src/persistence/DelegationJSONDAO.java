package persistence;

import business.Delegation;
import com.google.gson.*;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DelegationJSONDAO {

    public static final String DELEGATIONS_FILE = "data/delegations.json";

    /**
     * Comprueba que el fichero existe y que su estructura JSON es correcta.
     * @return true si se puede leer como lista de delegaciones
     */
    public boolean existeFichero() {
        File file = new File(DELEGATIONS_FILE);
        if (!file.exists()) {
            return false;
        }
        try (FileReader reader = new FileReader(file)) {
            Delegation[] read = new Gson().fromJson(reader, Delegation[].class);
            return read != null;
        } catch (IOException | JsonParseException e) {
            return false;
        }
    }

    /**
     * Lee todas las delegaciones del fichero.
     * @return la lista de delegaciones (vacía si no se puede leer)
     */
    public List<Delegation> cargarDelegaciones() {
        List<Delegation> delegations = new ArrayList<>();
        try (FileReader reader = new FileReader(DELEGATIONS_FILE)) {
            Delegation[] read = new Gson().fromJson(reader, Delegation[].class);
            if (read != null) {
                for (Delegation delegation : read) {
                    delegations.add(delegation);
                }
            }
        } catch (IOException | JsonParseException e) {
            return new ArrayList<>();
        }
        return delegations;
    }

    /**
     * Escribe en el fichero la lista completa de delegaciones.
     * @param delegaciones lista que sustituye al contenido actual
     */
    public void guardarDelegaciones(List<Delegation> delegaciones) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(DELEGATIONS_FILE)) {
            gson.toJson(delegaciones, writer);
        } catch (IOException e) {
            // La persistencia no puede mostrar mensajes: si falla, el fichero queda como estaba
        }
    }
}
