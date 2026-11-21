package persistence;

import business.Delegation;
import com.google.gson.*;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * lee y escribe el fichero delegations.json
 */
public class DelegationJSONDAO {

    //ruta del fichero json
    public static final String DELEGATIONS_FILE = "data/delegations.json";

    /**
     * mira que el fichero este en su sitio y que el json se pueda leer
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
            //si falla al abrir o al leer return false
            return false;
        }
    }

    /**
     * lee el fichero entero y lo pasa a una lista de delegaciones
     * @return la lista de delegaciones (vacia si algo falla)
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
            return new ArrayList<>();
        }
        return delegations;
    }

    /**
     * escribe la lista entera en el fichero
     * @param delegaciones la lista que se guarda
     */
    public void guardarDelegaciones(List<Delegation> delegaciones) {
        //este gson escribe el json con tabulaciones para que se lea bien
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(DELEGATIONS_FILE)) {
            gson.toJson(delegaciones, writer);
        } catch (IOException e) {
        }
    }
}
