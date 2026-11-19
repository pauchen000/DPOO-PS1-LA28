package presentation;

import business.Athlete;
import business.Competition;
import business.Delegation;
import business.Performance;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class View {

    private Scanner scanner;

    public View() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Pide un número entre min y max (ambos incluidos) y lo repite hasta que sea válido.
     * @param min opción más pequeña permitida
     * @param max opción más grande permitida
     * @return la opción elegida
     */
    public int leerOpcion(int min, int max) {
        while (true) {
            System.out.print("Choose an option: ");
            try {
                int option = scanner.nextInt();
                if (option >= min && option <= max) {
                    return option;
                }
                mostrarError("Wrong option. Enter a number from " + min + " to " + max + ".");
            } catch (InputMismatchException e) {
                mostrarError("Wrong option. Enter a number from " + min + " to " + max + ".");
            } finally {
                scanner.nextLine();
            }
        }
    }

    /**
     * Muestra un mensaje por pantalla.
     * @param texto mensaje a mostrar
     */
    public void mostrarMensaje(String texto) {
        System.out.println(texto);
    }

    /**
     * Muestra un mensaje de error por pantalla.
     * @param texto descripción del error
     */
    public void mostrarError(String texto) {
        System.out.println("Error: " + texto);
    }

    /**
     * Espera a que el usuario pulse ENTER.
     */
    public void esperarEnter() {
        System.out.println();
        System.out.print("<Press ENTER to continue>");
        scanner.nextLine();
    }

    /**
     * Muestra el menú principal y pide una opción.
     * @param simulacionDisponible false si no hay competiciones y no se puede simular
     * @return la opción elegida (0 a 4)
     */
    public int mostrarMenuPrincipal(boolean simulacionDisponible) {
        mostrarMensaje("");
        mostrarMensaje("Main menu:");
        mostrarMensaje("");
        mostrarMensaje("    1) Delegations");
        mostrarMensaje("    2) Athletes");
        mostrarMensaje("    3) Competitions");
        if (simulacionDisponible) {
            mostrarMensaje("    4) Simulate Olympic Games");
        } else {
            mostrarMensaje("    4) Simulate Olympic Games (not available: there are no competitions)");
        }
        mostrarMensaje("");
        mostrarMensaje("    0) Exit");
        mostrarMensaje("");

        return leerOpcion(0, 4);
    }

    /**
     * Muestra el submenú de delegaciones y pide una opción.
     * @return la opción elegida (0 a 2)
     */
    public int mostrarMenuDelegaciones() {
        mostrarMensaje("");
        mostrarMensaje("Delegations submenu:");
        mostrarMensaje("");
        mostrarMensaje("    1) Show list of delegations");
        mostrarMensaje("    2) Show medals by delegation");
        mostrarMensaje("");
        mostrarMensaje("    0) Back");
        mostrarMensaje("");

        return leerOpcion(0, 2);
    }

    /**
     * Muestra el submenú de atletas y pide una opción.
     * @return la opción elegida (0 a 3)
     */
    public int mostrarMenuAtletas() {
        mostrarMensaje("");
        mostrarMensaje("Athletes submenu:");
        mostrarMensaje("");
        mostrarMensaje("    1) Register a new athlete");
        mostrarMensaje("    2) List and search athletes");
        mostrarMensaje("    3) Delete an athlete");
        mostrarMensaje("");
        mostrarMensaje("    0) Back");
        mostrarMensaje("");

        return leerOpcion(0, 3);
    }
}
