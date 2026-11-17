package RoundRobin.Modelo;

import RoundRobin.Estructuras.ListaCircular;

/**
 * Representa un nodo de procesamiento (núcleo CPU) en la red del planificador.
 *
 * <p>En esta versión 2.0 del planificador, los procesos no flotan en una única
 * cola global — cada núcleo de procesamiento tiene su propia cola circular
 * de procesos asignados. {@code NodoCPU} encapsula esa dualidad: es al mismo
 * tiempo un vértice del {@code GrafoRed} y el propietario de un
 * {@code ListaCircular} sobre la que el {@code PlanificadorRR} opera.</p>
 *
 * <p>El identificador del nodo (ej: {@code "CPU-0"}, {@code "CPU-1"}) sirve
 * como clave en la lista de adyacencia del grafo. Las aristas del grafo
 * referencian estos identificadores para definir la topología de la red.</p>
 *
 * <p>Analogía para la sustentación: si el {@code GrafoRed} es el plano de un
 * datacenter, cada {@code NodoCPU} es un servidor físico en ese plano. El
 * servidor tiene su propio stack de tareas pendientes ({@code ListaCircular})
 * y está conectado a otros servidores a través de los cables ({@code Arista})
 * del plano.</p>
 *
 * @author
 */
public class NodoCPU {

    /** Identificador único del núcleo (ej: "CPU-0", "CPU-1"). Clave en el grafo. */
    private String id;

    /**
     * Cola circular de procesos asignados a este núcleo.
     * El {@code PlanificadorRR} opera sobre esta lista cuando ejecuta
     * un turno Round Robin en este nodo específico.
     */
    private ListaCircular colaProcesos;

    /**
     * Tiempo de CPU total consumido por este núcleo durante la simulación.
     * Se actualiza cada vez que el planificador ejecuta un turno en este nodo.
     * Permite calcular estadísticas de carga por núcleo al finalizar.
     */
    private int tiempoAcumulado;

    // Constructor
    /**
     * Construye un nodo CPU vacío con la cola de procesos inicializada.
     *
     * @param id identificador único del núcleo (ej: "CPU-0")
     */
    public NodoCPU(String id) {
        this.id = id;
        this.colaProcesos = new ListaCircular();
        this.tiempoAcumulado = 0;
    }

    // Operaciones
    /**
     * Agrega un proceso a la cola circular de este núcleo.
     *
     * <p>El proceso entra al final de la cola y será atendido en su turno
     * por el algoritmo Round Robin. Este método permite inyectar procesos
     * de forma dinámica, incluso mientras la simulación está en curso.</p>
     *
     * @param proceso el proceso a encolar en este núcleo
     */
    public void encolarProceso(Proceso proceso) {
        colaProcesos.agregar(proceso);
    }

    /**
     * Incrementa el contador de tiempo acumulado de este núcleo.
     *
     * <p>El planificador invoca este método al finalizar cada turno para
     * registrar cuántos milisegundos de CPU ha consumido este núcleo en total.</p>
     *
     * @param ms milisegundos a sumar al acumulador
     */
    public void sumarTiempo(int ms) {
        this.tiempoAcumulado += ms;
    }

    /**
     * Indica si este núcleo tiene procesos pendientes en su cola.
     *
     * @return {@code true} si la cola de procesos está vacía
     */
    public boolean colaVacia() {
        return colaProcesos.estaVacia();
    }

    // Getters
    /**
     * Retorna el identificador del núcleo.
     *
     * @return id del nodo CPU
     */
    public String getId() {
        return id;
    }

    /**
     * Retorna la lista circular de procesos de este núcleo.
     * El planificador accede a esta lista para ejecutar turnos Round Robin.
     *
     * @return cola circular de procesos
     */
    public ListaCircular getColaProcesos() {
        return colaProcesos;
    }

    /**
     * Retorna el tiempo total de CPU consumido por este núcleo.
     *
     * @return tiempo acumulado en milisegundos
     */
    public int getTiempoAcumulado() {
        return tiempoAcumulado;
    }

    // Representación textual
    /**
     * Retorna una representación del estado actual del núcleo.
     *
     * @return cadena con id, procesos en cola y tiempo acumulado
     */
    @Override
    public String toString() {
        return String.format("[%s | Procesos en cola: %d | CPU time: %dms]",
                id, colaProcesos.getTamanio(), tiempoAcumulado);
    }
}