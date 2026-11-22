package persistence;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * lee el fichero csv con las competiciones nuevas
 */
public class CompetitionCSVDAO {

    private static final String SEPARATOR = ",";

    /**
     * lee el csv y devuelve cada linea partida en campos. se salta la cabecera y las lineas vacias
     * @param nombreFichero el nombre (o ruta) del fichero csv
     * @return una lista con los campos de cada competicion
     * @throws DataFileNotFoundException si el fichero no existe o no se puede abrir
     */
    public List<String[]> leerFichero(String nombreFichero) throws DataFileNotFoundException {
        List<String[]> rows = new ArrayList<>();
        Path path = Path.of(nombreFichero);
        if (!Files.exists(path)) {
            throw new DataFileNotFoundException("The file \"" + nombreFichero + "\" does not exist.");
        }
        try {
            List<String> lines = Files.readAllLines(path);
            for (String line : lines) {
                //algunos editores (bloc de notas, excel) ponen un caracter invisible al principio
                line = line.replace("\uFEFF", "");
                //la primera linea es la cabecera (id,format,...) y no es una competicion
                if (!line.isBlank() && !line.startsWith("id" + SEPARATOR)) {
                    //el -1 hace que tambien salgan los campos vacios del final
                    rows.add(line.split(SEPARATOR, -1));
                }
            }
        } catch (IOException e) {
            throw new DataFileNotFoundException("The file \"" + nombreFichero + "\" can't be opened.");
        }
        return rows;
    }
}