package edu.universidad.estructuras.proyecto_estructura.service;

import edu.universidad.estructuras.proyecto_estructura.model.*;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Servicio para gestión de persistencia de datos
 * ORDEN DE CARGA: Canciones → Usuarios → Favoritos → Conexiones Sociales → Playlists
 *
 * @author SyncUp Team
 * @version 1.0
 */
public class PersistenciaService {
    private static PersistenciaService instance;

    private static final String DIRECTORIO_DATOS = "data";
    private static final String ARCHIVO_USUARIOS = DIRECTORIO_DATOS + "/usuarios.dat";
    private static final String ARCHIVO_FAVORITOS = DIRECTORIO_DATOS + "/favoritos.dat";
    private static final String ARCHIVO_PLAYLISTS = DIRECTORIO_DATOS + "/playlists.dat";
    private static final String ARCHIVO_CONEXIONES = DIRECTORIO_DATOS + "/conexiones.dat";

    private PersistenciaService() {
        crearDirectorioSiNoExiste();
    }

    public static PersistenciaService getInstance() {
        if (instance == null) {
            instance = new PersistenciaService();
        }
        return instance;
    }

    /**
     * Crea el directorio de datos si no existe
     */
    private void crearDirectorioSiNoExiste() {
        File directorio = new File(DIRECTORIO_DATOS);
        if (!directorio.exists()) {
            boolean creado = directorio.mkdir();
            if (creado) {
                System.out.println("✓ Directorio de datos creado");
            }
        }
    }

    /**
     * Guarda todos los datos del sistema
     */
    public void guardarTodo() {
        System.out.println("=== Guardando datos del sistema ===");
        guardarUsuarios();
        guardarFavoritos();
        guardarPlaylists();
        guardarConexionesSociales();
        System.out.println("=== Datos guardados exitosamente ===");
    }

    /**
     * Carga todos los datos del sistema en el orden correcto
     * IMPORTANTE: El orden es crucial para mantener las referencias
     */
    public void cargarTodo() {
        System.out.println("=== Cargando datos del sistema ===");

        // 1. Las canciones ya se cargan automáticamente en CancionService
        System.out.println("1. Canciones cargadas desde CancionService");

        // 2. Cargar usuarios (sin favoritos aún)
        cargarUsuarios();
        System.out.println("2. Usuarios cargados: " + UsuarioService.getInstance().getCantidadUsuarios());

        // 3. Cargar favoritos (ahora que tenemos usuarios y canciones)
        cargarFavoritos();
        System.out.println("3. Favoritos cargados");

        // 4. Cargar conexiones sociales (reconstruye el grafo)
        cargarConexionesSociales();
        System.out.println("4. Conexiones sociales cargadas");

        // 5. Cargar playlists
        cargarPlaylists();
        System.out.println("5. Playlists cargadas");

        System.out.println("=== Datos cargados exitosamente ===");
    }

    /**
     * Guarda los usuarios en archivo
     */
    public void guardarUsuarios() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARCHIVO_USUARIOS))) {
            UsuarioService usuarioService = UsuarioService.getInstance();
            HashMap<String, Usuario> usuarios = usuarioService.getUsuarios();

            int guardados = 0;
            for (Usuario usuario : usuarios.values()) {
                // Formato: username|password|nombre|tipoUsuario
                pw.println(String.format("%s|%s|%s|%s",
                        usuario.getUsername(),
                        usuario.getPassword(),
                        usuario.getNombre(),
                        usuario.getTipoUsuario().toString()
                ));
                guardados++;
            }
            System.out.println("  ✓ " + guardados + " usuarios guardados");
        } catch (IOException e) {
            System.err.println("  ✗ Error al guardar usuarios: " + e.getMessage());
        }
    }

    /**
     * Carga los usuarios desde archivo
     * NO carga los usuarios por defecto si ya existen en el archivo
     */
    public void cargarUsuarios() {
        File archivo = new File(ARCHIVO_USUARIOS);
        if (!archivo.exists()) {
            System.out.println("  ! No hay archivo de usuarios, usando valores por defecto");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            UsuarioService usuarioService = UsuarioService.getInstance();

            // Limpiar usuarios existentes (excepto los que queremos mantener)
            HashMap<String, Usuario> usuariosTemp = new HashMap<>();

            String linea;
            int cargados = 0;

            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split("\\|");
                if (partes.length == 4) {
                    String username = partes[0];
                    String password = partes[1];
                    String nombre = partes[2];
                    Usuario.TipoUsuario tipo = Usuario.TipoUsuario.valueOf(partes[3]);

                    Usuario usuario = new Usuario(username, password, nombre, tipo);
                    usuariosTemp.put(username, usuario);
                    cargados++;
                }
            }

            // Reemplazar el mapa de usuarios
            if (!usuariosTemp.isEmpty()) {
                usuarioService.getUsuarios().clear();
                usuarioService.getUsuarios().putAll(usuariosTemp);
            }

            System.out.println("  ✓ " + cargados + " usuarios cargados");

        } catch (IOException e) {
            System.err.println("  ✗ Error al cargar usuarios: " + e.getMessage());
        }
    }

    /**
     * Guarda las canciones favoritas de cada usuario
     */
    public void guardarFavoritos() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARCHIVO_FAVORITOS))) {
            UsuarioService usuarioService = UsuarioService.getInstance();

            int guardados = 0;
            for (Usuario usuario : usuarioService.getUsuarios().values()) {
                for (Cancion cancion : usuario.getListaFavoritos()) {
                    // Formato: username|cancionId
                    pw.println(String.format("%s|%s",
                            usuario.getUsername(),
                            cancion.getId()
                    ));
                    guardados++;
                }
            }
            System.out.println("  ✓ " + guardados + " favoritos guardados");
        } catch (IOException e) {
            System.err.println("  ✗ Error al guardar favoritos: " + e.getMessage());
        }
    }

    /**
     * Carga las canciones favoritas de cada usuario
     */
    public void cargarFavoritos() {
        File archivo = new File(ARCHIVO_FAVORITOS);
        if (!archivo.exists()) {
            System.out.println("  ! No hay archivo de favoritos");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            UsuarioService usuarioService = UsuarioService.getInstance();
            CancionService cancionService = CancionService.getInstance();
            String linea;
            int cargados = 0;
            int errores = 0;

            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split("\\|");
                if (partes.length == 2) {
                    String username = partes[0];
                    String cancionId = partes[1];

                    Usuario usuario = usuarioService.obtenerUsuario(username);
                    Cancion cancion = cancionService.obtenerCancion(cancionId);

                    if (usuario != null && cancion != null) {
                        usuario.agregarFavorito(cancion);
                        cargados++;
                    } else {
                        errores++;
                        if (usuario == null) {
                            System.out.println("    ! Usuario no encontrado: " + username);
                        }
                        if (cancion == null) {
                            System.out.println("    ! Canción no encontrada: " + cancionId);
                        }
                    }
                }
            }
            System.out.println("  ✓ " + cargados + " favoritos cargados" +
                    (errores > 0 ? " (" + errores + " errores)" : ""));
        } catch (IOException e) {
            System.err.println("  ✗ Error al cargar favoritos: " + e.getMessage());
        }
    }

    /**
     * Guarda las playlists de los usuarios
     */
    public void guardarPlaylists() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARCHIVO_PLAYLISTS))) {
            PlaylistService playlistService = PlaylistService.getInstance();
            UsuarioService usuarioService = UsuarioService.getInstance();

            int playlistsGuardadas = 0;
            int cancionesGuardadas = 0;

            for (Usuario usuario : usuarioService.getUsuarios().values()) {
                List<Playlist> playlists = playlistService.obtenerPlaylistsDeUsuario(usuario.getUsername());

                for (Playlist playlist : playlists) {
                    // Línea de playlist: PLAYLIST|id|nombre|descripcion|propietario|fechaCreacion
                    pw.println(String.format("PLAYLIST|%s|%s|%s|%s|%s",
                            playlist.getId(),
                            escapar(playlist.getNombre()),
                            escapar(playlist.getDescripcion()),
                            playlist.getUsuarioPropietario(),
                            playlist.getFechaCreacion().toString()
                    ));
                    playlistsGuardadas++;

                    // Líneas de canciones: CANCION|playlistId|cancionId
                    for (Cancion cancion : playlist.getCanciones()) {
                        pw.println(String.format("CANCION|%s|%s",
                                playlist.getId(),
                                cancion.getId()
                        ));
                        cancionesGuardadas++;
                    }
                }
            }
            System.out.println("  ✓ " + playlistsGuardadas + " playlists y " +
                    cancionesGuardadas + " canciones en playlists guardadas");
        } catch (IOException e) {
            System.err.println("  ✗ Error al guardar playlists: " + e.getMessage());
        }
    }

    /**
     * Carga las playlists desde archivo
     */
    public void cargarPlaylists() {
        File archivo = new File(ARCHIVO_PLAYLISTS);
        if (!archivo.exists()) {
            System.out.println("  ! No hay archivo de playlists");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            PlaylistService playlistService = PlaylistService.getInstance();
            CancionService cancionService = CancionService.getInstance();
            String linea;
            Map<String, Playlist> playlistsMap = new HashMap<>();
            int playlistsCargadas = 0;
            int cancionesCargadas = 0;

            // Limpiar playlists existentes
            playlistService.obtenerTodosLosUsuarios().forEach(username ->
                    playlistService.limpiarPlaylistsDeUsuario(username)
            );

            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split("\\|");

                if (partes[0].equals("PLAYLIST") && partes.length >= 5) {
                    String id = partes[1];
                    String nombre = desescapar(partes[2]);
                    String descripcion = partes.length > 3 ? desescapar(partes[3]) : "";
                    String propietario = partes.length > 4 ? partes[4] : "";

                    Playlist playlist = new Playlist(id, nombre, descripcion, propietario);

                    // Establecer fecha de creación si existe
                    if (partes.length > 5) {
                        try {
                            playlist.setFechaCreacion(LocalDateTime.parse(partes[5]));
                        } catch (Exception e) {
                            // Usar fecha actual si hay error
                        }
                    }

                    playlistsMap.put(id, playlist);

                    // Agregar al servicio
                    List<Playlist> playlistsUsuario = playlistService.obtenerPlaylistsDeUsuario(propietario);
                    playlistsUsuario.add(playlist);
                    playlistsCargadas++;

                } else if (partes[0].equals("CANCION") && partes.length == 3) {
                    String playlistId = partes[1];
                    String cancionId = partes[2];

                    Playlist playlist = playlistsMap.get(playlistId);
                    Cancion cancion = cancionService.obtenerCancion(cancionId);

                    if (playlist != null && cancion != null) {
                        playlist.agregarCancion(cancion);
                        cancionesCargadas++;
                    }
                }
            }
            System.out.println("  ✓ " + playlistsCargadas + " playlists y " +
                    cancionesCargadas + " canciones en playlists cargadas");
        } catch (IOException e) {
            System.err.println("  ✗ Error al cargar playlists: " + e.getMessage());
        }
    }

    /**
     * Guarda las conexiones sociales (seguimientos)
     */
    public void guardarConexionesSociales() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARCHIVO_CONEXIONES))) {
            UsuarioService usuarioService = UsuarioService.getInstance();

            int guardados = 0;
            Set<String> conexionesGuardadas = new HashSet<>(); // Para evitar duplicados

            for (Usuario usuario : usuarioService.getUsuarios().values()) {
                List<Usuario> seguidos = usuarioService.obtenerSeguidos(usuario.getUsername());

                for (Usuario seguido : seguidos) {
                    // Crear clave única para evitar duplicados
                    String clave1 = usuario.getUsername() + "|" + seguido.getUsername();
                    String clave2 = seguido.getUsername() + "|" + usuario.getUsername();

                    // Solo guardar si no hemos guardado esta conexión
                    if (!conexionesGuardadas.contains(clave1) && !conexionesGuardadas.contains(clave2)) {
                        pw.println(clave1);
                        conexionesGuardadas.add(clave1);
                        guardados++;
                    }
                }
            }
            System.out.println("  ✓ " + guardados + " conexiones sociales guardadas");
        } catch (IOException e) {
            System.err.println("  ✗ Error al guardar conexiones: " + e.getMessage());
        }
    }

    /**
     * Carga las conexiones sociales desde archivo
     * IMPORTANTE: Debe llamarse DESPUÉS de cargar usuarios
     */
    public void cargarConexionesSociales() {
        File archivo = new File(ARCHIVO_CONEXIONES);
        if (!archivo.exists()) {
            System.out.println("  ! No hay archivo de conexiones sociales");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            UsuarioService usuarioService = UsuarioService.getInstance();

            // Primero reconstruir el grafo con todos los usuarios
            usuarioService.reconstruirGrafoSocial();

            String linea;
            int cargados = 0;
            int errores = 0;

            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split("\\|");
                if (partes.length == 2) {
                    String seguidor = partes[0];
                    String seguido = partes[1];

                    // Verificar que ambos usuarios existen
                    if (usuarioService.existeUsuario(seguidor) && usuarioService.existeUsuario(seguido)) {
                        if (usuarioService.seguirUsuario(seguidor, seguido)) {
                            cargados++;
                        }
                    } else {
                        errores++;
                        System.out.println("    ! Usuario no encontrado: " + seguidor + " o " + seguido);
                    }
                }
            }
            System.out.println("  ✓ " + cargados + " conexiones sociales cargadas" +
                    (errores > 0 ? " (" + errores + " errores)" : ""));
        } catch (IOException e) {
            System.err.println("  ✗ Error al cargar conexiones: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Escapa caracteres especiales en strings
     */
    private String escapar(String texto) {
        if (texto == null) return "";
        return texto.replace("|", "\\|").replace("\n", "\\n");
    }

    /**
     * Desescapa caracteres especiales
     */
    private String desescapar(String texto) {
        if (texto == null) return "";
        return texto.replace("\\|", "|").replace("\\n", "\n");
    }
}