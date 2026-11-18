package Presentation;

import Business.Athlete;
import Business.Competition;
import Business.Delegation;
import Business.Results;
import Business.Weights;

import java.time.format.DateTimeFormatter;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import business.Athlete;
import business.Competition;
import business.Delegation;
import business.Performance;

import java.util.List;
import java.util.Scanner;

public class View {

    private Scanner scanner;

    public View() {
        this.scanner = new Scanner(System.in);
    }

    // ---------- Menús ----------

    public int mostrarMenuPrincipal(boolean simulacionDisponible) {
        System.out.println();
        System.out.println("Main menu:");
        System.out.println(" 1) Delegations");
        System.out.println(" 2) Athletes");
        System.out.println(" 3) Competitions");
        if (simulacionDisponible) {
            System.out.println(" 4) Simulate Olympic Games");
        } else {
            System.out.println(" 4) Simulate Olympic Games (not available: there are no competitions)");
        }
        System.out.println(" 0) Exit");
        System.out.print("Choose an option: ");
        return leerOpcion(0, 4);
    }

    public int mostrarMenuCompeticiones() {
        System.out.println();
        System.out.println("Competitions submenu:");
        System.out.println(" 1) Load new competitions");
        System.out.println(" 2) List available competitions");
        System.out.println(" 3) Delete a competition");
        System.out.println(" 0) Back");
        System.out.print("Choose an option: ");
        return leerOpcion(0, 3);
    }

    // ---------- Lectura ----------

    // Igual que askForInteger de Market, pero además comprueba el rango.
    // El texto de la pregunta lo imprime quien llama ("Choose an option: ").
    public int leerOpcion(int min, int max) {
        while (true) {
            try {
                int opcion = scanner.nextInt();
                if (opcion >= min && opcion <= max) {
                    return opcion;
                }
                System.out.print("Error: enter a number between " + min + " and " + max + ": ");
            } catch (InputMismatchException e) {
                System.out.print("Error: that's not a number. Try again: ");
            } finally {
                scanner.nextLine();
            }
        }
    }

    public void esperarEnter() {
        System.out.println("<Press ENTER to continue>");
        scanner.nextLine();
    }

    public void mostrarMensaje(String texto) {
        System.out.println(texto);
    }

    // ---------- Competiciones ----------

    public Competition elegirCompeticion(List<Competition> competiciones) {
        for (int i = 0; i < competiciones.size(); i++) {
            Competition competicion = competiciones.get(i);
            System.out.println(" " + (i + 1) + ") " + competicion.getName() + " - " + competicion.getFormat());
        }
        System.out.println(" 0) Back");
        System.out.print("Choose a competition: ");

        int opcion = leerOpcion(0, competiciones.size());
        if (opcion == 0) {
            return null;
        }
        return competiciones.get(opcion - 1);
    }

    // No espera ENTER: al borrar, después de los detalles va la confirmación.
    // En "listar", el Controller llama a esperarEnter() justo después.
    public void mostrarCompeticion(Competition competicion, List<Athlete> participantes, List<Delegation> delegaciones) {
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        Weights pesos = competicion.getWeights();

        System.out.println();
        System.out.println("Competition details");
        System.out.println("-------------------");
        System.out.println("-| " + competicion.getId() + " | " + competicion.getName() + " |-");
        System.out.println("Format: " + competicion.getFormat());
        System.out.println("Date and time: " + competicion.getDateTime().format(formato));

        System.out.println("Attribute weights");
        System.out.println("-----------------");
        System.out.println("Speed: " + String.format(Locale.US, "%.2f", pesos.getSpeed()));
        System.out.println("Endurance: " + String.format(Locale.US, "%.2f", pesos.getEndurance()));
        System.out.println("Strength: " + String.format(Locale.US, "%.2f", pesos.getStrength()));
        System.out.println("Technique: " + String.format(Locale.US, "%.2f", pesos.getTechnique()));
        System.out.println("Precision: " + String.format(Locale.US, "%.2f", pesos.getPrecision()));

        System.out.println("Competition result");
        System.out.println("------------------");
        Results resultados = competicion.getResults();
        if (resultados == null) {
            System.out.println("This competition has not been simulated yet.");
        } else {
            String[] medallas = {"Gold", "Silver", "Bronze"};
            String[] ids = {resultados.getGold(), resultados.getSilver(), resultados.getBronze()};
            for (int m = 0; m < medallas.length; m++) {
                for (Athlete atleta : participantes) {
                    if (atleta.getId().equals(ids[m])) {
                        String nombreDelegacion = atleta.getDelegationCode();
                        for (Delegation delegacion : delegaciones) {
                            if (delegacion.getCode().equals(atleta.getDelegationCode())) {
                                nombreDelegacion = delegacion.getName();
                            }
                        }
                        System.out.println(" " + medallas[m] + ": " + atleta.getName() + " - " + nombreDelegacion);
                    }
                }
            }
        }

        System.out.println("Participants");
        System.out.println("------------");
        for (int i = 0; i < participantes.size(); i++) {
            Athlete atleta = participantes.get(i);
            String nombreDelegacion = atleta.getDelegationCode();
            for (Delegation delegacion : delegaciones) {
                if (delegacion.getCode().equals(atleta.getDelegationCode())) {
                    nombreDelegacion = delegacion.getName();
                }
            }
            System.out.println(" " + (i + 1) + ") " + atleta.getName() + " - " + nombreDelegacion);
        }
    }
}