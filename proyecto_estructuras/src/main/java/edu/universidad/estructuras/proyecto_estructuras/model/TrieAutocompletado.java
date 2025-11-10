package edu.universidad.estructuras.proyecto_estructuras.model;

import java.util.*;

public class TrieAutocompletado {
    private NodoTrie raiz;


    public TrieAutocompletado() {
        this.raiz = new NodoTrie();
    }

    public void insertar(String palabra) {
        if (palabra == null || palabra.isEmpty()) {
            return;
        }

        // Convertir a minúsculas para que la búsqueda no sea case-sensitive
        palabra = palabra.toLowerCase().trim();

        NodoTrie nodoActual = raiz;

        // Recorrer cada letra
        for (char letra : palabra.toCharArray()) {
            nodoActual = nodoActual.obtenerOCrearHijo(letra);
        }

        // Marcar el último nodo como fin de palabra
        nodoActual.marcarComoFinal();
    }



     // Busca TODAS las palabras que empiezan con un prefijo.
    public List<String> buscarPorPrefijo(String prefijo) {
        List<String> resultados = new ArrayList<>();

        if (prefijo == null || prefijo.isEmpty()) {
            return resultados;
        }

        // Convertir a minúsculas
        prefijo = prefijo.toLowerCase().trim();

        // PASO 1: Navegar hasta el nodo del prefijo
        NodoTrie nodoActual = raiz;
        for (char letra : prefijo.toCharArray()) {
            if (!nodoActual.tieneHijo(letra)) {
                return resultados; // El prefijo no existe
            }
            nodoActual = nodoActual.getHijo(letra);
        }

        // PASO 2: Desde ese nodo, recolectar todas las palabras
        recolectarPalabras(nodoActual, prefijo, resultados);

        return resultados;
    }


    private void recolectarPalabras(NodoTrie nodo, String palabraActual,
                                    List<String> resultados) {
        // Si este nodo marca fin de palabra, agrégala
        if (nodo.esFinPalabra()) {
            resultados.add(palabraActual);
        }

        // Explorar todos los hijos (todas las letras posibles)
        for (Map.Entry<Character, NodoTrie> entry : nodo.getHijos().entrySet()) {
            char letra = entry.getKey();
            NodoTrie hijo = entry.getValue();

            // RECURSIÓN: agregar la letra y seguir explorando
            recolectarPalabras(hijo, palabraActual + letra, resultados);
        }
    }


     // Verifica si una palabra exacta existe.

    public boolean existe(String palabra) {
        if (palabra == null || palabra.isEmpty()) {
            return false;
        }

        palabra = palabra.toLowerCase().trim();
        NodoTrie nodoActual = raiz;

        // Navegar letra por letra
        for (char letra : palabra.toCharArray()) {
            if (!nodoActual.tieneHijo(letra)) {
                return false; // No existe
            }
            nodoActual = nodoActual.getHijo(letra);
        }

        // Existe si terminamos en un nodo marcado como final
        return nodoActual.esFinPalabra();
    }



    // * Ejemplo: contarPorPrefijo("bo") → 3
     //* (si hay 3 canciones que empiezan con "bo")

    public int contarPorPrefijo(String prefijo) {
        return buscarPorPrefijo(prefijo).size();
    }


    public void limpiar() {
        this.raiz = new NodoTrie();
    }
}