package edu.universidad.estructuras.proyecto_estructuras.repository;

import edu.universidad.estructuras.proyecto_estructuras.model.Usuario;
import java.util.*;

/**
 * Repositorio para gestionar usuarios del sistema.
 * RF-014: Usa HashMap para acceso O(1) por username.
 * Implementa patrón Singleton.
 *
 * @author Tu Nombre
 * @version 1.0
 */
public class UsuarioRepository {

    // RF-014: HashMap<String, Usuario> para acceso O(1)
    private Map<String, Usuario> usuarios;
    private static UsuarioRepository instancia;

    /**
     * Constructor privado (Singleton).
     */
    private UsuarioRepository() {
        this.usuarios = new HashMap<>();
    }

    /**
     * Obtiene la única instancia del repositorio.
     *
     * @return Instancia del repositorio
     */
    public static UsuarioRepository obtenerInstancia() {
        if (instancia == null) {
            instancia = new UsuarioRepository();
        }
        return instancia;
    }

    /**
     * Guarda un usuario en el repositorio.
     * Complejidad: O(1)
     *
     * @param usuario Usuario a guardar
     */
    public void guardar(Usuario usuario) {
        usuarios.put(usuario.getUsername(), usuario);
    }

    /**
     * Busca un usuario por username.
     * Complejidad: O(1)
     *
     * @param username Nombre de usuario
     * @return Optional con el usuario si existe
     */
    public Optional<Usuario> buscarPorUsername(String username) {
        return Optional.ofNullable(usuarios.get(username));
    }

    /**
     * Verifica si existe un usuario con ese username.
     * Complejidad: O(1)
     *
     * @param username Nombre de usuario
     * @return true si existe, false si no
     */
    public boolean existe(String username) {
        return usuarios.containsKey(username);
    }

    /**
     * Elimina un usuario del repositorio.
     * Complejidad: O(1)
     *
     * @param username Nombre de usuario a eliminar
     * @return true si se eliminó, false si no existía
     */
    public boolean eliminar(String username) {
        return usuarios.remove(username) != null;
    }

    /**
     * Obtiene todos los usuarios.
     * Complejidad: O(n)
     *
     * @return Lista con todos los usuarios
     */
    public List<Usuario> obtenerTodos() {
        return new ArrayList<>(usuarios.values());
    }

    /**
     * Actualiza un usuario existente.
     * Complejidad: O(1)
     *
     * @param usuario Usuario actualizado
     */
    public void actualizar(Usuario usuario) {
        usuarios.put(usuario.getUsername(), usuario);
    }

    /**
     * Cuenta el total de usuarios.
     * Complejidad: O(1)
     *
     * @return Cantidad de usuarios
     */
    public int contar() {
        return usuarios.size();
    }

    /**
     * Limpia el repositorio (útil para testing).
     */
    public void limpiar() {
        usuarios.clear();
    }
}