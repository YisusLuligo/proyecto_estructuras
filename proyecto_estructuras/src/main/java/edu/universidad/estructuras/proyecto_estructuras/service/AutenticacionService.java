package edu.universidad.estructuras.proyecto_estructuras.service;

import edu.universidad.estructuras.proyecto_estructuras.exception.AutenticacionException;
import edu.universidad.estructuras.proyecto_estructuras.model.TipoUsuario;
import edu.universidad.estructuras.proyecto_estructuras.model.Usuario;
import edu.universidad.estructuras.proyecto_estructuras.repository.UsuarioRepository;
import edu.universidad.estructuras.proyecto_estructuras.util.Constants;
import edu.universidad.estructuras.proyecto_estructuras.util.PasswordHasher;
import edu.universidad.estructuras.proyecto_estructuras.util.Validator;

import java.util.Optional;

/**
 * Servicio de autenticación de usuarios.
 * RF-001: Registrarse y/o iniciar sesión.
 *
 * @author Tu Nombre
 * @version 1.0
 */
public class AutenticacionService {

    private UsuarioRepository usuarioRepo;
    private SessionManager sessionManager;
    private static AutenticacionService instancia;

    private AutenticacionService() {
        this.usuarioRepo = UsuarioRepository.obtenerInstancia();
        this.sessionManager = SessionManager.obtenerInstancia();
        inicializarUsuariosDefault();
    }

    public static AutenticacionService obtenerInstancia() {
        if (instancia == null) {
            instancia = new AutenticacionService();
        }
        return instancia;
    }

    /**
     * Inicializa usuario administrador por defecto.
     */
    private void inicializarUsuariosDefault() {
        if (!usuarioRepo.existe("admin")) {
            String passwordHasheada = PasswordHasher.hashPassword("admin123");
            Usuario admin = new Usuario(
                    "admin",
                    passwordHasheada,
                    "Administrador del Sistema",
                    "admin@syncup.com",
                    TipoUsuario.ADMINISTRADOR
            );
            usuarioRepo.guardar(admin);
            System.out.println("✓ Usuario admin creado (admin/admin123)");
        }
    }

    /**
     * RF-001: Registra un nuevo usuario.
     *
     * @param username Nombre de usuario
     * @param password Contraseña en texto plano
     * @param nombre Nombre completo
     * @param correo Correo electrónico
     * @return Usuario registrado
     * @throws AutenticacionException Si hay errores de validación
     */
    public Usuario registrar(String username, String password,
                             String nombre, String correo)
            throws AutenticacionException {

        // Validar campos vacíos
        if (!Validator.noVacio(username) || !Validator.noVacio(password) ||
                !Validator.noVacio(nombre) || !Validator.noVacio(correo)) {
            throw new AutenticacionException(Constants.MSG_CAMPOS_VACIOS);
        }

        // Validar username
        if (!Validator.validarUsername(username)) {
            throw new AutenticacionException(Constants.MSG_USERNAME_INVALIDO);
        }

        // Validar password
        if (!Validator.validarPassword(password)) {
            throw new AutenticacionException(Constants.MSG_PASSWORD_CORTA);
        }

        // Validar nombre
        if (!Validator.validarNombre(nombre)) {
            throw new AutenticacionException("El nombre debe tener entre 2 y 100 caracteres");
        }

        // Validar email
        if (!Validator.validarEmail(correo)) {
            throw new AutenticacionException("El correo no es válido, parcero");
        }

        // Verificar si ya existe
        if (usuarioRepo.existe(username)) {
            throw new AutenticacionException(Constants.MSG_USUARIO_EXISTE);
        }

        // Crear usuario
        String passwordHasheada = PasswordHasher.hashPassword(password);
        Usuario nuevoUsuario = new Usuario(
                username, passwordHasheada, nombre, correo
        );

        // Guardar
        usuarioRepo.guardar(nuevoUsuario);

        System.out.println("✓ Usuario registrado: " + username);
        return nuevoUsuario;
    }

    /**
     * RF-001: Inicia sesión de un usuario.
     *
     * @param username Nombre de usuario
     * @param password Contraseña en texto plano
     * @return Usuario autenticado
     * @throws AutenticacionException Si las credenciales son inválidas
     */
    public Usuario iniciarSesion(String username, String password)
            throws AutenticacionException {

        // Validar campos vacíos
        if (!Validator.noVacio(username) || !Validator.noVacio(password)) {
            throw new AutenticacionException(Constants.MSG_CAMPOS_VACIOS);
        }

        // Buscar usuario
        Optional<Usuario> usuarioOpt = usuarioRepo.buscarPorUsername(username);

        if (!usuarioOpt.isPresent()) {
            throw new AutenticacionException(Constants.MSG_LOGIN_FALLIDO);
        }

        Usuario usuario = usuarioOpt.get();

        // Verificar contraseña
        if (!PasswordHasher.verificarPassword(password, usuario.getPassword())) {
            throw new AutenticacionException(Constants.MSG_LOGIN_FALLIDO);
        }

        // Establecer sesión
        sessionManager.establecerSesion(usuario);

        System.out.println("✓ Sesión iniciada: " + username);
        return usuario;
    }

    /**
     * Cierra la sesión del usuario actual.
     */
    public void cerrarSesion() {
        Usuario usuarioActual = sessionManager.obtenerUsuarioActual();
        if (usuarioActual != null) {
            System.out.println("✓ Sesión cerrada: " + usuarioActual.getUsername());
        }
        sessionManager.cerrarSesion();
    }

    /**
     * Cambia la contraseña de un usuario.
     *
     * @param usuario Usuario
     * @param passwordActual Contraseña actual
     * @param passwordNueva Nueva contraseña
     * @throws AutenticacionException Si la contraseña actual es incorrecta
     */
    public void cambiarPassword(Usuario usuario, String passwordActual,
                                String passwordNueva)
            throws AutenticacionException {

        // Verificar contraseña actual
        if (!PasswordHasher.verificarPassword(passwordActual, usuario.getPassword())) {
            throw new AutenticacionException("La contraseña actual es incorrecta");
        }

        // Validar nueva contraseña
        if (!Validator.validarPassword(passwordNueva)) {
            throw new AutenticacionException(Constants.MSG_PASSWORD_CORTA);
        }

        // Actualizar
        String passwordHasheada = PasswordHasher.hashPassword(passwordNueva);
        usuario.setPassword(passwordHasheada);
        usuarioRepo.actualizar(usuario);

        System.out.println("✓ Contraseña actualizada: " + usuario.getUsername());
    }

    /**
     * Recupera contraseña generando una nueva temporal.
     *
     * @param username Nombre de usuario
     * @return Nueva contraseña temporal
     * @throws AutenticacionException Si el usuario no existe
     */
    public String recuperarPassword(String username)
            throws AutenticacionException {

        Optional<Usuario> usuarioOpt = usuarioRepo.buscarPorUsername(username);

        if (!usuarioOpt.isPresent()) {
            throw new AutenticacionException("Usuario no encontrado");
        }

        // Generar password temporal
        String passwordTemporal = generarPasswordTemporal();

        Usuario usuario = usuarioOpt.get();
        String passwordHasheada = PasswordHasher.hashPassword(passwordTemporal);
        usuario.setPassword(passwordHasheada);
        usuarioRepo.actualizar(usuario);

        System.out.println("✓ Password temporal generada para: " + username);
        return passwordTemporal;
    }

    /**
     * Genera una contraseña temporal aleatoria.
     */
    private String generarPasswordTemporal() {
        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder(8);
        java.util.Random random = new java.util.Random();

        for (int i = 0; i < 8; i++) {
            int indice = random.nextInt(caracteres.length());
            sb.append(caracteres.charAt(indice));
        }

        return sb.toString();
    }

    public boolean haySesionActiva() {
        return sessionManager.haySesion();
    }

    public Usuario obtenerUsuarioActual() {
        return sessionManager.obtenerUsuarioActual();
    }
}