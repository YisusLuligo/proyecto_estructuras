package edu.universidad.estructuras.proyecto_estructuras.service;

import edu.universidad.estructuras.proyecto_estructuras.model.Cancion;
import edu.universidad.estructuras.proyecto_estructuras.model.Usuario;
import edu.universidad.estructuras.proyecto_estructuras.util.CSVExporter;

import java.io.IOException;
import java.util.List;

/**
 * Servicio de reportes.
 * RF-009: Exportar CSV
 */
public class ReporteService {

    private static ReporteService instancia;

    private ReporteService() {}

    public static ReporteService obtenerInstancia() {
        if (instancia == null) {
            instancia = new ReporteService();
        }
        return instancia;
    }

    /**
     * RF-009: Exporta canciones favoritas a CSV.
     */
    public void exportarFavoritos(Usuario usuario, String rutaArchivo)
            throws IOException {
        List<Cancion> favoritos = usuario.getListaFavoritos();
        CSVExporter.exportarCanciones(favoritos, rutaArchivo);
    }

    public void exportarCanciones(List<Cancion> canciones, String rutaArchivo)
            throws IOException {
        CSVExporter.exportarCanciones(canciones, rutaArchivo);
    }
}