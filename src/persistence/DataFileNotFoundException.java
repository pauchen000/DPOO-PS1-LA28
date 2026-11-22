package persistence;

/**
 * el fichero que se intenta abrir no existe o no se puede abrir
 */
public class DataFileNotFoundException extends PersistenceException {

    /**
     * crea el error con el mensaje que se mostrara
     * @param message que ha pasado
     */
    public DataFileNotFoundException(String message) {
        super(message);
    }
}
