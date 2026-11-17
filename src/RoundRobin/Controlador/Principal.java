package RoundRobin.Controlador;

import RoundRobin.Estructuras.ListaCircular;
import RoundRobin.Logica.PlanificadorRR;
import RoundRobin.Modelo.Proceso;
import java.util.Scanner;

/**
 * Punto de entrada del programa.
 *
 * Esta clase exclusivamente gestiona la interacción con el algoritmo, mediante
 * un menú de consola simple. Por consiguiente, no interviene directamente con
 * la lógica de planificación ni de las estructuras de datos; su único rol es
 * leer entradas, invocar las clases correspondientes y mostrar los resultados.
 *
 * Flujo del programa:
 * - Los procesos son agregados.
 * - Se puede consultar la lista de procesos registrados para confirmar.
 * - Al elegir la opción de iniciar la simulación, el programa solicita ingresar el
 *  quantum. De esta menera, el planificador se ejecuta.
 * - Al finalizar, se muestran las estadisticas y el programa regresa al menú inicial.
 *
 * @author
 */
public class Principal {

    /** Leer los datos ingresados por el usuario durante toda la sesión. */
    private static Scanner scanner = new Scanner(System.in);

    /** Lista circular cuya función es acumular los procesos registrados por el usuario. */
    private static ListaCircular lista = new ListaCircular();

    /** Contador auto-incremental que asigna identificadores únicos a cada proceso. */
    private static int contadorId = 1;

    // Punto de partida
    /**
     * Método principal. Muestra el menú y redirige al usuario, de acuerdo a su elección.
     */
    public static void main(String[] args) {
        System.out.println(" ___________________________________________");
        System.out.println("|            SIMULADOR ROUND ROBIN          |");
        System.out.println("|___________________________________________|");

        int opcion;

        do {
            mostrarMenu();
            opcion = leerEnteroPositivo("Seleccione una opcion: ");

            switch (opcion) {
                case 1:
                    agregarProceso();
                    break;
                case 2:
                    verProcesos();
                    break;
                case 3:
                    iniciarSimulacion();
                    break;
                case 4:
                    System.out.println("\n  Operacion finalizada.\n");
                    break;
                default:
                    System.out.println("\n  La opcion no es valida. Por favor, intente de nuevo.\n");
            }

        } while (opcion != 4);

    }

    // Opciones del menú
    /**
     * Muestra el menu principal en la consola
     */
    private static void mostrarMenu() {
        System.out.println("\n----------------------------------------");
        System.out.println("  MENU PRINCIPAL");
        System.out.println("----------------------------------------");
        System.out.println("  1. Agregar proceso");
        System.out.println("  2. Ver procesos registrados");
        System.out.println("  3. Iniciar simulacion");
        System.out.println("  4. Salir");
        System.out.println("----------------------------------------");
    }

    /**
     * Solicita al usuario los datos de un nuevo proceso y posteriormente lo agrega a la lista.
     *
     * El ID se asigna automáticamente. Por ello, el usuario solamente debe ingresar
     * el nombre del proceso y el tiempo total de CPU que requiere.
     */
    private static void agregarProceso() {
        System.out.println("\n--- AGREGAR PROCESO ---");

        // registrar el nombre del proceso
        System.out.print("  Ingrese el nombre del proceso: ");
        String nombre = scanner.nextLine().trim();

        if (nombre.isEmpty()) {
            System.out.println("  El nombre no puede estar vacio. El proceso no fue agregado.");
            return;
        }

        // registrar el tiempo total con validación
        int tiempoTotal = leerEnteroPositivo("  Tiempo total de CPU (ms): ");

        // crear el proceso y agregarlo a la lista circular
        Proceso proceso = new Proceso(contadorId, nombre, tiempoTotal);
        lista.agregar(proceso);
        contadorId++;

        System.out.println("  Proceso agregado exitosamente: " + proceso);
    }

    /**
     * Muestra todos los procesos registrados actualmente en la lista circular.
     */
    private static void verProcesos() {
        System.out.println("\n--- PROCESOS REGISTRADOS ---");
        lista.mostrar();
    }

    /**
     * Solicita el quantum al usuario e inicia la simulación.
     *
     * Garantiza que haya al menos un proceso registrado antes de ejecutar.
     * Crea un nuevo planificador con la lista actual y el quantum ingresado, y
     * luego reinicia la lista para habilitar una nueva simulación en la misma
     * sesión en caso de ser necesario.
     */
    private static void iniciarSimulacion() {
        System.out.println("\n--- INICIAR SIMULACION ---");

        // confirmar que haya procesos registrados
        if (lista.estaVacia()) {
            System.out.println("  No hay procesos registrados. Debe agregar al menos uno.");
            return;
        }

        // solicitar quantum al usuario
        int quantum = leerEnteroPositivo("  Ingrese el quantum (ms): ");

        // crear y ejecutar el planificador
        PlanificadorRR planificador = new PlanificadorRR(lista, quantum);
        planificador.ejecutar();

        // reiniciar la lista y el contador para una nueva sesión si el usuario lo desea
        lista = new ListaCircular();
        contadorId = 1;
        System.out.println("  Lista reiniciada. Ahora, puede agregar nuevos procesos y empezar una nueva simulacion.");
    }

    // Utilidades de entradas del usuario
    /**
     * Lee un entero positivo. Si el usuario ingresa un valor negativo, cero
     * o no numérico, el programa le pedirá reintentar.
     *
     * Este método centraliza la validación de entradas numéricas con el fin de evitar
     * repetir lógica de manejo de excepciones en cada punto del menú.
     *
     * @param mensaje -> texto que se muestra al usuario antes de leer
     *
     * @return entero positivo ingresado por el usuario
     */
    private static int leerEnteroPositivo(String mensaje) {
        int valor = 0;

        while (valor <= 0) {
            System.out.print(mensaje);
            try {
                valor = Integer.parseInt(scanner.nextLine().trim());
                if (valor <= 0) {
                    System.out.println("  El valor debe ser mayor que cero. Por favor, intente de nuevo.");
                }
            } catch (NumberFormatException e) {
                System.out.println("  La entrada no es valida. Debe Ingresarr un numero entero positivo.");
            }
        }

        return valor;
    }
}