package business;

/**
 * se busca algo (atleta, delegacion o competicion) que no existe
 */
public class NotFoundException extends BusinessException {

    /**
     * crea el error con el mensaje que se mostrara
     * @param message que es lo que no se ha encontrado
     */
    public NotFoundException(String message) {
        super(message);
    }
}
