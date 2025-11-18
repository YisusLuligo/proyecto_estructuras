package edu.universidad.estructuras.proyecto_estructura.service;

import edu.universidad.estructuras.proyecto_estructura.model.*;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Servicio para gestión de persistencia de datos
 * ✅ CORREGIDO: Carga y guarda playlists correctamente
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

    private void crearDirectorioSiNoExiste() {
        File directorio = new File(DIRECTORIO_DATOS);
        if (!directorio.exists()) {
            boolean creado = directorio.mkdir();
            if (creado) {
                System.out.println("✓ Directorio de datos creado");
            }
        }
    }

    public void guardarTodo() {
        System.out.println("\n=== Guardando datos del sistema ===");
        guardarUsuarios();
        guardarFavoritos();
        guardarPlaylists();
        guardarConexionesSociales();
        System.out.println("=== Datos guardados exitosamente ===\n");
    }

    public void cargarTodo() {
        System.out.println("\n=== Cargando datos del sistema ===");

        // 1. Canciones
        System.out.println("1. Canciones cargadas desde CancionService");

        // 2. Usuarios
        cargarUsuarios();
        System.out.println("2. Usuarios cargados: " + UsuarioService.getInstance().getCantidadUsuarios());

        // 3. Favoritos
        cargarFavoritos();
        System.out.println("3. Favoritos cargados");

        // 4. Playlists
        cargarPlaylists();
        System.out.println("4. Playlists cargadas");

        // 5. Conexiones sociales
        cargarConexionesSociales();
        System.out.println("5. Conexiones sociales cargadas");

        System.out.println("=== Datos cargados exitosamente ===\n");
    }

    public void guardarUsuarios() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARCHIVO_USUARIOS))) {
            UsuarioService usuarioService = UsuarioService.getInstance();
            HashMap<String, Usuario> usuarios = usuarioService.getUsuarios();

            int guardados = 0;
            for (Usuario usuario : usuarios.values()) {
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

    public void cargarUsuarios() {
        File archivo = new File(ARCHIVO_USUARIOS);
        if (!archivo.exists()) {
            System.out.println("  ! No hay archivo de usuarios");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            UsuarioService usuarioService = UsuarioService.getInstance();
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

            if (!usuariosTemp.isEmpty()) {
                usuarioService.getUsuarios().clear();
                usuarioService.getUsuarios().putAll(usuariosTemp);
            }

            System.out.println("  ✓ " + cargados + " usuarios cargados");

        } catch (IOException e) {
            System.err.println("  ✗ Error al cargar usuarios: " + e.getMessage());
        }
    }

    public void guardarFavoritos() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARCHIVO_FAVORITOS))) {
            UsuarioService usuarioService = UsuarioService.getInstance();

            int guardados = 0;
            for (Usuario usuario : usuarioService.getUsuarios().values()) {
                for (Cancion cancion : usuario.getListaFavoritos()) {
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
                        usuario.getListaFavoritos().add(cancion);
                        cargados++;
                    } else {
                        errores++;
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
     * ✅ CORREGIDO: Guarda todas las playlists correctamente
     */
    public void guardarPlaylists() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARCHIVO_PLAYLISTS))) {
            PlaylistService playlistService = PlaylistService.getInstance();

            int playlistsGuardadas = 0;
            int cancionesGuardadas = 0;

            // Iterar sobre el mapa completo
            Map<String, List<Playlist>> todasPlaylists = playlistService.getPlaylistsPorUsuario();

            System.out.println("  → Guardando playlists de " + todasPlaylists.size() + " usuarios");

            for (Map.Entry<String, List<Playlist>> entry : todasPlaylists.entrySet()) {
                String username = entry.getKey();
                List<Playlist> playlists = entry.getValue();

                System.out.println("  → Usuario: " + username + " tiene " + playlists.size() + " playlists");

                for (Playlist playlist : playlists) {
                    // Línea de playlist
                    pw.println(String.format("PLAYLIST|%s|%s|%s|%s|%s",
                            playlist.getId(),
                            escapar(playlist.getNombre()),
                            escapar(playlist.getDescripcion()),
                            playlist.getUsuarioPropietario(),
                            playlist.getFechaCreacion().toString()
                    ));
                    playlistsGuardadas++;

                    // Líneas de canciones
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
                    cancionesGuardadas + " canciones guardadas");

        } catch (IOException e) {
            System.err.println("  ✗ Error al guardar playlists: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * ✅ CORREGIDO: Carga playlists y actualiza el contador de IDs
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

            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split("\\|");

                if (partes[0].equals("PLAYLIST") && partes.length >= 5) {
                    String id = partes[1];
                    String nombre = desescapar(partes[2]);
                    String descripcion = partes.length > 3 ? desescapar(partes[3]) : "";
                    String propietario = partes.length > 4 ? partes[4] : "";

                    Playlist playlist = new Playlist(id, nombre, descripcion, propietario);

                    // Establecer fecha de creación
                    if (partes.length > 5) {
                        try {
                            playlist.setFechaCreacion(LocalDateTime.parse(partes[5]));
                        } catch (Exception e) {
                            // Usar fecha actual si hay error
                        }
                    }

                    playlistsMap.put(id, playlist);

                    // ✅ CRÍTICO: Agregar directamente a la lista del usuario
                    playlistService.obtenerPlaylistsDeUsuario(propietario).add(playlist);

                    // Actualizar contador de IDs
                    playlistService.actualizarContadorId(id);

                    playlistsCargadas++;
                    System.out.println("  → Playlist cargada: " + nombre + " (propietario: " + propietario + ")");

                } else if (partes[0].equals("CANCION") && partes.length == 3) {
                    String playlistId = partes[1];
                    String cancionId = partes[2];

                    Playlist playlist = playlistsMap.get(playlistId);
                    Cancion cancion = cancionService.obtenerCancion(cancionId);

                    if (playlist != null && cancion != null) {
                        playlist.getCanciones().add(cancion);
                        cancionesCargadas++;
                    }
                }
            }

            System.out.println("  ✓ " + playlistsCargadas + " playlists y " +
                    cancionesCargadas + " canciones cargadas");

        } catch (IOException e) {
            System.err.println("  ✗ Error al cargar playlists: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void guardarConexionesSociales() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARCHIVO_CONEXIONES))) {
            UsuarioService usuarioService = UsuarioService.getInstance();

            int guardados = 0;
            Set<String> conexionesGuardadas = new HashSet<>();

            for (Usuario usuario : usuarioService.getUsuarios().values()) {
                List<Usuario> seguidos = usuarioService.obtenerSeguidos(usuario.getUsername());

                for (Usuario seguido : seguidos) {
                    String clave1 = usuario.getUsername() + "|" + seguido.getUsername();
                    String clave2 = seguido.getUsername() + "|" + usuario.getUsername();

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

    public void cargarConexionesSociales() {
        File archivo = new File(ARCHIVO_CONEXIONES);
        if (!archivo.exists()) {
            System.out.println("  ! No hay archivo de conexiones sociales");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            UsuarioService usuarioService = UsuarioService.getInstance();
            usuarioService.reconstruirGrafoSocial();

            String linea;
            int cargados = 0;
            int errores = 0;

            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split("\\|");
                if (partes.length == 2) {
                    String seguidor = partes[0];
                    String seguido = partes[1];

                    if (usuarioService.existeUsuario(seguidor) && usuarioService.existeUsuario(seguido)) {
                        if (usuarioService.seguirUsuario(seguidor, seguido)) {
                            cargados++;
                        }
                    } else {
                        errores++;
                    }
                }
            }
            System.out.println("  ✓ " + cargados + " conexiones sociales cargadas" +
                    (errores > 0 ? " (" + errores + " errores)" : ""));
        } catch (IOException e) {
            System.err.println("  ✗ Error al cargar conexiones: " + e.getMessage());
        }
    }

    private String escapar(String texto) {
        if (texto == null) return "";
        return texto.replace("|", "\\|").replace("\n", "\\n");
    }

    private String desescapar(String texto) {
        if (texto == null) return "";
        return texto.replace("\\|", "|").replace("\\n", "\n");
    }
}