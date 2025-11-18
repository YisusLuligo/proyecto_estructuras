package edu.universidad.estructuras.proyecto_estructura.model;


import java.util.HashMap;
import java.util.Map;

/**
 * Nodo del Árbol Trie para autocompletado.
 * Cada nodo contiene un mapa de sus hijos y un marcador de fin de palabra.
 *
 */
public class TrieNode {
    private final Map<Character, TrieNode> hijos;
    private boolean esFinalDePalabra;
    private int frecuencia; // Para ordenar sugerencias por popularidad

    /**
     * Constructor del nodo Trie
     */
    public TrieNode() {
        this.hijos = new HashMap<>();
        this.esFinalDePalabra = false;
        this.frecuencia = 0;
    }

    /**
     * Obtiene el mapa de hijos del nodo
     *
     * @return Mapa de hijos
     */
    public Map<Character, TrieNode> getHijos() {
        return hijos;
    }

    /**
     * Verifica si el nodo marca el final de una palabra
     *
     * @return true si es final de palabra
     */
    public boolean esFinalDePalabra() {
        return esFinalDePalabra;
    }

    /**
     * Establece si el nodo marca el final de una palabra
     *
     * @param esFinalDePalabra valor a establecer
     */
    public void setEsFinalDePalabra(boolean esFinalDePalabra) {
        this.esFinalDePalabra = esFinalDePalabra;
    }

    /**
     * Obtiene la frecuencia de uso de la palabra
     *
     * @return frecuencia
     */
    public int getFrecuencia() {
        return frecuencia;
    }

    /**
     * Incrementa la frecuencia de uso
     */
    public void incrementarFrecuencia() {
        this.frecuencia++;
    }

    /**
     * Establece la frecuencia manualmente
     *
     * @param frecuencia nueva frecuencia
     */
    public void setFrecuencia(int frecuencia) {
        this.frecuencia = frecuencia;
    }
}
