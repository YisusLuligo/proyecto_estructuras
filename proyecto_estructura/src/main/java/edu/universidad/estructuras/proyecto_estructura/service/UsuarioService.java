package edu.universidad.estructuras.proyecto_estructura.service;



import edu.universidad.estructuras.proyecto_estructura.model.GrafoSocial;
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
    private GrafoSocial grafoSocial;

    /**
     * Constructor privado para patrón Singleton
     */
    private UsuarioService() {
        usuarios = new HashMap<>();
        grafoSocial = new GrafoSocial();
        inicializarUsuariosPorDefecto();

        for (Usuario usuario : usuarios.values()) {
            grafoSocial.agregarUsuario(usuario);
        }
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

        // Usuarios de prueba con perfiles variados
        Usuario usuario1 = new Usuario("user1", "pass123", "Juan Pérez");
        usuarios.put(usuario1.getUsername(), usuario1);

        Usuario usuario2 = new Usuario("maria_rock", "pass123", "María García");
        usuarios.put(usuario2.getUsername(), usuario2);

        Usuario usuario3 = new Usuario("carlos_pop", "pass123", "Carlos López");
        usuarios.put(usuario3.getUsername(), usuario3);

        Usuario usuario4 = new Usuario("ana_music", "pass123", "Ana Martínez");
        usuarios.put(usuario4.getUsername(), usuario4);

        Usuario usuario5 = new Usuario("pedro_metal", "pass123", "Pedro Rodríguez");
        usuarios.put(usuario5.getUsername(), usuario5);

        Usuario usuario6 = new Usuario("sofia_indie", "pass123", "Sofía Fernández");
        usuarios.put(usuario6.getUsername(), usuario6);

        Usuario usuario7 = new Usuario("lucas_jazz", "pass123", "Lucas Sánchez");
        usuarios.put(usuario7.getUsername(), usuario7);

        Usuario usuario8 = new Usuario("laura_pop", "pass123", "Laura Torres");
        usuarios.put(usuario8.getUsername(), usuario8);

        Usuario usuario9 = new Usuario("diego_rock", "pass123", "Diego Ramírez");
        usuarios.put(usuario9.getUsername(), usuario9);

        Usuario usuario10 = new Usuario("valentina_music", "pass123", "Valentina Cruz");
        usuarios.put(usuario10.getUsername(), usuario10);
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

        // ✅ NUEVO: Agregar al grafo social
        grafoSocial.agregarUsuario(nuevoUsuario);

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

        Usuario usuario = usuarios.get(username);
        if (usuario != null && usuario.esAdministrador()) {
            return false;
        }

        Usuario eliminado = usuarios.remove(username);

        if (eliminado != null) {
            // ✅ NUEVO: Eliminar del grafo social
            grafoSocial.eliminarUsuario(username);
            return true;
        }

        return false;
    }

    /**
     * Obtiene todos los usuarios del sistema
     *
     * @return Lista con todos los usuarios
     */
    public List<Usuario> obtenerTodosLosUsuarios() {
        return new ArrayList<>(usuarios.values());
    }

    /**
     * Hace que un usuario siga a otro
     *
     * @param seguidor Username del que sigue
     * @param seguido Username del que será seguido
     * @return true si se realizó la acción exitosamente
     */
    public boolean seguirUsuario(String seguidor, String seguido) {
        return grafoSocial.conectarUsuarios(seguidor, seguido);
    }

    /**
     * Hace que un usuario deje de seguir a otro
     *
     * @param seguidor Username del que sigue
     * @param seguido Username del que será dejado de seguir
     * @return true si se realizó la acción exitosamente
     */
    public boolean dejarDeSeguirUsuario(String seguidor, String seguido) {
        return grafoSocial.desconectarUsuarios(seguidor, seguido);
    }

    /**
     * Verifica si un usuario sigue a otro
     *
     * @param seguidor Username del que sigue
     * @param seguido Username del que puede estar siendo seguido
     * @return true si lo sigue
     */
    public boolean estaSiguiendo(String seguidor, String seguido) {
        return grafoSocial.estanConectados(seguidor, seguido);
    }

    /**
     * Obtiene la lista de usuarios que sigue un usuario
     *
     * @param username Username del usuario
     * @return Lista de usuarios seguidos
     */
    public List<Usuario> obtenerSeguidos(String username) {
        return grafoSocial.obtenerSeguidos(username);
    }

    /**
     * Obtiene la cantidad de usuarios que sigue
     *
     * @param username Username del usuario
     * @return Cantidad de seguidos
     */
    public int getCantidadSeguidos(String username) {
        return grafoSocial.getCantidadSeguidos(username);
    }

    /**
     * Obtiene la cantidad de seguidores de un usuario
     *
     * @param username Username del usuario
     * @return Cantidad de seguidores
     */
    public int getCantidadSeguidores(String username) {
        return grafoSocial.getCantidadSeguidores(username);
    }

    /**
     * Obtiene sugerencias de usuarios a seguir usando BFS (amigos de amigos)
     *
     * @param username Username del usuario
     * @param limite Número máximo de sugerencias
     * @return Lista de usuarios sugeridos
     */
    public List<Usuario> obtenerSugerenciasUsuarios(String username, int limite) {
        return grafoSocial.obtenerSugerencias(username, limite);
    }

    /**
     * Busca usuarios por username o nombre
     *
     * @param busqueda Texto a buscar
     * @return Lista de usuarios que coinciden
     */
    public List<Usuario> buscarUsuarios(String busqueda) {
        if (busqueda == null || busqueda.trim().isEmpty()) {
            return new ArrayList<>();
        }

        String busquedaLower = busqueda.toLowerCase();
        List<Usuario> resultados = new ArrayList<>();

        for (Usuario usuario : usuarios.values()) {
            if (usuario.getUsername().toLowerCase().contains(busquedaLower) ||
                    usuario.getNombre().toLowerCase().contains(busquedaLower)) {
                resultados.add(usuario);
            }
        }

        return resultados;
    }

    public void reconstruirGrafoSocial() {
        // Limpiar grafo existente
        grafoSocial.limpiar();

        // Agregar todos los usuarios al grafo
        for (Usuario usuario : usuarios.values()) {
            grafoSocial.agregarUsuario(usuario);
        }

        System.out.println("  ✓ Grafo social reconstruido con " + usuarios.size() + " usuarios");
    }
}
