package business;

import persistence.AthleteJSONDAO;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AthleteManager {

    //formato dd/MM/aaaa en modo estricto, asi el 31/02 no vale
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

    private AthleteJSONDAO dao;

    public AthleteManager(AthleteJSONDAO dao) {
        this.dao = dao;
    }

    /**
     * Convierte un texto dd/MM/aaaa en fecha.
     * @param texto fecha escrita por el usuario
     * @return la fecha, o null si no es una fecha válida
     */
    public LocalDate validarFechaNacimiento(String texto) {
        try {
            return LocalDate.parse(texto.trim(), DATE_FORMAT);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Convierte la opción del menú de perfiles en un Profile.
     * @param opcion 1 Balanced, 2 Power, 3 Endurance, 4 Technical
     * @return el perfil, o null si la opción no es válida
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
     * Crea un atleta nuevo (id y atributos automáticos) y lo guarda.
     * @param nombre nombre del atleta
     * @param fechaNacimiento fecha de nacimiento
     * @param codigoDelegacion código de su delegación
     * @param perfil perfil del atleta
     * @return el atleta guardado, o null si ya existe uno con ese nombre en la delegación
     */
    public Athlete guardarAtleta(String nombre, LocalDate fechaNacimiento, String codigoDelegacion, Profile perfil) {
        List<Athlete> athletes = dao.cargarAtletas();

        //si ya hay uno con ese nombre en la delegacion no se guarda
        for (Athlete athlete : athletes) {
            if (athlete.getDelegationCode().equals(codigoDelegacion)
                    && athlete.getName().equalsIgnoreCase(nombre)) {
                return null;
            }
        }

        //el id aleatorio pero en orden de uno en uno crece y los 5 atributos
        Random random = new Random();
        Athlete newAthlete = new Athlete(nextId(athletes), nombre, fechaNacimiento, codigoDelegacion, perfil, randomAttribute(random), randomAttribute(random), randomAttribute(random), randomAttribute(random), randomAttribute(random));
        dao.escribirAtleta(newAthlete);
        return newAthlete;
    }

    /**
     * Busca atletas cuyo nombre contenga el texto (sin distinguir mayúsculas).
     * @param texto texto a buscar; vacío devuelve todos los atletas
     * @return atletas que coinciden
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
     * Devuelve los atletas de una delegación.
     * @param codigoDelegacion código de la delegación
     * @return atletas de esa delegación
     */
    public List<Athlete> atletasDeDelegacion(String codigoDelegacion) {
        List<Athlete> found = new ArrayList<>();
        //nos quedamos solo con los de esa delegacion
        for (Athlete athlete : dao.cargarAtletas()) {
            if (athlete.getDelegationCode().equals(codigoDelegacion)) {
                found.add(athlete);
            }
        }
        return found;
    }

    /**
     * Elimina un atleta del sistema.
     * @param idAtleta identificador del atleta
     */
    public void borrarAtleta(String idAtleta) {
        dao.eliminarAtleta(idAtleta);
    }

    /**
     * Busca un atleta por su identificador.
     * @param idAtleta identificador del atleta
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
