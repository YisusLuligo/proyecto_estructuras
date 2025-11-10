package edu.universidad.estructuras.proyecto_estructuras.algoritmos;

import edu.universidad.estructuras.proyecto_estructuras.model.Arista;
import edu.universidad.estructuras.proyecto_estructuras.model.Cancion;
import edu.universidad.estructuras.proyecto_estructuras.model.GrafoDeSimilitud;

import java.util.*;

/**
 * Implementación del algoritmo de Dijkstra para encontrar canciones similares.
 * RF-020: Encuentra rutas de mayor similitud en el grafo ponderado.
 *
 * Complejidad: O((V + E) log V) donde V = vértices (canciones) y E = aristas (conexiones)
 *
 * @author Tu Nombre
 * @version 1.0
 */
public class Dijkstra {

    /**
     * Encuentra las N canciones más similares a una canción de origen.
     * Usa el algoritmo de Dijkstra modificado para maximizar similitud.
     *
     * @param grafo Grafo de similitud de canciones
     * @param origen Canción de origen
     * @param n Número de canciones similares a retornar
     * @return Lista con las N canciones más similares
     */
    public static List<Cancion> encontrarCancionesSimilares(
            GrafoDeSimilitud grafo,
            Cancion origen,
            int n) {

        // Verificar que la canción existe en el grafo
        if (!grafo.contieneCancion(origen)) {
            return new ArrayList<>();
        }

        // Mapa de similitudes (distancias): canción -> similitud acumulada
        Map<Cancion, Double> similitudes = new HashMap<>();

        // Conjunto de canciones ya visitadas
        Set<Cancion> visitadas = new HashSet<>();

        // Cola de prioridad: mayor similitud = mayor prioridad
        PriorityQueue<ParCancionSimilitud> cola = new PriorityQueue<>(
                (a, b) -> Double.compare(b.similitud, a.similitud)
        );

        // Inicializar similitudes en 0
        for (Cancion cancion : grafo.obtenerTodasCanciones()) {
            similitudes.put(cancion, 0.0);
        }

        // La canción origen tiene similitud 1.0 consigo misma
        similitudes.put(origen, 1.0);
        cola.offer(new ParCancionSimilitud(origen, 1.0));

        // Algoritmo de Dijkstra
        while (!cola.isEmpty()) {
            ParCancionSimilitud actual = cola.poll();
            Cancion cancionActual = actual.cancion;

            // Si ya fue visitada, continuar
            if (visitadas.contains(cancionActual)) {
                continue;
            }

            visitadas.add(cancionActual);

            // Explorar vecinos (canciones conectadas)
            for (Arista arista : grafo.obtenerVecinos(cancionActual)) {
                Cancion vecino = arista.getDestino();
                double pesoArista = arista.getPeso(); // Similitud entre canciones

                // Calcular nueva similitud: similitud acumulada * peso de la arista
                double nuevaSimilitud = similitudes.get(cancionActual) * pesoArista;

                // Si encontramos un camino con mayor similitud, actualizamos
                if (nuevaSimilitud > similitudes.get(vecino)) {
                    similitudes.put(vecino, nuevaSimilitud);
                    cola.offer(new ParCancionSimilitud(vecino, nuevaSimilitud));
                }
            }
        }

        // Ordenar por similitud (mayor a menor) y retornar las N primeras
        // Excluir la canción origen
        return similitudes.entrySet().stream()
                .filter(entry -> !entry.getKey().equals(origen))
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .limit(n)
                .map(Map.Entry::getKey)
                .toList();
    }

    /**
     * Encuentra el camino de mayor similitud entre dos canciones.
     *
     * @param grafo Grafo de similitud
     * @param origen Canción de inicio
     * @param destino Canción de destino
     * @return Lista con el camino de canciones (vacía si no hay camino)
     */
    public static List<Cancion> encontrarCamino(
            GrafoDeSimilitud grafo,
            Cancion origen,
            Cancion destino) {

        if (!grafo.contieneCancion(origen) || !grafo.contieneCancion(destino)) {
            return new ArrayList<>();
        }

        Map<Cancion, Double> similitudes = new HashMap<>();
        Map<Cancion, Cancion> predecesores = new HashMap<>();
        Set<Cancion> visitadas = new HashSet<>();

        PriorityQueue<ParCancionSimilitud> cola = new PriorityQueue<>(
                (a, b) -> Double.compare(b.similitud, a.similitud)
        );

        for (Cancion cancion : grafo.obtenerTodasCanciones()) {
            similitudes.put(cancion, 0.0);
        }

        similitudes.put(origen, 1.0);
        cola.offer(new ParCancionSimilitud(origen, 1.0));

        while (!cola.isEmpty()) {
            ParCancionSimilitud actual = cola.poll();
            Cancion cancionActual = actual.cancion;

            if (visitadas.contains(cancionActual)) {
                continue;
            }

            visitadas.add(cancionActual);

            // Si llegamos al destino, reconstruir el camino
            if (cancionActual.equals(destino)) {
                return reconstruirCamino(predecesores, origen, destino);
            }

            for (Arista arista : grafo.obtenerVecinos(cancionActual)) {
                Cancion vecino = arista.getDestino();
                double pesoArista = arista.getPeso();
                double nuevaSimilitud = similitudes.get(cancionActual) * pesoArista;

                if (nuevaSimilitud > similitudes.get(vecino)) {
                    similitudes.put(vecino, nuevaSimilitud);
                    predecesores.put(vecino, cancionActual);
                    cola.offer(new ParCancionSimilitud(vecino, nuevaSimilitud));
                }
            }
        }

        return new ArrayList<>(); // No hay camino
    }

    /**
     * Reconstruye el camino desde origen hasta destino.
     */
    private static List<Cancion> reconstruirCamino(
            Map<Cancion, Cancion> predecesores,
            Cancion origen,
            Cancion destino) {

        List<Cancion> camino = new ArrayList<>();
        Cancion actual = destino;

        while (actual != null && !actual.equals(origen)) {
            camino.add(0, actual);
            actual = predecesores.get(actual);
        }

        if (actual != null) {
            camino.add(0, origen);
        }

        return camino;
    }

    /**
     * Clase auxiliar para manejar pares de canción-similitud en la cola de prioridad.
     */
    private static class ParCancionSimilitud {
        Cancion cancion;
        double similitud;

        ParCancionSimilitud(Cancion cancion, double similitud) {
            this.cancion = cancion;
            this.similitud = similitud;
        }
    }
}