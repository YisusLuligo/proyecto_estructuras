package edu.universidad.estructuras.proyecto_estructura.service;

import edu.universidad.estructuras.proyecto_estructura.model.Cancion;
import edu.universidad.estructuras.proyecto_estructura.model.GrafoDeSimilitud;
import edu.universidad.estructuras.proyecto_estructura.model.TrieAutocompletado;

import java.io.*;
import java.util.*;

/**
 * Servicio para gestión del catálogo de canciones.
 * Carga canciones ÚNICAMENTE desde el archivo canciones.txt
 *
 * @author SyncUp Team
 * @version 1.0
 */
public class CancionService {
    private static CancionService instance;
    private HashMap<String, Cancion> catalogo;
    private static final String ARCHIVO_CANCIONES = "canciones.txt";
    private int contadorId;
    private TrieAutocompletado trieTitulos;
    private TrieAutocompletado trieArtistas;
    private GrafoDeSimilitud grafoSimilitud;

    /**
     * Constructor privado para patrón Singleton
     */
    private CancionService() {
        catalogo = new HashMap<>();
        trieTitulos = new TrieAutocompletado();
        trieArtistas = new TrieAutocompletado();
        grafoSimilitud = new GrafoDeSimilitud();
        contadorId = 1;

        // ✅ CARGAR SOLO DESDE ARCHIVO
        cargarCancionesDesdeArchivo();

        // Si no hay archivo o está vacío, crear uno con canciones de ejemplo
        if (catalogo.isEmpty()) {
            System.out.println("⚠️ No se encontraron canciones. Creando archivo inicial...");
            crearArchivoInicial();
            cargarCancionesDesdeArchivo();
        }

        construirGrafoSimilitud();
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
     * Crea un archivo inicial con canciones de ejemplo
     */
    private void crearArchivoInicial() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARCHIVO_CANCIONES))) {
            pw.println("# Base de Datos de Canciones - SyncUp");
            pw.println("# Formato: ID|Título|Artista|Género|Año|Duración|URL_YouTube");
            pw.println("# La duración está en formato decimal: 3.45 = 3 minutos 45 segundos");
            pw.println();
            pw.println("C0001|Bohemian Rhapsody|Queen|Rock|1975|5.55|https://www.youtube.com/watch?v=fJ9rUzIMcZQ");
            pw.println("C0002|Stairway to Heaven|Led Zeppelin|Rock|1971|8.02|https://www.youtube.com/watch?v=QkF3oxziUI4");
            pw.println("C0003|Imagine|John Lennon|Pop|1971|3.03|https://www.youtube.com/watch?v=YkgkThdzX-8");
            pw.println("C0004|Hotel California|Eagles|Rock|1977|6.30|https://www.youtube.com/watch?v=09839DpTctU");
            pw.println("C0005|Billie Jean|Michael Jackson|Pop|1983|4.54|https://www.youtube.com/watch?v=Zi_XLOBDo_Y");
            pw.println("C0006|Smells Like Teen Spirit|Nirvana|Rock|1991|5.01|https://www.youtube.com/watch?v=hTWKbfoikeg");
            pw.println("C0007|Hey Jude|The Beatles|Rock|1968|7.11|https://www.youtube.com/watch?v=A_MjCqQoLLA");
            pw.println("C0008|Sweet Child O' Mine|Guns N' Roses|Rock|1987|5.56|https://www.youtube.com/watch?v=1w7OgIMMRc4");
            pw.println("C0009|Purple Haze|Jimi Hendrix|Rock|1967|2.51|https://www.youtube.com/watch?v=WGoDaYjdfSg");
            pw.println("C0010|Wonderwall|Oasis|Rock|1995|4.18|https://www.youtube.com/watch?v=bx1Bh8ZvH84");
            System.out.println("✓ Archivo inicial creado con 10 canciones");
        } catch (IOException e) {
            System.err.println("✗ Error al crear archivo inicial: " + e.getMessage());
        }
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
     * Verifica que no exista una canción con el mismo título
     *
     * @param titulo Título de la canción
     * @param artista Artista
     * @param genero Género musical
     * @param anio Año de lanzamiento
     * @param duracion Duración en minutos
     * @param urlYoutube URL del video en YouTube
     * @return Canción creada, o null si ya existe
     */
    public Cancion agregarCancion(String titulo, String artista, String genero, int anio, double duracion, String urlYoutube) {
        // ✅ Verificar si ya existe una canción con el mismo título (ignorando mayúsculas/minúsculas)
        for (Cancion c : catalogo.values()) {
            if (c.getTitulo().equalsIgnoreCase(titulo.trim())) {
                System.out.println("  ⚠️ Canción duplicada (omitida): " + titulo);
                return null;
            }
        }

        String id = generarId();
        Cancion nuevaCancion = new Cancion(id, titulo, artista, genero, anio, duracion, urlYoutube);
        catalogo.put(id, nuevaCancion);

        // Agregar al Trie
        trieTitulos.insertar(titulo);
        trieArtistas.insertar(artista);

        grafoSimilitud.agregarCancion(nuevaCancion);
        reconstruirGrafoSimilitud();

        guardarCancionesEnArchivo();
        return nuevaCancion;
    }

    /**
     * Sobrecarga para mantener compatibilidad (sin URL)
     */
    public Cancion agregarCancion(String titulo, String artista, String genero, int anio, double duracion) {
        return agregarCancion(titulo, artista, genero, anio, duracion, "");
    }

    /**
     * Actualiza una canción existente
     */
    public boolean actualizarCancion(String id, String titulo, String artista, String genero, int anio, double duracion, String urlYoutube) {
        Cancion cancion = catalogo.get(id);
        if (cancion != null) {
            cancion.setTitulo(titulo);
            cancion.setArtista(artista);
            cancion.setGenero(genero);
            cancion.setAnio(anio);
            cancion.setDuracion(duracion);
            cancion.setUrlYoutube(urlYoutube);

            // Actualizar en Trie si cambió el título
            trieTitulos.insertar(titulo);
            trieArtistas.insertar(artista);

            guardarCancionesEnArchivo();
            return true;
        }
        return false;
    }
    public boolean actualizarCancion(String id, String titulo, String artista, String genero, int anio, double duracion) {
        return actualizarCancion(id, titulo, artista, genero, anio, duracion, "");
    }
    /**
     * Elimina una canción del catálogo
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
     */
    public Cancion obtenerCancion(String id) {
        return catalogo.get(id);
    }

    /**
     * Obtiene todas las canciones del catálogo
     */
    public List<Cancion> obtenerTodasLasCanciones() {
        return new ArrayList<>(catalogo.values());
    }

    /**
     * Busca canciones por título (búsqueda parcial)
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
     */
    public int getCantidadCanciones() {
        return catalogo.size();
    }

    /**
     * Carga canciones masivamente desde un archivo de texto
     * ✅ Formato: ID|Título|Artista|Género|Año|Duración|URL_YouTube
     * ✅ Omite duplicados por título
     *
     * @param rutaArchivo Ruta del archivo
     * @return Número de canciones cargadas exitosamente
     */
    public int cargarCancionesMasivamente(String rutaArchivo) {
        int cancionesCargadas = 0;
        int cancionesOmitidas = 0;
        int lineasError = 0;

        System.out.println("\n=== Carga Masiva de Canciones ===");
        System.out.println("Archivo: " + rutaArchivo);

        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            int numeroLinea = 0;

            while ((linea = br.readLine()) != null) {
                numeroLinea++;
                linea = linea.trim();

                // Ignorar líneas vacías y comentarios
                if (linea.isEmpty() || linea.startsWith("#")) {
                    continue;
                }

                String[] partes = linea.split("\\|");

                // Formato con 7 campos: ID|Título|Artista|Género|Año|Duración|URL
                if (partes.length == 7) {
                    try {
                        String id = partes[0].trim();
                        String titulo = partes[1].trim();
                        String artista = partes[2].trim();
                        String genero = partes[3].trim();
                        int anio = Integer.parseInt(partes[4].trim());
                        String duracionStr = partes[5].trim().replace(",", ".");
                        double duracion = Double.parseDouble(duracionStr);
                        String urlYoutube = partes[6].trim();

                        // Intentar agregar (verifica duplicados internamente)
                        Cancion resultado = agregarCancion(titulo, artista, genero, anio, duracion, urlYoutube);

                        if (resultado != null) {
                            cancionesCargadas++;
                            System.out.println("  ✓ Línea " + numeroLinea + ": " + titulo);
                        } else {
                            cancionesOmitidas++;
                        }
                    } catch (NumberFormatException e) {
                        lineasError++;
                        System.err.println("  ✗ Error línea " + numeroLinea + ": " + e.getMessage());
                    }
                }
                // Formato sin URL: ID|Título|Artista|Género|Año|Duración
                else if (partes.length == 6) {
                    try {
                        String id = partes[0].trim();
                        String titulo = partes[1].trim();
                        String artista = partes[2].trim();
                        String genero = partes[3].trim();
                        int anio = Integer.parseInt(partes[4].trim());
                        String duracionStr = partes[5].trim().replace(",", ".");
                        double duracion = Double.parseDouble(duracionStr);

                        Cancion resultado = agregarCancion(titulo, artista, genero, anio, duracion, "");

                        if (resultado != null) {
                            cancionesCargadas++;
                            System.out.println("  ✓ Línea " + numeroLinea + ": " + titulo + " (sin URL)");
                        } else {
                            cancionesOmitidas++;
                        }
                    } catch (NumberFormatException e) {
                        lineasError++;
                        System.err.println("  ✗ Error línea " + numeroLinea + ": " + e.getMessage());
                    }
                } else {
                    lineasError++;
                    System.err.println("  ✗ Formato incorrecto línea " + numeroLinea + " (" + partes.length + " campos)");
                }
            }

            System.out.println("\n=== Resumen de Carga ===");
            System.out.println("✓ Canciones agregadas: " + cancionesCargadas);
            System.out.println("⚠️ Canciones omitidas (duplicadas): " + cancionesOmitidas);
            System.out.println("✗ Líneas con error: " + lineasError);
            System.out.println("📊 Total en catálogo: " + getCantidadCanciones());

        } catch (IOException e) {
            System.err.println("✗ Error al leer archivo: " + e.getMessage());
        }

        return cancionesCargadas;
    }

    /**
     * Guarda las canciones en el archivo de texto
     */
    private void guardarCancionesEnArchivo() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARCHIVO_CANCIONES))) {
            pw.println("# Base de Datos de Canciones - SyncUp");
            pw.println("# Formato: ID|Título|Artista|Género|Año|Duración|URL_YouTube");
            pw.println("# La duración está en formato decimal: 3.45 = 3 minutos 45 segundos");
            pw.println();

            for (Cancion cancion : catalogo.values()) {
                pw.println(String.format("%s|%s|%s|%s|%d|%.2f|%s",
                        cancion.getId(),
                        cancion.getTitulo(),
                        cancion.getArtista(),
                        cancion.getGenero(),
                        cancion.getAnio(),
                        cancion.getDuracion(),
                        cancion.getUrlYoutube() != null ? cancion.getUrlYoutube() : ""
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
            System.out.println("⚠️ Archivo " + ARCHIVO_CANCIONES + " no encontrado");
            return;
        }

        System.out.println("📂 Cargando canciones desde " + ARCHIVO_CANCIONES + "...");
        int cargadas = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;

            while ((linea = br.readLine()) != null) {
                linea = linea.trim();

                // Ignorar líneas vacías y comentarios
                if (linea.isEmpty() || linea.startsWith("#")) {
                    continue;
                }

                String[] partes = linea.split("\\|");

                // Formato con URL: ID|Título|Artista|Género|Año|Duración|URL
                if (partes.length == 7) {
                    try {
                        String id = partes[0].trim();
                        String titulo = partes[1].trim();
                        String artista = partes[2].trim();
                        String genero = partes[3].trim();
                        int anio = Integer.parseInt(partes[4].trim());
                        String duracionStr = partes[5].trim().replace(",", ".");
                        double duracion = Double.parseDouble(duracionStr);
                        String urlYoutube = partes[6].trim();

                        Cancion cancion = new Cancion(id, titulo, artista, genero, anio, duracion, urlYoutube);
                        catalogo.put(id, cancion);

                        trieTitulos.insertar(titulo);
                        trieArtistas.insertar(artista);

                        if (id.startsWith("C")) {
                            int numId = Integer.parseInt(id.substring(1));
                            if (numId >= contadorId) {
                                contadorId = numId + 1;
                            }
                        }
                        cargadas++;
                    } catch (NumberFormatException e) {
                        System.err.println("Error al parsear línea: " + linea);
                    }
                }
                // Formato sin URL (compatibilidad): ID|Título|Artista|Género|Año|Duración
                else if (partes.length == 6) {
                    try {
                        String id = partes[0].trim();
                        String titulo = partes[1].trim();
                        String artista = partes[2].trim();
                        String genero = partes[3].trim();
                        int anio = Integer.parseInt(partes[4].trim());
                        String duracionStr = partes[5].trim().replace(",", ".");
                        double duracion = Double.parseDouble(duracionStr);

                        Cancion cancion = new Cancion(id, titulo, artista, genero, anio, duracion, "");
                        catalogo.put(id, cancion);

                        trieTitulos.insertar(titulo);
                        trieArtistas.insertar(artista);

                        if (id.startsWith("C")) {
                            int numId = Integer.parseInt(id.substring(1));
                            if (numId >= contadorId) {
                                contadorId = numId + 1;
                            }
                        }
                        cargadas++;
                    } catch (NumberFormatException e) {
                        System.err.println("Error al parsear línea: " + linea);
                    }
                }
            }

            System.out.println("✓ " + cargadas + " canciones cargadas desde archivo");

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

    /**
     * Obtiene sugerencias de títulos basadas en un prefijo
     */
    public List<String> autocompletarTitulos(String prefijo) {
        return trieTitulos.autocompletarConLimite(prefijo, 10);
    }

    /**
     * Obtiene sugerencias de artistas basadas en un prefijo
     */
    public List<String> autocompletarArtistas(String prefijo) {
        return trieArtistas.autocompletarConLimite(prefijo, 10);
    }

    /**
     * Busca canciones por autocompletado de título
     */
    public List<Cancion> buscarPorAutocompletado(String prefijo) {
        List<String> sugerencias = trieTitulos.autocompletar(prefijo);
        List<Cancion> resultados = new ArrayList<>();

        for (String titulo : sugerencias) {
            for (Cancion cancion : catalogo.values()) {
                if (cancion.getTitulo().equalsIgnoreCase(titulo)) {
                    resultados.add(cancion);
                    break;
                }
            }
        }

        return resultados;
    }

    /**
     * Construye el grafo de similitud conectando canciones similares
     */
    private void construirGrafoSimilitud() {
        List<Cancion> todasCanciones = obtenerTodasLasCanciones();

        for (Cancion cancion : todasCanciones) {
            grafoSimilitud.agregarCancion(cancion);
        }

        for (int i = 0; i < todasCanciones.size(); i++) {
            for (int j = i + 1; j < todasCanciones.size(); j++) {
                grafoSimilitud.agregarConexion(todasCanciones.get(i), todasCanciones.get(j));
            }
        }
    }

    /**
     * Reconstruye el grafo de similitud
     */
    public void reconstruirGrafoSimilitud() {
        grafoSimilitud.limpiar();
        construirGrafoSimilitud();
    }

    /**
     * Genera una lista de canciones similares para la función "Radio"
     */
    public List<Cancion> generarRadio(Cancion cancionInicial, int cantidad) {
        return grafoSimilitud.generarRadio(cancionInicial, cantidad);
    }

    /**
     * Obtiene canciones similares a una dada
     */
    public List<Cancion> obtenerCancionesSimilares(Cancion cancion, int limite) {
        return grafoSimilitud.encontrarCancionesSimilares(cancion, limite);
    }
}