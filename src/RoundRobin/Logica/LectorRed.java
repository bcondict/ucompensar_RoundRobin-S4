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
 * {@link GrafoRed}. Cada línea de conexión debe tener el formato:
 * 
 * <pre>
 * origen,destino,peso
 * </pre>
 * 
 * También se aceptan líneas separadas por espacios, punto y coma o tabuladores.
 * Las líneas que comienzan con # o // y las líneas vacías se ignoran.
 */
public class LectorRed {

    private GrafoRed grafo;

    public LectorRed() {
        this.grafo = null;
    }

    public LectorRed(GrafoRed grafo) {
        this.grafo = grafo;
    }

    /**
     * Lee un archivo y agrega sus aristas al grafo indicado.
     *
     * @param rutaArchivo ruta del archivo de texto
     * @param grafo       grafo al que se agregaran las aristas
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

                // Permite comentarios al final de una línea.
                int comentario = linea.indexOf('#');
                if (comentario >= 0) {
                    linea = linea.substring(0, comentario).trim();
                }

                // Sustituir separadores comunes por espacios.
                linea = linea.replace(',', ' ')
                        .replace(';', ' ')
                        .replace('|', ' ')
                        .replace('\t', ' ');

                if (linea.isEmpty()) {
                    continue;
                }

                String[] partes = linea.trim().split("\\s+");

                if (partes.length != 3) {
                    throw new IllegalArgumentException(
                            "Línea " + numeroLinea + " inválida: se esperaba origen destino peso. "
                                    + "Contenido: '" + linea + "'");
                }

                String origen = partes[0];
                String destino = partes[1];

                if (origen.isEmpty() || destino.isEmpty()) {
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

                grafo.agregarArista(origen, destino, peso);
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

    /**
     * Sobrecarga alternativa útil si se desea mantener esta clase ligada a un
     * grafo.
     */
    public GrafoRed leerArchivo(GrafoRed grafo, String rutaArchivo) throws IOException {
        return leerArchivo(rutaArchivo, grafo);
    }

    /** Nombre corto para cargar el archivo. */
    public GrafoRed cargar(String rutaArchivo, GrafoRed grafo) throws IOException {
        return leerArchivo(rutaArchivo, grafo);
    }

    public GrafoRed cargar(String rutaArchivo) throws IOException {
        return leerArchivo(rutaArchivo);
    }

    public GrafoRed getGrafo() {
        return grafo;
    }
}
