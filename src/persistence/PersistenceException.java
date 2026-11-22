package persistence;

/**
 * error de la capa de persistencia: algo ha ido mal al leer o escribir un fichero
 */
public class PersistenceException extends Exception {

    /**
     * crea el error con el mensaje que se mostrara
     * @param message que ha pasado
     */
    public PersistenceException(String message) {
        super(message);
    }
}
