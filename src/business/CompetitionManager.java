package business;

import persistence.CompetitionCSVDAO;
import persistence.CompetitionJSONDAO;
import persistence.PersistenceException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * la logica de las competiciones: cargar el csv, validar, borrar y simular
 */
public class CompetitionManager {

    //posicion de cada campo en una linea del csv
    private static final int ID = 0;
    private static final int FORMAT = 1;
    private static final int NAME = 2;
    private static final int DATE_TIME = 3;
    private static final int FIRST_WEIGHT = 4;
    private static final int ATHLETES = 9;
    private static final int NUM_FIELDS = 10;

    //los ids de los atletas van separados por | dentro del csv
    private static final String ATHLETE_SEPARATOR = "\\|";
    private static final int MIN_ATHLETES = 3;

    //formato yyyy-MM-dd HH:mm en modo estricto
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm").withResolverStyle(ResolverStyle.STRICT);

    //nombres de los pesos, en el mismo orden que en el csv (para los mensajes de error)
    private static final String[] WEIGHT_NAMES = {"speed", "endurance", "strength", "technique", "precision"};

    private CompetitionJSONDAO dao;
    private CompetitionCSVDAO csvDao;

    /**
     * guarda los dos daos que usa: el del json y el del csv
     * @param dao el acceso a competitions.json
     * @param csvDao el acceso a los csv de competiciones nuevas
     */
    public CompetitionManager(CompetitionJSONDAO dao, CompetitionCSVDAO csvDao) {
        this.dao = dao;
        this.csvDao = csvDao;
    }

    /**
     * mira si hay al menos una competicion (para saber si se puede simular)
     * @return true si hay alguna
     * @throws BusinessException si falla el fichero
     */
    public boolean hayCompeticiones() throws BusinessException {
        return !listarCompeticiones().isEmpty();
    }

    /**
     * da todas las competiciones, en el orden del fichero
     * @return la lista de competiciones
     * @throws BusinessException si falla el fichero
     */
    public List<Competition> listarCompeticiones() throws BusinessException {
        try {
            return dao.cargarCompeticiones();
        } catch (PersistenceException e) {
            throw new BusinessException(e.getMessage());
        }
    }

    /**
     * lee el csv y devuelve los campos de cada competicion
     * @param nombreFichero el nombre del csv
     * @return una lista con los campos de cada linea
     * @throws InvalidInputException si el fichero no existe o no se puede abrir
     */
    public List<String[]> leerCSV(String nombreFichero) throws InvalidInputException {
        try {
            return csvDao.leerFichero(nombreFichero);
        } catch (PersistenceException e) {
            //el usuario ha escrito un fichero que no se puede usar
            throw new InvalidInputException(e.getMessage());
        }
    }

    /**
     * comprueba todos los campos de una competicion del csv y apunta cada error que encuentra
     * @param campos los campos de la linea del csv
     * @param atletas los atletas del sistema, para mirar que los participantes existan
     * @return la lista de errores (vacia si todo esta bien)
     * @throws BusinessException si falla el fichero al mirar si el id ya existe
     */
    public List<String> validarCompeticion(String[] campos, List<Athlete> atletas) throws BusinessException {
        List<String> errors = new ArrayList<>();

        //si no tiene los 10 campos no se puede mirar nada mas
        if (campos.length != NUM_FIELDS) {
            errors.add("The line has " + campos.length + " fields and it must have " + NUM_FIELDS + ".");
            return errors;
        }

        //id: CMP- y despues numeros, y que no exista ya
        String id = campos[ID].trim();
        if (!id.startsWith("CMP-") || !isNumber(id.substring(4))) {
            errors.add("Invalid competition identifier \"" + id + "\". It must follow the format CMP-XXXX.");
        } else {
            try {
                if (dao.buscarPorId(id) != null) {
                    errors.add("Competition identifier \"" + id + "\" already exists.");
                }
            } catch (PersistenceException e) {
                throw new BusinessException(e.getMessage());
            }
        }

        //formato: TIMED, SCORED o MEASURED
        String format = campos[FORMAT].trim();
        if (!format.equals("TIMED") && !format.equals("SCORED") && !format.equals("MEASURED")) {
            errors.add("Invalid competition format \"" + format + "\".");
        }

        //nombre: libre, pero no vacio
        if (campos[NAME].trim().isEmpty()) {
            errors.add("The competition name can't be empty.");
        }

        //fecha y hora en formato yyyy-MM-dd HH:mm
        try {
            LocalDateTime.parse(campos[DATE_TIME].trim(), DATE_FORMAT);
        } catch (DateTimeParseException e) {
            errors.add("Invalid start date and time \"" + campos[DATE_TIME].trim() + "\".");
        }

        //pesos: cada uno entre 0 y 1, y que sumen 1
        double sum = 0;
        boolean weightsAreNumbers = true;
        for (int i = 0; i < WEIGHT_NAMES.length; i++) {
            try {
                double weight = Double.parseDouble(campos[FIRST_WEIGHT + i].trim());
                if (weight < 0.0 || weight > 1.0) {
                    errors.add("The " + WEIGHT_NAMES[i] + " weight must be between 0.0 and 1.0.");
                }
                sum = sum + weight;
            } catch (NumberFormatException e) {
                errors.add("The " + WEIGHT_NAMES[i] + " weight \"" + campos[FIRST_WEIGHT + i].trim() + "\" is not a number.");
                weightsAreNumbers = false;
            }
        }
        //los double no son exactos (0.1 + 0.2 no da 0.3 justo), por eso se deja un margen pequeno
        if (weightsAreNumbers && Math.abs(sum - 1.0) > 0.001) {
            errors.add(String.format("The weights add up to %.2f. Their sum must be 1.0.", sum));
        }

        //atletas: que existan, que no se repitan y que haya minimo 3
        String[] ids = campos[ATHLETES].trim().split(ATHLETE_SEPARATOR);
        List<String> seen = new ArrayList<>();
        for (String athleteId : ids) {
            String trimmed = athleteId.trim();
            if (!trimmed.isEmpty()) {
                if (!existsAthlete(trimmed, atletas)) {
                    errors.add("Athlete \"" + trimmed + "\" does not exist in the system.");
                } else if (seen.contains(trimmed)) {
                    errors.add("Athlete \"" + trimmed + "\" appears more than once.");
                }
                seen.add(trimmed);
            }
        }
        if (seen.size() < MIN_ATHLETES) {
            errors.add("A competition needs at least " + MIN_ATHLETES + " athletes.");
        }

        return errors;
    }

    /**
     * crea la competicion a partir de los campos del csv (ya validados) y la guarda sin resultados
     * @param campos los campos de la linea del csv
     * @return la competicion guardada
     * @throws BusinessException si falla el fichero
     */
    public Competition guardarCompeticion(String[] campos) throws BusinessException {
        Weights weights = new Weights(
                Double.parseDouble(campos[FIRST_WEIGHT].trim()),
                Double.parseDouble(campos[FIRST_WEIGHT + 1].trim()),
                Double.parseDouble(campos[FIRST_WEIGHT + 2].trim()),
                Double.parseDouble(campos[FIRST_WEIGHT + 3].trim()),
                Double.parseDouble(campos[FIRST_WEIGHT + 4].trim()));

        List<String> athleteIds = new ArrayList<>();
        for (String athleteId : campos[ATHLETES].trim().split(ATHLETE_SEPARATOR)) {
            if (!athleteId.trim().isEmpty()) {
                athleteIds.add(athleteId.trim());
            }
        }

        Competition competition = new Competition(campos[ID].trim(), Format.valueOf(campos[FORMAT].trim()),
                campos[NAME].trim(), LocalDateTime.parse(campos[DATE_TIME].trim(), DATE_FORMAT), weights, athleteIds);

        try {
            dao.guardarCompeticion(competition);
        } catch (PersistenceException e) {
            throw new BusinessException(e.getMessage());
        }
        return competition;
    }

    /**
     * borra una competicion del fichero
     * @param idCompeticion id de la competicion
     * @return sus resultados, para poder quitar las medallas (null si no se habia simulado)
     * @throws BusinessException si no existe (NotFoundException) o si falla el fichero
     */
    public Results borrarCompeticion(String idCompeticion) throws BusinessException {
        Competition competition = buscarPorId(idCompeticion);
        try {
            dao.eliminarCompeticion(idCompeticion);
        } catch (PersistenceException e) {
            throw new BusinessException(e.getMessage());
        }
        return competition.getResults();
    }

    /**
     * quita a un atleta de todas sus competiciones. si una ya estaba simulada se anula su podio,
     * y si se queda con menos de 3 participantes se borra
     * @param idAtleta id del atleta que se va a borrar
     * @return los podios anulados, para que se resten esas medallas
     * @throws BusinessException si falla el fichero
     */
    public List<Results> quitarAtletaDeCompeticiones(String idAtleta) throws BusinessException {
        List<Results> cancelled = new ArrayList<>();
        try {
            //solo se cargan las competiciones donde sale este atleta
            for (Competition competition : dao.buscarPorAtleta(idAtleta)) {
                if (competition.estaSimulada()) {
                    cancelled.add(competition.getResults());
                    competition.setResults(null);
                }
                competition.quitarAtleta(idAtleta);

                if (competition.getAthleteIds().size() < MIN_ATHLETES) {
                    dao.eliminarCompeticion(competition.getId());
                } else {
                    dao.guardarCompeticion(competition);
                }
            }
        } catch (PersistenceException e) {
            throw new BusinessException(e.getMessage());
        }
        return cancelled;
    }

    /**
     * borra los resultados de todas las competiciones (antes de una simulacion nueva)
     * @throws BusinessException si falla el fichero
     */
    public void reiniciarResultados() throws BusinessException {
        List<Competition> competitions = listarCompeticiones();
        for (Competition competition : competitions) {
            competition.setResults(null);
        }
        try {
            dao.guardarCompeticiones(competitions);
        } catch (PersistenceException e) {
            throw new BusinessException(e.getMessage());
        }
    }

    /**
     * simula una competicion: da una suerte a cada atleta, calcula su tiempo, los ordena
     * de menor a mayor tiempo y guarda el podio
     * @param competicion la competicion que se simula
     * @param participantes los atletas que participan
     * @return las actuaciones ordenadas (la primera es la del ganador)
     * @throws BusinessException si falla el fichero
     */
    public List<Performance> simularCompeticion(Competition competicion, List<Athlete> participantes) throws BusinessException {
        Random random = new Random();
        List<Performance> sorted = new ArrayList<>();

        for (Athlete athlete : participantes) {
            double luck = random.nextDouble();
            Performance performance = new Performance(athlete, luck, competicion.calcularTiempo(athlete, luck));

            //se mete en su sitio para que la lista quede ordenada por tiempo
            int position = 0;
            while (position < sorted.size() && sorted.get(position).getTime() <= performance.getTime()) {
                position++;
            }
            sorted.add(position, performance);
        }

        //el podio son los tres primeros
        if (sorted.size() >= MIN_ATHLETES) {
            competicion.setResults(new Results(sorted.get(0).getAthlete().getId(),
                    sorted.get(1).getAthlete().getId(), sorted.get(2).getAthlete().getId()));
        }

        try {
            dao.guardarCompeticion(competicion);
        } catch (PersistenceException e) {
            throw new BusinessException(e.getMessage());
        }
        return sorted;
    }

    /**
     * busca una competicion por su id
     * @param id el id que se busca
     * @return la competicion
     * @throws BusinessException si no existe (NotFoundException) o si falla el fichero
     */
    public Competition buscarPorId(String id) throws BusinessException {
        try {
            Competition competition = dao.buscarPorId(id);
            if (competition == null) {
                throw new NotFoundException("The competition \"" + id + "\" does not exist.");
            }
            return competition;
        } catch (PersistenceException e) {
            throw new BusinessException(e.getMessage());
        }
    }

    //mira si un texto son solo numeros (y no esta vacio)
    private boolean isNumber(String text) {
        if (text.isEmpty()) {
            return false;
        }
        for (int i = 0; i < text.length(); i++) {
            if (!Character.isDigit(text.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    //mira si el id esta en la lista de atletas
    private boolean existsAthlete(String id, List<Athlete> atletas) {
        for (Athlete athlete : atletas) {
            if (athlete.getId().equals(id)) {
                return true;
            }
        }
        return false;
    }
}
