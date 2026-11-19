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

    // Menú principal
    private static final int DELEGATIONS = 1;
    private static final int ATHLETES = 2;
    private static final int COMPETITIONS = 3;
    private static final int SIMULATE_GAMES = 4;
    private static final int EXIT = 0;

    // Submenú de delegaciones
    private static final int LIST_DELEGATIONS = 1;
    private static final int SHOW_MEDALS = 2;

    // Submenú de atletas
    private static final int REGISTER_ATHLETE = 1;
    private static final int LIST_ATHLETES = 2;
    private static final int DELETE_ATHLETE = 3;

    // Volver desde cualquier submenú
    private static final int BACK = 0;

    private View view;
    private DelegationManager delegationManager;
    private AthleteManager athleteManager;
    private CompetitionManager competitionManager;

    public Controller(View view, DelegationManager delegationManager,
                      AthleteManager athleteManager, CompetitionManager competitionManager) {
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

        // Solo delegations.json es obligatorio: los otros dos se crean al guardar el primer dato
        if (delegationManager.comprobarFichero()) {
            view.mostrarMensaje("Files OK.");
            view.mostrarMensaje("Starting program...");
            return true;
        }

        view.mostrarError("The delegations.json file can't be accessed.");
        view.mostrarMensaje("Shutting down...");
        return false;
    }

    public void run() {
        int option;

        do {
            boolean simulationAvailable = competitionManager.hayCompeticiones();
            option = view.mostrarMenuPrincipal(simulationAvailable);

            switch (option) {
                case DELEGATIONS -> menuDelegaciones();
                case ATHLETES -> menuAtletas();
                case COMPETITIONS -> menuCompeticiones();
                case SIMULATE_GAMES -> {
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

        do {
            option = view.mostrarMenuDelegaciones();

            switch (option) {
                case LIST_DELEGATIONS -> listarDelegaciones();
                case SHOW_MEDALS -> mostrarMedallero();
                case BACK -> { }
                default -> view.mostrarError("Wrong option. Enter a number from 0 to 2.");
            }
        } while (option != BACK);
    }

    public void listarDelegaciones() {
        List<Delegation> delegations = delegationManager.listarDelegaciones();

        // La View devuelve la posición elegida en la lista (0 = volver)
        int option = view.mostrarListaDelegaciones(delegations);

        if (option != BACK) {
            Delegation delegation = delegations.get(option - 1);
            List<Athlete> athletes = athleteManager.atletasDeDelegacion(delegation.getCode());

            view.mostrarDelegacion(delegation, athletes);
            view.esperarEnter();
        }
    }

    public void mostrarMedallero() {
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
        String name = view.pedirNombre();

        // Se repite hasta que la fecha sea válida
        LocalDate birthDate = athleteManager.validarFechaNacimiento(view.pedirFecha());
        while (birthDate == null) {
            view.mostrarError("The date of birth is not valid. Please enter a valid date using the format DD/MM/YYYY.");
            birthDate = athleteManager.validarFechaNacimiento(view.pedirFecha());
        }

        // La View devuelve null si el usuario cancela
        Delegation delegation = view.elegirDelegacion(delegationManager.listarDelegaciones());
        if (delegation == null) {
            return;
        }

        int profileOption = view.elegirPerfil();
        if (profileOption == BACK) {
            return;
        }
        Profile profile = athleteManager.perfilAtleta(profileOption);

        // El manager devuelve null si ya existe un atleta con ese nombre en la delegación
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
        String search = view.pedirBusqueda();
        List<Athlete> athletes = athleteManager.buscarAtletas(search);
        List<Delegation> delegations = delegationManager.listarDelegaciones();

        // La View devuelve null si el usuario elige volver (0)
        Athlete athlete = view.elegirAtleta(athletes, delegations);

        if (athlete != null) {
            Delegation delegation = delegationManager.buscarPorCodigo(athlete.getDelegationCode());

            view.mostrarAtleta(athlete, delegation);
            view.esperarEnter();
        }
    }

    public void eliminarAtleta() {
        String search = view.pedirBusqueda();
        List<Athlete> athletes = athleteManager.buscarAtletas(search);
        List<Delegation> delegations = delegationManager.listarDelegaciones();

        Athlete athlete = view.elegirAtleta(athletes, delegations);
        if (athlete == null) {
            return;
        }

        boolean confirmed = view.pedirConfirmacion(
                "Are you sure you want to delete " + athlete.getName()
                        + "? This may affect competitions and medals.");

        if (confirmed) {
            // Primero se quita de las competiciones (antes de borrarlo de athletes.json)
            List<Results> changes = competitionManager.quitarAtletaDeCompeticiones(athlete.getId());

            // Por cada competición donde el atleta tenía podio, se restan esas medallas
            // a las delegaciones. Se hace antes de borrarlo, porque hace falta buscar a los medallistas.
            for (Results results : changes) {
                Map<Medal, String> podium = new HashMap<>();
                podium.put(Medal.GOLD, athleteManager.buscarPorId(results.getGold()).getDelegationCode());
                podium.put(Medal.SILVER, athleteManager.buscarPorId(results.getSilver()).getDelegationCode());
                podium.put(Medal.BRONZE, athleteManager.buscarPorId(results.getBronze()).getDelegationCode());

                if (!delegationManager.restarMedallas(podium)) {
                    view.mostrarError("Some medals could not be subtracted because the delegation has no medals.");
                }
            }

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
