package edu.universidad.estructuras.proyecto_estructuras.algoritmos;

import edu.universidad.estructuras.proyecto_estructuras.model.GrafoSocial;
import edu.universidad.estructuras.proyecto_estructuras.model.Usuario;

import java.util.*;

/**
 * Implementación del algoritmo BFS (Breadth-First Search) para el grafo social.
 * RF-022: Encuentra "parceros de parceros" (amigos de amigos).
 *
 * Complejidad: O(V + E) donde V = vértices (usuarios) y E = aristas (conexiones)
 *
 * @author Tu Nombre
 * @version 1.0
 */
public class BFS {

    /**
     * Encuentra sugerencias de parceros basadas en amigos en común.
     * RF-008: Recibe sugerencias de usuarios a seguir.
     *
     * @param grafo Grafo social de usuarios
     * @param origen Usuario para quien buscar sugerencias
     * @param maxSugerencias Número máximo de sugerencias
     * @return Lista de usuarios sugeridos ordenada por amigos en común
     */
    public static List<Usuario> encontrarSugerenciasAmigos(
            GrafoSocial grafo,
            Usuario origen,
            int maxSugerencias) {

        if (!grafo.contieneUsuario(origen)) {
            return new ArrayList<>();
        }

        // Parceros directos (seguidos) del usuario
        Set<Usuario> parcerosDirectos = grafo.obtenerSeguidos(origen);

        // Mapa para contar cuántos parceros en común tiene cada usuario
        Map<Usuario, Integer> conteoMutuos = new HashMap<>();

        // Cola para BFS
        Queue<Usuario> cola = new LinkedList<>();
        Set<Usuario> visitados = new HashSet<>();
        Map<Usuario, Integer> niveles = new HashMap<>();

        // Iniciar BFS desde el usuario origen
        cola.offer(origen);
        visitados.add(origen);
        niveles.put(origen, 0);

        while (!cola.isEmpty()) {
            Usuario actual = cola.poll();
            int nivelActual = niveles.get(actual);

            // Solo exploramos hasta nivel 2 (parceros de parceros)
            if (nivelActual >= 2) {
                continue;
            }

            // Explorar todos los parceros del usuario actual
            for (Usuario parcero : grafo.obtenerSeguidos(actual)) {
                if (!visitados.contains(parcero)) {
                    visitados.add(parcero);
                    niveles.put(parcero, nivelActual + 1);
                    cola.offer(parcero);

                    // Si está a nivel 2 (parcero de parcero) y NO es parcero directo
                    if (nivelActual + 1 == 2 && !parcerosDirectos.contains(parcero)) {
                        conteoMutuos.put(parcero,
                                conteoMutuos.getOrDefault(parcero, 0) + 1);
                    }
                }
            }
        }

        // Ordenar por cantidad de parceros en común (más en común = mejor sugerencia)
        return conteoMutuos.entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
                .limit(maxSugerencias)
                .map(Map.Entry::getKey)
                .toList();
    }

    /**
     * Calcula la distancia (grados de separación) entre dos usuarios.
     *
     * @param grafo Grafo social
     * @param origen Usuario origen
     * @param destino Usuario destino
     * @return Número de grados de separación (-1 si no hay conexión)
     */
    public static int calcularDistancia(
            GrafoSocial grafo,
            Usuario origen,
            Usuario destino) {

        if (!grafo.contieneUsuario(origen) || !grafo.contieneUsuario(destino)) {
            return -1;
        }

        if (origen.equals(destino)) {
            return 0;
        }

        Queue<Usuario> cola = new LinkedList<>();
        Set<Usuario> visitados = new HashSet<>();
        Map<Usuario, Integer> distancias = new HashMap<>();

        cola.offer(origen);
        visitados.add(origen);
        distancias.put(origen, 0);

        while (!cola.isEmpty()) {
            Usuario actual = cola.poll();

            for (Usuario parcero : grafo.obtenerSeguidos(actual)) {
                if (!visitados.contains(parcero)) {
                    visitados.add(parcero);
                    int distancia = distancias.get(actual) + 1;
                    distancias.put(parcero, distancia);

                    if (parcero.equals(destino)) {
                        return distancia;
                    }

                    cola.offer(parcero);
                }
            }
        }

        return -1; // No hay camino
    }

    /**
     * Encuentra el camino más corto entre dos usuarios.
     *
     * @param grafo Grafo social
     * @param origen Usuario origen
     * @param destino Usuario destino
     * @return Lista con el camino (vacía si no hay camino)
     */
    public static List<Usuario> encontrarCamino(
            GrafoSocial grafo,
            Usuario origen,
            Usuario destino) {

        if (!grafo.contieneUsuario(origen) || !grafo.contieneUsuario(destino)) {
            return new ArrayList<>();
        }

        if (origen.equals(destino)) {
            return Arrays.asList(origen);
        }

        Queue<Usuario> cola = new LinkedList<>();
        Set<Usuario> visitados = new HashSet<>();
        Map<Usuario, Usuario> predecesores = new HashMap<>();

        cola.offer(origen);
        visitados.add(origen);

        while (!cola.isEmpty()) {
            Usuario actual = cola.poll();

            for (Usuario parcero : grafo.obtenerSeguidos(actual)) {
                if (!visitados.contains(parcero)) {
                    visitados.add(parcero);
                    predecesores.put(parcero, actual);

                    if (parcero.equals(destino)) {
                        return reconstruirCamino(predecesores, origen, destino);
                    }

                    cola.offer(parcero);
                }
            }
        }

        return new ArrayList<>();
    }

    /**
     * Reconstruye el camino desde origen hasta destino.
     */
    private static List<Usuario> reconstruirCamino(
            Map<Usuario, Usuario> predecesores,
            Usuario origen,
            Usuario destino) {

        List<Usuario> camino = new ArrayList<>();
        Usuario actual = destino;

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
     * Encuentra todos los usuarios a una distancia específica.
     *
     * @param grafo Grafo social
     * @param origen Usuario origen
     * @param distancia Distancia deseada
     * @return Set de usuarios a esa distancia
     */
    public static Set<Usuario> usuariosADistancia(
            GrafoSocial grafo,
            Usuario origen,
            int distancia) {

        Set<Usuario> resultado = new HashSet<>();

        if (!grafo.contieneUsuario(origen) || distancia < 0) {
            return resultado;
        }

        Queue<Usuario> cola = new LinkedList<>();
        Set<Usuario> visitados = new HashSet<>();
        Map<Usuario, Integer> niveles = new HashMap<>();

        cola.offer(origen);
        visitados.add(origen);
        niveles.put(origen, 0);

        while (!cola.isEmpty()) {
            Usuario actual = cola.poll();
            int nivelActual = niveles.get(actual);

            if (nivelActual == distancia) {
                resultado.add(actual);
            }

            if (nivelActual < distancia) {
                for (Usuario parcero : grafo.obtenerSeguidos(actual)) {
                    if (!visitados.contains(parcero)) {
                        visitados.add(parcero);
                        niveles.put(parcero, nivelActual + 1);
                        cola.offer(parcero);
                    }
                }
            }
        }

        return resultado;
    }
}