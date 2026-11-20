package presentation;

import business.Athlete;
import business.Competition;
import business.Delegation;
import business.Performance;

import java.time.format.DateTimeFormatter;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

/**
 * escribe los menus y los textos y lee lo que se teclea
 */
public class View {

    //para leer loq escrib ele usuario
    private Scanner scanner;

    /**
     * crea la view y deja listo el scanner para leer del teclado
     */
    public View() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * pide un numero entre min y max y lo repite hasta que valga
     * @param min el numero mas pequeno que se acepta
     * @param max el numero mas grande que se acepta
     * @return la opcion elegida
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
                //si escribe letras nextInt falla y entra aqui
                mostrarError("Wrong option. Enter a number from " + min + " to " + max + ".");
            } finally {
                //limpia el salto de linea que deja nextInt, si no se colaria en la siguiente lectura
                scanner.nextLine();
            }
        }
    }

    /**
     * escribe un texto en pantalla
     * @param texto lo que se escribe
     */
    public void mostrarMensaje(String texto) {
        System.out.println(texto);
    }

    /**
     * escribe un error en pantalla, con Error: delante
     * @param texto que ha ido mal
     */
    public void mostrarError(String texto) {
        System.out.println("Error: " + texto);
    }

    /**
     * se para hasta que el usuario pulse enter
     */
    public void esperarEnter() {
        System.out.println();
        System.out.print("<Press ENTER to continue>");
        scanner.nextLine();
    }

    /**
     * dibuja el menu principal y pide la opcion
     * @param simulacionDisponible false si no hay competiciones: la 4 sale marcada como no disponible
     * @return la opcion elegida (0 a 4)
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

        //leerOpcion ya se encarga de que sea un numero de 0 a 4
        return leerOpcion(0, 4);
    }

    /**
     * dibuja el submenu de delegaciones y pide la opcion
     * @return la opcion elegida (0 a 2)
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
     * dibuja el submenu de atletas y pide la opcion
     * @return la opcion elegida (0 a 3)
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
     * lista las delegaciones numeradas y pide cual quiere ver
     * @param delegaciones las que se muestran
     * @return la posicion elegida (1 a N), o 0 para volver
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

        return leerOpcion(0, delegaciones.size());
    }

    /**
     * escribe la ficha de una delegacion: codigo, nombre, medallas y sus atletas
     * @param delegacion la delegacion que se muestra
     * @param atletas los atletas que pertenecen a ella
     */
    public void mostrarDelegacion(Delegation delegacion, List<Athlete> atletas) {
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
     * dibuja el medallero como una tabla
     * @param delegaciones las delegaciones ya ordenadas por medallas
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
     * pregunta el nombre del atleta
     * @return lo que escribe el usuario
     */
    public String pedirNombre() {
        mostrarMensaje("");
        System.out.print("Athlete name: ");
        return scanner.nextLine();
    }

    /**
     * pregunta la fecha de nacimiento y la devuelve como texto, sin comprobarla (eso lo hace el manager)
     * @return el texto escrito
     */
    public String pedirFecha() {
        System.out.print("Date of birth (DD/MM/YYYY): ");
        return scanner.nextLine();
    }

    /**
     * lista las delegaciones y deja elegir una
     * @param delegaciones las que se pueden elegir
     * @return la delegacion elegida, o null si cancela
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
        if (option == 0) {
            return null;
        }
        //option-1 pasa de la posicion de pantalla al indice de la lista
        return delegaciones.get(option - 1);
    }

    /**
     * lista los 4 perfiles y pide uno
     * @return 1 Balanced, 2 Power, 3 Endurance, 4 Technical, o 0 si cancela
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
     * pregunta que atleta buscar. si pulsa enter sin escribir, devuelve texto vacio
     * @return el texto escrito
     */
    public String pedirBusqueda() {
        mostrarMensaje("");
        System.out.print("Enter a search term or press ENTER to show all athletes: ");
        return scanner.nextLine();
    }

    /**
     * lista los atletas con el nombre de su delegacion al lado y deja elegir uno
     * @param atletas los atletas que se muestran
     * @param delegaciones las delegaciones, para poner el nombre de la de cada atleta
     * @return el atleta elegido, o null si no hay ninguno o elige volver
     */
    public Athlete elegirAtleta(List<Athlete> atletas, List<Delegation> delegaciones) {
        mostrarMensaje("");
        if (atletas.isEmpty()) {
            mostrarMensaje("No athletes found.");
            return null;
        }

        for (int i = 0; i < atletas.size(); i++) {
            Athlete athlete = atletas.get(i);
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
     * escribe la ficha de un atleta: id, nombre, delegacion, fecha, perfil y atributos
     * @param atleta el atleta que se muestra
     * @param delegacion la delegacion a la que pertenece
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
     * hace una pregunta de si o no (y/n) y la repite hasta que contesten bien
     * @param mensaje la pregunta
     * @return true si contesta y, false si contesta n
     */
    public boolean pedirConfirmacion(String mensaje) {
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
