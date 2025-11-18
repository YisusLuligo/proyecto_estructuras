package edu.universidad.estructuras.proyecto_estructura.model;


import edu.universidad.estructuras.proyecto_estructura.model.Usuario;

import java.util.*;

/**
 * Grafo No Dirigido que representa conexiones sociales entre usuarios.
 * Utiliza BFS (Breadth-First Search) para encontrar "amigos de amigos" y sugerencias.
 *
 * Complejidad de BFS: O(V + E) donde V = vértices (usuarios), E = aristas (conexiones)
 *
 */
public class GrafoSocial {
    // Mapa de adyacencias: username -> Set de usernames de amigos
    private final Map<String, Set<String>> adyacencias;
    private final Map<String, Usuario> usuarios; // username -> Usuario

    /**
     * Constructor del grafo social
     */
    public GrafoSocial() {
        this.adyacencias = new HashMap<>();
        this.usuarios = new HashMap<>();
    }

    /**
     * Agrega un usuario al grafo
     *
     * @param usuario Usuario a agregar
     */
    public void agregarUsuario(Usuario usuario) {
        if (usuario == null) return;

        String username = usuario.getUsername();
        if (!usuarios.containsKey(username)) {
            usuarios.put(username, usuario);
            adyacencias.put(username, new HashSet<>());
        }
    }

    /**
     * Crea una conexión bidireccional entre dos usuarios (seguirse mutuamente)
     * Complejidad: O(1)
     *
     * @param usuario1 Primer usuario
     * @param usuario2 Segundo usuario
     * @return true si se creó la conexión exitosamente
     */
    public boolean conectarUsuarios(String usuario1, String usuario2) {
        if (usuario1 == null || usuario2 == null || usuario1.equals(usuario2)) {
            return false;
        }

        // Verificar que ambos usuarios existen
        if (!usuarios.containsKey(usuario1) || !usuarios.containsKey(usuario2)) {
            return false;
        }

        // Agregar conexión bidireccional
        adyacencias.get(usuario1).add(usuario2);
        adyacencias.get(usuario2).add(usuario1);

        return true;
    }

    /**
     * Elimina la conexión entre dos usuarios
     * Complejidad: O(1)
     *
     * @param usuario1 Primer usuario
     * @param usuario2 Segundo usuario
     * @return true si se eliminó la conexión
     */
    public boolean desconectarUsuarios(String usuario1, String usuario2) {
        if (usuario1 == null || usuario2 == null) {
            return false;
        }

        boolean removed1 = false;
        boolean removed2 = false;

        if (adyacencias.containsKey(usuario1)) {
            removed1 = adyacencias.get(usuario1).remove(usuario2);
        }

        if (adyacencias.containsKey(usuario2)) {
            removed2 = adyacencias.get(usuario2).remove(usuario1);
        }

        return removed1 || removed2;
    }

    /**
     * Verifica si dos usuarios están conectados
     *
     * @param usuario1 Primer usuario
     * @param usuario2 Segundo usuario
     * @return true si están conectados
     */
    public boolean estanConectados(String usuario1, String usuario2) {
        if (usuario1 == null || usuario2 == null) {
            return false;
        }

        Set<String> conexiones = adyacencias.get(usuario1);
        return conexiones != null && conexiones.contains(usuario2);
    }

    /**
     * Obtiene la lista de usuarios que sigue un usuario dado
     *
     * @param username Username del usuario
     * @return Lista de usuarios que sigue
     */
    public List<Usuario> obtenerSeguidos(String username) {
        List<Usuario> seguidos = new ArrayList<>();

        if (username == null || !adyacencias.containsKey(username)) {
            return seguidos;
        }

        Set<String> conexiones = adyacencias.get(username);
        for (String usernameConexion : conexiones) {
            Usuario usuario = usuarios.get(usernameConexion);
            if (usuario != null) {
                seguidos.add(usuario);
            }
        }

        return seguidos;
    }

    /**
     * Obtiene la cantidad de usuarios que sigue un usuario
     *
     * @param username Username del usuario
     * @return Cantidad de seguidos
     */
    public int getCantidadSeguidos(String username) {
        if (username == null || !adyacencias.containsKey(username)) {
            return 0;
        }
        return adyacencias.get(username).size();
    }

    /**
     * Obtiene la cantidad de seguidores de un usuario
     * (usuarios que lo siguen a él)
     *
     * @param username Username del usuario
     * @return Cantidad de seguidores
     */
    public int getCantidadSeguidores(String username) {
        if (username == null) return 0;

        int count = 0;
        for (Map.Entry<String, Set<String>> entry : adyacencias.entrySet()) {
            if (entry.getValue().contains(username)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Implementación de BFS (Breadth-First Search) para encontrar "amigos de amigos"
     * y generar sugerencias de usuarios a seguir.
     *
     * Algoritmo:
     * 1. Explora primero los usuarios directamente conectados (distancia 1)
     * 2. Luego explora los amigos de esos amigos (distancia 2)
     * 3. Excluye usuarios ya seguidos y el propio usuario
     *
     * Complejidad: O(V + E)
     *
     * @param username Username del usuario
     * @param limite Número máximo de sugerencias
     * @return Lista de usuarios sugeridos
     */
    public List<Usuario> obtenerSugerencias(String username, int limite) {
        List<Usuario> sugerencias = new ArrayList<>();

        if (username == null || !usuarios.containsKey(username)) {
            return sugerencias;
        }

        // Set de usuarios ya seguidos (para excluir)
        Set<String> yaConectados = new HashSet<>(adyacencias.get(username));
        yaConectados.add(username); // Excluir a sí mismo

        // Cola para BFS
        Queue<String> cola = new LinkedList<>();
        // Mapa para rastrear distancias
        Map<String, Integer> distancias = new HashMap<>();
        // Set de visitados
        Set<String> visitados = new HashSet<>();

        // Iniciar BFS desde el usuario actual
        cola.offer(username);
        distancias.put(username, 0);
        visitados.add(username);

        // Ejecutar BFS
        while (!cola.isEmpty() && sugerencias.size() < limite) {
            String usuarioActual = cola.poll();
            int distanciaActual = distancias.get(usuarioActual);

            // Solo explorar hasta distancia 2 (amigos de amigos)
            if (distanciaActual >= 2) {
                continue;
            }

            // Explorar vecinos
            Set<String> vecinos = adyacencias.get(usuarioActual);
            if (vecinos != null) {
                for (String vecino : vecinos) {
                    if (!visitados.contains(vecino)) {
                        visitados.add(vecino);
                        distancias.put(vecino, distanciaActual + 1);
                        cola.offer(vecino);

                        // Si está a distancia 2 y no está conectado, es una sugerencia
                        if (distanciaActual == 1 && !yaConectados.contains(vecino)) {
                            Usuario usuarioSugerido = usuarios.get(vecino);
                            if (usuarioSugerido != null && !sugerencias.contains(usuarioSugerido)) {
                                sugerencias.add(usuarioSugerido);

                                if (sugerencias.size() >= limite) {
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }

        // Si no hay suficientes sugerencias de amigos de amigos,
        // agregar usuarios aleatorios no conectados
        if (sugerencias.size() < limite) {
            List<Usuario> todosUsuarios = new ArrayList<>(usuarios.values());
            Collections.shuffle(todosUsuarios);

            for (Usuario usuario : todosUsuarios) {
                if (sugerencias.size() >= limite) {
                    break;
                }

                if (!yaConectados.contains(usuario.getUsername()) &&
                        !sugerencias.contains(usuario)) {
                    sugerencias.add(usuario);
                }
            }
        }

        return sugerencias;
    }

    /**
     * Encuentra el camino más corto entre dos usuarios usando BFS
     * Complejidad: O(V + E)
     *
     * @param origen Username del usuario origen
     * @param destino Username del usuario destino
     * @return Lista de usernames representando el camino, o lista vacía si no hay camino
     */
    public List<String> encontrarCaminoMasCorto(String origen, String destino) {
        if (origen == null || destino == null ||
                !usuarios.containsKey(origen) || !usuarios.containsKey(destino)) {
            return new ArrayList<>();
        }

        if (origen.equals(destino)) {
            return List.of(origen);
        }

        // Cola para BFS
        Queue<String> cola = new LinkedList<>();
        // Mapa para rastrear el camino (hijo -> padre)
        Map<String, String> padres = new HashMap<>();
        // Set de visitados
        Set<String> visitados = new HashSet<>();

        cola.offer(origen);
        visitados.add(origen);
        padres.put(origen, null);

        // BFS
        while (!cola.isEmpty()) {
            String actual = cola.poll();

            if (actual.equals(destino)) {
                // Reconstruir camino
                return reconstruirCamino(padres, origen, destino);
            }

            Set<String> vecinos = adyacencias.get(actual);
            if (vecinos != null) {
                for (String vecino : vecinos) {
                    if (!visitados.contains(vecino)) {
                        visitados.add(vecino);
                        padres.put(vecino, actual);
                        cola.offer(vecino);
                    }
                }
            }
        }

        return new ArrayList<>(); // No hay camino
    }

    /**
     * Reconstruye el camino desde el destino hasta el origen
     */
    private List<String> reconstruirCamino(Map<String, String> padres, String origen, String destino) {
        List<String> camino = new ArrayList<>();
        String actual = destino;

        while (actual != null) {
            camino.add(0, actual); // Agregar al inicio
            actual = padres.get(actual);
        }

        return camino;
    }

    /**
     * Obtiene todos los usuarios del grafo
     *
     * @return Lista de usuarios
     */
    public List<Usuario> obtenerTodosLosUsuarios() {
        return new ArrayList<>(usuarios.values());
    }

    /**
     * Obtiene el número de usuarios en el grafo
     *
     * @return Cantidad de usuarios
     */
    public int getCantidadUsuarios() {
        return usuarios.size();
    }

    /**
     * Obtiene el número total de conexiones en el grafo
     *
     * @return Cantidad de conexiones (dividido por 2 porque es no dirigido)
     */
    public int getCantidadConexiones() {
        int total = 0;
        for (Set<String> conexiones : adyacencias.values()) {
            total += conexiones.size();
        }
        return total / 2; // Dividir por 2 porque cada conexión se cuenta dos veces
    }

    /**
     * Limpia el grafo
     */
    public void limpiar() {
        adyacencias.clear();
        usuarios.clear();
    }

    /**
     * Elimina un usuario del grafo
     *
     * @param username Username del usuario a eliminar
     */
    public void eliminarUsuario(String username) {
        if (username == null) return;

        // Eliminar de todos los conjuntos de adyacencias
        for (Set<String> conexiones : adyacencias.values()) {
            conexiones.remove(username);
        }

        // Eliminar sus propias conexiones
        adyacencias.remove(username);
        usuarios.remove(username);
    }

    @Override
    public String toString() {
        return String.format("GrafoSocial[usuarios=%d, conexiones=%d]",
                getCantidadUsuarios(), getCantidadConexiones());
    }
}