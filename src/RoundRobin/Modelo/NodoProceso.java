package RoundRobin.Modelo;

/**
 * Nodo de la lista circular simplemente enlazada del planificador Round Robin.
 *
 * Cada nodo almacena una referencia a un {@link Proceso} y un puntero
 * {@code siguiente}, que apunta al próximo nodo de la cadena. Gracias a que en las listas
 * circulares el último nodo no apunta a {@code null}, sino de regreso al
 * primer nodo, es posible formar un ciclo continuo entre los procesos.
 *
 * La separación entre {@code Proceso} y {@code NodoProceso} se debe a que el proceso representa
 * un dato del dominio del problema (el cual existe independientemente de cualquier estructura),
 * mientras que, a su vez, el nodo representa la infraestructura de la lista. Esto posibilita
 * la reutilización de {@code Proceso} en otras estructuras sin complicaciones.
 *
 * @author
 */
public class NodoProceso {

    /** El proceso almacenado en el nodo. */
    private Proceso proceso;

    /**
     * Puntero al siguiente nodo en la lista circular.
     * En caso de que la lista contuviera un solo elemento, este campo apuntaría al propio nodo.
     * No debe ser {@code null} mientras el nodo esté dentro de la lista.
     */
    private NodoProceso siguiente;

    // Constructor
    /**
     * Construye un nodo que almacena el proceso especificado.
     *
     * Al ser creado, {@code siguiente} queda en {@code null} de forma temporal.
     * Por su parte, la lista circular es responsable de establecer el enlace correcto
     * inmediatamente después de insertar el nodo.
     *
     * @param proceso -> el proceso que este nodo va a contener
     */
    public NodoProceso(Proceso proceso) {
        this.proceso = proceso;
        this.siguiente = null; // la lista asignará este puntero al insertarlo
    }

    // Métodos getters y setters
    /**
     * Retorna el proceso almacenado en el nodo.
     *
     * @return el proceso del nodo
     */
    public Proceso getProceso() {
        return proceso;
    }

    /**
     * Retorna el siguiente nodo en la lista circular.
     *
     * @return referencia al nodo siguiente
     */
    public NodoProceso getSiguiente() {
        return siguiente;
    }

    /**
     * Establece el puntero al siguiente nodo.
     *
     * Este método es invocado exclusivamente por {@code ListaCircular}
     * en el momento en que se insertan o eliminan nodos.
     * Esto permite que el ciclo se mantenga funcionando correctamente.
     *
     * @param siguiente -> el nodo que debe seguir a este en la cadena
     */
    public void setSiguiente(NodoProceso siguiente) {
        this.siguiente = siguiente;
    }
}