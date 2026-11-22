package business;

/**
 * error de la capa de negocio. es la que le llega al controller
 */
public class BusinessException extends Exception {

    /**
     * crea el error con el mensaje que se mostrara
     * @param message que ha pasado
     */
    public BusinessException(String message) {
        super(message);
    }
}
