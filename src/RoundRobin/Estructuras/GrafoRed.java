package RoundRobin.Estructuras;
import RoundRobin.Modelo.NodoCPU;
import java.util.*;

/**
 * Grafo no dirigido ponderado que modela la red de núcleos de procesamiento.
 *
 * <p>Cada vértice es un {@link NodoCPU}: un núcleo físico con su propia cola
 * circular de procesos. Cada arista representa un canal de comunicación directa
 * entre dos núcleos, con un peso que indica la latencia del enlace en ms.</p>
 * <p>La estructura interna es una <b>lista de adyacencia</b> implementada como
 * {@code HashMap<String, List<Arista>>}, donde la clave es el id del núcleo
 * origen y el valor es la lista de aristas que salen de él. Esta elección
 * es óptima para grafos dispersos (pocos enlaces entre muchos nodos), que es
 * el caso típico de una red de CPUs.</p>
 *
 * <p>Esta clase implementa los requisitos R2 a R7 del proyecto integrador:</p>
 * <ul>
 *   <li>R2 — Clase Grafo propia con lista de adyacencia</li>
 *   <li>R3 — Lectura desde archivo (delegada a {@code LectorRed})</li>
 *   <li>R4 — Recorridos BFS y DFS</li>
 *   <li>R5 — Grados de nodos y verificación de conexidad</li>
 *   <li>R6 — Dijkstra: ruta de menor latencia acumulada</li>
 *   <li>R7 — MST con algoritmo de Prim</li>
 * </ul>
 *
 * @author
 */
public class GrafoRed {

    /**
     * Lista de adyacencia: mapea el id de cada núcleo a la lista de canales
     * que parten de él. Se usa LinkedHashMap para preservar el orden de inserción
     * y obtener una representación visual consistente.
     */
    private LinkedHashMap<String, List<Arista>> adyacencia;

    /**
     * Mapa de vértices: asocia cada id de núcleo con su objeto {@code NodoCPU}.
     * Permite acceder en O(1) al nodo completo (y a su cola de procesos)
     * desde cualquier algoritmo del grafo.
     */
    private LinkedHashMap<String, NodoCPU> vertices;

    // Constructor
    /**
     * Construye un grafo vacío listo para recibir vértices y aristas.
     */
    public GrafoRed() {
        this.adyacencia = new LinkedHashMap<>();
        this.vertices = new LinkedHashMap<>();
    }

    // R2 — Construcción del grafo
    /**
     * Agrega un nuevo nodo CPU al grafo.
     *
     * <p>Si el id ya existe, la operación no hace nada (evita duplicados).
     * El nodo se inicializa con la cola de procesos vacía.</p>
     *
     * @param id identificador del núcleo (ej: "CPU-0")
     */
    public void agregarVertice(String id) {
        if (!vertices.containsKey(id)) {
            vertices.put(id, new NodoCPU(id));
            adyacencia.put(id, new ArrayList<>());
        }
    }

    /**
     * Agrega un canal bidireccional entre dos núcleos con la latencia dada.
     *
     * <p>Al ser el grafo no dirigido, se insertan dos aristas dirigidas:
     * una de {@code origen} a {@code destino} y otra en sentido contrario.
     * Ambas con el mismo peso, ya que la latencia del canal es igual en
     * ambas direcciones.</p>
     *
     * <p>Si alguno de los nodos no existe, se crea automáticamente antes
     * de insertar las aristas.</p>
     *
     * @param origen  id del nodo origen
     * @param destino id del nodo destino
     * @param peso    latencia del canal en milisegundos
     */
    public void agregarArista(String origen, String destino, int peso) {
        // garantizar que ambos vértices existan
        agregarVertice(origen);
        agregarVertice(destino);

        // insertar arista en ambas direcciones (grafo no dirigido)
        adyacencia.get(origen).add(new Arista(destino, peso));
        adyacencia.get(destino).add(new Arista(origen, peso));
    }

    /**
     * Muestra la lista de adyacencia completa del grafo en consola.
     *
     * <p>El formato de salida por nodo es:
     * {@code CPU-0 | grado: 2 -> CPU-1 (2ms), -> CPU-3 (5ms)}</p>
     */
    public void imprimirListaAdyacencia() {
        System.out.println("\n  RED DE CPUs — Lista de adyacencia:");
        System.out.println("  " + "─".repeat(50));
        for (String id : adyacencia.keySet()) {
            List<Arista> aristas = adyacencia.get(id);
            StringBuilder sb = new StringBuilder();
            sb.append("  ").append(id)
                    .append(" | grado: ").append(aristas.size()).append("  ");
            for (Arista a : aristas) {
                sb.append(a).append("  ");
            }
            System.out.println(sb.toString().trim());
        }
        System.out.println("  " + "─".repeat(50));
    }

    /**
     * Muestra la matriz de adyacencia del grafo.
     *
     * <p>Las celdas contienen la latencia del canal si existe un enlace directo,
     * o {@code 0} si no hay conexión directa entre los dos nodos. La diagonal
     * siempre es {@code 0} (un nodo no tiene latencia consigo mismo).</p>
     */
    public void imprimirMatriz() {
        List<String> ids = new ArrayList<>(vertices.keySet());
        int n = ids.size();

        // construir matriz de adyacencia en memoria
        int[][] matriz = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (Arista a : adyacencia.get(ids.get(i))) {
                int j = ids.indexOf(a.getDestino());
                if (j >= 0) matriz[i][j] = a.getPeso();
            }
        }

        // encabezado de columnas
        System.out.println("\n  MATRIZ DE ADYACENCIA (latencias en ms):");
        System.out.print("          ");
        for (String id : ids) {
            System.out.printf("%-9s", id);
        }
        System.out.println();
        System.out.println("  " + "─".repeat(10 + 9 * n));

        // filas de la matriz
        for (int i = 0; i < n; i++) {
            System.out.printf("  %-8s|", ids.get(i));
            for (int j = 0; j < n; j++) {
                System.out.printf("%-9s", matriz[i][j] == 0 ? "  —" : "  " + matriz[i][j] + "ms");
            }
            System.out.println();
        }
        System.out.println("  " + "─".repeat(10 + 9 * n));
    }

    // R4 — Recorridos BFS y DFS
    /**
     * Recorrido en anchura (BFS) desde un nodo origen.
     *
     * <p>BFS explora primero todos los vecinos directos del origen, luego los
     * vecinos de esos vecinos, y así sucesivamente. En el contexto de esta red,
     * BFS responde: <i>"¿Cuántos saltos de red separan el nodo maestro de cada
     * otro núcleo?"</i>. Un núcleo alcanzable en 1 salto es vecino directo;
     * en 2 saltos, vecino de vecino.</p>
     *
     * <p>Implementación: usa una {@code Queue} (cola FIFO). Cada nodo se marca
     * como visitado antes de encolarlo para evitar ciclos.</p>
     *
     * @param origen id del nodo desde el que inicia el recorrido
     */
    public void bfs(String origen) {
        if (!vertices.containsKey(origen)) {
            System.out.println("  Error: el nodo '" + origen + "' no existe en la red.");
            return;
        }

        System.out.println("\n  BFS desde " + origen + " (recorrido por anchura — saltos de red):");

        Set<String> visitados = new HashSet<>();
        Queue<String> cola = new LinkedList<>();
        Map<String, Integer> saltos = new HashMap<>(); // saltos desde el origen

        visitados.add(origen);
        cola.add(origen);
        saltos.put(origen, 0);

        while (!cola.isEmpty()) {
            String actual = cola.poll();
            int nivelActual = saltos.get(actual);

            System.out.printf("  %-10s  (salto %d desde %s)%n", actual, nivelActual, origen);

            for (Arista a : adyacencia.get(actual)) {
                if (!visitados.contains(a.getDestino())) {
                    visitados.add(a.getDestino());
                    cola.add(a.getDestino());
                    saltos.put(a.getDestino(), nivelActual + 1);
                }
            }
        }

        // reportar nodos inalcanzables si los hay
        for (String id : vertices.keySet()) {
            if (!visitados.contains(id)) {
                System.out.printf("  %-10s  *** INALCANZABLE desde %s ***%n", id, origen);
            }
        }
    }

    /**
     * Recorrido en profundidad (DFS) desde un nodo origen.
     *
     * <p>DFS sigue un camino hasta el fondo antes de retroceder y explorar
     * otro. En esta red, DFS responde: <i>"¿Es posible alcanzar todos los
     * núcleos desde el maestro sin importar cuántos saltos tome?"</i>.
     * Si al finalizar quedan nodos no visitados, la red tiene núcleos
     * aislados — un problema de conectividad crítico.</p>
     *
     * <p>Implementación: usa una {@code Stack} (pila LIFO). Se invoca de forma
     * iterativa para evitar desbordamiento de pila en redes grandes.</p>
     *
     * @param origen id del nodo desde el que inicia el recorrido
     */
    public void dfs(String origen) {
        if (!vertices.containsKey(origen)) {
            System.out.println("  Error: el nodo '" + origen + "' no existe en la red.");
            return;
        }

        System.out.println("\n  DFS desde " + origen + " (recorrido por profundidad — alcanzabilidad):");

        Set<String> visitados = new LinkedHashSet<>(); // LinkedHashSet preserva orden de visita
        Stack<String> pila = new Stack<>();

        pila.push(origen);

        while (!pila.isEmpty()) {
            String actual = pila.pop();

            if (!visitados.contains(actual)) {
                visitados.add(actual);
                System.out.println("  Visitado: " + actual);

                // agregar vecinos en orden inverso para mantener consistencia visual
                List<Arista> vecinos = adyacencia.get(actual);
                for (int i = vecinos.size() - 1; i >= 0; i--) {
                    String vecino = vecinos.get(i).getDestino();
                    if (!visitados.contains(vecino)) {
                        pila.push(vecino);
                    }
                }
            }
        }

        // verificar alcanzabilidad completa
        boolean conexo = visitados.size() == vertices.size();
        System.out.println("\n  Nodos alcanzados: " + visitados.size() + " / " + vertices.size());
        if (conexo) {
            System.out.println("  Resultado: todos los nucleos son alcanzables desde " + origen + ".");
        } else {
            System.out.println("  ADVERTENCIA: hay nucleos inalcanzables desde " + origen + ".");
        }
    }

    // R5 — Grados y conexidad
    /**
     * Muestra el grado de cada nodo y verifica el lema del apretón de manos.
     *
     * <p>El grado de un nodo es el número de canales directos que tiene.
     * En esta red, indica cuántos núcleos vecinos puede contactar directamente.
     * Un nodo de grado 1 es un punto de falla: si su único canal cae, queda
     * aislado.</p>
     *
     * <p>El <b>lema del apretón de manos</b> establece que la suma de todos los
     * grados siempre es igual a dos veces el número de aristas. Esto se debe a
     * que cada arista contribuye 1 al grado de cada uno de sus dos extremos.
     * Este lema sirve como verificación de integridad de la estructura del grafo.</p>
     */
    public void mostrarGradosYConexidad() {
        System.out.println("\n  GRADOS DE NODOS:");
        System.out.println("  " + "─".repeat(45));

        int sumaGrados = 0;
        int nodosGrado1 = 0;

        for (String id : adyacencia.keySet()) {
            int grado = adyacencia.get(id).size();
            sumaGrados += grado;
            if (grado == 1) nodosGrado1++;

            String advertencia = (grado == 1) ? "  ⚠ punto de falla unico" : "";
            System.out.printf("  %-10s  grado: %d%s%n", id, grado, advertencia);
        }

        // número de aristas (cada una fue contada dos veces)
        int numAristas = sumaGrados / 2;

        System.out.println("  " + "─".repeat(45));
        System.out.println("  Suma de grados      : " + sumaGrados);
        System.out.println("  Numero de aristas   : " + numAristas);
        System.out.println("  Verificacion lema   : " + sumaGrados + " == 2 × " + numAristas
                + " → " + (sumaGrados == 2 * numAristas ? "CORRECTO" : "ERROR"));
        if (nodosGrado1 > 0) {
            System.out.println("  Nodos de grado 1    : " + nodosGrado1 + " (puntos de falla)");
        }

        
        // Verificar conexidad y contar componentes
        if (vertices.isEmpty()) {

            System.out.println("  La red no tiene vertices.");

        } else {

            boolean conexo = esConexo();
            int cantidadComponentes = contarComponentesConexas();

            System.out.println("  Red conexa          : "
                    + (conexo ? "SI" : "NO"));

            System.out.println("  Componentes conexas : "
                    + cantidadComponentes);
        }

        System.out.println("  " + "─".repeat(45));

    }

    /**
     * Verifica si el grafo es conexo usando BFS interno.
     *
     * <p>El grafo es conexo si desde cualquier nodo se puede alcanzar
     * cualquier otro. Se lanza un BFS desde el primer nodo y se verifica
     * si todos los vértices quedaron visitados.</p>
     *
     * @return {@code true} si la red es completamente conexa
     */
    public boolean esConexo() {
        if (vertices.isEmpty()) return true;

        String origen = vertices.keySet().iterator().next();
        Set<String> visitados = new HashSet<>();
        Queue<String> cola = new LinkedList<>();

        visitados.add(origen);
        cola.add(origen);

        while (!cola.isEmpty()) {
            String actual = cola.poll();
            for (Arista a : adyacencia.get(actual)) {
                if (!visitados.contains(a.getDestino())) {
                    visitados.add(a.getDestino());
                    cola.add(a.getDestino());
                }
            }
        }

        return visitados.size() == vertices.size();
    }


/**
 * Cuenta la cantidad de componentes conexas de la red.
 * Un componente conexo es un grupo de CPUs que pueden comunicarse entre sí.
 *
 * Se utiliza BFS para recorrer cada componente.
 *
 * @return cantidad de componentes conexas.
 */
public int contarComponentesConexas() {

    Set<String> visitados = new HashSet<>();
    int componentes = 0;

    // Revisa todos los vertices de la red
    for (String id : vertices.keySet()) {

        // Si el vertice no ha sido visitado,
        // encontramos un nuevo componente.
        if (!visitados.contains(id)) {

            componentes++;

            Queue<String> cola = new LinkedList<>();

            // Inicia BFS desde este vertice
            visitados.add(id);
            cola.add(id);

            while (!cola.isEmpty()) {

                String actual = cola.poll();

                // Revisa los vecinos del vertice actual
                for (Arista arista : adyacencia.get(actual)) {

                    String vecino = arista.getDestino();

                    if (!visitados.contains(vecino)) {

                        visitados.add(vecino);
                        cola.add(vecino);
                    }
                }
            }
        }
    }

    return componentes;
}


    // R6 — Dijkstra
    /**
     * Calcula la ruta de menor latencia acumulada desde el origen hacia todos
     * los demás nodos, usando el algoritmo de Dijkstra.
     *
     * <p>En esta red, Dijkstra responde: <i>"Si necesito migrar un proceso
     * del nodo maestro a cualquier otro núcleo, ¿cuál es la ruta con menor
     * latencia total de red?"</i>. La latencia total es la suma de las
     * latencias de cada enlace en la ruta elegida.</p>
     *
     * <p>Implementación: usa una {@code PriorityQueue} (min-heap) que ordena
     * los nodos por su distancia acumulada al origen. En cada iteración se
     * extrae el nodo más cercano no procesado y se relajan sus vecinos.</p>
     *
     * <p>Complejidad: O((V + E) log V) con la cola de prioridad.</p>
     *
     * @param origen id del nodo desde el que calcular distancias
     */
    public void dijkstra(String origen) {
        if (!vertices.containsKey(origen)) {
            System.out.println("  Error: el nodo '" + origen + "' no existe.");
            return;
        }

        // distancias acumuladas inicializadas a infinito
        Map<String, Integer> distancia = new HashMap<>();
        // para reconstruir rutas
        Map<String, String> predecesor = new HashMap<>();

        for (String id : vertices.keySet()) {
            distancia.put(id, Integer.MAX_VALUE);
            predecesor.put(id, null);
        }
        distancia.put(origen, 0);

        // cola de prioridad: [distanciaAcumulada, idNodo]
        // Integer[] = {distancia, índice del nodo} — se compara por distancia
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[0]));
        List<String> ids = new ArrayList<>(vertices.keySet());
        pq.offer(new int[]{0, ids.indexOf(origen)});

        Set<String> procesados = new HashSet<>();

        while (!pq.isEmpty()) {
            int[] par = pq.poll();
            String actual = ids.get(par[1]);

            if (procesados.contains(actual)) continue;
            procesados.add(actual);

            for (Arista a : adyacencia.get(actual)) {
                String vecino = a.getDestino();
                int nuevaDist = distancia.get(actual) + a.getPeso();

                // relajación: si encontré un camino más corto, actualizo
                if (nuevaDist < distancia.get(vecino)) {
                    distancia.put(vecino, nuevaDist);
                    predecesor.put(vecino, actual);
                    pq.offer(new int[]{nuevaDist, ids.indexOf(vecino)});
                }
            }
        }

        // mostrar resultados
        System.out.println("\n  DIJKSTRA desde " + origen + " (latencia minima acumulada):");
        System.out.println("  " + "─".repeat(55));

        for (String id : ids) {
            if (id.equals(origen)) continue;

            int dist = distancia.get(id);
            if (dist == Integer.MAX_VALUE) {
                System.out.printf("  %-10s  INALCANZABLE%n", id);
            } else {
                // reconstruir ruta
                List<String> ruta = new ArrayList<>();
                String paso = id;
                while (paso != null) {
                    ruta.add(0, paso);
                    paso = predecesor.get(paso);
                }
                System.out.printf("  %-10s  %3dms   ruta: %s%n",
                        id, dist, String.join(" -> ", ruta));
            }
        }
        System.out.println("  " + "─".repeat(55));
    }

    /**
     * Retorna la distancia mínima entre dos nodos específicos (para migración).
     *
     * <p>Este método es invocado por el {@code PlanificadorRR} cuando el usuario
     * solicita migrar un proceso de un núcleo a otro. Retorna la latencia
     * total de la ruta más corta calculada por Dijkstra.</p>
     *
     * @param origen  nodo de origen del proceso
     * @param destino nodo destino al que se migrará
     * @return latencia total en ms, o {@code Integer.MAX_VALUE} si no hay ruta
     */
    public int obtenerDistanciaMinima(String origen, String destino) {
        if (!vertices.containsKey(origen) || !vertices.containsKey(destino)) {
            return Integer.MAX_VALUE;
        }

        Map<String, Integer> distancia = new HashMap<>();
        for (String id : vertices.keySet()) distancia.put(id, Integer.MAX_VALUE);
        distancia.put(origen, 0);

        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[0]));
        List<String> ids = new ArrayList<>(vertices.keySet());
        pq.offer(new int[]{0, ids.indexOf(origen)});

        Set<String> procesados = new HashSet<>();
        while (!pq.isEmpty()) {
            int[] par = pq.poll();
            String actual = ids.get(par[1]);
            if (procesados.contains(actual)) continue;
            procesados.add(actual);
            if (actual.equals(destino)) break; // ya encontré el destino

            for (Arista a : adyacencia.get(actual)) {
                int nuevaDist = distancia.get(actual) + a.getPeso();
                if (nuevaDist < distancia.get(a.getDestino())) {
                    distancia.put(a.getDestino(), nuevaDist);
                    pq.offer(new int[]{nuevaDist, ids.indexOf(a.getDestino())});
                }
            }
        }
        return distancia.get(destino);
    }

    // R7 — MST con algoritmo de Prim
    /**
     * Calcula el Árbol de Expansión Mínima (MST) usando el algoritmo de Prim.
     *
     * <p>El MST responde: <i>"¿Cuáles son los canales mínimos necesarios para
     * mantener todos los núcleos comunicados, con la menor latencia total
     * posible?"</i>. Es relevante para el diseño de la red: si se quisiera
     * reducir la infraestructura de cables al mínimo sin perder conexidad,
     * el MST dice exactamente cuáles conservar.</p>
     *
     * <p>Prim funciona de forma greedy: comienza con un nodo arbitrario y en
     * cada paso agrega la arista de menor peso que conecte un nodo ya incluido
     * con uno que todavía no lo está. Continúa hasta haber incluido todos
     * los nodos.</p>
     *
     * <p>Complejidad con cola de prioridad: O((V + E) log V).</p>
     *
     * <p>Nota sobre orden topológico: el orden topológico aplica a grafos
     * <b>dirigidos acíclicos (DAGs)</b> para ordenar nodos respetando
     * dependencias (ej: prerrequisitos de materias). Esta red de CPUs es
     * <b>no dirigida</b> y puede tener ciclos, por lo que el orden topológico
     * no aplica. El MST es el algoritmo estructuralmente apropiado para
     * grafos ponderados no dirigidos.</p>
     */
    public void prim() {
        if (vertices.isEmpty()) {
            System.out.println("  La red esta vacia.");
            return;
        }

        System.out.println("\n  MST — ARBOL DE EXPANSION MINIMA (Prim):");
        System.out.println("  " + "─".repeat(50));
        System.out.println("  Interpretacion: canales minimos para mantener");
        System.out.println("  toda la red conectada con menor latencia total.");
        System.out.println();

        Set<String> enMST = new HashSet<>();
        // cola de prioridad: [peso, origen, destino] ordenada por peso
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[0]));
        List<String> ids = new ArrayList<>(vertices.keySet());

        // comenzar desde el primer nodo
        String inicio = ids.get(0);
        enMST.add(inicio);

        // agregar todas las aristas del nodo inicial
        for (Arista a : adyacencia.get(inicio)) {
            pq.offer(new int[]{a.getPeso(), ids.indexOf(inicio), ids.indexOf(a.getDestino())});
        }

        int costoTotal = 0;
        List<String> aristas_mst = new ArrayList<>();

        while (!pq.isEmpty() && enMST.size() < vertices.size()) {
            int[] arista = pq.poll();
            String origenNodo = ids.get(arista[1]);
            String destinoNodo = ids.get(arista[2]);

            // si el destino ya está en el MST, esta arista crearía un ciclo
            if (enMST.contains(destinoNodo)) continue;

            // agregar destino al MST
            enMST.add(destinoNodo);
            costoTotal += arista[0];
            aristas_mst.add(String.format("  %s -- %s  (%dms)", origenNodo, destinoNodo, arista[0]));

            // agregar aristas del nuevo nodo que conecten con nodos fuera del MST
            for (Arista a : adyacencia.get(destinoNodo)) {
                if (!enMST.contains(a.getDestino())) {
                    pq.offer(new int[]{a.getPeso(), ids.indexOf(destinoNodo), ids.indexOf(a.getDestino())});
                }
            }
        }

        // mostrar resultado
        System.out.println("  Canales incluidos en el MST:");
        for (String arista : aristas_mst) {
            System.out.println(arista);
        }
        System.out.println();
        System.out.println("  Latencia total del MST : " + costoTotal + "ms");
        System.out.println("  Numero de canales      : " + aristas_mst.size());

        if (enMST.size() < vertices.size()) {
            System.out.println("  ADVERTENCIA: el grafo no es conexo — el MST no cubre todos los nucleos.");
        }
        System.out.println("  " + "─".repeat(50));
    }

    // Acceso a vértices
    /**
     * Retorna el objeto {@code NodoCPU} asociado a un id.
     *
     * @param id identificador del núcleo
     * @return el NodoCPU, o {@code null} si no existe
     */
    public NodoCPU getNodoCPU(String id) {
        return vertices.get(id);
    }

    /**
     * Retorna el conjunto de ids de todos los núcleos registrados.
     *
     * @return set de identificadores de vértices
     */
    public Set<String> getIds() {
        return vertices.keySet();
    }

    /**
     * Indica si el grafo no tiene ningún nodo registrado.
     *
     * @return {@code true} si el grafo está vacío
     */
    public boolean estaVacio() {
        return vertices.isEmpty();
    }

    /**
     * Verifica si un nodo con el id dado existe en la red.
     *
     * @param id id del nodo a buscar
     * @return {@code true} si el nodo existe
     */
    public boolean existeNodo(String id) {
        return vertices.containsKey(id);
    }
}