package edu.universidad.estructuras.proyecto_estructura.service;

import edu.universidad.estructuras.proyecto_estructura.model.Cancion;
import edu.universidad.estructuras.proyecto_estructura.model.Playlist;

import java.util.*;

/**
 * Servicio para gestión de playlists de usuarios
 * CORREGIDO: Las listas ahora se guardan y cargan correctamente
 */
public class PlaylistService {
    private static PlaylistService instance;

    // CRÍTICO: Este mapa almacena las playlists en memoria
    private final Map<String, List<Playlist>> playlistsPorUsuario;
    private int contadorId;

    private PlaylistService() {
        this.playlistsPorUsuario = new HashMap<>();
        this.contadorId = 1;
    }

    public static PlaylistService getInstance() {
        if (instance == null) {
            instance = new PlaylistService();
        }
        return instance;
    }

    /**
     * ✅ CORREGIDO: Ahora devuelve la lista que está en el mapa
     * Si no existe, la crea y la agrega al mapa ANTES de devolverla
     */
    public List<Playlist> obtenerPlaylistsDeUsuario(String username) {
        if (username == null) {
            return new ArrayList<>();
        }

        // CRÍTICO: putIfAbsent asegura que la lista está en el mapa
        playlistsPorUsuario.putIfAbsent(username, new ArrayList<>());
        return playlistsPorUsuario.get(username);
    }

    /**
     * Crea una nueva playlist para un usuario
     */
    public Playlist crearPlaylist(String nombre, String descripcion, String username) {
        String id = generarId();
        Playlist playlist = new Playlist(id, nombre, descripcion, username);

        // Agregar al mapa
        obtenerPlaylistsDeUsuario(username).add(playlist);

        System.out.println("✓ Playlist creada: " + nombre + " (ID: " + id + ")");
        return playlist;
    }

    /**
     * Actualiza una playlist existente
     */
    public boolean actualizarPlaylist(String username, String playlistId, String nuevoNombre, String nuevaDescripcion) {
        Playlist playlist = buscarPlaylist(username, playlistId);
        if (playlist != null) {
            playlist.setNombre(nuevoNombre);
            playlist.setDescripcion(nuevaDescripcion);
            return true;
        }
        return false;
    }

    /**
     * Elimina una playlist
     */
    public boolean eliminarPlaylist(String username, String playlistId) {
        List<Playlist> playlists = obtenerPlaylistsDeUsuario(username);
        return playlists.removeIf(p -> p.getId().equals(playlistId));
    }

    /**
     * Duplica una playlist
     */
    public Playlist duplicarPlaylist(String username, String playlistId) {
        Playlist original = buscarPlaylist(username, playlistId);
        if (original != null) {
            String nuevoId = generarId();
            String nuevoNombre = original.getNombre() + " (Copia)";
            Playlist duplicada = original.duplicar(nuevoId, nuevoNombre);

            obtenerPlaylistsDeUsuario(username).add(duplicada);
            return duplicada;
        }
        return null;
    }

    /**
     * Agrega una canción a una playlist
     */
    public boolean agregarCancionAPlaylist(String username, String playlistId, Cancion cancion) {
        Playlist playlist = buscarPlaylist(username, playlistId);
        if (playlist != null) {
            return playlist.agregarCancion(cancion);
        }
        return false;
    }

    /**
     * Elimina una canción de una playlist
     */
    public boolean eliminarCancionDePlaylist(String username, String playlistId, Cancion cancion) {
        Playlist playlist = buscarPlaylist(username, playlistId);
        if (playlist != null) {
            return playlist.eliminarCancion(cancion);
        }
        return false;
    }

    /**
     * Mueve una canción a otra posición en la playlist
     */
    public boolean moverCancion(String username, String playlistId, int indiceActual, int indiceNuevo) {
        Playlist playlist = buscarPlaylist(username, playlistId);
        if (playlist != null) {
            return playlist.moverCancion(indiceActual, indiceNuevo);
        }
        return false;
    }

    /**
     * Busca una playlist por ID
     */
    private Playlist buscarPlaylist(String username, String playlistId) {
        List<Playlist> playlists = obtenerPlaylistsDeUsuario(username);
        return playlists.stream()
                .filter(p -> p.getId().equals(playlistId))
                .findFirst()
                .orElse(null);
    }

    /**
     * Genera un ID único para playlist
     */
    private String generarId() {
        return String.format("PL%04d", contadorId++);
    }

    /**
     * Obtiene todos los usuarios que tienen playlists
     */
    public Set<String> obtenerTodosLosUsuarios() {
        return playlistsPorUsuario.keySet();
    }

    /**
     * Limpia todas las playlists de un usuario
     */
    public void limpiarPlaylistsDeUsuario(String username) {
        playlistsPorUsuario.remove(username);
    }

    /**
     * Limpia todas las playlists del sistema
     */
    public void limpiarTodo() {
        playlistsPorUsuario.clear();
        contadorId = 1;
    }

    /**
     * Obtiene el número total de playlists en el sistema
     */
    public int getCantidadTotalPlaylists() {
        return playlistsPorUsuario.values().stream()
                .mapToInt(List::size)
                .sum();
    }

    /**
     * ✅ NUEVO: Obtiene el mapa completo (para depuración)
     */
    public Map<String, List<Playlist>> getPlaylistsPorUsuario() {
        return playlistsPorUsuario;
    }

    /**
     * ✅ NUEVO: Actualiza el contador de IDs (usado al cargar desde archivo)
     */
    public void actualizarContadorId(String id) {
        if (id.startsWith("PL")) {
            try {
                int numId = Integer.parseInt(id.substring(2));
                if (numId >= contadorId) {
                    contadorId = numId + 1;
                }
            } catch (NumberFormatException e) {
                // Ignorar si no se puede parsear
            }
        }
    }
}