package business;

/**
 * ya hay un atleta con el mismo nombre en la misma delegacion
 */
public class DuplicateAthleteException extends BusinessException {

    /**
     * crea el error con el mensaje que se mostrara
     * @param message que atleta esta repetido
     */
    public DuplicateAthleteException(String message) {
        super(message);
    }
}
