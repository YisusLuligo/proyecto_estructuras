package edu.universidad.estructuras.proyecto_estructuras.service;

import edu.universidad.estructuras.proyecto_estructuras.algoritmos.BFS;
import edu.universidad.estructuras.proyecto_estructuras.model.GrafoSocial;
import edu.universidad.estructuras.proyecto_estructuras.model.Usuario;
import edu.universidad.estructuras.proyecto_estructuras.util.Constants;

import java.util.*;

/**
 * Servicio de conexiones sociales.
 * RF-007: Seguir/dejar de seguir
 * RF-008: Sugerencias de usuarios
 */
public class SocialService {

    private GrafoSocial grafoSocial;
    private static SocialService instancia;

    private SocialService() {
        this.grafoSocial = new GrafoSocial();
    }

    public static SocialService obtenerInstancia() {
        if (instancia == null) {
            instancia = new SocialService();
        }
        return instancia;
    }

    public void agregarUsuario(Usuario usuario) {
        grafoSocial.agregarUsuario(usuario);
    }

    /**
     * RF-007: Seguir a otro usuario.
     */
    public boolean seguir(Usuario seguidor, Usuario seguido) {
        return grafoSocial.seguir(seguidor, seguido);
    }

    /**
     * RF-007: Dejar de seguir.
     */
    public boolean dejarDeSeguir(Usuario seguidor, Usuario seguido) {
        return grafoSocial.dejarDeSeguir(seguidor, seguido);
    }

    public Set<Usuario> obtenerParceros(Usuario usuario) {
        return grafoSocial.obtenerSeguidos(usuario);
    }

    public boolean sonParceros(Usuario u1, Usuario u2) {
        return grafoSocial.sigueA(u1, u2);
    }

    /**
     * RF-008: Genera sugerencias de parceros.
     */
    public List<Usuario> generarSugerencias(Usuario usuario) {
        return BFS.encontrarSugerenciasAmigos(
                grafoSocial,
                usuario,
                Constants.MAX_SUGERENCIAS_PARCEROS
        );
    }

    public int gradoSeparacion(Usuario u1, Usuario u2) {
        return BFS.calcularDistancia(grafoSocial, u1, u2);
    }
}