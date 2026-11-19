package presentation;

import business.Athlete;
import business.Competition;
import business.Delegation;
import business.Performance;

import java.time.format.DateTimeFormatter;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class View {

    //para leer loq escrib ele usuario
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
        //no sale del bucle hasta que el numero este dentro del rango
        while (true) {
            System.out.print("Choose an option: ");
            try {
                int option = scanner.nextInt();
                if (option >= min && option <= max) {
                    return option;
                }
                mostrarError("Wrong option. Enter a number from " + min + " to " + max + ".");
            } catch (InputMismatchException e) {
                //si escribe letras nextInt falla y entra aqui
                mostrarError("Wrong option. Enter a number from " + min + " to " + max + ".");
            } finally {
                //limpia el salto de linea que deja nextInt, si no se colaria en la siguiente lectura
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
        //la única linea que cambia es la 4, segun haya competiciones o no
        if (simulacionDisponible) {
            mostrarMensaje("    4) Simulate Olympic Games");
        } else {
            mostrarMensaje("    4) Simulate Olympic Games (not available: there are no competitions)");
        }
        mostrarMensaje("");
        mostrarMensaje("    0) Exit");
        mostrarMensaje("");

        //leerOpcion ya se encarga de que sea un numero de 0 a 4
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

    /**
     * Muestra las delegaciones numeradas y pide cuál ver.
     * @param delegaciones delegaciones a mostrar
     * @return la posición elegida en la lista (1 a N), o 0 para volver
     */
    public int mostrarListaDelegaciones(List<Delegation> delegaciones) {
        mostrarMensaje("");
        for (int i = 0; i < delegaciones.size(); i++) {
            Delegation delegation = delegaciones.get(i);
            //i+1 porque en pantalla se empieza a contar en 1 y en la lista de java en 0
            mostrarMensaje("    " + (i + 1) + ") " + delegation.getCode() + " " + delegation.getName());
        }
        mostrarMensaje("");
        mostrarMensaje("    0) Back");
        mostrarMensaje("");

        //el rango va de 0 (volver) hasta la ultima delegacion
        return leerOpcion(0, delegaciones.size());
    }

    /**
     * Muestra el detalle de una delegación: código, nombre, medallas y atletas.
     * @param delegacion delegación a mostrar
     * @param atletas atletas que pertenecen a la delegación
     */
    public void mostrarDelegacion(Delegation delegacion, List<Athlete> atletas) {
        //cabecera con el codigo y el nombre, y debajo las tres medallas
        mostrarMensaje("");
        mostrarMensaje("-| " + delegacion.getCode() + " | " + delegacion.getName() + " |-");
        mostrarMensaje("    Gold: " + delegacion.getGoldMedals());
        mostrarMensaje("    Silver: " + delegacion.getSilverMedals());
        mostrarMensaje("    Bronze: " + delegacion.getBronzeMedals());
        mostrarMensaje("");
        mostrarMensaje("    " + atletas.size() + " athletes");
        mostrarMensaje("    ----------");
        //mostrat un atleta por linea
        for (Athlete athlete : atletas) {
            mostrarMensaje("    " + athlete.getName());
        }
    }

    /**
     * Muestra el medallero en forma de tabla.
     * @param delegaciones delegaciones ya ordenadas por medallas
     */
    public void mostrarMedallero(List<Delegation> delegaciones) {
        //linea separadora de la tabla y con el ancho de cada columna
        String line = "+" + "-".repeat(9) + "+" + "-".repeat(52) + "+" + "-".repeat(4) + "+"
                + "-".repeat(6) + "+" + "-".repeat(6) + "+";

        mostrarMensaje("");
        mostrarMensaje(line);
        //%-7s alinea a la izquierda y %4s a la derecha, asi las columnas cuadran
        mostrarMensaje(String.format("| %-7s | %-50s |%4s|%6s|%6s|", "Code", "Delegation", "Gold", "Silver", "Bronze"));
        mostrarMensaje(line);
        for (Delegation delegation : delegaciones) {
            String name = delegation.getName();
            //si el nombre es muy largo se corta para no descuadrar la tabla
            if (name.length() > 50) {
                name = name.substring(0, 47) + "...";
            }
            mostrarMensaje(String.format("| %-7s | %-50s |%4d|%6d|%6d|", delegation.getCode(), name,
                    delegation.getGoldMedals(), delegation.getSilverMedals(), delegation.getBronzeMedals()));
        }
        mostrarMensaje(line);
    }

    /**
     * Pide el nombre del atleta.
     * @return el nombre escrito
     */
    public String pedirNombre() {
        mostrarMensaje("");
        System.out.print("Athlete name: ");
        return scanner.nextLine();
    }

    /**
     * Pide la fecha de nacimiento como texto. No la valida: lo hace el manager.
     * @return el texto escrito
     */
    public String pedirFecha() {
        //pedri la fecha de nacimiento
        System.out.print("Date of birth (DD/MM/YYYY): ");
        return scanner.nextLine();
    }

    /**
     * Muestra las delegaciones y deja elegir una.
     * @param delegaciones delegaciones disponibles
     * @return la delegación elegida, o null si el usuario cancela
     */
    public Delegation elegirDelegacion(List<Delegation> delegaciones) {
        mostrarMensaje("");
        mostrarMensaje("Select the athlete's delegation:");
        mostrarMensaje("");
        for (int i = 0; i < delegaciones.size(); i++) {
            Delegation delegation = delegaciones.get(i);
            mostrarMensaje("    " + (i + 1) + ") " + delegation.getCode() + " " + delegation.getName());
        }
        mostrarMensaje("");
        mostrarMensaje("    0) Cancel");
        mostrarMensaje("");

        int option = leerOpcion(0, delegaciones.size());
        //0 es cancelar, asi que no hay delegacion que devolver
        if (option == 0) {
            return null;
        }
        //option-1 pasa de la posicion de pantalla al indice de la lista
        return delegaciones.get(option - 1);
    }

    /**
     * Muestra los perfiles y pide uno.
     * @return 1 Balanced, 2 Power, 3 Endurance, 4 Technical, o 0 para cancelar
     */
    public int elegirPerfil() {
        mostrarMensaje("");
        mostrarMensaje("Select the athlete's profile:");
        mostrarMensaje("");
        mostrarMensaje("    1) Balanced");
        mostrarMensaje("    2) Power");
        mostrarMensaje("    3) Endurance");
        mostrarMensaje("    4) Technical");
        mostrarMensaje("");
        mostrarMensaje("    0) Cancel");
        mostrarMensaje("");

        //solo devuelve el numero es el manager q lo convierte en perfil
        return leerOpcion(0, 4);
    }

    /**
     * Pide el texto de búsqueda de atletas. ENTER sin escribir devuelve texto vacío.
     * @return el texto escrito
     */
    public String pedirBusqueda() {
        mostrarMensaje("");
        System.out.print("Enter a search term or press ENTER to show all athletes: ");
        //si pulsa enter sin escribir, nextLine devuelve texto vacio
        return scanner.nextLine();
    }

    /**
     * Muestra los atletas con el nombre de su delegación y deja elegir uno.
     * @param atletas atletas a mostrar
     * @param delegaciones delegaciones, para poner el nombre de la delegación de cada atleta
     * @return el atleta elegido, o null si no hay atletas o el usuario elige volver
     */
    public Athlete elegirAtleta(List<Athlete> atletas, List<Delegation> delegaciones) {
        mostrarMensaje("");
        //si la busqueda no ha encontrado hay mensaje de error
        if (atletas.isEmpty()) {
            mostrarMensaje("No athletes found.");
            return null;
        }

        for (int i = 0; i < atletas.size(); i++) {
            Athlete athlete = atletas.get(i);
            //junto a cada atleta se pone el nombre de su delegacion
            mostrarMensaje("    " + (i + 1) + ") " + athlete.getName() + " - "
                    + delegationName(athlete.getDelegationCode(), delegaciones));
        }
        mostrarMensaje("");
        mostrarMensaje("    0) Back");
        mostrarMensaje("");

        int option = leerOpcion(0, atletas.size());
        if (option == 0) {
            return null;
        }
        return atletas.get(option - 1);
    }

    /**
     * Muestra el detalle de un atleta: id, nombre, delegación, fecha, perfil y atributos.
     * @param atleta atleta a mostrar
     * @param delegacion delegación del atleta
     */
    public void mostrarAtleta(Athlete atleta, Delegation delegacion) {
        //pasa de TECHNICAL a solo la primera letra tal cual y el resto en minuscula
        String profile = atleta.getProfile().name().charAt(0)
                + atleta.getProfile().name().substring(1).toLowerCase();

        mostrarMensaje("");
        mostrarMensaje("-| " + atleta.getId() + " | " + atleta.getName() + " |-");
        mostrarMensaje("");
        mostrarMensaje("Delegation: " + delegacion.getCode() + " " + delegacion.getName());
        mostrarMensaje("Date of birth: " + atleta.getBirthDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        mostrarMensaje("Profile: " + profile);
        mostrarMensaje("");
        mostrarMensaje("Attributes");
        mostrarMensaje("----------");
        mostrarMensaje("Speed:      " + atleta.getSpeed());
        mostrarMensaje("Endurance:  " + atleta.getEndurance());
        mostrarMensaje("Strength:   " + atleta.getStrength());
        mostrarMensaje("Technique:  " + atleta.getTechnique());
        mostrarMensaje("Precision:  " + atleta.getPrecision());
    }

    /**
     * Pide una confirmación al usuario (y/n) y repite hasta que responda una de las dos.
     * @param mensaje pregunta a mostrar
     * @return true si responde y, false si responde n
     */
    public boolean pedirConfirmacion(String mensaje) {
        //no sale del bucle hasta que conteste y o n
        while (true) {
            mostrarMensaje("");
            System.out.print(mensaje + " (y/n): ");
            String answer = scanner.nextLine().trim();

            if (answer.equalsIgnoreCase("y")) {
                return true;
            }
            if (answer.equalsIgnoreCase("n")) {
                return false;
            }
            mostrarError("Please answer y or n.");
        }
    }

    //busca el nombre de una delegacion a partir de su codigo
    private String delegationName(String code, List<Delegation> delegaciones) {
        for (Delegation delegation : delegaciones) {
            if (delegation.getCode().equals(code)) {
                return delegation.getName();
            }
        }
        //si no la encuentra devuelve el propio codigo
        return code;
    }
}
