package RoundRobin.Modelo;

/**
 * Representa un proceso del sistema operativo dentro del simulador Round Robin.
 *
 * En esta clase, se definen y alojan los datos esenciales de un proceso:
 * identificador, nombre, tiempo total de ejecución requerido y tiempo restante por ejecutar.
 *
 * No contiene lógica de planificación, dado que su único propósito y función
 * consiste en almacenar el estado del proceso en un momento determinado.
 *
 * En el contexto de un sistema operativo real, los procesos que solicitan tiempo de CPU
 * tiene una estructura similar en el kernel, la cual está compuesta por campos
 * de control que el "regulador" consulta y actualiza durante cada ciclo.
 *
 * @author
 */
public class Proceso {

    /** Identificador numérico único del proceso. */
    private int id;

    /** Nombre descriptivo del proceso (por ejemplo: "Navegador web", "Reproductor de música"). */
    private String nombre;

    /**
     * Tiempo total de CPU que el proceso necesita para completarse.
     * Puede medirse en cualquier unidad de tiempo. En este caso, en milisegundos.
     * Este valor no cambia tras ser asignado. Por lo tanto, funge como una referencia histórica.
     */
    private int tiempoTotal;

    /**
     * Tiempo de uso de CPU que le falta al proceso para completarse (nuevamente, en milisegundos).
     * Este valor se reduce en cada quantum ejecutado. Una vez llega a 0,
     * el proceso ha finalizado, razón por la cual debe eliminarse de la lista circular.
     */
    private int tiempoRestante;

    // Constructor
    /**
     * Construye un proceso nuevo. El tiempo restante es igual al tiempo total, a razón de que,
     * al momento de su creación, el proceso aún no ha ejecutado nada. De este modo,
     * {@code tiempoRestante} se inicializa con el mismo valor que {@code tiempoTotal}.
     *
     * @param id -> identificador único del proceso
     * @param nombre -> nombre descriptivo del proceso
     * @param tiempoTotal -> tiempo total de CPU requerido, en milisegundos
     */
    public Proceso(int id, String nombre, int tiempoTotal) {
        this.id = id;
        this.nombre = nombre;
        this.tiempoTotal = tiempoTotal;
        this.tiempoRestante = tiempoTotal; // al inicio, no ha ejecutado nada todavía
    }

    // Métodos getters y setters
    /**
     * Retorna el identificador del proceso.
     *
     * @return id del proceso
     */
    public int getId() {
        return id;
    }

    /**
     * Retorna el nombre del proceso.
     *
     * @return nombre del proceso
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Retorna el tiempo total de CPU que requiere el proceso.
     *
     * @return tiempo total en milisegundos
     */
    public int getTiempoTotal() {
        return tiempoTotal;
    }

    /**
     * Retorna el tiempo restante de CPU que requiere el proceso antes de finalizar.
     *
     * @return tiempo restante en milisegundos
     */
    public int getTiempoRestante() {
        return tiempoRestante;
    }

    /**
     * Actualiza el tiempo faltante del proceso, posterior a la ejecución de un quantum.
     *
     * El planificador invoca a este método después de cada turno.
     * El valor no puede ser negativo bajo ninguna circuntancia.
     * Por ello, el planificador cumple con la tarea de pasar un valor mayor o igual a cero.
     *
     * @param tiempoRestante -> nuevo tiempo restante en milisegundos
     */
    public void setTiempoRestante(int tiempoRestante) {
        this.tiempoRestante = tiempoRestante;
    }

    // Representación textual
    /**
     * Retorna una representación visual del proceso, junto con su estado actual.
     *
     * @return cadena con id, nombre, tiempo total y tiempo restante
     */
    @Override
    public String toString() {
        return String.format("[P%d | %s | Total: %dms | Restante: %dms]",
                id, nombre, tiempoTotal, tiempoRestante);
    }
}