package edu.universidad.estructuras.proyecto_estructuras.model;


public class Arista {
    private Cancion destino;    // A qué canción apunta esta arista
    private double peso;


    public Arista(Cancion destino, double peso) {
        this.destino = destino;
        this.peso = peso;
    }

    public Cancion getDestino() {
        return destino;
    }

    public void setDestino(Cancion destino) {
        this.destino = destino;
    }

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        this.peso = peso;
    }

    @Override
    public String toString() {
        return String.format("→ %s (similitud: %.2f)",
                destino.getTitulo(), peso);
    }
}