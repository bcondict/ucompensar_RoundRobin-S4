package RoundRobin.Estructuras;

/**
 * Representa una arista en la lista de adyacencia del grafo de red de CPUs.
 *
 * <p>En el contexto de este planificador, una arista modela un canal de
 * comunicación directa entre dos nodos de procesamiento (CPUs). El peso
 * representa la latencia de ese canal en milisegundos: cuánto tarda una
 * señal (o una migración de proceso) en ir de un núcleo al otro.</p>
 *
 * <p>Esta clase es el elemento atómico de la lista de adyacencia almacenada
 * en {@code GrafoRed}. Cada entrada en esa lista es un {@code ArrayList<Arista>},
 * donde cada {@code Arista} apunta al destino y lleva el costo del canal.</p>
 *
 * <p>Ejemplo: si CPU-0 tiene en su lista la arista {@code (destino="CPU-1", peso=2)},
 * significa que existe un canal directo entre CPU-0 y CPU-1 con 2ms de latencia.</p>
 *
 * @author
 */
public class Arista {

    /** Identificador del nodo de destino de este canal (ej: "CPU-1"). */
    private String destino;

    /**
     * Latencia del canal de comunicación en milisegundos.
     * Representa el costo de usar este enlace para migrar un proceso
     * o para transmitir una señal entre los dos núcleos.
     */
    private int peso;

    // Constructor
    /**
     * Construye una arista hacia el nodo destino con el peso (latencia) indicado.
     *
     * @param destino identificador del nodo CPU de destino
     * @param peso    latencia del canal en milisegundos
     */
    public Arista(String destino, int peso) {
        this.destino = destino;
        this.peso = peso;
    }

    // Getters
    /**
     * Retorna el identificador del nodo de destino.
     *
     * @return nombre del nodo CPU de destino
     */
    public String getDestino() {
        return destino;
    }

    /**
     * Retorna la latencia del canal en milisegundos.
     *
     * @return peso de la arista
     */
    public int getPeso() {
        return peso;
    }

    // Representación textual
    /**
     * Retorna una representación legible de la arista.
     *
     * @return cadena con destino y latencia
     */
    @Override
    public String toString() {
        return String.format("-> %s (%dms)", destino, peso);
    }
}
