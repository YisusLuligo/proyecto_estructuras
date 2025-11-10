package edu.universidad.estructuras.proyecto_estructuras.util;

import edu.universidad.estructuras.proyecto_estructuras.model.Cancion;
import java.io.*;
import java.util.List;

/**
 * Exportador de canciones a formato CSV.
 * RF-009: Descargar reporte de canciones favoritas.
 *
 * @author Tu Nombre
 * @version 1.0
 */
public class CSVExporter {

    /**
     * Exporta una lista de canciones a archivo CSV.
     *
     * @param canciones Lista de canciones
     * @param rutaArchivo Ruta donde guardar el archivo
     * @throws IOException Si hay error al escribir
     */
    public static void exportarCanciones(List<Cancion> canciones, String rutaArchivo)
            throws IOException {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(rutaArchivo))) {
            // Encabezado
            writer.write("ID,Título,Artista,Género,Año,Duración(seg),Duración");
            writer.newLine();

            // Datos
            for (Cancion cancion : canciones) {
                writer.write(String.format("%d,%s,%s,%s,%d,%d,%s",
                        cancion.getId(),
                        escaparCSV(cancion.getTitulo()),
                        escaparCSV(cancion.getArtista()),
                        escaparCSV(cancion.getGenero()),
                        cancion.getAnio(),
                        cancion.getDuracionSegundos(),
                        escaparCSV(cancion.getDuracionFormato())
                ));
                writer.newLine();
            }
        }
    }

    /**
     * Escapa caracteres especiales en CSV.
     */
    private static String escaparCSV(String valor) {
        if (valor.contains(",") || valor.contains("\"") || valor.contains("\n")) {
            return "\"" + valor.replace("\"", "\"\"") + "\"";
        }
        return valor;
    }
}