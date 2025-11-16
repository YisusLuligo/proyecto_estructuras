package edu.universidad.estructuras.proyecto_estructura.service;



import edu.universidad.estructuras.proyecto_estructura.model.Usuario;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Servicio para gestión de usuarios del sistema.
 * Mantiene un HashMap para acceso O(1) a los usuarios.
 *
 * @author SyncUp Team
 * @version 1.0
 */
public class UsuarioService {
    private static UsuarioService instance;
    private HashMap<String, Usuario> usuarios;
    private Usuario usuarioActual;

    /**
     * Constructor privado para patrón Singleton
     */
    private UsuarioService() {
        usuarios = new HashMap<>();
        inicializarUsuariosPorDefecto();
    }

    /**
     * Obtiene la instancia única del servicio (Singleton)
     *
     * @return Instancia de UsuarioService
     */
    public static UsuarioService getInstance() {
        if (instance == null) {
            instance = new UsuarioService();
        }
        return instance;
    }

    /**
     * Inicializa usuarios por defecto para pruebas
     */
    private void inicializarUsuariosPorDefecto() {
        // Usuario administrador por defecto
        Usuario admin = new Usuario("admin", "admin123", "Administrador",
                Usuario.TipoUsuario.ADMINISTRADOR);
        usuarios.put(admin.getUsername(), admin);

        // Usuario de prueba
        Usuario usuario1 = new Usuario("user1", "pass123", "Usuario Demo");
        usuarios.put(usuario1.getUsername(), usuario1);
    }

    /**
     * Registra un nuevo usuario en el sistema
     *
     * @param username Nombre de usuario
     * @param password Contraseña
     * @param nombre Nombre completo
     * @return true si se registró exitosamente, false si el username ya existe
     */
    public boolean registrarUsuario(String username, String password, String nombre) {
        if (usuarios.containsKey(username)) {
            return false;
        }
        Usuario nuevoUsuario = new Usuario(username, password, nombre);
        usuarios.put(username, nuevoUsuario);
        return true;
    }

    /**
     * Inicia sesión de un usuario
     *
     * @param username Nombre de usuario
     * @param password Contraseña
     * @return Usuario si las credenciales son correctas, null en caso contrario
     */
    public Usuario iniciarSesion(String username, String password) {
        Usuario usuario = usuarios.get(username);
        if (usuario != null && usuario.getPassword().equals(password)) {
            usuarioActual = usuario;
            return usuario;
        }
        return null;
    }

    /**
     * Cierra la sesión del usuario actual
     */
    public void cerrarSesion() {
        usuarioActual = null;
    }

    /**
     * Obtiene el usuario actualmente logueado
     *
     * @return Usuario actual o null si no hay sesión activa
     */
    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    /**
     * Verifica si existe un usuario con el username dado
     *
     * @param username Nombre de usuario a verificar
     * @return true si existe, false en caso contrario
     */
    public boolean existeUsuario(String username) {
        return usuarios.containsKey(username);
    }

    /**
     * Obtiene un usuario por su username
     *
     * @param username Nombre de usuario
     * @return Usuario si existe, null en caso contrario
     */
    public Usuario obtenerUsuario(String username) {
        return usuarios.get(username);
    }

    /**
     * Actualiza la contraseña de un usuario
     *
     * @param username Nombre de usuario
     * @param nuevaPassword Nueva contraseña
     * @return true si se actualizó exitosamente
     */
    public boolean actualizarPassword(String username, String nuevaPassword) {
        Usuario usuario = usuarios.get(username);
        if (usuario != null) {
            usuario.setPassword(nuevaPassword);
            return true;
        }
        return false;
    }

    /**
     * Actualiza el nombre de un usuario
     *
     * @param username Nombre de usuario
     * @param nuevoNombre Nuevo nombre
     * @return true si se actualizó exitosamente
     */
    public boolean actualizarNombre(String username, String nuevoNombre) {
        Usuario usuario = usuarios.get(username);
        if (usuario != null) {
            usuario.setNombre(nuevoNombre);
            return true;
        }
        return false;
    }

    /**
     * Obtiene el HashMap de todos los usuarios
     *
     * @return HashMap con todos los usuarios
     */
    public HashMap<String, Usuario> getUsuarios() {
        return usuarios;
    }

    /**
     * Obtiene la cantidad total de usuarios registrados
     *
     * @return Número de usuarios
     */
    public int getCantidadUsuarios() {
        return usuarios.size();
    }

    /**
     * Elimina un usuario del sistema
     *
     * @param username Username del usuario a eliminar
     * @return true si se eliminó exitosamente
     */
    public boolean eliminarUsuario(String username) {
        if (username == null || username.isEmpty()) {
            return false;
        }

        // No permitir eliminar al admin
        Usuario usuario = usuarios.get(username);
        if (usuario != null && usuario.esAdministrador()) {
            return false;
        }

        Usuario eliminado = usuarios.remove(username);
        return eliminado != null;
    }

    /**
     * Obtiene todos los usuarios del sistema
     *
     * @return Lista con todos los usuarios
     */
    public List<Usuario> obtenerTodosLosUsuarios() {
        return new ArrayList<>(usuarios.values());
    }
}
