package edu.universidad.estructuras.proyecto_estructura.service;

import edu.universidad.estructuras.proyecto_estructura.model.Cancion;
import edu.universidad.estructuras.proyecto_estructura.model.Playlist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para gestión de playlists de usuarios.
 * Mantiene un HashMap de playlists por usuario.
 *
 * @author SyncUp Team
 * @version 1.0
 */
public class PlaylistService {
    private static PlaylistService instance;
    private HashMap<String, List<Playlist>> playlistsPorUsuario; // username -> lista de playlists
    private int contadorId;

    /**
     * Constructor privado para patrón Singleton
     */
    private PlaylistService() {
        playlistsPorUsuario = new HashMap<>();
        contadorId = 1;
    }

    /**
     * Obtiene la instancia única del servicio (Singleton)
     *
     * @return Instancia de PlaylistService
     */
    public static PlaylistService getInstance() {
        if (instance == null) {
            instance = new PlaylistService();
        }
        return instance;
    }

    /**
     * Genera un ID único para una nueva playlist
     *
     * @return ID generado
     */
    private String generarId() {
        return String.format("PL%04d", contadorId++);
    }

    /**
     * Crea una nueva playlist para un usuario
     *
     * @param nombre Nombre de la playlist
     * @param descripcion Descripción de la playlist
     * @param usuarioPropietario Username del propietario
     * @return Playlist creada
     */
    public Playlist crearPlaylist(String nombre, String descripcion, String usuarioPropietario) {
        String id = generarId();
        Playlist playlist = new Playlist(id, nombre, descripcion, usuarioPropietario);

        // Obtener o crear lista de playlists del usuario
        List<Playlist> playlistsUsuario = playlistsPorUsuario.getOrDefault(usuarioPropietario, new ArrayList<>());
        playlistsUsuario.add(playlist);
        playlistsPorUsuario.put(usuarioPropietario, playlistsUsuario);

        return playlist;
    }

    /**
     * Obtiene todas las playlists de un usuario
     *
     * @param username Username del usuario
     * @return Lista de playlists del usuario
     */
    public List<Playlist> obtenerPlaylistsDeUsuario(String username) {
        return playlistsPorUsuario.getOrDefault(username, new ArrayList<>());
    }

    /**
     * Busca una playlist por su ID en las playlists de un usuario
     *
     * @param username Username del propietario
     * @param playlistId ID de la playlist
     * @return Playlist si existe, null en caso contrario
     */
    public Playlist obtenerPlaylist(String username, String playlistId) {
        List<Playlist> playlists = obtenerPlaylistsDeUsuario(username);
        return playlists.stream()
                .filter(p -> p.getId().equals(playlistId))
                .findFirst()
                .orElse(null);
    }

    /**
     * Actualiza el nombre y descripción de una playlist
     *
     * @param username Username del propietario
     * @param playlistId ID de la playlist
     * @param nuevoNombre Nuevo nombre
     * @param nuevaDescripcion Nueva descripción
     * @return true si se actualizó exitosamente
     */
    public boolean actualizarPlaylist(String username, String playlistId, String nuevoNombre, String nuevaDescripcion) {
        Playlist playlist = obtenerPlaylist(username, playlistId);
        if (playlist != null) {
            playlist.setNombre(nuevoNombre);
            playlist.setDescripcion(nuevaDescripcion);
            return true;
        }
        return false;
    }

    /**
     * Elimina una playlist
     *
     * @param username Username del propietario
     * @param playlistId ID de la playlist
     * @return true si se eliminó exitosamente
     */
    public boolean eliminarPlaylist(String username, String playlistId) {
        List<Playlist> playlists = obtenerPlaylistsDeUsuario(username);
        return playlists.removeIf(p -> p.getId().equals(playlistId));
    }

    /**
     * Agrega una canción a una playlist
     *
     * @param username Username del propietario
     * @param playlistId ID de la playlist
     * @param cancion Canción a agregar
     * @return true si se agregó exitosamente
     */
    public boolean agregarCancionAPlaylist(String username, String playlistId, Cancion cancion) {
        Playlist playlist = obtenerPlaylist(username, playlistId);
        if (playlist != null) {
            return playlist.agregarCancion(cancion);
        }
        return false;
    }

    /**
     * Elimina una canción de una playlist
     *
     * @param username Username del propietario
     * @param playlistId ID de la playlist
     * @param cancion Canción a eliminar
     * @return true si se eliminó exitosamente
     */
    public boolean eliminarCancionDePlaylist(String username, String playlistId, Cancion cancion) {
        Playlist playlist = obtenerPlaylist(username, playlistId);
        if (playlist != null) {
            return playlist.eliminarCancion(cancion);
        }
        return false;
    }

    /**
     * Duplica una playlist existente
     *
     * @param username Username del propietario
     * @param playlistId ID de la playlist a duplicar
     * @return Nueva playlist duplicada, o null si no se encontró
     */
    public Playlist duplicarPlaylist(String username, String playlistId) {
        Playlist original = obtenerPlaylist(username, playlistId);
        if (original != null) {
            String nuevoId = generarId();
            String nuevoNombre = original.getNombre() + " (Copia)";
            Playlist duplicada = original.duplicar(nuevoId, nuevoNombre);

            List<Playlist> playlistsUsuario = playlistsPorUsuario.get(username);
            playlistsUsuario.add(duplicada);

            return duplicada;
        }
        return null;
    }

    /**
     * Obtiene la cantidad total de playlists de un usuario
     *
     * @param username Username del usuario
     * @return Número de playlists
     */
    public int getCantidadPlaylists(String username) {
        return obtenerPlaylistsDeUsuario(username).size();
    }

    /**
     * Busca playlists por nombre
     *
     * @param username Username del propietario
     * @param nombreBusqueda Texto a buscar en el nombre
     * @return Lista de playlists que coinciden
     */
    public List<Playlist> buscarPlaylistsPorNombre(String username, String nombreBusqueda) {
        return obtenerPlaylistsDeUsuario(username).stream()
                .filter(p -> p.getNombre().toLowerCase().contains(nombreBusqueda.toLowerCase()))
                .collect(Collectors.toList());
    }

    /**
     * Obtiene todas las playlists que contienen una canción específica
     *
     * @param username Username del propietario
     * @param cancion Canción a buscar
     * @return Lista de playlists que contienen la canción
     */
    public List<Playlist> obtenerPlaylistsConCancion(String username, Cancion cancion) {
        return obtenerPlaylistsDeUsuario(username).stream()
                .filter(p -> p.contieneCancion(cancion))
                .collect(Collectors.toList());
    }

    /**
     * Limpia todas las playlists de un usuario
     *
     * @param username Username del usuario
     */
    public void limpiarPlaylistsDeUsuario(String username) {
        playlistsPorUsuario.remove(username);
    }

    /**
     * Obtiene la cantidad total de canciones en todas las playlists de un usuario
     *
     * @param username Username del usuario
     * @return Número total de canciones
     */
    public int getCantidadTotalCanciones(String username) {
        return obtenerPlaylistsDeUsuario(username).stream()
                .mapToInt(Playlist::getCantidadCanciones)
                .sum();
    }

    /**
     * Mueve una canción dentro de una playlist
     *
     * @param username Username del propietario
     * @param playlistId ID de la playlist
     * @param indiceActual Índice actual de la canción
     * @param indiceNuevo Nuevo índice
     * @return true si se movió exitosamente
     */
    public boolean moverCancion(String username, String playlistId, int indiceActual, int indiceNuevo) {
        Playlist playlist = obtenerPlaylist(username, playlistId);
        if (playlist != null) {
            return playlist.moverCancion(indiceActual, indiceNuevo);
        }
        return false;
    }
}