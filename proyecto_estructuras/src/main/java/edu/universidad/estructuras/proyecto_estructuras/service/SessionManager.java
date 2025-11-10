package edu.universidad.estructuras.proyecto_estructuras.service;

import edu.universidad.estructuras.proyecto_estructuras.model.Usuario;

/**
 * Gestor de sesión de usuario (Singleton).
 * Mantiene el usuario actualmente autenticado.
 *
 * @author Tu Nombre
 * @version 1.0
 */
public class SessionManager {

    private Usuario usuarioActual;
    private static SessionManager instancia;

    private SessionManager() {
        this.usuarioActual = null;
    }

    public static SessionManager obtenerInstancia() {
        if (instancia == null) {
            instancia = new SessionManager();
        }
        return instancia;
    }

    public void establecerSesion(Usuario usuario) {
        this.usuarioActual = usuario;
    }

    public void cerrarSesion() {
        this.usuarioActual = null;
    }

    public Usuario obtenerUsuarioActual() {
        return usuarioActual;
    }

    public boolean haySesion() {
        return usuarioActual != null;
    }

    public boolean esAdministrador() {
        return haySesion() && usuarioActual.esAdministrador();
    }
}