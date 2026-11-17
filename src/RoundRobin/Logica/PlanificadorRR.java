package RoundRobin.Logica;

import RoundRobin.Estructuras.ListaCircular;
import RoundRobin.Modelo.NodoProceso;
import RoundRobin.Modelo.Proceso;
import java.util.ArrayList;

/**
 * Motor del algoritmo de planificación Round Robin.
 *
 * Se encarga de gestionar el ciclo de ejecución de los procesos, usando la
 * {@link ListaCircular} como estructura principal. En cada turno, el proceso
 * en la cabeza de la lista recibe una porcion de tiempo de CPU denominada
 * quantum. Si el proceso termina, se elimina; si no es así, regresa al final de la cola
 * automaticamente y vuelve a esperar su turno.
 *
 * El funcionamiento del puntero {@code anterior} consiste en lo siguiente:
 * En una lista simplemente enlazada, si se quiere eliminar un nodo, se necesita
 * el nodo previo para que el enlace pueda ser reparado. Como consecuencia
 * de esto, el planificador mantiene siempre un puntero {@code anterior},
 * que precede al proceso en turno. En una lista circular, {@code anterior} empieza en {@code cola}
 * debido a que {@code cola.getSiguiente()} es la cabeza (es decir, el primer proceso
 * en ser asignado el uso de la CPU).
 *
 * @author
 */
public class PlanificadorRR {

    /** Lista circular que contiene los procesos activos en espera de CPU. */
    private ListaCircular lista;

    /** Quantum de tiempo asignado a cada proceso por turno, en milisegundos. */
    private int quantum;

    /** Acumulador del tiempo total transcurrido durante la ejecución. */
    private int tiempoTotal;

    /**
     * Lista que registra el orden en que los procesos finalizaron.
     * Se hace uso de un ArrayList con el propósito de crear un historial de resultados.
     */
    private ArrayList<String> ordenFinalizacion;

    // Constructor
    /**
     * Construye el planificador con la lista de procesos y el quantum definidos.
     *
     * @param lista -> lista circular con los procesos a planificar
     * @param quantum -> tiempo de CPU asignado por turno, en milisegundos
     */
    public PlanificadorRR(ListaCircular lista, int quantum) {
        this.lista = lista;
        this.quantum = quantum;
        this.tiempoTotal = 0;
        this.ordenFinalizacion = new ArrayList<>();
    }

    // Simulación
    /**
     * Ejecuta la simulación completa del algoritmo de planificación Round Robin.
     *
     * El ciclo continúa siempre y cuando existan procesos en la lista circular.
     * En cada iteración se atiende el proceso que está en la cabeza de la lista,
     * descontando el tiempo ejecutado (el quantum, o lo que le reste),
     * y si su tiempo restante equivale a cero, es eliminado del ciclo.
     *
     * El puntero {@code anterior} es muy importante: dado que en las listas simplemente
     * enlazadas no se puede retroceder, siempre se mantiene una referencia al nodo previo
     * con el fin de poder eliminar el actual sin romper la cadena circular.
     */
    public void ejecutar() {
        System.out.println("\n_________________________________________________");
        System.out.println(" | LA SIMULACION ROUND ROBIN HA SIDO INICIALIZADA! | ");
        System.out.println(" |            Quantum: " + quantum + "ms"       + "|"  );
        System.out.println(" |_________________________________________________|\n");

        int numeroCiclo = 1; // contador de turnos

        /*
         * anterior: apunta al nodo previo al que está en turno actualmente.
         * Empieza en cola porque cola.getSiguiente() = cabeza = primer turno.
         * En el momento en que la lista rota, anterior avanza junto con el proceso actual.
         */
        NodoProceso anterior = lista.getCola();

        while (!lista.estaVacia()) {

            // el proceso en turno es siempre el siguiente del anterior
            NodoProceso actual = anterior.getSiguiente();
            Proceso proceso = actual.getProceso();

            // calcular cuánto tiempo se ejecutará en este turno
            /* si el restante es menor que el quantum, solo se ejecuta lo que queda.
               No es necesario ejecutar el quantum en su totalidad.
             */
            int tiempoEjecutado = Math.min(quantum, proceso.getTiempoRestante());

            // mostrar el estado del turno
            System.out.printf("  Turno %-3d | %-15s | Restante antes: %3dms | Ejecuta: %3dms",
                    numeroCiclo,
                    "P" + proceso.getId() + "-" + proceso.getNombre(),
                    proceso.getTiempoRestante(),
                    tiempoEjecutado);

            // descontar el tiempo ejecutado del proceso
            proceso.setTiempoRestante(proceso.getTiempoRestante() - tiempoEjecutado);

            // acumular al tiempo total de la simulación
            tiempoTotal += tiempoEjecutado;

            if (proceso.getTiempoRestante() == 0) {
                // el proceso terminó. Entonces, se elimina de la lista circular
                System.out.printf(" | Restante: %3dms  --> FINALIZADO ✓%n",
                        proceso.getTiempoRestante());

                // registrar en el historial de finalización
                ordenFinalizacion.add("P" + proceso.getId() + " (" + proceso.getNombre() + ")");

                /*
                 * Eliminar el nodo actual (anterior.getSiguiente()).
                 * Después de la eliminación, anterior.getSiguiente() ya apunta
                 * al nuevo siguiente, asi que anterior no avanza esta vez.
                 * Si la lista queda vacía, el bucle while termina.
                 */
                lista.eliminarSiguiente(anterior);

                // si aún quedan procesos, se actualiza la cola en caso de haber cambiado al eliminar
                if (!lista.estaVacia()) {
                    // anterior sigue siendo válido. Por tal motivo, no se mueve
                }

            } else {
                // el proceso no terminó. Por ello, rota al final de la cola naturalmente
                // de este modo, simplemente avanza el puntero anterior
                System.out.printf(" | Restante: %3dms%n", proceso.getTiempoRestante());

                /*
                 * Avanzar anterior: ahora, anterior apunta al nodo que acaba
                 * de ejecutarse (el cual sigue en la lista), preparado para que en
                 * el próximo turno anterior.getSiguiente() sea el siguiente proceso en usar la CPU.
                 */
                anterior = actual;
            }

            numeroCiclo++;
        }

        // mostrar estadísticas al finalizar todos los procesos
        mostrarEstadisticas();
    }

    // Estadísticas
    /**
     * Muestra los resultados finales del procedimiento.
     *
     * Incluye el tiempo total de ejecución y el orden en que cada proceso finalizó.
     */
    private void mostrarEstadisticas() {
        System.out.println("\n_______________________________________");
        System.out.println(" |           ESTADISTICAS FINALES        |   ");
        System.out.println(" |_______________________________________|");
        System.out.println("  Tiempo total de ejecucion: " + tiempoTotal + "ms");
        System.out.println("  Orden de finalizacion:");

        for (int i = 0; i < ordenFinalizacion.size(); i++) {
            System.out.printf("    %d. %s%n", i + 1, ordenFinalizacion.get(i));
        }

        System.out.println("------------------------------------------\n");
    }

    /**
     * Retorna el tiempo total acumulado durante la simulación.
     *
     * @return tiempo total en milisegundos
     */
    public int getTiempoTotal() {
        return tiempoTotal;
    }

    /**
     * Retorna la lista con el orden de finalización de los procesos.
     *
     * @return lista de nombres en orden de finalización
     */
    public ArrayList<String> getOrdenFinalizacion() {
        return ordenFinalizacion;
    }
}