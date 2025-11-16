package edu.universidad.estructuras.proyecto_estructura.model;


import java.util.LinkedList;
import java.util.Objects;

/**
 * Representa un usuario de la plataforma SyncUp.
 * Almacena información del perfil y lista de canciones favoritas.
 *
 * @author SyncUp Team
 * @version 1.0
 */
public class Usuario {
    private String username;
    private String password;
    private String nombre;
    private LinkedList<Cancion> listaFavoritos;
    private TipoUsuario tipoUsuario;

    /**
     * Enum para diferenciar tipos de usuario
     */
    public enum TipoUsuario {
        USUARIO,
        ADMINISTRADOR
    }

    /**
     * Constructor completo de Usuario
     *
     * @param username Nombre de usuario único
     * @param password Contraseña
     * @param nombre Nombre completo
     * @param tipoUsuario Tipo de usuario (USUARIO o ADMINISTRADOR)
     */
    public Usuario(String username, String password, String nombre, TipoUsuario tipoUsuario) {
        this.username = username;
        this.password = password;
        this.nombre = nombre;
        this.tipoUsuario = tipoUsuario;
        this.listaFavoritos = new LinkedList<>();
    }

    /**
     * Constructor para usuario regular (por defecto)
     *
     * @param username Nombre de usuario único
     * @param password Contraseña
     * @param nombre Nombre completo
     */
    public Usuario(String username, String password, String nombre) {
        this(username, password, nombre, TipoUsuario.USUARIO);
    }

    // Getters y Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public LinkedList<Cancion> getListaFavoritos() {
        return listaFavoritos;
    }

    public void setListaFavoritos(LinkedList<Cancion> listaFavoritos) {
        this.listaFavoritos = listaFavoritos;
    }

    public TipoUsuario getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(TipoUsuario tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    /**
     * Agrega una canción a la lista de favoritos si no existe
     *
     * @param cancion Canción a agregar
     * @return true si se agregó exitosamente, false si ya existía
     */
    public boolean agregarFavorito(Cancion cancion) {
        if (!listaFavoritos.contains(cancion)) {
            listaFavoritos.add(cancion);
            return true;
        }
        return false;
    }

    /**
     * Elimina una canción de la lista de favoritos
     *
     * @param cancion Canción a eliminar
     * @return true si se eliminó exitosamente, false si no existía
     */
    public boolean eliminarFavorito(Cancion cancion) {
        return listaFavoritos.remove(cancion);
    }

    /**
     * Verifica si una canción está en favoritos
     *
     * @param cancion Canción a verificar
     * @return true si está en favoritos, false en caso contrario
     */
    public boolean esFavorita(Cancion cancion) {
        return listaFavoritos.contains(cancion);
    }

    /**
     * Obtiene el número de canciones favoritas
     *
     * @return Cantidad de favoritos
     */
    public int getCantidadFavoritos() {
        return listaFavoritos.size();
    }

    /**
     * Verifica si el usuario es administrador
     *
     * @return true si es administrador, false en caso contrario
     */
    public boolean esAdministrador() {
        return tipoUsuario == TipoUsuario.ADMINISTRADOR;
    }

    /**
     * Calcula el hashCode basado en el username
     *
     * @return hashCode del usuario
     */
    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

    /**
     * Compara dos usuarios basándose en su username
     *
     * @param obj Objeto a comparar
     * @return true si los usuarios son iguales, false en caso contrario
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Usuario usuario = (Usuario) obj;
        return Objects.equals(username, usuario.username);
    }

    /**
     * Representación en String del usuario
     *
     * @return String con la información del usuario
     */
    @Override
    public String toString() {
        return String.format("Usuario: %s (%s) - %s - %d favoritos",
                username, nombre, tipoUsuario, listaFavoritos.size());
    }
}
