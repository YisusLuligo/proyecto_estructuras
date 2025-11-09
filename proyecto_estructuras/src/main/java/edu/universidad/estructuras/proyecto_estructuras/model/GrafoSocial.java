package edu.universidad.estructuras.proyecto_estructuras.model;


import java.util.*;

//Grafo de conexiones sociales entre usuarios
public class GrafoSocial {

    private Map<Usuario, Set<Usuario>> conexiones;


    public GrafoSocial() {
        this.conexiones = new HashMap<>();
    }


    public void agregarUsuario(Usuario usuario) {
        conexiones.putIfAbsent(usuario, new HashSet<>());
    }


    public boolean seguir(Usuario seguidor, Usuario seguido) {

        if (seguidor.equals(seguido)) {
            return false;
        }

        // Asegurar que ambos están en el grafo
        agregarUsuario(seguidor);
        agregarUsuario(seguido);

        // Crear la conexión bidireccional
        boolean agregado1 = conexiones.get(seguidor).add(seguido);
        conexiones.get(seguido).add(seguidor);

        return agregado1;
    }


    public boolean dejarDeSeguir(Usuario seguidor, Usuario seguido) {
        if (!conexiones.containsKey(seguidor) || !conexiones.containsKey(seguido)) {
            return false;
        }

        // Eliminar la conexión bidireccional
        boolean eliminado1 = conexiones.get(seguidor).remove(seguido);
        conexiones.get(seguido).remove(seguidor);

        return eliminado1;
    }


    public Set<Usuario> obtenerSeguidos(Usuario usuario) {
        return conexiones.getOrDefault(usuario, new HashSet<>());
    }


    public boolean sigueA(Usuario seguidor, Usuario seguido) {
        return conexiones.containsKey(seguidor) &&
                conexiones.get(seguidor).contains(seguido);
    }


    public int cantidadSeguidos(Usuario usuario) {
        return obtenerSeguidos(usuario).size();
    }


    public Set<Usuario> obtenerTodosUsuarios() {
        return conexiones.keySet();
    }


    public boolean contieneUsuario(Usuario usuario) {
        return conexiones.containsKey(usuario);
    }
}