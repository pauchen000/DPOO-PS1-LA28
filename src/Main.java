public class Main {

    public static void main(String[] args) {
        Controller controller = new Controller();

        if (controller.comprobarFicheros()) {
            controller.run();
        }
    }
}
