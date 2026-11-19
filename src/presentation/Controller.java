package presentation;

import business.Athlete;
import business.AthleteManager;
import business.CompetitionManager;
import business.Delegation;
import business.DelegationManager;
import business.Medal;
import business.Profile;
import business.Results;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    //el 0 vuelve atras en cualquier submenu asiq es geenral para todos
    private static final int BACK = 0;

    private View view;
    private DelegationManager delegationManager;
    private AthleteManager athleteManager;
    private CompetitionManager competitionManager;

    public Controller(View view, DelegationManager delegationManager,
                      AthleteManager athleteManager, CompetitionManager competitionManager) {
        //llegan ya creados desde el main, aqui no se hace ningun new
        this.view = view;
        this.delegationManager = delegationManager;
        this.athleteManager = athleteManager;
        this.competitionManager = competitionManager;
    }

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

        //si falla devolvemos false y el main no arranca el menu
        view.mostrarError("The delegations.json file can't be accessed.");
        view.mostrarMensaje("Shutting down...");
        return false;
    }

    public void run() {
        int option;

        //bucle del menu principal q se repite hasta que elijan salir
        do {
            //se pregunta en cada vuelta porque pueden haber añadido o borrado competiciones
            boolean simulationAvailable = competitionManager.hayCompeticiones();
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

    public void listarDelegaciones() {
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
    }

    public void mostrarMedallero() {
        //el manager ordena por medallas y la view dibuja la tabla
        List<Delegation> delegations = delegationManager.obtenerMedallero();

        view.mostrarMedallero(delegations);
        view.esperarEnter();
    }

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

    public void registrarAtleta() {
        //los datos se piden uno a uno a la view
        String name = view.pedirNombre();

        //se repite hasta que la fecha sea valida (el manager devuelve null si no lo es)
        LocalDate birthDate = athleteManager.validarFechaNacimiento(view.pedirFecha());
        while (birthDate == null) {
            view.mostrarError("The date of birth is not valid. Please enter a valid date using the format DD/MM/YYYY.");
            birthDate = athleteManager.validarFechaNacimiento(view.pedirFecha());
        }

        //si el usuario cancela la view devuelve null y salimos sin guardar nada
        Delegation delegation = view.elegirDelegacion(delegationManager.listarDelegaciones());
        if (delegation == null) {
            return;
        }

        //el 0 tambien es cancelar
        int profileOption = view.elegirPerfil();
        if (profileOption == BACK) {
            return;
        }
        //el manager convierte el numero en un perfil
        Profile profile = athleteManager.perfilAtleta(profileOption);

        //el manager devuelve null si ya existe un atleta con ese nombre en la delegacion
        Athlete athlete = athleteManager.guardarAtleta(name, birthDate, delegation.getCode(), profile);
        if (athlete == null) {
            view.mostrarError("An athlete with that name already exists in this delegation.");
        } else {
            view.mostrarMensaje("Athlete registered successfully.");
            view.mostrarAtleta(athlete, delegation);
        }
        view.esperarEnter();
    }

    public void listarAtletas() {
        //buscar y elegir uno de la lista y mostrar-lo
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
    }

    public void eliminarAtleta() {
        //el principio es igual que listar que es buscar y elegir un atleta
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

            //por cada podio anulado se restan sus medallas a las delegaciones y se hace antes de borrar el atleta porque hace falta buscar a los medallistas
            for (Results results : changes) {
                //el manager espera un codigo de la delegacion que la tenia
                Map<Medal, String> podium = new HashMap<>();
                podium.put(Medal.GOLD, athleteManager.buscarPorId(results.getGold()).getDelegationCode());
                podium.put(Medal.SILVER, athleteManager.buscarPorId(results.getSilver()).getDelegationCode());
                podium.put(Medal.BRONZE, athleteManager.buscarPorId(results.getBronze()).getDelegationCode());

                if (!delegationManager.restarMedallas(podium)) {
                    view.mostrarError("Some medals could not be subtracted because the delegation has no medals.");
                }
            }

            //y se borra del fichero de atletas
            athleteManager.borrarAtleta(athlete.getId());
            view.mostrarMensaje("Athlete deleted successfully.");
        } else {
            view.mostrarMensaje("Operation cancelled.");
        }
        view.esperarEnter();
    }

    public void menuCompeticiones() {
        // TODO
    }

    public void cargarCompeticiones() {
        // TODO
    }

    public void listarCompeticiones() {
        // TODO
    }

    public void eliminarCompeticion() {
        // TODO
    }

    public void simularJuegos() {
        // TODO
    }
}
