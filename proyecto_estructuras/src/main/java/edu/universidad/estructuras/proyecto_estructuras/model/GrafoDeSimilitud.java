package edu.universidad.estructuras.proyecto_estructuras.model;



import java.util.*;


public class GrafoDeSimilitud {

    // HashMap que guarda: Canción → Lista de canciones conectadas
    private Map<Cancion, List<Arista>> adyacencias;


    public GrafoDeSimilitud() {
        this.adyacencias = new HashMap<>();
    }


    public void agregarCancion(Cancion cancion) {
        adyacencias.putIfAbsent(cancion, new ArrayList<>());
    }

    //Conecta dos canciones con un peso de similitud.

    public void agregarArista(Cancion cancion1, Cancion cancion2, double similitud) {
        // Asegurarse de que ambas canciones existen en el grafo
        agregarCancion(cancion1);
        agregarCancion(cancion2);

        // Crear arista de cancion1 → cancion2
        adyacencias.get(cancion1).add(new Arista(cancion2, similitud));

        // Crear arista de cancion2 → cancion1 (grafo no dirigido)
        adyacencias.get(cancion2).add(new Arista(cancion1, similitud));
    }


    public List<Arista> obtenerVecinos(Cancion cancion) {
        return adyacencias.getOrDefault(cancion, new ArrayList<>());
    }


    public Set<Cancion> obtenerTodasCanciones() {
        return adyacencias.keySet();
    }


    public boolean contieneCancion(Cancion cancion) {
        return adyacencias.containsKey(cancion);
    }


     // Cuenta cuántas canciones hay en el grafo.

    public int cantidadCanciones() {
        return adyacencias.size();
    }

     //Cuenta cuántas conexiones tiene una canción.

    public int gradoDeCancion(Cancion cancion) {
        return obtenerVecinos(cancion).size();
    }


    //Elimina una canción y todas sus conexiones.

    public void eliminarCancion(Cancion cancion) {
        // Eliminar la canción de las listas de sus vecinos
        List<Arista> vecinos = obtenerVecinos(cancion);
        for (Arista arista : vecinos) {
            Cancion vecino = arista.getDestino();
            adyacencias.get(vecino).removeIf(a -> a.getDestino().equals(cancion));
        }

        // Eliminar la canción del grafo
        adyacencias.remove(cancion);
    }
}