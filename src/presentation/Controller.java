package presentation;

import business.Athlete;
import business.AthleteManager;
import business.BusinessException;
import business.Competition;
import business.CompetitionManager;
import business.Delegation;
import business.DelegationManager;
import business.InvalidInputException;
import business.Medal;
import business.Performance;
import business.Profile;
import business.Results;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * pide cosas a la view y se las pasa a los managers y muestra el resultado
 */
public class Controller {

    //opciones del menu principal
    private static final int DELEGATIONS = 1;
    private static final int ATHLETES = 2;
    private static final int COMPETITIONS = 3;
    private static final int SIMULATE_GAMES = 4;
    private static final int EXIT = 0;

    //opciones del submenu de delegaciones
    private static final int LIST_DELEGATIONS = 1;
    private static final int SHOW_MEDALS = 2;

    //opciones del submenu de atletas
    private static final int REGISTER_ATHLETE = 1;
    private static final int LIST_ATHLETES = 2;
    private static final int DELETE_ATHLETE = 3;

    //opciones del submenu de competiciones
    private static final int LOAD_COMPETITIONS = 1;
    private static final int LIST_COMPETITIONS = 2;
    private static final int DELETE_COMPETITION = 3;

    //el 0 vuelve atras en cualquier submenu asiq es geenral para todos
    private static final int BACK = 0;

    private View view;
    private DelegationManager delegationManager;
    private AthleteManager athleteManager;
    private CompetitionManager competitionManager;

    /**
     * guarda la view y los managers que le da el main
     * @param view para mostrar y pedir cosas al usuario
     * @param delegationManager la logica de las delegaciones
     * @param athleteManager la logica de los atletas
     * @param competitionManager la logica de las competiciones
     */
    public Controller(View view, DelegationManager delegationManager,
                      AthleteManager athleteManager, CompetitionManager competitionManager) {
        //llegan ya creados desde el main, aqui no se hace ningun new
        this.view = view;
        this.delegationManager = delegationManager;
        this.athleteManager = athleteManager;
        this.competitionManager = competitionManager;
    }

    /**
     * da la bienvenida y mira que delegations.json este bien
     * @return true si el programa puede arrancar, false si hay que cerrarlo
     */
    public boolean comprobarFicheros() {
        view.mostrarMensaje("Welcome to LA28 Games Lab.");
        view.mostrarMensaje("The Olympics Simulator.");
        view.mostrarMensaje("");
        view.mostrarMensaje("Verifying local files...");

        if (delegationManager.comprobarFichero()) {
            view.mostrarMensaje("Files OK.");
            view.mostrarMensaje("Starting program...");
            return true;
        }

        view.mostrarError("The delegations.json file can't be accessed.");
        view.mostrarMensaje("Shutting down...");
        return false;
    }

    /**
     * el bucle del menu principal y no para hasta que elijan salir
     */
    public void run() {
        int option;

        do {
            //se pregunta en cada vuelta porque pueden haber añadido o borrado competiciones
            boolean simulationAvailable;
            try {
                simulationAvailable = competitionManager.hayCompeticiones();
            } catch (BusinessException e) {
                view.mostrarError(e.getMessage());
                simulationAvailable = false;
            }
            option = view.mostrarMenuPrincipal(simulationAvailable);

            switch (option) {
                case DELEGATIONS -> menuDelegaciones();
                case ATHLETES -> menuAtletas();
                case COMPETITIONS -> menuCompeticiones();
                case SIMULATE_GAMES -> {
                    //solo se simula si hay competiciones, si no sale un error
                    if (simulationAvailable) {
                        simularJuegos();
                    } else {
                        view.mostrarError("The simulation can't be done: there are no competitions.");
                    }
                }
                case EXIT -> view.mostrarMensaje("We hope to see you again!");
                default -> view.mostrarError("Wrong option. Enter a number from 0 to 4.");
            }
        } while (option != EXIT);
    }

    /**
     * el submenu de delegaciones
     */
    public void menuDelegaciones() {
        int option;

        //el submenu se repite hasta que elijan volver
        do {
            option = view.mostrarMenuDelegaciones();

            switch (option) {
                case LIST_DELEGATIONS -> listarDelegaciones();
                case SHOW_MEDALS -> mostrarMedallero();
                //con el 0 no se hace nada, el while termina y se vuelve al menu principal
                case BACK -> { }
                default -> view.mostrarError("Wrong option. Enter a number from 0 to 2.");
            }
        } while (option != BACK);
    }

    /**
     * saca la lista de delegaciones y, si eligen una, su ficha con sus atletas
     */
    public void listarDelegaciones() {
        try {
            //el manager da la lista ya ordenada por nombre
            List<Delegation> delegations = delegationManager.listarDelegaciones();

            //la view devuelve la posicion elegida (0 = volver)
            int option = view.mostrarListaDelegaciones(delegations);

            if (option != BACK) {
                //option-1 porque la lista de java empieza en 0
                Delegation delegation = delegations.get(option - 1);
                //pedimos al manager de atletas los de esa delegacion
                List<Athlete> athletes = athleteManager.atletasDeDelegacion(delegation.getCode());

                view.mostrarDelegacion(delegation, athletes);
                view.esperarEnter();
            }
        } catch (BusinessException e) {
            view.mostrarError(e.getMessage());
        }
    }

    /**
     * muestra la tabla del medallero
     */
    public void mostrarMedallero() {
        try {
            //el manager ordena por medallas y la view dibuja la tabla
            List<Delegation> delegations = delegationManager.obtenerMedallero();

            view.mostrarMedallero(delegations);
            view.esperarEnter();
        } catch (BusinessException e) {
            view.mostrarError(e.getMessage());
        }
    }

    /**
     * el submenu de atletas
     */
    public void menuAtletas() {
        int option;

        do {
            option = view.mostrarMenuAtletas();

            switch (option) {
                case REGISTER_ATHLETE -> registrarAtleta();
                case LIST_ATHLETES -> listarAtletas();
                case DELETE_ATHLETE -> eliminarAtleta();
                case BACK -> { }
                default -> view.mostrarError("Wrong option. Enter a number from 0 to 3.");
            }
        } while (option != BACK);
    }

    /**
     * pide los datos de un atleta nuevo y lo guarda. se puede cancelar al elegir delegacion o perfil
     */
    public void registrarAtleta() {
        String name = view.pedirNombre();

        //se repite hasta que la fecha sea valida (el manager lanza un error si no lo es)
        LocalDate birthDate = null;
        while (birthDate == null) {
            try {
                birthDate = athleteManager.validarFechaNacimiento(view.pedirFecha());
            } catch (InvalidInputException e) {
                view.mostrarError(e.getMessage());
            }
        }

        try {
            //si el usuario cancela la view devuelve null y salimos sin guardar nada
            Delegation delegation = view.elegirDelegacion(delegationManager.listarDelegaciones());
            if (delegation == null) {
                return;
            }

            int profileOption = view.elegirPerfil();
            if (profileOption == BACK) {
                return;
            }
            //el manager convierte el numero en un perfil
            Profile profile = athleteManager.perfilAtleta(profileOption);

            //si ya existe un atleta con ese nombre en la delegacion, el manager lanza DuplicateAthleteException
            Athlete athlete = athleteManager.guardarAtleta(name, birthDate, delegation.getCode(), profile);
            view.mostrarMensaje("Athlete registered successfully.");
            view.mostrarAtleta(athlete, delegation);
        } catch (BusinessException e) {
            view.mostrarError(e.getMessage());
        }
        view.esperarEnter();
    }

    /**
     * busca atletas por nombre, deja elegir uno y muestra su ficha
     */
    public void listarAtletas() {
        try {
            String search = view.pedirBusqueda();
            List<Athlete> athletes = athleteManager.buscarAtletas(search);
            List<Delegation> delegations = delegationManager.listarDelegaciones();

            //la view devuelve null si elige volver (0)
            Athlete athlete = view.elegirAtleta(athletes, delegations);

            if (athlete != null) {
                //buscamos su delegacion para poder mostrar el código y el nombre
                Delegation delegation = delegationManager.buscarPorCodigo(athlete.getDelegationCode());

                view.mostrarAtleta(athlete, delegation);
                view.esperarEnter();
            }
        } catch (BusinessException e) {
            view.mostrarError(e.getMessage());
        }
    }

    /**
     * busca y elige un atleta, lo quita de las competiciones, resta las medallas que eso anula y lo borra del fichero
     */
    public void eliminarAtleta() {
        try {
            String search = view.pedirBusqueda();
            List<Athlete> athletes = athleteManager.buscarAtletas(search);
            List<Delegation> delegations = delegationManager.listarDelegaciones();

            Athlete athlete = view.elegirAtleta(athletes, delegations);
            if (athlete == null) {
                return;
            }

            //antes de borrar nada se pide confirmacion
            boolean confirmed = view.pedirConfirmacion(
                    "Are you sure you want to delete " + athlete.getName()
                            + "? This may affect competitions and medals.");

            if (confirmed) {
                //se quita de las competiciones y devuelve los podios que se anulan
                List<Results> changes = competitionManager.quitarAtletaDeCompeticiones(athlete.getId());

                //por cada podio anulado se restan sus medallas. se hace antes de borrar el atleta porque hace falta buscar a los medallistas
                for (Results results : changes) {
                    restarPodio(results);
                }

                athleteManager.borrarAtleta(athlete.getId());
                view.mostrarMensaje("Athlete deleted successfully.");
            } else {
                view.mostrarMensaje("Operation cancelled.");
            }
        } catch (BusinessException e) {
            view.mostrarError(e.getMessage());
        }
        view.esperarEnter();
    }

    /**
     * el submenu de competiciones
     */
    public void menuCompeticiones() {
        int option;

        do {
            option = view.mostrarMenuCompeticiones();

            switch (option) {
                case LOAD_COMPETITIONS -> cargarCompeticiones();
                case LIST_COMPETITIONS -> listarCompeticiones();
                case DELETE_COMPETITION -> eliminarCompeticion();
                case BACK -> { }
                default -> view.mostrarError("Wrong option. Enter a number from 0 to 3.");
            }
        } while (option != BACK);
    }

    /**
     * pide un csv, valida cada competicion y guarda las que estan bien
     */
    public void cargarCompeticiones() {
        String fileName = view.pedirNombreFichero();
        try {
            List<String[]> rows = competitionManager.leerCSV(fileName);
            view.mostrarMensaje("Opening file \"" + fileName + "\"...");
            view.mostrarMensaje("Validating competitions...");

            //los atletas hacen falta para mirar que los participantes existan
            List<Athlete> athletes = athleteManager.buscarAtletas("");

            for (int i = 0; i < rows.size(); i++) {
                String[] fields = rows.get(i);
                List<String> errors = competitionManager.validarCompeticion(fields, athletes);
                //se guarda en cuanto se valida, asi si el fichero repite un id la siguiente ya da error
                if (errors.isEmpty()) {
                    competitionManager.guardarCompeticion(fields);
                }
                view.mostrarCompeticionLeida(i + 1, rows.size(), fields, errors);
            }
        } catch (BusinessException e) {
            view.mostrarError(e.getMessage());
        }
        view.esperarEnter();
    }

    /**
     * lista las competiciones y, si eligen una, ensena su ficha
     */
    public void listarCompeticiones() {
        try {
            view.mostrarMensaje("");
            view.mostrarMensaje("Available competitions");
            view.mostrarMensaje("----------------------");
            Competition competition = view.elegirCompeticion(competitionManager.listarCompeticiones());

            if (competition != null) {
                //solo se piden los atletas de esta competicion
                List<Athlete> participants = athleteManager.buscarPorIds(competition.getAthleteIds());
                view.mostrarCompeticion(competition, participants, delegationManager.listarDelegaciones());
                view.esperarEnter();
            }
        } catch (BusinessException e) {
            view.mostrarError(e.getMessage());
        }
    }

    /**
     * deja elegir una competicion, pide confirmacion y la borra (y resta sus medallas si estaba simulada)
     */
    public void eliminarCompeticion() {
        try {
            view.mostrarMensaje("");
            view.mostrarMensaje("Delete a competition");
            view.mostrarMensaje("--------------------");
            Competition competition = view.elegirCompeticion(competitionManager.listarCompeticiones());
            if (competition == null) {
                return;
            }

            List<Athlete> participants = athleteManager.buscarPorIds(competition.getAthleteIds());
            view.mostrarCompeticion(competition, participants, delegationManager.listarDelegaciones());

            boolean confirmed = view.pedirConfirmacion(
                    "Deleting this competition will also remove its results and update any related information. Are you sure?");

            if (confirmed) {
                Results results = competitionManager.borrarCompeticion(competition.getId());
                //si ya estaba simulada, sus medallas ya no cuentan
                if (results != null) {
                    restarPodio(results);
                }
                view.mostrarMensaje("The competition \"" + competition.getName() + "\" has been removed from the system.");
            } else {
                view.mostrarMensaje("Operation cancelled.");
            }
        } catch (BusinessException e) {
            view.mostrarError(e.getMessage());
        }
        view.esperarEnter();
    }

    /**
     * simula todas las competiciones una a una y al final ensena el medallero
     */
    public void simularJuegos() {
        try {
            view.mostrarMensaje("");
            view.mostrarMensaje("Simulate Olympic Games");
            view.mostrarMensaje("----------------------");
            view.mostrarMensaje("Starting Olympic Games simulation...");

            //las medallas y resultados de antes se borran y se calculan de nuevo
            delegationManager.reiniciarMedallas();
            competitionManager.reiniciarResultados();

            List<Competition> competitions = competitionManager.listarCompeticiones();
            List<Delegation> delegations = delegationManager.listarDelegaciones();

            for (int i = 0; i < competitions.size(); i++) {
                Competition competition = competitions.get(i);
                List<Athlete> participants = athleteManager.buscarPorIds(competition.getAthleteIds());

                List<Performance> results = competitionManager.simularCompeticion(competition, participants);
                view.mostrarSimulacion(i + 1, competitions.size(), competition, results, delegations);

                //las medallas del podio se suman a las delegaciones
                if (competition.estaSimulada()) {
                    delegationManager.sumarMedallas(podioDelegaciones(competition.getResults()));
                }
                view.esperarEnter();
            }

            view.mostrarMensaje("");
            view.mostrarMensaje("Olympic Games simulation completed");
            view.mostrarMensaje("");
            view.mostrarMensaje("Delegation medal classification");
            view.mostrarMensaje("-------------------------------");
            view.mostrarMedallero(delegationManager.obtenerMedallero());
        } catch (BusinessException e) {
            view.mostrarError(e.getMessage());
        }
        view.esperarEnter();
    }

    //resta a las delegaciones las medallas de un podio anulado
    private void restarPodio(Results results) throws BusinessException {
        if (!delegationManager.restarMedallas(podioDelegaciones(results))) {
            view.mostrarError("Some medals could not be subtracted because the delegation has no medals.");
        }
    }

    //el podio guarda ids de atletas, pero las medallas se suman a la delegacion de cada atleta
    private Map<Medal, String> podioDelegaciones(Results results) throws BusinessException {
        Map<Medal, String> podium = new HashMap<>();
        for (Map.Entry<Medal, String> entry : results.toPodio().entrySet()) {
            Athlete athlete = athleteManager.buscarPorId(entry.getValue());
            podium.put(entry.getKey(), athlete.getDelegationCode());
        }
        return podium;
    }
}
