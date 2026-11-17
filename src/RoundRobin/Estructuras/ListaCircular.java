package RoundRobin.Estructuras;

import RoundRobin.Modelo.NodoProceso;
import RoundRobin.Modelo.Proceso;

/**
 * Lista circular simplemente enlazada para gestionar la cola de
 * procesos del planificador Round Robin.
 *
 * Visto y considerando la principal propiedad de las listas circulares simplemente enlazadas
 * (el último nodo no apunta a {@code null} sino de regreso al primer nodo, formando un bucle),
 * es absolutamente necesario incluir una, debido a que los procesos deben rotarse
 * indefinidamente hasta que todos terminen.
 *
 * La lista es gestionada por medio de un único puntero {@code cola}, que apunta
 * siempre al último nodo insertado. De esta manera, es posible acceder tanto al último
 * nodo ({@code cola}), como al primero ({@code cola.getSiguiente()}),
 * sin necesidad de recorrer la lista.
 *
 * @author
 */
public class ListaCircular {

    /**
     * Puntero al último nodo de la lista circular.
     * A través de {@code cola.getSiguiente()}, se accede al primer nodo (cabeza),
     * que es {@code null} cuando la lista está vacía.
     */
    private NodoProceso cola;

    /** Cantidad de procesos actualmente en la lista. */
    private int tamanio;

    // Constructor
    /**
     * Construye una lista circular vacía.
     */
    public ListaCircular() {
        this.cola = null;
        this.tamanio = 0;
    }

    // Operaciones de la lista
    /**
     * Agrega un nuevo proceso al final de la lista circular.
     *
     * Primero, el nuevo nodo se inserta entre {@code cola} y {@code cola.getSiguiente()}.
     * Posteriormente, {@code cola} avanza al nuevo nodo, preservando el ciclo y
     * garantizando que el nuevo proceso quede al final de la cola de atención.
     *
     * Casos posibles:
     * - La lista está vacía: el nodo apunta a sí mismo (es cabeza y cola a la vez).
     * - La lista contiene elementos: el nodo es enlazado al final del ciclo.
     *
     * @param proceso -> el proceso a encolar
     */
    public void agregar(Proceso proceso) {
        NodoProceso nuevo = new NodoProceso(proceso);

        if (cola == null) {
            // lista vacía: el único nodo forma un ciclo consigo mismo
            nuevo.setSiguiente(nuevo);
            cola = nuevo;
        } else {
            // enlazar el nuevo nodo entre cola y la cabeza actual
            nuevo.setSiguiente(cola.getSiguiente()); // nuevo nodo apunta a cabeza
            cola.setSiguiente(nuevo);                // cola apunta al nuevo nodo
            cola = nuevo;                            // cola avanza al nuevo nodo
        }

        tamanio++;
    }

    /**
     * Elimina el nodo que sigue al nodo dado (es decir, el nodo actual en turno).
     *
     * Funciona del siguiente modo: el planificador mantiene un puntero {@code anterior},
     * que siempre apunta al nodo previo al que está en ejecución.
     * Cuando un proceso termina, este método desconecta el nodo siguiente de {@code anterior}
     * del ciclo, reparando así el enlace, y manteniendo la circularidad.
     *
     * Casos especiales:
     * - Si es el único nodo, la lista queda vacía ({@code cola = null}).
     * - Si el nodo a eliminar es {@code cola}, {@code cola} retrocede a {@code anterior}.
     *
     * @param anterior -> nodo previo al que se va a eliminar
     */
    public void eliminarSiguiente(NodoProceso anterior) {
        // el nodo a eliminar es el que sigue al anterior
        NodoProceso aEliminar = anterior.getSiguiente();

        if (aEliminar == anterior) {
            // único nodo en la lista
            cola = null;
        } else {
            // omitir el nodo a eliminar conectando anterior con el siguiente del eliminado
            anterior.setSiguiente(aEliminar.getSiguiente());

            // actualizar la referencia en caso de que el nodo eliminado fuese la cola
            if (aEliminar == cola) {
                cola = anterior;
            }
        }

        tamanio--;
    }

    /**
     * Retorna el nodo cabeza (el primero en ser otorgado el uso de la CPU).
     *
     * Bajo este panorama, la cabeza siempre es {@code cola.getSiguiente()}.
     * Si la lista está vacía, retorna {@code null}.
     *
     * @return nodo cabeza, o {@code null} si la lista está vacía
     */
    public NodoProceso getCabeza() {
        if (cola == null) return null;
        return cola.getSiguiente();
    }

    /**
     * Retorna el nodo cola (el último proceso en la lista circular).
     *
     * @return nodo cola, o {@code null} si la lista está vacía
     */
    public NodoProceso getCola() {
        return cola;
    }

    /**
     * Indica si la lista está vacía.
     *
     * @return {@code true} si no hay procesos esperando en la lista
     */
    public boolean estaVacia() {
        return cola == null;
    }

    /**
     * Retorna la cantidad de procesos que están actualmente en la lista.
     *
     * @return número de nodos en la lista circular
     */
    public int getTamanio() {
        return tamanio;
    }

    /**
     * Muestra todos los procesos de la lista circular en la consola,
     * comenzando desde la cabeza y realizando un solo recorrido completo.
     *
     * Si la lista está vacía, se informa. Este método es de utilidad
     * para conocer el estado de la cola antes de comenzar con la ejecución.
     */
    public void mostrar() {
        if (estaVacia()) {
            System.out.println("  La lista de procesos esta vacia.");
            return;
        }

        NodoProceso actual = cola.getSiguiente(); // empezar desde la cabeza

        System.out.println("  Procesos en cola (orden de atencion):");
        int posicion = 1;

        do {
            // mostrar cada proceso y su posición en la cola
            System.out.printf("  %d. %s%n", posicion, actual.getProceso().toString());
            actual = actual.getSiguiente();
            posicion++;
        } while (actual != cola.getSiguiente()); // parar una vez se complete el ciclo
    }
}