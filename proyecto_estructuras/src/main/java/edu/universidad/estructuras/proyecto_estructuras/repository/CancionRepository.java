package edu.universidad.estructuras.proyecto_estructuras.repository;

import edu.universidad.estructuras.proyecto_estructuras.model.Cancion;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Repositorio para gestionar canciones del catálogo.
 * Usa índices múltiples para búsquedas eficientes.
 * Implementa patrón Singleton.
 *
 * @author Tu Nombre
 * @version 1.0
 */
public class CancionRepository {

    private Map<Integer, Cancion> canciones;           // Por ID
    private Map<String, List<Cancion>> indiceArtista;  // Por artista
    private Map<String, List<Cancion>> indiceGenero;   // Por género
    private Map<Integer, List<Cancion>> indiceAnio;    // Por año

    private static CancionRepository instancia;

    /**
     * Constructor privado (Singleton).
     */
    private CancionRepository() {
        this.canciones = new HashMap<>();
        this.indiceArtista = new HashMap<>();
        this.indiceGenero = new HashMap<>();
        this.indiceAnio = new HashMap<>();
    }

    /**
     * Obtiene la única instancia del repositorio.
     *
     * @return Instancia del repositorio
     */
    public static CancionRepository obtenerInstancia() {
        if (instancia == null) {
            instancia = new CancionRepository();
        }
        return instancia;
    }

    /**
     * Guarda una canción y actualiza todos los índices.
     * Complejidad: O(1) promedio
     *
     * @param cancion Canción a guardar
     */
    public void guardar(Cancion cancion) {
        canciones.put(cancion.getId(), cancion);

        // Actualizar índice por artista
        indiceArtista.computeIfAbsent(
                cancion.getArtista().toLowerCase(),
                k -> new ArrayList<>()
        ).add(cancion);

        // Actualizar índice por género
        indiceGenero.computeIfAbsent(
                cancion.getGenero().toLowerCase(),
                k -> new ArrayList<>()
        ).add(cancion);

        // Actualizar índice por año
        indiceAnio.computeIfAbsent(
                cancion.getAnio(),
                k -> new ArrayList<>()
        ).add(cancion);
    }

    /**
     * Busca una canción por ID.
     * Complejidad: O(1)
     *
     * @param id ID de la canción
     * @return Optional con la canción si existe
     */
    public Optional<Cancion> buscarPorId(int id) {
        return Optional.ofNullable(canciones.get(id));
    }

    /**
     * Busca canciones por artista (case-insensitive).
     * Complejidad: O(1) promedio
     *
     * @param artista Nombre del artista
     * @return Lista de canciones del artista
     */
    public List<Cancion> buscarPorArtista(String artista) {
        return indiceArtista.getOrDefault(
                artista.toLowerCase(),
                new ArrayList<>()
        );
    }

    /**
     * Busca canciones por género (case-insensitive).
     * Complejidad: O(1) promedio
     *
     * @param genero Género musical
     * @return Lista de canciones del género
     */
    public List<Cancion> buscarPorGenero(String genero) {
        return indiceGenero.getOrDefault(
                genero.toLowerCase(),
                new ArrayList<>()
        );
    }

    /**
     * Busca canciones por año.
     * Complejidad: O(1) promedio
     *
     * @param anio Año de lanzamiento
     * @return Lista de canciones del año
     */
    public List<Cancion> buscarPorAnio(int anio) {
        return indiceAnio.getOrDefault(anio, new ArrayList<>());
    }

    /**
     * Obtiene todas las canciones.
     * Complejidad: O(n)
     *
     * @return Lista con todas las canciones
     */
    public List<Cancion> obtenerTodas() {
        return new ArrayList<>(canciones.values());
    }

    /**
     * Elimina una canción y actualiza los índices.
     * Complejidad: O(n) en el peor caso
     *
     * @param id ID de la canción a eliminar
     * @return true si se eliminó, false si no existía
     */
    public boolean eliminar(int id) {
        Cancion cancion = canciones.remove(id);
        if (cancion != null) {
            // Actualizar índices
            indiceArtista.get(cancion.getArtista().toLowerCase()).remove(cancion);
            indiceGenero.get(cancion.getGenero().toLowerCase()).remove(cancion);
            indiceAnio.get(cancion.getAnio()).remove(cancion);
            return true;
        }
        return false;
    }

    /**
     * Actualiza una canción existente.
     * Complejidad: O(n) en el peor caso
     *
     * @param cancion Canción con datos actualizados
     */
    public void actualizar(Cancion cancion) {
        // Eliminar la versión antigua
        Cancion antigua = canciones.get(cancion.getId());
        if (antigua != null) {
            eliminar(antigua.getId());
        }
        // Guardar la nueva versión
        guardar(cancion);
    }

    /**
     * Cuenta el total de canciones.
     * Complejidad: O(1)
     *
     * @return Cantidad de canciones
     */
    public int contar() {
        return canciones.size();
    }

    /**
     * Busca canciones por múltiples criterios.
     *
     * @param artista Artista (puede ser null)
     * @param genero Género (puede ser null)
     * @param anio Año (puede ser null)
     * @return Lista de canciones que cumplen los criterios
     */
    public List<Cancion> buscarPorCriterios(String artista, String genero, Integer anio) {
        List<Cancion> resultado = new ArrayList<>(obtenerTodas());

        if (artista != null && !artista.trim().isEmpty()) {
            resultado = resultado.stream()
                    .filter(c -> c.getArtista().toLowerCase().contains(artista.toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (genero != null && !genero.trim().isEmpty()) {
            resultado = resultado.stream()
                    .filter(c -> c.getGenero().toLowerCase().contains(genero.toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (anio != null) {
            resultado = resultado.stream()
                    .filter(c -> c.getAnio() == anio)
                    .collect(Collectors.toList());
        }

        return resultado;
    }

    /**
     * Limpia el repositorio (útil para testing).
     */
    public void limpiar() {
        canciones.clear();
        indiceArtista.clear();
        indiceGenero.clear();
        indiceAnio.clear();
    }
}