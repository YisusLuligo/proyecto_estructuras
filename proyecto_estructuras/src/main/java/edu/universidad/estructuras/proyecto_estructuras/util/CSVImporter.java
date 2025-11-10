package edu.universidad.estructuras.proyecto_estructuras.util;

import edu.universidad.estructuras.proyecto_estructuras.model.Cancion;
import java.io.*;
import java.util.*;

/**
 * Importador de canciones desde archivo CSV/TXT.
 * RF-012: Carga masiva de canciones.
 *
 * @author Tu Nombre
 * @version 1.0
 */
public class CSVImporter {

    /**
     * Importa canciones desde un archivo de texto.
     * Formato: id,titulo,artista,genero,año,duracion
     *
     * @param rutaArchivo Ruta del archivo
     * @return Lista de canciones importadas
     * @throws IOException Si hay error al leer
     */
    public static List<Cancion> importarCanciones(String rutaArchivo)
            throws IOException {

        List<Cancion> canciones = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            boolean primeraLinea = true;

            while ((linea = reader.readLine()) != null) {
                // Saltar encabezado si existe
                if (primeraLinea && linea.toLowerCase().contains("id")) {
                    primeraLinea = false;
                    continue;
                }
                primeraLinea = false;

                String[] partes = linea.split(",");
                if (partes.length >= 6) {
                    try {
                        int id = Integer.parseInt(partes[0].trim());
                        String titulo = partes[1].trim();
                        String artista = partes[2].trim();
                        String genero = partes[3].trim();
                        int anio = Integer.parseInt(partes[4].trim());
                        int duracion = Integer.parseInt(partes[5].trim());

                        Cancion cancion = new Cancion(
                                id, titulo, artista, genero, anio, duracion
                        );
                        canciones.add(cancion);

                    } catch (NumberFormatException e) {
                        System.err.println("Error procesando línea: " + linea);
                    }
                }
            }
        }

        return canciones;
    }
}