package edu.universidad.estructuras.proyecto_estructura.model;


import edu.universidad.estructuras.proyecto_estructura.model.Cancion;

import java.util.*;

/**
 * Grafo Ponderado No Dirigido que representa similitudes entre canciones.
 * Utiliza el algoritmo de Dijkstra para encontrar las canciones más similares.
 *
 * Complejidad de Dijkstra: O((V + E) log V) con PriorityQueue
 * donde V = número de canciones, E = número de conexiones
 *
 */
public class GrafoDeSimilitud {
    // Mapa de adyacencias: Cancion -> Lista de Aristas (conexiones)
    private final Map<String, List<Arista>> adyacencias;
    private final Map<String, Cancion> canciones; // ID -> Canción

    /**
     * Constructor del grafo de similitud
     */
    public GrafoDeSimilitud() {
        this.adyacencias = new HashMap<>();
        this.canciones = new HashMap<>();
    }

    /**
     * Agrega una canción al grafo
     *
     * @param cancion Canción a agregar
     */
    public void agregarCancion(Cancion cancion) {
        if (cancion == null) return;

        String id = cancion.getId();
        if (!canciones.containsKey(id)) {
            canciones.put(id, cancion);
            adyacencias.put(id, new ArrayList<>());
        }
    }

    /**
     * Calcula y agrega una arista de similitud entre dos canciones
     * El peso se calcula automáticamente basado en atributos compartidos
     *
     * @param cancion1 Primera canción
     * @param cancion2 Segunda canción
     */
    public void agregarConexion(Cancion cancion1, Cancion cancion2) {
        if (cancion1 == null || cancion2 == null || cancion1.equals(cancion2)) {
            return;
        }

        // Agregar canciones si no existen
        agregarCancion(cancion1);
        agregarCancion(cancion2);

        // Calcular similitud
        double similitud = calcularSimilitud(cancion1, cancion2);
        double peso = 100 - similitud; // Convertir a peso (menor = más similar)

        // Agregar arista en ambas direcciones (grafo no dirigido)
        agregarAristaDirigida(cancion1.getId(), cancion2, peso);
        agregarAristaDirigida(cancion2.getId(), cancion1, peso);
    }

    /**
     * Agrega una arista dirigida con peso específico
     *
     * @param origenId ID de la canción origen
     * @param destino Canción destino
     * @param peso Peso de la arista
     */
    private void agregarAristaDirigida(String origenId, Cancion destino, double peso) {
        List<Arista> aristasOrigen = adyacencias.get(origenId);
        if (aristasOrigen != null) {
            // Verificar si ya existe la conexión
            for (Arista arista : aristasOrigen) {
                if (arista.getDestino().equals(destino)) {
                    arista.setPeso(peso); // Actualizar peso si existe
                    return;
                }
            }
            // Agregar nueva arista
            aristasOrigen.add(new Arista(destino, peso));
        }
    }

    /**
     * Calcula el porcentaje de similitud entre dos canciones
     * Basado en: género, artista, década
     *
     * @param c1 Canción 1
     * @param c2 Canción 2
     * @return Porcentaje de similitud (0-100)
     */
    private double calcularSimilitud(Cancion c1, Cancion c2) {
        double similitud = 0.0;

        // Mismo género: +50%
        if (c1.getGenero().equalsIgnoreCase(c2.getGenero())) {
            similitud += 50;
        }

        // Mismo artista: +30%
        if (c1.getArtista().equalsIgnoreCase(c2.getArtista())) {
            similitud += 30;
        }

        // Misma década: +20%
        int decada1 = (c1.getAnio() / 10) * 10;
        int decada2 = (c2.getAnio() / 10) * 10;
        if (decada1 == decada2) {
            similitud += 20;
        }

        // Si tienen palabras en común en el título: +10%
        if (titulosSimilares(c1.getTitulo(), c2.getTitulo())) {
            similitud += 10;
        }

        return Math.min(100, similitud); // Máximo 100%
    }

    /**
     * Verifica si dos títulos tienen palabras en común
     *
     * @param titulo1 Primer título
     * @param titulo2 Segundo título
     * @return true si tienen palabras en común
     */
    private boolean titulosSimilares(String titulo1, String titulo2) {
        Set<String> palabras1 = new HashSet<>(Arrays.asList(
                titulo1.toLowerCase().split("\\s+")));
        Set<String> palabras2 = new HashSet<>(Arrays.asList(
                titulo2.toLowerCase().split("\\s+")));

        // Remover palabras comunes que no aportan similitud
        Set<String> stopWords = new HashSet<>(Arrays.asList(
                "the", "a", "an", "and", "or", "but", "in", "on", "at", "to", "for",
                "el", "la", "los", "las", "un", "una", "y", "o", "de", "en"));
        palabras1.removeAll(stopWords);
        palabras2.removeAll(stopWords);

        palabras1.retainAll(palabras2); // Intersección
        return !palabras1.isEmpty();
    }

    /**
     * Implementación del algoritmo de Dijkstra para encontrar las canciones más similares
     * Complejidad: O((V + E) log V)
     *
     * @param origen Canción de origen
     * @param limite Número máximo de resultados
     * @return Lista de canciones similares ordenadas por similitud
     */
    public List<Cancion> encontrarCancionesSimilares(Cancion origen, int limite) {
        if (origen == null || !canciones.containsKey(origen.getId())) {
            return new ArrayList<>();
        }

        // Mapa de distancias (peso acumulado desde el origen)
        Map<String, Double> distancias = new HashMap<>();
        // Cola de prioridad para seleccionar el nodo con menor distancia
        PriorityQueue<NodoDijkstra> colaPrioridad = new PriorityQueue<>();
        // Set de nodos visitados
        Set<String> visitados = new HashSet<>();

        // Inicializar distancias
        for (String id : canciones.keySet()) {
            distancias.put(id, Double.MAX_VALUE);
        }
        distancias.put(origen.getId(), 0.0);

        // Agregar origen a la cola
        colaPrioridad.offer(new NodoDijkstra(origen.getId(), 0.0));

        // Algoritmo de Dijkstra
        while (!colaPrioridad.isEmpty()) {
            NodoDijkstra nodoActual = colaPrioridad.poll();
            String idActual = nodoActual.getId();

            // Si ya fue visitado, saltar
            if (visitados.contains(idActual)) {
                continue;
            }

            visitados.add(idActual);

            // Explorar vecinos
            List<Arista> vecinos = adyacencias.get(idActual);
            if (vecinos != null) {
                for (Arista arista : vecinos) {
                    String idVecino = arista.getDestino().getId();
                    double nuevaDistancia = distancias.get(idActual) + arista.getPeso();

                    // Si encontramos un camino más corto, actualizar
                    if (nuevaDistancia < distancias.get(idVecino)) {
                        distancias.put(idVecino, nuevaDistancia);
                        colaPrioridad.offer(new NodoDijkstra(idVecino, nuevaDistancia));
                    }
                }
            }
        }

        // Ordenar canciones por distancia (menor distancia = mayor similitud)
        List<Map.Entry<String, Double>> ordenadas = new ArrayList<>(distancias.entrySet());
        ordenadas.sort(Map.Entry.comparingByValue());

        // Convertir a lista de canciones (excluyendo el origen)
        List<Cancion> resultados = new ArrayList<>();
        for (Map.Entry<String, Double> entrada : ordenadas) {
            String id = entrada.getKey();
            if (!id.equals(origen.getId()) && entrada.getValue() != Double.MAX_VALUE) {
                resultados.add(canciones.get(id));
                if (resultados.size() >= limite) {
                    break;
                }
            }
        }

        return resultados;
    }

    /**
     * Genera una cola de reproducción tipo "Radio" basada en similitud
     *
     * @param cancionInicial Canción semilla
     * @param cantidadCanciones Cantidad de canciones para la radio
     * @return Lista de canciones para la radio
     */
    public List<Cancion> generarRadio(Cancion cancionInicial, int cantidadCanciones) {
        return encontrarCancionesSimilares(cancionInicial, cantidadCanciones);
    }

    /**
     * Obtiene las conexiones directas de una canción
     *
     * @param cancion Canción
     * @return Lista de aristas conectadas
     */
    public List<Arista> obtenerConexiones(Cancion cancion) {
        if (cancion == null) return new ArrayList<>();
        return adyacencias.getOrDefault(cancion.getId(), new ArrayList<>());
    }

    /**
     * Obtiene el número de canciones en el grafo
     *
     * @return Cantidad de canciones
     */
    public int getCantidadCanciones() {
        return canciones.size();
    }

    /**
     * Obtiene el número total de conexiones en el grafo
     *
     * @return Cantidad de aristas (dividido por 2 porque es no dirigido)
     */
    public int getCantidadConexiones() {
        int total = 0;
        for (List<Arista> lista : adyacencias.values()) {
            total += lista.size();
        }
        return total / 2; // Dividir por 2 porque cada conexión se cuenta dos veces
    }

    /**
     * Limpia el grafo
     */
    public void limpiar() {
        adyacencias.clear();
        canciones.clear();
    }

    /**
     * Clase interna para representar nodos en el algoritmo de Dijkstra
     */
    private static class NodoDijkstra implements Comparable<NodoDijkstra> {
        private final String id;
        private final double distancia;

        public NodoDijkstra(String id, double distancia) {
            this.id = id;
            this.distancia = distancia;
        }

        public String getId() {
            return id;
        }

        public double getDistancia() {
            return distancia;
        }

        @Override
        public int compareTo(NodoDijkstra otro) {
            return Double.compare(this.distancia, otro.distancia);
        }
    }

    @Override
    public String toString() {
        return String.format("GrafoDeSimilitud[canciones=%d, conexiones=%d]",
                getCantidadCanciones(), getCantidadConexiones());
    }
}
