
package RoundRobin.Controlador;

import RoundRobin.Estructuras.GrafoRed;
import RoundRobin.Estructuras.ListaCircular;
import RoundRobin.Logica.LectorRed;
import RoundRobin.Logica.PlanificadorRR;
import RoundRobin.Modelo.Proceso;
import java.io.IOException;
import java.util.Scanner;

/**
 * Punto de entrada del programa.
 *
 * Esta clase gestiona la interacción con el usuario mediante
 * un menú de consola.
 *
 * Permite:
 * - Agregar procesos.
 * - Consultar los procesos registrados.
 * - Ejecutar la simulación Round Robin.
 * - Acceder al menú de algoritmos del grafo.
 * - Salir del programa.
 *
 * @author
 */
public class Principal {

    /** Scanner para leer los datos ingresados por el usuario. */
    private static Scanner scanner = new Scanner(System.in);

    /** Lista circular de procesos registrados. */
    private static ListaCircular lista = new ListaCircular();

    /** Contador automático de identificadores de procesos. */
    private static int contadorId = 1;

    /**
     * Método principal que inicia el programa.
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
                    abrirMenuGrafo();
                    break;

                case 5:
                    System.out.println("\n  Operacion finalizada.\n");
                    break;

                default:
                    System.out.println(
                        "\n  La opcion no es valida. Por favor, intente de nuevo.\n"
                    );
            }

        } while (opcion != 5);

    }

    /**
     * Muestra las opciones del menú principal.
     */
    private static void mostrarMenu() {

        System.out.println("\n----------------------------------------");
        System.out.println("  MENU PRINCIPAL");
        System.out.println("----------------------------------------");
        System.out.println("  1. Agregar proceso");
        System.out.println("  2. Ver procesos registrados");
        System.out.println("  3. Iniciar simulacion");
        System.out.println("  4. Menu de la red de CPUs");
        System.out.println("  5. Salir");
        System.out.println("----------------------------------------");
    }

    /**
     * Solicita los datos de un nuevo proceso y lo agrega
     * a la lista circular.
     */
    private static void agregarProceso() {

        System.out.println("\n--- AGREGAR PROCESO ---");

        System.out.print("  Ingrese el nombre del proceso: ");
        String nombre = scanner.nextLine().trim();

        if (nombre.isEmpty()) {

            System.out.println(
                "  El nombre no puede estar vacio. El proceso no fue agregado."
            );

            return;
        }

        int tiempoTotal = leerEnteroPositivo(
            "  Tiempo total de CPU (ms): "
        );

        Proceso proceso = new Proceso(
            contadorId,
            nombre,
            tiempoTotal
        );

        lista.agregar(proceso);
        contadorId++;

        System.out.println(
            "  Proceso agregado exitosamente: " + proceso
        );
    }

    /**
     * Muestra los procesos registrados actualmente.
     */
    private static void verProcesos() {

        System.out.println("\n--- PROCESOS REGISTRADOS ---");

        lista.mostrar();
    }

    /**
     * Inicia la simulación Round Robin.
     */
    private static void iniciarSimulacion() {

        System.out.println("\n--- INICIAR SIMULACION ---");

        if (lista.estaVacia()) {

            System.out.println(
                "  No hay procesos registrados. Debe agregar al menos uno."
            );

            return;
        }

        int quantum = leerEnteroPositivo(
            "  Ingrese el quantum (ms): "
        );

        PlanificadorRR planificador = new PlanificadorRR(
            lista,
            quantum
        );

        planificador.ejecutar();

        // Reiniciar la lista después de finalizar la simulación.
        lista = new ListaCircular();
        contadorId = 1;

        System.out.println(
            "  Lista reiniciada. Ahora puede agregar nuevos procesos."
        );
    }

    /**
     * Abre el menú de algoritmos del grafo.
     *
     * TEMPORALMENTE utiliza una red de prueba creada desde código.
     *
     * Cuando se implemente LectorRed.java, este método se modificará
     * para cargar los vértices y conexiones desde el archivo TXT.
     */
        private static void abrirMenuGrafo() {
        System.out.println("\n--- RED DE CPUs ---");

        GrafoRed grafo = new GrafoRed();
        LectorRed lector = new LectorRed();

        try {
            lector.leerArchivo("red.txt", grafo);

            if (grafo.estaVacio()) {
                System.out.println("El archivo no contiene conexiones.");
                return;
            }

            System.out.println("\nRed cargada correctamente:");
            grafo.imprimirListaAdyacencia();

            String origen = lector.getOrigen();

            if (origen == null || origen.trim().isEmpty()) {
                System.out.println("El archivo no define un origen.");
                return;
            }

            if (!grafo.existeNodo(origen)) {
                System.out.println("El origen indicado no existe en el grafo.");
                return;
            }

            MenuGrafo menu = new MenuGrafo(grafo, scanner, origen);
            menu.mostrarMenu();

        } catch (IOException | IllegalArgumentException e) {
            System.out.println("No fue posible cargar la red: " + e.getMessage());
        }
    }

    /**
     * Lee un número entero positivo.
     *
     * Si el usuario ingresa un valor incorrecto,
     * solicita nuevamente la entrada.
     *
     * @param mensaje texto que se muestra al usuario.
     * @return número entero positivo.
     */
    private static int leerEnteroPositivo(String mensaje) {

        int valor = 0;

        while (valor <= 0) {

            System.out.print(mensaje);

            try {

                valor = Integer.parseInt(
                    scanner.nextLine().trim()
                );

                if (valor <= 0) {

                    System.out.println(
                        "  El valor debe ser mayor que cero. Intente nuevamente."
                    );
                }

            } catch (NumberFormatException e) {

                System.out.println(
                    "  La entrada no es valida. Debe ingresar un numero entero positivo."
                );

            }
        }

        return valor;
    }

}
