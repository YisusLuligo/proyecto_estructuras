package edu.universidad.estructuras.proyecto_estructuras.service;

import edu.universidad.estructuras.proyecto_estructuras.model.Cancion;
import edu.universidad.estructuras.proyecto_estructuras.model.TrieAutocompletado;
import edu.universidad.estructuras.proyecto_estructuras.repository.CancionRepository;
import edu.universidad.estructuras.proyecto_estructuras.util.Constants;

import java.util.*;
import java.util.concurrent.*;

/**
 * Servicio de búsqueda de canciones.
 * RF-003: Autocompletado
 * RF-004: Búsqueda avanzada con AND/OR
 * RF-027: Uso de hilos
 */
public class BusquedaService {

    private CancionRepository cancionRepo;
    private TrieAutocompletado trieAutocompletado;
    private static BusquedaService instancia;

    private BusquedaService() {
        this.cancionRepo = CancionRepository.obtenerInstancia();
        this.trieAutocompletado = new TrieAutocompletado();
        inicializarTrie();
    }

    public static BusquedaService obtenerInstancia() {
        if (instancia == null) {
            instancia = new BusquedaService();
        }
        return instancia;
    }

    /**
     * Inicializa el Trie con todas las canciones.
     */
    private void inicializarTrie() {
        for (Cancion cancion : cancionRepo.obtenerTodas()) {
            trieAutocompletado.insertar(cancion.getTitulo());
            trieAutocompletado.insertar(cancion.getArtista());
        }
    }

    /**
     * RF-003: Autocompletado por título o artista.
     */
    public List<String> autocompletar(String prefijo) {
        List<String> resultados = trieAutocompletado.buscarPorPrefijo(prefijo);
        return resultados.size() > Constants.MAX_RESULTADOS_BUSQUEDA
                ? resultados.subList(0, Constants.MAX_RESULTADOS_BUSQUEDA)
                : resultados;
    }

    /**
     * RF-004 y RF-027: Búsqueda avanzada con hilos.
     *
     * @param artista Artista (puede ser null)
     * @param genero Género (puede ser null)
     * @param anio Año (puede ser null)
     * @param usarAND true para AND, false para OR
     */
    public List<Cancion> busquedaAvanzada(
            String artista,
            String genero,
            Integer anio,
            boolean usarAND) throws InterruptedException, ExecutionException {

        ExecutorService executor = Executors.newFixedThreadPool(3);
        List<Future<List<Cancion>>> futuros = new ArrayList<>();

        // Thread 1: Buscar por artista
        if (artista != null && !artista.trim().isEmpty()) {
            futuros.add(executor.submit(() -> cancionRepo.buscarPorArtista(artista)));
        }

        // Thread 2: Buscar por género
        if (genero != null && !genero.trim().isEmpty()) {
            futuros.add(executor.submit(() -> cancionRepo.buscarPorGenero(genero)));
        }

        // Thread 3: Buscar por año
        if (anio != null) {
            futuros.add(executor.submit(() -> cancionRepo.buscarPorAnio(anio)));
        }

        // Recopilar resultados
        List<List<Cancion>> resultados = new ArrayList<>();
        for (Future<List<Cancion>> futuro : futuros) {
            resultados.add(futuro.get());
        }

        executor.shutdown();

        if (resultados.isEmpty()) {
            return new ArrayList<>();
        }

        // Combinar con lógica AND u OR
        return usarAND ? interseccion(resultados) : union(resultados);
    }

    private List<Cancion> interseccion(List<List<Cancion>> listas) {
        if (listas.isEmpty()) return new ArrayList<>();

        Set<Cancion> resultado = new HashSet<>(listas.get(0));
        for (int i = 1; i < listas.size(); i++) {
            resultado.retainAll(new HashSet<>(listas.get(i)));
        }
        return new ArrayList<>(resultado);
    }

    private List<Cancion> union(List<List<Cancion>> listas) {
        Set<Cancion> resultado = new HashSet<>();
        for (List<Cancion> lista : listas) {
            resultado.addAll(lista);
        }
        return new ArrayList<>(resultado);
    }

    public void agregarCancionAIndice(Cancion cancion) {
        trieAutocompletado.insertar(cancion.getTitulo());
        trieAutocompletado.insertar(cancion.getArtista());
    }
}