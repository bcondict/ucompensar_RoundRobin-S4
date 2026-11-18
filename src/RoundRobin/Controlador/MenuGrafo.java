
package RoundRobin.Controlador;

import RoundRobin.Estructuras.GrafoRed;
import java.util.Scanner;

/**
 * Menú para ejecutar los algoritmos del grafo.
 */
public class MenuGrafo {

    private Scanner scanner;
    private GrafoRed grafo;
    private String origen;

    public MenuGrafo(GrafoRed grafo, Scanner scanner, String origen) {
        this.grafo = grafo;
        this.scanner = scanner;
        this.origen = origen;
    }

    /**
     * Menú de opciones
     */
    public void mostrarMenu() {

        int opcion;

        do {
            System.out.println("       MENU DE LA RED (estructuras de datos)");
            System.out.println("================================");
            System.out.println("Origen configurado: " + origen);
            System.out.println("--------------------------------");
            System.out.println("1. Ver matriz de adyacencia");
            System.out.println("2. Ver grados y conexidad");
            System.out.println("3. Recorrido BFS");
            System.out.println("4. Recorrido DFS");
            System.out.println("5. Dijkstra");
            System.out.println("6. Arbol de expansion minima (Prim)");
            System.out.println("7. Regresar al menu principal");
            System.out.println("================================");

            System.out.print("Seleccione una opcion: ");

            try {
                opcion = Integer.parseInt(scanner.nextLine());

                switch (opcion) {

                    case 1:
                        grafo.imprimirMatriz();
                        break;

                    case 2:
                        grafo.mostrarGradosYConexidad();
                        break;

                    case 3:
                        ejecutarBFS();
                        break;

                    case 4:
                        ejecutarDFS();
                        break;

                    case 5:
                        ejecutarDijkstra();
                        break;

                    case 6:
                        grafo.prim();
                        break;

                    case 7:
                        System.out.println("Regresando al menu principal...");
                        break;

                    default:
                        System.out.println("Opcion no valida.");
                }

            } catch (NumberFormatException e) {
                opcion = 0;
                System.out.println("Debe ingresar un numero.");
            }

        } while (opcion != 7);
    }

    private void ejecutarBFS() {

        System.out.println("\n << RECORRIDO BFS >>");
        System.out.println("CPU de origen: " + origen);

        grafo.bfs(origen);
    }

    private void ejecutarDFS() {

        System.out.println("\n << RECORRIDO DFS >>");
        System.out.println("CPU de origen: " + origen);

        grafo.dfs(origen);
    }

    private void ejecutarDijkstra() {

        System.out.println("\n << ALGORITMO DIJKSTRA >>");
        System.out.println("CPU de origen: " + origen);

        grafo.dijkstra(origen);
    }
}
