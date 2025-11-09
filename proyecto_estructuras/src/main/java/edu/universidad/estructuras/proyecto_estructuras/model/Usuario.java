package edu.universidad.estructuras.proyecto_estructuras.model;

import java.util.*;

public class Usuario {
    private final String username;
    private String password;
    private String nombre;
    private String correo;
    private TipoUsuario tipo;
    private LinkedList<Cancion> listaFavoritos;


    public Usuario(String username, String password,
                   String nombre, TipoUsuario tipo) {
        this.username = username;
        this.password = password;
        this.nombre = nombre;
        this.tipo = tipo;
        this.listaFavoritos = new LinkedList<>();
    }


    public Usuario(String username, String password, String nombre) {
        this(username, password, nombre, TipoUsuario.USUARIO);
    }


    public String getUsername() {
        return username;
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

    public TipoUsuario getTipo() {
        return tipo;
    }

    public void setTipo(TipoUsuario tipo) {
        this.tipo = tipo;
    }

    public LinkedList<Cancion> getListaFavoritos() {
        return listaFavoritos;
    }



    public boolean agregarFavorito(Cancion cancion) {
        if (!listaFavoritos.contains(cancion)) {
            listaFavoritos.add(cancion);
            return true;
        }
        return false;
    }


    public boolean eliminarFavorito(Cancion cancion) {
        return listaFavoritos.remove(cancion);
    }


    public boolean esFavorita(Cancion cancion) {
        return listaFavoritos.contains(cancion);
    }


    public boolean esAdministrador() {
        return tipo == TipoUsuario.ADMINISTRADOR;
    }


    public int getCantidadFavoritos() {
        return listaFavoritos.size();
    }

    /*
      hashCode basado en username.
      Lo usamos para meter usuarios en HashMap.
     */
    @Override
    public int hashCode() {
        return Objects.hash(username);
    }


     // Dos usuarios son iguales si tienen el mismo username.

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Usuario otro = (Usuario) obj;
        return username.equals(otro.username);
    }


    @Override
    public String toString() {
        return String.format("%s (@%s) - %d favoritos",
                nombre, username, listaFavoritos.size());
    }
}