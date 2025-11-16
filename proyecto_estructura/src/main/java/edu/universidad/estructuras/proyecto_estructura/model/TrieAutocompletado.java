package edu.universidad.estructuras.proyecto_estructura.model;


import java.util.*;

/**
 * Árbol de Prefijos (Trie) para autocompletado eficiente de títulos de canciones.
 * Soporta inserción, búsqueda y sugerencias de palabras con un prefijo dado.
 *
 * Complejidad temporal:
 * - Inserción: O(m) donde m es la longitud de la palabra
 * - Búsqueda: O(m)
 * - Autocompletado: O(p + n) donde p es la longitud del prefijo y n es el número de resultados
 *
 * @author SyncUp Team
 * @version 1.0
 */
public class TrieAutocompletado {
    private TrieNode raiz;
    private int cantidadPalabras;

    /**
     * Constructor del Trie
     */
    public TrieAutocompletado() {
        this.raiz = new TrieNode();
        this.cantidadPalabras = 0;
    }

    /**
     * Inserta una palabra en el Trie
     * Complejidad: O(m) donde m es la longitud de la palabra
     *
     * @param palabra Palabra a insertar
     */
    public void insertar(String palabra) {
        if (palabra == null || palabra.trim().isEmpty()) {
            return;
        }

        // Normalizar: convertir a minúsculas y limpiar espacios
        palabra = palabra.toLowerCase().trim();

        TrieNode nodoActual = raiz;

        // Recorrer cada carácter de la palabra
        for (char c : palabra.toCharArray()) {
            // Si el carácter no existe, crear nuevo nodo
            nodoActual.getHijos().putIfAbsent(c, new TrieNode());
            // Avanzar al siguiente nodo
            nodoActual = nodoActual.getHijos().get(c);
        }

        // Marcar el final de la palabra
        if (!nodoActual.esFinalDePalabra()) {
            nodoActual.setEsFinalDePalabra(true);
            cantidadPalabras++;
        }

        // Incrementar frecuencia para ordenar sugerencias
        nodoActual.incrementarFrecuencia();
    }

    /**
     * Busca si una palabra completa existe en el Trie
     * Complejidad: O(m) donde m es la longitud de la palabra
     *
     * @param palabra Palabra a buscar
     * @return true si la palabra existe, false en caso contrario
     */
    public boolean buscar(String palabra) {
        if (palabra == null || palabra.trim().isEmpty()) {
            return false;
        }

        palabra = palabra.toLowerCase().trim();
        TrieNode nodo = buscarNodo(palabra);
        return nodo != null && nodo.esFinalDePalabra();
    }

    /**
     * Busca si existe alguna palabra con el prefijo dado
     * Complejidad: O(m) donde m es la longitud del prefijo
     *
     * @param prefijo Prefijo a buscar
     * @return true si existe al menos una palabra con ese prefijo
     */
    public boolean existePrefijo(String prefijo) {
        if (prefijo == null || prefijo.trim().isEmpty()) {
            return false;
        }

        prefijo = prefijo.toLowerCase().trim();
        return buscarNodo(prefijo) != null;
    }

    /**
     * Obtiene todas las palabras que comienzan con el prefijo dado
     * Complejidad: O(p + n*k) donde p es longitud del prefijo, n es número de resultados, k es longitud promedio
     *
     * @param prefijo Prefijo a buscar
     * @return Lista de palabras que comienzan con el prefijo, ordenadas por frecuencia
     */
    public List<String> autocompletar(String prefijo) {
        List<String> resultados = new ArrayList<>();

        if (prefijo == null) {
            return resultados;
        }

        prefijo = prefijo.toLowerCase().trim();

        // Si el prefijo está vacío, retornar lista vacía
        if (prefijo.isEmpty()) {
            return resultados;
        }

        // Buscar el nodo que corresponde al prefijo
        TrieNode nodo = buscarNodo(prefijo);

        if (nodo == null) {
            return resultados; // No hay palabras con ese prefijo
        }

        // Recolectar todas las palabras desde ese nodo
        recolectarPalabras(nodo, prefijo, resultados);

        // Ordenar por frecuencia (más usadas primero)
        resultados.sort((a, b) -> {
            TrieNode nodoA = buscarNodo(a);
            TrieNode nodoB = buscarNodo(b);
            int freqA = nodoA != null ? nodoA.getFrecuencia() : 0;
            int freqB = nodoB != null ? nodoB.getFrecuencia() : 0;
            return Integer.compare(freqB, freqA); // Descendente
        });

        return resultados;
    }

    /**
     * Obtiene las N palabras más relevantes con el prefijo dado
     *
     * @param prefijo Prefijo a buscar
     * @param limite Número máximo de resultados
     * @return Lista con hasta N sugerencias
     */
    public List<String> autocompletarConLimite(String prefijo, int limite) {
        List<String> todas = autocompletar(prefijo);
        return todas.size() > limite ? todas.subList(0, limite) : todas;
    }

    /**
     * Elimina una palabra del Trie
     * Complejidad: O(m) donde m es la longitud de la palabra
     *
     * @param palabra Palabra a eliminar
     * @return true si se eliminó exitosamente
     */
    public boolean eliminar(String palabra) {
        if (palabra == null || palabra.trim().isEmpty()) {
            return false;
        }

        palabra = palabra.toLowerCase().trim();
        return eliminarRecursivo(raiz, palabra, 0);
    }

    /**
     * Elimina una palabra de forma recursiva
     *
     * @param nodoActual Nodo actual en la recursión
     * @param palabra Palabra a eliminar
     * @param indice Índice actual en la palabra
     * @return true si el nodo puede ser eliminado
     */
    private boolean eliminarRecursivo(TrieNode nodoActual, String palabra, int indice) {
        if (indice == palabra.length()) {
            // Llegamos al final de la palabra
            if (!nodoActual.esFinalDePalabra()) {
                return false; // La palabra no existe
            }

            nodoActual.setEsFinalDePalabra(false);
            cantidadPalabras--;

            // Retornar true si no tiene hijos (se puede eliminar)
            return nodoActual.getHijos().isEmpty();
        }

        char c = palabra.charAt(indice);
        TrieNode nodo = nodoActual.getHijos().get(c);

        if (nodo == null) {
            return false; // La palabra no existe
        }

        boolean debeEliminarNodoActual = eliminarRecursivo(nodo, palabra, indice + 1);

        if (debeEliminarNodoActual) {
            nodoActual.getHijos().remove(c);
            // Retornar true si no tiene hijos y no es final de otra palabra
            return nodoActual.getHijos().isEmpty() && !nodoActual.esFinalDePalabra();
        }

        return false;
    }

    /**
     * Busca el nodo que corresponde a una palabra o prefijo
     *
     * @param str Palabra o prefijo
     * @return Nodo encontrado o null si no existe
     */
    private TrieNode buscarNodo(String str) {
        TrieNode nodoActual = raiz;

        for (char c : str.toCharArray()) {
            TrieNode nodo = nodoActual.getHijos().get(c);
            if (nodo == null) {
                return null; // No existe el camino
            }
            nodoActual = nodo;
        }

        return nodoActual;
    }

    /**
     * Recolecta todas las palabras desde un nodo dado (DFS)
     *
     * @param nodo Nodo desde donde buscar
     * @param prefijo Prefijo acumulado hasta ahora
     * @param resultados Lista donde agregar los resultados
     */
    private void recolectarPalabras(TrieNode nodo, String prefijo, List<String> resultados) {
        // Si este nodo marca el final de una palabra, agregarla
        if (nodo.esFinalDePalabra()) {
            resultados.add(prefijo);
        }

        // Recorrer todos los hijos recursivamente
        for (Map.Entry<Character, TrieNode> entrada : nodo.getHijos().entrySet()) {
            recolectarPalabras(entrada.getValue(), prefijo + entrada.getKey(), resultados);
        }
    }

    /**
     * Limpia todo el Trie
     */
    public void limpiar() {
        this.raiz = new TrieNode();
        this.cantidadPalabras = 0;
    }

    /**
     * Obtiene la cantidad de palabras almacenadas
     *
     * @return Número de palabras
     */
    public int getCantidadPalabras() {
        return cantidadPalabras;
    }

    /**
     * Verifica si el Trie está vacío
     *
     * @return true si no contiene palabras
     */
    public boolean estaVacio() {
        return cantidadPalabras == 0;
    }

    /**
     * Obtiene todas las palabras almacenadas en el Trie
     *
     * @return Lista con todas las palabras
     */
    public List<String> obtenerTodasLasPalabras() {
        List<String> palabras = new ArrayList<>();
        recolectarPalabras(raiz, "", palabras);
        return palabras;
    }

    /**
     * Representación en String del Trie
     *
     * @return Información del Trie
     */
    @Override
    public String toString() {
        return String.format("Trie[palabras=%d]", cantidadPalabras);
    }
}