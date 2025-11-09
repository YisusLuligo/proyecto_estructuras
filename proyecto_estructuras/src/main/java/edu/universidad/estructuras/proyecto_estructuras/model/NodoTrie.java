package edu.universidad.estructuras.proyecto_estructuras.model;


import java.util.*;

// - Sirve para autocompletar palabras RAPIDÍSIMO

public class NodoTrie {

    // HashMap que guarda: letra → siguiente nodo
    private Map<Character, NodoTrie> hijos;



    // ¿Este nodo marca el final de una palabra?
    private boolean esFinalPalabra;

    public NodoTrie() {
        this.hijos = new HashMap<>();
        this.esFinalPalabra = false;
    }



    public Map<Character, NodoTrie> getHijos() {
        return hijos;
    }

    public boolean esFinPalabra() {
        return esFinalPalabra;
    }

    public void marcarComoFinal() {
        this.esFinalPalabra = true;
    }



   //Obtiene o crea un hijo para una letra. Si ya existe el nodo de esa letra, lo devuelve, Si no existe, lo crea y lo devuelve


    public NodoTrie obtenerOCrearHijo(char letra) {
        hijos.putIfAbsent(letra, new NodoTrie());
        return hijos.get(letra);
    }


    public boolean tieneHijo(char letra) {
        return hijos.containsKey(letra);
    }


    public NodoTrie getHijo(char letra) {
        return hijos.get(letra);
    }
}