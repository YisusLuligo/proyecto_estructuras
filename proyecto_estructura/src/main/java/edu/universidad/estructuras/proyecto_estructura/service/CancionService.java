package edu.universidad.estructuras.proyecto_estructura.service;

import edu.universidad.estructuras.proyecto_estructura.model.Cancion;
import java.io.*;
import java.util.*;

/**
 * Servicio para gestión del catálogo de canciones.
 * Mantiene un HashMap para acceso O(1) y persistencia en archivo.
 *
 * @author SyncUp Team
 * @version 1.0
 */
public class CancionService {
    private static CancionService instance;
    private HashMap<String, Cancion> catalogo;
    private static final String ARCHIVO_CANCIONES = "canciones.txt";
    private int contadorId;

    /**
     * Constructor privado para patrón Singleton
     */
    private CancionService() {
        catalogo = new HashMap<>();
        contadorId = 1;
        cargarCancionesDesdeArchivo();

        // Si no hay canciones, inicializar con datos de ejemplo
        if (catalogo.isEmpty()) {
            inicializarCancionesPorDefecto();
        }
    }

    /**
     * Obtiene la instancia única del servicio (Singleton)
     *
     * @return Instancia de CancionService
     */
    public static CancionService getInstance() {
        if (instance == null) {
            instance = new CancionService();
        }
        return instance;
    }

    /**
     * Inicializa canciones por defecto para demostración
     */
    private void inicializarCancionesPorDefecto() {
        agregarCancion("Bohemian Rhapsody", "Queen", "Rock", 1975, 5.55);
        agregarCancion("Stairway to Heaven", "Led Zeppelin", "Rock", 1971, 8.02);
        agregarCancion("Imagine", "John Lennon", "Pop", 1971, 3.03);
        agregarCancion("Hotel California", "Eagles", "Rock", 1977, 6.30);
        agregarCancion("Billie Jean", "Michael Jackson", "Pop", 1983, 4.54);
        agregarCancion("Smells Like Teen Spirit", "Nirvana", "Rock", 1991, 5.01);
        agregarCancion("Hey Jude", "The Beatles", "Rock", 1968, 7.11);
        agregarCancion("Sweet Child O' Mine", "Guns N' Roses", "Rock", 1987, 5.56);
        agregarCancion("Purple Haze", "Jimi Hendrix", "Rock", 1967, 2.51);
        agregarCancion("Wonderwall", "Oasis", "Rock", 1995, 4.18);
    }

    /**
     * Genera un ID único para una nueva canción
     *
     * @return ID generado
     */
    private String generarId() {
        return String.format("C%04d", contadorId++);
    }

    /**
     * Agrega una nueva canción al catálogo
     *
     * @param titulo Título de la canción
     * @param artista Artista
     * @param genero Género musical
     * @param anio Año de lanzamiento
     * @param duracion Duración en minutos
     * @return Canción creada, o null si ya existe
     */
    public Cancion agregarCancion(String titulo, String artista, String genero, int anio, double duracion) {
        // Verificar si ya existe una canción con el mismo título y artista
        for (Cancion c : catalogo.values()) {
            if (c.getTitulo().equalsIgnoreCase(titulo) && c.getArtista().equalsIgnoreCase(artista)) {
                return null; // Ya existe
            }
        }

        String id = generarId();
        Cancion nuevaCancion = new Cancion(id, titulo, artista, genero, anio, duracion);
        catalogo.put(id, nuevaCancion);
        guardarCancionesEnArchivo();
        return nuevaCancion;
    }

    /**
     * Actualiza una canción existente
     *
     * @param id ID de la canción
     * @param titulo Nuevo título
     * @param artista Nuevo artista
     * @param genero Nuevo género
     * @param anio Nuevo año
     * @param duracion Nueva duración
     * @return true si se actualizó exitosamente
     */
    public boolean actualizarCancion(String id, String titulo, String artista, String genero, int anio, double duracion) {
        Cancion cancion = catalogo.get(id);
        if (cancion != null) {
            cancion.setTitulo(titulo);
            cancion.setArtista(artista);
            cancion.setGenero(genero);
            cancion.setAnio(anio);
            cancion.setDuracion(duracion);
            guardarCancionesEnArchivo();
            return true;
        }
        return false;
    }

    /**
     * Elimina una canción del catálogo
     *
     * @param id ID de la canción
     * @return true si se eliminó exitosamente
     */
    public boolean eliminarCancion(String id) {
        Cancion eliminada = catalogo.remove(id);
        if (eliminada != null) {
            guardarCancionesEnArchivo();
            return true;
        }
        return false;
    }

    /**
     * Obtiene una canción por su ID
     *
     * @param id ID de la canción
     * @return Canción si existe, null en caso contrario
     */
    public Cancion obtenerCancion(String id) {
        return catalogo.get(id);
    }

    /**
     * Obtiene todas las canciones del catálogo
     *
     * @return Lista con todas las canciones
     */
    public List<Cancion> obtenerTodasLasCanciones() {
        return new ArrayList<>(catalogo.values());
    }

    /**
     * Busca canciones por título (búsqueda parcial)
     *
     * @param titulo Título o parte del título
     * @return Lista de canciones que coinciden
     */
    public List<Cancion> buscarPorTitulo(String titulo) {
        List<Cancion> resultados = new ArrayList<>();
        String tituloBusqueda = titulo.toLowerCase();

        for (Cancion cancion : catalogo.values()) {
            if (cancion.getTitulo().toLowerCase().contains(tituloBusqueda)) {
                resultados.add(cancion);
            }
        }

        return resultados;
    }

    /**
     * Busca canciones por artista (búsqueda parcial)
     *
     * @param artista Artista o parte del nombre
     * @return Lista de canciones que coinciden
     */
    public List<Cancion> buscarPorArtista(String artista) {
        List<Cancion> resultados = new ArrayList<>();
        String artistaBusqueda = artista.toLowerCase();

        for (Cancion cancion : catalogo.values()) {
            if (cancion.getArtista().toLowerCase().contains(artistaBusqueda)) {
                resultados.add(cancion);
            }
        }

        return resultados;
    }

    /**
     * Busca canciones por género
     *
     * @param genero Género musical
     * @return Lista de canciones del género
     */
    public List<Cancion> buscarPorGenero(String genero) {
        List<Cancion> resultados = new ArrayList<>();
        String generoBusqueda = genero.toLowerCase();

        for (Cancion cancion : catalogo.values()) {
            if (cancion.getGenero().toLowerCase().contains(generoBusqueda)) {
                resultados.add(cancion);
            }
        }

        return resultados;
    }

    /**
     * Busca canciones por año
     *
     * @param anio Año de lanzamiento
     * @return Lista de canciones del año
     */
    public List<Cancion> buscarPorAnio(int anio) {
        List<Cancion> resultados = new ArrayList<>();

        for (Cancion cancion : catalogo.values()) {
            if (cancion.getAnio() == anio) {
                resultados.add(cancion);
            }
        }

        return resultados;
    }

    /**
     * Obtiene todos los géneros únicos del catálogo
     *
     * @return Set con todos los géneros
     */
    public Set<String> obtenerGenerosUnicos() {
        Set<String> generos = new HashSet<>();
        for (Cancion cancion : catalogo.values()) {
            generos.add(cancion.getGenero());
        }
        return generos;
    }

    /**
     * Obtiene la cantidad total de canciones
     *
     * @return Número de canciones en el catálogo
     */
    public int getCantidadCanciones() {
        return catalogo.size();
    }

    /**
     * Carga canciones masivamente desde un archivo de texto
     * Formato: titulo|artista|genero|anio|duracion
     *
     * @param rutaArchivo Ruta del archivo
     * @return Número de canciones cargadas exitosamente
     */
    public int cargarCancionesMasivamente(String rutaArchivo) {
        int cancionesCargadas = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;

            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty() || linea.startsWith("#")) {
                    continue; // Ignorar líneas vacías y comentarios
                }

                String[] partes = linea.split("\\|");
                if (partes.length == 5) {
                    try {
                        String titulo = partes[0].trim();
                        String artista = partes[1].trim();
                        String genero = partes[2].trim();
                        int anio = Integer.parseInt(partes[3].trim());
                        double duracion = Double.parseDouble(partes[4].trim());

                        if (agregarCancion(titulo, artista, genero, anio, duracion) != null) {
                            cancionesCargadas++;
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("Error al parsear línea: " + linea);
                    }
                }
            }

            guardarCancionesEnArchivo();

        } catch (IOException e) {
            System.err.println("Error al leer archivo: " + e.getMessage());
        }

        return cancionesCargadas;
    }

    /**
     * Guarda las canciones en un archivo de texto
     */
    private void guardarCancionesEnArchivo() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARCHIVO_CANCIONES))) {
            for (Cancion cancion : catalogo.values()) {
                pw.println(String.format("%s|%s|%s|%s|%d|%.2f",
                        cancion.getId(),
                        cancion.getTitulo(),
                        cancion.getArtista(),
                        cancion.getGenero(),
                        cancion.getAnio(),
                        cancion.getDuracion()
                ));
            }
        } catch (IOException e) {
            System.err.println("Error al guardar canciones: " + e.getMessage());
        }
    }

    /**
     * Carga las canciones desde el archivo de texto
     */
    private void cargarCancionesDesdeArchivo() {
        File archivo = new File(ARCHIVO_CANCIONES);
        if (!archivo.exists()) {
            return; // No hay archivo, se inicializará con datos por defecto
        }

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;

            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split("\\|");
                if (partes.length == 6) {
                    try {
                        String id = partes[0].trim();
                        String titulo = partes[1].trim();
                        String artista = partes[2].trim();
                        String genero = partes[3].trim();
                        int anio = Integer.parseInt(partes[4].trim());
                        double duracion = Double.parseDouble(partes[5].trim());

                        Cancion cancion = new Cancion(id, titulo, artista, genero, anio, duracion);
                        catalogo.put(id, cancion);

                        // Actualizar contador de ID
                        if (id.startsWith("C")) {
                            int numId = Integer.parseInt(id.substring(1));
                            if (numId >= contadorId) {
                                contadorId = numId + 1;
                            }
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("Error al parsear línea: " + linea);
                    }
                }
            }

        } catch (IOException e) {
            System.err.println("Error al cargar canciones: " + e.getMessage());
        }
    }

    /**
     * Limpia el catálogo completo
     */
    public void limpiarCatalogo() {
        catalogo.clear();
        guardarCancionesEnArchivo();
    }
}