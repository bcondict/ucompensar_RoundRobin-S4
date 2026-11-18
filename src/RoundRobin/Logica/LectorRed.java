package RoundRobin.Logica;

import RoundRobin.Estructuras.GrafoRed;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Lee la topología de una red desde un archivo de texto y la agrega a un
 * {@link GrafoRed}. El formato esperado es:
 *
 * <pre>
 * TIPO: no-dirigido ponderado
 * ORIGEN: CPU-0
 * CPU-0 CPU-1 5
 * CPU-1 CPU-2 3
 * CPU-2 CPU-3 8
 * CPU-3
 * </pre>
 *
 * Las líneas con tres datos representan aristas; las líneas con un dato
 * representan nodos aislados. Las líneas que comienzan con # o // se ignoran.
 */
public class LectorRed {

    private GrafoRed grafo;
    private String origen;
    private String tipo;

    public LectorRed() {
        this.grafo = null;
        this.origen = null;
        this.tipo = null;
    }

    public LectorRed(GrafoRed grafo) {
        this.grafo = grafo;
        this.origen = null;
        this.tipo = null;
    }

    /**
     * Lee un archivo y agrega sus vértices y aristas al grafo indicado.
     *
     * @param rutaArchivo ruta del archivo de texto
     * @param grafo       grafo al que se agregarán los vértices y aristas
     * @return el mismo grafo recibido
     * @throws IOException si no puede leer el archivo
     */
    public GrafoRed leerArchivo(String rutaArchivo, GrafoRed grafo) throws IOException {
        if (rutaArchivo == null || rutaArchivo.trim().isEmpty()) {
            throw new IllegalArgumentException("Debe indicar la ruta del archivo.");
        }
        if (grafo == null) {
            throw new IllegalArgumentException("El grafo no puede ser nulo.");
        }

        this.grafo = grafo;
        this.origen = null;
        this.tipo = null;
        Path ruta = Paths.get(rutaArchivo.trim());

        try (BufferedReader lector = Files.newBufferedReader(ruta, StandardCharsets.UTF_8)) {
            String linea;
            int numeroLinea = 0;

            while ((linea = lector.readLine()) != null) {
                numeroLinea++;
                linea = linea.trim();

                if (linea.isEmpty() || linea.startsWith("#") || linea.startsWith("//")) {
                    continue;
                }

                int comentario = linea.indexOf('#');
                if (comentario >= 0) {
                    linea = linea.substring(0, comentario).trim();
                }

                if (linea.isEmpty()) {
                    continue;
                }

                if (linea.matches("(?i)^ORIGEN\\s*:.*$")) {
                    String idOrigen = linea.replaceFirst("(?i)^ORIGEN\\s*:\\s*", "").trim();

                    if (idOrigen.isEmpty()) {
                        throw new IllegalArgumentException(
                                "Línea " + numeroLinea + " inválida: falta el identificador del origen.");
                    }

                    origen = idOrigen;
                    grafo.agregarVertice(idOrigen);
                    continue;
                }

                if (linea.matches("(?i)^TIPO\\s*:.*$")) {
                    String tipoRed = linea.replaceFirst("(?i)^TIPO\\s*:\\s*", "").trim();

                    if (tipoRed.isEmpty()) {
                        throw new IllegalArgumentException(
                                "Línea " + numeroLinea + " inválida: falta el tipo de la red.");
                    }

                    tipo = tipoRed;
                    continue;
                }

                linea = linea.replace(',', ' ')
                        .replace(';', ' ')
                        .replace('|', ' ')
                        .replace('\t', ' ');

                String[] partes = linea.trim().split("\\s+");

                if (partes.length == 1) {
                    grafo.agregarVertice(partes[0]);
                    continue;
                }

                if (partes.length != 3) {
                    throw new IllegalArgumentException(
                            "Línea " + numeroLinea + " inválida: se esperaba 'origen destino peso' "
                                    + "o un solo identificador de nodo. Contenido: '" + linea + "'");
                }

                String origenArista = partes[0];
                String destino = partes[1];

                if (origenArista.isEmpty() || destino.isEmpty()) {
                    throw new IllegalArgumentException(
                            "Línea " + numeroLinea + " inválida: origen y destino no pueden estar vacíos.");
                }

                int peso;
                try {
                    peso = Integer.parseInt(partes[2]);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException(
                            "Línea " + numeroLinea + " inválida: el peso debe ser un número entero.", e);
                }

                if (peso < 0) {
                    throw new IllegalArgumentException(
                            "Línea " + numeroLinea + " inválida: el peso no puede ser negativo.");
                }

                grafo.agregarArista(origenArista, destino, peso);
            }
        }

        return grafo;
    }

    /** Sobrecarga con el nombre del archivo primero. */
    public GrafoRed leerArchivo(String rutaArchivo) throws IOException {
        if (grafo == null) {
            grafo = new GrafoRed();
        }
        return leerArchivo(rutaArchivo, grafo);
    }

    public GrafoRed leerArchivo(GrafoRed grafo, String rutaArchivo) throws IOException {
        return leerArchivo(rutaArchivo, grafo);
    }

    public GrafoRed cargar(String rutaArchivo, GrafoRed grafo) throws IOException {
        return leerArchivo(rutaArchivo, grafo);
    }

    public GrafoRed cargar(String rutaArchivo) throws IOException {
        return leerArchivo(rutaArchivo);
    }

    public GrafoRed getGrafo() {
        return grafo;
    }

    /** Retorna el nodo indicado en la línea ORIGEN. */
    public String getOrigen() {
        return origen;
    }

    /** Retorna el tipo de la red indicado en la línea TIPO. */
    public String getTipo() {
        return tipo;
    }
}