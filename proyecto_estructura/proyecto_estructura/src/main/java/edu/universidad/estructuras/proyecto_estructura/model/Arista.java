package edu.universidad.estructuras.proyecto_estructura.model;

import edu.universidad.estructuras.proyecto_estructura.model.Cancion;

/**
 * Representa una arista (conexión) entre dos canciones en el grafo de similitud.
 * El peso representa el grado de similitud entre las canciones (menor peso = mayor similitud).
 *
 */
public class Arista implements Comparable<Arista> {
    private final Cancion destino;
    private double peso; // Peso de similitud (0-100, menor = más similar)

    /**
     * Constructor de la arista
     *
     * @param destino Canción destino
     * @param peso Peso de la arista (similitud)
     */
    public Arista(Cancion destino, double peso) {
        this.destino = destino;
        this.peso = peso;
    }

    /**
     * Obtiene la canción destino
     *
     * @return Canción destino
     */
    public Cancion getDestino() {
        return destino;
    }

    /**
     * Obtiene el peso de la arista
     *
     * @return Peso (similitud)
     */
    public double getPeso() {
        return peso;
    }

    /**
     * Establece el peso de la arista
     *
     * @param peso Nuevo peso
     */
    public void setPeso(double peso) {
        this.peso = peso;
    }

    /**
     * Compara aristas por peso (para ordenamiento)
     *
     * @param otra Otra arista
     * @return Comparación
     */
    @Override
    public int compareTo(Arista otra) {
        return Double.compare(this.peso, otra.peso);
    }

    @Override
    public String toString() {
        return String.format("%s (similitud: %.2f)", destino.getTitulo(), 100 - peso);
    }
}