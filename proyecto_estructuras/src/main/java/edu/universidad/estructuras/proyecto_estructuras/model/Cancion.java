package edu.universidad.estructuras.proyecto_estructuras.model;

import java.util.Objects;

public class Cancion {
    private final int id;
    private String titulo;
    private String artista;
    private String genero;
    private int anio;
    private int duracionSegundos;



    public Cancion(int id, String titulo, String artista,
                   String genero, int anio, int duracionSegundos) {
        this.id = id;
        this.titulo = titulo;
        this.artista = artista;
        this.genero = genero;
        this.anio = anio;
        this.duracionSegundos = duracionSegundos;
    }



    public int getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getArtista() {
        return artista;
    }

    public void setArtista(String artista) {
        this.artista = artista;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    public int getDuracionSegundos() {
        return duracionSegundos;
    }

    public void setDuracionSegundos(int duracionSegundos) {
        this.duracionSegundos = duracionSegundos;
    }


     // Convierte los segundos a formato bonito: "3:45"

    public String getDuracionFormato() {
        int minutos = duracionSegundos / 60;
        int segundos = duracionSegundos % 60;
        return String.format("%d:%02d", minutos, segundos);
    }


    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Cancion otra = (Cancion) obj;
        return id == otra.id;
    }


    @Override
    public String toString() {
        return String.format("%s - %s (%d) [%s]",
                titulo, artista, anio, getDuracionFormato());
    }
}