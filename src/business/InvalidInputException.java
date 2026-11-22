package business;

/**
 * un dato que ha entrado el usuario no es valido (una fecha, un fichero...)
 */
public class InvalidInputException extends BusinessException {

    /**
     * crea el error con el mensaje que se mostrara
     * @param message que dato esta mal
     */
    public InvalidInputException(String message) {
        super(message);
    }
}
