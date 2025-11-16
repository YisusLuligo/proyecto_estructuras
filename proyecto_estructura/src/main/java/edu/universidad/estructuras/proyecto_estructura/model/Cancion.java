package edu.universidad.estructuras.proyecto_estructura.model;


import java.util.Objects;

/**
 * Representa una pista musical en el catálogo de SyncUp.
 * Funciona como nodo en el Grafo de Similitud.
 *
 * @author SyncUp Team
 * @version 1.0
 */
public class Cancion {
    private String id;
    private String titulo;
    private String artista;
    private String genero;
    private int anio;
    private double duracion; // En minutos

    /**
     * Constructor completo de Cancion
     *
     * @param id Identificador único de la canción
     * @param titulo Título de la canción
     * @param artista Nombre del artista
     * @param genero Género musical
     * @param anio Año de lanzamiento
     * @param duracion Duración en minutos
     */
    public Cancion(String id, String titulo, String artista, String genero, int anio, double duracion) {
        this.id = id;
        this.titulo = titulo;
        this.artista = artista;
        this.genero = genero;
        this.anio = anio;
        this.duracion = duracion;
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public double getDuracion() {
        return duracion;
    }

    public void setDuracion(double duracion) {
        this.duracion = duracion;
    }

    /**
     * Calcula el hashCode basado en el ID de la canción
     *
     * @return hashCode de la canción
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Compara dos canciones basándose en su ID
     *
     * @param obj Objeto a comparar
     * @return true si las canciones son iguales, false en caso contrario
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Cancion cancion = (Cancion) obj;
        return Objects.equals(id, cancion.id);
    }

    /**
     * Representación en String de la canción
     *
     * @return String con la información de la canción
     */
    @Override
    public String toString() {
        return String.format("%s - %s (%d) [%s] - %.2f min",
                titulo, artista, anio, genero, duracion);
    }

    /**
     * Formatea la duración de minutos a formato MM:SS
     *
     * @return String con formato MM:SS
     */
    public String getDuracionFormateada() {
        int minutos = (int) duracion;
        int segundos = (int) ((duracion - minutos) * 60);
        return String.format("%d:%02d", minutos, segundos);
    }
}
