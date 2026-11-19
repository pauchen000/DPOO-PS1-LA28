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

    //ruta del fichero json
    public static final String DELEGATIONS_FILE = "data/delegations.json";

    /**
     * Comprueba que el fichero existe y que su estructura JSON es correcta.
     * @return true si se puede leer como lista de delegaciones
     */
    public boolean existeFichero() {
        //ver si el fichero esta en su sitio
        File file = new File(DELEGATIONS_FILE);
        if (!file.exists()) {
            return false;
        }
        //si está,lo leemos
        try (FileReader reader = new FileReader(file)) {
            Delegation[] read = new Gson().fromJson(reader, Delegation[].class);
            return read != null;
        } catch (IOException | JsonParseException e) {
            //si falla al abrir o al leer return false
            return false;
        }
    }

    /**
     * Lee todas las delegaciones del fichero.
     * @return la lista de delegaciones (vacía si no se puede leer)
     */
    public List<Delegation> cargarDelegaciones() {
        //lista vacia donde iremos metiendo lo que se lea
        List<Delegation> delegations = new ArrayList<>();
        try (FileReader reader = new FileReader(DELEGATIONS_FILE)) {
            //gson lee el json y crea un objeto delegation por cada elemento del array
            Delegation[] read = new Gson().fromJson(reader, Delegation[].class);
            if (read != null) {
                //pasamos del array a la lista, que es mas comoda de usar
                for (Delegation delegation : read) {
                    delegations.add(delegation);
                }
            }
        } catch (IOException | JsonParseException e) {
            //ante cualquier fallo devolvemos una lista vacia
            return new ArrayList<>();
        }
        return delegations;
    }

    /**
     * Escribe en el fichero la lista completa de delegaciones.
     * @param delegaciones lista que sustituye al contenido actual
     */
    public void guardarDelegaciones(List<Delegation> delegaciones) {
        //este gson escribe el json con sangrias para que se lea bien
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        //filewriter sobreescribe el fichero entero con la lista que nos llega
        try (FileWriter writer = new FileWriter(DELEGATIONS_FILE)) {
            gson.toJson(delegaciones, writer);
        } catch (IOException e) {
        }
    }
}
