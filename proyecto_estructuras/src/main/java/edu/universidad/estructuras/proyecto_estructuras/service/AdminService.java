package edu.universidad.estructuras.proyecto_estructuras.service;

import edu.universidad.estructuras.proyecto_estructuras.model.Cancion;
import edu.universidad.estructuras.proyecto_estructuras.model.Usuario;
import edu.universidad.estructuras.proyecto_estructuras.repository.CancionRepository;
import edu.universidad.estructuras.proyecto_estructuras.repository.UsuarioRepository;
import edu.universidad.estructuras.proyecto_estructuras.util.CSVImporter;

import java.io.IOException;
import java.util.List;

/**
 * Servicio de administración.
 * RF-010: Gestión de canciones
 * RF-011: Gestión de usuarios
 * RF-012: Carga masiva
 */
public class AdminService {

    private CancionRepository cancionRepo;
    private UsuarioRepository usuarioRepo;
    private BusquedaService busquedaService;
    private RecomendacionService recomendacionService;
    private static AdminService instancia;

    private AdminService() {
        this.cancionRepo = CancionRepository.obtenerInstancia();
        this.usuarioRepo = UsuarioRepository.obtenerInstancia();
        this.busquedaService = BusquedaService.obtenerInstancia();
        this.recomendacionService = RecomendacionService.obtenerInstancia();
    }

    public static AdminService obtenerInstancia() {
        if (instancia == null) {
            instancia = new AdminService();
        }
        return instancia;
    }

    // RF-010: Gestión de canciones

    public void agregarCancion(Cancion cancion) {
        cancionRepo.guardar(cancion);
        busquedaService.agregarCancionAIndice(cancion);
        recomendacionService.agregarCancionAlGrafo(cancion, cancionRepo.obtenerTodas());
    }

    public void actualizarCancion(Cancion cancion) {
        cancionRepo.actualizar(cancion);
    }

    public boolean eliminarCancion(int id) {
        return cancionRepo.eliminar(id);
    }

    public List<Cancion> listarCanciones() {
        return cancionRepo.obtenerTodas();
    }

    // RF-011: Gestión de usuarios

    public List<Usuario> listarUsuarios() {
        return usuarioRepo.obtenerTodos();
    }

    public boolean eliminarUsuario(String username) {
        return usuarioRepo.eliminar(username);
    }

    /**
     * RF-012: Carga masiva desde archivo.
     */
    public int cargarCancionesMasivamente(String rutaArchivo) throws IOException {
        List<Cancion> canciones = CSVImporter.importarCanciones(rutaArchivo);

        for (Cancion cancion : canciones) {
            agregarCancion(cancion);
        }

        return canciones.size();
    }

    public int contarCanciones() {
        return cancionRepo.contar();
    }

    public int contarUsuarios() {
        return usuarioRepo.contar();
    }
}