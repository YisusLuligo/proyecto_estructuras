package edu.universidad.estructuras.proyecto_estructura.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Representa una playlist de canciones creada por un usuario.
 * Permite agrupar canciones y gestionarlas como una colección.
 *
 */
public class Playlist {
    private String id;
    private String nombre;
    private String descripcion;
    private String usuarioPropietario;
    private List<Cancion> canciones;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;

    /**
     * Constructor completo de Playlist
     *
     * @param id Identificador único de la playlist
     * @param nombre Nombre de la playlist
     * @param descripcion Descripción de la playlist
     * @param usuarioPropietario Username del creador
     */
    public Playlist(String id, String nombre, String descripcion, String usuarioPropietario) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.usuarioPropietario = usuarioPropietario;
        this.canciones = new ArrayList<>();
        this.fechaCreacion = LocalDateTime.now();
        this.fechaModificacion = LocalDateTime.now();
    }

    /**
     * Constructor simplificado
     *
     * @param id ID de la playlist
     * @param nombre Nombre de la playlist
     * @param usuarioPropietario Username del creador
     */
    public Playlist(String id, String nombre, String usuarioPropietario) {
        this(id, nombre, "", usuarioPropietario);
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
        actualizarFechaModificacion();
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
        actualizarFechaModificacion();
    }

    public String getUsuarioPropietario() {
        return usuarioPropietario;
    }

    public void setUsuarioPropietario(String usuarioPropietario) {
        this.usuarioPropietario = usuarioPropietario;
    }

    public List<Cancion> getCanciones() {
        return canciones;
    }

    public void setCanciones(List<Cancion> canciones) {
        this.canciones = canciones;
        actualizarFechaModificacion();
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaModificacion() {
        return fechaModificacion;
    }

    public void setFechaModificacion(LocalDateTime fechaModificacion) {
        this.fechaModificacion = fechaModificacion;
    }

    /**
     * Actualiza la fecha de modificación al momento actual
     */
    private void actualizarFechaModificacion() {
        this.fechaModificacion = LocalDateTime.now();
    }

    /**
     * Agrega una canción a la playlist si no existe
     *
     * @param cancion Canción a agregar
     * @return true si se agregó exitosamente, false si ya existía
     */
    public boolean agregarCancion(Cancion cancion) {
        if (!canciones.contains(cancion)) {
            canciones.add(cancion);
            actualizarFechaModificacion();
            return true;
        }
        return false;
    }

    /**
     * Elimina una canción de la playlist
     *
     * @param cancion Canción a eliminar
     * @return true si se eliminó exitosamente, false si no existía
     */
    public boolean eliminarCancion(Cancion cancion) {
        boolean resultado = canciones.remove(cancion);
        if (resultado) {
            actualizarFechaModificacion();
        }
        return resultado;
    }

    /**
     * Verifica si una canción está en la playlist
     *
     * @param cancion Canción a verificar
     * @return true si está en la playlist, false en caso contrario
     */
    public boolean contieneCancion(Cancion cancion) {
        return canciones.contains(cancion);
    }

    /**
     * Obtiene el número de canciones en la playlist
     *
     * @return Cantidad de canciones
     */
    public int getCantidadCanciones() {
        return canciones.size();
    }

    /**
     * Calcula la duración total de la playlist en minutos
     *
     * @return Duración total en minutos
     */
    public double getDuracionTotal() {
        return canciones.stream()
                .mapToDouble(Cancion::getDuracion)
                .sum();
    }

    /**
     * Obtiene la duración total formateada como HH:MM:SS
     *
     * @return String con formato HH:MM:SS
     */
    public String getDuracionTotalFormateada() {
        double duracionTotal = getDuracionTotal();
        int horas = (int) (duracionTotal / 60);
        int minutos = (int) (duracionTotal % 60);
        int segundos = (int) ((duracionTotal % 1) * 60);

        if (horas > 0) {
            return String.format("%d:%02d:%02d", horas, minutos, segundos);
        } else {
            return String.format("%d:%02d", minutos, segundos);
        }
    }

    /**
     * Mueve una canción a una nueva posición
     *
     * @param indiceActual Índice actual de la canción
     * @param indiceNuevo Nuevo índice deseado
     * @return true si se movió exitosamente
     */
    public boolean moverCancion(int indiceActual, int indiceNuevo) {
        if (indiceActual < 0 || indiceActual >= canciones.size() ||
                indiceNuevo < 0 || indiceNuevo >= canciones.size()) {
            return false;
        }

        Cancion cancion = canciones.remove(indiceActual);
        canciones.add(indiceNuevo, cancion);
        actualizarFechaModificacion();
        return true;
    }

    /**
     * Limpia todas las canciones de la playlist
     */
    public void limpiar() {
        canciones.clear();
        actualizarFechaModificacion();
    }

    /**
     * Obtiene la fecha de creación formateada
     *
     * @return String con la fecha formateada
     */
    public String getFechaCreacionFormateada() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return fechaCreacion.format(formatter);
    }

    /**
     * Obtiene la fecha de modificación formateada
     *
     * @return String con la fecha formateada
     */
    public String getFechaModificacionFormateada() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return fechaModificacion.format(formatter);
    }

    /**
     * Calcula el hashCode basado en el ID de la playlist
     *
     * @return hashCode de la playlist
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Compara dos playlists basándose en su ID
     *
     * @param obj Objeto a comparar
     * @return true si las playlists son iguales, false en caso contrario
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Playlist playlist = (Playlist) obj;
        return Objects.equals(id, playlist.id);
    }

    /**
     * Representación en String de la playlist
     *
     * @return String con la información de la playlist
     */
    @Override
    public String toString() {
        return String.format("%s (%d canciones - %s)",
                nombre, canciones.size(), getDuracionTotalFormateada());
    }

    /**
     * Crea una copia de esta playlist con un nuevo ID
     *
     * @param nuevoId ID para la nueva playlist
     * @param nuevoNombre Nombre para la nueva playlist
     * @return Nueva playlist duplicada
     */
    public Playlist duplicar(String nuevoId, String nuevoNombre) {
        Playlist nueva = new Playlist(nuevoId, nuevoNombre, this.descripcion + " (Copia)", this.usuarioPropietario);
        nueva.canciones.addAll(this.canciones);
        return nueva;
    }
}