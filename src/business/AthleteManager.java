package business;

import persistence.AthleteJSONDAO;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
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
     * @return la fecha, o null si no es valida
     */
    public LocalDate validarFechaNacimiento(String texto) {
        try {
            return LocalDate.parse(texto.trim(), DATE_FORMAT);
        } catch (DateTimeParseException e) {
            return null;
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
     * @return el atleta guardado, o null si en esa delegacion ya hay uno con ese nombre
     */
    public Athlete guardarAtleta(String nombre, LocalDate fechaNacimiento, String codigoDelegacion, Profile perfil) {
        List<Athlete> athletes = dao.cargarAtletas();

        for (Athlete athlete : athletes) {
            if (athlete.getDelegationCode().equals(codigoDelegacion)
                    && athlete.getName().equalsIgnoreCase(nombre)) {
                return null;
            }
        }

        Random random = new Random();
        Athlete newAthlete = new Athlete(nextId(athletes), nombre, fechaNacimiento, codigoDelegacion, perfil, randomAttribute(random), randomAttribute(random), randomAttribute(random), randomAttribute(random), randomAttribute(random));
        dao.escribirAtleta(newAthlete);
        return newAthlete;
    }

    /**
     * saca los atletas que coincidan con el nombre que te dicen de buscar  y no importan lsa mayusculas
     * @param texto lo que se busca; si esta vacio salen todos
     * @return los atletas que coinciden
     */
    public List<Athlete> buscarAtletas(String texto) {
        List<Athlete> found = new ArrayList<>();
        for (Athlete athlete : dao.cargarAtletas()) {
            if (athlete.getName().toLowerCase().contains(texto.toLowerCase())) {
                found.add(athlete);
            }
        }
        return found;
    }

    /**
     * da los atletas que pertenecen a una delegacion
     * @param codigoDelegacion codigo de la delegacion
     * @return los atletas de esa delegacion
     */
    public List<Athlete> atletasDeDelegacion(String codigoDelegacion) {
        List<Athlete> found = new ArrayList<>();
        for (Athlete athlete : dao.cargarAtletas()) {
            if (athlete.getDelegationCode().equals(codigoDelegacion)) {
                found.add(athlete);
            }
        }
        return found;
    }

    /**
     * borra al atleta del fichero
     * @param idAtleta id del atleta a borrar
     */
    public void borrarAtleta(String idAtleta) {
        dao.eliminarAtleta(idAtleta);
    }

    /**
     * busca un atleta por su id
     * @param idAtleta el id que se busca
     * @return el atleta, o null si no existe
     */
    public Athlete buscarPorId(String idAtleta) {
        for (Athlete athlete : dao.cargarAtletas()) {
            if (athlete.getId().equals(idAtleta)) {
                return athlete;
            }
        }
        return null;
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
