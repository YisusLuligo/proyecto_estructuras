package edu.universidad.estructuras.proyecto_estructuras.service;

import edu.universidad.estructuras.proyecto_estructuras.algoritmos.Dijkstra;
import edu.universidad.estructuras.proyecto_estructuras.model.*;
import edu.universidad.estructuras.proyecto_estructuras.util.Constants;
import edu.universidad.estructuras.proyecto_estructuras.util.SimilitudCalculator;

import java.util.*;

/**
 * Servicio de recomendaciones musicales.
 * RF-005: Descubrimiento Semanal
 * RF-006: Radio basada en canción
 */
public class RecomendacionService {

    private GrafoDeSimilitud grafoDeSimilitud;
    private static RecomendacionService instancia;

    private RecomendacionService() {
        this.grafoDeSimilitud = new GrafoDeSimilitud();
    }

    public static RecomendacionService obtenerInstancia() {
        if (instancia == null) {
            instancia = new RecomendacionService();
        }
        return instancia;
    }

    /**
     * Construye el grafo de similitud.
     */
    public void construirGrafoDeSimilitud(List<Cancion> catalogo) {
        // Agregar todas las canciones
        for (Cancion cancion : catalogo) {
            grafoDeSimilitud.agregarCancion(cancion);
        }

        // Conectar canciones similares
        for (int i = 0; i < catalogo.size(); i++) {
            for (int j = i + 1; j < catalogo.size(); j++) {
                Cancion c1 = catalogo.get(i);
                Cancion c2 = catalogo.get(j);

                double similitud = SimilitudCalculator.calcularSimilitud(c1, c2);

                if (similitud > Constants.UMBRAL_SIMILITUD) {
                    grafoDeSimilitud.agregarArista(c1, c2, similitud);
                }
            }
        }
    }

    /**
     * RF-005: Genera playlist "Descubrimiento Semanal".
     */
    public List<Cancion> generarDescubrimientoSemanal(Usuario usuario) {
        Set<Cancion> recomendaciones = new HashSet<>();
        LinkedList<Cancion> favoritos = usuario.getListaFavoritos();

        if (favoritos.isEmpty()) {
            return new ArrayList<>();
        }

        // Para cada favorita, encontrar similares
        for (Cancion favorita : favoritos) {
            List<Cancion> similares = Dijkstra.encontrarCancionesSimilares(
                    grafoDeSimilitud, favorita, 5
            );

            // Filtrar las que ya están en favoritos
            similares = similares.stream()
                    .filter(c -> !favoritos.contains(c))
                    .toList();

            recomendaciones.addAll(similares);

            if (recomendaciones.size() >= Constants.CANCIONES_DESCUBRIMIENTO) {
                break;
            }
        }

        return recomendaciones.stream()
                .limit(Constants.CANCIONES_DESCUBRIMIENTO)
                .toList();
    }

    /**
     * RF-006: Genera "Radio" desde una canción.
     */
    public Queue<Cancion> generarRadio(Cancion semilla) {
        List<Cancion> similares = Dijkstra.encontrarCancionesSimilares(
                grafoDeSimilitud, semilla, Constants.CANCIONES_RADIO
        );

        Queue<Cancion> cola = new LinkedList<>();
        cola.add(semilla);
        cola.addAll(similares);

        return cola;
    }

    public void agregarCancionAlGrafo(Cancion nuevaCancion, List<Cancion> catalogo) {
        grafoDeSimilitud.agregarCancion(nuevaCancion);

        for (Cancion existente : catalogo) {
            if (!existente.equals(nuevaCancion)) {
                double similitud = SimilitudCalculator.calcularSimilitud(
                        nuevaCancion, existente
                );

                if (similitud > Constants.UMBRAL_SIMILITUD) {
                    grafoDeSimilitud.agregarArista(nuevaCancion, existente, similitud);
                }
            }
        }
    }
}