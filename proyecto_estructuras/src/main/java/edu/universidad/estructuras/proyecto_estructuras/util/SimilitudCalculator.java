package edu.universidad.estructuras.proyecto_estructuras.util;

import edu.universidad.estructuras.proyecto_estructuras.model.Cancion;

/**
 * Calculador de similitud entre canciones.
 * Usa múltiples factores: género, artista, año, duración.
 *
 * @author Tu Nombre
 * @version 1.0
 */
public class SimilitudCalculator {

    /**
     * Calcula la similitud entre dos canciones (0 a 1).
     *
     * @param c1 Primera canción
     * @param c2 Segunda canción
     * @return Valor de similitud entre 0 y 1
     */
    public static double calcularSimilitud(Cancion c1, Cancion c2) {
        double similitud = 0.0;

        // Factor 1: Género (peso 40%)
        if (c1.getGenero().equalsIgnoreCase(c2.getGenero())) {
            similitud += Constants.PESO_GENERO;
        }

        // Factor 2: Artista (peso 30%)
        if (c1.getArtista().equalsIgnoreCase(c2.getArtista())) {
            similitud += Constants.PESO_ARTISTA;
        }

        // Factor 3: Año (peso 15%)
        int diferenciaAnio = Math.abs(c1.getAnio() - c2.getAnio());
        if (diferenciaAnio <= 5) {
            double factorAnio = 1.0 - (diferenciaAnio / 10.0);
            similitud += Constants.PESO_ANIO * factorAnio;
        }

        // Factor 4: Duración (peso 15%)
        int diferenciaDuracion = Math.abs(
                c1.getDuracionSegundos() - c2.getDuracionSegundos()
        );
        if (diferenciaDuracion <= 60) {
            double factorDuracion = 1.0 - (diferenciaDuracion / 120.0);
            similitud += Constants.PESO_DURACION * factorDuracion;
        }

        return Math.min(1.0, similitud);
    }
}