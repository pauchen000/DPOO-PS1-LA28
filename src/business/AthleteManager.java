package business;

import persistence.AthleteJSONDAO;
import persistence.PersistenceException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;
import java.util.Random;

/**
 *  valida datos, y despues ya o crea o busca o borra atletas
 */
public class AthleteManager {

    //formato dd/MM/aaaa en modo estricto, asi el 31/02 no vale
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

    private AthleteJSONDAO dao;

    /**
     * guarda el dao con el que leera y escribira los atletas
     * @param dao el acceso al fichero de atletas
     */
    public AthleteManager(AthleteJSONDAO dao) {
        this.dao = dao;
    }

    /**
     * pasa un texto dd/MM/aaaa a fecha
     * @param texto la fecha tal como la escribio el usuario
     * @return la fecha
     * @throws InvalidInputException si la fecha no es valida
     */
    public LocalDate validarFechaNacimiento(String texto) throws InvalidInputException {
        try {
            return LocalDate.parse(texto.trim(), DATE_FORMAT);
        } catch (DateTimeParseException e) {
            throw new InvalidInputException("The date of birth is not valid. Please enter a valid date using the format DD/MM/YYYY.");
        }
    }

    /**
     * traduce el numero del menu de perfiles al perfil que le toca
     * @param opcion 1 Balanced, 2 Power, 3 Endurance, 4 Technical
     * @return el perfil, o null si el numero no vale
     */
    public Profile perfilAtleta(int opcion) {
        switch (opcion) {
            case 1:
                return Profile.BALANCED;
            case 2:
                return Profile.POWER;
            case 3:
                return Profile.ENDURANCE;
            case 4:
                return Profile.TECHNICAL;
            default:
                return null;
        }
    }

    /**
     * crea un atleta nuevo (el id y los atributos se generan solos) y lo guarda en el fichero
     * @param nombre nombre del atleta
     * @param fechaNacimiento fecha de nacimiento
     * @param codigoDelegacion codigo de su delegacion
     * @param perfil perfil del atleta
     * @return el atleta guardado
     * @throws BusinessException si ya hay uno con ese nombre en la delegacion o si falla el fichero
     */
    public Athlete guardarAtleta(String nombre, LocalDate fechaNacimiento, String codigoDelegacion, Profile perfil) throws BusinessException {
        //se quitan los espacios de los lados, asi "  Pepe  " es lo mismo que "Pepe"
        nombre = nombre.trim();
        if (nombre.isEmpty()) {
            throw new InvalidInputException("The athlete name can't be empty.");
        }
        try {
            //el dao mira si en esa delegacion ya hay alguien con el mismo nombre
            if (dao.existeAtleta(nombre, codigoDelegacion)) {
                throw new DuplicateAthleteException("An athlete with that name already exists in this delegation.");
            }

            Random random = new Random();
            Athlete newAthlete = new Athlete(nextId(dao.cargarAtletas()), nombre, fechaNacimiento, codigoDelegacion, perfil, randomAttribute(random), randomAttribute(random), randomAttribute(random), randomAttribute(random), randomAttribute(random));
            dao.escribirAtleta(newAthlete);
            return newAthlete;
        } catch (PersistenceException e) {
            //el error del fichero se pasa a la capa de presentacion como error de negocio
            throw new BusinessException(e.getMessage());
        }
    }

    /**
     * saca los atletas que coincidan con el nombre que te dicen de buscar  y no importan lsa mayusculas
     * @param texto lo que se busca; si esta vacio salen todos
     * @return los atletas que coinciden
     * @throws BusinessException si falla el fichero
     */
    public List<Athlete> buscarAtletas(String texto) throws BusinessException {
        try {
            return dao.buscarPorTexto(texto);
        } catch (PersistenceException e) {
            throw new BusinessException(e.getMessage());
        }
    }

    /**
     * da los atletas que pertenecen a una delegacion
     * @param codigoDelegacion codigo de la delegacion
     * @return los atletas de esa delegacion
     * @throws BusinessException si falla el fichero
     */
    public List<Athlete> atletasDeDelegacion(String codigoDelegacion) throws BusinessException {
        try {
            return dao.buscarPorDelegacion(codigoDelegacion);
        } catch (PersistenceException e) {
            throw new BusinessException(e.getMessage());
        }
    }

    /**
     * borra al atleta del fichero
     * @param idAtleta id del atleta a borrar
     * @throws BusinessException si falla el fichero
     */
    public void borrarAtleta(String idAtleta) throws BusinessException {
        try {
            dao.eliminarAtleta(idAtleta);
        } catch (PersistenceException e) {
            throw new BusinessException(e.getMessage());
        }
    }

    /**
     * busca un atleta por su id
     * @param idAtleta el id que se busca
     * @return el atleta
     * @throws BusinessException si no existe (NotFoundException) o si falla el fichero
     */
    public Athlete buscarPorId(String idAtleta) throws BusinessException {
        try {
            Athlete athlete = dao.buscarPorId(idAtleta);
            if (athlete == null) {
                throw new NotFoundException("The athlete \"" + idAtleta + "\" does not exist.");
            }
            return athlete;
        } catch (PersistenceException e) {
            throw new BusinessException(e.getMessage());
        }
    }

    /**
     * da los atletas de una lista de ids (por ejemplo, los participantes de una competicion)
     * @param ids los ids de los atletas
     * @return los atletas que existen, en el mismo orden
     * @throws BusinessException si falla el fichero
     */
    public List<Athlete> buscarPorIds(List<String> ids) throws BusinessException {
        try {
            return dao.buscarPorIds(ids);
        } catch (PersistenceException e) {
            throw new BusinessException(e.getMessage());
        }
    }

    //el id nuevo es el mayor que haya en el fichero mas uno
    private String nextId(List<Athlete> athletes) {
        int max = 0;
        for (Athlete athlete : athletes) {
            //substring(4) quita el id y deja solo el numero
            int number = Integer.parseInt(athlete.getId().substring(4));
            if (number > max) {
                max = number;
            }
        }
        return String.format("ATH-%03d", max + 1);
    }

    private int randomAttribute(Random random) {
        return random.nextInt(100) + 1;
    }
}